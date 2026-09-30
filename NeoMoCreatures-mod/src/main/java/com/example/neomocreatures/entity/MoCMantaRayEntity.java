package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.ai.AquaticMoveControl;
import com.example.neomocreatures.entity.ai.DepthBandSwimGoal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.NamingHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityMantaRay} (and its {@code MoCEntityRay}
 * base), built on {@link TamableAnimal} like the shark, the stingray and the dolphin instead of the
 * original's own aquatic ownership framework.
 * <p>
 * A big, slow, harmless ray that glides a few blocks under the surface and suffocates out of water.
 * A wild one is tamed by riding it until it accepts you (or by catching it in a fish net and
 * releasing it), and a tamed one is ridden without a saddle. Not implemented yet: natural spawning.
 * <p>
 * Like the original it makes no sounds of its own, never flees and never attacks, and drops nothing.
 */
public class MoCMantaRayEntity extends TamableAnimal {

    /** Original: age 180 gives a size factor of 1.5 (capped), which scales the whole model. */
    private static final double SCALE = 1.5D;
    private static final double MAX_HEALTH = 20.0D;
    /** Original: getAIMoveSpeed() = 0.06, the same as the stingray. */
    private static final double SWIM_SPEED = 0.06D;

    // ---- Swimming ----
    private static final double WANDER_SPEED = 1.0D;
    private static final int WANDER_INTERVAL = 10;
    /** Original: minDivingDepth() / maxDivingDepth() of the rays, in blocks under the water surface. */
    private static final double MIN_CRUISE_DEPTH = 3.0D;
    private static final double MAX_CRUISE_DEPTH = 6.0D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    /** Same water movement as the original aquatic mobs: thrust per tick of forward input. */
    private static final float WATER_THRUST = 0.1F;
    private static final double WATER_DRAG = 0.9D;
    /** Upward push when it bumps into a ledge while swimming, so it can climb it (original: 0.05). */
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private static final double VERTICAL_STEERING = 0.1D;

    // ---- Taming ----
    /**
     * Original: a ridden wild ray is tamed each tick with odds of 1 in (maxTemper 100 - temper 50) * 8.
     * No food changes its temper, so the odds never change.
     */
    private static final int TAMING_ODDS = 400;

    // ---- Bucking (wild manta ray with a rider) ----
    /** Original: each tick a 1 in 100 chance to throw the rider off with an upward kick. */
    private static final int BUCK_ODDS = 100;
    private static final double BUCK_UPWARD_IMPULSE = 0.2D;
    /** Original: each tick a 1 in 10 chance to lurch sideways. */
    private static final int LURCH_ODDS = 10;
    private static final double LURCH_STRENGTH_X = 1.0D / 30.0D;
    private static final double LURCH_STRENGTH_Z = 1.0D / 10.0D;

    // ---- Riding (tamed manta ray) ----
    /** Original: the ray's custom speed, 1.5; forward input is scaled by it divided by 5. */
    private static final double MOUNT_SPEED = 1.5D;
    private static final double MOUNT_SPEED_DIVISOR = 5.0D;
    /** Original: strafing while ridden is cut to 35%. */
    private static final double RIDDEN_STRAFE_FACTOR = 0.35D;
    private static final float RIDDEN_THRUST = 0.1F;
    private static final double RIDDEN_DRAG = 0.8D;
    /** Per tick while the jump / descend key is held (same values as the dolphin and the komodo dragon). */
    private static final double RIDDEN_ASCEND_THRUST = 0.05D;
    private static final double RIDDEN_DESCEND_THRUST = 0.05D;
    /** Original: the rider sits centred on the ray, 0.26 blocks below its origin. */
    private static final double RIDER_Y_OFFSET = -0.26D;

    /** NBT flag that marks a filled fish net as holding a manta ray. */
    public static final String NET_KEY = "MantaRay";

    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCMantaRayEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCMantaRayEntity.class, EntityDataSerializers.BOOLEAN);

    private float lastAppliedScale = -1.0F;
    private int outOfWaterTicks;

    public MoCMantaRayEntity(EntityType<? extends MoCMantaRayEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost
        // every water position for this mob, and the manta ray never finds anywhere to swim to.
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
        // Original: EntityAIWanderMoC2(1.0D, 80), kept between its diving depths.
        this.goalSelector.addGoal(2, new DepthBandSwimGoal(this, WANDER_SPEED, WANDER_INTERVAL,
                MIN_CRUISE_DEPTH, MAX_CRUISE_DEPTH));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
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
            this.tickWildRider();
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

    /** Stranded manta rays cannot move (no water to path through) and eventually suffocate. */
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
        }
        if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // The move control sets the forward input, this turns it into thrust and drag slows it
        // down again. There is no gravity: an idle manta ray keeps its depth.
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
        InteractionResult result = InteractionResult.PASS;
        if (!this.isTame()) {
            result = this.interactAsStranger(player, hand, stack);
        } else if (this.isOwnedBy(player)) {
            result = this.interactAsOwner(player, hand, stack);
        }
        return result != InteractionResult.PASS ? result : super.mobInteract(player, hand);
    }

    /** A wild manta ray can be caught by anyone (releasing the net tames it), or ridden until it accepts. */
    private InteractionResult interactAsStranger(Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(ModItems.FISH_NET.get()) && !this.isVehicle()) {
            return this.captureInFishNet(player, hand, stack);
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
            return this.captureInFishNet(player, hand, stack);
        }
        if (isEntityScroll(stack)) {
            return InteractionResult.PASS;
        }
        return this.mount(player);
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

    /** A wild manta ray with a rider tries to throw it off, and every tick the rider might tame it. */
    private void tickWildRider() {
        Entity rider = this.getFirstPassenger();
        if (rider == null || this.isTame()) {
            return;
        }
        if (!this.isInWater()) {
            this.ejectPassengers();
            return;
        }
        if (rider instanceof Player player && this.random.nextInt(TAMING_ODDS) == 0) {
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
        }
    }

    /** Never hurt by the entity riding it. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null && this.hasPassenger(attacker)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /** Original: aquatic mobs take no fall damage, so a manta ray that leaves the water lands unharmed. */
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    /** Turns one empty net into a filled one holding this manta ray, and removes it from the world. */
    private InteractionResult captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        if (!this.level().isClientSide) {
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
        return InteractionResult.SUCCESS;
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    /** Applies the data stored by {@link #createNetTag} to a freshly created manta ray. */
    public void restoreFromNet(CompoundTag tag) {
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

    // ---------------------------------------------------------------------
    // Riding
    // ---------------------------------------------------------------------

    /** Only a tamed manta ray is steered by its rider; a wild one keeps its own AI and bucks. */
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.isTame() && this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            moveFunction.accept(passenger, this.getX(), this.getY() + RIDER_Y_OFFSET, this.getZ());
        }
    }

    /** A manta ray cannot swim on land, so it ignores the rider's input there. */
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

    /**
     * Swims where the rider looks; jump goes up, the descend key goes down, and it holds its depth
     * otherwise. Speed potions on the ray scale the forward thrust through its speed attribute.
     */
    private void travelRidden(Player rider, Vec3 travelVector) {
        this.tickRidden(rider, travelVector);
        Vec3 input = this.getRiddenInput(rider, travelVector);
        double speedBoost = this.getAttributeValue(Attributes.MOVEMENT_SPEED) / SWIM_SPEED;
        double forward = input.z * MOUNT_SPEED / MOUNT_SPEED_DIVISOR * speedBoost;
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
        // Vanilla's travel() updates the limb animation that beats the wings; this branch skips it.
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

    /** A tamed manta ray never despawns and must not count against the spawn cap of wild ones. */
    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
    }

    /** Wild manta rays despawn like other water creatures instead of filling the mob cap forever. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    /** Manta rays have no baby stage, so vanilla's 5% baby roll at spawn must not make one. */
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
        return null; // manta rays do not breed
    }
}