package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCOstrichEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCOstrichRenderer extends MobRenderer<MoCOstrichEntity, MoCOstrichModel> {

    public static final ModelLayerLocation MOC_OSTRICH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_ostrich"), "main");

    public MoCOstrichRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCOstrichModel(context.bakeLayer(MOC_OSTRICH_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCOstrichEntity entity) {
        String textureName = entity.isBaby() ? "ostrich_baby" : entity.getVariant().getTextureName();
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_ostrich/" + textureName + ".png");
    }
}