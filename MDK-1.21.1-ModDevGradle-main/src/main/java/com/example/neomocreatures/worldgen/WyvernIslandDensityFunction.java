package com.example.neomocreatures.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record WyvernIslandDensityFunction(double radius, double edgeFade, double amplitude)
        implements DensityFunction.SimpleFunction {

    public static final MapCodec<WyvernIslandDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("radius").forGetter(WyvernIslandDensityFunction::radius),
            Codec.DOUBLE.fieldOf("edge_fade").forGetter(WyvernIslandDensityFunction::edgeFade),
            Codec.DOUBLE.fieldOf("amplitude").forGetter(WyvernIslandDensityFunction::amplitude)
    ).apply(instance, WyvernIslandDensityFunction::new));

    public static final KeyDispatchDataCodec<WyvernIslandDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(FunctionContext context) {
        double x = context.blockX();
        double z = context.blockZ();
        double dist = Math.sqrt(x * x + z * z);
        double falloffStart = radius - edgeFade;

        double t;
        if (dist <= falloffStart) {
            t = 1.0;
        } else if (dist >= radius) {
            t = -1.0;
        } else {
            double progress = (dist - falloffStart) / edgeFade;
            t = 1.0 - 2.0 * progress;
        }
        return t * amplitude;
    }

    @Override
    public double minValue() {
        return -amplitude;
    }

    @Override
    public double maxValue() {
        return amplitude;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}