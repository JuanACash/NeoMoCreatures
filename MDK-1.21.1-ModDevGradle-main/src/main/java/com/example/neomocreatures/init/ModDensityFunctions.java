package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.worldgen.WyvernIslandDensityFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDensityFunctions {

    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES =
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, NeoMoCreatures.MODID);

    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<WyvernIslandDensityFunction>> WYVERN_ISLAND_SHAPE =
            DENSITY_FUNCTION_TYPES.register("wyvern_island_shape", () -> WyvernIslandDensityFunction.DATA_CODEC);
}