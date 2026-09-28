package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * Shared base of the flying insects, ported from {@code MoCEntityInsect}. Flies with vanilla's own
 * flying move control/navigation and never takes fall damage. Species that are attracted to light
 * periodically head for the nearest torch within 8 blocks.
 */
public abstract class MoCInsectEntity extends PathfinderMob {

    private static final double LIGHT_SEEK_RADIUS = 8.0D;
    private static final int LIGHT_SEEK_CHANCE = 50;
    private static final double WANDER_SPEED = 0.8D;
    /** Vanilla's default is 120 ticks between destinations; shorter keeps them moving instead of idling. */
    private static final int WANDER_INTERVAL_TICKS = 40;

    protected MoCInsectEntity(EntityType<? extends MoCInsectEntity> type, Level level) {
        super(type, level);
        // hoversInPlace = true: with false, gravity switched back on every time it was waiting for
        // its next destination, so it kept dropping to the ground.
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        WaterAvoidingRandomFlyingGoal wander = new WaterAvoidingRandomFlyingGoal(this, WANDER_SPEED);
        wander.setInterval(WANDER_INTERVAL_TICKS);
        this.goalSelector.addGoal(0, wander);
    }

    /** Original: two empty air blocks below it, i.e. not resting on anything. */
    public boolean isOnAir() {
        BlockPos below1 = BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
        BlockPos below2 = BlockPos.containing(this.getX(), this.getY() - 1.2D, this.getZ());
        return this.level().isEmptyBlock(below1) && this.level().isEmptyBlock(below2);
    }

    /** Deviates from the original (two empty blocks below and no wall contact), which left insects
     *  visibly airborne but drawn in their perched pose whenever they hovered low or brushed a wall. */
    public boolean isFlying() {
        return !this.onGround() && !this.isInWater();
    }

    /** Original: getSizeFactor() - uniform render scale, 1.0 unless a species overrides it. */
    public float getSizeFactor() {
        return 1.0F;
    }

    public boolean isAttractedToLight() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isInWater()) {
            // Original added +1.0 to X/Z velocity every tick here, flinging it away; a gentle lift
            // out of the water is what that was clearly meant to be.
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
        }
        if (!this.level().isClientSide && this.isAttractedToLight() && this.random.nextInt(LIGHT_SEEK_CHANCE) == 0) {
            BlockPos torch = this.findNearestTorch();
            if (torch != null) {
                this.getNavigation().moveTo(torch.getX() + 0.5D, torch.getY() + 0.5D, torch.getZ() + 0.5D, 1.0D);
            }
        }
    }

    @Nullable
    private BlockPos findNearestTorch() {
        int radius = (int) LIGHT_SEEK_RADIUS;
        BlockPos center = this.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            var block = this.level().getBlockState(pos).getBlock();
            if (block == Blocks.TORCH || block == Blocks.WALL_TORCH) {
                double distance = pos.distSqr(center);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = pos.immutable();
                }
            }
        }
        return nearest;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    /** Wiki: every insect can scale solid blocks. */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    /** Wiki: every insect gives 1-3 experience when killed by a player or a tamed wolf. */
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }
}
