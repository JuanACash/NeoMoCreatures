package com.example.neomocreatures.client;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.manticore.ManticoreVariant;

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
        if (player == null) {
            return;
        }
        if (player.getVehicle() instanceof MoCHorseEntity horse
                && (horse.getSpecies() == Species.NIGHTMARE || horse.getSpecies() == Species.DARK_PEGASUS)) {
            event.setCanceled(true);
            return;
        }
        if (player.getVehicle() instanceof MoCManticoreEntity manticore && manticore.getVariant() == ManticoreVariant.FIRE) {
            event.setCanceled(true);
        }
    }
}