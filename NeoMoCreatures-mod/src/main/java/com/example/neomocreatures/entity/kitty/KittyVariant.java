package com.example.neomocreatures.entity.kitty;

import net.minecraft.util.RandomSource;

/** The 11 coat colors a wild/spawn-egg kitty can roll, plus the default "cream". */
public enum KittyVariant {

    CREAM(0, "kitty_cream"),
    GRAY(1, "kitty_gray"),
    BLACK(2, "kitty_black"),
    CALICO(3, "kitty_calico"),
    TUXEDO(4, "kitty_tuxedo"),
    WHITE_BLACK(5, "kitty_white_black"),
    WHITE(6, "kitty_white"),
    ORANGE_TABBY(7, "kitty_orange_tabby"),
    CREAM_DARK(8, "kitty_cream_dark"),
    GRAY_TABBY(9, "kitty_gray_tabby"),
    YELLOW_TABBY(10, "kitty_yellow_tabby"),
    CALICO_ALT(11, "kitty_calico_alt");

    private final int id;
    private final String textureName;

    KittyVariant(int id, String textureName) {
        this.id = id;
        this.textureName = textureName;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    /** Matches the original's selectType(): CREAM is the "default" value, never rolled directly —
     *  a fresh spawn rolls uniformly among the other 10 named colors instead. */
    public static KittyVariant rollNatural(RandomSource random) {
        KittyVariant[] rollable = {GRAY, BLACK, CALICO, TUXEDO, WHITE_BLACK, WHITE,
                ORANGE_TABBY, CREAM_DARK, GRAY_TABBY, YELLOW_TABBY, CALICO_ALT};
        return rollable[random.nextInt(rollable.length)];
    }

    public static KittyVariant byId(int id) {
        for (KittyVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return CREAM;
    }
}