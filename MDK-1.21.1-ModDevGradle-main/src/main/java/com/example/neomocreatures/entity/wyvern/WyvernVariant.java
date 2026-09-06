package com.example.neomocreatures.entity.wyvern;

import net.minecraft.util.RandomSource;

public enum WyvernVariant {

    JUNGLE("wyvern_jungle", false),
    SWAMP("wyvern_swamp", false),
    SAND("wyvern_sand", false),
    SUN("wyvern_sun", false),
    ARCTIC("wyvern_arctic", false),
    CAVE("wyvern_cave", false),
    MOUNTAIN("wyvern_mountain", false),
    SEA("wyvern_sea", false),
    MOTHER("wyvern_mother", true),
    MOTHER_UNDEAD("wyvern_mother_undead", true),
    MOTHER_LIGHT("wyvern_mother_light", true),
    MOTHER_DARK("wyvern_mother_dark", true),
    MOTHER_CORRUPT("wyvern_mother_corrupt", true);

    private static final WyvernVariant[] VALUES = values();
    private static final WyvernVariant[] WILD_VARIANTS =
            {JUNGLE, SWAMP, SAND, SUN, ARCTIC, CAVE, MOUNTAIN, SEA};
    private static final WyvernVariant[] MOTHER_VARIANTS =
            {MOTHER, MOTHER_UNDEAD, MOTHER_LIGHT, MOTHER_DARK, MOTHER_CORRUPT};

    private final String textureName;
    private final boolean mother;

    WyvernVariant(String textureName, boolean mother) {
        this.textureName = textureName;
        this.mother = mother;
    }

    public String getTextureName() {
        return textureName;
    }

    /**
     * Texture for the ghost form: identical to the living texture for every
     * variant except the plain (essence-less) mother, which shows the corrupt
     * texture instead.
     */
    public String getGhostTextureName() {
        return this == MOTHER ? "wyvern_mother_corrupt" : textureName;
    }

    public boolean isMother() {
        return mother;
    }

    public int getId() {
        return ordinal();
    }

    public static WyvernVariant byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : SUN;
    }

    public static WyvernVariant randomWild(RandomSource random) {
        return WILD_VARIANTS[random.nextInt(WILD_VARIANTS.length)];
    }

    public static WyvernVariant randomMother(RandomSource random) {
        return MOTHER_VARIANTS[random.nextInt(MOTHER_VARIANTS.length)];
    }
}