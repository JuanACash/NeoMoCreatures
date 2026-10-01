package com.example.neomocreatures.entity;

import com.example.neomocreatures.breeding.MoCDolphinGenetics;
import com.example.neomocreatures.entity.dolphin.DolphinVariant;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.init.ModTags;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityDolphin}, built on
 * {@link TamableAnimal} like the shark and the stingray instead of the original's own aquatic
 * ownership framework.
 * <p>
 * Six colour variants, swimming near the surface, growth from calf to adult, taming by riding,
 * riding, fish feeding, breeding through love mode and suffocation out of water. Not implemented
 * yet: natural spawning and drops.
 * <p>
 * Neutral: an adult dolphin fights back when hurt, a calf flees.
**/
public class MoCDolphinEntity extends TamableAnimal implements GrowthScaled {

    // ---- Size and growth ----
    /** Original: a wild dolphin is created with age 120, which renders the model at 1.2x. */
    private static final float ADULT_SCALE = 1.2F;
    /** Original: a bred calf starts at age 35, which renders the model at 0.35x. */
    private static final float CALF_SCALE = 0.35F;
    /**
     * Original: a calf gains one age point with a 1/50 chance per tick (plus 1/300 from the base
     * class) until age 150, so about 115 / (1/50 + 1/300) = 4900 ticks.
     */
    private static final int GROWTH_TICKS = 4900;
    /** Calves only push a new scale to the clients after it changed this much, to save packets. */
    private static final double SCALE_SYNC_THRESHOLD = 0.01D;

    // ---- Attributes ----
    private static final double MAX_HEALTH = 15.0D;
    /** Original: registered attack damage (its old attack code hit for 5). */
    private static final double ATTACK_DAMAGE = 4.5D;
    /**
     * Original: getAIMoveSpeed() is 0.15 and the move helper adds an eighth of the gap to
     * speed * attribute, which makes it cruise at about 0.19 blocks per tick.
     */
    private static final double SWIM_SPEED = 0.2D;

    // ---- Goals ----
    private static final double PANIC_SPEED = 1.3D;
    private static final double ATTACK_SPEED = 1.3D;
    private static final double WANDER_SPEED = 1.0D;
    private static final int WANDER_INTERVAL = 10;
    private static final int WANDER_PRIORITY = 5;

    // ---- Swimming ----
    /** Original: minDivingDepth() / maxDivingDepth(), in blocks under the water surface. */
    private static final double MIN_CRUISE_DEPTH = 0.4D;
    private static final double MAX_CRUISE_DEPTH = 4.0D;
    private static final int MAX_SURFACE_SCAN = 32;
    /** Keeps every destination inside the pathfinder's search range (follow range is 16). */
    private static final double MAX_VERTICAL_STEP = 8.0D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    /** Same water movement as the original aquatic mobs: thrust per tick of forward input. */
    private static final float WATER_THRUST = 0.1F;
    private static final double WATER_DRAG = 0.9D;
    /** Upward push when it bumps into a ledge while swimming, so it can climb it (original: 0.05). */
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private static final double VERTICAL_STEERING = 0.1D;

    // ---- Taming ----
    /** Original: every dolphin starts with temper 50 (set by the aquatic base class). */
    private static final int INITIAL_TEMPER = 50;
    /** Original: each raw fish adds 25 temper, which makes the dolphin easier to tame. */
    private static final int TEMPER_PER_RAW_FISH = 25;
    /** Original: a ridden wild dolphin is tamed each tick with odds of 1 in (maxTemper - temper) * 8. */
    private static final int TAMING_ODDS_FACTOR = 8;
    /** Growth a calf gains from a raw fish (original: one age point out of about 115). */
    private static final int RAW_FISH_GROWTH_TICKS = 40;

    // ---- Drops ----
    /** Wiki: 0-2 raw cod, and Looting adds up to its level in extra ones. */
    private static final int MAX_COD_DROP = 2;

    // ---- Breeding ----
    /** Same as the horse: 15 seconds together. The original waited about 5 minutes; use 1 for an instant birth. */
    private static final int GESTATION_TICKS = 300;
    /** Original: the mate must be within 4 blocks horizontally. */
    private static final double MATE_RANGE_HORIZONTAL = 4.0D;
    private static final double MATE_RANGE_VERTICAL = 2.0D;
    /** Original: no third dolphin within 8 blocks, or they will not breed. */
    private static final double CROWD_RANGE_HORIZONTAL = 8.0D;
    private static final double CROWD_RANGE_VERTICAL = 4.0D;
    private static final int HEART_INTERVAL_TICKS = 10;

    // ---- Bucking (wild dolphin with a rider) ----
    /** Original: each tick a 1 in 100 chance to throw the rider off with an upward kick. */
    private static final int BUCK_ODDS = 100;
    private static final double BUCK_UPWARD_IMPULSE = 0.2D;
    /** Original: each tick a 1 in 10 chance to lurch sideways. */
    private static final int LURCH_ODDS = 10;
    private static final double LURCH_STRENGTH_X = 1.0D / 30.0D;
    private static final double LURCH_STRENGTH_Z = 1.0D / 10.0D;

    // ---- Riding (tamed dolphin) ----
    /** Original: forward input is scaled by the variant's mount speed divided by 5. */
    private static final double MOUNT_SPEED_DIVISOR = 5.0D;
    /** Original: strafing while ridden is cut to 35%. */
    private static final double RIDDEN_STRAFE_FACTOR = 0.35D;
    private static final float RIDDEN_THRUST = 0.1F;
    private static final double RIDDEN_DRAG = 0.8D;
    /** Per tick while the jump / descend key is held (same values as the komodo dragon). */
    private static final double RIDDEN_ASCEND_THRUST = 0.05D;
    private static final double RIDDEN_DESCEND_THRUST = 0.05D;
    /** Original: the rider sits 0.8 blocks behind the origin, about the middle of the body. */
    private static final double RIDER_BACK_OFFSET = 0.8D;
    private static final double RIDER_Y_OFFSET = -0.13D;

    /** NBT flag that marks a filled fish net as holding a dolphin. */
    public static final String NET_KEY = "Dolphin";

    // ---- Sounds ----
    /** Original: getTalkInterval() is 300 instead of vanilla's 80, so dolphins call less often. */
    private static final int AMBIENT_SOUND_INTERVAL = 300;
    /** Original: every aquatic mob plays its sounds at 0.4 volume. */
    private static final float SOUND_VOLUME = 0.4F;

    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    private static final String VARIANT_TAG = "DolphinVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCDolphinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCDolphinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCDolphinEntity.class, EntityDataSerializers.BOOLEAN);

    /** Like the original, temper is not saved: a reloaded wild dolphin starts again from the initial value. */
    private int temper = INITIAL_TEMPER;
    private int gestationTicks;
    private int outOfWaterTicks;
    private float lastAppliedScale = -1.0F;

    public MoCDolphinEntity(EntityType<? extends MoCDolphinEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost
        // every water position for this mob, and the dolphin never finds anywhere to swim to.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new DolphinMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, SWIM_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.SCALE, ADULT_SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CalfPanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, ATTACK_SPEED, true));
        this.goalSelector.addGoal(WANDER_PRIORITY, new CruiseSwimGoal(this, WANDER_SPEED, WANDER_INTERVAL));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Pufferfish.class, true));
    }

    // ---------------------------------------------------------------------
    // Variant
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, DolphinVariant.BLUE.getId());
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
    }

    public DolphinVariant getVariant() {
        return DolphinVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(DolphinVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(VARIANT_TAG, this.getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(VARIANT_TAG, Tag.TAG_STRING)) {
            this.setVariant(DolphinVariant.byName(tag.getString(VARIANT_TAG)));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(DolphinVariant.random(this.random));
        // Wild dolphins are always adults (original: the constructor sets adult), so AgeableMob's
        // random baby roll is turned off. The variant is rolled per dolphin, not per group.
        return super.finalizeSpawn(level, difficulty, spawnType, new AgeableMob.AgeableMobGroupData(false));
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        this.tickGrowth();
        if (!this.level().isClientSide) {
            this.tickOutOfWater();
            this.tickWildRider();
            this.tickBreeding();
        }
    }

    /** Server: sets the body scale from the age. Both sides: refresh the hitbox when the scale changed. */
    private void tickGrowth() {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        if (scale == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = this.getGrowthScale();
            if (!this.isBaby() || Math.abs(scale.getBaseValue() - target) >= SCALE_SYNC_THRESHOLD) {
                scale.setBaseValue(target);
            }
        }
        float current = (float) scale.getValue();
        if (this.lastAppliedScale != current) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.tickGrowth();
    }

    /** An adult is full size; a calf grows linearly from {@link #CALF_SCALE} over {@link #GROWTH_TICKS}. */
    private float getGrowthScale() {
        if (!this.isBaby()) {
            return ADULT_SCALE;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, CALF_SCALE, ADULT_SCALE);
    }

    /** Stranded dolphins cannot move (no water to path through) and eventually suffocate. */
    private void tickOutOfWater() {
        if (this.isInWaterOrBubble()) {
            this.outOfWaterTicks = 0;
            return;
        }
        this.outOfWaterTicks++;
        if (this.outOfWaterTicks > SUFFOCATION_GRACE_TICKS
                && this.outOfWaterTicks % SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), SUFFOCATION_DAMAGE);
        }
    }

    // ---------------------------------------------------------------------
    // Water
    // ---------------------------------------------------------------------

    @Override
    public void travel(Vec3 travelVector) {
        // Player-controlled: this runs on the rider's client, which owns the movement.
        if (this.isInWater() && this.getControllingPassenger() instanceof Player rider) {
            this.travelRidden(rider, travelVector);
            return;
        } if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // The move control sets the forward input, this turns it into thrust and drag slows it
        // down again. There is no gravity: an idle dolphin keeps its depth.
        this.moveRelative(WATER_THRUST, travelVector);
        if (this.horizontalCollision && !this.getNavigation().isDone()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, Math.max(motion.y, CLIMB_LEDGE_IMPULSE), motion.z);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(WATER_DRAG));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // NeoForge's replacement for canBreatheUnderwater().
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // ---------------------------------------------------------------------
    // Taming and interaction
    // ---------------------------------------------------------------------

    public int getMaxTemper() {
        return this.getVariant().getMaxTemper();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = InteractionResult.PASS;
        if (!this.isTame()) {
            result = this.interactAsStranger(player, stack);
        } else if (this.isOwnedBy(player)) {
            result = this.interactAsOwner(player, hand, stack);
        }
        return result != InteractionResult.PASS ? result : super.mobInteract(player, hand);
    }

    private InteractionResult interactAsStranger(Player player, ItemStack stack) {
        if (stack.is(ModTags.RAW_FISHES)) {
            return this.feedRawFish(player, stack);
        }
        return this.mount(player);
    }

    private InteractionResult interactAsOwner(Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ModItems.FISH_NET.get()) && !this.isVehicle()) {
            if (!this.level().isClientSide) {
                this.captureInFishNet(player, hand, stack);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ModTags.RAW_FISHES)) {
            return this.feedRawFish(player, stack);
        }
        if (stack.is(ModTags.COOKED_FISHES)) {
            return this.feedCookedFish(player, stack);
        }
        if (isEntityScroll(stack)) {
            return InteractionResult.PASS;
        }
        return this.mount(player);
    }

    /** Raw fish makes a wild dolphin easier to tame, heals it and speeds up a calf's growth. */
    private InteractionResult feedRawFish(Player player, ItemStack stack) {
        if (!this.level().isClientSide) {
            int raised = this.temper + TEMPER_PER_RAW_FISH;
            this.temper = raised > this.getMaxTemper() ? this.getMaxTemper() - 1 : raised;
            if (this.isBaby()) {
                this.setAge(Math.min(0, this.getAge() + RAW_FISH_GROWTH_TICKS));
            }
            this.eatFish(player, stack);
        }
        return InteractionResult.SUCCESS;
    }

    /** Cooked fish heals, and an adult that is not already in love mode falls in love (hearts included). */
    private InteractionResult feedCookedFish(Player player, ItemStack stack) {
        if (!this.level().isClientSide) {
            this.eatFish(player, stack);
            if (!this.isBaby() && this.canFallInLove()) {
                this.setInLove(player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void eatFish(Player player, ItemStack stack) {
        this.heal(this.getMaxHealth());
        this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private InteractionResult mount(Player player) {
        if (this.isVehicle() || player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    /** Scrolls act on the entity through their own interaction, so a right click with one must not mount. */
    private static boolean isEntityScroll(ItemStack stack) {
        return stack.is(ModItems.SCROLL_OF_FREEDOM.get())
                || stack.is(ModItems.SCROLL_OF_SALE.get())
                || stack.is(ModItems.SCROLL_OF_OWNER.get());
    }

    private void tameBy(Player player) {
        this.tame(player);
        this.level().broadcastEntityEvent(this, (byte) 7);
        NamingHelper.promptRename(this, player.getUUID());
    }

    /** A wild dolphin with a rider tries to throw it off, and every tick the rider might tame it. */
    private void tickWildRider() {
        Entity rider = this.getFirstPassenger();
        if (rider == null || this.isTame()) {
            return;
        }
        if (!this.isInWater()) {
            this.ejectPassengers();
            return;
        }
        if (rider instanceof Player player && this.random.nextInt(this.getTamingOdds()) == 0) {
            this.tameBy(player);
            return;
        }
        if (this.random.nextInt(LURCH_ODDS) == 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(
                    this.random.nextDouble() * LURCH_STRENGTH_X, 0.0D, this.random.nextDouble() * LURCH_STRENGTH_Z));
        }
        if (this.random.nextInt(BUCK_ODDS) == 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, BUCK_UPWARD_IMPULSE, 0.0D));
            this.ejectPassengers();
            this.playSound(ModSounds.DOLPHIN_UPSET.get(), this.getSoundVolume(), 1.0F);
        }
    }

    private int getTamingOdds() {
        return Math.max(this.getMaxTemper() - this.temper, 1) * TAMING_ODDS_FACTOR;
    }

    /** Never hurt by, or retaliating against, the entity riding it. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null && this.hasPassenger(attacker)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /** Immune to poison */
    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return !effectInstance.is(MobEffects.POISON) && super.canBeAffected(effectInstance);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    // ---------------------------------------------------------------------
    // Breeding
    // ---------------------------------------------------------------------

    /**
     * Two tamed adults in love mode that stay near each other, with no third dolphin around, have a
     * calf after {@link #GESTATION_TICKS}. Only the parent with the smaller UUID gives birth.
     */
    private void tickBreeding() {
        MoCDolphinEntity mate = this.isReadyToBreed() ? this.findMate() : null;
        if (mate == null) {
            this.gestationTicks = 0;
            return;
        }
        this.gestationTicks++;
        if (this.gestationTicks % HEART_INTERVAL_TICKS == 0) {
            this.spawnHearts();
        }
        if (this.gestationTicks < GESTATION_TICKS
                || this.getUUID().compareTo(mate.getUUID()) > 0
                || this.isCrowded(mate)) {
            return;
        }
        this.gestationTicks = 0;
        this.giveBirth(mate);
    }

    private boolean isReadyToBreed() {
        return this.isTame() && !this.isBaby() && !this.isVehicle() && this.isInLove();
    }

    @Nullable
    private MoCDolphinEntity findMate() {
        List<MoCDolphinEntity> mates = this.level().getEntitiesOfClass(MoCDolphinEntity.class,
                this.getBoundingBox().inflate(MATE_RANGE_HORIZONTAL, MATE_RANGE_VERTICAL, MATE_RANGE_HORIZONTAL),
                other -> other != this && other.isReadyToBreed());
        return mates.isEmpty() ? null : mates.get(0);
    }

    private boolean isCrowded(MoCDolphinEntity mate) {
        return !this.level().getEntitiesOfClass(MoCDolphinEntity.class,
                this.getBoundingBox().inflate(CROWD_RANGE_HORIZONTAL, CROWD_RANGE_VERTICAL, CROWD_RANGE_HORIZONTAL),
                other -> other != this && other != mate).isEmpty();
    }

    private void spawnHearts() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + this.getBbHeight() * 0.5D,
                    this.getZ(), 1, 0.4D, 0.3D, 0.4D, 0.0D);
        }
    }

    /** The calf is born tamed, with the same owner, and the owner is asked to name it. */
    private void giveBirth(MoCDolphinEntity mate) {
        MoCDolphinEntity calf = ModEntities.MOC_DOLPHIN.get().create(this.level());
        if (calf == null) {
            return;
        }
        calf.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
        calf.setVariant(MoCDolphinGenetics.resolveOffspring(this.getVariant(), mate.getVariant(), this.random));
        calf.setAge(-GROWTH_TICKS);
        calf.setTame(true, false);
        calf.setOwnerUUID(this.getOwnerUUID());
        this.level().addFreshEntity(calf);
        NamingHelper.promptRename(calf, this.getOwnerUUID());
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
        this.resetLove();
        mate.resetLove();
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    /** Turns one empty net into a filled one holding this dolphin, and removes it from the world. */
    private void captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        ItemStack filled = new ItemStack(ModItems.FISH_NET_FULL.get());
        filled.set(DataComponents.CUSTOM_DATA, CustomData.of(this.createNetTag(player)));
        if (!player.getAbilities().instabuild) {
            emptyNet.shrink(1);
        }
        if (emptyNet.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
        this.discard();
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putString(VARIANT_TAG, this.getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    /** Applies the data stored by {@link #createNetTag} to a freshly created dolphin. */
    public void restoreFromNet(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setVariant(DolphinVariant.byName(tag.getString(VARIANT_TAG)));
        this.setAge(tag.getInt("Age"));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }

    // ---------------------------------------------------------------------
    // Riding
    // ---------------------------------------------------------------------

    /** Only a tamed dolphin is steered by its rider; a wild one keeps its own AI and bucks. */
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.isTame() && this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        double x = this.getX() + Math.sin(yaw) * RIDER_BACK_OFFSET;
        double z = this.getZ() - Math.cos(yaw) * RIDER_BACK_OFFSET;
        moveFunction.accept(passenger, x, this.getY() + RIDER_Y_OFFSET, z);
    }

    /** A dolphin cannot swim on land, so it ignores the rider's input there. */
    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (!this.isInWater()) {
            return Vec3.ZERO;
        }
        return new Vec3(player.xxa * RIDDEN_STRAFE_FACTOR, 0.0D, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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

    /** Swims where the rider looks; jump goes up, the descend key goes down, and it holds its depth otherwise. */
    private void travelRidden(Player rider, Vec3 travelVector) {
        this.tickRidden(rider, travelVector);
        Vec3 input = this.getRiddenInput(rider, travelVector);
        double forward = input.z * this.getVariant().getMountSpeed() / MOUNT_SPEED_DIVISOR;
        this.moveRelative(RIDDEN_THRUST, new Vec3(input.x, 0.0D, forward));

        double vertical = 0.0D;
        if (this.isAscendHeld()) {
            vertical += RIDDEN_ASCEND_THRUST;
        }
        if (this.isDescendHeld()) {
            vertical -= RIDDEN_DESCEND_THRUST;
        }
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y + vertical, motion.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(RIDDEN_DRAG));
        // Vanilla's travel() updates the limb animation that beats the tail; this branch skips it.
        this.calculateEntityAnimation(false);
    }

    /** Without this a key still held when the rider leaves would keep pushing the next rider. */
    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.isVehicle()) {
            this.setAscendHeld(false);
            this.setDescendHeld(false);
        }
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

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    /**
     * Mob's default rejects any position with liquid in the hitbox, which would stop natural
     * spawns in water. WaterAnimal overrides it the same way.
     */
    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    /**
     * Like vanilla water animals: no preference for light. Animal's version only accepts bright spots,
     * so it barely spawned at night or in deep, dark water.
     */
    @Override
    public float getWalkTargetValue(net.minecraft.core.BlockPos pos, net.minecraft.world.level.LevelReader level) {
        return 0.0F;
    }

    /** A tamed dolphin never despawns and must not count against the spawn cap of wild ones. */
    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
    }

    /** Wild dolphins despawn like other water creatures instead of filling the mob cap forever. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    /** Original: only adults fight back, and never on Peaceful; calves flee instead. */
    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isBaby()
                && this.level().getDifficulty() != Difficulty.PEACEFUL
                && !this.hasPassenger(target)
                && super.canAttack(target);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        // Like vanilla mobs, a dolphin that dies on fire drops its meat cooked.
        MoCLootUtil.dropItems(this, MoCLootUtil.rawOrCooked(this, Items.COD, Items.COOKED_COD),
                MoCLootUtil.rollWithLootingBonus(this.random, MAX_COD_DROP + 1, lootingLevel));
    }


    @Override
    public boolean isFood(ItemStack stack) {
        return false; // dolphins are fed raw and cooked fish in the taming step, not through love mode
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // breeding is done by tickBreeding(), not by vanilla's love-mode goal
    }

    /** Wiki: 1-3 experience, awarded only when a player or a tamed wolf made the kill (vanilla's rule). */
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DOLPHIN_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DOLPHIN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DOLPHIN_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return AMBIENT_SOUND_INTERVAL;
    }

    @Override
    protected float getSoundVolume() {
        return SOUND_VOLUME;
    }

    // ---------------------------------------------------------------------
    // Goals and movement
    // ---------------------------------------------------------------------

    /** Original: only dolphins old enough to fight retaliate, so calves panic instead. */
    private static final class CalfPanicGoal extends PanicGoal {

        CalfPanicGoal(MoCDolphinEntity dolphin, double speedModifier) {
            super(dolphin, speedModifier);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isBaby() && super.shouldPanic();
        }
    }

    /**
     * Random swimming that keeps to the top of the water column, like the original's diving
     * depth: every destination lies between {@link #MIN_CRUISE_DEPTH} and {@link #MAX_CRUISE_DEPTH}
     * blocks under the surface. A dolphin that ended up deeper rises a few blocks at a time.
     */
    private static final class CruiseSwimGoal extends RandomSwimmingGoal {

        CruiseSwimGoal(MoCDolphinEntity dolphin, double speedModifier, int interval) {
            super(dolphin, speedModifier, interval);
        }

        /** Original: isMovementCeased() is true out of water, so it never wanders while stranded. */
        @Override
        public boolean canUse() {
            return this.mob.isInWater() && super.canUse();
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            Vec3 candidate = super.getPosition();
            return candidate == null ? null : this.moveToCruisingDepth(candidate);
        }

        /**
         * Keeps the horizontal position of the point but puts it at a random depth under the
         * surface. Falls back to the original point when that depth is not water (shallow bays).
         */
        @Nullable
        private Vec3 moveToCruisingDepth(Vec3 point) {
            BlockPos.MutableBlockPos surface = BlockPos.containing(point).mutable();
            if (!this.isWater(surface)) {
                return null;
            }
            for (int i = 0; i < MAX_SURFACE_SCAN && this.isWater(surface.above()); i++) {
                surface.move(Direction.UP);
            }
            double depth = MIN_CRUISE_DEPTH + this.mob.getRandom().nextDouble() * (MAX_CRUISE_DEPTH - MIN_CRUISE_DEPTH);
            double targetY = Mth.clamp(surface.getY() + 1.0D - depth,
                    this.mob.getY() - MAX_VERTICAL_STEP, this.mob.getY() + MAX_VERTICAL_STEP);
            Vec3 target = new Vec3(point.x, targetY, point.z);
            return this.isWater(BlockPos.containing(target)) ? target : point;
        }

        private boolean isWater(BlockPos pos) {
            return this.mob.level().getFluidState(pos).is(FluidTags.WATER);
        }
    }

    /** Same as vanilla fish: swims toward the path target, steering vertically a little at a time. */
    private static final class DolphinMoveControl extends MoveControl {
        private final MoCDolphinEntity dolphin;

        DolphinMoveControl(MoCDolphinEntity dolphin) {
            super(dolphin);
            this.dolphin = dolphin;
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO || this.dolphin.getNavigation().isDone()
                    || !this.dolphin.isInWater() || this.dolphin.getControllingPassenger() != null) {
                this.dolphin.setSpeed(0.0F);
                return;
            }
            float speed = (float) (this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.dolphin.setSpeed(Mth.lerp(0.125F, this.dolphin.getSpeed(), speed));

            double dx = this.wantedX - this.dolphin.getX();
            double dy = this.wantedY - this.dolphin.getY();
            double dz = this.wantedZ - this.dolphin.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 1.0E-5D) {
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement()
                        .add(0.0D, this.dolphin.getSpeed() * (dy / distance) * VERTICAL_STEERING, 0.0D));
            }
            if (dx != 0.0D || dz != 0.0D) {
                float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                this.dolphin.setYRot(this.rotlerp(this.dolphin.getYRot(), targetYaw, SWIM_MAX_TURN_DEGREES));
                this.dolphin.yBodyRot = this.dolphin.getYRot();
            }
        }
    }
}