package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCEntEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelEnt}. Every part pivots on its own absolute point, as in the original. */
public class MoCEntModel<T extends MoCEntEntity> extends HierarchicalModel<T> {

    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 256;

    private static final float BODY_PIVOT_Y = -31.0F;
    private static final float ARM_PIVOT_Y = -42.0F;
    private static final float LEG_PIVOT_Y = -21.0F;
    private static final float HEAD_PIVOT_Y = -44.0F;
    private static final float ARM_PIVOT_X = 10.0F;
    private static final float ARM_PIVOT_Z = 1.0F;

    private static final float SHOULDER_ROLL = 0.1745329F;
    private static final float FOOT_PITCH = 0.2617994F;
    private static final float NECK_PITCH = 0.5235988F;
    private static final float NOSE_PITCH = -0.122173F;

    private static final float ARM_REST_ROLL = 10.0F * Mth.DEG_TO_RAD;
    private static final float FOOT_REST_PITCH = 15.0F * Mth.DEG_TO_RAD;
    private static final float WALK_FREQUENCY = ModelAnimations.WALK_FREQUENCY;
    private static final float WRIST_SWAY_SPEED = 0.09F;
    private static final float WRIST_SWAY_AMOUNT = 0.05F;

    private static final int LEAF_SIZE = 16;
    /** Origin {x, y, z} of each 16x16x16 leaf cube, around the head pivot. */
    private static final float[][] LEAF_ORIGINS = {
            {-16F, -45F, -17F}, {0F, -45F, -17F}, {0F, -45F, -1F}, {-16F, -45F, -1F},
            {-16F, -45F, -33F}, {0F, -45F, -33F}, {16F, -45F, -17F}, {16F, -45F, -1F},
            {0F, -45F, 15F}, {-16F, -45F, 15F}, {-32F, -45F, -1F}, {-32F, -45F, -17F},
            {-16F, -61F, -17F}, {0F, -61F, -17F}, {0F, -61F, -1F}, {-16F, -61F, -1F}
    };
    private static final String[] HEAD_PART_NAMES = {"face", "head", "nose", "mouth", "tree_base"};

    private final ModelPart root;
    private final ModelPart leftArm;
    private final ModelPart leftWrist;
    private final ModelPart leftHand;
    private final ModelPart leftFingers;
    private final ModelPart rightArm;
    private final ModelPart rightWrist;
    private final ModelPart rightHand;
    private final ModelPart rightFingers;
    private final ModelPart leftLeg;
    private final ModelPart leftThigh;
    private final ModelPart leftKnee;
    private final ModelPart leftAnkle;
    private final ModelPart leftFoot;
    private final ModelPart rightLeg;
    private final ModelPart rightThigh;
    private final ModelPart rightKnee;
    private final ModelPart rightAnkle;
    private final ModelPart rightFoot;
    private final ModelPart neck;
    /** Everything that turns with the neck: face, head, nose, mouth, tree base and all leaves. */
    private final ModelPart[] headParts;

    public MoCEntModel(ModelPart root) {
        this.root = root;
        this.leftArm = root.getChild("left_arm");
        this.leftWrist = root.getChild("left_wrist");
        this.leftHand = root.getChild("left_hand");
        this.leftFingers = root.getChild("left_fingers");
        this.rightArm = root.getChild("right_arm");
        this.rightWrist = root.getChild("right_wrist");
        this.rightHand = root.getChild("right_hand");
        this.rightFingers = root.getChild("right_fingers");
        this.leftLeg = root.getChild("left_leg");
        this.leftThigh = root.getChild("left_thigh");
        this.leftKnee = root.getChild("left_knee");
        this.leftAnkle = root.getChild("left_ankle");
        this.leftFoot = root.getChild("left_foot");
        this.rightLeg = root.getChild("right_leg");
        this.rightThigh = root.getChild("right_thigh");
        this.rightKnee = root.getChild("right_knee");
        this.rightAnkle = root.getChild("right_ankle");
        this.rightFoot = root.getChild("right_foot");
        this.neck = root.getChild("neck");

        this.headParts = new ModelPart[HEAD_PART_NAMES.length + LEAF_ORIGINS.length];
        for (int i = 0; i < HEAD_PART_NAMES.length; i++) {
            this.headParts[i] = root.getChild(HEAD_PART_NAMES[i]);
        }
        for (int i = 0; i < LEAF_ORIGINS.length; i++) {
            this.headParts[HEAD_PART_NAMES.length + i] = root.getChild(leafName(i));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartPose bodyPivot = PartPose.offset(0.0F, BODY_PIVOT_Y, 0.0F);
        PartPose leftArmPivot = PartPose.offset(ARM_PIVOT_X, ARM_PIVOT_Y, ARM_PIVOT_Z);
        PartPose rightArmPivot = PartPose.offset(-ARM_PIVOT_X, ARM_PIVOT_Y, ARM_PIVOT_Z);
        PartPose legPivot = PartPose.offset(0.0F, LEG_PIVOT_Y, 0.0F);
        PartPose headPivot = PartPose.offset(0.0F, HEAD_PIVOT_Y, 0.0F);

        // Torso and shoulders
        addBox(root, "body", 68, 36, -7.5F, -12.5F, -4.5F, 15, 25, 9, bodyPivot);
        addBox(root, "left_shoulder", 48, 108, 6.0F, -14.0F, -4.8F, 9, 7, 7,
                PartPose.offsetAndRotation(0.0F, BODY_PIVOT_Y, 0.0F, 0.0F, 0.0F, -SHOULDER_ROLL));
        addBox(root, "right_shoulder", 48, 122, -15.0F, -14.0F, -4.8F, 9, 7, 7,
                PartPose.offsetAndRotation(0.0F, BODY_PIVOT_Y, 0.0F, 0.0F, 0.0F, SHOULDER_ROLL));

        // Left arm
        addBox(root, "left_arm", 80, 108, 0.0F, -4.0F, -5.0F, 6, 24, 6,
                PartPose.offsetAndRotation(ARM_PIVOT_X, ARM_PIVOT_Y, ARM_PIVOT_Z, 0.0F, 0.0F, -SHOULDER_ROLL));
        addBox(root, "left_wrist", 0, 169, 2.0F, 17.0F, -6.0F, 8, 15, 8, leftArmPivot);
        addBox(root, "left_hand", 88, 241, 1.0F, 28.0F, -7.0F, 10, 5, 10, leftArmPivot);
        addBox(root, "left_fingers", 88, 176, 1.0F, 33.0F, -7.0F, 10, 15, 10, leftArmPivot);

        // Right arm
        addBox(root, "right_arm", 104, 108, -6.0F, -4.0F, -5.0F, 6, 24, 6,
                PartPose.offsetAndRotation(-ARM_PIVOT_X, ARM_PIVOT_Y, ARM_PIVOT_Z, 0.0F, 0.0F, SHOULDER_ROLL));
        addBox(root, "right_wrist", 32, 169, -10.0F, 17.0F, -6.0F, 8, 15, 8, rightArmPivot);
        addBox(root, "right_hand", 88, 226, -11.0F, 28.0F, -7.0F, 10, 5, 10, rightArmPivot);
        addBox(root, "right_fingers", 88, 201, -11.0F, 33.0F, -7.0F, 10, 15, 10, rightArmPivot);

        // Left leg
        addBox(root, "left_leg", 0, 90, 3.0F, 0.0F, -3.0F, 6, 20, 6, legPivot);
        addBox(root, "left_thigh", 24, 64, 2.5F, 4.0F, -3.5F, 7, 12, 7, legPivot);
        addBox(root, "left_knee", 0, 0, 2.0F, 20.0F, -4.0F, 8, 24, 8, legPivot);
        addBox(root, "left_ankle", 32, 29, 1.5F, 25.0F, -4.5F, 9, 20, 9, legPivot);
        addBox(root, "left_foot", 0, 206, 1.5F, 38.0F, -23.5F, 9, 5, 9,
                PartPose.offsetAndRotation(0.0F, LEG_PIVOT_Y, 0.0F, FOOT_PITCH, 0.0F, 0.0F));

        // Right leg
        addBox(root, "right_leg", 0, 64, -9.0F, 0.0F, -3.0F, 6, 20, 6, legPivot);
        addBox(root, "right_thigh", 24, 83, -9.5F, 4.0F, -3.5F, 7, 12, 7, legPivot);
        addBox(root, "right_knee", 0, 32, -10.0F, 20.0F, -4.0F, 8, 24, 8, legPivot);
        addBox(root, "right_ankle", 32, 0, -10.5F, 25.0F, -4.5F, 9, 20, 9, legPivot);
        addBox(root, "right_foot", 0, 192, -10.5F, 38.0F, -23.5F, 9, 5, 9,
                PartPose.offsetAndRotation(0.0F, LEG_PIVOT_Y, 0.0F, FOOT_PITCH, 0.0F, 0.0F));

        // Neck and head
        addBox(root, "neck", 52, 90, -4.0F, -8.0F, -5.8F, 8, 10, 8,
                PartPose.offsetAndRotation(0.0F, HEAD_PIVOT_Y, 0.0F, NECK_PITCH, 0.0F, 0.0F));
        addBox(root, "face", 52, 70, -4.5F, -11.0F, -9.0F, 9, 7, 8, headPivot);
        addBox(root, "head", 84, 88, -6.0F, -20.5F, -9.5F, 12, 10, 10, headPivot);
        addBox(root, "nose", 82, 88, -1.5F, -12.0F, -12.0F, 3, 7, 3,
                PartPose.offsetAndRotation(0.0F, HEAD_PIVOT_Y, 0.0F, NOSE_PITCH, 0.0F, 0.0F));
        addBox(root, "mouth", 77, 36, -3.0F, -8.0F, -6.8F, 6, 2, 1,
                PartPose.offsetAndRotation(0.0F, HEAD_PIVOT_Y, 0.0F, NECK_PITCH, 0.0F, 0.0F));
        addBox(root, "tree_base", 0, 136, -10.0F, -31.5F, -11.5F, 20, 13, 20, headPivot);

        // Canopy
        for (int i = 0; i < LEAF_ORIGINS.length; i++) {
            float[] origin = LEAF_ORIGINS[i];
            addBox(root, leafName(i), 0, 224, origin[0], origin[1], origin[2], LEAF_SIZE, LEAF_SIZE, LEAF_SIZE, headPivot);
        }

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static void addBox(PartDefinition root, String name, int texU, int texV,
                               float x, float y, float z, int width, int height, int depth, PartPose pose) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(texU, texV).addBox(x, y, z, width, height, depth), pose);
    }

    private static String leafName(int index) {
        return "leaf_" + (index + 1);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float walkPhase = limbSwing * WALK_FREQUENCY;
        float leftArmPitch = Mth.cos(walkPhase) * limbSwingAmount;
        float rightArmPitch = Mth.cos(walkPhase + Mth.PI) * limbSwingAmount;
        float leftLegPitch = Mth.cos(walkPhase + Mth.PI) * limbSwingAmount;
        float rightLegPitch = Mth.cos(walkPhase) * limbSwingAmount;

        float wristSway = Mth.cos(ageInTicks * WRIST_SWAY_SPEED) * WRIST_SWAY_AMOUNT;
        poseArm(this.leftArm, this.leftWrist, this.leftHand, this.leftFingers,
                leftArmPitch, wristSway - WRIST_SWAY_AMOUNT, -ARM_REST_ROLL);
        poseArm(this.rightArm, this.rightWrist, this.rightHand, this.rightFingers,
                rightArmPitch, -wristSway + WRIST_SWAY_AMOUNT, ARM_REST_ROLL);

        poseLeg(this.leftLeg, this.leftThigh, this.leftKnee, this.leftAnkle, this.leftFoot, leftLegPitch);
        poseLeg(this.rightLeg, this.rightThigh, this.rightKnee, this.rightAnkle, this.rightFoot, rightLegPitch);

        float headYaw = netHeadYaw * Mth.DEG_TO_RAD;
        this.neck.yRot = headYaw;
        for (ModelPart part : this.headParts) {
            part.yRot = headYaw;
        }
    }

    private static void poseArm(ModelPart arm, ModelPart wrist, ModelPart hand, ModelPart fingers,
                                float pitch, float wristRoll, float armRestRoll) {
        wrist.xRot = pitch;
        wrist.zRot = wristRoll;
        hand.xRot = pitch;
        hand.zRot = wristRoll;
        fingers.xRot = pitch;
        fingers.zRot = wristRoll;
        arm.xRot = pitch;
        arm.zRot = armRestRoll + wristRoll;
    }

    private static void poseLeg(ModelPart leg, ModelPart thigh, ModelPart knee, ModelPart ankle, ModelPart foot, float pitch) {
        leg.xRot = pitch;
        thigh.xRot = pitch;
        knee.xRot = pitch;
        ankle.xRot = pitch;
        foot.xRot = FOOT_REST_PITCH + pitch;
    }
}