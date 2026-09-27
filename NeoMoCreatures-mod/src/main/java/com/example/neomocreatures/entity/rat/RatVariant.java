package com.example.neomocreatures.entity.rat;

/** Original: selectType() rolls 65% brown, 33% black, 2% white — but checkSpawningBiome() forces
 *  BROWN in desert/mesa and WHITE in snow/frozen biomes before that roll ever happens. */
public enum RatVariant {

    BROWN(1, "rat_brown"),
    BLACK(2, "rat_black"),
    WHITE(3, "rat_white"),
    // Not in the original mod's own texture switch (the file exists in its assets but the code
    // never references it) — added here on request as a 4th real variant.
    RED(4, "rat_red");

    private final int id;
    private final String textureName;

    RatVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static RatVariant byId(int id) {
        for (RatVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BROWN;
    }
}