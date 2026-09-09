package com.example.neomocreatures.event;

import com.example.neomocreatures.entity.MoCScorpionEntity;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * A baby scorpion held in hand (see MoCScorpionEntity#startHolding) is
 * released by right-clicking ANYWHERE — empty air, a block, or an item use —
 * not just by clicking the scorpion itself. Matches the original's pick-up
 * mechanic.
 */
public class ScorpionHoldReleaseHandler {

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
        var held = player.level().getEntitiesOfClass(MoCScorpionEntity.class,
                player.getBoundingBox().inflate(3.0D),
                scorpion -> scorpion.getHolder() == player);
        if (held.isEmpty()) {
            return false;
        }
        held.get(0).releaseHeldPublic();
        return true;
    }
}