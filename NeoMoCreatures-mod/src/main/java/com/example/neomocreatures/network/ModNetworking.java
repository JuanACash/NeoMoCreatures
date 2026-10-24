package com.example.neomocreatures.network;

import java.util.UUID;

import com.example.neomocreatures.client.ClientPayloadHandlers;
import com.example.neomocreatures.entity.AscendingMount;
import com.example.neomocreatures.entity.DescendingMount;
import com.example.neomocreatures.util.PetOwnershipUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
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

    /** Max squared distance (16 blocks) between the player and the pet being renamed. */
    private static final double MAX_NAMING_DISTANCE_SQR = 16.0 * 16.0;

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
            // Ignore missing or dead entities, and entities too far away: the client
            // sends a raw entity id, so the server must not trust it blindly.
            if (entity == null || !entity.isAlive()
                    || player.distanceToSqr(entity) > MAX_NAMING_DISTANCE_SQR) {
                return;
            }
            UUID playerId = player.getUUID();
            // A tamed pet with no owner is adopted by whoever renames it first.
            boolean adopting = PetOwnershipUtil.isTamedWithoutOwner(entity);
            if (!adopting && !PetOwnershipUtil.isOwnedBy(entity, playerId)) {
                return;
            }
            if (adopting) {
                PetOwnershipUtil.setOwner(entity, playerId);
            }
            entity.setCustomName(Component.literal(payload.name()));
            entity.setCustomNameVisible(true);
        });
    }
}
