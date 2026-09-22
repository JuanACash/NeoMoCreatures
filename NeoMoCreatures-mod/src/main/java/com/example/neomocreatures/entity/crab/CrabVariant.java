package com.example.neomocreatures.entity.crab;

import net.minecraft.util.RandomSource;

/** The 5 crab colours from {@code MoCEntityCrab.getTexture()}, sorted uniformly on spawn. */
public enum CrabVariant {

    RED(0, "crab_red"),
    BLUE(1, "crab_blue"),
    SPOTTED(2, "crab_spotted"),
    GREEN(3, "crab_green"),
    RUSSET(4, "crab_russet");

    private final int id;
    private final String textureName;

    CrabVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static CrabVariant byId(int id) {
        for (CrabVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return RED;
    }

    public static CrabVariant byName(String name) {
        for (CrabVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return RED;
    }

    public static CrabVariant random(RandomSource random) {
        CrabVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}