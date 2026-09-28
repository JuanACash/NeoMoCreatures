package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCFireflyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Port of {@code MoCRenderFirefly}: tilts 40 degrees nose-up while flying, with a glowing overlay
 *  pass of the whole model. */
public class MoCFireflyRenderer extends MoCInsectRenderer<MoCFireflyEntity, MoCFireflyModel<MoCFireflyEntity>> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_firefly"), "main");

    private static final float FLYING_TILT_DEGREES = 40.0F;

    public MoCFireflyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCFireflyModel<>(context.bakeLayer(LAYER)));
        this.addLayer(new GlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCFireflyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_firefly/firefly.png");
    }

    @Override
    protected void scale(MoCFireflyEntity entity, PoseStack poseStack, float partialTick) {
        if (entity.isFlying()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(FLYING_TILT_DEGREES));
        }
    }

    /** Original: LayerMoCFirefly - redraws the whole model with {@code firefly_glow.png}. Drawn with
     *  the fullbright "eyes" render type so it glows regardless of the surrounding light. */
    private static final class GlowLayer extends RenderLayer<MoCFireflyEntity, MoCFireflyModel<MoCFireflyEntity>> {

        private static final ResourceLocation GLOW =
                ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_firefly/firefly_glow.png");

        GlowLayer(RenderLayerParent<MoCFireflyEntity, MoCFireflyModel<MoCFireflyEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoCFireflyEntity entity,
                           float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                           float netHeadYaw, float headPitch) {
            // Wiki: they only glow at night, and purely as ambience (it isn't a light source).
            if (entity.level().isDay()) {
                return;
            }
            VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(GLOW));
            this.getParentModel().renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
