package com.example.neomocreatures.init;

import java.util.List;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** Slabs, stairs, walls, pressure plates and buttons made from the wyvern stones. */
public final class WyvstoneBlocks {

    private static final int STONE_BUTTON_PRESS_TICKS = 20;

    public static final StoneFamily WYVSTONE = StoneFamilyRegistrar.register(
            "wyvstone", ModBlocks.WYVSTONE, MapColor.STONE, 1.5F, 10.0F);
    public static final StoneFamily COBBLED_WYVSTONE = StoneFamilyRegistrar.register(
            "cobbled_wyvstone", ModBlocks.COBBLED_WYVSTONE, MapColor.STONE, 2.0F, 10.0F);
    public static final StoneFamily DEEP_WYVSTONE = StoneFamilyRegistrar.register(
            "deep_wyvstone", ModBlocks.DEEP_WYVSTONE, MapColor.STONE, 3.0F, 10.0F);
    public static final StoneFamily COBBLED_DEEP_WYVSTONE = StoneFamilyRegistrar.register(
            "cobbled_deep_wyvstone", ModBlocks.COBBLED_DEEP_WYVSTONE, MapColor.STONE, 3.5F, 10.0F);
    public static final StoneFamily MOSSY_COBBLED_WYVSTONE = StoneFamilyRegistrar.register(
            "mossy_cobbled_wyvstone", ModBlocks.MOSSY_COBBLED_WYVSTONE, MapColor.STONE, 1.5F, 10.0F);
    public static final StoneFamily MOSSY_COBBLED_DEEP_WYVSTONE = StoneFamilyRegistrar.register(
            "mossy_cobbled_deep_wyvstone", ModBlocks.MOSSY_COBBLED_DEEP_WYVSTONE, MapColor.STONE, 1.5F, 10.0F);

    public static final StoneSwitches WYVSTONE_SWITCHES = registerSwitches("wyvstone");
    public static final StoneSwitches DEEP_WYVSTONE_SWITCHES = registerSwitches("deep_wyvstone");

    private static final List<StoneFamily> FAMILIES = List.of(
            WYVSTONE, COBBLED_WYVSTONE, DEEP_WYVSTONE,
            COBBLED_DEEP_WYVSTONE, MOSSY_COBBLED_WYVSTONE, MOSSY_COBBLED_DEEP_WYVSTONE);

    /** The pressure plate and button of one base stone, with their block items. */
    public record StoneSwitches(
            DeferredBlock<PressurePlateBlock> pressurePlate,
            DeferredBlock<ButtonBlock> button,
            DeferredItem<BlockItem> pressurePlateItem,
            DeferredItem<BlockItem> buttonItem) {

        void addToCreativeTab(CreativeModeTab.Output output) {
            output.accept(pressurePlateItem.get());
            output.accept(buttonItem.get());
        }
    }

    private WyvstoneBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        FAMILIES.forEach(family -> family.addToCreativeTab(output));
        WYVSTONE_SWITCHES.addToCreativeTab(output);
        DEEP_WYVSTONE_SWITCHES.addToCreativeTab(output);
    }

    private static StoneSwitches registerSwitches(String baseName) {
        DeferredBlock<PressurePlateBlock> plate = ModBlocks.BLOCKS.registerBlock(baseName + "_pressure_plate",
                props -> new PressurePlateBlock(BlockSetType.STONE, props),
                switchProperties(Blocks.STONE_PRESSURE_PLATE));

        DeferredBlock<ButtonBlock> button = ModBlocks.BLOCKS.registerBlock(baseName + "_button",
                props -> new ButtonBlock(BlockSetType.STONE, STONE_BUTTON_PRESS_TICKS, props),
                switchProperties(Blocks.STONE_BUTTON));

        return new StoneSwitches(plate, button,
                ModItems.ITEMS.registerSimpleBlockItem(baseName + "_pressure_plate", plate),
                ModItems.ITEMS.registerSimpleBlockItem(baseName + "_button", button));
    }

    private static BlockBehaviour.Properties switchProperties(Block vanilla) {
        return BlockBehaviour.Properties.ofFullCopy(vanilla).mapColor(MapColor.STONE);
    }
}