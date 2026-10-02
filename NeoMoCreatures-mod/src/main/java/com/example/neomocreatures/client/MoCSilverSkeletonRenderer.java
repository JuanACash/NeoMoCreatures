package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSilverSkeletonEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Silver Skeleton's renderer registration (shadow 0.5). */
public class MoCSilverSkeletonRenderer extends MobRenderer<MoCSilverSkeletonEntity, MoCSilverSkeletonModel<MoCSilverSkeletonEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_silver_skeleton");

    public static final ModelLayerLocation MOC_SILVER_SKELETON_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_silver_skeleton"), "main");

    private static final float SHADOW_RADIUS = 0.5F;

    public MoCSilverSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSilverSkeletonModel<>(context.bakeLayer(MOC_SILVER_SKELETON_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSilverSkeletonEntity entity) {
        return TEXTURE_CACHE.get("silver_skeleton");
    }
}