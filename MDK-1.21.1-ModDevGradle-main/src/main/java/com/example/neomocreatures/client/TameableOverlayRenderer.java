package com.example.neomocreatures.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

/**
 * Draws a small green/red health bar above a tamed creature's name tag,
 * matching the original Mo' Creatures behavior. Shared utility so every
 * future tameable entity (wyverns, dolphins, manticores, rabbits, etc.)
 * can call the same thing from its renderNameTag() override instead of
 * each reimplementing it.
 */
public final class TameableOverlayRenderer {

    private static final double SHOW_DISTANCE_SQR = 64.0D; // 8 blocks, same as the name tag
    private static final int BAR_WIDTH = 40;
    private static final int BAR_HEIGHT = 4;

    private TameableOverlayRenderer() {
    }

    public static boolean isOwnedAndTamed(LivingEntity entity) {
        if (entity instanceof TamableAnimal tamable) return tamable.isTame();
        if (entity instanceof AbstractHorse horse) return horse.isTamed();
        return false;
    }

    public static void renderHealthBar(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer,
                                        int packedLight, EntityRenderDispatcher dispatcher) {
        if (!isOwnedAndTamed(entity) || dispatcher.distanceToSqr(entity) > SHOW_DISTANCE_SQR) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0, entity.getBbHeight() + 0.65F, 0.0);
        poseStack.mulPose(dispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);

        Matrix4f matrix = poseStack.last().pose();
        float maxHealth = entity.getMaxHealth();
        float pct = maxHealth > 0F ? Mth.clamp(entity.getHealth() / maxHealth, 0F, 1F) : 0F;
        float left = -BAR_WIDTH / 2F;

        VertexConsumer vc = buffer.getBuffer(RenderType.textBackgroundSeeThrough());
        float filledWidth = BAR_WIDTH * pct;
        fillRect(vc, matrix, left + filledWidth, 0, left + BAR_WIDTH, BAR_HEIGHT, 0xFFAA0000, packedLight, 0F);
        fillRect(vc, matrix, left, 0, left + filledWidth, BAR_HEIGHT, 0xFF00AA00, packedLight, 0F);

        poseStack.popPose();
    }

    private static void fillRect(VertexConsumer vc, Matrix4f matrix, float x0, float y0, float x1, float y1,
                              int argb, int packedLight, float z) {
        float a = ((argb >> 24) & 0xFF) / 255F;
        float r = ((argb >> 16) & 0xFF) / 255F;
        float g = ((argb >> 8) & 0xFF) / 255F;
        float b = (argb & 0xFF) / 255F;
        int lightU = net.minecraft.client.renderer.LightTexture.block(packedLight);
        int lightV = net.minecraft.client.renderer.LightTexture.sky(packedLight);
        vc.addVertex(matrix, x0, y1, z).setColor(r, g, b, a).setUv(0F, 1F).setUv2(lightU, lightV);
        vc.addVertex(matrix, x1, y1, z).setColor(r, g, b, a).setUv(1F, 1F).setUv2(lightU, lightV);
        vc.addVertex(matrix, x1, y0, z).setColor(r, g, b, a).setUv(1F, 0F).setUv2(lightU, lightV);
        vc.addVertex(matrix, x0, y0, z).setColor(r, g, b, a).setUv(0F, 0F).setUv2(lightU, lightV);
    }
}