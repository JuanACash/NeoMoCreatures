package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.deer.DeerVariant;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityDeer}. Passive: never fights back, only
 * flees anything bigger than 0.8 blocks that isn't another deer, and panics when hurt. Built on
 * {@link TamableAnimal} for consistency with the project's own class hierarchy, but — like the
 * Crocodile — has no taming interaction of any kind; the original doesn't have one either.
 */
public class MoCDeerEntity extends TamableAnimal {

    private static final double FOLLOW_RANGE = 12.0D;
    private static final double MAX_HEALTH = 10.0D;
    private static final double MOVEMENT_SPEED = 0.35D;

    /** Original: a 1 in 300 chance per tick to grow by 1 — not every tick. */
    private static final int GROWTH_CHANCE = 300;

    private static final double AI_SPEED = 1.1D;
    private static final double PANIC_SPEED = AI_SPEED * 1.2D;
    private static final double FLEE_NEAR_SPEED = AI_SPEED * 1.2D;
    private static final float FLEE_DISTANCE = 6.0F;
    /** Wiki: "run away from anything bigger than a chicken" — a chicken's own hitbox is 0.4 wide by
     *  0.7 tall, replacing the original code's flat 0.8 threshold. */
    private static final float FLEE_SIZE_THRESHOLD = 0.7F;

    /** Deviates from the original (which starts a fawn at age 75, i.e. 0.75 scale — already fairly
     *  large): starts smaller instead, for a more baby-like look, still growing toward the doe's
     *  fixed 1.3 scale by the time it reaches max age. */
    private static final int START_AGE = 75;
    private static final int MAX_AGE = 130;

    /** Original: readyToJumpTimer — a bounding "leap" gait, roughly every 1-1.5s while moving fast. */
    private static final float LEAP_SPEED_THRESHOLD = 0.17F;
    private static final double LEAP_VERTICAL_VELOCITY = 0.5D;
    private static final double LEAP_HORIZONTAL_VELOCITY = 0.5D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCDeerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOC_AGE =
            SynchedEntityData.defineId(MoCDeerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_LEAP_LEFT =
            SynchedEntityData.defineId(MoCDeerEntity.class, EntityDataSerializers.BOOLEAN);

    private int jumpTimer;

    /** Deviates from the original (which ties the tilt sign to the current vertical velocity, so
     *  it flips mid-arc, rising one way and falling the other): tilts the same direction for the
     *  whole jump instead, alternating sides jump to jump — that reads much better visually. */
    private static final float LEAP_TILT_DEGREES = 10.0F;

    public MoCDeerEntity(EntityType<? extends MoCDeerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, DeerVariant.DOE.getId());
        builder.define(DATA_MOC_AGE, MAX_AGE);
        builder.define(DATA_LEAP_LEFT, false);
    }

    private int getMocAge() {
        return this.entityData.get(DATA_MOC_AGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, FLEE_DISTANCE,
                AI_SPEED, FLEE_NEAR_SPEED, e -> !(e instanceof MoCDeerEntity)
                        && (e.getBbHeight() > FLEE_SIZE_THRESHOLD || e.getBbWidth() > FLEE_SIZE_THRESHOLD)));
        this.goalSelector.addGoal(2, new PanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, AI_SPEED));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, AI_SPEED));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    // ---------------------------------------------------------------------
    // Variant / growth
    // ---------------------------------------------------------------------

    public DeerVariant getVariant() {
        return DeerVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public boolean isFawn() {
        return this.getVariant() == DeerVariant.FAWN;
    }

    @Override
    public boolean isBaby() {
        return this.isFawn();
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
                                        net.minecraft.world.DifficultyInstance difficulty,
                                        net.minecraft.world.entity.MobSpawnType spawnType,
                                        @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        DeerVariant variant = DeerVariant.values()[this.random.nextInt(DeerVariant.values().length)];
        this.entityData.set(DATA_VARIANT, variant.getId());
        this.entityData.set(DATA_MOC_AGE, variant == DeerVariant.FAWN ? START_AGE : MAX_AGE);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.isFawn() && this.getMocAge() < MAX_AGE && this.random.nextInt(GROWTH_CHANCE) == 0) {
                this.entityData.set(DATA_MOC_AGE, this.getMocAge() + 1);
            }
            this.tickLeap();
        }
    }

    @Override
    public float getAgeScale() {
        DeerVariant variant = this.getVariant();
        return variant.getFixedScale() > 0.0F ? variant.getFixedScale() : this.getMocAge() * 0.01F;
    }

    /** Original: getDamageAfterMagicAbsorb-equivalent override — never takes fall damage. */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    /** Original: readyToJumpTimer — every so often while moving fast on the ground, a forward-and-up
     *  bound, like a real deer's gait. */
    private void tickLeap() {
        if (!this.onGround() || --this.jumpTimer > 0) {
            return;
        }
        float speed = (float) this.getDeltaMovement().horizontalDistance();
        if (speed <= LEAP_SPEED_THRESHOLD) {
            return;
        }
        // Original subtracts this vector from its current motion, not adds it — using + here made
        // the deer lunge the wrong way.
        float yawRad = (this.getYRot() - 90.0F) * Mth.DEG_TO_RAD;
        double vx = LEAP_HORIZONTAL_VELOCITY * Math.cos(yawRad);
        double vz = LEAP_HORIZONTAL_VELOCITY * Math.sin(yawRad);
        this.setDeltaMovement(this.getDeltaMovement().x - vx, LEAP_VERTICAL_VELOCITY, this.getDeltaMovement().z - vz);
        this.jumpTimer = this.random.nextInt(10) + 20;
        // Alternates side every jump instead of picking at random, so it reads as a steady gait
        // rather than a coin flip each time.
        this.entityData.set(DATA_LEAP_LEFT, !this.entityData.get(DATA_LEAP_LEFT));
    }


    /** Original: pitchRotationOffset() — tilts the whole body while airborne and moving fast,
     *  giving the leap a visible arc instead of a flat, stiff jump. Degrees. */
    public float getLeapTiltDegrees() {
        if (this.onGround() || this.getDeltaMovement().horizontalDistance() <= 0.08D) {
            return 0.0F;
        }
        return this.entityData.get(DATA_LEAP_LEFT) ? LEAP_TILT_DEGREES : -LEAP_TILT_DEGREES;
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // no breeding goal in the original
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.AgeableMob getBreedOffspring(ServerLevel level, net.minecraft.world.entity.AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isFawn() ? com.example.neomocreatures.init.ModSounds.DEER_AMBIENT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.DEER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.DEER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.DEER_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 1-2 raw venison (auto-cooked if it died on fire) + 0-2 leather, both
     *  scaling with Looting independently. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        int venisonCount = 1 + this.random.nextInt(2) + this.random.nextInt(lootingLevel + 1);
        Item venisonItem = this.isOnFire() ? ModItems.VENISON_COOKED.get() : ModItems.VENISON_RAW.get();
        this.spawnAtLocation(new ItemStack(venisonItem, venisonCount));
        int fur = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (fur > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.FUR.get(), fur));
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }
}