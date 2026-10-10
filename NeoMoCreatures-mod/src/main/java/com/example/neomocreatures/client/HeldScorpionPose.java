package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCScorpionEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Moves a held scorpion into its holder's hand, shared by the modern and legacy renderers. */
public final class HeldScorpionPose {

    private static final double HAND_FORWARD = 0.6D;
    private static final double HAND_DOWN = 0.35D;
    private static final double HELD_LIFT = 0.15D;

    private HeldScorpionPose() {
    }

    /** Does nothing when the scorpion is not held. Callers wrap this in pushPose/popPose. */
    public static void applyIfHeld(MoCScorpionEntity entity, float partialTicks, PoseStack poseStack) {
        Player holder = entity.isHeld() ? entity.getHolder() : null;
        if (holder == null) {
            return;
        }
        Vec3 scorpionPos = entity.getPosition(partialTicks);
        Vec3 handPos = holder.getEyePosition(partialTicks)
                .add(holder.getViewVector(partialTicks).scale(HAND_FORWARD))
                .add(0.0D, -HAND_DOWN, 0.0D);
        Vec3 delta = handPos.subtract(scorpionPos);

        poseStack.translate(delta.x, delta.y + HELD_LIFT, delta.z);
        poseStack.mulPose(Axis.XP.rotationDegrees(90F));
    }
}