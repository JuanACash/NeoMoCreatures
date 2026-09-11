package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCOstrichEntity;
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

public class MoCOstrichModel extends HierarchicalModel<MoCOstrichEntity> {

    private static final float R = 57.29578F;

    private final ModelPart root;
    private final ModelPart uBeak;
    private final ModelPart uBeak2;
    private final ModelPart uBeakB;
    private final ModelPart uBeak2B;
    private final ModelPart lBeak;
    private final ModelPart lBeakB;
    private final ModelPart lBeak2;
    private final ModelPart lBeak2B;
    private final ModelPart body;
    private final ModelPart lLegA;
    private final ModelPart lLegB;
    private final ModelPart lLegC;
    private final ModelPart lFoot;
    private final ModelPart rLegA;
    private final ModelPart rLegB;
    private final ModelPart rLegC;
    private final ModelPart rFoot;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tailBase;
    private final ModelPart tailPart1;
    private final ModelPart tailPart2;
    private final ModelPart tailPart3;
    private final ModelPart tailPart4;
    private final ModelPart tailPart5;    
    private final ModelPart lWingB;
    private final ModelPart lWingC;
    private final ModelPart lWingD;
    private final ModelPart lWingE;
    private final ModelPart rWingB;
    private final ModelPart rWingC;
    private final ModelPart rWingD;
    private final ModelPart rWingE;
    private final ModelPart neckLFeather;
    private final ModelPart neckUFeather;
    private final ModelPart neckD;
    private final ModelPart neckU;
    private final ModelPart neckL;
    private final ModelPart head;
    private final ModelPart saddleA;
    private final ModelPart saddleB;
    private final ModelPart saddleC;
    private final ModelPart saddleL;
    private final ModelPart saddleR;
    private final ModelPart saddleL2;
private final ModelPart saddleR2;
private final ModelPart chestBag;
private final ModelPart flagpole;
private final ModelPart flagWhite;
private final ModelPart flagOrange;
private final ModelPart flagMagenta;
private final ModelPart flagLightBlue;
private final ModelPart flagYellow;
private final ModelPart flagLime;
private final ModelPart flagPink;
private final ModelPart flagGray;
private final ModelPart flagLightGray;
private final ModelPart flagCyan;
private final ModelPart flagPurple;
private final ModelPart flagBlue;
private final ModelPart flagBrown;
private final ModelPart flagGreen;
private final ModelPart flagRed;
private final ModelPart flagBlack;
private final ModelPart helmetLeather;
private final ModelPart helmetIron;
private final ModelPart helmetGold;
private final ModelPart helmetDiamond;
private final ModelPart helmetHide;
private final ModelPart helmetNeckHide;
private final ModelPart helmetHideEar1;
private final ModelPart helmetHideEar2;
private final ModelPart helmetFur;
private final ModelPart helmetNeckFur;
private final ModelPart helmetFurEar1;
private final ModelPart helmetFurEar2;
private final ModelPart helmetReptile;
private final ModelPart helmetReptileEar1;
private final ModelPart helmetReptileEar2;
private final ModelPart helmetScorpDirt;
private final ModelPart helmetScorpCave;
private final ModelPart helmetScorpFrost;
private final ModelPart helmetScorpNether;
private final ModelPart helmetScorpUndead;
private final ModelPart uniHorn;

    public MoCOstrichModel(ModelPart root) {
        this.root = root;
        this.uBeak = root.getChild("u_beak");
        this.uBeak2 = root.getChild("u_beak2");
        this.uBeakB = root.getChild("u_beak_b");
        this.uBeak2B = root.getChild("u_beak2_b");
        this.lBeak = root.getChild("l_beak");
        this.lBeakB = root.getChild("l_beak_b");
        this.lBeak2 = root.getChild("l_beak2");
        this.lBeak2B = root.getChild("l_beak2_b");
        this.body = root.getChild("body");
        this.lLegA = root.getChild("l_leg_a");
        this.lLegB = root.getChild("l_leg_b");
        this.lLegC = root.getChild("l_leg_c");
        this.lFoot = root.getChild("l_foot");
        this.rLegA = root.getChild("r_leg_a");
        this.rLegB = root.getChild("r_leg_b");
        this.rLegC = root.getChild("r_leg_c");
        this.rFoot = root.getChild("r_foot");
        this.tail1 = root.getChild("tail1");
        this.tail2 = root.getChild("tail2");
        this.tail3 = root.getChild("tail3");
        this.tailBase = root.getChild("tail_base");
        this.tailPart1 = root.getChild("tail_part1");
        this.tailPart2 = root.getChild("tail_part2");
        this.tailPart3 = root.getChild("tail_part3");
        this.tailPart4 = root.getChild("tail_part4");
        this.tailPart5 = root.getChild("tail_part5");
        this.lWingB = root.getChild("l_wing_b");
        this.lWingC = root.getChild("l_wing_c");
        this.lWingD = root.getChild("l_wing_d");
        this.lWingE = root.getChild("l_wing_e");
        this.rWingB = root.getChild("r_wing_b");
        this.rWingC = root.getChild("r_wing_c");
        this.rWingD = root.getChild("r_wing_d");
        this.rWingE = root.getChild("r_wing_e");
        this.neckLFeather = root.getChild("neck_l_feather");
        this.neckUFeather = root.getChild("neck_u_feather");
        this.neckD = root.getChild("neck_d");
        this.neckU = root.getChild("neck_u");
        this.neckL = root.getChild("neck_l");
        this.head = root.getChild("head");
        this.saddleA = root.getChild("saddle_a");
        this.saddleB = root.getChild("saddle_b");
        this.saddleC = root.getChild("saddle_c");
        this.saddleL = root.getChild("saddle_l");
        this.saddleR = root.getChild("saddle_r");
        this.saddleL2 = root.getChild("saddle_l2");
        this.saddleR2 = root.getChild("saddle_r2");
        this.chestBag = root.getChild("chest_bag");
        this.flagpole = root.getChild("flagpole");
        this.flagWhite = root.getChild("flag_white");
        this.flagOrange = root.getChild("flag_orange");
        this.flagMagenta = root.getChild("flag_magenta");
        this.flagLightBlue = root.getChild("flag_light_blue");
        this.flagYellow = root.getChild("flag_yellow");
        this.flagLime = root.getChild("flag_lime");
        this.flagPink = root.getChild("flag_pink");
        this.flagGray = root.getChild("flag_gray");
        this.flagLightGray = root.getChild("flag_light_gray");
        this.flagCyan = root.getChild("flag_cyan");
        this.flagPurple = root.getChild("flag_purple");
        this.flagBlue = root.getChild("flag_blue");
        this.flagBrown = root.getChild("flag_brown");
        this.flagGreen = root.getChild("flag_green");
        this.flagRed = root.getChild("flag_red");
        this.flagBlack = root.getChild("flag_black");
        this.helmetLeather = root.getChild("helmet_leather");
        this.helmetIron = root.getChild("helmet_iron");
        this.helmetGold = root.getChild("helmet_gold");
        this.helmetDiamond = root.getChild("helmet_diamond");
        this.helmetHide = root.getChild("helmet_hide");
        this.helmetNeckHide = root.getChild("helmet_neck_hide");
        this.helmetHideEar1 = root.getChild("helmet_hide_ear1");
        this.helmetHideEar2 = root.getChild("helmet_hide_ear2");
        this.helmetFur = root.getChild("helmet_fur");
        this.helmetNeckFur = root.getChild("helmet_neck_fur");
        this.helmetFurEar1 = root.getChild("helmet_fur_ear1");
        this.helmetFurEar2 = root.getChild("helmet_fur_ear2");
        this.helmetReptile = root.getChild("helmet_reptile");
        this.helmetReptileEar1 = root.getChild("helmet_reptile_ear1");
        this.helmetReptileEar2 = root.getChild("helmet_reptile_ear2");
        this.helmetScorpDirt = root.getChild("helmet_scorp_dirt");
        this.helmetScorpCave = root.getChild("helmet_scorp_cave");
        this.helmetScorpFrost = root.getChild("helmet_scorp_frost");
        this.helmetScorpNether = root.getChild("helmet_scorp_nether");
        this.helmetScorpUndead = root.getChild("helmet_scorp_undead");
        this.uniHorn = root.getChild("uni_horn");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("u_beak",
                CubeListBuilder.create().texOffs(12, 16).addBox(-1.5F, -15F, -5.5F, 3, 1, 1),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("u_beak2",
                CubeListBuilder.create().texOffs(20, 16).addBox(-1F, -15F, -7.5F, 2, 1, 2),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("u_beak_b",
                CubeListBuilder.create().texOffs(12, 16).addBox(-1.5F, -15F, -6.5F, 3, 1, 1),
                PartPose.offsetAndRotation(0F, 3F, -6F, -4F / R, 0F, 0F));
        root.addOrReplaceChild("u_beak2_b",
                CubeListBuilder.create().texOffs(20, 16).addBox(-1F, -15F, -8.5F, 2, 1, 2),
                PartPose.offsetAndRotation(0F, 3F, -6F, -4F / R, 0F, 0F));
        root.addOrReplaceChild("l_beak",
                CubeListBuilder.create().texOffs(12, 22).addBox(-1.5F, -14F, -5.5F, 3, 1, 1),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("l_beak_b",
                CubeListBuilder.create().texOffs(12, 22).addBox(-1.5F, -14F, -3.9F, 3, 1, 1),
                PartPose.offsetAndRotation(0F, 3F, -6F, 7F / R, 0F, 0F));
        root.addOrReplaceChild("l_beak2",
                CubeListBuilder.create().texOffs(20, 22).addBox(-1F, -14F, -7.5F, 2, 1, 2),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("l_beak2_b",
                CubeListBuilder.create().texOffs(20, 22).addBox(-1F, -14F, -5.9F, 2, 1, 2),
                PartPose.offsetAndRotation(0F, 3F, -6F, 7F / R, 0F, 0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 38).addBox(-4F, 1F, 0F, 8, 10, 16),
                PartPose.offset(0F, 0F, -6F));

        root.addOrReplaceChild("l_leg_a",
                CubeListBuilder.create().texOffs(50, 28).addBox(-2F, -1F, -2.5F, 4, 6, 5),
                PartPose.offsetAndRotation(4F, 5F, 4F, 10F / R, 0F, 0F));
        root.addOrReplaceChild("l_leg_b",
                CubeListBuilder.create().texOffs(50, 39).addBox(-1.5F, 5F, -1.5F, 3, 4, 3),
                PartPose.offsetAndRotation(4F, 5F, 4F, 10F / R, 0F, 0F));
        root.addOrReplaceChild("l_leg_c",
                CubeListBuilder.create().texOffs(8, 38).addBox(-1F, 8F, 2.5F, 2, 10, 2),
                PartPose.offsetAndRotation(4F, 5F, 4F, -15F / R, 0F, 0F));
        root.addOrReplaceChild("l_foot",
                CubeListBuilder.create().texOffs(32, 42).addBox(-1F, 17F, -9F, 2, 1, 5),
                PartPose.offsetAndRotation(4F, 5F, 4F, 10F / R, 0F, 0F));

        root.addOrReplaceChild("r_leg_a",
                CubeListBuilder.create().texOffs(0, 27).addBox(-2F, -1F, -2.5F, 4, 6, 5),
                PartPose.offsetAndRotation(-4F, 5F, 4F, 10F / R, 0F, 0F));
        root.addOrReplaceChild("r_leg_b",
                CubeListBuilder.create().texOffs(18, 27).addBox(-1.5F, 5F, -1.5F, 3, 4, 3),
                PartPose.offsetAndRotation(-4F, 5F, 4F, 10F / R, 0F, 0F));
        root.addOrReplaceChild("r_leg_c",
                CubeListBuilder.create().texOffs(0, 38).addBox(-1F, 8F, 2.5F, 2, 10, 2),
                PartPose.offsetAndRotation(-4F, 5F, 4F, -15F / R, 0F, 0F));
        root.addOrReplaceChild("r_foot",
                CubeListBuilder.create().texOffs(32, 48).addBox(-1F, 17F, -9F, 2, 1, 5),
                PartPose.offsetAndRotation(-4F, 5F, 4F, 10F / R, 0F, 0F));

        root.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(44, 18).addBox(-0.5F, -2F, -2F, 1, 4, 6),
                PartPose.offsetAndRotation(0F, 4F, 15F, 20F / R, 0F, 0F));
        root.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(58, 18).addBox(-2.6F, -2F, -2F, 1, 4, 6),
                PartPose.offsetAndRotation(0F, 4F, 15F, 20F / R, -15F / R, 0F));
        root.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(30, 18).addBox(1.6F, -2F, -2F, 1, 4, 6),
                PartPose.offsetAndRotation(0F, 4F, 15F, 20F / R, 15F / R, 0F));

        root.addOrReplaceChild("tail_base",
                CubeListBuilder.create().texOffs(30, 28).addBox(-2.5F, -1F, 0F, 5, 5, 5),
                PartPose.offset(0F, 4F, 10F));
        root.addOrReplaceChild("tail_part1",
                CubeListBuilder.create().texOffs(30, 28).addBox(-2.5F, -2.2F, 5F, 5, 5, 5),
                PartPose.offsetAndRotation(0F, 4F, 10F, -0.2974289F, 0F, 0F));
        root.addOrReplaceChild("tail_part2",
                CubeListBuilder.create().texOffs(60, 73).addBox(-2.5F, -4.3F, 9F, 5, 5, 8),
                PartPose.offsetAndRotation(0F, 4F, 10F, -0.5205006F, 0F, 0F));
        root.addOrReplaceChild("tail_part3",
                CubeListBuilder.create().texOffs(60, 86).addBox(-2F, 1F, 16F, 4, 4, 7),
                PartPose.offsetAndRotation(0F, 4F, 10F, -0.2230717F, 0F, 0F));
        root.addOrReplaceChild("tail_part4",
                CubeListBuilder.create().texOffs(60, 97).addBox(-1.5F, 8F, 20.6F, 3, 3, 7),
                PartPose.offsetAndRotation(0F, 4F, 10F, 0.0743572F, 0F, 0F));
        root.addOrReplaceChild("tail_part5",
                CubeListBuilder.create().texOffs(60, 107).addBox(-1F, 16.5F, 22.9F, 2, 2, 5),
                PartPose.offsetAndRotation(0F, 4F, 10F, 0.4089647F, 0F, 0F));

        root.addOrReplaceChild("l_wing_b",
                CubeListBuilder.create().texOffs(68, 46).addBox(-0.5F, -3F, 0F, 1, 4, 14),
                PartPose.offsetAndRotation(4F, 4F, -3F, 5F / R, 5F / R, 0F));
        root.addOrReplaceChild("l_wing_c",
                CubeListBuilder.create().texOffs(98, 46).addBox(-1F, 0F, 0F, 1, 4, 14),
                PartPose.offsetAndRotation(4F, 4F, -3F, 0F, 5F / R, 0F));
        root.addOrReplaceChild("l_wing_d",
                CubeListBuilder.create().texOffs(26, 84).addBox(0F, -1F, -1F, 15, 2, 2),
                PartPose.offsetAndRotation(4F, 3F, -3F, 0F, 0F, -20F / R));
        root.addOrReplaceChild("l_wing_e",
                CubeListBuilder.create().texOffs(0, 103).addBox(0F, 0F, 1F, 15, 0, 15),
                PartPose.offsetAndRotation(4F, 3F, -3F, 0F, 0F, -20F / R));

        root.addOrReplaceChild("r_wing_b",
                CubeListBuilder.create().texOffs(68, 0).addBox(-0.5F, -3F, 0F, 1, 4, 14),
                PartPose.offsetAndRotation(-4F, 4F, -3F, 5F / R, -5F / R, 0F));
        root.addOrReplaceChild("r_wing_c",
                CubeListBuilder.create().texOffs(98, 0).addBox(0F, 0F, 0F, 1, 4, 14),
                PartPose.offsetAndRotation(-4F, 4F, -3F, 0F, -5F / R, 0F));
        root.addOrReplaceChild("r_wing_d",
                CubeListBuilder.create().texOffs(26, 80).addBox(-15F, -1F, -1F, 15, 2, 2),
                PartPose.offsetAndRotation(-4F, 3F, -3F, 0F, 0F, 20F / R));
        root.addOrReplaceChild("r_wing_e",
                CubeListBuilder.create().texOffs(0, 88).addBox(-15F, 0F, 1F, 15, 0, 15),
                PartPose.offsetAndRotation(-4F, 3F, -3F, 0F, 0F, 20F / R));

        root.addOrReplaceChild("neck_l_feather",
                CubeListBuilder.create().texOffs(8, 73).addBox(0F, -8F, -0.5F, 0, 7, 4),
                PartPose.offsetAndRotation(0F, 3F, -6F, 11.5F / R, 0F, 0F));
        root.addOrReplaceChild("neck_u_feather",
                CubeListBuilder.create().texOffs(0, 73).addBox(0F, -16F, -2F, 0, 9, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("neck_d",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -4F, -2F, 3, 8, 3),
                PartPose.offsetAndRotation(0F, 3F, -6F, 25F / R, 0F, 0F));
        root.addOrReplaceChild("neck_u",
                CubeListBuilder.create().texOffs(20, 0).addBox(-1F, -12F, -4F, 2, 5, 2),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("neck_l",
                CubeListBuilder.create().texOffs(20, 7).addBox(-1F, -8F, -2.5F, 2, 5, 2),
                PartPose.offsetAndRotation(0F, 3F, -6F, 11.5F / R, 0F, 0F));
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -16F, -4.5F, 3, 4, 3),
                PartPose.offset(0F, 3F, -6F));

        root.addOrReplaceChild("saddle_a",
                CubeListBuilder.create().texOffs(72, 18).addBox(-4F, 0.5F, -3F, 8, 1, 8), PartPose.ZERO);
        root.addOrReplaceChild("saddle_b",
                CubeListBuilder.create().texOffs(72, 27).addBox(-1.5F, 0F, -3F, 3, 1, 2), PartPose.ZERO);
        root.addOrReplaceChild("saddle_c",
                CubeListBuilder.create().texOffs(84, 27).addBox(-4F, 0F, 3F, 8, 1, 2), PartPose.ZERO);
        root.addOrReplaceChild("saddle_l",
                CubeListBuilder.create().texOffs(72, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1), PartPose.offset(4F, 1F, 0F));
        root.addOrReplaceChild("saddle_r",
                CubeListBuilder.create().texOffs(84, 30).addBox(-0.5F, 0F, -0.5F, 1, 6, 1), PartPose.offset(-4F, 1F, 0F));
        root.addOrReplaceChild("saddle_l2",
                CubeListBuilder.create().texOffs(76, 30).addBox(-0.5F, 6F, -1F, 1, 2, 2), PartPose.offset(4F, 1F, 0F));
        root.addOrReplaceChild("saddle_r2",
                CubeListBuilder.create().texOffs(88, 30).addBox(-0.5F, 6F, -1F, 1, 2, 2), PartPose.offset(-4F, 1F, 0F));

        root.addOrReplaceChild("chest_bag",
                CubeListBuilder.create().texOffs(32, 7).addBox(-4.5F, -3F, 5F, 9, 4, 7),
                PartPose.rotation(-14.91F / R, 0F, 0F));

        root.addOrReplaceChild("flagpole",
                CubeListBuilder.create().texOffs(28, 0).addBox(-0.5F, -15F, -0.5F, 1, 17, 1),
                PartPose.offsetAndRotation(0F, 0F, 5F, -14.91F / R, 0F, 0F));

        java.util.Map<String, int[]> flagTexOffsets = new java.util.LinkedHashMap<>();
        flagTexOffsets.put("flag_black", new int[]{108, 8});
        flagTexOffsets.put("flag_gray", new int[]{108, 16});
        flagTexOffsets.put("flag_yellow", new int[]{48, 46});
        flagTexOffsets.put("flag_brown", new int[]{48, 42});
        flagTexOffsets.put("flag_green", new int[]{48, 38});
        flagTexOffsets.put("flag_cyan", new int[]{48, 50});
        flagTexOffsets.put("flag_light_blue", new int[]{68, 32});
        flagTexOffsets.put("flag_blue", new int[]{68, 28});
        flagTexOffsets.put("flag_purple", new int[]{88, 32});
        flagTexOffsets.put("flag_magenta", new int[]{88, 28});
        flagTexOffsets.put("flag_lime", new int[]{108, 32});
        flagTexOffsets.put("flag_pink", new int[]{108, 28});
        flagTexOffsets.put("flag_red", new int[]{108, 24});
        flagTexOffsets.put("flag_white", new int[]{108, 20});
        flagTexOffsets.put("flag_light_gray", new int[]{108, 12});
        flagTexOffsets.put("flag_orange", new int[]{88, 24});
        for (var entry : flagTexOffsets.entrySet()) {
        root.addOrReplaceChild(entry.getKey(),
                CubeListBuilder.create().texOffs(entry.getValue()[0], entry.getValue()[1]).addBox(0F, -2.1F, 0F, 0, 4, 10),
                PartPose.offsetAndRotation(0F, -12F, 8F, -14.91F / R, 0F, 0F));
        }

        root.addOrReplaceChild("helmet_leather",
                CubeListBuilder.create().texOffs(66, 0).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_iron",
                CubeListBuilder.create().texOffs(84, 46).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_gold",
                CubeListBuilder.create().texOffs(112, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_diamond",
                CubeListBuilder.create().texOffs(96, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));

        root.addOrReplaceChild("helmet_hide",
                CubeListBuilder.create().texOffs(96, 5).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_neck_hide",
                CubeListBuilder.create().texOffs(58, 0).addBox(-1.5F, -12F, -4.5F, 3, 1, 3),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_hide_ear1",
                CubeListBuilder.create().texOffs(84, 9).addBox(-2.5F, -18F, -3F, 2, 2, 1),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_hide_ear2",
                CubeListBuilder.create().texOffs(90, 9).addBox(0.5F, -18F, -3F, 2, 2, 1),
                PartPose.offset(0F, 3F, -6F));

        root.addOrReplaceChild("helmet_fur",
                CubeListBuilder.create().texOffs(84, 0).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_neck_fur",
                CubeListBuilder.create().texOffs(96, 0).addBox(-1.5F, -12F, -4.5F, 3, 1, 3),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_fur_ear1",
                CubeListBuilder.create().texOffs(66, 9).addBox(-2.5F, -18F, -3F, 2, 2, 1),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_fur_ear2",
                CubeListBuilder.create().texOffs(76, 9).addBox(0.5F, -18F, -3F, 2, 2, 1),
                PartPose.offset(0F, 3F, -6F));

        root.addOrReplaceChild("helmet_reptile",
                CubeListBuilder.create().texOffs(64, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_reptile_ear1",
                CubeListBuilder.create().texOffs(114, 50).addBox(-2.5F, -16.5F, -2F, 0, 5, 5),
                PartPose.offsetAndRotation(0F, 3F, -6F, 0F, -35F / R, 0F));
        root.addOrReplaceChild("helmet_reptile_ear2",
                CubeListBuilder.create().texOffs(114, 45).addBox(2.5F, -16.5F, -2F, 0, 5, 5),
                PartPose.offsetAndRotation(0F, 3F, -6F, 0F, 35F / R, 0F));

        root.addOrReplaceChild("helmet_scorp_dirt",
                CubeListBuilder.create().texOffs(0, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_scorp_cave",
                CubeListBuilder.create().texOffs(32, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_scorp_frost",
                CubeListBuilder.create().texOffs(16, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_scorp_nether",
                CubeListBuilder.create().texOffs(48, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));
        root.addOrReplaceChild("helmet_scorp_undead",
                CubeListBuilder.create().texOffs(80, 64).addBox(-2F, -16.5F, -5F, 4, 5, 4),
                PartPose.offset(0F, 3F, -6F));

        root.addOrReplaceChild("uni_horn",
                CubeListBuilder.create().texOffs(0, 8).addBox(-0.5F, -21F, 0.5F, 1, 6, 1),
                PartPose.offsetAndRotation(0F, 3F, -6F, 18.17F / R, 0F, 0F));
        
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCOstrichEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float lLegXRot = Mth.cos(limbSwing * 0.4F) * 1.1F * limbSwingAmount;
        float rLegXRot = Mth.cos(limbSwing * 0.4F + (float) Math.PI) * 1.1F * limbSwingAmount;

        boolean hiding = entity.isHiding() || entity.isHeadBuried();
        float headY;
        float headXRot;
        float headYRot;
        if (entity.isCharging()) {
        headY = 3F;
        headXRot = 90F / R;
        headYRot = 0F;
        } else if (hiding) {
            headY = 11F;
            headXRot = 150F / R;
            headYRot = 0F;
        } else if (entity.isVehicle()) {
            headY = 3F;
            headXRot = 0F;
            headYRot = 0F;
        } else {
             headY = 3F;
             headXRot = rLegXRot / 20F + (-headPitch / R);
             headYRot = netHeadYaw / R;
        }

        head.y = headY;
        head.xRot = headXRot;
        head.yRot = headYRot;

        uBeak.y = headY;
        uBeak2.y = headY;
        lBeak.y = headY;
        lBeak2.y = headY;
        uBeakB.y = headY;
        uBeak2B.y = headY;
        lBeakB.y = headY;
        lBeak2B.y = headY;
        neckU.y = headY;
        neckD.y = headY;
        neckL.y = headY;
        neckUFeather.y = headY;
        neckLFeather.y = headY;

        uBeak.xRot = headXRot;
        uBeak.yRot = headYRot;
        uBeak2.xRot = headXRot;
        uBeak2.yRot = headYRot;
        lBeak.xRot = headXRot;
        lBeak.yRot = headYRot;
        lBeak2.xRot = headXRot;
        lBeak2.yRot = headYRot;
        neckU.xRot = headXRot;
        neckU.yRot = headYRot;
        neckD.xRot = 25F / R + headXRot;
        neckD.yRot = headYRot;
        neckL.xRot = 11.5F / R + headXRot;
        neckL.yRot = headYRot;
        uBeakB.xRot = -4F / R + headXRot;
        uBeakB.yRot = headYRot;
        uBeak2B.xRot = -4F / R + headXRot;
        uBeak2B.yRot = headYRot;
        lBeakB.xRot = 7F / R + headXRot;
        lBeakB.yRot = headYRot;
        lBeak2B.xRot = 7F / R + headXRot;
        lBeak2B.yRot = headYRot;
        neckUFeather.xRot = headXRot;
        neckUFeather.yRot = headYRot;
        neckLFeather.xRot = 11.5F / R + headXRot;
        neckLFeather.yRot = headYRot;

        // Mouth open/closed is a hard swap between two hand-modeled pose sets
        // (matches the original), not a continuous rotation — rotating either set
        // away from its authored angle exposes gaps never meant to be visible.
        boolean mouthOpen = entity.getMouthTicks() != 0;
        uBeak.visible = !mouthOpen;
        uBeak2.visible = !mouthOpen;
        lBeak.visible = !mouthOpen;
        lBeak2.visible = !mouthOpen;
        uBeakB.visible = mouthOpen;
        uBeak2B.visible = mouthOpen;
        lBeakB.visible = mouthOpen;
        lBeak2B.visible = mouthOpen;

        boolean specialWings = entity.getEssence() == MoCOstrichEntity.ESSENCE_FIRE
                || entity.getEssence() == MoCOstrichEntity.ESSENCE_WYVERN;
        boolean floating = specialWings && entity.isVehicle() && entity.isFlying();

        if (floating) {
        // Folded flight pose: legs tucked up and back, matches the original's flyer stance
        lLegC.y = 8F;
        lLegC.z = 17F;
        rLegC.y = 8F;
        rLegC.z = 17F;
        lFoot.y = -5F;
        lFoot.z = -3F;
        rFoot.y = -5F;
        rFoot.z = -3F;

        lLegA.xRot = 40F / R;
        lLegB.xRot = lLegA.xRot;
        lLegC.xRot = -85F / R;
        lFoot.xRot = 25F / R;

        rLegA.xRot = 40F / R;
        rLegB.xRot = rLegA.xRot;
        rLegC.xRot = -85F / R;
        rFoot.xRot = 25F / R;
        } else {
        // Ground pose (default resting pivots restored in case the previous frame was floating)
        lLegC.y = 5F;
        lLegC.z = 4F;
        rLegC.y = 5F;
        rLegC.z = 4F;
        lFoot.y = 5F;
        lFoot.z = 4F;
        rFoot.y = 5F;
        rFoot.z = 4F;

        lLegA.xRot = 10F / R + lLegXRot;
        lLegB.xRot = lLegA.xRot;
        lLegC.xRot = -15F / R + lLegXRot;
        lFoot.xRot = lLegA.xRot;
        rLegA.xRot = 10F / R + rLegXRot;
        rLegB.xRot = rLegA.xRot;
        rLegC.xRot = -15F / R + rLegXRot;
        rFoot.xRot = rLegA.xRot;
        }

        lWingB.visible = !specialWings;
        lWingC.visible = !specialWings;
        rWingB.visible = !specialWings;
        rWingC.visible = !specialWings;
        lWingD.visible = specialWings;
        lWingE.visible = specialWings;
        rWingD.visible = specialWings;
        rWingE.visible = specialWings;

        float wingF;
        if (specialWings) {
        int ascendCooldown = entity.getAscendCooldownTicks();
        if (ascendCooldown > 0) {
                wingF = -40F / R + Mth.cos(ascendCooldown * 0.3F) * 1.3F; // big flap pulse right after a thrust
        } else if (entity.isVehicle() && entity.isFlying()) {
                wingF = Mth.cos(ageInTicks * 0.8F) * 0.2F; // slow glide wave while airborne
        } else {
                wingF = Mth.cos(limbSwing * 0.3F) * limbSwingAmount; // ground idle sway
        }
        lWingD.zRot = -20F / R - wingF;
        lWingE.zRot = -20F / R - wingF;
        rWingD.zRot = 20F / R + wingF;
        rWingE.zRot = 20F / R + wingF;
        } else {
        wingF = 10F / R + Mth.cos(limbSwing * 0.6F) * 0.2F * limbSwingAmount;
        if (entity.getWingTicks() != 0) {
                wingF += 0.87266463F;
        }
        lWingB.yRot = 5F / R + wingF;
        lWingC.yRot = 5F / R + wingF;
        rWingB.yRot = -5F / R - wingF;
        rWingC.yRot = -5F / R - wingF;
        lWingB.xRot = 5F / R + rLegXRot / 10F;
        lWingC.xRot = rLegXRot / 10F;
        rWingB.xRot = 5F / R + rLegXRot / 10F;
        rWingC.xRot = rLegXRot / 10F;
        }

        boolean darkTail = entity.getEssence() == MoCOstrichEntity.ESSENCE_WYVERN;
        tail1.visible = !darkTail;
        tail2.visible = !darkTail;
        tail3.visible = !darkTail;
        tailPart1.visible = darkTail;
        tailPart2.visible = darkTail;
        tailPart3.visible = darkTail;
        tailPart4.visible = darkTail;
        tailPart5.visible = darkTail;

        if (darkTail) {
        float tailSwaySpread = 15F;
        float darkTailRot = floating
                ? Mth.cos(ageInTicks * 0.5F) * 0.15F // gentle sway while airborne, since limbSwing goes flat mid-flight
                : Mth.cos(limbSwing * 0.5F) * 0.3F * limbSwingAmount;
        tailBase.yRot = darkTailRot; // the connector piece sways with the first segment
        darkTailRot += darkTailRot / tailSwaySpread;
        tailPart1.yRot = darkTailRot;
        darkTailRot += darkTailRot / tailSwaySpread;
        tailPart2.yRot = darkTailRot;
        darkTailRot += darkTailRot / tailSwaySpread;
        tailPart3.yRot = darkTailRot;
        darkTailRot += darkTailRot / tailSwaySpread;
        tailPart4.yRot = darkTailRot;
        darkTailRot += darkTailRot / tailSwaySpread;
        tailPart5.yRot = darkTailRot;
        } else {
        tailBase.yRot = 0F; // static connector for the normal (non-dark) tail
        float tailRot = Mth.cos(limbSwing * 0.5F) * 0.3F * limbSwingAmount;
        tail1.yRot = tailRot;
        tail2.yRot = tailRot - 15F / R;
        tail3.yRot = tailRot + 15F / R;
        }

        boolean saddled = entity.isSaddled();
        saddleA.visible = saddled;
        saddleB.visible = saddled;
        saddleC.visible = saddled;
        saddleL.visible = saddled;
        saddleR.visible = saddled;
        saddleL2.visible = saddled;
        saddleR2.visible = saddled;

        boolean hasChestVisible = entity.hasChest();
        chestBag.visible = hasChestVisible;
        flagpole.visible = hasChestVisible;

        int flagColor = entity.getFlagColor();
        flagWhite.visible = flagColor == 0;
        flagOrange.visible = flagColor == 1;
        flagMagenta.visible = flagColor == 2;
        flagLightBlue.visible = flagColor == 3;
        flagYellow.visible = flagColor == 4;
        flagLime.visible = flagColor == 5;
        flagPink.visible = flagColor == 6;
        flagGray.visible = flagColor == 7;
        flagLightGray.visible = flagColor == 8;
        flagCyan.visible = flagColor == 9;
        flagPurple.visible = flagColor == 10;
        flagBlue.visible = flagColor == 11;
        flagBrown.visible = flagColor == 12;
        flagGreen.visible = flagColor == 13;
        flagRed.visible = flagColor == 14;
        flagBlack.visible = flagColor == 15;

        // Helmets track the head's live rotation every frame (matches the original
        // exactly — including through the hiding/burying pose).
        int helmet = entity.getHelmet();
        ModelPart[] allHelmets = {helmetLeather, helmetIron, helmetGold, helmetDiamond,
                helmetHide, helmetNeckHide, helmetHideEar1, helmetHideEar2,
                helmetFur, helmetNeckFur, helmetFurEar1, helmetFurEar2,
                helmetReptile, helmetReptileEar1, helmetReptileEar2,
                helmetScorpDirt, helmetScorpCave, helmetScorpFrost, helmetScorpNether, helmetScorpUndead};
        for (ModelPart part : allHelmets) {
        part.visible = false;
        }
        ModelPart[] activeGroup = switch (helmet) {
        case 1 -> new ModelPart[]{helmetLeather};
        case 2 -> new ModelPart[]{helmetIron};
        case 3 -> new ModelPart[]{helmetGold};
        case 4 -> new ModelPart[]{helmetDiamond};
        case 5 -> new ModelPart[]{helmetHide, helmetNeckHide, helmetHideEar1, helmetHideEar2};
        case 6 -> new ModelPart[]{helmetFur, helmetNeckFur, helmetFurEar1, helmetFurEar2};
        case 7 -> new ModelPart[]{helmetReptile, helmetReptileEar1, helmetReptileEar2};
        case 8 -> new ModelPart[]{helmetScorpDirt};
        case 9 -> new ModelPart[]{helmetScorpCave};
        case 10 -> new ModelPart[]{helmetScorpFrost};
        case 11 -> new ModelPart[]{helmetScorpNether};
        case 12 -> new ModelPart[]{helmetScorpUndead};
        default -> new ModelPart[0];
        };
        for (ModelPart part : activeGroup) {
        part.visible = true;
        part.y = head.y;
        part.xRot = head.xRot;
        part.yRot = head.yRot;
        }
        // The reptile helmet's ears keep their own fixed yRot offset from the head, per the original.
        if (helmet == 7) {
        helmetReptileEar1.yRot = -35F / R + head.yRot;
        helmetReptileEar2.yRot = 35F / R + head.yRot;
        }

        uniHorn.visible = entity.getEssence() == MoCOstrichEntity.ESSENCE_UNIHORNED;
        uniHorn.y = head.y;
        uniHorn.xRot = 18F / R + headXRot;
        uniHorn.yRot = headYRot;

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}