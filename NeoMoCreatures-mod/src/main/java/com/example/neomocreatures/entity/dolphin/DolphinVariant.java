package com.example.neomocreatures.entity.dolphin;

import net.minecraft.util.RandomSource;

/**
 * The 6 dolphin colour variants, ported from the switches in
 * {@code drzhark.mocreatures.entity.aquatic.MoCEntityDolphin} (original
 * numeric types 1-6): {@code selectType()}, {@code getTexture()},
 * {@code getMaxTemper()} and {@code getCustomSpeed()}.
 * <p>
 * Rarer colours are harder to tame ({@code maxTemper}) and faster to ride
 * ({@code mountSpeed}); both are used by the taming and riding step.
 */
public enum DolphinVariant {

    BLUE(0, "dolphin_blue", 36, 50, 1.5D),
    GREEN(1, "dolphin_green", 25, 100, 2.5D),
    PURPLE(2, "dolphin_purple", 25, 150, 3.5D),
    BLACK(3, "dolphin_black", 11, 200, 4.5D),
    PINK(4, "dolphin_pink", 2, 250, 5.5D),
    WHITE(5, "dolphin_white", 1, 300, 6.5D);

    private final int id;
    private final String textureName;
    private final int spawnWeight;
    private final int maxTemper;
    private final double mountSpeed;

    DolphinVariant(int id, String textureName, int spawnWeight, int maxTemper, double mountSpeed) {
        this.id = id;
        this.textureName = textureName;
        this.spawnWeight = spawnWeight;
        this.maxTemper = maxTemper;
        this.mountSpeed = mountSpeed;
    }

    public int getId() {
        return id;
    }

    /** Original numeric type (1-6): the value the breeding genetics adds up. */
    public int getGeneticValue() {
        return id + 1;
    }

    /** Inverse of {@link #getGeneticValue()}; falls back to blue for a value outside 1-6. */
    public static DolphinVariant byGeneticValue(int geneticValue) {
        return byId(geneticValue - 1);
    }

    public String getTextureName() {
        return textureName;
    }

    /** Original's {@code getMaxTemper()}: the higher it is, the longer a wild dolphin takes to tame. */
    public int getMaxTemper() {
        return maxTemper;
    }

    /** Original's {@code getCustomSpeed()}: forward speed factor while ridden. */
    public double getMountSpeed() {
        return mountSpeed;
    }

    public static DolphinVariant byId(int id) {
        for (DolphinVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLUE;
    }

    public static DolphinVariant byName(String name) {
        for (DolphinVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return BLUE;
    }

    /** Weighted pick using the original's rarity table (36/25/25/11/2/1 out of 100). */
    public static DolphinVariant random(RandomSource random) {
        int roll = random.nextInt(totalSpawnWeight());
        for (DolphinVariant variant : values()) {
            roll -= variant.spawnWeight;
            if (roll < 0) {
                return variant;
            }
        }
        return BLUE;
    }

    private static int totalSpawnWeight() {
        int total = 0;
        for (DolphinVariant variant : values()) {
            total += variant.spawnWeight;
        }
        return total;
    }
}