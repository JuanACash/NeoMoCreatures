package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.client.TameableOverlayRenderer;
import com.example.neomocreatures.entity.MoCSharkEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Renders sharks with the legacy model and texture, enabled by the legacySharkModel option. */
public class MoCLegacySharkRenderer extends MobRenderer<MoCSharkEntity, MoCLegacySharkModel> {

    public static final ModelLayerLocation MOC_LEGACY_SHARK_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_legacy_shark"), "main");

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NeoMoCreatures.MODID, "textures/entity/moc_shark/shark_legacy.png");
    
    /** Compensates for the original age-based scaling, which the port no longer has. */
    private static final float LEGACY_SCALE = 2.0F;

    public MoCLegacySharkRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCLegacySharkModel(context.bakeLayer(MOC_LEGACY_SHARK_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSharkEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderNameTag(MoCSharkEntity entity, Component displayName, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    @Override
    protected void scale(MoCSharkEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(LEGACY_SCALE, LEGACY_SCALE, LEGACY_SCALE);
    }

}