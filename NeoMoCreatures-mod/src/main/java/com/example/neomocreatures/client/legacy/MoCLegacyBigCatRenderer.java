package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.client.AlphaVertexConsumer;
import com.example.neomocreatures.client.TameableOverlayRenderer;
import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.example.neomocreatures.entity.bigcat.BigCatVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Renders big cats with the simple legacy model and textures, enabled by the legacyBigCatModels option. */
public class MoCLegacyBigCatRenderer extends MobRenderer<MoCBigCatEntity, MoCLegacyBigCatModel> {

    public static final ModelLayerLocation MOC_LEGACY_BIG_CAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_legacy_big_cat"), "main");
    public static final ModelLayerLocation MOC_LEGACY_BIG_CAT_MANE_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_legacy_big_cat"), "mane");

    private static final float GHOST_ALPHA = 0.35F;

    public MoCLegacyBigCatRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCLegacyBigCatModel(context.bakeLayer(MOC_LEGACY_BIG_CAT_LAYER)), 0.7F);
        this.addLayer(new ManeLayer(this,
                new MoCLegacyBigCatManeModel(context.bakeLayer(MOC_LEGACY_BIG_CAT_MANE_LAYER))));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBigCatEntity entity) {
        return texture(legacyTextureName(entity.getVariant()));
    }

    /** Legacy art has one texture per species, so male and female variants share it. */
    private static String legacyTextureName(BigCatVariant variant) {
        String name = variant.getTextureName();
        if (name.endsWith("_female")) {
            name = name.substring(0, name.length() - "_female".length());
        } else if (name.endsWith("_male")) {
            name = name.substring(0, name.length() - "_male".length());
        }
        return name + "_legacy";
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_big_cat/" + name + ".png");
    }

    @Override
    protected RenderType getRenderType(MoCBigCatEntity entity, boolean bodyVisible, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, bodyVisible, translucent || entity.isGhost(), showOutline);
    }

    @Override
    public void render(MoCBigCatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (!entity.isGhost()) {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        int alphaInt = (int) (GHOST_ALPHA * 255F);
        MultiBufferSource alphaBuffer = renderType -> new AlphaVertexConsumer(buffer.getBuffer(renderType), alphaInt);
        super.render(entity, entityYaw, partialTicks, poseStack, alphaBuffer, packedLight);
    }

    @Override
    protected void renderNameTag(MoCBigCatEntity entity, Component displayName, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    /** Draws the legacy mane shell; every cat without a mane gets the near-empty female layer, as in the original. */
    private static final class ManeLayer extends RenderLayer<MoCBigCatEntity, MoCLegacyBigCatModel> {

        private final MoCLegacyBigCatManeModel maneModel;

        ManeLayer(RenderLayerParent<MoCBigCatEntity, MoCLegacyBigCatModel> parent, MoCLegacyBigCatManeModel maneModel) {
            super(parent);
            this.maneModel = maneModel;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoCBigCatEntity entity,
                           float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                           float netHeadYaw, float headPitch) {
            this.maneModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture(layerTextureName(entity))));
            this.maneModel.renderToBuffer(poseStack, consumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F), -1);
        }

        private static String layerTextureName(MoCBigCatEntity entity) {
            BigCatVariant variant = entity.getVariant();
            boolean maned = variant.hasMane() && !entity.isBaby();
            if (maned && variant == BigCatVariant.WHITE_LION) {
                return "big_cat_white_lion_legacy_layer";
            }
            if (maned && variant == BigCatVariant.LION_MALE) {
                return "big_cat_lion_legacy_layer_male";
            }
            return "big_cat_lion_legacy_layer_female";
        }
    }
}