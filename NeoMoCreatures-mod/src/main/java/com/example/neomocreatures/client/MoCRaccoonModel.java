package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCRaccoonEntity;


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelRaccoon (Techne) to
 * HierarchicalModel. Snout/ears are nested under head here (they weren't in
 * the original, which manually copied Head's rotation onto them every tick)
 * so the hierarchy handles that for free. Legs stay as flat, independent
 * siblings exactly like the original — nesting them would double up the
 * walk-cycle rotation, since each segment's angle is computed directly
 * rather than as an offset from its neighbour. The "sideburn" parts from
 * the original are omitted entirely: they were commented out of its own
 * render() call and never actually appeared.
 */
public class MoCRaccoonModel extends HierarchicalModel<MoCRaccoonEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart legFrontRightA;
    private final ModelPart legFrontRightB;
    private final ModelPart footFrontRight;
    private final ModelPart legFrontLeftA;
    private final ModelPart legFrontLeftB;
    private final ModelPart footFrontLeft;
    private final ModelPart legRearRightA;
    private final ModelPart legRearRightB;
    private final ModelPart footRearRight;
    private final ModelPart legRearLeftA;
    private final ModelPart legRearLeftB;
    private final ModelPart footRearLeft;
    private final ModelPart tailA;
    private final ModelPart tailB;

    public MoCRaccoonModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.legFrontRightA = root.getChild("leg_front_right_a");
        this.legFrontRightB = root.getChild("leg_front_right_b");
        this.footFrontRight = root.getChild("foot_front_right");
        this.legFrontLeftA = root.getChild("leg_front_left_a");
        this.legFrontLeftB = root.getChild("leg_front_left_b");
        this.footFrontLeft = root.getChild("foot_front_left");
        this.legRearRightA = root.getChild("leg_rear_right_a");
        this.legRearRightB = root.getChild("leg_rear_right_b");
        this.footRearRight = root.getChild("foot_rear_right");
        this.legRearLeftA = root.getChild("leg_rear_left_a");
        this.legRearLeftB = root.getChild("leg_rear_left_b");
        this.footRearLeft = root.getChild("foot_rear_left");
        this.tailA = root.getChild("tail_a");
        this.tailB = root.getChild("tail_b");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(38, 21).addBox(-4F, -3.5F, -6.5F, 8, 6, 5),
                PartPose.offset(0F, 17F, -4F));

        head.addOrReplaceChild("snout",
                CubeListBuilder.create().texOffs(24, 25).addBox(-1.5F, -0.5F, -10.5F, 3, 3, 4),
                PartPose.ZERO);

        head.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(24, 22).addBox(-4F, -5.5F, -3.5F, 3, 2, 1),
                PartPose.ZERO);

        head.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(24, 18).addBox(1F, -5.5F, -3.5F, 3, 2, 1),
                PartPose.ZERO);


        head.addOrReplaceChild("sideburn_right",
                CubeListBuilder.create().texOffs(0, 32).addBox(-3F, -2F, -2F, 3, 4, 4),
                PartPose.offsetAndRotation(-2.5F, 0.5F, -3.2F, 0F, -0.5235988F, 0F));

        head.addOrReplaceChild("sideburn_left",
                CubeListBuilder.create().texOffs(0, 40).addBox(0F, -2F, -2F, 3, 4, 4),
                PartPose.offsetAndRotation(2.5F, 0.5F, -3.2F, 0F, 0.5235988F, 0F));

        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(46, 4).addBox(-2.5F, -2F, -3F, 5, 4, 3),
                PartPose.offsetAndRotation(0F, 17F, -4F, -0.4461433F, 0F, 0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3F, 0F, -3F, 6, 6, 12),
                PartPose.offset(0F, 15F, -2F));

        root.addOrReplaceChild("tail_a",
                CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -6F, -1.5F, 3, 6, 3),
                PartPose.offsetAndRotation(0F, 16.5F, 6.5F, -2.024582F, 0F, 0F));

        root.addOrReplaceChild("tail_b",
                CubeListBuilder.create().texOffs(24, 3).addBox(-1.5F, -11F, 0.3F, 3, 6, 3),
                PartPose.offsetAndRotation(0F, 16.5F, 6.5F, -1.689974F, 0F, 0F));

        root.addOrReplaceChild("leg_front_right_a",
                CubeListBuilder.create().texOffs(36, 0).addBox(-4F, -1F, -1F, 2, 5, 3),
                PartPose.offsetAndRotation(0F, 18F, -4F, 0.5205006F, 0F, 0F));
        root.addOrReplaceChild("leg_front_right_b",
                CubeListBuilder.create().texOffs(46, 11).addBox(-3.5F, 1F, 2F, 2, 4, 2),
                PartPose.offsetAndRotation(0F, 18F, -4F, -0.3717861F, 0F, 0F));
        root.addOrReplaceChild("foot_front_right",
                CubeListBuilder.create().texOffs(46, 0).addBox(-4F, 5F, -1F, 3, 1, 3),
                PartPose.offset(0F, 18F, -4F));

        root.addOrReplaceChild("leg_front_left_a",
                CubeListBuilder.create().texOffs(36, 8).addBox(2F, -1F, -1F, 2, 5, 3),
                PartPose.offsetAndRotation(0F, 18F, -4F, 0.5205006F, 0F, 0F));
        root.addOrReplaceChild("leg_front_left_b",
                CubeListBuilder.create().texOffs(54, 11).addBox(1.5F, 1F, 2F, 2, 4, 2),
                PartPose.offsetAndRotation(0F, 18F, -4F, -0.3717861F, 0F, 0F));
        root.addOrReplaceChild("foot_front_left",
                CubeListBuilder.create().texOffs(46, 0).addBox(1F, 5F, -1F, 3, 1, 3),
                PartPose.offset(0F, 18F, -4F));

        root.addOrReplaceChild("leg_rear_right_a",
                CubeListBuilder.create().texOffs(12, 18).addBox(-5F, -2F, -3F, 2, 5, 4),
                PartPose.offsetAndRotation(0F, 18F, 4F, 0.9294653F, 0F, 0F));
        root.addOrReplaceChild("leg_rear_right_b",
                CubeListBuilder.create().texOffs(0, 27).addBox(-4.5F, 2F, -5F, 2, 2, 3),
                PartPose.offsetAndRotation(0F, 18F, 4F, 0.9294653F, 0F, 0F));
        root.addOrReplaceChild("foot_rear_right",
                CubeListBuilder.create().texOffs(46, 0).addBox(-5F, 5F, -2F, 3, 1, 3),
                PartPose.offset(0F, 18F, 4F));

        root.addOrReplaceChild("leg_rear_left_a",
                CubeListBuilder.create().texOffs(0, 18).addBox(3F, -2F, -3F, 2, 5, 4),
                PartPose.offsetAndRotation(0F, 18F, 4F, 0.9294653F, 0F, 0F));
        root.addOrReplaceChild("leg_rear_left_b",
                CubeListBuilder.create().texOffs(10, 27).addBox(2.5F, 2F, -5F, 2, 2, 3),
                PartPose.offsetAndRotation(0F, 18F, 4F, 0.9294653F, 0F, 0F));
        root.addOrReplaceChild("foot_rear_left",
                CubeListBuilder.create().texOffs(46, 0).addBox(2F, 5F, -2F, 3, 1, 3),
                PartPose.offset(0F, 18F, 4F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCRaccoonEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        float rLeg = Mth.cos(limbSwing + (float) Math.PI) * 0.8F * limbSwingAmount;
        float lLeg = Mth.cos(limbSwing) * 0.8F * limbSwingAmount;

        this.legFrontRightA.xRot = (30F / ModelAnimations.DEGREES_PER_RADIAN) + rLeg;
        this.legFrontLeftA.xRot = (30F / ModelAnimations.DEGREES_PER_RADIAN) + lLeg;
        this.legRearRightA.xRot = (53F / ModelAnimations.DEGREES_PER_RADIAN) + lLeg;
        this.legRearLeftA.xRot = (53F / ModelAnimations.DEGREES_PER_RADIAN) + rLeg;

        this.legFrontRightB.xRot = (-21F / ModelAnimations.DEGREES_PER_RADIAN) + rLeg;
        this.footFrontRight.xRot = rLeg;
        this.legFrontLeftB.xRot = (-21F / ModelAnimations.DEGREES_PER_RADIAN) + lLeg;
        this.footFrontLeft.xRot = lLeg;

        this.legRearRightB.xRot = (53F / ModelAnimations.DEGREES_PER_RADIAN) + lLeg;
        this.footRearRight.xRot = lLeg;
        this.legRearLeftB.xRot = (53F / ModelAnimations.DEGREES_PER_RADIAN) + rLeg;
        this.footRearLeft.xRot = rLeg;

        this.tailA.yRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.7F);
        this.tailB.yRot = this.tailA.yRot;
    }
}