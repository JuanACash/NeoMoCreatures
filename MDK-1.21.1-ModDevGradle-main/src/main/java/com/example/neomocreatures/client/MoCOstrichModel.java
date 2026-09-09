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
                PartPose.offsetAndRotation(0F, 4F, 11F, 20F / R, 0F, 0F));
        root.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(58, 18).addBox(-2.6F, -2F, -2F, 1, 4, 6),
                PartPose.offsetAndRotation(0F, 4F, 11F, 20F / R, -15F / R, 0F));
        root.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(30, 18).addBox(1.6F, -2F, -2F, 1, 4, 6),
                PartPose.offsetAndRotation(0F, 4F, 11F, 20F / R, 15F / R, 0F));

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

        boolean hiding = entity.isHiding();
        float headY;
        float headXRot;
        float headYRot;
        if (hiding) {
            headY = 15F;
            headXRot = 160F / R;
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

        lLegA.xRot = 10F / R + lLegXRot;
        lLegB.xRot = lLegA.xRot;
        lLegC.xRot = -15F / R + lLegXRot;
        lFoot.xRot = lLegA.xRot;
        rLegA.xRot = 10F / R + rLegXRot;
        rLegB.xRot = rLegA.xRot;
        rLegC.xRot = -15F / R + rLegXRot;
        rFoot.xRot = rLegA.xRot;

        float wingF = 10F / R + Mth.cos(limbSwing * 0.6F) * 0.2F * limbSwingAmount;
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

        float tailRot = Mth.cos(limbSwing * 0.5F) * 0.3F * limbSwingAmount;
        tail1.yRot = tailRot;
        tail2.yRot = tailRot - 15F / R;
        tail3.yRot = tailRot + 15F / R;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}