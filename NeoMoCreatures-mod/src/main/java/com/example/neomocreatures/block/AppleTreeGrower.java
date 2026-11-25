package com.example.neomocreatures.block;

import java.util.Optional;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/** Tells the apple tree sapling which tree to grow. */
public final class AppleTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "apple_tree"));

    public static final TreeGrower APPLE = new TreeGrower(
            "apple",
            Optional.empty(),
            Optional.of(APPLE_TREE),
            Optional.empty());

    private AppleTreeGrower() {
    }
}