package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCCrocodileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Port of the crocodile's renderer registration ({@code MoCRenderCrocodile}, shadow 0.7). */
public class MoCCrocodileRenderer extends MobRenderer<MoCCrocodileEntity, MoCCrocodileModel> {

    public static final ModelLayerLocation MOC_CROCODILE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_crocodile"), "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/entity/moc_crocodile/crocodile.png");

    private static final float SHADOW_RADIUS = 0.7F;

    public MoCCrocodileRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCCrocodileModel(context.bakeLayer(MOC_CROCODILE_LAYER)), SHADOW_RADIUS);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCCrocodileEntity entity) {
        return TEXTURE;
    }

    /** The real "death roll": tumbles the model around its own forward axis, not around the vertical
     *  (yaw) axis — a barrel roll, matching what real crocodiles actually do. */
    @Override
    protected void scale(MoCCrocodileEntity entity, PoseStack poseStack, float partialTick) {
        if (entity.getRollAngle() != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getRollAngle()));
        }
    }
}