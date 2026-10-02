package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCCrabEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelCrab}: a shell with two eye stalks, two
 * jointed pincer arms and eight jointed legs.
 * <p>
 * The original raises the claws only while actively fleeing (real-time movement speed above a
 * threshold, which our animation callback has no direct access to); this uses limb swing amount as
 * the closest available stand-in, so the claws lift during any brisk movement, not only right after
 * being hurt. Adjust {@link #CLAW_RAISE_THRESHOLD} if that reads as raised too often or too rarely.
 */
public class MoCCrabModel extends HierarchicalModel<MoCCrabEntity> {

    private static final float CLAW_RAISE_THRESHOLD = 0.3F;
    private static final float CLAW_RAISE_ANGLE = -Mth.HALF_PI;

    private final ModelPart root;
    private final ModelPart rightArmA;
    private final ModelPart rightArmB;
    private final ModelPart leftArmA;
    private final ModelPart leftArmB;
    private final ModelPart rightLeg1A;
    private final ModelPart rightLeg1B;
    private final ModelPart rightLeg2A;
    private final ModelPart rightLeg2B;
    private final ModelPart rightLeg3A;
    private final ModelPart rightLeg3B;
    private final ModelPart rightLeg4A;
    private final ModelPart leftLeg1A;
    private final ModelPart leftLeg1B;
    private final ModelPart leftLeg2A;
    private final ModelPart leftLeg2B;
    private final ModelPart leftLeg3A;
    private final ModelPart leftLeg3B;
    private final ModelPart leftLeg4A;

    public MoCCrabModel(ModelPart root) {
        this.root = root;
        this.rightArmA = root.getChild("right_arm_a");
        this.rightArmB = this.rightArmA.getChild("right_arm_b");
        this.leftArmA = root.getChild("left_arm_a");
        this.leftArmB = this.leftArmA.getChild("left_arm_b");
        this.rightLeg1A = root.getChild("right_leg_1a");
        this.rightLeg1B = this.rightLeg1A.getChild("right_leg_1b");
        this.rightLeg2A = root.getChild("right_leg_2a");
        this.rightLeg2B = this.rightLeg2A.getChild("right_leg_2b");
        this.rightLeg3A = root.getChild("right_leg_3a");
        this.rightLeg3B = this.rightLeg3A.getChild("right_leg_3b");
        this.rightLeg4A = root.getChild("right_leg_4a");
        this.leftLeg1A = root.getChild("left_leg_1a");
        this.leftLeg1B = this.leftLeg1A.getChild("left_leg_1b");
        this.leftLeg2A = root.getChild("left_leg_2a");
        this.leftLeg2B = this.leftLeg2A.getChild("left_leg_2b");
        this.leftLeg3A = root.getChild("left_leg_3a");
        this.leftLeg3B = this.leftLeg3A.getChild("left_leg_3b");
        this.leftLeg4A = root.getChild("left_leg_4a");
    }

    /** The texture layout is 64x64. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("shell",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, -4F, 10, 4, 8),
                PartPose.offset(0F, 16F, 0F));
        root.addOrReplaceChild("shell_right",
                CubeListBuilder.create().texOffs(0, 23).addBox(4.6F, -2F, -4F, 3, 3, 8),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, 0.418879F));
        root.addOrReplaceChild("shell_left",
                CubeListBuilder.create().texOffs(0, 12).addBox(-7.6F, -2F, -4F, 3, 3, 8),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, -0.418879F));
        root.addOrReplaceChild("shell_back",
                CubeListBuilder.create().texOffs(10, 42).addBox(-5F, -1.6F, 3.6F, 10, 3, 3),
                PartPose.offsetAndRotation(0F, 16F, 0F, -0.418879F, 0F, 0F));

        root.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(0, 4).addBox(1F, -2F, -4.5F, 1, 3, 1),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, 0.1745329F));
        root.addOrReplaceChild("left_eye_base",
                CubeListBuilder.create().texOffs(0, 16).addBox(1F, 1F, -5F, 2, 3, 1),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, 0.2094395F));
        root.addOrReplaceChild("right_eye_base",
                CubeListBuilder.create().texOffs(0, 12).addBox(-3F, 1F, -5F, 2, 3, 1),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, -0.2094395F));
        root.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2F, -4.5F, 1, 3, 1),
                PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, -0.1745329F));

        // ---- Right pincer arm ----
        PartDefinition rightArmA = root.addOrReplaceChild("right_arm_a",
                CubeListBuilder.create().texOffs(0, 34).addBox(-4F, -1F, -1F, 4, 2, 2),
                PartPose.offsetAndRotation(-4F, 19F, -4F, 0F, -0.5235988F, 0F));
        PartDefinition rightArmB = rightArmA.addOrReplaceChild("right_arm_b",
                CubeListBuilder.create().texOffs(22, 12).addBox(-4F, -1.5F, -1F, 4, 3, 2),
                PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, -2.094395F, 0F));
        rightArmB.addOrReplaceChild("right_arm_c",
                CubeListBuilder.create().texOffs(22, 17).addBox(-3F, -1.5F, -1F, 3, 1, 2),
                PartPose.offset(-4F, 0F, 0F));
        rightArmB.addOrReplaceChild("right_arm_d",
                CubeListBuilder.create().texOffs(16, 12).addBox(-2F, 0.5F, -0.5F, 2, 1, 1),
                PartPose.offset(-4F, 0F, 0F));

        // ---- Left pincer arm ----
        PartDefinition leftArmA = root.addOrReplaceChild("left_arm_a",
                CubeListBuilder.create().texOffs(0, 38).addBox(0F, -1F, -1F, 4, 2, 2),
                PartPose.offsetAndRotation(4F, 19F, -4F, 0F, 0.5235988F, 0F));
        PartDefinition leftArmB = leftArmA.addOrReplaceChild("left_arm_b",
                CubeListBuilder.create().texOffs(22, 20).addBox(0F, -1.5F, -1F, 4, 3, 2),
                PartPose.offsetAndRotation(4F, 0F, 0F, 0F, 2.094395F, 0F));
        leftArmB.addOrReplaceChild("left_arm_c",
                CubeListBuilder.create().texOffs(22, 25).addBox(0F, -1.5F, -1F, 3, 1, 2),
                PartPose.offset(4F, 0F, 0F));
        leftArmB.addOrReplaceChild("left_arm_d",
                CubeListBuilder.create().texOffs(16, 23).addBox(0F, 0.5F, -0.5F, 2, 1, 1),
                PartPose.offset(4F, 0F, 0F));

        // ---- Right legs ----
        PartDefinition rightLeg1A = root.addOrReplaceChild("right_leg_1a",
                CubeListBuilder.create().texOffs(0, 42).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-5F, 19.5F, -2.5F, 0F, -0.1745329F, -0.418879F));
        rightLeg1A.addOrReplaceChild("right_leg_1b",
                CubeListBuilder.create().texOffs(0, 48).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, 0F, -0.5235988F));

        PartDefinition rightLeg2A = root.addOrReplaceChild("right_leg_2a",
                CubeListBuilder.create().texOffs(0, 44).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-5F, 19.5F, 0F, 0F, 0.0872665F, -0.418879F));
        rightLeg2A.addOrReplaceChild("right_leg_2b",
                CubeListBuilder.create().texOffs(0, 50).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, 0F, -0.5235988F));

        PartDefinition rightLeg3A = root.addOrReplaceChild("right_leg_3a",
                CubeListBuilder.create().texOffs(0, 46).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-5F, 19.5F, 2.5F, 0F, 0.6981317F, -0.418879F));
        rightLeg3A.addOrReplaceChild("right_leg_3b",
                CubeListBuilder.create().texOffs(0, 52).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, 0F, -0.5235988F));

        PartDefinition rightLeg4A = root.addOrReplaceChild("right_leg_4a",
                CubeListBuilder.create().texOffs(12, 34).addBox(-4F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(-3F, 19.5F, 3.5F, 0F, 0.6108652F, -0.418879F));
        PartDefinition rightLeg4B = rightLeg4A.addOrReplaceChild("right_leg_4b",
                CubeListBuilder.create().texOffs(12, 36).addBox(-3F, -0.5F, -1F, 3, 1, 2),
                PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, 1.308997F, -0.418879F));
        rightLeg4B.addOrReplaceChild("right_leg_4c",
                CubeListBuilder.create().mirror().texOffs(12, 39).addBox(-3F, -0.5F, -1F, 3, 1, 2),
                PartPose.offsetAndRotation(-3F, 0F, 0F, 0F, 0.8726646F, -0.418879F));

        // ---- Left legs ----
        PartDefinition leftLeg1A = root.addOrReplaceChild("left_leg_1a",
                CubeListBuilder.create().texOffs(0, 54).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(5F, 19.5F, -2.5F, 0F, 0.1745329F, 0.418879F));
        leftLeg1A.addOrReplaceChild("left_leg_1b",
                CubeListBuilder.create().texOffs(0, 56).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(4F, 0F, 0F, 0F, 0F, 0.5235988F));

        PartDefinition leftLeg2A = root.addOrReplaceChild("left_leg_2a",
                CubeListBuilder.create().texOffs(0, 62).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(5F, 19.5F, 0F, 0F, -0.0872665F, 0.418879F));
        leftLeg2A.addOrReplaceChild("left_leg_2b",
                CubeListBuilder.create().texOffs(10, 62).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(4F, 0F, 0F, 0F, 0F, 0.5235988F));

        PartDefinition leftLeg3A = root.addOrReplaceChild("left_leg_3a",
                CubeListBuilder.create().texOffs(0, 58).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(5F, 19.5F, 2.5F, 0F, -0.6981317F, 0.418879F));
        leftLeg3A.addOrReplaceChild("left_leg_3b",
                CubeListBuilder.create().texOffs(0, 60).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(4F, 0F, 0F, 0F, 0F, 0.5235988F));

        PartDefinition leftLeg4A = root.addOrReplaceChild("left_leg_4a",
                CubeListBuilder.create().texOffs(22, 34).addBox(0F, -0.5F, -0.5F, 4, 1, 1),
                PartPose.offsetAndRotation(2F, 19.5F, 3.5F, 0F, -0.6108652F, 0.418879F));
        PartDefinition leftLeg4B = leftLeg4A.addOrReplaceChild("left_leg_4b",
                CubeListBuilder.create().texOffs(22, 36).addBox(0F, -0.5F, -1F, 3, 1, 2),
                PartPose.offsetAndRotation(4F, 0F, 0F, 0F, -1.308997F, 0.418879F));
        leftLeg4B.addOrReplaceChild("left_leg_4c",
                CubeListBuilder.create().texOffs(22, 39).addBox(0F, -0.5F, -1F, 3, 1, 2),
                PartPose.offsetAndRotation(3F, 0F, 0F, 0F, -0.8726646F, 0.418879F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCCrabEntity crab, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fleeing = limbSwingAmount > CLAW_RAISE_THRESHOLD;
        this.leftArmA.xRot = fleeing ? CLAW_RAISE_ANGLE : 0F;
        this.rightArmA.xRot = fleeing ? CLAW_RAISE_ANGLE : 0F;

        if (limbSwingAmount < 0.1F) {
            // Idle claw fidget: each arm occasionally twitches on its own short cycle.
            float leftTwitch = idleTwitch(ageInTicks, 100F, 0F, 10F);
            float rightTwitch = idleTwitch(ageInTicks, 75F, 30F, 40F);
            this.leftArmA.yRot = 0.5235988F + leftTwitch;
            this.rightArmA.yRot = -0.5235988F - rightTwitch;
        }

        float f9 = -Mth.cos(limbSwing * 5F) * limbSwingAmount * 2F;
        float f10 = -Mth.cos(limbSwing * 5F + Mth.PI) * limbSwingAmount * 2F;
        float f11 = -Mth.cos(limbSwing * 5F + Mth.HALF_PI) * limbSwingAmount * 2F;
        float f12 = -Mth.cos(limbSwing * 5F + Mth.PI + Mth.HALF_PI) * limbSwingAmount * 2F;
        float f13 = Math.abs(Mth.sin(limbSwing * 0.6662F) * 0.4F) * limbSwingAmount * 5F;
        float f14 = Math.abs(Mth.sin(limbSwing * 0.6662F + Mth.PI) * 0.4F) * limbSwingAmount;
        float f15 = Math.abs(Mth.sin(limbSwing * 0.6662F + Mth.HALF_PI) * 0.4F) * limbSwingAmount;
        float f16 = Math.abs(Mth.sin(limbSwing * 0.6662F + Mth.PI + Mth.HALF_PI) * 0.4F) * limbSwingAmount;

        this.rightLeg1A.yRot = -0.1745329F + f9;
        this.rightLeg1A.zRot = -0.418879F + f13;
        this.rightLeg1B.zRot = -0.5235988F - f13;

        this.rightLeg2A.yRot = 0.0872665F + f10;
        this.rightLeg2A.zRot = -0.418879F + f14;
        this.rightLeg2B.zRot = -0.5235988F - f14;

        this.rightLeg3A.yRot = 0.6981317F + f11;
        this.rightLeg3A.zRot = -0.418879F + f15;
        this.rightLeg3B.zRot = -0.5235988F - f15;

        this.rightLeg4A.yRot = 0.6108652F + f12;
        this.rightLeg4A.zRot = -0.418879F + f16;

        this.leftLeg1A.yRot = 0.1745329F - f9;
        this.leftLeg1A.zRot = 0.418879F - f13;
        this.leftLeg1B.zRot = 0.5235988F + f13;

        this.leftLeg2A.yRot = -0.0872665F - f10;
        this.leftLeg2A.zRot = 0.418879F - f14;
        this.leftLeg2B.zRot = 0.5235988F + f14;

        this.leftLeg3A.yRot = -0.6981317F - f11;
        this.leftLeg3A.zRot = 0.418879F - f15;
        this.leftLeg3B.zRot = 0.5235988F + f15;

        this.leftLeg4A.yRot = -0.6108652F - f12;
        this.leftLeg4A.zRot = 0.418879F - f16;
    }

    /** Original: a claw stays still except for a short twitch once every {@code period} ticks. */
    private static float idleTwitch(float ageInTicks, float period, float windowStart, float windowEnd) {
        float phase = ageInTicks % period;
        if (phase > windowStart && phase < windowEnd) {
            return (phase - windowStart) * 2F / ModelAnimations.DEGREES_PER_RADIAN;
        }
        return 0F;
    }
}