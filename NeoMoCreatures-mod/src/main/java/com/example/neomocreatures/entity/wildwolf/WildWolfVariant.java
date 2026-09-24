package com.example.neomocreatures.entity.wildwolf;

import net.minecraft.util.RandomSource;

/** Original: selectType() rolls 1-5 uniformly, but checkSpawningBiome() forces TIMBER first in any
 *  snowy/frozen/ice/cold biome, before selectType() ever runs (which only assigns if still unset). */
public enum WildWolfVariant {

    BLACK(1, "wild_wolf_black"),
    CLASSIC(2, "wild_wolf_classic"),
    TIMBER(3, "wild_wolf_timber"),
    DARK(4, "wild_wolf_dark"),
    BRIGHT(5, "wild_wolf_bright");

    private final int id;
    private final String textureName;

    WildWolfVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static WildWolfVariant byId(int id) {
        for (WildWolfVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return CLASSIC;
    }

    public static WildWolfVariant random(RandomSource random) {
        WildWolfVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}