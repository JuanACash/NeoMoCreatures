package com.example.neomocreatures.entity.wyvern.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

/** Parrot-like landing: once a flight is over it glides down to firm ground nearby and walks again. */
public final class WyvernLandGoal extends Goal {

    /** Landing spots are searched within 8 blocks, retried every 5 s; it touches down within 1.5 blocks of the ground. */
    private static final int LANDING_SEARCH_RANGE = 8;
    public static final int LANDING_RETRY_TICKS = 100;
    private static final double TOUCHDOWN_HEIGHT = 1.5D;

    private final MoCWyvernEntity wyvern;
    @Nullable
    private BlockPos landingSpot;
    private int retryTicks;

    public WyvernLandGoal(MoCWyvernEntity wyvern) {
        this.wyvern = wyvern;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.wyvern.canFlyFreely() && this.wyvern.wantsToLand();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.findLandingSpot();
    }

    @Override
    public void stop() {
        this.landingSpot = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.wyvern.onGround() || this.isAboutToTouchDown()) {
            this.wyvern.setIsFlying(false);
            return;
        }
        if (this.landingSpot == null || --this.retryTicks <= 0) {
            this.findLandingSpot();
            return;
        }
        this.wyvern.getMoveControl().setWantedPosition(
                this.landingSpot.getX() + 0.5D, this.landingSpot.getY() + 1.0D, this.landingSpot.getZ() + 0.5D, 1.0D);
    }

    /** A dry, sturdy spot within 8 blocks; over water or lava it just keeps flying a little longer. */
    private void findLandingSpot() {
        this.retryTicks = LANDING_RETRY_TICKS;
        RandomSource random = this.wyvern.getRandom();
        Level level = this.wyvern.level();
        int x = this.wyvern.getBlockX() + random.nextInt(LANDING_SEARCH_RANGE * 2 + 1) - LANDING_SEARCH_RANGE;
        int z = this.wyvern.getBlockZ() + random.nextInt(LANDING_SEARCH_RANGE * 2 + 1) - LANDING_SEARCH_RANGE;
        BlockPos spot = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
        BlockPos ground = spot.below();
        if (level.getFluidState(ground).isEmpty() && level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)) {
            this.landingSpot = spot;
        } else {
            this.landingSpot = null;
            this.wyvern.extendFlight();
        }
    }

    /** Something solid right under its feet — close enough to put its legs down. */
    private boolean isAboutToTouchDown() {
        AABB below = this.wyvern.getBoundingBox().move(0.0D, -TOUCHDOWN_HEIGHT, 0.0D);
        return !this.wyvern.level().noCollision(this.wyvern, below);
    }
}