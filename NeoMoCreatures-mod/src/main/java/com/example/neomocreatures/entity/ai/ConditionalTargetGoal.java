package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/**
 * Vanilla nearest-target search that only runs while a condition holds
 * (in darkness, adult only, not tamed...). Replaces one small subclass per creature.
 */
public class ConditionalTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    private final BooleanSupplier condition;

    public ConditionalTargetGoal(Mob mob, Class<T> targetType, boolean mustSee, BooleanSupplier condition) {
        super(mob, targetType, mustSee);
        this.condition = condition;
    }

    public ConditionalTargetGoal(Mob mob, Class<T> targetType, int randomInterval, boolean mustSee,
                                 boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate,
                                 BooleanSupplier condition) {
        super(mob, targetType, randomInterval, mustSee, mustReach, targetPredicate);
        this.condition = condition;
    }

    @Override
    public boolean canUse() {
        return this.condition.getAsBoolean() && super.canUse();
    }
}
