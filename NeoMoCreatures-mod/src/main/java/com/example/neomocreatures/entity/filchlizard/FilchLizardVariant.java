package com.example.neomocreatures.entity.filchlizard;

/** Original: getTexture() — plain, sand (sandy biomes), red sand (badlands) and silver (Wyvern Lair). */
public enum FilchLizardVariant {

    NORMAL(1, "lizard_filch"),
    SAND(2, "lizard_filch_sand"),
    RED_SAND(3, "lizard_filch_sand_red"),
    SILVER(4, "lizard_filch_sand_silver");

    private final int id;
    private final String textureName;

    FilchLizardVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static FilchLizardVariant byId(int id) {
        for (FilchLizardVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return NORMAL;
    }
}