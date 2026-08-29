package com.example.examplemod.worldgen;

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

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Tronco tipo dragonblood/mirk oak "grande" CON una copa central sobre el
 * tronco (como el mirk oak normal) ADEMÁS de los racimos en cada rama —
 * mezcla de ambos estilos: el tronco queda tapado por el domo central,
 * y las ramas alrededor añaden volumen extra.
 *
 * Coloca las hojas directamente (no delega a un FoliagePlacer).
 */
public class WyvwoodSmallTrunkPlacer extends TrunkPlacer {

    public record BranchSettings(IntProvider count, IntProvider length) {
        public static final MapCodec<BranchSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProvider.codec(1, 16).fieldOf("count").forGetter(BranchSettings::count),
                IntProvider.codec(1, 16).fieldOf("length").forGetter(BranchSettings::length)
        ).apply(instance, BranchSettings::new));
    }

    public record CanopySettings(int maxRadius, int layers) {
        public static final MapCodec<CanopySettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.intRange(1, 8).fieldOf("max_radius").forGetter(CanopySettings::maxRadius),
                Codec.intRange(1, 6).fieldOf("layers").forGetter(CanopySettings::layers)
        ).apply(instance, CanopySettings::new));
    }

    public record RootSettings(IntProvider count, IntProvider length, int maxDepthPerStep) {
        public static final MapCodec<RootSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IntProvider.codec(0, 16).fieldOf("count").forGetter(RootSettings::count),
                IntProvider.codec(1, 8).fieldOf("length").forGetter(RootSettings::length),
                Codec.intRange(1, 8).fieldOf("max_depth_per_step").forGetter(RootSettings::maxDepthPerStep)
        ).apply(instance, RootSettings::new));
    }

    public static final MapCodec<WyvwoodSmallTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance)
                    .and(Codec.intRange(0, 4).fieldOf("trunk_radius").forGetter(p -> p.trunkRadius))
                    .and(BranchSettings.CODEC.codec().fieldOf("branches").forGetter(p -> p.branches))
                    .and(CanopySettings.CODEC.codec().fieldOf("branch_canopy").forGetter(p -> p.branchCanopy))
                    .and(CanopySettings.CODEC.codec().fieldOf("main_canopy").forGetter(p -> p.mainCanopy))
                    .and(RootSettings.CODEC.codec().fieldOf("roots").forGetter(p -> p.roots))
                    .apply(instance, WyvwoodSmallTrunkPlacer::new));

    private final int trunkRadius;
    private final BranchSettings branches;
    private final CanopySettings branchCanopy;
    private final CanopySettings mainCanopy;
    private final RootSettings roots;

    public WyvwoodSmallTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                                    int trunkRadius, BranchSettings branches, CanopySettings branchCanopy,
                                    CanopySettings mainCanopy, RootSettings roots) {
        super(baseHeight, heightRandA, heightRandB);
        this.trunkRadius = trunkRadius;
        this.branches = branches;
        this.branchCanopy = branchCanopy;
        this.mainCanopy = mainCanopy;
        this.roots = roots;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.WYVWOOD_SMALL_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
                                                              BiConsumer<BlockPos, BlockState> blockSetter,
                                                              RandomSource random,
                                                              int freeTreeHeight,
                                                              BlockPos pos,
                                                              TreeConfiguration config) {
        setDirtAt(level, blockSetter, random, pos.below(), config);

        // Tronco (cilindro de radio trunk_radius).
        for (int y = 0; y < freeTreeHeight; y++) {
            for (int dx = -trunkRadius; dx <= trunkRadius; dx++) {
                for (int dz = -trunkRadius; dz <= trunkRadius; dz++) {
                    if (dx * dx + dz * dz <= trunkRadius * trunkRadius) {
                        placeLog(level, blockSetter, random, pos.offset(dx, y, dz), config);
                    }
                }
            }
        }

        // Ramas en ángulos aleatorios, cada una con su propio domo de hojas al final.
        int totalBranches = branches.count().sample(random);
        for (int i = 0; i < totalBranches; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double angleY = random.nextDouble() * 0.35;

            int startY = Math.max(0, freeTreeHeight - 1 - random.nextInt(5));
            BlockPos start = pos.offset(0, startY, 0);

            BlockPos end = growBranch(level, blockSetter, random, start, angle, angleY,
                    branches.length().sample(random), config);

            placeCanopyDome(level, blockSetter, random, config, end, branchCanopy);
        }

        // Domo central grande sobre la punta del tronco (tapa el tronco, estilo mirk oak normal).
        placeCanopyDome(level, blockSetter, random, config, pos.above(freeTreeHeight), mainCanopy);

        // Raíces que serpentean hacia abajo y hacia los lados.
        int rootCount = roots.count().sample(random);
        for (int i = 0; i < rootCount; i++) {
            boolean alongX = random.nextBoolean();
            int dir = random.nextBoolean() ? 1 : -1;
            int rx = pos.getX() + (alongX ? dir * (trunkRadius + 1) : random.nextInt(trunkRadius * 2 + 1) - trunkRadius);
            int rz = pos.getZ() + (!alongX ? dir * (trunkRadius + 1) : random.nextInt(trunkRadius * 2 + 1) - trunkRadius);
            int ry = pos.getY() + 1 + random.nextInt(trunkRadius * 2 + 1);

            int segments = roots.length().sample(random);
            for (int s = 0; s < segments; s++) {
                int placed = 0;
                while (placed < roots.maxDepthPerStep()
                        && level.isStateAtPosition(new BlockPos(rx, ry, rz), BlockState::isAir)) {
                    placeLog(level, blockSetter, random, new BlockPos(rx, ry, rz), config);
                    ry--;
                    placed++;
                }
                ry--;
                if (random.nextBoolean()) {
                    if (alongX) rx += dir; else rz += dir;
                }
            }
        }

        return Collections.emptyList();
    }

    private BlockPos growBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                 RandomSource random, BlockPos start, double angle, double angleY,
                                 int length, TreeConfiguration config) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double dy = Math.sin(angleY);
        Direction.Axis axis = Math.abs(dx) >= Math.abs(dz) ? Direction.Axis.X : Direction.Axis.Z;
        Function<BlockState, BlockState> axisModifier = state ->
                state.hasProperty(BlockStateProperties.AXIS) ? state.setValue(BlockStateProperties.AXIS, axis) : state;

        BlockPos current = start;
        for (int step = 1; step <= length; step++) {
            current = new BlockPos(
                    start.getX() + (int) Math.round(dx * step),
                    start.getY() + (int) Math.round(dy * step),
                    start.getZ() + (int) Math.round(dz * step));
            placeLog(level, blockSetter, random, current, config, axisModifier);
        }
        return current;
    }

    private void placeCanopyDome(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                  RandomSource random, TreeConfiguration config, BlockPos center,
                                  CanopySettings settings) {
        for (int layer = 0; layer < settings.layers(); layer++) {
            int y = center.getY() + layer;
            int radius = Math.max(1, settings.maxRadius() - layer);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int distSq = dx * dx + dz * dz;
                    if (distSq > radius * radius) continue;

                    boolean onEdge = distSq > (radius - 1) * (radius - 1);
                    if (onEdge && random.nextInt(4) == 0) continue;

                    BlockPos leafPos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                    tryPlaceLeaf(level, blockSetter, random, config, leafPos);
                }
            }
        }
    }

    private void tryPlaceLeaf(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                               RandomSource random, TreeConfiguration config, BlockPos pos) {
        if (level.isStateAtPosition(pos, BlockState::isAir)) {
            blockSetter.accept(pos, config.foliageProvider.getState(random, pos));
        }
    }
}