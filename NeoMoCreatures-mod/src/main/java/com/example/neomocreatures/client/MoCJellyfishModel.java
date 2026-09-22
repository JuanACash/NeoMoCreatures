package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCJellyfishEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelJellyFish}: a domed bell with 4 short side
 * flaps and 12 trailing tentacles that all swing together with the swim animation.
 */
public class MoCJellyfishModel extends HierarchicalModel<MoCJellyfishEntity> {

    private static final String[] TENTACLE_NAMES = {
            "leg_small_1", "leg_c1", "leg_c2", "leg_c3",
            "leg_1", "leg_2", "leg_3", "leg_4", "leg_5", "leg_6", "leg_7", "leg_8", "leg_9"
    };

    private final ModelPart root;
    private final ModelPart[] tentacles = new ModelPart[TENTACLE_NAMES.length];

    public MoCJellyfishModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < TENTACLE_NAMES.length; i++) {
            this.tentacles[i] = root.getChild(TENTACLE_NAMES[i]);
        }
    }

    /** The texture layout is 64x16. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("top",
                CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, 0F, -2.5F, 5, 1, 5),
                PartPose.offset(0F, 11F, 0F));
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, 0F, -4F, 8, 2, 8),
                PartPose.offset(0F, 12F, 0F));
        root.addOrReplaceChild("head_small",
                CubeListBuilder.create().texOffs(24, 0).addBox(-2F, 0F, -2F, 4, 3, 4),
                PartPose.offset(0F, 12.5F, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(36, 0).addBox(-3.5F, 0F, -3.5F, 7, 7, 7),
                PartPose.offset(0F, 13.8F, 0F));
        root.addOrReplaceChild("body_center",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 3, 2),
                PartPose.offset(0F, 15.5F, 0F));
        root.addOrReplaceChild("body_bottom",
                CubeListBuilder.create().texOffs(20, 10).addBox(-2F, 0F, -2F, 4, 2, 4),
                PartPose.offset(0F, 18.3F, 0F));

        root.addOrReplaceChild("side_1",
                CubeListBuilder.create().texOffs(20, 10).addBox(-2F, 5F, 0F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 12.5F, 0F, -0.7679449F, 0F, 0F));
        root.addOrReplaceChild("side_2",
                CubeListBuilder.create().texOffs(20, 10).addBox(-4F, 5F, -2F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 12.5F, 0F, 0F, 0F, -0.7679449F));
        root.addOrReplaceChild("side_3",
                CubeListBuilder.create().texOffs(20, 10).addBox(0F, 5F, -2F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 12.5F, 0F, 0F, 0F, 0.7679449F));
        root.addOrReplaceChild("side_4",
                CubeListBuilder.create().texOffs(20, 10).addBox(-2F, 5F, -4F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 12.5F, 0F, 0.7679449F, 0F, 0F));

        root.addOrReplaceChild("leg_small_1",
                CubeListBuilder.create().texOffs(60, 2).addBox(-1F, 0F, -1F, 1, 3, 1),
                PartPose.offset(0F, 18.5F, 0F));
        root.addOrReplaceChild("leg_c1",
                CubeListBuilder.create().texOffs(15, 10).addBox(-1F, 0F, -1F, 1, 4, 1),
                PartPose.offsetAndRotation(-0.5F, 15.5F, -0.5F, -0.2602503F, 0F, 0.1487144F));
        root.addOrReplaceChild("leg_c2",
                CubeListBuilder.create().texOffs(15, 10).addBox(-1F, 0F, 0F, 1, 4, 1),
                PartPose.offsetAndRotation(0.5F, 15.5F, -0.5F, 0.1487144F, 1.747395F, 0F));
        root.addOrReplaceChild("leg_c3",
                CubeListBuilder.create().texOffs(15, 10).addBox(-1F, 0F, 0F, 1, 4, 1),
                PartPose.offsetAndRotation(-0.5F, 15.5F, 0.5F, 0.1115358F, 0.3717861F, 0.2230717F));

        root.addOrReplaceChild("leg_1",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offset(0F, 20F, 2.5F));
        root.addOrReplaceChild("leg_2",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offset(0F, 20F, -2.5F));
        root.addOrReplaceChild("leg_3",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offset(2.5F, 20F, 0F));
        root.addOrReplaceChild("leg_4",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offset(-2.5F, 20F, 0F));
        root.addOrReplaceChild("leg_5",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(2F, 20F, 2F, 0F, 0.7853982F, 0F));
        root.addOrReplaceChild("leg_6",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(2F, 20F, -2F, 0F, 0.7853982F, 0F));
        root.addOrReplaceChild("leg_7",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(-2F, 20F, -2F, 0F, 0.7853982F, 0F));
        root.addOrReplaceChild("leg_8",
                CubeListBuilder.create().texOffs(60, 0).addBox(0F, 0F, 0F, 1, 5, 1),
                PartPose.offset(0F, 18.5F, 0F));
        root.addOrReplaceChild("leg_9",
                CubeListBuilder.create().texOffs(0, 10).addBox(-0.5F, 0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(-2F, 20F, 2F, 0F, 0.7853982F, 0F));

        return LayerDefinition.create(mesh, 64, 16);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCJellyfishEntity jellyfish, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float swing = Math.min(limbSwingAmount * 2.0F, 1.0F);
        for (ModelPart tentacle : this.tentacles) {
            tentacle.xRot = swing;
        }
    }
}