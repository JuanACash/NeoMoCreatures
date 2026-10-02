package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMoleEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelMole}. */
public class MoCMoleModel<T extends MoCMoleEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart nose;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leftLeg;
    private final ModelPart leftFingers;
    private final ModelPart rightLeg;
    private final ModelPart rightFingers;
    private final ModelPart leftRearLeg;
    private final ModelPart rightRearLeg;

    public MoCMoleModel(ModelPart root) {
        this.root = root;
        this.nose = root.getChild("nose");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.leftLeg = root.getChild("left_leg");
        this.leftFingers = root.getChild("left_fingers");
        this.rightLeg = root.getChild("right_leg");
        this.rightFingers = root.getChild("right_fingers");
        this.leftRearLeg = root.getChild("left_rear_leg");
        this.rightRearLeg = root.getChild("right_rear_leg");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(0, 25).addBox(-1F, 0F, -4F, 2, 2, 3),
                PartPose.offsetAndRotation(0F, 20F, -6F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 18).addBox(-3F, -2F, -2F, 6, 4, 3),
                PartPose.offset(0F, 20F, -6F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, 0F, 10, 6, 10),
                PartPose.offset(0F, 17F, -6F));
        root.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(18, 16).addBox(-4F, -3F, 0F, 8, 5, 4),
                PartPose.offset(0F, 21F, 4F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(52, 8).addBox(-0.5F, 0F, 1F, 1, 1, 5),
                PartPose.offsetAndRotation(0F, 21F, 6F, -0.3490659F, 0F, 0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(10, 25).addBox(0F, -2F, -1F, 6, 4, 2),
                PartPose.offsetAndRotation(4F, 21F, -4F, 0F, 0F, 0.2268928F));
        root.addOrReplaceChild("left_fingers",
                CubeListBuilder.create().texOffs(44, 8).addBox(5F, -2F, 1F, 1, 4, 1),
                PartPose.offsetAndRotation(4F, 21F, -4F, 0F, 0F, 0.2268928F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(26, 25).addBox(-6F, -2F, -1F, 6, 4, 2),
                PartPose.offsetAndRotation(-4F, 21F, -4F, 0F, 0F, -0.2268928F));
        root.addOrReplaceChild("right_fingers",
                CubeListBuilder.create().texOffs(48, 8).addBox(-6F, -2F, 1F, 1, 4, 1),
                PartPose.offsetAndRotation(-4F, 21F, -4F, 0F, 0F, -0.2268928F));
        root.addOrReplaceChild("left_rear_leg",
                CubeListBuilder.create().texOffs(36, 0).addBox(0F, -2F, -1F, 2, 3, 5),
                PartPose.offsetAndRotation(3F, 22F, 5F, -0.2792527F, 0.5235988F, 0F));
        root.addOrReplaceChild("right_rear_leg",
                CubeListBuilder.create().texOffs(50, 0).addBox(-2F, -2F, -1F, 2, 3, 5),
                PartPose.offsetAndRotation(-3F, 22F, 5F, -0.2792527F, -0.5235988F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T mole, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw / ModelAnimations.DEGREES_PER_RADIAN;
        this.head.xRot = headPitch / ModelAnimations.DEGREES_PER_RADIAN;
        this.nose.xRot = 0.2617994F + this.head.xRot;
        this.nose.yRot = this.head.yRot;

        float rightLegX = Mth.cos(limbSwing + Mth.PI) * 0.8F * limbSwingAmount;
        float leftLegX = Mth.cos(limbSwing) * 0.8F * limbSwingAmount;
        this.rightFingers.yRot = this.rightLeg.yRot = rightLegX;
        this.leftFingers.yRot = this.leftLeg.yRot = leftLegX;
        this.rightRearLeg.yRot = -0.5235988F + leftLegX;
        this.leftRearLeg.yRot = 0.5235988F + rightLegX;
        this.tail.zRot = this.leftLeg.xRot * 0.625F;
    }
}