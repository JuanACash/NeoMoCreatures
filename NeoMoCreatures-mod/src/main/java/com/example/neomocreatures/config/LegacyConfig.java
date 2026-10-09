package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Independent toggles that bring back legacy sounds and models from older Mo' Creatures versions. */
public final class LegacyConfig {

    public final ModConfigSpec.BooleanValue legacySounds;
    public final ModConfigSpec.BooleanValue legacyBigCatModels;
    public final ModConfigSpec.BooleanValue legacySharkModel;
    public final ModConfigSpec.BooleanValue legacyScorpionModel;

    public LegacyConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("legacy")).push("legacy");

        legacySounds = builder
                .comment("Uses the legacy rat death sound and the legacy wraith ambient, hurt and death sounds.")
                .translation(ConfigTranslations.of("legacySounds"))
                .define("legacySounds", false);

        legacyBigCatModels = builder
                .comment("Uses the simple legacy big cat models and textures. Press F3+T or restart to apply.")
                .translation(ConfigTranslations.of("legacyBigCatModels"))
                .define("legacyBigCatModels", false);

        legacySharkModel = builder
                .comment("Uses the legacy shark model and texture. Press F3+T or restart to apply.")
                .translation(ConfigTranslations.of("legacySharkModel"))
                .define("legacySharkModel", false);

        legacyScorpionModel = builder
                .comment("Uses the legacy scorpion model and textures. Press F3+T or restart to apply.")
                .translation(ConfigTranslations.of("legacyScorpionModel"))
                .define("legacyScorpionModel", false);

        builder.pop();
    }
}