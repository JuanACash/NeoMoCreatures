package com.example.neomocreatures.entity.ai;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

/**
 * Vanilla flee-from-entity that only starts (and optionally only continues) while a condition
 * holds — e.g. only young animals, only while carrying loot.
 */
public class ConditionalAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    private final BooleanSupplier canStart;
    private final BooleanSupplier canKeepGoing;

    /** The condition only gates the start; once fleeing it runs like the vanilla goal. */
    public ConditionalAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float maxDistance,
                                      double walkSpeedModifier, double sprintSpeedModifier, BooleanSupplier condition) {
        super(mob, avoidClass, maxDistance, walkSpeedModifier, sprintSpeedModifier);
        this.canStart = condition;
        this.canKeepGoing = () -> true;
    }

    /** Separate conditions to start fleeing and to keep fleeing. */
    public ConditionalAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float maxDistance,
                                      double walkSpeedModifier, double sprintSpeedModifier,
                                      BooleanSupplier canStart, BooleanSupplier canKeepGoing) {
        super(mob, avoidClass, maxDistance, walkSpeedModifier, sprintSpeedModifier);
        this.canStart = canStart;
        this.canKeepGoing = canKeepGoing;
    }

    /** Only flees from entities matching the predicate; the condition only gates the start. */
    public ConditionalAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float maxDistance,
                                      double walkSpeedModifier, double sprintSpeedModifier,
                                      Predicate<LivingEntity> avoidPredicate, BooleanSupplier condition) {
        super(mob, avoidClass, maxDistance, walkSpeedModifier, sprintSpeedModifier, avoidPredicate);
        this.canStart = condition;
        this.canKeepGoing = () -> true;
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
