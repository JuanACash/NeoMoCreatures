package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBoarEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Boar's renderer registration (shadow 0.7, its own texture for adult vs. baby). */
public class MoCBoarRenderer extends MobRenderer<MoCBoarEntity, MoCBoarModel<MoCBoarEntity>> {

    public static final ModelLayerLocation MOC_BOAR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_boar"), "main");

    private static final float SHADOW_RADIUS = 0.7F;

    public MoCBoarRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBoarModel<>(context.bakeLayer(MOC_BOAR_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBoarEntity entity) {
        String texture = entity.isGrownAdult() ? "boar" : "boar_baby";
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_boar/" + texture + ".png");
    }

    @Override
    protected void scale(MoCBoarEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
        float scale = entity.getAgeScale();
        poseStack.scale(scale, scale, scale);
    }
}