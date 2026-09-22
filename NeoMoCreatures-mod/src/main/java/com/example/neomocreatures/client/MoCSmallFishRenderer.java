package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSmallFishEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Port of the small fish's renderer registration ({@code MoCRenderMoC} with
 * {@code MoCModelSmallFish}, shadow 0.1). Shared by all 8 variants; only the texture differs.
 */
public class MoCSmallFishRenderer extends MobRenderer<MoCSmallFishEntity, MoCSmallFishModel> {

    public static final ModelLayerLocation MOC_SMALL_FISH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_small_fish"), "main");

    private static final float SHADOW_RADIUS = 0.1F;

    public MoCSmallFishRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSmallFishModel(context.bakeLayer(MOC_SMALL_FISH_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSmallFishEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_small_fish/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCSmallFishEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}