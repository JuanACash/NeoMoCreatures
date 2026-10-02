package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCBigGolemEntity;
import com.example.neomocreatures.entity.golem.GolemBody;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import com.example.neomocreatures.entity.golem.GolemState;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelGolem}. Each of the 23 body slots is a single
 * 8x8x8 cube; which of the 28 textures of golem.png it shows is picked at render time by shifting its
 * UVs, and empty slots are simply skipped.
 */
public class MoCBigGolemModel<T extends MoCBigGolemEntity> extends EntityModel<T> {

    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;
    /** Each cube texture is a 32x16 tile; tiles are laid out in columns of 8. */
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 16;
    private static final int TILES_PER_COLUMN = 8;

    private static final float DEG = Mth.DEG_TO_RAD;
    private static final float QUARTER_TURN = 45.0F * DEG;
    private static final float WALK_FREQUENCY = ModelAnimations.WALK_FREQUENCY;
    private static final float WALK_AMPLITUDE = 1.2F;
    private static final float ARM_SWAY_SPEED = 0.09F;
    private static final float ARM_SWAY_AMOUNT = 0.05F;
    private static final float THIGH_REST_PITCH = -20.0F * DEG;
    private static final float CHEST_CLOSED_Z = -4.0F;
    private static final float CHEST_OPEN_Z = -7.0F;

    /** Original: both arms pitch -90 degrees while throwing a cube. */
    private static final float THROW_ARM_PITCH = -90.0F * DEG;

    private static final String[] SLOT_NAMES = {
            "chest_left_1", "chest_left_2", "chest_right_1", "chest_right_2", "back",
            "back_left_1", "back_left_2", "back_right_1", "back_right_2",
            "left_shoulder", "left_arm", "left_hand", "right_shoulder", "right_arm", "right_hand",
            "left_thigh", "left_knee", "left_foot", "right_thigh", "right_knee", "right_foot",
            "groin", "butt"};

    private final ModelPart[] cubes = new ModelPart[GolemBody.SLOT_COUNT];
    private final ModelPart head;
    private final ModelPart headAngry;
    private final ModelPart chest;
    private final ModelPart chestAngry;

    private byte[] cubeTextures = new byte[GolemBody.SLOT_COUNT];
    private boolean angry;
    private float yOffset;

    public MoCBigGolemModel(ModelPart root) {
        for (int slot = 0; slot < GolemBody.SLOT_COUNT; slot++) {
            this.cubes[slot] = root.getChild(SLOT_NAMES[slot]);
        }
        this.head = root.getChild("head");
        this.headAngry = root.getChild("head_angry");
        this.chest = root.getChild("chest");
        this.chestAngry = root.getChild("chest_angry");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Chest plates
        addCube(root, 0, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -97.0F * DEG, -40.0F * DEG, 0.0F));
        addCube(root, 1, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -55.0F * DEG, -41.0F * DEG, 0.0F));
        addCube(root, 2, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -97.0F * DEG, 40.0F * DEG, 0.0F));
        addCube(root, 3, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -55.0F * DEG, 41.0F * DEG, 0.0F));
        // Back (slot 4 is the core)
        addCube(root, 4, -7.0F, -14.0F, -1.0F, PartPose.offsetAndRotation(0.0F, 6.0F, 3.0F, 0.0F, QUARTER_TURN, 0.0F));
        addCube(root, 5, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.919862F, 0.6981317F, 0.0F));
        addCube(root, 6, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.183003F, 0.6981317F, 0.0F));
        addCube(root, 7, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.919862F, -0.6981317F, 0.0F));
        addCube(root, 8, -4.0F, 3.0F, -4.0F, PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 1.183003F, -0.6981317F, 0.0F));
        // Left arm
        addCube(root, 9, 0.0F, -2.0F, -4.0F, PartPose.offsetAndRotation(8.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.6981317F));
        addCube(root, 10, 2.0F, 4.0F, -4.0F, PartPose.offsetAndRotation(8.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.2094395F));
        addCube(root, 11, 4.5F, 11.0F, -4.0F, PartPose.offset(8.0F, -3.0F, 0.0F));
        // Right arm
        addCube(root, 12, -8.0F, -2.0F, -4.0F, PartPose.offsetAndRotation(-8.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.6981317F));
        addCube(root, 13, -10.0F, 4.0F, -4.0F, PartPose.offsetAndRotation(-8.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.2094395F));
        addCube(root, 14, -12.5F, 11.0F, -4.0F, PartPose.offset(-8.0F, -3.0F, 0.0F));
        // Left leg
        addCube(root, 15, -3.5F, 0.0F, -4.0F, PartPose.offsetAndRotation(5.0F, 4.0F, 0.0F, -0.3490659F, 0.0F, 0.0F));
        addCube(root, 16, -4.0F, 6.0F, -7.0F, PartPose.offset(5.0F, 4.0F, 0.0F));
        addCube(root, 17, -3.5F, 12.0F, -5.0F, PartPose.offset(5.0F, 4.0F, 0.0F));
        // Right leg
        addCube(root, 18, -4.5F, 0.0F, -4.0F, PartPose.offsetAndRotation(-5.0F, 4.0F, 0.0F, -0.3490659F, 0.0F, 0.0F));
        addCube(root, 19, -4.0F, 6.0F, -7.0F, PartPose.offset(-5.0F, 4.0F, 0.0F));
        addCube(root, 20, -4.5F, 12.0F, -5.0F, PartPose.offset(-5.0F, 4.0F, 0.0F));
        // Hips
        addCube(root, 21, 0.0F, -4.0F, -8.0F, PartPose.offsetAndRotation(0.0F, 6.0F, 3.0F, 0.0F, QUARTER_TURN, 0.0F));
        addCube(root, 22, -4.0F, -4.0F, -4.0F, PartPose.offsetAndRotation(0.0F, 6.0F, 3.0F, -0.7435722F, 0.0F, 0.0F));

        // Head and glowing chest core, normal and angry (red) textures
        PartPose headPose = PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, QUARTER_TURN, 0.0F);
        PartPose chestPose = PartPose.offsetAndRotation(0.0F, -3.0F, CHEST_CLOSED_Z, 0.0F, QUARTER_TURN, 0.0F);
        addPart(root, "head", 96, 64, headPose);
        addPart(root, "head_angry", 96, 80, headPose);
        addPart(root, "chest", 96, 96, chestPose);
        addPart(root, "chest_angry", 96, 112, chestPose);

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /** Every body cube is built on the first texture tile; renderToBuffer shifts it to the right one. */
    private static void addCube(PartDefinition root, int slot, float x, float y, float z, PartPose pose) {
        root.addOrReplaceChild(SLOT_NAMES[slot], CubeListBuilder.create().texOffs(0, 0).addBox(x, y, z, 8, 8, 8), pose);
    }

    private static void addPart(PartDefinition root, String name, int texU, int texV, PartPose pose) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(texU, texV).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8), pose);
    }

    @Override
    public void setupAnim(T golem, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.cubeTextures = golem.getCubeTextures();
        this.angry = golem.getGolemState().isAngry();
        this.yOffset = computeYOffset(this.cubeTextures);

        for (ModelPart cube : this.cubes) {
            cube.resetPose();
        }
        this.head.resetPose();
        this.headAngry.resetPose();
        this.chest.resetPose();
        this.chestAngry.resetPose();

        float headYaw = QUARTER_TURN + netHeadYaw * DEG;
        this.head.yRot = headYaw;
        this.headAngry.yRot = headYaw;

        // Original: the chest core spins while the golem is still missing cubes.
        if (golem.getGolemState() == GolemState.SUMMONING && hasEmptySlot(this.cubeTextures)) {
            float spin = QUARTER_TURN + ageInTicks / 2.0F;
            this.chest.yRot = spin;
            this.chestAngry.yRot = spin;
        }

        if (golem.isChestOpen()) {
            this.chest.z = CHEST_OPEN_Z;
            this.chestAngry.z = CHEST_OPEN_Z;
            this.cubes[0].yRot = -60.0F * DEG;
            this.cubes[1].yRot = -55.0F * DEG;
            this.cubes[2].yRot = 60.0F * DEG;
            this.cubes[3].yRot = 55.0F * DEG;
        }

        float walkPhase = limbSwing * WALK_FREQUENCY;
        float rightLegPitch = Mth.cos(walkPhase + Mth.PI) * WALK_AMPLITUDE * limbSwingAmount;
        float leftLegPitch = Mth.cos(walkPhase) * WALK_AMPLITUDE * limbSwingAmount;
        this.poseLeg(GolemBody.LEFT_THIGH, GolemBody.LEFT_KNEE, GolemBody.LEFT_FOOT, leftLegPitch);
        this.poseLeg(GolemBody.RIGHT_THIGH, GolemBody.RIGHT_KNEE, GolemBody.RIGHT_FOOT, rightLegPitch);

        float sway = Mth.cos(ageInTicks * ARM_SWAY_SPEED) * ARM_SWAY_AMOUNT;
        boolean throwing = golem.isThrowing();
        // Arms swing opposite to their own-side leg, or point straight ahead while throwing.
        float rightArmPitch = throwing ? THROW_ARM_PITCH : leftLegPitch;
        float leftArmPitch = throwing ? THROW_ARM_PITCH : rightLegPitch;
        float rightArmRoll = throwing ? 0.0F : -sway + ARM_SWAY_AMOUNT;
        float leftArmRoll = throwing ? 0.0F : sway - ARM_SWAY_AMOUNT;
        this.poseArm(GolemBody.RIGHT_SHOULDER, GolemBody.RIGHT_ARM, GolemBody.RIGHT_HAND, 1.0F, rightArmPitch, rightArmRoll);
        this.poseArm(GolemBody.LEFT_SHOULDER, GolemBody.LEFT_ARM, GolemBody.LEFT_HAND, -1.0F, leftArmPitch, leftArmRoll);
    }

    private void poseLeg(int thigh, int knee, int foot, float pitch) {
        this.cubes[thigh].xRot = THIGH_REST_PITCH + pitch;
        this.cubes[knee].xRot = pitch;
        this.cubes[foot].xRot = pitch;
    }

    /** side: +1 for the right arm, -1 for the left one (mirrors the rest angles). */
    private void poseArm(int shoulder, int arm, int hand, float side, float pitch, float roll) {
        this.cubes[shoulder].zRot = side * 40.0F * DEG + roll;
        this.cubes[shoulder].xRot = pitch;
        this.cubes[arm].zRot = side * 12.0F * DEG + roll;
        this.cubes[arm].xRot = pitch;
        this.cubes[hand].zRot = roll;
        this.cubes[hand].xRot = pitch;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0.0F, this.yOffset, 0.0F);

        for (int slot = 0; slot < GolemBody.SLOT_COUNT; slot++) {
            int texture = this.cubeTextures[slot];
            if (texture != GolemBody.EMPTY_TEXTURE) {
                float u = (float) (texture / TILES_PER_COLUMN * TILE_WIDTH) / TEXTURE_WIDTH;
                float v = (float) (texture % TILES_PER_COLUMN * TILE_HEIGHT) / TEXTURE_HEIGHT;
                this.cubes[slot].render(poseStack, new UvOffsetVertexConsumer(buffer, u, v), packedLight, packedOverlay, color);
            }
        }

        (this.angry ? this.headAngry : this.head).render(poseStack, buffer, packedLight, packedOverlay, color);
        (this.angry ? this.chestAngry : this.chest).render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    /** Original: getAdjustedYOffset() — sinks the model when its legs are missing, so it never floats. */
    private static float computeYOffset(byte[] textures) {
        if (has(textures, GolemBody.LEFT_FOOT) || has(textures, GolemBody.RIGHT_FOOT)) {
            return 0.0F;
        }
        if (has(textures, GolemBody.LEFT_KNEE) || has(textures, GolemBody.RIGHT_KNEE)) {
            return 0.4F;
        }
        if (has(textures, GolemBody.LEFT_THIGH) || has(textures, GolemBody.RIGHT_THIGH)) {
            return 0.7F;
        }
        if (has(textures, 1) || has(textures, 3)) {
            return 0.8F;
        }
        return 1.45F;
    }

    private static boolean has(byte[] textures, int slot) {
        return textures[slot] != GolemBody.EMPTY_TEXTURE;
    }

    private static boolean hasEmptySlot(byte[] textures) {
        for (byte texture : textures) {
            if (texture == GolemBody.EMPTY_TEXTURE) {
                return true;
            }
        }
        return false;
    }
}