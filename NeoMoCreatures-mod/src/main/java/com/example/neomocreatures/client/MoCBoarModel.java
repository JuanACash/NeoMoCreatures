package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBoarEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelBoar}. */
public class MoCBoarModel<T extends MoCBoarEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart trout;
    private final ModelPart tusks;
    private final ModelPart jaw;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart headMane;
    private final ModelPart tail;
    private final ModelPart upperLegRight;
    private final ModelPart lowerLegRight;
    private final ModelPart upperLegLeft;
    private final ModelPart lowerLegLeft;
    private final ModelPart upperHindLegRight;
    private final ModelPart lowerHindLegRight;
    private final ModelPart upperHindLegLeft;
    private final ModelPart lowerHindLegLeft;

    public MoCBoarModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.trout = root.getChild("trout");
        this.tusks = root.getChild("tusks");
        this.jaw = root.getChild("jaw");
        this.leftEar = root.getChild("left_ear");
        this.rightEar = root.getChild("right_ear");
        this.headMane = root.getChild("head_mane");
        this.tail = root.getChild("tail");
        this.upperLegRight = root.getChild("upper_leg_right");
        this.lowerLegRight = root.getChild("lower_leg_right");
        this.upperLegLeft = root.getChild("upper_leg_left");
        this.lowerLegLeft = root.getChild("lower_leg_left");
        this.upperHindLegRight = root.getChild("upper_hind_leg_right");
        this.lowerHindLegRight = root.getChild("lower_hind_leg_right");
        this.upperHindLegLeft = root.getChild("upper_hind_leg_left");
        this.lowerHindLegLeft = root.getChild("lower_hind_leg_left");
    }

    /** The texture layout is 64x64. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3F, 0F, -5F, 6, 6, 5),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("trout",
                CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, 1.5F, -9.5F, 3, 3, 5),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.3490659F, 0F, 0F));
        root.addOrReplaceChild("tusks",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2F, 3F, -8F, 4, 2, 1),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.3490659F, 0F, 0F));
        root.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(0, 19).addBox(-1F, 4.9F, -8.5F, 2, 1, 4),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(16, 11).addBox(1F, -4F, -2F, 2, 4, 2),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.6981317F, 0F, 0.3490659F));
        root.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(16, 17).addBox(-3F, -4F, -2F, 2, 4, 2),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.6981317F, 0F, -0.3490659F));
        root.addOrReplaceChild("head_mane",
                CubeListBuilder.create().texOffs(23, 0).addBox(-1F, -2F, -5F, 2, 2, 5),
                PartPose.offsetAndRotation(0F, 11F, -5F, 0.4363323F, 0F, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(24, 0).addBox(-3.5F, 0F, 0F, 7, 8, 13),
                PartPose.offsetAndRotation(0F, 11F, -5F, -0.0872665F, 0F, 0F));
        root.addOrReplaceChild("body_mane",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1F, -2F, -1F, 2, 2, 9),
                PartPose.offsetAndRotation(0F, 11.3F, -4F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(60, 38).addBox(-0.5F, 0F, 0F, 1, 5, 1),
                PartPose.offsetAndRotation(0F, 13F, 7.5F, 0.0872665F, 0F, 0F));
        root.addOrReplaceChild("upper_leg_right",
                CubeListBuilder.create().texOffs(32, 21).addBox(-1F, -2F, -2F, 1, 5, 3),
                PartPose.offsetAndRotation(-3.5F, 16F, -2.5F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("lower_leg_right",
                CubeListBuilder.create().texOffs(32, 29).addBox(-0.5F, 2F, -1F, 2, 6, 2),
                PartPose.offset(-3.5F, 16F, -2.5F));
        root.addOrReplaceChild("upper_leg_left",
                CubeListBuilder.create().texOffs(24, 21).addBox(0F, -2F, -2F, 1, 5, 3),
                PartPose.offsetAndRotation(3.5F, 16F, -2.5F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("lower_leg_left",
                CubeListBuilder.create().texOffs(24, 29).addBox(-1.5F, 2F, -1F, 2, 6, 2),
                PartPose.offset(3.5F, 16F, -2.5F));
        root.addOrReplaceChild("upper_hind_leg_right",
                CubeListBuilder.create().texOffs(44, 21).addBox(-1.5F, -2F, -2F, 1, 5, 4),
                PartPose.offsetAndRotation(-3F, 16F, 5.5F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("lower_hind_leg_right",
                CubeListBuilder.create().texOffs(46, 30).addBox(-1F, 2F, 0F, 2, 6, 2),
                PartPose.offset(-3F, 16F, 5.5F));
        root.addOrReplaceChild("upper_hind_leg_left",
                CubeListBuilder.create().texOffs(54, 21).addBox(0.5F, -2F, -2F, 1, 5, 4),
                PartPose.offsetAndRotation(3F, 16F, 5.5F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("lower_hind_leg_left",
                CubeListBuilder.create().texOffs(56, 30).addBox(-1F, 2F, 0F, 2, 6, 2),
                PartPose.offset(3F, 16F, 5.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T boar, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float xAngle = headPitch * Mth.DEG_TO_RAD;
        float yAngle = netHeadYaw * Mth.DEG_TO_RAD;

        this.head.xRot = 0.2617994F + xAngle;
        this.head.yRot = yAngle;
        this.headMane.xRot = 0.4363323F + xAngle;
        this.headMane.yRot = yAngle;
        this.trout.xRot = 0.3490659F + xAngle;
        this.trout.yRot = yAngle;
        this.jaw.xRot = 0.2617994F + xAngle;
        this.jaw.yRot = yAngle;
        this.tusks.xRot = 0.3490659F + xAngle;
        this.tusks.yRot = yAngle;
        this.leftEar.xRot = 0.6981317F + xAngle;
        this.leftEar.yRot = yAngle;
        this.rightEar.xRot = 0.6981317F + xAngle;
        this.rightEar.yRot = yAngle;

        float leftLegRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float rightLegRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;

        this.upperLegLeft.xRot = leftLegRot;
        this.lowerLegLeft.xRot = leftLegRot;
        this.upperHindLegRight.xRot = leftLegRot;
        this.lowerHindLegRight.xRot = leftLegRot;
        this.upperLegRight.xRot = rightLegRot;
        this.lowerLegRight.xRot = rightLegRot;
        this.upperHindLegLeft.xRot = rightLegRot;
        this.lowerHindLegLeft.xRot = rightLegRot;

        this.tail.zRot = leftLegRot * 0.2F;
    }
}