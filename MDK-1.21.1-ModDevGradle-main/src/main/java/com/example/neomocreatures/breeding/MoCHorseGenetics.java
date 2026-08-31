package com.example.neomocreatures.breeding;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Coat + species genetics for MoC horses, built directly from the
 * community-supplied breeding chart (pre-1.6 "Mo' Creatures" design):
 * self-contained horse genetics with no vanilla Horse involvement at all,
 * a 4-tier coat system, and three sterile hybrids (Zorse, Mule, Zonky).
 */
public final class MoCHorseGenetics {

    private MoCHorseGenetics() {}

    private static final Random RANDOM = new Random();

    // ---------------------------------------------------------------
    // Coats (only meaningful when species == HORSE)
    // ---------------------------------------------------------------
    public enum Coat {
        // Tier 1 — found in the wild
        WHITE, CREAMY, BROWN, DARKBROWN, BLACK,
        // Tier 2
        BRIGHTCREAMY, SPECKLED, PALEBROWN, GREY,
        // Tier 3
        PINTO, BRIGHTPINTO, PALESPECKLES,
        // Tier 4
        SPOTTED, COW
    }

    public enum FairyColor {
        WHITE, ORANGE, YELLOW, LIGHTGREEN, GREEN, CYAN, BLUE, DARKBLUE, PURPLE, PINK, RED, BLACK
    }

    /** Tier-1 coats: the ones that spawn naturally in the wild. */
    public static final Coat[] WILD_COATS = {
            Coat.WHITE, Coat.CREAMY, Coat.BROWN, Coat.DARKBROWN, Coat.BLACK
    };

    public static Coat randomWildCoat() {
        return WILD_COATS[RANDOM.nextInt(WILD_COATS.length)];
    }

    // ---------------------------------------------------------------
    // Species — separate axis from Coat. Zebra/Donkey are their own
    // species (single fixed look, no coat variants). Mule/Zonky/Zorse
    // are sterile end-products of cross-species breeding.
    public enum Species {
        HORSE, ZEBRA, DONKEY, MULE, ZONKY, ZORSE, BATHORSE, NIGHTMARE, UNICORN, PEGASUS, DARK_PEGASUS, FAIRY_HORSE,
        GHOST, GHOST_WINGED, HORSE_BUG;

        public boolean isSterile() {
            return this == MULE || this == ZONKY || this == ZORSE || this == GHOST || this == GHOST_WINGED
                    || this == HORSE_BUG;
        }
    }

    /** Returns the display name for a given species. */
    public static String displayName(Species species) {
        return switch (species) {
            case HORSE -> "Horse";
            case ZEBRA -> "Zebra";
            case DONKEY -> "Donkey";
            case MULE -> "Mule";
            case ZONKY -> "Zonkey";
            case ZORSE -> "Zorse";
            case BATHORSE -> "Bat Horse";
            case NIGHTMARE -> "Nightmare";
            case UNICORN -> "Unicorn";
            case PEGASUS -> "Pegasus";
            case DARK_PEGASUS -> "Dark Pegasus";
            case FAIRY_HORSE -> "Fairy Horse";
            case GHOST -> "Ghost Horse";
            case GHOST_WINGED -> "Winged Ghost Horse";
            case HORSE_BUG -> "???";
        };
    }
    
    /** Species that can be found/tamed in the wild (zebra has a special taming rule — see notes). */
    public static final Species[] WILD_SPECIES = {
            Species.HORSE, Species.HORSE, Species.HORSE, Species.HORSE, Species.HORSE, // horse coats weighted 5x
            Species.ZEBRA, Species.DONKEY
    };

    // ---------------------------------------------------------------
    // Coat genetics table — transcribed directly from the community
    // breeding chart. Same-coat pairs always breed true (handled in
    // code, not listed here). Unlisted differing pairs fall back to a
    // 50/50 pick between the two parent coats (also per the chart's
    // stated rule).
    // ---------------------------------------------------------------
    private static final Map<Long, Coat> TABLE = new HashMap<>();

    private static long key(Coat a, Coat b) {
        int x = Math.min(a.ordinal(), b.ordinal());
        int y = Math.max(a.ordinal(), b.ordinal());
        return ((long) x << 32) | y;
    }

    private static void put(Coat a, Coat b, Coat result) {
        TABLE.put(key(a, b), result);
    }

    static {
        put(Coat.WHITE, Coat.CREAMY, Coat.BRIGHTCREAMY);
        put(Coat.WHITE, Coat.BROWN, Coat.CREAMY);
        put(Coat.WHITE, Coat.DARKBROWN, Coat.SPECKLED);
        put(Coat.WHITE, Coat.BLACK, Coat.GREY);
        put(Coat.WHITE, Coat.SPECKLED, Coat.BRIGHTPINTO);
        put(Coat.WHITE, Coat.PALEBROWN, Coat.SPECKLED);
        put(Coat.WHITE, Coat.GREY, Coat.PALESPECKLES);
        put(Coat.WHITE, Coat.PINTO, Coat.BRIGHTPINTO);
        put(Coat.WHITE, Coat.BRIGHTPINTO, Coat.PALESPECKLES);
        put(Coat.WHITE, Coat.COW, Coat.SPOTTED);

        put(Coat.CREAMY, Coat.DARKBROWN, Coat.BROWN);
        put(Coat.CREAMY, Coat.BLACK, Coat.DARKBROWN);
        put(Coat.CREAMY, Coat.SPECKLED, Coat.PALEBROWN);
        put(Coat.CREAMY, Coat.PALEBROWN, Coat.BROWN);
        put(Coat.CREAMY, Coat.BRIGHTPINTO, Coat.BRIGHTCREAMY);
        put(Coat.CREAMY, Coat.SPOTTED, Coat.PALESPECKLES);
        put(Coat.CREAMY, Coat.COW, Coat.BRIGHTPINTO);

        put(Coat.BROWN, Coat.DARKBROWN, Coat.PALEBROWN);
        put(Coat.BROWN, Coat.BLACK, Coat.PALEBROWN);
        put(Coat.BROWN, Coat.BRIGHTCREAMY, Coat.CREAMY);
        put(Coat.BROWN, Coat.SPECKLED, Coat.PINTO);
        put(Coat.BROWN, Coat.GREY, Coat.PALEBROWN);
        put(Coat.BROWN, Coat.BRIGHTPINTO, Coat.PINTO);
        put(Coat.BROWN, Coat.SPOTTED, Coat.PINTO);
        put(Coat.BROWN, Coat.COW, Coat.PINTO);

        put(Coat.DARKBROWN, Coat.BRIGHTCREAMY, Coat.BROWN);
        put(Coat.DARKBROWN, Coat.SPECKLED, Coat.PALEBROWN);
        put(Coat.DARKBROWN, Coat.GREY, Coat.SPECKLED);
        put(Coat.DARKBROWN, Coat.PINTO, Coat.SPECKLED);
        put(Coat.DARKBROWN, Coat.BRIGHTPINTO, Coat.SPECKLED);
        put(Coat.DARKBROWN, Coat.PALESPECKLES, Coat.SPECKLED);
        put(Coat.DARKBROWN, Coat.SPOTTED, Coat.PALESPECKLES);
        put(Coat.DARKBROWN, Coat.COW, Coat.BLACK);

        put(Coat.BLACK, Coat.BRIGHTCREAMY, Coat.DARKBROWN);
        put(Coat.BLACK, Coat.SPECKLED, Coat.DARKBROWN);
        put(Coat.BLACK, Coat.PALEBROWN, Coat.DARKBROWN);
        put(Coat.BLACK, Coat.PINTO, Coat.COW);
        put(Coat.BLACK, Coat.BRIGHTPINTO, Coat.PALESPECKLES);
        put(Coat.BLACK, Coat.PALESPECKLES, Coat.SPOTTED);
        put(Coat.BLACK, Coat.SPOTTED, Coat.COW);

        put(Coat.BRIGHTCREAMY, Coat.PALEBROWN, Coat.CREAMY);
        put(Coat.BRIGHTCREAMY, Coat.COW, Coat.SPECKLED);

        put(Coat.SPECKLED, Coat.SPOTTED, Coat.PALESPECKLES);

        put(Coat.PALEBROWN, Coat.PINTO, Coat.SPECKLED);
        put(Coat.PALEBROWN, Coat.BRIGHTPINTO, Coat.SPECKLED);
        put(Coat.PALEBROWN, Coat.PALESPECKLES, Coat.SPECKLED);
        put(Coat.PALEBROWN, Coat.SPOTTED, Coat.SPECKLED);
        put(Coat.PALEBROWN, Coat.COW, Coat.SPECKLED);

        put(Coat.GREY, Coat.SPOTTED, Coat.PALESPECKLES);

        put(Coat.PINTO, Coat.SPOTTED, Coat.PALESPECKLES);
        put(Coat.PINTO, Coat.COW, Coat.SPECKLED);

        put(Coat.BRIGHTPINTO, Coat.SPOTTED, Coat.PALESPECKLES);

        put(Coat.PALESPECKLES, Coat.COW, Coat.GREY);
    }

    /** Resolves a foal's coat from two horse parents' coats (species == HORSE on both sides). */
    public static Coat resolveOffspringCoat(Coat a, Coat b) {
        if (a == b) {
            return a;
        }
        Coat result = TABLE.get(key(a, b));
        if (result != null) {
            return result;
        }
        // Chart's stated fallback: unlisted combos produce either parent, 50/50.
        return RANDOM.nextBoolean() ? a : b;
    }

    /**
     * Resolves what species (and, if HORSE, what coat) two parents produce.
     * Callers must have already checked neither parent is sterile
     * (MULE/ZONKY/ZORSE) before calling this.
     */
    public static Species resolveOffspringSpecies(Species a, Species b) {
        if (a == Species.HORSE && b == Species.HORSE) return Species.HORSE;
        if (a == Species.ZEBRA && b == Species.ZEBRA) return Species.ZEBRA;
        if (a == Species.DONKEY && b == Species.DONKEY) return Species.DONKEY;
        if ((a == Species.HORSE && b == Species.ZEBRA) || (a == Species.ZEBRA && b == Species.HORSE)) return Species.ZORSE;
        if ((a == Species.HORSE && b == Species.DONKEY) || (a == Species.DONKEY && b == Species.HORSE)) return Species.MULE;
        if ((a == Species.ZEBRA && b == Species.DONKEY) || (a == Species.DONKEY && b == Species.ZEBRA)) return Species.ZONKY;
        if (a == Species.BATHORSE && b == Species.BATHORSE) return Species.BATHORSE;
        if (a == Species.NIGHTMARE && b == Species.NIGHTMARE) return Species.NIGHTMARE;
        if (a == Species.UNICORN && b == Species.UNICORN) return Species.UNICORN;
        if (a == Species.PEGASUS && b == Species.PEGASUS) return Species.PEGASUS;
        if (a == Species.DARK_PEGASUS && b == Species.DARK_PEGASUS) return Species.DARK_PEGASUS;
        if ((a == Species.UNICORN && b == Species.PEGASUS) || (a == Species.PEGASUS && b == Species.UNICORN)) return Species.FAIRY_HORSE;
        if (a == Species.FAIRY_HORSE && b == Species.FAIRY_HORSE) return Species.FAIRY_HORSE;
        // Shouldn't happen if sterile parents are filtered out before calling this.
        return Species.HORSE;
    }
    private static boolean isTier4(Coat coat) {
        return coat == Coat.SPOTTED || coat == Coat.COW;
    }

    /** Whether two parents are allowed to breed at all — separate from WHAT
     * they produce (resolveOffspringSpecies/resolveOffspringCoat). Zebras only
     * cross with tier-4 horses (spotted/cow) or donkeys; same-species pairs
     * and horse+donkey are unrestricted. */
    public static boolean canBreed(Species a, Coat coatA, Species b, Coat coatB) {
        if (a.isSterile() || b.isSterile()) return false;
        if (a == Species.ZEBRA && b == Species.HORSE) return isTier4(coatB);
        if (b == Species.ZEBRA && a == Species.HORSE) return isTier4(coatA);
        if (a == Species.BATHORSE || b == Species.BATHORSE) {
            return a == Species.BATHORSE && b == Species.BATHORSE;
        }
        if (a == Species.NIGHTMARE || b == Species.NIGHTMARE) {
            return a == Species.NIGHTMARE && b == Species.NIGHTMARE;
        }
        // --- caso mixto ANTES de las exclusividades ---
        if ((a == Species.UNICORN && b == Species.PEGASUS) || (a == Species.PEGASUS && b == Species.UNICORN)) {
            return true;
        }
        if (a == Species.UNICORN || b == Species.UNICORN) {
            return a == Species.UNICORN && b == Species.UNICORN;
        }
        if (a == Species.PEGASUS || b == Species.PEGASUS) {
            return a == Species.PEGASUS && b == Species.PEGASUS;
        }
        if (a == Species.DARK_PEGASUS || b == Species.DARK_PEGASUS) {
            return a == Species.DARK_PEGASUS && b == Species.DARK_PEGASUS;
        }
        if (a == Species.FAIRY_HORSE || b == Species.FAIRY_HORSE) {
            return a == Species.FAIRY_HORSE && b == Species.FAIRY_HORSE;
        }
        return true;
    }
}
