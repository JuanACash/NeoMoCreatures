package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCCrabEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Port of the crab's renderer registration ({@code MoCRenderMoC} with {@code MoCModelCrab}, shadow 0.3). */
public class MoCCrabRenderer extends MobRenderer<MoCCrabEntity, MoCCrabModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_crab");

    public static final ModelLayerLocation MOC_CRAB_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_crab"), "main");

    private static final float SHADOW_RADIUS = 0.3F;

    public MoCCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCCrabModel(context.bakeLayer(MOC_CRAB_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCCrabEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected void scale(MoCCrabEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getCrabScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    protected void renderNameTag(MoCCrabEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}