package com.example.neomocreatures.client;

import com.example.neomocreatures.network.OpenNamingScreenPayload;
import com.example.neomocreatures.network.OpenPlayerInventoryPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client side of the server-to-client packets. Kept in the client package so the common
 * networking code never references client-only classes (screens, Minecraft).
 */
public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {
        // Utility class, no instances
    }

    public static void openNamingScreen(OpenNamingScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().setScreen(new MoCNamingScreen(payload.entityId())));
    }

    /** Opens the player's own inventory (E key while riding a mount without its own screen). */
    public static void openPlayerInventory(OpenPlayerInventoryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.setScreen(new InventoryScreen(mc.player));
            }
        });
    }
}
