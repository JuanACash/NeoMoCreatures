package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMaggotEntity;
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

/** Port of {@code MoCModelMaggot}: no limb animation of its own — the whole body squashes and stretches
 *  along its length in time with its walk cycle instead. */
public class MoCMaggotModel<T extends MoCMaggotEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailTip;

    public MoCMaggotModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("Head");
        this.body = root.getChild("Body");
        this.tail = root.getChild("Tail");
        this.tailTip = root.getChild("Tailtip");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("Head",
                CubeListBuilder.create().texOffs(0, 11).addBox(-1F, -1F, -2F, 2, 2, 2),
                PartPose.offset(0F, 23F, -2F));
        root.addOrReplaceChild("Body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2F, 0F, 3, 3, 4),
                PartPose.offset(0F, 23F, -2F));
        root.addOrReplaceChild("Tail",
                CubeListBuilder.create().texOffs(0, 7).addBox(-1F, -1F, 0F, 2, 2, 2),
                PartPose.offset(0F, 23F, 2F));
        root.addOrReplaceChild("Tailtip",
                CubeListBuilder.create().texOffs(8, 7).addBox(-0.5F, 0F, 0F, 1, 1, 1),
                PartPose.offset(0F, 23F, 4F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T maggot, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float stretch = -Mth.cos(limbSwing * 3.0F) * limbSwingAmount * 2.0F;
        this.root.zScale = 1.0F + stretch;
    }


}
