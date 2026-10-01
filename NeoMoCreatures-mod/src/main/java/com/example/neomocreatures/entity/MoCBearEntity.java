package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModTags;

import com.example.neomocreatures.entity.bear.BearVariant;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class MoCBearEntity extends TamableAnimal implements GrowthScaled, net.minecraft.world.entity.HasCustomInventoryScreen,
        net.minecraft.world.entity.PlayerRideableJumping {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BEAR_STATE =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICKS =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.BOOLEAN);

    private final net.minecraft.world.SimpleContainer chestInventory = new net.minecraft.world.SimpleContainer(18);
    @Nullable
    private net.minecraft.resources.ResourceLocation saddleItemId;
    
       
    private static final int MOUTH_TICKS_MAX = 20;
    private static final int ATTACK_TICKS_MAX = 8;
    public static final int FOURS_STATE = 0;
    public static final int STANDING_STATE = 1;
    public static final int SITTING_STATE = 2;
    private static final float RIDER_FORWARD = -0.1F;

    private static final float BABY_SCALE = 0.5F;
    private static final double BREED_ISOLATION_RADIUS = 8.0D;

    private int standingTicks;
    private float lastAppliedScale = -1F;
    private float playerJumpPendingScale;

    public MoCBearEntity(EntityType<? extends MoCBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BearVariant.BLACK.getId());
        builder.define(DATA_BEAR_STATE, FOURS_STATE);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_ATTACK_TICKS, 0);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_HAS_CHEST, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.JUMP_STRENGTH, 0.5D);
    }

    public BearVariant getVariant() {
        return BearVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(BearVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public int getBearState() {
        return this.entityData.get(DATA_BEAR_STATE);
    }

    private void setBearState(int state) {
        this.entityData.set(DATA_BEAR_STATE, state);
    }

    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    private void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    public boolean hasChest() {
        return this.entityData.get(DATA_HAS_CHEST);
    }

    private void setHasChest(boolean hasChest) {
        this.entityData.set(DATA_HAS_CHEST, hasChest);
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    public int getAttackTicks() {
        return this.entityData.get(DATA_ATTACK_TICKS);
    }

    private void startTalking() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    // ---------------------------------------------------------------
    // Temperament: this is the single choke point for "does this bear
    // ever fight back". Cubs and pandas simply can never have a target,
    // no matter which goal tries to set one.
    // ---------------------------------------------------------------
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && (this.isBaby() || getVariant().getTemperament() == BearVariant.Temperament.PASSIVE)) {
            return;
        }
        super.setTarget(target);
    }

    private static boolean isBlackGrizzlyTamingMeat(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.COOKED_BEEF)
                || stack.is(net.minecraft.world.item.Items.COOKED_PORKCHOP)
                || stack.is(net.minecraft.world.item.Items.COOKED_CHICKEN)
                || stack.is(net.minecraft.world.item.Items.COOKED_MUTTON)
                || stack.is(net.minecraft.world.item.Items.COOKED_RABBIT)
                || stack.is(com.example.neomocreatures.init.ModItems.TURKEY_COOKED.get())
                || stack.is(com.example.neomocreatures.init.ModItems.DUCK_COOKED.get())
                || stack.is(com.example.neomocreatures.init.ModItems.VENISON_COOKED.get());
    }

    private static boolean isPolarTamingMeat(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.COOKED_COD)
                || stack.is(net.minecraft.world.item.Items.COOKED_SALMON)
                || stack.is(com.example.neomocreatures.init.ModItems.TURTLE_COOKED.get())
                || stack.is(com.example.neomocreatures.init.ModItems.CRAB_COOKED.get());
    }

    private boolean isTamingMeatForVariant(ItemStack stack) {
        return getVariant() == BearVariant.POLAR ? isPolarTamingMeat(stack) : isBlackGrizzlyTamingMeat(stack);
    }

    /** Healing accepts any meat, cooked or raw, either species' list — more lenient than taming on purpose. */
    private static boolean isAnyMeat(ItemStack stack) {
        return isBlackGrizzlyTamingMeat(stack) || isPolarTamingMeat(stack)
                || stack.is(net.minecraft.world.item.Items.BEEF)
                || stack.is(net.minecraft.world.item.Items.PORKCHOP)
                || stack.is(net.minecraft.world.item.Items.CHICKEN)
                || stack.is(net.minecraft.world.item.Items.MUTTON)
                || stack.is(net.minecraft.world.item.Items.RABBIT)
                || stack.is(net.minecraft.world.item.Items.COD)
                || stack.is(net.minecraft.world.item.Items.SALMON)
                || stack.is(com.example.neomocreatures.init.ModItems.TURKEY_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.DUCK_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.VENISON_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.TURTLE_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.CRAB_RAW.get());
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (getBearState() == SITTING_STATE) {
            this.getNavigation().stop();
            super.travel(net.minecraft.world.phys.Vec3.ZERO);
            return;
        }
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player
                && this.playerJumpPendingScale > 0.0F
                && (this.onGround() || this.isInWater() || this.isInLava())) {
            executeRidersJump(this.playerJumpPendingScale);
            this.playerJumpPendingScale = 0.0F;
        }
        super.travel(travelVector);
        if (this.isVehicle() && (this.isInWater() || this.isInLava())) {
            applyFluidBuoyancy();
        }
    }

    private void executeRidersJump(float scale) {
        double jumpY = this.getJumpPower() * scale;
        net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, jumpY, motion.z);
        this.hasImpulse = true;
    }

    /**
     * Applied AFTER super.travel() on purpose: LivingEntity's water/lava physics has already run and
     * applied its own gravity/drag, so pushing before it would get damped by that logic. Pushing after
     * guarantees the bear really floats instead of sinking.
     */
    private void applyFluidBuoyancy() {
        double fluidTop = this.blockPosition().getY()
                + this.level().getFluidState(this.blockPosition()).getHeight(this.level(), this.blockPosition());
        double bodyTop = this.getY() + this.getBbHeight();
        double submersion = fluidTop - bodyTop;
        net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
        if (submersion > 0.1D) {
            double push = Mth.clamp(submersion * 0.15D, 0.04D, 0.2D);
            this.setDeltaMovement(motion.x, Math.max(motion.y, push), motion.z);
        } else if (motion.y < 0.0D) {
            this.setDeltaMovement(motion.x, motion.y * 0.3D, motion.z);
        }
    }

    private boolean shouldTargetPlayers(@Nullable LivingEntity target) {
        if (this.isTame()) {
            return false; // A tamed bear never picks fights on its own.
        }
        BearVariant.Temperament temperament = getVariant().getTemperament();
        if (temperament == BearVariant.Temperament.HOSTILE) {
            return true;
        }
        return temperament == BearVariant.Temperament.NEUTRAL && hasNearbyCubOfSameSpecies(8.0D);
    }

    private boolean hasNearbyCubOfSameSpecies(double radius) {
        return !this.level().getEntitiesOfClass(MoCBearEntity.class,
                this.getBoundingBox().inflate(radius, 4.0D, radius),
                b -> b.isBaby() && b.getVariant() == this.getVariant()).isEmpty();
    }

    private boolean canHuntAnimal(@Nullable LivingEntity target) {
        if (this.isTame()) {
            return false; // A tamed bear never hunts other animals on its own.
        }
        return !(target instanceof MoCBearEntity) && !(target instanceof MoCBigCatEntity)
                && !(target instanceof net.minecraft.world.entity.animal.PolarBear)
                && !(target instanceof net.minecraft.world.entity.animal.Panda)
                && !(target instanceof MoCElephantEntity)
                && !(target instanceof net.minecraft.world.entity.animal.Bee);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PandaOnlyPanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new FollowSameVariantAdultGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ProtectCubGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::shouldTargetPlayers));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, true, this::canHuntAnimal));
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return isTame() && isAnyMeat(stack);
    }


    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this || !(otherAnimal instanceof MoCBearEntity other)) {
            return false;
        }
        if (other.getVariant() != this.getVariant() || !super.canMate(otherAnimal)) {
            return false;
        }
        return !hasNearbyThirdBear(other) && !other.hasNearbyThirdBear(this);
    }


    private boolean hasNearbyThirdBear(MoCBearEntity partner) {
        return !this.level().getEntitiesOfClass(MoCBearEntity.class,
                this.getBoundingBox().inflate(BREED_ISOLATION_RADIUS),
                bear -> bear != this && bear != partner).isEmpty();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob otherParent) {
        MoCBearEntity baby = com.example.neomocreatures.init.ModEntities.MOC_BEAR.get().create(level);
        if (baby != null) {
            baby.setVariant(this.getVariant());
        }
        return baby;
    }

    @Override
    public void spawnChildFromBreeding(net.minecraft.server.level.ServerLevel level, Animal partner) {
        MoCBearEntity baby = (MoCBearEntity) this.getBreedOffspring(level, partner);
        if (baby == null) {
            return;
        }
        baby.setBaby(true);
        baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
        level.addFreshEntity(baby);

        this.setAge(6000);
        partner.setAge(6000);
        this.resetLove();
        partner.resetLove();
        level.broadcastEntityEvent(this, (byte) 18);
        if (level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
            level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                    level, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

        java.util.UUID ownerUUID = this.getOwnerUUID();
        Player nearbyOwner = ownerUUID != null ? level.getPlayerByUUID(ownerUUID) : null;
        if (nearbyOwner != null && nearbyOwner.distanceToSqr(baby) <= 1000.0D) { // squared distance: ~31 blocks
            baby.setOwnerUUID(nearbyOwner.getUUID());
            baby.setTame(true, true);
            com.example.neomocreatures.util.NamingHelper.promptRename(baby, nearbyOwner.getUUID());
        }
    }

    /** Carries the variant chosen for the first spawned member to the rest of its group — same fix
     *  MoCBigCatEntity uses so a herd never ends up with mixed species. */
    private static final class BearGroupData implements SpawnGroupData {
        final BearVariant variant;
        BearGroupData(BearVariant variant) {
            this.variant = variant;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData resultGroupData = spawnGroupData;

        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            BearVariant variant = spawnGroupData instanceof BearGroupData shared
                    ? shared.variant
                    : pickVariantForBiome(level, this.blockPosition());
            resultGroupData = new BearGroupData(variant);
            setVariant(variant);

            // Wiki: "Bear cubs will occasionally spawn with adults." Same 1-in-4
            // roll MoCBigCatEntity uses for its own wild groups.
            if (this.random.nextInt(4) == 0) {
                this.setAge(-getVariant().getGrowthTicks());
            }
        }
        // For SPAWN_EGG / mob spawner / command spawns the variant is left as the
        // default (BLACK) or set explicitly beforehand — see BearSpawnEggItem.

        // Never forward our own custom SpawnGroupData into AgeableMob's finalizeSpawn —
        // same fix as MoCBigCatEntity: it converts the data without checking the type
        // and crashes with anything that isn't its own. We track the group's shared
        // variant ourselves and hand it back separately.
        super.finalizeSpawn(level, difficulty, spawnType, null);
        return resultGroupData;
    }

    /** Which variant a whole group will be, decided once per group by biome — never mixed within a group. */
    private BearVariant pickVariantForBiome(ServerLevelAccessor level, net.minecraft.core.BlockPos pos) {
        var biome = level.getBiome(pos);

        // Checked in order; each tag holds vanilla biomes plus optional modded ones.
        if (biome.is(ModTags.BEAR_POLAR_BIOMES)) {
            return BearVariant.POLAR;
        }
        if (biome.is(ModTags.BEAR_PANDA_BIOMES)) {
            return BearVariant.PANDA;
        }
        if (biome.is(ModTags.BEAR_GRIZZLY_BIOMES)) {
            return BearVariant.GRIZZLY;
        }
        if (biome.is(ModTags.BEAR_BLACK_BIOMES)) {
            return BearVariant.BLACK;
        }
        if (biome.is(ModTags.BEAR_BLACK_OR_GRIZZLY_BIOMES)) {
            return this.random.nextBoolean() ? BearVariant.BLACK : BearVariant.GRIZZLY;
        }

        // Safety net for a biome not explicitly listed (e.g. a datapack biome reusing
        // one of these spawners) — decide by climate instead of defaulting silently.
        float temperature = biome.value().getBaseTemperature();
        if (temperature <= 0.15F) {
            return BearVariant.POLAR;
        }
        return this.random.nextBoolean() ? BearVariant.BLACK : BearVariant.GRIZZLY;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            tickGrowth();
            tickBearState();
            tickMouthAndAttack();
            tickEatNearbyFood();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            this.entityData.set(DATA_ATTACK_TICKS, ATTACK_TICKS_MAX);
            if (getVariant() != BearVariant.PANDA && getBearState() != SITTING_STATE) {
                // Reuses tickBearState's existing 100-tick expiry for a short rear-up swipe,
                // instead of adding a second timer.
                setBearState(STANDING_STATE);
                this.standingTicks = 81;
            }
        }
        return hurt;
    }

    // ---------------------------------------------------------------
    // Growth — identical pattern to MoCBigCatEntity: smooth scale
    // interpolation over getVariant().getGrowthTicks(), not a sudden
    // pop at adulthood.
    // ---------------------------------------------------------------
    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        int growthTicks = getVariant().getGrowthTicks();
        float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        float newScale = getGrowthFraction() * (float) getVariant().getRenderScale();
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

    // ---------------------------------------------------------------
    // Standing on hind legs (black/grizzly/polar) / sitting (panda),
    // occasionally, especially near a player.
    // ---------------------------------------------------------------
    private void tickBearState() {
        if (this.isVehicle()) {
            // Being ridden always overrides any stand/sit pose — no rearing up or sitting mid-ride.
            if (getBearState() != FOURS_STATE) {
                setBearState(FOURS_STATE);
            }
            this.standingTicks = 0;
            return;
        }
        if (this.standingTicks > 0 && ++this.standingTicks > 100) {
            this.standingTicks = 0;
            setBearState(FOURS_STATE);
        }
        if (!this.isBaby() && getBearState() == FOURS_STATE && this.standingTicks == 0 && this.random.nextInt(200) == 0) {
            Player nearby = this.level().getNearestPlayer(this, 6D);
            if (nearby != null && this.hasLineOfSight(nearby)) {
                this.standingTicks = 1;
                setBearState(getVariant() == BearVariant.PANDA ? SITTING_STATE : STANDING_STATE);
            }
        }
    }

    private static final double EAT_NEARBY_ITEM_RANGE = 8.0D;

    private void tickEatNearbyFood() {
        if (this.isTame() || !this.isBaby() || getVariant() == BearVariant.PANDA) {
            return;
        }
        net.minecraft.world.entity.item.ItemEntity nearestFood = null;
        double nearestDistSqr = EAT_NEARBY_ITEM_RANGE * EAT_NEARBY_ITEM_RANGE;
        for (net.minecraft.world.entity.item.ItemEntity itemEntity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.item.ItemEntity.class, this.getBoundingBox().inflate(EAT_NEARBY_ITEM_RANGE))) {
            if (itemEntity.getOwner() == null || !isTamingMeatForVariant(itemEntity.getItem())) {
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
        startTalking();
        this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
        java.util.UUID thrower = nearestFood.getOwner().getUUID();
        this.setOwnerUUID(thrower);
        this.setTame(true, true);
        com.example.neomocreatures.util.NamingHelper.promptRename(this, thrower);
    }

    private void tickMouthAndAttack() {
        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > MOUTH_TICKS_MAX) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int attack = this.entityData.get(DATA_ATTACK_TICKS);
        if (attack > 0) {
            this.entityData.set(DATA_ATTACK_TICKS, attack - 1);
        }
}

    // ---------------------------------------------------------------
    // Sounds — all 4 species share the same set, matching the original.
    // ---------------------------------------------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        startTalking();
        return com.example.neomocreatures.init.ModSounds.BEAR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        startTalking();
        return com.example.neomocreatures.init.ModSounds.BEAR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.BEAR_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BearVariant", getVariant().getId());
        tag.putBoolean("BearSitting", getBearState() == SITTING_STATE);
        tag.putBoolean("BearSaddled", isSaddled());
        if (this.saddleItemId != null) {
            tag.putString("BearSaddleItem", this.saddleItemId.toString());
        }
        tag.putBoolean("BearHasChest", hasChest());
        if (hasChest()) {
            net.minecraft.nbt.ListTag chestList = new net.minecraft.nbt.ListTag();
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack chestStack = chestInventory.getItem(slot);
                if (!chestStack.isEmpty()) {
                    net.minecraft.nbt.CompoundTag slotTag = new net.minecraft.nbt.CompoundTag();
                    slotTag.putInt("Slot", slot);
                    slotTag.put("Item", chestStack.save(this.registryAccess()));
                    chestList.add(slotTag);
                }
            }
            tag.put("BearChestItems", chestList);
        }
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BearVariant")) {
            setVariant(BearVariant.byId(tag.getInt("BearVariant")));
        }
        if (tag.getBoolean("BearSitting")) {
            setBearState(SITTING_STATE);
            this.standingTicks = 0;
        }
        setSaddled(tag.getBoolean("BearSaddled"));
        if (tag.contains("BearSaddleItem")) {
            this.saddleItemId = net.minecraft.resources.ResourceLocation.parse(tag.getString("BearSaddleItem"));
        }
        if (tag.getBoolean("BearHasChest")) {
            setHasChest(true);
            for (net.minecraft.nbt.Tag entry : tag.getList("BearChestItems", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                net.minecraft.nbt.CompoundTag slotTag = (net.minecraft.nbt.CompoundTag) entry;
                int slot = slotTag.getInt("Slot");
                ItemStack chestStack = ItemStack.parse(this.registryAccess(), slotTag.getCompound("Item")).orElse(ItemStack.EMPTY);
                if (slot >= 0 && slot < chestInventory.getContainerSize()) {
                    chestInventory.setItem(slot, chestStack);
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Drops: 0-2 hide (+1 per Looting level, capped at 5), the exact saddle
    // it was wearing, and its chest plus contents if it had one. XP (1-3) is
    // granted through shouldDropExperience()/lastHurtByPlayerTime, which
    // vanilla already triggers for both players and tamed wolves.
    // ---------------------------------------------------------------
    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING),
                    attacker);
        }

        int hide = Math.min(this.random.nextInt(3) + lootingLevel, 5);
        if (hide > 0) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.HIDE.get(), hide));
        }

        dropAllEquipment();
    }

    /**
     * Drops the saddle and chest (with its contents) and clears the
     * saddled/chest flags. Shared by {@link #dropCustomDeathLoot} and by
     * the Scroll of Freedom / Pet Amulet items, which need the bear to
     * stay alive afterward.
     */
    public void dropAllEquipment() {
        if (isSaddled()) {
            net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                    ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                    : net.minecraft.world.item.Items.SADDLE;
            this.spawnAtLocation(new ItemStack(saddleItem));
            this.saddleItemId = null;
            setSaddled(false);
        }
        if (hasChest()) {
            this.spawnAtLocation(new ItemStack(net.minecraft.world.item.Items.CHEST));
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack chestStack = chestInventory.getItem(slot);
                if (!chestStack.isEmpty()) {
                    this.spawnAtLocation(chestStack);
                }
            }
            setHasChest(false);
        }
    }

    /** Builds the NBT payload stored inside a filled Pet Amulet for this bear. */
    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putInt("BearVariant", getVariant().getId());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Captures this tamed bear into a Pet Amulet and removes it from the world. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropAllEquipment();
        net.minecraft.nbt.CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3); // 1-3
    }

    // ---------------------------------------------------------------
    // A cub only follows an ADULT of its own species — never a
    // different species, tamed or not. It loses this once it itself
    // is tamed (no-op for now since taming doesn't exist yet).
    // ---------------------------------------------------------------
    private static class FollowSameVariantAdultGoal extends Goal {
        private final MoCBearEntity cub;
        private final double speedModifier;
        private MoCBearEntity adult;
        private int timeToRecalcPath;

        FollowSameVariantAdultGoal(MoCBearEntity cub, double speedModifier) {
            this.cub = cub;
            this.speedModifier = speedModifier;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.cub.isBaby() || this.cub.isTame()) {
                return false;
            }
            java.util.List<MoCBearEntity> nearby = this.cub.level().getEntitiesOfClass(MoCBearEntity.class,
                    this.cub.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                    bear -> !bear.isBaby() && bear.getVariant() == this.cub.getVariant());
            if (nearby.isEmpty()) {
                return false;
            }
            this.adult = nearby.get(0);
            return this.cub.distanceToSqr(this.adult) > 9.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cub.isBaby() && !this.cub.isTame() && this.adult != null && this.adult.isAlive()
                    && this.cub.distanceToSqr(this.adult) > 9.0D && this.cub.distanceToSqr(this.adult) < 256.0D;
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
                this.cub.getNavigation().moveTo(this.adult, this.speedModifier);
            }
        }
    }

    private static class PandaOnlyPanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
    private final MoCBearEntity bear;

    PandaOnlyPanicGoal(MoCBearEntity bear, double speedModifier) {
            super(bear, speedModifier);
            this.bear = bear;
        }

        @Override
        public boolean canUse() {
            return this.bear.getVariant().getTemperament() == BearVariant.Temperament.PASSIVE && super.canUse();
        }
    }

    /**
     * Reactive check, not a real ongoing goal: every so often, if a nearby
     * cub of the same species currently has an attacker, the adult (if
     * neutral) picks up that same target. Polar bears don't need this
     * (already always hostile) and pandas can never get a target anyway
     * (blocked in setTarget()).
     */
    private static class ProtectCubGoal extends Goal {
        private final MoCBearEntity bear;

        ProtectCubGoal(MoCBearEntity bear) {
            this.bear = bear;
            this.setFlags(java.util.EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (this.bear.isTame() || this.bear.isBaby()
                    || this.bear.getVariant().getTemperament() != BearVariant.Temperament.NEUTRAL
                    || this.bear.getTarget() != null) {
                return false;
            }
            for (MoCBearEntity cub : this.bear.level().getEntitiesOfClass(MoCBearEntity.class,
                    this.bear.getBoundingBox().inflate(10.0D, 6.0D, 10.0D),
                    b -> b.isBaby() && b.getVariant() == this.bear.getVariant())) {
                LivingEntity threat = cub.getLastHurtByMob();
                if (threat != null && threat.isAlive() && this.bear.distanceToSqr(threat) < 400.0D) {
                    this.bear.setTarget(threat);
                    break;
                }
            }
            return false;
        }
    }

    private void openChestMenu(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : net.minecraft.network.chat.Component.literal("Bear Storage");
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                            net.minecraft.world.inventory.MenuType.GENERIC_9x2, id, inv, this.chestInventory, 2),
                    title));
        }
    }

    /** E while riding opens the chest instead of the player's own inventory. */
    @Override
    public void openCustomInventoryScreen(Player player) {
        if (this.level().isClientSide || !this.isTame()) {
            return;
        }
        if (hasChest()) {
            openChestMenu(player);
            return;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer,
                    new com.example.neomocreatures.network.OpenPlayerInventoryPayload());
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (isSaddled() && this.getFirstPassenger() instanceof Player player && this.hasPassenger(player)) {
            return player;
        }
        return null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() - Math.sin(yaw) * RIDER_FORWARD;
        double z = this.getZ() + Math.cos(yaw) * RIDER_FORWARD;
        double y = this.getY() + (float) getVariant().getRiderHeight() * this.getScale();
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(Player player, net.minecraft.world.phys.Vec3 travelVector) {
        return new net.minecraft.world.phys.Vec3(player.xxa, 0.0D, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        // Noticeably slower than BigCat/Manticore on purpose — combine with speed potions if needed.
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    protected void tickRidden(Player player, net.minecraft.world.phys.Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public boolean canJump() {
        return isSaddled() && this.isVehicle();
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower < 0) {
            jumpPower = 0;
        }
        this.playerJumpPendingScale = jumpPower >= 90 ? 1.0F : 0.4F + 0.4F * jumpPower / 90.0F;
    }

    @Override
    public void handleStartJump(int jumpPower) {
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(net.minecraft.world.item.Items.BOOK)) {
            if (!this.level().isClientSide) {
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                setBearState(getBearState() == SITTING_STATE ? FOURS_STATE : SITTING_STATE);
                this.standingTicks = 0; // whip-sit is permanent, not the timed wild stand/sit
                this.setTarget(null);
                this.getNavigation().stop();
                this.level().playSound(null, this.blockPosition(), com.example.neomocreatures.init.ModSounds.WHIP.get(),
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.5F,
                        0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTame() && getVariant() == BearVariant.PANDA && stack.is(net.minecraft.world.item.Items.BAMBOO)) {
            if (!this.level().isClientSide) {
                startTalking();
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.tame(player);
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isAnyMeat(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                startTalking();
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !isSaddled()
            && (stack.is(net.minecraft.world.item.Items.SADDLE)
                || stack.is(com.example.neomocreatures.init.ModItems.HORSE_SADDLE.get()))) {
            if (!this.level().isClientSide) {
                setSaddled(true);
                this.saddleItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                this.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(net.minecraft.world.item.Items.SHEARS) && isSaddled()) {
            if (!this.level().isClientSide) {
                setSaddled(false);
                this.ejectPassengers();
                net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                        ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                        : net.minecraft.world.item.Items.SADDLE;
                this.saddleItemId = null;
                this.spawnAtLocation(new ItemStack(saddleItem));
                this.playSound(net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !hasChest()
                && stack.is(net.minecraft.world.item.Items.CHEST)) {
            if (!this.level().isClientSide) {
                setHasChest(true);
                this.playSound(net.minecraft.sounds.SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                setBearState(FOURS_STATE); // interrumpe cualquier pose de pie/sentado al montarlo
                this.standingTicks = 0;
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.level().isClientSide && hasChest() && player.isSecondaryUseActive()
                && !stack.is(com.example.neomocreatures.init.ModItems.SCROLL_OF_FREEDOM.get())) {
            openChestMenu(player);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}