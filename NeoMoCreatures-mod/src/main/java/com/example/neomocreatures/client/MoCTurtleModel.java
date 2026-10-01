package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCTurtleEntity;
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
 * Port of {@code drzhark.mocreatures.client.model.MoCModelTurtle} to {@link HierarchicalModel}.
 * <p>
 * The texture layout is 64x32; the texture files are a 128x64 hi-res version of it.
 * <p>
 * Unlike the original, {@link #setupAnim} does not rely on state left over from the previous frame:
 * every pose is reset first and then rebuilt from the entity, and the flipped-over struggle is
 * driven by {@code ageInTicks} instead of a hand-updated swing counter.
 */
public class MoCTurtleModel extends HierarchicalModel<MoCTurtleEntity> {

    /** Largest angle, in radians, that the legs of a flipped turtle flail. */
    private static final float STRUGGLE_MAX_ANGLE = 1.2F;
    /** How far the legs are tilted while paddling. */
    private static final float SWIM_LEG_TILT = 1.2F;
    private static final float DEG_TO_RAD = (float) Math.PI / 180F;

    private final ModelPart root;
    private final ModelPart shellTop;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart legFrontLeft;
    private final ModelPart legFrontRight;
    private final ModelPart legRearLeft;
    private final ModelPart legRearRight;

    public MoCTurtleModel(ModelPart root) {
        this.root = root;
        this.shellTop = root.getChild("shell_top");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.legFrontLeft = root.getChild("leg_front_left");
        this.legFrontRight = root.getChild("leg_front_right");
        this.legRearLeft = root.getChild("leg_rear_left");
        this.legRearRight = root.getChild("leg_rear_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("shell",
                CubeListBuilder.create().texOffs(28, 0).addBox(0F, 0F, 0F, 9, 1, 9),
                PartPose.offset(-4.5F, 19F, -4.5F));
        root.addOrReplaceChild("shell_up",
                CubeListBuilder.create().texOffs(0, 22).addBox(0F, 0F, 0F, 8, 2, 8),
                PartPose.offset(-4F, 17F, -4F));
        root.addOrReplaceChild("shell_top",
                CubeListBuilder.create().texOffs(40, 10).addBox(0F, 0F, 0F, 6, 1, 6),
                PartPose.offset(-3F, 16F, -3F));
        root.addOrReplaceChild("belly",
                CubeListBuilder.create().texOffs(0, 12).addBox(0F, 0F, 0F, 8, 1, 8),
                PartPose.offset(-4F, 20F, -4F));

        // +X is the entity's left in model space.
        root.addOrReplaceChild("leg_front_left",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(3.5F, 20F, -3.5F));
        root.addOrReplaceChild("leg_front_right",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(-3.5F, 20F, -3.5F));
        root.addOrReplaceChild("leg_rear_left",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(3.5F, 20F, 3.5F));
        root.addOrReplaceChild("leg_rear_right",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(-3.5F, 20F, 3.5F));

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(10, 0).addBox(-1.5F, -1F, -4F, 3, 2, 4),
                PartPose.offset(0F, 20F, -4.5F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(0, 5).addBox(-1F, -1F, 0F, 2, 1, 3),
                PartPose.offset(0F, 21F, 4F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    /**
     * Angle, in radians, of the flailing of a flipped turtle at the given time. Comes in bursts
     * rather than continuously. Shared with the renderer so the legs and the body roll agree.
     */
    public static float struggleWave(float ageInTicks) {
        float burst = Mth.abs(Mth.sin(ageInTicks * 0.12F));
        return Mth.sin(ageInTicks * 0.5F) * STRUGGLE_MAX_ANGLE * burst;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCTurtleEntity turtle, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Ninja Turtles do not show the top shell cube (it hides the shell texture of their own skin).
        this.shellTop.visible = turtle.getTmntBrother() == null;

        if (turtle.isHiding() && !turtle.isInWater()) {
            this.retreatIntoShell();
        } else {
            this.head.xRot = headPitch * DEG_TO_RAD;
            this.head.yRot = netHeadYaw * DEG_TO_RAD;
        }

        if (turtle.isUpsideDown()) {
            this.animateStruggling(ageInTicks);
        } else if (!turtle.isHeld()) {
            // While carried on its owner's head the legs stay at rest, even if the owner is swimming.
            if (turtle.isInWater()) {
                this.animateSwimming(limbSwing, limbSwingAmount);
            } else {
                this.animateWalking(limbSwing, limbSwingAmount);
            }
        }
    }

    /** Head, legs and tail are pulled in towards the body. */
    private void retreatIntoShell() {
        this.head.setPos(0F, 19.5F, -1F);
        this.legFrontLeft.setPos(2.9F, 18.5F, -2.9F);
        this.legFrontRight.setPos(-2.9F, 18.5F, -2.9F);
        this.legRearLeft.setPos(2.9F, 18.5F, 2.9F);
        this.legRearRight.setPos(-2.9F, 18.5F, 2.9F);
        this.tail.setPos(0F, 21F, 2F);
    }

    private void animateWalking(float limbSwing, float limbSwingAmount) {
        // Diagonal legs move together.
        float stepA = Mth.cos(limbSwing * 2.0F) * 2.0F * limbSwingAmount;
        float stepB = Mth.cos(limbSwing * 2.0F + (float) Math.PI) * 2.0F * limbSwingAmount;
        this.legFrontLeft.xRot = stepA;
        this.legFrontRight.xRot = stepB;
        this.legRearLeft.xRot = stepB;
        this.legRearRight.xRot = stepA;
        this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
    }

    private void animateSwimming(float limbSwing, float limbSwingAmount) {
        float paddle = Mth.cos(limbSwing * 0.5F) * 6.0F * limbSwingAmount;
        this.legFrontLeft.xRot = -SWIM_LEG_TILT;
        this.legFrontLeft.yRot = -SWIM_LEG_TILT + paddle;
        this.legFrontRight.xRot = -SWIM_LEG_TILT;
        this.legFrontRight.yRot = SWIM_LEG_TILT - paddle;
        this.legRearLeft.xRot = SWIM_LEG_TILT;
        this.legRearRight.xRot = SWIM_LEG_TILT;
    }

    private void animateStruggling(float ageInTicks) {
        float flail = struggleWave(ageInTicks);
        this.legFrontLeft.xRot = -flail;
        this.legFrontRight.xRot = flail;
        this.legRearLeft.xRot = flail;
        this.legRearRight.xRot = -flail;
        this.tail.yRot = -flail;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}