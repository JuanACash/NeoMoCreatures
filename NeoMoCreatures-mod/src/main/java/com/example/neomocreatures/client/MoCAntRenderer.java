package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCAntEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Ant's renderer registration (the generic renderer with shadow size 0). */
public class MoCAntRenderer extends MobRenderer<MoCAntEntity, MoCAntModel<MoCAntEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_ant");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_ant"), "main");

    public MoCAntRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCAntModel<>(context.bakeLayer(LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCAntEntity entity) {
        return TEXTURE_CACHE.get("ant");
    }
}
