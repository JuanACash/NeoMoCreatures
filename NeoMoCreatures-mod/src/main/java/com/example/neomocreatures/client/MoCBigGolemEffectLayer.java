package com.example.neomocreatures.client;

import java.util.EnumMap;
import java.util.Map;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCBigGolemEntity;
import com.example.neomocreatures.entity.golem.GolemState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Port of {@code MoCRenderGolem.LayerMoCGolem}: a scrolling energy glow whose colour shows the golem's state. */
public class MoCBigGolemEffectLayer extends RenderLayer<MoCBigGolemEntity, MoCBigGolemModel<MoCBigGolemEntity>> {

    /** Original: rendered at half brightness (0.5 on every colour channel). */
    private static final int EFFECT_COLOR = 0xFF808080;
    private static final float SCROLL_SPEED = 0.01F;
    /** Dying flickers between red and orange every few ticks — "red = about to explode", as on the wiki. */
    private static final int DYING_FLICKER_TICKS = 3;
    private static final Map<GolemState, ResourceLocation> TEXTURES = new EnumMap<>(GolemState.class);

    static {
        for (GolemState state : GolemState.values()) {
            String name = state.getEffectTextureName();
            if (name != null) {
                TEXTURES.put(state, ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                        "textures/entity/moc_golem/" + name + ".png"));
            }
        }
    }

    public MoCBigGolemEffectLayer(RenderLayerParent<MoCBigGolemEntity, MoCBigGolemModel<MoCBigGolemEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, MoCBigGolemEntity golem,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        GolemState state = golem.getGolemState();
        if (state == GolemState.DYING && (golem.tickCount / DYING_FLICKER_TICKS) % 2 == 0) {
            state = GolemState.ENRAGED;
        }
        ResourceLocation texture = TEXTURES.get(state);
        if (texture == null) {
            return;
        }
        float scroll = ((golem.tickCount + partialTick) * SCROLL_SPEED) % 1.0F;
        VertexConsumer glow = buffer.getBuffer(RenderType.energySwirl(texture, scroll, scroll));
        this.getParentModel().renderToBuffer(poseStack, glow, packedLight, OverlayTexture.NO_OVERLAY, EFFECT_COLOR);
    }
}