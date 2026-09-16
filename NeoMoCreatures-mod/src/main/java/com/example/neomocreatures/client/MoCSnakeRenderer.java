package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSnakeEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCSnakeRenderer extends MobRenderer<MoCSnakeEntity, MoCSnakeModel> {

    public static final ModelLayerLocation MOC_SNAKE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_snake"), "main");

    public MoCSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSnakeModel(context.bakeLayer(MOC_SNAKE_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSnakeEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_snake/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCSnakeEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}