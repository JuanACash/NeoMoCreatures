package com.example.neomocreatures.entity.komodo;

import com.example.neomocreatures.entity.MoCKomodoDragonEntity;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Makes the Komodo dragon occasionally settle down and sit with its legs
 * spread out, matching the wiki's described idle behaviour:
 * "Occasionally, a Komodo dragon will sit on the ground with their legs
 * spread out."
 * <p>
 * The pose itself is rendered by {@code KomodoDragonModel} by reading
 * {@link net.minecraft.world.entity.TamableAnimal#isInSittingPose()} —
 * this goal only decides WHEN that flag is toggled.
 */
public class KomodoSitGoal extends Goal {

    /** Roughly one attempt every SIT_CHANCE ticks (~25s at 20 tps). */
    private static final int SIT_CHANCE = 500;
    private static final int MAX_SIT_DURATION_TICKS = 150;

    private final MoCKomodoDragonEntity komodo;
    private int sitDurationTicks;

    public KomodoSitGoal(MoCKomodoDragonEntity komodo) {
        this.komodo = komodo;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.komodo.isInWater() || this.komodo.isVehicle() || this.komodo.getTarget() != null) {
            return false;
        }
        return this.komodo.getRandom().nextInt(SIT_CHANCE) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.sitDurationTicks < MAX_SIT_DURATION_TICKS
                && !this.komodo.isInWater()
                && !this.komodo.isVehicle()
                && this.komodo.getTarget() == null;
    }

    @Override
    public void start() {
        this.sitDurationTicks = 0;
        this.komodo.setInSittingPose(true);
        this.komodo.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.sitDurationTicks++;
    }

    @Override
    public void stop() {
        this.komodo.setInSittingPose(false);
    }
}