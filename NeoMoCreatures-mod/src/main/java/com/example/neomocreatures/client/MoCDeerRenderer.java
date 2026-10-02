package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCDeerEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/** Port of the Deer's renderer registration (shadow 0.7). */
public class MoCDeerRenderer extends MobRenderer<MoCDeerEntity, MoCDeerModel<MoCDeerEntity>> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_deer");

    public static final ModelLayerLocation MOC_DEER_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_deer"), "main");

    private static final float SHADOW_RADIUS = 0.7F;

    public MoCDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCDeerModel<>(context.bakeLayer(MOC_DEER_LAYER)), SHADOW_RADIUS);
    }

    private float prevLeapTilt;

    @Override
    public ResourceLocation getTextureLocation(MoCDeerEntity entity) {
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName());
    }

    @Override
    protected void scale(MoCDeerEntity entity, PoseStack poseStack, float partialTick) {
        float scale = entity.getAgeScale();
        poseStack.scale(scale, scale, scale);

        // Applied directly (no cross-frame smoothing) — an earlier attempt at interpolating this
        // per render frame instead of per game tick left it stuck partway instead of resetting
        // cleanly to 0 on the ground.
        float tilt = entity.getLeapTiltDegrees();
        if (tilt != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(tilt));
        }
    }
}