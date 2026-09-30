package com.example.neomocreatures.client;

import java.util.EnumMap;
import java.util.Map;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCFilchLizardEntity;
import com.example.neomocreatures.entity.filchlizard.FilchLizardVariant;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of {@code MoCRenderFilchLizard}: one texture per variant, plus the loot drawn in its mouth. */
public class MoCFilchLizardRenderer extends MobRenderer<MoCFilchLizardEntity, MoCFilchLizardModel<MoCFilchLizardEntity>> {

    public static final ModelLayerLocation MOC_FILCH_LIZARD_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_filch_lizard"), "main");

    private static final float SHADOW_RADIUS = 0.5F;
    private static final Map<FilchLizardVariant, ResourceLocation> TEXTURES = new EnumMap<>(FilchLizardVariant.class);

    static {
        for (FilchLizardVariant variant : FilchLizardVariant.values()) {
            TEXTURES.put(variant, ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                    "textures/entity/moc_filch_lizard/" + variant.getTextureName() + ".png"));
        }
    }

    public MoCFilchLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCFilchLizardModel<>(context.bakeLayer(MOC_FILCH_LIZARD_LAYER)), SHADOW_RADIUS);
        this.addLayer(new MoCFilchLizardLootLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCFilchLizardEntity lizard) {
        return TEXTURES.get(lizard.getVariant());
    }
}