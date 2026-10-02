package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBirdEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCBirdRenderer extends MobRenderer<MoCBirdEntity, MoCBirdModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_bird");

    public static final ModelLayerLocation MOC_BIRD_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_bird"), "main");

    public MoCBirdRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBirdModel(context.bakeLayer(MOC_BIRD_LAYER)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBirdEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected void renderNameTag(MoCBirdEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}