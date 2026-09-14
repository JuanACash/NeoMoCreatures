package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.egg.MoCEggEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCEggRenderer extends MobRenderer<MoCEggEntity, MoCEggModel> {

    public static final ModelLayerLocation MOC_EGG_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_egg"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_egg/egg.png");

    public MoCEggRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCEggModel(context.bakeLayer(MOC_EGG_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCEggEntity entity) {
        // Same texture no matter what's inside — appearance never depends on
        // the hatch type, only the hatched creature does.
        return TEXTURE;
    }
}