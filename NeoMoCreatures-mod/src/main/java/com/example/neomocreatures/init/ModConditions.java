package com.example.neomocreatures.init;

import java.util.function.Supplier;

import com.example.neomocreatures.NeoMoCreatures;
import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Registers the custom recipe conditions of the mod. */
public final class ModConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, NeoMoCreatures.MODID);

    public static final Supplier<MapCodec<ConfigCondition>> CONFIG =
            CONDITIONS.register("config", () -> ConfigCondition.CODEC);

    private ModConditions() {
    }
}