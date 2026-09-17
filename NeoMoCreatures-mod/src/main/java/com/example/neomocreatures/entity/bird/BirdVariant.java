package com.example.neomocreatures.entity.bird;

import net.minecraft.util.RandomSource;

/**
 * The 6 bird colour variants, ported from the texture switch in
 * {@code drzhark.mocreatures.entity.passive.MoCEntityBird#getTexture()}
 * (original numeric types 1-6; type 4 falls through to the default case in
 * the original, which is blue — same result as giving it its own explicit
 * entry here).
 */
public enum BirdVariant {

    WHITE(0, "bird_white"),
    BLACK(1, "bird_black"),
    GREEN(2, "bird_green"),
    BLUE(3, "bird_blue"),
    YELLOW(4, "bird_yellow"),
    RED(5, "bird_red");

    private final int id;
    private final String textureName;

    BirdVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static BirdVariant byId(int id) {
        for (BirdVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLUE;
    }

    public static BirdVariant random(RandomSource random) {
        BirdVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}