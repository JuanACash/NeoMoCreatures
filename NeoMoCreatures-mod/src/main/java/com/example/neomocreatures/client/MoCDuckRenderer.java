package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCDuckEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Duck's renderer registration (shadow 0.3). */
public class MoCDuckRenderer extends MobRenderer<MoCDuckEntity, MoCDuckModel<MoCDuckEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_duck");

    public static final ModelLayerLocation MOC_DUCK_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_duck"), "main");

    private static final float SHADOW_RADIUS = 0.3F;

    public MoCDuckRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCDuckModel<>(context.bakeLayer(MOC_DUCK_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCDuckEntity entity) {
        return TEXTURE_CACHE.get("duck");
    }
}