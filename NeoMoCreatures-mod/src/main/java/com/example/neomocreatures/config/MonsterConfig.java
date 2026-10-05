package com.example.neomocreatures.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Options for hostile mobs. */
public final class MonsterConfig {

    public final ModConfigSpec.BooleanValue golemDestroyBlocks;
    public final ModConfigSpec.DoubleValue ogreStrength;
    public final ModConfigSpec.DoubleValue fireOgreStrength;
    public final ModConfigSpec.DoubleValue caveOgreStrength;

    public MonsterConfig(ModConfigSpec.Builder builder) {
        builder.push("monsters");

        golemDestroyBlocks = builder
                .comment("Allows golems to pick up blocks. Still requires mobGriefing to be enabled.")
                .define("golemDestroyBlocks", true);

        ogreStrength = builder
                .comment("Block destruction radius of green ogres.")
                .defineInRange("ogreStrength", 2.5D, 0.0D, 10.0D);

        fireOgreStrength = builder
                .comment("Block destruction radius of fire ogres.")
                .defineInRange("fireOgreStrength", 2.0D, 0.0D, 10.0D);

        caveOgreStrength = builder
                .comment("Block destruction radius of cave ogres.")
                .defineInRange("caveOgreStrength", 3.0D, 0.0D, 10.0D);

        builder.pop();
    }
}