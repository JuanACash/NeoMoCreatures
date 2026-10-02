package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBigCatEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelAbstractBigCat (Techne)
 * to HierarchicalModel. Step 3 adds the tamed collar/medallion on top of the
 * mane from step 2. Still no saddle/harness, storage chest, stinger tail, or
 * wings — those come in later steps.
 */
public class MoCBigCatModel extends HierarchicalModel<MoCBigCatEntity> {

    private static final float R = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    private final ModelPart chest;
    private final ModelPart abdomen;
    private final ModelPart neckBase;
    private final ModelPart headBack;
    private final ModelPart head;
    private final ModelPart lowerJaw;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart tailRoot;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tailTip;
    private final ModelPart tailTusk;
    private final ModelPart leftUpperLeg;
    private final ModelPart leftLowerLeg;
    private final ModelPart rightUpperLeg;
    private final ModelPart rightLowerLeg;
    private final ModelPart leftHindUpperLeg;
    private final ModelPart leftHindLowerLeg;
    private final ModelPart leftHindFoot;
    private final ModelPart rightHindUpperLeg;
    private final ModelPart rightHindLowerLeg;
    private final ModelPart rightHindFoot;
    private final ModelPart mane;
    private final ModelPart chinHair;
    private final ModelPart leftChinBeard;
    private final ModelPart rightChinBeard;
    private final ModelPart foreheadHair;
    private final ModelPart neckHair;
    private final ModelPart collar;
    private final ModelPart saddle;
    private final ModelPart saddleFront;
    private final ModelPart saddleBack;
    private final ModelPart leftFootHarness;
    private final ModelPart leftFootRing;
    private final ModelPart rightFootHarness;
    private final ModelPart rightFootRing;
    private final ModelPart storageChest;
    private final ModelPart innerWing;
    private final ModelPart midWing;
    private final ModelPart outerWing;
    private final ModelPart innerWingR;
    private final ModelPart midWingR;
    private final ModelPart outerWingR;
    private final ModelPart leftClaw1;
    private final ModelPart leftClaw2;
    private final ModelPart leftClaw3;
    private final ModelPart rightClaw1;
    private final ModelPart rightClaw2;
    private final ModelPart rightClaw3;
    private final ModelPart neckHarness;
    private final ModelPart harnessStick;
    private final ModelPart leftHarness;
    private final ModelPart rightHarness;

    public MoCBigCatModel(ModelPart root) {
        this.root = root;
        this.chest = root.getChild("chest");
        this.neckBase = chest.getChild("neck_base");
        this.headBack = neckBase.getChild("head_back");
        this.neckHarness = headBack.getChild("neck_harness");
        this.harnessStick = headBack.getChild("harness_stick");
        this.leftHarness = root.getChild("left_harness");
        this.rightHarness = root.getChild("right_harness");
        this.head = headBack.getChild("head");
        this.lowerJaw = head.getChild("lower_jaw");
        this.leftEar = head.getChild("left_ear");
        this.rightEar = head.getChild("right_ear");
        this.abdomen = chest.getChild("abdomen");
        this.tailRoot = abdomen.getChild("tail_root");
        this.tail2 = tailRoot.getChild("tail_2");
        this.tail3 = tail2.getChild("tail_3");
        this.tail4 = tail3.getChild("tail_4");
        this.tailTip = tail4.getChild("tail_tip");
        this.tailTusk = tail4.getChild("tail_tusk");
        this.leftUpperLeg = chest.getChild("left_upper_leg");
        this.leftLowerLeg = leftUpperLeg.getChild("left_lower_leg");
        this.rightUpperLeg = chest.getChild("right_upper_leg");
        this.rightLowerLeg = rightUpperLeg.getChild("right_lower_leg");
        this.leftHindUpperLeg = abdomen.getChild("left_hind_upper_leg");
        this.leftHindLowerLeg = leftHindUpperLeg.getChild("left_ankle").getChild("left_hind_lower_leg");
        this.leftHindFoot = leftHindLowerLeg.getChild("left_hind_foot");
        this.rightHindUpperLeg = abdomen.getChild("right_hind_upper_leg");
        this.rightHindLowerLeg = rightHindUpperLeg.getChild("right_ankle").getChild("right_hind_lower_leg");
        this.rightHindFoot = rightHindLowerLeg.getChild("right_hind_foot");
        this.mane = head.getChild("mane");
        this.chinHair = lowerJaw.getChild("chin_hair");
        this.leftChinBeard = head.getChild("left_chin_beard");
        this.rightChinBeard = head.getChild("right_chin_beard");
        this.foreheadHair = head.getChild("forehead_hair");
        this.neckHair = neckBase.getChild("neck_hair");
        this.collar = neckBase.getChild("collar");
        this.saddle = chest.getChild("saddle");
        this.saddleFront = saddle.getChild("saddle_front");
        this.saddleBack = saddle.getChild("saddle_back");
        this.leftFootHarness = saddle.getChild("left_foot_harness");
        this.leftFootRing = leftFootHarness.getChild("left_foot_ring");
        this.rightFootHarness = saddle.getChild("right_foot_harness");
        this.rightFootRing = rightFootHarness.getChild("right_foot_ring");
        this.storageChest = abdomen.getChild("storage_chest");
        this.innerWing = root.getChild("inner_wing");
        this.midWing = root.getChild("mid_wing");
        this.outerWing = root.getChild("outer_wing");
        this.innerWingR = root.getChild("inner_wing_r");
        this.midWingR = root.getChild("mid_wing_r");
        this.outerWingR = root.getChild("outer_wing_r");
        ModelPart leftFrontFoot = leftLowerLeg.getChild("left_front_foot");
        this.leftClaw1 = leftFrontFoot.getChild("left_claw_1");
        this.leftClaw2 = leftFrontFoot.getChild("left_claw_2");
        this.leftClaw3 = leftFrontFoot.getChild("left_claw_3");
        ModelPart rightFrontFoot = rightLowerLeg.getChild("right_front_foot");
        this.rightClaw1 = rightFrontFoot.getChild("right_claw_1");
        this.rightClaw2 = rightFrontFoot.getChild("right_claw_2");
        this.rightClaw3 = rightFrontFoot.getChild("right_claw_3");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition chest = root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, 0F, -8F, 7, 8, 9),
                PartPose.offset(0F, 8F, 0F));

        PartDefinition neckBase = chest.addOrReplaceChild("neck_base",
                CubeListBuilder.create().texOffs(0, 7).addBox(-2.5F, 0F, -2.5F, 5, 6, 5),
                PartPose.offsetAndRotation(0F, -0.5F, -8F, -14F / R, 0F, 0F));

        PartDefinition headBack = neckBase.addOrReplaceChild("head_back",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.51F, -2.5F, -1F, 5, 5, 2),
                PartPose.offsetAndRotation(0F, 2.7F, -2.9F, 14F / R, 0F, 0F));

        headBack.addOrReplaceChild("neck_harness",
                CubeListBuilder.create().texOffs(85, 32).addBox(-3F, -3F, -2F, 6, 6, 2),
                PartPose.offset(0F, 0F, 0.95F));
        headBack.addOrReplaceChild("harness_stick",
                CubeListBuilder.create().texOffs(85, 42).addBox(-3.5F, -0.5F, -0.5F, 7, 1, 1),
                PartPose.offsetAndRotation(0F, -1.8F, 0.5F, 45F / R, 0F, 0F));
        root.addOrReplaceChild("left_harness",
                CubeListBuilder.create().texOffs(85, 32).addBox(3.2F, -0.6F, 1.5F, 0, 1, 9),
                PartPose.offsetAndRotation(0F, 8.6F, -13F, 25F / R, 0F, 0F));
        root.addOrReplaceChild("right_harness",
                CubeListBuilder.create().texOffs(85, 31).addBox(-3.2F, -0.6F, 1.5F, 0, 1, 9),
                PartPose.offsetAndRotation(0F, 8.6F, -13F, 25F / R, 0F, 0F));

        neckBase.addOrReplaceChild("neck_hair",
                CubeListBuilder.create().texOffs(108, 17).addBox(-2F, -1F, -3F, 4, 2, 6),
                PartPose.offsetAndRotation(0F, -0.5F, 3F, -10.6F / R, 0F, 0F));

        neckBase.addOrReplaceChild("collar",
                CubeListBuilder.create().texOffs(18, 0).addBox(-2.5F, 0F, 0F, 5, 4, 1),
                PartPose.offsetAndRotation(0F, 6F, -2F, 20F / R, 0F, 0F));

        PartDefinition head = headBack.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(32, 0).addBox(-3.5F, -3F, -2F, 7, 6, 4),
                PartPose.offset(0F, 0.2F, -2.2F));

        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(46, 19).addBox(-1.5F, -1F, -2F, 3, 2, 4),
                PartPose.offsetAndRotation(0F, 0F, -3F, 27F / R, 0F, 0F));

        head.addOrReplaceChild("right_upper_lip",
                CubeListBuilder.create().texOffs(34, 19).addBox(-1F, -1F, -2F, 2, 2, 4),
                PartPose.offsetAndRotation(-1.25F, 1F, -2.8F, 10F / R, 2F / R, -15F / R));

        head.addOrReplaceChild("left_upper_lip",
                CubeListBuilder.create().texOffs(34, 25).addBox(-1F, -1F, -2F, 2, 2, 4),
                PartPose.offsetAndRotation(1.25F, 1F, -2.8F, 10F / R, -2F / R, 15F / R));

        head.addOrReplaceChild("upper_teeth",
                CubeListBuilder.create().texOffs(20, 7).addBox(-1.5F, -1F, -1.5F, 3, 2, 3),
                PartPose.offsetAndRotation(0F, 2F, -2.5F, 15F / R, 0F, 0F));

        head.addOrReplaceChild("inside_mouth",
                CubeListBuilder.create().texOffs(50, 0).addBox(-1.5F, -1F, -1F, 3, 2, 2),
                PartPose.offset(0F, 2F, -1F));

        PartDefinition lowerJaw = head.addOrReplaceChild("lower_jaw",
                CubeListBuilder.create().texOffs(46, 25).addBox(-1.5F, -1F, -4F, 3, 2, 4),
                PartPose.offset(0F, 2.1F, 0F));

        lowerJaw.addOrReplaceChild("lower_jaw_teeth",
                CubeListBuilder.create().texOffs(20, 12).mirror().addBox(-1F, 0F, -1F, 2, 1, 2),
                PartPose.offset(0F, -1.8F, -2.7F));

        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(54, 7).addBox(-1F, -1F, -0.5F, 2, 2, 1),
                PartPose.offsetAndRotation(-2.7F, -3.5F, 1F, 0F, 0F, -15F / R));

        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(54, 4).addBox(-1F, -1F, -0.5F, 2, 2, 1),
                PartPose.offsetAndRotation(2.7F, -3.5F, 1F, 0F, 0F, 15F / R));

        // Mane and related hair — only shown for maned adult lions (see setupAnim's hasMane check).
        head.addOrReplaceChild("mane",
                CubeListBuilder.create().texOffs(94, 0).addBox(-5.5F, -5.5F, -3F, 11, 11, 6),
                PartPose.offsetAndRotation(0F, 0.7F, 3.7F, -5F / R, 0F, 0F));

        head.addOrReplaceChild("left_chin_beard",
                CubeListBuilder.create().texOffs(48, 10).addBox(-1F, -2.5F, -2F, 2, 5, 4),
                PartPose.offsetAndRotation(3.6F, 0F, 0.25F, 0F, 30F / R, 0F));

        head.addOrReplaceChild("right_chin_beard",
                CubeListBuilder.create().texOffs(36, 10).addBox(-1F, -2.5F, -2F, 2, 5, 4),
                PartPose.offsetAndRotation(-3.6F, 0F, 0.25F, 0F, -30F / R, 0F));

        head.addOrReplaceChild("forehead_hair",
                CubeListBuilder.create().texOffs(88, 0).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3),
                PartPose.offsetAndRotation(0F, -3.2F, 0F, 10F / R, 0F, 0F));

        lowerJaw.addOrReplaceChild("chin_hair",
                CubeListBuilder.create().texOffs(76, 7).addBox(-2.5F, 0F, -2F, 5, 6, 4),
                PartPose.offset(0F, 0F, 1F));

        PartDefinition abdomen = chest.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(0, 35).addBox(-3F, 0F, 0F, 6, 7, 7),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.0523599F, 0F, 0F));

        abdomen.addOrReplaceChild("ass",
                CubeListBuilder.create().texOffs(0, 49).addBox(-2.5F, 0F, 0F, 5, 5, 3),
                PartPose.offsetAndRotation(0F, 0F, 7F, -20F / R, 0F, 0F));

        PartDefinition tailRoot = abdomen.addOrReplaceChild("tail_root",
                CubeListBuilder.create().texOffs(96, 83).addBox(-1F, 0F, -1F, 2, 4, 2),
                PartPose.offsetAndRotation(0F, 1F, 7F, 87F / R, 0F, 0F));
        PartDefinition tail2 = tailRoot.addOrReplaceChild("tail_2",
                CubeListBuilder.create().texOffs(96, 75).addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offsetAndRotation(-0.01F, 3.5F, 0F, -30F / R, 0F, 0F));
        PartDefinition tail3 = tail2.addOrReplaceChild("tail_3",
                CubeListBuilder.create().texOffs(96, 67).addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offsetAndRotation(0.01F, 5.5F, 0F, -17F / R, 0F, 0F));
        PartDefinition tail4 = tail3.addOrReplaceChild("tail_4",
                CubeListBuilder.create().texOffs(96, 61).addBox(-1F, 0F, -1F, 2, 4, 2),
                PartPose.offsetAndRotation(-0.01F, 5.5F, 0F, 21F / R, 0F, 0F));
        tail4.addOrReplaceChild("tail_tip",
                CubeListBuilder.create().texOffs(96, 55).addBox(-1F, 0F, -1F, 2, 4, 2),
                PartPose.offsetAndRotation(0.01F, 3.5F, 0F, 21F / R, 0F, 0F));
        tail4.addOrReplaceChild("tail_tusk",
                CubeListBuilder.create().texOffs(96, 49).addBox(-1.5F, 0F, -1.5F, 3, 3, 3),
                PartPose.offsetAndRotation(0F, 3.5F, 0F, 21F / R, 0F, 0F));

        PartDefinition leftUpperLeg = chest.addOrReplaceChild("left_upper_leg",
                CubeListBuilder.create().texOffs(0, 96).addBox(-1.5F, 0F, -2F, 3, 7, 4),
                PartPose.offsetAndRotation(3.99F, 3F, -7F, 15F / R, 0F, 0F));
        PartDefinition leftLowerLeg = leftUpperLeg.addOrReplaceChild("left_lower_leg",
                CubeListBuilder.create().texOffs(0, 107).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offsetAndRotation(-0.01F, 6.5F, 0.2F, -21.5F / R, 0F, 0F));
        PartDefinition leftFrontFoot = leftLowerLeg.addOrReplaceChild("left_front_foot",
                CubeListBuilder.create().texOffs(0, 116).addBox(-2F, 0F, -2F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 5F, -1.0F, 6.5F / R, 0F, 0F));

        leftFrontFoot.addOrReplaceChild("left_claw_1",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(-1.3F, 1.2F, -3.0F, 45F / R, 0F, -1F / R));
        leftFrontFoot.addOrReplaceChild("left_claw_2",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(0F, 1.1F, -3F, 45F / R, 0F, 0F));
        leftFrontFoot.addOrReplaceChild("left_claw_3",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(1.3F, 1.2F, -3F, 45F / R, 0F, 1F / R));

        PartDefinition rightUpperLeg = chest.addOrReplaceChild("right_upper_leg",
                CubeListBuilder.create().texOffs(14, 96).addBox(-1.5F, 0F, -2F, 3, 7, 4),
                PartPose.offsetAndRotation(-3.99F, 3F, -7F, 15F / R, 0F, 0F));
        PartDefinition rightLowerLeg = rightUpperLeg.addOrReplaceChild("right_lower_leg",
                CubeListBuilder.create().texOffs(12, 107).addBox(-1.5F, 0F, -1.5F, 3, 6, 3),
                PartPose.offsetAndRotation(0.01F, 6.5F, 0.2F, -21.5F / R, 0F, 0F));
        PartDefinition rightFrontFoot = rightLowerLeg.addOrReplaceChild("right_front_foot",
                CubeListBuilder.create().texOffs(0, 122).addBox(-2F, 0F, -2F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 5F, -1.0F, 6.5F / R, 0F, 0F));

        rightFrontFoot.addOrReplaceChild("right_claw_1",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(-1.3F, 1.2F, -3.0F, 45F / R, 0F, -1F / R));
        rightFrontFoot.addOrReplaceChild("right_claw_2",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(0F, 1.1F, -3F, 45F / R, 0F, 0F));
        rightFrontFoot.addOrReplaceChild("right_claw_3",
                CubeListBuilder.create().texOffs(16, 125).addBox(-0.5F, 0F, -0.5F, 1, 1, 2),
                PartPose.offsetAndRotation(1.3F, 1.2F, -3F, 45F / R, 0F, 1F / R));

        PartDefinition leftHindUpperLeg = abdomen.addOrReplaceChild("left_hind_upper_leg",
                CubeListBuilder.create().texOffs(0, 67).addBox(-2F, -1.0F, -1.5F, 3, 8, 5),
                PartPose.offsetAndRotation(3F, 3F, 6.8F, -25F / R, 0F, 0F));
        PartDefinition leftAnkle = leftHindUpperLeg.addOrReplaceChild("left_ankle",
                CubeListBuilder.create().texOffs(0, 80).addBox(-1F, 0F, -1.5F, 2, 3, 3),
                PartPose.offset(-0.5F, 4F, 5F));
        PartDefinition leftHindLowerLeg = leftAnkle.addOrReplaceChild("left_hind_lower_leg",
                CubeListBuilder.create().texOffs(0, 86).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(0F, 3F, 0.5F));
        leftHindLowerLeg.addOrReplaceChild("left_hind_foot",
                CubeListBuilder.create().texOffs(0, 91).addBox(-1.5F, 0F, -1.5F, 3, 2, 3),
                PartPose.offsetAndRotation(0F, 2.6F, -0.8F, 27F / R, 0F, 0F));

        PartDefinition rightHindUpperLeg = abdomen.addOrReplaceChild("right_hind_upper_leg",
                CubeListBuilder.create().texOffs(16, 67).addBox(-2F, -1F, -1.5F, 3, 8, 5),
                PartPose.offsetAndRotation(-2F, 3F, 6.8F, -25F / R, 0F, 0F));
        PartDefinition rightAnkle = rightHindUpperLeg.addOrReplaceChild("right_ankle",
                CubeListBuilder.create().texOffs(10, 80).addBox(-1F, 0F, -1.5F, 2, 3, 3),
                PartPose.offset(-0.5F, 4F, 5F));
        PartDefinition rightHindLowerLeg = rightAnkle.addOrReplaceChild("right_hind_lower_leg",
                CubeListBuilder.create().texOffs(8, 86).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(0F, 3F, 0.5F));
        rightHindLowerLeg.addOrReplaceChild("right_hind_foot",
                CubeListBuilder.create().texOffs(12, 91).addBox(-1.5F, 0F, -1.5F, 3, 2, 3),
                PartPose.offsetAndRotation(0F, 2.6F, -0.8F, 27F / R, 0F, 0F));

        PartDefinition saddle = chest.addOrReplaceChild("saddle",
                CubeListBuilder.create().texOffs(79, 18).addBox(-4F, -1F, -3F, 8, 2, 6),
                PartPose.offset(0F, 0.5F, -1F));

        saddle.addOrReplaceChild("saddle_front",
                CubeListBuilder.create().texOffs(101, 26).addBox(-2.5F, -1F, -1.5F, 5, 2, 3),
                PartPose.offsetAndRotation(0F, -1.0F, -1.5F, -10.6F / R, 0F, 0F));

        saddle.addOrReplaceChild("saddle_back",
                CubeListBuilder.create().texOffs(77, 26).addBox(-4F, -2F, -2F, 8, 2, 4),
                PartPose.offsetAndRotation(0F, 0.7F, 4F, 12.78F / R, 0F, 0F));

        PartDefinition leftFootHarness = saddle.addOrReplaceChild("left_foot_harness",
                CubeListBuilder.create().texOffs(81, 18).addBox(-0.5F, 0F, -0.5F, 1, 5, 1),
                PartPose.offset(4F, 0F, 0.5F));
        leftFootHarness.addOrReplaceChild("left_foot_ring",
                CubeListBuilder.create().texOffs(107, 31).addBox(0F, 0F, 0F, 1, 2, 2),
                PartPose.offset(-0.5F, 5F, -1F));

        PartDefinition rightFootHarness = saddle.addOrReplaceChild("right_foot_harness",
                CubeListBuilder.create().texOffs(101, 18).addBox(-0.5F, 0F, -0.5F, 1, 5, 1),
                PartPose.offset(-4F, 0F, 0.5F));
        rightFootHarness.addOrReplaceChild("right_foot_ring",
                CubeListBuilder.create().texOffs(101, 31).addBox(0F, 0F, 0F, 1, 2, 2),
                PartPose.offset(-0.5F, 5F, -1F));

        abdomen.addOrReplaceChild("storage_chest",
                CubeListBuilder.create().texOffs(32, 59).addBox(-5F, -2F, -2.5F, 10, 4, 5),
                PartPose.offsetAndRotation(0F, -2F, 5.5F, -90F / R, 0F, 0F));

        root.addOrReplaceChild("inner_wing",
                CubeListBuilder.create().texOffs(26, 115).addBox(0F, 0F, 0F, 7, 2, 11),
                PartPose.offsetAndRotation(4F, 9F, -7F, 0F, -20F / R, 0F));
        root.addOrReplaceChild("mid_wing",
                CubeListBuilder.create().texOffs(36, 89).addBox(1F, 0.1F, 1F, 12, 2, 11),
                PartPose.offsetAndRotation(4F, 9F, -7F, 0F, 5F / R, 0F));
        root.addOrReplaceChild("outer_wing",
                CubeListBuilder.create().texOffs(62, 115).addBox(0F, 0F, 0F, 22, 2, 11),
                PartPose.offsetAndRotation(16F, 9F, -7F, 0F, -18F / R, 0F));
        root.addOrReplaceChild("inner_wing_r",
                CubeListBuilder.create().texOffs(26, 102).addBox(-7F, 0F, 0F, 7, 2, 11),
                PartPose.offsetAndRotation(-4F, 9F, -7F, 0F, 20F / R, 0F));
        root.addOrReplaceChild("mid_wing_r",
                CubeListBuilder.create().texOffs(82, 89).addBox(-13F, 0.1F, 1F, 12, 2, 11),
                PartPose.offsetAndRotation(-4F, 9F, -7F, 0F, -5F / R, 0F));
        root.addOrReplaceChild("outer_wing_r",
                CubeListBuilder.create().texOffs(62, 102).addBox(-22F, 0F, 0F, 22, 2, 11),
                PartPose.offsetAndRotation(-16F, 9F, -7F, 0F, 18F / R, 0F));

        return LayerDefinition.create(mesh, 128, 128);
        
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCBigCatEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float rLegXRot = Mth.cos((limbSwing * 0.8F) + (float) Math.PI) * 0.8F * limbSwingAmount;
        float lLegXRot = Mth.cos(limbSwing * 0.8F) * 0.8F * limbSwingAmount;
        float gallopRLegXRot = Mth.cos((limbSwing * 0.6F) + (float) Math.PI) * 0.8F * limbSwingAmount;
        float gallopLLegXRot = Mth.cos(limbSwing * 0.6F) * 0.8F * limbSwingAmount;
        boolean galloping = limbSwingAmount >= 0.97F;

        if (galloping) {
            // Both front legs move together, both hind legs move together —
            // matches the original's galloping case exactly (unlike the diagonal
            // walk gait below, where front/hind pairs are diagonally opposite).
            rightUpperLeg.xRot = 15F / R + gallopRLegXRot;
            leftUpperLeg.xRot = 15F / R + gallopRLegXRot;
            rightHindUpperLeg.xRot = -25F / R + gallopLLegXRot;
            leftHindUpperLeg.xRot = -25F / R + gallopLLegXRot;
            abdomen.yRot = 0F;
        } else {
            rightUpperLeg.xRot = 15F / R + rLegXRot;
            leftHindUpperLeg.xRot = -25F / R + rLegXRot;
            leftUpperLeg.xRot = 15F / R + lLegXRot;
            rightHindUpperLeg.xRot = -25F / R + lLegXRot;
            abdomen.yRot = Mth.cos(limbSwing * 0.3F) * 0.25F * limbSwingAmount;
        }
        leftLowerLeg.xRot = -21.5F / R;
        rightLowerLeg.xRot = -21.5F / R;
        leftHindFoot.xRot = 27F / R;
        rightHindFoot.xRot = 27F / R;

        // Idle tail sway, livelier swish while the counter is running.
        int tailTicks = entity.getTailTicks();
        float tailXRot = tailTicks != 0 ? Mth.cos(ageInTicks * 0.3F) * 0.15F : 0F;
        tailRoot.xRot = 87F / R + tailXRot;
        tail2.xRot = -30F / R + tailXRot;
        tail3.xRot = -17F / R + tailXRot;
        tail4.xRot = 21F / R + tailXRot;
        tailTip.xRot = 21F / R + tailXRot;
        tailTusk.xRot = 21F / R + tailXRot;
        tail2.yRot = tailTicks != 0 ? Mth.cos(ageInTicks * 0.3F) : 0F;

        // Head tracking.
        headBack.xRot = 14F / R + headPitch / R;
        headBack.yRot = netHeadYaw / R;

        leftHarness.xRot = 25F / R + headBack.xRot;
        leftHarness.yRot = headBack.yRot;
        rightHarness.xRot = 25F / R + headBack.xRot;
        rightHarness.yRot = headBack.yRot;

        // Mouth open/close for roaring, hurt, and eating.
        int mouthTicks = entity.getMouthTicks();
        float targetMouthAngle;
        if (mouthTicks == 0) {
            targetMouthAngle = 0F;
        } else if (mouthTicks < 10) {
            targetMouthAngle = 22F + mouthTicks * 3F;
        } else if (mouthTicks > 20) {
            targetMouthAngle = 22F + (90F - mouthTicks * 3F);
        } else {
            targetMouthAngle = 55F;
        }
        lowerJaw.xRot = targetMouthAngle / R;

        // Cubs never have a mane yet, even maned species — it comes in with adulthood.
        boolean hasMane = entity.getVariant().hasMane() && !entity.isBaby();
        mane.visible = hasMane;
        chinHair.visible = hasMane;
        leftChinBeard.visible = hasMane;
        rightChinBeard.visible = hasMane;
        foreheadHair.visible = hasMane;
        neckHair.visible = hasMane;

        collar.visible = entity.isTame();
        boolean bigCatSaddled = entity.isSaddled();
        saddle.visible = bigCatSaddled;
        storageChest.visible = entity.hasChest();

        neckHarness.visible = bigCatSaddled;
        harnessStick.visible = bigCatSaddled;
        boolean bigCatRiddenSaddled = bigCatSaddled && entity.isVehicle();
        leftHarness.visible = bigCatRiddenSaddled;
        rightHarness.visible = bigCatRiddenSaddled;

        // Sitting reclines the whole torso back and down — not just folded legs —
        // matching the original exactly (Chest itself moves/rotates, not just its children).
        boolean sitting = entity.isSittingSynced();
        if (sitting) {
        chest.y = 14F;
        chest.xRot = -45F / R;
        abdomen.xRot = -10F / R;
        neckBase.xRot = 20F / R;
        rightUpperLeg.xRot = 35F / R;
        leftUpperLeg.xRot = 35F / R;
        rightLowerLeg.xRot = 5F / R;
        leftLowerLeg.xRot = 5F / R;
        rightHindUpperLeg.y = 1F;
        leftHindUpperLeg.y = 1F;
        rightHindUpperLeg.xRot = -50F / R;
        leftHindUpperLeg.xRot = -50F / R;
        rightHindFoot.xRot = 90F / R;
        leftHindFoot.xRot = 90F / R;
        tailRoot.xRot = 100F / R;
        tail2.xRot = 35F / R;
        tail3.xRot = 10F / R;
        collar.y = 7F;
        collar.z = -4F;
        } else {
                chest.y = 8F;
                chest.xRot = 0F;
                abdomen.xRot = 0F;
                neckBase.xRot = -14F / R;
                rightHindUpperLeg.y = 3F;
                leftHindUpperLeg.y = 3F;
                collar.y = 6F;
                collar.z = -2F;
        }

        // Ghosts float with their legs folded at all times — the exact same pose
        // winged big cats use mid-flight in the original (RightUpperLeg/LeftUpperLeg
        // 45°, hind legs 10°, both nudged further by movement speed).
        boolean foldedLegs = entity.isGhost() || (entity.hasWings() && entity.getIsFlying());
        if (foldedLegs && !sitting) {
                float speedMov = limbSwingAmount * 0.5F;
                rightUpperLeg.xRot = 45F / R + speedMov;
                leftUpperLeg.xRot = 45F / R + speedMov;
                rightHindUpperLeg.xRot = 10F / R + speedMov;
                leftHindUpperLeg.xRot = 10F / R + speedMov;
        }
        boolean winged = entity.hasWings();
        innerWing.visible = winged;
        midWing.visible = winged;
        outerWing.visible = winged;
        innerWingR.visible = winged;
        midWingR.visible = winged;
        outerWingR.visible = winged;

        if (winged) {
                boolean flying = entity.getIsFlying();
                float wingRot;
                if (flying) {
                wingRot = entity.isAirborneFlapping()
                        ? Mth.cos((ageInTicks * 0.3F) + (float) Math.PI) * 1.2F
                        : 0.1F; // gliding: fully still, fully extended — no oscillation at all
                outerWing.yRot = -0.3228859F + (wingRot / 2F);
                outerWingR.yRot = 0.3228859F - (wingRot / 2F);
                } else {
                // Folded at rest — same fixed pose a pegasus/bathorse uses on the ground.
                wingRot = 60F / R;
                outerWing.yRot = -90F / R;
                outerWingR.yRot = 90F / R;
                }

                innerWingR.y = innerWing.y;
                innerWingR.z = innerWing.z;
                outerWing.x = innerWing.x + (Mth.cos(wingRot) * 12F);
                outerWingR.x = innerWingR.x - (Mth.cos(wingRot) * 12F);

                midWing.y = innerWing.y;
                midWingR.y = innerWing.y;
                outerWing.y = innerWing.y + (Mth.sin(wingRot) * 12F);
                outerWingR.y = innerWingR.y + (Mth.sin(wingRot) * 12F);

                midWing.z = innerWing.z;
                midWingR.z = innerWing.z;
                outerWing.z = innerWing.z;
                outerWingR.z = innerWing.z;

                midWing.zRot = wingRot;
                innerWing.zRot = wingRot;
                outerWing.zRot = wingRot;
                innerWingR.zRot = -wingRot;
                midWingR.zRot = -wingRot;
                outerWingR.zRot = -wingRot;
        }
    }
}