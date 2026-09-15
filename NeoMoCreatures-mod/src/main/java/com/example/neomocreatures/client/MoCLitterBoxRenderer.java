package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCLitterBoxEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCLitterBoxRenderer extends MobRenderer<MoCLitterBoxEntity, MoCLitterBoxModel> {

    public static final ModelLayerLocation MOC_LITTER_BOX_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_litter_box"), "main");

    public MoCLitterBoxRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCLitterBoxModel(context.bakeLayer(MOC_LITTER_BOX_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCLitterBoxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_litter_box/litter_box.png");
    }
}