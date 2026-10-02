package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCSnakeEntity;
import com.example.neomocreatures.entity.snake.SnakeVariant;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of {@code drzhark.mocreatures.client.model.MoCModelSnake}, decompiled
 * directly from the official 1.20.1 release jar. The original does NOT bake its
 * per-frame pose into each ModelPart's x/y/z fields — it manually pushes/pops the
 * PoseStack once per body segment inside a custom render loop, and every offset it
 * applies there is IN ADDITION to each part's own baked pivot (23 for y, the
 * segment's spine position for z). That loop is ported verbatim in
 * {@link #renderToBuffer}; {@link #setupAnim} only stores the rotation fields and
 * reads the entity's state, exactly like the original's setupAnim/setLivingAnimations.
 */
public class MoCSnakeModel extends HierarchicalModel<MoCSnakeEntity> {

    private static final int BODY_SEGMENTS = 40;
    private static final float SEGMENT_SPACING = -1.6F;
    private static final float RADIAN = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    private final ModelPart[] body = new ModelPart[BODY_SEGMENTS];
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart lowerNose;
    private final ModelPart toothRight;
    private final ModelPart toothLeft;
    private final ModelPart tongue0;
    private final ModelPart tongue;
    private final ModelPart tongue1;
    private final ModelPart tail;
    private final ModelPart wing1L;
    private final ModelPart wing1R;
    private final ModelPart wing2L;
    private final ModelPart wing2R;
    private final ModelPart wing3L;
    private final ModelPart wing3R;
    private final ModelPart wing4L;
    private final ModelPart wing4R;
    private final ModelPart wing5L;
    private final ModelPart wing5R;

    // Fields setupAnim stores and renderToBuffer reads, exactly like the original
    // split between setLivingAnimations() and render().
    private SnakeVariant variant = SnakeVariant.GREEN_DARK;
    private float tongueOff;
    private float mouthOff;
    private float rattleOff;
    private boolean climbing;
    private boolean isResting;
    private int movInt;
    private float bodyswing;
    private boolean nearPlayer;
    private boolean picked;
    private float limbSwing;

    public MoCSnakeModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < BODY_SEGMENTS; i++) {
            this.body[i] = root.getChild(bodyPartName(i));
        }
        this.head = root.getChild("head");
        this.nose = root.getChild("nose");
        this.lowerNose = root.getChild("lower_nose");
        this.toothRight = root.getChild("tooth_right");
        this.toothLeft = root.getChild("tooth_left");
        this.tongue0 = root.getChild("tongue0");
        this.tongue = root.getChild("tongue");
        this.tongue1 = root.getChild("tongue1");
        this.tail = root.getChild("tail");
        this.wing1L = root.getChild("wing_1_l");
        this.wing1R = root.getChild("wing_1_r");
        this.wing2L = root.getChild("wing_2_l");
        this.wing2R = root.getChild("wing_2_r");
        this.wing3L = root.getChild("wing_3_l");
        this.wing3R = root.getChild("wing_3_r");
        this.wing4L = root.getChild("wing_4_l");
        this.wing4R = root.getChild("wing_4_r");
        this.wing5L = root.getChild("wing_5_l");
        this.wing5R = root.getChild("wing_5_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        for (int i = 0; i < BODY_SEGMENTS; i++) {
            float z = ((BODY_SEGMENTS / 2F) - i) * SEGMENT_SPACING;
            int texV = (i % 2 == 0) ? 0 : 4;
            root.addOrReplaceChild(bodyPartName(i),
                    CubeListBuilder.create().texOffs(8, texV)
                            .addBox(-1F, -0.5F, 0F, 2, 2, 2, new CubeDeformation(taperFor(i))),
                    PartPose.offset(0F, 23F, z));
        }

        // Original reuses its loop's leftover "flength" variable: the tail
        // mesh is created first (still holding i = 39's value), THEN
        // flength is reassigned to the "i = 0" end for the head/mouth parts.
        float tailZ = ((BODY_SEGMENTS / 2F) - (BODY_SEGMENTS - 1)) * SEGMENT_SPACING;
        float headZ = (BODY_SEGMENTS / 2F) * SEGMENT_SPACING;

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0.5F, -1.0F, 1, 1, 5),
                PartPose.offset(0F, 23F, tailZ));

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, -0.5F, -2F, 2, 2, 2),
                PartPose.offset(0F, 23F, headZ));
        root.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(16, 0).addBox(-0.5F, -0.3F, -4F, 1, 1, 2),
                PartPose.offset(0F, 23F, headZ));
        root.addOrReplaceChild("lower_nose",
                CubeListBuilder.create().texOffs(22, 0).addBox(-0.5F, 0.3F, -4F, 1, 1, 2),
                PartPose.offset(0F, 23F, headZ));
        // Original literally uses zero-thickness Techne planes here; the
        // shipped mod renders them fine, so we match it exactly instead of
        // padding them out.
        root.addOrReplaceChild("tooth_right",
                CubeListBuilder.create().texOffs(46, 0).addBox(-0.4F, 0.3F, -3.8F, 0F, 1F, 1F),
                PartPose.offset(0F, 23F, headZ));
        root.addOrReplaceChild("tooth_left",
                CubeListBuilder.create().texOffs(44, 0).addBox(0.4F, 0.3F, -3.8F, 0F, 1F, 1F),
                PartPose.offset(0F, 23F, headZ));
        // Three separate tongue poses (retracted / mid / extended), swapped
        // in renderToBuffer — not one part animated dynamically.
        root.addOrReplaceChild("tongue0",
                CubeListBuilder.create().texOffs(28, 0).addBox(-0.5F, 0.25F, -4F, 1F, 0F, 3F),
                PartPose.offset(0F, 23F, headZ));
        root.addOrReplaceChild("tongue",
                CubeListBuilder.create().texOffs(28, 0).addBox(-0.5F, 0.5F, -6F, 1F, 0F, 3F),
                PartPose.offset(0F, 23F, headZ));
        root.addOrReplaceChild("tongue1",
                CubeListBuilder.create().texOffs(28, 0).addBox(-0.5F, 0.5F, -5F, 1F, 0F, 3F),
                PartPose.offset(0F, 23F, headZ));

        // Cobra hood — only visible for the COBRA variant while nearPlayer
        // (see renderToBuffer). Right-side pieces are UV-mirrored, which the
        // previous version of this file was missing entirely.
        float wing1Z = ((BODY_SEGMENTS / 2F) - 1) * SEGMENT_SPACING;
        float wing2Z = ((BODY_SEGMENTS / 2F) - 2) * SEGMENT_SPACING;
        float wing3Z = ((BODY_SEGMENTS / 2F) - 3) * SEGMENT_SPACING;
        float wing4Z = ((BODY_SEGMENTS / 2F) - 4) * SEGMENT_SPACING;
        float wing5Z = ((BODY_SEGMENTS / 2F) - 5) * SEGMENT_SPACING;

        root.addOrReplaceChild("wing_1_l",
                CubeListBuilder.create().texOffs(8, 4).addBox(0F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing1Z));
        root.addOrReplaceChild("wing_1_r",
                CubeListBuilder.create().texOffs(8, 4).mirror().addBox(-2F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing1Z));
        root.addOrReplaceChild("wing_2_l",
                CubeListBuilder.create().texOffs(8, 4).addBox(0.5F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing2Z));
        root.addOrReplaceChild("wing_2_r",
                CubeListBuilder.create().texOffs(8, 4).mirror().addBox(-2.5F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing2Z));
        root.addOrReplaceChild("wing_3_l",
                CubeListBuilder.create().texOffs(16, 4).addBox(1F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing3Z));
        root.addOrReplaceChild("wing_3_r",
                CubeListBuilder.create().texOffs(16, 4).mirror().addBox(-3F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing3Z));
        root.addOrReplaceChild("wing_4_l",
                CubeListBuilder.create().texOffs(16, 8).addBox(0.5F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing4Z));
        root.addOrReplaceChild("wing_4_r",
                CubeListBuilder.create().texOffs(16, 8).mirror().addBox(-2.5F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing4Z));
        root.addOrReplaceChild("wing_5_l",
                CubeListBuilder.create().texOffs(16, 8).addBox(0F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing5Z));
        root.addOrReplaceChild("wing_5_r",
                CubeListBuilder.create().texOffs(16, 8).mirror().addBox(-2F, -0.5F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, wing5Z));

        return LayerDefinition.create(mesh, 64, 32);
    }

    /** Original's fixed taper factors, keyed by how far along the body a segment sits. */
    private static float taperFor(int i) {
        float fraction = (i + 1F) / BODY_SEGMENTS;
        if (fraction < 0.125F) return -0.20F;
        if (fraction < 0.25F) return -0.15F;
        if (fraction < 0.5F) return 0.0F;
        if (fraction < 0.75F) return 0.0F;
        if (fraction < 0.875F) return -0.15F;
        return -0.2F;
    }

    private static String bodyPartName(int i) {
        return "body_" + i;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    /**
     * Exact port of the original's setLivingAnimations()+setupAnim() split:
     * stores every piece of state renderToBuffer needs, and computes the
     * rotation-only fields (position offsets are computed later, in the
     * render loop itself, exactly like the original).
     */
    @Override
    public void setupAnim(MoCSnakeEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        this.variant = entity.getVariant();
        this.tongueOff = entity.getTongueOff();
        this.mouthOff = entity.getMouthOff();
        this.rattleOff = entity.getRattleOff();
        this.climbing = entity.isClimbing();
        this.isResting = entity.isResting();
        this.movInt = entity.getMovInt();
        this.bodyswing = entity.bodyswing;
        this.nearPlayer = entity.getNearPlayer();
        this.picked = entity.isPickedUp();
        this.limbSwing = limbSwing;

        float rAX = headPitch / RADIAN;
        float rAY = netHeadYaw / RADIAN;
        this.head.xRot = rAX;
        this.head.yRot = rAY;
        this.body[0].xRot = rAX * 0.95F;
        this.body[1].xRot = rAX * 0.9F;
        this.body[2].xRot = rAX * 0.85F;
        this.body[3].xRot = rAX * 0.8F;
        this.body[4].xRot = rAX * 0.75F;
        this.body[0].yRot = rAY * 0.85F;
        this.body[1].yRot = rAY * 0.65F;
        this.body[2].yRot = rAY * 0.45F;
        this.body[3].yRot = rAY * 0.25F;
        this.body[4].yRot = rAY * 0.1F;

        float f8 = Mth.cos(this.tongueOff * 10F) / 40F;
        this.nose.xRot = this.head.xRot - this.mouthOff;
        this.lowerNose.xRot = this.head.xRot + this.mouthOff;
        this.tongue1.xRot = this.head.xRot + f8;
        this.tongue.xRot = this.head.xRot + f8;
        this.tongue0.xRot = this.lowerNose.xRot;
        this.toothRight.xRot = this.head.xRot - this.mouthOff;
        this.toothLeft.xRot = this.head.xRot - this.mouthOff;
        this.nose.yRot = this.head.yRot;
        this.lowerNose.yRot = this.head.yRot;
        this.tongue0.yRot = this.head.yRot;
        this.tongue.yRot = this.head.yRot;
        this.tongue1.yRot = this.head.yRot;
        this.toothRight.yRot = this.head.yRot;
        this.toothLeft.yRot = this.head.yRot;

        if (this.variant == SnakeVariant.COBRA) {
            this.wing1L.xRot = this.body[1].xRot; this.wing1L.yRot = this.body[1].yRot;
            this.wing1R.xRot = this.body[1].xRot; this.wing1R.yRot = this.body[1].yRot;
            this.wing2L.xRot = this.body[2].xRot; this.wing2L.yRot = this.body[2].yRot;
            this.wing2R.xRot = this.body[2].xRot; this.wing2R.yRot = this.body[2].yRot;
            this.wing3L.xRot = this.body[3].xRot; this.wing3L.yRot = this.body[3].yRot;
            this.wing3R.xRot = this.body[3].xRot; this.wing3R.yRot = this.body[3].yRot;
            this.wing4L.xRot = this.body[4].xRot; this.wing4L.yRot = this.body[4].yRot;
            this.wing4R.xRot = this.body[4].xRot; this.wing4R.yRot = this.body[4].yRot;
            this.wing5L.xRot = this.body[4].xRot; this.wing5L.yRot = this.body[4].yRot;
            this.wing5R.xRot = this.body[4].xRot; this.wing5R.yRot = this.body[4].yRot;
        }
        if (this.variant == SnakeVariant.RATTLE) {
            this.tail.xRot = (this.nearPlayer || this.rattleOff != 0F)
                    ? ((Mth.cos(netHeadYaw * 10F) * 20F) + 90F) / RADIAN
                    : 0F;
        }
    }

    /**
     * Exact port of the original's render(): pushes/pops the PoseStack once
     * per body segment, translating IN ADDITION to each part's own baked
     * pivot (never overwriting it) before rendering that one segment.
     */
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        float w = 1.5F;
        float t = this.limbSwing / 2F;
        for (int i = 0; i < BODY_SEGMENTS; i++) {
            float yOff = 0F;
            float sideperf = 1F;
            poseStack.pushPose();

            if (!this.isResting) {
                if (this.climbing && i < 20) {
                    yOff = (i - 20F) * 0.08F;
                    poseStack.translate(0F, yOff / 3.0F, -yOff * 1.2F);
                } else if (this.nearPlayer || this.picked) {
                    if (i < 13) {
                        yOff = (i - 13.333333F) * 0.09F;
                        float zOff = (i - 13.333333F) * 0.065F;
                        poseStack.translate(0F, yOff / 1.5F, -zOff * this.bodyswing);
                    }
                    if (i < 6) {
                        sideperf = 0F;
                    } else {
                        sideperf = (i - 7) / 13.333333F;
                        if (sideperf > 1F) sideperf = 1F;
                    }
                }
            }
            if (this.variant == SnakeVariant.RATTLE && this.nearPlayer && i > 33 && !this.picked) {
                yOff = 0.55F + (i - 40) * 0.08F;
                poseStack.translate(0F, -yOff / 1.5F, 0F);
            }
            if (this.picked && i > 20) {
                yOff = (i - 20F) * 0.08F;
                poseStack.translate(0F, yOff / 1.5F, -yOff);
            }

            float sidef = 0.5F * Mth.sin(w * t - 0.3F * i) - (this.movInt / 20F) * Mth.sin(0.8F * t - 0.2F * i);
            sidef *= sideperf;
            poseStack.translate(sidef, 0F, 0F);

            this.body[i].render(poseStack, buffer, packedLight, packedOverlay, color);

            if (i == 0) {
                this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.nose.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.lowerNose.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.toothRight.render(poseStack, buffer, packedLight, packedOverlay, color);
                this.toothLeft.render(poseStack, buffer, packedLight, packedOverlay, color);
                if (this.tongueOff != 0F) {
                    if (this.mouthOff != 0F || this.tongueOff < 2F || this.tongueOff > 7F) {
                        this.tongue1.render(poseStack, buffer, packedLight, packedOverlay, color);
                    } else {
                        this.tongue.render(poseStack, buffer, packedLight, packedOverlay, color);
                    }
                } else {
                    this.tongue0.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
            }
            if (this.variant == SnakeVariant.COBRA && this.nearPlayer) {
                if (i == 1) {
                    this.wing1L.render(poseStack, buffer, packedLight, packedOverlay, color);
                    this.wing1R.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
                if (i == 2) {
                    this.wing2L.render(poseStack, buffer, packedLight, packedOverlay, color);
                    this.wing2R.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
                if (i == 3) {
                    this.wing3L.render(poseStack, buffer, packedLight, packedOverlay, color);
                    this.wing3R.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
                if (i == 4) {
                    this.wing4L.render(poseStack, buffer, packedLight, packedOverlay, color);
                    this.wing4R.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
                if (i == 5) {
                    this.wing5L.render(poseStack, buffer, packedLight, packedOverlay, color);
                    this.wing5R.render(poseStack, buffer, packedLight, packedOverlay, color);
                }
            }
            if (i == 39 && this.variant == SnakeVariant.RATTLE) {
                this.tail.render(poseStack, buffer, packedLight, packedOverlay, color);
            }
            poseStack.popPose();
        }
    }
}