package com.example.neomocreatures.entity.jellyfish;

import net.minecraft.util.RandomSource;

/**
 * The 12 jellyfish colours from {@code MoCEntityJellyFish.getTexture()}. The original only ever
 * rolled the first 5 for a wild spawn or its plain spawn egg; here every colour is reachable both
 * ways.
 */
public enum JellyfishVariant {

    ORANGE_DARK(0, "jellyfish_orange_dark"),
    PURPLE_GRAY(1, "jellyfish_purple_gray"),
    BLUE_DARK(2, "jellyfish_blue_dark"),
    GREEN(3, "jellyfish_green"),
    ORANGE_RED(4, "jellyfish_orange_red"),
    ORANGE_YELLOW(5, "jellyfish_orange_yellow"),
    BLUE_SPECKLED(6, "jellyfish_blue_speckled"),
    WHITE(7, "jellyfish_white"),
    PURPLE(8, "jellyfish_purple"),
    ORANGE_LIGHT(9, "jellyfish_orange_light"),
    RED(10, "jellyfish_red"),
    BLUE_LIGHT(11, "jellyfish_blue_light");

    private final int id;
    private final String textureName;

    JellyfishVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static JellyfishVariant byId(int id) {
        for (JellyfishVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return ORANGE_DARK;
    }

    public static JellyfishVariant byName(String name) {
        for (JellyfishVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return ORANGE_DARK;
    }

    /** Unlike the original (which only rolled 5 of the 12), every colour can come from a wild spawn
     *  or the plain spawn egg here. */
    public static JellyfishVariant randomWild(RandomSource random) {
        JellyfishVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}