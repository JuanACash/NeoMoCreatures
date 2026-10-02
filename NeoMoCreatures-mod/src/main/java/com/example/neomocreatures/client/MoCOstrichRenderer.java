package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCOstrichEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MoCOstrichRenderer extends MobRenderer<MoCOstrichEntity, MoCOstrichModel> {

    private static final EntityTextureCache TEXTURE_CACHE = new EntityTextureCache("moc_ostrich");

    public static final ModelLayerLocation MOC_OSTRICH_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_ostrich"), "main");

    public MoCOstrichRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCOstrichModel(context.bakeLayer(MOC_OSTRICH_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(MoCOstrichEntity entity) {
        if (entity.isBaby()) {
            return TEXTURE_CACHE.get("ostrich_baby");
        }

        int currentEssence = entity.getEssence();
        if (entity.isTransforming()) {
            int ticksLeft = entity.getTransformTicks();
            int interval = Math.max(1, ticksLeft / 8);
            boolean showTarget = (entity.tickCount / interval) % 2 == 0;
            if (showTarget) {
                currentEssence = entity.getPendingEssence();
            }
        }

        String textureName = essenceTextureName(currentEssence, entity.getVariant());
        return TEXTURE_CACHE.get(textureName);
    }

    private static String essenceTextureName(int essence, com.example.neomocreatures.entity.ostrich.OstrichVariant variant) {
        return switch (essence) {
            case MoCOstrichEntity.ESSENCE_WYVERN -> "ostrich_dark";
            case MoCOstrichEntity.ESSENCE_FIRE -> "ostrich_fire";
            case MoCOstrichEntity.ESSENCE_UNDEAD -> "ostrich_undead";
            case MoCOstrichEntity.ESSENCE_UNIHORNED -> "ostrich_light";
            default -> variant.getTextureName();
        };
    }

    @Override
    protected void renderNameTag(MoCOstrichEntity entity, net.minecraft.network.chat.Component displayName,
                                com.mojang.blaze3d.vertex.PoseStack poseStack,
                                net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, float partialTick) {
        if (this.entityRenderDispatcher.distanceToSqr(entity) > TameableOverlayRenderer.NAME_AND_HEALTH_SHOW_DISTANCE_SQR) {
            return;
        }
        TameableOverlayRenderer.renderHealthBar(entity, poseStack, buffer, packedLight, this.entityRenderDispatcher);
        super.renderNameTag(entity, displayName, poseStack, buffer, packedLight, partialTick);
    }
}