package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCSilverSkeletonEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Silver Skeleton's renderer registration (shadow 0.5). */
public class MoCSilverSkeletonRenderer extends MobRenderer<MoCSilverSkeletonEntity, MoCSilverSkeletonModel<MoCSilverSkeletonEntity>> {

    public static final ModelLayerLocation MOC_SILVER_SKELETON_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_silver_skeleton"), "main");

    private static final float SHADOW_RADIUS = 0.5F;

    public MoCSilverSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCSilverSkeletonModel<>(context.bakeLayer(MOC_SILVER_SKELETON_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCSilverSkeletonEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_silver_skeleton/silver_skeleton.png");
    }
}