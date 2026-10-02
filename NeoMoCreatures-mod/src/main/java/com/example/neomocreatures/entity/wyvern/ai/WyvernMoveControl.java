package com.example.neomocreatures.entity.wyvern.ai;

import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Ghast-style free flight: every few ticks a small push towards its destination, with air drag doing
 * the rest, so it glides in smooth curves and turns to face where it is flying. Checks the way is
 * clear first and gives up on blocked destinations. On the ground (or ridden) it moves like any mob.
 */
public final class WyvernMoveControl extends MoveControl {

    /** Ghast: a push of 0.1 towards its destination every 2-6 ticks, with 0.91 air drag — smooth, floaty flight. */
    private static final double FLIGHT_IMPULSE = 0.1D;
    private static final int MIN_IMPULSE_INTERVAL = 2;
    private static final int IMPULSE_INTERVAL_RANGE = 5;
    /** How close (squared) it has to get to a destination before picking a new one. */
    public static final double ARRIVED_DISTANCE_SQR = 1.0D;
    private static final float TURN_SPEED = 10.0F;

    private final MoCWyvernEntity wyvern;
    private int impulseCooldown;

    public WyvernMoveControl(MoCWyvernEntity wyvern) {
        super(wyvern);
        this.wyvern = wyvern;
    }

    @Override
    public void tick() {
        if (!this.wyvern.getIsFlying() || this.wyvern.isVehicle()) {
            super.tick();
            return;
        }
        if (this.operation == Operation.MOVE_TO && --this.impulseCooldown <= 0) {
            this.impulseCooldown = MIN_IMPULSE_INTERVAL + this.wyvern.getRandom().nextInt(IMPULSE_INTERVAL_RANGE);
            Vec3 toTarget = new Vec3(this.wantedX - this.wyvern.getX(), this.wantedY - this.wyvern.getY(),
                    this.wantedZ - this.wyvern.getZ());
            double distance = toTarget.length();
            Vec3 direction = toTarget.normalize();
            if (distance * distance < ARRIVED_DISTANCE_SQR || !this.canReach(direction, Mth.ceil(distance))) {
                this.operation = Operation.WAIT;
            } else {
                this.wyvern.setDeltaMovement(this.wyvern.getDeltaMovement().add(direction.scale(FLIGHT_IMPULSE * this.speedModifier)));
            }
        }
        this.faceFlightDirection();
    }

    private boolean canReach(Vec3 direction, int steps) {
        AABB box = this.wyvern.getBoundingBox();
        for (int i = 1; i < steps; i++) {
            box = box.move(direction);
            if (!this.wyvern.level().noCollision(this.wyvern, box)) {
                return false;
            }
        }
        return true;
    }

    private void faceFlightDirection() {
        Vec3 motion = this.wyvern.getDeltaMovement();
        if (motion.horizontalDistanceSqr() > 1.0E-4D) {
            float yaw = (float) (Mth.atan2(motion.z, motion.x) * Mth.RAD_TO_DEG) - 90.0F;
            this.wyvern.setYRot(this.rotlerp(this.wyvern.getYRot(), yaw, TURN_SPEED));
            this.wyvern.yBodyRot = this.wyvern.getYRot();
        }
    }
}