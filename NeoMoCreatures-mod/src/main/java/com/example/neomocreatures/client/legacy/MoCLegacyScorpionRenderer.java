package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.client.HeldScorpionPose;
import com.example.neomocreatures.client.TameableOverlayRenderer;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Renders scorpions with the legacy model and textures, enabled by the legacyScorpionModel option. */
public class MoCLegacyScorpionRenderer extends MobRenderer<MoCScorpionEntity, MoCLegacyScorpionModel> {

    public static final ModelLayerLocation MOC_LEGACY_SCORPION_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_legacy_scorpion"), "main");

    private static final float ADULT_HEIGHT_OFFSET = -0.1F;

    public MoCLegacyScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCLegacyScorpionModel(context.bakeLayer(MOC_LEGACY_SCORPION_LAYER)), 0.5F);
    }

    @Override
    public void render(MoCScorpionEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        HeldScorpionPose.applyIfHeld(entity, partialTicks, poseStack);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    /** Baby size comes from the SCALE attribute; adults sit slightly higher, as in the original. */
    @Override
    protected void scale(MoCScorpionEntity entity, PoseStack poseStack, float partialTick) {
        if (!entity.isBaby()) {
            poseStack.translate(0.0F, ADULT_HEIGHT_OFFSET, 0.0F);
        }
    }

    /** Legacy art has no saddled texture, so the saddle is not drawn. */
    @Override
    public ResourceLocation getTextureLocation(MoCScorpionEntity entity) {
        ScorpionVariant shown = entity.getVariant();
        if (entity.isTransforming()) {
            int interval = Math.max(1, entity.getTransformTicks() / 8);
            if ((entity.tickCount / interval) % 2 == 0) {
                shown = ScorpionVariant.UNDEAD;
            }
        }
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_scorpion/" + shown.getTextureName() + "_legacy.png");
    }

    @Override
    protected void renderNameTag(MoCScorpionEntity entity, Component displayName, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}