package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCFoxEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCFoxRenderer extends MobRenderer<MoCFoxEntity, MoCFoxModel> {

    public static final ModelLayerLocation MOC_FOX_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_fox"), "main");

    public MoCFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCFoxModel(context.bakeLayer(MOC_FOX_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCFoxEntity entity) {
        String file = entity.isSnow() ? "fox_snow" : (entity.isBaby() ? "fox_cub" : "fox");
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_fox/" + file + ".png");
    }

    @Override
    protected void renderNameTag(MoCFoxEntity entity, Component displayName, PoseStack poseStack,
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