package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCWildWolfEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Wild Wolf's renderer registration ({@code MoCRenderWWolf}, shadow 0.5). */
public class MoCWildWolfRenderer extends MobRenderer<MoCWildWolfEntity, MoCWildWolfModel<MoCWildWolfEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_wild_wolf");

    public static final ModelLayerLocation MOC_WILD_WOLF_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_wild_wolf"), "main");

    private static final float SHADOW_RADIUS = 0.5F;

    public MoCWildWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWildWolfModel<>(context.bakeLayer(MOC_WILD_WOLF_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWildWolfEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }
}