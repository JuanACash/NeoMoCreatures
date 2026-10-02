package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCLitterBoxEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCLitterBoxRenderer extends MobRenderer<MoCLitterBoxEntity, MoCLitterBoxModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_litter_box");

    public static final ModelLayerLocation MOC_LITTER_BOX_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_litter_box"), "main");

    public MoCLitterBoxRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCLitterBoxModel(context.bakeLayer(MOC_LITTER_BOX_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCLitterBoxEntity entity) {
        return TEXTURE_CACHE.get("litter_box");
    }
}