package com.example.neomocreatures.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * Move control of the swimming mobs, the same as vanilla fish: it swims toward the path target,
 * turning at a limited rate and steering vertically a little at a time. Out of water, or while a
 * player steers the mob, it does nothing.
 */
public class AquaticMoveControl extends MoveControl {

    private static final float SPEED_SMOOTHING = 0.125F;

    private final float maxTurnDegrees;
    /** Share of the forward speed applied as vertical steering toward the path (vanilla fish use 0.1). */
    private final double verticalSteering;

    public AquaticMoveControl(Mob mob, float maxTurnDegrees, double verticalSteering) {
        super(mob);
        this.maxTurnDegrees = maxTurnDegrees;
        this.verticalSteering = verticalSteering;
    }

    @Override
    public void tick() {
        if (this.operation != MoveControl.Operation.MOVE_TO || this.mob.getNavigation().isDone()
                || !this.mob.isInWater() || this.mob.getControllingPassenger() != null) {
            this.mob.setSpeed(0.0F);
            return;
        }
        float speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        this.mob.setSpeed(Mth.lerp(SPEED_SMOOTHING, this.mob.getSpeed(), speed));

        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 1.0E-5D) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement()
                    .add(0.0D, this.mob.getSpeed() * (dy / distance) * this.verticalSteering, 0.0D));
        }
        if (dx != 0.0D || dz != 0.0D) {
            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, this.maxTurnDegrees));
            this.mob.yBodyRot = this.mob.getYRot();
        }
    }
}