package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;

public class MoCWyvernRenderer extends MobRenderer<MoCWyvernEntity, MoCWyvernModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("wyvern");

    public static final ModelLayerLocation MOC_WYVERN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "wyvern"), "main");

    public MoCWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWyvernModel(context.bakeLayer(MOC_WYVERN_LAYER)), 0.9F);
    }

    /**
     * Visual size only — completely separate from the hitbox math in
     * MoCWyvernEntity#tickGrowth() (which is deliberately capped at 1.0 so
     * it never multiplies the EntityType's own already-tier-sized hitbox
     * again). getVisualScale() is the one that actually reaches 1.3/1.5/2.0
     * for a grown tier 2/mother/mother-tamed.
     */
    @Override
    protected void scale(MoCWyvernEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getVisualScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWyvernEntity entity) {
        var variant = entity.getVariant();
        if (entity.isTransforming()) {
            int ticksLeft = entity.getTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showTarget = (entity.tickCount / interval) % 2 == 0;
            variant = showTarget ? entity.getTransformTarget() : variant;
        }
        String textureName = entity.isGhost() ? variant.getGhostTextureName() : variant.getTextureName();
        return TEXTURE_CACHE.get(textureName);
    }

    @Override
    protected RenderType getRenderType(MoCWyvernEntity entity, boolean bodyVisible, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, bodyVisible, translucent || entity.isGhost(), showOutline);
    }

    @Override
    public void render(MoCWyvernEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!entity.isGhost()) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        int alphaInt = (int) (0.35F * 255F);
        MultiBufferSource alphaBuffer = renderType ->
                new AlphaVertexConsumer(buffer.getBuffer(renderType), alphaInt);
        super.render(entity, entityYaw, partialTicks, poseStack, alphaBuffer, packedLight);
    }

    @Override
    protected void renderNameTag(MoCWyvernEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}