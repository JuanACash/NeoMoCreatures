package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCRoachEntity;
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

/** Port of {@code MoCModelRoach}: shells closed on the ground and swung open in flight, with the wings
 *  (60% opaque) shown only then. Like the source, the four antenna parts are animated but never
 *  actually drawn. */
public class MoCRoachModel<T extends MoCRoachEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart lAntenna;
    private final ModelPart lAntennaB;
    private final ModelPart rAntenna;
    private final ModelPart rAntennaB;
    private final ModelPart thorax;
    private final ModelPart frontLegs;
    private final ModelPart midLegs;
    private final ModelPart rearLegs;
    private final ModelPart abdomen;
    private final ModelPart tailL;
    private final ModelPart tailR;
    private final ModelPart lShellClosed;
    private final ModelPart rShellClosed;
    private final ModelPart lShellOpen;
    private final ModelPart rShellOpen;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public MoCRoachModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.lAntenna = root.getChild("l_antenna");
        this.lAntennaB = root.getChild("l_antenna_b");
        this.rAntenna = root.getChild("r_antenna");
        this.rAntennaB = root.getChild("r_antenna_b");
        this.thorax = root.getChild("thorax");
        this.frontLegs = root.getChild("front_legs");
        this.midLegs = root.getChild("mid_legs");
        this.rearLegs = root.getChild("rear_legs");
        this.abdomen = root.getChild("abdomen");
        this.tailL = root.getChild("tail_l");
        this.tailR = root.getChild("tail_r");
        this.lShellClosed = root.getChild("l_shell_closed");
        this.rShellClosed = root.getChild("r_shell_closed");
        this.lShellOpen = root.getChild("l_shell_open");
        this.rShellOpen = root.getChild("r_shell_open");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0F, -1F, 1, 1, 2),
                PartPose.offset(0F, 23F, -2F));
        root.addOrReplaceChild("l_antenna",
                CubeListBuilder.create().texOffs(3, 21).addBox(0F, 0F, 0F, 4, 0, 1),
                PartPose.offsetAndRotation(0.5F, 0F, 0F, -3.7420273F, 0.4363323F, 0F));
        root.addOrReplaceChild("l_antenna_b",
                CubeListBuilder.create().texOffs(4, 21).addBox(0F, 0F, 1F, 3, 0, 1),
                PartPose.offsetAndRotation(2.5F, 0F, -0.5F, 0F, 0.7853982F, 0F));
        root.addOrReplaceChild("r_antenna",
                CubeListBuilder.create().texOffs(3, 19).addBox(-4.5F, 0F, 0F, 4, 0, 1),
                PartPose.offsetAndRotation(0F, 0F, 0F, -3.7420273F, -0.4363323F, 0F));
        root.addOrReplaceChild("r_antenna_b",
                CubeListBuilder.create().texOffs(4, 19).addBox(-4F, 0F, 1F, 3, 0, 1),
                PartPose.offsetAndRotation(-2.5F, 0F, 0.5F, 0F, -0.7853982F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 3).addBox(-1F, 0F, -1F, 2, 1, 2),
                PartPose.offset(0F, 22F, -1F));
        root.addOrReplaceChild("front_legs",
                CubeListBuilder.create().texOffs(0, 11).addBox(-2F, 0F, 0F, 4, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.8F, -1.115358F, 0F, 0F));
        root.addOrReplaceChild("mid_legs",
                CubeListBuilder.create().texOffs(0, 13).addBox(-2.5F, 0F, 0F, 5, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.2F, 1.264073F, 0F, 0F));
        root.addOrReplaceChild("rear_legs",
                CubeListBuilder.create().texOffs(0, 15).addBox(-2F, 0F, 0F, 4, 4, 0),
                PartPose.offsetAndRotation(0F, 23F, -0.4F, 1.368173F, 0F, 0F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(0, 6).addBox(-1F, 0F, -1F, 2, 4, 1),
                PartPose.offsetAndRotation(0F, 22F, 0F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("tail_l",
                CubeListBuilder.create().texOffs(2, 29).addBox(-0.5F, 0F, 0F, 1, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, 3.6F, 1.554066F, 0.6457718F, 0F));
        root.addOrReplaceChild("tail_r",
                CubeListBuilder.create().texOffs(0, 29).addBox(-0.5F, 0F, 0F, 1, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, 3.6F, 1.554066F, -0.6457718F, 0F));
        root.addOrReplaceChild("l_shell_closed",
                CubeListBuilder.create().texOffs(4, 23).addBox(0F, 0F, 0F, 2, 0, 6),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, -0.1487144F, -0.0872665F, 0.1919862F));
        root.addOrReplaceChild("r_shell_closed",
                CubeListBuilder.create().texOffs(0, 23).addBox(-2F, 0F, 0F, 2, 0, 6),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, -0.1487144F, 0.0872665F, -0.1919862F));
        root.addOrReplaceChild("l_shell_open",
                CubeListBuilder.create().texOffs(4, 23).addBox(0F, 0F, 0F, 2, 0, 6),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, 1.117011F, -0.0872665F, 1.047198F));
        root.addOrReplaceChild("r_shell_open",
                CubeListBuilder.create().texOffs(0, 23).addBox(-2F, 0F, 0F, 2, 0, 6),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, 1.117011F, 0.0872665F, -1.047198F));
        root.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(11, 21).addBox(0F, 1F, -1F, 6, 0, 2),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, 0F, -1.047198F, -0.4363323F));
        root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(11, 19).addBox(-6F, 1F, -1F, 6, 0, 2),
                PartPose.offsetAndRotation(0F, 21.5F, -1.5F, 0F, 1.047198F, 0.4363323F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    private boolean flying;

    @Override
    public void setupAnim(T roach, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.flying = roach.isFlying() || roach.getDeltaMovement().y < -0.1D;

        this.head.xRot = -2.171231F + headPitch / ModelAnimations.DEGREES_PER_RADIAN;
        float antennaMove = 0.08726646F + limbSwingAmount * 1.5F;
        this.lAntenna.zRot = -antennaMove;
        this.rAntenna.zRot = antennaMove;

        float frontLegAdj = 0.0F;
        float legMov;
        float legMovB;
        if (this.flying) {
            float wingRot = Mth.cos(ageInTicks * 2.0F) * 0.7F;
            this.rightWing.yRot = 1.047198F + wingRot;
            this.leftWing.yRot = -1.047198F - wingRot;
            legMov = limbSwingAmount * 1.5F;
            legMovB = legMov;
            frontLegAdj = 1.4F;
        } else {
            legMov = Mth.cos(limbSwing * 1.5F + Mth.PI) * 0.6F * limbSwingAmount;
            legMovB = Mth.cos(limbSwing * 1.5F) * 0.8F * limbSwingAmount;
        }
        this.frontLegs.xRot = -1.115358F + frontLegAdj + legMov;
        this.midLegs.xRot = 1.264073F + legMovB;
        this.rearLegs.xRot = 1.368173F - frontLegAdj + legMov;
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.thorax.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.frontLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.midLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rearLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.abdomen.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.tailL.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.tailR.render(poseStack, buffer, packedLight, packedOverlay, color);
        if (!this.flying) {
            this.lShellClosed.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.rShellClosed.render(poseStack, buffer, packedLight, packedOverlay, color);
        } else {
            this.lShellOpen.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.rShellOpen.render(poseStack, buffer, packedLight, packedOverlay, color);
            int wingColor = InsectModelUtil.withAlpha(color, 0.6F);
            this.leftWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
            this.rightWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        }
    }

}
