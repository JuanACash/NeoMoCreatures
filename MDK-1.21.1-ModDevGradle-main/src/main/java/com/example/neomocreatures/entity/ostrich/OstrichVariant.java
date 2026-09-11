package com.example.neomocreatures.entity.ostrich;

public enum OstrichVariant {
    MALE(0, "ostrich_male"),
    FEMALE(1, "ostrich_female"),
    DARK(2, "ostrich_dark"),
    WHITE(3, "ostrich_white");

    private final int id;
    private final String textureName;

    OstrichVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static OstrichVariant rollNatural(net.minecraft.util.RandomSource random) {
        int roll = random.nextInt(100);
        if (roll <= 56) {
            return FEMALE;
        } else if (roll <= 94) {
            return MALE;
        } else {
            return WHITE; // albino
        }
    }

    public static OstrichVariant byId(int id) {
        for (OstrichVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return DARK;
    }
}