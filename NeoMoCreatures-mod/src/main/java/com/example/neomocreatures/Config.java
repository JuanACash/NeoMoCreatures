package com.example.neomocreatures;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common mod config. Empty for now — add real values here whenever something needs to be tweakable
 * without recompiling (e.g. spawn rates, armour damage, breeding with vanilla horses, etc).
 *
 * Example of what a real value looks like, so the syntax doesn't have to be looked up:
 *
 * public static final ModConfigSpec.BooleanValue ALLOW_VANILLA_BREEDING = BUILDER
 *         .comment("Whether Mo' Creatures horses can breed with vanilla horses/donkeys")
 *         .define("allowVanillaBreeding", false);
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static final ModConfigSpec SPEC = BUILDER.build();
}