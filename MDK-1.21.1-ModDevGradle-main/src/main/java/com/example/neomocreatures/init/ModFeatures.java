package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.worldgen.FirestoneClusterFeature;
import com.example.neomocreatures.worldgen.WyvernNestFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, NeoMoCreatures.MODID);

    public static final DeferredHolder<Feature<?>, FirestoneClusterFeature> FIRESTONE_CLUSTER =
            FEATURES.register("firestone_cluster",
                    () -> new FirestoneClusterFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, WyvernNestFeature> WYVERN_NEST =
            FEATURES.register("wyvern_nest",
                    () -> new WyvernNestFeature(NoneFeatureConfiguration.CODEC));
}