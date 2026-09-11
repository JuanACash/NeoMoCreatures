package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBearEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCBearRenderer extends MobRenderer<MoCBearEntity, MoCBearModel> {

    public static final ModelLayerLocation MOC_BEAR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_bear"), "main");

    public MoCBearRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBearModel(context.bakeLayer(MOC_BEAR_LAYER)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBearEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_bear/" + entity.getVariant().getTextureName() + ".png");
    }
}