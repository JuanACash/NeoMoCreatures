package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCOgreEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Shared renderer for the 3 ogre species ({@code MoCRenderMoC} with {@code MoCModelOgre}, shadow 0.9). */
public class MoCOgreRenderer<T extends MoCOgreEntity> extends MobRenderer<T, MoCOgreModel<T>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_ogre");

    public static final ModelLayerLocation MOC_OGRE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_ogre"), "main");

    private static final float SHADOW_RADIUS = 0.9F;

    public MoCOgreRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCOgreModel<>(context.bakeLayer(MOC_OGRE_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_CACHE.get(entity.getTextureName());
    }
}