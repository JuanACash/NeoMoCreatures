package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBunnyEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MoCBunnyRenderer extends MobRenderer<MoCBunnyEntity, MoCBunnyModel> {

    public static final ModelLayerLocation MOC_BUNNY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_bunny"), "main");

    public MoCBunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCBunnyModel(context.bakeLayer(MOC_BUNNY_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCBunnyEntity entity) {
        // Per the user's instruction: the original's "_detailed" texture
        // pair (a legacy-vs-new art style config toggle in the real mod) is
        // repurposed here as the adult/baby distinction instead.
        String suffix = entity.isBaby() ? "" : "_detailed";
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_bunny/" + entity.getVariant().getTextureName() + suffix + ".png");
    }

    @Override
    protected void scale(MoCBunnyEntity entity, PoseStack poseStack, float partialTick) {
        // Original's getAdjustedYOffset(): a small fixed visual nudge, unrelated to growth scale.
        poseStack.translate(0.0, 0.2, 0.0);

        // Original's rotBunny(): tilts the whole model to face the direction
        // it's currently falling/rising, while airborne and not being carried.
        if (!entity.onGround() && entity.getVehicle() == null) {
            Vec3 motion = entity.getDeltaMovement();
            float angle;
            if (motion.y > 0.5D) {
                angle = 35F;
            } else if (motion.y < -0.5D) {
                angle = -35F;
            } else {
                angle = (float) (motion.y * 70.0D);
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        }
    }

    @Override
    protected void renderNameTag(MoCBunnyEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}