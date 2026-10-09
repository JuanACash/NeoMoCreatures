package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.entity.MoCScorpionEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Blocky scorpion from legacy Mo' Creatures versions (ported from MoCLegacyModelScorpion). */
public class MoCLegacyScorpionModel extends HierarchicalModel<MoCScorpionEntity> {

    private static final float ARM_YAW = 0.27925F;
    private static final float LEG_ROLL = 0.7853982F;
    private static final float LEG_YAW = 0.3926991F;
    private static final float REAR_END_X_ROT = 0.13963F;
    private static final float LEG_STRIDE_SPEED = 0.6662F;

    private final ModelPart root;
    private final ModelPart rearEnd;
    private final ModelPart[] legs = new ModelPart[8];
    private final ModelPart[] tail = new ModelPart[6];
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightHand;
    private final ModelPart leftHand;
    private final ModelPart rightHandB;
    private final ModelPart leftHandB;

    public MoCLegacyScorpionModel(ModelPart root) {
        this.root = root;
        this.rearEnd = root.getChild("rear_end");
        for (int i = 0; i < legs.length; i++) {
            legs[i] = root.getChild("leg" + (i + 1));
        }
        for (int i = 0; i < tail.length; i++) {
            tail[i] = root.getChild("tail" + (i + 1));
        }
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightHand = root.getChild("right_hand");
        this.leftHand = root.getChild("left_hand");
        this.rightHandB = root.getChild("right_hand_b");
        this.leftHandB = root.getChild("left_hand_b");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 19)
                .addBox(-4.0F, -3.0F, -6.0F, 8.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 20.0F, -5.0F));
        parts.addOrReplaceChild("rear_end", CubeListBuilder.create().texOffs(0, 11)
                .addBox(-5.0F, -3.0F, -3.0F, 10.0F, 4.0F, 17.0F),
                PartPose.offsetAndRotation(0.0F, 20.0F, -3.0F, REAR_END_X_ROT, 0.0F, 0.0F));

        addLegs(parts);
        addTail(parts);
        addArms(parts);

        return LayerDefinition.create(mesh, 64, 32);
    }

    /** Legs 1, 3, 5, 7 are on the right side, legs 2, 4, 6, 8 on the left, rear to front. */
    private static void addLegs(PartDefinition parts) {
        CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(20, 6)
                .addBox(-13.0F, -1.0F, 1.0F, 14.0F, 2.0F, 2.0F);
        CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(20, 6).mirror()
                .addBox(-1.0F, -1.0F, 1.0F, 14.0F, 2.0F, 2.0F);
        float[] zPivots = {6.0F, 3.0F, 1.0F, -2.0F};
        for (int pair = 0; pair < zPivots.length; pair++) {
            parts.addOrReplaceChild("leg" + (pair * 2 + 1), rightLeg, PartPose.offset(-4.0F, 20.0F, zPivots[pair]));
            parts.addOrReplaceChild("leg" + (pair * 2 + 2), leftLeg, PartPose.offset(4.0F, 20.0F, zPivots[pair]));
        }
    }

    private static void addTail(PartDefinition parts) {
        PartPose pivot = PartPose.offset(0.0F, 20.0F, 0.0F);
        parts.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -8.0F, 8.0F, 6.0F, 4.0F, 4.0F), pivot);
        parts.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -12.0F, 10.0F, 6.0F, 4.0F, 4.0F), pivot);
        parts.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 8)
                .addBox(-2.0F, -14.0F, 8.0F, 4.0F, 4.0F, 4.0F), pivot);
        parts.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(0, 8)
                .addBox(-2.0F, -17.0F, 6.0F, 4.0F, 4.0F, 4.0F), pivot);
        parts.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(0, 8)
                .addBox(-2.0F, -19.0F, 3.0F, 4.0F, 4.0F, 4.0F), pivot);
        parts.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(0, 20)
                .addBox(-1.0F, -18.0F, 0.0F, 2.0F, 4.0F, 4.0F), pivot);
    }

    private static void addArms(PartDefinition parts) {
        PartPose rightPivot = PartPose.offsetAndRotation(-4.0F, 20.0F, -9.0F, 0.0F, -ARM_YAW, 0.0F);
        PartPose leftPivot = PartPose.offsetAndRotation(4.0F, 20.0F, -9.0F, 0.0F, ARM_YAW, 0.0F);

        parts.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-11.0F, -1.0F, -1.0F, 11.0F, 2.0F, 4.0F), rightPivot);
        parts.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).mirror()
                .addBox(-1.0F, -1.0F, -1.0F, 12.0F, 2.0F, 4.0F), leftPivot);
        parts.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(44, 9)
                .addBox(-11.0F, -1.0F, -9.0F, 2.0F, 2.0F, 8.0F), rightPivot);
        parts.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(44, 9)
                .addBox(9.0F, -1.0F, -9.0F, 2.0F, 2.0F, 8.0F), leftPivot);
        parts.addOrReplaceChild("right_hand_b", CubeListBuilder.create().texOffs(44, 9)
                .addBox(-8.0F, -1.0F, -9.0F, 2.0F, 2.0F, 8.0F), rightPivot);
        parts.addOrReplaceChild("left_hand_b", CubeListBuilder.create().texOffs(44, 9)
                .addBox(6.0F, -1.0F, -9.0F, 2.0F, 2.0F, 8.0F), leftPivot);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCScorpionEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        animateLegPair(legs[0], legs[1], LEG_ROLL, LEG_YAW * 2.0F, 0.0F, limbSwing, limbSwingAmount);
        animateLegPair(legs[2], legs[3], LEG_ROLL * 0.74F, LEG_YAW, Mth.PI, limbSwing, limbSwingAmount);
        animateLegPair(legs[4], legs[5], LEG_ROLL * 0.74F, -LEG_YAW, 1.570796F, limbSwing, limbSwingAmount);
        animateLegPair(legs[6], legs[7], LEG_ROLL, -LEG_YAW * 2.0F, 4.712389F, limbSwing, limbSwingAmount);

        this.rearEnd.xRot = REAR_END_X_ROT;

        float sway = Mth.cos(limbSwing * 0.4F) * 0.3F * limbSwingAmount;
        this.tail[0].xRot = sway * 0.8F;
        for (int i = 1; i < tail.length; i++) {
            this.tail[i].xRot = sway;
        }

        this.rightArm.yRot = -ARM_YAW;
        this.rightHand.yRot = -ARM_YAW;
        this.rightHandB.yRot = -ARM_YAW;
        this.leftArm.yRot = ARM_YAW;
        this.leftHand.yRot = ARM_YAW;
        this.leftHandB.yRot = ARM_YAW;

        this.rightArm.zRot = sway;
        this.rightHand.zRot = sway;
        this.rightHandB.zRot = sway;
        this.leftArm.zRot = -sway;
        this.leftHand.zRot = -sway;
        this.leftHandB.zRot = -sway;
    }

    private static void animateLegPair(ModelPart right, ModelPart left, float baseRoll, float baseYaw,
                                       float phase, float limbSwing, float limbSwingAmount) {
        float yawWalk = -(Mth.cos(limbSwing * LEG_STRIDE_SPEED * 2.0F + phase) * 0.4F) * limbSwingAmount;
        float rollWalk = Math.abs(Mth.sin(limbSwing * LEG_STRIDE_SPEED + phase) * 0.4F) * limbSwingAmount;
        right.zRot = -baseRoll + rollWalk;
        left.zRot = baseRoll - rollWalk;
        right.yRot = baseYaw + yawWalk;
        left.yRot = -baseYaw - yawWalk;
    }
}