package com.example.neomocreatures.entity.bear;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

/**
 * The 4 bear species. Stats/render scale taken directly from
 * MoCEntityBlackBear/GrizzlyBear/PolarBear/PandaBear (registerAttributes()
 * and getBearSize()) in the original mod.
 */
public enum BearVariant {

    BLACK(0, "bear_black", 30.0D, 5.5D, 0.9D, Temperament.NEUTRAL, 0.65D),
    GRIZZLY(1, "bear_grizzly", 40.0D, 7.0D, 1.2D, Temperament.NEUTRAL, 0.75D),
    POLAR(2, "bear_polar", 45.0D, 7.5D, 1.4D, Temperament.HOSTILE, 0.85D),
    PANDA(3, "bear_panda", 20.0D, 4.0D, 0.8D, Temperament.PASSIVE, 0.5D);

    /**
     * NEUTRAL: leaves the player alone unless provoked, or unless a cub of
     * its own species nearby is under attack.
     * HOSTILE: attacks the player on sight (polar).
     * PASSIVE: never fights back or hunts, even if hit (panda).
     */
    public enum Temperament { NEUTRAL, HOSTILE, PASSIVE }

    private static final double SMALLEST_RENDER_SCALE = 0.8D; // Panda, the smallest of the 4

    private final int id;
    private final String textureName;
    private final double maxHealth;
    private final double attackDamage;
    private final double renderScale;
    private final Temperament temperament;

    private final double riderHeight;

    BearVariant(int id, String textureName, double maxHealth, double attackDamage,
                double renderScale, Temperament temperament, double riderHeight) {
        this.id = id;
        this.textureName = textureName;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.renderScale = renderScale;
        this.temperament = temperament;
        this.riderHeight = riderHeight;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public double getRiderHeight() {
        return riderHeight;
    }

    public Temperament getTemperament() {
        return temperament;
    }

    /** At least 20 minutes (24000 ticks) — bigger species take proportionally longer, same curve as BigCat. */
    public int getGrowthTicks() {
        return (int) Math.round(24000D * (renderScale / SMALLEST_RENDER_SCALE));
    }

    public static BearVariant byId(int id) {
        for (BearVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLACK;
    }


    /** Which variant a whole group will be, decided once per group by biome — never mixed within a group. */
    public static BearVariant forBiome(Holder<Biome> biome, RandomSource random) {
        // Checked in order; each tag holds vanilla biomes plus optional modded ones.
        if (biome.is(ModTags.BEAR_POLAR_BIOMES)) {
            return POLAR;
        }
        if (biome.is(ModTags.BEAR_PANDA_BIOMES)) {
            return PANDA;
        }
        if (biome.is(ModTags.BEAR_GRIZZLY_BIOMES)) {
            return GRIZZLY;
        }
        if (biome.is(ModTags.BEAR_BLACK_BIOMES)) {
            return BLACK;
        }
        if (biome.is(ModTags.BEAR_BLACK_OR_GRIZZLY_BIOMES)) {
            return random.nextBoolean() ? BLACK : GRIZZLY;
        }

        // Safety net for a biome not explicitly listed (e.g. a datapack biome reusing
        // one of these spawners) — decide by climate instead of defaulting silently.
        float temperature = biome.value().getBaseTemperature();
        if (temperature <= 0.15F) {
            return POLAR;
        }
        return random.nextBoolean() ? BLACK : GRIZZLY;
    }

}