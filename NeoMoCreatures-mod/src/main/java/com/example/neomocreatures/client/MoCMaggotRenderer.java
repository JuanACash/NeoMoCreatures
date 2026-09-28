package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMaggotEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Maggot's renderer registration (the generic renderer with shadow size 0). */
public class MoCMaggotRenderer extends MobRenderer<MoCMaggotEntity, MoCMaggotModel<MoCMaggotEntity>> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_maggot"), "main");

    public MoCMaggotRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMaggotModel<>(context.bakeLayer(LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCMaggotEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_maggot/" + "maggot" + ".png");
    }
}
