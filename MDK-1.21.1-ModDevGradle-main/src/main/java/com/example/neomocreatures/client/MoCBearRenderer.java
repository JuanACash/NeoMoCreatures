package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBearEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCBearRenderer extends MobRenderer<MoCBearEntity, MoCBearModel> {

    public static final ModelLayerLocation MOC_BEAR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_bear"), "main");

    public MoCBearRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBearModel(context.bakeLayer(MOC_BEAR_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBearEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_bear/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void renderNameTag(MoCBearEntity entity, net.minecraft.network.chat.Component displayName, PoseStack poseStack,
                                net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}