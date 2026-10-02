package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBeeEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCBeeRenderer extends MoCInsectRenderer<MoCBeeEntity, MoCBeeModel<MoCBeeEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_bee");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_bee"), "main");

    public MoCBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBeeModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBeeEntity entity) {
        return TEXTURE_CACHE.get("bee");
    }
}
