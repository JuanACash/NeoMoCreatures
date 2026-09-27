package com.example.neomocreatures.entity.mouse;

/** Original: selectType() rolls 1-3 uniformly — but checkSpawningBiome() forces BROWN in mesa and
 *  WHITE in snowy biomes before that roll ever happens. */
public enum MouseVariant {

    BEIGE(1, "mouse_beige"),
    BROWN(2, "mouse_brown"),
    WHITE(3, "mouse_white");

    private final int id;
    private final String textureName;

    MouseVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static MouseVariant byId(int id) {
        for (MouseVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BEIGE;
    }
}