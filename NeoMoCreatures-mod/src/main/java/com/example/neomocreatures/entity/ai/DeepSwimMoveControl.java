package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * Move control of land reptiles that also swim underwater (crocodile, komodo dragon): on land,
 * or whenever the swim condition is false, it is the vanilla control; deep in water it steers
 * straight toward the path target in 3D, easing its current motion toward the wanted one.
 */
public class DeepSwimMoveControl extends MoveControl {

    /** How quickly the motion eases toward the wanted direction each tick. */
    private static final double MOTION_EASING = 0.125D;
    /** Idle in water: vertical motion is damped toward zero instead of rising or sinking. */
    private static final double IDLE_VERTICAL_DAMPING = 0.8D;
    private static final double ARRIVED_DISTANCE_SQR = 2.5E-7D;
    private static final float MAX_TURN_DEGREES = 90.0F;

    private final BooleanSupplier isSwimming;
    private final float swimSpeedMultiplier;

    public DeepSwimMoveControl(Mob mob, BooleanSupplier isSwimming, float swimSpeedMultiplier) {
        super(mob);
        this.isSwimming = isSwimming;
        this.swimSpeedMultiplier = swimSpeedMultiplier;
    }

    @Override
    public void tick() {
        if (!this.isSwimming.getAsBoolean()) {
            super.tick();
            return;
        }
        if (this.operation != MoveControl.Operation.MOVE_TO || this.mob.getNavigation().isDone()) {
            // Idle in water: damp existing motion toward zero instead of pushing up or down, so
            // it neither rockets to the surface nor sinks like a stone while it has nowhere to go.
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(1.0D, IDLE_VERTICAL_DAMPING, 1.0D));
            this.mob.setSpeed(0.0F);
            return;
        }
        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double distSqr = dx * dx + dy * dy + dz * dz;
        if (distSqr < ARRIVED_DISTANCE_SQR) {
            this.mob.setSpeed(0.0F);
            return;
        }
        float speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED))
                * this.swimSpeedMultiplier;
        Vec3 desired = new Vec3(dx, dy, dz).normalize().scale(speed);
        this.mob.setDeltaMovement(this.mob.getDeltaMovement().lerp(desired, MOTION_EASING));
        float yRotTarget = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotTarget, MAX_TURN_DEGREES));
        this.mob.yBodyRot = this.mob.getYRot();
    }
}