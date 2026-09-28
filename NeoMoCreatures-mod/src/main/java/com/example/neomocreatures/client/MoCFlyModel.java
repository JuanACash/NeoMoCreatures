package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFlyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code MoCModelFly}. Wings are drawn 60% opaque and only while flying; otherwise the
 *  folded wings are drawn solid. */
public class MoCFlyModel<T extends MoCFlyEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart tail;
    private final ModelPart frontLegs;
    private final ModelPart rearLegs;
    private final ModelPart midLegs;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart foldedWings;
    private boolean flying;

    public MoCFlyModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.thorax = root.getChild("thorax");
        this.abdomen = root.getChild("abdomen");
        this.tail = root.getChild("tail");
        this.frontLegs = root.getChild("front_legs");
        this.rearLegs = root.getChild("rear_legs");
        this.midLegs = root.getChild("mid_legs");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
        this.foldedWings = root.getChild("folded_wings");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 4).addBox(-1F, 0F, -1F, 2, 1, 2),
                PartPose.offsetAndRotation(0F, 21.5F, -2F, -2.171231F, 0F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offset(0F, 20.5F, -1F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(8, 0).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offsetAndRotation(0F, 21.5F, 0F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(10, 2).addBox(-1F, 0F, -1F, 1, 1, 1),
                PartPose.offsetAndRotation(0.5F, 21.2F, 1.5F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("front_legs",
                CubeListBuilder.create().texOffs(0, 7).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -1.8F, 0.1487144F, 0F, 0F));
        root.addOrReplaceChild("rear_legs",
                CubeListBuilder.create().texOffs(0, 11).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -0.4F, 1.070744F, 0F, 0F));
        root.addOrReplaceChild("mid_legs",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -1.2F, 0.5948578F, 0F, 0F));
        root.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(4, 4).addBox(-1F, 0F, 0.5F, 2, 0, 4),
                PartPose.offsetAndRotation(0F, 20.4F, -1F, 0F, 1.047198F, 0F));
        root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(4, 4).addBox(-1F, 0F, 0.5F, 2, 0, 4),
                PartPose.offsetAndRotation(0F, 20.4F, -1F, 0F, -1.047198F, 0F));
        root.addOrReplaceChild("folded_wings",
                CubeListBuilder.create().texOffs(4, 4).addBox(-1F, 0F, 0F, 2, 0, 4),
                PartPose.offsetAndRotation(0F, 20.5F, -2F, 0.0872665F, 0F, 0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T fly, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.flying = fly.isFlying() || fly.getDeltaMovement().y < -0.1D;

        float wingRot = Mth.cos(ageInTicks * 3.0F) * 0.7F;
        this.rightWing.zRot = wingRot;
        this.leftWing.zRot = -wingRot;

        float legMov;
        float legMovB;
        if (this.flying) {
            legMov = limbSwingAmount * 1.5F;
            legMovB = legMov;
        } else {
            legMov = Mth.cos(limbSwing * 1.5F + Mth.PI) * 2.0F * limbSwingAmount;
            legMovB = Mth.cos(limbSwing * 1.5F) * 2.0F * limbSwingAmount;
        }
        this.frontLegs.xRot = 0.1487144F + legMov;
        this.midLegs.xRot = 0.5948578F + legMovB;
        this.rearLegs.xRot = 1.070744F + legMov;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.frontLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rearLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.midLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.tail.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.abdomen.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.thorax.render(poseStack, buffer, packedLight, packedOverlay, color);
        if (this.flying) {
            int wingColor = InsectModelUtil.withAlpha(color, 0.6F);
            this.leftWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
            this.rightWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        } else {
            this.foldedWings.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }
}
