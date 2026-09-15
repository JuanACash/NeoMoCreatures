package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCKittyBedEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCKittyBedRenderer extends MobRenderer<MoCKittyBedEntity, MoCKittyBedModel> {

    public static final ModelLayerLocation MOC_KITTY_BED_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_kitty_bed"), "main");

    public MoCKittyBedRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCKittyBedModel(context.bakeLayer(MOC_KITTY_BED_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCKittyBedEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_kitty_bed/kitty_bed.png");
    }
}