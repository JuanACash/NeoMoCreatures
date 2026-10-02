package com.example.neomocreatures.entity.bigcat;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

/**
 * Family of a naturally spawned big cat. The whole spawn group shares one family, chosen once
 * from the biome; each member then rolls its own variant within that family.
 */
public enum BigCatWildFamily {
    SNOW_LEOPARD, LEOPARD, PANTHER, TIGER, LION;

    /** Family for a spawn group in this biome — decided by biome tags, then climate, never pure random. */
    public static BigCatWildFamily forBiome(Holder<Biome> biome, RandomSource random) {
        if (biome.is(ModTags.BIGCAT_SNOW_LEOPARD_BIOMES)) {
            return SNOW_LEOPARD;
        }
        if (biome.is(ModTags.BIGCAT_JUNGLE_BIOMES)) {
            int roll = random.nextInt(38); // 14 + 10 + 14
            if (roll < 14) return LEOPARD;
            if (roll < 24) return PANTHER;
            return TIGER;
        }
        if (biome.is(ModTags.BIGCAT_FOREST_BIOMES)) {
            return random.nextInt(24) < 14 ? LEOPARD : PANTHER; // 14 vs 10
        }
        if (biome.is(ModTags.BIGCAT_LION_BIOMES)) {
            return LION;
        }

        // Safety net for any biome not explicitly listed — decide by climate, never pure random.
        float temperature = biome.value().getBaseTemperature();
        if (temperature <= 0.15F) {
            return SNOW_LEOPARD;
        }
        if (temperature >= 1.5F) {
            return LION;
        }
        return random.nextBoolean() ? LEOPARD : PANTHER;
    }

    /** Variant of one member of a group of this family. */
    public BigCatVariant rollVariant(RandomSource random) {
        return switch (this) {
            case SNOW_LEOPARD -> BigCatVariant.SNOW_LEOPARD;
            case LEOPARD -> BigCatVariant.LEOPARD;
            case PANTHER -> BigCatVariant.PANTHER;
            case TIGER -> BigCatVariant.randomWildTiger(random);
            case LION -> BigCatVariant.randomWildLion(random);
        };
    }
}