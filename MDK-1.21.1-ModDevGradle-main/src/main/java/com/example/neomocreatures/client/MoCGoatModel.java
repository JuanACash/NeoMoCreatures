package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCGoatEntity;

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
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelGoat (Techne) to
 * HierarchicalModel. It's a single model — not two per-sex geometries —
 * where the original toggled part visibility by sex/age (udder for
 * females, progressively bigger horns + beard for mature males). Horn/head
 * family parts share Head's exact pivot in the original, so they're nested
 * under head here (rotation-follow is free via the hierarchy) instead of
 * manually copied every tick. Ear/tail/mouth swing counters are replaced
 * with steady ageInTicks-driven idle wiggles.
 */
public class MoCGoatModel extends HierarchicalModel<MoCGoatEntity> {

    private final ModelPart root;
    private final ModelPart legFrontRight;
    private final ModelPart legFrontLeft;
    private final ModelPart legRearLeft;
    private final ModelPart legRearRight;
    private final ModelPart tail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart earLeft;
    private final ModelPart earRight;
    private final ModelPart hornR1, hornR2, hornR3, hornR4, hornR5;
    private final ModelPart hornL1, hornL2, hornL3, hornL4, hornL5;
    private final ModelPart goatie;
    private final ModelPart mouth;
    private final ModelPart tongue;
    private final ModelPart tits;

    public MoCGoatModel(ModelPart root) {
        this.root = root;
        this.legFrontRight = root.getChild("leg_front_right");
        this.legFrontLeft = root.getChild("leg_front_left");
        this.legRearLeft = root.getChild("leg_rear_left");
        this.legRearRight = root.getChild("leg_rear_right");
        this.tail = root.getChild("tail");
        this.neck = root.getChild("neck");
        this.head = root.getChild("head");
        this.earLeft = head.getChild("ear_left");
        this.earRight = head.getChild("ear_right");
        this.hornR1 = head.getChild("horn_r1");
        this.hornR2 = head.getChild("horn_r2");
        this.hornR3 = head.getChild("horn_r3");
        this.hornR4 = head.getChild("horn_r4");
        this.hornR5 = head.getChild("horn_r5");
        this.hornL1 = head.getChild("horn_l1");
        this.hornL2 = head.getChild("horn_l2");
        this.hornL3 = head.getChild("horn_l3");
        this.hornL4 = head.getChild("horn_l4");
        this.hornL5 = head.getChild("horn_l5");
        this.goatie = head.getChild("goatie");
        this.mouth = head.getChild("mouth");
        this.tongue = head.getChild("tongue");
        this.tits = root.getChild("tits");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("leg_front_right",
                CubeListBuilder.create().texOffs(0, 23).addBox(-1F, 0F, -1F, 2, 7, 2),
                PartPose.offset(2F, 17F, -6F));
        root.addOrReplaceChild("leg_front_left",
                CubeListBuilder.create().texOffs(0, 23).addBox(-1F, 0F, -1F, 2, 7, 2),
                PartPose.offset(-2F, 17F, -6F));
        root.addOrReplaceChild("leg_rear_left",
                CubeListBuilder.create().texOffs(0, 23).addBox(-1F, 0F, -1F, 2, 7, 2),
                PartPose.offset(-2F, 17F, 6F));
        root.addOrReplaceChild("leg_rear_right",
                CubeListBuilder.create().texOffs(0, 23).addBox(-1F, 0F, -1F, 2, 7, 2),
                PartPose.offset(2F, 17F, 6F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(20, 8).addBox(-3F, -4F, -8F, 6, 8, 16),
                PartPose.offset(0F, 13F, 0F));

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(22, 8).addBox(-1.5F, -1F, 0F, 3, 2, 4),
                PartPose.offset(0F, 10F, 8F));

        root.addOrReplaceChild("tits",
                CubeListBuilder.create().texOffs(18, 0).addBox(-2.5F, 0F, -2F, 5, 1, 4),
                PartPose.offset(0F, 17F, 3F));

        PartDefinition neck = root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(18, 14).addBox(-1.5F, -2.0F, -5F, 3, 4, 6),
                PartPose.offsetAndRotation(0F, 11F, -8F, -24F / 57.29578F, 0F, 0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(52, 16).addBox(-1.5F, -2F, -2F, 3, 5, 3),
                PartPose.offset(0F, 8F, -12F));

        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(52, 10).addBox(-1.5F, -1F, -5F, 3, 3, 3),
                PartPose.ZERO);
        head.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(54, 0).addBox(-1F, 2F, -5F, 2, 1, 3),
                PartPose.ZERO);
        head.addOrReplaceChild("tongue",
                CubeListBuilder.create().texOffs(56, 5).addBox(-0.5F, 2F, -5F, 1, 0, 3),
                PartPose.ZERO);
        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(52, 8).addBox(1.5F, -2F, 0F, 2, 1, 1),
                PartPose.ZERO);
        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(52, 8).addBox(-3.5F, -2F, 0F, 2, 1, 1),
                PartPose.ZERO);
        head.addOrReplaceChild("goatie",
                CubeListBuilder.create().texOffs(52, 5).addBox(-0.5F, 3F, -4F, 1, 2, 1),
                PartPose.ZERO);

        // Horn growth stages 1-2: both sexes, once past a maturity threshold.
        head.addOrReplaceChild("horn_r1",
                CubeListBuilder.create().addBox(-1.5F, -3F, -0.7F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_l1",
                CubeListBuilder.create().addBox(0.5F, -3F, -0.7F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_r2",
                CubeListBuilder.create().addBox(-1.9F, -4F, -0.2F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_l2",
                CubeListBuilder.create().addBox(0.9F, -4F, -0.2F, 1, 1, 1), PartPose.ZERO);
        // Horn growth stages 3-5: males only, at higher maturity.
        head.addOrReplaceChild("horn_r3",
                CubeListBuilder.create().addBox(-2.1F, -4.8F, 0.5F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_l3",
                CubeListBuilder.create().addBox(1.2F, -4.9F, 0.5F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_r4",
                CubeListBuilder.create().addBox(-2.3F, -5.2F, 1.4F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_l4",
                CubeListBuilder.create().addBox(1.4F, -5.3F, 1.4F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_r5",
                CubeListBuilder.create().addBox(-2.6F, -4.9F, 2.0F, 1, 1, 1), PartPose.ZERO);
        head.addOrReplaceChild("horn_l5",
                CubeListBuilder.create().addBox(1.7F, -4.9F, 2.1F, 1, 1, 1), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCGoatEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        this.legFrontRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legFrontLeft.xRot = Mth.cos((limbSwing * 0.6662F) + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.legRearLeft.xRot = Mth.cos((limbSwing * 0.6662F) + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.legRearRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        // Wiki/original: pawing-the-ground warning stomp (front-right leg
        // only), overriding the regular walk swing while it plays.
        int legTicks = entity.getLegTicks();
        if (legTicks > 0) {
            float stomp;
            if (legTicks < 21) {
                stomp = -legTicks;
            } else if (legTicks < 70) {
                stomp = legTicks - 40;
            } else {
                stomp = -legTicks + 100;
            }
            this.legFrontRight.xRot = stomp / 57.29578F;
        }

        float clampedYaw = Mth.clamp(netHeadYaw, -20F, 20F);
        this.head.yRot = clampedYaw * ((float) Math.PI / 180F);

        boolean angry = entity.isAngry();
        int attackTicks = entity.getAttackTicks();
        // Original: baseAngle = 30° + headPitch, the resting forward/down tilt.
        float restingHeadXRot = (30F / 57.29578F) + (headPitch * ((float) Math.PI / 180F));
        // head's pivot is FIXED (see the "disconnected head" fix earlier —
        // it's an independent part, not a child of neck, so its position
        // never follows neck's rotation). Rotating it further and further
        // just spins that fixed-position box past its natural range, which
        // reads as arched/twisted instead of "lower" — like the horse
        // grazing dip you mentioned, actually lowering it toward the ground
        // needs its POSITION dragged down and forward, not just its angle.
        // These x/y/z resets have to happen in every branch, not just the
        // ram one — ModelPart fields persist frame to frame, so without an
        // explicit reset the head would stay shifted forever once a ram ends.
        if (attackTicks > 0) {
            // Same trick now applied to neck too — it was still only
            // rotating in place around its fixed pivot (0,11,-8), so it
            // never actually came down to meet the head. Both head and
            // neck now drag their POSITION down/forward together, with
            // rotation on top for the visible tilt.
            final float RAM_HEAD_XROT_DEG = 85F;     // more pronounced tilt than the previous 45°
            final float RAM_NECK_XROT_DEG = 30F;     // inverted from resting's -30° — same pivot, just flipped to point down instead of up
            final float RAM_HEAD_DOWN_SHIFT = 5F;    // added to head's baseline y (8)
            final float RAM_HEAD_FORWARD_SHIFT = 2F; // added to head's baseline |z| (12)
            this.head.xRot = RAM_HEAD_XROT_DEG / 57.29578F;
            this.neck.xRot = RAM_NECK_XROT_DEG / 57.29578F;
            this.head.x = 0F;
            this.head.y = 8F + RAM_HEAD_DOWN_SHIFT;
            this.head.z = -12F - RAM_HEAD_FORWARD_SHIFT;
            // neck stays anchored at its normal resting position (11, -8) —
            // only xRot changes. It's the same pivot as always, just flipped
            // to point down instead of up.
            this.neck.x = 0F;
            this.neck.y = 11F;
            this.neck.z = -8F;
        } else if (angry) {
            this.head.xRot = 30F / 57.29578F;
            this.neck.xRot = -40F / 57.29578F;
            this.head.x = 0F;
            this.head.y = 8F;
            this.head.z = -12F;
            this.neck.x = 0F;
            this.neck.y = 11F;
            this.neck.z = -8F;
        } else {
            this.head.xRot = restingHeadXRot;
            this.neck.xRot = -30F / 57.29578F;
            this.head.x = 0F;
            this.head.y = 8F;
            this.head.z = -12F;
            this.neck.x = 0F;
            this.neck.y = 11F;
            this.neck.z = -8F;
        }

        // Idle ear twitch / tail swing, steady per-instance timing instead of
        // the original's manual tick counters — same trick used for the
        // Komodo dragon's tail flick.
        long seed = entity.getId();
        float earWiggle = Mth.sin((ageInTicks + seed * 13L) * 0.05F) * 0.15F;
        this.earLeft.xRot = earWiggle;
        this.earRight.xRot = earWiggle;
        this.tail.xRot = Mth.sin((ageInTicks + seed * 7L) * 0.08F) * 0.5F;
        this.mouth.xRot = Mth.sin(ageInTicks * 0.3F) * 0.05F;

        boolean male = entity.isMale() && !entity.isBaby();
        boolean female = !entity.isMale() && !entity.isBaby();
        this.tits.visible = female;

        float growth = entity.getGrowthFraction();
        boolean matureEnough = !entity.isBaby();
        this.hornR1.visible = matureEnough && growth > 0.7F;
        this.hornL1.visible = matureEnough && growth > 0.7F;
        this.hornR2.visible = matureEnough && growth > 0.8F;
        this.hornL2.visible = matureEnough && growth > 0.8F;
        this.hornR3.visible = male && growth > 0.8F;
        this.hornL3.visible = male && growth > 0.8F;
        this.hornR4.visible = male && growth > 0.85F;
        this.hornL4.visible = male && growth > 0.85F;
        this.hornR5.visible = male && growth > 0.9F;
        this.hornL5.visible = male && growth > 0.9F;
        this.goatie.visible = male && growth > 0.9F;

        this.tongue.xRot = entity.isBleating() ? (-5F / 57.29578F) : 0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}