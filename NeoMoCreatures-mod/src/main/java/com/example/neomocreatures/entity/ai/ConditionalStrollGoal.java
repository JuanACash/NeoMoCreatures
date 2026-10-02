package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

/**
 * Vanilla random walking (avoiding water) that only runs while a condition holds,
 * e.g. not while ridden or flying.
 */
public class ConditionalStrollGoal extends WaterAvoidingRandomStrollGoal {

    private final BooleanSupplier condition;

    public ConditionalStrollGoal(PathfinderMob mob, double speedModifier, BooleanSupplier condition) {
        super(mob, speedModifier);
        this.condition = condition;
    }

    @Override
    public boolean canUse() {
        return this.condition.getAsBoolean() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.condition.getAsBoolean() && super.canContinueToUse();
    }
}
