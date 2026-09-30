package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.kitty.KittyVariant;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.syncher.EntityDataSerializers;

import javax.annotation.Nullable;

/**
 * Step 1+2 port of drzhark.mocreatures.entity.neutral.MoCEntityKitty: walks,
 * grows from kitten to adult, makes sound, 11 coat colors. No litter box,
 * kitty bed, taming, or the original's ~20-state AI yet — those come in
 * later steps.
 */
public class MoCKittyEntity extends TamableAnimal implements com.example.neomocreatures.entity.CarriedPet, GrowthScaled {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;
    private static final float BABY_HITBOX_SCALE = 0.75F;
    private static final int SWING_TICKS_MAX = 10;
    private static final double EAT_NEARBY_ITEM_RANGE = 8.0D;
    private static final int FLEE_IMMUNITY_TICKS = 6000; // 5 minutes — "for a while" after eating
    private static final int STATE_SEEKING_BED = 3;
    private static final int STATE_IN_BED = 4;
    private static final int STATE_IDLE = 7;
    private static final int STATE_AGGRESSIVE = 13;
    private static final double CARE_SEARCH_RADIUS = 18.0D;
    private static final int STATE_SEEKING_LITTER = 5;
    private static final int STATE_IN_LITTER = 6;
    private static final int STATE_HELD_LEAD = 14;
    private static final int STATE_HELD_PLAYER = 15;
    private static final int STATE_PLAYING = 8;
    private static final int STATE_CURIOUS = 11;
    private static final int STATE_LOOKING_FOR_MATE = 9;
    private static final int STATE_MATING = 18;
    private static final int STATE_SEEKING_BIRTH_BED = 19;
    private static final int STATE_GIVING_BIRTH = 20;
    private static final int STATE_DEFENDING_KITTENS = 21;
    private static final int STATE_SLEEPING = 12;
    private static final int STATE_WANTS_TREE = 16;
    private static final int STATE_STUCK_IN_TREE = 17;

    private int fleeImmuneTicks;
    private int careTimer;
    private int pickupCooldown;
    private float lastAppliedScale = -1F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SWING_TICKS =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EATEN =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_KITTY_CARE_STATE =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SHOW_EMOTE_ICON =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<java.util.Optional<java.util.UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    @Nullable
    private Player heldBy;

    @Nullable
    private net.minecraft.core.BlockPos treeTarget;
    private boolean onTree;

    public MoCKittyEntity(EntityType<? extends MoCKittyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, KittyVariant.CREAM.getId());
        builder.define(DATA_SWING_TICKS, 0);
        builder.define(DATA_HAS_EATEN, false);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_KITTY_CARE_STATE, STATE_IDLE);
        builder.define(DATA_SHOW_EMOTE_ICON, false);
        builder.define(DATA_HELD_BY, java.util.Optional.empty());
    }

    @Nullable
    private MoCKittyEntity matePartner;

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.SCALE, 1.0D);
    }

    public KittyVariant getVariant() {
        return KittyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(KittyVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Kitty", true);
        tag.putInt("KittyVariant", getVariant().getId());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    private void capturePetInstant(Player player, net.minecraft.world.InteractionHand hand) {
        net.minecraft.nbt.CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        if (this.isTame()) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.MEDALLION.get()));
        }
    }

    // ---------------------------------------------------------------
    // Model hooks — always neutral for now. Sitting/swinging/mood state
    // get wired to real behavior once the AI state machine is ported.
    // ---------------------------------------------------------------
    public boolean isKittySitting() {
        return this.entityData.get(DATA_SITTING);
    }

    private void setSitting(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
    }

    public boolean hasEaten() {
        return this.entityData.get(DATA_HAS_EATEN);
    }

    public boolean isKittySwinging() {
        return this.entityData.get(DATA_SWING_TICKS) > 0;
    }

    @Override
    protected net.minecraft.world.phys.AABB makeBoundingBox() {
        if (this.isBaby()) {
            net.minecraft.world.entity.EntityDimensions babyDimensions =
                    this.getType().getDimensions().scale(BABY_HITBOX_SCALE);
            return babyDimensions.makeBoundingBox(this.position());
        }
        return super.makeBoundingBox();
    }

    /** Ramps 0 → 2.0 over the swing, exactly like the original's swingProgress. */
    public float getSwingProgress() {
        int ticksRemaining = this.entityData.get(DATA_SWING_TICKS);
        return (SWING_TICKS_MAX - ticksRemaining) * 0.2F;
    }

    public int getKittyState() {
        return this.entityData.get(DATA_KITTY_CARE_STATE);
    }

    public boolean showEmoteIcon() {
        return this.entityData.get(DATA_SHOW_EMOTE_ICON);
    }

    private void setKittyCareState(int state) {
        if (state == STATE_AGGRESSIVE && getKittyState() != STATE_AGGRESSIVE) {
            this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_UPSET.get(), 1.0F, 1.0F);
        }
        this.entityData.set(DATA_KITTY_CARE_STATE, state);
        this.careTimer = 0;
    }

    @Nullable
    private net.minecraft.world.entity.item.ItemEntity playTarget;

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // taming/breeding come in a later step
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.AgeableMob otherParent) {
        return null; // taming/breeding come in a later step
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new UntamedFleeGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new FollowNearestAdultKittyGoal(this));
        this.goalSelector.addGoal(7, new KittenPlayfulGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ProtectKittenGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true, this::canHuntSmallMob));
    }

    /** Wild instinct, not retaliation — an untamed kitty occasionally bolts from
     *  a nearby player even if nothing happened, matching the original's random
     *  "Untamed"/"Scared" state toggle. Once tamed, it stops caring. */
    private static class UntamedFleeGoal extends AvoidEntityGoal<Player> {
        private final MoCKittyEntity kitty;

        UntamedFleeGoal(MoCKittyEntity kitty) {
            super(kitty, Player.class, 6.0F, 1.0D, 1.3D);
            this.kitty = kitty;
        }

        @Override
        public boolean canUse() {
            // Retaliation always wins over instinctive fleeing — if something
            // (usually whoever just hit it) is already the target, fight instead.
            // Recently having eaten also suppresses fleeing for a while.
            return !this.kitty.isTame() && this.kitty.getTarget() == null
                    && this.kitty.fleeImmuneTicks <= 0 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.kitty.getTarget() == null && this.kitty.fleeImmuneTicks <= 0 && super.canContinueToUse();
        }
    }

    private boolean canHuntSmallMob(@Nullable LivingEntity target) {
        if (target == null || target instanceof MoCKittyEntity || target instanceof Player) {
            return false;
        }
        return target.getBbWidth() < this.getBbWidth() && target.getBbHeight() < this.getBbHeight();
    }

    // Kittens never fight back or hunt, no matter which goal tries to set a target.
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.isBaby()) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            this.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
        }
        return hurt;
    }

    /**
     * A tamed kitty attacks the player if they hurt one of its kittens nearby —
     * checked reactively rather than as a real ongoing goal.
     */
    private static class ProtectKittenGoal extends Goal {
        private final MoCKittyEntity kitty;

        ProtectKittenGoal(MoCKittyEntity kitty) {
            this.kitty = kitty;
            this.setFlags(java.util.EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.kitty.isTame() || this.kitty.isBaby() || this.kitty.getTarget() != null) {
                return false;
            }
            for (MoCKittyEntity kitten : this.kitty.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitty.getBoundingBox().inflate(10.0D, 6.0D, 10.0D), MoCKittyEntity::isBaby)) {
                LivingEntity threat = kitten.getLastHurtByMob();
                if (threat instanceof Player && threat.isAlive() && this.kitty.distanceToSqr(threat) < 400.0D) {
                    this.kitty.setTarget(threat);
                    break;
                }
            }
            return false;
        }
    }

    /** "Follow their mother" — simplified to "nearest tamed adult kitty", since our
     *  breeding doesn't track exact parentage. Mirrors Bear's cub-follow pattern. */
    private static class FollowNearestAdultKittyGoal extends Goal {
        private final MoCKittyEntity kitten;
        private MoCKittyEntity adult;
        private int timeToRecalcPath;

        FollowNearestAdultKittyGoal(MoCKittyEntity kitten) {
            this.kitten = kitten;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.kitten.isBaby() || !this.kitten.isTame()) {
                return false;
            }
            java.util.List<MoCKittyEntity> nearby = this.kitten.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitten.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), k -> !k.isBaby());
            if (nearby.isEmpty()) {
                return false;
            }
            this.adult = nearby.get(0);
            return this.kitten.distanceToSqr(this.adult) > 9.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.kitten.isBaby() && this.adult != null && this.adult.isAlive()
                    && this.kitten.distanceToSqr(this.adult) > 9.0D && this.kitten.distanceToSqr(this.adult) < 256.0D;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.adult = null;
        }

        @Override
        public void tick() {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.kitten.getNavigation().moveTo(this.adult, 1.0D);
            }
        }
    }

    /** "Will chase any item, will play with you" — chases the nearest dropped item;
     *  if none nearby but a player is close, does a harmless playful pounce instead. */
    private static class KittenPlayfulGoal extends Goal {
        private final MoCKittyEntity kitten;
        private net.minecraft.world.entity.item.ItemEntity chasedItem;

        KittenPlayfulGoal(MoCKittyEntity kitten) {
            this.kitten = kitten;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.kitten.isBaby()) {
                return false;
            }
            this.chasedItem = this.kitten.level().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                            this.kitten.getBoundingBox().inflate(10.0D, 4.0D, 10.0D))
                    .stream().findFirst().orElse(null);
            if (this.chasedItem != null) {
                return true;
            }
            Player nearby = this.kitten.level().getNearestPlayer(this.kitten, 4.0D);
            return nearby != null && this.kitten.random.nextInt(200) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.chasedItem != null && this.chasedItem.isAlive();
        }

        @Override
        public void tick() {
            if (this.chasedItem == null) {
                this.kitten.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
                return;
            }
            double dist = this.kitten.distanceTo(this.chasedItem);
            if (dist > 1.2D) {
                this.kitten.getNavigation().moveTo(this.chasedItem, 1.2D);
            } else {
                this.kitten.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
            }
        }

        @Override
        public void stop() {
            this.chasedItem = null;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // Matches the original's selectType(): only rolls a color if one hasn't
        // already been set (e.g. by a future spawn-egg override).
        if (getVariant() == KittyVariant.CREAM) {
            setVariant(KittyVariant.rollNatural(this.random));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickHeld();
        if (!this.level().isClientSide) {
            tickGrowth();
            int swing = this.entityData.get(DATA_SWING_TICKS);
            if (swing > 0) {
                this.entityData.set(DATA_SWING_TICKS, swing - 1);
            }
            if (this.fleeImmuneTicks > 0) {
                this.fleeImmuneTicks--;
            }
            if (this.pickupCooldown > 0) {
                this.pickupCooldown--;
            }
            tickEatNearbyFood();
            tickKittyCare();
        }
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        float newScale = getGrowthFraction();
        if (scaleAttr.getBaseValue() != newScale) {
            scaleAttr.setBaseValue(newScale);
        }
        float currentScale = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != currentScale) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.tickGrowth();
    }

    /** Wild kitty eating dropped cooked fish, same "throw it and step back" pattern as Bear. */
    private void tickEatNearbyFood() {
        if (this.isTame() || hasEaten()) {
            return;
        }
        ItemEntity nearestFood = null;
        double nearestDistSqr = EAT_NEARBY_ITEM_RANGE * EAT_NEARBY_ITEM_RANGE;
        for (ItemEntity itemEntity : this.level().getEntitiesOfClass(ItemEntity.class,
                this.getBoundingBox().inflate(EAT_NEARBY_ITEM_RANGE))) {
            if (itemEntity.getOwner() == null || !isTamingFish(itemEntity.getItem())) {
                continue;
            }
            double distSqr = itemEntity.distanceToSqr(this);
            if (distSqr < nearestDistSqr) {
                nearestDistSqr = distSqr;
                nearestFood = itemEntity;
            }
        }
        if (nearestFood == null) {
            return;
        }
        if (nearestDistSqr > 4.0D) {
            this.getNavigation().moveTo(nearestFood, 1.0D);
            return;
        }
        nearestFood.getItem().shrink(1);
        if (nearestFood.getItem().isEmpty()) {
            nearestFood.discard();
        }
        this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_EATING_FISH.get(), 1.0F, 1.0F);
        this.entityData.set(DATA_HAS_EATEN, true);
        this.fleeImmuneTicks = FLEE_IMMUNITY_TICKS;
    }

    /** Steps 3/4/13 of the original's state machine: seek a filled bed when idle
     *  or aggressive, eat in it, calm down. Litter box (5/6) comes in a later step. */
    private void tickKittyCare() {
        if (this.random.nextInt(200) == 0) {
            this.entityData.set(DATA_SHOW_EMOTE_ICON, !showEmoteIcon());
        }
        if (!this.isTame() || this.isBaby()) {
            return;
        }
        switch (getKittyState()) {
            case STATE_SEEKING_BED -> tickSeekingBed();
            case STATE_IN_BED -> tickInBed();
            case STATE_SEEKING_LITTER -> tickSeekingLitter();
            case STATE_IN_LITTER -> tickInLitter();
            case STATE_AGGRESSIVE -> tickAggressive();
            case STATE_CURIOUS -> tickCurious();
            case STATE_PLAYING -> tickPlaying();
            case STATE_LOOKING_FOR_MATE -> tickLookingForMate();
            case STATE_MATING -> tickMating();
            case STATE_SEEKING_BIRTH_BED -> tickSeekingBirthBed();
            case STATE_GIVING_BIRTH -> tickGivingBirth();
            case STATE_DEFENDING_KITTENS -> tickDefendingKittens();
            case STATE_HELD_LEAD, STATE_HELD_PLAYER -> tickHeld();
            case STATE_SLEEPING -> tickSleeping();
            case STATE_WANTS_TREE -> tickWantsTree();
            case STATE_STUCK_IN_TREE -> tickStuckInTree();
            default -> tickIdleCare();
        }
    }

    private void tickIdleCare() {
        if (!this.level().isDay() && this.random.nextInt(500) == 0) {
            com.example.neomocreatures.entity.MoCKittyBedEntity bed = findAnyBed(18.0D);
            if (bed == null) {
                setKittyCareState(STATE_SLEEPING);
            } else {
                double dist = bed.distanceTo(this);
                if (dist > 2.0F) {
                    this.getNavigation().moveTo(bed, 1.0D);
                } else if (this.startRiding(bed)) {
                    setKittyCareState(STATE_SLEEPING);
                }
            }
            return;
        }
        if (this.random.nextInt(20) == 0) {
            Player nearby = this.level().getNearestPlayer(this, 12D);
            if (nearby != null && nearby.getMainHandItem().is(com.example.neomocreatures.init.ModItems.WOOL_BALL.get())) {
                setKittyCareState(STATE_CURIOUS);
                return;
            }
        }
        if (this.getHealth() < this.getMaxHealth() || this.random.nextInt(3000) == 0) {
            setKittyCareState(STATE_SEEKING_BED);
            return;
        }
        if (this.level().canSeeSky(this.blockPosition()) && this.random.nextInt(4000) == 0) {
            setKittyCareState(STATE_WANTS_TREE);
        }
    }

    private void tickSeekingBed() {
        this.careTimer++;
        if (this.careTimer > 500) {
            if (this.random.nextInt(200) == 0) {
                setKittyCareState(STATE_AGGRESSIVE);
                return;
            }
            if (this.random.nextInt(500) == 0) {
                setKittyCareState(STATE_IDLE);
                return;
            }
        }
        if (this.random.nextInt(20) != 0) {
            return;
        }
        approachAndUseBed();
    }

    private void tickInBed() {
        if (!(this.getVehicle() instanceof com.example.neomocreatures.entity.MoCKittyBedEntity bed)) {
            setKittyCareState(STATE_IDLE);
            return;
        }
        lockRotationToVehicle(bed);
        if (!bed.hasFood() && !bed.hasMilk()) {
            this.heal(this.getMaxHealth());
            this.stopRiding();
            setKittyCareState(STATE_SEEKING_LITTER);
            return;
        }
        if (this.random.nextInt(2500) == 0) {
            this.heal(this.getMaxHealth());
            this.stopRiding();
            setKittyCareState(STATE_IDLE);
        }
    }

    private void tickAggressive() {
        com.example.neomocreatures.entity.MoCKittyBedEntity bed = findFilledBed(CARE_SEARCH_RADIUS);
        if (bed != null) {
            this.setTarget(null);
            double dist = bed.distanceTo(this);
            if (dist > 2.0F) {
                this.getNavigation().moveTo(bed, 1.0D);
            } else if (this.startRiding(bed)) {
                setKittyCareState(STATE_IN_BED);
            }
            return;
        }
        Player nearest = this.level().getNearestPlayer(this, CARE_SEARCH_RADIUS);
        this.setTarget(nearest);
        if (nearest == null || this.random.nextInt(500) == 0) {
            setKittyCareState(STATE_IDLE);
        }
    }

    private void tickLookingForMate() {
        this.careTimer++;
        if (this.random.nextInt(20) == 0) {
            MoCKittyEntity candidate = this.level().getEntitiesOfClass(MoCKittyEntity.class,
                            this.getBoundingBox().inflate(16.0D, 6.0D, 16.0D),
                            k -> k != this && k.getKittyState() == STATE_LOOKING_FOR_MATE)
                    .stream()
                    .min(java.util.Comparator.comparingDouble(this::distanceToSqr))
                    .orElse(null);
            if (candidate != null) {
                if (this.distanceToSqr(candidate) < 4.0D) {
                    this.matePartner = candidate;
                    candidate.matePartner = this;
                    setKittyCareState(STATE_MATING);
                    candidate.setKittyCareState(STATE_MATING);
                } else {
                    this.getNavigation().moveTo(candidate, 1.0D);
                }
            }
        }
        if (this.careTimer > 2000) {
            setKittyCareState(STATE_IDLE);
        }
    }

    private void tickMating() {
        if (this.matePartner == null || !this.matePartner.isAlive() || this.matePartner.getKittyState() != STATE_MATING) {
            setKittyCareState(STATE_LOOKING_FOR_MATE);
            return;
        }
        if (this.random.nextInt(50) == 0) {
            this.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
        }
        double dist = this.matePartner.distanceTo(this);
        if (dist < 5.0D) {
            this.careTimer++;
        }
        if (this.careTimer > 500 && this.random.nextInt(50) == 0) {
            this.matePartner.setKittyCareState(STATE_IDLE);
            setKittyCareState(STATE_SEEKING_BIRTH_BED);
        }
    }

    private void tickSeekingBirthBed() {
        if (this.random.nextInt(20) != 0) {
            return;
        }
        com.example.neomocreatures.entity.MoCKittyBedEntity bed = findAnyBed(CARE_SEARCH_RADIUS);
        if (bed == null) {
            return;
        }
        double dist = bed.distanceTo(this);
        if (dist > 2.0F) {
            this.getNavigation().moveTo(bed, 1.0D);
            return;
        }
        if (this.startRiding(bed)) {
            setKittyCareState(STATE_GIVING_BIRTH);
        }
    }

    private void tickGivingBirth() {
        if (this.getVehicle() == null) {
            setKittyCareState(STATE_SEEKING_BIRTH_BED);
            return;
        }
        this.setYRot(180F);
        this.careTimer++;
        if (this.careTimer <= 1000) {
            return;
        }
        int litterSize = this.random.nextInt(3) + 1;
        for (int i = 0; i < litterSize; i++) {
            MoCKittyEntity kitten = com.example.neomocreatures.init.ModEntities.MOC_KITTY.get().create((net.minecraft.server.level.ServerLevel) this.level());
            if (kitten == null) {
                continue;
            }
            KittyVariant kittenVariant = this.random.nextBoolean() ? getVariant() : KittyVariant.rollNatural(this.random);
            kitten.setVariant(kittenVariant);
            kitten.moveTo(this.getX(), this.getY(), this.getZ(), 0F, 0F);
            kitten.setBaby(true);
            this.level().addFreshEntity(kitten);
            this.playSound(net.minecraft.sounds.SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
            if (this.getOwnerUUID() != null) {
                kitten.setOwnerUUID(this.getOwnerUUID());
                kitten.setTame(true, true);
                com.example.neomocreatures.util.NamingHelper.promptRename(kitten, this.getOwnerUUID());
            }
        }
        this.stopRiding();
        setKittyCareState(STATE_DEFENDING_KITTENS);
    }

    private void tickDefendingKittens() {
        this.careTimer++;
        if (this.careTimer > 2000) {
            boolean anyKittensNearby = !this.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.getBoundingBox().inflate(24.0D, 8.0D, 24.0D), MoCKittyEntity::isBaby).isEmpty();
            if (!anyKittensNearby) {
                setKittyCareState(STATE_IDLE);
                return;
            }
            this.careTimer = 1000;
        }
    }

    private void tickSleeping() {
        setSitting(true);
        // Like eating: keep its rotation locked to the bed, or its look-around AI turns it and the
        // renderer drags the whole sleeping body along (a passenger's body follows a head turned past 50°).
        if (this.getVehicle() != null) {
            lockRotationToVehicle(this.getVehicle());
        }
        if (this.random.nextInt(100) == 0) {
            this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_PURR.get(), 0.7F, 1.0F);
        }
        this.careTimer++;
        if (this.level().isDay() || (this.careTimer > 500 && this.random.nextInt(500) == 0)) {
            setSitting(false);
            if (this.isVehicle() || this.getVehicle() != null) {
                this.stopRiding();
            }
            setKittyCareState(STATE_IDLE);
        }
    }

    /**
     * Simplified from the original: it walks toward a nearby tree and "arrives"
     * there instead of literally climbing through leaves block by block (the
     * original disables collision to crawl up through leaves, which needs APIs
     * that don't map cleanly to the modern pathfinder without real risk of
     * getting a kitty stuck inside a tree).
     */
    private void tickWantsTree() {
        this.careTimer++;
        if (this.careTimer > 500) {
            setKittyCareState(this.onTree ? STATE_STUCK_IN_TREE : STATE_IDLE);
            return;
        }
        if (this.treeTarget == null && this.random.nextInt(50) == 0) {
            this.treeTarget = findNearbyTreeTop(18);
        }
        if (this.treeTarget == null) {
            return;
        }
        this.getNavigation().moveTo(this.treeTarget.getX() + 0.5D, this.treeTarget.getY(), this.treeTarget.getZ() + 0.5D, 1.0D);
        if (this.blockPosition().closerThan(this.treeTarget, 2.0D)) {
            this.onTree = true;
            this.treeTarget = null;
        }
    }

    private void tickStuckInTree() {
        if (this.random.nextInt(100) == 0) {
            setKittyCareState(STATE_IDLE);
            this.onTree = false;
            return;
        }
        Player nearby = this.level().getNearestPlayer(this, 2.0D);
        if (nearby != null) {
            setKittyCareState(STATE_IDLE);
            this.onTree = false;
        }
    }

    @Nullable
    private net.minecraft.core.BlockPos findNearbyTreeTop(int radius) {
        net.minecraft.core.BlockPos base = this.blockPosition();
        for (int i = 0; i < 10; i++) {
            int dx = this.random.nextInt(radius * 2 + 1) - radius;
            int dz = this.random.nextInt(radius * 2 + 1) - radius;
            net.minecraft.core.BlockPos.MutableBlockPos pos = base.offset(dx, 10, dz).mutable();
            for (int y = base.getY() + 10; y > base.getY() - 5; y--) {
                pos.setY(y);
                if (this.level().getBlockState(pos).is(net.minecraft.tags.BlockTags.LEAVES)) {
                    return pos.immutable();
                }
            }
        }
        return null;
    }

    @Nullable
    private com.example.neomocreatures.entity.MoCKittyBedEntity findAnyBed(double radius) {
        com.example.neomocreatures.entity.MoCKittyBedEntity best = null;
        double bestDistSqr = radius * radius;
        for (com.example.neomocreatures.entity.MoCKittyBedEntity bed : this.level().getEntitiesOfClass(
                com.example.neomocreatures.entity.MoCKittyBedEntity.class, this.getBoundingBox().inflate(radius))) {
            if (bed.isVehicle()) {
                continue;
            }
            double d = bed.distanceToSqr(this);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = bed;
            }
        }
        return best;
    }

    /** Mirrors the original's pickable()/whipable() — aggressive, already-held,
     *  or busy giving birth/defending kittens are never pick-uppable. */
    private boolean canBePickedUp() {
        int state = getKittyState();
        return state != STATE_AGGRESSIVE && state != STATE_HELD_LEAD && state != STATE_HELD_PLAYER;
    }

    private boolean isWhipable() {
        return getKittyState() != STATE_AGGRESSIVE;
    }

    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    @Nullable
    public Player getHolder() {
        return this.entityData.get(DATA_HELD_BY).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    private void startHolding(Player player) {
        this.heldBy = player;
        this.entityData.set(DATA_HELD_BY, java.util.Optional.of(player.getUUID()));
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, java.util.Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.heldBy = null;
    }

    /** Called by KittyHoldReleaseHandler — releasing by right-clicking anywhere, same as the scorpion. */
    public void releaseHeldPublic() {
        stopHolding();
        setKittyCareState(STATE_IDLE);
    }
    
    private void tickHeld() {
        if (!isHeld()) {
            return;
        }
        Player holder = getHolder();
        if (holder == null || holder.isRemoved()) {
            if (!this.level().isClientSide) {
                stopHolding();
            }
            return;
        }
        if (!this.level().isClientSide && holder.isShiftKeyDown()) {
            stopHolding();
            setKittyCareState(STATE_IDLE);
            return;
        }

        net.minecraft.world.phys.Vec3 targetPos = this.isBaby()
                ? holder.getEyePosition().add(0.0D, 0.2D, 0.0D)
                : holder.getEyePosition().add(0.0D, 0.2D, 0.0D);
        this.moveTo(targetPos.x, targetPos.y, targetPos.z, holder.getYRot(), 0.0F);
        this.xo = targetPos.x;
        this.yo = targetPos.y;
        this.zo = targetPos.z;
        this.yRotO = holder.getYRot();
        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
    }

    @Override
    public boolean isPushable() {
        return !isHeld();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isHeld() && super.canBeCollidedWith();
    }

    @Override
    public void pushEntities() {
        if (isHeld()) {
            return;
        }
        super.pushEntities();
    }

    private void approachAndUseBed() {
        com.example.neomocreatures.entity.MoCKittyBedEntity bed = findFilledBed(CARE_SEARCH_RADIUS);
        if (bed == null) {
            return;
        }
        double dist = bed.distanceTo(this);
        if (dist > 2.0F) {
            this.getNavigation().moveTo(bed, 1.0D);
            return;
        }
        if (this.startRiding(bed)) {
            setKittyCareState(STATE_IN_BED);
        }
    }

    private void tickSeekingLitter() {
        this.careTimer++;
        if (this.careTimer > 2000 && this.random.nextInt(1000) == 0) {
            setKittyCareState(STATE_AGGRESSIVE);
            return;
        }
        if (this.random.nextInt(20) != 0) {
            return;
        }
        com.example.neomocreatures.entity.MoCLitterBoxEntity box = findCleanLitterBox(CARE_SEARCH_RADIUS);
        if (box == null) {
            return;
        }
        double dist = box.distanceTo(this);
        if (dist > 2.0F) {
            this.getNavigation().moveTo(box, 1.0D);
            return;
        }
        if (this.startRiding(box)) {
            setKittyCareState(STATE_IN_LITTER);
        }
    }

    private void tickCurious() {
    Player nearby = this.level().getNearestPlayer(this, 18D);
    if (nearby == null || this.random.nextInt(10) != 0) {
        return;
    }
    if (!nearby.getMainHandItem().is(com.example.neomocreatures.init.ModItems.WOOL_BALL.get())) {
        setKittyCareState(STATE_IDLE);
        return;
    }
    double dist = nearby.distanceTo(this);
    if (dist > 5.0F) {
        this.getNavigation().moveTo(nearby, 1.0D);
    }
}

    private void tickPlaying() {
        int boredomChance = 200; // TODO: use getTemper()-based 300 once temperament exists
        if (this.random.nextInt(boredomChance) == 0) {
            setKittyCareState(STATE_IDLE);
            return;
        }
        if (this.playTarget == null || !this.playTarget.isAlive()) {
            setKittyCareState(STATE_IDLE);
            return;
        }
        double dist = this.playTarget.distanceTo(this);
        if (dist < 1.5D) {
            this.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
            if (this.random.nextInt(10) == 0) {
                net.minecraft.world.phys.Vec3 push = this.playTarget.position().subtract(this.position()).normalize().scale(0.3D);
                this.playTarget.setDeltaMovement(push.x, 0.15D, push.z);
            }
        } else {
            this.getNavigation().moveTo(this.playTarget, 1.0D);
        }
    }

    private void tickInLitter() {
        if (!(this.getVehicle() instanceof com.example.neomocreatures.entity.MoCLitterBoxEntity box)) {
            setKittyCareState(STATE_IDLE);
            return;
        }
        lockRotationToVehicle(box);
        this.careTimer++;
        if (this.careTimer <= 300) {
            if (this.random.nextInt(40) == 0) {
                this.playSound(net.minecraft.sounds.SoundEvents.SAND_BREAK, 1.0F, 1.0F);
            }
            return;
        }
        this.playSound(net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE, 1.0F, 1.0F);
        box.setUsedLitter(true);
        this.stopRiding();
        setKittyCareState(STATE_IDLE);
    }

    private void lockRotationToVehicle(Entity vehicle) {
        this.setYRot(vehicle.getYRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.setXRot(0F);
    }

    @Nullable
    private com.example.neomocreatures.entity.MoCLitterBoxEntity findCleanLitterBox(double radius) {
        com.example.neomocreatures.entity.MoCLitterBoxEntity best = null;
        double bestDistSqr = radius * radius;
        for (com.example.neomocreatures.entity.MoCLitterBoxEntity box : this.level().getEntitiesOfClass(
                com.example.neomocreatures.entity.MoCLitterBoxEntity.class, this.getBoundingBox().inflate(radius))) {
            if (box.isVehicle() || box.isUsedLitter()) {
                continue;
            }
            double d = box.distanceToSqr(this);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = box;
            }
        }
        return best;
    }

    @Nullable
    private com.example.neomocreatures.entity.MoCKittyBedEntity findFilledBed(double radius) {
        com.example.neomocreatures.entity.MoCKittyBedEntity best = null;
        double bestDistSqr = radius * radius;
        for (com.example.neomocreatures.entity.MoCKittyBedEntity bed : this.level().getEntitiesOfClass(
                com.example.neomocreatures.entity.MoCKittyBedEntity.class, this.getBoundingBox().inflate(radius))) {
            if (bed.isVehicle() || (!bed.hasFood() && !bed.hasMilk())) {
                continue;
            }
            double d = bed.distanceToSqr(this);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = bed;
            }
        }
        return best;
    }

    private static boolean isTamingFish(ItemStack stack) {
        return stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON);
    }

    private static boolean isHealFood(ItemStack stack) {
        return stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON) || stack.is(Items.CAKE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeLeashed() {
        return false; // the Lead is repurposed entirely for the "carry by rope" mechanic below
    }

    // ---------------------------------------------------------------
    // Sounds — babies use their own separate set, matching the original.
    // ---------------------------------------------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_AMBIENT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_HURT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_DEATH_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_DEATH.get();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (isKittySitting()) {
            this.getNavigation().stop();
            super.travel(net.minecraft.world.phys.Vec3.ZERO);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("KittyVariant", getVariant().getId());
        tag.putBoolean("KittySitting", isKittySitting());
        tag.putInt("KittyCareState", getKittyState());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("KittyVariant")) {
            setVariant(KittyVariant.byId(tag.getInt("KittyVariant")));
        }
        if (tag.getBoolean("KittySitting")) {
            setSitting(true);
        }
        if (tag.contains("KittyCareState")) {
            int savedState = tag.getInt("KittyCareState");
            this.entityData.set(DATA_KITTY_CARE_STATE, savedState == STATE_HELD_PLAYER ? STATE_IDLE : savedState);
        }
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.entityData.set(DATA_HELD_BY, java.util.Optional.empty());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && isWhipable() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                setSitting(!isKittySitting());
                this.setTarget(null);
                this.getNavigation().stop();
                this.level().playSound(null, this.blockPosition(), com.example.neomocreatures.init.ModSounds.WHIP.get(),
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.5F,
                        0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && getKittyState() == STATE_CURIOUS && stack.is(com.example.neomocreatures.init.ModItems.WOOL_BALL.get())) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                net.minecraft.world.entity.item.ItemEntity ball = new net.minecraft.world.entity.item.ItemEntity(
                        this.level(), this.getX(), this.getY() + 1.0D, this.getZ(),
                        new ItemStack(com.example.neomocreatures.init.ModItems.WOOL_BALL.get()));
                ball.setPickUpDelay(30);
                ball.setUnlimitedLifetime();
                ball.setDeltaMovement(
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.3D,
                        this.random.nextFloat() * 0.05D,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.3D);
                this.level().addFreshEntity(ball);
                this.playTarget = ball;
                setKittyCareState(STATE_PLAYING);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && getKittyState() == STATE_IDLE
                && (stack.is(Items.CAKE) || stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON))) {
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                setKittyCareState(STATE_LOOKING_FOR_MATE);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.pickupCooldown <= 0 && canBePickedUp() && stack.isEmpty()
                && !com.example.neomocreatures.util.PetCarryUtil.isAlreadyCarryingAPet(player)) {
            if (!this.level().isClientSide) {
                startHolding(player);
                setKittyCareState(STATE_HELD_PLAYER);
                this.pickupCooldown = 10;
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTame() && hasEaten() && stack.is(com.example.neomocreatures.init.ModItems.MEDALLION.get())) {
            if (!this.level().isClientSide) {
                this.tame(player);
                this.entityData.set(DATA_HAS_EATEN, false);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.KITTY_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}