package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCWerewolfEntity;
import com.example.neomocreatures.entity.werewolf.WerewolfVariant;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Port of {@code MoCRenderWerewolf}: the wolf model always drives the base render pass, with a
 *  layer that draws vanilla's plain humanoid mesh — and only that — while in human form. */
public class MoCWerewolfRenderer extends MobRenderer<MoCWerewolfEntity, MoCWerewolfModel<MoCWerewolfEntity>> {

    public static final ModelLayerLocation MOC_WEREWOLF_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_werewolf"), "main");
    public static final ModelLayerLocation MOC_WEREHUMAN_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "moc_werehuman"), "main");

    private static final float SHADOW_RADIUS = 0.7F;

    public MoCWerewolfRenderer(EntityRendererProvider.Context context) {
        super(context, new MoCWerewolfModel<>(context.bakeLayer(MOC_WEREWOLF_LAYER)), SHADOW_RADIUS);
        this.addLayer(new WerehumanLayer(this, new HumanoidModel<>(context.bakeLayer(MOC_WEREHUMAN_LAYER))));
    }

    /** Fully hides the wolf mesh in human form: relying on each of its 36 parts' own {@code visible}
     *  flag was not actually keeping it from showing through behind the human layer, so this forces
     *  it fully transparent instead — same technique already used for the Ghost Horse. */
    @Override
    public void render(MoCWerewolfEntity entity, float entityYaw, float partialTick,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        if (entity.isHumanForm()) {
            net.minecraft.client.renderer.MultiBufferSource invisibleBuffer = renderType ->
                    new com.example.neomocreatures.client.AlphaVertexConsumer(buffer.getBuffer(renderType), 0);
            super.render(entity, entityYaw, partialTick, poseStack, invisibleBuffer, packedLight);
        } else {
            super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MoCWerewolfEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                "textures/entity/moc_werewolf/" + wolfTextureFor(entity) + ".png");
    }

    /** Fire cycles through 3 frames continuously (1-2-3-1-...), same cadence as the Nightmare horse. */
    private static String wolfTextureFor(MoCWerewolfEntity entity) {
        if (entity.getVariant() == WerewolfVariant.FIRE) {
            int frame = 1 + (entity.tickCount / 5) % 3;
            return "werewolf_fire" + frame;
        }
        return entity.getVariant().getTextureName();
    }

    /** Port of the original's inner LayerMoCWereHuman: draws the plain human mesh, only visible in
     *  human form, textured by whichever wolf colour this individual last rolled — matching the
     *  original's own switch exactly, missing case included (WHITE falls through to "oldie", same
     *  as any other unlisted case, because the original's switch never had a case 3). */
    private static final class WerehumanLayer extends RenderLayer<MoCWerewolfEntity, MoCWerewolfModel<MoCWerewolfEntity>> {

        private final HumanoidModel<MoCWerewolfEntity> humanModel;

        WerehumanLayer(MoCWerewolfRenderer renderer, HumanoidModel<MoCWerewolfEntity> humanModel) {
            super(renderer);
            this.humanModel = humanModel;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           MoCWerewolfEntity entity, float limbSwing, float limbSwingAmount, float partialTick,
                           float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isHumanForm()) {
                return;
            }
            this.humanModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.getParentModel().copyPropertiesTo(this.humanModel);

            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                    "textures/entity/moc_werewolf/" + humanTextureFor(entity.getVariant()) + ".png");
            var vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            this.humanModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }

        private static String humanTextureFor(WerewolfVariant variant) {
            return switch (variant) {
                case BLACK -> "werehuman_dude";
                case BROWN -> "werehuman_classic";
                case FIRE -> "werehuman_woman";
                default -> "werehuman_oldie"; // WHITE, and anything else
            };
        }
    }
}