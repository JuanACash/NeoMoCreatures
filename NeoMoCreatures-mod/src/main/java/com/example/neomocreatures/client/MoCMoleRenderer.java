package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCMoleEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the Mole's real renderer registration: in the original it has no dedicated renderer
 *  class at all, it's registered directly as the shared generic {@code MoCRenderMoC} with shadow
 *  size 0.0. The pitch tilt is smoothed toward its target by a fixed 0.05 blend factor per render
 *  call (shared across every mole, same as the original's own shared renderer instance); the sink
 *  offset is applied raw, with no smoothing at all — exactly like the source. */
public class MoCMoleRenderer extends MobRenderer<MoCMoleEntity, MoCMoleModel<MoCMoleEntity>> {

    public static final ModelLayerLocation MOC_MOLE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_mole"), "main");

    private static final float SHADOW_RADIUS = 0.0F;
    public MoCMoleRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCMoleModel<>(context.bakeLayer(MOC_MOLE_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCMoleEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_mole/mole.png");
    }

    @Override
    protected void scale(MoCMoleEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.translate(0.0D, entity.getSinkOffset(), 0.0D);

        float tilt = entity.getTiltDegrees();
        if (tilt != 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
        }
    }
}