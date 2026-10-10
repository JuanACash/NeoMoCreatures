package com.example.neomocreatures.config;

import java.util.LinkedHashMap;
import java.util.Map;

import net.neoforged.neoforge.common.ModConfigSpec;

/** One spawn weight per biome modifier, so every creature and every variant can be tuned on its own. */
public final class SpawnConfig {

    private static final int MAX_WEIGHT = 200;
    private static final Map<String, Integer> DEFAULT_WEIGHTS = new LinkedHashMap<>();

    static {
        DEFAULT_WEIGHTS.put("ant_spawns", 15);
        DEFAULT_WEIGHTS.put("bass_spawns", 11);
        DEFAULT_WEIGHTS.put("bear_spawns_forest", 24);
        DEFAULT_WEIGHTS.put("bear_spawns_panda", 20);
        DEFAULT_WEIGHTS.put("bear_spawns_polar", 12);
        DEFAULT_WEIGHTS.put("bee_spawns", 15);
        DEFAULT_WEIGHTS.put("bigcat_spawns_forest", 24);
        DEFAULT_WEIGHTS.put("bigcat_spawns_jungle", 38);
        DEFAULT_WEIGHTS.put("bigcat_spawns_savanna", 12);
        DEFAULT_WEIGHTS.put("bigcat_spawns_snow_leopard", 14);
        DEFAULT_WEIGHTS.put("bird_spawns", 15);
        DEFAULT_WEIGHTS.put("boar_spawns", 10);
        DEFAULT_WEIGHTS.put("bunny_spawns_overworld", 10);
        DEFAULT_WEIGHTS.put("bunny_spawns_wyvernlair", 3);
        DEFAULT_WEIGHTS.put("butterfly_spawns", 15);
        DEFAULT_WEIGHTS.put("cod_spawns", 10);
        DEFAULT_WEIGHTS.put("crab_spawns", 11);
        DEFAULT_WEIGHTS.put("cricket_spawns", 15);
        DEFAULT_WEIGHTS.put("crocodile_spawns", 8);
        DEFAULT_WEIGHTS.put("deer_spawns", 10);
        DEFAULT_WEIGHTS.put("dolphin_spawns", 10);
        DEFAULT_WEIGHTS.put("dragonfly_spawns", 15);
        DEFAULT_WEIGHTS.put("duck_spawns", 15);
        DEFAULT_WEIGHTS.put("elephant_spawns_african", 12);
        DEFAULT_WEIGHTS.put("elephant_spawns_asian", 28);
        DEFAULT_WEIGHTS.put("elephant_spawns_mammoth", 6);
        DEFAULT_WEIGHTS.put("ent_spawns", 20);
        DEFAULT_WEIGHTS.put("firefly_spawns", 15);
        DEFAULT_WEIGHTS.put("fishy_spawns", 12);
        DEFAULT_WEIGHTS.put("fishy_spawns_ocean", 12);
        DEFAULT_WEIGHTS.put("fly_spawns", 15);
        DEFAULT_WEIGHTS.put("fox_spawns_normal", 24);
        DEFAULT_WEIGHTS.put("fox_spawns_snow", 24);
        DEFAULT_WEIGHTS.put("goat_spawns", 10);
        DEFAULT_WEIGHTS.put("big_golem_spawns", 70);
        DEFAULT_WEIGHTS.put("mini_golem_spawns", 95);
        DEFAULT_WEIGHTS.put("grasshopper_spawns", 15);
        DEFAULT_WEIGHTS.put("hellrat_spawns", 35);
        DEFAULT_WEIGHTS.put("horse_spawns_donkey", 6);
        DEFAULT_WEIGHTS.put("horse_spawns_tier1", 6);
        DEFAULT_WEIGHTS.put("horse_spawns_zebra", 12);
        DEFAULT_WEIGHTS.put("horsemob_spawns_nether", 40);
        DEFAULT_WEIGHTS.put("horsemob_spawns_overworld", 100);
        DEFAULT_WEIGHTS.put("jellyfish_spawns", 14);
        DEFAULT_WEIGHTS.put("komodo_dragon_spawns", 15);
        DEFAULT_WEIGHTS.put("maggot_spawns", 15);
        DEFAULT_WEIGHTS.put("manta_ray_spawns", 10);
        DEFAULT_WEIGHTS.put("manticore_spawns_nether", 40);
        DEFAULT_WEIGHTS.put("manticore_spawns_overworld", 90);
        DEFAULT_WEIGHTS.put("mole_spawns", 8);
        DEFAULT_WEIGHTS.put("mouse_spawns", 15);
        DEFAULT_WEIGHTS.put("cave_ogre_spawns", 80);
        DEFAULT_WEIGHTS.put("fire_ogre_spawns", 80);
        DEFAULT_WEIGHTS.put("fire_ogre_spawns_nether", 40);
        DEFAULT_WEIGHTS.put("green_ogre_spawns", 80);
        DEFAULT_WEIGHTS.put("ostrich_spawns", 12);
        DEFAULT_WEIGHTS.put("raccoon_spawns", 10);
        DEFAULT_WEIGHTS.put("rat_spawns", 100);
        DEFAULT_WEIGHTS.put("roach_spawns", 15);
        DEFAULT_WEIGHTS.put("salmon_spawns", 10);
        DEFAULT_WEIGHTS.put("scorpion_spawns_nether", 40);
        DEFAULT_WEIGHTS.put("scorpion_spawns_overworld", 100);
        DEFAULT_WEIGHTS.put("shark_spawns", 5);
        DEFAULT_WEIGHTS.put("silver_skeleton_spawns", 100);
        DEFAULT_WEIGHTS.put("small_fish_spawns", 17);
        DEFAULT_WEIGHTS.put("snail_spawns", 15);
        DEFAULT_WEIGHTS.put("snake_spawns_desert_badlands", 14);
        DEFAULT_WEIGHTS.put("snake_spawns_everywhere", 6);
        DEFAULT_WEIGHTS.put("snake_spawns_forest", 14);
        DEFAULT_WEIGHTS.put("snake_spawns_jungle_savanna", 14);
        DEFAULT_WEIGHTS.put("snake_spawns_plains", 10);
        DEFAULT_WEIGHTS.put("stingray_spawns", 11);
        DEFAULT_WEIGHTS.put("turkey_spawns", 10);
        DEFAULT_WEIGHTS.put("turtle_spawns", 12);
        DEFAULT_WEIGHTS.put("werewolf_spawns", 70);
        DEFAULT_WEIGHTS.put("werewolf_spawns_nether", 25);
        DEFAULT_WEIGHTS.put("wild_wolf_spawns", 100);
        DEFAULT_WEIGHTS.put("wraith_spawns", 100);
        DEFAULT_WEIGHTS.put("flame_wraith_spawns", 35);
        DEFAULT_WEIGHTS.put("wyvern_spawns_mountains", 2);
        DEFAULT_WEIGHTS.put("wyvern_spawns_lair", 70);
        DEFAULT_WEIGHTS.put("wyvern_mother_spawns_lair", 5);
        DEFAULT_WEIGHTS.put("wyvern_tier2_spawns_lair", 25);
    }

    private final Map<String, ModConfigSpec.IntValue> weights = new LinkedHashMap<>();

    public SpawnConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("spawns")).push("spawns");

        DEFAULT_WEIGHTS.forEach((option, defaultWeight) -> weights.put(option, builder
                .comment("Spawn weight of this spawn rule. Higher is more common, lower is rarer, 0 disables it. Applies when the world is loaded.")
                .translation(ConfigTranslations.of("spawn." + option))
                .defineInRange(option, defaultWeight, 0, MAX_WEIGHT)));

        builder.pop();
    }

    /** Returns the configured weight for a spawn rule, or the given fallback if the rule is not in the config. */
    public int getWeight(String option, int fallback) {
        ModConfigSpec.IntValue value = weights.get(option);
        return value == null ? fallback : value.get();
    }
}