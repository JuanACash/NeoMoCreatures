package com.example.neomocreatures.entity.snail;

/** The original stores snails and slugs as one entity: variants 1-4 are snails, 5-6 are slugs. */
public enum SnailVariant {

    BROWN(1, "snail_brown", false),
    GREEN(2, "snail_green", false),
    YELLOW(3, "snail_yellow", false),
    RED(4, "snail_red", false),
    GOLDEN_SLUG(5, "slug_golden", true),
    BLACK_SLUG(6, "slug_black", true);

    private final int id;
    private final String textureName;
    private final boolean slug;

    SnailVariant(int id, String textureName, boolean slug) {
        this.id = id;
        this.textureName = textureName;
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    /** Slugs have no shell to hide in. */
    public boolean isSlug() {
        return slug;
    }

    public static SnailVariant byId(int id) {
        for (SnailVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BROWN;
    }
}
