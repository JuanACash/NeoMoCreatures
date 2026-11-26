package com.example.neomocreatures.block;

import java.util.Optional;

import com.example.neomocreatures.NeoMoCreatures;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/** Tells the Ogre Lair sapling which tree to grow. */
public final class OgreLairTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> OGRE_LAIR_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "ogre_lair_tree"));

    public static final TreeGrower OGRE_LAIR = new TreeGrower(
            "ogre_lair",
            Optional.empty(),
            Optional.of(OGRE_LAIR_TREE),
            Optional.empty());

    private OgreLairTreeGrower() {
    }
}