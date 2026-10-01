package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.CarriedBlockEntity;

import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

/** Port of {@code MoCRenderTRock}: draws the carried block itself, tumbling around its vertical axis. Shared by thrown and summoned rocks. */
public class MoCThrowableRockRenderer<T extends Entity & CarriedBlockEntity> extends EntityRenderer<T> {

    private static final float SHADOW_RADIUS = 0.5F;
    private static final float SPIN_DEGREES_PER_TICK = 18.0F;

    private final BlockRenderDispatcher blockRenderer;

    public MoCThrowableRockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = SHADOW_RADIUS;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState state = entity.getBlockState();
        if (state.getRenderShape() == RenderShape.MODEL) {
            poseStack.pushPose();
            // Spin around the centre of the 1x1x1 hitbox, then draw the block from its corner.
            poseStack.translate(0.0D, 0.5D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTick) * SPIN_DEGREES_PER_TICK));
            poseStack.translate(-0.5D, -0.5D, -0.5D);
            this.blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}