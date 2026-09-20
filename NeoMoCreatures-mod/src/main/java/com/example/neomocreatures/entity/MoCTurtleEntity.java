package com.example.neomocreatures.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetCarryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.level.LevelReader;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityTurtle}.
 * <p>
 * Behaviour, as in the original:
 * <ul>
 *   <li>Amphibious. Wild turtles hide in their shell when something bigger
 *       than a small critter comes close, and are immune while hiding.</li>
 *   <li>Right-clicking a wild turtle (or hitting it) flips it upside down; it
 *       struggles for a while and then rights itself.</li>
 *   <li>Tamed by eating melon slices or sugar cane, either dropped on the
 *       ground near it or fed by hand.</li>
 *   <li>A tamed turtle follows its owner, can be carried on the owner's head
 *       and slowly grows into a "mega turtle" (3x size).</li>
 *   <li>Named after a Ninja Turtle it becomes an easter egg, see {@link TmntBrother}.</li>
 * </ul>
 * The original stored the turtle's size in its "age" field and scaled the
 * model by hand. Here size is the vanilla {@link Attributes#SCALE} attribute,
 * so the hitbox, shadow and name tag follow it and it is saved/synced for free.
 */
public class MoCTurtleEntity extends TamableAnimal implements CarriedPet {

    /** NBT flag that marks a Pet Amulet as holding a turtle. */
    public static final String AMULET_KEY = "Turtle";

    // ---- Size: wild adults are small, tamed turtles slowly grow ----
    private static final double WILD_SCALE = 0.9D;
    private static final double MAX_SCALE = 3.0D;
    private static final double GROWTH_STEP = 0.01D;
    /** A tamed turtle has a 1 in N chance per tick of growing one step (about 45 s on average). */
    private static final int GROWTH_STEP_ODDS = 900;
    /** Smaller changes are float noise, not worth a refreshDimensions() call. */
    private static final float SCALE_CHANGE_THRESHOLD = 0.001F;

    // ---- Hiding (wild turtles only) ----
    private static final double THREAT_SCAN_RADIUS = 4.0D;
    private static final int THREAT_SCAN_INTERVAL_TICKS = 10;
    /** Anything smaller than this in both width and height is not scary. */
    private static final float MIN_THREAT_SIZE = 0.5F;
    /** A hit on a hiding turtle has a 1 in N chance of flipping it over (without hurting it). */
    private static final int HIDING_FLIP_ODDS = 10;

    // ---- Flipping ----
    /** A hit that hurts has a 1 in N chance of flipping the turtle over. */
    private static final int HURT_FLIP_ODDS = 3;
    private static final int MIN_STRUGGLE_TICKS = 160;
    private static final int EXTRA_STRUGGLE_TICKS = 100;

    // ---- Food and taming ----
    private static final double FOOD_SEARCH_RADIUS = 10.0D;
    private static final double PLAYER_TAME_RADIUS = 24.0D;

    // ---- Being carried on the owner's head ----
    private static final int PICKUP_COOLDOWN_TICKS = 10;
    /** Height of the turtle's feet above the holder's eyes for a wild-sized turtle. */
    private static final double HOLD_OFFSET_ABOVE_EYES = 0.15D;
    /** Bigger turtles sink into the holder's head by this much per unit of extra scale. */
    private static final double HOLD_OFFSET_DROP_PER_SCALE = 0.2D;

    private static final EntityDataAccessor<Boolean> DATA_UPSIDE_DOWN =
            SynchedEntityData.defineId(MoCTurtleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(MoCTurtleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCTurtleEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    /** Server-side countdown until a flipped turtle rights itself. */
    private int struggleTicks;
    private int pickupCooldown;
    private float lastAppliedScale = -1F;

    /** Cache for {@link #getTmntBrother()}: the name is compared by identity, so it is only parsed when it changes. */
    @Nullable
    private Component lastCheckedName;
    @Nullable
    private TmntBrother cachedBrother;

    public MoCTurtleEntity(EntityType<? extends MoCTurtleEntity> type, Level level) {
        super(type, level);
        // Amphibious: the pathfinder must not treat water as costly terrain.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new TurtleSwimMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.SCALE, WILD_SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ImmobilizedGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerWithinRangeGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new SeekFloorFoodGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_UPSIDE_DOWN, false);
        builder.define(DATA_HIDING, false);
        builder.define(DATA_HELD_BY, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("UpsideDown", this.isUpsideDown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setUpsideDown(tag.getBoolean("UpsideDown"));
    }

    // ---------------------------------------------------------------------
    // Synced state
    // ---------------------------------------------------------------------

    public boolean isUpsideDown() {
        return this.entityData.get(DATA_UPSIDE_DOWN);
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    private void setHiding(boolean hiding) {
        this.entityData.set(DATA_HIDING, hiding);
    }

    /** A hiding or flipped turtle cannot move. */
    private boolean isImmobilized() {
        return this.isHiding() || this.isUpsideDown();
    }

    private void setUpsideDown(boolean upsideDown) {
        this.entityData.set(DATA_UPSIDE_DOWN, upsideDown);
        if (upsideDown) {
            this.setHiding(false);
            this.struggleTicks = MIN_STRUGGLE_TICKS + this.random.nextInt(EXTRA_STRUGGLE_TICKS);
            this.getNavigation().stop();
        }
    }

    /** The Ninja Turtle this turtle is named after, or null. Used by the renderer, model and loot. */
    @Nullable
    public TmntBrother getTmntBrother() {
        Component name = this.getCustomName();
        if (name != this.lastCheckedName) {
            this.lastCheckedName = name;
            this.cachedBrother = name == null ? null : TmntBrother.fromName(name.getString());
        }
        return this.cachedBrother;
    }

    // ---------------------------------------------------------------------
    // Size and growth
    // ---------------------------------------------------------------------

    public float getGrowthScale() {
        return (float) this.getAttributeBaseValue(Attributes.SCALE);
    }

    /** Restores a saved size, e.g. when a turtle is released from a Pet Amulet. */
    public void setGrowthScale(float scale) {
        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(Mth.clamp(scale, WILD_SCALE, MAX_SCALE));
        }
    }

    private void tickGrowth() {
        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute == null) {
            return;
        }
        if (!this.level().isClientSide && this.isTame() && scaleAttribute.getBaseValue() < MAX_SCALE
                && this.random.nextInt(GROWTH_STEP_ODDS) == 0) {
            scaleAttribute.setBaseValue(Math.min(MAX_SCALE, scaleAttribute.getBaseValue() + GROWTH_STEP));
        }
        float current = (float) scaleAttribute.getValue();
        if (Math.abs(this.lastAppliedScale - current) > SCALE_CHANGE_THRESHOLD) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        this.tickGrowth();
        if (this.pickupCooldown > 0) {
            this.pickupCooldown--;
        }
        this.tickHeld();
        if (!this.level().isClientSide) {
            this.tickUpsideDown();
            this.tickHiding();
        }
    }

    private void tickUpsideDown() {
        if (!this.isUpsideDown()) {
            return;
        }
        if (this.isInWater()) {
            this.setUpsideDown(false);
        } else if (--this.struggleTicks <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
            this.setUpsideDown(false);
        }
    }

    private void tickHiding() {
        if (this.isTame() || this.isUpsideDown() || this.isInWater()) {
            this.setHiding(false);
            return;
        }
        // Scanning for nearby entities is the costly part, so it does not run every tick.
        if (this.tickCount % THREAT_SCAN_INTERVAL_TICKS != 0) {
            return;
        }
        boolean threatened = this.hasVisibleThreat();
        if (threatened && !this.isHiding()) {
            this.playSound(ModSounds.TURTLE_ANGRY.get(), 1.0F, 1.0F);
            this.getNavigation().stop();
        }
        this.setHiding(threatened);
    }

    private boolean hasVisibleThreat() {
        return !this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(THREAT_SCAN_RADIUS), this::isThreat).isEmpty();
    }

    private boolean isThreat(LivingEntity other) {
        return other != this
                && other.getType() != this.getType()
                && !other.isSpectator()
                && (other.getBbWidth() >= MIN_THREAT_SIZE || other.getBbHeight() >= MIN_THREAT_SIZE)
                && this.hasLineOfSight(other);
    }

    // ---------------------------------------------------------------------
    // Damage
    // ---------------------------------------------------------------------

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Safe on the owner's head, but /kill and the void still work.
        if (this.isHeld() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        // Environmental damage (fall, fire, drowning...) never flips or hides the turtle.
        if (source.getEntity() == null) {
            return super.hurt(source, amount);
        }
        if (this.isHiding()) {
            // hurt() also runs on the client for the attacking player's own hits, so everything that
            // changes state or makes noise must stay server-side or the client desyncs.
            if (!this.level().isClientSide) {
                // The shell absorbs the hit: no damage, but the clang tells the attacker it was blocked.
                this.playSound(ModSounds.TURTLE_HURT.get(), 1.0F, 1.0F);
                if (this.random.nextInt(HIDING_FLIP_ODDS) == 0) {
                    this.setUpsideDown(true);
                }
            }
            return false;
        }
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && this.random.nextInt(HURT_FLIP_ODDS) == 0) {
            this.setUpsideDown(true);
        }
        return wasHurt;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // Turtles breathe underwater (NeoForge's replacement for canBreatheUnderwater()).
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // ---------------------------------------------------------------------
    // Water: floats at a random depth under the surface, as in the original
    // (amphibian travel + "diving depth"). It does not dive or swim freely.
    // ---------------------------------------------------------------------

    /** Horizontal swimming speed relative to walking speed (original: 0.08 in water vs 0.12 on land). */
    private static final float SWIM_SPEED_MULTIPLIER = 0.67F;
    private static final double SWIM_ACCELERATION = 0.125D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    private static final double WATER_DRAG = 0.9D;
    private static final double WATER_SINK_PER_TICK = 0.005D;
    /** 1 in N chance per tick of picking a new floating depth. */
    private static final int DIVING_DEPTH_REROLL_ODDS = 500;
    /** Caps the rise so a turtle that ends up deep (e.g. spawned underwater) floats up gently instead of shooting out. */
    private static final double MAX_RISE_SPEED = 0.05D;
    /** Upward push when it bumps into a bank while swimming, so it can climb out of the water. */
    private static final double CLIMB_OUT_IMPULSE = 0.1D;
    private static final int MAX_SURFACE_SCAN = 32;

    /** How far below the water surface this turtle floats; negative until first rolled. */
    private double divingDepth = -1.0D;

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isInWater() || this.isHeld()) {
            super.travel(travelVector);
            return;
        }
        this.applyBuoyancy();
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 motion = this.getDeltaMovement().scale(WATER_DRAG);
        this.setDeltaMovement(motion.x, motion.y - WATER_SINK_PER_TICK, motion.z);
    }

    /** Original: min = (size + 8) / 340, max = size / 100, where size is the scale in hundredths. */
    private void rollDivingDepth() {
        double scale = this.getScale();
        double min = (scale * 100.0D + 8.0D) / 340.0D;
        this.divingDepth = min + this.random.nextDouble() * (scale - min);
    }

    /** Pushes the turtle up whenever it is deeper than its floating depth. */
    private void applyBuoyancy() {
        if (this.divingDepth < 0.0D || this.random.nextInt(DIVING_DEPTH_REROLL_ODDS) == 0) {
            this.rollDivingDepth();
        }
        double surfaceY = this.findWaterSurfaceY();
        if (surfaceY == Double.NEGATIVE_INFINITY) {
            return;
        }
        double depth = surfaceY - this.getY();
        if (depth > this.divingDepth) {
            Vec3 motion = this.getDeltaMovement();
            double rise = Math.min(Math.max(motion.y, 0.0D) + 0.001D + depth * 0.01D, MAX_RISE_SPEED);
            this.setDeltaMovement(motion.x, rise, motion.z);
        }
    }

    /** Y of the water surface above the turtle, or {@code Double.NEGATIVE_INFINITY} if it is not in a water column. */
    private double findWaterSurfaceY() {
        BlockPos.MutableBlockPos pos = this.blockPosition().mutable();
        if (!this.isWaterAt(pos)) {
            pos.move(0, -1, 0); // feet can bob just above the surface
            if (!this.isWaterAt(pos)) {
                return Double.NEGATIVE_INFINITY;
            }
        }
        for (int i = 0; i < MAX_SURFACE_SCAN; i++) {
            pos.move(0, 1, 0);
            if (!this.isWaterAt(pos)) {
                pos.move(0, -1, 0);
                break;
            }
        }
        return pos.getY() + this.level().getFluidState(pos).getHeight(this.level(), pos);
    }

    private boolean isWaterAt(BlockPos pos) {
        return this.level().getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public boolean isAffectedByFluids() {
        // Opts out of vanilla's water jump/buoyancy; the buoyancy above replaces it.
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false; // currents do not carry a turtle away
    }

    /** Original: turtles never jump on land. */
    @Override
    public void jumpFromGround() {
    }

    /** Horizontal movement toward the navigation target while in water; depth is handled by applyBuoyancy(). */
    private static final class TurtleSwimMoveControl extends MoveControl {
        private final MoCTurtleEntity turtle;

        TurtleSwimMoveControl(MoCTurtleEntity turtle) {
            super(turtle);
            this.turtle = turtle;
        }

        @Override
        public void tick() {
            if (!this.turtle.isInWater() || this.turtle.isHeld()) {
                super.tick();
                return;
            }
            if (this.operation != MoveControl.Operation.MOVE_TO || this.turtle.getNavigation().isDone()) {
                this.turtle.setSpeed(0.0F);
                return;
            }
            double dx = this.wantedX - this.turtle.getX();
            double dz = this.wantedZ - this.turtle.getZ();
            if (dx * dx + dz * dz < 2.5E-7D) {
                this.turtle.setSpeed(0.0F);
                return;
            }
            float speed = (float) (this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED))
                    * SWIM_SPEED_MULTIPLIER;
            Vec3 wanted = new Vec3(dx, 0.0D, dz).normalize().scale(speed);
            Vec3 motion = this.turtle.getDeltaMovement();
            double y = this.turtle.horizontalCollision ? Math.max(motion.y, CLIMB_OUT_IMPULSE) : motion.y;
            this.turtle.setDeltaMovement(
                    motion.x + (wanted.x - motion.x) * SWIM_ACCELERATION,
                    y,
                    motion.z + (wanted.z - motion.z) * SWIM_ACCELERATION);

            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
            this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), targetYaw, SWIM_MAX_TURN_DEGREES));
            this.turtle.yBodyRot = this.turtle.getYRot();
        }
    }

    // ---------------------------------------------------------------------
    // Interaction
    // ---------------------------------------------------------------------

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
        if (isTurtleFood(stack)) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.tameBy(player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        // Poking a wild turtle with an empty hand flips it over, and again to flip it back.
        if (stack.isEmpty()) {
            if (!this.level().isClientSide) {
                this.setUpsideDown(!this.isUpsideDown());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private InteractionResult interactAsOwner(Player player, InteractionHand hand, ItemStack stack) {
        if (stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                this.captureIntoAmulet(player, hand);
            }
            return InteractionResult.SUCCESS;
        }
        if (isTurtleFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(this.getMaxHealth());
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (this.isUpsideDown()) {
            if (!this.level().isClientSide) {
                this.setUpsideDown(false);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.isEmpty() && this.pickupCooldown <= 0 && !this.isHeld()
                && !PetCarryUtil.isAlreadyCarryingAPet(player)) {
            if (!this.level().isClientSide) {
                this.startHolding(player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /** Melon slices and sugar cane: what the turtle eats, tames with and is healed by. */
    private static boolean isTurtleFood(ItemStack stack) {
        return stack.is(Items.MELON_SLICE) || stack.is(Items.SUGAR_CANE);
    }

    private void tameBy(Player player) {
        this.tame(player);
        NamingHelper.promptRename(this, player.getUUID());
    }

    /** Stores this turtle (including its size) in a filled Pet Amulet and removes it from the world. */
    private void captureIntoAmulet(Player player, InteractionHand hand) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(AMULET_KEY, true);
        tag.putFloat("Health", this.getHealth());
        tag.putFloat("Scale", this.getGrowthScale());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", player.getUUID());

        ItemStack filled = new ItemStack(ModItems.PET_AMULET_FULL.get());
        filled.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    // ---------------------------------------------------------------------
    // Being carried on the owner's head
    // ---------------------------------------------------------------------

    @Override
    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    @Nullable
    @Override
    public Player getHolder() {
        return this.entityData.get(DATA_HELD_BY).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    private void startHolding(Player player) {
        this.entityData.set(DATA_HELD_BY, Optional.of(player.getUUID()));
        this.setUpsideDown(false);
        this.setHiding(false);
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
        this.getNavigation().stop();
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.pickupCooldown = PICKUP_COOLDOWN_TICKS;
    }

    /** Repositions the real entity above the holder's head every tick (same approach as the bunny and kitty). */
    private void tickHeld() {
        if (!this.isHeld()) {
            return;
        }
        Player holder = this.getHolder();
        if (holder == null || holder.isRemoved()) {
            if (!this.level().isClientSide) {
                this.stopHolding();
            }
            return;
        }
        if (!this.level().isClientSide && holder.isShiftKeyDown()) {
            this.stopHolding();
            return;
        }

        double heightAboveEyes = HOLD_OFFSET_ABOVE_EYES - (this.getScale() - WILD_SCALE) * HOLD_OFFSET_DROP_PER_SCALE;
        Vec3 target = holder.getEyePosition().add(0.0D, heightAboveEyes, 0.0D);
        this.moveTo(target.x, target.y, target.z, holder.getYRot(), 0.0F);
        this.xo = target.x;
        this.yo = target.y;
        this.zo = target.z;
        this.yRotO = holder.getYRot();
        this.setYHeadRot(holder.getYRot());
        this.yHeadRotO = holder.getYRot();
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean isPushable() {
        return !this.isHeld() && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isHeld() && super.canBeCollidedWith();
    }

    @Override
    public void pushEntities() {
        if (this.isHeld()) {
            return;
        }
        super.pushEntities();
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply to turtles
    // ---------------------------------------------------------------------

    /** Turtles have no baby stage, so vanilla's 5% baby roll at spawn must not make one. */
    @Override
    public void setAge(int age) {
        super.setAge(0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming and healing are handled in mobInteract()
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // turtles do not breed
    }

    /**
     * Mob's default rejects any position with liquid in the hitbox, which would stop natural spawns in
     * water. WaterAnimal overrides it the same way.
     */
    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    // ---------------------------------------------------------------------
    // Sounds, XP and loot
    // ---------------------------------------------------------------------



    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TURTLE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TURTLE_DEATH.get();
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    /** Drops raw turtle (cooked if it dies burning), plus the weapon when it is a Ninja Turtle. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            lootingLevel = EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        // Original loot table: 1 piece, plus 0-1 more per level of looting.
        int meatCount = 1;
        for (int i = 0; i < lootingLevel; i++) {
            meatCount += this.random.nextInt(2);
        }
        Item meat = this.isOnFire() ? ModItems.TURTLE_COOKED.get() : ModItems.TURTLE_RAW.get();
        this.spawnAtLocation(new ItemStack(meat, meatCount));

        TmntBrother brother = this.getTmntBrother();
        if (brother != null) {
            this.spawnAtLocation(new ItemStack(brother.getWeapon()));
        }
    }

    // ---------------------------------------------------------------------
    // Goals
    // ---------------------------------------------------------------------

    /** Holds the turtle still while it hides or is flipped over, keeping lower-priority goals from moving it. */
    private static final class ImmobilizedGoal extends Goal {
        private final MoCTurtleEntity turtle;

        ImmobilizedGoal(MoCTurtleEntity turtle) {
            this.turtle = turtle;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.turtle.isImmobilized();
        }

        @Override
        public boolean canContinueToUse() {
            return this.turtle.isImmobilized();
        }

        @Override
        public void start() {
            this.turtle.getNavigation().stop();
        }
    }

    /**
     * Same range as the original goal: the turtle only follows while its owner is between 2 and 10
     * blocks away. Vanilla's FollowOwnerGoal is not used because it rejects amphibious navigation.
     */
    private static final class FollowOwnerWithinRangeGoal extends Goal {
        private static final double MIN_DISTANCE_SQR = 2.0D * 2.0D;
        private static final double MAX_DISTANCE_SQR = 10.0D * 10.0D;
        private static final int REPATH_INTERVAL_TICKS = 10;

        private final MoCTurtleEntity turtle;
        private final double speedModifier;
        @Nullable
        private LivingEntity owner;
        private int repathDelay;

        FollowOwnerWithinRangeGoal(MoCTurtleEntity turtle, double speedModifier) {
            this.turtle = turtle;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity candidate = this.turtle.getOwner();
            if (candidate == null || candidate.isSpectator() || !this.isInRange(candidate)) {
                return false;
            }
            this.owner = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.owner != null && this.owner.isAlive() && this.isInRange(this.owner);
        }

        @Override
        public void start() {
            this.repathDelay = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.turtle.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner == null) {
                return;
            }
            this.turtle.getLookControl().setLookAt(this.owner, 10.0F, this.turtle.getMaxHeadXRot());
            if (--this.repathDelay <= 0) {
                this.repathDelay = REPATH_INTERVAL_TICKS;
                this.turtle.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }

        private boolean isInRange(LivingEntity target) {
            double distanceSqr = this.turtle.distanceToSqr(target);
            return distanceSqr >= MIN_DISTANCE_SQR && distanceSqr <= MAX_DISTANCE_SQR;
        }
    }

    /**
     * Walks to melon slices or sugar cane lying on the ground and eats one. If a player is near,
     * that player becomes the owner and is asked to name the turtle (original taming method).
     */
    private static final class SeekFloorFoodGoal extends Goal {
        private static final int START_ODDS = 50;
        private static final double EAT_DISTANCE_SQR = 2.0D * 2.0D;
        private static final int REPATH_INTERVAL_TICKS = 10;
        /** Gives up on food it cannot reach instead of re-pathing forever. */
        private static final int MAX_SEEK_TICKS = 200;

        private final MoCTurtleEntity turtle;
        private final double speedModifier;
        @Nullable
        private ItemEntity food;
        private int repathDelay;
        private int seekTicksLeft;

        SeekFloorFoodGoal(MoCTurtleEntity turtle, double speedModifier) {
            this.turtle = turtle;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.turtle.isTame() || this.turtle.getRandom().nextInt(START_ODDS) != 0) {
                return false;
            }
            this.food = this.findClosestFood();
            return this.food != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.food != null && this.food.isAlive() && !this.turtle.isTame() && this.seekTicksLeft > 0;
        }

        @Override
        public void start() {
            this.repathDelay = 0;
            this.seekTicksLeft = MAX_SEEK_TICKS;
        }

        @Override
        public void stop() {
            this.food = null;
        }

        @Override
        public void tick() {
            if (this.food == null) {
                return;
            }
            this.seekTicksLeft--;
            if (this.turtle.distanceToSqr(this.food) < EAT_DISTANCE_SQR) {
                this.eat(this.food);
                this.food = null;
            } else if (--this.repathDelay <= 0) {
                this.repathDelay = REPATH_INTERVAL_TICKS;
                this.turtle.getNavigation().moveTo(this.food, this.speedModifier);
            }
        }

        @Nullable
        private ItemEntity findClosestFood() {
            return this.turtle.level().getEntitiesOfClass(ItemEntity.class,
                            this.turtle.getBoundingBox().inflate(FOOD_SEARCH_RADIUS),
                            item -> isTurtleFood(item.getItem()))
                    .stream()
                    .min(Comparator.comparingDouble(this.turtle::distanceToSqr))
                    .orElse(null);
        }

        private void eat(ItemEntity foodEntity) {
            // A copy is stored back so the change is synced to clients.
            ItemStack remaining = foodEntity.getItem().copy();
            remaining.shrink(1);
            if (remaining.isEmpty()) {
                foodEntity.discard();
            } else {
                foodEntity.setItem(remaining);
            }
            this.turtle.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
            Player nearestPlayer = this.turtle.level().getNearestPlayer(this.turtle, PLAYER_TAME_RADIUS);
            if (nearestPlayer != null) {
                this.turtle.tameBy(nearestPlayer);
            }
        }
    }

    /**
     * Easter egg from the original mod: a turtle named after one of the four Ninja Turtles gets
     * its own texture, hides its top shell cube and drops that turtle's weapon on death.
     * The original matched only some spellings for the texture but more for the drop; here every
     * alias behaves the same.
     */
    public enum TmntBrother {
        DONATELLO("turtle_donatello", ModItems.BO, "donatello"),
        LEONARDO("turtle_leonardo", ModItems.KATANA, "leonardo"),
        RAPHAEL("turtle_raphael", ModItems.SAI, "raphael", "rafael"),
        MICHELANGELO("turtle_michelangelo", ModItems.NUNCHAKU, "michelangelo", "michaelangelo");

        private final String textureName;
        private final Supplier<? extends Item> weapon;
        private final String[] aliases;

        TmntBrother(String textureName, Supplier<? extends Item> weapon, String... aliases) {
            this.textureName = textureName;
            this.weapon = weapon;
            this.aliases = aliases;
        }

        /** File name (without extension) of this brother's texture. */
        public String getTextureName() {
            return this.textureName;
        }

        public Item getWeapon() {
            return this.weapon.get();
        }

        /** Case-insensitive lookup by pet name; null when the name is not a brother. */
        @Nullable
        public static TmntBrother fromName(@Nullable String name) {
            if (name == null) {
                return null;
            }
            String normalized = name.trim().toLowerCase(Locale.ROOT);
            for (TmntBrother brother : values()) {
                for (String alias : brother.aliases) {
                    if (alias.equals(normalized)) {
                        return brother;
                    }
                }
            }
            return null;
        }
    }
}