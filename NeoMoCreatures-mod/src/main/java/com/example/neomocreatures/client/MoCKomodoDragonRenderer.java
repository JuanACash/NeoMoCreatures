package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCKomodoDragonEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCKomodoDragonRenderer extends MobRenderer<MoCKomodoDragonEntity, MoCKomodoDragonModel> {

    public static final ModelLayerLocation MOC_KOMODO_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_komodo"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_komodo/komodo_dragon.png");

    public MoCKomodoDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCKomodoDragonModel(context.bakeLayer(MOC_KOMODO_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCKomodoDragonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderNameTag(MoCKomodoDragonEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}