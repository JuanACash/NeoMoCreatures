package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for passive, tameable and neutral creatures. */
public final class CreatureConfig {

    public final ModConfigSpec.BooleanValue elephantBulldozer;
    public final ModConfigSpec.IntValue wyvernEggDropChance;
    public final ModConfigSpec.IntValue motherWyvernEggDropChance;
    public final ModConfigSpec.IntValue rareItemDropChance;
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