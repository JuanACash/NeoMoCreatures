package com.example.neomocreatures.entity.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Port of {@code drzhark.mocreatures.entity.ai.EntityAIFollowHerd}: every so often, if a matching
 * herd-mate is between {@code minRange} and {@code maxRange} away, walk toward it. Used by the
 * piranha so a group breaks up less easily when one member wanders off chasing something.
 */
public class HerdFollowGoal extends Goal {

    private static final int RECHECK_TICKS = 20;

    private final Mob mob;
    private final double speedModifier;
    private final double minRange;
    private final double maxRange;
    private final int executionChance;
    /** Same-class alone is not enough when several kinds of mob share one Java class (e.g. the small
     *  fish's 8 species): this decides which candidates actually count as this mob's herd. */
    private final Predicate<Mob> herdMatePredicate;

    private Mob herdMate;
    private int delayCounter;

    public HerdFollowGoal(Mob mob, double speedModifier, double minRange, double maxRange, int executionChance) {
        this(mob, speedModifier, minRange, maxRange, executionChance, other -> true);
    }

    public HerdFollowGoal(Mob mob, double speedModifier, double minRange, double maxRange, int executionChance,
                          Predicate<Mob> herdMatePredicate) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.executionChance = executionChance;
        this.herdMatePredicate = herdMatePredicate;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.executionChance) != 0) {
            return false;
        }
        List<? extends Mob> nearby = this.mob.level().getEntitiesOfClass(this.mob.getClass(),
                this.mob.getBoundingBox().inflate(this.maxRange, 4.0D, this.maxRange));
        Mob closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Mob candidate : nearby) {
            if (candidate == this.mob || !this.herdMatePredicate.test(candidate)) {
                continue;
            }
            double distance = this.mob.distanceToSqr(candidate);
            if (distance >= this.minRange * this.minRange && distance < closestDistance) {
                closestDistance = distance;
                closest = candidate;
            }
        }
        if (closest == null || closestDistance < this.maxRange * this.maxRange) {
            return false;
        }
        this.herdMate = closest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.herdMate.isAlive()) {
            return false;
        }
        double distance = this.mob.distanceToSqr(this.herdMate);
        return distance >= this.minRange * this.minRange && distance <= this.maxRange * this.maxRange;
    }

    @Override
    public void start() {
        this.delayCounter = 0;
    }

    @Override
    public void stop() {
        this.herdMate = null;
    }

    @Override
    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = RECHECK_TICKS;
            this.mob.getNavigation().moveTo(this.herdMate, this.speedModifier);
        }
    }
}