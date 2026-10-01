package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.ai.AquaticMoveControl;
import com.example.neomocreatures.entity.ai.DepthBandSwimGoal;
import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityMediumFish}, the shared behaviour of the
 * three medium fish (cod, salmon, bass), each its own {@link EntityType} with its own spawn egg, as
 * in the original and the wiki, built on {@link TamableAnimal} instead of the original's own aquatic
 * ownership framework.
 * <p>
 * A mid-sized fish that swims 0.5-4 blocks under the surface, flees anything bigger than 0.6x0.3
 * blocks and suffocates out of water. Unlike the fishy, it takes normal fall damage and can burn —
 * the original only exempts it from drowning. Healing needs no extra code: a splash potion of
 * Healing or Regeneration already reaches it through vanilla's own potion-effect area.
 * <p>
 * Tamed either by catching it in a fish net (wild or already tame — releasing it always tames it, per
 * the wiki) or by hatching its egg in water. A hatched fish is a growing baby that reaches, over
 * {@link #GROWTH_TICKS}, a random adult size between {@link #MIN_GROWN_SCALE} and
 * {@link #MAX_GROWN_SCALE} (the wiki's "2 to 2.5 blocks long"); a wild-caught adult keeps the fixed
 * wild size instead, since it was never a growing pet.
 */
public abstract class MoCMediumFishEntity extends TamableAnimal implements EggHatchable, GrowthScaled, StorablePet {

    /** Original: age 100 with a size factor of age * 0.0081 gives a model scale of 0.81. */
    private static final double SCALE = 0.81D;
    private static final float WILD_ADULT_SCALE = (float) SCALE;
    private static final double MAX_HEALTH = 7.0D;
    /** Original: getAIMoveSpeed() = 0.15. */
    private static final double SWIM_SPEED = 0.15D;

    // ---- Goals ----
    /** Original: flees only from things taller than 0.6 AND wider than 0.3 blocks (unlike the fishy's OR). */
    private static final float FLEE_HEIGHT_THRESHOLD = 0.6F;
    private static final float FLEE_WIDTH_THRESHOLD = 0.3F;
    private static final int FLEE_PRIORITY = 3;
    private static final float FLEE_DISTANCE = 2.0F;
    private static final double FLEE_FAR_SPEED = 0.6D;
    private static final double FLEE_NEAR_SPEED = 1.5D;
    private static final int WANDER_PRIORITY = 5;
    private static final double WANDER_SPEED = 1.0D;
    // Vanilla's own fish (AbstractFish) use 10 here; our earlier 50 meant a long average pause
    // between swims, which read as standing still.
    private static final int WANDER_INTERVAL = 10;

    // ---- Swimming ----
    private static final double MIN_CRUISE_DEPTH = 0.5D;
    private static final double MAX_CRUISE_DEPTH = 4.0D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    /** Same water movement as the original aquatic mobs: thrust per tick of forward input. */
    private static final float WATER_THRUST = 0.1F;
    private static final double WATER_DRAG = 0.9D;
    /** Upward push when it bumps into a ledge while swimming, so it can climb it (original: 0.05). */
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private static final double VERTICAL_STEERING = 0.1D;

    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    // ---- Growth (tamed pets only; a wild-spawned fish is always a fixed-size adult) ----
    /** Original hitbox width (0.7) times this is roughly how long a full-grown pet ends up. */
    private static final float TINY_HATCHLING_SCALE = 0.2F;
    /** Wiki: "eventually... grow up to be around 2 to 2.5 blocks long" — not given a duration, so
     *  this picks a slow, pet-tank pace rather than the few minutes wild-mob growth normally takes. */
    private static final int GROWTH_TICKS = 24000;
    /** 2.0 / 0.7 and 2.5 / 0.7 blocks, converted to the SCALE attribute against the declared hitbox. */
    private static final float MIN_GROWN_SCALE = 2.857F;
    private static final float MAX_GROWN_SCALE = 3.571F;
    private static final double SCALE_SYNC_THRESHOLD = 0.01D;

    /** NBT flag that marks a filled fish net as holding a medium fish (any of the three species). */
    public static final String NET_KEY = "MediumFish";

    private static final EntityDataAccessor<Float> DATA_GROWN_SCALE =
            SynchedEntityData.defineId(MoCMediumFishEntity.class, EntityDataSerializers.FLOAT);

    private float lastAppliedScale = -1.0F;
    private int outOfWaterTicks;

    protected MoCMediumFishEntity(EntityType<? extends MoCMediumFishEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost
        // every water position for this mob, and the fish never finds anywhere to swim to.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AquaticMoveControl(this, SWIM_MAX_TURN_DEGREES, VERTICAL_STEERING);
    }

    /** The texture file name under {@code textures/entity/moc_medium_fish/}, without the extension. */
    public abstract String getTextureName();

    
    /** The raw fish item this species drops (bass has no vanilla equivalent, so it drops a tropical fish). */
    public abstract Item getRawFishItem();

    /** This species' own egg item, for the "0-2 eggs of that variant" drop. */
    public abstract Item getEggItem();

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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // 0 means "not a growing pet": a wild adult just uses the fixed WILD_ADULT_SCALE constant.
        builder.define(DATA_GROWN_SCALE, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("GrownScale", this.entityData.get(DATA_GROWN_SCALE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("GrownScale")) {
            this.entityData.set(DATA_GROWN_SCALE, tag.getFloat("GrownScale"));
        }
    }

    @Override
    protected void registerGoals() {
        // Original: no panic goal, only fleeing from things bigger than itself.
        this.goalSelector.addGoal(FLEE_PRIORITY, new AvoidEntityGoal<>(this, LivingEntity.class, FLEE_DISTANCE,
                FLEE_FAR_SPEED, FLEE_NEAR_SPEED, MoCMediumFishEntity::isBigEnoughToFleeFrom));
        this.goalSelector.addGoal(WANDER_PRIORITY, new DepthBandSwimGoal(this, WANDER_SPEED, WANDER_INTERVAL,
                MIN_CRUISE_DEPTH, MAX_CRUISE_DEPTH));
    }

    private static boolean isBigEnoughToFleeFrom(LivingEntity entity) {
        return entity.getBbHeight() > FLEE_HEIGHT_THRESHOLD && entity.getBbWidth() > FLEE_WIDTH_THRESHOLD;
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
        }
    }

    /** Server: grows a hatched baby toward its rolled adult size. Both sides: refresh the hitbox when it changes. */
    private void tickGrowth() {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        if (scale == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = this.getGrowthScale();
            if (this.isBaby() || Math.abs(scale.getBaseValue() - target) >= SCALE_SYNC_THRESHOLD) {
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

    /** An adult keeps its rolled size (or the fixed wild size if it was never a growing pet). A baby
     *  grows from {@link #TINY_HATCHLING_SCALE} toward that same rolled size over {@link #GROWTH_TICKS}. */
    private float getGrowthScale() {
        float grownTarget = this.entityData.get(DATA_GROWN_SCALE);
        if (grownTarget <= 0.0F) {
            grownTarget = WILD_ADULT_SCALE;
        }
        if (!this.isBaby()) {
            return grownTarget;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, TINY_HATCHLING_SCALE, grownTarget);
    }

    /** Stranded fish cannot move (no water to path through) and eventually suffocate. */
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
        if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // The move control sets the forward input, this turns it into thrust and drag slows it
        // down again. There is no gravity: an idle fish keeps its depth.
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
        // NeoForge's replacement for canBreatheUnderwater(). Unlike the fishy, this mob still takes
        // normal fall damage and can burn (the original exempts it only from drowning).
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
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

    /** Wild fish despawn like other water creatures instead of filling the mob cap forever. */
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
        return null; // medium fish do not breed
    }

        /** Wiki: 0-2 raw fish of its own kind and 0-2 eggs of its own variant, both scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, this.getRawFishItem(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
        MoCLootUtil.dropItems(this, this.getEggItem(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }


    /** Wiki: 1-3 experience, awarded only when a player or a tamed wolf made the kill (vanilla's rule). */
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    // ---------------------------------------------------------------------
    // Hatching from an egg
    // ---------------------------------------------------------------------

    /** Wiki: a hatched medium fish is a growing baby, tamed to whoever was nearby. */
    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        this.rollGrownScale();
        this.setAge(-GROWTH_TICKS);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    /** Rolls once, at hatch or at capture time, the size (2 to 2.5 blocks long) this individual grows into. */
    private void rollGrownScale() {
        float grownScale = MIN_GROWN_SCALE + this.random.nextFloat() * (MAX_GROWN_SCALE - MIN_GROWN_SCALE);
        this.entityData.set(DATA_GROWN_SCALE, grownScale);
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

    /** A tamed medium fish never despawns and must not count against the spawn cap of wild ones. */
    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    /** Turns one empty net into a filled one holding this fish, and removes it from the world. */
    private InteractionResult captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        if (!this.level().isClientSide) {
            PetStorageUtil.storeConsumingOne(player, hand, emptyNet, this, ModItems.FISH_NET_FULL.get(), this.createNetTag(player));
        }
        return InteractionResult.SUCCESS;
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        // Which of the 3 species this is, so a single "release" path can spawn the right one back.
        tag.putString("EntityId", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
        tag.putFloat("Health", this.getHealth());
        tag.putInt("Age", this.getAge());
        tag.putFloat("GrownScale", this.entityData.get(DATA_GROWN_SCALE));
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    /** Applies the data stored by {@link #createNetTag} to a freshly created fish, and tames it: the
     *  wiki tames on release from a net regardless of whether it was tame when caught. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setAge(tag.getInt("Age"));
        this.entityData.set(DATA_GROWN_SCALE, tag.getFloat("GrownScale"));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }
}