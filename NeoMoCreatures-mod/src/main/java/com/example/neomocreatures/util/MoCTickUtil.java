package com.example.neomocreatures.util;

import net.minecraft.world.entity.Entity;

/**
 * Helpers to spread expensive per-tick work (area scans, re-pathing) over several ticks.
 */
public final class MoCTickUtil {

    /** Searching for dropped food: a half-second delay is never noticeable. */
    public static final int FOOD_SCAN_INTERVAL = 10;

    /** Searching for nearby threats or targets: reacts within a quarter of a second. */
    public static final int THREAT_SCAN_INTERVAL = 5;

    private MoCTickUtil() {
        // Utility class, no instances
    }

    /**
     * True once every {@code interval} ticks for this entity. The entity id offsets the
     * phase, so a group of the same creature does not scan on the very same tick.
     */
    public static boolean isScanTick(Entity entity, int interval) {
        return (entity.tickCount + entity.getId()) % interval == 0;
    }
}