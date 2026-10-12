package com.example.neomocreatures.client.legacy;

import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Simple blocky big cat body from legacy Mo' Creatures versions (ported from MoCLegacyModelBigCat2). */
public class MoCLegacyBigCatModel extends EntityModel<MoCBigCatEntity> {

    private static final float TAIL_REST_X_ROT = -0.5235988F;
    private static final float BODY_STANDING_X_ROT = 1.570796F;
    private static final float BODY_SITTING_X_ROT = 0.8726646F;

    private final ModelPart root;
    private final ModelPart snout;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart ears;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart collar;

    public MoCLegacyBigCatModel(ModelPart root) {
        this.root = root;
        this.snout = root.getChild("snout");
        this.tail = root.getChild("tail");
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.ears = root.getChild("ears");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.collar = root.getChild("collar");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        PartPose headPivot = PartPose.offset(0.0F, 4.0F, -8.0F);

        parts.addOrReplaceChild("ears", CubeListBuilder.create().texOffs(16, 25)
                .addBox(-4.0F, -7.0F, -3.0F, 8.0F, 4.0F, 1.0F), headPivot);
        parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F), headPivot);
        parts.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(14, 14)
                .addBox(-2.0F, 0.0F, -9.0F, 4.0F, 4.0F, 6.0F), headPivot);
        parts.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(24, 0)
                .addBox(-2.5F, 4.0F, -3.0F, 5.0F, 4.0F, 1.0F), headPivot);
        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 0)
                .addBox(-5.0F, -10.0F, -7.0F, 10.0F, 18.0F, 8.0F), PartPose.offset(0.0F, 5.0F, 2.0F));
        parts.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 15)
                .addBox(-5.0F, -5.0F, -2.0F, 3.0F, 3.0F, 14.0F),
                PartPose.offsetAndRotation(3.5F, 9.3F, 9.0F, TAIL_REST_X_ROT, 0.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        parts.addOrReplaceChild("leg1", leg, PartPose.offset(-3.0F, 12.0F, 7.0F));
        parts.addOrReplaceChild("leg2", leg, PartPose.offset(3.0F, 12.0F, 7.0F));
        parts.addOrReplaceChild("leg3", leg, PartPose.offset(-3.0F, 12.0F, -5.0F));
        parts.addOrReplaceChild("leg4", leg, PartPose.offset(3.0F, 12.0F, -5.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(MoCBigCatEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.leg1.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        this.leg3.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        this.leg4.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        for (ModelPart headPart : new ModelPart[]{this.snout, this.ears, this.collar}) {
            headPart.xRot = this.head.xRot;
            headPart.yRot = this.head.yRot;
        }
        this.collar.visible = entity.isTame();

        if (entity.isSittingSynced()) {
            applySittingPose();
        } else {
            applyStandingPose(limbSwing, limbSwingAmount);
        }
    }

    private void applyStandingPose(float limbSwing, float limbSwingAmount) {
        this.body.setPos(0.0F, 5.0F, 2.0F);
        this.body.xRot = BODY_STANDING_X_ROT;
        this.leg1.setPos(-3.0F, 12.0F, 7.0F);
        this.leg2.setPos(3.0F, 12.0F, 7.0F);
        this.leg3.setPos(-3.0F, 12.0F, -5.0F);
        this.leg4.setPos(3.0F, 12.0F, -5.0F);
        this.tail.setPos(3.5F, 9.3F, 9.0F);
        this.tail.xRot = TAIL_REST_X_ROT;
        this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
    }

    private void applySittingPose() {
        this.body.setPos(0.0F, 12.0F, 1.0F);
        this.body.xRot = BODY_SITTING_X_ROT;
        this.leg1.setPos(-5.0F, 12.0F, 0.0F);
        this.leg2.setPos(5.0F, 12.0F, 0.0F);
        this.leg3.setPos(-2.0F, 12.0F, -8.0F);
        this.leg4.setPos(2.0F, 12.0F, -8.0F);
        this.tail.setPos(3.5F, 22.0F, 8.0F);
        this.tail.xRot = -0.1745329F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}