package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCSmallFishEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelSmallFish}, shared by all 8 small fish
 * variants (only the texture differs). Like the medium fish, the original builds this along its own
 * X axis and rotates 90 degrees at render time — baked here into the "assembly" wrapper part, with
 * -90 (not +90) so the fish faces forward instead of swimming backward.
 */
public class MoCSmallFishModel extends HierarchicalModel<MoCSmallFishEntity> {

    private static final float TAIL_SWAY_SPEED = 0.8F;
    private static final float TAIL_SWAY = 0.6F;
    private static final float FIN_IDLE_SPEED = 0.4F;
    private static final float FIN_IDLE_AMOUNT = 0.2F;
    private static final float MID_FIN_YAW = 0.7853982F;

    /**
     * The model's own parts sit around Y=13.5-17.2, well below where Dolphin/Manta/Fishy's parts sit
     * (Y=18-22, close to the hitbox); this raises the whole fish onto its hitbox. Not confirmed
     * visually — if the fish still floats above or sinks below its hitbox, adjust this first.
     */
    private static final float VERTICAL_CORRECTION = 6.0F;

    private final ModelPart root;
    private final ModelPart tail;
    private final ModelPart midBodyFin;
    private final ModelPart lowerFinB;

    public MoCSmallFishModel(ModelPart root) {
        this.root = root;
        ModelPart assembly = root.getChild("assembly");
        this.tail = assembly.getChild("tail");
        this.midBodyFin = assembly.getChild("mid_body_fin");
        this.lowerFinB = assembly.getChild("lower_fin_b");
    }

    /** The texture layout is 32x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition assembly = root.addOrReplaceChild("assembly",
                CubeListBuilder.create(), PartPose.offsetAndRotation(0F, VERTICAL_CORRECTION, 0F, 0F, -Mth.HALF_PI, 0F));

        assembly.addOrReplaceChild("body_flat",
                CubeListBuilder.create().texOffs(0, 2).addBox(0F, -1.5F, -1F, 5, 3, 2),
                PartPose.offset(-3F, 15F, 0F));
        assembly.addOrReplaceChild("body_romboid",
                CubeListBuilder.create().texOffs(0, 7).addBox(0F, 0F, -0.5F, 4, 4, 1),
                PartPose.offsetAndRotation(-4F, 15F, 0F, 0F, 0F, -0.7853982F));
        assembly.addOrReplaceChild("mid_body_fin",
                CubeListBuilder.create().texOffs(0, 12).addBox(0F, -0.5F, 0F, 4, 2, 4),
                PartPose.offsetAndRotation(-3F, 15F, 0F, 0F, MID_FIN_YAW, 0F));

        assembly.addOrReplaceChild("upper_fin_a",
                CubeListBuilder.create().texOffs(10, 0).addBox(-0.5F, -1.3F, -0.5F, 2, 1, 1),
                PartPose.offset(-0.65F, 13.5F, 0F));
        assembly.addOrReplaceChild("upper_fin_b",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -1F, -0.5F, 4, 1, 1),
                PartPose.offset(0F, 13.5F, 0F));
        assembly.addOrReplaceChild("upper_fin_c",
                CubeListBuilder.create().texOffs(0, 18).addBox(-5F, -2F, 0F, 8, 3, 0),
                PartPose.offset(0F, 13.5F, 0F));

        assembly.addOrReplaceChild("lower_fin_a",
                CubeListBuilder.create().texOffs(16, 0).addBox(-0.5F, -0.3F, -0.5F, 2, 1, 1),
                PartPose.offset(-0.65F, 17.2F, 0F));
        assembly.addOrReplaceChild("lower_fin_b",
                CubeListBuilder.create().texOffs(0, 21).addBox(0F, 0F, -3F, 5, 0, 6),
                PartPose.offset(-3F, 16F, 0F));
        assembly.addOrReplaceChild("lower_fin_c",
                CubeListBuilder.create().texOffs(16, 18).addBox(-5F, 0F, 0F, 8, 3, 0),
                PartPose.offset(0F, 15.5F, 0F));

        assembly.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(10, 7).addBox(0F, 0F, -0.5F, 3, 3, 1),
                PartPose.offsetAndRotation(1.3F, 15F, 0F, 0F, 0F, -0.7853982F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCSmallFishEntity fish, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float tailMovement = Mth.cos(limbSwing * TAIL_SWAY_SPEED) * limbSwingAmount * TAIL_SWAY;
        float finIdle = Mth.cos(ageInTicks * FIN_IDLE_SPEED) * FIN_IDLE_AMOUNT;

        this.tail.yRot = tailMovement;
        this.midBodyFin.yRot = MID_FIN_YAW + finIdle;
        this.lowerFinB.zRot = finIdle;
    }
}