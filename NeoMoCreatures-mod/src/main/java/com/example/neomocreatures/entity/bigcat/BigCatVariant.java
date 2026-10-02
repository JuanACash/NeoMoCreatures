package com.example.neomocreatures.entity.bigcat;

import java.util.Set;

import net.minecraft.util.RandomSource;

/**
 * The 8 base big-cat species (no hybrids yet — those come in a later step).
 * Stats are taken directly from the original MoCEntityLion/Tiger/Leopard/Panther
 * (calculateMaxHealth/calculateAttackDmg) rather than guessed.
 */
public enum BigCatVariant {

    LION_FEMALE(0, "big_cat_lion_female", 30.0D, 7.0D, false, 1.0D, SpawnFamily.LION),
    LION_MALE(1, "big_cat_lion_male", 35.0D, 7.0D, true, 1.0D, SpawnFamily.LION),
    WHITE_LION(2, "big_cat_white_lion", 35.0D, 7.5D, true, 1.0D, SpawnFamily.LION),
    WHITE_LION_FEMALE(3, "big_cat_white_lion", 35.0D, 7.5D, false, 1.0D, SpawnFamily.LION),
    TIGER(4, "big_cat_tiger", 35.0D, 7.0D, false, 1.0D, SpawnFamily.TIGER),
    WHITE_TIGER(5, "big_cat_white_tiger", 40.0D, 7.5D, false, 1.0D, SpawnFamily.TIGER),
    LEOPARD(6, "big_cat_leopard", 25.0D, 6.0D, false, 0.93D, SpawnFamily.LEOPARD),
    SNOW_LEOPARD(7, "big_cat_snow_leopard", 25.0D, 6.0D, false, 0.93D, SpawnFamily.LEOPARD),
    PANTHER(8, "big_cat_panther", 25.0D, 6.0D, false, 0.94D, SpawnFamily.PANTHER),
    LIGER(9, "big_cat_liger", 35.0D, 7.0D, false, 1.3D, SpawnFamily.HYBRID),
    LIARD(10, "big_cat_liard", 30.0D, 6.5D, false, 0.94D, SpawnFamily.HYBRID),
    LEOGER(11, "big_cat_leoger", 40.0D, 6.5D, false, 1.04D, SpawnFamily.HYBRID),
    LITHER(12, "big_cat_lither", 25.0D, 6.5D, false, 0.94D, SpawnFamily.HYBRID),
    PANTHARD(13, "big_cat_panthard", 25.0D, 6.0D, false, 0.91D, SpawnFamily.HYBRID),
    PANTHGER(14, "big_cat_panthger", 30.0D, 6.5D, false, 0.98D, SpawnFamily.HYBRID);

    /** Which spawn egg (Lion/Tiger/Leopard/Panther — no separate white/snow egg) can produce this variant. */
    public enum SpawnFamily {LION, TIGER, LEOPARD, PANTHER, HYBRID}

    private final int id;
    private final String textureName;
    private final double maxHealth;
    private final double attackDamage;
    private final boolean hasMane;
    private final double renderScale;
    private final SpawnFamily spawnFamily;
    private static final double SMALLEST_RENDER_SCALE = 0.91D; // Panthard, the smallest of all 15


    BigCatVariant(int id, String textureName, double maxHealth, double attackDamage,
                  boolean hasMane, double renderScale, SpawnFamily spawnFamily) {
        this.id = id;
        this.textureName = textureName;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.hasMane = hasMane;
        this.renderScale = renderScale;
        this.spawnFamily = spawnFamily;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public boolean hasMane() {
        return hasMane;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public SpawnFamily getSpawnFamily() {
        return spawnFamily;
    }

    /** At least 20 minutes (24000 ticks) — bigger species like lions/tigers take proportionally longer. */
    public int getGrowthTicks() {
        return (int) Math.round(24000D * (renderScale / SMALLEST_RENDER_SCALE));
    }

    /** Essence of Darkness only works on these — the panther family. */
    public boolean canGetDarknessWings() {
        return this == PANTHER || this == PANTHARD || this == PANTHGER;
    }

    /** Essence of Light only works on these — every lion (both genders, white included), white tiger, and their hybrids. */
    public boolean canGetLightWings() {
        return this == LION_MALE || this == WHITE_LION || this == WHITE_LION_FEMALE
                || this == WHITE_TIGER || this == LITHER || this == LIGER;
    }

    public static BigCatVariant byId(int id) {
        for (BigCatVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return LION_FEMALE;
    }

    /** Lion egg: 75% a random-gender lion, 25% a random-gender white lion. */
    public static BigCatVariant randomLion(RandomSource random) {
        if (random.nextInt(4) == 0) {
            return random.nextBoolean() ? WHITE_LION : WHITE_LION_FEMALE;
        }
        return random.nextBoolean() ? LION_MALE : LION_FEMALE;
    }

    /** Tiger egg: 75% orange, 25% white. */
    public static BigCatVariant randomTiger(RandomSource random) {
        return random.nextInt(4) == 0 ? WHITE_TIGER : TIGER;
    }

    /** Wild spawn: 1/20 chance of white, not the spawn egg's 25%. */
    public static BigCatVariant randomWildLion(RandomSource random) {
        boolean white = random.nextInt(20) == 0;
        boolean male = random.nextBoolean();
        return white ? (male ? WHITE_LION : WHITE_LION_FEMALE) : (male ? LION_MALE : LION_FEMALE);
    }

    public static BigCatVariant randomWildTiger(RandomSource random) {
        return random.nextInt(20) == 0 ? WHITE_TIGER : TIGER;
    }

    /** Panther egg: only one look exists. */
    public static BigCatVariant randomPanther(RandomSource random) {
        return PANTHER;
    }

    public static BigCatVariant randomLiger(RandomSource random) {
        return LIGER;
    }

    public static BigCatVariant randomLiard(RandomSource random) {
        return LIARD;
    }

    public static BigCatVariant randomLeoger(RandomSource random) {
        return LEOGER;
    }

    public static BigCatVariant randomLither(RandomSource random) {
        return LITHER;
    }

    public static BigCatVariant randomPanthard(RandomSource random) {
        return PANTHARD;
    }

    public static BigCatVariant randomPanthger(RandomSource random) {
        return PANTHGER;
    }

    private enum Genus { LION, TIGER, LEOPARD, PANTHER, HYBRID }

    private static Genus genusOf(BigCatVariant v) {
        return switch (v) {
            case LION_MALE, LION_FEMALE, WHITE_LION, WHITE_LION_FEMALE -> Genus.LION;
            case TIGER, WHITE_TIGER -> Genus.TIGER;
            case LEOPARD, SNOW_LEOPARD -> Genus.LEOPARD;
            case PANTHER -> Genus.PANTHER;
            default -> Genus.HYBRID;
        };
    }

    public boolean isHybrid() {
        return genusOf(this) == Genus.HYBRID;
    }

    private static boolean isLionMaleType(BigCatVariant v) {
        return v == LION_MALE || v == WHITE_LION;
    }

    private static boolean isLionFemaleType(BigCatVariant v) {
        return v == LION_FEMALE || v == WHITE_LION_FEMALE;
    }

    private static boolean isWhiteLionType(BigCatVariant v) {
        return v == WHITE_LION || v == WHITE_LION_FEMALE;
    }

    /** Only the plain-colored base species hybridize — white/snow variants never cross with a different species. */
    private static BigCatVariant hybridFor(BigCatVariant a, BigCatVariant b) {
        Set<BigCatVariant> pair = Set.of(a, b);
        if (pair.equals(Set.of(LEOPARD, TIGER))) return LEOGER;
        if (pair.equals(Set.of(LION_MALE, LEOPARD))) return LIARD;
        if (pair.equals(Set.of(LION_MALE, TIGER))) return LIGER;
        if (pair.equals(Set.of(LION_MALE, PANTHER))) return LITHER;
        if (pair.equals(Set.of(LEOPARD, PANTHER))) return PANTHARD;
        if (pair.equals(Set.of(PANTHER, TIGER))) return PANTHGER;
        return null;
    }

    /** True if these two can produce a cub at all — doesn't roll the actual result. */
    public static boolean canBreedTogether(BigCatVariant a, BigCatVariant b) {
        Genus ga = genusOf(a);
        Genus gb = genusOf(b);
        if (ga == Genus.HYBRID || gb == Genus.HYBRID) {
            return false;
        }
        if (ga == gb) {
            if (ga == Genus.LION) {
                // Only one male-type + one female-type — two of the same sex never breed, regardless of color.
                return (isLionMaleType(a) && isLionFemaleType(b)) || (isLionFemaleType(a) && isLionMaleType(b));
            }
            return true; // tiger/leopard/panther: any combo within the genus works
        }
        return hybridFor(a, b) != null;
    }

    /** 0 white/snow parents -> 1%, 1 -> 25%, 2 -> 99% — same curve for lion white and leopard snow genes. */
    private static boolean rollRareGene(boolean aRare, boolean bRare, RandomSource random) {
        int rareParents = (aRare ? 1 : 0) + (bRare ? 1 : 0);
        float chance = switch (rareParents) {
            case 0 -> 0.01F;
            case 1 -> 0.25F;
            default -> 0.99F;
        };
        return random.nextFloat() < chance;
    }

    /** Only call once canBreedTogether() has already confirmed these two can breed. */
    public static BigCatVariant rollOffspring(BigCatVariant a, BigCatVariant b, RandomSource random) {
        Genus ga = genusOf(a);
        if (ga != genusOf(b)) {
            return hybridFor(a, b);
        }
        return switch (ga) {
            case LION -> {
                boolean white = rollRareGene(isWhiteLionType(a), isWhiteLionType(b), random);
                boolean male = random.nextBoolean();
                yield white ? (male ? WHITE_LION : WHITE_LION_FEMALE) : (male ? LION_MALE : LION_FEMALE);
            }
            case TIGER -> rollRareGene(a == WHITE_TIGER, b == WHITE_TIGER, random) ? WHITE_TIGER : TIGER;
            case LEOPARD -> {
                boolean aSnow = a == SNOW_LEOPARD;
                boolean bSnow = b == SNOW_LEOPARD;
                boolean snow = aSnow && bSnow ? true      // snow x snow -> always snow
                        : !aSnow && !bSnow ? false        // leopard x leopard -> always leopard
                        : random.nextBoolean();           // one of each -> 50/50
                yield snow ? SNOW_LEOPARD : LEOPARD;
            }
            default -> PANTHER;
        };
    }
}