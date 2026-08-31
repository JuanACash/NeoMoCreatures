package com.example.neomocreatures.block;

import com.example.neomocreatures.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Reimplementacion simplificada del comportamiento de GrassBlock de vanilla,
 * pero reconociendo WYVDIRT en vez de Blocks.DIRT (que esta hardcodeado en
 * la clase vanilla y no se puede extender de forma limpia para un dirt custom).
 */
public class WyvGrassBlock extends Block {

    public WyvGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Si esta tapado por un bloque solido, se revierte a tierra (igual que vanilla).
        BlockPos above = pos.above();
        if (level.getBlockState(above).isSolidRender(level, above)) {
            level.setBlockAndUpdate(pos, ModBlocks.WYVDIRT.get().defaultBlockState());
            return;
        }

        // Intenta esparcirse a un WYVDIRT vecino con suficiente luz encima.
        if (random.nextInt(4) != 0) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            if (level.getBlockState(target).is(ModBlocks.WYVDIRT.get())
                    && level.getBlockState(target.above()).getLightBlock(level, target.above()) < 4) {
                level.setBlockAndUpdate(target, this.defaultBlockState());
            }
        }
    }
}