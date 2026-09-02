package com.example.neomocreatures.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.example.neomocreatures.init.ModBlocks;

/**
 * Builds the small quartz arrival platform that marks the fixed entry point
 * of the Wyvern Lair dimension.
 * <p>
 * This is a direct port of the original Mo' Creatures {@code MoCWorldGenPortal}
 * structure (stairs ring, inner wall, center platform, four corner pillars),
 * kept as a stateless, single-responsibility helper so it can later be reused
 * by other fixed-point teleporters without duplicating the layout.
 */
public final class WyvernPortalPlatform {

    private static final BlockState PILLAR_BLOCK = Blocks.QUARTZ_PILLAR.defaultBlockState();
    private static final BlockState STAIR_BLOCK = Blocks.QUARTZ_STAIRS.defaultBlockState();
    private static final BlockState WALL_BLOCK = Blocks.QUARTZ_BLOCK.defaultBlockState();
    private static final BlockState CENTER_BLOCK = Blocks.QUARTZ_BLOCK.defaultBlockState();

    /** Flag 2: send the change to clients. Matches the original generator's update flags. */
    private static final int UPDATE_FLAGS = 2;

    private WyvernPortalPlatform() {
    }

    /**
     * Generates the platform below {@code arrivalPos} unless it has already been built.
     *
     * @param level      the destination level (the Wyvern Lair)
     * @param arrivalPos the block the player is teleported onto
     */
    public static void generateIfMissing(ServerLevel level, BlockPos arrivalPos) {
        if (!level.isEmptyBlock(arrivalPos)) {
            return;
        }
        carveBase(level, arrivalPos);
        generate(level, arrivalPos);
    }

    private static void carveBase(ServerLevel level, BlockPos arrivalPos) {
        BlockState fill = ModBlocks.WYVSTONE.get().defaultBlockState();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy = -10; dy < 0; dy++) {
                    level.setBlock(arrivalPos.offset(dx, dy, dz), fill, UPDATE_FLAGS);
                }
                for (int dy = 1; dy <= 8; dy++) {
                    level.setBlock(arrivalPos.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
                }
            }
        }
    }

    private static void generate(ServerLevel level, BlockPos arrivalPos) {
        int x = arrivalPos.getX();
        int y = arrivalPos.getY() - 1;
        int z = arrivalPos.getZ();

        placeStairRing(level, x, y, z);
        placeInnerWall(level, x, y, z);
        placeCenterPlatform(level, x, y, z);
        placeTopBlocks(level, x, y, z);

        generatePillar(level, new BlockPos(x - 3, y, z - 3));
        generatePillar(level, new BlockPos(x - 3, y, z + 2));
        generatePillar(level, new BlockPos(x + 2, y, z - 3));
        generatePillar(level, new BlockPos(x + 2, y, z + 2));
    }

    private static void placeStairRing(ServerLevel level, int x, int y, int z) {
        for (int stairZ = z - 3; stairZ <= z + 2; stairZ += 5) {
            Direction facing = stairZ < z ? Direction.SOUTH : Direction.NORTH;
            BlockState facingStairs = STAIR_BLOCK.setValue(StairBlock.FACING, facing);
            for (int stairX = x - 2; stairX < x + 2; stairX++) {
                level.setBlock(new BlockPos(stairX, y + 1, stairZ), facingStairs, UPDATE_FLAGS);
            }
        }
    }

    private static void placeInnerWall(ServerLevel level, int x, int y, int z) {
        for (int wallX = x - 2; wallX < x + 2; wallX++) {
            for (int wallZ = z - 2; wallZ < z + 2; wallZ++) {
                level.setBlock(new BlockPos(wallX, y + 1, wallZ), WALL_BLOCK, UPDATE_FLAGS);
            }
        }
    }

    private static void placeCenterPlatform(ServerLevel level, int x, int y, int z) {
        for (int centerX = x - 1; centerX < x + 1; centerX++) {
            for (int centerZ = z - 1; centerZ < z + 1; centerZ++) {
                level.setBlock(new BlockPos(centerX, y + 1, centerZ), CENTER_BLOCK, UPDATE_FLAGS);
            }
        }
    }

    private static void placeTopBlocks(ServerLevel level, int x, int y, int z) {
        for (int topX = x - 3; topX < x + 3; topX += 5) {
            for (int topZ = z - 3; topZ < z + 3; topZ++) {
                level.setBlock(new BlockPos(topX, y + 6, topZ), WALL_BLOCK, UPDATE_FLAGS);
            }
        }
    }

    private static void generatePillar(ServerLevel level, BlockPos base) {
        for (int step = 0; step < 6; step++) {
            level.setBlock(base.above(step), PILLAR_BLOCK, UPDATE_FLAGS);
        }
    }
}