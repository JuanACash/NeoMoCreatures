package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * 1:1 port of drzhark.mocreatures.client.model.MoCModelWyvern (Techne / ModelRenderer)
 * to the modern HierarchicalModel / PartDefinition format.
 *
 * NOTE: this only ports the "base" wyvern body — the parts that are always shown
 * (armor is 0, isSaddled/isChested false). The original model also has iron/gold/
 * diamond helmet+armor parts, a saddle, storage chest, mouth rod and control ropes,
 * all of which are conditionally hidden (showModel = false) unless the wyvern is
 * ridden/equipped. If you need those too, say so and I'll add them the same way.
 */
public class MoCWyvernModel extends HierarchicalModel<MoCWyvernEntity> {

    // Minecraft's radian-per-degree constant, same value the original Techne model used.
    private static final float R = ModelAnimations.DEGREES_PER_RADIAN;

    private final ModelPart root;
    /**
     * Ported from the original's yOffset field: set here (during setupAnim,
     * where we already know the sitting state) and applied in
     * renderToBuffer() below — exactly matching the original's
     * getAdjustedYOffset()/yOffset mechanism, instead of guessing at a
     * renderer-level translate.
     */
    private float yOffset;

    // tail
    private final ModelPart back1;
    private final ModelPart tail;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;

    // chest / neck plates (standalone, root-level, like the original)
    private final ModelPart chest;
    private final ModelPart neckplate3;
    private final ModelPart neck3;

    // head assembly
    private final ModelPart mainHead;
    private final ModelPart neck2;
    private final ModelPart neckplate2;
    private final ModelPart neck1;
    private final ModelPart neckplate1;
    private final ModelPart head;
    private final ModelPart snout;
    private final ModelPart beak;
    private final ModelPart headplate;
    private final ModelPart righteyesock;
    private final ModelPart lefteyesock;
    private final ModelPart jaw;
    private final ModelPart leftupjaw;
    private final ModelPart rightupjaw;
    private final ModelPart rightearskin;
    private final ModelPart leftearskin;
    private final ModelPart rightspine1;
    private final ModelPart rightspine2;
    private final ModelPart rightspine3;
    private final ModelPart leftspine1;
    private final ModelPart leftspine2;
    private final ModelPart leftspine3;

    // torso / shoulders
    private final ModelPart torso;
    private final ModelPart saddle;
    private final ModelPart storage;
    // Armor — three full sets (iron/gold/diamond), each: helmet + 2 helmet
    // horns + helmet snout + chest armor + 2 shoulder pads + 2 leg armor.
    private final ModelPart ironHelmet;
    private final ModelPart ironHelmetHorn1;
    private final ModelPart ironHelmetHorn2;
    private final ModelPart ironHelmetSnout;
    private final ModelPart ironChestArmor;
    private final ModelPart ironLeftShoulder;
    private final ModelPart ironRightShoulder;
    private final ModelPart ironLeftLegArmor;
    private final ModelPart ironRightLegArmor;
    private final ModelPart goldHelmet;
    private final ModelPart goldHelmetHorn1;
    private final ModelPart goldHelmetHorn2;
    private final ModelPart goldHelmetSnout;
    private final ModelPart goldChestArmor;
    private final ModelPart goldLeftShoulder;
    private final ModelPart goldRightShoulder;
    private final ModelPart goldLeftLegArmor;
    private final ModelPart goldRightLegArmor;
    private final ModelPart diamondHelmet;
    private final ModelPart diamondHelmetHorn1;
    private final ModelPart diamondHelmetHorn2;
    private final ModelPart diamondHelmetSnout;
    private final ModelPart diamondChestArmor;
    private final ModelPart diamondLeftShoulder;
    private final ModelPart diamondRightShoulder;
    private final ModelPart diamondLeftLegArmor;
    private final ModelPart diamondRightLegArmor;
    private final ModelPart rightshoulder;
    private final ModelPart leftshoulder;

    // left wing
    private final ModelPart leftWing;
    private final ModelPart leftuparm;
    private final ModelPart leftlowarm;
    private final ModelPart leftfing1a;
    private final ModelPart leftfing1b;
    private final ModelPart leftfing2a;
    private final ModelPart leftfing2b;
    private final ModelPart leftfing3a;
    private final ModelPart leftfing3b;
    private final ModelPart leftwingflap1;
    private final ModelPart leftwingflap2;
    private final ModelPart leftwingflap3;

    // right wing
    private final ModelPart rightWing;
    private final ModelPart rightuparm;
    private final ModelPart rightlowarm;
    private final ModelPart rightfing1a;
    private final ModelPart rightfing1b;
    private final ModelPart rightfing2a;
    private final ModelPart rightfing2b;
    private final ModelPart rightfing3a;
    private final ModelPart rightfing3b;
    private final ModelPart rightwingflap1;
    private final ModelPart rightwingflap2;
    private final ModelPart rightwingflap3;

    // left leg
    private final ModelPart leftupleg;
    private final ModelPart leftmidleg;
    private final ModelPart leftlowleg;
    private final ModelPart leftfoot;
    private final ModelPart lefttoe1;
    private final ModelPart lefttoe2;
    private final ModelPart lefttoe3;
    private final ModelPart leftclaw1;
    private final ModelPart leftclaw2;
    private final ModelPart leftclaw3;

    // right leg
    private final ModelPart rightupleg;
    private final ModelPart rightmidleg;
    private final ModelPart rightlowleg;
    private final ModelPart rightfoot;
    private final ModelPart righttoe1;
    private final ModelPart righttoe2;
    private final ModelPart righttoe3;
    private final ModelPart rightclaw1;
    private final ModelPart rightclaw2;
    private final ModelPart rightclaw3;

    public MoCWyvernModel(ModelPart root) {
        this.root = root;

        this.back1 = root.getChild("back1");
        this.tail = root.getChild("tail");
        this.tail1 = this.tail.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.tail5 = this.tail4.getChild("tail5");

        this.chest = root.getChild("chest");
        this.neckplate3 = root.getChild("neckplate3");
        this.neck3 = root.getChild("neck3");

        this.mainHead = root.getChild("main_head");
        this.neck2 = this.mainHead.getChild("neck2");
        this.neckplate2 = this.neck2.getChild("neckplate2");
        this.neck1 = this.neck2.getChild("neck1");
        this.neckplate1 = this.neck1.getChild("neckplate1");
        this.head = this.neck1.getChild("head");
        this.snout = this.head.getChild("snout");
        this.beak = this.snout.getChild("beak");
        this.headplate = this.head.getChild("headplate");
        this.righteyesock = this.head.getChild("righteyesock");
        this.lefteyesock = this.head.getChild("lefteyesock");
        this.jaw = this.head.getChild("jaw");
        this.leftupjaw = this.head.getChild("leftupjaw");
        this.rightupjaw = this.head.getChild("rightupjaw");
        this.rightearskin = this.head.getChild("rightearskin");
        this.leftearskin = this.head.getChild("leftearskin");
        this.rightspine1 = this.rightearskin.getChild("rightspine1");
        this.rightspine2 = this.rightearskin.getChild("rightspine2");
        this.rightspine3 = this.rightearskin.getChild("rightspine3");
        this.leftspine1 = this.leftearskin.getChild("leftspine1");
        this.leftspine2 = this.leftearskin.getChild("leftspine2");
        this.leftspine3 = this.leftearskin.getChild("leftspine3");

        this.torso = root.getChild("torso");
        this.saddle = root.getChild("saddle");
        this.storage = root.getChild("storage");
        this.ironHelmet = this.head.getChild("iron_helmet");
        this.ironHelmetSnout = this.snout.getChild("iron_helmet_snout");
        this.ironHelmetHorn1 = this.leftspine1.getChild("iron_helmet_horn1");
        this.ironHelmetHorn2 = this.rightspine1.getChild("iron_helmet_horn2");
        this.ironChestArmor = root.getChild("iron_chest_armor");
        this.ironLeftShoulder = root.getChild("iron_left_shoulder");
        this.ironRightShoulder = root.getChild("iron_right_shoulder");
        this.goldHelmet = this.head.getChild("gold_helmet");
        this.goldHelmetSnout = this.snout.getChild("gold_helmet_snout");
        this.goldHelmetHorn1 = this.leftspine1.getChild("gold_helmet_horn1");
        this.goldHelmetHorn2 = this.rightspine1.getChild("gold_helmet_horn2");
        this.goldChestArmor = root.getChild("gold_chest_armor");
        this.goldLeftShoulder = root.getChild("gold_left_shoulder");
        this.goldRightShoulder = root.getChild("gold_right_shoulder");
        this.diamondHelmet = this.head.getChild("diamond_helmet");
        this.diamondHelmetSnout = this.snout.getChild("diamond_helmet_snout");
        this.diamondHelmetHorn1 = this.leftspine1.getChild("diamond_helmet_horn1");
        this.diamondHelmetHorn2 = this.rightspine1.getChild("diamond_helmet_horn2");
        this.diamondChestArmor = root.getChild("diamond_chest_armor");
        this.diamondLeftShoulder = root.getChild("diamond_left_shoulder");
        this.diamondRightShoulder = root.getChild("diamond_right_shoulder");
        this.rightshoulder = root.getChild("rightshoulder");
        this.leftshoulder = root.getChild("leftshoulder");

        this.leftWing = root.getChild("left_wing");
        this.leftuparm = this.leftWing.getChild("leftuparm");
        this.leftlowarm = this.leftuparm.getChild("leftlowarm");
        this.leftfing1a = this.leftlowarm.getChild("leftfing1a");
        this.leftfing1b = this.leftfing1a.getChild("leftfing1b");
        this.leftfing2a = this.leftlowarm.getChild("leftfing2a");
        this.leftfing2b = this.leftfing2a.getChild("leftfing2b");
        this.leftfing3a = this.leftlowarm.getChild("leftfing3a");
        this.leftfing3b = this.leftfing3a.getChild("leftfing3b");
        this.leftwingflap1 = this.leftfing1a.getChild("leftwingflap1");
        this.leftwingflap2 = this.leftfing2a.getChild("leftwingflap2");
        this.leftwingflap3 = this.leftfing3a.getChild("leftwingflap3");

        this.rightWing = root.getChild("right_wing");
        this.rightuparm = this.rightWing.getChild("rightuparm");
        this.rightlowarm = this.rightuparm.getChild("rightlowarm");
        this.rightfing1a = this.rightlowarm.getChild("rightfing1a");
        this.rightfing1b = this.rightfing1a.getChild("rightfing1b");
        this.rightfing2a = this.rightlowarm.getChild("rightfing2a");
        this.rightfing2b = this.rightfing2a.getChild("rightfing2b");
        this.rightfing3a = this.rightlowarm.getChild("rightfing3a");
        this.rightfing3b = this.rightfing3a.getChild("rightfing3b");
        this.rightwingflap1 = this.rightfing1a.getChild("rightwingflap1");
        this.rightwingflap2 = this.rightfing2a.getChild("rightwingflap2");
        this.rightwingflap3 = this.rightfing3a.getChild("rightwingflap3");

        this.leftupleg = root.getChild("leftupleg");
        this.leftmidleg = this.leftupleg.getChild("leftmidleg");
        this.leftlowleg = this.leftmidleg.getChild("leftlowleg");
        this.leftfoot = this.leftlowleg.getChild("leftfoot");
        this.lefttoe1 = this.leftfoot.getChild("lefttoe1");
        this.lefttoe2 = this.leftfoot.getChild("lefttoe2");
        this.lefttoe3 = this.leftfoot.getChild("lefttoe3");
        this.leftclaw1 = this.lefttoe1.getChild("leftclaw1");
        this.leftclaw2 = this.lefttoe2.getChild("leftclaw2");
        this.leftclaw3 = this.lefttoe3.getChild("leftclaw3");

        this.rightupleg = root.getChild("rightupleg");
        this.rightmidleg = this.rightupleg.getChild("rightmidleg");
        this.rightlowleg = this.rightmidleg.getChild("rightlowleg");
        this.ironLeftLegArmor = this.leftlowleg.getChild("iron_left_leg_armor");
        this.ironRightLegArmor = this.rightlowleg.getChild("iron_right_leg_armor");
        this.goldLeftLegArmor = this.leftlowleg.getChild("gold_left_leg_armor");
        this.goldRightLegArmor = this.rightlowleg.getChild("gold_right_leg_armor");
        this.diamondLeftLegArmor = this.leftlowleg.getChild("diamond_left_leg_armor");
        this.diamondRightLegArmor = this.rightlowleg.getChild("diamond_right_leg_armor");
        this.rightfoot = this.rightlowleg.getChild("rightfoot");
        this.righttoe1 = this.rightfoot.getChild("righttoe1");
        this.righttoe2 = this.rightfoot.getChild("righttoe2");
        this.righttoe3 = this.rightfoot.getChild("righttoe3");
        this.rightclaw1 = this.righttoe1.getChild("rightclaw1");
        this.rightclaw2 = this.righttoe2.getChild("rightclaw2");
        this.rightclaw3 = this.righttoe3.getChild("rightclaw3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ---- tail ----
        root.addOrReplaceChild("back1",
                CubeListBuilder.create().texOffs(92, 0).addBox(-3F, -2F, -12F, 6, 2, 12),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create(),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1",
                CubeListBuilder.create().texOffs(0, 22).addBox(-4F, 0F, 0F, 8, 8, 10),
                PartPose.offset(0F, 0F, 0F));

        tail1.addOrReplaceChild("back2",
                CubeListBuilder.create().texOffs(100, 14).addBox(-2F, -2F, 0F, 4, 2, 10),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(0, 40).addBox(-3F, 0F, 0F, 6, 6, 9),
                PartPose.offset(0F, 0F, 10F));

        tail2.addOrReplaceChild("back3",
                CubeListBuilder.create().texOffs(104, 26).addBox(-1.5F, -2F, 0F, 3, 2, 9),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
                CubeListBuilder.create().texOffs(0, 55).addBox(-2F, 0F, 0F, 4, 5, 8),
                PartPose.offset(0F, 0F, 8F));

        tail3.addOrReplaceChild("back4",
                CubeListBuilder.create().texOffs(108, 37).addBox(-1F, -2F, 0F, 2, 2, 8),
                PartPose.offset(0F, 0F, 0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4",
                CubeListBuilder.create().texOffs(0, 68).addBox(-1F, 0F, 0F, 2, 5, 7),
                PartPose.offset(0F, -1F, 7F));

        tail4.addOrReplaceChild("tail5",
                CubeListBuilder.create().texOffs(0, 80).addBox(-0.5F, 0F, 0F, 1, 3, 7),
                PartPose.offset(0F, 1F, 6F));

        // ---- chest / neck plates (root level) ----
        root.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(44, 0).addBox(-4.5F, 2.7F, -13F, 9, 10, 4),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2602503F, 0F, 0F));

        root.addOrReplaceChild("neckplate3",
                CubeListBuilder.create().texOffs(112, 64).addBox(-2F, -2F, -2F, 4, 2, 4),
                PartPose.offsetAndRotation(0F, 0F, -12F, -0.669215F, 0F, 0F));

        root.addOrReplaceChild("neck3",
                CubeListBuilder.create().texOffs(100, 113).addBox(-3F, 0F, -2F, 6, 7, 8),
                PartPose.offsetAndRotation(0F, 0F, -12F, -0.669215F, 0F, 0F));

        // ---- head assembly ----
        PartDefinition mainHead = root.addOrReplaceChild("main_head", CubeListBuilder.create(),
                PartPose.offset(0F, 3F, -15F));

        PartDefinition neck2 = mainHead.addOrReplaceChild("neck2",
                CubeListBuilder.create().texOffs(102, 99).addBox(-2.5F, -3F, -8F, 5, 6, 8),
                PartPose.offset(0F, 0F, 0F));

        neck2.addOrReplaceChild("neckplate2",
                CubeListBuilder.create().texOffs(106, 54).addBox(-1.5F, -2F, -8F, 3, 2, 8),
                PartPose.offset(0F, -3F, 0F));

        PartDefinition neck1 = neck2.addOrReplaceChild("neck1",
                CubeListBuilder.create().texOffs(104, 85).addBox(-2F, -3F, -8F, 4, 6, 8),
                PartPose.offset(0F, -0.5F, -5.5F));

        neck1.addOrReplaceChild("neckplate1",
                CubeListBuilder.create().texOffs(80, 108).addBox(-1F, -2F, -8F, 2, 2, 8),
                PartPose.offset(0F, -3F, 0F));

        PartDefinition head = neck1.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(98, 70).addBox(-3.5F, -3.5F, -8F, 7, 7, 8),
                PartPose.offset(0F, 0F, -7F));

        PartDefinition snout = head.addOrReplaceChild("snout",
                CubeListBuilder.create().texOffs(72, 70).addBox(-2F, -1.5F, -9F, 4, 3, 9),
                PartPose.offsetAndRotation(0F, -1.5F, -8F, 2F / R, 0F, 0F));

        snout.addOrReplaceChild("beak",
                CubeListBuilder.create().texOffs(60, 85).addBox(-1.5F, -2.5F, -1.5F, 3, 5, 3),
                PartPose.offsetAndRotation(0F, 0.8F, -8.0F, -6F / R, 45F / R, -6F / R));

        head.addOrReplaceChild("headplate",
                CubeListBuilder.create().texOffs(80, 118).addBox(-1F, -1F, -4F, 2, 2, 8),
                PartPose.offsetAndRotation(0F, -3F, -1F, 10F / R, 0F, 0F));

        head.addOrReplaceChild("righteyesock",
                CubeListBuilder.create().texOffs(70, 108).addBox(0F, 0F, 0F, 1, 2, 4),
                PartPose.offset(-3.5F, -2.5F, -8F));

        head.addOrReplaceChild("lefteyesock",
                CubeListBuilder.create().texOffs(70, 114).addBox(0F, 0F, 0F, 1, 2, 4),
                PartPose.offset(2.5F, -2.5F, -8F));

        head.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(72, 82).addBox(-2F, -1F, -9F, 4, 2, 9),
                PartPose.offsetAndRotation(0F, 2.5F, -7.5F, -10F / R, 0F, 0F));

        head.addOrReplaceChild("leftupjaw",
                CubeListBuilder.create().texOffs(42, 93).addBox(-1F, -1F, -6.5F, 2, 2, 13),
                PartPose.offsetAndRotation(2F, 0F, -10.5F, -10F / R, 10F / R, 0F));

        head.addOrReplaceChild("rightupjaw",
                CubeListBuilder.create().texOffs(72, 93).addBox(-1F, -1F, -6.5F, 2, 2, 13),
                PartPose.offsetAndRotation(-2F, 0F, -10.5F, -10F / R, -10F / R, 0F));

        PartDefinition rightearskin = head.addOrReplaceChild("rightearskin",
                CubeListBuilder.create().texOffs(112, 201).addBox(0F, -4F, 0F, 0, 8, 8),
                PartPose.offset(-3F, -0.5F, 0F));

        PartDefinition leftearskin = head.addOrReplaceChild("leftearskin",
                CubeListBuilder.create().texOffs(96, 201).addBox(0F, -4F, 0F, 0, 8, 8),
                PartPose.offset(3F, -0.5F, 0F));

        PartDefinition rightspine1 = rightearskin.addOrReplaceChild("rightspine1",
                CubeListBuilder.create().texOffs(50, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offsetAndRotation(0F, -2F, 0F, 15F / R, 0F, 0F));
        rightearskin.addOrReplaceChild("rightspine2",
                CubeListBuilder.create().texOffs(50, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offset(0F, 0F, 0F));
        rightearskin.addOrReplaceChild("rightspine3",
                CubeListBuilder.create().texOffs(50, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offsetAndRotation(0F, 2F, 0F, -15F / R, 0F, 0F));

        PartDefinition leftspine1 = leftearskin.addOrReplaceChild("leftspine1",
                CubeListBuilder.create().texOffs(68, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offsetAndRotation(0F, -2F, 0F, 15F / R, 0F, 0F));
        leftearskin.addOrReplaceChild("leftspine2",
                CubeListBuilder.create().texOffs(68, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offset(0F, 0F, 0F));
        leftearskin.addOrReplaceChild("leftspine3",
                CubeListBuilder.create().texOffs(68, 141).addBox(-0.5F, -1F, 0F, 1, 2, 8),
                PartPose.offsetAndRotation(0F, 2F, 0F, -15F / R, 0F, 0F));

        // ---- torso / shoulders ----
        root.addOrReplaceChild("torso",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 0F, -12F, 10, 10, 12),
                PartPose.offset(0F, 0F, 0F));

        // Same texture atlas as the rest of the model — the saddle artwork
        // is already baked into each variant's own PNG, just toggled
        // visible/invisible (see setupAnim()), no separate layer/texture.
        root.addOrReplaceChild("saddle",
                CubeListBuilder.create().texOffs(38, 70).addBox(-3.5F, -2.5F, -8F, 7, 3, 10),
                PartPose.offset(0F, 0F, 0F));

        // Every tier can carry this one — same texture atlas, toggled by
        // hasChest() in setupAnim(), no separate texture needed.
        root.addOrReplaceChild("storage",
                CubeListBuilder.create().texOffs(28, 59).addBox(-5F, -4.5F, 1.5F, 10, 5, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2268928F, 0F, 0F));

        // ---- armor (iron/gold/diamond) — same texture atlas, toggled
        // visible/invisible per tier in setupAnim(), same idea as the saddle.
        head.addOrReplaceChild("iron_helmet",
                CubeListBuilder.create().texOffs(32, 128).addBox(-4F, -4F, -9F, 8, 4, 9),
                PartPose.offset(0F, 0F, 0F));
        snout.addOrReplaceChild("iron_helmet_snout",
                CubeListBuilder.create().texOffs(0, 144).addBox(-2.5F, -2F, -7F, 5, 2, 7),
                PartPose.offset(0F, 0F, -1F));
        leftspine1.addOrReplaceChild("iron_helmet_horn1",
                CubeListBuilder.create().texOffs(106, 139).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(-0.5F, 0F, 0.1F));
        rightspine1.addOrReplaceChild("iron_helmet_horn2",
                CubeListBuilder.create().texOffs(106, 128).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(0.5F, 0F, 0.1F));
        root.addOrReplaceChild("iron_chest_armor",
                CubeListBuilder.create().texOffs(0, 128).addBox(-5.5F, 2.2F, -13.5F, 11, 11, 5),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2602503F, 0F, 0F));
        root.addOrReplaceChild("iron_left_shoulder",
                CubeListBuilder.create().texOffs(26, 201).addBox(1.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("iron_right_shoulder",
                CubeListBuilder.create().texOffs(74, 201).addBox(-6.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));

        head.addOrReplaceChild("gold_helmet",
                CubeListBuilder.create().texOffs(94, 226).addBox(-4F, -4F, -9F, 8, 4, 9),
                PartPose.offset(0F, 0F, 0F));
        snout.addOrReplaceChild("gold_helmet_snout",
                CubeListBuilder.create().texOffs(71, 235).addBox(-2.5F, -2F, -7F, 5, 2, 7),
                PartPose.offset(0F, 0F, -1F));
        leftspine1.addOrReplaceChild("gold_helmet_horn1",
                CubeListBuilder.create().texOffs(106, 161).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(-0.5F, 0F, 0.1F));
        rightspine1.addOrReplaceChild("gold_helmet_horn2",
                CubeListBuilder.create().texOffs(106, 150).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(0.5F, 0F, 0.1F));
        root.addOrReplaceChild("gold_chest_armor",
                CubeListBuilder.create().texOffs(71, 219).addBox(-5.5F, 2.2F, -13.5F, 11, 11, 5),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2602503F, 0F, 0F));
        root.addOrReplaceChild("gold_left_shoulder",
                CubeListBuilder.create().texOffs(71, 244).addBox(1.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("gold_right_shoulder",
                CubeListBuilder.create().texOffs(93, 244).addBox(-6.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));

        head.addOrReplaceChild("diamond_helmet",
                CubeListBuilder.create().texOffs(23, 226).addBox(-4F, -4F, -9F, 8, 4, 9),
                PartPose.offset(0F, 0F, 0F));
        snout.addOrReplaceChild("diamond_helmet_snout",
                CubeListBuilder.create().texOffs(0, 235).addBox(-2.5F, -2F, -7F, 5, 2, 7),
                PartPose.offset(0F, 0F, -1F));
        leftspine1.addOrReplaceChild("diamond_helmet_horn1",
                CubeListBuilder.create().texOffs(49, 245).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(-0.5F, 0F, 0.1F));
        rightspine1.addOrReplaceChild("diamond_helmet_horn2",
                CubeListBuilder.create().texOffs(49, 234).addBox(-1.5F, -1.5F, 0F, 3, 3, 8),
                PartPose.offset(0.5F, 0F, 0.1F));
        root.addOrReplaceChild("diamond_chest_armor",
                CubeListBuilder.create().texOffs(0, 219).addBox(-5.5F, 2.2F, -13.5F, 11, 11, 5),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2602503F, 0F, 0F));
        root.addOrReplaceChild("diamond_left_shoulder",
                CubeListBuilder.create().texOffs(0, 244).addBox(1.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));
        root.addOrReplaceChild("diamond_right_shoulder",
                CubeListBuilder.create().texOffs(22, 244).addBox(-6.5F, 0.5F, -13F, 5, 6, 6),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));

        root.addOrReplaceChild("rightshoulder",
                CubeListBuilder.create().texOffs(42, 83).addBox(-6F, 1F, -12.5F, 4, 5, 5),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));

        root.addOrReplaceChild("leftshoulder",
                CubeListBuilder.create().texOffs(24, 83).addBox(2F, 1F, -12.5F, 4, 5, 5),
                PartPose.offsetAndRotation(0F, 0F, 0F, -0.2617994F, 0F, 0F));

        // ---- left wing ----
        PartDefinition leftWing = root.addOrReplaceChild("left_wing", CubeListBuilder.create(),
                PartPose.offset(4F, 1F, -11F));

        PartDefinition leftuparm = leftWing.addOrReplaceChild("leftuparm",
                CubeListBuilder.create().texOffs(44, 14).addBox(0F, -2F, -2F, 10, 4, 4),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -10F / R, 0F));

        PartDefinition leftlowarm = leftuparm.addOrReplaceChild("leftlowarm",
                CubeListBuilder.create().texOffs(72, 14).addBox(0F, -2F, -2F, 10, 4, 4),
                PartPose.offsetAndRotation(9F, 0F, 0F, 0F, 10F / R, 0F));

        PartDefinition leftfing1a = leftlowarm.addOrReplaceChild("leftfing1a",
                CubeListBuilder.create().texOffs(52, 30).addBox(0F, 0F, -1F, 2, 15, 2),
                PartPose.offsetAndRotation(9F, 1F, 0F, 90F / R, 70F / R, 0F));
        leftfing1a.addOrReplaceChild("leftfing1b",
                CubeListBuilder.create().texOffs(52, 47).addBox(0F, 0F, -1F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 35F / R));
        leftfing1a.addOrReplaceChild("leftwingflap1",
                CubeListBuilder.create().texOffs(74, 153).addBox(3.5F, -3F, 0.95F, 14, 24, 0),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 70F / R));

        PartDefinition leftfing2a = leftlowarm.addOrReplaceChild("leftfing2a",
                CubeListBuilder.create().texOffs(44, 30).addBox(-1F, 0F, 0F, 2, 15, 2),
                PartPose.offsetAndRotation(9F, 1F, 0F, 90F / R, 35F / R, 0F));
        leftfing2a.addOrReplaceChild("leftfing2b",
                CubeListBuilder.create().texOffs(44, 47).addBox(-1F, 0F, 0F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 30F / R));
        leftfing2a.addOrReplaceChild("leftwingflap2",
                CubeListBuilder.create().texOffs(36, 153).addBox(-7F, 1.05F, 1.05F, 19, 24, 0),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 40F / R));

        PartDefinition leftfing3a = leftlowarm.addOrReplaceChild("leftfing3a",
                CubeListBuilder.create().texOffs(36, 30).addBox(-1F, 0F, 1F, 2, 15, 2),
                PartPose.offsetAndRotation(9F, 1F, 0F, 90F / R, -5F / R, 0F));
        leftfing3a.addOrReplaceChild("leftfing3b",
                CubeListBuilder.create().texOffs(36, 47).addBox(-1F, 0F, 1F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 30F / R));
        leftfing3a.addOrReplaceChild("leftwingflap3",
                CubeListBuilder.create().texOffs(0, 153).addBox(-17.5F, 1F, 1.1F, 18, 24, 0),
                PartPose.offset(0F, 0F, 0F));

        // ---- right wing ----
        PartDefinition rightWing = root.addOrReplaceChild("right_wing", CubeListBuilder.create(),
                PartPose.offset(-4F, 1F, -11F));

        PartDefinition rightuparm = rightWing.addOrReplaceChild("rightuparm",
                CubeListBuilder.create().texOffs(44, 22).addBox(-10F, -2F, -2F, 10, 4, 4),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 10F / R, 0F));

        PartDefinition rightlowarm = rightuparm.addOrReplaceChild("rightlowarm",
                CubeListBuilder.create().texOffs(72, 22).addBox(-10F, -2F, -2F, 10, 4, 4),
                PartPose.offsetAndRotation(-9F, 0F, 0F, 0F, -10F / R, 0F));

        PartDefinition rightfing1a = rightlowarm.addOrReplaceChild("rightfing1a",
                CubeListBuilder.create().texOffs(36, 30).addBox(-1F, 0F, -1F, 2, 15, 2),
                PartPose.offsetAndRotation(-9F, 1F, -1F, 90F / R, -70F / R, 0F));
        rightfing1a.addOrReplaceChild("rightfing1b",
                CubeListBuilder.create().texOffs(36, 47).addBox(-1F, 0F, -1F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, -35F / R));
        rightfing1a.addOrReplaceChild("rightwingflap1",
                CubeListBuilder.create().texOffs(74, 177).addBox(-17.5F, -3F, 0.95F, 14, 24, 0),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -70F / R));

        PartDefinition rightfing2a = rightlowarm.addOrReplaceChild("rightfing2a",
                CubeListBuilder.create().texOffs(44, 30).addBox(-1F, 0F, 0F, 2, 15, 2),
                PartPose.offsetAndRotation(-9F, 1F, 0F, 90F / R, -35F / R, 0F));
        rightfing2a.addOrReplaceChild("rightfing2b",
                CubeListBuilder.create().texOffs(44, 47).addBox(-1F, 0F, 0F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, -30F / R));
        rightfing2a.addOrReplaceChild("rightwingflap2",
                CubeListBuilder.create().texOffs(36, 177).addBox(-19F, 1.05F, 1.05F, 19, 24, 0),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -40F / R));

        PartDefinition rightfing3a = rightlowarm.addOrReplaceChild("rightfing3a",
                CubeListBuilder.create().texOffs(52, 30).addBox(-1F, 0F, 1F, 2, 15, 2),
                PartPose.offsetAndRotation(-9F, 1F, 0F, 90F / R, 5F / R, 0F));
        rightfing3a.addOrReplaceChild("rightfing3b",
                CubeListBuilder.create().texOffs(52, 47).addBox(-1F, 0F, 1F, 2, 10, 2),
                PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, -30F / R));
        rightfing3a.addOrReplaceChild("rightwingflap3",
                CubeListBuilder.create().texOffs(0, 177).addBox(-0.5F, 1F, 1.1F, 18, 24, 0),
                PartPose.offset(0F, 0F, 0F));

        // ---- left leg ----
        PartDefinition leftupleg = root.addOrReplaceChild("leftupleg",
                CubeListBuilder.create().texOffs(0, 111).addBox(-2F, -3F, -3F, 4, 10, 7),
                PartPose.offsetAndRotation(5F, 6F, -5F, -25F / R, 0F, 0F));

        PartDefinition leftmidleg = leftupleg.addOrReplaceChild("leftmidleg",
                CubeListBuilder.create().texOffs(0, 102).addBox(-1.5F, -2F, 0F, 3, 4, 5),
                PartPose.offset(0F, 5F, 4F));

        PartDefinition leftlowleg = leftmidleg.addOrReplaceChild("leftlowleg",
                CubeListBuilder.create().texOffs(0, 91).addBox(-1.5F, 0F, -1.5F, 3, 8, 3),
                PartPose.offset(0F, 2F, 3.5F));

        PartDefinition leftfoot = leftlowleg.addOrReplaceChild("leftfoot",
                CubeListBuilder.create().texOffs(44, 121).addBox(-2F, -1F, -3F, 4, 3, 4),
                PartPose.offsetAndRotation(0F, 7F, 0.5F, 25F / R, 0F, 0F));

        PartDefinition lefttoe1 = leftfoot.addOrReplaceChild("lefttoe1",
                CubeListBuilder.create().texOffs(96, 35).addBox(-0.5F, -1F, -3F, 1, 2, 3),
                PartPose.offset(-1.5F, 1F, -3F));
        PartDefinition lefttoe3 = leftfoot.addOrReplaceChild("lefttoe3",
                CubeListBuilder.create().texOffs(96, 30).addBox(-0.5F, -1F, -3F, 1, 2, 3),
                PartPose.offset(1.5F, 1F, -3F));
        PartDefinition lefttoe2 = leftfoot.addOrReplaceChild("lefttoe2",
                CubeListBuilder.create().texOffs(84, 30).addBox(-1F, -1.5F, -4F, 2, 3, 4),
                PartPose.offset(0F, 0.5F, -3F));

        lefttoe1.addOrReplaceChild("leftclaw1",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0.5F, -0.5F, -2.5F, -25F / R, 0F, 0F));
        lefttoe2.addOrReplaceChild("leftclaw2",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 3, 1),
                PartPose.offsetAndRotation(0F, -1F, -3.5F, -25F / R, 0F, 0F));
        lefttoe3.addOrReplaceChild("leftclaw3",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-0.5F, -0.5F, -2.5F, -25F / R, 0F, 0F));

        // ---- right leg ----
        PartDefinition rightupleg = root.addOrReplaceChild("rightupleg",
                CubeListBuilder.create().texOffs(0, 111).addBox(-2F, -3F, -3F, 4, 10, 7),
                PartPose.offsetAndRotation(-5F, 6F, -5F, -25F / R, 0F, 0F));

        PartDefinition rightmidleg = rightupleg.addOrReplaceChild("rightmidleg",
                CubeListBuilder.create().texOffs(0, 102).addBox(-1.5F, -2F, 0F, 3, 4, 5),
                PartPose.offset(0F, 5F, 4F));

        PartDefinition rightlowleg = rightmidleg.addOrReplaceChild("rightlowleg",
                CubeListBuilder.create().texOffs(0, 91).addBox(-1.5F, 0F, -1.5F, 3, 8, 3),
                PartPose.offset(0F, 2F, 3.5F));

        leftlowleg.addOrReplaceChild("iron_left_leg_armor",
                CubeListBuilder.create().texOffs(39, 97).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));
        rightlowleg.addOrReplaceChild("iron_right_leg_armor",
                CubeListBuilder.create().texOffs(39, 97).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));
        leftlowleg.addOrReplaceChild("gold_left_leg_armor",
                CubeListBuilder.create().texOffs(112, 181).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));
        rightlowleg.addOrReplaceChild("gold_right_leg_armor",
                CubeListBuilder.create().texOffs(112, 181).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));
        leftlowleg.addOrReplaceChild("diamond_left_leg_armor",
                CubeListBuilder.create().texOffs(43, 215).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));
        rightlowleg.addOrReplaceChild("diamond_right_leg_armor",
                CubeListBuilder.create().texOffs(43, 215).addBox(-2F, -2.5F, -2F, 4, 5, 4),
                PartPose.offset(0F, 2.5F, 0F));

        PartDefinition rightfoot = rightlowleg.addOrReplaceChild("rightfoot",
                CubeListBuilder.create().texOffs(44, 121).addBox(-2F, -1F, -3F, 4, 3, 4),
                PartPose.offsetAndRotation(0F, 7F, 0.5F, 25F / R, 0F, 0F));

        PartDefinition righttoe1 = rightfoot.addOrReplaceChild("righttoe1",
                CubeListBuilder.create().texOffs(96, 35).addBox(-0.5F, -1F, -3F, 1, 2, 3),
                PartPose.offset(-1.5F, 1F, -3F));
        PartDefinition righttoe3 = rightfoot.addOrReplaceChild("righttoe3",
                CubeListBuilder.create().texOffs(96, 30).addBox(-0.5F, -1F, -3F, 1, 2, 3),
                PartPose.offset(1.5F, 1F, -3F));
        PartDefinition righttoe2 = rightfoot.addOrReplaceChild("righttoe2",
                CubeListBuilder.create().texOffs(84, 30).addBox(-1F, -1.5F, -4F, 2, 3, 4),
                PartPose.offset(0F, 0.5F, -3F));

        righttoe1.addOrReplaceChild("rightclaw1",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0.5F, -0.5F, -2.5F, -25F / R, 0F, 0F));
        righttoe2.addOrReplaceChild("rightclaw2",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 3, 1),
                PartPose.offsetAndRotation(0F, -1F, -3.5F, -25F / R, 0F, 0F));
        righttoe3.addOrReplaceChild("rightclaw3",
                CubeListBuilder.create().texOffs(100, 26).addBox(-0.5F, 0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(-0.5F, -0.5F, -2.5F, -25F / R, 0F, 0F));

        // Original Techne model declared textureWidth=128, textureHeight=256 — keep this
        // even though the shipped PNGs are 256x512 (2x/"HD" textures); the UV mapping
        // above is baked for the 128x256 grid and scales fine onto the larger PNG.
        return LayerDefinition.create(mesh, 128, 256);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(MoCWyvernEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {

        this.saddle.visible = entity.isSaddled();
        this.storage.visible = entity.hasChest();

        int armorTier = entity.getArmorTier();
        this.ironHelmet.visible = armorTier == 1;
        this.ironHelmetSnout.visible = armorTier == 1;
        this.ironHelmetHorn1.visible = armorTier == 1;
        this.ironHelmetHorn2.visible = armorTier == 1;
        this.ironChestArmor.visible = armorTier == 1;
        this.ironLeftShoulder.visible = armorTier == 1;
        this.ironRightShoulder.visible = armorTier == 1;
        this.ironLeftLegArmor.visible = armorTier == 1;
        this.ironRightLegArmor.visible = armorTier == 1;
        this.goldHelmet.visible = armorTier == 2;
        this.goldHelmetSnout.visible = armorTier == 2;
        this.goldHelmetHorn1.visible = armorTier == 2;
        this.goldHelmetHorn2.visible = armorTier == 2;
        this.goldChestArmor.visible = armorTier == 2;
        this.goldLeftShoulder.visible = armorTier == 2;
        this.goldRightShoulder.visible = armorTier == 2;
        this.goldLeftLegArmor.visible = armorTier == 2;
        this.goldRightLegArmor.visible = armorTier == 2;
        this.diamondHelmet.visible = armorTier == 3;
        this.diamondHelmetSnout.visible = armorTier == 3;
        this.diamondHelmetHorn1.visible = armorTier == 3;
        this.diamondHelmetHorn2.visible = armorTier == 3;
        this.diamondChestArmor.visible = armorTier == 3;
        this.diamondLeftShoulder.visible = armorTier == 3;
        this.diamondRightShoulder.visible = armorTier == 3;
        this.diamondLeftLegArmor.visible = armorTier == 3;
        this.diamondRightLegArmor.visible = armorTier == 3;

        netHeadYaw = Mth.clamp(netHeadYaw, -60F, 60F);

        // ---- head / neck ----
        this.neck2.xRot = -66F / R + (headPitch / 3F / R);
        this.neck1.xRot = 30F / R + (headPitch * 2F / 3F / R);
        this.head.xRot = 45F / R;

        this.neck2.yRot = (netHeadYaw * 2F / 3F) / R;
        this.neck1.yRot = (netHeadYaw / 3F) / R;

        this.head.yRot = 0F;
        this.head.zRot = 0F;

        // ---- tail base curve ----
        this.tail1.xRot = -19F / R;
        this.tail2.xRot = -16F / R;
        this.tail3.xRot = 7F / R;
        this.tail4.xRot = 11F / R;
        this.tail5.xRot = 8F / R;

        // ---- tail side-to-side wave ----
        float t = limbSwing / 2F;
        float amplitude = 0.15F;
        float w = 0.9F;
        float k = 0.6F;

        this.tail1.yRot = amplitude * Mth.sin(w * t - k * 0);
        this.tail2.yRot = amplitude * Mth.sin(w * t - k * 1);
        this.tail3.yRot = amplitude * Mth.sin(w * t - k * 2);
        this.tail4.yRot = amplitude * Mth.sin(w * t - k * 3);
        this.tail5.yRot = amplitude * Mth.sin(w * t - k * 4);

        // ---- wings + legs: gated by onAir (physically airborne OR AI "flying"
        // flag), exactly like the original's onAir/flapwings logic — except
        // "flapping" is now driven by actual vertical motion (isGliding()),
        // not a random counter: it flaps continuously while airborne unless
        // it's genuinely falling (gliding), matching how it should always
        // look like it's flying, and only glide with wings held out while
        // actually descending.
        boolean onAir = entity.isAirborne();
        boolean gliding = entity.isGliding();
        boolean flapping = onAir && !gliding;
        float rLegXRot = Mth.cos((limbSwing * 0.6662F) + (float) Math.PI) * 0.8F * limbSwingAmount;
        float lLegXRot = ModelAnimations.walkSwing(limbSwing, limbSwingAmount, 0.8F);

        // ---- ridden with a player: matches the original's isRidden block —
        // the neck stops tracking the rider's look direction entirely
        // (locked straight ahead), flying holds it level, grounded uses a
        // fixed lowered brace instead of the free head-tracking pose above.
        boolean ridden = entity.isVehicle() && entity.getControllingPassenger() instanceof Player;
        if (ridden) {
            this.neck1.yRot = 0F;
            this.neck2.yRot = 0F;
            if (onAir) {
                this.neck1.xRot = 0F;
                this.neck2.xRot = 0F;
            } else {
                this.neck2.xRot = -1.1519173F + rLegXRot * 0.016666668F;
                this.neck1.xRot = 0.5235988F + rLegXRot * 0.033333335F;
            }
        }

        float wingSpread = flapping
                ? Mth.cos(ageInTicks * 0.3F + (float) Math.PI) * 1.2F
                : Mth.cos(limbSwing * 0.5F) * 0.1F;

        if (onAir) {
            float speedMov = limbSwingAmount * 0.5F;
            // Bird-like tucked leg: the thigh (upleg) stays close to
            // vertical/straight against the body, and the trail-back happens
            // from the knee down (mid/lower leg + foot), not by swinging the
            // whole leg back from the hip.
            float kneeBend = 0.6108652F;
            float shinBend = 0.34906584F;

            this.leftuparm.zRot = wingSpread * 2F / 3F;
            this.rightuparm.zRot = -wingSpread * 2F / 3F;
            this.leftlowarm.zRot = wingSpread * 0.1F;
            this.leftfing1a.zRot = wingSpread;
            this.leftfing2a.zRot = wingSpread * 0.8F;
            this.rightlowarm.zRot = -wingSpread * 0.1F;
            this.rightfing1a.zRot = -wingSpread;
            this.rightfing2a.zRot = -wingSpread * 0.8F;

            this.leftuparm.yRot = -0.17453292F - wingSpread / 2F;
            this.leftlowarm.yRot = 0.2617994F + wingSpread / 2F;
            this.leftfing1a.yRot = 1.2217305F;
            this.leftfing2a.yRot = 0.61086524F;
            this.leftfing3a.yRot = -0.08726646F;
            this.rightuparm.yRot = 0.17453292F + wingSpread / 2F;
            this.rightlowarm.yRot = -0.2617994F - wingSpread / 2F;
            this.rightfing1a.yRot = -1.2217305F;
            this.rightfing2a.yRot = -0.61086524F;
            this.rightfing3a.yRot = 0.08726646F;

            this.leftupleg.xRot = speedMov;
            this.leftmidleg.xRot = kneeBend + speedMov;
            this.leftlowleg.xRot = shinBend;
            this.leftfoot.xRot = 0.43633232F;
            this.lefttoe1.xRot = speedMov;
            this.lefttoe2.xRot = speedMov;
            this.lefttoe3.xRot = speedMov;
            this.rightfoot.xRot = 0.43633232F;
            this.rightupleg.xRot = speedMov;
            this.rightmidleg.xRot = kneeBend + speedMov;
            this.rightlowleg.xRot = shinBend;
            this.righttoe1.xRot = speedMov;
            this.righttoe2.xRot = speedMov;
            this.righttoe3.xRot = speedMov;
        } else {
            this.leftlowarm.zRot = 0F;
            this.leftfing1a.zRot = 0F;
            this.leftfing2a.zRot = 0F;
            this.rightlowarm.zRot = 0F;
            this.rightfing1a.zRot = 0F;
            this.rightfing2a.zRot = 0F;

            this.leftuparm.zRot = 0.5235988F;
            this.leftuparm.yRot = -1.0471976F + lLegXRot / 5F;
            this.leftlowarm.yRot = 1.8325957F;
            this.leftfing1a.yRot = -0.34906584F;
            this.leftfing2a.yRot = -0.4537856F;
            this.leftfing3a.yRot = -0.55850536F;
            this.rightuparm.yRot = 1.0471976F - rLegXRot / 5F;
            this.rightuparm.zRot = -0.5235988F;
            this.rightlowarm.yRot = -1.8325957F;
            this.rightfing1a.yRot = 0.27925268F;
            this.rightfing2a.yRot = 0.4537856F;
            this.rightfing3a.yRot = 0.55850536F;

            this.leftupleg.xRot = -0.43633232F + lLegXRot;
            this.rightupleg.xRot = -0.43633232F + rLegXRot;
            this.leftmidleg.xRot = 0F;
            this.leftlowleg.xRot = 0F;
            this.leftfoot.xRot = 0.43633232F - lLegXRot;
            this.lefttoe1.xRot = lLegXRot;
            this.lefttoe2.xRot = lLegXRot;
            this.lefttoe3.xRot = lLegXRot;
            this.rightmidleg.xRot = 0F;
            this.rightlowleg.xRot = 0F;
            this.rightfoot.xRot = 0.43633232F - rLegXRot;
            this.righttoe1.xRot = rLegXRot;
            this.righttoe2.xRot = rLegXRot;
            this.righttoe3.xRot = rLegXRot;
        }

        // ---- sitting: overrides the grounded leg pose above, plus a lowered
        // neck/head. Confirmed against the real 1.20.1 jar's isSitting block
        // — these numbers are an exact match, not an approximation.
        if (entity.isSittingSynced()) {
            this.leftupleg.xRot = 0.7853981F + lLegXRot;
            this.rightupleg.xRot = 0.7853981F + rLegXRot;
            this.leftmidleg.xRot = 0.5235988F;
            this.rightmidleg.xRot = 0.5235988F;
            this.neck2.xRot = -0.62831855F + headPitch * 0.33333334F / R;
            this.neck1.xRot = 0.5235988F + headPitch * 0.6666667F / R;
            // Original's getAdjustedYOffset(): sinks the whole model down to
            // meet the now-shorter (folded) legs. Applied in renderToBuffer().
            this.yOffset = 0.65F;
        } else {
            this.yOffset = 0.0F;
        }

        // ---- dive (rider hits descend/Z): exact match from the original's
        // "diving" block — wings pulled in and twisted tight against the body.
        if (entity.isDiving()) {
            this.leftuparm.zRot = -0.6981317F;
            this.rightuparm.zRot = 0.6981317F;
            this.leftlowarm.zRot = 0F;
            this.leftfing1a.zRot = 0F;
            this.leftfing2a.zRot = 0F;
            this.rightlowarm.zRot = 0F;
            this.rightfing1a.zRot = 0F;
            this.rightfing2a.zRot = 0F;
            this.leftuparm.yRot = -0.87266463F;
            this.leftlowarm.yRot = 0.5235988F;
            this.leftfing1a.yRot = 0.87266463F;
            this.leftfing2a.yRot = 0.5235988F;
            this.leftfing3a.yRot = 0.17453292F;
            this.rightuparm.yRot = 0.87266463F;
            this.rightlowarm.yRot = -0.5235988F;
            this.rightfing1a.yRot = -0.87266463F;
            this.rightfing2a.yRot = -0.5235988F;
            this.rightfing3a.yRot = -0.17453292F;
        }

        // ---- jaw / ears ----
        // Same shape as the original's openMouth: a full open-close sine over
        // MoCWyvernEntity's 1..30 mouthCounter, driven by getBiteTicks().
        int mouthCounter = entity.getBiteTicks();
        if (mouthCounter != 0) {
            float mouthMov = Mth.cos((mouthCounter - 15) * 0.11F) * 0.8F;
            this.jaw.xRot = -0.17453292F + mouthMov;
            this.leftearskin.yRot = mouthMov;
            this.rightearskin.yRot = -mouthMov;
        } else {
            this.jaw.xRot = -0.17453292F;
            this.leftearskin.yRot = 0F;
            this.rightearskin.yRot = 0F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0.0, this.yOffset, 0.0);
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}