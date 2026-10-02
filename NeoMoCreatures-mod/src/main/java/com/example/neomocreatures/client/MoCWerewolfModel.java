package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCWerewolfEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Port of {@code drzhark.mocreatures.client.model.MoCModelWerewolf} — the wolf form only; the human
 * form reuses vanilla's own {@link net.minecraft.client.model.HumanoidModel}, since the original's
 * own human model is exactly that with nothing added.
 * <p>
 * The original keeps this model always active and paints it with a transparent texture to "hide" it
 * in human form; here the wolf parts are simply set invisible instead (same technique as the Ogre's
 * two head sets), which is why there's no equivalent of {@code wereblank.png} in this port.
 */
public class MoCWerewolfModel<T extends MoCWerewolfEntity> extends HierarchicalModel<T> {

    private static final float RADIAN = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart snout;
    private final ModelPart teethU;
    private final ModelPart teethL;
    private final ModelPart mouth;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart neck;
    private final ModelPart neck2;
    private final ModelPart sideburnL;
    private final ModelPart sideburnR;
    private final ModelPart chest;
    private final ModelPart abdomen;
    private final ModelPart tailA;
    private final ModelPart tailB;
    private final ModelPart tailC;
    private final ModelPart tailD;
    private final ModelPart legRA;
    private final ModelPart footR;
    private final ModelPart legRB;
    private final ModelPart legRC;
    private final ModelPart legLB;
    private final ModelPart footL;
    private final ModelPart legLC;
    private final ModelPart legLA;
    private final ModelPart armRB;
    private final ModelPart armRC;
    private final ModelPart armLB;
    private final ModelPart handR;
    private final ModelPart armRA;
    private final ModelPart armLA;
    private final ModelPart armLC;
    private final ModelPart handL;
    private final ModelPart[] fingersR = new ModelPart[5];
    private final ModelPart[] fingersL = new ModelPart[5];
    private final ModelPart[] allParts;

    public MoCWerewolfModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.nose = root.getChild("nose");
        this.snout = root.getChild("snout");
        this.teethU = root.getChild("teeth_u");
        this.teethL = root.getChild("teeth_l");
        this.mouth = root.getChild("mouth");
        this.earL = root.getChild("ear_l");
        this.earR = root.getChild("ear_r");
        this.neck = root.getChild("neck");
        this.neck2 = root.getChild("neck2");
        this.sideburnL = root.getChild("sideburn_l");
        this.sideburnR = root.getChild("sideburn_r");
        this.chest = root.getChild("chest");
        this.abdomen = root.getChild("abdomen");
        this.tailA = root.getChild("tail_a");
        this.tailB = root.getChild("tail_b");
        this.tailC = root.getChild("tail_c");
        this.tailD = root.getChild("tail_d");
        this.legRA = root.getChild("leg_r_a");
        this.footR = root.getChild("foot_r");
        this.legRB = root.getChild("leg_r_b");
        this.legRC = root.getChild("leg_r_c");
        this.legLB = root.getChild("leg_l_b");
        this.footL = root.getChild("foot_l");
        this.legLC = root.getChild("leg_l_c");
        this.legLA = root.getChild("leg_l_a");
        this.armRB = root.getChild("arm_r_b");
        this.armRC = root.getChild("arm_r_c");
        this.armLB = root.getChild("arm_l_b");
        this.handR = root.getChild("hand_r");
        this.armRA = root.getChild("arm_r_a");
        this.armLA = root.getChild("arm_l_a");
        this.armLC = root.getChild("arm_l_c");
        this.handL = root.getChild("hand_l");
        for (int i = 0; i < 5; i++) {
            this.fingersR[i] = root.getChild("finger_r_" + (i + 1));
            this.fingersL[i] = root.getChild("finger_l_" + (i + 1));
        }
        this.allParts = new ModelPart[] { this.head, this.nose, this.snout, this.teethU, this.teethL,
                this.mouth, this.earL, this.earR, this.neck, this.neck2, this.sideburnL, this.sideburnR,
                this.chest, this.abdomen, this.tailA, this.tailB, this.tailC, this.tailD, this.legRA,
                this.footR, this.legRB, this.legRC, this.legLB, this.footL, this.legLC, this.legLA,
                this.armRB, this.armRC, this.armLB, this.handR, this.armRA, this.armLA, this.armLC,
                this.handL, this.fingersR[0], this.fingersR[1], this.fingersR[2], this.fingersR[3],
                this.fingersR[4], this.fingersL[0], this.fingersL[1], this.fingersL[2], this.fingersL[3],
                this.fingersL[4] };
    }

    /** The texture layout is 64x128. */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -3F, -6F, 8, 8, 6),
                PartPose.offset(0F, -8F, -6F));
        root.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(44, 33).addBox(-1.5F, -1.7F, -12.3F, 3, 2, 7),
                PartPose.offsetAndRotation(0F, -8F, -6F, 0.2792527F, 0F, 0F));
        root.addOrReplaceChild("snout",
                CubeListBuilder.create().texOffs(0, 25).addBox(-2F, 2F, -12F, 4, 2, 6),
                PartPose.offset(0F, -8F, -6F));
        root.addOrReplaceChild("teeth_u",
                CubeListBuilder.create().texOffs(46, 18).addBox(-2F, 4.01F, -12F, 4, 2, 5),
                PartPose.offset(0F, -8F, -6F));
        root.addOrReplaceChild("teeth_l",
                CubeListBuilder.create().texOffs(20, 109).addBox(-1.5F, -12.5F, 2.01F, 3, 5, 2),
                PartPose.offsetAndRotation(0F, -8F, -6F, 2.530727F, 0F, 0F));
        root.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(42, 69).addBox(-1.5F, -12.5F, 0F, 3, 9, 2),
                PartPose.offsetAndRotation(0F, -8F, -6F, 2.530727F, 0F, 0F));
        root.addOrReplaceChild("ear_l",
                CubeListBuilder.create().texOffs(13, 14).addBox(0.5F, -7.5F, -1F, 3, 5, 1),
                PartPose.offsetAndRotation(0F, -8F, -6F, 0F, 0F, 0.1745329F));
        root.addOrReplaceChild("ear_r",
                CubeListBuilder.create().texOffs(22, 0).addBox(-3.5F, -7.5F, -1F, 3, 5, 1),
                PartPose.offsetAndRotation(0F, -8F, -6F, 0F, 0F, -0.1745329F));
        root.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(28, 0).addBox(-3.5F, -3F, -7F, 7, 8, 7),
                PartPose.offsetAndRotation(0F, -5F, -2F, -0.6025001F, 0F, 0F));
        root.addOrReplaceChild("neck2",
                CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -2F, -5F, 3, 4, 7),
                PartPose.offsetAndRotation(0F, -1F, -6F, -0.4537856F, 0F, 0F));
        root.addOrReplaceChild("sideburn_l",
                CubeListBuilder.create().texOffs(28, 33).addBox(3F, 0F, -2F, 2, 6, 6),
                PartPose.offsetAndRotation(0F, -8F, -6F, -0.2094395F, 0.418879F, -0.0872665F));
        root.addOrReplaceChild("sideburn_r",
                CubeListBuilder.create().texOffs(28, 45).addBox(-5F, 0F, -2F, 2, 6, 6),
                PartPose.offsetAndRotation(0F, -8F, -6F, -0.2094395F, -0.418879F, 0.0872665F));
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(20, 15).addBox(-4F, 0F, -7F, 8, 8, 10),
                PartPose.offsetAndRotation(0F, -6F, -2.5F, 0.641331F, 0F, 0F));
        root.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(0, 40).addBox(-3F, -8F, -8F, 6, 14, 8),
                PartPose.offsetAndRotation(0F, 4.5F, 5F, 0.2695449F, 0F, 0F));
        root.addOrReplaceChild("tail_a",
                CubeListBuilder.create().texOffs(52, 42).addBox(-1.5F, -1F, -2F, 3, 4, 3),
                PartPose.offsetAndRotation(0F, 9.5F, 6F, 1.064651F, 0F, 0F));
        root.addOrReplaceChild("tail_b",
                CubeListBuilder.create().texOffs(48, 49).addBox(-2F, 2F, -2F, 4, 6, 4),
                PartPose.offsetAndRotation(0F, 9.5F, 6F, 0.7504916F, 0F, 0F));
        root.addOrReplaceChild("tail_c",
                CubeListBuilder.create().texOffs(48, 59).addBox(-2F, 6.8F, -4.6F, 4, 6, 4),
                PartPose.offsetAndRotation(0F, 9.5F, 6F, 1.099557F, 0F, 0F));
        root.addOrReplaceChild("tail_d",
                CubeListBuilder.create().texOffs(52, 69).addBox(-1.5F, 9.8F, -4.1F, 3, 5, 3),
                PartPose.offsetAndRotation(0F, 9.5F, 6F, 1.099557F, 0F, 0F));

        root.addOrReplaceChild("leg_r_a",
                CubeListBuilder.create().texOffs(12, 64).addBox(-2.5F, -1.5F, -3.5F, 3, 8, 5),
                PartPose.offsetAndRotation(-3F, 9.5F, 3F, -0.8126625F, 0F, 0F));
        root.addOrReplaceChild("foot_r",
                CubeListBuilder.create().texOffs(14, 93).addBox(-2.506667F, 12.5F, -5F, 3, 2, 3),
                PartPose.offset(-3F, 9.5F, 3F));
        root.addOrReplaceChild("leg_r_b",
                CubeListBuilder.create().texOffs(14, 76).addBox(-1.9F, 4.2F, 0.5F, 2, 2, 5),
                PartPose.offsetAndRotation(-3F, 9.5F, 3F, -0.8445741F, 0F, 0F));
        root.addOrReplaceChild("leg_r_c",
                CubeListBuilder.create().texOffs(14, 83).addBox(-2F, 6.2F, 0.5F, 2, 8, 2),
                PartPose.offsetAndRotation(-3F, 9.5F, 3F, -0.2860688F, 0F, 0F));

        root.addOrReplaceChild("leg_l_a",
                CubeListBuilder.create().texOffs(0, 64).addBox(-0.5F, -1.5F, -3.5F, 3, 8, 5),
                PartPose.offsetAndRotation(3F, 9.5F, 3F, -0.8126625F, 0F, 0F));
        root.addOrReplaceChild("foot_l",
                CubeListBuilder.create().texOffs(0, 93).addBox(-0.5066667F, 12.5F, -5F, 3, 2, 3),
                PartPose.offset(3F, 9.5F, 3F));
        root.addOrReplaceChild("leg_l_b",
                CubeListBuilder.create().texOffs(0, 76).addBox(-0.1F, 4.2F, 0.5F, 2, 2, 5),
                PartPose.offsetAndRotation(3F, 9.5F, 3F, -0.8445741F, 0F, 0F));
        root.addOrReplaceChild("leg_l_c",
                CubeListBuilder.create().texOffs(0, 83).addBox(0F, 6.2F, 0.5F, 2, 8, 2),
                PartPose.offsetAndRotation(3F, 9.5F, 3F, -0.2860688F, 0F, 0F));

        root.addOrReplaceChild("arm_r_a",
                CubeListBuilder.create().texOffs(0, 108).addBox(-5F, -3F, -2F, 5, 5, 5),
                PartPose.offsetAndRotation(-4F, -4F, -2F, 0.6320364F, 0F, 0F));
        root.addOrReplaceChild("arm_r_b",
                CubeListBuilder.create().texOffs(48, 77).addBox(-3.5F, 1F, -1.5F, 4, 8, 4),
                PartPose.offsetAndRotation(-4F, -4F, -2F, 0.2617994F, 0F, 0.3490659F));
        root.addOrReplaceChild("arm_r_c",
                CubeListBuilder.create().texOffs(48, 112).addBox(-6F, 5F, 3F, 4, 7, 4),
                PartPose.offsetAndRotation(-4F, -4F, -2F, -0.3490659F, 0F, 0F));
        root.addOrReplaceChild("hand_r",
                CubeListBuilder.create().texOffs(32, 118).addBox(-6F, 12.5F, -1.5F, 4, 3, 4),
                PartPose.offset(-4F, -4F, -2F));

        root.addOrReplaceChild("arm_l_a",
                CubeListBuilder.create().texOffs(0, 98).addBox(0F, -3F, -2F, 5, 5, 5),
                PartPose.offsetAndRotation(4F, -4F, -2F, 0.6320364F, 0F, 0F));
        root.addOrReplaceChild("arm_l_b",
                CubeListBuilder.create().texOffs(48, 89).addBox(-0.5F, 1F, -1.5F, 4, 8, 4),
                PartPose.offsetAndRotation(4F, -4F, -2F, 0.2617994F, 0F, -0.3490659F));
        root.addOrReplaceChild("arm_l_c",
                CubeListBuilder.create().texOffs(48, 101).addBox(2F, 5F, 3F, 4, 7, 4),
                PartPose.offsetAndRotation(4F, -4F, -2F, -0.3490659F, 0F, 0F));
        root.addOrReplaceChild("hand_l",
                CubeListBuilder.create().texOffs(32, 111).addBox(2F, 12.5F, -1.5F, 4, 3, 4),
                PartPose.offset(4F, -4F, -2F));

        root.addOrReplaceChild("finger_r_1",
                CubeListBuilder.create().texOffs(8, 120).addBox(-3F, 15.5F, 1F, 1, 3, 1),
                PartPose.offset(-4F, -4F, -2F));
        root.addOrReplaceChild("finger_r_2",
                CubeListBuilder.create().texOffs(12, 124).addBox(-3.5F, 15.5F, -1.5F, 1, 3, 1),
                PartPose.offset(-4F, -4F, -2F));
        root.addOrReplaceChild("finger_r_3",
                CubeListBuilder.create().texOffs(12, 119).addBox(-4.8F, 15.5F, -1.5F, 1, 4, 1),
                PartPose.offset(-4F, -4F, -2F));
        root.addOrReplaceChild("finger_r_4",
                CubeListBuilder.create().texOffs(16, 119).addBox(-6F, 15.5F, -0.5F, 1, 4, 1),
                PartPose.offset(-4F, -4F, -2F));
        root.addOrReplaceChild("finger_r_5",
                CubeListBuilder.create().texOffs(16, 124).addBox(-6F, 15.5F, 1F, 1, 3, 1),
                PartPose.offset(-4F, -4F, -2F));

        root.addOrReplaceChild("finger_l_1",
                CubeListBuilder.create().texOffs(8, 124).addBox(2F, 15.5F, 1F, 1, 3, 1),
                PartPose.offset(4F, -4F, -2F));
        root.addOrReplaceChild("finger_l_2",
                CubeListBuilder.create().texOffs(0, 124).addBox(2.5F, 15.5F, -1.5F, 1, 3, 1),
                PartPose.offset(4F, -4F, -2F));
        root.addOrReplaceChild("finger_l_3",
                CubeListBuilder.create().texOffs(0, 119).addBox(3.8F, 15.5F, -1.5F, 1, 4, 1),
                PartPose.offset(4F, -4F, -2F));
        root.addOrReplaceChild("finger_l_4",
                CubeListBuilder.create().texOffs(4, 119).addBox(5F, 15.5F, -0.5F, 1, 4, 1),
                PartPose.offset(4F, -4F, -2F));
        root.addOrReplaceChild("finger_l_5",
                CubeListBuilder.create().texOffs(4, 124).addBox(5F, 15.5F, 1F, 1, 3, 1),
                PartPose.offset(4F, -4F, -2F));

        return LayerDefinition.create(mesh, 64, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T werewolf, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        boolean isHuman = werewolf.isHumanForm();
        for (ModelPart part : this.allParts) {
            part.visible = !isHuman;
        }
        if (isHuman) {
            return;
        }

        boolean hunched = werewolf.isHunched();
        float rLegXRot = ModelAnimations.walkSwingOpposite(limbSwing, limbSwingAmount, 0.8F);
        float lLegXRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.8F);

        this.head.yRot = netHeadYaw / RADIAN;

        if (!hunched) {
            this.head.y = -8F;
            this.head.z = -6F;
            this.head.xRot = headPitch / RADIAN;
            this.neck.xRot = -34F / RADIAN;
            this.neck.y = -5F;
            this.neck.z = -2F;
            this.neck2.y = -1F;
            this.neck2.z = -6F;
            this.chest.y = -6F;
            this.chest.z = -2.5F;
            this.chest.xRot = 36F / RADIAN;
            this.abdomen.xRot = 15F / RADIAN;
            this.legLA.z = 3F;
            this.armLA.y = -4F;
            this.armLA.z = -2F;
            this.tailA.y = 9.5F;
            this.tailA.z = 6F;
        } else {
            this.head.y = 0F;
            this.head.z = -11F;
            this.head.xRot = (15F + headPitch) / RADIAN;
            this.neck.xRot = -10F / RADIAN;
            this.neck.y = 2F;
            this.neck.z = -6F;
            this.neck2.y = 9F;
            this.neck2.z = -9F;
            this.chest.y = 1F;
            this.chest.z = -7.5F;
            this.chest.xRot = 60F / RADIAN;
            this.abdomen.xRot = 75F / RADIAN;
            this.legLA.z = 7F;
            this.armLA.y = 4.5F;
            this.armLA.z = -6F;
            this.tailA.y = 7.5F;
            this.tailA.z = 10F;
        }

        for (ModelPart headPart : new ModelPart[] { this.nose, this.snout, this.teethU, this.earL,
                this.earR, this.teethL, this.mouth, this.sideburnL, this.sideburnR }) {
            headPart.y = this.head.y;
            headPart.z = this.head.z;
        }
        for (ModelPart armPart : new ModelPart[] { this.armLB, this.armLC, this.handL,
                this.fingersL[0], this.fingersL[1], this.fingersL[2], this.fingersL[3], this.fingersL[4],
                this.armRA, this.armRB, this.armRC, this.handR,
                this.fingersR[0], this.fingersR[1], this.fingersR[2], this.fingersR[3], this.fingersR[4] }) {
            armPart.y = this.armLA.y;
            armPart.z = this.armLA.z;
        }
        for (ModelPart legPart : new ModelPart[] { this.legRA, this.legRB, this.legRC, this.footR,
                this.legLB, this.legLC, this.footL }) {
            legPart.z = this.legLA.z;
        }
        this.tailB.y = this.tailA.y;
        this.tailB.z = this.tailA.z;
        this.tailC.y = this.tailA.y;
        this.tailC.z = this.tailA.z;
        this.tailD.y = this.tailA.y;
        this.tailD.z = this.tailA.z;

        for (ModelPart headPart : new ModelPart[] { this.nose, this.snout, this.teethU, this.earL,
                this.earR, this.teethL, this.mouth }) {
            headPart.yRot = this.head.yRot;
        }
        this.teethL.xRot = this.head.xRot + 2.530727F;
        this.mouth.xRot = this.head.xRot + 2.530727F;
        this.sideburnL.xRot = -0.2094395F + this.head.xRot;
        this.sideburnL.yRot = 0.418879F + this.head.yRot;
        this.sideburnR.xRot = -0.2094395F + this.head.xRot;
        this.sideburnR.yRot = -0.418879F + this.head.yRot;
        this.nose.xRot = 0.2792527F + this.head.xRot;
        this.snout.xRot = this.head.xRot;
        this.teethU.xRot = this.head.xRot;
        this.earL.xRot = this.head.xRot;
        this.earR.xRot = this.head.xRot;

        this.legRA.xRot = -0.8126625F + rLegXRot;
        this.legRB.xRot = -0.8445741F + rLegXRot;
        this.legRC.xRot = -0.2860688F + rLegXRot;
        this.footR.xRot = rLegXRot;
        this.legLA.xRot = -0.8126625F + lLegXRot;
        this.legLB.xRot = -0.8445741F + lLegXRot;
        this.legLC.xRot = -0.2860688F + lLegXRot;
        this.footL.xRot = lLegXRot;

        this.armRA.zRot = -(Mth.cos(ageInTicks * 0.09F) * 0.05F) + 0.05F;
        this.armLA.zRot = Mth.cos(ageInTicks * 0.09F) * 0.05F - 0.05F;
        this.armRA.xRot = lLegXRot;
        this.armLA.xRot = rLegXRot;

        this.armRB.zRot = 0.3490659F + this.armRA.zRot;
        this.armLB.zRot = -0.3490659F + this.armLA.zRot;
        this.armRB.xRot = 0.2617994F + this.armRA.xRot;
        this.armLB.xRot = 0.2617994F + this.armLA.xRot;

        this.armRC.zRot = this.armRA.zRot;
        this.armLC.zRot = this.armLA.zRot;
        this.armRC.xRot = -0.3490659F + this.armRA.xRot;
        this.armLC.xRot = -0.3490659F + this.armLA.xRot;

        this.handR.zRot = this.armRA.zRot;
        this.handL.zRot = this.armLA.zRot;
        this.handR.xRot = this.armRA.xRot;
        this.handL.xRot = this.armLA.xRot;

        for (ModelPart finger : this.fingersR) {
            finger.xRot = this.armRA.xRot;
            finger.zRot = this.armRA.zRot;
        }
        for (ModelPart finger : this.fingersL) {
            finger.xRot = this.armLA.xRot;
            finger.zRot = this.armLA.zRot;
        }
    }
}