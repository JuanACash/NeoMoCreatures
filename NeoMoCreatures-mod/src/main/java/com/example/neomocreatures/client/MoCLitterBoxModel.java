package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCLitterBoxEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MoCLitterBoxModel extends HierarchicalModel<MoCLitterBoxEntity> {

    private final ModelPart root;
    private final ModelPart table1;
    private final ModelPart table2;
    private final ModelPart table3;
    private final ModelPart table4;
    private final ModelPart bottom;
    private final ModelPart litter;
    private final ModelPart litterUsed;

    public MoCLitterBoxModel(ModelPart root) {
        this.root = root;
        this.table1 = root.getChild("table1");
        this.table2 = root.getChild("table2");
        this.table3 = root.getChild("table3");
        this.table4 = root.getChild("table4");
        this.bottom = root.getChild("bottom");
        this.litter = root.getChild("litter");
        this.litterUsed = root.getChild("litter_used");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("table1", CubeListBuilder.create().texOffs(30, 0).addBox(-8F, 0F, 7F, 16, 6, 1),
                PartPose.offset(0F, 18F, 0F));
        root.addOrReplaceChild("table3", CubeListBuilder.create().texOffs(30, 0).addBox(-8F, 18F, -8F, 16, 6, 1),
                PartPose.ZERO);
        root.addOrReplaceChild("table2", CubeListBuilder.create().texOffs(30, 0).addBox(-8F, -3F, 0F, 16, 6, 1),
                PartPose.offsetAndRotation(8F, 21F, 0F, 0F, 1.5708F, 0F));
        root.addOrReplaceChild("table4", CubeListBuilder.create().texOffs(30, 0).addBox(-8F, -3F, 0F, 16, 6, 1),
                PartPose.offsetAndRotation(-9F, 21F, 0F, 0F, 1.5708F, 0F));
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(16, 15).addBox(-10F, 0F, -7F, 16, 1, 14),
                PartPose.offset(2F, 23F, 0F));

        root.addOrReplaceChild("litter", CubeListBuilder.create().texOffs(0, 15).addBox(0F, 0F, 0F, 16, 2, 14),
                PartPose.offset(-8F, 21F, -7F));
        root.addOrReplaceChild("litter_used", CubeListBuilder.create().texOffs(16, 15).addBox(0F, 0F, 0F, 16, 2, 14),
                PartPose.offset(-8F, 21F, -7F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCLitterBoxEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        boolean used = entity.isUsedLitter();
        litter.visible = !used;
        litterUsed.visible = used;
    }
}