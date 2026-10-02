package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;


public class MoCScorpionRenderer extends MobRenderer<MoCScorpionEntity, MoCScorpionModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_scorpion");

    public static final ModelLayerLocation MOC_SCORPION_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_scorpion"), "main");

    public MoCScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCScorpionModel(context.bakeLayer(MOC_SCORPION_LAYER)), 0.5F);
    }

    @Override
    public void render(MoCScorpionEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
        net.minecraft.world.entity.player.Player holder = entity.isHeld() ? entity.getHolder() : null;
        if (holder != null) {
            net.minecraft.world.phys.Vec3 scorpionPos = entity.getPosition(partialTicks);
            net.minecraft.world.phys.Vec3 look = holder.getViewVector(partialTicks);
            net.minecraft.world.phys.Vec3 handPos = holder.getEyePosition(partialTicks)
                    .add(look.scale(0.6D))
                    .add(0.0D, -0.35D, 0.0D);
            net.minecraft.world.phys.Vec3 delta = handPos.subtract(scorpionPos);

            poseStack.pushPose();
            poseStack.translate(delta.x, delta.y + 0.15D, delta.z);
            poseStack.mulPose(Axis.XP.rotationDegrees(90F));
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
            return;
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCScorpionEntity entity) {
        if (entity.isTransforming()) {
            int ticksLeft = entity.getTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showUndead = (entity.tickCount / interval) % 2 == 0;
            ScorpionVariant shown = showUndead ? ScorpionVariant.UNDEAD : entity.getVariant();
            return TEXTURE_CACHE.get(shown.getTextureName());
        }
        // Saddle is texture-only, no separate 3D model — just swap to the "_saddled" file.
        String suffix = (entity.isSaddled() && !entity.isBaby()) ? "_saddled" : "";
        return TEXTURE_CACHE.get(entity.getVariant().getTextureName() + suffix);
    }

    @Override
    protected void renderNameTag(MoCScorpionEntity entity, Component displayName, PoseStack poseStack,
                                  MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}