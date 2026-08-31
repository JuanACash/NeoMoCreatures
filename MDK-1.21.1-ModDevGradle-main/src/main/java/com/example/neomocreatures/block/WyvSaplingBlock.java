package com.example.neomocreatures.block;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WyvSaplingBlock extends SaplingBlock {

    public WyvSaplingBlock(BlockBehaviour.Properties properties) {
        super(WyvwoodTreeGrower.WYVWOOD, properties);
    }
}