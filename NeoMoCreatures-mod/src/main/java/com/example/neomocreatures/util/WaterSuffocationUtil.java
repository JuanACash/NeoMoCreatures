package com.example.neomocreatures.util;

import net.minecraft.world.entity.LivingEntity;

/**
 * Out-of-water suffocation shared by every aquatic creature. Each creature keeps its own
 * counter and tuning values; this class only holds the rule.
 */
public final class WaterSuffocationUtil {

    private WaterSuffocationUtil() {
        // Utility class, no instances
    }

    /**
     * Advances the out-of-water counter and drowns the creature once the grace period is over.
     * Call it on the server side once per tick.
     *
     * @param ticksOutOfWater the creature's current counter
     * @param graceTicks      ticks out of water before any damage
     * @param intervalTicks   ticks between damage hits after the grace period
     * @param damage          damage dealt on each hit
     * @return the new counter value, which the caller must store
     */
    public static int tick(LivingEntity creature, int ticksOutOfWater, int graceTicks,
                           int intervalTicks, float damage) {
        if (creature.isInWaterOrBubble()) {
            return 0;
        }
        int updated = ticksOutOfWater + 1;
        if (updated > graceTicks && updated % intervalTicks == 0) {
            creature.hurt(creature.damageSources().drown(), damage);
        }
        return updated;
    }
}