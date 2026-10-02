package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityStingRay} (and its {@code MoCEntityRay}
 * base), built on {@link TamableAnimal} like the shark instead of the original's own aquatic
 * ownership framework.
 * <p>
 * Behaviour, as in the original:
 * <ul>
 *   <li>Swims slowly along the seabed; stranded on land it cannot move and suffocates after a
 *       while.</li>
 *   <li>A wild stingray poisons a player swimming right next to it, and lifts its tail while doing so.</li>
 *   <li>Drops nothing (the original's loot table is an empty placeholder).</li>
 * </ul>
 * Not implemented yet: taming and natural spawning.
 */
public class MoCStingrayEntity extends TamableAnimal implements StorablePet {


    /** NBT flag that marks a filled fish net as holding a stingray. */
    public static final String NET_KEY = "Stingray";
    /** The original sets the stingray's age to 90, which renders it at 0.9x size. */
    private static final double SCALE = 0.9D;

    // ---- Poison ----
    /** Minimum ticks between two poisonings (the counter also has to be exceeded on the first one). */
    private static final int POISON_COOLDOWN_TICKS = 250;
    /** Once off cooldown, a 1 in N chance per tick of attempting to poison. */
    private static final int POISON_ATTEMPT_ODDS = 30;
    private static final double POISON_RANGE = 2.0D;
    private static final int POISON_DURATION_TICKS = 120;
    private static final int TAIL_LASH_TICKS = 50;
    /** Melee reach: a player who hits the stingray is close enough to be stung in return. */
    private static final double PROVOKED_POISON_RANGE = 3.0D;

    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    // ---- Swimming ----
    private static final double WATER_DRAG = 0.9D;
    private static final double WATER_SINK_PER_TICK = 0.005D;
    /** Upward push when it bumps into a ledge while swimming, so it can climb it (original: 0.05). */
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    private static final int MAX_SEABED_TARGET_ATTEMPTS = 8;
    /** Deepest water column it looks through for a floor when picking a destination. */
    private static final int MAX_SEABED_SCAN = 64;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    /** Same water movement as the original aquatic mobs and vanilla fish: thrust per tick of forward input. */
    private static final float WATER_THRUST = 0.1F;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private static final double VERTICAL_STEERING = 0.1D;


    private static final EntityDataAccessor<Boolean> DATA_TAIL_LASH =
            SynchedEntityData.defineId(MoCStingrayEntity.class, EntityDataSerializers.BOOLEAN);

    private int outOfWaterTicks;
    private int poisonCooldownTicks;
    private int tailLashTicksLeft;

    public MoCStingrayEntity(EntityType<? extends MoCStingrayEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost every
        // water position for this mob, and the stingray never finds anywhere to swim to.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new StingrayMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                // Original: getAIMoveSpeed() = 0.06.
                .add(Attributes.MOVEMENT_SPEED, 0.06D)
                .add(Attributes.SCALE, SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        // Original: EntityAIWanderMoC2(1.0D, 80).
        this.goalSelector.addGoal(2, new SeabedSwimGoal(this, 1.0D, 10));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TAIL_LASH, false);
    }

    /** True while the tail is lifted after poisoning someone. Read by the model. */
    public boolean isPoisoning() {
        return this.entityData.get(DATA_TAIL_LASH);
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickOutOfWater();
            this.tickPoison();
        }
    }

    /** Stranded rays cannot move (no water to path through) and eventually suffocate. */
    private void tickOutOfWater() {
        if (this.isInWaterOrBubble()) {
            this.outOfWaterTicks = 0;
            return;
        }
        this.outOfWaterTicks++;
        if (this.outOfWaterTicks > SUFFOCATION_GRACE_TICKS && this.outOfWaterTicks % SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), SUFFOCATION_DAMAGE);
        }
    }

    private void tickPoison() {
        if (this.tailLashTicksLeft > 0 && --this.tailLashTicksLeft == 0) {
            this.entityData.set(DATA_TAIL_LASH, false);
        }
        if (this.isTame() || ++this.poisonCooldownTicks <= POISON_COOLDOWN_TICKS) {
            return;
        }
        if (this.random.nextInt(POISON_ATTEMPT_ODDS) == 0) {
            this.tryPoison(this.level().getNearestPlayer(this, POISON_RANGE), POISON_RANGE);
        }
    }

    /** Being hit makes a wild stingray sting the attacker back, without waiting for the cooldown. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && !this.level().isClientSide && !this.isTame() && source.getEntity() instanceof Player attacker) {
            this.tryPoison(attacker, PROVOKED_POISON_RANGE);
        }
        return wasHurt;
    }

    // ---------------------------------------------------------------------
    // Taming: a fish net catches the stingray; releasing the net makes it a pet
    // ---------------------------------------------------------------------

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }
        // A wild stingray can be caught by anyone; a tame one only by its owner.
        if (stack.is(ModItems.FISH_NET.get()) && (!this.isTame() || this.isOwnedBy(player))) {
            if (!this.level().isClientSide) {
                this.captureInFishNet(player, hand, stack);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /** Turns one empty net into a filled one holding this stingray, and removes it from the world. */
    private void captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", player.getUUID());

        PetStorageUtil.storeConsumingOne(player, hand, emptyNet, this, ModItems.FISH_NET_FULL.get(), tag);
    }

    /**
     * Stings the player and lifts the tail. Nothing happens to players on land, riding anything
     * (a boat, a horse...), in creative, or on Peaceful.
     */
    private boolean tryPoison(@Nullable Player target, double range) {
        if (target == null || this.level().getDifficulty() == Difficulty.PEACEFUL
                || this.distanceTo(target) >= range
                || !target.isInWater() || target.isPassenger() || target.getAbilities().invulnerable) {
            return false;
        }
        target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
        this.poisonCooldownTicks = 0;
        this.tailLashTicksLeft = TAIL_LASH_TICKS;
        this.entityData.set(DATA_TAIL_LASH, true);
        return true;
    }

    // ---------------------------------------------------------------------
    // Water: swims along the seabed
    // ---------------------------------------------------------------------

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // Same water movement as vanilla fish: the move control sets the forward input, this turns
        // it into thrust, and drag slows it down again.
        this.moveRelative(WATER_THRUST, travelVector);
        if (this.horizontalCollision && !this.getNavigation().isDone()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, Math.max(motion.y, CLIMB_LEDGE_IMPULSE), motion.z);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 motion = this.getDeltaMovement().scale(WATER_DRAG);
        // Idle rays sink slowly to the bottom; swimming ones follow their path along it.
        double sink = this.getNavigation().isDone() ? WATER_SINK_PER_TICK : 0.0D;
        this.setDeltaMovement(motion.x, motion.y - sink, motion.z);
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
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    /** Wild stingrays despawn like other water creatures instead of filling the mob cap forever. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    /** Stingrays have no baby stage, so vanilla's 5% baby roll at spawn must not make one. */
    @Override
    public void setAge(int age) {
        super.setAge(0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // stingrays do not breed
    }

     /**
     * Mob's default rejects any position with liquid in the hitbox, which would stop natural spawns in
     * water. WaterAnimal overrides it the same way.
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
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    // ---------------------------------------------------------------------
    // Goals and movement
    // ---------------------------------------------------------------------

    /**
     * Random swimming that keeps the current depth. Depth is handled by {@link #applyDepthControl()};
     * targets at other depths would never be reached by the horizontal-only move control.
     */

    /**
     * Random swimming that picks its destinations on the seabed, so stingrays cruise along the
     * bottom like real ones instead of hovering in mid-water.
     */
    private static final class SeabedSwimGoal extends RandomSwimmingGoal {

        SeabedSwimGoal(MoCStingrayEntity ray, double speedModifier, int interval) {
            super(ray, speedModifier, interval);
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            for (int attempt = 0; attempt < MAX_SEABED_TARGET_ATTEMPTS; attempt++) {
                Vec3 candidate = super.getPosition();
                Vec3 seabed = candidate == null ? null : this.projectOntoSeabed(candidate);
                if (seabed != null) {
                    return seabed;
                }
            }
            return null;
        }

        /** Slides a swimmable point straight down through the water to just above the floor. */
        @Nullable
        private Vec3 projectOntoSeabed(Vec3 point) {
            BlockPos.MutableBlockPos pos = BlockPos.containing(point).mutable();
            if (!this.isWater(pos)) {
                return null;
            }
            for (int i = 0; i < MAX_SEABED_SCAN; i++) {
                pos.move(0, -1, 0);
                if (!this.isWater(pos)) {
                    pos.move(0, 1, 0);
                    return new Vec3(point.x, pos.getY(), point.z);
                }
            }
            return null;
        }

        private boolean isWater(BlockPos pos) {
            return this.mob.level().getFluidState(pos).is(FluidTags.WATER);
        }
    }

    /** Same as vanilla fish: swims toward the path target, steering vertically a little at a time. */
    private static final class StingrayMoveControl extends MoveControl {
        private final MoCStingrayEntity ray;

        StingrayMoveControl(MoCStingrayEntity ray) {
            super(ray);
            this.ray = ray;
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO || this.ray.getNavigation().isDone()
                    || !this.ray.isInWater()) {
                // A stranded ray does not move at all.
                this.ray.setSpeed(0.0F);
                return;
            }
            float speed = (float) (this.speedModifier * this.ray.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.ray.setSpeed(Mth.lerp(0.125F, this.ray.getSpeed(), speed));

            double dx = this.wantedX - this.ray.getX();
            double dy = this.wantedY - this.ray.getY();
            double dz = this.wantedZ - this.ray.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 1.0E-5D) {
                this.ray.setDeltaMovement(this.ray.getDeltaMovement()
                        .add(0.0D, this.ray.getSpeed() * (dy / distance) * VERTICAL_STEERING, 0.0D));
            }
            if (dx != 0.0D || dz != 0.0D) {
                float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                this.ray.setYRot(this.rotlerp(this.ray.getYRot(), targetYaw, SWIM_MAX_TURN_DEGREES));
                this.ray.yBodyRot = this.ray.getYRot();
            }
        }
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #captureInFishNet} when a Fish Net releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }
}
