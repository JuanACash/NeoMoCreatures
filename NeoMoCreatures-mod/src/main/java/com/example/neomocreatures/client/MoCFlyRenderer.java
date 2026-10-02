package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCFlyEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCFlyRenderer extends MoCInsectRenderer<MoCFlyEntity, MoCFlyModel<MoCFlyEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_fly");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_fly"), "main");

    public MoCFlyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCFlyModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCFlyEntity entity) {
        return TEXTURE_CACHE.get("fly");
    }
}
