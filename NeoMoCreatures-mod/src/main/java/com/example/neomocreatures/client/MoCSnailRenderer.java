package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSnailEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Snail's renderer registration (the generic renderer with shadow size 0). */
public class MoCSnailRenderer extends MobRenderer<MoCSnailEntity, MoCSnailModel<MoCSnailEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_snail");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_snail"), "main");

    public MoCSnailRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSnailModel<>(context.bakeLayer(LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSnailEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }
}
