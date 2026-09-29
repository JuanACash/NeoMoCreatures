package com.example.neomocreatures.client;

import java.util.EnumMap;
import java.util.Map;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCEntEntity;
import com.example.neomocreatures.entity.ent.EntVariant;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Ent's renderer registration (shadow 0.5, one texture per variant). */
public class MoCEntRenderer extends MobRenderer<MoCEntEntity, MoCEntModel<MoCEntEntity>> {

    public static final ModelLayerLocation MOC_ENT_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_ent"), "main");

    private static final float SHADOW_RADIUS = 0.5F;
    private static final Map<EntVariant, ResourceLocation> TEXTURES = new EnumMap<>(EntVariant.class);

    static {
        for (EntVariant variant : EntVariant.values()) {
            TEXTURES.put(variant, ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                    "textures/entity/moc_ent/" + variant.getTextureName() + ".png"));
        }
    }

    public MoCEntRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCEntModel<>(context.bakeLayer(MOC_ENT_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCEntEntity entity) {
        return TEXTURES.get(entity.getVariant());
    }
}