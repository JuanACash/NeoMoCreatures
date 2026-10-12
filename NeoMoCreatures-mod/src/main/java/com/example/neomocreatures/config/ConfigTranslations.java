package com.example.neomocreatures.config;

import com.example.neomocreatures.NeoMoCreatures;

/** Builds the translation keys used by the in-game configuration screen. */
public final class ConfigTranslations {

    private static final String PREFIX = NeoMoCreatures.MODID + ".configuration.";

    private ConfigTranslations() {
    }

    public static String of(String name) {
        return PREFIX + name;
    }
}