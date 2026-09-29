package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMiniGolemEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Mini Golem's renderer registration (shadow 0.5, single texture). */
public class MoCMiniGolemRenderer extends MobRenderer<MoCMiniGolemEntity, MoCMiniGolemModel<MoCMiniGolemEntity>> {

    public static final ModelLayerLocation MOC_MINI_GOLEM_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_mini_golem"), "main");

    private static final float SHADOW_RADIUS = 0.5F;
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_golem/mini_golem.png");

    public MoCMiniGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMiniGolemModel<>(context.bakeLayer(MOC_MINI_GOLEM_LAYER)), SHADOW_RADIUS);
        this.addLayer(new MoCMiniGolemHeldRockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCMiniGolemEntity entity) {
        return TEXTURE;
    }
}