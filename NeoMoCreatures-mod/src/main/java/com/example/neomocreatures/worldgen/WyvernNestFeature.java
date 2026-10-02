package com.example.neomocreatures.worldgen;

import com.example.neomocreatures.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

import com.mojang.serialization.Codec;

public class WyvernNestFeature extends Feature<NoneFeatureConfiguration> {

    public WyvernNestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (random.nextInt(3) != 0) {
            return false;
        }

        int x = origin.getX();
        int z = origin.getZ();
        int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        BlockPos base = new BlockPos(x, y - 1, z);

        int validBlocks = 0;
        int totalBlocks = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos check = base.offset(dx, 0, dz);
                totalBlocks++;
                if (level.isEmptyBlock(check.below())) continue;
                BlockState state = level.getBlockState(check);
                if (isValidGround(state.getBlock())) {
                    validBlocks++;
                }
            }
        }

        if (validBlocks < totalBlocks * 0.75) {
            return false;
        }

        return buildNest(level, base, random);
    }

    private boolean isValidGround(Block block) {
        return block == ModBlocks.WYVGRASS.get()
                || block == ModBlocks.SILVER_SAND.get()
                || block == Blocks.DIRT
                || block == Blocks.GRASS_BLOCK
                || block == Blocks.COARSE_DIRT
                || block == Blocks.PODZOL
                || block == Blocks.MYCELIUM
                || block == Blocks.SAND;
    }

    private boolean buildNest(WorldGenLevel level, BlockPos base, RandomSource random) {
        BlockState log = random.nextBoolean()
                ? ModBlocks.WYVWOOD_LOG.get().defaultBlockState()
                : Blocks.DARK_OAK_LOG.defaultBlockState();

        for (int i = 0; i < 8; i++) {
            level.setBlock(base.above(i), log, 2);
        }

        BlockState nest = ModBlocks.BLOCK_WYVERN_NEST.get().defaultBlockState();
        BlockPos top = base.above(8);

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq > 9) continue;
                level.setBlock(top.offset(dx, 0, dz), nest, 2);
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                int distSq = dx * dx + dz * dz;
                if (distSq < 10 || distSq > 13) continue;
                level.setBlock(top.offset(dx, 1, dz), nest, 2);
            }
        }

        BlockPos chestPos = top.above();
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH), 2);
        BlockEntity tile = level.getBlockEntity(chestPos);
        if (tile instanceof ChestBlockEntity chest) {
            ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.fromNamespaceAndPath("neomocreatures", "chests/wyvern_nest"));
            chest.setLootTable(lootTable, random.nextLong());
        }

        return true;
    }

    public static boolean forceSpawnNest(Level level, BlockPos pos) {
        WyvernNestFeature feature = new WyvernNestFeature(NoneFeatureConfiguration.CODEC);
        return feature.buildNest((WorldGenLevel) level, pos, level.getRandom());
    }
}