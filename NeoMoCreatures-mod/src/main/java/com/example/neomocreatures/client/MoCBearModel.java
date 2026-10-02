package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBearEntity;
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

public class MoCBearModel extends HierarchicalModel<MoCBearEntity> {

    private static final int ATTACK_TICKS_MAX = 8;

    private final ModelPart root;

    // fours (state 0)
    private final ModelPart head, snout, mouth, mouthOpen, lEar, rEar, neck, torso, abdomen, tail;
    private final ModelPart legFL1, legFL2, legFL3, legFR1, legFR2, legFR3, legRL1, legRL2, legRL3, legRR1, legRR2, legRR3;
    // standing (state 1)
    private final ModelPart bHead, bSnout, bMouth, bMouthOpen, bLEar, bREar, bNeck, bTorso, bAbdomen, bTail;
    private final ModelPart bLegFL1, bLegFL2, bLegFL3, bLegFR1, bLegFR2, bLegFR3, bLegRL1, bLegRL2, bLegRL3, bLegRR1, bLegRR2, bLegRR3;
    // sitting (state 2)
    private final ModelPart cHead, cSnout, cMouth, cMouthOpen, cLEar, cREar, cNeck, cTorso, cAbdomen, cTail;
    private final ModelPart cLegFL1, cLegFL2, cLegFL3, cLegFR1, cLegFR2, cLegFR3, cLegRL1, cLegRL2, cLegRL3, cLegRR1, cLegRR2, cLegRR3;

    private final ModelPart saddle, saddleBack, saddleFront, bag;
    private final ModelPart saddleSitted, saddleBackSitted, saddleFrontSitted, bagSitted;

    public MoCBearModel(ModelPart root) {
        this.root = root;

        this.head = root.getChild("head");
        this.snout = root.getChild("snout");
        this.mouth = root.getChild("mouth");
        this.mouthOpen = root.getChild("mouth_open");
        this.lEar = root.getChild("l_ear");
        this.rEar = root.getChild("r_ear");
        this.neck = root.getChild("neck");
        this.torso = root.getChild("torso");
        this.abdomen = root.getChild("abdomen");
        this.tail = root.getChild("tail");
        this.legFL1 = root.getChild("leg_fl1");
        this.legFL2 = root.getChild("leg_fl2");
        this.legFL3 = root.getChild("leg_fl3");
        this.legFR1 = root.getChild("leg_fr1");
        this.legFR2 = root.getChild("leg_fr2");
        this.legFR3 = root.getChild("leg_fr3");
        this.legRL1 = root.getChild("leg_rl1");
        this.legRL2 = root.getChild("leg_rl2");
        this.legRL3 = root.getChild("leg_rl3");
        this.legRR1 = root.getChild("leg_rr1");
        this.legRR2 = root.getChild("leg_rr2");
        this.legRR3 = root.getChild("leg_rr3");

        this.bHead = root.getChild("b_head");
        this.bSnout = root.getChild("b_snout");
        this.bMouth = root.getChild("b_mouth");
        this.bMouthOpen = root.getChild("b_mouth_open");
        this.bLEar = root.getChild("b_l_ear");
        this.bREar = root.getChild("b_r_ear");
        this.bNeck = root.getChild("b_neck");
        this.bTorso = root.getChild("b_torso");
        this.bAbdomen = root.getChild("b_abdomen");
        this.bTail = root.getChild("b_tail");
        this.bLegFL1 = root.getChild("b_leg_fl1");
        this.bLegFL2 = root.getChild("b_leg_fl2");
        this.bLegFL3 = root.getChild("b_leg_fl3");
        this.bLegFR1 = root.getChild("b_leg_fr1");
        this.bLegFR2 = root.getChild("b_leg_fr2");
        this.bLegFR3 = root.getChild("b_leg_fr3");
        this.bLegRL1 = root.getChild("b_leg_rl1");
        this.bLegRL2 = root.getChild("b_leg_rl2");
        this.bLegRL3 = root.getChild("b_leg_rl3");
        this.bLegRR1 = root.getChild("b_leg_rr1");
        this.bLegRR2 = root.getChild("b_leg_rr2");
        this.bLegRR3 = root.getChild("b_leg_rr3");

        this.cHead = root.getChild("c_head");
        this.cSnout = root.getChild("c_snout");
        this.cMouth = root.getChild("c_mouth");
        this.cMouthOpen = root.getChild("c_mouth_open");
        this.cLEar = root.getChild("c_l_ear");
        this.cREar = root.getChild("c_r_ear");
        this.cNeck = root.getChild("c_neck");
        this.cTorso = root.getChild("c_torso");
        this.cAbdomen = root.getChild("c_abdomen");
        this.cTail = root.getChild("c_tail");
        this.cLegFL1 = root.getChild("c_leg_fl1");
        this.cLegFL2 = root.getChild("c_leg_fl2");
        this.cLegFL3 = root.getChild("c_leg_fl3");
        this.cLegFR1 = root.getChild("c_leg_fr1");
        this.cLegFR2 = root.getChild("c_leg_fr2");
        this.cLegFR3 = root.getChild("c_leg_fr3");
        this.cLegRL1 = root.getChild("c_leg_rl1");
        this.cLegRL2 = root.getChild("c_leg_rl2");
        this.cLegRL3 = root.getChild("c_leg_rl3");
        this.cLegRR1 = root.getChild("c_leg_rr1");
        this.cLegRR2 = root.getChild("c_leg_rr2");
        this.cLegRR3 = root.getChild("c_leg_rr3");

        this.saddle = root.getChild("saddle");
        this.saddleBack = root.getChild("saddle_back");
        this.saddleFront = root.getChild("saddle_front");
        this.bag = root.getChild("bag");
        this.saddleSitted = root.getChild("saddle_sitted");
        this.saddleBackSitted = root.getChild("saddle_back_sitted");
        this.saddleFrontSitted = root.getChild("saddle_front_sitted");
        this.bagSitted = root.getChild("bag_sitted");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ===================== FOURS (state 0) =====================
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 0).addBox(-4F, 0F, -4F, 8, 8, 5),
                PartPose.offsetAndRotation(0F, 6F, -10F, 0.1502636F, 0F, 0F));
        root.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(23, 13).addBox(-2F, 3F, -8F, 4, 3, 5),
                PartPose.offsetAndRotation(0F, 6F, -10F, 0.1502636F, 0F, 0F));
        root.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 6F, -6.8F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, 6F, -10F, -0.0068161F, 0F, 0F));
        root.addOrReplaceChild("mouth_open", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 4F, -9.5F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, 6F, -10F, 0.534236F, 0F, 0F));
        root.addOrReplaceChild("l_ear", CubeListBuilder.create().texOffs(40, 0).addBox(2F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, 6F, -10F, 0.1502636F, -0.3490659F, 0.1396263F));
        root.addOrReplaceChild("r_ear", CubeListBuilder.create().texOffs(16, 0).addBox(-5F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, 6F, -10F, 0.1502636F, 0.3490659F, -0.1396263F));
        root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(18, 28).addBox(-3.5F, 0F, -7F, 7, 7, 7),
                PartPose.offsetAndRotation(0F, 5F, -5F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(13, 62).addBox(-4.5F, 0F, 0F, 9, 11, 10),
                PartPose.offsetAndRotation(0F, 5F, 5F, -0.4363323F, 0F, 0F));
        root.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(12, 42).addBox(-5F, 0F, 0F, 10, 10, 10),
                PartPose.offset(0F, 5F, -5F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 83).addBox(-1.5F, 0F, 0F, 3, 3, 3),
                PartPose.offsetAndRotation(0F, 8.466666F, 12F, 0.4363323F, 0F, 0F));

        root.addOrReplaceChild("leg_fl1", CubeListBuilder.create().texOffs(40, 22).addBox(-2.5F, 0F, -2.5F, 5, 8, 5),
                PartPose.offsetAndRotation(4F, 10F, -4F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("leg_fl2", CubeListBuilder.create().texOffs(46, 35).addBox(-2F, 7F, 0F, 4, 6, 4),
                PartPose.offset(4F, 10F, -4F));
        root.addOrReplaceChild("leg_fl3", CubeListBuilder.create().texOffs(46, 45).addBox(-2F, 12F, -1F, 4, 2, 5),
                PartPose.offset(4F, 10F, -4F));
        root.addOrReplaceChild("leg_fr1", CubeListBuilder.create().texOffs(4, 22).addBox(-2.5F, 0F, -2.5F, 5, 8, 5),
                PartPose.offsetAndRotation(-4F, 10F, -4F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("leg_fr2", CubeListBuilder.create().texOffs(2, 35).addBox(-2F, 7F, 0F, 4, 6, 4),
                PartPose.offset(-4F, 10F, -4F));
        root.addOrReplaceChild("leg_fr3", CubeListBuilder.create().texOffs(0, 45).addBox(-2F, 12F, -1F, 4, 2, 5),
                PartPose.offset(-4F, 10F, -4F));

        root.addOrReplaceChild("leg_rl1", CubeListBuilder.create().texOffs(34, 83).addBox(-1.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(3.5F, 11F, 9F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg_rl2", CubeListBuilder.create().texOffs(41, 97).addBox(-2F, 6F, -1F, 4, 6, 4),
                PartPose.offset(3.5F, 11F, 9F));
        root.addOrReplaceChild("leg_rl3", CubeListBuilder.create().texOffs(44, 107).addBox(-2F, 11F, -2F, 4, 2, 5),
                PartPose.offset(3.5F, 11F, 9F));
        root.addOrReplaceChild("leg_rr1", CubeListBuilder.create().texOffs(10, 83).addBox(-2.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(-3.5F, 11F, 9F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg_rr2", CubeListBuilder.create().texOffs(7, 97).addBox(-2F, 6F, -1F, 4, 6, 4),
                PartPose.offset(-3.5F, 11F, 9F));
        root.addOrReplaceChild("leg_rr3", CubeListBuilder.create().texOffs(2, 107).addBox(-2F, 11F, -2F, 4, 2, 5),
                PartPose.offset(-3.5F, 11F, 9F));

        // ===================== STANDING (state 1) =====================
        root.addOrReplaceChild("b_head", CubeListBuilder.create().texOffs(19, 0).addBox(-4F, 0F, -4F, 8, 8, 5),
                PartPose.offsetAndRotation(0F, -12F, 5F, -0.0242694F, 0F, 0F));
        root.addOrReplaceChild("b_snout", CubeListBuilder.create().texOffs(23, 13).addBox(-2F, 2.5F, -8.5F, 4, 3, 5),
                PartPose.offsetAndRotation(0F, -12F, 5F, -0.0242694F, 0F, 0F));
        root.addOrReplaceChild("b_mouth", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 5.5F, -8.0F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, -12F, 5F, -0.08726F, 0F, 0F));
        root.addOrReplaceChild("b_mouth_open", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 3.5F, -11F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, -12F, 5F, 0.5235988F, 0F, 0F));
        root.addOrReplaceChild("b_neck", CubeListBuilder.create().texOffs(18, 28).addBox(-3.5F, 0F, -7F, 7, 6, 7),
                PartPose.offsetAndRotation(0F, -3F, 11F, -1.336881F, 0F, 0F));
        root.addOrReplaceChild("b_l_ear", CubeListBuilder.create().texOffs(40, 0).addBox(2F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, -12F, 5F, -0.0242694F, -0.3490659F, 0.1396263F));
        root.addOrReplaceChild("b_r_ear", CubeListBuilder.create().texOffs(16, 0).addBox(-5F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, -12F, 5F, -0.0242694F, 0.3490659F, -0.1396263F));
        root.addOrReplaceChild("b_torso", CubeListBuilder.create().texOffs(12, 42).addBox(-5F, 0F, 0F, 10, 10, 10),
                PartPose.offsetAndRotation(0F, -3.5F, 12.3F, -1.396263F, 0F, 0F));
        root.addOrReplaceChild("b_abdomen", CubeListBuilder.create().texOffs(13, 62).addBox(-4.5F, 0F, 0F, 9, 11, 10),
                PartPose.offsetAndRotation(0F, 6F, 14F, -1.570796F, 0F, 0F));
        root.addOrReplaceChild("b_tail", CubeListBuilder.create().texOffs(26, 83).addBox(-1.5F, 0F, 0F, 3, 3, 3),
                PartPose.offsetAndRotation(0F, 12.46667F, 12.6F, 0.3619751F, 0F, 0F));

        root.addOrReplaceChild("b_leg_fl1", CubeListBuilder.create().texOffs(40, 22).addBox(-2.5F, 0F, -2.5F, 5, 8, 5),
                PartPose.offsetAndRotation(5F, -1F, 6F, 0.2617994F, 0F, -0.2617994F));
        root.addOrReplaceChild("b_leg_fl2", CubeListBuilder.create().texOffs(46, 35).addBox(0F, 5F, 3F, 4, 6, 4),
                PartPose.offsetAndRotation(5F, -1F, 6F, -0.5576792F, 0F, 0F));
        root.addOrReplaceChild("b_leg_fl3", CubeListBuilder.create().texOffs(46, 45).addBox(0.1F, -7F, -14F, 4, 2, 5),
                PartPose.offsetAndRotation(5F, -1F, 6F, 2.007645F, 0F, 0F));
        root.addOrReplaceChild("b_leg_fr1", CubeListBuilder.create().texOffs(4, 22).addBox(-2.5F, 0F, -2.5F, 5, 8, 5),
                PartPose.offsetAndRotation(-5F, -1F, 6F, 0.2617994F, 0F, 0.2617994F));
        root.addOrReplaceChild("b_leg_fr2", CubeListBuilder.create().texOffs(2, 35).addBox(-4F, 5F, 3F, 4, 6, 4),
                PartPose.offsetAndRotation(-5F, -1F, 6F, -0.5576792F, 0F, 0F));
        root.addOrReplaceChild("b_leg_fr3", CubeListBuilder.create().texOffs(0, 45).addBox(-4.1F, -7F, -14F, 4, 2, 5),
                PartPose.offsetAndRotation(-5F, -1F, 6F, 2.007129F, 0F, 0F));

        root.addOrReplaceChild("b_leg_rl1", CubeListBuilder.create().texOffs(34, 83).addBox(-1.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(3F, 11F, 9F, -0.5235988F, -0.2617994F, 0F));
        root.addOrReplaceChild("b_leg_rl2", CubeListBuilder.create().texOffs(41, 97).addBox(-1.3F, 6F, -3F, 4, 6, 4),
                PartPose.offsetAndRotation(3F, 11F, 9F, 0F, -0.2617994F, 0F));
        root.addOrReplaceChild("b_leg_rl3", CubeListBuilder.create().texOffs(44, 107).addBox(-1.2F, 11F, -4F, 4, 2, 5),
                PartPose.offsetAndRotation(3F, 11F, 9F, 0F, -0.2617994F, 0F));
        root.addOrReplaceChild("b_leg_rr1", CubeListBuilder.create().texOffs(10, 83).addBox(-2.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(-3F, 11F, 9F, -0.1745329F, 0.2617994F, 0F));
        root.addOrReplaceChild("b_leg_rr2", CubeListBuilder.create().texOffs(7, 97).addBox(-2.4F, 6F, -1F, 4, 6, 4),
                PartPose.offsetAndRotation(-3F, 11F, 9F, 0F, 0.2617994F, 0F));
        root.addOrReplaceChild("b_leg_rr3", CubeListBuilder.create().texOffs(2, 107).addBox(-2.5F, 11F, -2F, 4, 2, 5),
                PartPose.offsetAndRotation(-3F, 11F, 9F, 0F, 0.2617994F, 0F));

        // ===================== SITTING (state 2) =====================
        root.addOrReplaceChild("c_head", CubeListBuilder.create().texOffs(19, 0).addBox(-4F, 0F, -4F, 8, 8, 5),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, 0.1502636F, 0F, 0F));
        root.addOrReplaceChild("c_snout", CubeListBuilder.create().texOffs(23, 13).addBox(-2F, 3F, -8.5F, 4, 3, 5),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, 0.1502636F, 0F, 0F));
        root.addOrReplaceChild("c_mouth", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 6F, -7F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, -0.0068161F, 0F, 0F));
        root.addOrReplaceChild("c_mouth_open", CubeListBuilder.create().texOffs(24, 21).addBox(-1.5F, 5.5F, -9F, 3, 2, 5),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, 0.3665191F, 0F, 0F));
        root.addOrReplaceChild("c_l_ear", CubeListBuilder.create().texOffs(40, 0).addBox(2F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, 0.1502636F, -0.3490659F, 0.1396263F));
        root.addOrReplaceChild("c_r_ear", CubeListBuilder.create().texOffs(16, 0).addBox(-5F, -2F, -2F, 3, 3, 1),
                PartPose.offsetAndRotation(0F, 3F, -3.5F, 0.1502636F, 0.3490659F, -0.1396263F));
        root.addOrReplaceChild("c_neck", CubeListBuilder.create().texOffs(18, 28).addBox(-3.5F, 0F, -7F, 7, 7, 7),
                PartPose.offsetAndRotation(0F, 5.8F, 3.4F, -0.3316126F, 0F, 0F));
        root.addOrReplaceChild("c_torso", CubeListBuilder.create().texOffs(12, 42).addBox(-5F, 0F, 0F, 10, 10, 10),
                PartPose.offsetAndRotation(0F, 5.8F, 3.4F, -0.9712912F, 0F, 0F));
        root.addOrReplaceChild("c_abdomen", CubeListBuilder.create().texOffs(13, 62).addBox(-4.5F, 0F, 0F, 9, 11, 10),
                PartPose.offsetAndRotation(0F, 14F, 9F, -1.570796F, 0F, 0F));
        root.addOrReplaceChild("c_tail", CubeListBuilder.create().texOffs(26, 83).addBox(-1.5F, 0F, 0F, 3, 3, 3),
                PartPose.offsetAndRotation(0F, 21.46667F, 8F, 0.4363323F, 0F, 0F));

        root.addOrReplaceChild("c_leg_fl1", CubeListBuilder.create().texOffs(40, 22).addBox(-2.5F, 0F, -1.5F, 5, 8, 5),
                PartPose.offsetAndRotation(4F, 10F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("c_leg_fl2", CubeListBuilder.create().texOffs(46, 35).addBox(-2F, 0F, -1.2F, 4, 6, 4),
                PartPose.offsetAndRotation(4F, 17F, -2F, -0.3490659F, 0F, 0.2617994F));
        root.addOrReplaceChild("c_leg_fl3", CubeListBuilder.create().texOffs(46, 45).addBox(-2F, 0F, -3F, 4, 2, 5),
                PartPose.offsetAndRotation(2.5F, 22F, -4F, 0F, 0.1745329F, 0F));
        root.addOrReplaceChild("c_leg_fr1", CubeListBuilder.create().texOffs(4, 22).addBox(-2.5F, 0F, -1.5F, 5, 8, 5),
                PartPose.offsetAndRotation(-4F, 10F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("c_leg_fr2", CubeListBuilder.create().texOffs(2, 35).addBox(-2F, 0F, -1.2F, 4, 6, 4),
                PartPose.offsetAndRotation(-4F, 17F, -2F, -0.3490659F, 0F, -0.2617994F));
        root.addOrReplaceChild("c_leg_fr3", CubeListBuilder.create().texOffs(0, 45).addBox(-2F, 0F, -3F, 4, 2, 5),
                PartPose.offsetAndRotation(-2.5F, 22F, -4F, 0F, -0.1745329F, 0F));

        root.addOrReplaceChild("c_leg_rl1", CubeListBuilder.create().texOffs(34, 83).addBox(-1.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(3F, 21F, 5F, -1.396263F, -0.3490659F, 0.3490659F));
        root.addOrReplaceChild("c_leg_rl2", CubeListBuilder.create().texOffs(41, 97).addBox(-2F, 0F, -2F, 4, 6, 4),
                PartPose.offsetAndRotation(5.2F, 22.5F, -1F, -1.570796F, 0F, 0.3490659F));
        root.addOrReplaceChild("c_leg_rl3", CubeListBuilder.create().texOffs(44, 107).addBox(-2F, 0F, -3F, 4, 2, 5),
                PartPose.offsetAndRotation(5.5F, 22F, -6F, -1.375609F, 0F, 0.3490659F));
        root.addOrReplaceChild("c_leg_rr1", CubeListBuilder.create().texOffs(10, 83).addBox(-2.5F, 0F, -2.5F, 4, 8, 6),
                PartPose.offsetAndRotation(-3F, 21F, 5F, -1.396263F, 0.3490659F, -0.3490659F));
        root.addOrReplaceChild("c_leg_rr2", CubeListBuilder.create().texOffs(7, 97).addBox(-2F, 0F, -2F, 4, 6, 4),
                PartPose.offsetAndRotation(-5.2F, 22.5F, -1F, -1.570796F, 0F, -0.3490659F));
        root.addOrReplaceChild("c_leg_rr3", CubeListBuilder.create().texOffs(2, 107).addBox(-2F, 0F, -3F, 4, 2, 5),
                PartPose.offsetAndRotation(-5.5F, 22F, -6F, -1.375609F, 0F, -0.3490659F));

        root.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(36, 114).addBox(-4F, -0.5F, -3F, 8, 2, 6),
                PartPose.offset(0F, 4F, -2F));
        root.addOrReplaceChild("saddle_back", CubeListBuilder.create().texOffs(20, 108).addBox(-4F, -0.2F, 2.9F, 8, 2, 4),
                PartPose.offsetAndRotation(0F, 4F, -2F, 0.10088F, 0F, 0F));
        root.addOrReplaceChild("saddle_front", CubeListBuilder.create().texOffs(36, 122).addBox(-2.5F, -1F, -3F, 5, 2, 3),
                PartPose.offsetAndRotation(0F, 4F, -2F, -0.1850049F, 0F, 0F));
        root.addOrReplaceChild("bag", CubeListBuilder.create().texOffs(0, 114).addBox(-5F, -3F, -2.5F, 10, 2, 5),
                PartPose.offsetAndRotation(0F, 7F, 7F, -0.4363323F, 0F, 0F));

        root.addOrReplaceChild("saddle_sitted", CubeListBuilder.create().texOffs(36, 114).addBox(-4F, -0.5F, -3F, 8, 2, 6),
                PartPose.offsetAndRotation(0F, 7.5F, 6.5F, -0.9686577F, 0F, 0F));
        root.addOrReplaceChild("saddle_back_sitted", CubeListBuilder.create().texOffs(20, 108).addBox(-4F, -0.3F, 2.9F, 8, 2, 4),
                PartPose.offsetAndRotation(0F, 7.5F, 6.5F, -0.9162979F, 0F, 0F));
        root.addOrReplaceChild("saddle_front_sitted", CubeListBuilder.create().texOffs(36, 122).addBox(-2.5F, -1F, -3F, 5, 2, 3),
                PartPose.offsetAndRotation(0F, 7.5F, 6.5F, -1.151917F, 0F, 0F));
        root.addOrReplaceChild("bag_sitted", CubeListBuilder.create().texOffs(0, 114).addBox(-5F, -3F, -2.5F, 10, 2, 5),
                PartPose.offsetAndRotation(0F, 17F, 8F, -1.570796F, 0F, 0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCBearEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        // Every part starts hidden; only the active state's set gets shown below.
        for (String name : new String[]{
                "head", "snout", "mouth", "mouth_open", "l_ear", "r_ear", "neck", "torso", "abdomen", "tail",
                "leg_fl1", "leg_fl2", "leg_fl3", "leg_fr1", "leg_fr2", "leg_fr3",
                "leg_rl1", "leg_rl2", "leg_rl3", "leg_rr1", "leg_rr2", "leg_rr3",
                "b_head", "b_snout", "b_mouth", "b_mouth_open", "b_l_ear", "b_r_ear", "b_neck", "b_torso", "b_abdomen", "b_tail",
                "b_leg_fl1", "b_leg_fl2", "b_leg_fl3", "b_leg_fr1", "b_leg_fr2", "b_leg_fr3",
                "b_leg_rl1", "b_leg_rl2", "b_leg_rl3", "b_leg_rr1", "b_leg_rr2", "b_leg_rr3",
                "c_head", "c_snout", "c_mouth", "c_mouth_open", "c_l_ear", "c_r_ear", "c_neck", "c_torso", "c_abdomen", "c_tail",
                "c_leg_fl1", "c_leg_fl2", "c_leg_fl3", "c_leg_fr1", "c_leg_fr2", "c_leg_fr3",
                "c_leg_rl1", "c_leg_rl2", "c_leg_rl3", "c_leg_rr1", "c_leg_rr2", "c_leg_rr3",
                "saddle", "saddle_back", "saddle_front", "bag",
                "saddle_sitted", "saddle_back_sitted", "saddle_front_sitted", "bag_sitted"}) {
            root.getChild(name).visible = false;
        }

        boolean openMouth = entity.getMouthTicks() > 0 || entity.getAttackTicks() > 0;
        float attackLunge = entity.getAttackTicks() > 0
                ? Mth.sin((ATTACK_TICKS_MAX - entity.getAttackTicks()) * 0.4F) * 0.35F
                : 0F;
        int state = entity.getBearState();

        float lLegRotX = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.8F);
        float rLegRotX = Mth.cos((limbSwing * 0.6662F) + 3.141593F) * 0.8F * limbSwingAmount;
        float xAngle = headPitch / ModelAnimations.DEGREES_PER_RADIAN;
        float yAngle = netHeadYaw / ModelAnimations.DEGREES_PER_RADIAN;

        if (state == MoCBearEntity.FOURS_STATE) {
            head.visible = true; snout.visible = true; lEar.visible = true; rEar.visible = true;
            neck.visible = true; torso.visible = true; abdomen.visible = true; tail.visible = true;
            legFL1.visible = true; legFL2.visible = true; legFL3.visible = true;
            legFR1.visible = true; legFR2.visible = true; legFR3.visible = true;
            legRL1.visible = true; legRL2.visible = true; legRL3.visible = true;
            legRR1.visible = true; legRR2.visible = true; legRR3.visible = true;
            mouth.visible = !openMouth;
            mouthOpen.visible = openMouth;

            head.xRot = 0.1502636F + xAngle - attackLunge; head.yRot = yAngle;
            snout.xRot = 0.1502636F + xAngle; snout.yRot = yAngle;
            mouth.xRot = -0.0068161F + xAngle; mouth.yRot = yAngle;
            mouthOpen.xRot = 0.534236F + xAngle; mouthOpen.yRot = yAngle;
            lEar.xRot = 0.1502636F + xAngle; lEar.yRot = -0.3490659F + yAngle;
            rEar.xRot = 0.1502636F + xAngle; rEar.yRot = 0.3490659F + yAngle;

            legFL1.xRot = 0.2617994F + lLegRotX; legFL2.xRot = lLegRotX; legFL3.xRot = lLegRotX;
            legRR1.xRot = -0.1745329F + lLegRotX; legRR2.xRot = lLegRotX; legRR3.xRot = lLegRotX;
            legFR1.xRot = 0.2617994F + rLegRotX; legFR2.xRot = rLegRotX; legFR3.xRot = rLegRotX;
            legRL1.xRot = -0.1745329F + rLegRotX; legRL2.xRot = rLegRotX; legRL3.xRot = rLegRotX;
            tail.zRot = lLegRotX * 0.2F;
            if (entity.isSaddled()) {
                saddle.visible = true;
                saddleBack.visible = true;
                saddleFront.visible = true;
                }
                if (entity.hasChest()) {
                bag.visible = true;
                }
        } else if (state == MoCBearEntity.STANDING_STATE) {
            bHead.visible = true; bSnout.visible = true; bLEar.visible = true; bREar.visible = true;
            bNeck.visible = true; bTorso.visible = true; bAbdomen.visible = true; bTail.visible = true;
            bLegFL1.visible = true; bLegFL2.visible = true; bLegFL3.visible = true;
            bLegFR1.visible = true; bLegFR2.visible = true; bLegFR3.visible = true;
            bLegRL1.visible = true; bLegRL2.visible = true; bLegRL3.visible = true;
            bLegRR1.visible = true; bLegRR2.visible = true; bLegRR3.visible = true;
            bMouth.visible = !openMouth;
            bMouthOpen.visible = openMouth;

            bHead.xRot = -0.0242694F - xAngle - attackLunge; bHead.yRot = yAngle;
            bSnout.xRot = -0.0242694F - xAngle; bSnout.yRot = yAngle;
            bMouth.xRot = -0.08726F - xAngle; bMouth.yRot = yAngle;
            bMouthOpen.xRot = 0.5235988F - xAngle; bMouthOpen.yRot = yAngle;
            bLEar.xRot = -0.0242694F - xAngle; bLEar.yRot = -0.3490659F + yAngle;
            bREar.xRot = -0.0242694F - xAngle; bREar.yRot = 0.3490659F + yAngle;

            float breathing = Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            bLegFR1.zRot = 0.2617994F + breathing; bLegFR2.zRot = breathing; bLegFR3.zRot = breathing;
            bLegFL1.zRot = -0.2617994F - breathing; bLegFL2.zRot = -breathing; bLegFL3.zRot = -breathing;
            bLegRR1.xRot = -0.1745329F + lLegRotX; bLegRR2.xRot = lLegRotX; bLegRR3.xRot = lLegRotX;
            bLegRL1.xRot = -0.5235988F + rLegRotX; bLegRL2.xRot = rLegRotX; bLegRL3.xRot = rLegRotX;
            bTail.zRot = lLegRotX * 0.2F;
        } else {
            cHead.visible = true; cSnout.visible = true; cLEar.visible = true; cREar.visible = true;
            cNeck.visible = true; cTorso.visible = true; cAbdomen.visible = true; cTail.visible = true;
            cLegFL1.visible = true; cLegFL2.visible = true; cLegFL3.visible = true;
            cLegFR1.visible = true; cLegFR2.visible = true; cLegFR3.visible = true;
            cLegRL1.visible = true; cLegRL2.visible = true; cLegRL3.visible = true;
            cLegRR1.visible = true; cLegRR2.visible = true; cLegRR3.visible = true;
            cMouth.visible = !openMouth;
            cMouthOpen.visible = openMouth;

            cHead.xRot = 0.1502636F + xAngle - attackLunge; cHead.yRot = yAngle;
            cSnout.xRot = 0.1502636F + xAngle; cSnout.yRot = yAngle;
            cMouth.xRot = -0.0068161F + xAngle; cMouth.yRot = yAngle;
            cMouthOpen.xRot = 0.3665191F + xAngle; cMouthOpen.yRot = yAngle;
            cLEar.xRot = 0.1502636F + xAngle; cLEar.yRot = -0.3490659F + yAngle;
            cREar.xRot = 0.1502636F + xAngle; cREar.yRot = 0.3490659F + yAngle;
        }
        if (entity.isSaddled()) {
        saddleSitted.visible = true;
        saddleBackSitted.visible = true;
        saddleFrontSitted.visible = true;
        }
        if (state == MoCBearEntity.SITTING_STATE && entity.hasChest()) {
        bagSitted.visible = true;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay,
                                int color) {
        root.render(poseStack, consumer, packedLight, packedOverlay, color);
    }
}