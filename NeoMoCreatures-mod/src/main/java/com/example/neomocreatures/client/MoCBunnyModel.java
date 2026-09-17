package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBunnyEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * 1:1 port of {@code drzhark.mocreatures.client.model.MoCModelBunny}. Unlike
 * the snake, this model needs no custom render loop — all 11 parts are plain
 * children of the root with no dynamic per-frame position offsets, so the
 * default {@link HierarchicalModel#renderToBuffer} (which just renders the
 * whole root hierarchy) is exactly equivalent to the original's manual
 * part-by-part render() call.
 */
public class MoCBunnyModel extends HierarchicalModel<MoCBunnyEntity> {

    private static final float DEG_TO_RAD = 57.29578F;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart cheekRight;
    private final ModelPart cheekLeft;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart legFrontRight;
    private final ModelPart legFrontLeft;
    private final ModelPart legBackRight;
    private final ModelPart legBackLeft;

    public MoCBunnyModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.earRight = root.getChild("ear_right");
        this.earLeft = root.getChild("ear_left");
        this.cheekRight = root.getChild("cheek_right");
        this.cheekLeft = root.getChild("cheek_left");
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
        this.legFrontRight = root.getChild("leg_front_right");
        this.legFrontLeft = root.getChild("leg_front_left");
        this.legBackRight = root.getChild("leg_back_right");
        this.legBackLeft = root.getChild("leg_back_left");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -1F, -4F, 4, 4, 6),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("ear_right",
                CubeListBuilder.create().texOffs(14, 0).addBox(-2F, -5F, -3F, 1, 4, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("ear_left",
                CubeListBuilder.create().texOffs(14, 0).addBox(1F, -5F, -3F, 1, 4, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("cheek_right",
                CubeListBuilder.create().texOffs(20, 0).addBox(-4F, 0F, -3F, 2, 3, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("cheek_left",
                CubeListBuilder.create().texOffs(20, 0).addBox(2F, 0F, -3F, 2, 3, 2),
                PartPose.offset(0F, 15F, -4F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 10).addBox(-3F, -4F, -3F, 6, 8, 6),
                PartPose.offset(0F, 16F, 0F));
        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2F, 4F, -2F, 4, 3, 4),
                PartPose.offset(0F, 16F, 0F));
        root.addOrReplaceChild("leg_front_right",
                CubeListBuilder.create().texOffs(24, 16).addBox(-2F, 0F, -1F, 2, 2, 2),
                PartPose.offset(3F, 19F, -3F));
        root.addOrReplaceChild("leg_front_left",
                CubeListBuilder.create().texOffs(24, 16).addBox(0F, 0F, -1F, 2, 2, 2),
                PartPose.offset(-3F, 19F, -3F));
        root.addOrReplaceChild("leg_back_right",
                CubeListBuilder.create().texOffs(16, 24).addBox(-2F, 0F, -4F, 2, 2, 4),
                PartPose.offset(3F, 19F, 4F));
        root.addOrReplaceChild("leg_back_left",
                CubeListBuilder.create().texOffs(16, 24).addBox(0F, 0F, -4F, 2, 2, 4),
                PartPose.offset(-3F, 19F, 4F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCBunnyEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        float headX = -headPitch / DEG_TO_RAD;
        float headY = netHeadYaw / DEG_TO_RAD;
        this.head.xRot = headX;
        this.head.yRot = headY;
        this.earRight.xRot = headX;
        this.earRight.yRot = headY;
        this.earLeft.xRot = headX;
        this.earLeft.yRot = headY;
        this.cheekRight.xRot = headX;
        this.cheekRight.yRot = headY;
        this.cheekLeft.xRot = headX;
        this.cheekLeft.yRot = headY;

        // Original: body and tail are permanently rotated 90 degrees — the
        // rounded haunch/rump shape comes from the box being laid on its side.
        this.body.xRot = (float) (Math.PI / 2);
        this.tail.xRot = (float) (Math.PI / 2);

        // TODO (step 2): also skip this while entity.isHeld() (CarriedPet) once
        // the pickup mechanic lands, matching the original's getVehicle() check.
        if (entity.getVehicle() == null) {
            float frontLeg = Mth.cos(limbSwing * 0.6662F) * 1.0F * limbSwingAmount;
            float hindLeg = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;
            this.legFrontRight.xRot = frontLeg;
            this.legFrontLeft.xRot = frontLeg;
            this.legBackRight.xRot = hindLeg;
            this.legBackLeft.xRot = hindLeg;
        }
    }
}