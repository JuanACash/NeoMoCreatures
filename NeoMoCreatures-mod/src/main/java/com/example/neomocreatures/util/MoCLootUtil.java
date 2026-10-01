package com.example.neomocreatures.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Shared loot helpers for mod entities.
 * Centralizes logic that was previously duplicated in every entity class.
 */
public final class MoCLootUtil {

    private MoCLootUtil() {
        // Utility class, no instances
    }

    /**
     * Returns the Looting level of whoever caused the damage,
     * or 0 if the source is not a living entity.
     */
    public static int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }
}