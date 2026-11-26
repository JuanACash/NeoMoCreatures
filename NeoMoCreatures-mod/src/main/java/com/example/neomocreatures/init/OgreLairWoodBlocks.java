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
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** Decorative blocks crafted from Ogre Lair planks, plus their block items. */
public final class OgreLairWoodBlocks {

    private static final int BUTTON_PRESS_TICKS = 30;

    public static final DeferredBlock<DoorBlock> DOOR = ModBlocks.BLOCKS.registerBlock("door_ogre_lair",
            props -> new DoorBlock(ModBlockSetTypes.OGRE_LAIR, props), woodProperties(Blocks.OAK_DOOR));

    public static final DeferredBlock<TrapDoorBlock> TRAPDOOR = ModBlocks.BLOCKS.registerBlock("trapdoor_ogre_lair",
            props -> new TrapDoorBlock(ModBlockSetTypes.OGRE_LAIR, props), woodProperties(Blocks.OAK_TRAPDOOR));

    public static final DeferredBlock<ButtonBlock> BUTTON = ModBlocks.BLOCKS.registerBlock("button_ogre_lair",
            props -> new ButtonBlock(ModBlockSetTypes.OGRE_LAIR, BUTTON_PRESS_TICKS, props),
            woodProperties(Blocks.OAK_BUTTON));

    public static final DeferredBlock<PressurePlateBlock> PRESSURE_PLATE = ModBlocks.BLOCKS.registerBlock(
            "pressure_plate_ogre_lair",
            props -> new PressurePlateBlock(ModBlockSetTypes.OGRE_LAIR, props),
            woodProperties(Blocks.OAK_PRESSURE_PLATE));

    public static final DeferredBlock<FenceBlock> FENCE = ModBlocks.BLOCKS.registerBlock("fence_ogre_lair",
            FenceBlock::new, plankProperties(Blocks.OAK_FENCE));

    public static final DeferredBlock<FenceGateBlock> FENCE_GATE = ModBlocks.BLOCKS.registerBlock("fence_gate_ogre_lair",
            props -> new FenceGateBlock(ModBlockSetTypes.OGRE_LAIR_WOOD, props), plankProperties(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<SlabBlock> SLAB = ModBlocks.BLOCKS.registerBlock("slab_ogre_lair",
            SlabBlock::new, plankProperties(Blocks.OAK_SLAB));

    public static final DeferredBlock<StairBlock> STAIRS = ModBlocks.BLOCKS.registerBlock("stairs_ogre_lair",
            props -> new StairBlock(ModBlocks.WOOD_PLANKS_OGRE_LAIR.get().defaultBlockState(), props),
            plankProperties(Blocks.OAK_STAIRS));

    public static final DeferredItem<DoubleHighBlockItem> DOOR_ITEM = ModItems.ITEMS.register("door_ogre_lair",
            () -> new DoubleHighBlockItem(DOOR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> TRAPDOOR_ITEM = ModItems.ITEMS.registerSimpleBlockItem("trapdoor_ogre_lair", TRAPDOOR);
    public static final DeferredItem<BlockItem> BUTTON_ITEM = ModItems.ITEMS.registerSimpleBlockItem("button_ogre_lair", BUTTON);
    public static final DeferredItem<BlockItem> PRESSURE_PLATE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("pressure_plate_ogre_lair", PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> FENCE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("fence_ogre_lair", FENCE);
    public static final DeferredItem<BlockItem> FENCE_GATE_ITEM = ModItems.ITEMS.registerSimpleBlockItem("fence_gate_ogre_lair", FENCE_GATE);
    public static final DeferredItem<BlockItem> SLAB_ITEM = ModItems.ITEMS.registerSimpleBlockItem("slab_ogre_lair", SLAB);
    public static final DeferredItem<BlockItem> STAIRS_ITEM = ModItems.ITEMS.registerSimpleBlockItem("stairs_ogre_lair", STAIRS);

    private OgreLairWoodBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(DOOR_ITEM.get());
        output.accept(TRAPDOOR_ITEM.get());
        output.accept(BUTTON_ITEM.get());
        output.accept(PRESSURE_PLATE_ITEM.get());
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
        return BlockBehaviour.Properties.ofFullCopy(base).mapColor(MapColor.COLOR_PURPLE);
    }

    /** Same hardness and resistance as the Ogre Lair planks. */
    private static BlockBehaviour.Properties plankProperties(Block base) {
        return woodProperties(base).strength(2.0F, 5.0F);
    }
}