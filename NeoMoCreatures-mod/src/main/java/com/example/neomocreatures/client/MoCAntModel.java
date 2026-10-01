package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCAntEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code MoCModelAnt}: only the legs animate. */
public class MoCAntModel<T extends MoCAntEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart rightAntenna;
    private final ModelPart leftAntenna;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart midLegs;
    private final ModelPart frontLegs;
    private final ModelPart rearLegs;

    public MoCAntModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.mouth = root.getChild("mouth");
        this.rightAntenna = root.getChild("right_antenna");
        this.leftAntenna = root.getChild("left_antenna");
        this.thorax = root.getChild("thorax");
        this.abdomen = root.getChild("abdomen");
        this.midLegs = root.getChild("mid_legs");
        this.frontLegs = root.getChild("front_legs");
        this.rearLegs = root.getChild("rear_legs");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 11).addBox(-0.5F, 0F, 0F, 1, 1, 1),
                PartPose.offsetAndRotation(0F, 21.9F, -1.3F, -2.171231F, 0F, 0F));
        root.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(8, 10).addBox(0F, 0F, 0F, 2, 1, 0),
                PartPose.offsetAndRotation(-1F, 22.3F, -1.9F, -0.8286699F, 0F, 0F));
        root.addOrReplaceChild("right_antenna",
                CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, 0F, -1F, 1, 0, 1),
                PartPose.offsetAndRotation(-0.5F, 21.7F, -2.3F, -1.041001F, 0.7853982F, 0F));
        root.addOrReplaceChild("left_antenna",
                CubeListBuilder.create().texOffs(4, 6).addBox(-0.5F, 0F, -1F, 1, 0, 1),
                PartPose.offsetAndRotation(0.5F, 21.7F, -2.3F, -1.041001F, -0.7853982F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.5F, -1F, 1, 1, 2),
                PartPose.offset(0F, 20F, -0.5F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(8, 1).addBox(-0.5F, -0.2F, -1F, 1, 2, 1),
                PartPose.offsetAndRotation(0F, 21.5F, 0.3F, 1.706911F, 0F, 0F));
        root.addOrReplaceChild("mid_legs",
                CubeListBuilder.create().texOffs(4, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22F, -0.7F, 0.5948578F, 0F, 0F));
        root.addOrReplaceChild("front_legs",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22F, -0.8F, -0.6192304F, 0F, 0F));
        root.addOrReplaceChild("rear_legs",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22F, 0F, 0.9136644F, 0F, 0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T ant, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float legMov = Mth.cos(limbSwing + Mth.PI) * limbSwingAmount;
        float legMovB = Mth.cos(limbSwing) * limbSwingAmount;
        this.frontLegs.xRot = -0.6192304F + legMov;
        this.midLegs.xRot = 0.5948578F + legMovB;
        this.rearLegs.xRot = 0.9136644F + legMov;
    }


}
