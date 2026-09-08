package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCScorpionEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCScorpionRenderer extends MobRenderer<MoCScorpionEntity, MoCScorpionModel> {

    public static final ModelLayerLocation MOC_SCORPION_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_scorpion"), "main");

    public MoCScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCScorpionModel(context.bakeLayer(MOC_SCORPION_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCScorpionEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_scorpion/" + entity.getVariant().getTextureName() + ".png");
    }
}