package com.example.neomocreatures.client;

import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MoCKittyBedModel extends HierarchicalModel<MoCKittyBedEntity> {

    // The original's 16-entry fleeceColorTable (RGB 0-1) * 0.35 brightness, packed as 0xRRGGBB.
    // Order matches vanilla DyeColor: white, orange, magenta, light_blue, yellow, lime, pink, gray,
    // light_gray, cyan, purple, blue, brown, green, red, black.
        private static final int[] SHEET_TINTS = {
                0xFFFFFF, 0xF2B333, 0xE680D9, 0x99B3F2, 0xE6E633, 0x80CC1A, 0xF2B3CC, 0x4D4D4D,
                0x999999, 0x4D99B3, 0xB366E6, 0x3366CC, 0x80664D, 0x668033, 0xCC4D4D, 0x1A1A1A
        };

    private final ModelPart root;
    private final ModelPart tableL;
    private final ModelPart tableR;
    private final ModelPart tableB;
    private final ModelPart bottom;
    private final ModelPart foodT;
    private final ModelPart foodTraySide;
    private final ModelPart foodTraySideB;
    private final ModelPart foodTraySideC;
    private final ModelPart foodTraySideD;
    private final ModelPart milk;
    private final ModelPart petFood;
    private final ModelPart sheet;

    private int sheetColorIndex;

    public MoCKittyBedModel(ModelPart root) {
        this.root = root;
        this.tableL = root.getChild("table_l");
        this.tableR = root.getChild("table_r");
        this.tableB = root.getChild("table_b");
        this.bottom = root.getChild("bottom");
        this.foodT = root.getChild("food_t");
        this.foodTraySide = root.getChild("food_tray_side");
        this.foodTraySideB = root.getChild("food_tray_side_b");
        this.foodTraySideC = root.getChild("food_tray_side_c");
        this.foodTraySideD = root.getChild("food_tray_side_d");
        this.milk = root.getChild("milk");
        this.petFood = root.getChild("pet_food");
        this.sheet = root.getChild("sheet");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("table_l", CubeListBuilder.create().texOffs(30, 8).addBox(-8F, 0F, 7F, 16, 6, 1),
                PartPose.offset(0F, 18F, 0F));
        root.addOrReplaceChild("table_r", CubeListBuilder.create().texOffs(30, 8).addBox(-8F, 18F, -8F, 16, 6, 1),
                PartPose.ZERO);
        root.addOrReplaceChild("table_b", CubeListBuilder.create().texOffs(30, 0).addBox(-8F, -3F, 0F, 16, 6, 1),
                PartPose.offsetAndRotation(8F, 21F, 0F, 0F, 1.5708F, 0F));
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(16, 15).addBox(-10F, 0F, -7F, 16, 1, 14),
                PartPose.offset(2F, 23F, 0F));

        root.addOrReplaceChild("food_t", CubeListBuilder.create().texOffs(14, 0).addBox(1F, 1F, 1F, 4, 1, 4),
                PartPose.offset(-16F, 22F, 0F));
        root.addOrReplaceChild("food_tray_side", CubeListBuilder.create().texOffs(0, 0).addBox(-16F, 21F, 5F, 5, 3, 1),
                PartPose.ZERO);
        root.addOrReplaceChild("food_tray_side_b", CubeListBuilder.create().texOffs(0, 0).addBox(-15F, 21F, 0F, 5, 3, 1),
                PartPose.ZERO);
        root.addOrReplaceChild("food_tray_side_c", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -1F, 0F, 5, 3, 1),
                PartPose.offsetAndRotation(-16F, 22F, 2F, 0F, 1.5708F, 0F));
        root.addOrReplaceChild("food_tray_side_d", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -1F, 0F, 5, 3, 1),
                PartPose.offsetAndRotation(-11F, 22F, 3F, 0F, 1.5708F, 0F));

        root.addOrReplaceChild("milk", CubeListBuilder.create().texOffs(14, 9).addBox(0F, 0F, 0F, 4, 1, 4),
                PartPose.offset(-15F, 21F, 1F));
        root.addOrReplaceChild("pet_food", CubeListBuilder.create().texOffs(0, 9).addBox(0F, 0F, 0F, 4, 1, 4),
                PartPose.offset(-15F, 21F, 1F));

        root.addOrReplaceChild("sheet", CubeListBuilder.create().texOffs(0, 15).addBox(0F, 0F, 0F, 16, 3, 14),
                PartPose.offset(-8F, 21F, -7F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(MoCKittyBedEntity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        boolean pickedUp = entity.isPickedUp();
        foodT.visible = !pickedUp;
        foodTraySide.visible = !pickedUp;
        foodTraySideB.visible = !pickedUp;
        foodTraySideC.visible = !pickedUp;
        foodTraySideD.visible = !pickedUp;
        milk.visible = !pickedUp && entity.hasMilk();
        petFood.visible = !pickedUp && entity.hasFood();
        milk.y = 21F + entity.getMilkLevel();
        petFood.y = 21F + entity.getMilkLevel();
        this.sheetColorIndex = entity.getSheetColor();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        // Pass 1: everything except the sheet, at the color the caller gave us (normal, untinted).
        sheet.visible = false;
        root.render(poseStack, buffer, packedLight, packedOverlay, color);

        // Pass 2: just the sheet, tinted to match the dyed color.
        int rgb = SHEET_TINTS[Math.floorMod(sheetColorIndex, SHEET_TINTS.length)];
        int tint = 0xFF000000 | rgb;
        sheet.visible = true;
        sheet.render(poseStack, buffer, packedLight, packedOverlay, tint);
    }
}