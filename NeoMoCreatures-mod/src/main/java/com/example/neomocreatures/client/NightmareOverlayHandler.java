package com.example.neomocreatures.client;

import com.example.neomocreatures.event.NightmareRiderFireImmunityHandler;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;

/** Hides the burning overlay while the local player rides a fireproof mount. */
public class NightmareOverlayHandler {

    @SubscribeEvent
    public static void onRenderBlockOverlay(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player != null && NightmareRiderFireImmunityHandler.isOnFireproofMount(player)) {
            event.setCanceled(true);
        }
    }
}