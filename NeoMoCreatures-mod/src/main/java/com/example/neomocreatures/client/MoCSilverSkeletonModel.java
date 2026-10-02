package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCSilverSkeletonEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code MoCModelSilverSkeleton}: each arm carries a fixed bone-blade "sword" piece that
 *  swings along with the arm itself; the swords are part of the model, not a held item. */
public class MoCSilverSkeletonModel<T extends MoCSilverSkeletonEntity> extends HierarchicalModel<T> {

    private static final float RADIAN = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart rightHand;
    private final ModelPart rightSwordA;
    private final ModelPart rightSwordB;
    private final ModelPart rightSwordC;
    private final ModelPart leftArm;
    private final ModelPart leftHand;
    private final ModelPart leftSwordA;
    private final ModelPart leftSwordB;
    private final ModelPart leftSwordC;
    private final ModelPart rightThigh;
    private final ModelPart rightKnee;
    private final ModelPart rightLeg;
    private final ModelPart leftThigh;
    private final ModelPart leftKnee;
    private final ModelPart leftLeg;

    public MoCSilverSkeletonModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.rightHand = root.getChild("right_hand");
        this.rightSwordA = root.getChild("right_sword_a");
        this.rightSwordB = root.getChild("right_sword_b");
        this.rightSwordC = root.getChild("right_sword_c");
        this.leftArm = root.getChild("left_arm");
        this.leftHand = root.getChild("left_hand");
        this.leftSwordA = root.getChild("left_sword_a");
        this.leftSwordB = root.getChild("left_sword_b");
        this.leftSwordC = root.getChild("left_sword_c");
        this.rightThigh = root.getChild("right_thigh");
        this.rightKnee = root.getChild("right_knee");
        this.rightLeg = this.rightThigh.getChild("right_leg");
        this.leftThigh = root.getChild("left_thigh");
        this.leftKnee = root.getChild("left_knee");
        this.leftLeg = this.leftThigh.getChild("left_leg");
    }

    /** The texture layout is 64x64. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -4F, 8, 8, 8),
                PartPose.offset(0F, -2F, 0F));
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4F, 0F, -2F, 8, 12, 4),
                PartPose.offset(0F, -2F, 0F));
        body.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(44, 54).addBox(-4F, -4F, 0.5F, 8, 8, 2),
                PartPose.offsetAndRotation(0F, 2F, 2F, -0.1570796F, 0F, 0F));
        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(48, 31).addBox(-3F, -2.5F, -2.5F, 4, 11, 4),
                PartPose.offset(-5F, 1F, 0F));
        root.addOrReplaceChild("right_hand",
                CubeListBuilder.create().texOffs(24, 16).addBox(-2.5F, -2F, -2F, 3, 12, 3),
                PartPose.offset(-5F, 1F, 0F));
        root.addOrReplaceChild("right_sword_a",
                CubeListBuilder.create().texOffs(52, 46).addBox(-1.5F, 8.5F, -3F, 1, 1, 5),
                PartPose.offset(-5F, 1F, 0F));
        root.addOrReplaceChild("right_sword_b",
                CubeListBuilder.create().texOffs(48, 50).addBox(-1.5F, 7.5F, -4F, 1, 3, 1),
                PartPose.offset(-5F, 1F, 0F));
        root.addOrReplaceChild("right_sword_c",
                CubeListBuilder.create().texOffs(28, 28).addBox(-1F, 7.5F, -14F, 0, 3, 10),
                PartPose.offset(-5F, 1F, 0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(48, 16).addBox(-1F, -2.5F, -2.5F, 4, 11, 4),
                PartPose.offset(5F, 1F, 0F));
        root.addOrReplaceChild("left_hand",
                CubeListBuilder.create().texOffs(36, 16).addBox(-0.5F, -2F, -2F, 3, 12, 3),
                PartPose.offset(5F, 1F, 0F));
        root.addOrReplaceChild("left_sword_a",
                CubeListBuilder.create().texOffs(52, 46).addBox(0.5F, 8.5F, -3F, 1, 1, 5),
                PartPose.offset(5F, 1F, 0F));
        root.addOrReplaceChild("left_sword_b",
                CubeListBuilder.create().texOffs(48, 46).addBox(0.5F, 7.5F, -4F, 1, 3, 1),
                PartPose.offset(5F, 1F, 0F));
        root.addOrReplaceChild("left_sword_c",
                CubeListBuilder.create().texOffs(28, 31).addBox(1F, 7.5F, -14F, 0, 3, 10),
                PartPose.offset(5F, 1F, 0F));

        PartDefinition rightThigh = root.addOrReplaceChild("right_thigh",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offset(-2F, 10.5F, 0F));
        root.addOrReplaceChild("right_knee",
                CubeListBuilder.create().texOffs(0, 46).addBox(-2F, 1F, -2F, 4, 4, 4),
                PartPose.offset(-2F, 10.5F, 0F));
        PartDefinition rightLeg = rightThigh.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 25).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offset(0F, 6F, 0F));
        rightLeg.addOrReplaceChild("right_foot",
                CubeListBuilder.create().texOffs(0, 54).addBox(-2F, 0F, -2F, 4, 6, 4),
                PartPose.offset(0F, 2F, 0F));

        PartDefinition leftThigh = root.addOrReplaceChild("left_thigh",
                CubeListBuilder.create().texOffs(12, 16).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offset(2F, 10.5F, 0F));
        root.addOrReplaceChild("left_knee",
                CubeListBuilder.create().texOffs(16, 46).addBox(-2F, 1F, -2F, 4, 4, 4),
                PartPose.offset(2F, 10.5F, 0F));
        PartDefinition leftLeg = leftThigh.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(12, 25).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offset(0F, 6F, 0F));
        leftLeg.addOrReplaceChild("left_foot",
                CubeListBuilder.create().texOffs(16, 54).addBox(-2F, 0F, -2F, 4, 6, 4),
                PartPose.offset(0F, 2F, 0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T skeleton, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch / RADIAN;
        this.head.yRot = netHeadYaw / RADIAN;

        float rightLegX = ModelAnimations.walkSwingOpposite(limbSwing, limbSwingAmount, 0.8F);
        float leftLegX = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.8F);

        int leftAttack = skeleton.attackCounterLeft;
        int rightAttack = skeleton.attackCounterRight;

        if (leftAttack == 0) {
            this.leftArm.zRot = Mth.cos(ageInTicks * 0.09F) * 0.05F - 0.05F;
            this.leftArm.xRot = rightLegX;
        } else {
            this.leftArm.xRot = -(Mth.cos(leftAttack * 0.18F) * 3.0F);
        }
        if (rightAttack == 0) {
            this.rightArm.zRot = -(Mth.cos(ageInTicks * 0.09F) * 0.05F) + 0.05F;
            this.rightArm.xRot = leftLegX;
        } else {
            this.rightArm.xRot = -(Mth.cos(rightAttack * 0.18F) * 3.0F);
        }

        this.leftSwordB.xRot = this.leftSwordC.xRot = this.leftArm.xRot;
        this.leftSwordA.xRot = this.leftSwordC.xRot;
        this.leftHand.xRot = this.leftSwordC.xRot;
        this.leftSwordB.zRot = this.leftSwordC.zRot = this.leftArm.zRot;
        this.leftSwordA.zRot = this.leftSwordC.zRot;
        this.leftHand.zRot = this.leftSwordC.zRot;

        this.rightSwordB.xRot = this.rightSwordC.xRot = this.rightArm.xRot;
        this.rightSwordA.xRot = this.rightSwordC.xRot;
        this.rightHand.xRot = this.rightSwordC.xRot;
        this.rightSwordB.zRot = this.rightSwordC.zRot = this.rightArm.zRot;
        this.rightSwordA.zRot = this.rightSwordC.zRot;
        this.rightHand.zRot = this.rightSwordC.zRot;

        this.rightThigh.yRot = 0.0F;
        this.rightKnee.yRot = 0.0F;
        this.leftThigh.yRot = 0.0F;
        this.leftKnee.yRot = 0.0F;
        this.rightThigh.xRot = rightLegX;
        this.leftThigh.xRot = leftLegX;
        this.rightKnee.xRot = this.rightThigh.xRot;
        this.leftKnee.xRot = this.leftThigh.xRot;

        float rightLegXNext = Mth.cos((limbSwing + 0.1F) * 0.6662F + Mth.PI) * 0.8F * limbSwingAmount;
        float leftLegXNext = Mth.cos((limbSwing + 0.1F) * 0.6662F) * 0.8F * limbSwingAmount;
        float rightLegLower = rightLegX;
        float leftLegLower = leftLegX;
        if (limbSwingAmount > 0.15F) {
            if (rightLegX > rightLegXNext) {
                rightLegLower = rightLegX + 25.0F / RADIAN;
            }
            if (leftLegX > leftLegXNext) {
                leftLegLower = leftLegX + 25.0F / RADIAN;
            }
        }
        this.rightLeg.xRot = leftLegLower;
        this.leftLeg.xRot = rightLegLower;

        boolean sprinting = skeleton.isSprinting();
        this.body.xRot = sprinting && limbSwingAmount > 0.3F ? -limbSwingAmount * 20.0F / RADIAN : 0.0F;
    }
}