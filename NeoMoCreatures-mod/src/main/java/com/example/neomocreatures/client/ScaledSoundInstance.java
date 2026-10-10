package com.example.neomocreatures.client;

import javax.annotation.Nullable;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

/** Wraps a sound instance and multiplies its volume, leaving everything else untouched. */
public class ScaledSoundInstance implements SoundInstance {

    protected final SoundInstance delegate;
    private final float scale;

    protected ScaledSoundInstance(SoundInstance delegate, float scale) {
        this.delegate = delegate;
        this.scale = scale;
    }

    /** Wraps the sound, keeping tickable sounds ticking. */
    public static SoundInstance wrap(SoundInstance sound, float scale) {
        return sound instanceof TickableSoundInstance tickable
                ? new Tickable(tickable, scale)
                : new ScaledSoundInstance(sound, scale);
    }

    @Override
    public ResourceLocation getLocation() {
        return delegate.getLocation();
    }

    @Nullable
    @Override
    public WeighedSoundEvents resolve(SoundManager manager) {
        return delegate.resolve(manager);
    }

    @Override
    public Sound getSound() {
        return delegate.getSound();
    }

    @Override
    public SoundSource getSource() {
        return delegate.getSource();
    }

    @Override
    public boolean isLooping() {
        return delegate.isLooping();
    }

    @Override
    public boolean isRelative() {
        return delegate.isRelative();
    }

    @Override
    public int getDelay() {
        return delegate.getDelay();
    }

    @Override
    public float getVolume() {
        return delegate.getVolume() * scale;
    }

    @Override
    public float getPitch() {
        return delegate.getPitch();
    }

    @Override
    public double getX() {
        return delegate.getX();
    }

    @Override
    public double getY() {
        return delegate.getY();
    }

    @Override
    public double getZ() {
        return delegate.getZ();
    }

    @Override
    public Attenuation getAttenuation() {
        return delegate.getAttenuation();
    }

    @Override
    public boolean canStartSilent() {
        return delegate.canStartSilent();
    }

    @Override
    public boolean canPlaySound() {
        return delegate.canPlaySound();
    }

    private static final class Tickable extends ScaledSoundInstance implements TickableSoundInstance {

        private final TickableSoundInstance tickable;

        private Tickable(TickableSoundInstance tickable, float scale) {
            super(tickable, scale);
            this.tickable = tickable;
        }

        @Override
        public boolean isStopped() {
            return tickable.isStopped();
        }

        @Override
        public void tick() {
            tickable.tick();
        }
    }
}