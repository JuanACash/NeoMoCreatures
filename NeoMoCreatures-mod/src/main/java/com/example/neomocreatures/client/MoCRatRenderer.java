package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCRatEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Rat's renderer registration (shadow 0.3). */
public class MoCRatRenderer extends MobRenderer<MoCRatEntity, MoCRatModel<MoCRatEntity>> {

    public static final ModelLayerLocation MOC_RAT_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_rat"), "main");

    private static final float SHADOW_RADIUS = 0.3F;

    public MoCRatRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCRatModel<>(context.bakeLayer(MOC_RAT_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCRatEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_rat/" + entity.getVariant().getTextureName() + ".png");
    }
}