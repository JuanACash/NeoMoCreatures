package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCJellyfishEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderType;

/**
 * Port of the jellyfish's renderer registration ({@code MoCRenderMoC} with
 * {@code MoCModelJellyFish}, shadow 0.3). The night glow is approximated by forcing the whole model
 * to full brightness rather than true light emission (see the entity's class comment for why).
 */
public class MoCJellyfishRenderer extends MobRenderer<MoCJellyfishEntity, MoCJellyfishModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_jellyfish");

    public static final ModelLayerLocation MOC_JELLYFISH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_jellyfish"), "main");

    private static final float SHADOW_RADIUS = 0.3F;
    private static final int FULL_BRIGHT = 15;

    public MoCJellyfishRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCJellyfishModel(context.bakeLayer(MOC_JELLYFISH_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCJellyfishEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected int getBlockLightLevel(MoCJellyfishEntity entity, BlockPos pos) {
        if (entity.isGlowingAtNight()) {
            return FULL_BRIGHT;
        }
        return super.getBlockLightLevel(entity, pos);
    }

    /** Original: always drawn semi-transparent (0.7 alpha), so this always forces the translucent
     *  render type instead of the model's default cutout. */
    @Override
    protected RenderType getRenderType(MoCJellyfishEntity entity, boolean bodyVisible, boolean translucent, boolean showOutline) {
        return super.getRenderType(entity, bodyVisible, true, showOutline);
    }

    @Override
    public void render(MoCJellyfishEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        int alphaInt = (int) (0.7F * 255F);
        MultiBufferSource alphaBuffer = renderType ->
                new com.example.neomocreatures.client.AlphaVertexConsumer(buffer.getBuffer(renderType), alphaInt);
        super.render(entity, entityYaw, partialTicks, poseStack, alphaBuffer, packedLight);
    }

    @Override
    protected void renderNameTag(MoCJellyfishEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}