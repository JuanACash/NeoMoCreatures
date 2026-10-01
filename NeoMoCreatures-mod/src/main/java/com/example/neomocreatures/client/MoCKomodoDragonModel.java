package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCKomodoDragonEntity;
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
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelKomodo (Techne) to
 * HierarchicalModel. Texture canvas declared as 64x64 to match the
 * original model's UV layout — the actual PNG on disk is a uniform 2x
 * upscale (128x128) of that same layout, so the fractional UV coordinates
 * still land correctly.
 */
public class MoCKomodoDragonModel extends HierarchicalModel<MoCKomodoDragonEntity> {

    private static final float R = 57.29578F;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart nose;
    private final ModelPart mouth;
    private final ModelPart tongue;
    private final ModelPart saddleA;
    private final ModelPart saddleB;
    private final ModelPart saddleC;
    private final ModelPart chest;
    private final ModelPart abdomen;
    private final ModelPart tail;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart legFrontLeft;
    private final ModelPart legFrontLeft1;
    private final ModelPart legFrontLeft2;
    private final ModelPart legBackLeft;
    private final ModelPart legBackLeft1;
    private final ModelPart legBackLeft2;
    private final ModelPart legFrontRight;
    private final ModelPart legFrontRight1;
    private final ModelPart legFrontRight2;
    private final ModelPart legBackRight;
    private final ModelPart legBackRight1;
    private final ModelPart legBackRight2;

    public MoCKomodoDragonModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = this.head.getChild("neck");
        this.nose = this.neck.getChild("nose");
        this.mouth = this.neck.getChild("mouth");
        this.tongue = this.mouth.getChild("tongue");
        this.saddleA = root.getChild("saddle_a");
        this.saddleB = root.getChild("saddle_b");
        this.saddleC = root.getChild("saddle_c");
        this.chest = root.getChild("chest");
        this.abdomen = root.getChild("abdomen");
        this.tail = root.getChild("tail");
        this.tail1 = this.tail.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.legFrontLeft = root.getChild("leg_front_left");
        this.legFrontLeft1 = this.legFrontLeft.getChild("leg_front_left_1");
        this.legFrontLeft2 = this.legFrontLeft1.getChild("leg_front_left_2");
        this.legBackLeft = root.getChild("leg_back_left");
        this.legBackLeft1 = this.legBackLeft.getChild("leg_back_left_1");
        this.legBackLeft2 = this.legBackLeft1.getChild("leg_back_left_2");
        this.legFrontRight = root.getChild("leg_front_right");
        this.legFrontRight1 = this.legFrontRight.getChild("leg_front_right_1");
        this.legFrontRight2 = this.legFrontRight1.getChild("leg_front_right_2");
        this.legBackRight = root.getChild("leg_back_right");
        this.legBackRight1 = this.legBackRight.getChild("leg_back_right_1");
        this.legBackRight2 = this.legBackRight1.getChild("leg_back_right_2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // --- Head / neck / mouth / tongue -----------------------------------
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create(),
                PartPose.offset(0F, 13F, -8F));

        PartDefinition neck = head.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(22, 34).addBox(-2F, 0F, -6F, 4, 5, 6),
                PartPose.offset(0F, 0F, 0F));

        neck.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(24, 45).addBox(-1.5F, -1F, -6.5F, 3, 2, 6),
                PartPose.offset(0F, 1F, -5F));

        PartDefinition mouth = neck.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(0, 12).addBox(-1F, -0.3F, -5F, 2, 1, 6),
                PartPose.offset(0F, 3F, -5.8F));

        mouth.addOrReplaceChild("tongue",
                CubeListBuilder.create().texOffs(48, 44).addBox(-1.5F, 0F, -5F, 3, 0, 5),
                PartPose.offset(0F, -0.4F, -4.7F));

        // --- Chest / abdomen (independent, sit directly under root) --------
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(36, 2).addBox(-3F, 0F, -8F, 6, 6, 7),
                PartPose.offset(0F, 13F, 0F));

        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(36, 49).addBox(-3F, 0F, -1F, 6, 7, 8),
                PartPose.offset(0F, 13F, 0F));

        // 1:1 port of MoCModelKomodo's SaddleA/SaddleB/SaddleC — same UV offsets
        // on the same 64x64 canvas, so the existing texture art lines up as-is.
        root.addOrReplaceChild("saddle_a",
                CubeListBuilder.create().texOffs(36, 28).mirror().addBox(-2.5F, 0.5F, -4F, 5, 1, 8),
                PartPose.offset(0F, 12F, 0F));
        root.addOrReplaceChild("saddle_b",
                CubeListBuilder.create().texOffs(54, 37).mirror().addBox(-1.5F, 0F, -4F, 3, 1, 2),
                PartPose.offset(0F, 12F, 0F));
        root.addOrReplaceChild("saddle_c",
                CubeListBuilder.create().texOffs(36, 37).mirror().addBox(-2.5F, 0F, 2F, 5, 1, 2),
                PartPose.offset(0F, 12F, 0F));

        // --- Tail (4-segment chain) ------------------------------------------
        PartDefinition tail = root.addOrReplaceChild("tail",
                CubeListBuilder.create(),
                PartPose.offset(0F, 13F, 7F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(0, 21).addBox(-2F, 0F, 0F, 4, 5, 8),
                PartPose.offset(0F, 0F, 0F));
        PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, 0F, 0F, 3, 4, 8),
                PartPose.offset(0F, 0.1F, 7.7F));
        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, 0F, 2, 3, 8),
                PartPose.offset(0F, 0.1F, 7.3F));
        tail3.addOrReplaceChild("tail4",
                CubeListBuilder.create().texOffs(24, 21).addBox(-0.5F, 0F, 0F, 1, 2, 8),
                PartPose.offset(0F, 0.1F, 7F));

        // --- Legs (each: pivot group -> upper -> lower, 3rd segment baked with
        // its original +/-10 degree foot splay, never touched in setupAnim) ---
        buildLeg(root, "leg_front_left", 2F, 17F, -7F, 0, 0, 22, 0, 16, 58, -10F / R);
        buildLeg(root, "leg_back_left", 2F, 17F, 6F, 0, 0, 22, 0, 16, 58, -10F / R);
        buildLeg(root, "leg_front_right", -2F, 17F, -7F, 0, 6, 22, 7, 0, 58, 10F / R);
        buildLeg(root, "leg_back_right", -2F, 17F, 6F, 0, 6, 22, 7, 0, 58, 10F / R);

        return LayerDefinition.create(mesh, 64, 64);
    }

    /**
     * Builds one leg's 3-part chain. Front and back legs on the same side
     * reuse identical texture offsets, matching the original Techne model.
     */
    private static void buildLeg(PartDefinition root, String name, float x, float y, float z,
                                  int upperU, int upperV, int lowerU, int lowerV,
                                  int footU, int footV, float footYRot) {
        boolean mirrored = footYRot < 0F;
        CubeListBuilder upperBox = mirrored
                ? CubeListBuilder.create().texOffs(upperU, upperV).addBox(0F, -1F, -1.5F, 4, 3, 3)
                : CubeListBuilder.create().texOffs(upperU, upperV).addBox(-4F, -1F, -1.5F, 4, 3, 3);

        PartDefinition pivot = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offset(x, y, z));
        PartDefinition upper = pivot.addOrReplaceChild(name + "_1", upperBox, PartPose.offset(0F, 0F, 0F));
        PartDefinition lower = upper.addOrReplaceChild(name + "_2",
                CubeListBuilder.create().texOffs(lowerU, lowerV).addBox(-1.5F, 0F, -1.5F, 3, 4, 3),
                PartPose.offset(mirrored ? 3F : -3F, 0.5F, 0F));
        lower.addOrReplaceChild(name + "_3",
                CubeListBuilder.create().texOffs(footU, footV).addBox(-1.5F, 0F, -3.5F, 3, 1, 5),
                PartPose.offsetAndRotation(0F, 4F, 0F, 0F, footYRot, 0F));
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCKomodoDragonEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        boolean sitting = entity.isInSittingPose();
        boolean swimming = entity.isSwimmingDeep();
        boolean saddled = entity.isSaddled();
        this.saddleA.visible = saddled;
        this.saddleB.visible = saddled;
        this.saddleC.visible = saddled;
        int mouthTicks = entity.getMouthTicks();
        boolean mouthOpen = mouthTicks != 0;

        // Per-instance idle flourishes (tail flick / tongue flick), driven
        // purely client-side off ageInTicks + entity id so dragons standing
        // next to each other don't animate in lockstep. No server sync
        // needed since these are cosmetic only.
        long seed = entity.getId();
        boolean tailFlicking = (ageInTicks + seed * 17L) % 240F < 40F;
        boolean tongueFlicking = !mouthOpen && (ageInTicks + seed * 31L) % 180F < 20F;

        // Ridden movement holds the "accelerator" fully down as long as the
        // player presses forward, so limbSwingAmount (computed purely from
        // distance moved that tick) sits near its 1.0 ceiling almost
        // constantly — unlike unridden AI walking, which eases in/out and
        // rarely sustains that peak. That's the actual cause of the front
        // legs swinging far enough to poke through the jaw while mounted;
        // it has nothing to do with the numeric movement speed. Capping the
        // amount used for the leg swing (only while there's a rider) fixes
        // it without changing how the unridden walk looks at all.
        float legSwingAmount = entity.isVehicle() ? Math.min(limbSwingAmount, 0.6F) : limbSwingAmount;

        float tailXRot = Mth.cos(limbSwing * 0.4F) * 0.2F * limbSwingAmount;
        float leftLegXRot = Mth.cos(limbSwing * 1.2F) * 1.2F * legSwingAmount;
        float rightLegXRot = Mth.cos((limbSwing * 1.2F) + (float) Math.PI) * 1.2F * legSwingAmount;
        float clampedYaw = Mth.clamp(netHeadYaw, -60F, 60F);

        float bodyLift = 0F;
        if (swimming) {
            bodyLift = 4F;
            this.tail1.xRot = -tailXRot;
            // Explicitly zero xRot on every leg segment here — it's the field
            // the walking branch below uses for its leg-swing oscillation, and
            // if a dragon walks into water this branch is the only thing that
            // stops that stale oscillation from leaking into the swim pose.
            this.legFrontLeft1.xRot = 0F; this.legFrontLeft2.xRot = 0F;
            this.legFrontLeft1.zRot = 0F; this.legFrontLeft2.zRot = -65F / R; this.legFrontLeft1.yRot = -80F / R;
            this.legBackLeft1.xRot = 0F; this.legBackLeft2.xRot = 0F;
            this.legBackLeft1.zRot = 0F; this.legBackLeft2.zRot = -65F / R; this.legBackLeft1.yRot = -80F / R;
            this.legFrontRight1.xRot = 0F; this.legFrontRight2.xRot = 0F;
            this.legFrontRight1.zRot = 0F; this.legFrontRight2.zRot = 65F / R; this.legFrontRight1.yRot = 80F / R;
            this.legBackRight1.xRot = 0F; this.legBackRight2.xRot = 0F;
            this.legBackRight1.zRot = 0F; this.legBackRight2.zRot = 65F / R; this.legBackRight1.yRot = 80F / R;
        } else if (sitting) {
            bodyLift = 4F;
            this.tail1.xRot = (-5F / R) - tailXRot;
            this.legFrontLeft1.zRot = -30F / R; this.legFrontLeft2.zRot = 0F; this.legFrontLeft1.yRot = 0F;
            this.legBackLeft1.zRot = 0F; this.legBackLeft2.zRot = -65F / R; this.legBackLeft1.yRot = -40F / R;
            this.legFrontRight1.zRot = 30F / R; this.legFrontRight2.zRot = 0F; this.legFrontRight1.yRot = 0F;
            this.legBackRight1.zRot = 0F; this.legBackRight2.zRot = 65F / R; this.legBackRight1.yRot = 40F / R;
        } else {
            this.tail1.xRot = (-15F / R) - tailXRot;
            this.legFrontLeft1.zRot = 30F / R; this.legFrontLeft2.zRot = -30F / R;
            this.legFrontLeft1.yRot = leftLegXRot; this.legFrontLeft2.xRot = -leftLegXRot;
            this.legBackLeft1.zRot = 30F / R; this.legBackLeft2.zRot = -30F / R;
            this.legBackLeft1.yRot = rightLegXRot; this.legBackLeft2.xRot = -rightLegXRot;
            this.legFrontRight1.zRot = -30F / R; this.legFrontRight2.zRot = 30F / R;
            this.legFrontRight1.yRot = -rightLegXRot; this.legFrontRight2.xRot = -rightLegXRot;
            this.legBackRight1.zRot = -30F / R; this.legBackRight2.zRot = 30F / R;
            this.legBackRight1.yRot = -leftLegXRot; this.legBackRight2.xRot = -leftLegXRot;
        }
        adjustBodyLift(bodyLift);

        float tongueZ = 0.3F;
        float tongueXRot = 0F;
        if (tongueFlicking) {
            tongueXRot = Mth.cos(ageInTicks * 3F) / 10F;
            tongueZ = -4.7F;
        }
        float mouthOpenRot = 0F;
        if (mouthOpen) {
            mouthOpenRot = 35F / R;
            tongueZ = -0.8F;
        }
        this.tongue.z = tongueZ;
        this.tongue.xRot = tongueXRot;

        this.neck.xRot = 11F / R + (headPitch * 0.33F / R);
        this.nose.xRot = 10.6F / R + (headPitch * 0.66F / R);
        this.mouth.xRot = mouthOpenRot + (-3F / R) + (headPitch * 0.66F / R);
        this.neck.yRot = clampedYaw * 0.33F / R;
        this.nose.yRot = clampedYaw * 0.66F / R;
        this.mouth.yRot = clampedYaw * 0.66F / R;

        this.tail2.xRot = (-17F / R) + tailXRot;
        this.tail3.xRot = (13F / R) + tailXRot;
        this.tail4.xRot = (11F / R) + tailXRot;

        // Swimming must ALWAYS wag the tail regardless of limbSwing (which is
        // unreliable while ridden) or the idle tailFlicking roll — the
        // side-to-side motion IS the swim stroke that sells self-propulsion.
        float t;
        if (swimming) {
            t = ageInTicks / 3F;
        } else if (tailFlicking) {
            t = ageInTicks / 4F;
        } else {
            t = limbSwing / 2F;
        }
        // A slightly wider stroke while swimming makes the propulsion read
        // more clearly than the subtler idle-flick amplitude used on land.
        float amplitude = swimming ? 0.5F : 0.35F;
        float angularSpeed = 0.6F;
        float phaseStep = 0.6F;
        this.tail1.yRot = amplitude * Mth.sin(angularSpeed * t);
        this.tail2.yRot = amplitude * Mth.sin(angularSpeed * t - phaseStep);
        this.tail3.yRot = amplitude * Mth.sin(angularSpeed * t - phaseStep * 2F);
        this.tail4.yRot = amplitude * Mth.sin(angularSpeed * t - phaseStep * 3F);
    }

    /** Shifts the whole body up while swimming/sitting, matching the original AdjustY(). */
    private void adjustBodyLift(float lift) {
        this.tail.y = lift + 13F;
        this.head.y = lift + 13F;
        this.chest.y = lift + 13F;
        this.legFrontLeft.y = lift + 17F;
        this.legBackLeft.y = lift + 17F;
        this.legFrontRight.y = lift + 17F;
        this.legBackRight.y = lift + 17F;
        this.abdomen.y = lift + 13F;
        this.saddleA.y = lift + 12F;
        this.saddleB.y = lift + 12F;
        this.saddleC.y = lift + 12F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}