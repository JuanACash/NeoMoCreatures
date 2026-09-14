package com.example.neomocreatures.block;

import com.example.neomocreatures.init.ModBlocks;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class OgreLairTallGrassBlock extends BushBlock {

    public static final MapCodec<OgreLairTallGrassBlock> CODEC = simpleCodec(OgreLairTallGrassBlock::new);

    public OgreLairTallGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlocks.GRASS_OGRE_LAIR.get()) || state.is(ModBlocks.DIRT_OGRE_LAIR.get());
    }
}