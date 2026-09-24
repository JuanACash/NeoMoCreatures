package com.example.neomocreatures.entity.werewolf;

import net.minecraft.util.RandomSource;

/** The 4 wolf-form colours. Only used while transformed — the human form always uses one fixed
 *  texture, with no variant of its own. */
public enum WerewolfVariant {

    BLACK(0, "werewolf_black"),
    BROWN(1, "werewolf_brown"),
    WHITE(2, "werewolf_white"),
    FIRE(3, "werewolf_fire");

    private final int id;
    private final String textureName;

    WerewolfVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public static WerewolfVariant byId(int id) {
        for (WerewolfVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BROWN;
    }

    /** Original: 28% black, 28% brown, 29% white, 15% fire (rounding the original's 28/56/85/100
     *  cutoffs out of 100 into exact percentages). */
    public static WerewolfVariant random(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 28) {
            return BLACK;
        } else if (roll < 56) {
            return BROWN;
        } else if (roll < 85) {
            return WHITE;
        }
        return FIRE;
    }
}