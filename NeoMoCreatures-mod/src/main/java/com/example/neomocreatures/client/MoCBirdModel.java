package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBirdEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of {@code drzhark.mocreatures.client.model.MoCModelBird}. Like
 * the bunny, all 8 parts are plain independent children of the root with no
 * per-frame position offsets, so the default
 * {@link HierarchicalModel#renderToBuffer} is exactly equivalent to the
 * original's manual part-by-part render() call.
 */
public class MoCBirdModel extends HierarchicalModel<MoCBirdEntity> {

    private static final float DEG_TO_RAD = 57.29578F;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart body;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tail;

    public MoCBirdModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.beak = root.getChild("beak");
        this.body = root.getChild("body");
        this.leftLeg = root.getChild("leftleg");
        this.rightLeg = root.getChild("rightleg");
        this.rightWing = root.getChild("rwing");
        this.leftWing = root.getChild("lwing");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3F, -2F, 3, 3, 3),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("beak",
                CubeListBuilder.create().texOffs(14, 0).addBox(-0.5F, -1.5F, -3F, 1, 1, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 9).addBox(-2F, -4F, -3F, 4, 8, 4),
                PartPose.offsetAndRotation(0F, 16F, 0F, 1.047198F, 0F, 0F));
        root.addOrReplaceChild("leftleg",
                CubeListBuilder.create().texOffs(26, 0).addBox(-1F, 0F, -4F, 3, 4, 3),
                PartPose.offset(-2F, 19F, 1F));
        root.addOrReplaceChild("rightleg",
                CubeListBuilder.create().texOffs(26, 0).addBox(-1F, 0F, -4F, 3, 4, 3),
                PartPose.offset(1F, 19F, 1F));
        root.addOrReplaceChild("rwing",
                CubeListBuilder.create().texOffs(24, 13).addBox(-1F, 0F, -3F, 1, 5, 5),
                PartPose.offset(-2F, 14F, 0F));
        root.addOrReplaceChild("lwing",
                CubeListBuilder.create().texOffs(24, 13).addBox(0F, 0F, -3F, 1, 5, 5),
                PartPose.offset(2F, 14F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(0, 23).addBox(-6F, 5F, 2F, 4, 1, 4),
                PartPose.offsetAndRotation(4F, 13F, 0F, 0.261799F, 0F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCBirdEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = -(headPitch * 0.5F) / DEG_TO_RAD;
        this.beak.yRot = this.head.yRot = netHeadYaw / DEG_TO_RAD;

        // While carried, the bird isn't flying on its own, but should still
        // flap if the player wearing it is currently falling — read from the
        // entity's own hysteresis-smoothed flag instead of recomputing a raw
        // velocity check here every frame, which flickered on/off constantly.
        boolean flying = entity.isHeld() ? entity.isHolderFalling() : !entity.onGround();

        if (flying) {
            this.leftLeg.xRot = 1.4F;
            this.rightLeg.xRot = 1.4F;
        } else {
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
        }

        if (flying) {
            float flap = Mth.sin(ageInTicks * 1.3F) * 0.9F + 0.9F;
            this.rightWing.zRot = flap;
            this.leftWing.zRot = -flap;
        } else {
            this.rightWing.zRot = 0F;
            this.leftWing.zRot = 0F;
        }
    }
}