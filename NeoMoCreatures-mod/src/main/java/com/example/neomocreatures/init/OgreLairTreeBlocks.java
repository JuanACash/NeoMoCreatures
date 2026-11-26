package com.example.neomocreatures.init;

import com.example.neomocreatures.block.OgreLairSaplingBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** The Ogre Lair tree sapling and its block item. */
public final class OgreLairTreeBlocks {

    public static final DeferredBlock<OgreLairSaplingBlock> SAPLING = ModBlocks.BLOCKS.registerBlock("sapling_ogre_lair",
            OgreLairSaplingBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));

    public static final DeferredItem<BlockItem> SAPLING_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("sapling_ogre_lair", SAPLING);

    private OgreLairTreeBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        output.accept(SAPLING_ITEM.get());
    }
}