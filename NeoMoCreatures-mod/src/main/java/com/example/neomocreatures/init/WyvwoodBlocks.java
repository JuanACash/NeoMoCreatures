package com.example.neomocreatures.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** Decorative blocks crafted from wyvwood planks, plus their block items. */
public final class WyvwoodBlocks {

    private static final int BUTTON_PRESS_TICKS = 30;

    public static final DeferredBlock<DoorBlock> DOOR = ModBlocks.BLOCKS.registerBlock("wyvwood_door",
            props -> new DoorBlock(ModBlockSetTypes.WYVWOOD, props), woodProperties(Blocks.OAK_DOOR));

    public static final DeferredBlock<TrapDoorBlock> TRAPDOOR = ModBlocks.BLOCKS.registerBlock("wyvwood_trapdoor",
            props -> new TrapDoorBlock(ModBlockSetTypes.WYVWOOD, props), woodProperties(Blocks.OAK_TRAPDOOR));

    public static final DeferredBlock<ButtonBlock> BUTTON = ModBlocks.BLOCKS.registerBlock("wyvwood_button",
            props -> new ButtonBlock(ModBlockSetTypes.WYVWOOD, BUTTON_PRESS_TICKS, props),
            woodProperties(Blocks.OAK_BUTTON));

    public static final DeferredBlock<FenceBlock> FENCE = ModBlocks.BLOCKS.registerBlock("wyvwood_fence",
            FenceBlock::new, plankProperties(Blocks.OAK_FENCE));

    public static final DeferredBlock<FenceGateBlock> FENCE_GATE = ModBlocks.BLOCKS.registerBlock("wyvwood_fence_gate",
            props -> new FenceGateBlock(ModBlockSetTypes.WYVWOOD_WOOD, props), plankProperties(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<SlabBlock> SLAB = ModBlocks.BLOCKS.registerBlock("wyvwood_slab",
            SlabBlock::new, plankProperties(Blocks.OAK_SLAB));

    public static final DeferredBlock<StairBlock> STAIRS = ModBlocks.BLOCKS.registerBlock("wyvwood_stairs",
            props -> new StairBlock(ModBlocks.WYVWOOD_PLANKS.get().defaultBlockState(), props),
            plankProperties(Blocks.OAK_STAIRS));

    public static final DeferredItem<DoubleHighBlockItem> DOOR_ITEM = ModItems.ITEMS.register("wyvwood_door",
            () -> new DoubleHighBlockItem(DOOR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TRAPDOOR_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_trapdoor", TRAPDOOR);
    public static final DeferredItem<BlockItem> BUTTON_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_button", BUTTON);
    public static final DeferredItem<BlockItem> FENCE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_fence", FENCE);
    public static final DeferredItem<BlockItem> FENCE_GATE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_fence_gate", FENCE_GATE);
    public static final DeferredItem<BlockItem> SLAB_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_slab", SLAB);
    public static final DeferredItem<BlockItem> STAIRS_ITEM = ModItems.ITEMS.registerSimpleBlockItem("wyvwood_stairs", STAIRS);

    private WyvwoodBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(DOOR_ITEM.get());
        output.accept(TRAPDOOR_ITEM.get());
        output.accept(BUTTON_ITEM.get());
        output.accept(FENCE_ITEM.get());
        output.accept(FENCE_GATE_ITEM.get());
        output.accept(SLAB_ITEM.get());
        output.accept(STAIRS_ITEM.get());
    }

    /** Same flammability values vanilla uses for wooden slabs, stairs, fences and gates. */
    public static void registerFlammability(FireBlock fire) {
        fire.setFlammable(FENCE.get(), 5, 20);
        fire.setFlammable(FENCE_GATE.get(), 5, 20);
        fire.setFlammable(SLAB.get(), 5, 20);
        fire.setFlammable(STAIRS.get(), 5, 20);
    }

    private static BlockBehaviour.Properties woodProperties(Block base) {
        return BlockBehaviour.Properties.ofFullCopy(base).mapColor(MapColor.COLOR_CYAN);
    }

    /** Same hardness and resistance as the wyvwood planks. */
    private static BlockBehaviour.Properties plankProperties(Block base) {
        return woodProperties(base).strength(2.0F, 5.0F);
    }
}