package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFilchLizardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Port of {@code MoCRenderFilchLizard.LayerHeldItemCustom}: draws the stolen item held up in front of it. */
public class MoCFilchLizardLootLayer extends RenderLayer<MoCFilchLizardEntity, MoCFilchLizardModel<MoCFilchLizardEntity>> {

    private final ItemInHandRenderer itemRenderer;

    public MoCFilchLizardLootLayer(RenderLayerParent<MoCFilchLizardEntity, MoCFilchLizardModel<MoCFilchLizardEntity>> parent,
                                   ItemInHandRenderer itemRenderer) {
        super(parent);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoCFilchLizardEntity lizard,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack loot = lizard.getCarriedLoot();
        if (loot.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        // Original transforms, applied in model space.
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(20.0F));
        poseStack.translate(-0.55F, -1.0F, -0.05F);
        this.itemRenderer.renderItem(lizard, loot, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}