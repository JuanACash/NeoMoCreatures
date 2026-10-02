package com.example.neomocreatures.client;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.entity.MoCHorseEntity;

import java.util.EnumSet;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * NOTE on the head/neck cluster: decompiled a newer official build
 * (MoCModelNewHorse.class, from the assets zip) to check how the original
 * mod's "eating" pose avoided a gap at the neck-body joint. It does NOT use
 * a two-bone neck — it shifts the whole head/neck pivot's Y from 4 to 11
 * while rotating from 30° to a fixed 125° (2.18166F), and every part in
 * this cluster (Neck, ears, mouth, mane) copies Head's position/rotation
 * exactly. That's what's ported below (single rigid cluster, same as the
 * original), replacing an earlier two-bone experiment that wasn't based on
 * a verified reference.
 */
public class MoCHorseModel extends HierarchicalModel<MoCHorseEntity> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart upperMouth;
    private final ModelPart lowerMouth;
    private final ModelPart upperMouthOpen;
    private final ModelPart lowerMouthOpen;
    private final ModelPart earLeft;
    private final ModelPart earRight;
    private final ModelPart muleEarLeft;
    private final ModelPart muleEarRight;
    private final ModelPart mane;
    private final ModelPart horn;
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
    private final ModelPart wingButterflyL;
    private final ModelPart wingButterflyR;
    private final ModelPart wingButterflyWideL;
    private final ModelPart wingButterflyWideR;

    private final ModelPart saddleTop;
    private final ModelPart bagLeft;
    private final ModelPart bagRight;
    private final ModelPart saddleBack;
    private final ModelPart saddleFront;
    private final ModelPart strapLeftUpper;
    private final ModelPart strapLeftLower;
    private final ModelPart strapRightUpper;
    private final ModelPart strapRightLower;
    private final ModelPart mouthStrapLeft;
    private final ModelPart mouthStrapRight;
    private final ModelPart reinLeft;
    private final ModelPart reinRight;
    private final ModelPart headSaddle;
    

    public MoCHorseModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.upperMouth = root.getChild("upper_mouth");
        this.lowerMouth = root.getChild("lower_mouth");
        this.upperMouthOpen = root.getChild("upper_mouth_open");
        this.lowerMouthOpen = root.getChild("lower_mouth_open");
        this.earLeft = root.getChild("ear_left");
        this.earRight = root.getChild("ear_right");
        this.muleEarLeft = root.getChild("mule_ear_left");
        this.muleEarRight = root.getChild("mule_ear_right");
        this.mane = root.getChild("mane");
        this.horn = root.getChild("horn");
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
        this.wingButterflyL = root.getChild("wing_butterfly_l");
        this.wingButterflyR = root.getChild("wing_butterfly_r");
        this.wingButterflyWideL = root.getChild("wing_butterfly_wide_l");
        this.wingButterflyWideR = root.getChild("wing_butterfly_wide_r");

        this.saddleTop = root.getChild("saddle_top");
        this.saddleBack = root.getChild("saddle_back");
        this.saddleFront = root.getChild("saddle_front");
        this.strapLeftUpper = root.getChild("strap_left_upper");
        this.strapLeftLower = root.getChild("strap_left_lower");
        this.strapRightUpper = root.getChild("strap_right_upper");
        this.strapRightLower = root.getChild("strap_right_lower");
        this.mouthStrapLeft = root.getChild("mouth_strap_left");
        this.mouthStrapRight = root.getChild("mouth_strap_right");
        this.reinLeft = root.getChild("rein_left");
        this.reinRight = root.getChild("rein_right");
        this.headSaddle = root.getChild("head_saddle");
        this.bagLeft = root.getChild("bag_left");
        this.bagRight = root.getChild("bag_right");
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
        root.addOrReplaceChild("mule_ear_left",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2F, -16F, 4F, 2, 7, 1),
                PartPose.offsetAndRotation(0F, 4F, -10F, 0.5235988F, 0F, 0.2617994F));
        root.addOrReplaceChild("mule_ear_right",
                CubeListBuilder.create().texOffs(0, 12).addBox(0F, -16F, 4F, 2, 7, 1),
                PartPose.offsetAndRotation(0F, 4F, -10F, 0.5235988F, 0F, -0.2617994F));

        root.addOrReplaceChild("mane",
                CubeListBuilder.create().texOffs(58, 0).addBox(-1F, -11.5F, 5F, 2, 16, 4), headPivot);
        root.addOrReplaceChild("horn",
                CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -18F, 2F, 1, 8, 1), headPivot);

        root.addOrReplaceChild("saddle_top",
                CubeListBuilder.create().texOffs(80, 0).addBox(-5F, 0F, -3F, 10, 1, 8),
                PartPose.offset(0F, 2F, 2F));
        root.addOrReplaceChild("saddle_back",
                CubeListBuilder.create().texOffs(106, 9).addBox(-1.5F, -1F, -3F, 3, 1, 2),
                PartPose.offset(0F, 2F, 2F));
        root.addOrReplaceChild("saddle_front",
                CubeListBuilder.create().texOffs(80, 9).addBox(-4F, -1F, 3F, 8, 1, 2),
                PartPose.offset(0F, 2F, 2F));
        root.addOrReplaceChild("strap_left_lower",
                CubeListBuilder.create().texOffs(70, 0).addBox(-0.5F, 0F, -0.5F, 1, 6, 1),
                PartPose.offset(5F, 3F, 2F));
        root.addOrReplaceChild("strap_left_upper",
                CubeListBuilder.create().texOffs(74, 0).addBox(-0.5F, 6F, -1F, 1, 2, 2),
                PartPose.offset(5F, 3F, 2F));
        root.addOrReplaceChild("strap_right_lower",
                CubeListBuilder.create().texOffs(80, 0).addBox(-0.5F, 0F, -0.5F, 1, 6, 1),
                PartPose.offset(-5F, 3F, 2F));
        root.addOrReplaceChild("strap_right_upper",
                CubeListBuilder.create().texOffs(74, 4).addBox(-0.5F, 6F, -1F, 1, 2, 2),
                PartPose.offset(-5F, 3F, 2F));
        root.addOrReplaceChild("mouth_strap_left",
                CubeListBuilder.create().texOffs(74, 13).addBox(1.5F, -8F, -4F, 1, 2, 2), headPivot);
        root.addOrReplaceChild("mouth_strap_right",
                CubeListBuilder.create().texOffs(74, 13).addBox(-2.5F, -8F, -4F, 1, 2, 2), headPivot);
        root.addOrReplaceChild("rein_left",
                CubeListBuilder.create().texOffs(44, 10).addBox(2.6F, -6F, -6F, 0, 3, 16), headPivot);
        root.addOrReplaceChild("rein_right",
                CubeListBuilder.create().texOffs(44, 5).addBox(-2.6F, -6F, -6F, 0, 3, 16), headPivot);
        root.addOrReplaceChild("head_saddle",
                CubeListBuilder.create().texOffs(80, 12).addBox(-2.5F, -10.1F, -7F, 5, 5, 12, new CubeDeformation(0.2F)), headPivot);
        root.addOrReplaceChild("bag_left",
                CubeListBuilder.create().texOffs(0, 34).addBox(-3F, 0F, 0F, 8, 8, 3),
                PartPose.offsetAndRotation(-7.5F, 3.5F, 10F, 0F, 1.570796F, 0F));
        root.addOrReplaceChild("bag_right",
                CubeListBuilder.create().texOffs(0, 47).addBox(-3F, 0F, 0F, 8, 8, 3),
                PartPose.offsetAndRotation(4.5F, 3.5F, 10F, 0F, 1.570796F, 0F));

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

        root.addOrReplaceChild("wing_butterfly_l",
                CubeListBuilder.create().texOffs(0, 98).addBox(-1F, 0F, -14F, 26, 0, 30),
                PartPose.offsetAndRotation(4.5F, 4F, -2F, 0F, 0F, -0.7853982F));
        root.addOrReplaceChild("wing_butterfly_r",
                CubeListBuilder.create().texOffs(0, 68).addBox(-25F, 0F, -14F, 26, 0, 30),
                PartPose.offsetAndRotation(-4.5F, 4F, -2F, 0F, 0F, 0.7853982F));

        // Variant for horsefairywhite, horsefairypink, and horsefairyblue:
        // the normal wing (dy=0) automatically generates TWO overlapping faces
        // on the exact same plane (the cube's "top" and "bottom"), each with a
        // slightly different wing texture -> that's what was flickering/looking
        // duplicated. Here we restrict the cube to a single visible face (DOWN),
        // so only one consistent wing gets drawn and there's nothing left to z-fight.
        root.addOrReplaceChild("wing_butterfly_wide_l",
                CubeListBuilder.create()
                        .texOffs(0, 98).addBox(-1F, 0F, -14F, 26, 0, 30,
                                EnumSet.of(Direction.UP))
                        .texOffs(0, 98).addBox(-1F, -0.5F, -14F, 26, 0, 30,
                                EnumSet.of(Direction.DOWN)),
                PartPose.offsetAndRotation(5.3F, 4F, -2F, 0F, 0F, -0.7853982F));
        root.addOrReplaceChild("wing_butterfly_wide_r",
                CubeListBuilder.create()
                        .texOffs(0, 68).addBox(-25F, 0F, -14F, 26, 0, 30,
                                EnumSet.of(Direction.UP))
                        .texOffs(0, 68).addBox(-25F, -0.5F, -14F, 26, 0, 30,
                                EnumSet.of(Direction.DOWN)),
                PartPose.offsetAndRotation(-5.3F, 4F, -2F, 0F, 0F, 0.7853982F));

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

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        boolean normalWingsVisible = this.wingButterflyL.visible;
        boolean wideWingsVisible = this.wingButterflyWideL.visible;

        // first pass: the whole model, without the butterfly wings
        this.wingButterflyL.visible = false;
        this.wingButterflyR.visible = false;
        this.wingButterflyWideL.visible = false;
        this.wingButterflyWideR.visible = false;
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);

        // second pass: only the butterfly wings, with reinforced color
        this.wingButterflyL.visible = normalWingsVisible;
        this.wingButterflyR.visible = normalWingsVisible;
        this.wingButterflyWideL.visible = wideWingsVisible;
        this.wingButterflyWideR.visible = wideWingsVisible;

        VertexConsumer brightened = new FullBrightVertexConsumer(buffer);        this.wingButterflyL.render(poseStack, brightened, packedLight, packedOverlay, color);
        this.wingButterflyR.render(poseStack, brightened, packedLight, packedOverlay, color);
        this.wingButterflyWideL.render(poseStack, brightened, packedLight, packedOverlay, color);
        this.wingButterflyWideR.render(poseStack, brightened, packedLight, packedOverlay, color);
        }

    @Override
    public void setupAnim(MoCHorseEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        boolean longEared = entity.getSpecies() == Species.DONKEY
                || entity.getSpecies() == Species.MULE
                || entity.getSpecies() == Species.ZONKY;
        this.earLeft.visible = !longEared;
        this.earRight.visible = !longEared;
        this.muleEarLeft.visible = longEared;
        this.muleEarRight.visible = longEared;
        this.horn.visible = entity.getSpecies() == Species.UNICORN || entity.getSpecies() == Species.FAIRY_HORSE;

        boolean isFairy = entity.getSpecies() == MoCHorseGenetics.Species.FAIRY_HORSE;
        boolean isGhostWinged = entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED;

        // horsefairywhite, horsefairypink, and horsefairyblue use the "wide"
        // wing (a single face, no front/back duplicate) to avoid flickering.
        boolean useWideWings = isFairy && (
                entity.getFairyColor() == MoCHorseGenetics.FairyColor.WHITE
                        || entity.getFairyColor() == MoCHorseGenetics.FairyColor.PINK
                        || entity.getFairyColor() == MoCHorseGenetics.FairyColor.BLUE);

        this.wingButterflyL.visible = (isFairy && !useWideWings) || isGhostWinged;
        this.wingButterflyR.visible = (isFairy && !useWideWings) || isGhostWinged;
        this.wingButterflyWideL.visible = useWideWings;
        this.wingButterflyWideR.visible = useWideWings;

        if (isFairy || isGhostWinged) {
        boolean flyingNow = !entity.onGround();
        float wingRot;

        if (flyingNow) {
                wingRot = Mth.cos(ageInTicks * 0.3F) * 0.55F;
        } else if (isFairy) {
                int flapTicks = entity.getWingFlapTicks();
                if (flapTicks > 0) {
                int elapsed = MoCHorseEntity.WING_FLAP_DURATION_TICKS - flapTicks;
                float easeIn = Math.min(1F, elapsed / 5F);
                float easeOut = Math.min(1F, flapTicks / 5F);
                float flapAmount = Math.min(easeIn, easeOut);
                wingRot = flapAmount * 0.9F;
                } else {
                wingRot = 0F;
                }
        } else {
                wingRot = 0F;
        }

        float baseAngle = 0.52359F;
        this.wingButterflyL.zRot = -baseAngle + wingRot;
        this.wingButterflyR.zRot = baseAngle - wingRot;
        this.wingButterflyWideL.zRot = -baseAngle + wingRot;
        this.wingButterflyWideR.zRot = baseAngle - wingRot;
        }

        boolean saddled = entity.isSaddled();
        this.saddleTop.visible = saddled;
        this.saddleBack.visible = saddled;
        this.saddleFront.visible = saddled;
        this.strapLeftUpper.visible = saddled;
        this.strapLeftLower.visible = saddled;
        this.strapRightUpper.visible = saddled;
        this.strapRightLower.visible = saddled;
        this.mouthStrapLeft.visible = saddled;
        this.mouthStrapRight.visible = saddled;
        this.reinLeft.visible = saddled && entity.isVehicle();
        this.reinRight.visible = saddled && entity.isVehicle();
        this.headSaddle.visible = saddled;

        boolean chested = entity.hasChest();
        this.bagLeft.visible = chested;
        this.bagRight.visible = chested;

        int grazeTicks = entity.getGrazeTicks();
        boolean grazing = grazeTicks > 0;

        float biteAmount = 0F;
        if (grazing) {
            int elapsed = MoCHorseEntity.GRAZE_DURATION_TICKS - grazeTicks;
            float easeIn = Math.min(1F, elapsed / 5F);
            float easeOut = Math.min(1F, grazeTicks / 5F);
            biteAmount = Math.min(easeIn, easeOut);
        }

        boolean chewing = grazing && biteAmount > 0.9F && Mth.sin(ageInTicks * 0.5F) > 0F;

        boolean mouthOpen = entity.getMouthTicks() > 0 || chewing;
        this.upperMouth.visible = !mouthOpen;
        this.lowerMouth.visible = !mouthOpen;
        this.upperMouthOpen.visible = mouthOpen;
        this.lowerMouthOpen.visible = mouthOpen;

        float f = limbSwing;
        float f1 = limbSwingAmount;

        boolean dancing = entity.getSpecies() == Species.ZEBRA && entity.isDancing();
        // isBucking() is a mod-specific flag (DATA_BUCKING_TICKS), not
        // vanilla's generic isStanding(): that one also triggers while
        // charging a mounted jump, and we don't want the rearing pose there —
        // only when a mount-based taming attempt fails.
        boolean rearing = entity.isBucking();

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

        if (rearing) {
                // Front legs (leg3/leg4): the pivot moves up and forward so that,
                // when raised, the whole leg ends up at chest height instead of
                // rotating around the spot where it was planted.
                this.leg3Upper.y = -2F;
                this.leg3Upper.z = -2F;
                this.leg4Upper.y = -2F;
                this.leg4Upper.z = -2F;

                float frontRightUpper = -1.0471976F + Mth.cos(ageInTicks * 0.4F + (float) Math.PI);
                float frontLeftUpper = -1.0471976F + Mth.cos(ageInTicks * 0.4F);
                float frontLower = 0.7853982F;
                float rearBrace = 0.2617994F;

                setLegAngle(this.leg3Upper, this.leg3Lower, this.leg3Hoof, frontRightUpper, frontLower);
                setLegAngle(this.leg4Upper, this.leg4Lower, this.leg4Hoof, frontLeftUpper, frontLower);
                setLegAngle(this.leg1Upper, this.leg1Lower, this.leg1Hoof, -rearBrace, -rearBrace);
                setLegAngle(this.leg2Upper, this.leg2Lower, this.leg2Hoof, rearBrace, rearBrace);
        } else {
                this.leg3Upper.y = 9F;
                this.leg3Upper.z = -8F;
                this.leg4Upper.y = 9F;
                this.leg4Upper.z = -8F;

                setLegAngle(this.leg1Upper, this.leg1Lower, this.leg1Hoof, lLegXRot, lLegXRotC);
                setLegAngle(this.leg2Upper, this.leg2Lower, this.leg2Hoof, rLegXRot, rLegXRotC);
                setLegAngle(this.leg3Upper, this.leg3Lower, this.leg3Hoof, rLegXRot, rLegXRotB);
                setLegAngle(this.leg4Upper, this.leg4Lower, this.leg4Hoof, lLegXRot, lLegXRotB);
                if (dancing) {
                        float danceRight = Mth.cos(ageInTicks * 0.4F);
                        if (danceRight > 0.1F) danceRight = 0.3F;
                        float danceLeft = Mth.cos(ageInTicks * 0.4F + (float) Math.PI);
                        if (danceLeft > 0.1F) danceLeft = 0.3F;
                        this.leg3Upper.xRot = danceRight;
                        this.leg4Upper.xRot = danceLeft;
                        }
        }

        // Body and tail follow the rearing tilt; outside of rearing they go
        // back to their neutral position (these ModelParts aren't rebuilt
        // every frame, so they have to be restored explicitly in the else).
        this.body.xRot = rearing ? -0.7853982F : 0F;
        float tailPivotY = rearing ? 9F : 3F;
        float tailPivotZ = rearing ? 18F : 14F;
        this.tailA.y = tailPivotY;
        this.tailA.z = tailPivotZ;
        this.tailB.y = tailPivotY;
        this.tailB.z = tailPivotZ;
        this.tailC.y = tailPivotY;
        this.tailC.z = tailPivotZ;


        float tailSway = (grazing || limbSwingAmount > 0.05F) ? Mth.cos(ageInTicks * 0.3F) * 0.15F : 0.0F;
        this.tailA.yRot = tailSway;
        this.tailB.yRot = tailSway;
        this.tailC.yRot = tailSway;

        float headBob = entity.isFlyingNow() ? 0F : (dancing
                ? Mth.cos(ageInTicks * 0.4F) * 0.15F
                : (limbSwingAmount > 0.05F ? Mth.cos(limbSwing * 0.4F) * 0.15F * limbSwingAmount : 0.0F));
        float restAngle = 0.5235988F + headBob;
        float grazeAngle = 2.18166F;
        float restY = 4F;
        float grazeY = 11F;

        float headXRot = restAngle + (grazeAngle - restAngle) * biteAmount;
        float headY = restY + (grazeY - restY) * biteAmount;
        float headZ = -10F;

        // --- Unicorn ability animation (independent from grazing) ---
        // Just lowers/raises the head to the same position as grazing, but
        // without the mouth animation (that's controlled separately by
        // DATA_MOUTH_TICKS, and is never touched here). Lasts exactly as long
        // as the ability (UNICORN_CHARGE_DURATION_TICKS) and then returns to
        // the normal position.
        int chargeTicks = entity.getUnicornChargeTicks();
        if (chargeTicks > 0) {
            int elapsed = MoCHorseEntity.UNICORN_CHARGE_DURATION_TICKS - chargeTicks;
            float easeIn = Math.min(1F, elapsed / 5F);
            float easeOut = Math.min(1F, chargeTicks / 5F);
            float chargeAmount = Math.min(easeIn, easeOut);

            headXRot = restAngle + (grazeAngle - restAngle) * chargeAmount;
            headY = restY + (grazeY - restY) * chargeAmount;
        }

        if (rearing) {
                // Head thrown back, above the chest line, like in vanilla's rearing.
                // Takes priority over grazing or the unicorn charge because it's an
                // involuntary reaction.
            headXRot = 0.2617994F;
            headY = -6F;
            headZ = -1F;
        }

        this.head.xRot = headXRot;

        this.head.xRot = headXRot;
        this.head.y = headY;
        this.head.z = headZ;
        this.neck.xRot = headXRot;
        this.neck.y = headY;
        this.neck.z = headZ;
        this.upperMouth.xRot = headXRot;
        this.upperMouth.y = headY;
        this.upperMouth.z = headZ;
        this.lowerMouth.xRot = headXRot;
        this.lowerMouth.y = headY;
        this.lowerMouth.z = headZ;
        this.upperMouthOpen.xRot = headXRot - 0.0872664F;
        this.upperMouthOpen.y = headY;
        this.upperMouthOpen.z = headZ;
        this.lowerMouthOpen.xRot = headXRot + 0.261799F;
        this.lowerMouthOpen.y = headY;
        this.lowerMouthOpen.z = headZ;
        this.earLeft.xRot = headXRot;
        this.earLeft.y = headY;
        this.earLeft.z = headZ;
        this.earRight.xRot = headXRot;
        this.earRight.y = headY;
        this.earRight.z = headZ;
        this.muleEarLeft.xRot = headXRot;
        this.muleEarLeft.y = headY;
        this.muleEarLeft.z = headZ;
        this.muleEarRight.xRot = headXRot;
        this.muleEarRight.y = headY;
        this.muleEarRight.z = headZ;
        this.mane.xRot = headXRot;
        this.mane.y = headY;
        this.mane.z = headZ;
        this.horn.xRot = headXRot;
        this.horn.y = headY;
        this.horn.z = headZ;
        this.headSaddle.xRot = headXRot;
        this.headSaddle.y = headY;
        this.headSaddle.z = headZ;
        this.mouthStrapLeft.xRot = headXRot;
        this.mouthStrapLeft.y = headY;
        this.mouthStrapLeft.z = headZ;
        this.mouthStrapRight.xRot = headXRot;
        this.mouthStrapRight.y = headY;
        this.mouthStrapRight.z = headZ;
        this.reinLeft.xRot = headXRot;
        this.reinLeft.y = headY;
        this.reinLeft.z = headZ;
        this.reinRight.xRot = headXRot;
        this.reinRight.y = headY;
        this.reinRight.z = headZ;

        boolean hasSegmentedWings = entity.getSpecies() == MoCHorseGenetics.Species.BATHORSE
                || entity.getSpecies() == MoCHorseGenetics.Species.PEGASUS
                || entity.getSpecies() == MoCHorseGenetics.Species.DARK_PEGASUS;
        boolean isGhost = entity.getSpecies() == MoCHorseGenetics.Species.GHOST
                || entity.getSpecies() == MoCHorseGenetics.Species.GHOST_WINGED;
        boolean canFoldLegsFlying = hasSegmentedWings || isFairy || isGhost;

        this.wingInnerL.visible = hasSegmentedWings;
        this.wingMidL.visible = hasSegmentedWings;
        this.wingOuterL.visible = hasSegmentedWings;
        this.wingInnerR.visible = hasSegmentedWings;
        this.wingMidR.visible = hasSegmentedWings;
        this.wingOuterR.visible = hasSegmentedWings;

        if (hasSegmentedWings) {
        boolean flying = !entity.onGround();

        float wingRot = flying
                ? Mth.cos(ageInTicks * 0.3F + (float) Math.PI) * 1.2F
                : 60F / ModelAnimations.DEGREES_PER_RADIAN;

        this.wingInnerL.zRot = wingRot;
        this.wingMidL.zRot = wingRot;
        this.wingOuterL.zRot = wingRot;
        this.wingInnerR.zRot = -wingRot;
        this.wingMidR.zRot = -wingRot;
        this.wingOuterR.zRot = -wingRot;

        if (flying) {
                this.wingOuterL.yRot = -0.3228859F + wingRot / 2F;
                this.wingOuterR.yRot = 0.3228859F - wingRot / 2F;
        } else {
                this.wingOuterL.yRot = -90F / ModelAnimations.DEGREES_PER_RADIAN;
                this.wingOuterR.yRot = 90F / ModelAnimations.DEGREES_PER_RADIAN;
        }

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
        }

        if (isGhost || (canFoldLegsFlying && !entity.onGround())) {
                float upperFold = 15F / ModelAnimations.DEGREES_PER_RADIAN;
                float lowerFold = 45F / ModelAnimations.DEGREES_PER_RADIAN;
                setLegAngle(this.leg1Upper, this.leg1Lower, this.leg1Hoof, upperFold, lowerFold);
                setLegAngle(this.leg2Upper, this.leg2Lower, this.leg2Hoof, upperFold, lowerFold);
                setLegAngle(this.leg3Upper, this.leg3Lower, this.leg3Hoof, upperFold, lowerFold);
                setLegAngle(this.leg4Upper, this.leg4Lower, this.leg4Hoof, upperFold, lowerFold);
        }
    }

    private static void setLegAngle(ModelPart upper, ModelPart lower, ModelPart hoof, float upperAngle, float lowerAngle) {
        upper.xRot = upperAngle;
        lower.xRot = lowerAngle - upperAngle;
        hoof.xRot = 0F;
    }
}