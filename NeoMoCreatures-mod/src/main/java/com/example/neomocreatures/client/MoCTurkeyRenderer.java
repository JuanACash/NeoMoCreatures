package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCTurkeyEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCTurkeyRenderer extends MobRenderer<MoCTurkeyEntity, MoCTurkeyModel> {

    public static final ModelLayerLocation MOC_TURKEY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_turkey"), "main");

    public MoCTurkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCTurkeyModel(context.bakeLayer(MOC_TURKEY_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCTurkeyEntity entity) {
        String file = (entity.isMale() && !entity.isBaby()) ? "turkey_male" : "turkey_female";
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_turkey/" + file + ".png");
    }

    @Override
    protected void renderNameTag(MoCTurkeyEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}