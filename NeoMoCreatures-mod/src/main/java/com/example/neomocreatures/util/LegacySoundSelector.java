package com.example.neomocreatures.util;

import java.util.function.Supplier;

import com.example.neomocreatures.Config;

import net.minecraft.sounds.SoundEvent;

/** Picks between the current and the legacy version of a sound, following the legacy sounds config. */
public final class LegacySoundSelector {

    private LegacySoundSelector() {
    }

    public static SoundEvent choose(Supplier<SoundEvent> current, Supplier<SoundEvent> legacy) {
        return Config.LEGACY.legacySounds.get() ? legacy.get() : current.get();
    }
}