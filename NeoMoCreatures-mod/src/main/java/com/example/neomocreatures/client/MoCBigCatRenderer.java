package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBigCatEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCBigCatRenderer extends MobRenderer<MoCBigCatEntity, MoCBigCatModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_big_cat");

    public static final ModelLayerLocation MOC_BIG_CAT_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_big_cat"), "main");

    public MoCBigCatRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBigCatModel(context.bakeLayer(MOC_BIG_CAT_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBigCatEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected RenderType getRenderType(MoCBigCatEntity entity, boolean bodyVisible, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, bodyVisible, translucent || entity.isGhost(), showOutline);
    }

    @Override
    public void render(MoCBigCatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
        if (!entity.isGhost()) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        int alphaInt = (int) (0.35F * 255F);
        MultiBufferSource alphaBuffer = renderType ->
                new com.example.neomocreatures.client.AlphaVertexConsumer(buffer.getBuffer(renderType), alphaInt);
        super.render(entity, entityYaw, partialTicks, poseStack, alphaBuffer, packedLight);
    }

    @Override
    protected void renderNameTag(MoCBigCatEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}