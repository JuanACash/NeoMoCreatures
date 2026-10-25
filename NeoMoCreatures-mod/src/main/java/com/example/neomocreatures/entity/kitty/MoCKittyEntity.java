package com.example.neomocreatures.entity.kitty;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.AgeGrowthScaler;
import com.example.neomocreatures.entity.CarriedPet;
import com.example.neomocreatures.entity.GrowthScaled;
import com.example.neomocreatures.entity.StorablePet;
import com.example.neomocreatures.entity.ai.ConditionalAvoidEntityGoal;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCInventoryUtil;
import com.example.neomocreatures.util.MoCTickUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetCarryUtil;
import com.example.neomocreatures.util.PetInteractionUtil;
import com.example.neomocreatures.util.PetStorageTags;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Step 1+2 port of drzhark.mocreatures.entity.neutral.MoCEntityKitty: walks,
 * grows from kitten to adult, makes sound, 11 coat colors. No litter box,
 * kitty bed, taming, or the original's ~20-state AI yet — those come in
 * later steps.
 */
public class MoCKittyEntity extends TamableAnimal implements CarriedPet, GrowthScaled, StorablePet {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;
    private static final float BABY_HITBOX_SCALE = 0.75F;
    private static final int SWING_TICKS_MAX = 10;
    private static final double EAT_NEARBY_ITEM_RANGE = 8.0D;
    private static final int FLEE_IMMUNITY_TICKS = 6000; // 5 minutes — "for a while" after eating

    private static final double BASE_FOLLOW_RANGE = 12.0D;
    private static final double BASE_MAX_HEALTH = 10.0D;

    /** Squared distance (20 blocks) beyond which an adult stops targeting a threat to a kitten. */
    private static final double PROTECT_MAX_DISTANCE_SQR = 400.0D;
    private static final double FOLLOW_MIN_DISTANCE_SQR = 9.0D;
    private static final double FOLLOW_MAX_DISTANCE_SQR = 256.0D;
    private static final int PATH_RECALC_TICKS = 10;

    /** Range in which a kitten notices a player for play. */
    private static final double PLAYFUL_PLAYER_RANGE = 4.0D;
    /** 1 in N chance per tick that a kitten near a player starts playing. */
    private static final int PLAYFUL_ODDS = 200;

    private static final int WOOL_BALL_PICKUP_DELAY_TICKS = 30;
    private static final int PICKUP_COOLDOWN_TICKS = 10;

    private int fleeImmuneTicks;

    /** Tamed-kitty needs: bed, litter box, play, mating, birth and trees. */
    private final KittyCareController care = new KittyCareController(this);
    private int pickupCooldown;
    private final AgeGrowthScaler growth = new AgeGrowthScaler(GROWTH_TICKS, BABY_SCALE, 1.0F);

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
    private static final EntityDataAccessor<Optional<UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    @Nullable
    private Player heldBy;


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
        builder.define(DATA_KITTY_CARE_STATE, KittyCareState.STATE_IDLE);
        builder.define(DATA_SHOW_EMOTE_ICON, false);
        builder.define(DATA_HELD_BY, Optional.empty());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, BASE_FOLLOW_RANGE)
                .add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
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

    private CompoundTag buildAmuletTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Kitty", true);
        tag.putInt("KittyVariant", getVariant().getId());
        PetStorageTags.writeCommon(tag, this, owner);
        return tag;
    }

    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        if (this.isTame()) {
            this.spawnAtLocation(new ItemStack(ModItems.MEDALLION.get()));
        }
    }

    // ---------------------------------------------------------------
    // Model hooks — always neutral for now. Sitting/swinging/mood state
    // get wired to real behavior once the AI state machine is ported.
    // ---------------------------------------------------------------
    public boolean isKittySitting() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
    }

    public boolean hasEaten() {
        return this.entityData.get(DATA_HAS_EATEN);
    }

    public boolean isKittySwinging() {
        return this.entityData.get(DATA_SWING_TICKS) > 0;
    }

    @Override
    protected AABB makeBoundingBox() {
        if (this.isBaby()) {
            EntityDimensions babyDimensions =
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

    /** Randomly shows/hides the care emote icon above the kitty. */
    public void toggleEmoteIcon() {
        this.entityData.set(DATA_SHOW_EMOTE_ICON, !showEmoteIcon());
    }

    /** Pairs this kitty with another one that is ready to mate. */
    public void setMatePartner(@Nullable MoCKittyEntity partner) {
        this.care.setMatePartner(partner);
    }

    /** Starts the paw-swing animation (playing, mating). */
    public void startSwing() {
        this.entityData.set(DATA_SWING_TICKS, SWING_TICKS_MAX);
    }

    public boolean showEmoteIcon() {
        return this.entityData.get(DATA_SHOW_EMOTE_ICON);
    }

    public void setKittyCareState(int state) {
        if (state == KittyCareState.STATE_AGGRESSIVE && getKittyState() != KittyCareState.STATE_AGGRESSIVE) {
            this.playSound(ModSounds.KITTY_UPSET.get(), 1.0F, 1.0F);
        }
        this.entityData.set(DATA_KITTY_CARE_STATE, state);
        this.care.resetTimer();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming/breeding come in a later step
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // taming/breeding come in a later step
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ConditionalAvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.3D,
                // Retaliation always wins over instinctive fleeing — if something
                // (usually whoever just hit it) is already the target, fight instead.
                // Recently having eaten also suppresses fleeing for a while.
                () -> !this.isTame() && this.getTarget() == null && this.fleeImmuneTicks <= 0,
                () -> this.getTarget() == null && this.fleeImmuneTicks <= 0));
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
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.kitty.isTame() || this.kitty.isBaby() || this.kitty.getTarget() != null) {
                return false;
            }
            if (!MoCTickUtil.isScanTick(this.kitty, MoCTickUtil.GOAL_SCAN_INTERVAL)) {
                return false;
            }
            for (MoCKittyEntity kitten : this.kitty.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitty.getBoundingBox().inflate(10.0D, 6.0D, 10.0D), MoCKittyEntity::isBaby)) {
                LivingEntity threat = kitten.getLastHurtByMob();
                if (threat instanceof Player && threat.isAlive() && this.kitty.distanceToSqr(threat) < PROTECT_MAX_DISTANCE_SQR) {
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
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.kitten.isBaby() || !this.kitten.isTame()) {
                return false;
            }
            if (!MoCTickUtil.isScanTick(this.kitten, MoCTickUtil.GOAL_SCAN_INTERVAL)) {
                return false;
            }
            List<MoCKittyEntity> nearby = this.kitten.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitten.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), k -> !k.isBaby());
            if (nearby.isEmpty()) {
                return false;
            }
            this.adult = nearby.get(0);
            return this.kitten.distanceToSqr(this.adult) > FOLLOW_MIN_DISTANCE_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            return this.kitten.isBaby() && this.adult != null && this.adult.isAlive()
                    && this.kitten.distanceToSqr(this.adult) > FOLLOW_MIN_DISTANCE_SQR
                                    && this.kitten.distanceToSqr(this.adult) < FOLLOW_MAX_DISTANCE_SQR;
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
                this.timeToRecalcPath = PATH_RECALC_TICKS;
                this.kitten.getNavigation().moveTo(this.adult, 1.0D);
            }
        }
    }

    /** "Will chase any item, will play with you" — chases the nearest dropped item;
     *  if none nearby but a player is close, does a harmless playful pounce instead. */
    private static class KittenPlayfulGoal extends Goal {
        private final MoCKittyEntity kitten;
        private ItemEntity chasedItem;

        KittenPlayfulGoal(MoCKittyEntity kitten) {
            this.kitten = kitten;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.kitten.isBaby()) {
                return false;
            }
            this.chasedItem = null;
            if (MoCTickUtil.isScanTick(this.kitten, MoCTickUtil.GOAL_SCAN_INTERVAL)) {
                this.chasedItem = this.kitten.level().getEntitiesOfClass(ItemEntity.class,
                                this.kitten.getBoundingBox().inflate(10.0D, 4.0D, 10.0D))
                        .stream().findFirst().orElse(null);
            }
            if (this.chasedItem != null) {
                return true;
            }
            Player nearby = this.kitten.level().getNearestPlayer(this.kitten, PLAYFUL_PLAYER_RANGE);
            return nearby != null && this.kitten.random.nextInt(PLAYFUL_ODDS) == 0;
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
            this.growth.tick(this);
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
            if (MoCTickUtil.isScanTick(this, MoCTickUtil.FOOD_SCAN_INTERVAL)) {
                tickEatNearbyFood();
            }
            this.care.tick();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.growth.tick(this);
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
        this.playSound(ModSounds.KITTY_EATING_FISH.get(), 1.0F, 1.0F);
        this.entityData.set(DATA_HAS_EATEN, true);
        this.fleeImmuneTicks = FLEE_IMMUNITY_TICKS;
    }

    /** Mirrors the original's pickable()/whipable() — aggressive, already-held,
     *  or busy giving birth/defending kittens are never pick-uppable. */
    private boolean canBePickedUp() {
        int state = getKittyState();
        return state != KittyCareState.STATE_AGGRESSIVE && state != KittyCareState.STATE_HELD_LEAD && state != KittyCareState.STATE_HELD_PLAYER;
    }

    private boolean isWhipable() {
        return getKittyState() != KittyCareState.STATE_AGGRESSIVE;
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
        this.entityData.set(DATA_HELD_BY, Optional.of(player.getUUID()));
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.heldBy = null;
    }

    public void tickHeld() {
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
            setKittyCareState(KittyCareState.STATE_IDLE);
            return;
        }

        Vec3 targetPos = this.isBaby()
                ? holder.getEyePosition().add(0.0D, 0.2D, 0.0D)
                : holder.getEyePosition().add(0.0D, 0.2D, 0.0D);
        this.moveTo(targetPos.x, targetPos.y, targetPos.z, holder.getYRot(), 0.0F);
        this.xo = targetPos.x;
        this.yo = targetPos.y;
        this.zo = targetPos.z;
        this.yRotO = holder.getYRot();
        this.setDeltaMovement(Vec3.ZERO);
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

    private static boolean isTamingFish(ItemStack stack) {
        return stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON);
    }

    private static boolean isHealFood(ItemStack stack) {
        return stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON) || stack.is(Items.CAKE);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(DamageTypes.IN_WALL)) {
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
                ? ModSounds.KITTY_AMBIENT_BABY.get()
                : ModSounds.KITTY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isBaby()
                ? ModSounds.KITTY_HURT_BABY.get()
                : ModSounds.KITTY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isBaby()
                ? ModSounds.KITTY_DEATH_BABY.get()
                : ModSounds.KITTY_DEATH.get();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isKittySitting()) {
            this.getNavigation().stop();
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("KittyVariant", getVariant().getId());
        tag.putBoolean("KittySitting", isKittySitting());
        tag.putInt("KittyCareState", getKittyState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("KittyVariant")) {
            setVariant(KittyVariant.byId(tag.getInt("KittyVariant")));
        }
        if (tag.getBoolean("KittySitting")) {
            setSitting(true);
        }
        if (tag.contains("KittyCareState")) {
            int savedState = tag.getInt("KittyCareState");
            this.entityData.set(DATA_KITTY_CARE_STATE, savedState == KittyCareState.STATE_HELD_PLAYER ? KittyCareState.STATE_IDLE : savedState);
        }
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.entityData.set(DATA_HELD_BY, Optional.empty());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shared owner interactions: rename with a book and capture with a Pet Amulet.
        InteractionResult shared = PetInteractionUtil.tryOwnerBookOrAmulet(this, player, hand, this::buildAmuletTag);
        if (shared != InteractionResult.PASS) {
            return shared;
        }

        if (this.isTame() && isWhipable() && stack.is(ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                setSitting(!isKittySitting());
                this.setTarget(null);
                this.getNavigation().stop();
                PetInteractionUtil.playWhipSound(this);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && getKittyState() == KittyCareState.STATE_CURIOUS && stack.is(ModItems.WOOL_BALL.get())) {
            if (!this.level().isClientSide) {
                MoCInventoryUtil.shrinkUnlessCreative(player, stack);
                ItemEntity ball = new ItemEntity(
                        this.level(), this.getX(), this.getY() + 1.0D, this.getZ(),
                        new ItemStack(ModItems.WOOL_BALL.get()));
                ball.setPickUpDelay(WOOL_BALL_PICKUP_DELAY_TICKS);
                ball.setUnlimitedLifetime();
                ball.setDeltaMovement(
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.3D,
                        this.random.nextFloat() * 0.05D,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.3D);
                this.level().addFreshEntity(ball);
                this.care.setPlayTarget(ball);
                setKittyCareState(KittyCareState.STATE_PLAYING);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && getKittyState() == KittyCareState.STATE_IDLE
                && (stack.is(Items.CAKE) || stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON))) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.KITTY_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                MoCInventoryUtil.shrinkUnlessCreative(player, stack);
                setKittyCareState(KittyCareState.STATE_LOOKING_FOR_MATE);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.pickupCooldown <= 0 && canBePickedUp() && stack.isEmpty()
                && !PetCarryUtil.isAlreadyCarryingAPet(player)) {
            if (!this.level().isClientSide) {
                startHolding(player);
                setKittyCareState(KittyCareState.STATE_HELD_PLAYER);
                this.pickupCooldown = PICKUP_COOLDOWN_TICKS;
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTame() && hasEaten() && stack.is(ModItems.MEDALLION.get())) {
            if (!this.level().isClientSide) {
                this.tame(player);
                this.entityData.set(DATA_HAS_EATEN, false);
                this.heal(this.getMaxHealth());
                MoCInventoryUtil.shrinkUnlessCreative(player, stack);
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.KITTY_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                MoCInventoryUtil.shrinkUnlessCreative(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setVariant(KittyVariant.byId(tag.getInt("KittyVariant")));
        PetStorageTags.restoreCommon(tag, this);
    }
}