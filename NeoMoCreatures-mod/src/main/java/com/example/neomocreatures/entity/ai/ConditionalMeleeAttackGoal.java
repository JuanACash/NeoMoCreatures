package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * Vanilla melee attack that only starts (and optionally only continues) while a condition
 * holds — e.g. not while ridden, only when retaliating. Replaces one small subclass per creature.
 */
public class ConditionalMeleeAttackGoal extends MeleeAttackGoal {

    private final BooleanSupplier canStart;
    private final BooleanSupplier canKeepGoing;

    /** The same condition is required to start and to keep attacking. */
    public ConditionalMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followEvenIfNotSeen,
                                      BooleanSupplier condition) {
        this(mob, speedModifier, followEvenIfNotSeen, condition, condition);
    }

    public ConditionalMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followEvenIfNotSeen,
                                      BooleanSupplier canStart, BooleanSupplier canKeepGoing) {
        super(mob, speedModifier, followEvenIfNotSeen);
        this.canStart = canStart;
        this.canKeepGoing = canKeepGoing;
    }

    @Override
    public boolean canUse() {
        return this.canStart.getAsBoolean() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canKeepGoing.getAsBoolean() && super.canContinueToUse();
    }
}
