package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {

    private static TagKey<Item> tag(String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, name));
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
    /** Cooked meat that tames black and grizzly bears. */
    public static final TagKey<Item> BEAR_TAMING_MEAT = tag("bear_taming_meat");
    /** Cooked fish/seafood that tames polar bears. */
    public static final TagKey<Item> POLAR_BEAR_TAMING_MEAT = tag("polar_bear_taming_meat");
    /** Any meat (raw or cooked) a tamed bear can be healed with. */
    public static final TagKey<Item> BEAR_HEALING_MEAT = tag("bear_healing_meat");
    /** Food big cats eat, are healed with and pick up from the ground. */
    public static final TagKey<Item> BIG_CAT_FOOD = tag("big_cat_food");
    /** Food that puts two big cats in love mode. */
    public static final TagKey<Item> BIG_CAT_BREEDING_FOOD = tag("big_cat_breeding_food");
    /** Food a tamed manticore is healed with. */
    public static final TagKey<Item> MANTICORE_FOOD = tag("manticore_food");
    /** Saddles any rideable mod creature accepts (vanilla saddle and the mod's horse saddle). */
    public static final TagKey<Item> MOUNT_SADDLES = tag("mount_saddles");
    /** Scrolls that act on a pet when used on it, so the click must never mount it instead. */
    public static final TagKey<Item> ENTITY_SCROLLS = tag("entity_scrolls");
    public static final TagKey<Item> ELEPHANT_TAMING_FOOD = tag("elephant_taming_food");
    public static final TagKey<Item> ELEPHANT_HEALING_FOOD = tag("elephant_healing_food");
    public static final TagKey<Item> OSTRICH_HEALING_FOOD = tag("ostrich_healing_food");
    public static final TagKey<Item> WYVERN_HEALING_FOOD = tag("wyvern_healing_food");
    public static final TagKey<Item> SCORPION_HEALING_FOOD = tag("scorpion_healing_food");
    public static final TagKey<Item> KOMODO_DRAGON_HEALING_FOOD = tag("komodo_dragon_healing_food");
    public static final TagKey<Item> FOX_HEALING_FOOD = tag("fox_healing_food");
    /** Seeds birds eat (healing, pre-taming); the same set as the vanilla parrot used to accept. */
    public static final TagKey<Item> BIRD_SEEDS = tag("bird_seeds");

    private static TagKey<Block> blockTag(String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, name));
    }

    /** Blocks no golem may tear out of the world, on top of the built-in safety rules in GolemBlockPicker. */
    public static final TagKey<Block> GOLEM_CANNOT_LIFT = blockTag("golem_cannot_lift");
    
    /** Ground bears can spawn on besides grass (#minecraft:animals_spawnable_on): ice and snow blocks for polar bears. */
    public static final TagKey<Block> BEAR_SPAWNABLE_ON = blockTag("bear_spawnable_on");

    private static TagKey<EntityType<?>> entityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, name));
    }

    /** Large animals that wild big cats and bears never hunt (each other, elephants, pandas...). */
    public static final TagKey<EntityType<?>> NOT_HUNTED_BY_PREDATORS = entityTag("not_hunted_by_predators");

    private static TagKey<Biome> biomeTag(String name) {
        return TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, name));
    }

    // Biomes that decide which variant a creature spawns as (data/neomocreatures/tags/worldgen/biome/variants/).
    // Each holds the vanilla biomes plus optional modded ones, so it works with or without biome mods.
    public static final TagKey<Biome> BEAR_POLAR_BIOMES = biomeTag("variants/bear_polar");
    public static final TagKey<Biome> BEAR_PANDA_BIOMES = biomeTag("variants/bear_panda");
    public static final TagKey<Biome> BEAR_GRIZZLY_BIOMES = biomeTag("variants/bear_grizzly");
    public static final TagKey<Biome> BEAR_BLACK_BIOMES = biomeTag("variants/bear_black");
    public static final TagKey<Biome> BEAR_BLACK_OR_GRIZZLY_BIOMES = biomeTag("variants/bear_black_or_grizzly");
    public static final TagKey<Biome> BIGCAT_SNOW_LEOPARD_BIOMES = biomeTag("variants/bigcat_snow_leopard");
    public static final TagKey<Biome> BIGCAT_JUNGLE_BIOMES = biomeTag("variants/bigcat_jungle");
    public static final TagKey<Biome> BIGCAT_FOREST_BIOMES = biomeTag("variants/bigcat_forest");
    public static final TagKey<Biome> BIGCAT_LION_BIOMES = biomeTag("variants/bigcat_lion");
    public static final TagKey<Biome> ELEPHANT_ASIAN_BIOMES = biomeTag("variants/elephant_asian");
    public static final TagKey<Biome> ELEPHANT_AFRICAN_BIOMES = biomeTag("variants/elephant_african");
    public static final TagKey<Biome> ELEPHANT_MAMMOTH_BIOMES = biomeTag("variants/elephant_mammoth");
    public static final TagKey<Biome> HORSE_TIER1_BIOMES = biomeTag("variants/horse_tier1");
    public static final TagKey<Biome> HORSE_ZEBRA_BIOMES = biomeTag("variants/horse_zebra");
    public static final TagKey<Biome> HORSE_DONKEY_BIOMES = biomeTag("variants/horse_donkey");
    public static final TagKey<Biome> SNAKE_RATTLE_OR_WOLF_BIOMES = biomeTag("variants/snake_rattle_or_wolf");
    public static final TagKey<Biome> SNAKE_COBRA_BIOMES = biomeTag("variants/snake_cobra");
    public static final TagKey<Biome> SNAKE_PYTHON_BIOMES = biomeTag("variants/snake_python");
    public static final TagKey<Biome> SNAKE_JUNGLE_BIOMES = biomeTag("variants/snake_jungle");
    public static final TagKey<Biome> SNAKE_CORAL_BIOMES = biomeTag("variants/snake_coral");
    public static final TagKey<Biome> SNAKE_ORANGE_BIOMES = biomeTag("variants/snake_orange");
    public static final TagKey<Biome> FOX_SNOW_BIOMES = biomeTag("variants/fox_snow");
    public static final TagKey<Biome> FOX_NORMAL_BIOMES = biomeTag("variants/fox_normal");
    public static final TagKey<Biome> BUNNY_WHITE_BIOMES = biomeTag("variants/bunny_white");
    public static final TagKey<Biome> ENT_OAK_BIOMES = biomeTag("variants/ent_oak");
    public static final TagKey<Biome> ENT_BIRCH_BIOMES = biomeTag("variants/ent_birch");
    public static final TagKey<Biome> FROST_VARIANT_BIOMES = biomeTag("variants/frost_variant");
    public static final TagKey<Biome> FIRE_VARIANT_BIOMES = biomeTag("variants/fire_variant");
    public static final TagKey<Biome> SMALL_FISH_ANCHOVY_BIOMES = biomeTag("variants/small_fish_anchovy");
    public static final TagKey<Biome> SMALL_FISH_ANGELFISH_BIOMES = biomeTag("variants/small_fish_angelfish");
    public static final TagKey<Biome> SMALL_FISH_ANGLER_BIOMES = biomeTag("variants/small_fish_angler");
    public static final TagKey<Biome> SMALL_FISH_CLOWNFISH_BIOMES = biomeTag("variants/small_fish_clownfish");
    public static final TagKey<Biome> SMALL_FISH_GOLDFISH_BIOMES = biomeTag("variants/small_fish_goldfish");
    public static final TagKey<Biome> SMALL_FISH_HIPPOTANG_BIOMES = biomeTag("variants/small_fish_hippotang");
    public static final TagKey<Biome> SMALL_FISH_MANDARIN_BIOMES = biomeTag("variants/small_fish_mandarin");
    public static final TagKey<Biome> SMALL_FISH_PIRANHA_BIOMES = biomeTag("variants/small_fish_piranha");

}
