package com.example.neomocreatures.worldgen;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/** Generates a cone that hangs from a cave ceiling and narrows towards its tip. */
public class StalactiteFeature extends Feature<StalactiteConfiguration> {

    /** Chance to leave out a block on the thick outer ring, so the cone is not a perfect shape. */
    private static final float EDGE_SKIP_CHANCE = 0.3F;
    private static final double EDGE_RING_START = 0.6D;
    private static final double MIN_RADIUS_FOR_ROUGH_EDGE = 1.5D;

    public StalactiteFeature(Codec<StalactiteConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StalactiteConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        StalactiteConfiguration config = context.config();

        BlockPos top = findAirBelowCeiling(level, context.origin(), config);
        if (top == null) {
            return false;
        }

        int baseRadius = config.baseRadius().sample(random);
        int length = config.length().sample(random);
        boolean placedAny = false;
        for (int depth = 0; depth < length; depth++) {
            double radius = baseRadius * (1.0D - (double) depth / length);
            placedAny |= placeLayer(level, random, config, top.below(depth), radius);
        }
        return placedAny;
    }

    /** Walks upwards through air; returns the last air block before an accepted ceiling block, or null. */
    private BlockPos findAirBelowCeiling(WorldGenLevel level, BlockPos origin, StalactiteConfiguration config) {
        if (!level.isEmptyBlock(origin)) {
            return null;
        }
        BlockPos.MutableBlockPos cursor = origin.mutable();
        for (int step = 0; step < config.maxCeilingScan(); step++) {
            cursor.move(Direction.UP);
            if (!level.isEmptyBlock(cursor)) {
                return config.ceiling().test(level, cursor) ? cursor.immutable().below() : null;
            }
        }
        return null;
    }

    private boolean placeLayer(WorldGenLevel level, RandomSource random, StalactiteConfiguration config,
                               BlockPos center, double radius) {
        int reach = (int) Math.ceil(radius);
        double limit = radius * radius + 0.25D;
        boolean placed = false;
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                double distanceSq = dx * dx + dz * dz;
                if (distanceSq > limit || this.skipRoughEdge(random, radius, distanceSq, limit)) {
                    continue;
                }
                BlockPos pos = center.offset(dx, 0, dz);
                if (!level.ensureCanWrite(pos) || !level.isEmptyBlock(pos) || level.isEmptyBlock(pos.above())) {
                    continue;
                }
                level.setBlock(pos, config.block().getState(random, pos), Block.UPDATE_CLIENTS);
                placed = true;
            }
        }
        return placed;
    }

    private boolean skipRoughEdge(RandomSource random, double radius, double distanceSq, double limit) {
        return radius > MIN_RADIUS_FOR_ROUGH_EDGE
                && distanceSq > limit * EDGE_RING_START
                && random.nextFloat() < EDGE_SKIP_CHANCE;
    }
}