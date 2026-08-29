package com.example.examplemod.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.example.examplemod.init.ModTrunkPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Tronco tipo baobab: cilindro grueso (disco relleno) que se INCLINA
 * progresivamente mientras crece, con ramas saliendo en ángulos aleatorios
 * de 360° desde la zona superior del tronco, y "picos" extra de hojas en
 * el borde de la copa. Inspirado en el algoritmo de LOTRWorldGenBaobab
 * (LOTR Mod, 1.7.10), portado a la API de TrunkPlacer de 1.21.1.
 *
 * Los campos custom van agrupados en sub-records (LeanSettings,
 * BranchSettings, TopSpikeSettings) porque RecordCodecBuilder.mapCodec
 * solo soporta hasta 8 campos combinados por .and(), y trunkPlacerParts
 * ya ocupa 3 de esos 8.
 */
public class WyvwoodTrunkPlacer extends TrunkPlacer {

    public record LeanSettings(IntProvider interval, int maxShift) {
        public static final MapCodec<LeanSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProvider.codec(1, 16).fieldOf("interval").forGetter(LeanSettings::interval),
                Codec.intRange(0, 4).fieldOf("max_shift").forGetter(LeanSettings::maxShift)
        ).apply(instance, LeanSettings::new));
    }

    public record BranchSettings(int zoneHeight, IntProvider count, IntProvider length) {
        public static final MapCodec<BranchSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.intRange(1, 32).fieldOf("zone_height").forGetter(BranchSettings::zoneHeight),
                IntProvider.codec(0, 32).fieldOf("count").forGetter(BranchSettings::count),
                IntProvider.codec(1, 16).fieldOf("length").forGetter(BranchSettings::length)
        ).apply(instance, BranchSettings::new));
    }

    public record TopSpikeSettings(float chance, IntProvider height) {
        public static final MapCodec<TopSpikeSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(TopSpikeSettings::chance),
                IntProvider.codec(1, 8).fieldOf("height").forGetter(TopSpikeSettings::height)
        ).apply(instance, TopSpikeSettings::new));
    }

    public static final MapCodec<WyvwoodTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance)
                    .and(Codec.intRange(1, 8).fieldOf("trunk_radius").forGetter(p -> p.trunkRadius))
                    .and(LeanSettings.CODEC.codec().fieldOf("lean").forGetter(p -> p.lean))
                    .and(BranchSettings.CODEC.codec().fieldOf("branches").forGetter(p -> p.branches))
                    .and(TopSpikeSettings.CODEC.codec().fieldOf("top_spikes").forGetter(p -> p.topSpikes))
                    .apply(instance, WyvwoodTrunkPlacer::new));

    private final int trunkRadius;
    private final LeanSettings lean;
    private final BranchSettings branches;
    private final TopSpikeSettings topSpikes;

    public WyvwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                               int trunkRadius, LeanSettings lean, BranchSettings branches,
                               TopSpikeSettings topSpikes) {
        super(baseHeight, heightRandA, heightRandB);
        this.trunkRadius = trunkRadius;
        this.lean = lean;
        this.branches = branches;
        this.topSpikes = topSpikes;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.WYVWOOD_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
                                                              BiConsumer<BlockPos, BlockState> blockSetter,
                                                              RandomSource random,
                                                              int freeTreeHeight,
                                                              BlockPos pos,
                                                              TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();

        int intervalVal = Math.max(1, lean.interval().sample(random));
        int xSign = random.nextBoolean() ? 1 : -1;
        int zSign = random.nextBoolean() ? 1 : -1;

        // Tierra debajo del disco base (altura 0, sin inclinación todavía).
        for (int dx = -trunkRadius; dx <= trunkRadius; dx++) {
            for (int dz = -trunkRadius; dz <= trunkRadius; dz++) {
                if (dx * dx + dz * dz <= trunkRadius * trunkRadius) {
                    setDirtAt(level, blockSetter, random, pos.offset(dx, -1, dz), config);
                }
            }
        }

        // Tronco: disco relleno que se desplaza (inclina) cada "interval" bloques.
        for (int y = 0; y < freeTreeHeight; y++) {
            int[] center = centerAt(y, intervalVal, xSign, zSign);
            for (int dx = -trunkRadius; dx <= trunkRadius; dx++) {
                for (int dz = -trunkRadius; dz <= trunkRadius; dz++) {
                    if (dx * dx + dz * dz <= trunkRadius * trunkRadius) {
                        placeLog(level, blockSetter, random, pos.offset(center[0] + dx, y, center[1] + dz), config);
                    }
                }
            }
        }

        int topY = freeTreeHeight - 1;
        int[] topCenter = centerAt(topY, intervalVal, xSign, zSign);

        // Copa principal (alimenta el foliage_placer del JSON, ej. dark_oak_foliage_placer).
        attachments.add(new FoliagePlacer.FoliageAttachment(
                pos.offset(topCenter[0], freeTreeHeight, topCenter[1]), trunkRadius - 1, true));

        // Ramas en ángulos aleatorios de 360°, saliendo de la zona superior del tronco.
        int zoneStart = Math.max(0, freeTreeHeight - branches.zoneHeight());
        int totalBranches = branches.count().sample(random);
        for (int i = 0; i < totalBranches; i++) {
            int by = zoneStart + random.nextInt(Math.max(1, freeTreeHeight - zoneStart));
            int[] c = centerAt(by, intervalVal, xSign, zSign);
            double angle = random.nextDouble() * Math.PI * 2.0;

            BlockPos start = pos.offset(
                    c[0] + (int) Math.round(Math.cos(angle) * trunkRadius),
                    by,
                    c[1] + (int) Math.round(Math.sin(angle) * trunkRadius));

            BlockPos end = growBranch(level, blockSetter, random, start, angle, branches.length().sample(random), config);
            attachments.add(new FoliagePlacer.FoliageAttachment(end.above(), 0, false));
        }

        // Picos extra en el borde de la copa (textura irregular, como el baobab real).
        for (int dx = -trunkRadius; dx <= trunkRadius; dx++) {
            for (int dz = -trunkRadius; dz <= trunkRadius; dz++) {
                int distSq = dx * dx + dz * dz;
                boolean onEdge = distSq <= trunkRadius * trunkRadius && distSq > (trunkRadius - 1) * (trunkRadius - 1);
                if (onEdge && random.nextFloat() < topSpikes.chance()) {
                    BlockPos spikeBase = pos.offset(topCenter[0] + dx, freeTreeHeight, topCenter[1] + dz);
                    int spikeH = topSpikes.height().sample(random);
                    BlockPos spikeTop = spikeBase;
                    for (int s = 0; s < spikeH; s++) {
                        spikeTop = spikeBase.above(s);
                        placeLog(level, blockSetter, random, spikeTop, config);
                    }
                    attachments.add(new FoliagePlacer.FoliageAttachment(spikeTop.above(), 0, false));
                }
            }
        }

        return attachments;
    }

    private int[] centerAt(int y, int intervalVal, int xSign, int zSign) {
        int shifts = y / intervalVal;
        return new int[]{xSign * lean.maxShift() * shifts, zSign * lean.maxShift() * shifts};
    }

    private BlockPos growBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                 RandomSource random, BlockPos start, double angle, int length,
                                 TreeConfiguration config) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        Direction.Axis axis = Math.abs(dx) >= Math.abs(dz) ? Direction.Axis.X : Direction.Axis.Z;
        Function<BlockState, BlockState> axisModifier = state ->
                state.hasProperty(BlockStateProperties.AXIS) ? state.setValue(BlockStateProperties.AXIS, axis) : state;

        BlockPos current = start;
        int yOffset = 0;
        for (int step = 1; step <= length; step++) {
            if (step > 2 && random.nextInt(3) == 0) {
                yOffset++;
            }
            current = new BlockPos(
                    start.getX() + (int) Math.round(dx * step),
                    start.getY() + yOffset,
                    start.getZ() + (int) Math.round(dz * step));
            placeLog(level, blockSetter, random, current, config, axisModifier);
        }
        return current;
    }
}