package com.example.neomocreatures.block;

import java.util.Optional;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class WyvwoodTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> WYVWOOD_TREE = ResourceKey.create(
            net.minecraft.core.registries.Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "wyvwood_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> WYVWOOD_MEGA_TREE = ResourceKey.create(
            net.minecraft.core.registries.Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "wyvwood_mega_tree"));

    public static final TreeGrower WYVWOOD = new TreeGrower(
            "wyvwood",
            Optional.of(WYVWOOD_MEGA_TREE),
            Optional.of(WYVWOOD_TREE),
            Optional.empty()
    );
}