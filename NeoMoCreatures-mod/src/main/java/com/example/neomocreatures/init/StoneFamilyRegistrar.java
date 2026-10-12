package com.example.neomocreatures.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Registers the slab, stairs and wall of a base block, reusable for any stone-like family. */
public final class StoneFamilyRegistrar {

    private StoneFamilyRegistrar() {
    }

    public static StoneFamily register(String baseName, DeferredBlock<? extends Block> base,
                                       MapColor color, float hardness, float resistance) {
        DeferredBlock<SlabBlock> slab = ModBlocks.BLOCKS.registerBlock(baseName + "_slab",
                SlabBlock::new, properties(Blocks.STONE_SLAB, color, hardness, resistance));

        DeferredBlock<StairBlock> stairs = ModBlocks.BLOCKS.registerBlock(baseName + "_stairs",
                props -> new StairBlock(base.get().defaultBlockState(), props),
                properties(Blocks.STONE_STAIRS, color, hardness, resistance));

        DeferredBlock<WallBlock> wall = ModBlocks.BLOCKS.registerBlock(baseName + "_wall",
                WallBlock::new, properties(Blocks.COBBLESTONE_WALL, color, hardness, resistance));

        return new StoneFamily(slab, stairs, wall,
                ModItems.ITEMS.registerSimpleBlockItem(baseName + "_slab", slab),
                ModItems.ITEMS.registerSimpleBlockItem(baseName + "_stairs", stairs),
                ModItems.ITEMS.registerSimpleBlockItem(baseName + "_wall", wall));
    }

    private static BlockBehaviour.Properties properties(Block vanilla, MapColor color,
                                                        float hardness, float resistance) {
        return BlockBehaviour.Properties.ofFullCopy(vanilla).mapColor(color).strength(hardness, resistance);
    }
}