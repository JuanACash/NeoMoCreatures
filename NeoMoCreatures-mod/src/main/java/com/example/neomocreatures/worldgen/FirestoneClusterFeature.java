package com.example.neomocreatures.worldgen;

import com.example.neomocreatures.init.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FirestoneClusterFeature extends Feature<NoneFeatureConfiguration> {

    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    public FirestoneClusterFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        boolean success = false;

        attempts:
        for (int attempt = 0; attempt < 5; attempt++) {
            BlockPos base = origin.offset(random.nextInt(16), random.nextInt(50) + 20, random.nextInt(16));

            for (int offsetY = 0; offsetY < 20; offsetY++) {
                BlockPos check = base.below(offsetY);
                if (isTooCloseToSurface(level, check)) continue;

                boolean canPlace = false;
                Direction attachDirection = null;

                if (level.isEmptyBlock(check)) {
                    BlockPos abovePos = check.above();
                    BlockState blockAbove = level.getBlockState(abovePos);
                    if (blockAbove.isFaceSturdy(level, abovePos, Direction.DOWN) && !isWyvwoodBlock(blockAbove.getBlock())) {
                        boolean isGoodCeiling = true;
                        for (int i = 1; i <= 3 && isGoodCeiling; i++) {
                            if (!level.isEmptyBlock(check.below(i))) {
                                isGoodCeiling = false;
                            }
                        }
                        if (isGoodCeiling) {
                            canPlace = true;
                            attachDirection = Direction.DOWN;
                        }
                    }
                }

                if (!canPlace) {
                    for (Direction dir : HORIZONTAL_DIRECTIONS) {
                        if (!level.isEmptyBlock(check)) continue;
                        BlockPos sidePos = check.relative(dir);
                        if (level.isEmptyBlock(sidePos)) continue;
                        BlockState blockSide = level.getBlockState(sidePos);
                        if (isWyvwoodBlock(blockSide.getBlock())) continue;
                        if (!blockSide.isFaceSturdy(level, sidePos, dir.getOpposite())) continue;

                        boolean isGoodWall = true;
                        for (int i2 = 1; i2 <= 3 && isGoodWall; i2++) {
                            if (!level.isEmptyBlock(check.relative(dir.getOpposite(), i2))) {
                                isGoodWall = false;
                            }
                        }
                        if (!isGoodWall) continue;

                        canPlace = true;
                        attachDirection = dir.getOpposite();
                        break;
                    }
                }

                if (canPlace) {
                    BlockPos below = check.below();
                    if (!level.isEmptyBlock(below) && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                        canPlace = false;
                    }
                }

                if (!canPlace || attachDirection == null) continue;

                BlockState firestoneState = ModBlocks.ORE_FIRESTONE.get().defaultBlockState();
                level.setBlock(check, firestoneState, 2);
                success = true;

                int extra = 12 + random.nextInt(8);
                for (int i3 = 0; i3 < extra; i3++) {
                    int xOffset = random.nextInt(5) - 2;
                    int yOffset = random.nextInt(3) - 1;
                    int zOffset = random.nextInt(5) - 2;

                    switch (attachDirection) {
                        case DOWN -> yOffset = -Math.abs(yOffset);
                        case NORTH -> zOffset = -Math.abs(zOffset);
                        case SOUTH -> zOffset = Math.abs(zOffset);
                        case WEST -> xOffset = -Math.abs(xOffset);
                        case EAST -> xOffset = Math.abs(xOffset);
                        default -> { }
                    }

                    BlockPos nearby = check.offset(xOffset, yOffset, zOffset);
                    if (level.isEmptyBlock(nearby)) {
                        level.setBlock(nearby, firestoneState, 2);
                    }
                }

                continue attempts;
            }
        }

        return success;
    }

    private boolean isTooCloseToSurface(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() > level.getMaxBuildHeight() - 20) {
            return true;
        }
        int airBlocksAbove = 0;
        for (int y = 1; y <= 10 && level.isEmptyBlock(pos.above(y)); y++) {
            airBlocksAbove++;
        }
        return airBlocksAbove >= 8;
    }

    private boolean isWyvwoodBlock(Block block) {
        return block == ModBlocks.WYVWOOD_LOG.get()
                || block == ModBlocks.WYVWOOD_PLANKS.get()
                || block == ModBlocks.WYVWOOD_LEAVES.get();
    }
}