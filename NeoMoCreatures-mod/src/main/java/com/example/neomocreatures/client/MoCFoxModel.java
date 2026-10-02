package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCFoxEntity;


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelFox (Techne) to
 * HierarchicalModel. Snout and ears are nested under head here (they weren't
 * in the original, which manually copied Head's rotation onto them every
 * tick) so the hierarchy handles that for free.
 */
public class MoCFoxModel extends HierarchicalModel<MoCFoxEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public MoCFoxModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(1F, 0F, 0F, 6, 6, 12),
                PartPose.offset(-4F, 10F, -6F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 20).addBox(-2F, -3F, -4F, 6, 6, 4),
                PartPose.offset(-1F, 11F, -6F));

        head.addOrReplaceChild("snout",
                CubeListBuilder.create().texOffs(20, 20).addBox(0F, 1F, -7F, 2, 2, 4),
                PartPose.ZERO);

        head.addOrReplaceChild("ears",
                CubeListBuilder.create().texOffs(50, 20).addBox(-2F, -6F, -2F, 6, 4, 1),
                PartPose.ZERO);

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(32, 20).addBox(-4F, -5F, -2F, 3, 3, 8),
                PartPose.offsetAndRotation(2.5F, 15F, 5F, -0.5235988F, 0F, 0F));

        final int legHeight = 8;
        root.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -2F, 3, legHeight, 3),
                PartPose.offset(-2F, 24 - legHeight, 5F));
        root.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -2F, 3, legHeight, 3),
                PartPose.offset(1F, 24 - legHeight, 5F));
        root.addOrReplaceChild("leg3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -2F, 3, legHeight, 3),
                PartPose.offset(-2F, 24 - legHeight, -4F));
        root.addOrReplaceChild("leg4",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -2F, 3, legHeight, 3),
                PartPose.offset(1F, 24 - legHeight, -4F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCFoxEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        float legAngle = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 1.4F);
        float legAngleOpposite = Mth.cos((limbSwing * 0.6662F) + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg1.xRot = legAngle;
        this.leg2.xRot = legAngleOpposite;
        this.leg3.xRot = legAngleOpposite;
        this.leg4.xRot = legAngle;
    }
}