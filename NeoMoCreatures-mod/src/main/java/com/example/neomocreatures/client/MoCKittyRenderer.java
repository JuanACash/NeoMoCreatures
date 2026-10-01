package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCKittyEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoCKittyRenderer extends MobRenderer<MoCKittyEntity, MoCKittyModel> {

    public static final ModelLayerLocation MOC_KITTY_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_kitty"), "main");

    public MoCKittyRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCKittyModel(context.bakeLayer(MOC_KITTY_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCKittyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_kitty/" + entity.getVariant().getTextureName() + ".png");
    }

    /**
     * The bed's food bowl sits on its side, not its front (and the litter box is longer along that axis).
     * A passenger's body is drawn with its vehicle's rotation, so the quarter turn that lines the kitty up
     * with the bowl has to happen here, not on the entity.
     */
    private static final float VEHICLE_FACING_OFFSET = 90.0F;

    @Override
    protected void setupRotations(MoCKittyEntity kitty, PoseStack poseStack, float bob, float yBodyRot,
                                  float partialTick, float scale) {
        if (kitty.getVehicle() instanceof com.example.neomocreatures.entity.MoCKittyBedEntity
                || kitty.getVehicle() instanceof com.example.neomocreatures.entity.MoCLitterBoxEntity) {
            yBodyRot += VEHICLE_FACING_OFFSET;
        }
        super.setupRotations(kitty, poseStack, bob, yBodyRot, partialTick, scale);
    }

    @Override
    protected void renderNameTag(MoCKittyEntity entity, Component displayName, PoseStack poseStack,
                                MultiBufferSource buffer, int packedLight, float partialTick) {
        MoCKittyEmoticonRenderer.render(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }

    @Override
    public void render(MoCKittyEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
        net.minecraft.world.entity.player.Player holder = entity.getHolder();
        if (entity.isHeld() && holder != null) {
            net.minecraft.world.phys.Vec3 holderEye = holder.getEyePosition(partialTick);
            net.minecraft.world.phys.Vec3 targetPos = entity.isBaby()
                    ? holderEye.add(0.0D, 0.2D, 0.0D)
                    : holderEye.add(0.0D, 0.2D, 0.0D);
            net.minecraft.world.phys.Vec3 renderedPos = entity.getPosition(partialTick);

            poseStack.pushPose();
            poseStack.translate(targetPos.x - renderedPos.x, targetPos.y - renderedPos.y, targetPos.z - renderedPos.z);
            super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
            poseStack.popPose();
            return;
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}