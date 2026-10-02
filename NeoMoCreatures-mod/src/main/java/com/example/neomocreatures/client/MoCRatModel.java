package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCRatEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelRat}. */
public class MoCRatModel<T extends MoCRatEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart earR;
    private final ModelPart earL;
    private final ModelPart whiskerR;
    private final ModelPart whiskerL;
    private final ModelPart tail;
    private final ModelPart frontL;
    private final ModelPart frontR;
    private final ModelPart rearL;
    private final ModelPart rearR;

    public MoCRatModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.earR = root.getChild("ear_r");
        this.earL = root.getChild("ear_l");
        this.whiskerR = root.getChild("whisker_r");
        this.whiskerL = root.getChild("whisker_l");
        this.tail = root.getChild("tail");
        this.frontL = root.getChild("front_l");
        this.frontR = root.getChild("front_r");
        this.rearL = root.getChild("rear_l");
        this.rearR = root.getChild("rear_r");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1F, -6F, 3, 4, 6),
                PartPose.offset(0F, 19F, -6F));
        root.addOrReplaceChild("ear_r",
                CubeListBuilder.create().texOffs(16, 26).addBox(-3.5F, -3F, -2F, 3, 3, 1),
                PartPose.offset(0F, 19F, -6F));
        root.addOrReplaceChild("ear_l",
                CubeListBuilder.create().texOffs(24, 26).addBox(0.5F, -3F, -2F, 3, 3, 1),
                PartPose.offset(0F, 19F, -6F));
        root.addOrReplaceChild("whisker_r",
                CubeListBuilder.create().texOffs(24, 16).addBox(-4.5F, -1F, -6F, 3, 3, 1),
                PartPose.offset(0F, 19F, -6F));
        root.addOrReplaceChild("whisker_l",
                CubeListBuilder.create().texOffs(24, 20).addBox(1.5F, -1F, -6F, 3, 3, 1),
                PartPose.offset(0F, 19F, -6F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(24, 0).addBox(-4F, -3F, -3F, 8, 8, 8),
                PartPose.offsetAndRotation(0F, 20F, 2F, Mth.HALF_PI, 0F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(56, 0).addBox(-1F, 0F, -1F, 2, 18, 2),
                PartPose.offsetAndRotation(0F, 20F, 7F, Mth.HALF_PI, 0F, 0F));
        root.addOrReplaceChild("front_l",
                CubeListBuilder.create().texOffs(0, 18).addBox(-2F, 0F, -3F, 2, 1, 4),
                PartPose.offset(3F, 23F, -5F));
        root.addOrReplaceChild("front_r",
                CubeListBuilder.create().texOffs(0, 18).addBox(0F, 0F, -3F, 2, 1, 4),
                PartPose.offset(-3F, 23F, -5F));
        root.addOrReplaceChild("rear_l",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2F, 0F, -4F, 2, 1, 5),
                PartPose.offset(4F, 23F, 4F));
        root.addOrReplaceChild("rear_r",
                CubeListBuilder.create().texOffs(0, 24).addBox(0F, 0F, -4F, 2, 1, 5),
                PartPose.offset(-4F, 23F, 4F));
        root.addOrReplaceChild("body_f",
                CubeListBuilder.create().texOffs(32, 16).addBox(-3F, -3F, -7F, 6, 6, 6),
                PartPose.offset(0F, 20F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T rat, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        // Original: negative headPitch here — a real, confirmed quirk of this specific model.
        this.head.xRot = -headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.earR.xRot = this.head.xRot;
        this.earR.yRot = this.head.yRot;
        this.earL.xRot = this.head.xRot;
        this.earL.yRot = this.head.yRot;
        this.whiskerR.xRot = this.head.xRot;
        this.whiskerR.yRot = this.head.yRot;
        this.whiskerL.xRot = this.head.xRot;
        this.whiskerL.yRot = this.head.yRot;

        float frontLegX = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.6F);
        float rearLegX = ModelAnimations.walkSwingOpposite(limbSwing, limbSwingAmount, 0.8F);
        this.frontL.xRot = frontLegX;
        this.rearL.xRot = rearLegX;
        this.rearR.xRot = frontLegX;
        this.frontR.xRot = rearLegX;
        this.tail.yRot = this.frontL.xRot * 0.625F;
    }
}