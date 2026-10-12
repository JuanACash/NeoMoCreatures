package com.example.neomocreatures.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** The slab, stairs and wall variants of one base block, with their block items. */
public record StoneFamily(
        DeferredBlock<SlabBlock> slab,
        DeferredBlock<StairBlock> stairs,
        DeferredBlock<WallBlock> wall,
        DeferredItem<BlockItem> slabItem,
        DeferredItem<BlockItem> stairsItem,
        DeferredItem<BlockItem> wallItem) {

    public void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(slabItem.get());
        output.accept(stairsItem.get());
        output.accept(wallItem.get());
    }
}