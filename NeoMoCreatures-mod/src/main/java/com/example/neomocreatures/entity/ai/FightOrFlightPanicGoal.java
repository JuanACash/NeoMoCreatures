package com.example.neomocreatures.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

/** Panics like vanilla, except while the mob has a target it is fighting. */
public class FightOrFlightPanicGoal extends PanicGoal {

    public FightOrFlightPanicGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
    }

    @Override
    protected boolean shouldPanic() {
        return this.mob.getTarget() == null && super.shouldPanic();
    }
}