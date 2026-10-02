package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.entity.komodo.KomodoSitGoal;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCInventoryUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 1:1 behavioural port of drzhark.mocreatures.entity.hunter.MoCEntityKomodo,
 * following the wiki: wanders on land and in water, hisses and flicks its
 * tongue idly, occasionally sits down, and aggressively hunts and poisons
 * the player.
 * <p>
 * Extends {@link TamableAnimal} (like {@code MoCManticoreEntity} and
 * {@code MoCScorpionEntity}) purely so a future taming/riding pass does not
 * require re-registering the entity type or migrating saved data. Taming
 * itself is intentionally NOT implemented yet — {@link #isFood} always
 * returns false and {@link #getBreedOffspring} always returns null.
 */
public class MoCKomodoDragonEntity extends TamableAnimal implements GrowthScaled, EggHatchable,
        PlayerRideableJumping, StorablePet {

    /** How long the mouth-open hiss pose lasts after a sound plays, in ticks. */
    private static final int MOUTH_TICKS_MAX = 20;
    private static final int POISON_DURATION_TICKS = 120; // 6 seconds
    private static final int POISON_AMPLIFIER = 0;

    /** Ticks from hatch to full adult — same "grows visibly over time" pattern as MoCManticoreEntity. */
    private static final int GROWTH_TICKS = 48000;
    /** Fraction of ADULT_SCALE a freshly-hatched baby starts at. */
    private static final float BABY_SCALE = 0.4F;
    /** Base adult scale before per-individual variance is applied. */
    private static final float ADULT_SCALE = 1.6F;
    /** Wiki: "often spawn with random sizes... can grow to at least 3 blocks long." */
    private static final float MIN_ADULT_SCALE_VARIANCE = 0.6F;
    private static final float MAX_ADULT_SCALE_VARIANCE = 1.0F;
    private static final float LARGE_ADULT_EGG_SCALE_THRESHOLD = ADULT_SCALE * 0.9F;

    /** Wiki: "made to jump (1.2 blocks)" — same velocity vanilla gives the player's ~1.25-block jump. */
    private static final float JUMP_VELOCITY = 0.42F;
    /** Rider seat position relative to the entity's own origin — likely needs visual tuning in-game. */
    private static final double RIDER_FORWARD = -0.1D;
    private static final double RIDER_HEIGHT = 0.3D;
    private static final double RIDER_HEIGHT_SWIMMING = 0.1D;
    /** Much gentler than MoCBigCatEntity's flight thrust — this is swimming, not flying. */
    private static final double RIDDEN_ASCEND_THRUST = 0.05D;
    private static final double RIDDEN_DESCEND_THRUST = 0.05D;
    private static final float SWIM_SPEED_MULTIPLIER = 1.6F;

    /** This individual's rolled adult scale — same for its whole life, persisted below. */
    private float individualAdultScale = ADULT_SCALE;

    public void setIndividualAdultScale(float scale) {
        this.individualAdultScale = scale;
    }

    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCKomodoDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCKomodoDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCKomodoDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCKomodoDragonEntity.class, EntityDataSerializers.BOOLEAN);

    /** Which item to give back when the saddle is sheared off — vanilla saddle vs the mod's crafted one. */
    @Nullable
    private ResourceLocation saddleItemId;

    public MoCKomodoDragonEntity(EntityType<? extends MoCKomodoDragonEntity> type, Level level) {
        super(type, level);
        // Without this, the pathfinder treats water as costly terrain and the
        // dragon hesitates at the shoreline instead of moving in and out freely.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        // Default MoveControl only handles vertical movement by jumping, which is
        // exactly why the dragon was bobbing at the surface instead of swimming to
        // a submerged target. This drives deltaMovement toward the goal directly.
        this.moveControl = new KomodoSwimMoveControl(this);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        // Lets the dragon path across both land and water, the same way
        // vanilla's Frog/Turtle do — required for the wiki's "semi-aquatic"
        // behaviour (wandering into and swimming through water).
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isSwimmingDeep()) {
            if (this.isVehicle() && this.getControllingPassenger() instanceof Player rider) {
                travelRiddenInWater(rider, travelVector);
            } else {
                // KomodoSwimMoveControl already computes the desired deltaMovement
                // each tick — just apply it and let drag settle it.
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
            }
        } else {
            super.travel(travelVector);
        }
    }

    /**
     * Ridden + submerged bypasses KomodoSwimMoveControl entirely and reads the
     * player's own input directly — same pace as unridden swimming (deliberately
     * unchanged), but actually steerable instead of just drifting with whatever
     * the AI wander goal happened to be doing. Holds depth steady since there's
     * no dive/surface input yet (that's the separate Z-key feature).
     */
    private void travelRiddenInWater(Player rider, Vec3 travelVector) {
        this.tickRidden(rider, travelVector);
        Vec3 input = this.getRiddenInput(rider, travelVector);
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double dx = -Math.sin(yaw) * input.z + Math.cos(yaw) * input.x;
        double dz = Math.cos(yaw) * input.z + Math.sin(yaw) * input.x;

        float speed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * SWIM_SPEED_MULTIPLIER;
        Vec3 desired = new Vec3(dx * speed, 0.0D, dz * speed);

        Vec3 current = this.getDeltaMovement();
        double newY;
        if (isAscendHeld()) {
            newY = current.y + RIDDEN_ASCEND_THRUST;
        } else if (isDescendHeld()) {
            newY = current.y - RIDDEN_DESCEND_THRUST;
        } else {
            newY = current.y * 0.8D; // no input: hold depth steady, same as before
        }

        this.setDeltaMovement(new Vec3(
                Mth.lerp(0.2D, current.x, desired.x),
                newY,
                Mth.lerp(0.2D, current.z, desired.z)));
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /** Moves smoothly toward the nav target on all three axes instead of jumping to correct height. */
    private static class KomodoSwimMoveControl extends MoveControl {
        private final MoCKomodoDragonEntity komodo;

        KomodoSwimMoveControl(MoCKomodoDragonEntity komodo) {
            super(komodo);
            this.komodo = komodo;
        }

        @Override
        public void tick() {
            if (!this.komodo.isSwimmingDeep() || this.komodo.isVehicle()) {
                super.tick();
                return;
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || this.komodo.getNavigation().isDone()) {
                // Idle in water: damp existing motion toward zero instead of
                // pushing up or down, so it neither rockets to the surface nor
                // sinks like a stone while it has nowhere to go.
                this.komodo.setDeltaMovement(this.komodo.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
                this.komodo.setSpeed(0.0F);
                return;
            }

            double dx = this.wantedX - this.komodo.getX();
            double dy = this.wantedY - this.komodo.getY();
            double dz = this.wantedZ - this.komodo.getZ();
            double distSqr = dx * dx + dy * dy + dz * dz;
            if (distSqr < 2.5E-7D) {
                this.komodo.setSpeed(0.0F);
                return;
            }

            float speed = (float) (this.speedModifier * this.komodo.getAttributeValue(Attributes.MOVEMENT_SPEED))* SWIM_SPEED_MULTIPLIER;
            Vec3 desired = new Vec3(dx, dy, dz).normalize().scale(speed);
            this.komodo.setDeltaMovement(this.komodo.getDeltaMovement().lerp(desired, 0.125D));

            float yRotTarget = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.komodo.setYRot(this.rotlerp(this.komodo.getYRot(), yRotTarget, 90.0F));
            this.komodo.yBodyRot = this.komodo.getYRot();
        }
        
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

     /** True only when genuinely submerged (eyes underwater) — not just standing
     *  in ankle-deep water at the shore, which is where isInWater() gets noisy
     *  and was causing the swim/land movement models to fight each other. */
    public boolean isSwimmingDeep() {
        return this.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean isAffectedByFluids() {
        // Opts out of vanilla's water buoyancy/"gasping for air" push entirely —
        // that push runs in LivingEntity.aiStep() regardless of our travel()
        // override, and it's what kept winning against KomodoSwimMoveControl.
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // NeoForge's replacement for the now-final canBreatheUnderwater() — same
        // intent as the original mod's MoCEntityKomodo.canBreatheUnderwater().
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
    }

    public void setAscendHeld(boolean held) {
        this.entityData.set(DATA_ASCEND_HELD, held);
    }

    public void setDescendHeld(boolean held) {
        this.entityData.set(DATA_DESCEND_HELD, held);
    }

    public boolean isAscendHeld() {
        return this.entityData.get(DATA_ASCEND_HELD);
    }

    public boolean isDescendHeld() {
        return this.entityData.get(DATA_DESCEND_HELD);
    }

    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    private void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.SCALE, ADULT_SCALE);
    }

    @Override
    protected void registerGoals() {
        // Top priority so a whip-forced sit overrides everything else — same
        // vanilla mechanism wolves/cats use, and it already reuses
        // isInSittingPose() for the pose, same as KomodoSitGoal below.
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new KomodoSitGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, this::canAttackPlayer));
    }

    private boolean canAttackPlayer(@Nullable LivingEntity target) {
        // Wiki: unlike most other creatures here, Komodo dragons are aggressive
        // at every age — deliberately no "!this.isBaby()" exclusion. Only being
        // tamed turns it off.
        return !this.isTame();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        startHiss();
        return this.random.nextBoolean() ? ModSounds.KOMODO_HISS_1.get() : ModSounds.KOMODO_HISS_2.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        startHiss();
        return ModSounds.KOMODO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        startHiss();
        return ModSounds.KOMODO_DEATH.get();
    }

    private void startHiss() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    /** @return ticks remaining in the open-mouth hiss pose; 0 when closed. Used by the model. */
    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickMouthAnimation();
        tickGrowth();
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private float lastAppliedScale = -1F;

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction() * this.individualAdultScale;
            if (scaleAttr.getBaseValue() != newScale) {
                scaleAttr.setBaseValue(newScale);
            }
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
            DifficultyInstance difficulty, MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnGroupData) {
        if (spawnType == MobSpawnType.NATURAL
                || spawnType == MobSpawnType.CHUNK_GENERATION
                || spawnType == MobSpawnType.SPAWN_EGG) {
            // Rolled once per individual and kept for life — egg-hatched dragons
            // (onHatchedFromEgg) deliberately skip this and keep the flat ADULT_SCALE.
            this.individualAdultScale = ADULT_SCALE
                    * Mth.randomBetween(this.random, MIN_ADULT_SCALE_VARIANCE, MAX_ADULT_SCALE_VARIANCE);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("IndividualAdultScale", this.individualAdultScale);
        tag.putBoolean("KomodoSaddled", isSaddled());
        if (this.saddleItemId != null) {
            tag.putString("KomodoSaddleItem", this.saddleItemId.toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("IndividualAdultScale")) {
            this.individualAdultScale = tag.getFloat("IndividualAdultScale");
        }
        if (tag.contains("KomodoSaddled")) {
            setSaddled(tag.getBoolean("KomodoSaddled"));
        }
        if (tag.contains("KomodoSaddleItem", 8)) {
            this.saddleItemId = ResourceLocation.parse(tag.getString("KomodoSaddleItem"));
        }
    }

    private void tickMouthAnimation() {
        int mouthTicks = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouthTicks > 0 && ++mouthTicks > MOUTH_TICKS_MAX) {
            mouthTicks = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouthTicks);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, POISON_AMPLIFIER));
        }
        return hurt;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming happens via egg hatching, not feeding — see onHatchedFromEgg
    }

    private boolean isHealingFood(ItemStack stack) {
        // Wiki: "Tamed Komodo dragons can be healed with raw turkey or raw rat."
        return stack.is(ModItems.TURKEY_RAW.get())
                || stack.is(ModItems.RAT_RAW.get());
    }
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.setTarget(null);
                this.getNavigation().stop();
                this.level().playSound(null, this.blockPosition(), ModSounds.WHIP.get(),
                        SoundSource.NEUTRAL, 0.5F,
                        0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "After a Komodo dragon reaches its full size, it can be equipped
        // with a saddle" — gated on adulthood (!isBaby()), same as the tame check.
        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !isSaddled()
                && (stack.is(Items.SADDLE)
                    || stack.is(ModItems.HORSE_SADDLE.get()))) {
            if (!this.level().isClientSide) {
                setSaddled(true);
                this.saddleItemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(Items.SHEARS) && isSaddled()) {
            if (!this.level().isClientSide) {
                setSaddled(false);
                this.ejectPassengers();
                Item saddleItem = MoCInventoryUtil.saddleItemOrDefault(this.saddleItemId);
                this.saddleItemId = null;
                this.spawnAtLocation(new ItemStack(saddleItem));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
                this.setInSittingPose(false);
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
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
        double height = this.isSwimmingDeep() ? RIDER_HEIGHT_SWIMMING : RIDER_HEIGHT;
        double y = this.getY() + height * this.getScale();
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        // Wiki: "very slow when moving sideways" — forward/back stays full, strafe is cut down hard.
        return new Vec3(player.xxa * 0.3D, 0.0D, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) *0.5F;
    }


    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(0.0F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public boolean canJump() {
        // Wiki: jumping is turned off while mounted in water — Space instead
        // means "ascend" there (see travelRiddenInWater).
        return isSaddled() && this.isVehicle() && !this.isInWater();
    }

    /** Wiki: "made to jump (1.2 blocks)" — fixed impulse, no charge bar. */
    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 0 && (this.onGround() || this.isInWater() || this.isInLava())) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, JUMP_VELOCITY, motion.z);
            this.hasImpulse = true;
        }
    }

    @Override
    public void handleStartJump(int jumpPower) {
        // No charge to release — the jump already happened in onPlayerJump().
    }

    @Override
    public void handleStopJump() {
        // Nothing to reset — no charge state is kept.
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        this.setBaby(true);
        this.setAge(-GROWTH_TICKS);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        MoCLootUtil.dropItems(this, ModItems.REPTILE_HIDE.get(), MoCLootUtil.rollWithFlatLooting(this.random, 2, lootingLevel, 4));

        if (!this.isBaby() && this.individualAdultScale >= LARGE_ADULT_EGG_SCALE_THRESHOLD) {
            if (MoCLootUtil.rollChance(this.random, 0.25F, 0.10F, lootingLevel)) {
                this.spawnAtLocation(new ItemStack(ModItems.KOMODO_DRAGON_EGG.get()));
            }
        }

        dropAllEquipment();
    }

    public void dropAllEquipment() {
        if (!isSaddled()) {
            return;
        }
        Item saddleItem = MoCInventoryUtil.saddleItemOrDefault(this.saddleItemId);
        this.spawnAtLocation(new ItemStack(saddleItem));
        this.saddleItemId = null;
        setSaddled(false);
    }

    /** Builds the NBT payload stored inside a filled Pet Amulet for this dragon. */
    private CompoundTag buildAmuletTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("KomodoDragon", true);
        tag.putFloat("IndividualAdultScale", this.individualAdultScale);
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Captures this tamed dragon into a Pet Amulet and removes it from the world. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropAllEquipment();
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // no breeding
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        if (tag.contains("IndividualAdultScale")) {
            this.setIndividualAdultScale(tag.getFloat("IndividualAdultScale"));
        }
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            this.setAge(tag.getInt("Age"));
        } else {
            this.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
        }
    }
}
