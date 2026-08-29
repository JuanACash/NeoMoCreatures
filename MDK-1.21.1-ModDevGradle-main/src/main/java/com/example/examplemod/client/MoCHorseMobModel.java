package com.example.examplemod.client;

import com.example.examplemod.entity.monster.MoCHorseMobEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;


/**
 * Same body/head/neck/leg/tail geometry as MoCHorsePlaceholderModel (the
 * original mod's HorseMob literally extends its horse model and just skips
 * the saddle parts — no reason to duplicate different numbers). Not yet
 * included: bathorse wings/horn, which need new geometry — bathorse
 * currently renders as a plain horse body with the bat texture.
 */
public class MoCHorseMobModel extends HierarchicalModel<MoCHorseMobEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart upperMouth;
    private final ModelPart lowerMouth;
    private final ModelPart upperMouthOpen;
    private final ModelPart lowerMouthOpen;
    private final ModelPart earLeft;
    private final ModelPart earRight;
    private final ModelPart mane;
    private final ModelPart tailA;
    private final ModelPart tailB;
    private final ModelPart tailC;

    private final ModelPart leg1Upper;
    private final ModelPart leg1Lower;
    private final ModelPart leg1Hoof;
    private final ModelPart leg2Upper;
    private final ModelPart leg2Lower;
    private final ModelPart leg2Hoof;
    private final ModelPart leg3Upper;
    private final ModelPart leg3Lower;
    private final ModelPart leg3Hoof;
    private final ModelPart leg4Upper;
    private final ModelPart leg4Lower;
    private final ModelPart leg4Hoof;

    private final ModelPart wingInnerL;
    private final ModelPart wingMidL;
    private final ModelPart wingOuterL;
    private final ModelPart wingInnerR;
    private final ModelPart wingMidR;
    private final ModelPart wingOuterR;

    public MoCHorseMobModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.upperMouth = root.getChild("upper_mouth");
        this.lowerMouth = root.getChild("lower_mouth");
        this.upperMouthOpen = root.getChild("upper_mouth_open");
        this.lowerMouthOpen = root.getChild("lower_mouth_open");
        this.earLeft = root.getChild("ear_left");
        this.earRight = root.getChild("ear_right");
        this.mane = root.getChild("mane");
        this.tailA = root.getChild("tail_a");
        this.tailB = root.getChild("tail_b");
        this.tailC = root.getChild("tail_c");

        this.leg1Upper = root.getChild("leg1_upper");
        this.leg1Lower = this.leg1Upper.getChild("leg1_lower");
        this.leg1Hoof = this.leg1Lower.getChild("leg1_hoof");
        this.leg2Upper = root.getChild("leg2_upper");
        this.leg2Lower = this.leg2Upper.getChild("leg2_lower");
        this.leg2Hoof = this.leg2Lower.getChild("leg2_hoof");
        this.leg3Upper = root.getChild("leg3_upper");
        this.leg3Lower = this.leg3Upper.getChild("leg3_lower");
        this.leg3Hoof = this.leg3Lower.getChild("leg3_hoof");
        this.leg4Upper = root.getChild("leg4_upper");
        this.leg4Lower = this.leg4Upper.getChild("leg4_lower");
        this.leg4Hoof = this.leg4Lower.getChild("leg4_hoof");

        this.wingInnerL = root.getChild("wing_inner_l");
        this.wingMidL = root.getChild("wing_mid_l");
        this.wingOuterL = root.getChild("wing_outer_l");
        this.wingInnerR = root.getChild("wing_inner_r");
        this.wingMidR = root.getChild("wing_mid_r");
        this.wingOuterR = root.getChild("wing_outer_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 34).addBox(-5F, -8F, -19F, 10, 10, 24),
                PartPose.offset(0F, 11F, 9F));

        PartPose headPivot = PartPose.offsetAndRotation(0F, 4F, -10F, 0.5235988F, 0F, 0F);
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -10F, -1.5F, 5, 5, 7), headPivot);
        root.addOrReplaceChild("upper_mouth",
                CubeListBuilder.create().texOffs(24, 18).addBox(-2F, -10F, -7F, 4, 3, 6), headPivot);
        root.addOrReplaceChild("lower_mouth",
                CubeListBuilder.create().texOffs(24, 27).addBox(-2F, -7F, -6.5F, 4, 2, 5), headPivot);
        root.addOrReplaceChild("upper_mouth_open",
                CubeListBuilder.create().texOffs(24, 18).addBox(-2F, -10F, -8F, 4, 3, 6), headPivot);
        root.addOrReplaceChild("lower_mouth_open",
                CubeListBuilder.create().texOffs(24, 27).addBox(-2F, -7F, -5.5F, 4, 2, 5), headPivot);
        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2.05F, -9.8F, -2F, 4, 14, 8), headPivot);
        root.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.45F, -12F, 4F, 2, 3, 1), headPivot);
        root.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.45F, -12F, 4F, 2, 3, 1), headPivot);
        root.addOrReplaceChild("mane",
                CubeListBuilder.create().texOffs(58, 0).addBox(-1F, -11.5F, 5F, 2, 16, 4), headPivot);

        root.addOrReplaceChild("tail_a",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1F, -1F, 0F, 2, 2, 3),
                PartPose.offsetAndRotation(0F, 3F, 14F, -1.134464F, 0F, 0F));
        root.addOrReplaceChild("tail_b",
                CubeListBuilder.create().texOffs(38, 7).addBox(-1.5F, -2F, 3F, 3, 4, 7),
                PartPose.offsetAndRotation(0F, 3F, 14F, -1.134464F, 0F, 0F));
        root.addOrReplaceChild("tail_c",
                CubeListBuilder.create().texOffs(24, 3).addBox(-1.5F, -4.5F, 9F, 3, 4, 7),
                PartPose.offsetAndRotation(0F, 3F, 14F, -1.40215F, 0F, 0F));

        PartDefinition leg1Upper = root.addOrReplaceChild("leg1_upper",
                CubeListBuilder.create().texOffs(78, 29).addBox(-2.5F, -2F, -2.5F, 4, 9, 5),
                PartPose.offset(4F, 9F, 11F));
        PartDefinition leg1Lower = leg1Upper.addOrReplaceChild("leg1_lower",
                CubeListBuilder.create().texOffs(78, 43).addBox(-2F, 0F, -1.5F, 3, 5, 3),
                PartPose.offset(0F, 7F, 0F));
        leg1Lower.addOrReplaceChild("leg1_hoof",
                CubeListBuilder.create().texOffs(78, 51).addBox(-2.5F, 5.1F, -2F, 4, 3, 4),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition leg2Upper = root.addOrReplaceChild("leg2_upper",
                CubeListBuilder.create().texOffs(96, 29).addBox(-1.5F, -2F, -2.5F, 4, 9, 5),
                PartPose.offset(-4F, 9F, 11F));
        PartDefinition leg2Lower = leg2Upper.addOrReplaceChild("leg2_lower",
                CubeListBuilder.create().texOffs(96, 43).addBox(-1F, 0F, -1.5F, 3, 5, 3),
                PartPose.offset(0F, 7F, 0F));
        leg2Lower.addOrReplaceChild("leg2_hoof",
                CubeListBuilder.create().texOffs(96, 51).addBox(-1.5F, 5.1F, -2F, 4, 3, 4),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition leg3Upper = root.addOrReplaceChild("leg3_upper",
                CubeListBuilder.create().texOffs(44, 29).addBox(-1.9F, -1F, -2.1F, 3, 8, 4),
                PartPose.offset(4F, 9F, -8F));
        PartDefinition leg3Lower = leg3Upper.addOrReplaceChild("leg3_lower",
                CubeListBuilder.create().texOffs(44, 41).addBox(-1.9F, 0F, -1.6F, 3, 5, 3),
                PartPose.offset(0F, 7F, 0F));
        leg3Lower.addOrReplaceChild("leg3_hoof",
                CubeListBuilder.create().texOffs(44, 51).addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition leg4Upper = root.addOrReplaceChild("leg4_upper",
                CubeListBuilder.create().texOffs(60, 29).addBox(-1.1F, -1F, -2.1F, 3, 8, 4),
                PartPose.offset(-4F, 9F, -8F));
        PartDefinition leg4Lower = leg4Upper.addOrReplaceChild("leg4_lower",
                CubeListBuilder.create().texOffs(60, 41).addBox(-1.1F, 0F, -1.6F, 3, 5, 3),
                PartPose.offset(0F, 7F, 0F));
        leg4Lower.addOrReplaceChild("leg4_hoof",
                CubeListBuilder.create().texOffs(60, 51).addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4),
                PartPose.offset(0F, 0F, 0F));

        root.addOrReplaceChild("wing_inner_l",
                CubeListBuilder.create().texOffs(0, 96).addBox(0F, 0F, 0F, 7, 2, 11),
                PartPose.offsetAndRotation(5F, 3F, -6F, 0F, -0.3490659F, 0F));
        root.addOrReplaceChild("wing_mid_l",
                CubeListBuilder.create().texOffs(82, 68).addBox(1F, 0.1F, 1F, 12, 2, 11),
                PartPose.offsetAndRotation(5F, 3F, -6F, 0F, 0.0872665F, 0F));
        root.addOrReplaceChild("wing_outer_l",
                CubeListBuilder.create().texOffs(0, 68).addBox(0F, 0F, 0F, 22, 2, 11),
                PartPose.offsetAndRotation(17F, 3F, -6F, 0F, -0.3228859F, 0F));

        root.addOrReplaceChild("wing_inner_r",
                CubeListBuilder.create().texOffs(0, 110).addBox(-7F, 0F, 0F, 7, 2, 11),
                PartPose.offsetAndRotation(-5F, 3F, -6F, 0F, 0.3490659F, 0F));
        root.addOrReplaceChild("wing_mid_r",
                CubeListBuilder.create().texOffs(82, 82).addBox(-13F, 0.1F, 1F, 12, 2, 11),
                PartPose.offsetAndRotation(-5F, 3F, -6F, 0F, -0.0872665F, 0F));
        root.addOrReplaceChild("wing_outer_r",
                CubeListBuilder.create().texOffs(0, 82).addBox(-22F, 0F, 0F, 22, 2, 11),
                PartPose.offsetAndRotation(-17F, 3F, -6F, 0F, 0.3228859F, 0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCHorseMobEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float f = limbSwing;
        float f1 = limbSwingAmount;

        float rLegXRot = Mth.cos(f * 0.6662F + (float) Math.PI) * 0.8F * f1;
        float lLegXRot = Mth.cos(f * 0.6662F) * 0.8F * f1;

        float rLegXRotB = rLegXRot;
        float lLegXRotB = lLegXRot;
        float rLegXRotC = rLegXRot;
        float lLegXRotC = lLegXRot;

        if (f1 > 0.1F) {
            float rLegXRot2 = Mth.cos((f + 0.1F) * 0.6662F + (float) Math.PI) * 0.8F * f1;
            float lLegXRot2 = Mth.cos((f + 0.1F) * 0.6662F) * 0.8F * f1;
            if (rLegXRot > rLegXRot2) rLegXRotB = rLegXRot + 0.9599F;
            if (rLegXRot < rLegXRot2) rLegXRotC = rLegXRot + 0.2618F;
            if (lLegXRot > lLegXRot2) lLegXRotB = lLegXRot + 0.9599F;
            if (lLegXRot < lLegXRot2) lLegXRotC = lLegXRot + 0.2618F;
        }

        setLegAngle(this.leg1Upper, this.leg1Lower, this.leg1Hoof, lLegXRot, lLegXRotC);
        setLegAngle(this.leg2Upper, this.leg2Lower, this.leg2Hoof, rLegXRot, rLegXRotC);
        setLegAngle(this.leg3Upper, this.leg3Lower, this.leg3Hoof, rLegXRot, rLegXRotB);
        setLegAngle(this.leg4Upper, this.leg4Lower, this.leg4Hoof, lLegXRot, lLegXRotB);

        float tailSway = limbSwingAmount > 0.05F ? Mth.cos(ageInTicks * 0.3F) * 0.15F : 0.0F;
        this.tailA.yRot = tailSway;
        this.tailB.yRot = tailSway;
        this.tailC.yRot = tailSway;

        float headBob = limbSwingAmount > 0.05F ? Mth.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount : 0.0F;
        float headXRot = 0.5235988F + headBob;
        this.head.xRot = headXRot;
        this.neck.xRot = headXRot;
        this.upperMouth.xRot = headXRot;
        this.lowerMouth.xRot = headXRot;
        this.earLeft.xRot = headXRot;
        this.earRight.xRot = headXRot;
        this.mane.xRot = headXRot;

        boolean mouthOpen = entity.getMouthTicks() > 0;
        this.upperMouth.visible = !mouthOpen;
        this.lowerMouth.visible = !mouthOpen;
        this.upperMouthOpen.visible = mouthOpen;
        this.lowerMouthOpen.visible = mouthOpen;
        this.upperMouthOpen.xRot = headXRot - 0.0872664F;
        this.lowerMouthOpen.xRot = headXRot + 0.261799F;

        boolean isBat = entity.getVariant() == com.example.examplemod.entity.monster.MoCHorseMobEntity.Variant.BATHORSE;
        this.wingInnerL.visible = isBat;
        this.wingMidL.visible = isBat;
        this.wingOuterL.visible = isBat;
        this.wingInnerR.visible = isBat;
        this.wingMidR.visible = isBat;
        this.wingOuterR.visible = isBat;

        if (isBat) {
                boolean flying = entity.isSoaring();

                float wingRot = flying
                        ? Mth.cos(ageInTicks * 0.3F + (float) Math.PI) * 1.2F
                        : 60F / 57.29578F; // plegada — mismo valor que usaba el original para Todo (rotación Y posición)

                // Rotación Z (bisagra del aleteo) — mismo ángulo absoluto en las 3
                // piezas, tal cual el original (sin anidar, es un modelo plano).
                this.wingInnerL.zRot = wingRot;
                this.wingMidL.zRot = wingRot;
                this.wingOuterL.zRot = wingRot;
                this.wingInnerR.zRot = -wingRot;
                this.wingMidR.zRot = -wingRot;
                this.wingOuterR.zRot = -wingRot;

                // Rotación Y — solo la punta exterior la cambia.
                if (flying) {
                        this.wingOuterL.yRot = -0.3228859F + wingRot / 2F;
                        this.wingOuterR.yRot = 0.3228859F - wingRot / 2F;
                } else {
                        this.wingOuterL.yRot = -90F / 57.29578F;
                        this.wingOuterR.yRot = 90F / 57.29578F;
                }

                // Posición — esto es lo que faltaba: la punta exterior se recalcula
                // con seno/coseno para seguir pegada a las otras dos piezas mientras
                // el ala gira, en vez de quedarse fija en su punto de fábrica.
                this.wingInnerL.x = 5F; this.wingInnerL.y = 3F; this.wingInnerL.z = -6F;
                this.wingMidL.x = 5F; this.wingMidL.y = 3F; this.wingMidL.z = -6F;
                this.wingOuterL.x = 5F + Mth.cos(wingRot) * 12F;
                this.wingOuterL.y = 3F + Mth.sin(wingRot) * 12F;
                this.wingOuterL.z = -6F;

                this.wingInnerR.x = -5F; this.wingInnerR.y = 3F; this.wingInnerR.z = -6F;
                this.wingMidR.x = -5F; this.wingMidR.y = 3F; this.wingMidR.z = -6F;
                this.wingOuterR.x = -5F - Mth.cos(wingRot) * 12F;
                this.wingOuterR.y = 3F + Mth.sin(wingRot) * 12F;
                this.wingOuterR.z = -6F;

                if (flying) {
                        float upperFold = 15F / 57.29578F;
                        float lowerFold = 45F / 57.29578F;
                        setLegAngle(this.leg1Upper, this.leg1Lower, this.leg1Hoof, upperFold, lowerFold);
                        setLegAngle(this.leg2Upper, this.leg2Lower, this.leg2Hoof, upperFold, lowerFold);
                        setLegAngle(this.leg3Upper, this.leg3Lower, this.leg3Hoof, upperFold, lowerFold);
                        setLegAngle(this.leg4Upper, this.leg4Lower, this.leg4Hoof, upperFold, lowerFold);
                }
                }
    }

    private static void setLegAngle(ModelPart upper, ModelPart lower, ModelPart hoof, float upperAngle, float lowerAngle) {
        upper.xRot = upperAngle;
        lower.xRot = lowerAngle - upperAngle;
        hoof.xRot = 0F;
    }
}