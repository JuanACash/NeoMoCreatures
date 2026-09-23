package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCCrocodileEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelCrocodile}: a long low body, 4 jointed
 * legs, a 4-segment spiked tail, and a two-part jaw with teeth that opens for the bite animation.
 * <p>
 * Some original boxes are 0 blocks wide (thin plates for teeth/spikes, drawn edge-on); modern cube
 * geometry needs a non-zero size to generate faces, so those use {@link #THIN} (0.05) instead of 0.
 * Visually this is imperceptible at normal view distance.
 */
public class MoCCrocodileModel extends HierarchicalModel<MoCCrocodileEntity> {

    private static final float THIN = 0.05F;

    // ---- Walking cycle ----
    private static final float LEG_SWING_SPEED = 0.6662F;
    private static final float LEG_SWING_AMOUNT = 1.4F;
    private static final float LEG_SWAY_PERIOD = 1.919107651F;
    private static final float LEG_SWAY_AMOUNT = 0.261799387799149F * 5F;
    private static final float TAIL_SWAY_AMOUNT = 0.7F;

    // ---- Resting pose (legs tucked under the body) ----
    private static final float[] REST_LEG_X = {6F, -6F, 7F, -7F};
    private static final float[] REST_LEG_Y = {17F, 17F, 17F, 17F};
    private static final float[] REST_LEG_Z = {-6F, -6F, 7F, 7F};
    private static final float[] REST_LEG_YROT = {-0.7854F, 0.7854F, -0.7854F, 0.7854F};
    private static final float[] REST_UPPER_LEG_X = {5F, -5F, 5F, -5F};
    private static final float[] REST_UPPER_LEG_Z = {-3F, -3F, 9F, 9F};

    // ---- Swimming pose (legs trailing straight back) ----
    private static final float[] SWIM_LEG_X = {9F, -9F, 8F, -8F};
    private static final float[] SWIM_LEG_Z = {0F, 0F, 12F, 12F};
    private static final float[] SWIM_UPPER_LEG_X = {5F, -5F, 5F, -5F};
    private static final float[] SWIM_UPPER_LEG_Z = {-3F, -3F, 9F, 9F};

    // ---- Default (walking) rest position of each leg pair ----
    private static final float[] LEG_X = {5F, -5F, 5F, -5F};
    private static final float[] LEG_Z = {-3F, -3F, 9F, 9F};

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart spikeEye;
    private final ModelPart spikeEye1;
    private final ModelPart uJaw;
    private final ModelPart uJaw2;
    private final ModelPart lJaw;
    private final ModelPart lJaw2;
    private final ModelPart[] upperTeeth = new ModelPart[4];
    private final ModelPart[] lowerTeeth = new ModelPart[5];
    private final ModelPart tailA;
    private final ModelPart tailB;
    private final ModelPart tailC;
    private final ModelPart tailD;
    private final ModelPart[] tailSpikes = new ModelPart[12];
    private final ModelPart[] legs = new ModelPart[4];
    private final ModelPart[] upperLegs = new ModelPart[4];

    public MoCCrocodileModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.spikeEye = root.getChild("spike_eye_0");
        this.spikeEye1 = root.getChild("spike_eye_1");
        this.uJaw = root.getChild("upper_jaw");
        this.uJaw2 = root.getChild("upper_jaw_tip");
        this.lJaw = root.getChild("lower_jaw");
        this.lJaw2 = root.getChild("lower_jaw_tip");
        this.upperTeeth[0] = root.getChild("upper_tooth_0");
        this.upperTeeth[1] = root.getChild("upper_tooth_1");
        this.upperTeeth[2] = root.getChild("upper_tooth_2");
        this.upperTeeth[3] = root.getChild("upper_tooth_3");
        this.lowerTeeth[0] = root.getChild("lower_tooth_0");
        this.lowerTeeth[1] = root.getChild("lower_tooth_1");
        this.lowerTeeth[2] = root.getChild("lower_tooth_2");
        this.lowerTeeth[3] = root.getChild("lower_tooth_3");
        this.lowerTeeth[4] = root.getChild("lower_tooth_tip");
        this.tailA = root.getChild("tail_a");
        this.tailB = root.getChild("tail_b");
        this.tailC = root.getChild("tail_c");
        this.tailD = root.getChild("tail_d");
        for (int i = 0; i < this.tailSpikes.length; i++) {
            this.tailSpikes[i] = root.getChild("tail_spike_" + i);
        }
        for (int i = 0; i < 4; i++) {
            this.legs[i] = root.getChild("leg_" + i);
            this.upperLegs[i] = root.getChild("upper_leg_" + i);
        }
    }

    /** The texture layout is 64x32. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(4, 7).addBox(0F, 0F, 0F, 10, 5, 20),
                PartPose.offset(-5F, 16F, -8F));

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3F, -2F, -6F, 6, 5, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("spike_eye_0",
                CubeListBuilder.create().texOffs(44, 14).addBox(-3F, -3F, -6F, THIN, 1, 2),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("spike_eye_1",
                CubeListBuilder.create().texOffs(44, 14).addBox(3F, -3F, -6F, THIN, 1, 2),
                PartPose.offset(0F, 18F, -8F));

        root.addOrReplaceChild("upper_jaw",
                CubeListBuilder.create().texOffs(44, 8).addBox(-2F, -1F, -12F, 4, 2, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("upper_jaw_tip",
                CubeListBuilder.create().texOffs(37, 0).addBox(-1.5F, -1F, -16F, 3, 2, 4),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_jaw",
                CubeListBuilder.create().texOffs(42, 0).addBox(-2.5F, 1F, -12F, 5, 2, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_jaw_tip",
                CubeListBuilder.create().texOffs(24, 1).addBox(-2F, 1F, -16F, 4, 2, 4),
                PartPose.offset(0F, 18F, -8F));

        root.addOrReplaceChild("upper_tooth_0",
                CubeListBuilder.create().texOffs(52, 12).addBox(1.4F, 1F, -16.4F, THIN, 1, 4),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("upper_tooth_1",
                CubeListBuilder.create().texOffs(52, 12).addBox(-1.4F, 1F, -16.4F, THIN, 1, 4),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("upper_tooth_2",
                CubeListBuilder.create().texOffs(50, 10).addBox(1.9F, 1F, -12.5F, THIN, 1, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("upper_tooth_3",
                CubeListBuilder.create().texOffs(50, 10).addBox(-1.9F, 1F, -12.5F, THIN, 1, 6),
                PartPose.offset(0F, 18F, -8F));

        root.addOrReplaceChild("lower_tooth_0",
                CubeListBuilder.create().texOffs(8, 11).addBox(1.6F, 0F, -16F, THIN, 1, 4),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_tooth_1",
                CubeListBuilder.create().texOffs(8, 11).addBox(-1.6F, 0F, -16F, THIN, 1, 4),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_tooth_2",
                CubeListBuilder.create().texOffs(6, 9).addBox(2.1F, 0F, -12F, THIN, 1, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_tooth_3",
                CubeListBuilder.create().texOffs(6, 9).addBox(-2.1F, 0F, -12F, THIN, 1, 6),
                PartPose.offset(0F, 18F, -8F));
        root.addOrReplaceChild("lower_tooth_tip",
                CubeListBuilder.create().texOffs(19, 21).addBox(-1F, 0F, -16.1F, 2, 1, THIN),
                PartPose.offset(0F, 18F, -8F));

        root.addOrReplaceChild("tail_a",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -0.5F, 0F, 8, 4, 8),
                PartPose.offset(0F, 17F, 12F));
        root.addOrReplaceChild("tail_b",
                CubeListBuilder.create().texOffs(2, 0).addBox(-3F, 0F, 8F, 6, 3, 8),
                PartPose.offset(0F, 17F, 12F));
        root.addOrReplaceChild("tail_c",
                CubeListBuilder.create().texOffs(6, 2).addBox(-2F, 0.5F, 16F, 4, 2, 6),
                PartPose.offset(0F, 17F, 12F));
        root.addOrReplaceChild("tail_d",
                CubeListBuilder.create().texOffs(7, 2).addBox(-1.5F, 1F, 22F, 3, 1, 6),
                PartPose.offset(0F, 17F, 12F));

        float[] spikeX = {-1F, 1F, -1.5F, 1.5F, -2F, 2F, -2.5F, 2.5F, -3F, 3F, 3.5F, -3.5F};
        float[] spikeY = {-1F, -1F, -1.5F, -1.5F, -2F, -2F, -2F, -2F, -2.5F, -2.5F, -2.5F, -2.5F};
        float[] spikeZ = {23F, 23F, 17F, 17F, 12F, 12F, 8F, 8F, 4F, 4F, 0F, 0F};
        for (int i = 0; i < 12; i++) {
            root.addOrReplaceChild("tail_spike_" + i,
                    CubeListBuilder.create().texOffs(44, 16).addBox(spikeX[i], spikeY[i], spikeZ[i], THIN, 2, 4),
                    PartPose.offset(0F, 17F, 12F));
        }

        float[] backSpikeX = {0F, 0F, 4F, -4F, -4F, 4F};
        float[] backSpikeY = {14F, 14F, 14F, 14F, 14F, 14F};
        float[] backSpikeZ = {3F, -6F, -8F, -8F, 1F, 1F};
        for (int i = 0; i < 6; i++) {
            root.addOrReplaceChild("back_spike_" + i,
                    CubeListBuilder.create().texOffs(44, 10).addBox(0F, 0F, 0F, THIN, 2, 8),
                    PartPose.offset(backSpikeX[i], backSpikeY[i], backSpikeZ[i]));
        }

        float[] legX = {5F, -5F, 5F, -5F};
        float[] legZ = {-3F, -3F, 9F, 9F};
        int[] legFootDepth = {4, 4, 5, 5};
        int[] legFootTexU = {49, 49, 48, 48};
        int[] legFootTexV = {21, 21, 20, 20};
        int[] upperLegDepth = {3, 3, 4, 4};
        int[] upperLegTexU = {7, 7, 6, 6};
        int[] upperLegTexV = {9, 9, 8, 8};
        for (int i = 0; i < 4; i++) {
            boolean rightSide = i == 0 || i == 2;
            float footX0 = rightSide ? 1F : -4F;
            root.addOrReplaceChild("leg_" + i,
                    CubeListBuilder.create().texOffs(legFootTexU[i], legFootTexV[i])
                            .addBox(footX0, 2F, -3F, 3, 2, legFootDepth[i]),
                    PartPose.offset(legX[i], 19F, legZ[i]));
            float upperX0 = rightSide ? 0F : -3F;
            root.addOrReplaceChild("upper_leg_" + i,
                    CubeListBuilder.create().texOffs(upperLegTexU[i], upperLegTexV[i])
                            .addBox(upperX0, -1F, -2F, 3, 3, upperLegDepth[i]),
                    PartPose.offset(legX[i], 19F, legZ[i]));
        }

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCCrocodileEntity crocodile, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.spikeEye.xRot = this.head.xRot;
        this.spikeEye.yRot = this.head.yRot;
        this.spikeEye1.xRot = this.head.xRot;
        this.spikeEye1.yRot = this.head.yRot;
        this.lJaw.yRot = this.head.yRot;
        this.lJaw2.yRot = this.head.yRot;
        this.uJaw.yRot = this.head.yRot;
        this.uJaw2.yRot = this.head.yRot;

        if (crocodile.isInWater()) {
            this.poseSwimming();
        } else if (crocodile.isResting()) {
            this.poseResting();
        } else {
            this.poseWalking(limbSwing, limbSwingAmount);
        }

        float tailSway = Mth.cos(limbSwing * LEG_SWING_SPEED) * TAIL_SWAY_AMOUNT * limbSwingAmount;
        this.tailA.yRot = tailSway;
        this.tailB.yRot = tailSway;
        this.tailC.yRot = tailSway;
        this.tailD.yRot = tailSway;
        for (ModelPart spike : this.tailSpikes) {
            spike.yRot = tailSway;
        }

        float bite = crocodile.getBiteProgress();
        float jawOpen = bite >= 0.5F ? 0.5F - (bite - 0.5F) : bite;
        this.uJaw.xRot = this.head.xRot - jawOpen;
        this.uJaw2.xRot = this.uJaw.xRot;
        this.lJaw.xRot = this.head.xRot + jawOpen / 2F;
        this.lJaw2.xRot = this.lJaw.xRot;
        for (ModelPart tooth : this.lowerTeeth) {
            tooth.xRot = this.lJaw.xRot;
            tooth.yRot = this.lJaw.yRot;
        }
        for (ModelPart tooth : this.upperTeeth) {
            tooth.xRot = this.uJaw.xRot;
            tooth.yRot = this.uJaw.yRot;
        }
    }

    private void poseSwimming() {
        for (int i = 0; i < 4; i++) {
            this.legs[i].setPos(SWIM_LEG_X[i], 18F, SWIM_LEG_Z[i]);
            this.legs[i].xRot = 0F;
            this.legs[i].yRot = Mth.PI;
            this.legs[i].zRot = 0F;
            this.upperLegs[i].setPos(SWIM_UPPER_LEG_X[i], 19F, SWIM_UPPER_LEG_Z[i]);
            this.upperLegs[i].xRot = Mth.HALF_PI;
            this.upperLegs[i].zRot = 0F;
        }
    }

    private void poseResting() {
        for (int i = 0; i < 4; i++) {
            this.legs[i].setPos(REST_LEG_X[i], REST_LEG_Y[i], REST_LEG_Z[i]);
            this.legs[i].xRot = 0F;
            this.legs[i].yRot = REST_LEG_YROT[i];
            this.legs[i].zRot = 0F;
            this.upperLegs[i].setPos(REST_UPPER_LEG_X[i], 17F, REST_UPPER_LEG_Z[i]);
            this.upperLegs[i].xRot = 0F;
            this.upperLegs[i].zRot = 0F;
        }
    }

    private void poseWalking(float limbSwing, float limbSwingAmount) {
        float[] swing = new float[4];
        swing[0] = Mth.cos(limbSwing * LEG_SWING_SPEED) * LEG_SWING_AMOUNT * limbSwingAmount;
        swing[1] = Mth.cos(limbSwing * LEG_SWING_SPEED + Mth.PI) * LEG_SWING_AMOUNT * limbSwingAmount;
        swing[2] = swing[1];
        swing[3] = swing[0];

        float sway = Mth.cos(limbSwing / LEG_SWAY_PERIOD) * LEG_SWAY_AMOUNT * limbSwingAmount;
        float[] swayPerLeg = {sway, -sway, sway, -sway};

        for (int i = 0; i < 4; i++) {
            this.legs[i].setPos(LEG_X[i], 19F, LEG_Z[i]);
            this.legs[i].xRot = swing[i];
            this.legs[i].yRot = 0F;
            this.legs[i].zRot = swayPerLeg[i];
            this.upperLegs[i].setPos(LEG_X[i], 19F, LEG_Z[i]);
            this.upperLegs[i].xRot = swing[i];
            this.upperLegs[i].zRot = swayPerLeg[i];
        }
    }
}