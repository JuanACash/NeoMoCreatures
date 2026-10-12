package com.example.neomocreatures.entity.ai;

import javax.annotation.Nullable;

import com.example.neomocreatures.Config;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

/** Shared config-driven rules for what predators are allowed to hunt. */
public final class HuntingRules {

    private HuntingRules() {
    }

    public static boolean isHuntingEnabled() {
        return Config.CREATURES.enableHunters.get();
    }

    /** Whether a predator may pick this entity as prey. */
    public static boolean canHunt(@Nullable LivingEntity target) {
        if (!isHuntingEnabled()) {
            return false;
        }
        if (target instanceof AbstractHorse && !Config.CREATURES.attackHorses.get()) {
            return false;
        }
        return !(target instanceof Wolf) || Config.CREATURES.attackWolves.get();
    }
}