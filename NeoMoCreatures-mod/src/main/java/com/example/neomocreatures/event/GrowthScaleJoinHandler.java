package com.example.neomocreatures.event;

import com.example.neomocreatures.entity.GrowthScaled;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * Applies a growing mob's size the moment it joins the world — before it is sent to any player —
 * instead of waiting for its first tick. Otherwise a mob fresh out of a spawn egg or a pet amulet is
 * sent at scale 1 and shows up at full size for a moment before shrinking to its real size.
 */
public class GrowthScaleJoinHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof GrowthScaled growing) {
            growing.updateGrowthScale();
        }
    }
}