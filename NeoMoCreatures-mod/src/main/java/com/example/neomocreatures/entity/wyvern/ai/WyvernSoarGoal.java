package com.example.neomocreatures.entity.wyvern.ai;

import java.util.EnumSet;

import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

/** Ghast: RandomFloatAroundGoal — drifts to random points around it, a few blocks above the ground. */
public final class WyvernSoarGoal extends Goal {

    /** Ghast: gives up on a destination more than 60 blocks away (squared). */
    private static final double LOST_DISTANCE_SQR = 3600.0D;
    /** Ghast: new destinations are picked up to 16 blocks away horizontally. */
    private static final double SOAR_RANGE = 16.0D;
    /** Cruising height above the ground while soaring: 4 to 13 blocks. */
    private static final int MIN_SOAR_ALTITUDE = 4;
    private static final int SOAR_ALTITUDE_RANGE = 10;

    private final MoCWyvernEntity wyvern;

    public WyvernSoarGoal(MoCWyvernEntity wyvern) {
        this.wyvern = wyvern;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.wyvern.canFlyFreely() || this.wyvern.wantsToLand()) {
            return false;
        }
        MoveControl control = this.wyvern.getMoveControl();
        if (!control.hasWanted()) {
            return true;
        }
        double distanceSqr = this.wyvern.distanceToSqr(control.getWantedX(), control.getWantedY(), control.getWantedZ());
        return distanceSqr < WyvernMoveControl.ARRIVED_DISTANCE_SQR || distanceSqr > LOST_DISTANCE_SQR;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        RandomSource random = this.wyvern.getRandom();
        double x = this.wyvern.getX() + (random.nextDouble() * 2.0D - 1.0D) * SOAR_RANGE;
        double z = this.wyvern.getZ() + (random.nextDouble() * 2.0D - 1.0D) * SOAR_RANGE;
        int ground = this.wyvern.level().getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z));
        double y = ground + MIN_SOAR_ALTITUDE + random.nextInt(SOAR_ALTITUDE_RANGE);
        this.wyvern.getMoveControl().setWantedPosition(x, y, z, 1.0D);
    }
}