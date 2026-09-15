package com.example.neomocreatures.event;

import com.example.neomocreatures.entity.MoCKittyEntity;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * A kitty picked up (see MoCKittyEntity#startHolding) is released by
 * right-clicking ANYWHERE — same mechanic as the baby scorpion.
 */
public class KittyHoldReleaseHandler {

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        tryRelease(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (tryRelease(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (tryRelease(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static boolean tryRelease(Player player) {
        if (player.level().isClientSide) {
            return false;
        }
        var held = player.level().getEntitiesOfClass(MoCKittyEntity.class,
                player.getBoundingBox().inflate(3.0D),
                kitty -> kitty.getHolder() == player && kitty.getKittyState() != 14);
        if (held.isEmpty()) {
            return false;
        }
        held.get(0).releaseHeldPublic();
        return true;
    }
}