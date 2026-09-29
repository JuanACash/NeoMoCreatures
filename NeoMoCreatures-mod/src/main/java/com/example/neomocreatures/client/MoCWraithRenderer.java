package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCFlameWraithEntity;
import com.example.neomocreatures.entity.MoCWraithEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the real {@code MoCRenderWraith}: same standard render pipeline every other entity in
 *  this mod uses (position, body/head rotation, walk animation all handled automatically), just
 *  drawn semi-transparent with a colour tint — grey for the Wraith, reddish for the Flame Wraith. */
public class MoCWraithRenderer extends MobRenderer<MoCWraithEntity, MoCWraithModel<MoCWraithEntity>> {

    public static final ModelLayerLocation MOC_WRAITH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_wraith"), "main");

    public MoCWraithRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWraithModel<>(context.bakeLayer(MOC_WRAITH_LAYER)), 0.5F);
    }

    @Override
    public void render(MoCWraithEntity wraith, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        boolean isFlame = wraith instanceof MoCFlameWraithEntity;
        int alpha = isFlame ? 102 : 153; // 0.4 / 0.6 as a 0-255 value
        int tintR = isFlame ? 255 : 204; // 1.0 / 0.8
        int tintG = isFlame ? 153 : 204; // 0.6 / 0.8
        int tintB = isFlame ? 153 : 204; // 0.6 / 0.8

        MultiBufferSource tintedBuffer = renderType -> new AlphaVertexConsumer(
                buffer.getBuffer(renderType), alpha, tintR, tintG, tintB);
        super.render(wraith, entityYaw, partialTick, poseStack, tintedBuffer, packedLight);
    }

    @Override
    protected net.minecraft.client.renderer.RenderType getRenderType(MoCWraithEntity entity, boolean bodyVisible,
                       boolean translucent, boolean glowing) {
        return net.minecraft.client.renderer.RenderType.entityTranslucent(this.getTextureLocation(entity));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWraithEntity entity) {
        String texture = entity instanceof MoCFlameWraithEntity ? "wraith_flame" : "wraith";
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_wraith/" + texture + ".png");
    }
}