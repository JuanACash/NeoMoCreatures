package com.example.neomocreatures.entity.deer;

import javax.annotation.Nullable;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.entity.ai.DefendYoungGoal;
import com.example.neomocreatures.entity.ai.FightOrFlightPanicGoal;
import com.example.neomocreatures.entity.ai.YoungDefender;
import com.example.neomocreatures.entity.ai.YoungDefense;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.MountInteractionUtil;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityDeer}. Passive: never fights back, only
 * flees anything bigger than 0.8 blocks that isn't another deer, and panics when hurt. Built on
 * {@link TamableAnimal} for consistency with the project's own class hierarchy, but — like the
 * Crocodile — has no taming interaction of any kind; the original doesn't have one either.
 */
public class MoCDeerEntity extends TamableAnimal implements YoungDefender {

    private static final double FOLLOW_RANGE = 12.0D;
    private static final double MAX_HEALTH = 10.0D;
    private static final double MOVEMENT_SPEED = 0.35D;
    private static final double ATTACK_DAMAGE = 3.0D;
    private static final double MELEE_SPEED = 1.2D;

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
    private static final double BREED_SPEED = 1.0D;
    /** Extended: feeding a fawn skips a tenth of its growth window. */
    private static final int FEED_GROWTH_STEPS = (MAX_AGE - START_AGE) / 10;
    private static final int TICKS_PER_SECOND = 20;
    /** Share of the usual fall damage that a rider takes. */
    private static final float RIDER_FALL_DAMAGE_MULTIPLIER = 0.25F;
    /** Height of the model's back above the ground at scale 1 (14 of 16 pixels). */
    private static final double BODY_TOP_HEIGHT = 0.875D;
    /** How far the rider's feet sit below the back. The rider does not scale with the deer, so it is fixed. */
    private static final double RIDER_SINK = 0.54D;
    /** The model's body is centred 1 pixel (1/16 block) to the left of and behind the entity's origin. */
    private static final double BODY_CENTRE_LEFT = 0.0625D;
    private static final double BODY_CENTRE_BACK = 0.0625D;
    private static final double THROW_HORIZONTAL_SPEED = 0.5D;
    private static final double THROW_VERTICAL_SPEED = 0.4D;
    private static final int CALM_PARTICLE_COUNT = 7;
    private static final double CALM_PARTICLE_SPREAD = 0.5D;

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
    private final DeerCalm calm = new DeerCalm();

    private DefendYoungGoal defendYoungGoal;

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
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
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


    private static final String TAG_VARIANT = "DeerVariant";
    private static final String TAG_MOC_AGE = "MocAge";

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
        tag.putInt(TAG_MOC_AGE, this.getMocAge());
        this.calm.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.calm.load(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.entityData.set(DATA_VARIANT, tag.getInt(TAG_VARIANT));
        }
        if (tag.contains(TAG_MOC_AGE, Tag.TAG_INT)) {
            this.entityData.set(DATA_MOC_AGE, tag.getInt(TAG_MOC_AGE));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, FLEE_DISTANCE,
                AI_SPEED, FLEE_NEAR_SPEED, e -> !(e instanceof MoCDeerEntity)
                        && !this.calm.trusts(e)
                        && e != this.getTarget()
                        && (e.getBbHeight() > FLEE_SIZE_THRESHOLD || e.getBbWidth() > FLEE_SIZE_THRESHOLD)));
        this.goalSelector.addGoal(2, new FightOrFlightPanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(3, new BreedGoal(this, BREED_SPEED));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, MELEE_SPEED, false));
        this.defendYoungGoal = new DefendYoungGoal(this);
        this.targetSelector.addGoal(1, this.defendYoungGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, AI_SPEED));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, AI_SPEED));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
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

    /** Adds growth steps to a fawn; at max age it turns into an adult stag or doe. */
    private void growFawn(int steps) {
        int newAge = Math.min(this.getMocAge() + steps, MAX_AGE);
        this.entityData.set(DATA_MOC_AGE, newAge);
        if (newAge >= MAX_AGE) {
            this.entityData.set(DATA_VARIANT, DeerVariant.randomAdult(this.random).getId());
        }
    }

    /** Turns a freshly created deer into a newborn fawn. */
    private void becomeFawn() {
        this.entityData.set(DATA_VARIANT, DeerVariant.FAWN.getId());
        this.entityData.set(DATA_MOC_AGE, START_AGE);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        DeerVariant variant = DeerVariant.values()[this.random.nextInt(DeerVariant.values().length)];
        this.entityData.set(DATA_VARIANT, variant.getId());
        this.entityData.set(DATA_MOC_AGE, variant == DeerVariant.FAWN ? START_AGE : MAX_AGE);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.isFawn() && this.random.nextInt(GROWTH_CHANCE) == 0) {
                this.growFawn(1);
            }
            this.tickCalm();
            if (!this.isVehicle()) {
                this.tickLeap();
            }
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
        // The deer itself never takes fall damage; whoever rides it takes a reduced share
        for (Entity passenger : this.getPassengers()) {
            passenger.causeFallDamage(distance, damageMultiplier * RIDER_FALL_DAMAGE_MULTIPLIER, source);
        }
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
        return stack.is(Items.WHEAT);
    }

    /** The stag fights for its fawn; the doe runs from whoever hurt it. */
    @Override
    public void defendYoung(LivingEntity attacker) {
        if (this.getVariant() == DeerVariant.STAG) {
            this.defendYoungGoal.alert(attacker);
        } else if (YoungDefense.isValidAttacker(this, attacker)) {
            this.setLastHurtByMob(attacker);
        }
    }

    /** Extended: only an adult stag and an adult doe can mate. */
    @Override
    public boolean canMate(Animal otherAnimal) {
        return otherAnimal instanceof MoCDeerEntity other
                && !this.isFawn()
                && !other.isFawn()
                && this.getVariant() != other.getVariant()
                && super.canMate(otherAnimal);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MoCDeerEntity baby = ModEntities.MOC_DEER.get().create(level);
        if (baby != null) {
            baby.becomeFawn();
        }
        return baby;
    }

    /** Feeding a fawn speeds up its growth instead of using vanilla's age, which this deer does not use. */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isFawn() && !this.calm.isActive()
                && stack.is(ModItems.HAYSTACK.get()) && player.isShiftKeyDown()) {
            return this.tryCalm(player, stack);
        }
        if (this.canBeRiddenBy(player) && !this.isFood(stack)) {
            return this.tryRide(player);
        }
        if (this.isFawn() && this.isFood(stack)) {
            if (!this.level().isClientSide) {
                this.usePlayerItem(player, hand, stack);
                this.growFawn(FEED_GROWTH_STEPS);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isFawn() ? ModSounds.DEER_AMBIENT_BABY.get()
                : ModSounds.DEER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DEER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DEER_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 1-2 raw venison (auto-cooked if it died on fire) + 0-2 leather, both
     *  scaling with Looting independently. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, MoCLootUtil.rawOrCooked(this, ModItems.VENISON_RAW.get(), ModItems.VENISON_COOKED.get()),
                1 + MoCLootUtil.rollWithLootingBonus(this.random, 2, lootingLevel));
        MoCLootUtil.dropItems(this, ModItems.FUR.get(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }

    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    // ---------------------------------------------------------------------
    // Calm and riding
    // ---------------------------------------------------------------------

    private InteractionResult tryCalm(Player player, ItemStack hay) {
        if (!this.level().isClientSide) {
            this.calmDown();
            if (!player.getAbilities().instabuild) {
                hay.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    private InteractionResult tryRide(Player player) {
        if (!this.level().isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    private boolean canBeRiddenBy(Player player) {
        return Config.CREATURES.deerRideable.get()
                && this.calm.isActive()
                && !this.isFawn()
                && !this.isVehicle()
                && !player.isSecondaryUseActive();
    }

    /** Hay calms an adult deer: it stops fleeing players and can be ridden until the calm runs out. */
    private void calmDown() {
        this.calm.start(Config.CREATURES.deerCalmSeconds.get() * TICKS_PER_SECOND);
        this.setLastHurtByMob(null);
        this.getNavigation().stop();
        this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(),
                    CALM_PARTICLE_COUNT, CALM_PARTICLE_SPREAD, CALM_PARTICLE_SPREAD, CALM_PARTICLE_SPREAD, 0.0D);
        }
    }

    private void tickCalm() {
        if (this.calm.tick()) {
            this.throwOffRiders();
        }
    }

    /** When the calm ends the deer bucks off its rider, then runs from them like from any other player. */
    private void throwOffRiders() {
        Vec3 back = Vec3.directionFromRotation(0.0F, this.getYRot() + 180.0F);
        for (Entity rider : this.getPassengers()) {
            rider.stopRiding();
            rider.setDeltaMovement(back.x * THROW_HORIZONTAL_SPEED, THROW_VERTICAL_SPEED, back.z * THROW_HORIZONTAL_SPEED);
            rider.hurtMarked = true;
        }
    }

    /** Getting hurt breaks the calm, which also drops any rider. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && this.calm.isActive()) {
            this.calm.clear();
            this.throwOffRiders();
        }
        return hurt;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float yaw = this.getYRot() * Mth.DEG_TO_RAD;
        double scale = this.getAgeScale();
        double left = BODY_CENTRE_LEFT * scale;
        double back = BODY_CENTRE_BACK * scale;
        double x = this.getX() + Math.cos(yaw) * left + Math.sin(yaw) * back;
        double z = this.getZ() + Math.sin(yaw) * left - Math.cos(yaw) * back;
        double seatY = this.getY() + BODY_TOP_HEIGHT * scale - RIDER_SINK;
        moveFunction.accept(passenger, x, seatY, z);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa, 0.0D, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        MountInteractionUtil.alignWithRider(this, player, MountInteractionUtil.PITCH_LEVEL);
    }

}