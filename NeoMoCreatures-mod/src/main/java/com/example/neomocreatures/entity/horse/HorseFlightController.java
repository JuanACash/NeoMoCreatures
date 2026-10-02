package com.example.neomocreatures.entity.horse;

import javax.annotation.Nullable;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.client.ClientRiderInput;
import com.example.neomocreatures.entity.MoCHorseEntity;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

/**
 * Movement of a horse: rider-controlled flight of the winged species, gliding/falling while
 * airborne, buoyancy in water or lava, and the slow fall of unicorns and ghosts.
 */
public final class HorseFlightController {

    private static final double FLYER_THRUST = 0.3D;          // vertical thrust per tick while ascending/descending
    private static final float FLYER_FRICTION = 0.91F;        // horizontal friction per tick
    private static final double FLYER_FALL_SPEED = 0.6D;      // vertical damping (acts as "terminal velocity")
    private static final double FLYER_GRAVITY_PULL = 0.055D;  // constant downward pull each tick
    private static final double PEGASUS_THRUST_BONUS = 0.05D; // extra thrust when ascending/descending
    private static final float PEGASUS_FRICTION = 0.93F;      // less friction = more horizontal speed
    private static final double DARK_PEGASUS_THRUST_BONUS = 0.025D;
    private static final float DARK_PEGASUS_FRICTION = 0.92F;

    /** Extra downward push per tick while a sneaking rider floats in a fluid. */
    private static final double SNEAK_SINK_SPEED = 0.08D;
    /** Unicorns and ghosts fall at 60% speed once falling faster than this. */
    private static final double SLOW_FALL_THRESHOLD = -0.1D;
    private static final double SLOW_FALL_FACTOR = 0.6D;

    private final MoCHorseEntity horse;
    /** Ascend/descend key state sent by the rider's client (server side). */
    private boolean ascendHeld = false;
    private boolean descendHeld = false;

    public HorseFlightController(MoCHorseEntity horse) {
        this.horse = horse;
    }

    public void setAscendHeld(boolean held) {
        this.ascendHeld = held;
    }

    public void setDescendHeld(boolean held) {
        this.descendHeld = held;
    }

    /**
     * Applies the horse's own movement for this tick.
     *
     * @return the vector vanilla travel() should still use, or null when the movement
     *         was fully handled here (flight)
     */
    @Nullable
    public Vec3 travel(Vec3 travelVector) {
        Species species = this.horse.getSpecies();
        boolean isBatFlyer = (species == Species.BATHORSE || species == Species.PEGASUS
                || species == Species.DARK_PEGASUS || species == Species.FAIRY_HORSE || species == Species.GHOST_WINGED)
                && this.horse.isTamed() && !this.horse.isTransforming();
        boolean canControlFlight = isBatFlyer && this.horse.isVehicle();

        boolean flyingMount = this.horse.isFlyingNow();

        boolean ascend = this.ascendHeld;
        boolean descend = this.descendHeld;
        if (this.horse.level().isClientSide
                && ClientRiderInput.isLocalPlayer(this.horse.getControllingPassenger())) {
            ascend = ClientRiderInput.isAscendDown();
            descend = ClientRiderInput.isDescendDown();
        }

        if (!this.horse.isVehicle() && this.horse.getGrazeTicks() > 0) {
            this.horse.getNavigation().stop();
            return Vec3.ZERO;
        }

        if (canControlFlight) {
            double thrust = FLYER_THRUST + this.thrustBonus(species);
            if (ascend) {
                this.horse.setDeltaMovement(this.horse.getDeltaMovement().add(0.0D, thrust, 0.0D));
            } else if (descend) {
                this.horse.setDeltaMovement(this.horse.getDeltaMovement().add(0.0D, -thrust, 0.0D));
            }
        }
        this.horse.setNoGravity(flyingMount);

        if (flyingMount) {
            this.applyFlightMovement(travelVector);
            return null;
        }

        this.applyGroundedFluidBuoyancy();

        if ((species == Species.UNICORN || species == Species.GHOST)
                && this.horse.getDeltaMovement().y < SLOW_FALL_THRESHOLD && !this.horse.onGround()) {
            this.horse.setDeltaMovement(this.horse.getDeltaMovement().multiply(1.0D, SLOW_FALL_FACTOR, 1.0D));
        }
        return travelVector;
    }

    private double thrustBonus(Species species) {
        return switch (species) {
            case PEGASUS -> this.horse.isUndead() ? 0.0D : PEGASUS_THRUST_BONUS;
            case DARK_PEGASUS -> DARK_PEGASUS_THRUST_BONUS;
            case FAIRY_HORSE -> PEGASUS_THRUST_BONUS;
            default -> 0.0D;
        };
    }

    private float friction(Species species) {
        return switch (species) {
            case PEGASUS -> this.horse.isUndead() ? FLYER_FRICTION : PEGASUS_FRICTION;
            case DARK_PEGASUS -> DARK_PEGASUS_FRICTION;
            case FAIRY_HORSE -> PEGASUS_FRICTION;
            default -> FLYER_FRICTION;
        };
    }

    /**
     * Movement while actually flying/gliding (in the air, mounted or
     * not): friction depending on species, and two falling modes —
     * floating in water/lava with no gravity pull, or falling with
     * normal flyer gravity.
     */
    private void applyFlightMovement(Vec3 travelVector) {
        float friction = this.friction(this.horse.getSpecies());

        boolean floatingInWater = this.horse.isInWater() && !this.horse.isSkeletonStage();
        boolean floatingInLava = this.horse.isInLava() && !this.horse.isSkeletonStage();

        this.horse.move(MoverType.SELF, this.horse.getDeltaMovement());
        this.horse.moveRelative(friction / 10F, travelVector);

        if (floatingInWater || floatingInLava) {
            this.horse.setDeltaMovement(this.horse.getDeltaMovement().multiply(friction, FLYER_FALL_SPEED, friction));
            double fluidHeight = floatingInLava
                    ? this.horse.getFluidHeight(FluidTags.LAVA)
                    : this.horse.getFluidHeight(FluidTags.WATER);
            this.floatOnSurface(fluidHeight);
        } else {
            this.horse.setDeltaMovement(this.horse.getDeltaMovement()
                    .multiply(friction, FLYER_FALL_SPEED, friction)
                    .subtract(0.0D, FLYER_GRAVITY_PULL, 0.0D));
        }
    }

    /**
     * Floating while standing in water (any species) or lava (nightmare
     * only) while mounted and not a skeleton: prevents it from sinking
     * suddenly and lets a sneaking rider push it down on purpose.
     */
    private void applyGroundedFluidBuoyancy() {
        if (this.horse.isInWater() && this.horse.isVehicle() && !this.horse.isSkeletonStage()) {
            this.floatOnSurface(this.horse.getFluidHeight(FluidTags.WATER));
        }
        if (this.horse.isInLava() && this.horse.isVehicle() && !this.horse.isSkeletonStage()) {
            this.floatOnSurface(this.horse.getFluidHeight(FluidTags.LAVA));
        }
    }

    /** Stops sinking once half submerged; a sneaking rider still pushes the horse down. */
    private void floatOnSurface(double submergedHeight) {
        if (this.horse.getDeltaMovement().y < 0 && !this.horse.onGround() && submergedHeight >= 0.5) {
            this.horse.setDeltaMovement(this.horse.getDeltaMovement().multiply(1, 0.0, 1));
        }
        if (this.horse.getControllingPassenger() instanceof LivingEntity controllingRider
                && controllingRider.isShiftKeyDown()) {
            this.horse.setDeltaMovement(this.horse.getDeltaMovement().add(0, -SNEAK_SINK_SPEED, 0));
        }
    }
}