package com.example.neomocreatures.client;

import java.util.EnumMap;
import java.util.Map;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCTurtleEntity;
import com.example.neomocreatures.entity.MoCTurtleEntity.TmntBrother;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;

/**
 * Port of {@code drzhark.mocreatures.client.renderer.entity.MoCRenderTurtle}.
 * <p>
 * The turtle's size is the vanilla scale attribute, which the base renderer already applies, so
 * (unlike the original) nothing here scales the model. The vertical offsets below are written in
 * unscaled model space and are scaled together with the entity.
 */
public class MoCTurtleRenderer extends MobRenderer<MoCTurtleEntity, MoCTurtleModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_turtle");

    public static final ModelLayerLocation MOC_TURTLE_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_turtle"), "main");

    private static final ResourceLocation DEFAULT_TEXTURE = texture("turtle");
    private static final Map<TmntBrother, ResourceLocation> BROTHER_TEXTURES = new EnumMap<>(TmntBrother.class);

    // The legs are shorter than the model's ground line, so the body is lowered to rest on the floor.
    private static final float WALKING_DROP = 0.05F;
    /** Larger than walking: the legs are pulled in, so the shell sits lower. */
    private static final float HIDING_DROP = 0.15F;
    /** A flipped turtle rests on its shell, which is now the lowest part of the body. */
    private static final float UPSIDE_DOWN_LIFT = 0.5F;
    private static final float ROLL_DEGREES_PER_RADIAN = 12.0F;

    public MoCTurtleRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCTurtleModel(context.bakeLayer(MOC_TURTLE_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCTurtleEntity turtle) {
        TmntBrother brother = turtle.getTmntBrother();
        if (brother == null) {
            return DEFAULT_TEXTURE;
        }
        return BROTHER_TEXTURES.computeIfAbsent(brother, b -> texture(b.getTextureName()));
    }

    @Override
    protected void scale(MoCTurtleEntity turtle, PoseStack poseStack, float partialTick) {
        if (turtle.isUpsideDown()) {
            // Rocks from side to side while it struggles, in step with its flailing legs.
            float roll = MoCTurtleModel.struggleWave(turtle.tickCount + partialTick) * ROLL_DEGREES_PER_RADIAN;
            poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F + roll));
            poseStack.translate(0.0F, UPSIDE_DOWN_LIFT, 0.0F);
        } else if (turtle.isHiding()) {
            poseStack.translate(0.0F, HIDING_DROP, 0.0F);
        } else if (!turtle.isEyeInFluid(FluidTags.WATER)) {
            poseStack.translate(0.0F, WALKING_DROP, 0.0F);
        }
    }

    @Override
    protected void renderNameTag(MoCTurtleEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    private static ResourceLocation texture(String fileName) {
        return TEXTURE_CACHE.get(fileName);
    }
}