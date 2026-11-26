package com.example.neomocreatures.block;

import com.example.neomocreatures.init.ModDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Sapling that grows without light inside the Ogre Lair and behaves like a vanilla sapling elsewhere. */
public class OgreLairSaplingBlock extends SaplingBlock {

    /** Same odds as vanilla: one growth attempt every 7 random ticks on average. */
    private static final int GROWTH_CHANCE = 7;

    public OgreLairSaplingBlock(BlockBehaviour.Properties properties) {
        super(OgreLairTreeGrower.OGRE_LAIR, properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.dimension() == ModDimensions.OGRE_LAIR) {
            if (random.nextInt(GROWTH_CHANCE) == 0) {
                advanceTree(level, pos, state, random);
            }
            return;
        }
        super.randomTick(state, level, pos, random);
    }
}