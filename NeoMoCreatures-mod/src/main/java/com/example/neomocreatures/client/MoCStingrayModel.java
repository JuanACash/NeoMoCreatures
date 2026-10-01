package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCStingrayEntity;
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

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelRay}, keeping only the parts the original
 * draws for the stingray. The manta ray's extra wing segments and side fins are left out.
 */
public class MoCStingrayModel extends HierarchicalModel<MoCStingrayEntity> {

    /** Peak wing angle, in radians, at full limb swing. */
    private static final float WING_FLAP_AMPLITUDE = 1.5F;
    /** The second wing segment flaps this much more than the first (1 / 20 = 5% more). */
    private static final float WING_TIP_LAG = 1.0F / 20.0F;
    private static final float TAIL_LASH_ANGLE = 0.5F;

    private final ModelPart root;
    private final ModelPart tail;
    private final ModelPart rightWingA;
    private final ModelPart rightWingB;
    private final ModelPart leftWingA;
    private final ModelPart leftWingB;

    public MoCStingrayModel(ModelPart root) {
        this.root = root;
        this.tail = root.getChild("tail");
        this.rightWingA = root.getChild("right_wing_a");
        this.rightWingB = root.getChild("right_wing_b");
        this.leftWingA = root.getChild("left_wing_a");
        this.leftWingB = root.getChild("left_wing_b");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(26, 0).addBox(-4F, -1F, 0F, 8, 2, 11),
                PartPose.offset(0F, 22F, -5F));
        root.addOrReplaceChild("body_upper",
                CubeListBuilder.create().texOffs(0, 11).addBox(-3F, -1F, 0F, 6, 1, 8),
                PartPose.offset(0F, 21F, -4F));
        root.addOrReplaceChild("body_tail",
                CubeListBuilder.create().texOffs(0, 20).addBox(-1.8F, -0.5F, -3.2F, 5, 1, 5),
                PartPose.offsetAndRotation(0F, 22F, 7F, 0F, 1F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(30, 15).addBox(-0.5F, -0.5F, 1F, 1, 1, 16),
                PartPose.offset(0F, 22F, 8F));

        root.addOrReplaceChild("right_wing_a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -0.5F, -5F, 3, 1, 10),
                PartPose.offset(-4F, 22F, 1F));
        root.addOrReplaceChild("right_wing_b",
                CubeListBuilder.create().texOffs(2, 2).addBox(-6F, -0.5F, -4F, 3, 1, 8),
                PartPose.offset(-4F, 22F, 1F));
        root.addOrReplaceChild("left_wing_a",
                CubeListBuilder.create().mirror().texOffs(0, 0).addBox(0F, -0.5F, -5F, 3, 1, 10),
                PartPose.offset(4F, 22F, 1F));
        root.addOrReplaceChild("left_wing_b",
                CubeListBuilder.create().mirror().texOffs(2, 2).addBox(3F, -0.5F, -4F, 3, 1, 8),
                PartPose.offset(4F, 22F, 1F));

        root.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -2F, 1F, 1, 1, 2),
                PartPose.offset(0F, 21F, -4F));
        root.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(0, 3).addBox(2F, -2F, 1F, 1, 1, 2),
                PartPose.offset(0F, 21F, -4F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCStingrayEntity ray, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float swing = Mth.cos(limbSwing * 0.6662F);
        float flap = swing * WING_FLAP_AMPLITUDE * limbSwingAmount;
        this.rightWingA.zRot = flap;
        this.leftWingA.zRot = -flap;
        float tipFlap = flap + flap * WING_TIP_LAG;
        this.rightWingB.zRot = tipFlap;
        this.leftWingB.zRot = -tipFlap;

        this.tail.yRot = swing * 0.7F * limbSwingAmount;
        if (ray.isPoisoning()) {
            this.tail.xRot = TAIL_LASH_ANGLE;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}