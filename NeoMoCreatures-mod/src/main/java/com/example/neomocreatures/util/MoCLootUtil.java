package com.example.neomocreatures.util;

import com.example.neomocreatures.entity.MoCMaggotEntity;
import com.example.neomocreatures.init.ModEntities;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

/**
 * Shared death-loot helpers for mod entities.
 * Every roll keeps the exact formula each entity used before, so drop rates are unchanged.
 */
public final class MoCLootUtil {

    private MoCLootUtil() {
        // Utility class, no instances
    }

    // ---------------------------------------------------------------------
    // Looting
    // ---------------------------------------------------------------------

    /**
     * Returns the Looting level of whoever caused the damage,
     * or 0 if the source is not a living entity.
     */
    public static int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        return getLootingLevel(damageSource.getEntity());
    }

    /**
     * Returns the Looting level held by the given entity,
     * or 0 if it is null or not a living entity.
     */
    public static int getLootingLevel(@Nullable Entity entity) {
        if (entity instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    attacker.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }

    // ---------------------------------------------------------------------
    // Count rolls
    // ---------------------------------------------------------------------

    /** {@code [0, baseBound)} plus a random bonus of {@code [0, looting]}. */
    public static int rollWithLootingBonus(RandomSource random, int baseBound, int lootingLevel) {
        return random.nextInt(baseBound) + random.nextInt(lootingLevel + 1);
    }

    /** {@code [0, baseBound)} plus one per Looting level, never above {@code cap}. */
    public static int rollWithFlatLooting(RandomSource random, int baseBound, int lootingLevel, int cap) {
        return Math.min(random.nextInt(baseBound) + lootingLevel, cap);
    }

    /** {@code [0, baseBound + looting)}: Looting widens the range instead of adding a bonus. */
    public static int rollWithLootingRange(RandomSource random, int baseBound, int lootingLevel) {
        return random.nextInt(baseBound + lootingLevel);
    }

    /** Old loot-table style bonus: 0 or 1 extra item per Looting level. */
    public static int rollLootingExtras(RandomSource random, int lootingLevel) {
        int extras = 0;
        for (int i = 0; i < lootingLevel; i++) {
            extras += random.nextInt(2);
        }
        return extras;
    }

    /** True with probability {@code baseChance + perLevel * looting}. */
    public static boolean rollChance(RandomSource random, float baseChance, float perLevel, int lootingLevel) {
        return random.nextFloat() < baseChance + perLevel * lootingLevel;
    }

    // ---------------------------------------------------------------------
    // Dropping
    // ---------------------------------------------------------------------

    /** Drops {@code count} of the item as one stack; does nothing when count is 0 or less. */
    public static void dropItems(Entity entity, ItemLike item, int count) {
        if (count > 0) {
            entity.spawnAtLocation(new ItemStack(item, count));
        }
    }

    /** Vanilla convention: meat comes out cooked when the animal dies burning. */
    public static ItemLike rawOrCooked(Entity entity, ItemLike raw, ItemLike cooked) {
        return entity.isOnFire() ? cooked : raw;
    }

    // ---------------------------------------------------------------------
    // Killer checks
    // ---------------------------------------------------------------------

    /** True if the entity is a wolf that has been tamed. */
    public static boolean isTamedWolf(@Nullable Entity entity) {
        return entity instanceof Wolf wolf && wolf.isTame();
    }

    /** Vanilla rule for combat loot: a player hit it recently, or a tamed wolf killed it. */
    public static boolean isKilledByPlayerOrTamedWolf(boolean recentlyHitByPlayer, @Nullable Entity killer) {
        return recentlyHitByPlayer || isTamedWolf(killer);
    }

    // ---------------------------------------------------------------------
    // Special death spawns
    // ---------------------------------------------------------------------

    /** Spawns 1-3 maggots where the entity died (undead horse and undead wyvern). */
    public static void spawnMaggots(ServerLevel level, Entity source, RandomSource random) {
        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            MoCMaggotEntity maggot = ModEntities.MOC_MAGGOT.get().create(level);
            if (maggot != null) {
                maggot.moveTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), 0.0F);
                level.addFreshEntity(maggot);
            }
        }
    }
}
