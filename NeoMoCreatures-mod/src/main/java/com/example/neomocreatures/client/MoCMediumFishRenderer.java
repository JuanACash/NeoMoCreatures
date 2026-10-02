package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMediumFishEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared renderer for cod, salmon and bass ({@code MoCRenderMoC} with {@code MoCModelMediumFish} in
 * the original, shadow 0.3), since only the texture differs between the three.
 */
public class MoCMediumFishRenderer<T extends MoCMediumFishEntity> extends MobRenderer<T, MoCMediumFishModel<T>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_medium_fish");

    public static final ModelLayerLocation MOC_MEDIUM_FISH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_medium_fish"), "main");

    private static final float SHADOW_RADIUS = 0.3F;

    public MoCMediumFishRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMediumFishModel<>(context.bakeLayer(MOC_MEDIUM_FISH_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_CACHE.get(entity.getTextureName());
    }

    @Override
    protected void renderNameTag(T entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}