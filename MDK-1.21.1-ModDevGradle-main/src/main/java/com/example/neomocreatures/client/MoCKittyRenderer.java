package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCKittyEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

public class MoCKittyRenderer extends MobRenderer<MoCKittyEntity, MoCKittyModel> {

    public static final ModelLayerLocation MOC_KITTY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_kitty"), "main");

    public MoCKittyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCKittyModel(context.bakeLayer(MOC_KITTY_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCKittyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_kitty/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCKittyEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}