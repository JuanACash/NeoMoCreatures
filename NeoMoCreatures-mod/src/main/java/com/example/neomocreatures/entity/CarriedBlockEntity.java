package com.example.neomocreatures.entity;

import net.minecraft.world.level.block.state.BlockState;

/** An entity that looks like a single block (thrown and summoned golem rocks) — lets them share one renderer. */
public interface CarriedBlockEntity {

    BlockState getBlockState();
}