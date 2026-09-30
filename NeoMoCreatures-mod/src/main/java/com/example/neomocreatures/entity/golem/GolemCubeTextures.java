package com.example.neomocreatures.entity.golem;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

/**
 * Port of {@code MoCEntityGolem.translateOre()}: picks which of the 28 cube textures in golem.png a
 * block is drawn with. Unlike the original (which rejected — and lost — any block outside its list),
 * every block gets the closest look, falling back to stone; the golem still remembers the real block.
 */
public final class GolemCubeTextures {

    public static final int STONE = 0;
    public static final int DIRT = 1;
    public static final int COBBLESTONE = 2;
    public static final int PLANKS = 3;
    public static final int SAND = 4;
    public static final int GRAVEL = 5;
    public static final int LOG = 6;
    public static final int GOLD = 7;
    public static final int GLASS = 8;
    public static final int WOOL = 9;
    public static final int LEAVES = 10;
    public static final int IRON = 11;
    public static final int BRICKS = 12;
    public static final int SANDSTONE = 13;
    public static final int OBSIDIAN = 14;
    public static final int DIAMOND = 15;
    public static final int CRAFTING_TABLE = 16;
    public static final int FURNACE = 17;
    public static final int ICE = 18;
    public static final int CACTUS = 19;
    public static final int CLAY = 20;
    public static final int EMERALD = 21;
    public static final int PUMPKIN = 22;
    public static final int NETHERRACK = 23;
    public static final int DIAMOND_ORE = 24;
    public static final int GLOWSTONE = 25;
    public static final int STONE_BRICKS = 26;
    public static final int NETHER_BRICKS = 27;

    private record Rule(Predicate<BlockState> matches, int texture) {
    }

    /** Checked in order — the first match wins, so exact blocks come before the broader tags. */
    private static final List<Rule> RULES = List.of(
            new Rule(anyOf(Blocks.GOLD_BLOCK), GOLD),
            new Rule(anyOf(Blocks.IRON_BLOCK), IRON),
            new Rule(anyOf(Blocks.DIAMOND_BLOCK), DIAMOND),
            new Rule(anyOf(Blocks.EMERALD_BLOCK), EMERALD),
            new Rule(state -> state.is(BlockTags.GOLD_ORES), GOLD),
            new Rule(state -> state.is(BlockTags.IRON_ORES), IRON),
            new Rule(state -> state.is(BlockTags.DIAMOND_ORES), DIAMOND_ORE),
            new Rule(state -> state.is(BlockTags.EMERALD_ORES), EMERALD),
            new Rule(anyOf(Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLED_DEEPSLATE), COBBLESTONE),
            new Rule(anyOf(Blocks.GRAVEL), GRAVEL),
            new Rule(anyOf(Blocks.BRICKS), BRICKS),
            new Rule(anyOf(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN), OBSIDIAN),
            new Rule(anyOf(Blocks.CRAFTING_TABLE), CRAFTING_TABLE),
            new Rule(anyOf(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER), FURNACE),
            new Rule(anyOf(Blocks.CACTUS), CACTUS),
            new Rule(anyOf(Blocks.CLAY), CLAY),
            new Rule(anyOf(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.MELON), PUMPKIN),
            new Rule(anyOf(Blocks.NETHERRACK), NETHERRACK),
            new Rule(anyOf(Blocks.GLOWSTONE), GLOWSTONE),
            new Rule(anyOf(Blocks.NETHER_BRICKS), NETHER_BRICKS),
            new Rule(state -> state.is(BlockTags.STONE_BRICKS), STONE_BRICKS),
            new Rule(state -> state.is(BlockTags.DIRT), DIRT),
            new Rule(state -> state.is(BlockTags.PLANKS), PLANKS),
            new Rule(state -> state.is(Tags.Blocks.SANDSTONE_BLOCKS), SANDSTONE),
            new Rule(state -> state.is(BlockTags.SAND), SAND),
            new Rule(state -> state.is(BlockTags.LOGS), LOG),
            new Rule(state -> state.is(Tags.Blocks.GLASS_BLOCKS), GLASS),
            new Rule(state -> state.is(BlockTags.WOOL), WOOL),
            new Rule(state -> state.is(BlockTags.LEAVES), LEAVES),
            new Rule(state -> state.is(BlockTags.ICE), ICE));

    private GolemCubeTextures() {
    }

    public static int textureOf(BlockState state) {
        for (Rule rule : RULES) {
            if (rule.matches().test(state)) {
                return rule.texture();
            }
        }
        return STONE;
    }

    private static Predicate<BlockState> anyOf(Block... blocks) {
        return state -> {
            for (Block block : blocks) {
                if (state.is(block)) {
                    return true;
                }
            }
            return false;
        };
    }
}