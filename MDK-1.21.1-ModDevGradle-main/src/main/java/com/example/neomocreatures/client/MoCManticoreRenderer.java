package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCManticoreEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCManticoreRenderer extends MobRenderer<MoCManticoreEntity, MoCManticoreModel> {

    public static final ModelLayerLocation MOC_MANTICORE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_manticore"), "main");

    public MoCManticoreRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCManticoreModel(context.bakeLayer(MOC_MANTICORE_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCManticoreEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_manticore/" + entity.getVariant().getTextureName() + ".png");
    }
}