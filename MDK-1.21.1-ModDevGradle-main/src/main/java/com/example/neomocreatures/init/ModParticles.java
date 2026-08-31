package com.example.neomocreatures.init;

import com.example.neomocreatures.ExampleMod;
import com.example.neomocreatures.breeding.MoCHorseGenetics.FairyColor;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ExampleMod.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UNDEAD_DECAY =
            PARTICLE_TYPES.register("undead_decay", () -> new SimpleParticleType(false));

    // Estela dorada del unicornio (sin cambios)
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX =
            PARTICLE_TYPES.register("star_fx", () -> new SimpleParticleType(false));

    // Una estrella por color de fairy horse
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_WHITE =
            PARTICLE_TYPES.register("star_fx_white", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_ORANGE =
            PARTICLE_TYPES.register("star_fx_orange", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_YELLOW =
            PARTICLE_TYPES.register("star_fx_yellow", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_LIGHTGREEN =
            PARTICLE_TYPES.register("star_fx_lightgreen", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_GREEN =
            PARTICLE_TYPES.register("star_fx_green", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_CYAN =
            PARTICLE_TYPES.register("star_fx_cyan", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_BLUE =
            PARTICLE_TYPES.register("star_fx_blue", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_DARKBLUE =
            PARTICLE_TYPES.register("star_fx_darkblue", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_PURPLE =
            PARTICLE_TYPES.register("star_fx_purple", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_PINK =
            PARTICLE_TYPES.register("star_fx_pink", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_RED =
            PARTICLE_TYPES.register("star_fx_red", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_FX_BLACK =
            PARTICLE_TYPES.register("star_fx_black", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VANISH_FX =
            PARTICLE_TYPES.register("vanish_fx", () -> new SimpleParticleType(false));

    private static final Map<FairyColor, DeferredHolder<ParticleType<?>, SimpleParticleType>> FAIRY_STAR_BY_COLOR =
            new EnumMap<>(FairyColor.class);
    static {
        FAIRY_STAR_BY_COLOR.put(FairyColor.WHITE, STAR_FX_WHITE);
        FAIRY_STAR_BY_COLOR.put(FairyColor.ORANGE, STAR_FX_ORANGE);
        FAIRY_STAR_BY_COLOR.put(FairyColor.YELLOW, STAR_FX_YELLOW);
        FAIRY_STAR_BY_COLOR.put(FairyColor.LIGHTGREEN, STAR_FX_LIGHTGREEN);
        FAIRY_STAR_BY_COLOR.put(FairyColor.GREEN, STAR_FX_GREEN);
        FAIRY_STAR_BY_COLOR.put(FairyColor.CYAN, STAR_FX_CYAN);
        FAIRY_STAR_BY_COLOR.put(FairyColor.BLUE, STAR_FX_BLUE);
        FAIRY_STAR_BY_COLOR.put(FairyColor.DARKBLUE, STAR_FX_DARKBLUE);
        FAIRY_STAR_BY_COLOR.put(FairyColor.PURPLE, STAR_FX_PURPLE);
        FAIRY_STAR_BY_COLOR.put(FairyColor.PINK, STAR_FX_PINK);
        FAIRY_STAR_BY_COLOR.put(FairyColor.RED, STAR_FX_RED);
        FAIRY_STAR_BY_COLOR.put(FairyColor.BLACK, STAR_FX_BLACK);
    }

    public static SimpleParticleType starFxForFairyColor(FairyColor color) {
        return FAIRY_STAR_BY_COLOR.get(color).get();
    }
}