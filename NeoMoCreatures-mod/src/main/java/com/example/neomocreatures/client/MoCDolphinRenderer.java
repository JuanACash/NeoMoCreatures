package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCDolphinEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Port of the dolphin's renderer registration ({@code MoCRenderDolphin} with
 * {@code MoCModelDolphin}, shadow 0.6). The original's age-based model stretch is the entity's
 * scale attribute here, which the base renderer already applies.
 */
public class MoCDolphinRenderer extends MobRenderer<MoCDolphinEntity, MoCDolphinModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_dolphin");

    public static final ModelLayerLocation MOC_DOLPHIN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_dolphin"), "main");

    private static final float SHADOW_RADIUS = 0.6F;

    public MoCDolphinRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCDolphinModel(context.bakeLayer(MOC_DOLPHIN_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCDolphinEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected void renderNameTag(MoCDolphinEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}