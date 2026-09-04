package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

public class MoCWyvernRenderer extends MobRenderer<MoCWyvernEntity, MoCWyvernModel> {

    public static final ModelLayerLocation MOC_WYVERN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "wyvern"), "main");

    public MoCWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWyvernModel(context.bakeLayer(MOC_WYVERN_LAYER)), 0.9F);
    }

    /**
     * Visual size only — completely separate from the hitbox math in
     * MoCWyvernEntity#tickGrowth() (which is deliberately capped at 1.0 so
     * it never multiplies the EntityType's own already-tier-sized hitbox
     * again). getVisualScale() is the one that actually reaches 1.3/1.5/3.0
     * for a grown tier 2/mother/mother-tamed.
     */
    @Override
    protected void scale(MoCWyvernEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getVisualScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWyvernEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/wyvern/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCWyvernEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        // Match the health bar's own visibility range instead of vanilla's
        // much farther default (~64 blocks) — was making the name visible
        // from way farther away than the health bar next to it.
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}