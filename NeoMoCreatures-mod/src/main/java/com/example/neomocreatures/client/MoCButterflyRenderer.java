package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCButterflyEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCButterflyRenderer extends MoCInsectRenderer<MoCButterflyEntity, MoCButterflyModel<MoCButterflyEntity>> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_butterfly"), "main");

    public MoCButterflyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCButterflyModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCButterflyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_butterfly/" + entity.getVariant().getTextureName() + ".png");
    }

    /** Original: tFloat() - while flying it bobs up and down 0.2 blocks on a slow sine wave. */
    @Override
    protected void scale(MoCButterflyEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
        if (entity.isFlying()) {
            poseStack.translate(0.0D, net.minecraft.util.Mth.cos(entity.tickCount * 0.1F) * 0.2F, 0.0D);
        }
        super.scale(entity, poseStack, partialTick);
    }
}
