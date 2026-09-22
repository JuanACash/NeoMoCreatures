package com.example.neomocreatures.entity.smallfish;

import net.minecraft.util.RandomSource;

/**
 * The 8 small fish species, ported from {@code MoCEntitySmallFish.fishNames} plus the piranha,
 * which the original splits into its own aggressive subclass. Here they are all one entity with a
 * variant, so the aggressive behaviour is switched on per-individual by {@link #isAggressive()}.
 */
public enum SmallFishVariant {

    ANCHOVY(0, "smallfish_anchovy", false),
    ANGELFISH(1, "smallfish_angelfish", false),
    ANGLER(2, "smallfish_anglerfish", false),
    CLOWNFISH(3, "smallfish_clownfish", false),
    GOLDFISH(4, "smallfish_goldfish", false),
    HIPPOTANG(5, "smallfish_hippotang", false),
    MANDARIN(6, "smallfish_mandarinfish", false),
    PIRANHA(7, "smallfish_piranha", true);

    private final int id;
    private final String textureName;
    private final boolean aggressive;

    SmallFishVariant(int id, String textureName, boolean aggressive) {
        this.id = id;
        this.textureName = textureName;
        this.aggressive = aggressive;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    /** Wiki: every small fish is passive except the piranha, which attacks on sight. */
    public boolean isAggressive() {
        return aggressive;
    }

    public static SmallFishVariant byId(int id) {
        for (SmallFishVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return ANCHOVY;
    }

    public static SmallFishVariant byName(String name) {
        for (SmallFishVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return ANCHOVY;
    }

    /** Original: selectType() rolls uniformly among the 7 passive kinds — the piranha is its own spawn. */
    public static SmallFishVariant randomPassive(RandomSource random) {
        SmallFishVariant[] passive = { ANCHOVY, ANGELFISH, ANGLER, CLOWNFISH, GOLDFISH, HIPPOTANG, MANDARIN };
        return passive[random.nextInt(passive.length)];
    }
}