package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCStingrayEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Port of the stingray's renderer registration ({@code MoCRenderMoC} with {@code MoCModelRay}, shadow 0.4).
 * The 0.9x size is the entity's scale attribute, which the base renderer already applies.
 */
public class MoCStingrayRenderer extends MobRenderer<MoCStingrayEntity, MoCStingrayModel> {


    /** How far the body sinks into the block below when the stingray rests on the bottom. */
    private static final float BOTTOM_SINK = 0.12F;

    public static final ModelLayerLocation MOC_STINGRAY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_stingray"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_stingray/ray_sting.png");

    public MoCStingrayRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCStingrayModel(context.bakeLayer(MOC_STINGRAY_LAYER)), 0.4F);
    }

    @Override
    protected void scale(MoCStingrayEntity ray, PoseStack poseStack, float partialTick) {
        // Resting on the bottom, the wings sink into the sand: it looks like the stingray is hiding in the block.
        if (ray.onGround()) {
            poseStack.translate(0.0F, BOTTOM_SINK, 0.0F);
        }
    }

    @Override
    protected void renderNameTag(MoCStingrayEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCStingrayEntity entity) {
        return TEXTURE;
    }
}