package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCManticoreEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCManticoreRenderer extends MobRenderer<MoCManticoreEntity, MoCManticoreModel> {

    public static final ModelLayerLocation MOC_MANTICORE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_manticore"), "main");

    public MoCManticoreRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCManticoreModel(context.bakeLayer(MOC_MANTICORE_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCManticoreEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_manticore/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCManticoreEntity entity, Component displayName, PoseStack poseStack,
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