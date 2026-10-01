package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBigGolemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of {@code MoCRenderGolem}: the block body drawn 1.8x bigger, with its glowing state overlay on top. */
public class MoCBigGolemRenderer extends MobRenderer<MoCBigGolemEntity, MoCBigGolemModel<MoCBigGolemEntity>> {

    public static final ModelLayerLocation MOC_BIG_GOLEM_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_big_golem"), "main");

    /** Original: getSizeFactor() = 1.8. */
    private static final float MODEL_SCALE = 1.8F;
    /** Original shadow 0.5 before scaling. */
    private static final float SHADOW_RADIUS = 0.5F * MODEL_SCALE;
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_golem/golem.png");

    public MoCBigGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBigGolemModel<>(context.bakeLayer(MOC_BIG_GOLEM_LAYER)), SHADOW_RADIUS);
        this.addLayer(new MoCBigGolemEffectLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBigGolemEntity golem) {
        return TEXTURE;
    }

    @Override
    protected void scale(MoCBigGolemEntity golem, PoseStack poseStack, float partialTick) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        float lean = golem.getLeanDegrees();
        if (lean != 0.0F) {
            poseStack.mulPose(Axis.ZN.rotationDegrees(lean));
        }
    }
}