package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFishyEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelFishy}: a diamond-shaped body and a tail
 * that sways with the swimming animation.
 */
public class MoCFishyModel extends HierarchicalModel<MoCFishyEntity> {

    private static final float BODY_TILT = Mth.PI / 4.0F;
    private static final float TAIL_SWAY_SPEED = 0.6662F;
    private static final float TAIL_SWAY = 1.4F;

    private final ModelPart root;
    private final ModelPart tail;

    public MoCFishyModel(ModelPart root) {
        this.root = root;
        this.tail = root.getChild("tail");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, -3.5F, 1, 5, 5),
                PartPose.offsetAndRotation(0F, 18F, -1F, BODY_TILT, 0F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(12, 0).addBox(0F, 0F, 0F, 1, 3, 3),
                PartPose.offsetAndRotation(0F, 20.5F, 3F, BODY_TILT, 0F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCFishyEntity fishy, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.tail.yRot = Mth.cos(limbSwing * TAIL_SWAY_SPEED) * TAIL_SWAY * limbSwingAmount;
    }
}