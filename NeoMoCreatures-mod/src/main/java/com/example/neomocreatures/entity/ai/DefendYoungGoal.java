package com.example.neomocreatures.entity.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

/** Makes a mob fight whoever was marked as a threat to its young, for a limited time. */
public class DefendYoungGoal extends TargetGoal {

    private static final int DEFENSE_TICKS = 400;

    @Nullable
    private LivingEntity attacker;
    private int ticksLeft;

    public DefendYoungGoal(Mob mob) {
        super(mob, false);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    /** Marks an attacker as a threat, unless the shared rules say this mob should not fight it. */
    public void alert(LivingEntity threat) {
        if (YoungDefense.isValidAttacker(this.mob, threat)) {
            this.attacker = threat;
            this.ticksLeft = DEFENSE_TICKS;
        }
    }

    @Override
    public boolean canUse() {
        return this.attacker != null && this.ticksLeft > 0 && this.attacker.isAlive();
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticksLeft > 0 && this.attacker != null && this.attacker.isAlive() && super.canContinueToUse();
    }

    @Override
    public void tick() {
        this.ticksLeft--;
    }

    @Override
    public void stop() {
        if (this.mob.getTarget() == this.attacker) {
            this.mob.setTarget(null);
        }
        this.attacker = null;
        this.ticksLeft = 0;
        super.stop();
    }
}