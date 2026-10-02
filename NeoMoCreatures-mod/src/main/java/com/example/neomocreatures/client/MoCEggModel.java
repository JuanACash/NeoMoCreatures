package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.egg.MoCEggEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** 1:1 port of drzhark.mocreatures.client.model.MoCModelEgg — five static boxes, no animation. */
public class MoCEggModel extends HierarchicalModel<MoCEggEntity> {

    private final ModelPart root;

    public MoCEggModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("egg1",
                CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 3, 3, 3),
                PartPose.offset(0F, 20F, 0F));
        root.addOrReplaceChild("egg2",
                CubeListBuilder.create().texOffs(10, 0).addBox(0F, 0F, 0F, 2, 1, 2),
                PartPose.offset(0.5F, 19.5F, 0.5F));
        root.addOrReplaceChild("egg3",
                CubeListBuilder.create().texOffs(30, 0).addBox(0F, 0F, 0F, 2, 1, 2),
                PartPose.offset(0.5F, 22.5F, 0.5F));
        root.addOrReplaceChild("egg4",
                CubeListBuilder.create().texOffs(24, 0).addBox(0F, 0F, 0F, 1, 2, 2),
                PartPose.offset(-0.5F, 20.5F, 0.5F));
        root.addOrReplaceChild("egg5",
                CubeListBuilder.create().texOffs(18, 0).addBox(0F, 0F, 0F, 1, 2, 2),
                PartPose.offset(2.5F, 20.5F, 0.5F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCEggEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        // Static object, nothing to animate — matches the original.
    }
}