package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.entity.MoCSharkEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Blocky shark from legacy Mo' Creatures versions (ported from MoCLegacyModelShark). */
public class MoCLegacySharkModel extends HierarchicalModel<MoCSharkEntity> {

    private final ModelPart root;
    private final ModelPart upperTailFin;
    private final ModelPart lowerTailFin;

    public MoCLegacySharkModel(ModelPart root) {
        this.root = root;
        this.upperTailFin = root.getChild("upper_tail_fin");
        this.lowerTailFin = root.getChild("lower_tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(6, 6)
                .addBox(0.0F, 0.0F, 0.0F, 6.0F, 8.0F, 18.0F), PartPose.offset(-3.0F, 17.0F, -9.0F));
        parts.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(36, 8)
                .addBox(0.0F, 0.0F, 0.0F, 4.0F, 6.0F, 10.0F), PartPose.offset(-2.0F, 18.0F, 9.0F));

        parts.addOrReplaceChild("upper_head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(0.0F, 0.0F, 0.0F, 5.0F, 2.0F, 8.0F),
                PartPose.offsetAndRotation(-2.5F, 21.0F, -15.5F, 0.5235988F, 0.0F, 0.0F));
        parts.addOrReplaceChild("lower_head", CubeListBuilder.create().texOffs(44, 0)
                .addBox(0.0F, 0.0F, 0.0F, 5.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(-2.5F, 21.5F, -12.5F, -0.261799F, 0.0F, 0.0F));
        parts.addOrReplaceChild("right_head", CubeListBuilder.create().texOffs(0, 3)
                .addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(-2.45F, 21.3F, -12.85F, 0.7853981F, 0.0F, 0.0F));
        parts.addOrReplaceChild("left_head", CubeListBuilder.create().texOffs(0, 3)
                .addBox(0.0F, 0.0F, 0.0F, 1.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(1.45F, 21.3F, -12.8F, 0.7853981F, 0.0F, 0.0F));

        parts.addOrReplaceChild("upper_fin", CubeListBuilder.create().texOffs(6, 12)
                .addBox(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(-0.5F, 17.0F, 0.0F, 0.7853981F, 0.0F, 0.0F));
        parts.addOrReplaceChild("upper_tail_fin", CubeListBuilder.create().texOffs(6, 12)
                .addBox(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 8.0F),
                PartPose.offsetAndRotation(-0.5F, 18.0F, 17.0F, 0.5235988F, 0.0F, 0.0F));
        parts.addOrReplaceChild("lower_tail_fin", CubeListBuilder.create().texOffs(8, 14)
                .addBox(0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(-0.5F, 21.0F, 19.0F, -0.7853981F, 0.0F, 0.0F));
        parts.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(18, 0)
                .addBox(0.0F, 0.0F, 0.0F, 8.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(3.0F, 24.0F, -4.0F, 0.0F, -0.5235988F, 0.5235988F));
        parts.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(18, 0)
                .addBox(0.0F, 0.0F, 0.0F, 8.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(-9.0F, 27.5F, 0.0F, 0.0F, 0.5235988F, -0.5235988F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCSharkEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float tailSway = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.upperTailFin.yRot = tailSway;
        this.lowerTailFin.yRot = tailSway;
    }
}