package com.example.neomocreatures.entity.horse;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Coat;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;

/**
 * Base attributes of a horse by species and coat tier (health, speed, jump).
 * Undead/Skeleton stages keep their species, so they inherit their counterpart's stats.
 *
 * <p>The jump values come from simulating a horse's real jump physics (gravity 0.08
 * blocks/tick², drag 0.98) to reach the exact height in blocks, since jump_strength
 * is not linear with height.</p>
 */
public record HorseStats(double maxHealth, double movementSpeed, double jumpStrength) {

    private static final double JUMP_1_5_BLOCKS = 0.4965D;
    private static final double JUMP_2_BLOCKS = 0.5750D;
    private static final double JUMP_3_BLOCKS = 0.7099D;
    private static final double JUMP_4_BLOCKS = 0.8254D;
    private static final double JUMP_4_5_BLOCKS = 0.8791D;
    private static final double JUMP_5_5_BLOCKS = 0.9790D;

    /** "Walking alone" speed (no rider) of the special horses: the same as a tier-4 horse. */
    public static final double SPECIAL_UNMOUNTED_SPEED = 0.2594D;

    private static final HorseStats DEFAULT = new HorseStats(18D, 0.2101D, JUMP_2_BLOCKS);

    public static HorseStats of(Species species, Coat coat) {
        return switch (species) {
            case DONKEY -> new HorseStats(16D, 0.175D, JUMP_1_5_BLOCKS);
            case MULE, ZONKY -> new HorseStats(18D, 0.1901D, JUMP_1_5_BLOCKS);
            case ZEBRA -> new HorseStats(18D, 0.2101D, JUMP_2_BLOCKS);
            case ZORSE -> new HorseStats(24D, 0.2594D, JUMP_4_BLOCKS);
            case BATHORSE, NIGHTMARE -> new HorseStats(26D, 0.3104D, JUMP_4_5_BLOCKS);
            case UNICORN -> new HorseStats(28D, 0.4D, JUMP_5_5_BLOCKS);
            case PEGASUS -> new HorseStats(28D, 0.37D, JUMP_4_5_BLOCKS);
            case DARK_PEGASUS -> new HorseStats(28D, 0.34D, JUMP_4_5_BLOCKS);
            case FAIRY_HORSE -> new HorseStats(30D, 0.4D, JUMP_4_5_BLOCKS);
            case GHOST, GHOST_WINGED, HORSE_BUG -> new HorseStats(26D, 0.3104D, JUMP_4_5_BLOCKS);
            case HORSE -> switch (coatTier(coat)) {
                case 3 -> new HorseStats(20D, 0.2432D, JUMP_3_BLOCKS);
                case 4 -> new HorseStats(24D, 0.2594D, JUMP_4_BLOCKS);
                default -> DEFAULT; // tiers 1 and 2
            };
            default -> DEFAULT;
        };
    }

    /** Rarity tier (1-4) of a normal horse's coat. */
    public static int coatTier(Coat coat) {
        return switch (coat) {
            case WHITE, CREAMY, BROWN, DARKBROWN, BLACK -> 1;
            case BRIGHTCREAMY, SPECKLED, PALEBROWN, GREY -> 2;
            case PINTO, BRIGHTPINTO, PALESPECKLES -> 3;
            case SPOTTED, COW -> 4;
        };
    }

    /**
     * Bathorse, Nightmare, Unicorn, Pegasus, Dark Pegasus, Fairy and Ghost/Ghost Winged
     * (undead versions included, since undead does not change species) walk slower without
     * a rider. Zorse, donkey/mule/zonkey, zebra and horse are deliberately left out.
     */
    public static boolean isSlowedWhenUnridden(Species species) {
        return switch (species) {
            case BATHORSE, NIGHTMARE, UNICORN, PEGASUS, DARK_PEGASUS, FAIRY_HORSE, GHOST, GHOST_WINGED -> true;
            default -> false;
        };
    }
}