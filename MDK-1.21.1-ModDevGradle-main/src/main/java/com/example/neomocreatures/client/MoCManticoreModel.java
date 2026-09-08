package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCManticoreEntity;
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

public class MoCManticoreModel extends HierarchicalModel<MoCManticoreEntity> {

    private static final float R = 57.29578F;

    private final ModelPart root;
    private final ModelPart chest;
    private final ModelPart abdomen;
    private final ModelPart neckBase;
    private final ModelPart headBack;
    private final ModelPart head;
    private final ModelPart lowerJaw;
    private final ModelPart tailRoot;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tailTip;
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
    private final ModelPart innerWing;
    private final ModelPart midWing;
    private final ModelPart outerWing;
    private final ModelPart innerWingR;
    private final ModelPart midWingR;
    private final ModelPart outerWingR;
    private final ModelPart sTailRoot;
    private final ModelPart sTail2;
    private final ModelPart sTail3;
    private final ModelPart sTail4;
    private final ModelPart sTail5;
    private final ModelPart stingerLump;
    private final ModelPart stinger;
    private final ModelPart mane;
    private final ModelPart chinHair;
    private final ModelPart leftChinBeard;
    private final ModelPart rightChinBeard;
    private final ModelPart foreheadHair;
    private final ModelPart neckHair;
    private final ModelPart leftFang;
    private final ModelPart rightFang;
    private final ModelPart leftClaw1;
    private final ModelPart leftClaw2;
    private final ModelPart leftClaw3;
    private final ModelPart rightClaw1;
    private final ModelPart rightClaw2;
    private final ModelPart rightClaw3;
    private final ModelPart saddle;
    private final ModelPart saddleFront;
    private final ModelPart saddleBack;
    private final ModelPart leftFootHarness;
    private final ModelPart leftFootRing;
    private final ModelPart rightFootHarness;
    private final ModelPart rightFootRing;
    private final ModelPart storageChest;

    public MoCManticoreModel(ModelPart root) {
        this.root = root;
        this.chest = root.getChild("chest");
        this.neckBase = chest.getChild("neck_base");
        this.headBack = neckBase.getChild("head_back");
        this.head = headBack.getChild("head");
        this.lowerJaw = head.getChild("lower_jaw");
        this.abdomen = chest.getChild("abdomen");
        this.tailRoot = abdomen.getChild("tail_root");
        this.tail2 = tailRoot.getChild("tail_2");
        this.tail3 = tail2.getChild("tail_3");
        this.tail4 = tail3.getChild("tail_4");
        this.tailTip = tail4.getChild("tail_tip");
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
        this.innerWing = root.getChild("inner_wing");
        this.midWing = root.getChild("mid_wing");
        this.outerWing = root.getChild("outer_wing");
        this.innerWingR = root.getChild("inner_wing_r");
        this.midWingR = root.getChild("mid_wing_r");
        this.outerWingR = root.getChild("outer_wing_r");
        this.sTailRoot = root.getChild("s_tail_root");
        this.sTail2 = root.getChild("s_tail_2");
        this.sTail3 = root.getChild("s_tail_3");
        this.sTail4 = root.getChild("s_tail_4");
        this.sTail5 = root.getChild("s_tail_5");
        this.stingerLump = root.getChild("stinger_lump");
        this.stinger = root.getChild("stinger");
        this.mane = head.getChild("mane");
        this.chinHair = lowerJaw.getChild("chin_hair");
        this.leftChinBeard = head.getChild("left_chin_beard");
        this.rightChinBeard = head.getChild("right_chin_beard");
        this.foreheadHair = head.getChild("forehead_hair");
        this.neckHair = neckBase.getChild("neck_hair");
        this.leftFang = head.getChild("left_fang");
        this.rightFang = head.getChild("right_fang");
        ModelPart leftFrontFoot = leftLowerLeg.getChild("left_front_foot");
        this.leftClaw1 = leftFrontFoot.getChild("left_claw_1");
        this.leftClaw2 = leftFrontFoot.getChild("left_claw_2");
        this.leftClaw3 = leftFrontFoot.getChild("left_claw_3");
        ModelPart rightFrontFoot = rightLowerLeg.getChild("right_front_foot");
        this.rightClaw1 = rightFrontFoot.getChild("right_claw_1");
        this.rightClaw2 = rightFrontFoot.getChild("right_claw_2");
        this.rightClaw3 = rightFrontFoot.getChild("right_claw_3");
        this.saddle = chest.getChild("saddle");
        this.saddleFront = saddle.getChild("saddle_front");
        this.saddleBack = saddle.getChild("saddle_back");
        this.leftFootHarness = saddle.getChild("left_foot_harness");
        this.leftFootRing = leftFootHarness.getChild("left_foot_ring");
        this.rightFootHarness = saddle.getChild("right_foot_harness");
        this.rightFootRing = rightFootHarness.getChild("right_foot_ring");
        this.storageChest = abdomen.getChild("storage_chest");
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

        neckBase.addOrReplaceChild("neck_hair",
                CubeListBuilder.create().texOffs(108, 17).addBox(-2F, -1F, -3F, 4, 2, 6),
                PartPose.offsetAndRotation(0F, -0.5F, 3F, -10.6F / R, 0F, 0F));

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

        head.addOrReplaceChild("left_fang",
                CubeListBuilder.create().texOffs(44, 10).addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1),
                PartPose.offsetAndRotation(1.2F, 2.8F, -3.4F, 15F / R, 0F, 0F));

        head.addOrReplaceChild("right_fang",
                CubeListBuilder.create().texOffs(48, 10).addBox(-0.5F, -1.5F, -0.5F, 1, 3, 1),
                PartPose.offsetAndRotation(-1.2F, 2.8F, -3.4F, 15F / R, 0F, 0F));

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

        root.addOrReplaceChild("s_tail_root",
                CubeListBuilder.create().texOffs(104, 79).mirror().addBox(-3F, 4F, 5F, 6, 4, 6),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.5796765F, 0F, 0F));
        root.addOrReplaceChild("s_tail_2",
                CubeListBuilder.create().texOffs(106, 69).mirror().addBox(-2.5F, 7.5F, 7.3F, 5, 4, 6),
                PartPose.offsetAndRotation(0F, 8F, 0F, 0.9514626F, 0F, 0F));
        root.addOrReplaceChild("s_tail_3",
                CubeListBuilder.create().texOffs(108, 60).mirror().addBox(-2F, 13.5F, 3.3F, 4, 3, 6),
                PartPose.offsetAndRotation(0F, 8F, 0F, 1.660128F, 0F, 0F));
        root.addOrReplaceChild("s_tail_4",
                CubeListBuilder.create().texOffs(108, 51).mirror().addBox(-2F, 15.2F, -5.3F, 4, 3, 6),
                PartPose.offsetAndRotation(0F, 8F, 0F, 2.478058F, 0F, 0F));
        root.addOrReplaceChild("s_tail_5",
                CubeListBuilder.create().texOffs(108, 42).mirror().addBox(-2F, 12.9F, -9F, 4, 3, 6),
                PartPose.offsetAndRotation(0F, 8F, 0F, 3.035737F, 0F, 0F));
        root.addOrReplaceChild("stinger_lump",
                CubeListBuilder.create().texOffs(112, 34).mirror().addBox(-1.5F, 7.9F, 6F, 3, 3, 5),
                PartPose.offsetAndRotation(0F, 8F, 0F, 2.031914F, 0F, 0F));
        root.addOrReplaceChild("stinger",
                CubeListBuilder.create().texOffs(118, 29).mirror().addBox(-0.5F, 1.9F, 8F, 1, 1, 4),
                PartPose.offsetAndRotation(0F, 8F, 0F, 1.213985F, 0F, 0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCManticoreEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float rLegXRot = Mth.cos((limbSwing * 0.8F) + (float) Math.PI) * 0.8F * limbSwingAmount;
        float lLegXRot = Mth.cos(limbSwing * 0.8F) * 0.8F * limbSwingAmount;
        rightUpperLeg.xRot = 15F / R + rLegXRot;
        leftHindUpperLeg.xRot = -25F / R + rLegXRot;
        leftUpperLeg.xRot = 15F / R + lLegXRot;
        rightHindUpperLeg.xRot = -25F / R + lLegXRot;
        leftLowerLeg.xRot = -21.5F / R;
        rightLowerLeg.xRot = -21.5F / R;
        leftHindFoot.xRot = 27F / R;
        rightHindFoot.xRot = 27F / R;

        int tailTicks = entity.getTailTicks();
        float tailXRot = tailTicks != 0 ? Mth.cos(ageInTicks * 0.3F) * 0.15F : 0F;
        tailRoot.xRot = 87F / R + tailXRot;
        tail2.xRot = -30F / R + tailXRot;
        tail3.xRot = -17F / R + tailXRot;
        tail4.xRot = 21F / R + tailXRot;
        tailTip.xRot = 21F / R + tailXRot;
        tail2.yRot = tailTicks != 0 ? Mth.cos(ageInTicks * 0.3F) : 0F;

        headBack.xRot = 14F / R + headPitch / R;
        headBack.yRot = netHeadYaw / R;

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

        boolean floating = entity.isVehicle() ? !entity.onGround() : entity.isSoaring();
        boolean flapping = entity.isVehicle() ? entity.isAscendHeld() : floating;
        float wingRot;
        if (floating) {
        wingRot = flapping
                ? Mth.cos((ageInTicks * 0.3F) + (float) Math.PI) * 1.2F
                : 0.1F; // extended and still while gliding/descending
        outerWing.yRot = -0.3228859F + (wingRot / 2F);
        outerWingR.yRot = 0.3228859F - (wingRot / 2F);
        } else {
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

        boolean sitting = entity.isSittingSynced();
        float stingY = sitting ? 17F : 8F;
        float stingZ = sitting ? -3F : 0F;

        int stingTicks = entity.getStingTicks();
        if (stingTicks == 0) {
            sTailRoot.xRot = 33F / R;
            sTailRoot.y = stingY;
            sTailRoot.z = stingZ;
            sTail2.xRot = 54.5F / R;
            sTail2.y = stingY;
            sTail2.z = stingZ;
            sTail3.xRot = 95.1F / R;
            sTail3.y = stingY;
            sTail3.z = stingZ;
            sTail4.xRot = 141.8F / R;
            sTail4.y = stingY;
            sTail4.z = stingZ;
            sTail5.xRot = 173.9F / R;
            sTail5.y = stingY;
            sTail5.z = stingZ;
            stingerLump.y = stingY;
            stingerLump.z = stingZ;
            stinger.y = stingY;
            stinger.z = stingZ;
        } else {
            sTailRoot.xRot = 95.2F / R;
            sTailRoot.y = 14.5F;
            sTailRoot.z = 2F;
            sTail2.xRot = 128.5F / R;
            sTail2.y = 15F;
            sTail2.z = 4F;
            sTail3.xRot = 169F / R;
            sTail3.y = 14F;
            sTail3.z = 3.8F;
            sTail4.xRot = 177F / R;
            sTail4.y = 13.5F;
            sTail4.z = -8.5F;
            sTail5.xRot = 180F / R;
            sTail5.y = 11.5F;
            sTail5.z = -17F;
        }

        mane.visible = true;
        chinHair.visible = true;
        leftChinBeard.visible = true;
        rightChinBeard.visible = true;
        foreheadHair.visible = true;
        neckHair.visible = true;
        leftFang.visible = true;
        rightFang.visible = true;

        saddle.visible = entity.isSaddled();
        storageChest.visible = entity.hasChest();

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
        } else {
            chest.y = 8F;
            chest.xRot = 0F;
            abdomen.xRot = 0F;
            neckBase.xRot = -14F / R;
            rightHindUpperLeg.y = 3F;
            leftHindUpperLeg.y = 3F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}