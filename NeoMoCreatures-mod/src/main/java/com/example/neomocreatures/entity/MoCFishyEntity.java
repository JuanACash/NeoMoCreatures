package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.ai.AquaticMoveControl;
import com.example.neomocreatures.entity.ai.DepthBandSwimGoal;
import com.example.neomocreatures.entity.fishy.FishyVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityFishy}, built on {@link TamableAnimal}
 * like the other aquatic mobs instead of the original's own aquatic ownership framework.
 * <p>
 * Ten colour variants, a tiny fish that swims just under the surface, flees from anything bigger
 * than itself, suffocates out of water, can be caught and tamed with a fish net, and regenerates
 * slowly once tamed. Not implemented yet: natural spawning.
 * <p>
 * Like the original it is passive, makes no sounds of its own and cannot be bred (the original's
 * breeding code is disabled).
 */
public class MoCFishyEntity extends TamableAnimal implements StorablePet {

    /** Original: age 100 with a size factor of age * 0.006 gives a model scale of 0.6. */
    private static final double SCALE = 0.6D;
    private static final double MAX_HEALTH = 3.0D;
    /** Original: getAIMoveSpeed() = 0.10. */
    private static final double SWIM_SPEED = 0.10D;

    // ---- Goals ----
    private static final int PANIC_PRIORITY = 2;
    private static final double PANIC_SPEED = 1.3D;
    private static final int FLEE_PRIORITY = 3;
    /** Original: flees from every entity bigger than 0.3 blocks, when it is within 2 blocks. */
    private static final float FLEE_DISTANCE = 2.0F;
    private static final float FLEE_SIZE_THRESHOLD = 0.3F;
    private static final double FLEE_FAR_SPEED = 0.6D;
    private static final double FLEE_NEAR_SPEED = 1.5D;
    private static final int WANDER_PRIORITY = 5;
    private static final double WANDER_SPEED = 1.0D;
    private static final int WANDER_INTERVAL = 80;

    // ---- Swimming ----
    /** Original: the fishy's maxDivingDepth() is 2 and the base class's minDivingDepth() is 0.2. */
    private static final double MIN_CRUISE_DEPTH = 0.2D;
    private static final double MAX_CRUISE_DEPTH = 2.0D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    /** Same water movement as the original aquatic mobs: thrust per tick of forward input. */
    private static final float WATER_THRUST = 0.1F;
    private static final double WATER_DRAG = 0.9D;
    /** Upward push when it bumps into a ledge while swimming, so it can climb it (original: 0.05). */
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private static final double VERTICAL_STEERING = 0.1D;

    // ---- Taming ----
    /** Original: a 1 in 100 chance per tick to fully heal, once tamed. */
    private static final int REGEN_CHANCE = 100;

    /** NBT flag that marks a filled fish net as holding a fishy. */
    public static final String NET_KEY = "Fishy";
    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    private static final String VARIANT_TAG = "FishyVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCFishyEntity.class, EntityDataSerializers.INT);

    private float lastAppliedScale = -1.0F;
    private int outOfWaterTicks;

    public MoCFishyEntity(EntityType<? extends MoCFishyEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost
        // every water position for this mob, and the fishy never finds anywhere to swim to.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AquaticMoveControl(this, SWIM_MAX_TURN_DEGREES, VERTICAL_STEERING);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, SWIM_SPEED)
                .add(Attributes.SCALE, SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(PANIC_PRIORITY, new PanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(FLEE_PRIORITY, new AvoidEntityGoal<>(this, LivingEntity.class, FLEE_DISTANCE,
                FLEE_FAR_SPEED, FLEE_NEAR_SPEED, MoCFishyEntity::isBigEnoughToFleeFrom));
        this.goalSelector.addGoal(WANDER_PRIORITY, new DepthBandSwimGoal(this, WANDER_SPEED, WANDER_INTERVAL,
                MIN_CRUISE_DEPTH, MAX_CRUISE_DEPTH));
    }

    private static boolean isBigEnoughToFleeFrom(LivingEntity entity) {
        return entity.getBbHeight() > FLEE_SIZE_THRESHOLD || entity.getBbWidth() > FLEE_SIZE_THRESHOLD;
    }

    // ---------------------------------------------------------------------
    // Variant
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, FishyVariant.BLUE.getId());
    }

    public FishyVariant getVariant() {
        return FishyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(FishyVariant variant) {
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
            this.setVariant(FishyVariant.byName(tag.getString(VARIANT_TAG)));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(FishyVariant.random(this.random));
        // Fishies have no baby stage, so AgeableMob's random baby roll is turned off. The colour is
        // rolled per fishy, not per group.
        return super.finalizeSpawn(level, difficulty, spawnType, new AgeableMob.AgeableMobGroupData(false));
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        this.refreshHitboxOnScaleChange();
        if (!this.level().isClientSide) {
            this.tickOutOfWater();
            this.tickRegeneration();
        }
    }

    /** The scale attribute is set at creation, so the hitbox has to be refreshed once to match it. */
    private void refreshHitboxOnScaleChange() {
        float current = this.getScale();
        if (this.lastAppliedScale != current) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    /** Stranded fishies cannot move (no water to path through) and eventually suffocate. */
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

    /** Original: a tamed fishy has a 1 in 100 chance per tick to fully heal, with no food involved. */
    private void tickRegeneration() {
        if (this.isTame() && this.getHealth() < this.getMaxHealth() && this.random.nextInt(REGEN_CHANCE) == 0) {
            this.setHealth(this.getMaxHealth());
        }
    }

    // ---------------------------------------------------------------------
    // Water
    // ---------------------------------------------------------------------

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // The move control sets the forward input, this turns it into thrust and drag slows it
        // down again. There is no gravity: an idle fishy keeps its depth.
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.FISH_NET.get()) && (!this.isTame() || this.isOwnedBy(player))) {
            return this.captureInFishNet(player, hand, stack);
        }
        if (this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }
        return super.mobInteract(player, hand);
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    /** Turns one empty net into a filled one holding this fishy, and removes it from the world. */
    private InteractionResult captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        if (!this.level().isClientSide) {
            PetStorageUtil.storeConsumingOne(player, hand, emptyNet, this, ModItems.FISH_NET_FULL.get(), this.createNetTag(player));
        }
        return InteractionResult.SUCCESS;
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putString(VARIANT_TAG, this.getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        // A wild fishy caught and released is tamed on the spot, like the original.
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    /** Applies the data stored by {@link #createNetTag} to a freshly created fishy. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setVariant(FishyVariant.byName(tag.getString(VARIANT_TAG)));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }

    /** A tamed fishy never despawns and must not count against the spawn cap of wild ones. */
    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
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

    /** Wild fishies despawn like other water creatures instead of filling the mob cap forever. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // fishies do not breed
    }

    /** Wiki: 0-2 raw fish, no eggs, and Looting adds up to its level extra. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        MoCLootUtil.dropItems(this, Items.TROPICAL_FISH, MoCLootUtil.rollWithLootingBonus(this.random, 3, MoCLootUtil.getLootingLevel(level, damageSource)));
    }


    /** Wiki: 1-3 experience, awarded only when a player or a tamed wolf made the kill (vanilla's rule). */
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }
}