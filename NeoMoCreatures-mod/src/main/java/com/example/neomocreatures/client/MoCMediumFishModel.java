package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMediumFishEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelMediumFish}, shared by cod, salmon and
 * bass (only the texture differs between them). The original builds the fish along its own X axis
 * and rotates the whole model 90 degrees at render time; here that rotation is baked into the
 * "assembly" wrapper part instead, so every child keeps the original's numbers unchanged.
 */
public class MoCMediumFishModel<T extends MoCMediumFishEntity> extends HierarchicalModel<T> {

    private static final float TAIL_SWAY_SPEED = 0.6662F;
    private static final float TAIL_SWAY = 0.6F;
    /** Idle wag speed/amplitude of the pectoral fins and the mouth, driven by age instead of movement. */
    private static final float FIN_IDLE_SPEED = 0.2F;
    private static final float FIN_IDLE_AMOUNT = 0.4F;
    private static final float MOUTH_IDLE_SPEED = 0.3F;
    private static final float MOUTH_IDLE_AMOUNT = 0.2F;
    private static final float PECTORAL_FIN_YAW = 0.8726646F;
    private static final float MOUTH_BOTTOM_TILT = 0.3346075F;
    private static final float MOUTH_BOTTOM_B_TILT = -0.7132579F;

    private final ModelPart root;
    private final ModelPart tail;
    private final ModelPart tailFin;
    private final ModelPart leftPectoralFin;
    private final ModelPart rightPectoralFin;
    private final ModelPart mouthBottom;
    private final ModelPart mouthBottomB;

    public MoCMediumFishModel(ModelPart root) {
        this.root = root;
        ModelPart assembly = root.getChild("assembly");
        this.tail = assembly.getChild("tail");
        this.tailFin = assembly.getChild("tail_fin");
        this.leftPectoralFin = assembly.getChild("left_pectoral_fin");
        this.rightPectoralFin = assembly.getChild("right_pectoral_fin");
        this.mouthBottom = assembly.getChild("mouth_bottom");
        this.mouthBottomB = assembly.getChild("mouth_bottom_b");
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // The original model is built along its own local X axis and rotated 90 degrees about Y at
        // render time; this wrapper part reproduces that single rotation for every child below.
        // Negative 90°, not positive: with +90° the original's head (built at negative X) ends up
        // facing +Z, which is backward in Minecraft's own -Z-is-forward convention — the fish then
        // visibly swims tail-first and looks like it is spinning when it turns.
        //
        // The +12 Y also corrects the model's own vertical origin: its parts are built around Y=9-10
        // (Dolphin/Manta/Fishy's equivalent parts sit around Y=20-22, close to the hitbox), so without
        // this the whole fish floats about 0.75 blocks above its actual hitbox.
        PartDefinition assembly = root.addOrReplaceChild("assembly",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, -Mth.HALF_PI, 0F));

        assembly.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 10).addBox(-5F, 0F, -1.5F, 5, 3, 3),
                PartPose.offsetAndRotation(-8F, 6F, 0F, 0F, 0F, -0.4461433F));
        assembly.addOrReplaceChild("lower_head",
                CubeListBuilder.create().texOffs(0, 16).addBox(-4F, -3F, -1.5F, 4, 3, 3),
                PartPose.offsetAndRotation(-8F, 12F, 0F, 0F, 0F, 0.3346075F));
        assembly.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(14, 17).addBox(-1F, -1F, -1F, 1, 3, 2),
                PartPose.offsetAndRotation(-11F, 8.2F, 0F, 0F, 0F, 1.412787F));
        assembly.addOrReplaceChild("mouth_bottom",
                CubeListBuilder.create().texOffs(16, 10).addBox(-2F, -0.4F, -1F, 2, 1, 2),
                PartPose.offsetAndRotation(-11.5F, 10F, 0F, 0F, 0F, MOUTH_BOTTOM_TILT));
        assembly.addOrReplaceChild("mouth_bottom_b",
                CubeListBuilder.create().texOffs(16, 13).addBox(-1.5F, -2.4F, -0.5F, 1, 1, 1),
                PartPose.offsetAndRotation(-11.5F, 10F, 0F, 0F, 0F, MOUTH_BOTTOM_B_TILT));

        assembly.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(0F, -3F, -2F, 9, 6, 4),
                PartPose.offset(-8F, 9F, 0F));
        assembly.addOrReplaceChild("back_up",
                CubeListBuilder.create().texOffs(26, 0).addBox(0F, 0F, -1.5F, 8, 3, 3),
                PartPose.offsetAndRotation(1F, 6F, 0F, 0F, 0F, 0.1858931F));
        assembly.addOrReplaceChild("back_down",
                CubeListBuilder.create().texOffs(26, 6).addBox(0F, -3F, -1.5F, 8, 3, 3),
                PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, -0.1919862F));

        assembly.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(48, 0).addBox(0F, -1.5F, -1F, 4, 3, 2),
                PartPose.offset(8F, 9F, 0F));
        assembly.addOrReplaceChild("tail_fin",
                CubeListBuilder.create().texOffs(48, 5).addBox(3F, -5.3F, 0F, 5, 11, 0),
                PartPose.offset(8F, 9F, 0F));

        assembly.addOrReplaceChild("right_pectoral_fin",
                CubeListBuilder.create().texOffs(28, 12).addBox(0F, -2F, 0F, 5, 4, 0),
                PartPose.offsetAndRotation(-6.5F, 10F, 2F, 0F, -PECTORAL_FIN_YAW, 0.185895F));
        assembly.addOrReplaceChild("left_pectoral_fin",
                CubeListBuilder.create().texOffs(38, 12).addBox(0F, -2F, 0F, 5, 4, 0),
                PartPose.offsetAndRotation(-6.5F, 10F, -2F, 0F, PECTORAL_FIN_YAW, 0.1858931F));

        assembly.addOrReplaceChild("upper_fin",
                CubeListBuilder.create().texOffs(0, 22).addBox(0F, -4F, 0F, 15, 4, 0),
                PartPose.offsetAndRotation(-7F, 6F, 0F, 0F, 0F, 0.1047198F));
        assembly.addOrReplaceChild("lower_fin",
                CubeListBuilder.create().texOffs(46, 20).addBox(0F, 0F, 0F, 9, 4, 0),
                PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, -0.1858931F));
        assembly.addOrReplaceChild("right_lower_fin",
                CubeListBuilder.create().texOffs(28, 16).addBox(0F, 0F, 0F, 9, 4, 0),
                PartPose.offsetAndRotation(-7F, 12F, 1F, 0.5235988F, 0F, 0F));
        assembly.addOrReplaceChild("left_lower_fin",
                CubeListBuilder.create().texOffs(46, 16).addBox(0F, 0F, 0F, 9, 4, 0),
                PartPose.offsetAndRotation(-7F, 12F, -1F, -0.5235988F, 0F, 0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T fish, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float tailMovement = Mth.cos(limbSwing * TAIL_SWAY_SPEED) * limbSwingAmount * TAIL_SWAY;
        float finIdle = Mth.cos(ageInTicks * FIN_IDLE_SPEED) * FIN_IDLE_AMOUNT;
        float mouthIdle = Mth.cos(ageInTicks * MOUTH_IDLE_SPEED) * MOUTH_IDLE_AMOUNT;

        this.tail.yRot = tailMovement;
        this.tailFin.yRot = tailMovement;
        this.leftPectoralFin.yRot = PECTORAL_FIN_YAW + finIdle;
        this.rightPectoralFin.yRot = -PECTORAL_FIN_YAW - finIdle;
        this.mouthBottom.zRot = MOUTH_BOTTOM_TILT + mouthIdle;
        this.mouthBottomB.zRot = MOUTH_BOTTOM_B_TILT + mouthIdle;
    }
}