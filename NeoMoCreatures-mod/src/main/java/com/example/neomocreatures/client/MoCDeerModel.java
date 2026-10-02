package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCDeerEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelDeer}. Faithful to the original: only the
 * legs animate — the head, neck, ears and antlers never turn to track the player, even though the
 * entity does register a look-at-player goal. Antlers are also part of the one shared model, so a
 * doe or fawn shows them too, exactly like the source.
 */
public class MoCDeerModel<T extends MoCDeerEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public MoCDeerModel(ModelPart root) {
        this.root = root;
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -6F, -9.5F, 3, 3, 6),
                PartPose.offset(1F, 11.5F, -4.5F));
        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2F, -2F, -6F, 4, 4, 6),
                PartPose.offsetAndRotation(1F, 11.5F, -4.5F, -0.7853981F, 0F, 0F));
        root.addOrReplaceChild("l_ear",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -7.5F, -5F, 2, 3, 1),
                PartPose.offsetAndRotation(1F, 11.5F, -4.5F, 0F, 0F, 0.7853981F));
        root.addOrReplaceChild("r_ear",
                CubeListBuilder.create().texOffs(0, 0).addBox(2F, -7.5F, -5F, 2, 3, 1),
                PartPose.offsetAndRotation(1F, 11.5F, -4.5F, 0F, 0F, -0.7853981F));
        root.addOrReplaceChild("left_antler",
                CubeListBuilder.create().texOffs(54, 0).addBox(0F, -14F, -7F, 1, 8, 4),
                PartPose.offsetAndRotation(1F, 11.5F, -4.5F, 0F, 0F, 0.2094395F));
        root.addOrReplaceChild("right_antler",
                CubeListBuilder.create().texOffs(54, 0).addBox(0F, -14F, -7F, 1, 8, 4),
                PartPose.offsetAndRotation(1F, 11.5F, -4.5F, 0F, 0F, -0.2094395F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(24, 12).addBox(-2F, -3F, -6F, 6, 6, 14),
                PartPose.offset(0F, 13F, 0F));
        root.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(9, 20).addBox(-1F, 0F, -1F, 2, 8, 2),
                PartPose.offset(3F, 16F, -4F));
        root.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(0, 20).addBox(-1F, 0F, -1F, 2, 8, 2),
                PartPose.offset(-1F, 16F, -4F));
        root.addOrReplaceChild("leg3",
                CubeListBuilder.create().texOffs(9, 20).addBox(-1F, 0F, -1F, 2, 8, 2),
                PartPose.offset(3F, 16F, 6F));
        root.addOrReplaceChild("leg4",
                CubeListBuilder.create().texOffs(0, 20).addBox(-1F, 0F, -1F, 2, 8, 2),
                PartPose.offset(-1F, 16F, 6F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(50, 20).addBox(-1.5F, -1F, 0F, 3, 2, 4),
                PartPose.offsetAndRotation(1F, 11F, 7F, 0.7854F, 0F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T deer, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.leg1.xRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 1.4F);
        this.leg2.xRot = ModelAnimations.walkSwingOpposite(limbSwing, limbSwingAmount, 1.4F);
        this.leg3.xRot = ModelAnimations.walkSwingOpposite(limbSwing, limbSwingAmount, 1.4F);
        this.leg4.xRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 1.4F);
    }
}