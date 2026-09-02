package com.example.neomocreatures.worldgen;

import com.example.neomocreatures.NeoMoCreatures;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class WyvernIslandFeature extends Feature<NoneFeatureConfiguration> {

    private static final String[] STRUCTURE_NAMES = {"wyvernisland1", "wyvernisland2", "wyvernisland3", "wyvernisland4"};

    public WyvernIslandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        ResourceKey<Level> dimensionKey = context.level().getLevel().dimension();
        if (!dimensionKey.location().toString().contains("wyvernlairworld")) {
            return false;
        }

        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int chunkX = (origin.getX() >> 4) << 4;
        int chunkZ = (origin.getZ() >> 4) << 4;
        int count = random.nextInt(2) + 1;

        for (int a = 0; a < count; a++) {
            int x = chunkX + random.nextInt(16);
            int z = chunkZ + random.nextInt(16);
            int y = context.level().getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
            String structureName = STRUCTURE_NAMES[random.nextInt(STRUCTURE_NAMES.length)];
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            Mirror mirror = Mirror.values()[random.nextInt(Mirror.values().length)];
            BlockPos spawnPos = new BlockPos(x, y + random.nextInt(84) + 16, z);

            StructureTemplateManager templateManager = context.level().getLevel().getStructureManager();
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, structureName);

            Optional<StructureTemplate> optionalTemplate = templateManager.get(location);
            if (optionalTemplate.isEmpty()) {
                continue;
            }
            StructureTemplate template = optionalTemplate.get();
            Vec3i size = template.getSize();
            if (size.getX() == 0 || size.getY() == 0 || size.getZ() == 0) {
                continue;
            }

            StructurePlaceSettings placementSettings = new StructurePlaceSettings()
                    .setRotation(rotation)
                    .setMirror(mirror)
                    .setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(false)
                    .setKnownShape(false);
            template.placeInWorld((ServerLevelAccessor) context.level(), spawnPos, spawnPos, placementSettings, random, 2);
        }

        return true;
    }
}