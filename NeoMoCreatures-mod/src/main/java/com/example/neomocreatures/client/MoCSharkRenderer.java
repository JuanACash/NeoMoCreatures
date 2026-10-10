package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSharkEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCSharkRenderer extends MobRenderer<MoCSharkEntity, MoCSharkModel> {

    public static final ModelLayerLocation MOC_SHARK_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_shark"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_shark/shark.png");

    public MoCSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSharkModel(context.bakeLayer(MOC_SHARK_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSharkEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderNameTag(MoCSharkEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        if (TameableOverlayRenderer.shouldRenderName(entity)) {
            super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
        }
    }
}