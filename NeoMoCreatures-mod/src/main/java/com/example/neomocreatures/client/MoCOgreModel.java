package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCOgreEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelOgre}: shared torso/limbs plus two
 * mutually-exclusive head/weapon sets, switched per individual by {@link MoCOgreEntity#getOgreType()}
 * — type 1 is the single head with empty hands; type 2 is the two-head set with a hammer in each hand.
 */
public class MoCOgreModel<T extends MoCOgreEntity> extends HierarchicalModel<T> {

    private static final float RADIAN = 57.29578F;

    // ---- Type 1: single head ----
    private final ModelPart head;
    private final ModelPart brow;
    private final ModelPart noseBridge;
    private final ModelPart nose;
    private final ModelPart rightTusk;
    private final ModelPart rightTooth;
    private final ModelPart leftTooth;
    private final ModelPart leftTusk;
    private final ModelPart lip;
    private final ModelPart rightEar;
    private final ModelPart rightRing;
    private final ModelPart rightRingHole;
    private final ModelPart leftEar;
    private final ModelPart leftRing;
    private final ModelPart leftRingHole;
    private final ModelPart hairRope;
    private final ModelPart hair1;
    private final ModelPart hair2;
    private final ModelPart hair3;
    private final ModelPart diamondHorn;
    private final ModelPart rightHorn;
    private final ModelPart rightHornTip;
    private final ModelPart leftHorn;
    private final ModelPart leftHornTip;
    private final ModelPart[] type1Parts;

    // ---- Type 2: front head (Head3) ----
    private final ModelPart head3;
    private final ModelPart head3RightEar;
    private final ModelPart head3LeftEar;
    private final ModelPart head3Eyelid;
    private final ModelPart head3Nose;
    private final ModelPart head3Brow;
    private final ModelPart head3Hair;
    private final ModelPart head3Lip;
    private final ModelPart head3RightTusk;
    private final ModelPart head3RightTooth;
    private final ModelPart head3LeftTooth;
    private final ModelPart head3LeftTusk;
    private final ModelPart head3RingHole;
    private final ModelPart head3Ring;

    // ---- Type 2: side head (Head2) ----
    private final ModelPart head2;
    private final ModelPart head2Chin;
    private final ModelPart head2Lip;
    private final ModelPart head2LeftTusk;
    private final ModelPart head2RightTusk;
    private final ModelPart head2Nose;
    private final ModelPart head2NoseBridge;
    private final ModelPart head2Brow;
    private final ModelPart head2RightHorn;
    private final ModelPart head2LeftHorn;
    private final ModelPart head2DiamondHorn;
    private final ModelPart[] type2Parts;

    // ---- Torso, shared by both types ----
    private final ModelPart rightThigh;
    private final ModelPart rightLeg;
    private final ModelPart leftThigh;
    private final ModelPart leftLeg;
    private final ModelPart loinCloth;
    private final ModelPart buttCover;
    private final ModelPart rightShoulder;
    private final ModelPart rightArm;
    private final ModelPart rightHand;
    private final ModelPart leftShoulder;
    private final ModelPart leftArm;
    private final ModelPart leftHand;

    // ---- Hammers, one per hand, shown only for type 2 ----
    private final ModelPart leftWeaponRoot;
    private final ModelPart rightWeaponRoot;

    private final ModelPart root;

    public MoCOgreModel(ModelPart root) {
        this.root = root;

        this.head = root.getChild("head");
        this.brow = root.getChild("brow");
        this.noseBridge = root.getChild("nose_bridge");
        this.nose = root.getChild("nose");
        this.rightTusk = root.getChild("right_tusk");
        this.rightTooth = root.getChild("right_tooth");
        this.leftTooth = root.getChild("left_tooth");
        this.leftTusk = root.getChild("left_tusk");
        this.lip = root.getChild("lip");
        this.rightEar = root.getChild("right_ear");
        this.rightRing = root.getChild("right_ring");
        this.rightRingHole = root.getChild("right_ring_hole");
        this.leftEar = root.getChild("left_ear");
        this.leftRing = root.getChild("left_ring");
        this.leftRingHole = root.getChild("left_ring_hole");
        this.hairRope = root.getChild("hair_rope");
        this.hair1 = root.getChild("hair_1");
        this.hair2 = root.getChild("hair_2");
        this.hair3 = root.getChild("hair_3");
        this.diamondHorn = root.getChild("diamond_horn");
        this.rightHorn = root.getChild("right_horn");
        this.rightHornTip = root.getChild("right_horn_tip");
        this.leftHorn = root.getChild("left_horn");
        this.leftHornTip = root.getChild("left_horn_tip");
        this.type1Parts = new ModelPart[] { this.head, this.brow, this.noseBridge, this.nose,
                this.rightTusk, this.rightTooth, this.leftTooth, this.leftTusk, this.lip,
                this.rightEar, this.rightRing, this.rightRingHole, this.leftEar, this.leftRing,
                this.leftRingHole, this.hairRope, this.hair1, this.hair2, this.hair3,
                this.diamondHorn, this.rightHorn, this.rightHornTip, this.leftHorn, this.leftHornTip };

        this.head3 = root.getChild("head3");
        this.head3RightEar = root.getChild("head3_right_ear");
        this.head3LeftEar = root.getChild("head3_left_ear");
        this.head3Eyelid = root.getChild("head3_eyelid");
        this.head3Nose = root.getChild("head3_nose");
        this.head3Brow = root.getChild("head3_brow");
        this.head3Hair = root.getChild("head3_hair");
        this.head3Lip = root.getChild("head3_lip");
        this.head3RightTusk = root.getChild("head3_right_tusk");
        this.head3RightTooth = root.getChild("head3_right_tooth");
        this.head3LeftTooth = root.getChild("head3_left_tooth");
        this.head3LeftTusk = root.getChild("head3_left_tusk");
        this.head3RingHole = root.getChild("head3_ring_hole");
        this.head3Ring = root.getChild("head3_ring");

        this.head2 = root.getChild("head2");
        this.head2Chin = root.getChild("head2_chin");
        this.head2Lip = root.getChild("head2_lip");
        this.head2LeftTusk = root.getChild("head2_left_tusk");
        this.head2RightTusk = root.getChild("head2_right_tusk");
        this.head2Nose = root.getChild("head2_nose");
        this.head2NoseBridge = root.getChild("head2_nose_bridge");
        this.head2Brow = root.getChild("head2_brow");
        this.head2RightHorn = root.getChild("head2_right_horn");
        this.head2LeftHorn = root.getChild("head2_left_horn");
        this.head2DiamondHorn = root.getChild("head2_diamond_horn");
        this.type2Parts = new ModelPart[] { this.head3, this.head3RightEar, this.head3LeftEar,
                this.head3Eyelid, this.head3Nose, this.head3Brow, this.head3Hair, this.head3Lip,
                this.head3RightTusk, this.head3RightTooth, this.head3LeftTooth, this.head3LeftTusk,
                this.head3RingHole, this.head3Ring, this.head2, this.head2Chin, this.head2Lip,
                this.head2LeftTusk, this.head2RightTusk, this.head2Nose, this.head2NoseBridge,
                this.head2Brow, this.head2RightHorn, this.head2LeftHorn, this.head2DiamondHorn };

        this.rightThigh = root.getChild("right_thigh");
        this.rightLeg = this.rightThigh.getChild("right_leg");
        this.leftThigh = root.getChild("left_thigh");
        this.leftLeg = this.leftThigh.getChild("left_leg");
        this.loinCloth = root.getChild("loin_cloth");
        this.buttCover = root.getChild("butt_cover");
        this.rightShoulder = root.getChild("right_shoulder");
        this.rightArm = this.rightShoulder.getChild("right_arm");
        this.rightHand = this.rightArm.getChild("right_hand");
        this.leftShoulder = root.getChild("left_shoulder");
        this.leftArm = this.leftShoulder.getChild("left_arm");
        this.leftHand = this.leftArm.getChild("left_hand");

        this.leftWeaponRoot = this.leftHand.getChild("left_weapon_root");
        this.rightWeaponRoot = this.rightHand.getChild("right_weapon_root");
    }

    /** The texture layout is 128x128. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        addType1Head(root);
        addType2Heads(root);
        addTorso(root);
        PartDefinition rightHand = addRightArm(root);
        PartDefinition leftHand = addLeftArm(root);
        addWeapon(leftHand, "left_weapon_root", 24, 104);
        addWeapon(rightHand, "right_weapon_root", 24, 104);

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addType1Head(PartDefinition root) {
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(80, 0).addBox(-6F, -12F, -6F, 12, 12, 12),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("brow",
                CubeListBuilder.create().texOffs(68, 7).addBox(-5F, -10.5F, -8F, 10, 3, 2),
                PartPose.offsetAndRotation(0F, -13F, 0F, -0.0872665F, 0F, 0F));
        root.addOrReplaceChild("nose_bridge",
                CubeListBuilder.create().texOffs(80, 4).addBox(-1F, -7F, -8F, 2, 2, 1),
                PartPose.offsetAndRotation(0F, -13F, 0F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(80, 0).addBox(-2F, -7F, -7F, 4, 2, 2),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.0872665F, 0F, 0F));
        root.addOrReplaceChild("right_tusk",
                CubeListBuilder.create().texOffs(60, 4).addBox(-3.5F, -6F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("right_tooth",
                CubeListBuilder.create().texOffs(64, 4).addBox(-1.5F, -5F, -6.5F, 1, 1, 1),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("left_tooth",
                CubeListBuilder.create().texOffs(72, 4).addBox(0.5F, -5F, -6.5F, 1, 1, 1),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("left_tusk",
                CubeListBuilder.create().texOffs(76, 4).addBox(2.5F, -6F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("lip",
                CubeListBuilder.create().texOffs(60, 0).addBox(-4F, -4F, -7F, 8, 2, 2),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(60, 12).addBox(-9F, -9F, -1F, 3, 5, 2),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("right_ring",
                CubeListBuilder.create().texOffs(32, 58).addBox(-8F, -6F, -2F, 1, 4, 4),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("right_ring_hole",
                CubeListBuilder.create().texOffs(26, 50).addBox(-8F, -5F, -1F, 1, 2, 2),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(70, 12).addBox(6F, -9F, -1F, 3, 5, 2),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("left_ring",
                CubeListBuilder.create().texOffs(32, 58).addBox(7F, -6F, -2F, 1, 4, 4),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("left_ring_hole",
                CubeListBuilder.create().texOffs(26, 50).addBox(7F, -5F, -1F, 1, 2, 2),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("hair_rope",
                CubeListBuilder.create().texOffs(82, 83).addBox(-2F, -8F, 9F, 4, 4, 4),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.6108652F, 0F, 0F));
        root.addOrReplaceChild("hair_1",
                CubeListBuilder.create().texOffs(78, 107).addBox(-3F, -9F, 13F, 6, 8, 3),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.6108652F, 0F, 0F));
        root.addOrReplaceChild("hair_2",
                CubeListBuilder.create().texOffs(60, 107).addBox(-3F, -6.5F, 11.6F, 6, 8, 3),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("hair_3",
                CubeListBuilder.create().texOffs(42, 107).addBox(-3F, -2.4F, 11.4F, 6, 8, 3),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("diamond_horn",
                CubeListBuilder.create().texOffs(120, 31).addBox(-1F, -17F, -6F, 2, 6, 2),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0.0872665F, 0F, 0F));
        root.addOrReplaceChild("right_horn",
                CubeListBuilder.create().texOffs(46, 6).addBox(-6F, -12F, -11F, 2, 2, 5),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("right_horn_tip",
                CubeListBuilder.create().texOffs(44, 13).addBox(-6F, -15F, -11F, 2, 3, 2),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("left_horn",
                CubeListBuilder.create().texOffs(46, 6).addBox(4F, -12F, -11F, 2, 2, 5),
                PartPose.offset(0F, -13F, 0F));
        root.addOrReplaceChild("left_horn_tip",
                CubeListBuilder.create().texOffs(52, 13).addBox(4F, -15F, -11F, 2, 3, 2),
                PartPose.offset(0F, -13F, 0F));
    }

    private static void addType2Heads(PartDefinition root) {
        // ---- Front head (Head3), offset +7 on X ----
        root.addOrReplaceChild("head3_right_ear",
                CubeListBuilder.create().texOffs(110, 24).addBox(-8F, -9F, -1F, 3, 5, 2),
                PartPose.offset(7F, -13F, 0F));
        root.addOrReplaceChild("head3_left_ear",
                CubeListBuilder.create().texOffs(100, 24).addBox(5F, -9F, -1F, 3, 5, 2),
                PartPose.offset(7F, -13F, 0F));
        root.addOrReplaceChild("head3_eyelid",
                CubeListBuilder.create().texOffs(46, 3).addBox(-3F, -8F, -4.5F, 6, 2, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("head3_nose",
                CubeListBuilder.create().texOffs(60, 9).addBox(-1.5F, -8.5F, -3.5F, 3, 2, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.4886922F, 0F, 0F));
        root.addOrReplaceChild("head3",
                CubeListBuilder.create().texOffs(42, 83).addBox(-5F, -12F, -6F, 10, 12, 12),
                PartPose.offset(7F, -13F, 0F));
        root.addOrReplaceChild("head3_brow",
                CubeListBuilder.create().texOffs(46, 0).addBox(-3F, -9F, -8.5F, 6, 2, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("head3_hair",
                CubeListBuilder.create().texOffs(80, 118).addBox(-2F, -17F, -5F, 4, 6, 4),
                PartPose.offsetAndRotation(7F, -13F, 0F, -0.6108652F, 0F, 0F));
        root.addOrReplaceChild("head3_lip",
                CubeListBuilder.create().texOffs(22, 68).addBox(-4F, -4F, -7F, 8, 2, 2),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head3_right_tusk",
                CubeListBuilder.create().texOffs(83, 34).addBox(-3.5F, -6F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head3_right_tooth",
                CubeListBuilder.create().texOffs(87, 34).addBox(-1.5F, -5F, -6.5F, 1, 1, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head3_left_tooth",
                CubeListBuilder.create().texOffs(96, 34).addBox(0.5F, -5F, -6.5F, 1, 1, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head3_left_tusk",
                CubeListBuilder.create().texOffs(100, 34).addBox(2.5F, -6F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head3_ring_hole",
                CubeListBuilder.create().texOffs(26, 50).addBox(6F, -5F, -1F, 1, 2, 2),
                PartPose.offset(7F, -13F, 0F));
        root.addOrReplaceChild("head3_ring",
                CubeListBuilder.create().texOffs(32, 58).addBox(6F, -6F, -2F, 1, 4, 4),
                PartPose.offset(7F, -13F, 0F));

        // ---- Side head (Head2), offset -7 on X ----
        root.addOrReplaceChild("head2_chin",
                CubeListBuilder.create().texOffs(21, 24).addBox(-3F, -5F, -8F, 6, 3, 3),
                PartPose.offsetAndRotation(-7F, -13F, 0F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("head2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -12F, -6F, 10, 12, 12),
                PartPose.offset(-7F, -13F, 0F));
        root.addOrReplaceChild("head2_lip",
                CubeListBuilder.create().texOffs(0, 24).addBox(-4F, -5F, -8F, 8, 2, 2),
                PartPose.offset(-7F, -13F, 0F));
        root.addOrReplaceChild("head2_left_tusk",
                CubeListBuilder.create().texOffs(46, 28).addBox(2.5F, -8F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head2_right_tusk",
                CubeListBuilder.create().texOffs(39, 28).addBox(-3.5F, -8F, -6.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-7F, -13F, 0F, 0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head2_nose",
                CubeListBuilder.create().texOffs(116, 0).addBox(-2F, -7F, -7F, 4, 2, 2),
                PartPose.offsetAndRotation(-7F, -13F, 0F, 0.0872665F, 0F, 0F));
        root.addOrReplaceChild("head2_nose_bridge",
                CubeListBuilder.create().texOffs(116, 4).addBox(-1F, -7F, -8F, 2, 2, 1),
                PartPose.offsetAndRotation(-7F, -13F, 0F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("head2_brow",
                CubeListBuilder.create().texOffs(80, 24).addBox(-4F, -10.5F, -8F, 8, 3, 2),
                PartPose.offsetAndRotation(-7F, -13F, 0F, -0.0872665F, 0F, 0F));
        root.addOrReplaceChild("head2_right_horn",
                CubeListBuilder.create().texOffs(24, 30).addBox(-4F, -8F, -15F, 2, 2, 5),
                PartPose.offsetAndRotation(-7F, -13F, 0F, -0.5235988F, 0F, 0F));
        root.addOrReplaceChild("head2_left_horn",
                CubeListBuilder.create().texOffs(24, 30).addBox(2F, -8F, -15F, 2, 2, 5),
                PartPose.offsetAndRotation(-7F, -13F, 0F, -0.5235988F, 0F, 0F));
        root.addOrReplaceChild("head2_diamond_horn",
                CubeListBuilder.create().texOffs(120, 46).addBox(-1F, -17F, -6F, 2, 6, 2),
                PartPose.offsetAndRotation(-7F, -13F, 0F, 0.0872665F, 0F, 0F));
    }

    private static void addTorso(PartDefinition root) {
        root.addOrReplaceChild("neck_rest",
                CubeListBuilder.create().texOffs(39, 20).addBox(-7F, -19F, -3F, 14, 3, 11),
                PartPose.offset(0F, 5F, 0F));
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(32, 34).addBox(-9.5F, -17.8F, -7.3F, 19, 11, 13),
                PartPose.offsetAndRotation(0F, 5F, 0F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("stomach",
                CubeListBuilder.create().texOffs(28, 58).addBox(-11F, -8F, -6F, 22, 11, 14),
                PartPose.offset(0F, 5F, 0F));
        root.addOrReplaceChild("butt_cover",
                CubeListBuilder.create().texOffs(32, 118).addBox(-4F, 0F, 0F, 8, 8, 2),
                PartPose.offset(0F, 8F, 6F));
        root.addOrReplaceChild("loin_cloth",
                CubeListBuilder.create().texOffs(32, 118).addBox(-4F, 0F, -2F, 8, 8, 2),
                PartPose.offset(0F, 8F, -4F));

        PartDefinition rightThigh = root.addOrReplaceChild("right_thigh",
                CubeListBuilder.create().texOffs(0, 83).addBox(-10F, 0F, -5F, 10, 11, 10),
                PartPose.offset(-2F, 4F, 1F));
        PartDefinition rightLeg = rightThigh.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 104).addBox(-4F, -1F, -4F, 8, 11, 8),
                PartPose.offset(-5F, 10F, 0F));
        rightLeg.addOrReplaceChild("right_knee",
                CubeListBuilder.create().texOffs(0, 88).addBox(-2F, -2F, -0.5F, 4, 4, 1),
                PartPose.offset(0F, 2F, -4.25F));
        rightLeg.addOrReplaceChild("right_toes",
                CubeListBuilder.create().texOffs(0, 123).addBox(-2.5F, -1F, -3F, 5, 2, 3),
                PartPose.offset(-1.5F, 9F, -3.5F));
        rightLeg.addOrReplaceChild("right_big_toe",
                CubeListBuilder.create().texOffs(20, 123).addBox(-1.5F, -1F, -3F, 3, 2, 3),
                PartPose.offset(2.5F, 9F, -4F));

        PartDefinition leftThigh = root.addOrReplaceChild("left_thigh",
                CubeListBuilder.create().texOffs(88, 83).addBox(0F, 0F, -5F, 10, 11, 10),
                PartPose.offset(2F, 4F, 1F));
        PartDefinition leftLeg = leftThigh.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(96, 104).addBox(-4F, -1F, -4F, 8, 11, 8),
                PartPose.offset(5F, 10F, 0F));
        leftLeg.addOrReplaceChild("left_knee",
                CubeListBuilder.create().texOffs(118, 88).addBox(-2F, -2F, -0.5F, 4, 4, 1),
                PartPose.offset(0F, 2F, -4.25F));
        leftLeg.addOrReplaceChild("left_toes",
                CubeListBuilder.create().texOffs(112, 123).addBox(-2.5F, -1F, -3F, 5, 2, 3),
                PartPose.offset(1.5F, 9F, -3.5F));
        leftLeg.addOrReplaceChild("left_big_toe",
                CubeListBuilder.create().texOffs(96, 123).addBox(-1.5F, -1F, -3F, 3, 2, 3),
                PartPose.offset(-2.5F, 9F, -4F));
    }

    private static PartDefinition addLeftArm(PartDefinition root) {
        PartDefinition leftShoulder = root.addOrReplaceChild("left_shoulder",
                CubeListBuilder.create().texOffs(96, 31).addBox(0F, -3F, -4F, 8, 7, 8),
                PartPose.offset(7F, -10F, 2F));
        PartDefinition leftArm = leftShoulder.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(100, 66).addBox(0F, 0F, -4F, 6, 9, 8),
                PartPose.offset(6F, -1F, 1F));
        PartDefinition leftHand = leftArm.addOrReplaceChild("left_hand",
                CubeListBuilder.create().texOffs(96, 46).addBox(-4F, 0F, -4F, 8, 12, 8),
                PartPose.offset(3F, 8F, -1F));
        leftHand.addOrReplaceChild("left_elbow",
                CubeListBuilder.create().texOffs(86, 64).addBox(-2F, -1.5F, -0.5F, 4, 3, 1),
                PartPose.offset(0F, 2.5F, 4F));
        return leftHand;
    }

    private static PartDefinition addRightArm(PartDefinition root) {
        PartDefinition rightShoulder = root.addOrReplaceChild("right_shoulder",
                CubeListBuilder.create().texOffs(0, 31).addBox(0F, -3F, -4F, 8, 7, 8),
                PartPose.offset(-15F, -10F, 2F));
        PartDefinition rightArm = rightShoulder.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(0, 66).addBox(0F, 0F, -4F, 6, 9, 8),
                PartPose.offset(-4F, -1F, 1F));
        PartDefinition rightHand = rightArm.addOrReplaceChild("right_hand",
                CubeListBuilder.create().texOffs(0, 46).addBox(-4F, 0F, -4F, 8, 12, 8),
                PartPose.offset(3F, 8F, -1F));
        rightHand.addOrReplaceChild("right_elbow",
                CubeListBuilder.create().texOffs(86, 64).addBox(-2F, -1.5F, -0.5F, 4, 3, 1),
                PartPose.offset(0F, 2.5F, 4F));
        return rightHand;
    }

    /** A hammer: a handle with a bulge and cross-bar, ending in a spiked head. Identical geometry
     *  for both hands — the original reuses the same texture offsets on each side. */
    private static void addWeapon(PartDefinition hand, String rootName, int texU, int texV) {
        PartDefinition weaponRoot = hand.addOrReplaceChild(rootName,
                CubeListBuilder.create().texOffs(texU, texV).addBox(-1.5F, -1.5F, -4F, 3, 3, 4),
                PartPose.offset(-0.5F, 8.5F, -4F));
        weaponRoot.addOrReplaceChild(rootName + "_end",
                CubeListBuilder.create().texOffs(74, 90).addBox(-1.5F, -1.5F, 0F, 3, 3, 2),
                PartPose.offset(0F, 0F, 8F));
        PartDefinition lump = weaponRoot.addOrReplaceChild(rootName + "_lump",
                CubeListBuilder.create().texOffs(30, 83).addBox(-2.5F, -2.5F, -4F, 5, 5, 4),
                PartPose.offset(0F, 0F, -4F));
        PartDefinition between = lump.addOrReplaceChild(rootName + "_between",
                CubeListBuilder.create().texOffs(83, 42).addBox(-1.5F, -1.5F, -2F, 3, 3, 2),
                PartPose.offset(0F, 0F, -4F));
        PartDefinition tip = between.addOrReplaceChild(rootName + "_tip",
                CubeListBuilder.create().texOffs(60, 118).addBox(-2.5F, -2.5F, -5F, 5, 5, 5),
                PartPose.offset(0F, 0F, -2F));
        tip.addOrReplaceChild(rootName + "_hammer_neck",
                CubeListBuilder.create().texOffs(32, 39).addBox(-0.5F, -4F, -4F, 1, 4, 4),
                PartPose.offset(0F, -2.5F, -1F));
        PartDefinition hammerHeadSupport = tip.addOrReplaceChild(rootName + "_hammer_head_support",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -2F, 2, 2, 4),
                PartPose.offset(0F, 2.5F, -3F));
        hammerHeadSupport.addOrReplaceChild(rootName + "_hammer_head",
                CubeListBuilder.create().texOffs(32, 3).addBox(-2F, 0F, -2.5F, 4, 3, 5),
                PartPose.offset(0F, 2F, 0F));
        tip.addOrReplaceChild(rootName + "_spike_0",
                CubeListBuilder.create().texOffs(52, 118).addBox(-1F, -1F, -3F, 2, 2, 3),
                PartPose.offset(0F, 0F, -5F));
        tip.addOrReplaceChild(rootName + "_spike_1",
                CubeListBuilder.create().texOffs(52, 118).addBox(-3F, -1F, -1F, 3, 2, 2),
                PartPose.offset(-2.5F, 0F, -3F));
        tip.addOrReplaceChild(rootName + "_spike_2",
                CubeListBuilder.create().texOffs(52, 118).addBox(3F, -1F, -1F, 3, 2, 2),
                PartPose.offset(-0.5F, 0F, -3F));
        tip.addOrReplaceChild(rootName + "_spike_3",
                CubeListBuilder.create().texOffs(52, 118).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(0F, 2.5F, -3F));
        tip.addOrReplaceChild(rootName + "_spike_4",
                CubeListBuilder.create().texOffs(52, 118).addBox(-1F, -3F, -1F, 2, 3, 2),
                PartPose.offset(0F, -2.5F, -3F));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T ogre, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean singleHeaded = ogre.getOgreType() != 2;
        for (ModelPart part : this.type1Parts) {
            part.visible = singleHeaded;
        }
        for (ModelPart part : this.type2Parts) {
            part.visible = !singleHeaded;
        }
        // Deviates from the original (which only gave the hammer to the two-headed type): every
        // ogre here actually performs the ground-smash attack, so it makes no sense for the
        // single-headed ones to swing an empty hand at the ground.
        this.leftWeaponRoot.visible = true;
        this.rightWeaponRoot.visible = true;

        float headYawRad = netHeadYaw / RADIAN;
        float headPitchRad = headPitch / RADIAN;

        float rightLegSwing = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 0.8F * limbSwingAmount;
        float leftLegSwing = Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        float clothSway = Mth.cos(limbSwing * 0.9F) * 0.6F * limbSwingAmount;

        float rightLegSwingNext = Mth.cos((limbSwing + 0.1F) * 0.6662F + Mth.PI) * 0.8F * limbSwingAmount;
        float leftLegSwingNext = Mth.cos((limbSwing + 0.1F) * 0.6662F) * 0.8F * limbSwingAmount;
        float rightLowerLeg = rightLegSwing;
        float leftLowerLeg = leftLegSwing;
        if (limbSwingAmount > 0.15F) {
            if (rightLegSwing > rightLegSwingNext) {
                rightLowerLeg = rightLegSwing + 25F / RADIAN;
            }
            if (leftLegSwing > leftLegSwingNext) {
                leftLowerLeg = leftLegSwing + 25F / RADIAN;
            }
        }

        this.rightThigh.xRot = rightLegSwing;
        this.leftThigh.xRot = leftLegSwing;
        this.rightLeg.xRot = rightLowerLeg;
        this.leftLeg.xRot = leftLowerLeg;
        this.loinCloth.xRot = clothSway;
        this.buttCover.xRot = clothSway;

        float armSwingAmount = -(Mth.cos(ogre.attackCounter * 0.18F) * 3F);

        if (ogre.armToAnimate == 1 || ogre.armToAnimate == 3) {
            this.leftShoulder.xRot = armSwingAmount;
            this.leftHand.xRot = -45F / RADIAN;
        } else {
            this.leftShoulder.zRot = Mth.cos(ageInTicks * 0.09F) * 0.05F - 0.05F;
            this.leftShoulder.xRot = rightLegSwing;
            this.leftHand.xRot = 0F;
        }

        if (ogre.armToAnimate == 2 || ogre.armToAnimate == 3) {
            this.rightShoulder.xRot = armSwingAmount;
            this.rightHand.xRot = -45F / RADIAN;
        } else {
            this.rightShoulder.zRot = -(Mth.cos(ageInTicks * 0.09F) * 0.05F) + 0.05F;
            this.rightShoulder.xRot = leftLegSwing;
            this.rightHand.xRot = 0F;
        }

        if (singleHeaded) {
            this.head.xRot = headPitchRad;
            this.head.yRot = headYawRad;
            for (ModelPart part : this.type1Parts) {
                if (part != this.head) {
                    part.xRot = headPitchRad;
                    part.yRot = headYawRad;
                }
            }
            this.hairRope.xRot = 0.6108652F + headPitchRad;
            this.hair1.xRot = 0.6108652F + headPitchRad;
            this.hair2.xRot = 0.2617994F + headPitchRad;
            this.diamondHorn.xRot = 0.0872665F + headPitchRad;
        } else {
            int movingHead = ogre.getMovingHead();
            if (movingHead == 2) {
                this.head2.xRot = headPitchRad;
                this.head2.yRot = headYawRad;
            }
            if (movingHead == 3) {
                this.head3.xRot = headPitchRad;
                this.head3.yRot = headYawRad;
            }

            this.head3RightEar.xRot = this.head3.xRot;
            this.head3LeftEar.xRot = this.head3.xRot;
            this.head3Eyelid.xRot = 0.2617994F + this.head3.xRot;
            this.head3Nose.xRot = 0.4886922F + this.head3.xRot;
            this.head3Brow.xRot = -0.2617994F + this.head3.xRot;
            this.head3Hair.xRot = -0.6108652F + this.head3.xRot;
            this.head3Lip.xRot = 0.1745329F + this.head3.xRot;
            this.head3RightTusk.xRot = 0.1745329F + this.head3.xRot;
            this.head3RightTooth.xRot = 0.1745329F + this.head3.xRot;
            this.head3LeftTooth.xRot = 0.1745329F + this.head3.xRot;
            this.head3LeftTusk.xRot = 0.1745329F + this.head3.xRot;
            this.head3RingHole.xRot = this.head3.xRot;
            this.head3Ring.xRot = this.head3.xRot;

            this.head3RightEar.yRot = this.head3.yRot;
            this.head3LeftEar.yRot = this.head3.yRot;
            this.head3Eyelid.yRot = this.head3.yRot;
            this.head3Nose.yRot = this.head3.yRot;
            this.head3Brow.yRot = this.head3.yRot;
            this.head3Hair.yRot = this.head3.yRot;
            this.head3Lip.yRot = this.head3.yRot;
            this.head3RightTusk.yRot = this.head3.yRot;
            this.head3RightTooth.yRot = this.head3.yRot;
            this.head3LeftTooth.yRot = this.head3.yRot;
            this.head3LeftTusk.yRot = this.head3.yRot;
            this.head3RingHole.yRot = this.head3.yRot;
            this.head3Ring.yRot = this.head3.yRot;

            this.head2Chin.xRot = 0.2617994F + this.head2.xRot;
            this.head2Lip.xRot = this.head2.xRot;
            this.head2LeftTusk.xRot = 0.1745329F + this.head2.xRot;
            this.head2RightTusk.xRot = 0.1745329F + this.head2.xRot;
            this.head2Nose.xRot = 0.0872665F + this.head2.xRot;
            this.head2NoseBridge.xRot = -0.1745329F + this.head2.xRot;
            this.head2Brow.xRot = -0.0872665F + this.head2.xRot;
            this.head2RightHorn.xRot = -0.5235988F + this.head2.xRot;
            this.head2LeftHorn.xRot = -0.5235988F + this.head2.xRot;
            this.head2DiamondHorn.xRot = 0.0872665F + this.head2.xRot;

            this.head2Chin.yRot = this.head2.yRot;
            this.head2Lip.yRot = this.head2.yRot;
            this.head2LeftTusk.yRot = this.head2.yRot;
            this.head2RightTusk.yRot = this.head2.yRot;
            this.head2Nose.yRot = this.head2.yRot;
            this.head2NoseBridge.yRot = this.head2.yRot;
            this.head2Brow.yRot = this.head2.yRot;
            this.head2RightHorn.yRot = this.head2.yRot;
            this.head2LeftHorn.yRot = this.head2.yRot;
            this.head2DiamondHorn.yRot = this.head2.yRot;
        }
    }
}