package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCCricketEntity;
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

/** Port of {@code MoCModelCricket}: while airborne (falling) the hind legs are drawn in their stretched
 *  pose, otherwise folded. */
public class MoCCricketModel<T extends MoCCricketEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart antenna;
    private final ModelPart antennaB;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart tailA;
    private final ModelPart tailB;
    private final ModelPart frontLegs;
    private final ModelPart midLegs;
    private final ModelPart thighLeft;
    private final ModelPart thighLeftB;
    private final ModelPart thighRight;
    private final ModelPart thighRightB;
    private final ModelPart legLeft;
    private final ModelPart legLeftB;
    private final ModelPart legRight;
    private final ModelPart legRightB;

    public MoCCricketModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.antenna = root.getChild("antenna");
        this.antennaB = root.getChild("antennaB");
        this.thorax = root.getChild("thorax");
        this.abdomen = root.getChild("abdomen");
        this.tailA = root.getChild("tailA");
        this.tailB = root.getChild("tailB");
        this.frontLegs = root.getChild("frontLegs");
        this.midLegs = root.getChild("midLegs");
        this.thighLeft = root.getChild("thighLeft");
        this.thighLeftB = root.getChild("thighLeftB");
        this.thighRight = root.getChild("thighRight");
        this.thighRightB = root.getChild("thighRightB");
        this.legLeft = root.getChild("legLeft");
        this.legLeftB = root.getChild("legLeftB");
        this.legRight = root.getChild("legRight");
        this.legRightB = root.getChild("legRightB");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, 0F, -1F, 1, 1, 2),
                PartPose.offsetAndRotation(0F, 22.5F, -2F, -2.171231F, 0F, 0F));
        root.addOrReplaceChild("antenna",
                CubeListBuilder.create().texOffs(0, 11).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -3F, -2.736346F, 0F, 0F));
        root.addOrReplaceChild("antennaB",
                CubeListBuilder.create().texOffs(0, 9).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 20.7F, -3.8F, 2.88506F, 0F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offsetAndRotation(0F, 21F, -1F, 0F, 0F, 0F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(8, 0).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offsetAndRotation(0F, 22F, 0F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("tailA",
                CubeListBuilder.create().texOffs(4, 9).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22F, 2.8F, 1.308687F, 0F, 0F));
        root.addOrReplaceChild("tailB",
                CubeListBuilder.create().texOffs(4, 7).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, 2.8F, 1.665602F, 0F, 0F));
        root.addOrReplaceChild("frontLegs",
                CubeListBuilder.create().texOffs(0, 7).addBox(-1F, 0F, 0F, 2, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.8F, -0.8328009F, 0F, 0F));
        root.addOrReplaceChild("midLegs",
                CubeListBuilder.create().texOffs(0, 13).addBox(-2F, 0F, 0F, 4, 2, 0),
                PartPose.offsetAndRotation(0F, 23F, -1.2F, 1.070744F, 0F, 0F));
        root.addOrReplaceChild("thighLeft",
                CubeListBuilder.create().texOffs(8, 5).addBox(0F, -3F, 0F, 1, 3, 1),
                PartPose.offsetAndRotation(0.5F, 23F, 0F, -0.4886922F, 0.2617994F, 0F));
        root.addOrReplaceChild("thighLeftB",
                CubeListBuilder.create().texOffs(8, 5).addBox(0F, -3F, 0F, 1, 3, 1),
                PartPose.offsetAndRotation(0.5F, 22.5F, 0F, -1.762782F, 0F, 0F));
        root.addOrReplaceChild("thighRight",
                CubeListBuilder.create().texOffs(12, 5).addBox(-1F, -3F, 0F, 1, 3, 1),
                PartPose.offsetAndRotation(-0.5F, 23F, 0F, -0.4886922F, -0.2617994F, 0F));
        root.addOrReplaceChild("thighRightB",
                CubeListBuilder.create().texOffs(12, 5).addBox(-1F, -3F, 0F, 1, 3, 1),
                PartPose.offsetAndRotation(-0.5F, 22.5F, 0F, -1.762782F, 0F, 0F));
        root.addOrReplaceChild("legLeft",
                CubeListBuilder.create().texOffs(0, 15).addBox(0F, 0F, -1F, 0, 3, 2),
                PartPose.offsetAndRotation(2F, 21F, 2.5F, 0F, 0F, 0F));
        root.addOrReplaceChild("legLeftB",
                CubeListBuilder.create().texOffs(4, 15).addBox(0F, 0F, -1F, 0, 3, 2),
                PartPose.offsetAndRotation(1.5F, 23F, 2.9F, 1.249201F, 0F, 0F));
        root.addOrReplaceChild("legRight",
                CubeListBuilder.create().texOffs(4, 15).addBox(0F, 0F, -1F, 0, 3, 2),
                PartPose.offsetAndRotation(-2F, 21F, 2.5F, 0F, 0F, 0F));
        root.addOrReplaceChild("legRightB",
                CubeListBuilder.create().texOffs(4, 15).addBox(0F, 0F, -1F, 0, 3, 2),
                PartPose.offsetAndRotation(-1.5F, 23F, 2.9F, 1.249201F, 0F, 0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T cricket, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean flying = cricket.getDeltaMovement().y < -0.1D;

        float frontLegAdj = 0.0F;
        float legMov;
        float legMovB;
        if (flying) {
            legMov = limbSwingAmount * 1.5F;
            legMovB = legMov;
            frontLegAdj = 1.4F;
        } else {
            legMov = Mth.cos(limbSwing * 1.5F + Mth.PI) * 2.0F * limbSwingAmount;
            legMovB = Mth.cos(limbSwing * 1.5F) * 2.0F * limbSwingAmount;
        }
        this.antennaB.xRot = 2.88506F - legMov;
        this.frontLegs.xRot = -0.8328009F + frontLegAdj + legMov;
        this.midLegs.xRot = 1.070744F + legMovB;

        this.thighLeft.visible = !flying;
        this.thighRight.visible = !flying;
        this.legLeft.visible = !flying;
        this.legRight.visible = !flying;
        this.thighLeftB.visible = flying;
        this.thighRightB.visible = flying;
        this.legLeftB.visible = flying;
        this.legRightB.visible = flying;
    }


}
