package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** General gameplay toggles that do not belong to a single creature. */
public final class GeneralConfig {

    public final ModConfigSpec.BooleanValue armorSetEffects;
    public final ModConfigSpec.BooleanValue weaponEffects;
    public final ModConfigSpec.BooleanValue easterEggs;

    public GeneralConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("general")).push("general");

        armorSetEffects = builder
                .comment("Applies potion effects when wearing a full scorpion armor set.")
                .translation(ConfigTranslations.of("armorSetEffects"))
                .define("armorSetEffects", true);

        weaponEffects = builder
                .comment("Applies potion effects when dealing damage with scorpion weapons.")
                .translation(ConfigTranslations.of("weaponEffects"))
                .define("weaponEffects", true);

        easterEggs = builder
                .comment("Enables easter eggs: the wraith named Scratch, Ninja Turtle names and the zebra shuffle record.")
                .translation(ConfigTranslations.of("easterEggs"))
                .define("easterEggs", true);

        builder.pop();
    }
}