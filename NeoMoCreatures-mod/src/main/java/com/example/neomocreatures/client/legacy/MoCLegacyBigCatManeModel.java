package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Oversized shell drawn over the legacy big cat body to give it a mane (ported from MoCLegacyModelBigCat1). */
public class MoCLegacyBigCatManeModel extends EntityModel<MoCBigCatEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rearRightLeg;
    private final ModelPart rearLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;

    public MoCLegacyBigCatManeModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rearRightLeg = root.getChild("rear_right_leg");
        this.rearLeftLeg = root.getChild("rear_left_leg");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 0)
                .addBox(-7.0F, -8.0F, -2.0F, 14.0F, 14.0F, 8.0F), PartPose.offset(0.0F, 4.0F, -8.0F));
        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 0)
                .addBox(-6.0F, -11.0F, -8.0F, 12.0F, 10.0F, 10.0F), PartPose.offset(0.0F, 5.0F, 2.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        parts.addOrReplaceChild("rear_right_leg", leg, PartPose.offset(-3.0F, 12.0F, 7.0F));
        parts.addOrReplaceChild("rear_left_leg", leg, PartPose.offset(3.0F, 12.0F, 7.0F));
        parts.addOrReplaceChild("front_right_leg", leg, PartPose.offset(-3.0F, 12.0F, -5.0F));
        parts.addOrReplaceChild("front_left_leg", leg, PartPose.offset(3.0F, 12.0F, -5.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(MoCBigCatEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.body.xRot = Mth.HALF_PI;
        this.rearRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rearLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        this.frontRightLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        this.frontLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}