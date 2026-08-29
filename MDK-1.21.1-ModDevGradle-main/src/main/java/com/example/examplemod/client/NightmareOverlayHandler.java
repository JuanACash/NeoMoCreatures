package com.example.examplemod.client;

import com.example.examplemod.breeding.MoCHorseGenetics.Species;
import com.example.examplemod.entity.MoCHorseEntity;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;

public class NightmareOverlayHandler {

    @SubscribeEvent
    public static void onRenderBlockOverlay(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() != RenderBlockScreenEffectEvent.OverlayType.FIRE) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof MoCHorseEntity horse
                && (horse.getSpecies() == Species.NIGHTMARE || horse.getSpecies() == Species.DARK_PEGASUS)) {
            event.setCanceled(true);
        }
    }
}