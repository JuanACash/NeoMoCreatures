package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCWyvernRenderer extends MobRenderer<MoCWyvernEntity, MoCWyvernModel> {

    public static final ModelLayerLocation MOC_WYVERN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "wyvern"), "main");

    public MoCWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWyvernModel(context.bakeLayer(MOC_WYVERN_LAYER)), 0.9F);
    }

    @Override
    protected void scale(MoCWyvernEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getTier().getRenderScale();
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWyvernEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/wyvern/" + entity.getVariant().getTextureName() + ".png");
    }
}