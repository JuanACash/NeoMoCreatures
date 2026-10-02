package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCDragonflyEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCDragonflyRenderer extends MoCInsectRenderer<MoCDragonflyEntity, MoCDragonflyModel<MoCDragonflyEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_dragonfly");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_dragonfly"), "main");

    public MoCDragonflyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCDragonflyModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCDragonflyEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }
}
