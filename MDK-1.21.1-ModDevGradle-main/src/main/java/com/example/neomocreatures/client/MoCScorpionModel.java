package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCScorpionEntity;
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

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelScorpion (Techne) —
 * a flat/absolute-position model like the wyvern/elephant (NOT true nested
 * hierarchy): every part sits directly under root, and setupAnim recomputes
 * each leg's x/z rotation from scratch every frame rather than relying on
 * parent-child inheritance. 64x64 texture canvas (smaller than the big
 * cat/manticore's 128x128).
 */
public class MoCScorpionModel extends HierarchicalModel<MoCScorpionEntity> {

    private static final float R = 57.29578F;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mouthL;
    private final ModelPart mouthR;
    private final ModelPart body;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart sting1;
    private final ModelPart sting2;
    private final ModelPart lArm1;
    private final ModelPart lArm2;
    private final ModelPart lArm3;
    private final ModelPart lArm4;
    private final ModelPart rArm1;
    private final ModelPart rArm2;
    private final ModelPart rArm3;
    private final ModelPart rArm4;
    private final ModelPart leg1A;
    private final ModelPart leg1B;
    private final ModelPart leg1C;
    private final ModelPart leg2A;
    private final ModelPart leg2B;
    private final ModelPart leg2C;
    private final ModelPart leg3A;
    private final ModelPart leg3B;
    private final ModelPart leg3C;
    private final ModelPart leg4A;
    private final ModelPart leg4B;
    private final ModelPart leg4C;
    private final ModelPart leg5A;
    private final ModelPart leg5B;
    private final ModelPart leg5C;
    private final ModelPart leg6A;
    private final ModelPart leg6B;
    private final ModelPart leg6C;
    private final ModelPart leg7A;
    private final ModelPart leg7B;
    private final ModelPart leg7C;
    private final ModelPart leg8A;
    private final ModelPart leg8B;
    private final ModelPart leg8C;
    private final ModelPart baby1;
    private final ModelPart baby2;
    private final ModelPart baby3;
    private final ModelPart baby4;
    private final ModelPart baby5;

    public MoCScorpionModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.mouthL = root.getChild("mouth_l");
        this.mouthR = root.getChild("mouth_r");
        this.body = root.getChild("body");
        this.tail1 = root.getChild("tail1");
        this.tail2 = root.getChild("tail2");
        this.tail3 = root.getChild("tail3");
        this.tail4 = root.getChild("tail4");
        this.tail5 = root.getChild("tail5");
        this.sting1 = root.getChild("sting1");
        this.sting2 = root.getChild("sting2");
        this.lArm1 = root.getChild("l_arm1");
        this.lArm2 = root.getChild("l_arm2");
        this.lArm3 = root.getChild("l_arm3");
        this.lArm4 = root.getChild("l_arm4");
        this.rArm1 = root.getChild("r_arm1");
        this.rArm2 = root.getChild("r_arm2");
        this.rArm3 = root.getChild("r_arm3");
        this.rArm4 = root.getChild("r_arm4");
        this.leg1A = root.getChild("leg1a");
        this.leg1B = root.getChild("leg1b");
        this.leg1C = root.getChild("leg1c");
        this.leg2A = root.getChild("leg2a");
        this.leg2B = root.getChild("leg2b");
        this.leg2C = root.getChild("leg2c");
        this.leg3A = root.getChild("leg3a");
        this.leg3B = root.getChild("leg3b");
        this.leg3C = root.getChild("leg3c");
        this.leg4A = root.getChild("leg4a");
        this.leg4B = root.getChild("leg4b");
        this.leg4C = root.getChild("leg4c");
        this.leg5A = root.getChild("leg5a");
        this.leg5B = root.getChild("leg5b");
        this.leg5C = root.getChild("leg5c");
        this.leg6A = root.getChild("leg6a");
        this.leg6B = root.getChild("leg6b");
        this.leg6C = root.getChild("leg6c");
        this.leg7A = root.getChild("leg7a");
        this.leg7B = root.getChild("leg7b");
        this.leg7C = root.getChild("leg7c");
        this.leg8A = root.getChild("leg8a");
        this.leg8B = root.getChild("leg8b");
        this.leg8C = root.getChild("leg8c");
        this.baby1 = root.getChild("baby1");
        this.baby2 = root.getChild("baby2");
        this.baby3 = root.getChild("baby3");
        this.baby4 = root.getChild("baby4");
        this.baby5 = root.getChild("baby5");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, 0F, 10, 5, 13),
                PartPose.offset(0F, 14F, -9F));
        root.addOrReplaceChild("mouth_l",
                CubeListBuilder.create().texOffs(18, 58).addBox(-3F, -2F, -1F, 4, 4, 2),
                PartPose.offsetAndRotation(3F, 17F, -9F, 0F, -22F / R, 0F));
        root.addOrReplaceChild("mouth_r",
                CubeListBuilder.create().texOffs(30, 58).addBox(-1F, -2F, -1F, 4, 4, 2),
                PartPose.offsetAndRotation(-3F, 17F, -9F, 0F, 22F / R, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 18).addBox(-4F, -2F, 0F, 8, 4, 10),
                PartPose.offsetAndRotation(0F, 17F, 3F, 5F / R, 0F, 0F));

        root.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(0, 32).addBox(-3F, -2F, 0F, 6, 4, 6),
                PartPose.offset(0F, 16F, 12F));
        root.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(0, 42).addBox(-2F, -2F, 0F, 4, 4, 6),
                PartPose.offset(0F, 13F, 16.5F));
        root.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(0, 52).addBox(-1.5F, -1.5F, 0F, 3, 3, 6),
                PartPose.offset(0F, 8F, 18.5F));
        root.addOrReplaceChild("tail4",
                CubeListBuilder.create().texOffs(24, 32).addBox(-1.5F, -1.5F, 0F, 3, 3, 6),
                PartPose.offset(0F, 3F, 18F));
        root.addOrReplaceChild("tail5",
                CubeListBuilder.create().texOffs(24, 41).addBox(-1.5F, -1.5F, 0F, 3, 3, 6),
                PartPose.offset(0F, -0.2F, 14F));
        root.addOrReplaceChild("sting1",
                CubeListBuilder.create().texOffs(30, 50).addBox(-1.5F, 0F, -1.5F, 3, 5, 3),
                PartPose.offset(0F, -1F, 7F));
        root.addOrReplaceChild("sting2",
                CubeListBuilder.create().texOffs(26, 50).addBox(-0.5F, 0F, 0.5F, 1, 4, 1),
                PartPose.offset(0F, 2.6F, 8.8F));

        root.addOrReplaceChild("l_arm1",
                CubeListBuilder.create().texOffs(26, 18).addBox(-1F, -7F, -1F, 2, 7, 2),
                PartPose.offsetAndRotation(5F, 18F, -8F, -20F / R, 0F, 50F / R));
        root.addOrReplaceChild("l_arm2",
                CubeListBuilder.create().texOffs(42, 55).addBox(-1.5F, -1.5F, -6F, 3, 3, 6),
                PartPose.offset(10F, 14F, -6F));
        root.addOrReplaceChild("l_arm3",
                CubeListBuilder.create().texOffs(42, 39).addBox(-0.5F, -0.5F, -7F, 2, 1, 7),
                PartPose.offset(12F, 15F, -11F));
        root.addOrReplaceChild("l_arm4",
                CubeListBuilder.create().texOffs(42, 31).addBox(-1.5F, -0.5F, -6F, 1, 1, 7),
                PartPose.offset(11F, 15F, -11F));

        root.addOrReplaceChild("r_arm1",
                CubeListBuilder.create().texOffs(0, 18).addBox(-1F, -7F, -1F, 2, 7, 2),
                PartPose.offsetAndRotation(-5F, 18F, -8F, -20F / R, 0F, -50F / R));
        root.addOrReplaceChild("r_arm2",
                CubeListBuilder.create().texOffs(42, 55).addBox(-1.5F, -1.5F, -6F, 3, 3, 6),
                PartPose.offset(-10F, 14F, -6F));
        root.addOrReplaceChild("r_arm3",
                CubeListBuilder.create().texOffs(42, 47).addBox(-1.5F, -0.5F, -7F, 2, 1, 7),
                PartPose.offset(-12F, 15F, -11F));
        root.addOrReplaceChild("r_arm4",
                CubeListBuilder.create().texOffs(42, 31).addBox(0.5F, -0.5F, -6F, 1, 1, 7),
                PartPose.offset(-11F, 15F, -11F));

        // 8 legs, 3 segments each. Rotation is fully recomputed every frame in
        // setupAnim (the original overwrites xRot/zRot from scratch, never
        // reads back the construction-time value), so no rotation is baked in here.
        root.addOrReplaceChild("leg1a", CubeListBuilder.create().texOffs(38, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(5F, 18F, -5F));
        root.addOrReplaceChild("leg1b", CubeListBuilder.create().texOffs(50, 0).addBox(2F, -8F, -1F, 5, 2, 2), PartPose.offset(5F, 18F, -5F));
        root.addOrReplaceChild("leg1c", CubeListBuilder.create().texOffs(52, 16).addBox(4.5F, -9F, -0.7F, 5, 1, 1), PartPose.offset(5F, 18F, -5F));

        root.addOrReplaceChild("leg2a", CubeListBuilder.create().texOffs(38, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(5F, 18F, -2F));
        root.addOrReplaceChild("leg2b", CubeListBuilder.create().texOffs(50, 4).addBox(1F, -8F, -1F, 5, 2, 2), PartPose.offset(5F, 18F, -2F));
        root.addOrReplaceChild("leg2c", CubeListBuilder.create().texOffs(50, 18).addBox(4F, -8.5F, -1F, 6, 1, 1), PartPose.offset(5F, 18F, -2F));

        root.addOrReplaceChild("leg3a", CubeListBuilder.create().texOffs(38, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(5F, 17.5F, 1F));
        root.addOrReplaceChild("leg3b", CubeListBuilder.create().texOffs(48, 8).addBox(1F, -8F, -1F, 6, 2, 2), PartPose.offset(5F, 17.5F, 1F));
        root.addOrReplaceChild("leg3c", CubeListBuilder.create().texOffs(50, 20).addBox(4.5F, -8.2F, -1.3F, 6, 1, 1), PartPose.offset(5F, 17.5F, 1F));

        root.addOrReplaceChild("leg4a", CubeListBuilder.create().texOffs(38, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(5F, 17F, 4F));
        root.addOrReplaceChild("leg4b", CubeListBuilder.create().texOffs(46, 12).addBox(0.5F, -8.5F, -1F, 7, 2, 2), PartPose.offset(5F, 17F, 4F));
        root.addOrReplaceChild("leg4c", CubeListBuilder.create().texOffs(48, 22).addBox(3.5F, -8.5F, -1.5F, 7, 1, 1), PartPose.offset(5F, 17F, 4F));

        root.addOrReplaceChild("leg5a", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(-5F, 18F, -5F));
        root.addOrReplaceChild("leg5b", CubeListBuilder.create().texOffs(50, 0).addBox(-7F, -8F, -1F, 5, 2, 2), PartPose.offset(-5F, 18F, -5F));
        root.addOrReplaceChild("leg5c", CubeListBuilder.create().texOffs(52, 16).addBox(-9.5F, -9F, -0.7F, 5, 1, 1), PartPose.offset(-5F, 18F, -5F));

        root.addOrReplaceChild("leg6a", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(-5F, 18F, -2F));
        root.addOrReplaceChild("leg6b", CubeListBuilder.create().texOffs(50, 4).addBox(-6F, -8F, -1F, 5, 2, 2), PartPose.offset(-5F, 18F, -2F));
        root.addOrReplaceChild("leg6c", CubeListBuilder.create().texOffs(50, 18).addBox(-10F, -8.5F, -1F, 6, 1, 1), PartPose.offset(-5F, 18F, -2F));

        root.addOrReplaceChild("leg7a", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(-5F, 17.5F, 1F));
        root.addOrReplaceChild("leg7b", CubeListBuilder.create().texOffs(48, 8).addBox(-7F, -8.5F, -1F, 6, 2, 2), PartPose.offset(-5F, 17.5F, 1F));
        root.addOrReplaceChild("leg7c", CubeListBuilder.create().texOffs(50, 20).addBox(-10.5F, -8.7F, -1.3F, 6, 1, 1), PartPose.offset(-5F, 17.5F, 1F));

        root.addOrReplaceChild("leg8a", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -7F, -1F, 2, 7, 2), PartPose.offset(-5F, 17F, 4F));
        root.addOrReplaceChild("leg8b", CubeListBuilder.create().texOffs(46, 12).addBox(-7.5F, -8.5F, -1F, 7, 2, 2), PartPose.offset(-5F, 17F, 4F));
        root.addOrReplaceChild("leg8c", CubeListBuilder.create().texOffs(48, 22).addBox(-10.5F, -8.5F, -1.5F, 7, 1, 1), PartPose.offset(-5F, 17F, 4F));

        // Baby scorpions riding on the back — only shown when hasBabies() is true.
        root.addOrReplaceChild("baby1",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, 0F, -2.5F, 3, 2, 5),
                PartPose.offset(0F, 12F, 0F));
        root.addOrReplaceChild("baby2",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, 0F, -2.5F, 3, 2, 5),
                PartPose.offsetAndRotation(-5F, 13.4F, -1F, 0.4461433F, 2.490967F, 0.5205006F));
        root.addOrReplaceChild("baby3",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, 0F, -2.5F, 3, 2, 5),
                PartPose.offsetAndRotation(-2F, 13F, 4F, 0F, 0.8551081F, 0F));
        root.addOrReplaceChild("baby4",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, 0F, -2.5F, 3, 2, 5),
                PartPose.offsetAndRotation(4F, 13F, 2F, 0F, 2.714039F, -0.3717861F));
        root.addOrReplaceChild("baby5",
                CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, 0F, -2.5F, 3, 2, 5),
                PartPose.offsetAndRotation(1F, 13F, 8F, 0F, -1.189716F, 0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCScorpionEntity entity, float limbSwing, float limbSwingAmount,
                  float ageInTicks, float netHeadYaw, float headPitch) {
    if (entity.isHeld()) {
                limbSwing = 0F;
                limbSwingAmount = 0F;
                ageInTicks = 0F;
                netHeadYaw = 0F;
                headPitch = 0F;
    }
    boolean poisoning = entity.getStingTicks() != 0 && entity.getStingTicks() < 15;

        if (!poisoning) {
            body.xRot = 5F / R;
            tail1.xRot = 35F / R; tail1.y = 16F; tail1.z = 12F;
            tail2.xRot = 65F / R; tail2.y = 13F; tail2.z = 16.5F;
            tail3.xRot = 90F / R; tail3.y = 8F; tail3.z = 18.5F;
            tail4.xRot = 143F / R; tail4.y = 3F; tail4.z = 18F;
            tail5.xRot = 175F / R; tail5.y = -0.2F; tail5.z = 14F;
            sting1.xRot = 24F / R; sting1.y = -1F; sting1.z = 7F;
            sting2.xRot = -12F / R; sting2.y = 2.6F; sting2.z = 8.8F;
        } else {
            body.xRot = 50F / R;
            tail1.xRot = 100F / R; tail1.y = 9F; tail1.z = 10F;
            tail2.xRot = 160F / R; tail2.y = 3F; tail2.z = 9.5F;
            tail3.xRot = -170F / R; tail3.y = 1F; tail3.z = 3.5F;
            tail4.xRot = -156F / R; tail4.y = 1.8F; tail4.z = -2F;
            tail5.xRot = -154F / R; tail5.y = 3.8F; tail5.z = -7F;
            sting1.xRot = -57F / R; sting1.y = 6F; sting1.z = -12F;
            sting2.xRot = -93.7F / R; sting2.y = 8F; sting2.z = -15.2F;
        }

        // Mouth talk while making ambient noise.
        float mouthRot = 0F;
        if (entity.getMouthTicks() != 0) {
            mouthRot = Mth.cos(ageInTicks * 1.1F) * 0.2F;
        }
        mouthR.yRot = 22F / R + mouthRot;
        mouthL.yRot = -22F / R - mouthRot;

        lArm1.xRot = -20F / R;
        lArm2.x = 10F; lArm2.y = 14F; lArm2.z = -6F;
        lArm3.x = 12F; lArm3.y = 15F; lArm3.z = -11F;
        lArm4.x = 11F; lArm4.y = 15F; lArm4.z = -11F; lArm4.yRot = 0F;
        rArm1.xRot = -20F / R;
        rArm2.x = -10F; rArm2.y = 14F; rArm2.z = -6F;
        rArm3.x = -12F; rArm3.y = 15F; rArm3.z = -11F;
        rArm4.x = -11F; rArm4.y = 15F; rArm4.z = -11F; rArm4.yRot = 0F;

        int clawTicks = entity.getClawTicks();
        if (clawTicks == 0) {
            // Idle claw twitch — small periodic pincer flex, purely cosmetic.
            float lHand = 0F;
            float t1 = ageInTicks % 100F;
            if (t1 > 0F && t1 < 20F) {
                lHand = t1 / R;
            }
            lArm3.yRot = 9F / R - lHand;
            lArm4.yRot = lHand;
            float rHand = 0F;
            float t2 = ageInTicks % 75F;
            if (t2 > 30F && t2 < 50F) {
                rHand = (t2 - 29F) / R;
            }
            rArm3.yRot = -9F / R + rHand;
            rArm4.yRot = -rHand;
        } else if (clawTicks < 5) {
            lArm1.xRot = 50F / R;
            lArm2.x = 8F; lArm2.y = 15F; lArm2.z = -13F;
            lArm3.x = 10F; lArm3.y = 16F; lArm3.z = -18F;
            lArm4.x = 9F; lArm4.y = 16F; lArm4.z = -18F; lArm4.yRot = 40F / R;
        } else if (clawTicks < 10) {
            lArm1.xRot = 70F / R;
            lArm2.x = 7F; lArm2.y = 16F; lArm2.z = -14F;
            lArm3.x = 9F; lArm3.y = 17F; lArm3.z = -19F;
            lArm4.x = 8F; lArm4.y = 17F; lArm4.z = -19F; lArm4.yRot = 0F;
        } else if (clawTicks < 15) {
            rArm1.xRot = 50F / R;
            rArm2.x = -8F; rArm2.y = 15F; rArm2.z = -13F;
            rArm3.x = -10F; rArm3.y = 16F; rArm3.z = -18F;
            rArm4.x = -9F; rArm4.y = 16F; rArm4.z = -18F;
        } else if (clawTicks < 20) {
            rArm1.xRot = 70F / R;
            rArm2.x = -7F; rArm2.y = 16F; rArm2.z = -14F;
            rArm3.x = -9F; rArm3.y = 17F; rArm3.z = -19F;
            rArm4.x = -8F; rArm4.y = 17F; rArm4.z = -19F; rArm4.yRot = 0F;
        }

        boolean babies = entity.hasBabies();
        baby1.visible = babies;
        baby2.visible = babies;
        baby3.visible = babies;
        baby4.visible = babies;
        baby5.visible = babies;
        if (babies) {
            float fmov = ageInTicks % 100F;
            float fb1 = 0F;
            float fb2 = 142F / R;
            float fb3 = 49F / R;
            float fb4 = 155F / R;
            float fb5 = -68F / R;
            if (fmov > 0F && fmov < 20F) {
                fb2 -= Mth.cos(ageInTicks * 0.8F) * 0.3F;
                fb3 -= Mth.cos(ageInTicks * 0.6F) * 0.2F;
                fb1 += Mth.cos(ageInTicks * 0.4F) * 0.4F;
                fb5 += Mth.cos(ageInTicks * 0.7F) * 0.5F;
            }
            if (fmov > 30F && fmov < 50F) {
                fb4 -= Mth.cos(ageInTicks * 0.8F) * 0.4F;
                fb1 += Mth.cos(ageInTicks * 0.7F) * 0.1F;
                fb3 -= Mth.cos(ageInTicks * 0.6F) * 0.2F;
            }
            if (fmov > 80F) {
                fb5 += Mth.cos(ageInTicks * 0.2F) * 0.4F;
                fb2 -= Mth.cos(ageInTicks * 0.6F) * 0.3F;
                fb4 -= Mth.cos(ageInTicks * 0.4F) * 0.2F;
            }
            baby1.yRot = fb1;
            baby2.yRot = fb2;
            baby3.yRot = fb3;
            baby4.yRot = fb4;
            baby5.yRot = fb5;
        }

        // 8-leg walk cycle: 4 phase groups (legs 1/5, 2/6, 3/7, 4/8 mirror each
        // other), each leg's 3 segments always share the same x/z rotation.
        float f9 = -(Mth.cos(limbSwing * 0.6662F * 2F)) * 0.4F * limbSwingAmount;
        float f10 = -(Mth.cos(limbSwing * 0.6662F * 2F + (float) Math.PI)) * 0.4F * limbSwingAmount;
        float f11 = -(Mth.cos(limbSwing * 0.6662F * 2F + (float) (Math.PI / 2))) * 0.4F * limbSwingAmount;
        float f12 = -(Mth.cos(limbSwing * 0.6662F * 2F + (float) (Math.PI * 1.5))) * 0.4F * limbSwingAmount;
        float f13 = Math.abs(Mth.sin(limbSwing * 0.6662F)) * 0.4F * limbSwingAmount;
        float f14 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI)) * 0.4F * limbSwingAmount;
        float f15 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) (Math.PI / 2))) * 0.4F * limbSwingAmount;
        float f16 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) (Math.PI * 1.5))) * 0.4F * limbSwingAmount;

        leg1A.xRot = -10F / R; leg1A.zRot = 75F / R + f13;
        leg1A.xRot += f9;
        leg1B.xRot = leg1A.xRot; leg1B.zRot = 60F / R + f13;
        leg1C.xRot = leg1A.xRot; leg1C.zRot = 75F / R + f13;

        leg2A.xRot = -30F / R; leg2A.zRot = 70F / R + f14;
        leg2A.xRot += f10;
        leg2B.xRot = leg2A.xRot; leg2B.zRot = 60F / R + f14;
        leg2C.xRot = leg2A.xRot; leg2C.zRot = 70F / R + f14;

        leg3A.xRot = -45F / R; leg3A.zRot = 70F / R + f15;
        leg3A.xRot += f11;
        leg3B.xRot = leg3A.xRot; leg3B.zRot = 60F / R + f15;
        leg3C.xRot = leg3A.xRot; leg3C.zRot = 70F / R + f15;

        leg4A.xRot = -60F / R; leg4A.zRot = 70F / R + f16;
        leg4A.xRot += f12;
        leg4B.xRot = leg4A.xRot; leg4B.zRot = 60F / R + f16;
        leg4C.xRot = leg4A.xRot; leg4C.zRot = 70F / R + f16;

        leg5A.xRot = -10F / R; leg5A.zRot = -75F / R - f13;
        leg5A.xRot -= f9;
        leg5B.xRot = leg5A.xRot; leg5B.zRot = -60F / R - f13;
        leg5C.xRot = leg5A.xRot; leg5C.zRot = -75F / R - f13;

        leg6A.xRot = -30F / R; leg6A.zRot = -70F / R - f14;
        leg6A.xRot -= f10;
        leg6B.xRot = leg6A.xRot; leg6B.zRot = -70F / R - f14;
        leg6C.xRot = leg6A.xRot; leg6C.zRot = -70F / R - f14;

        leg7A.xRot = -45F / R; leg7A.zRot = -70F / R - f15;
        leg7A.xRot -= f11;
        leg7B.xRot = leg7A.xRot; leg7B.zRot = -70F / R - f15;
        leg7C.xRot = leg7A.xRot; leg7C.zRot = -70F / R - f15;

        leg8A.xRot = -60F / R; leg8A.zRot = -70F / R - f16;
        leg8A.xRot -= f12;
        leg8B.xRot = leg8A.xRot; leg8B.zRot = -70F / R - f16;
        leg8C.xRot = leg8A.xRot; leg8C.zRot = -70F / R - f16;

        headBackTrack(netHeadYaw, headPitch);
    }

    private void headBackTrack(float netHeadYaw, float headPitch) {
        // The original scorpion model has no separate head-tracking rotation
        // at all (Head never rotates toward the target) — intentional, no-op.
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}