package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for hostile mobs. */
public final class MonsterConfig {

    public final ModConfigSpec.BooleanValue golemDestroyBlocks;
    public final ModConfigSpec.DoubleValue ogreStrength;
    public final ModConfigSpec.DoubleValue fireOgreStrength;
    public final ModConfigSpec.DoubleValue caveOgreStrength;
    public final ModConfigSpec.BooleanValue alphaWraithEyes;

    public MonsterConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("monsters")).push("monsters");

        golemDestroyBlocks = builder
                .comment("Allows golems to pick up blocks. Still requires mobGriefing to be enabled.")
                .translation(ConfigTranslations.of("golemDestroyBlocks"))
                .define("golemDestroyBlocks", true);

        ogreStrength = builder
                .comment("Block destruction radius of green ogres.")
                .translation(ConfigTranslations.of("ogreStrength"))
                .defineInRange("ogreStrength", 2.5D, 0.0D, 10.0D);

        fireOgreStrength = builder
                .comment("Block destruction radius of fire ogres.")
                .translation(ConfigTranslations.of("fireOgreStrength"))
                .defineInRange("fireOgreStrength", 2.0D, 0.0D, 10.0D);

        caveOgreStrength = builder
                .comment("Block destruction radius of cave ogres.")
                .translation(ConfigTranslations.of("caveOgreStrength"))
                .defineInRange("caveOgreStrength", 3.0D, 0.0D, 10.0D);

        alphaWraithEyes = builder
                .comment("Uses the alternative wraith textures with translucent eyes. Applies right away.")
                .translation(ConfigTranslations.of("alphaWraithEyes"))
                .define("alphaWraithEyes", false);

        builder.pop();
    }
}