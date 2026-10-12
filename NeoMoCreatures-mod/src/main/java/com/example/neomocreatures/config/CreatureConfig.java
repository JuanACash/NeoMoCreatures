package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for passive, tameable and neutral creatures. */
public final class CreatureConfig {

    public final ModConfigSpec.BooleanValue elephantBulldozer;
    public final ModConfigSpec.IntValue wyvernEggDropChance;
    public final ModConfigSpec.IntValue motherWyvernEggDropChance;
    public final ModConfigSpec.IntValue rareItemDropChance;
    public final ModConfigSpec.BooleanValue alwaysNamePets;
    public final ModConfigSpec.BooleanValue eggWarningMessages;
    public final ModConfigSpec.BooleanValue easyHorseBreeding;
    public final ModConfigSpec.BooleanValue enableHunters;
    public final ModConfigSpec.BooleanValue attackHorses;
    public final ModConfigSpec.BooleanValue attackWolves;
    public final ModConfigSpec.IntValue kittyVillageSpawnChance;
    public final ModConfigSpec.BooleanValue staticBed;
    public final ModConfigSpec.BooleanValue staticLitter;

    public CreatureConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("creatures")).push("creatures");

        elephantBulldozer = builder
                .comment("Makes elephants with tusks destroy blocks in front of them when ridden.")
                .translation(ConfigTranslations.of("elephantBulldozer"))
                .define("elephantBulldozer", true);

        wyvernEggDropChance = builder
                .comment("Base percentage for a regular wyvern to drop its egg. Looting still adds its bonus on top.")
                .translation(ConfigTranslations.of("wyvernEggDropChance"))
                .defineInRange("wyvernEggDropChance", 10, 0, 100);

        motherWyvernEggDropChance = builder
                .comment("Base percentage for a mother wyvern to drop its egg. Looting still adds its bonus on top.")
                .translation(ConfigTranslations.of("motherWyvernEggDropChance"))
                .defineInRange("motherWyvernEggDropChance", 10, 0, 100);

        rareItemDropChance = builder
                .comment("Percentage for certain creatures (horses, ostriches, horse mobs) to drop a rare item when killed.")
                .translation(ConfigTranslations.of("rareItemDropChance"))
                .defineInRange("rareItemDropChance", 25, 0, 100);

        alwaysNamePets = builder
                .comment("Opens the naming screen every time a pet is tamed.")
                .translation(ConfigTranslations.of("alwaysNamePets"))
                .define("alwaysNamePets", true);

        eggWarningMessages = builder
                .comment("Warns the nearest player in chat, with coordinates, when a pet egg is about to hatch.")
                .translation(ConfigTranslations.of("eggWarningMessages"))
                .define("eggWarningMessages", true);

        easyHorseBreeding = builder
                .comment("When enabled, horses breed instantly. When disabled, a foal takes 5 minutes to arrive.")
                .translation(ConfigTranslations.of("easyHorseBreeding"))
                .define("easyHorseBreeding", false);
        enableHunters = builder
                .comment("Allows predators (bears, big cats, foxes, raccoons, boars) to hunt other animals.")
                .translation(ConfigTranslations.of("enableHunters"))
                .define("enableHunters", true);

        attackHorses = builder
                .comment("Allows predators and wild wolves to attack horses.")
                .translation(ConfigTranslations.of("attackHorses"))
                .define("attackHorses", false);

        attackWolves = builder
                .comment("Allows predators and wild wolves to attack wolves.")
                .translation(ConfigTranslations.of("attackWolves"))
                .define("attackWolves", false);

        kittyVillageSpawnChance = builder
                .comment("Percentage chance that each periodic village check spawns a kitty. 0 disables village kitties.")
                .translation(ConfigTranslations.of("kittyVillageSpawnChance"))
                .defineInRange("kittyVillageSpawnChance", 100, 0, 100);

        staticBed = builder
                .comment("Makes the kitty bed impossible to push.")
                .translation(ConfigTranslations.of("staticBed"))
                .define("staticBed", false);

        staticLitter = builder
                .comment("Makes the kitty litter box impossible to push.")
                .translation(ConfigTranslations.of("staticLitter"))
                .define("staticLitter", false);

        builder.pop();
    }
}