package com.example.neomocreatures.breeding;

import com.example.neomocreatures.entity.dolphin.DolphinVariant;

import net.minecraft.util.RandomSource;

/**
 * Colour genetics of the dolphins, ported from {@code MoCEntityDolphin.Genetics}. Every colour has a
 * genetic value (blue 1 ... albino 6). Two dolphins of the same colour always have a calf of that
 * colour; otherwise the sum of their values (the total genetic value) sets the odds of a calf whose
 * value equals that sum, and anything else is rolled like a wild dolphin.
 */
public final class MoCDolphinGenetics {

    /** A total below this gives the sum colour 1 time in {@link #LOW_TOTAL_ODDS}. */
    private static final int LOW_TOTAL_LIMIT = 5;
    private static final int LOW_TOTAL_ODDS = 3;
    /** A total from {@link #LOW_TOTAL_LIMIT} up to this gives the sum colour 1 time in {@link #HIGH_TOTAL_ODDS}. */
    private static final int HIGH_TOTAL_LIMIT = 6;
    private static final int HIGH_TOTAL_ODDS = 10;

    private MoCDolphinGenetics() {
    }

    public static DolphinVariant resolveOffspring(DolphinVariant first, DolphinVariant second, RandomSource random) {
        if (first == second) {
            return first;
        }
        int total = first.getGeneticValue() + second.getGeneticValue();
        if (total < LOW_TOTAL_LIMIT && random.nextInt(LOW_TOTAL_ODDS) == 0) {
            return DolphinVariant.byGeneticValue(total);
        }
        if (total >= LOW_TOTAL_LIMIT && total <= HIGH_TOTAL_LIMIT && random.nextInt(HIGH_TOTAL_ODDS) == 0) {
            return DolphinVariant.byGeneticValue(total);
        }
        return DolphinVariant.random(random);
    }
}