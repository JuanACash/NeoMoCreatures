package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCWildWolfEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Port of {@code drzhark.mocreatures.client.model.MoCModelWolf}: a wolf with an alternate open-mouth
 *  pose (shown while {@code mouthCounter != 0}) and a tail that wags on its own timer instead of
 *  always following the walk cycle. */
public class MoCWildWolfModel<T extends MoCWildWolfEntity> extends HierarchicalModel<T> {

    private static final float RADIAN = 57.29578F;

    private final ModelPart head;
    private final ModelPart mouthB;
    private final ModelPart nose2;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart lSide;
    private final ModelPart rSide;
    private final ModelPart nose;
    private final ModelPart mouth;
    private final ModelPart uTeeth;
    private final ModelPart lTeeth;
    private final ModelPart mouthOpen;
    private final ModelPart rEar;
    private final ModelPart lEar;
    private final ModelPart chest;
    private final ModelPart body;
    private final ModelPart tailA;
    private final ModelPart tailB;
    private final ModelPart tailC;
    private final ModelPart tailD;
    private final ModelPart leg1A;
    private final ModelPart leg1B;
    private final ModelPart leg1C;
    private final ModelPart leg2A;
    private final ModelPart leg2B;
    private final ModelPart leg2C;
    private final ModelPart leg3A;
    private final ModelPart leg3B;
    private final ModelPart leg3C;
    private final ModelPart leg3D;
    private final ModelPart leg4A;
    private final ModelPart leg4B;
    private final ModelPart leg4C;
    private final ModelPart leg4D;
    private final ModelPart root;

    private boolean openMouth;

    public MoCWildWolfModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.mouthB = root.getChild("mouth_b");
        this.nose2 = root.getChild("nose2");
        this.neck = root.getChild("neck");
        this.neck2 = root.getChild("neck2");
        this.lSide = root.getChild("l_side");
        this.rSide = root.getChild("r_side");
        this.nose = root.getChild("nose");
        this.mouth = root.getChild("mouth");
        this.uTeeth = root.getChild("u_teeth");
        this.lTeeth = root.getChild("l_teeth");
        this.mouthOpen = root.getChild("mouth_open");
        this.rEar = root.getChild("r_ear");
        this.lEar = root.getChild("l_ear");
        this.chest = root.getChild("chest");
        this.body = root.getChild("body");
        this.tailA = root.getChild("tail_a");
        this.tailB = root.getChild("tail_b");
        this.tailC = root.getChild("tail_c");
        this.tailD = root.getChild("tail_d");
        this.leg1A = root.getChild("leg1_a");
        this.leg1B = root.getChild("leg1_b");
        this.leg1C = root.getChild("leg1_c");
        this.leg2A = root.getChild("leg2_a");
        this.leg2B = root.getChild("leg2_b");
        this.leg2C = root.getChild("leg2_c");
        this.leg3A = root.getChild("leg3_a");
        this.leg3B = root.getChild("leg3_b");
        this.leg3C = root.getChild("leg3_c");
        this.leg3D = root.getChild("leg3_d");
        this.leg4A = root.getChild("leg4_a");
        this.leg4B = root.getChild("leg4_b");
        this.leg4C = root.getChild("leg4_c");
        this.leg4D = root.getChild("leg4_d");
    }

    /** The texture layout is 64x128. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -3F, -6F, 8, 8, 6),
                PartPose.offset(0F, 7F, -10F));
        root.addOrReplaceChild("mouth_b",
                CubeListBuilder.create().texOffs(16, 33).addBox(-2F, 4F, -7F, 4, 1, 2),
                PartPose.offset(0F, 7F, -10F));
        root.addOrReplaceChild("nose2",
                CubeListBuilder.create().texOffs(0, 25).addBox(-2F, 2F, -12F, 4, 2, 6),
                PartPose.offset(0F, 7F, -10F));
        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(28, 0).addBox(-3.5F, -3F, -7F, 7, 8, 7),
                PartPose.offsetAndRotation(0F, 10F, -6F, -0.4537856F, 0F, 0F));
        root.addOrReplaceChild("neck2",
                CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -2F, -5F, 3, 4, 7),
                PartPose.offsetAndRotation(0F, 14F, -10F, -0.4537856F, 0F, 0F));
        root.addOrReplaceChild("l_side",
                CubeListBuilder.create().texOffs(28, 33).addBox(3F, -0.5F, -2F, 2, 6, 6),
                PartPose.offsetAndRotation(0F, 7F, -10F, -0.2094395F, 0.418879F, -0.0872665F));
        root.addOrReplaceChild("r_side",
                CubeListBuilder.create().texOffs(28, 45).addBox(-5F, -0.5F, -2F, 2, 6, 6),
                PartPose.offsetAndRotation(0F, 7F, -10F, -0.2094395F, -0.418879F, 0.0872665F));
        root.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(44, 33).addBox(-1.5F, -1.8F, -12.4F, 3, 2, 7),
                PartPose.offsetAndRotation(0F, 7F, -10F, 0.2792527F, 0F, 0F));
        root.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(1, 34).addBox(-2F, 4F, -11.5F, 4, 1, 5),
                PartPose.offset(0F, 7F, -10F));
        root.addOrReplaceChild("u_teeth",
                CubeListBuilder.create().texOffs(46, 18).addBox(-2F, 4F, -12F, 4, 2, 5),
                PartPose.offset(0F, 7F, -10F));
        root.addOrReplaceChild("l_teeth",
                CubeListBuilder.create().texOffs(20, 109).addBox(-1.5F, -12.9F, 1.2F, 3, 5, 2),
                PartPose.offsetAndRotation(0F, 7F, -10F, 2.5307274F, 0F, 0F));
        root.addOrReplaceChild("mouth_open",
                CubeListBuilder.create().texOffs(42, 69).addBox(-1.5F, -12.9F, -0.81F, 3, 9, 2),
                PartPose.offsetAndRotation(0F, 7F, -10F, 2.5307274F, 0F, 0F));
        root.addOrReplaceChild("r_ear",
                CubeListBuilder.create().texOffs(22, 0).addBox(-3.5F, -7F, -1.5F, 3, 5, 1),
                PartPose.offsetAndRotation(0F, 7F, -10F, 0F, 0F, -0.1745329F));
        root.addOrReplaceChild("l_ear",
                CubeListBuilder.create().texOffs(13, 14).addBox(0.5F, -7F, -1.5F, 3, 5, 1),
                PartPose.offsetAndRotation(0F, 7F, -10F, 0F, 0F, 0.1745329F));
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(20, 15).addBox(-4F, -11F, -12F, 8, 8, 10),
                PartPose.offsetAndRotation(0F, 5F, 2F, Mth.HALF_PI, 0F, 0F));
        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 40).addBox(-3F, -8F, -9F, 6, 16, 8),
                PartPose.offsetAndRotation(0F, 6.5F, 2F, Mth.HALF_PI, 0F, 0F));
        root.addOrReplaceChild("tail_a",
                CubeListBuilder.create().texOffs(52, 42).addBox(-1.5F, 0F, -1.5F, 3, 4, 3),
                PartPose.offsetAndRotation(0F, 8.5F, 9F, 1.064651F, 0F, 0F));
        root.addOrReplaceChild("tail_b",
                CubeListBuilder.create().texOffs(48, 49).addBox(-2F, 3F, -1F, 4, 6, 4),
                PartPose.offsetAndRotation(0F, 8.5F, 9F, 0.7504916F, 0F, 0F));
        root.addOrReplaceChild("tail_c",
                CubeListBuilder.create().texOffs(48, 59).addBox(-2F, 7.8F, -4.1F, 4, 6, 4),
                PartPose.offsetAndRotation(0F, 8.5F, 9F, 1.099557F, 0F, 0F));
        root.addOrReplaceChild("tail_d",
                CubeListBuilder.create().texOffs(52, 69).addBox(-1.5F, 9.8F, -3.6F, 3, 5, 3),
                PartPose.offsetAndRotation(0F, 8.5F, 9F, 1.099557F, 0F, 0F));

        root.addOrReplaceChild("leg1_a",
                CubeListBuilder.create().texOffs(28, 57).addBox(0.01F, -4F, -2.5F, 2, 8, 4),
                PartPose.offsetAndRotation(4F, 12.5F, -5.5F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("leg1_b",
                CubeListBuilder.create().texOffs(28, 69).addBox(0F, 3.2F, 0.5F, 2, 8, 2),
                PartPose.offsetAndRotation(4F, 12.5F, -5.5F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg1_c",
                CubeListBuilder.create().texOffs(28, 79).addBox(-0.5066667F, 9.5F, -2.5F, 3, 2, 3),
                PartPose.offset(4F, 12.5F, -5.5F));

        root.addOrReplaceChild("leg2_a",
                CubeListBuilder.create().texOffs(28, 84).addBox(-2.01F, -4F, -2.5F, 2, 8, 4),
                PartPose.offsetAndRotation(-4F, 12.5F, -5.5F, 0.2617994F, 0F, 0F));
        root.addOrReplaceChild("leg2_b",
                CubeListBuilder.create().texOffs(28, 96).addBox(-2F, 3.2F, 0.5F, 2, 8, 2),
                PartPose.offsetAndRotation(-4F, 12.5F, -5.5F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg2_c",
                CubeListBuilder.create().texOffs(28, 106).addBox(-2.506667F, 9.5F, -2.5F, 3, 2, 3),
                PartPose.offset(-4F, 12.5F, -5.5F));

        root.addOrReplaceChild("leg3_a",
                CubeListBuilder.create().texOffs(0, 64).addBox(0F, -3.8F, -3.5F, 2, 7, 5),
                PartPose.offsetAndRotation(3F, 12.5F, 7F, -0.3665191F, 0F, 0F));
        root.addOrReplaceChild("leg3_b",
                CubeListBuilder.create().texOffs(0, 76).addBox(-0.1F, 1.9F, -1.8F, 2, 2, 5),
                PartPose.offsetAndRotation(3F, 12.5F, 7F, -0.7330383F, 0F, 0F));
        root.addOrReplaceChild("leg3_c",
                CubeListBuilder.create().texOffs(0, 83).addBox(0F, 3.2F, 0F, 2, 8, 2),
                PartPose.offsetAndRotation(3F, 12.5F, 7F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg3_d",
                CubeListBuilder.create().texOffs(0, 93).addBox(-0.5066667F, 9.5F, -3F, 3, 2, 3),
                PartPose.offset(3F, 12.5F, 7F));

        root.addOrReplaceChild("leg4_a",
                CubeListBuilder.create().texOffs(14, 64).addBox(-2F, -3.8F, -3.5F, 2, 7, 5),
                PartPose.offsetAndRotation(-3F, 12.5F, 7F, -0.3665191F, 0F, 0F));
        root.addOrReplaceChild("leg4_b",
                CubeListBuilder.create().texOffs(14, 76).addBox(-1.9F, 1.9F, -1.8F, 2, 2, 5),
                PartPose.offsetAndRotation(-3F, 12.5F, 7F, -0.7330383F, 0F, 0F));
        root.addOrReplaceChild("leg4_c",
                CubeListBuilder.create().texOffs(14, 83).addBox(-2F, 3.2F, 0F, 2, 8, 2),
                PartPose.offsetAndRotation(-3F, 12.5F, 7F, -0.1745329F, 0F, 0F));
        root.addOrReplaceChild("leg4_d",
                CubeListBuilder.create().texOffs(14, 93).addBox(-2.506667F, 9.5F, -3F, 3, 2, 3),
                PartPose.offset(-3F, 12.5F, 7F));

        return LayerDefinition.create(mesh, 64, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T wolf, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.openMouth = wolf.getMouthCounter() != 0;

        this.head.xRot = headPitch / RADIAN;
        this.head.yRot = netHeadYaw / RADIAN;

        float lLegX = Mth.cos(limbSwing * 0.6662F) * 0.8F * limbSwingAmount;
        float rLegX = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 0.8F * limbSwingAmount;

        for (ModelPart part : new ModelPart[] { this.mouth, this.mouthB, this.nose2, this.uTeeth, this.rEar, this.lEar }) {
            part.xRot = this.head.xRot;
            part.yRot = this.head.yRot;
        }
        this.mouthOpen.xRot = 2.5307274F + this.head.xRot;
        this.mouthOpen.yRot = this.head.yRot;
        this.lTeeth.xRot = 2.5307274F + this.head.xRot;
        this.lTeeth.yRot = this.head.yRot;
        this.nose.xRot = 0.27925268F + this.head.xRot;
        this.nose.yRot = this.head.yRot;
        this.lSide.xRot = -0.2094395F + this.head.xRot;
        this.lSide.yRot = 0.418879F + this.head.yRot;
        this.rSide.xRot = -0.2094395F + this.head.xRot;
        this.rSide.yRot = -0.418879F + this.head.yRot;

        this.leg1A.xRot = 0.2617994F + lLegX;
        this.leg1B.xRot = -0.17453292F + lLegX;
        this.leg1C.xRot = lLegX;
        this.leg2A.xRot = 0.2617994F + rLegX;
        this.leg2B.xRot = -0.17453292F + rLegX;
        this.leg2C.xRot = rLegX;
        this.leg3A.xRot = -0.36651915F + rLegX;
        this.leg3B.xRot = -0.7330383F + rLegX;
        this.leg3C.xRot = -0.17453292F + rLegX;
        this.leg3D.xRot = rLegX;
        this.leg4A.xRot = -0.36651915F + lLegX;
        this.leg4B.xRot = -0.7330383F + lLegX;
        this.leg4C.xRot = -0.17453292F + lLegX;
        this.leg4D.xRot = lLegX;

        // Tail wags on its own timer instead of tracking the walk cycle while active.
        float tailMove = -1.3089F + limbSwingAmount * 1.5F;
        if (wolf.getTailCounter() != 0) {
            this.tailA.yRot = Mth.cos(ageInTicks * 0.5F);
            tailMove = 0.0F;
        } else {
            this.tailA.yRot = 0.0F;
        }
        this.tailA.xRot = 1.0647582F - tailMove;
        this.tailB.xRot = 0.75056726F - tailMove;
        this.tailC.xRot = 1.0996684F - tailMove;
        this.tailD.xRot = 1.0996684F - tailMove;
        this.tailB.yRot = this.tailA.yRot;
        this.tailC.yRot = this.tailA.yRot;
        this.tailD.yRot = this.tailA.yRot;

        this.mouth.visible = !this.openMouth;
        this.mouthB.visible = !this.openMouth;
        this.mouthOpen.visible = this.openMouth;
        this.uTeeth.visible = this.openMouth;
        this.lTeeth.visible = this.openMouth;
    }
}