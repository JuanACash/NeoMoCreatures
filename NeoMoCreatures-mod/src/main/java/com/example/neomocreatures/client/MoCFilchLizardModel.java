package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFilchLizardEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelFilchLizard}. Two poses: on all fours with its
 * frill folded, or — while carrying loot — standing up on its hind legs with the frill spread out.
 */
public class MoCFilchLizardModel<T extends MoCFilchLizardEntity> extends HierarchicalModel<T> {

    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 32;

    private static final float WALK_FREQUENCY = 0.6662F * 2.0F;
    private static final float LEG_SWING = 0.6F;
    private static final float TAIL_SWING = 0.2F;
    private static final float QUARTER_TURN = Mth.HALF_PI;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart foldedHead;
    private final ModelPart foldedFrillLeft;
    private final ModelPart foldedFrillRight;
    /** Standing head, with the six spread frill plates as children. */
    private final ModelPart head;

    public MoCFilchLizardModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.foldedHead = root.getChild("folded_head");
        this.foldedFrillLeft = root.getChild("folded_frill_left");
        this.foldedFrillRight = root.getChild("folded_frill_right");
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Positions of body, tail and legs are set every frame in setupAnim (they depend on the pose).
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 6).addBox(-2.0F, -1.5F, -6.0F, 4, 3, 12), PartPose.ZERO);
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(32, 9).addBox(-1.0F, -0.5F, 0.0F, 2, 2, 10), PartPose.ZERO);
        root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -0.5F, -0.5F, 4, 1, 1), PartPose.ZERO);
        root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(16, 3).addBox(-4.0F, -0.5F, -0.5F, 4, 1, 1), PartPose.ZERO);
        root.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -0.5F, -0.5F, 4, 1, 1), PartPose.ZERO);
        root.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(16, 3).addBox(-4.0F, -0.5F, -0.5F, 4, 1, 1), PartPose.ZERO);

        // Folded (walking) head and frill
        root.addOrReplaceChild("folded_head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -0.5F, -4.0F, 4, 2, 4),
                PartPose.offset(0.0F, 21.0F, -6.0F));
        root.addOrReplaceChild("folded_frill_left", CubeListBuilder.create().texOffs(0, 22).addBox(1.0F, -1.5F, 0.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 21.0F, -6.0F, 0.0F, 0.0349066F, 0.0F));
        root.addOrReplaceChild("folded_frill_right", CubeListBuilder.create().texOffs(14, 22).addBox(-2.0F, -1.5F, 0.0F, 1, 3, 6),
                PartPose.offsetAndRotation(0.0F, 21.0F, -6.0F, 0.0F, -0.0349066F, 0.0F));

        // Standing head with the spread frill
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.5F, -4.0F, 4, 2, 4),
                PartPose.offset(0.0F, 12.0F, -1.0F));
        head.addOrReplaceChild("frill_1", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, -2.5F, 2.5F, 1, 3, 6),
                PartPose.rotation(0.3665191F, QUARTER_TURN, -0.296706F));
        head.addOrReplaceChild("frill_2", CubeListBuilder.create().texOffs(14, 22).addBox(-1.0F, -2.5F, 2.5F, 1, 3, 6),
                PartPose.rotation(0.3665191F, -QUARTER_TURN, 0.296706F));
        head.addOrReplaceChild("frill_3", CubeListBuilder.create().texOffs(0, 22).addBox(-0.5F, -2.5F, 2.0F, 1, 3, 6),
                PartPose.rotation(0.0F, QUARTER_TURN, -0.2617994F));
        head.addOrReplaceChild("frill_4", CubeListBuilder.create().texOffs(14, 22).addBox(-0.5F, -2.5F, 2.0F, 1, 3, 6),
                PartPose.rotation(0.0F, -QUARTER_TURN, 0.2617994F));
        head.addOrReplaceChild("frill_5", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -2.5F, 1.5F, 1, 3, 6),
                PartPose.rotation(-0.3839724F, QUARTER_TURN, -0.2617994F));
        head.addOrReplaceChild("frill_6", CubeListBuilder.create().texOffs(14, 22).addBox(0.0F, -2.5F, 1.5F, 1, 3, 6),
                PartPose.rotation(-0.4014257F, -QUARTER_TURN, 0.2617994F));

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T lizard, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean standing = lizard.isCarryingLoot();
        this.head.visible = standing;
        this.foldedHead.visible = !standing;
        this.foldedFrillLeft.visible = !standing;
        this.foldedFrillRight.visible = !standing;

        float walk = Mth.cos(limbSwing * WALK_FREQUENCY + Mth.PI) * limbSwingAmount;
        float headYaw = netHeadYaw * Mth.DEG_TO_RAD;
        float headPitchRad = headPitch * Mth.DEG_TO_RAD;

        if (standing) {
            pose(this.frontLeftLeg, -2.0F, 13.0F, -1.0F, 0.0F, 1.047198F, 0.6981317F);
            pose(this.frontRightLeg, 2.0F, 13.0F, -1.0F, 0.0F, -1.047198F, -0.6981317F);
            pose(this.backLeftLeg, 2.0F, 20.0F, 5.0F, 0.0F, 0.0F, 1.396263F);
            pose(this.backRightLeg, -2.0F, 20.0F, 5.0F, 0.0F, 0.0F, -1.396263F);
            pose(this.body, 0.0F, 16.0F, 2.0F, -0.9948377F, 0.0F, 0.0F);
            pose(this.tail, 0.0F, 20.0F, 6.0F, 0.6806784F, 0.0F, 0.0F);
            this.head.xRot = headPitchRad;
            this.head.yRot = headYaw;
        } else {
            pose(this.frontLeftLeg, 2.0F, 22.0F, -4.0F, 0.0F, walk * LEG_SWING, 0.3839724F);
            pose(this.frontRightLeg, -2.0F, 22.0F, -4.0F, 0.0F, -walk * LEG_SWING, -0.3839724F);
            pose(this.backLeftLeg, 2.0F, 22.0F, 5.0F, 0.0F, 0.0F, 0.3839724F);
            pose(this.backRightLeg, -2.0F, 22.0F, 5.0F, 0.0F, 0.0F, -0.3839724F);
            pose(this.body, 0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            pose(this.tail, 0.0F, 21.0F, 6.0F, 0.0F, 0.0F, 0.0F);
            this.foldedHead.xRot = headPitchRad;
            this.foldedHead.yRot = headYaw;
        }
        // Original: back legs and tail wiggle in both poses.
        this.backLeftLeg.yRot = walk * LEG_SWING;
        this.backRightLeg.yRot = walk * LEG_SWING;
        this.tail.yRot = -walk * TAIL_SWING;
    }

    private static void pose(ModelPart part, float x, float y, float z, float xRot, float yRot, float zRot) {
        part.x = x;
        part.y = y;
        part.z = z;
        part.xRot = xRot;
        part.yRot = yRot;
        part.zRot = zRot;
    }
}