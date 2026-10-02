package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.example.neomocreatures.entity.MoCKittyEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;


public class MoCKittyModel extends HierarchicalModel<MoCKittyEntity> {

    private final ModelPart root;

    // headParts[0..9] — all independent, all share the same head pivot/rotation, like the original.
    private final ModelPart[] headParts = new ModelPart[10];
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart tail;
    private final ModelPart medallion;

    public MoCKittyModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < 10; i++) {
            this.headParts[i] = root.getChild("head_part_" + i);
        }
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.tail = root.getChild("tail");
        this.medallion = root.getChild("medallion");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head_part_0", CubeListBuilder.create().texOffs(16, 0).addBox(-2F, -5F, -3F, 1, 1, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_1", CubeListBuilder.create().texOffs(16, 0).mirror().addBox(1F, -5F, -3F, 1, 1, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_2", CubeListBuilder.create().texOffs(20, 0).addBox(-2.5F, -4F, -3F, 2, 1, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_3", CubeListBuilder.create().texOffs(20, 0).mirror().addBox(0.5F, -4F, -3F, 2, 1, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_4", CubeListBuilder.create().texOffs(40, 0).addBox(-4F, -1.5F, -5F, 3, 3, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_5", CubeListBuilder.create().texOffs(40, 0).mirror().addBox(1F, -1.5F, -5F, 3, 3, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_6", CubeListBuilder.create().texOffs(21, 6).addBox(-1F, -1F, -5F, 2, 2, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_7", CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, 0.5F, -1F, 5, 4, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_8", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, -2F, -4.1F, 3, 1, 1),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("head_part_9", CubeListBuilder.create().texOffs(1, 1).addBox(-2.5F, -3F, -4F, 5, 4, 4),
                PartPose.offset(0F, 15F, -2F));

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 0).addBox(-2.5F, -2F, 0F, 5, 5, 10),
                PartPose.offset(0F, 15F, -2F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offset(-1.5F, 18F, -1F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 9).mirror().addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offset(1.5F, 18F, -1F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(8, 9).addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offset(-1.5F, 18F, 7F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(8, 9).mirror().addBox(-1F, 0F, -1F, 2, 6, 2),
                PartPose.offset(1.5F, 18F, 7F));
        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 9).mirror().addBox(-0.5F, -8F, -1F, 1, 8, 1),
                PartPose.offset(0F, 14.5F, 7.5F));
        root.addOrReplaceChild("medallion", CubeListBuilder.create().texOffs(51, 1).addBox(-2.5F, -1.75F, 0F, 5, 3.5F, 0),
                PartPose.offset(0F, 18F, -2.5F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCKittyEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        boolean sitting = entity.isKittySitting()
                || entity.getVehicle() instanceof MoCKittyBedEntity
                || entity.isHeld();
        boolean swinging = entity.isKittySwinging();
        int kittyState = entity.getKittyState();

        // headParts[7] only while kittyState > 2 (an open-mouth/alert detail once
        // the AI state machine exists); headParts[8] only while sleeping (state 12).
        for (int i = 0; i < 10; i++) {
            headParts[i].visible = i != 7 && i != 8;
        }
        headParts[7].visible = false;
        headParts[8].visible = kittyState == 12;

        float headYRot = netHeadYaw / ModelAnimations.DEGREES_PER_RADIAN;
        float headXRot = headPitch / ModelAnimations.DEGREES_PER_RADIAN;
        for (int i = 0; i < 9; i++) {
            headParts[i].yRot = headYRot;
            headParts[i].xRot = headXRot;
        }
        headParts[9].yRot = headYRot;
        headParts[9].xRot = headXRot;
        medallion.visible = entity.isTame();

        rightArm.xRot = Mth.cos((limbSwing * 0.6662F) + 3.141593F) * 2.0F * limbSwingAmount * 0.5F;
        leftArm.xRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 2.0F) * 0.5F;
        rightArm.zRot = 0F;
        leftArm.zRot = 0F;
        rightLeg.xRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 1.4F);
        leftLeg.xRot = Mth.cos((limbSwing * 0.6662F) + 3.141593F) * 1.4F * limbSwingAmount;
        rightLeg.yRot = 0F;
        leftLeg.yRot = 0F;

        if (swinging) {
                float swingProgress = entity.getSwingProgress(); // 0 → 2.0
                rightArm.xRot = -2F + swingProgress;
                rightArm.yRot = 2.25F - (swingProgress * 2.0F);
        } else {
                rightArm.yRot = 0F;
        }
        leftArm.yRot = 0F;

        tail.xRot = -0.5F;
        tail.zRot = leftLeg.xRot * 0.625F;

        root.y = sitting ? 4F : 0F; // approximates the original's matrixStack.translate(0, 0.25, 0) while sitting

        if (sitting) {
            tail.zRot = 0F;
            tail.xRot = -2.3F;
            rightArm.xRot = -1.570796F;
            leftArm.xRot = -1.570796F;
            rightLeg.xRot = -1.570796F;
            leftLeg.xRot = -1.570796F;
            rightLeg.yRot = 0.1F;
            leftLeg.yRot = -0.1F;
        }
    }
}