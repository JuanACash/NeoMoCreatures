package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCElephantEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class MoCElephantRenderer extends MobRenderer<MoCElephantEntity, MoCElephantModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_elephant");

    public static final ModelLayerLocation MOC_ELEPHANT_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_elephant"), "main");

    public MoCElephantRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCElephantModel(context.bakeLayer(MOC_ELEPHANT_LAYER)), 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCElephantEntity entity) {
        // The garment alone re-skins an Asian to the decorated texture. The howdah is
        // a separate physical throne sitting on top (see MoCElephantModel) — it doesn't
        // affect which texture is used.
        String textureName = entity.getVariant() == com.example.neomocreatures.entity.elephant.ElephantVariant.ASIAN
                && entity.hasGarment()
                ? com.example.neomocreatures.entity.elephant.ElephantVariant.ASIAN_DECORATED.getTextureName()
                : entity.getVariant().getTextureName();
        return TEXTURE_CACHE.get(textureName);
    }

    @Override
    protected void renderNameTag(MoCElephantEntity entity, Component displayName, PoseStack poseStack,
                                MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}