package com.example.neomocreatures.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;

/**
 * Shared experience helpers for mod entities.
 */
public final class MoCExperienceUtil {

    private static final int STANDARD_XP_MIN = 1;
    private static final int STANDARD_XP_RANGE = 3;

    private MoCExperienceUtil() {
        // Utility class, no instances
    }

    /** Standard 1-3 experience reward shared by most mod creatures. */
    public static int rollStandardXp(RandomSource random) {
        return STANDARD_XP_MIN + random.nextInt(STANDARD_XP_RANGE);
    }

    /** Spawns a single experience orb at the entity's position. */
    public static void dropExperienceOrb(ServerLevel level, Entity source, int amount) {
        level.addFreshEntity(new ExperienceOrb(level, source.getX(), source.getY(), source.getZ(), amount));
    }
}
