package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.hunter.MoCEntityCrocodile}. Extends
 * {@link TamableAnimal} for consistency with the rest of the project's class hierarchy, but has no
 * taming interaction, no fish net, no egg, no owner-only items: it is a plain hostile mob that
 * happens to share a superclass with the tameable ones. Confirmed against the official entity list
 * on the wiki: the crocodile is Hostile, not Tameable, even though its own original Java class
 * extends the same tameable base the real pets use.
 * <p>
 * For this entity the original mod's source takes priority over the wiki's prose wherever they
 * disagree (agreed with the user):
 * <ul>
 *   <li>{@code FOLLOW_RANGE} is 24, not the wiki's "12 block radius".</li>
 *   <li>It never attacks other animals/horses: that target goal is commented out in the source.</li>
 *   <li>It does not attack on sight constantly — it only actively hunts a player during short
 *   ~1-2.5s bursts triggered by a 1 in 500 chance per tick (see {@link #tickHunting()}); outside a
 *   burst it only fights back if hit (see the target selector).</li>
 *   <li>The source also registers a flee-from-player goal, but the crocodile's own
 *   {@code isNotScared()} always returns true, which makes that goal's {@code shouldExecute()} bail
 *   out immediately — real dead code in the original, so it is not ported here.</li>
 *   <li>The "death roll" grab-and-drag mechanic was already disabled by its own author and is not
 *   ported.</li>
 * </ul>
 */
public class MoCCrocodileEntity extends TamableAnimal {

    private static final double MAX_HEALTH = 25.0D;
    private static final double ARMOR = 6.0D;
    private static final double ATTACK_DAMAGE = 5.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double FOLLOW_RANGE = 24.0D;

    // ---- Goals ----
    private static final double ATTACK_SPEED = 1.0D;
    private static final double WANDER_SPEED = 0.9D;
    private static final int WANDER_INTERVAL = 120;
    /** Original: a 1 in 500 chance per tick to start or stop resting. */
    private static final int REST_TOGGLE_CHANCE = 500;
    private static final int REST_PRIORITY = 5;
    private static final int WANDER_PRIORITY = 6;

    // ---- Bite animation ----
    /** Original: the jaw opens over 6 ticks (0.1 per tick); past 0.6 it snaps shut and resets. */
    private static final float BITE_STEP = 0.1F;
    private static final float BITE_MAX = 0.6F;


    // ---- Death roll ----
    /** Original: a 1 in 3 chance per hit to grab instead of just biting. */
    private static final int GRAB_CHANCE = 3;
    /** Original: while carrying prey, hold the jaw at a fixed "clamped shut" pose. */
    private static final float CLAMPED_BITE_PROGRESS = 0.4F;
    /** Original: the roll sound plays every 20 spin-ticks, and every 80 the passenger takes damage. */
    private static final int SPIN_INCREMENT = 3;
    private static final int ROLL_SOUND_INTERVAL = 20;
    private static final int SPIN_DAMAGE_INTERVAL = 80;
    private static final float SPIN_DAMAGE = 4.0F;
    /** Original: while dragging prey overland to water, a 1 in 50 chance per tick of extra damage. */
    private static final int DRAG_DAMAGE_CHANCE = 50;
    private static final float DRAG_DAMAGE = 2.0F;
    private static final int WATER_SEEK_RADIUS = 20;
    private static final int WATER_SEEK_COOLDOWN_TICKS = 20;
     /** Original used getAge()*0.01F here (0.8 at its fixed age 80); we don't implement ageing, so
     *  this is that same effective reach as a constant instead. */
    private static final double MOUTH_REACH = 0.8D;
    /** Degrees the barrel-roll angle advances per tick while spinning. */
    private static final float ROLL_DEGREES_PER_TICK = 40.0F;


    private static final EntityDataAccessor<Float> DATA_BITE_PROGRESS =
            SynchedEntityData.defineId(MoCCrocodileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CAUGHT_PREY =
            SynchedEntityData.defineId(MoCCrocodileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_ROLL_ANGLE =
            SynchedEntityData.defineId(MoCCrocodileEntity.class, EntityDataSerializers.FLOAT);

    /** Synced so clients can show the resting pose (it used to be a server-only field). */
    private static final EntityDataAccessor<Boolean> DATA_RESTING =
            SynchedEntityData.defineId(MoCCrocodileEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean biting;
    /** Dragging a caught victim overland toward the nearest water. */
    private boolean waterbound;
    private int spinTicks;
    private int waterSeekCooldown;

    public MoCCrocodileEntity(EntityType<? extends MoCCrocodileEntity> type, Level level) {
        super(type, level);
        // Lets it cross water instead of pathing around it, matching the wiki's "semi-aquatic".
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        // Default MoveControl only corrects height by jumping, which is exactly why it was bobbing
        // at the surface / sinking instead of swimming to a submerged target.
        this.moveControl = new CrocodileSwimMoveControl(this);
    }

    /** Amphibious, like vanilla's turtle: paths across dry land and through open water alike. */
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BITE_PROGRESS, 0.0F);
        builder.define(DATA_HAS_CAUGHT_PREY, false);
        builder.define(DATA_ROLL_ANGLE, 0.0F);
        builder.define(DATA_RESTING, false);
    }

    /** Degrees, wrapped 0-360: rotates the model around its own body axis in the renderer — a real
     *  barrel roll, not a spin around the vertical axis. */
    public float getRollAngle() {
        return this.entityData.get(DATA_ROLL_ANGLE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, ATTACK_SPEED, true));
        this.goalSelector.addGoal(REST_PRIORITY, new RestGoal());
        this.goalSelector.addGoal(WANDER_PRIORITY, new RandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        // Original: EntityAIFleeFromPlayer is registered too, but the crocodile's own isNotScared()
        // always returns true, and that goal's shouldExecute() bails out immediately whenever
        // isNotScared() is true — it is dead code in the original, and stays out here.

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Deviates from the original's short random hunting-burst gate (see the class comment's
        // history): in practice that made the crocodile stand next to the player doing nothing most
        // of the time, which reads as neutral rather than hostile. Attacks on sight instead, matching
        // the wiki and normal hostile-mob expectations.
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isResting() {
        return this.entityData.get(DATA_RESTING);
    }

    private void setResting(boolean resting) {
        this.entityData.set(DATA_RESTING, resting);
    }


    public float getBiteProgress() {
        return this.entityData.get(DATA_BITE_PROGRESS);
    }

    /**
     * Original: every hit either bites normally, or — 1 in 3 of the time, only while not already
     * carrying someone and the target isn't already riding something else — grabs the target
     * instead, dealing no direct damage that hit.
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.hasCaughtPrey() && target.getVehicle() == null && this.random.nextInt(GRAB_CHANCE) == 0) {
            target.startRiding(this, true);
            this.setHasCaughtPrey(true);
            return false;
        }
        this.biting = true;
        this.playSound(ModSounds.CROCODILE_JAW_SNAP.get(), this.getSoundVolume(), this.getVoicePitch());
        return super.doHurtTarget(target);
    }

    public boolean hasCaughtPrey() {
        return this.entityData.get(DATA_HAS_CAUGHT_PREY);
    }

    private void setHasCaughtPrey(boolean value) {
        this.entityData.set(DATA_HAS_CAUGHT_PREY, value);
    }

    /** Original: getHasCaughtPrey() && isBeingRidden() && isSwimming() — using our isSwimmingDeep(). */
    public boolean isSpinning() {
        return this.hasCaughtPrey() && this.isVehicle() && this.isSwimmingDeep();
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickBite();
            this.tickDeathRoll();
        }
    }

    private void tickBite() {
        if (!this.biting) {
            return;
        }
        float progress = this.getBiteProgress() + BITE_STEP;
        if (progress > BITE_MAX) {
            this.biting = false;
            progress = 0.0F;
        }
        this.entityData.set(DATA_BITE_PROGRESS, progress);
    }

    private void tickDeathRoll() {
        if (!this.hasCaughtPrey()) {
            return;
        }
        Entity prey = this.getFirstPassenger();
        if (prey == null) {
            // Original bug: it checked getRidingEntity() (what the crocodile itself rides — always
            // null) instead of its passenger, so this release path could never actually run.
            this.setHasCaughtPrey(false);
            this.biting = false;
            this.entityData.set(DATA_BITE_PROGRESS, 0.0F);
            this.waterbound = false;
            return;
        }

        this.setTarget(null);
        this.entityData.set(DATA_BITE_PROGRESS, CLAMPED_BITE_PROGRESS);
        this.setResting(false);

        if (!this.isSwimmingDeep()) {
            this.waterbound = true;
            if (this.random.nextInt(DRAG_DAMAGE_CHANCE) == 0) {
                prey.hurt(this.damageSources().mobAttack(this), DRAG_DAMAGE);
            }
            this.tickSeekWater();
        } else {
            this.waterbound = false;
        }

        if (this.isSpinning()) {
            this.spinTicks += SPIN_INCREMENT;
            this.entityData.set(DATA_ROLL_ANGLE, (this.getRollAngle() + ROLL_DEGREES_PER_TICK) % 360.0F);
            if (this.spinTicks % ROLL_SOUND_INTERVAL == 0) {
                this.playSound(ModSounds.CROCODILE_ROLL.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            if (this.spinTicks > SPIN_DAMAGE_INTERVAL) {
                this.spinTicks = 0;
                prey.hurt(this.damageSources().mobAttack(this), SPIN_DAMAGE);
            }
        } else if (this.getRollAngle() != 0.0F) {
            this.entityData.set(DATA_ROLL_ANGLE, 0.0F);
        }
    }

    /** Original: "TODO replace with move to water AI" — a plain nearest-water search and path to it. */
    private void tickSeekWater() {
        if (this.waterSeekCooldown-- > 0) {
            return;
        }
        this.waterSeekCooldown = WATER_SEEK_COOLDOWN_TICKS;
        BlockPos origin = this.blockPosition();
        BlockPos best = null;
        double bestDistSqr = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(
                origin.offset(-WATER_SEEK_RADIUS, -4, -WATER_SEEK_RADIUS),
                origin.offset(WATER_SEEK_RADIUS, 4, WATER_SEEK_RADIUS))) {
            if (this.level().getFluidState(pos).is(FluidTags.WATER)) {
                double distSqr = pos.distSqr(origin);
                if (distSqr < bestDistSqr) {
                    bestDistSqr = distSqr;
                    best = pos.immutable();
                }
            }
        }
        if (best != null) {
            this.getNavigation().moveTo(best.getX() + 0.5D, best.getY(), best.getZ() + 0.5D, WANDER_SPEED);
        }
    }

    /** Original: updatePassenger() — carries the victim slightly behind its jaws, twisted during a spin. */
    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        double distance = MOUTH_REACH + passenger.getBbWidth() - 0.4D;
        float yaw = (this.getYRot() - 90.0F) * Mth.DEG_TO_RAD;
        double x = this.getX() - distance * Math.cos(yaw);
        double z = this.getZ() - distance * Math.sin(yaw);
        double y = this.getY() + this.getBbHeight() * 0.1D;
        moveFunction.accept(passenger, x, y, z);

    }


    /**
     * Original: unMount() called this.dismount() (makes the crocodile itself dismount whatever
     * it's riding — always a no-op, since it never rides anything) instead of releasing its own
     * passenger. Ejecting the passenger directly here is the actual fix.
     */
    private void releasePrey() {
        this.ejectPassengers();
        this.setHasCaughtPrey(false);
        this.waterbound = false;
        this.spinTicks = 0;
    }

    @Override
    public void die(DamageSource damageSource) {
        this.releasePrey();
        super.die(damageSource);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.isVehicle()) {
            this.setHasCaughtPrey(false);
            this.waterbound = false;
            this.spinTicks = 0;
            this.entityData.set(DATA_ROLL_ANGLE, 0.0F);
        }
    }

    // ---------------------------------------------------------------------
    // Water
    // ---------------------------------------------------------------------

    /** Wiki: survives underwater and doesn't drown, like turtles and crabs. */
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // ---- Swimming ----
    /** Same 0.98 drag the Komodo Dragon uses. */
    private static final double WATER_DRAG = 0.98D;

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isSwimmingDeep()) {
            // CrocodileSwimMoveControl already computes the desired deltaMovement each tick —
            // just apply it and let drag settle it.
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(WATER_DRAG));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    /** True only when genuinely submerged (eyes underwater) — not just standing in ankle-deep water
     *  at the shore, which is where isInWater() gets noisy. */
    public boolean isSwimmingDeep() {
        return this.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean isAffectedByFluids() {
        // Opts out of vanilla's water buoyancy/"gasping for air" push entirely — that push runs in
        // LivingEntity.aiStep() regardless of our travel() override, and it's what was winning
        // against our own swim movement, making the crocodile just sink.
        return false;
    }

    /** Moves smoothly toward the nav target on all three axes instead of jumping to correct height. */
    private static final class CrocodileSwimMoveControl extends MoveControl {
        private final MoCCrocodileEntity crocodile;

        CrocodileSwimMoveControl(MoCCrocodileEntity crocodile) {
            super(crocodile);
            this.crocodile = crocodile;
        }

        @Override
        public void tick() {
            if (!this.crocodile.isSwimmingDeep()) {
                super.tick();
                return;
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || this.crocodile.getNavigation().isDone()) {
                // Idle in water: damp existing motion toward zero instead of pushing up or down, so
                // it neither rockets to the surface nor sinks like a stone while it has nowhere to go.
                this.crocodile.setDeltaMovement(this.crocodile.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
                this.crocodile.setSpeed(0.0F);
                return;
            }

            double dx = this.wantedX - this.crocodile.getX();
            double dy = this.wantedY - this.crocodile.getY();
            double dz = this.wantedZ - this.crocodile.getZ();
            double distSqr = dx * dx + dy * dy + dz * dz;
            if (distSqr < 2.5E-7D) {
                this.crocodile.setSpeed(0.0F);
                return;
            }

            float speed = (float) (this.speedModifier * this.crocodile.getAttributeValue(Attributes.MOVEMENT_SPEED));
            Vec3 desired = new Vec3(dx, dy, dz).normalize().scale(speed);
            this.crocodile.setDeltaMovement(this.crocodile.getDeltaMovement().lerp(desired, 0.125D));

            float yRotTarget = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.crocodile.setYRot(this.rotlerp(this.crocodile.getYRot(), yRotTarget, 90.0F));
            this.crocodile.yBodyRot = this.crocodile.getYRot();
        }
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // crocodiles do not breed
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CROCODILE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CROCODILE_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isResting() ? ModSounds.CROCODILE_RESTING.get() : ModSounds.CROCODILE_AMBIENT.get();
    }

    /** Original: getTalkInterval() = 400, well above vanilla's own default. */
    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Wiki: 0-2 reptile hide, scaling with Looting, plus 1-3 experience. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, ModItems.REPTILE_HIDE.get(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }


    /** Original: a fixed experienceValue = 5, not a random range. */
    @Override
    protected int getBaseExperienceReward() {
        return 5;
    }

    // ---------------------------------------------------------------------
    // Resting
    // ---------------------------------------------------------------------

    /**
     * Wiki: "often remain still, with their jaws agape... don't be fooled, they are ready to
     * attack if you get too close." A resting crocodile does not wander, but immediately stops
     * resting the moment it gets a target.
     */
    private final class RestGoal extends Goal {

        RestGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return MoCCrocodileEntity.this.getTarget() == null
                    && MoCCrocodileEntity.this.random.nextInt(REST_TOGGLE_CHANCE) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return MoCCrocodileEntity.this.getTarget() == null
                    && MoCCrocodileEntity.this.random.nextInt(REST_TOGGLE_CHANCE) != 0;
        }

        @Override
        public void start() {
            MoCCrocodileEntity.this.setResting(true);
            MoCCrocodileEntity.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            MoCCrocodileEntity.this.setResting(false);
        }
    }

}