package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCButterflyEntity;
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

/** Port of {@code MoCModelButterfly}: six wings, drawn 80% opaque while flying and solid (folded by
 *  their own rotation) while perched, which also gets an occasional slow wing twitch. */
public class MoCButterflyModel<T extends MoCButterflyEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart rightAntenna;
    private final ModelPart leftAntenna;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart frontLegs;
    private final ModelPart midLegs;
    private final ModelPart rearLegs;
    private final ModelPart wingLeftFront;
    private final ModelPart wingLeft;
    private final ModelPart wingLeftBack;
    private final ModelPart wingRightFront;
    private final ModelPart wingRight;
    private final ModelPart wingRightBack;
    private boolean flying;

    public MoCButterflyModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.mouth = root.getChild("mouth");
        this.rightAntenna = root.getChild("right_antenna");
        this.leftAntenna = root.getChild("left_antenna");
        this.thorax = root.getChild("thorax");
        this.abdomen = root.getChild("abdomen");
        this.frontLegs = root.getChild("front_legs");
        this.midLegs = root.getChild("mid_legs");
        this.rearLegs = root.getChild("rear_legs");
        this.wingLeftFront = root.getChild("wing_left_front");
        this.wingLeft = root.getChild("wing_left");
        this.wingLeftBack = root.getChild("wing_left_back");
        this.wingRightFront = root.getChild("wing_right_front");
        this.wingRight = root.getChild("wing_right");
        this.wingRightBack = root.getChild("wing_right_back");
    }

    /** The texture layout is 32x32 (the PNG itself is a 2x-resolution version). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 11).addBox(-0.5F, 0F, 0F, 1, 1, 1),
                PartPose.offsetAndRotation(0F, 21.9F, -1.3F, -2.171231F, 0F, 0F));
        root.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(0, 8).addBox(0F, 0F, 0F, 1, 2, 0),
                PartPose.offsetAndRotation(-0.2F, 22F, -2.5F, 0.6548599F, 0F, 0F));
        root.addOrReplaceChild("right_antenna",
                CubeListBuilder.create().texOffs(0, 7).addBox(-0.5F, 0F, -1F, 1, 0, 1),
                PartPose.offsetAndRotation(-0.5F, 21.7F, -2.3F, -1.041001F, 0.7853982F, 0F));
        root.addOrReplaceChild("left_antenna",
                CubeListBuilder.create().texOffs(4, 7).addBox(-0.5F, 0F, -1F, 1, 0, 1),
                PartPose.offsetAndRotation(0.5F, 21.7F, -2.3F, -1.041001F, -0.7853982F, 0F));
        root.addOrReplaceChild("thorax",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.5F, -1F, 1, 1, 2),
                PartPose.offsetAndRotation(0F, 20F, -1F, 0F, 0F, 0F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(8, 1).addBox(-0.5F, 0F, -1F, 1, 3, 1),
                PartPose.offsetAndRotation(0F, 21.5F, 0F, 1.427659F, 0F, 0F));
        root.addOrReplaceChild("front_legs",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 21.5F, -1.8F, 0.1487144F, 0F, 0F));
        root.addOrReplaceChild("mid_legs",
                CubeListBuilder.create().texOffs(4, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22F, -1.2F, 0.5948578F, 0F, 0F));
        root.addOrReplaceChild("rear_legs",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1F, 0F, 0F, 2, 3, 0),
                PartPose.offsetAndRotation(0F, 22.5F, -0.4F, 1.070744F, 0F, 0F));
        root.addOrReplaceChild("wing_left_front",
                CubeListBuilder.create().texOffs(4, 20).addBox(0F, 0F, -4F, 8, 0, 6),
                PartPose.offsetAndRotation(0.3F, 21.4F, -1F, 0F, 0F, 0F));
        root.addOrReplaceChild("wing_left",
                CubeListBuilder.create().texOffs(4, 26).addBox(0F, 0F, -1F, 8, 0, 6),
                PartPose.offsetAndRotation(0.3F, 21.5F, -0.5F, 0F, 0F, 0F));
        root.addOrReplaceChild("wing_left_back",
                CubeListBuilder.create().texOffs(4, 0).addBox(0F, 0F, -1F, 5, 0, 8),
                PartPose.offsetAndRotation(0.3F, 21.2F, -1F, 0F, 0F, 0.5934119F));
        root.addOrReplaceChild("wing_right_front",
                CubeListBuilder.create().texOffs(4, 8).addBox(-8F, 0F, -4F, 8, 0, 6),
                PartPose.offsetAndRotation(-0.3F, 21.4F, -1F, 0F, 0F, 0F));
        root.addOrReplaceChild("wing_right",
                CubeListBuilder.create().texOffs(4, 14).addBox(-8F, 0F, -1F, 8, 0, 6),
                PartPose.offsetAndRotation(-0.3F, 21.5F, -0.5F, 0F, 0F, 0F));
        root.addOrReplaceChild("wing_right_back",
                CubeListBuilder.create().texOffs(14, 0).addBox(-5F, 0F, -1F, 5, 0, 8),
                PartPose.offsetAndRotation(0.3F, 21.2F, -1F, 0F, 0F, -0.5934119F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T butterfly, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.flying = butterfly.isFlying() || butterfly.getDeltaMovement().y < -0.1D;

        float cycle = ageInTicks % 100.0F;
        float wingRot = 0.0F;
        float legMov;
        float legMovB;
        if (this.flying) {
            wingRot = Mth.cos(ageInTicks * 0.9F) * 0.9F;
            legMov = limbSwingAmount * 1.5F;
            legMovB = legMov;
        } else {
            legMov = Mth.cos(limbSwing * 1.5F + Mth.PI) * 2.0F * limbSwingAmount;
            legMovB = Mth.cos(limbSwing * 1.5F) * 2.0F * limbSwingAmount;
            if (cycle > 40.0F && cycle < 60.0F) {
                wingRot = Mth.cos(ageInTicks * 0.15F) * 0.9F;
            }
        }
        float baseAngle = 0.52359F;
        this.wingLeft.zRot = -baseAngle + wingRot;
        this.wingRight.zRot = baseAngle - wingRot;
        this.wingLeftFront.zRot = -baseAngle + wingRot;
        this.wingLeftBack.zRot = 0.5934119F - baseAngle + wingRot;
        this.wingRightFront.zRot = baseAngle - wingRot;
        this.wingRightBack.zRot = -0.5934119F + baseAngle - wingRot;
        this.frontLegs.xRot = 0.1487144F + legMov;
        this.midLegs.xRot = 0.5948578F + legMovB;
        this.rearLegs.xRot = 1.070744F + legMov;

        float headX = -headPitch * Mth.DEG_TO_RAD;
        float headY = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headX;
        this.head.yRot = headY;
        this.mouth.xRot = 0.6548599F;
        this.mouth.yRot = 0.0F;
        this.rightAntenna.xRot = -1.041001F + headX;
        this.rightAntenna.yRot = 0.7853982F + headY;
        this.leftAntenna.xRot = -1.041001F + headX;
        this.leftAntenna.yRot = -0.7853982F + headY;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.abdomen.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.frontLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rightAntenna.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.leftAntenna.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.rearLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.midLegs.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.head.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.thorax.render(poseStack, buffer, packedLight, packedOverlay, color);
        this.mouth.render(poseStack, buffer, packedLight, packedOverlay, color);
        int wingColor = this.flying ? InsectModelUtil.withAlpha(color, 0.8F) : color;
        this.wingRight.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        this.wingLeft.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        this.wingRightFront.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        this.wingLeftFront.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        this.wingRightBack.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
        this.wingLeftBack.render(poseStack, buffer, packedLight, packedOverlay, wingColor);
    }
}
