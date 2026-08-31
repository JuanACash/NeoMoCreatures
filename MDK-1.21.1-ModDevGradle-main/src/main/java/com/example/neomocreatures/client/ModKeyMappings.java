package com.example.neomocreatures.client;

import com.example.neomocreatures.network.DescendInputPayload;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ModKeyMappings {

    public static final KeyMapping DESCEND = new KeyMapping(
            "key.neomocreatures.descend",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_Z,
            "key.categories.neomocreatures"
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DESCEND);
    }

    @EventBusSubscriber(modid = "neomocreatures", value = Dist.CLIENT)
    public static class TickHandler {
        private static boolean wasDescendPressed = false;
        private static boolean wasAscendPressed = false;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            boolean descendPressed = DESCEND.isDown();
            if (descendPressed != wasDescendPressed) {
                wasDescendPressed = descendPressed;
                if (Minecraft.getInstance().player != null) {
                    PacketDistributor.sendToServer(new com.example.neomocreatures.network.DescendInputPayload(descendPressed));
                }
            }

            boolean ascendPressed = Minecraft.getInstance().options.keyJump.isDown();
            if (ascendPressed != wasAscendPressed) {
                wasAscendPressed = ascendPressed;
                if (Minecraft.getInstance().player != null) {
                    PacketDistributor.sendToServer(new com.example.neomocreatures.network.AscendInputPayload(ascendPressed));
                }
            }
        }
    }
}