package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMantaRayEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Port of the manta ray's renderer registration ({@code MoCRenderMoC} with {@code MoCModelRay},
 * shadow 0.4). The 1.5x size is the entity's scale attribute, which the base renderer already applies.
 */
public class MoCMantaRayRenderer extends MobRenderer<MoCMantaRayEntity, MoCMantaRayModel> {

    public static final ModelLayerLocation MOC_MANTA_RAY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_manta_ray"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_manta_ray/ray_manta.png");

    private static final float SHADOW_RADIUS = 0.4F;

    public MoCMantaRayRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMantaRayModel(context.bakeLayer(MOC_MANTA_RAY_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCMantaRayEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderNameTag(MoCMantaRayEntity entity, Component displayName, PoseStack poseStack,
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