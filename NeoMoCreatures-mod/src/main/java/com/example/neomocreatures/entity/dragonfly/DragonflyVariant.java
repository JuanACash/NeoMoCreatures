package com.example.neomocreatures.entity.dragonfly;

/** Original: selectType() rolls 1-4 uniformly; the switch has no case 4, so blue is the default. */
public enum DragonflyVariant {

    GREEN(1, "dragonfly_green"),
    CYAN(2, "dragonfly_cyan"),
    RED(3, "dragonfly_red"),
    BLUE(4, "dragonfly_blue");

    private final int id;
    private final String textureName;

    DragonflyVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static DragonflyVariant byId(int id) {
        for (DragonflyVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLUE;
    }
}
