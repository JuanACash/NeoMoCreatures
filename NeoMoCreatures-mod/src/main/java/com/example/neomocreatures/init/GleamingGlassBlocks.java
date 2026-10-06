package com.example.neomocreatures.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** Pane variant of the gleaming glass block. */
public final class GleamingGlassBlocks {

    private static final int GLEAMING_LIGHT_LEVEL = 7;

    public static final DeferredBlock<IronBarsBlock> PANE = ModBlocks.BLOCKS.registerBlock("glass_gleaming_pane",
            IronBarsBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE)
                    .strength(0.4F)
                    .lightLevel(state -> GLEAMING_LIGHT_LEVEL));

    public static final DeferredItem<BlockItem> PANE_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("glass_gleaming_pane", PANE);

    private GleamingGlassBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(PANE_ITEM.get());
    }
}