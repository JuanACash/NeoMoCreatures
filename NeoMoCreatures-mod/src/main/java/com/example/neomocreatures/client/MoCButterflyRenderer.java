package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCButterflyEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;

public class MoCButterflyRenderer extends MoCInsectRenderer<MoCButterflyEntity, MoCButterflyModel<MoCButterflyEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_butterfly");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_butterfly"), "main");

    public MoCButterflyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCButterflyModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCButterflyEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    /** Original: tFloat() - while flying it bobs up and down 0.2 blocks on a slow sine wave. */
    @Override
    protected void scale(MoCButterflyEntity entity, PoseStack poseStack, float partialTick) {
        if (entity.isFlying()) {
            poseStack.translate(0.0D, Mth.cos(entity.tickCount * 0.1F) * 0.2F, 0.0D);
        }
        super.scale(entity, poseStack, partialTick);
    }
}
