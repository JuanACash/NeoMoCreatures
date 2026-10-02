package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCGrasshopperEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoCGrasshopperRenderer extends MoCInsectRenderer<MoCGrasshopperEntity, MoCGrasshopperModel<MoCGrasshopperEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_grasshopper");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_grasshopper"), "main");

    public MoCGrasshopperRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCGrasshopperModel<>(context.bakeLayer(LAYER)));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCGrasshopperEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    /** Original: while airborne it tilts nose-up when rising and nose-down when falling. */
    @Override
    protected void scale(MoCGrasshopperEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
        super.scale(entity, poseStack, partialTick);
        if (!entity.onGround()) {
            double vertical = entity.getDeltaMovement().y;
            float angle = vertical > 0.5D ? 35.0F : vertical < -0.5D ? -35.0F : (float) (vertical * 70.0D);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(angle));
        }
    }
}
