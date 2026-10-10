package com.example.neomocreatures.init;

import java.util.function.Supplier;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.worldgen.ConfigSpawnsBiomeModifier;
import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Registers the custom biome modifier types of the mod. */
public final class ModBiomeModifiers {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NeoMoCreatures.MODID);

    public static final Supplier<MapCodec<ConfigSpawnsBiomeModifier>> CONFIG_SPAWNS =
            BIOME_MODIFIERS.register("config_spawns", () -> ConfigSpawnsBiomeModifier.CODEC);

    private ModBiomeModifiers() {
    }
}