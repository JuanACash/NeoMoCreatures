package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCTurkeyEntity;

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

public class MoCTurkeyModel extends HierarchicalModel<MoCTurkeyEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart legRight;
    private final ModelPart footRight;
    private final ModelPart legLeft;
    private final ModelPart footLeft;
    private final ModelPart wingRight;
    private final ModelPart wingLeft;
    private final ModelPart uBody;
    private final ModelPart body;
    private final ModelPart chest;
    private final ModelPart tail;

    public MoCTurkeyModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.legRight = root.getChild("leg_right");
        this.footRight = root.getChild("foot_right");
        this.legLeft = root.getChild("leg_left");
        this.footLeft = root.getChild("foot_left");
        this.wingRight = root.getChild("wing_right");
        this.wingLeft = root.getChild("wing_left");
        this.uBody = root.getChild("u_body");
        this.body = root.getChild("body");
        this.chest = root.getChild("chest");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 27).addBox(-1F, -2F, -2F, 2, 2, 3),
                PartPose.offsetAndRotation(0F, 9.7F, -5.1F, 0.4833219F, 0F, 0F));

        head.addOrReplaceChild("beak",
                CubeListBuilder.create().texOffs(17, 17).addBox(-0.5F, -1.866667F, -3.366667F, 1, 1, 2),
                PartPose.rotation(0.2974F, 0F, 0F));

        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 32).addBox(-1F, -6F, -1F, 2, 6, 2),
                PartPose.offsetAndRotation(0F, 14.7F, -6.5F, -0.2246208F, 0F, 0F));

        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 17).addBox(-3F, 0F, -4F, 6, 6, 4),
                PartPose.offsetAndRotation(0F, 12.5F, -4F, 0.5934119F, 0F, 0F));

        root.addOrReplaceChild("wing_right",
                CubeListBuilder.create().texOffs(32, 30).addBox(-1F, -2F, 0F, 1, 6, 7),
                PartPose.offsetAndRotation(-4F, 14F, -3F, -0.3346075F, 0F, 0F));

        root.addOrReplaceChild("wing_left",
                CubeListBuilder.create().texOffs(48, 30).addBox(0F, -2F, 0F, 1, 6, 7),
                PartPose.offsetAndRotation(4F, 14F, -3F, -0.3346075F, 0F, 0F));

        root.addOrReplaceChild("u_body",
                CubeListBuilder.create().texOffs(34, 0).addBox(-2.5F, -4F, 0F, 5, 7, 9),
                PartPose.offset(0F, 15F, -3F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4F, 0F, 8, 8, 9),
                PartPose.offset(0F, 16F, -4F));

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(32, 17).addBox(-8F, -9F, 0F, 16, 12, 0),
                PartPose.offsetAndRotation(0F, 14F, 6F, -0.2974289F, 0F, 0F));

        root.addOrReplaceChild("leg_right",
                CubeListBuilder.create().texOffs(27, 17).addBox(-0.5F, 0F, -0.5F, 1, 5, 1),
                PartPose.offset(-2F, 19F, 0.5F));
        root.addOrReplaceChild("foot_right",
                CubeListBuilder.create().texOffs(20, 23).addBox(-1.5F, 5F, -2.5F, 3, 0, 3),
                PartPose.offset(-2F, 19F, 0.5F));

        root.addOrReplaceChild("leg_left",
                CubeListBuilder.create().texOffs(23, 17).addBox(-0.5F, 0F, -0.5F, 1, 5, 1),
                PartPose.offset(2F, 19F, 0.5F));
        root.addOrReplaceChild("foot_left",
                CubeListBuilder.create().texOffs(20, 26).addBox(-1.5F, 5F, -2.5F, 3, 0, 3),
                PartPose.offset(2F, 19F, 0.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCTurkeyEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float leftLegRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        float rightLegRot = Mth.cos((limbSwing * 0.6662F) + (float) Math.PI) * 1.4F * limbSwingAmount;
        float wingFlap = (Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount) / 4F;

        this.head.xRot = 0.4833219F + headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);

        this.legLeft.xRot = leftLegRot;
        this.footLeft.xRot = leftLegRot;
        this.legRight.xRot = rightLegRot;
        this.footRight.xRot = rightLegRot;

        this.wingLeft.yRot = wingFlap;
        this.wingRight.yRot = -wingFlap;


        boolean maleAdult = entity.isMale() && !entity.isBaby();
        this.uBody.visible = maleAdult;

        if (maleAdult) {
            this.tail.xRot = -0.2974289F + wingFlap;
            this.tail.y = 14F;
            this.tail.z = 6F;
            this.chest.y = 12.5F;
            this.body.y = 16F;
            this.wingLeft.x = 4F;
            this.wingRight.x = -4F;
            this.body.xScale = 1F;
            this.body.yScale = 1F;
            this.chest.xScale = 1F;
            this.chest.yScale = 1F;
        } else {
            this.tail.xRot = wingFlap - (110F / 57.29578F);
            this.tail.y = 17F;
            this.tail.z = 7F;
            this.chest.y = 16F * 0.8F;
            this.body.y = 20F * 0.8F;
            this.wingLeft.x = 3.2F;
            this.wingRight.x = -3.2F;
            this.body.xScale = 0.8F;
            this.body.yScale = 0.8F;
            this.chest.xScale = 0.8F;
            this.chest.yScale = 0.8F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}