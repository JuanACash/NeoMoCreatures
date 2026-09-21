package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCDolphinEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelDolphin}. The original only animates the
 * two tail fins, which beat with the swimming animation.
 */
public class MoCDolphinModel extends HierarchicalModel<MoCDolphinEntity> {

    private static final String LEFT_TAIL_FIN = "left_tail_fin";
    private static final String RIGHT_TAIL_FIN = "right_tail_fin";

    /** Tail beat speed relative to the limb swing, from the original model. */
    private static final float TAIL_BEAT_SPEED = 0.4F;
    private static final float DORSAL_FIN_TILT = Mth.PI / 4.0F;
    private static final float TAIL_FIN_SPREAD = Mth.PI / 4.0F;
    private static final float PECTORAL_FIN_YAW = Mth.PI / 6.0F;
    private static final float PECTORAL_FIN_ROLL = Mth.PI / 6.0F;
    /** Extra thickness of the tail fins, from the original model. */
    private static final CubeDeformation TAIL_FIN_INFLATE = new CubeDeformation(0.3F);

    private final ModelPart root;
    private final ModelPart leftTailFin;
    private final ModelPart rightTailFin;

    public MoCDolphinModel(ModelPart root) {
        this.root = root;
        this.leftTailFin = root.getChild(LEFT_TAIL_FIN);
        this.rightTailFin = root.getChild(RIGHT_TAIL_FIN);
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(4, 6).addBox(0F, 0F, 0F, 6, 8, 18),
                PartPose.offset(-3F, 17F, -4F));
        root.addOrReplaceChild("head_upper",
                CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, 0F, 5, 7, 8),
                PartPose.offset(-2.5F, 18F, -10.5F));
        root.addOrReplaceChild("head_lower",
                CubeListBuilder.create().texOffs(50, 0).addBox(0F, 0F, 0F, 3, 3, 4),
                PartPose.offset(-1.5F, 21.5F, -14.5F));
        root.addOrReplaceChild("tail_stock",
                CubeListBuilder.create().texOffs(34, 9).addBox(0F, 0F, 0F, 5, 5, 10),
                PartPose.offset(-2.5F, 19F, 14F));
        root.addOrReplaceChild("dorsal_fin",
                CubeListBuilder.create().texOffs(4, 12).addBox(0F, 0F, 0F, 1, 4, 8),
                PartPose.offsetAndRotation(-0.5F, 18F, 2F, DORSAL_FIN_TILT, 0F, 0F));

        root.addOrReplaceChild(LEFT_TAIL_FIN,
                CubeListBuilder.create().texOffs(34, 0).addBox(0F, 0F, 0F, 4, 1, 8, TAIL_FIN_INFLATE),
                PartPose.offsetAndRotation(-1F, 21.5F, 24F, 0F, TAIL_FIN_SPREAD, 0F));
        root.addOrReplaceChild(RIGHT_TAIL_FIN,
                CubeListBuilder.create().texOffs(34, 0).addBox(0F, 0F, 0F, 4, 1, 8, TAIL_FIN_INFLATE),
                PartPose.offsetAndRotation(-2F, 21.5F, 21F, 0F, -TAIL_FIN_SPREAD, 0F));

        root.addOrReplaceChild("left_pectoral_fin",
                CubeListBuilder.create().texOffs(14, 0).addBox(0F, 0F, 0F, 8, 1, 4),
                PartPose.offsetAndRotation(3F, 24F, -1F, 0F, -PECTORAL_FIN_YAW, PECTORAL_FIN_ROLL));
        root.addOrReplaceChild("right_pectoral_fin",
                CubeListBuilder.create().texOffs(14, 0).addBox(0F, 0F, 0F, 8, 1, 4),
                PartPose.offsetAndRotation(-9F, 27.5F, 3F, 0F, PECTORAL_FIN_YAW, -PECTORAL_FIN_ROLL));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCDolphinEntity dolphin, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float beat = Mth.cos(limbSwing * TAIL_BEAT_SPEED) * limbSwingAmount;
        this.leftTailFin.xRot = beat;
        this.rightTailFin.xRot = beat;
    }
}