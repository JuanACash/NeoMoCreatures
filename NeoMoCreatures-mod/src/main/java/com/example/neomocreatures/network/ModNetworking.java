package com.example.neomocreatures.network;

import com.example.neomocreatures.client.ClientPayloadHandlers;
import com.example.neomocreatures.entity.AscendingMount;
import com.example.neomocreatures.entity.DescendingMount;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Registers every mod packet. Client-bound handlers live in ClientPayloadHandlers;
 * server-bound handlers are below.
 */
public class ModNetworking {

    /** Longest name a pet can be given from the naming screen. */
    private static final int MAX_PET_NAME_LENGTH = 32;

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(OpenNamingScreenPayload.TYPE, OpenNamingScreenPayload.STREAM_CODEC,
                ClientPayloadHandlers::openNamingScreen);
        registrar.playToServer(SetPetNamePayload.TYPE, SetPetNamePayload.STREAM_CODEC,
                ModNetworking::handleSetPetName);
        registrar.playToClient(OpenPlayerInventoryPayload.TYPE, OpenPlayerInventoryPayload.STREAM_CODEC,
                ClientPayloadHandlers::openPlayerInventory);
        registrar.playToServer(DescendInputPayload.TYPE, DescendInputPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player().getVehicle() instanceof DescendingMount mount) {
                        mount.setDescendHeld(payload.pressed());
                    }
                }));
        registrar.playToServer(AscendInputPayload.TYPE, AscendInputPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player().getVehicle() instanceof AscendingMount mount) {
                        mount.setAscendHeld(payload.pressed());
                    }
                }));
    }

    /**
     * Only the pet's owner can (re)name it; a tamed but ownerless pet (Scroll of Sale / Reset Owner)
     * is adopted by whoever renames it first. Works for any TamableAnimal or AbstractHorse.
     */
    private static void handleSetPetName(SetPetNamePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.name().isBlank() || payload.name().length() > MAX_PET_NAME_LENGTH) {
                return;
            }
            Player player = context.player();
            Entity entity = player.level().getEntity(payload.entityId());
            if (entity == null) {
                return;
            }
            boolean isOwner = (entity instanceof TamableAnimal tamable
                            && player.getUUID().equals(tamable.getOwnerUUID()))
                    || (entity instanceof AbstractHorse horse
                            && player.getUUID().equals(horse.getOwnerUUID()));
            boolean isAdoptableOwnerless = (entity instanceof TamableAnimal ownerlessTamable
                            && ownerlessTamable.isTame() && ownerlessTamable.getOwnerUUID() == null)
                    || (entity instanceof AbstractHorse ownerlessHorse
                            && ownerlessHorse.isTamed() && ownerlessHorse.getOwnerUUID() == null);
            if (!isOwner && !isAdoptableOwnerless) {
                return;
            }
            if (isAdoptableOwnerless) {
                if (entity instanceof TamableAnimal adoptedTamable) {
                    adoptedTamable.setOwnerUUID(player.getUUID());
                } else if (entity instanceof AbstractHorse adoptedHorse) {
                    adoptedHorse.setOwnerUUID(player.getUUID());
                }
            }
            entity.setCustomName(Component.literal(payload.name()));
            entity.setCustomNameVisible(true);
        });
    }
}
