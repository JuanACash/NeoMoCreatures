package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

/**
 * Vanilla panic (run away when hurt, burning or freezing) that only applies while a condition
 * holds — e.g. only cubs, only passive variants, only while in water.
 */
public class ConditionalPanicGoal extends PanicGoal {

    private final BooleanSupplier condition;

    public ConditionalPanicGoal(PathfinderMob mob, double speedModifier, BooleanSupplier condition) {
        super(mob, speedModifier);
        this.condition = condition;
    }

    @Override
    public boolean canUse() {
        return this.condition.getAsBoolean() && super.canUse();
    }
}
