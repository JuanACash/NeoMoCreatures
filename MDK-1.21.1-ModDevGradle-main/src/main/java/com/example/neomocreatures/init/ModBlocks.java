package com.example.neomocreatures.init;

import com.example.neomocreatures.ExampleMod;
import com.example.neomocreatures.block.OgreLairGrassBlock;
import com.example.neomocreatures.block.OgreLairTallGrassBlock;
import com.example.neomocreatures.block.WyvGrassBlock;
import com.example.neomocreatures.block.WyvSaplingBlock;
import com.example.neomocreatures.block.WyvTallGrassBlock;
import com.example.neomocreatures.block.WyvernNestBlock;
import com.example.neomocreatures.block.OgreLairGrassBlock;
import com.example.neomocreatures.block.OgreLairTallGrassBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import com.example.neomocreatures.block.WyvernNestBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExampleMod.MODID);

    // ==== Piedra / cobble / mossy (dureza y resistencia tomadas del repo original) ====
    public static final DeferredBlock<Block> WYVSTONE = BLOCKS.registerSimpleBlock("wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(1.5F, 10.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> COBBLED_WYVSTONE = BLOCKS.registerSimpleBlock("cobbled_wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(2.0F, 10.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> DEEP_WYVSTONE = BLOCKS.registerSimpleBlock("deep_wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(3.0F, 10.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> COBBLED_DEEP_WYVSTONE = BLOCKS.registerSimpleBlock("cobbled_deep_wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(3.5F, 10.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> MOSSY_COBBLED_WYVSTONE = BLOCKS.registerSimpleBlock("mossy_cobbled_wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(1.5F, 10.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> MOSSY_COBBLED_DEEP_WYVSTONE = BLOCKS.registerSimpleBlock("mossy_cobbled_deep_wyvstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(1.5F, 10.0F).requiresCorrectToolForDrops());

    // ==== Tierra ====
    public static final DeferredBlock<Block> WYVDIRT = BLOCKS.registerSimpleBlock("wyvdirt",
            BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.GRAVEL).strength(0.6F));

    public static final DeferredBlock<WyvGrassBlock> WYVGRASS = BLOCKS.registerBlock("wyvgrass",
            props -> new WyvGrassBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).sound(SoundType.GRASS)
                    .strength(0.7F).randomTicks());

    public static final DeferredBlock<WyvTallGrassBlock> TALL_WYVGRASS = BLOCKS.registerBlock("tall_wyvgrass",
            props -> new WyvTallGrassBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                    .noCollission().instabreak().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<WyvSaplingBlock> WYVWOOD_SAPLING = BLOCKS.registerBlock("wyvwood_sapling",
            props -> new WyvSaplingBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                    .noCollission().randomTicks().instabreak().pushReaction(PushReaction.DESTROY));

    // ==== Madera ====
    public static final DeferredBlock<Block> WYVWOOD_LOG = BLOCKS.registerBlock("wyvwood_log",
            props -> new RotatedPillarBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOD)
                    .strength(2.0F).ignitedByLava());

    public static final DeferredBlock<LeavesBlock> WYVWOOD_LEAVES = BLOCKS.registerBlock("wyvwood_leaves",
            props -> new LeavesBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                    .strength(0.2F).randomTicks().noOcclusion().isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)
                    .ignitedByLava().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<Block> WYVWOOD_PLANKS = BLOCKS.registerSimpleBlock("wyvwood_planks",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).sound(SoundType.WOOD)
                    .strength(2.0F, 5.0F).ignitedByLava());

    // ==== Menas (dan XP al minarse, como vanilla) ====
    public static final DeferredBlock<net.minecraft.world.level.block.DropExperienceBlock> WYVERN_DIAMOND_ORE = BLOCKS.registerBlock("wyvern_diamond_ore",
            props -> new net.minecraft.world.level.block.DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(4, 8), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(4.5F, 5.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<net.minecraft.world.level.block.DropExperienceBlock> WYVERN_EMERALD_ORE = BLOCKS.registerBlock("wyvern_emerald_ore",
            props -> new net.minecraft.world.level.block.DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(4, 8), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(4.5F, 5.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<net.minecraft.world.level.block.DropExperienceBlock> WYVERN_LAPIS_ORE = BLOCKS.registerBlock("wyvern_lapis_ore",
            props -> new net.minecraft.world.level.block.DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(3, 6), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(1.5F, 5.0F).requiresCorrectToolForDrops());

    // Oro y hierro no dan XP directo al minar (igual que sus equivalentes vanilla)
    public static final DeferredBlock<Block> WYVERN_GOLD_ORE = BLOCKS.registerSimpleBlock("wyvern_gold_ore",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> WYVERN_IRON_ORE = BLOCKS.registerSimpleBlock("wyvern_iron_ore",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> WYVERN_ANCIENT_ORE = BLOCKS.registerSimpleBlock("wyvern_ancient_ore",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE)
                    .strength(3.0F, 5.0F).requiresCorrectToolForDrops());

    // ==== Ogre Lair block set ====
    public static final DeferredBlock<Block> DIRT_OGRE_LAIR = BLOCKS.registerSimpleBlock("dirt_ogre_lair",
            BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.GRAVEL).strength(0.6F));

        public static final DeferredBlock<OgreLairGrassBlock> GRASS_OGRE_LAIR = BLOCKS.registerBlock("grass_ogre_lair",
            props -> new OgreLairGrassBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.GRASS)
                    .strength(0.7F).randomTicks());

        public static final DeferredBlock<OgreLairTallGrassBlock> TALL_GRASS_OGRE_LAIR = BLOCKS.registerBlock("tall_grass_ogre_lair",
            props -> new OgreLairTallGrassBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                    .noCollission().instabreak().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<Block> LOG_OGRE_LAIR = BLOCKS.registerBlock("log_ogre_lair",
            props -> new RotatedPillarBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOD)
                    .strength(2.0F).ignitedByLava());

    public static final DeferredBlock<LeavesBlock> LEAVES_OGRE_LAIR = BLOCKS.registerBlock("leaves_ogre_lair",
            props -> new LeavesBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                    .strength(0.2F).randomTicks().noOcclusion().isValidSpawn((state, level, pos, type) -> false)
                    .isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)
                    .ignitedByLava().pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<Block> WOOD_PLANKS_OGRE_LAIR = BLOCKS.registerSimpleBlock("wood_planks_ogre_lair",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.WOOD)
                    .strength(2.0F, 5.0F).ignitedByLava());

    // ==== Ancient Silver / Silver Sandstone block set ====

   public static final DeferredBlock<Block> ANCIENT_SILVER_BLOCK = BLOCKS.register(
        "ancient_silver_block",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(3.0F, 10.0F))
        );

   public static final DeferredBlock<ColoredFallingBlock> SILVER_SAND = BLOCKS.register(
        "silver_sand",
        () -> new ColoredFallingBlock(
                new net.minecraft.util.ColorRGBA(0xC0C0C0FF),
                BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)
                .strength(0.6F, 0.6F)
        )
        );

    public static final DeferredBlock<Block> SILVER_SANDSTONE = BLOCKS.register(
        "silver_sandstone",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
                .strength(1.2F, 1.2F))
        );

    public static final DeferredBlock<Block> SILVER_SANDSTONE_CARVED = BLOCKS.register(
        "silver_sandstone_carved",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE)
                .strength(1.2F, 1.2F))
        );

    public static final DeferredBlock<Block> SILVER_SANDSTONE_SMOOTH = BLOCKS.register(
        "silver_sandstone_smooth",
        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_SANDSTONE)
                .strength(1.2F, 1.2F))
        );

    // ==== Piezas sueltas ====
    public static final DeferredBlock<Block> ORE_FIRESTONE = BLOCKS.registerSimpleBlock("ore_firestone",
            BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(SoundType.GLASS)
                    .strength(3.0F, 3.0F).lightLevel(state -> 7).requiresCorrectToolForDrops());

   public static final DeferredBlock<TransparentBlock> GLASS_GLEAMING = BLOCKS.registerBlock("glass_gleaming",
            props -> new TransparentBlock(props),
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS)
                    .strength(0.4F).lightLevel(state -> 7).noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false));

    public static final DeferredBlock<WyvernNestBlock> BLOCK_WYVERN_NEST = BLOCKS.registerBlock("block_wyvern_nest",
            props -> new WyvernNestBlock(props),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.GRASS).strength(0.5F, 0.5F));

}