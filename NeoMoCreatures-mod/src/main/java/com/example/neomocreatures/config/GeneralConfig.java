package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** General gameplay toggles that do not belong to a single creature. */
public final class GeneralConfig {

    public final ModConfigSpec.BooleanValue armorSetEffects;
    public final ModConfigSpec.BooleanValue weaponEffects;
    public final ModConfigSpec.BooleanValue easterEggs;
    public final ModConfigSpec.BooleanValue hideTamedNames;
    public final ModConfigSpec.BooleanValue hideHealthBar;
    public final ModConfigSpec.BooleanValue craftableHorseArmor;
    public final ModConfigSpec.BooleanValue craftableSaddles;

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

        hideTamedNames = builder
                .comment("Hides the name tag of all tamed creatures.")
                .translation(ConfigTranslations.of("hideTamedNames"))
                .define("hideTamedNames", false);

        hideHealthBar = builder
                .comment("Hides the health bar shown above tamed creatures.")
                .translation(ConfigTranslations.of("hideHealthBar"))
                .define("hideHealthBar", false);

        craftableHorseArmor = builder
                .comment("Adds recipes to craft the iron, golden and diamond horse armor. Needs a data reload (/reload) to apply.")
                .translation(ConfigTranslations.of("craftableHorseArmor"))
                .define("craftableHorseArmor", true);

        craftableSaddles = builder
                .comment("Allows crafting the Mo' Creatures horse saddle.")
                .translation(ConfigTranslations.of("craftableSaddles"))
                .define("craftableSaddles", true);

        builder.pop();
    }
}