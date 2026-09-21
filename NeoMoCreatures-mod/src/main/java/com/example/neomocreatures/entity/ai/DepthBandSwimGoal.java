package com.example.neomocreatures.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.phys.Vec3;

/**
 * Random swimming that keeps to a band of depths under the water surface, like the original's
 * diving depth: every destination lies between {@code minDepth} and {@code maxDepth} blocks under
 * the surface. A mob that ended up deeper rises a few blocks at a time.
 */
public class DepthBandSwimGoal extends RandomSwimmingGoal {

    private static final int MAX_SURFACE_SCAN = 32;
    /** Keeps every destination inside the pathfinder's search range (follow range is 16). */
    private static final double MAX_VERTICAL_STEP = 8.0D;

    private final double minDepth;
    private final double maxDepth;

    public DepthBandSwimGoal(PathfinderMob mob, double speedModifier, int interval, double minDepth, double maxDepth) {
        super(mob, speedModifier, interval);
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    /** Original: isMovementCeased() is true out of water, so it never wanders while stranded. */
    @Override
    public boolean canUse() {
        return this.mob.isInWater() && super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 candidate = super.getPosition();
        return candidate == null ? null : this.moveIntoBand(candidate);
    }

    /**
     * Keeps the horizontal position of the point but puts it at a random depth inside the band.
     * Falls back to the original point when that depth is not water (shallow water).
     */
    @Nullable
    private Vec3 moveIntoBand(Vec3 point) {
        BlockPos.MutableBlockPos surface = BlockPos.containing(point).mutable();
        if (!this.isWater(surface)) {
            return null;
        }
        for (int i = 0; i < MAX_SURFACE_SCAN && this.isWater(surface.above()); i++) {
            surface.move(Direction.UP);
        }
        double depth = this.minDepth + this.mob.getRandom().nextDouble() * (this.maxDepth - this.minDepth);
        double targetY = Mth.clamp(surface.getY() + 1.0D - depth,
                this.mob.getY() - MAX_VERTICAL_STEP, this.mob.getY() + MAX_VERTICAL_STEP);
        Vec3 target = new Vec3(point.x, targetY, point.z);
        return this.isWater(BlockPos.containing(target)) ? target : point;
    }

    private boolean isWater(BlockPos pos) {
        return this.mob.level().getFluidState(pos).is(FluidTags.WATER);
    }
}