package com.example.examplemod.util;

import com.example.examplemod.network.OpenNamingScreenPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

/**
 * Not creature-specific — anything that just got tamed/born-tamed (bred
 * offspring, a hatched egg the player placed, a future wyvern/dolphin/etc.)
 * can call promptRename() the moment it's tamed, as long as the owner is
 * online.
 */
public final class NamingHelper {

    private NamingHelper() {
    }

    public static void promptRename(Entity entity, UUID ownerUUID) {
        if (ownerUUID == null || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
        if (owner != null) {
            PacketDistributor.sendToPlayer(owner, new OpenNamingScreenPayload(entity.getId()));
        }
    }
}