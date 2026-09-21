package com.example.neomocreatures.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

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
}