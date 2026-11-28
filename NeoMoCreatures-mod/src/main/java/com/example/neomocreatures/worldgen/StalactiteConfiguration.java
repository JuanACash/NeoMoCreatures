package com.example.neomocreatures.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Settings of a hanging stalactite.
 *
 * @param block          what the stalactite is made of
 * @param ceiling        blocks a stalactite is allowed to hang from
 * @param baseRadius     radius of the cone where it meets the ceiling
 * @param length         how many blocks it hangs down
 * @param maxCeilingScan how far above the origin the ceiling is searched for
 */
public record StalactiteConfiguration(BlockStateProvider block, BlockPredicate ceiling, IntProvider baseRadius,
                                      IntProvider length, int maxCeilingScan) implements FeatureConfiguration {

    public static final Codec<StalactiteConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("block").forGetter(StalactiteConfiguration::block),
            BlockPredicate.CODEC.fieldOf("ceiling").forGetter(StalactiteConfiguration::ceiling),
            IntProvider.codec(1, 6).fieldOf("base_radius").forGetter(StalactiteConfiguration::baseRadius),
            IntProvider.codec(2, 32).fieldOf("length").forGetter(StalactiteConfiguration::length),
            Codec.intRange(1, 64).fieldOf("max_ceiling_scan").forGetter(StalactiteConfiguration::maxCeilingScan)
    ).apply(instance, StalactiteConfiguration::new));
}