package com.example.neomocreatures.entity.bunny;

import net.minecraft.util.RandomSource;

/**
 * The 5 bunny colour variants, ported from the texture switch in
 * {@code drzhark.mocreatures.entity.passive.MoCEntityBunny#getTexture()}
 * (original numeric types 1-5). The original also has a "legacy texture"
 * config toggle picking between a plain and a "_detailed" version of each
 * colour, unrelated to age — per the user's own instruction, this port
 * repurposes that same texture pair as the baby/adult distinction instead
 * (plain = baby, "_detailed" = adult), skipping the config toggle entirely.
 */
public enum BunnyVariant {

    GOLDEN(0, "bunny_golden"),
    BEIGE(1, "bunny_beige"),
    WHITE(2, "bunny_white"),
    BLACK(3, "bunny_black"),
    SPOTTED(4, "bunny_spotted");

    private final int id;
    private final String textureName;

    BunnyVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    /** Base texture name, without the baby/adult suffix — see MoCBunnyRenderer. */
    public String getTextureName() {
        return textureName;
    }

    public static BunnyVariant byId(int id) {
        for (BunnyVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return GOLDEN;
    }

    public static BunnyVariant random(RandomSource random) {
        BunnyVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}