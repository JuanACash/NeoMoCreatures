package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMiniGolemEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelMiniGolem}. Head and body swap to their red texture while aggressive. */
public class MoCMiniGolemModel<T extends MoCMiniGolemEntity> extends HierarchicalModel<T> {

    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 64;

    private static final float HEAD_BASE_YAW = -Mth.PI / 4.0F;
    private static final float WALK_FREQUENCY = 0.6662F;
    private static final float WALK_AMPLITUDE = 0.8F;
    private static final float ARM_SWAY_SPEED = 0.09F;
    private static final float ARM_SWAY_AMOUNT = 0.05F;
    /** Original: both shoulders pitch -180 degrees — arms straight up while holding a rock. */
    private static final float ARMS_RAISED_PITCH = -Mth.PI;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart headRed;
    private final ModelPart body;
    private final ModelPart bodyRed;
    private final ModelPart leftShoulder;
    private final ModelPart leftArm;
    private final ModelPart leftArmRingA;
    private final ModelPart leftArmRingB;
    private final ModelPart rightShoulder;
    private final ModelPart rightArm;
    private final ModelPart rightArmRingA;
    private final ModelPart rightArmRingB;
    private final ModelPart rightLeg;
    private final ModelPart rightFoot;
    private final ModelPart leftLeg;
    private final ModelPart leftFoot;

    public MoCMiniGolemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.headRed = root.getChild("head_red");
        this.body = root.getChild("body");
        this.bodyRed = root.getChild("body_red");
        this.leftShoulder = root.getChild("left_shoulder");
        this.leftArm = root.getChild("left_arm");
        this.leftArmRingA = root.getChild("left_arm_ring_a");
        this.leftArmRingB = root.getChild("left_arm_ring_b");
        this.rightShoulder = root.getChild("right_shoulder");
        this.rightArm = root.getChild("right_arm");
        this.rightArmRingA = root.getChild("right_arm_ring_a");
        this.rightArmRingB = root.getChild("right_arm_ring_b");
        this.rightLeg = root.getChild("right_leg");
        this.rightFoot = root.getChild("right_foot");
        this.leftLeg = root.getChild("left_leg");
        this.leftFoot = root.getChild("left_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartPose headPose = PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 0.0F, HEAD_BASE_YAW, 0.0F);
        PartPose bodyPose = PartPose.offset(0.0F, 18.0F, 0.0F);
        PartPose leftArmPose = PartPose.offset(5.0F, 11.0F, 0.0F);
        PartPose rightArmPose = PartPose.offset(-5.0F, 11.0F, 0.0F);
        PartPose rightLegPose = PartPose.offset(-2.0F, 18.0F, 0.0F);
        PartPose leftLegPose = PartPose.offset(2.0F, 18.0F, 0.0F);

        addBox(root, "head", 30, 0, -3.0F, -3.0F, -3.0F, 6, 3, 6, headPose);
        addBox(root, "head_red", 30, 29, -3.0F, -3.0F, -3.0F, 6, 3, 6, headPose);
        addBox(root, "body", 0, 0, -5.0F, -10.0F, -5.0F, 10, 10, 10, bodyPose);
        addBox(root, "body_red", 0, 28, -5.0F, -10.0F, -5.0F, 10, 10, 10, bodyPose);

        addBox(root, "left_shoulder", 0, 4, 0.0F, -1.0F, -1.0F, 1, 2, 2, leftArmPose);
        addBox(root, "left_arm", 0, 48, 1.0F, -2.0F, -2.0F, 4, 12, 4, leftArmPose);
        addBox(root, "left_arm_ring_a", 20, 20, 0.5F, 1.0F, -2.5F, 5, 3, 5, leftArmPose);
        addBox(root, "left_arm_ring_b", 20, 20, 0.5F, 5.0F, -2.5F, 5, 3, 5, leftArmPose);

        addBox(root, "right_shoulder", 0, 0, -1.0F, -1.0F, -1.0F, 1, 2, 2, rightArmPose);
        addBox(root, "right_arm", 16, 48, -5.0F, -2.0F, -2.0F, 4, 12, 4, rightArmPose);
        addBox(root, "right_arm_ring_a", 0, 20, -5.5F, 1.0F, -2.5F, 5, 3, 5, rightArmPose);
        addBox(root, "right_arm_ring_b", 0, 20, -5.5F, 5.0F, -2.5F, 5, 3, 5, rightArmPose);

        addBox(root, "right_leg", 40, 9, -2.5F, 0.0F, -2.0F, 4, 6, 4, rightLegPose);
        addBox(root, "right_foot", 15, 22, -2.5F, 5.0F, -3.0F, 4, 1, 1, rightLegPose);
        addBox(root, "left_leg", 40, 19, -1.5F, 0.0F, -2.0F, 4, 6, 4, leftLegPose);
        addBox(root, "left_foot", 15, 20, -1.5F, 5.0F, -3.0F, 4, 1, 1, leftLegPose);

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static void addBox(PartDefinition root, String name, int texU, int texV,
                               float x, float y, float z, int width, int height, int depth, PartPose pose) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(texU, texV).addBox(x, y, z, width, height, depth), pose);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        boolean angry = entity.isAggressive();
        this.head.visible = !angry;
        this.body.visible = !angry;
        this.headRed.visible = angry;
        this.bodyRed.visible = angry;

        float walkPhase = limbSwing * WALK_FREQUENCY;
        float rightLegPitch = Mth.cos(walkPhase + Mth.PI) * WALK_AMPLITUDE * limbSwingAmount;
        float leftLegPitch = Mth.cos(walkPhase) * WALK_AMPLITUDE * limbSwingAmount;
        this.rightLeg.xRot = rightLegPitch;
        this.rightFoot.xRot = rightLegPitch;
        this.leftLeg.xRot = leftLegPitch;
        this.leftFoot.xRot = leftLegPitch;

        float headYaw = HEAD_BASE_YAW + netHeadYaw * Mth.DEG_TO_RAD;
        this.head.yRot = headYaw;
        this.headRed.yRot = headYaw;

        if (entity.isHoldingRock()) {
            poseArm(this.leftShoulder, this.leftArm, this.leftArmRingA, this.leftArmRingB, ARMS_RAISED_PITCH, 0.0F);
            poseArm(this.rightShoulder, this.rightArm, this.rightArmRingA, this.rightArmRingB, ARMS_RAISED_PITCH, 0.0F);
        } else {
            // Arms swing opposite to their own-side leg, with a slow idle sway.
            float sway = Mth.cos(ageInTicks * ARM_SWAY_SPEED) * ARM_SWAY_AMOUNT;
            poseArm(this.leftShoulder, this.leftArm, this.leftArmRingA, this.leftArmRingB, rightLegPitch, sway - ARM_SWAY_AMOUNT);
            poseArm(this.rightShoulder, this.rightArm, this.rightArmRingA, this.rightArmRingB, leftLegPitch, -sway + ARM_SWAY_AMOUNT);
        }
    }

    private static void poseArm(ModelPart shoulder, ModelPart arm, ModelPart ringA, ModelPart ringB, float pitch, float roll) {
        shoulder.xRot = pitch;
        shoulder.zRot = roll;
        arm.xRot = pitch;
        arm.zRot = roll;
        ringA.xRot = pitch;
        ringA.zRot = roll;
        ringB.xRot = pitch;
        ringB.zRot = roll;
    }
}