package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFireflyEntity;
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

/** Port of {@code MoCModelFirefly}: the wing covers stay shut on the ground and swing open, with the
 *  wings showing 60% opaque, only while flying. */
public class MoCFireflyModel<T extends MoCFireflyEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart antenna;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart tail;
    private final ModelPart frontLegs;
    private final ModelPart midLegs;
    private final ModelPart rearLegs;
    private final ModelPart rightShellOpen;
    private final ModelPart leftShellOpen;
    private final ModelPart rightShell;
    private final ModelPart leftShell;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private boolean flying;

    public MoCFireflyModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.antenna = root.getChild("antenna");
        this.thorax = root.getChild("thorax");
        this.abdomen = root.getChild("abdomen");
        this.tail = root.getChild("tail");
        this.frontLegs = root.getChild("front_legs");
        this.midLegs = root.getChild("mid_legs");
        this.rearLegs = root.getChild("rear_legs");
        this.rightShellOpen = root.getChild("right_shell_open");
        this.leftShellOpen = root.getChild("left_shell_open");
        this.rightShell = root.getChild("right_shell");
        this.leftShell = root.getChild("left_shell");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 4).addBox(-1F, 0F, -1F, 2, 1, 2),
                PartPose.offsetAndRotation(0F, 22.5F, -2F, -2.171231F, 0F, 0F));
        root.addOrReplaceChild("antenna",
                CubeListBuilder.create().texOffs(0, 7).addBox(-1F, 0F, 0F, 2, 1, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -3F, -1.665602F, 0F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offset(0F, 21F, -1F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(8, 0).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offsetAndRotation(0F, 22F, 0F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(8, 17).addBox(-1F, 0.5F, -1F, 2, 2, 1),
                PartPose.offsetAndRotation(0F, 21.3F, 1.5F, 1.13023F, 0F, 0F));
        root.addOrReplaceChild("front_legs",
                CubeListBuilder.create().texOffs(0, 7).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.8F, -0.8328009F, 0F, 0F));
        root.addOrReplaceChild("mid_legs",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.2F, 1.070744F, 0F, 0F));
        root.addOrReplaceChild("rear_legs",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 23F, -0.4F, 1.249201F, 0F, 0F));
        root.addOrReplaceChild("right_shell_open",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(-1F, 21F, -2F, 1.22F, 0F, -0.6457718F));
        root.addOrReplaceChild("left_shell_open",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(1F, 21F, -2F, 1.22F, 0F, 0.6457718F));
        root.addOrReplaceChild("right_shell",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(-1F, 21F, -2F, 0.0174533F, 0F, -0.6457718F));
        root.addOrReplaceChild("left_shell",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(1F, 21F, -2F, 0.0174533F, 0F, 0.6457718F));
        root.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(15, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(1F, 21F, -1F, 0F, 1.047198F, 0F));
        root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(15, 12).addBox(-1F, 0F, 0F, 2, 0, 5),
                PartPose.offsetAndRotation(-1F, 21F, -1F, 0F, -1.047198F, 0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T firefly, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.flying = firefly.isFlying() || firefly.getDeltaMovement().y < -0.1D;

        float frontLegAdj = 0.0F;
        float legMov;
        float legMovB;
        if (this.flying) {
            float wingRot = Mth.cos(ageInTicks * 1.8F) * 0.8F;
            this.rightWing.zRot = wingRot;
            this.leftWing.zRot = -wingRot;
            legMov = limbSwingAmount * 1.5F;
            legMovB = legMov;
            frontLegAdj = 1.4F;
        } else {
            legMov = Mth.cos(limbSwing * 1.5F + Mth.PI) * 2.0F * limbSwingAmount;
            legMovB = Mth.cos(limbSwing * 1.5F) * 2.0F * limbSwingAmount;
        }
        this.frontLegs.xRot = -0.8328009F + frontLegAdj + legMov;
        this.midLegs.xRot = 1.070744F + legMovB;
        this.rearLegs.xRot = 1.249201F + legMov;
        if (!this.flying) {
            this.frontLegs.yRot = 0.0F;
            this.midLegs.yRot = 0.0F;
            this.rearLegs.yRot = 0.0F;
        }
        // Original: positive headPitch here (unlike the Rat/Mouse/Duck models, which negate it).
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.zRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.antenna.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rearLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.midLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.abdomen.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.frontLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.thorax.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.tail.render(poseStack, buffer, packedLight, packedOverlay, color);
        if (!this.flying) {
            this.rightShell.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.leftShell.render(poseStack, buffer, packedLight, packedOverlay, color);
        } else {
            this.rightShellOpen.render(poseStack, buffer, packedLight, packedOverlay, color);
            this.leftShellOpen.render(poseStack, buffer, packedLight, packedOverlay, color);
            int wingColor = InsectModelUtil.withAlpha(color, 0.6F);
            this.leftWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
            this.rightWing.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        }
    }
}
