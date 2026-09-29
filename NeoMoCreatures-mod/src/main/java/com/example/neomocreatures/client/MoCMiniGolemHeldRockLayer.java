package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMiniGolemEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.RenderShape;

/** Draws the block the Mini Golem is holding above its head, turning with its body (like the Enderman's carried block). */
public class MoCMiniGolemHeldRockLayer extends RenderLayer<MoCMiniGolemEntity, MoCMiniGolemModel<MoCMiniGolemEntity>> {

    /** LivingEntityRenderer shifts model space 1.501 blocks down before calling layers. */
    private static final double MODEL_ORIGIN_HEIGHT = 1.501D;

    private final BlockRenderDispatcher blockRenderer;

    public MoCMiniGolemHeldRockLayer(RenderLayerParent<MoCMiniGolemEntity, MoCMiniGolemModel<MoCMiniGolemEntity>> parent,
                                     BlockRenderDispatcher blockRenderer) {
        super(parent);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoCMiniGolemEntity golem,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        golem.getHeldBlock()
                .filter(state -> state.getRenderShape() == RenderShape.MODEL)
                .ifPresent(state -> {
                    poseStack.pushPose();
                    // Undo the model's upside-down flip so the block is drawn upright, sitting on its raised hands.
                    poseStack.scale(-1.0F, -1.0F, 1.0F);
                    poseStack.translate(-0.5D, MoCMiniGolemEntity.HELD_ROCK_HEIGHT - MODEL_ORIGIN_HEIGHT, -0.5D);
                    this.blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                });
    }
}