package com.example.neomocreatures.entity;

import java.util.function.Predicate;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;

/**
 * The wiki's "sometimes follows a player holding X" behaviour for butterflies and bees (flowers) and
 * flies (rotten flesh). Vanilla's own {@link TemptGoal} does the following; the extra random gate
 * only lets it start once in a while, so it reads as "sometimes" rather than every time.
 */
public class MoCInsectTemptGoal extends TemptGoal {

    /** Only 1 in this many checks may start following, on top of TemptGoal's own cool-down. */
    private static final int FOLLOW_CHANCE = 4;

    private final PathfinderMob insect;

    public MoCInsectTemptGoal(PathfinderMob insect, double speedModifier, Predicate<ItemStack> wanted) {
        super(insect, speedModifier, wanted, false);
        this.insect = insect;
    }

    @Override
    public boolean canUse() {
        return this.insect.getRandom().nextInt(FOLLOW_CHANCE) == 0 && super.canUse();
    }
}