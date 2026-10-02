package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCWraithEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of the real {@code MoCModelWraith} (from the 1.12.2 original — the version later ported
 * forward replaced this with a plain vanilla humanoid mesh, which is why it used to look like a
 * child): an oversized floating head, a tall narrow torso, ordinary arms, and near-invisible stub
 * legs that never animate at all.
 */
public class MoCWraithModel<T extends MoCWraithEntity> extends HierarchicalModel<T> {

    private static final float RADIAN = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public MoCWraithModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Minecraft's own model convention: y=0 sits near the head, and positive y goes DOWNWARD
        // toward the feet — vanilla's own HumanoidModel uses these exact same pivot numbers (arms at
        // y=2, legs at y=12), so the original's own (0,0,0)-based pivots need no adjustment at all.
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -8F, -4F, 8, 8, 8),
                PartPose.offset(0F, 0F, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(36, 0).addBox(-6F, 0F, -2F, 10, 20, 4),
                PartPose.offset(0F, 0F, 0F));
        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(16, 16).addBox(-5F, -2F, -2F, 4, 12, 4),
                PartPose.offset(-5F, 2F, 0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(16, 16).mirror().addBox(-1F, -2F, -2F, 4, 12, 4),
                PartPose.offset(5F, 2F, 0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2F, 0F, -2F, 2, 2, 2),
                PartPose.offset(-2F, 12F, 0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2F, 0F, -2F, 2, 2, 2),
                PartPose.offset(2F, 12F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        // Original: the legs never animate at all — they're just inert stubs.
        this.head.yRot = netHeadYaw / RADIAN;
        this.head.xRot = headPitch / RADIAN;

        float sinSwing = Mth.sin(limbSwingAmount * Mth.PI);

        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        this.rightArm.yRot = -(0.1F - sinSwing * 0.6F);
        this.leftArm.yRot = 0.1F - sinSwing * 0.6F;

        int attackCounter = entity.attackCounter;
        if (attackCounter != 0) {
            float armMove = Mth.cos(attackCounter * 0.12F) * 4.0F;
            this.rightArm.xRot = -armMove;
            this.leftArm.xRot = -armMove;
        } else {
            this.rightArm.xRot = -1.570796F - sinSwing * 1.2F;
            this.leftArm.xRot = -1.570796F - sinSwing * 1.2F;
            this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.leftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
        }
        this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.leftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    }
}