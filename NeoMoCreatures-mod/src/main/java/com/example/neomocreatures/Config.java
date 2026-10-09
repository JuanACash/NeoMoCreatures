package com.example.neomocreatures;

import com.example.neomocreatures.config.CreatureConfig;
import com.example.neomocreatures.config.GeneralConfig;
import com.example.neomocreatures.config.LegacyConfig;
import com.example.neomocreatures.config.MonsterConfig;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common mod config. Each section lives in its own class under the config package. */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final GeneralConfig GENERAL = new GeneralConfig(BUILDER);
    public static final CreatureConfig CREATURES = new CreatureConfig(BUILDER);
    public static final MonsterConfig MONSTERS = new MonsterConfig(BUILDER);
    public static final LegacyConfig LEGACY = new LegacyConfig(BUILDER);

    static final ModConfigSpec SPEC = BUILDER.build();
}