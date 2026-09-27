package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCDuckEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelDuck}. */
public class MoCDuckModel<T extends MoCDuckEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart bill;
    private final ModelPart chin;
    private final ModelPart body;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public MoCDuckModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.bill = root.getChild("bill");
        this.chin = root.getChild("chin");
        this.body = root.getChild("body");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -6F, -2F, 4, 6, 3),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("bill",
                CubeListBuilder.create().texOffs(14, 0).addBox(-2F, -4F, -4F, 4, 2, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("chin",
                CubeListBuilder.create().texOffs(14, 4).addBox(-1F, -2F, -3F, 2, 2, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 9).addBox(-3F, -4F, -3F, 6, 8, 6),
                PartPose.offset(0F, 16F, 0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(26, 0).addBox(-1F, 0F, -3F, 3, 5, 3),
                PartPose.offset(-2F, 19F, 1F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(26, 0).addBox(-1F, 0F, -3F, 3, 5, 3),
                PartPose.offset(1F, 19F, 1F));
        root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(24, 13).addBox(0F, 0F, -3F, 1, 4, 6),
                PartPose.offset(-4F, 13F, 0F));
        root.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(24, 13).addBox(-1F, 0F, -3F, 1, 4, 6),
                PartPose.offset(4F, 13F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T duck, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.head.xRot = -headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.bill.xRot = this.head.xRot;
        this.bill.yRot = this.head.yRot;
        this.chin.xRot = this.head.xRot;
        this.chin.yRot = this.head.yRot;
        this.body.xRot = Mth.HALF_PI;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;

        if (!duck.onGround()) {
            float wingRot = Mth.cos(ageInTicks * 1.4F + Mth.PI) * 0.6F;
            this.rightWing.zRot = 0.5F + wingRot;
            this.leftWing.zRot = -0.5F - wingRot;
        } else {
            this.rightWing.zRot = 0.0F;
            this.leftWing.zRot = 0.0F;
        }
    }
}