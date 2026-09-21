package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCMantaRayEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelRay} as the original draws it for the
 * manta ray: the body, two side fins, a tail and seven wing segments per side, without the
 * stingray's eyes.
 */
public class MoCMantaRayModel extends HierarchicalModel<MoCMantaRayEntity> {

    /**
     * One wing segment, from the body outward: texture offset, distance of its inner edge from the
     * wing root, width, and where it starts along the body and how deep it is (front to back).
     */
    private record WingSegment(int u, int v, float start, int width, float zStart, int depth) {
    }

    private static final WingSegment[] WING_SEGMENTS = {
            new WingSegment(0, 0, 0F, 3, -5F, 10),
            new WingSegment(2, 2, 3F, 3, -4F, 8),
            new WingSegment(5, 4, 6F, 2, -3F, 6),
            new WingSegment(6, 5, 8F, 2, -2.5F, 5),
            new WingSegment(7, 6, 10F, 2, -2F, 4),
            new WingSegment(8, 7, 12F, 2, -1.5F, 3),
            new WingSegment(9, 8, 14F, 2, -1F, 2)
    };

    /** Peak angle, in radians, of the innermost wing segment at full limb swing. */
    private static final float WING_FLAP_AMPLITUDE = 1.5F;
    /** Each segment further out flaps this much more than the one before (1 / 20 = 5% more). */
    private static final float WING_TIP_LAG = 1.0F / 20.0F;
    private static final float WING_BEAT_SPEED = 0.6662F;
    private static final float TAIL_SWAY = 0.7F;
    private static final float WING_ROOT_X = 4F;

    private final ModelPart root;
    private final ModelPart tail;
    private final ModelPart[] rightWings = new ModelPart[WING_SEGMENTS.length];
    private final ModelPart[] leftWings = new ModelPart[WING_SEGMENTS.length];

    public MoCMantaRayModel(ModelPart root) {
        this.root = root;
        this.tail = root.getChild("tail");
        for (int i = 0; i < WING_SEGMENTS.length; i++) {
            this.rightWings[i] = root.getChild(wingName("right", i));
            this.leftWings[i] = root.getChild(wingName("left", i));
        }
    }

    private static String wingName(String side, int index) {
        return side + "_wing_" + index;
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

        root.addOrReplaceChild("right_fin",
                CubeListBuilder.create().texOffs(10, 26).addBox(-0.5F, -1F, -4F, 1, 2, 4),
                PartPose.offset(-3F, 22F, -4.8F));
        root.addOrReplaceChild("left_fin",
                CubeListBuilder.create().texOffs(0, 26).addBox(-0.5F, -1F, -4F, 1, 2, 4),
                PartPose.offset(3F, 22F, -4.8F));

        for (int i = 0; i < WING_SEGMENTS.length; i++) {
            WingSegment segment = WING_SEGMENTS[i];
            root.addOrReplaceChild(wingName("right", i),
                    CubeListBuilder.create().texOffs(segment.u(), segment.v())
                            .addBox(-(segment.start() + segment.width()), -0.5F, segment.zStart(),
                                    segment.width(), 1, segment.depth()),
                    PartPose.offset(-WING_ROOT_X, 22F, 1F));
            root.addOrReplaceChild(wingName("left", i),
                    CubeListBuilder.create().mirror().texOffs(segment.u(), segment.v())
                            .addBox(segment.start(), -0.5F, segment.zStart(),
                                    segment.width(), 1, segment.depth()),
                    PartPose.offset(WING_ROOT_X, 22F, 1F));
        }

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCMantaRayEntity ray, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float swing = Mth.cos(limbSwing * WING_BEAT_SPEED);
        float flap = swing * WING_FLAP_AMPLITUDE * limbSwingAmount;
        for (int i = 0; i < WING_SEGMENTS.length; i++) {
            this.rightWings[i].zRot = flap;
            this.leftWings[i].zRot = -flap;
            flap += flap * WING_TIP_LAG;
        }
        this.tail.yRot = swing * TAIL_SWAY * limbSwingAmount;
    }
}