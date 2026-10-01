package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCSnailEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code MoCModelSnail}: while hiding, only the shell is drawn; otherwise the body, with the
 *  tail sliding back and forth while it moves. */
public class MoCSnailModel<T extends MoCSnailEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart antenna;
    private final ModelPart body;
    private final ModelPart shellUp;
    private final ModelPart shellDown;
    private final ModelPart tail;

    public MoCSnailModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.antenna = root.getChild("antenna");
        this.body = root.getChild("body");
        this.shellUp = root.getChild("shellUp");
        this.shellDown = root.getChild("shellDown");
        this.tail = root.getChild("tail");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 6).addBox(-1F, 0F, -1F, 2, 2, 2),
                PartPose.offsetAndRotation(0F, 21.8F, -1F, -0.4537856F, 0F, 0F));
        root.addOrReplaceChild("antenna",
                CubeListBuilder.create().texOffs(8, 0).addBox(-1.5F, 0F, -1F, 3, 2, 0),
                PartPose.offsetAndRotation(0F, 19.4F, -1F, 0.0523599F, 0F, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2, 2, 4),
                PartPose.offset(0F, 22F, 0F));
        root.addOrReplaceChild("shellUp",
                CubeListBuilder.create().texOffs(12, 0).addBox(-1F, -3F, 0F, 2, 3, 3),
                PartPose.offsetAndRotation(0F, 22.3F, -0.2F, 0.2268928F, 0F, 0F));
        root.addOrReplaceChild("shellDown",
                CubeListBuilder.create().texOffs(12, 0).addBox(-1F, 0F, 0F, 2, 3, 3),
                PartPose.offset(0F, 21F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(1, 2).addBox(-1F, 0F, 0F, 2, 1, 3),
                PartPose.offset(0F, 23F, 3F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T snail, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float tailMov = Mth.cos(ageInTicks * 0.3F) * 0.8F;
        if (limbSwingAmount < 0.1F) {
            tailMov = 0.0F;
        }
        this.tail.setPos(0.0F, 23.0F, 3.0F + tailMov);
        this.shellUp.xRot = 0.2268928F + tailMov / 10.0F;

        boolean hidden = snail.isHiding() && !snail.isSlug();
        this.shellDown.visible = hidden;
        this.head.visible = !hidden;
        this.antenna.visible = !hidden;
        this.body.visible = !hidden;
        this.shellUp.visible = !hidden;
        this.tail.visible = !hidden;
    }


}
