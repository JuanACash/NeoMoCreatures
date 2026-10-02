package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCRoachEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCRoachRenderer extends MoCInsectRenderer<MoCRoachEntity, MoCRoachModel<MoCRoachEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_roach");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_roach"), "main");

    public MoCRoachRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCRoachModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCRoachEntity entity) {
        return TEXTURE_CACHE.get("roach");
    }
}
