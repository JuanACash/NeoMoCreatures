package com.example.neomocreatures.worldgen;

import com.example.neomocreatures.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Builds the small arrival platform of the Ogre Lair and decides where it fits.
 * Stateless and single-purpose: finding the site and persisting it live elsewhere.
 */
public final class OgreLairPortalPlatform {

    private static final int HALF_SIZE = 2;
    private static final int CLEARANCE_HEIGHT = 4;
    private static final int SIDE_CLEARANCE_HEIGHT = 2;
    private static final int PILLAR_HEIGHT = 3;

    private OgreLairPortalPlatform() {
    }

    /** A site is suitable when the floor is solid and the space right above it is open air. */
    public static boolean isSuitableSite(ServerLevel level, BlockPos floorPos) {
        if (!level.getBlockState(floorPos).canOcclude()) {
            return false;
        }
        for (int dy = 1; dy <= CLEARANCE_HEIGHT; dy++) {
            if (!level.isEmptyBlock(floorPos.above(dy))) {
                return false;
            }
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            for (int dy = 1; dy <= SIDE_CLEARANCE_HEIGHT; dy++) {
                if (!level.isEmptyBlock(floorPos.relative(side).above(dy))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Lays a floor, clears the space above it and raises a log pillar on each corner. */
    public static void build(ServerLevel level, BlockPos floorPos) {
        BlockState floor = ModBlocks.MOSSY_COBBLED_DEEP_WYVSTONE.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState pillar = ModBlocks.SILVER_SANDSTONE_CARVED.get().defaultBlockState();

        for (int dx = -HALF_SIZE; dx <= HALF_SIZE; dx++) {
            for (int dz = -HALF_SIZE; dz <= HALF_SIZE; dz++) {
                BlockPos column = floorPos.offset(dx, 0, dz);
                level.setBlock(column, floor, Block.UPDATE_CLIENTS);
                for (int dy = 1; dy <= CLEARANCE_HEIGHT; dy++) {
                    level.setBlock(column.above(dy), air, Block.UPDATE_CLIENTS);
                }
            }
        }

        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sz = -1; sz <= 1; sz += 2) {
                BlockPos corner = floorPos.offset(sx * HALF_SIZE, 0, sz * HALF_SIZE);
                for (int step = 1; step <= PILLAR_HEIGHT; step++) {
                    level.setBlock(corner.above(step), pillar, Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}