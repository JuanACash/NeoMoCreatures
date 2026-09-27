package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMouseEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Mouse's renderer registration (shadow 0.2). */
public class MoCMouseRenderer extends MobRenderer<MoCMouseEntity, MoCMouseModel<MoCMouseEntity>> {

    public static final ModelLayerLocation MOC_MOUSE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_mouse"), "main");

    private static final float SHADOW_RADIUS = 0.2F;

    public MoCMouseRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMouseModel<>(context.bakeLayer(MOC_MOUSE_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCMouseEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_mouse/" + entity.getVariant().getTextureName() + ".png");
    }

    @Override
    protected void scale(MoCMouseEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
        float scale = 0.6F;
        poseStack.scale(scale, scale, scale);
    }
}