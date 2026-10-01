package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.worldgen.WyvwoodSmallTrunkPlacer;
import com.example.neomocreatures.worldgen.WyvwoodTrunkPlacer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTrunkPlacerTypes {

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, NeoMoCreatures.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<WyvwoodTrunkPlacer>> WYVWOOD_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("wyvwood_trunk_placer",
                    () -> new TrunkPlacerType<>(WyvwoodTrunkPlacer.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<WyvwoodSmallTrunkPlacer>> WYVWOOD_SMALL_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("wyvwood_small_trunk_placer",
                    () -> new TrunkPlacerType<>(WyvwoodSmallTrunkPlacer.CODEC));
}