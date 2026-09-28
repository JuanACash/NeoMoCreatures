package com.example.neomocreatures.entity.cricket;

/** Original: a coin flip between two browns (the roll of 0-50 is light brown, so 51% vs 49%). */
public enum CricketVariant {

    LIGHT_BROWN(1, "cricket_light_brown"),
    BROWN(2, "cricket_brown");

    private final int id;
    private final String textureName;

    CricketVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static CricketVariant byId(int id) {
        for (CricketVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BROWN;
    }
}
