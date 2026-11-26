package com.example.neomocreatures.entity.ogre;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Makes an ogre destroy any block that stands in its way while it chases a target,
 * except obsidian and unbreakable blocks. Does not use movement flags, so it runs
 * alongside the melee and look goals.
 */
public class OgreBreakObstructionGoal extends Goal {

    private static final int CHECK_INTERVAL_TICKS = 5;
    /** How far ahead of its body, in blocks, the ogre looks for obstructions. */
    private static final double REACH_AHEAD = 1.0D;
    private static final double MIN_HORIZONTAL_DISTANCE = 0.5D;
    /** Ignores the very bottom of the body so the floor under its feet is never broken. */
    private static final double FOOT_MARGIN = 0.05D;

    private final MoCOgreEntity ogre;
    private int cooldown;

    public OgreBreakObstructionGoal(MoCOgreEntity ogre) {
        this.ogre = ogre;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.ogre.getTarget();
        return target != null && target.isAlive() && this.isGriefingAllowed();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void tick() {
        if (--this.cooldown > 0) {
            return;
        }
        this.cooldown = CHECK_INTERVAL_TICKS;

        LivingEntity target = this.ogre.getTarget();
        if (target == null) {
            return;
        }
        Vec3 direction = this.horizontalDirectionTo(target);
        if (direction == null) {
            return;
        }
        AABB body = this.ogre.getBoundingBox();
        AABB searchBox = new AABB(body.minX, body.minY + FOOT_MARGIN, body.minZ, body.maxX, body.maxY, body.maxZ)
                .move(direction.scale(REACH_AHEAD));
        this.breakObstructionsIn(searchBox);
    }

    private Vec3 horizontalDirectionTo(LivingEntity target) {
        double dx = target.getX() - this.ogre.getX();
        double dz = target.getZ() - this.ogre.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < MIN_HORIZONTAL_DISTANCE) {
            return null;
        }
        return new Vec3(dx / distance, 0.0D, dz / distance);
    }

    private void breakObstructionsIn(AABB searchBox) {
        Level level = this.ogre.level();
        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(searchBox.minX), Mth.floor(searchBox.minY), Mth.floor(searchBox.minZ),
                Mth.floor(searchBox.maxX), Mth.floor(searchBox.maxY), Mth.floor(searchBox.maxZ))) {
            BlockState state = level.getBlockState(pos);
            if (this.isBreakableObstruction(level, pos, state, searchBox)) {
                level.destroyBlock(pos, true, this.ogre);
            }
        }
    }

    private boolean isBreakableObstruction(Level level, BlockPos pos, BlockState state, AABB searchBox) {
        if (state.isAir() || isProtected(level, pos, state)) {
            return false;
        }
        VoxelShape collision = state.getCollisionShape(level, pos);
        if (collision.isEmpty()) {
            return false;
        }
        return collision.bounds().move(pos).intersects(searchBox);
    }

    /** Obsidian and anything unbreakable (bedrock, barrier, command blocks...) is never destroyed. */
    private static boolean isProtected(Level level, BlockPos pos, BlockState state) {
        return state.is(Blocks.OBSIDIAN)
                || state.is(Blocks.CRYING_OBSIDIAN)
                || state.is(Blocks.BEDROCK)
                || state.getDestroySpeed(level, pos) < 0.0F;
    }

    private boolean isGriefingAllowed() {
        return this.ogre.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }
}