package com.example.neomocreatures.init;

import com.example.neomocreatures.block.AppleFruitLeavesBlock;
import com.example.neomocreatures.block.AppleTreeGrower;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** The apple tree: sapling, plain leaves and leaves that bear apples, plus their block items. */
public final class AppleTreeBlocks {

    /** Same values vanilla uses for leaves. */
    private static final int LEAVES_FIRE_ENCOURAGEMENT = 30;
    private static final int LEAVES_FLAMMABILITY = 60;

    public static final DeferredBlock<SaplingBlock> SAPLING = ModBlocks.BLOCKS.registerBlock("apple_tree_sapling",
            props -> new SaplingBlock(AppleTreeGrower.APPLE, props),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));

    public static final DeferredBlock<AppleFruitLeavesBlock> LEAVES = ModBlocks.BLOCKS.registerBlock(
            "apple_leaves", AppleFruitLeavesBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));

    public static final DeferredItem<BlockItem> SAPLING_ITEM = ModItems.ITEMS.registerSimpleBlockItem("apple_tree_sapling", SAPLING);
    public static final DeferredItem<BlockItem> LEAVES_ITEM = ModItems.ITEMS.registerSimpleBlockItem("apple_leaves", LEAVES);

    private AppleTreeBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(SAPLING_ITEM.get());
        output.accept(LEAVES_ITEM.get());
    }

    public static void registerFlammability(FireBlock fire) {
        fire.setFlammable(LEAVES.get(), LEAVES_FIRE_ENCOURAGEMENT, LEAVES_FLAMMABILITY);
    }
}