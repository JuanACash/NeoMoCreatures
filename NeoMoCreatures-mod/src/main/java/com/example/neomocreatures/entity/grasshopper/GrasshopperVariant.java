package com.example.neomocreatures.entity.grasshopper;

/** Original: a coin flip between two greens (the roll of 0-50 is bright green, so 51% vs 49%). */
public enum GrasshopperVariant {

    BRIGHT_GREEN(1, "grasshopper_bright_green"),
    OLIVE_GREEN(2, "grasshopper_olive_green");

    private final int id;
    private final String textureName;

    GrasshopperVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static GrasshopperVariant byId(int id) {
        for (GrasshopperVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return OLIVE_GREEN;
    }
}
