package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for passive, tameable and neutral creatures. */
public final class CreatureConfig {

    public final ModConfigSpec.BooleanValue elephantBulldozer;
    public final ModConfigSpec.IntValue wyvernEggDropChance;
    public final ModConfigSpec.BooleanValue staticBed;
    public final ModConfigSpec.BooleanValue staticLitter;

    public CreatureConfig(ModConfigSpec.Builder builder) {
        builder.push("creatures");

        elephantBulldozer = builder
                .comment("Makes elephants with tusks destroy blocks in front of them when ridden.")
                .define("elephantBulldozer", true);


        wyvernEggDropChance = builder
                .comment("Base percentage for a wyvern to drop its egg. Looting still adds its bonus on top.")
                .defineInRange("wyvernEggDropChance", 10, 0, 100);

        staticBed = builder
                .comment("Makes the kitty bed impossible to push.")
                .define("staticBed", false);

        staticLitter = builder
                .comment("Makes the kitty litter box impossible to push.")
                .define("staticLitter", false);

        builder.pop();
    }
}