package com.example.examplemod.network;

import com.example.examplemod.client.MoCNamingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenNamingScreenPayload.TYPE,
                OpenNamingScreenPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() ->
                        Minecraft.getInstance().setScreen(new MoCNamingScreen(payload.entityId()))));

        registrar.playToServer(
                SetPetNamePayload.TYPE,
                SetPetNamePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (payload.name().isBlank() || payload.name().length() > 32) {
                        return;
                    }
                    Entity entity = context.player().level().getEntity(payload.entityId());
                    // Only the entity's own owner can (re)name it — checked
                    // generically via TamableAnimal/AbstractHorse, so this
                    // works for any future tameable creature too.
                    boolean isOwner = (entity instanceof net.minecraft.world.entity.TamableAnimal tamable
                                    && context.player().getUUID().equals(tamable.getOwnerUUID()))
                            || (entity instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse
                                    && context.player().getUUID().equals(horse.getOwnerUUID()));
                    if (entity != null && isOwner) {
                        entity.setCustomName(Component.literal(payload.name()));
                        entity.setCustomNameVisible(true);
                    }
                }));
                
        registrar.playToServer(
            DescendInputPayload.TYPE,
            DescendInputPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> {
                if (context.player().getVehicle() instanceof com.example.examplemod.entity.MoCHorseEntity horse) {
                    horse.setDescendHeld(payload.pressed());
                }
            }));

        registrar.playToServer(
            AscendInputPayload.TYPE,
            AscendInputPayload.STREAM_CODEC,
            (payload, context) -> context.enqueueWork(() -> {
                if (context.player().getVehicle() instanceof com.example.examplemod.entity.MoCHorseEntity horse) {
                    horse.setAscendHeld(payload.pressed());
                }
            }));
    }
}