package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCCricketEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/** Port of the Cricket's renderer registration (the generic renderer with shadow size 0). */
public class MoCCricketRenderer extends MobRenderer<MoCCricketEntity, MoCCricketModel<MoCCricketEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_cricket");

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_cricket"), "main");

    public MoCCricketRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCCricketModel<>(context.bakeLayer(LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCCricketEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    /** Original: while airborne it tilts nose-up when rising and nose-down when falling. */
    @Override
    protected void scale(MoCCricketEntity entity, PoseStack poseStack, float partialTick) {
        if (!entity.onGround()) {
            double vertical = entity.getDeltaMovement().y;
            float angle = vertical > 0.5D ? 35.0F : vertical < -0.5D ? -35.0F : (float) (vertical * 70.0D);
            poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        }
    }
}
