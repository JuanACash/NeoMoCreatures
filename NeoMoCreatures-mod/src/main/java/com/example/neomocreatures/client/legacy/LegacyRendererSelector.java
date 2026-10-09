package com.example.neomocreatures.client.legacy;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

import java.util.function.BooleanSupplier;

/** Chooses between a legacy and a modern renderer when renderers are (re)built, e.g. on F3+T. */
public final class LegacyRendererSelector {

    private LegacyRendererSelector() {
    }

    public static <T extends Entity> EntityRendererProvider<T> of(BooleanSupplier useLegacy,
                                                                  EntityRendererProvider<T> legacy,
                                                                  EntityRendererProvider<T> modern) {
        return context -> useLegacy.getAsBoolean() ? legacy.create(context) : modern.create(context);
    }
}