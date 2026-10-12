package com.example.neomocreatures.worldgen;

import java.util.List;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.init.ModBiomeModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/** Like neoforge:add_spawns, but the spawn weight comes from the spawn config ("option"). 0 adds nothing. */
public record ConfigSpawnsBiomeModifier(HolderSet<Biome> biomes, String option, List<SpawnerData> spawners)
        implements BiomeModifier {

    public static final MapCodec<ConfigSpawnsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigSpawnsBiomeModifier::biomes),
            Codec.STRING.fieldOf("option").forGetter(ConfigSpawnsBiomeModifier::option),
            SpawnerData.CODEC.listOf().fieldOf("spawners").forGetter(ConfigSpawnsBiomeModifier::spawners)
    ).apply(instance, ConfigSpawnsBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !this.biomes.contains(biome)) {
            return;
        }
        MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
        for (SpawnerData spawner : this.spawners) {
            int weight = Config.SPAWNS.getWeight(this.option, spawner.getWeight().asInt());
            if (weight > 0) {
                spawns.addSpawn(spawner.type.getCategory(),
                        new SpawnerData(spawner.type, weight, spawner.minCount, spawner.maxCount));
            }
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.CONFIG_SPAWNS.get();
    }
}