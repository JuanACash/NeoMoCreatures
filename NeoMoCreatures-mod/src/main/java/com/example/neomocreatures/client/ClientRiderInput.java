package com.example.neomocreatures.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Client-only queries used by common entity code. Keeping every Minecraft/LocalPlayer
 * reference here means entity classes never touch client classes directly; callers must
 * only use it when level().isClientSide is true.
 */
public final class ClientRiderInput {

    private ClientRiderInput() {
        // Utility class, no instances
    }

    /** True if the given entity is the player playing on this client. */
    public static boolean isLocalPlayer(@Nullable Entity entity) {
        return entity == Minecraft.getInstance().player;
    }

    /** Jump key held: flying mounts climb. */
    public static boolean isAscendDown() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }

    /** Mod descend key held: flying mounts dive. */
    public static boolean isDescendDown() {
        return ModKeyMappings.DESCEND.isDown();
    }

    /** True if the local player is closer than the given squared distance to the entity. */
    public static boolean isLocalPlayerWithin(Entity entity, double maxDistanceSqr) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && entity.distanceToSqr(player) < maxDistanceSqr;
    }
}