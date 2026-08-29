package com.example.examplemod.block;

import com.example.examplemod.init.ModBlocks;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WyvTallGrassBlock extends BushBlock {

    public static final MapCodec<WyvTallGrassBlock> CODEC = simpleCodec(WyvTallGrassBlock::new);

    public WyvTallGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.WYVGRASS.get()) || state.is(ModBlocks.WYVDIRT.get());
    }
}