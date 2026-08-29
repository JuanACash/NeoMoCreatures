package com.example.examplemod.init;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.worldgen.WyvwoodSmallTrunkPlacer;
import com.example.examplemod.worldgen.WyvwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTrunkPlacerTypes {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, ExampleMod.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<WyvwoodTrunkPlacer>> WYVWOOD_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("wyvwood_trunk_placer",
                    () -> new TrunkPlacerType<>(WyvwoodTrunkPlacer.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<WyvwoodSmallTrunkPlacer>> WYVWOOD_SMALL_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("wyvwood_small_trunk_placer",
                    () -> new TrunkPlacerType<>(WyvwoodSmallTrunkPlacer.CODEC));
}