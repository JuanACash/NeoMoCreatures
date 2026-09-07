package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.elephant.ElephantVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelElephant (Techne /
 * ModelRenderer) to the modern HierarchicalModel / PartDefinition format —
 * BASE GEOMETRY ONLY (step 1): body, head, ears, natural tusks, trunk, legs,
 * tail. No harness, storage chests, cabin, fort, or equippable tusks yet —
 * those get added the same way once we build the equipment steps.
 * <p>
 * Kept as a FLAT hierarchy (every part is a direct child of root, exactly
 * like the original's independent ModelRenderers) instead of true nesting,
 * because the original's trunk/leg animation recomputes each part's
 * absolute position every frame from its neighbor's current rotation
 * (see adjustXRotationPoints/adjustAllRotationPoints below) rather than
 * relying on parent transforms — porting that to true nesting would risk
 * subtly changing the motion.
 */
public class MoCElephantModel extends HierarchicalModel<MoCElephantEntity> {

    // Minecraft's radian-per-degree constant, same value the original Techne model used.
    private static final float R = 57.29578F;

    private final ModelPart root;
    private float yOffset;

    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart headBump;
    private final ModelPart chin;
    private final ModelPart lowerLip;
    private final ModelPart back;
    private final ModelPart leftSmallEar;
    private final ModelPart leftBigEar;
    private final ModelPart rightSmallEar;
    private final ModelPart rightBigEar;
    private final ModelPart hump;
    private final ModelPart body;
    private final ModelPart skirt;
    private final ModelPart rightTuskA;
    private final ModelPart rightTuskB;
    private final ModelPart rightTuskC;
    private final ModelPart rightTuskD;
    private final ModelPart leftTuskA;
    private final ModelPart leftTuskB;
    private final ModelPart leftTuskC;
    private final ModelPart leftTuskD;
    private final ModelPart trunkA;
    private final ModelPart trunkB;
    private final ModelPart trunkC;
    private final ModelPart trunkD;
    private final ModelPart trunkE;
    private final ModelPart frontRightUpperLeg;
    private final ModelPart frontRightLowerLeg;
    private final ModelPart frontLeftUpperLeg;
    private final ModelPart frontLeftLowerLeg;
    private final ModelPart backRightUpperLeg;
    private final ModelPart backRightLowerLeg;
    private final ModelPart backLeftUpperLeg;
    private final ModelPart backLeftLowerLeg;
    private final ModelPart tailRoot;
    private final ModelPart tail;
    private final ModelPart tailPlush;
    private final ModelPart harnessBlanket;
    private final ModelPart harnessUpperBelt;
    private final ModelPart harnessLowerBelt;
    private final ModelPart storageRightBedroll;
    private final ModelPart storageLeftBedroll;
    private final ModelPart storageFrontRightChest;
    private final ModelPart storageBackRightChest;
    private final ModelPart storageFrontLeftChest;
    private final ModelPart storageBackLeftChest;
    private final ModelPart storageRightBlankets;
    private final ModelPart storageLeftBlankets;
    private final ModelPart storageUpLeft;
    private final ModelPart storageUpRight;
    private final ModelPart tuskWoodLeft1;
    private final ModelPart tuskWoodLeft2;
    private final ModelPart tuskWoodLeft3;
    private final ModelPart tuskWoodRight1;
    private final ModelPart tuskWoodRight2;
    private final ModelPart tuskWoodRight3;
    private final ModelPart tuskIronLeft1;
    private final ModelPart tuskIronLeft2;
    private final ModelPart tuskIronLeft3;
    private final ModelPart tuskIronRight1;
    private final ModelPart tuskIronRight2;
    private final ModelPart tuskIronRight3;
    private final ModelPart tuskDiamondLeft1;
    private final ModelPart tuskDiamondLeft2;
    private final ModelPart tuskDiamondLeft3;
    private final ModelPart tuskDiamondRight1;
    private final ModelPart tuskDiamondRight2;
    private final ModelPart tuskDiamondRight3;
    private final ModelPart tuskWoodLeft4;
    private final ModelPart tuskWoodLeft5;
    private final ModelPart tuskWoodRight4;
    private final ModelPart tuskWoodRight5;
    private final ModelPart tuskIronLeft4;
    private final ModelPart tuskIronLeft5;
    private final ModelPart tuskIronRight4;
    private final ModelPart tuskIronRight5;
    private final ModelPart tuskDiamondLeft4;
    private final ModelPart tuskDiamondLeft5;
    private final ModelPart tuskDiamondRight4;
    private final ModelPart tuskDiamondRight5;
    private final ModelPart howdahPillow;
    private final ModelPart howdahLeftRail;
    private final ModelPart howdahCabin;
    private final ModelPart howdahRightRail;
    private final ModelPart howdahBackRail;
    private final ModelPart howdahRoof;
    private final ModelPart platformFloor1;
    private final ModelPart platformFloor2;
    private final ModelPart platformFloor3;
    private final ModelPart platformBackWall;
    private final ModelPart platformBackLeftWall;
    private final ModelPart platformBackRightWall;
    private final ModelPart platformNeckBeam;
    private final ModelPart platformBackBeam;

    // Canonical (constructor) Y/Z of every part the trunk/leg IK-chain
    // mutates in place — reset here every frame before recomputing, exactly
    // like the original's AdjustY() + inline Z-pins did every frame. Without
    // this, distances computed from "current" positions would drift frame
    // to frame instead of staying anchored to the model's rest pose.
    private static final float TRUNK_A_Y = -3F;
    private static final float TRUNK_B_Y = 6.5F;
    private static final float TRUNK_C_Y = 13F;
    private static final float TRUNK_D_Y = 16F;
    private static final float TRUNK_E_Y = 19.5F;
    private static final float TRUNK_A_Z = -22.5F;
    private static final float TRUNK_B_Z = -22.5F;
    private static final float TRUNK_C_Z = -22.5F;
    private static final float TRUNK_D_Z = -21.5F;
    private static final float TRUNK_E_Z = -19F;
    private static final float LOWER_LEG_Y = 14F;
    private static final float HOWDAH_SCALE = 1.3F;
    private static final float HOWDAH_Y_OFFSET = -6F; // negative = higher up

    public MoCElephantModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.headBump = root.getChild("head_bump");
        this.chin = root.getChild("chin");
        this.lowerLip = root.getChild("lower_lip");
        this.back = root.getChild("back");
        this.leftSmallEar = root.getChild("left_small_ear");
        this.leftBigEar = root.getChild("left_big_ear");
        this.rightSmallEar = root.getChild("right_small_ear");
        this.rightBigEar = root.getChild("right_big_ear");
        this.hump = root.getChild("hump");
        this.body = root.getChild("body");
        this.skirt = root.getChild("skirt");
        this.rightTuskA = root.getChild("right_tusk_a");
        this.rightTuskB = root.getChild("right_tusk_b");
        this.rightTuskC = root.getChild("right_tusk_c");
        this.rightTuskD = root.getChild("right_tusk_d");
        this.leftTuskA = root.getChild("left_tusk_a");
        this.leftTuskB = root.getChild("left_tusk_b");
        this.leftTuskC = root.getChild("left_tusk_c");
        this.leftTuskD = root.getChild("left_tusk_d");
        this.trunkA = root.getChild("trunk_a");
        this.trunkB = root.getChild("trunk_b");
        this.trunkC = root.getChild("trunk_c");
        this.trunkD = root.getChild("trunk_d");
        this.trunkE = root.getChild("trunk_e");
        this.frontRightUpperLeg = root.getChild("front_right_upper_leg");
        this.frontRightLowerLeg = root.getChild("front_right_lower_leg");
        this.frontLeftUpperLeg = root.getChild("front_left_upper_leg");
        this.frontLeftLowerLeg = root.getChild("front_left_lower_leg");
        this.backRightUpperLeg = root.getChild("back_right_upper_leg");
        this.backRightLowerLeg = root.getChild("back_right_lower_leg");
        this.backLeftUpperLeg = root.getChild("back_left_upper_leg");
        this.backLeftLowerLeg = root.getChild("back_left_lower_leg");
        this.tailRoot = root.getChild("tail_root");
        this.tail = root.getChild("tail");
        this.tailPlush = root.getChild("tail_plush");
        this.harnessBlanket = root.getChild("harness_blanket");
        this.harnessUpperBelt = root.getChild("harness_upper_belt");
        this.harnessLowerBelt = root.getChild("harness_lower_belt");
        this.storageRightBedroll = root.getChild("storage_right_bedroll");
        this.storageLeftBedroll = root.getChild("storage_left_bedroll");
        this.storageFrontRightChest = root.getChild("storage_front_right_chest");
        this.storageBackRightChest = root.getChild("storage_back_right_chest");
        this.storageFrontLeftChest = root.getChild("storage_front_left_chest");
        this.storageBackLeftChest = root.getChild("storage_back_left_chest");
        this.storageRightBlankets = root.getChild("storage_right_blankets");
        this.storageLeftBlankets = root.getChild("storage_left_blankets");
        this.storageUpLeft = root.getChild("storage_up_left");
        this.storageUpRight = root.getChild("storage_up_right");
        this.tuskWoodLeft1 = root.getChild("tusk_wood_left_1");
        this.tuskWoodLeft2 = root.getChild("tusk_wood_left_2");
        this.tuskWoodLeft3 = root.getChild("tusk_wood_left_3");
        this.tuskWoodRight1 = root.getChild("tusk_wood_right_1");
        this.tuskWoodRight2 = root.getChild("tusk_wood_right_2");
        this.tuskWoodRight3 = root.getChild("tusk_wood_right_3");
        this.tuskIronLeft1 = root.getChild("tusk_iron_left_1");
        this.tuskIronLeft2 = root.getChild("tusk_iron_left_2");
        this.tuskIronLeft3 = root.getChild("tusk_iron_left_3");
        this.tuskIronRight1 = root.getChild("tusk_iron_right_1");
        this.tuskIronRight2 = root.getChild("tusk_iron_right_2");
        this.tuskIronRight3 = root.getChild("tusk_iron_right_3");
        this.tuskDiamondLeft1 = root.getChild("tusk_diamond_left_1");
        this.tuskDiamondLeft2 = root.getChild("tusk_diamond_left_2");
        this.tuskDiamondLeft3 = root.getChild("tusk_diamond_left_3");
        this.tuskDiamondRight1 = root.getChild("tusk_diamond_right_1");
        this.tuskDiamondRight2 = root.getChild("tusk_diamond_right_2");
        this.tuskDiamondRight3 = root.getChild("tusk_diamond_right_3");
        this.tuskWoodLeft4 = root.getChild("tusk_wood_left_4");
        this.tuskWoodLeft5 = root.getChild("tusk_wood_left_5");
        this.tuskWoodRight4 = root.getChild("tusk_wood_right_4");
        this.tuskWoodRight5 = root.getChild("tusk_wood_right_5");
        this.tuskIronLeft4 = root.getChild("tusk_iron_left_4");
        this.tuskIronLeft5 = root.getChild("tusk_iron_left_5");
        this.tuskIronRight4 = root.getChild("tusk_iron_right_4");
        this.tuskIronRight5 = root.getChild("tusk_iron_right_5");
        this.tuskDiamondLeft4 = root.getChild("tusk_diamond_left_4");
        this.tuskDiamondLeft5 = root.getChild("tusk_diamond_left_5");
        this.tuskDiamondRight4 = root.getChild("tusk_diamond_right_4");
        this.tuskDiamondRight5 = root.getChild("tusk_diamond_right_5");
        this.howdahPillow = root.getChild("howdah_pillow");
        this.howdahLeftRail = root.getChild("howdah_left_rail");
        this.howdahCabin = root.getChild("howdah_cabin");
        this.howdahRightRail = root.getChild("howdah_right_rail");
        this.howdahBackRail = root.getChild("howdah_back_rail");
        this.howdahRoof = root.getChild("howdah_roof");
        this.platformFloor1 = root.getChild("platform_floor_1");
        this.platformFloor2 = root.getChild("platform_floor_2");
        this.platformFloor3 = root.getChild("platform_floor_3");
        this.platformBackWall = root.getChild("platform_back_wall");
        this.platformBackLeftWall = root.getChild("platform_back_left_wall");
        this.platformBackRightWall = root.getChild("platform_back_right_wall");
        this.platformNeckBeam = root.getChild("platform_neck_beam");
        this.platformBackBeam = root.getChild("platform_back_beam");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinitionHolder p = new PartDefinitionHolder(mesh.getRoot());

        p.add("head", 60, 0, -5.5F, -6F, -8F, 11, 15, 10, 0F, -10F, -16.5F, -0.1745329F, 0F, 0F);
        p.add("neck", 46, 48, -4.95F, -6F, -8F, 10, 14, 8, 0F, -8F, -10F, -0.2617994F, 0F, 0F);
        p.add("head_bump", 104, 41, -3F, -9F, -6F, 6, 3, 6, 0F, -10F, -16.5F, -0.1745329F, 0F, 0F);
        p.add("chin", 86, 56, -1.5F, -6F, -10.7F, 3, 5, 4, 0F, -10F, -16.5F, 2.054118F, 0F, 0F);
        p.add("lower_lip", 80, 65, -2F, -2F, -14F, 4, 2, 6, 0F, -10F, -16.5F, 1.570796F, 0F, 0F);
        p.add("back", 0, 48, -5F, -10F, -10F, 10, 2, 26, 0F, -4F, -3F, 0F, 0F, 0F);
        p.add("left_small_ear", 102, 0, 2F, -8F, -5F, 8, 10, 1, 0F, -10F, -16.5F, -0.1745329F, -0.5235988F, 0.5235988F);
        p.add("left_big_ear", 102, 0, 2F, -8F, -5F, 12, 14, 1, 0F, -10F, -16.5F, -0.1745329F, -0.5235988F, 0.5235988F);
        p.add("right_small_ear", 106, 15, -10F, -8F, -5F, 8, 10, 1, 0F, -10F, -16.5F, -0.1745329F, 0.5235988F, -0.5235988F);
        p.add("right_big_ear", 102, 15, -14F, -8F, -5F, 12, 14, 1, 0F, -10F, -16.5F, -0.1745329F, 0.5235988F, -0.5235988F);
        p.add("hump", 88, 30, -6F, -2F, -3F, 12, 3, 8, 0F, -13F, -5.5F, 0F, 0F, 0F);
        p.add("body", 0, 0, -8F, -10F, -10F, 16, 20, 28, 0F, -2F, -3F, 0F, 0F, 0F);
        p.add("skirt", 28, 94, -8F, -10F, -6F, 16, 28, 6, 0F, 8F, -3F, 1.570796F, 0F, 0F);

        p.add("right_tusk_a", 2, 60, -3.8F, -3.5F, -19F, 2, 2, 10, 0F, -10F, -16.5F, 1.22173F, 0F, 0.1745329F);
        p.add("right_tusk_b", 0, 0, -3.8F, 6.2F, -24.2F, 2, 2, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, 0.1745329F);
        p.add("right_tusk_c", 0, 18, -3.8F, 17.1F, -21.9F, 2, 2, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("right_tusk_d", 14, 18, -3.8F, 25.5F, -14.5F, 2, 2, 5, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);
        p.add("left_tusk_a", 2, 48, 1.8F, -3.5F, -19F, 2, 2, 10, 0F, -10F, -16.5F, 1.22173F, 0F, -0.1745329F);
        p.add("left_tusk_b", 0, 9, 1.8F, 6.2F, -24.2F, 2, 2, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, -0.1745329F);
        p.add("left_tusk_c", 0, 18, 1.8F, 17.1F, -21.9F, 2, 2, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("left_tusk_d", 14, 18, 1.8F, 25.5F, -14.5F, 2, 2, 5, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);

        p.add("trunk_a", 0, 76, -4F, -2.5F, -18F, 8, 7, 10, 0F, TRUNK_A_Y, TRUNK_A_Z, 1.570796F, 0F, 0F);
        p.add("trunk_b", 0, 93, -3F, -2.5F, -7F, 6, 5, 7, 0F, TRUNK_B_Y, TRUNK_B_Z, 1.658063F, 0F, 0F);
        p.add("trunk_c", 0, 105, -2.5F, -2F, -4F, 5, 4, 5, 0F, TRUNK_C_Y, TRUNK_C_Z, 1.919862F, 0F, 0F);
        p.add("trunk_d", 0, 114, -2F, -1.5F, -5F, 4, 3, 5, 0F, TRUNK_D_Y, TRUNK_D_Z, 2.216568F, 0F, 0F);
        p.add("trunk_e", 0, 122, -1.5F, -1F, -4F, 3, 2, 4, 0F, TRUNK_E_Y, TRUNK_E_Z, 2.530727F, 0F, 0F);

        p.add("front_right_upper_leg", 100, 109, -3.5F, 0F, -3.5F, 7, 12, 7, -4.6F, 4F, -9.6F, 0F, 0F, 0F);
        p.add("front_right_lower_leg", 100, 73, -3.5F, 0F, -3.5F, 7, 10, 7, -4.6F, LOWER_LEG_Y, -9.6F, 0F, 0F, 0F);
        p.add("front_left_upper_leg", 100, 90, -3.5F, 0F, -3.5F, 7, 12, 7, 4.6F, 4F, -9.6F, 0F, 0F, 0F);
        p.add("front_left_lower_leg", 72, 73, -3.5F, 0F, -3.5F, 7, 10, 7, 4.6F, LOWER_LEG_Y, -9.6F, 0F, 0F, 0F);
        p.add("back_right_upper_leg", 72, 109, -3.5F, 0F, -3.5F, 7, 12, 7, -4.6F, 4F, 11.6F, 0F, 0F, 0F);
        p.add("back_right_lower_leg", 100, 56, -3.5F, 0F, -3.5F, 7, 10, 7, -4.6F, LOWER_LEG_Y, 11.6F, 0F, 0F, 0F);
        p.add("back_left_upper_leg", 72, 90, -3.5F, 0F, -3.5F, 7, 12, 7, 4.6F, 4F, 11.6F, 0F, 0F, 0F);
        p.add("back_left_lower_leg", 44, 77, -3.5F, 0F, -3.5F, 7, 10, 7, 4.6F, LOWER_LEG_Y, 11.6F, 0F, 0F, 0F);

        p.add("tail_root", 20, 105, -1F, 0F, -2F, 2, 10, 2, 0F, -8F, 15F, 0.296706F, 0F, 0F);
        p.add("tail", 20, 117, -1F, 9.7F, -0.2F, 2, 6, 2, 0F, -8F, 15F, 0.1134464F, 0F, 0F);
        p.add("tail_plush", 26, 76, -1.5F, 15.5F, -0.7F, 3, 6, 3, 0F, -8F, 15F, 0.1134464F, 0F, 0F);

        p.add("harness_blanket", 0, 196, -8.5F, -2F, -3F, 17, 14, 18, 0F, -13.2F, -3.5F, 0F, 0F, 0F);
        p.add("harness_upper_belt", 70, 196, -8.5F, 0.5F, -2F, 17, 10, 2, 0F, -2F, -2.5F, 0F, 0F, 0F);
        p.add("harness_lower_belt", 70, 196, -8.5F, 0.5F, -2.5F, 17, 10, 2, 0F, -2F, 7F, 0F, 0F, 0F);

        p.add("storage_right_bedroll", 90, 231, -2.5F, 8F, -8F, 3, 3, 16, -9F, -10.2F, 1F, 0F, 0F, 0.418879F);
        p.add("storage_left_bedroll", 90, 231, -0.5F, 8F, -8F, 3, 3, 16, 9F, -10.2F, 1F, 0F, 0F, -0.418879F);
        p.add("storage_front_right_chest", 76, 208, -3.5F, 0F, -5F, 5, 8, 10, -11F, -1.2F, -4.5F, 0F, 0F, -0.2617994F);
        p.add("storage_back_right_chest", 76, 208, -3.5F, 0F, -5F, 5, 8, 10, -11F, -1.2F, 6.5F, 0F, 0F, -0.2617994F);
        p.add("storage_front_left_chest", 76, 226, -1.5F, 0F, -5F, 5, 8, 10, 11F, -1.2F, -4.5F, 0F, 0F, 0.2617994F);
        p.add("storage_back_left_chest", 76, 226, -1.5F, 0F, -5F, 5, 8, 10, 11F, -1.2F, 6.5F, 0F, 0F, 0.2617994F);
        p.add("storage_right_blankets", 0, 228, -4.5F, -1F, -7F, 5, 10, 14, -9F, -10.2F, 1F, 0F, 0F, 0F);
        p.add("storage_left_blankets", 38, 228, -0.5F, -1F, -7F, 5, 10, 14, 9F, -10.2F, 1F, 0F, 0F, 0F);
        p.add("storage_up_left", 76, 226, 6.5F, 1F, -14F, 5, 8, 10, 0F, -16F, 10F, 0F, 0F, -0.3839724F);
        p.add("storage_up_right", 76, 208, -11.5F, 1F, -14F, 5, 8, 10, 0F, -16F, 10F, 0F, 0F, 0.3839724F);

        p.add("tusk_wood_left_1", 56, 166, 1.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, -0.1745329F);
        p.add("tusk_wood_left_2", 60, 158, 1.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_wood_left_3", 58, 149, 1.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_wood_right_1", 56, 166, -4.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, 0.1745329F);
        p.add("tusk_wood_right_2", 60, 158, -4.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_wood_right_3", 58, 149, -4.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("tusk_iron_left_1", 108, 180, 1.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, -0.1745329F);
        p.add("tusk_iron_left_2", 112, 172, 1.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_iron_left_3", 110, 163, 1.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_iron_right_1", 108, 180, -4.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, 0.1745329F);
        p.add("tusk_iron_right_2", 112, 172, -4.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_iron_right_3", 110, 163, -4.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("tusk_diamond_left_1", 108, 207, 1.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, -0.1745329F);
        p.add("tusk_diamond_left_2", 112, 199, 1.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_diamond_left_3", 110, 190, 1.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_diamond_right_1", 108, 207, -4.3F, 5.5F, -24.2F, 3, 3, 7, 0F, -10F, -16.5F, 0.6981317F, 0F, 0.1745329F);
        p.add("tusk_diamond_right_2", 112, 199, -4.29F, 16.5F, -21.9F, 3, 3, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_diamond_right_3", 110, 190, -4.3F, 24.9F, -15.5F, 3, 3, 6, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("tusk_wood_left_4", 46, 164, 2.7F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_wood_left_5", 52, 192, 2.7F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_wood_right_4", 46, 157, -2.8F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_wood_right_5", 52, 199, -2.8F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("tusk_iron_left_4", 96, 175, 2.7F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_iron_left_5", 112, 209, 2.7F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_iron_right_4", 96, 163, -2.8F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_iron_right_5", 112, 216, -2.8F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("tusk_diamond_left_4", 86, 175, 2.7F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, -0.1745329F);
        p.add("tusk_diamond_left_5", 112, 225, 2.7F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, -0.1745329F);
        p.add("tusk_diamond_right_4", 86, 163, -2.8F, 14.5F, -21.9F, 0, 7, 5, 0F, -10F, -16.5F, 0.1745329F, 0F, 0.1745329F);
        p.add("tusk_diamond_right_5", 112, 232, -2.8F, 22.9F, -17.5F, 0, 7, 8, 0F, -10F, -16.5F, -0.3490659F, 0F, 0.1745329F);

        p.add("howdah_pillow", 76, 146, -6.5F, 0F, -6.5F, 13, 4, 13, 0F, -16F, 2F, 0F, 0F, 0F);
        p.add("howdah_left_rail", 56, 147, -7F, 0F, 7F, 14, 1, 1, 0F, -23F + HOWDAH_Y_OFFSET, 1.5F, 0F, 1.570796F, 0F);
        p.add("howdah_cabin", 0, 128, -7F, 0F, -7F, 14, 20, 14, 0F, -40F, 2F, 0F, 0F, 0F);
        p.add("howdah_right_rail", 56, 147, -7F, 0F, 7F, 14, 1, 1, 0F, -23F + HOWDAH_Y_OFFSET, 1.5F, 0F, -1.570796F, 0F);
        p.add("howdah_back_rail", 56, 147, -7F, 0F, 7F, 14, 1, 1, 0F, -23F + HOWDAH_Y_OFFSET, 1.5F, 0F, 0F, 0F);
        p.add("howdah_roof", 56, 128, -7.5F, 0F, -7.5F, 15, 4, 15, 0F, -39.5F, 2F, 0F, 0F, 0F);

        p.add("platform_neck_beam", 26, 180, -12F, 0F, -20.5F, 24, 4, 4, 0F, -16F, 10F, 0F, 0F, 0F);
        p.add("platform_back_beam", 26, 180, -12F, 0F, 0F, 24, 4, 4, 0F, -16F, 10F, 0F, 0F, 0F);
        p.add("platform_floor_1", 0, 176, -0.5F, -20F, -6F, 1, 8, 12, 0F, -16F, 10F, 1.570796F, 0F, 1.570796F);
        p.add("platform_floor_2", 0, 176, -0.5F, -12F, -6F, 1, 8, 12, 0F, -16F, 10F, 1.570796F, 0F, 1.570796F);
        p.add("platform_floor_3", 0, 176, -0.5F, -4F, -6F, 1, 8, 12, 0F, -16F, 10F, 1.570796F, 0F, 1.570796F);
        p.add("platform_back_wall", 0, 176, -5F, -6.2F, -6F, 1, 8, 12, 0F, -16F, 10F, 0F, 1.570796F, 0F);
        p.add("platform_back_left_wall", 0, 176, 6F, -6F, -7F, 1, 8, 12, 0F, -16F, 10F, 0F, 0F, 0F);
        p.add("platform_back_right_wall", 0, 176, -7F, -6F, -7F, 1, 8, 12, 0F, -16F, 10F, 0F, 0F, 0F);

        return LayerDefinition.create(mesh, 128, 256);
    }

    /** Tiny local helper so createBodyLayer() reads as one line per cube, like the original Techne dump. */
    private record PartDefinitionHolder(net.minecraft.client.model.geom.builders.PartDefinition root) {
        void add(String name, int u, int v, float bx, float by, float bz, int w, int h, int d,
                 float px, float py, float pz, float rx, float ry, float rz) {
            root.addOrReplaceChild(name,
                    CubeListBuilder.create().texOffs(u, v).addBox(bx, by, bz, w, h, d),
                    PartPose.offsetAndRotation(px, py, pz, rx, ry, rz));
        }
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCElephantEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        ElephantVariant variant = entity.getVariant();

        // Reset the parts the IK-chain below mutates back to their rest pose
        // FIRST — otherwise each frame's "distance" would be computed from
        // last frame's already-rotated position and the chain would drift.
        trunkA.y = TRUNK_A_Y;
        trunkB.y = TRUNK_B_Y;
        trunkC.y = TRUNK_C_Y;
        trunkD.y = TRUNK_D_Y;
        trunkE.y = TRUNK_E_Y;
        frontRightLowerLeg.y = LOWER_LEG_Y;
        frontLeftLowerLeg.y = LOWER_LEG_Y;
        backRightLowerLeg.y = LOWER_LEG_Y;
        backLeftLowerLeg.y = LOWER_LEG_Y;

        float rLegXRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.8F * limbSwingAmount;
        float lLegXRot = Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;

        float headXRot = Math.max(headPitch, 0F) / R;
        float headYRot = Mth.clamp(netHeadYaw, -20F, 20F) / R;

        // Random idle trunk sway (server-driven counter — see MoCElephantEntity#getTrunkTicks).
        int trunkCounter = entity.getTrunkTicks();
        float trunkXRot = 0F;
        if (trunkCounter != 0) {
            headXRot = 0F;
            trunkXRot = Mth.cos(trunkCounter * 0.2F) * 12F;
        }

        head.xRot = -10F / R + headXRot;
        head.yRot = headYRot;
        headBump.xRot = head.xRot;
        headBump.yRot = head.yRot;

        rightTuskA.yRot = headYRot;
        leftTuskA.yRot = headYRot;
        rightTuskA.xRot = 70F / R + headXRot;
        leftTuskA.xRot = 70F / R + headXRot;

        chin.yRot = headYRot;
        chin.xRot = 113F / R + headXRot;
        lowerLip.yRot = headYRot;
        lowerLip.xRot = 85F / R + headXRot;

        // Random idle ear flap (server-driven counter) layered on top of head tracking.
        int earCounter = entity.getEarTicks();
        float earF = earCounter != 0 ? Mth.cos(earCounter * 0.5F) * 0.35F : 0F;

        rightBigEar.yRot = 30F / R + headYRot + earF;
        rightSmallEar.yRot = 30F / R + headYRot + earF;
        leftBigEar.yRot = -30F / R + headYRot - earF;
        leftSmallEar.yRot = -30F / R + headYRot - earF;
        rightBigEar.xRot = -10F / R + headXRot;
        rightSmallEar.xRot = -10F / R + headXRot;
        leftBigEar.xRot = -10F / R + headXRot;
        leftSmallEar.xRot = -10F / R + headXRot;

        // Trunk: each segment chases the one before it (IK-chain), matching
        // the original's adjustAllRotationPoints exactly.
        trunkA.z = TRUNK_A_Z; // matches the original's explicit Z-pin before the chain runs
        adjustAllRotationPoints(trunkA, head);
        trunkA.yRot = headYRot;
        trunkA.xRot = Math.max(90F - trunkXRot, 85F) / R + headXRot;

        trunkB.z = TRUNK_B_Z;
        adjustAllRotationPoints(trunkB, trunkA);
        trunkB.yRot = headYRot;
        trunkB.xRot = (95F - trunkXRot * 1.5F) / R + headXRot;

        trunkC.z = TRUNK_C_Z;
        adjustAllRotationPoints(trunkC, trunkB);
        trunkC.yRot = headYRot;
        trunkC.xRot = (110F - trunkXRot * 3F) / R + headXRot;

        trunkD.z = TRUNK_D_Z;
        adjustAllRotationPoints(trunkD, trunkC);
        trunkD.yRot = headYRot;
        trunkD.xRot = (127F - trunkXRot * 4.5F) / R + headXRot;

        trunkE.z = TRUNK_E_Z;
        adjustAllRotationPoints(trunkE, trunkD);
        trunkE.yRot = headYRot;
        trunkE.xRot = (145F - trunkXRot * 6F) / R + headXRot;

        // Legs.
        boolean sitting = entity.isSittingSynced() && !entity.isVehicle();
        this.yOffset = sitting ? 0.5F : 0.0F;
        if (sitting) {
            frontRightUpperLeg.xRot = -30F / R;
            frontLeftUpperLeg.xRot = -30F / R;
            backLeftUpperLeg.xRot = -30F / R;
            backRightUpperLeg.xRot = -30F / R;
        } else {
            frontRightUpperLeg.xRot = rLegXRot;
            frontLeftUpperLeg.xRot = lLegXRot;
            backLeftUpperLeg.xRot = rLegXRot;
            backRightUpperLeg.xRot = lLegXRot;
        }

        adjustXRotationPoints(frontRightLowerLeg, frontRightUpperLeg);
        adjustXRotationPoints(backRightLowerLeg, backRightUpperLeg);
        adjustXRotationPoints(frontLeftLowerLeg, frontLeftUpperLeg);
        adjustXRotationPoints(backLeftLowerLeg, backLeftUpperLeg);

        if (sitting) {
            frontLeftLowerLeg.xRot = 90F / R;
            frontRightLowerLeg.xRot = 90F / R;
            backLeftLowerLeg.xRot = 90F / R;
            backRightLowerLeg.xRot = 90F / R;
        } else {
            float lLegXRotDeg = lLegXRot * (180F / (float) Math.PI);
            float rLegXRotDeg = rLegXRot * (180F / (float) Math.PI);
            if (lLegXRotDeg > 0F) {
                lLegXRotDeg *= 2F;
            }
            if (rLegXRotDeg > 0F) {
                rLegXRotDeg *= 2F;
            }
            frontLeftLowerLeg.xRot = lLegXRotDeg / R;
            frontRightLowerLeg.xRot = rLegXRotDeg / R;
            backLeftLowerLeg.xRot = rLegXRotDeg / R;
            backRightLowerLeg.xRot = lLegXRotDeg / R;
        }

        // Natural tusks only show with nothing equipped; otherwise the matching
        // material's geometry (same angles, different texture) takes their place.
        int tuskTier = entity.getTuskTier();
        boolean naturalTusks = tuskTier == 0;
        leftTuskB.visible = naturalTusks;
        leftTuskC.visible = naturalTusks;
        leftTuskD.visible = naturalTusks;
        rightTuskB.visible = naturalTusks;
        rightTuskC.visible = naturalTusks;
        rightTuskD.visible = naturalTusks;

        leftTuskB.yRot = headYRot;
        leftTuskC.yRot = headYRot;
        leftTuskD.yRot = headYRot;
        rightTuskB.yRot = headYRot;
        rightTuskC.yRot = headYRot;
        rightTuskD.yRot = headYRot;
        leftTuskB.xRot = 40F / R + headXRot;
        leftTuskC.xRot = 10F / R + headXRot;
        leftTuskD.xRot = -20F / R + headXRot;
        rightTuskB.xRot = 40F / R + headXRot;
        rightTuskC.xRot = 10F / R + headXRot;
        rightTuskD.xRot = -20F / R + headXRot;

        tuskWoodLeft1.visible = tuskTier == 1;
        tuskWoodLeft2.visible = tuskTier == 1;
        tuskWoodLeft3.visible = tuskTier == 1;
        tuskWoodLeft4.visible = tuskTier == 1;
        tuskWoodLeft5.visible = tuskTier == 1;
        tuskWoodRight1.visible = tuskTier == 1;
        tuskWoodRight2.visible = tuskTier == 1;
        tuskWoodRight3.visible = tuskTier == 1;
        tuskWoodRight4.visible = tuskTier == 1;
        tuskWoodRight5.visible = tuskTier == 1;
        tuskIronLeft1.visible = tuskTier == 2;
        tuskIronLeft2.visible = tuskTier == 2;
        tuskIronLeft3.visible = tuskTier == 2;
        tuskIronLeft4.visible = tuskTier == 2;
        tuskIronLeft5.visible = tuskTier == 2;
        tuskIronRight1.visible = tuskTier == 2;
        tuskIronRight2.visible = tuskTier == 2;
        tuskIronRight3.visible = tuskTier == 2;
        tuskIronRight4.visible = tuskTier == 2;
        tuskIronRight5.visible = tuskTier == 2;
        tuskDiamondLeft1.visible = tuskTier == 3;
        tuskDiamondLeft2.visible = tuskTier == 3;
        tuskDiamondLeft3.visible = tuskTier == 3;
        tuskDiamondLeft4.visible = tuskTier == 3;
        tuskDiamondLeft5.visible = tuskTier == 3;
        tuskDiamondRight1.visible = tuskTier == 3;
        tuskDiamondRight2.visible = tuskTier == 3;
        tuskDiamondRight3.visible = tuskTier == 3;
        tuskDiamondRight4.visible = tuskTier == 3;
        tuskDiamondRight5.visible = tuskTier == 3;

        if (tuskTier != 0) {
            ModelPart[] equippedTusks = switch (tuskTier) {
                case 1 -> new ModelPart[]{tuskWoodLeft1, tuskWoodLeft2, tuskWoodLeft3, tuskWoodLeft4, tuskWoodLeft5,
                        tuskWoodRight1, tuskWoodRight2, tuskWoodRight3, tuskWoodRight4, tuskWoodRight5};
                case 2 -> new ModelPart[]{tuskIronLeft1, tuskIronLeft2, tuskIronLeft3, tuskIronLeft4, tuskIronLeft5,
                        tuskIronRight1, tuskIronRight2, tuskIronRight3, tuskIronRight4, tuskIronRight5};
                default -> new ModelPart[]{tuskDiamondLeft1, tuskDiamondLeft2, tuskDiamondLeft3, tuskDiamondLeft4, tuskDiamondLeft5,
                        tuskDiamondRight1, tuskDiamondRight2, tuskDiamondRight3, tuskDiamondRight4, tuskDiamondRight5};
            };
            // Segments 2 and 5 share the same angle (10°), 3 and 4 share -20°, matching the original's layout.
            float[] baseXRot = {40F, 10F, -20F, 10F, -20F, 40F, 10F, -20F, 10F, -20F};
            for (int i = 0; i < equippedTusks.length; i++) {
                equippedTusks[i].yRot = headYRot;
                equippedTusks[i].xRot = baseXRot[i] / R + headXRot;
            }
        }
        // Tail: gentle idle sway, livelier swish while the counter is running.
        int tailCounter = entity.getTailTicks();
        float tailMov = Math.max(limbSwingAmount * 0.9F, 0F);
        if (tailCounter != 0) {
            tailRoot.yRot = Mth.cos(ageInTicks * 0.4F) * 1.3F;
            tailMov = 30F / R;
        } else {
            tailRoot.yRot = 0F;
        }
        tailRoot.xRot = 17F / R + tailMov;
        tailPlush.xRot = tail.xRot = 6.5F / R + tailMov;
        tailPlush.yRot = tailRoot.yRot;
        tail.yRot = tailPlush.yRot;

        // Which optional parts show, per species.
        boolean bigEars = variant.hasBigEars();
        leftBigEar.visible = bigEars;
        rightBigEar.visible = bigEars;
        leftSmallEar.visible = !bigEars;
        rightSmallEar.visible = !bigEars;

        boolean mammoth = variant.isMammoth();
        headBump.visible = mammoth;
        skirt.visible = mammoth;

        // Eating: brief open-close on the chin/lower lip, layered on top of their normal head tracking.
        int eatTicks = entity.getEatTicks();
        if (eatTicks != 0) {
            float eatMov = Mth.cos((eatTicks - 10) * 0.3F) * 0.5F;
            chin.xRot += eatMov;
            lowerLip.xRot += eatMov;
        }

        boolean harnessed = entity.hasHarness();
        harnessBlanket.visible = harnessed;
        harnessUpperBelt.visible = harnessed;
        harnessLowerBelt.visible = harnessed;

        int chestCount = entity.getChestCount();
        storageRightBedroll.visible = chestCount >= 1;
        storageFrontRightChest.visible = chestCount >= 1;
        storageBackRightChest.visible = chestCount >= 1;
        storageRightBlankets.visible = chestCount >= 1;
        storageLeftBedroll.visible = chestCount >= 2;
        storageFrontLeftChest.visible = chestCount >= 2;
        storageBackLeftChest.visible = chestCount >= 2;
        storageLeftBlankets.visible = chestCount >= 2;
        storageUpLeft.visible = chestCount >= 3;
        storageUpRight.visible = chestCount >= 4;

        boolean howdah = entity.hasHowdah();
        howdahPillow.visible = howdah;
        howdahLeftRail.visible = howdah;
        howdahCabin.visible = howdah;
        howdahRightRail.visible = howdah;
        howdahBackRail.visible = howdah;
        howdahRoof.visible = howdah;
        if (howdah) {
            howdahPillow.xScale = howdahPillow.yScale = howdahPillow.zScale = HOWDAH_SCALE;
            howdahLeftRail.xScale = howdahLeftRail.yScale = howdahLeftRail.zScale = HOWDAH_SCALE;
            howdahCabin.xScale = howdahCabin.yScale = howdahCabin.zScale = HOWDAH_SCALE;
            howdahRightRail.xScale = howdahRightRail.yScale = howdahRightRail.zScale = HOWDAH_SCALE;
            howdahBackRail.xScale = howdahBackRail.yScale = howdahBackRail.zScale = HOWDAH_SCALE;
            howdahRoof.xScale = howdahRoof.yScale = howdahRoof.zScale = HOWDAH_SCALE;
        }

        boolean platform = entity.hasPlatform();
        platformFloor1.visible = platform;
        platformFloor2.visible = platform;
        platformFloor3.visible = platform;
        platformBackWall.visible = platform;
        platformBackLeftWall.visible = platform;
        platformBackRightWall.visible = platform;
        platformNeckBeam.visible = platform;
        platformBackBeam.visible = platform;
    }

    /** Ported verbatim from the original's adjustXRotationPoints (leg follow-through). */
    private static void adjustXRotationPoints(ModelPart target, ModelPart origin) {
        float distance = Math.abs(target.y - origin.y);
        target.z = origin.z + Mth.sin(origin.xRot) * distance;
        target.y = origin.y + Mth.cos(origin.xRot) * distance;
    }

    /** Ported verbatim from the original's adjustAllRotationPoints (trunk IK-chain). */
    private static void adjustAllRotationPoints(ModelPart target, ModelPart origin) {
        float distanceY = Math.abs(target.y - origin.y);
        target.y = origin.y + Mth.sin(origin.xRot) * distanceY;
        target.z = origin.z - Mth.cos(origin.yRot) * (Mth.cos(origin.xRot) * distanceY);
        target.x = origin.x - Mth.sin(origin.yRot) * (Mth.cos(origin.xRot) * distanceY);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0.0, this.yOffset, 0.0);
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}