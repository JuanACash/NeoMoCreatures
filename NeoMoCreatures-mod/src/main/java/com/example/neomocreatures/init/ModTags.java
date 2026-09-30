package com.example.neomocreatures.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class ModTags {

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(com.example.neomocreatures.NeoMoCreatures.MODID, name));
    }

    public static final TagKey<Item> REPAIRS_SCORP_CAVE = tag("repairs_scorp_cave_armor");
    public static final TagKey<Item> REPAIRS_SCORP_DIRT = tag("repairs_scorp_dirt_armor");
    public static final TagKey<Item> REPAIRS_SCORP_NETHER = tag("repairs_scorp_nether_armor");
    public static final TagKey<Item> REPAIRS_SCORP_FROST = tag("repairs_scorp_frost_armor");
    public static final TagKey<Item> REPAIRS_SCORP_UNDEAD = tag("repairs_scorp_undead_armor");
    public static final TagKey<Item> REPAIRS_SILVER = tag("repairs_silver_armor");
    public static final TagKey<Item> REPAIRS_FUR = tag("repairs_fur_armor");
    public static final TagKey<Item> REPAIRS_REPTILE = tag("repairs_reptile_armor");
    public static final TagKey<Item> REPAIRS_HIDE = tag("repairs_hide_armor");
    public static final TagKey<Item> RAW_FISHES = tag("raw_fishes");
    public static final TagKey<Item> COOKED_FISHES = tag("cooked_fishes");
    /** Items the Filch Lizard steals from players and picks up off the ground. */
    public static final TagKey<Item> FILCH_LIZARD_STEALS = tag("filch_lizard_steals");

    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(com.example.neomocreatures.NeoMoCreatures.MODID, name));
    }

    /** Blocks no golem may tear out of the world, on top of the built-in safety rules in GolemBlockPicker. */
    public static final TagKey<Block> GOLEM_CANNOT_LIFT = blockTag("golem_cannot_lift");
    
    /** Ground bears can spawn on besides grass (#minecraft:animals_spawnable_on): ice and snow blocks for polar bears. */
    public static final TagKey<Block> BEAR_SPAWNABLE_ON = blockTag("bear_spawnable_on");
}