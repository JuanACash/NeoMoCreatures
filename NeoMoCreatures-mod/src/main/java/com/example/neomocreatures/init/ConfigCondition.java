package com.example.neomocreatures.init;

import java.util.Map;
import java.util.function.BooleanSupplier;

import com.example.neomocreatures.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.neoforged.neoforge.common.conditions.ICondition;

/** Recipe condition that is true when the named config option is enabled. */
public record ConfigCondition(String option) implements ICondition {

    public static final MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("option").forGetter(ConfigCondition::option)
    ).apply(instance, ConfigCondition::new));

    private static final Map<String, BooleanSupplier> OPTIONS = Map.of(
            "craftable_saddles", () -> Config.GENERAL.craftableSaddles.get(),
            "craftable_horse_armor", () -> Config.GENERAL.craftableHorseArmor.get());

    @Override
    public boolean test(IContext context) {
        return OPTIONS.getOrDefault(this.option, () -> false).getAsBoolean();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}