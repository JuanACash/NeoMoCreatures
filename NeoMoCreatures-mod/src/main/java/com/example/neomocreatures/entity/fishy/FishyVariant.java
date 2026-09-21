package com.example.neomocreatures.entity.fishy;

import net.minecraft.util.RandomSource;

/**
 * The 10 fishy colours, in the order of the original's {@code fishNames} (types 1-10). The original
 * picks one uniformly at random for every fishy.
 */
public enum FishyVariant {

    BLUE(0, "fishy_blue"),
    ORANGE(1, "fishy_orange"),
    LIGHT_BLUE(2, "fishy_light_blue"),
    LIME(3, "fishy_lime"),
    GREEN(4, "fishy_green"),
    PURPLE(5, "fishy_purple"),
    YELLOW(6, "fishy_yellow"),
    CYAN(7, "fishy_cyan"),
    STRIPED(8, "fishy_striped"),
    RED(9, "fishy_red");

    private final int id;
    private final String textureName;

    FishyVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static FishyVariant byId(int id) {
        for (FishyVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLUE;
    }

    public static FishyVariant byName(String name) {
        for (FishyVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return BLUE;
    }

    public static FishyVariant random(RandomSource random) {
        FishyVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}