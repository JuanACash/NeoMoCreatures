package com.example.neomocreatures.entity.horse;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;

/**
 * Picks the sound a horse makes from its species and undead state.
 * Each check keeps the priority order the horse used before (ghost vs undead).
 */
public final class HorseSounds {

    private HorseSounds() {
        // Utility class, no instances
    }

    public static SoundEvent ambient(Species species, boolean undead, RandomSource random) {
        if (undead) {
            return random.nextBoolean() ? ModSounds.HORSE_UNDEAD_GRUNT1.get() : ModSounds.HORSE_UNDEAD_GRUNT2.get();
        }
        if (isGhost(species)) {
            return switch (random.nextInt(3)) {
                case 0 -> ModSounds.HORSE_GHOST_GRUNT1.get();
                case 1 -> ModSounds.HORSE_GHOST_GRUNT2.get();
                default -> ModSounds.HORSE_GHOST_GRUNT3.get();
            };
        }
        return switch (species) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_GRUNT.get();
            case ZEBRA, ZORSE -> ModSounds.ZEBRA_GRUNT.get();
            default -> ModSounds.HORSE_GRUNT.get();
        };
    }

    public static SoundEvent hurt(Species species, boolean undead) {
        if (isGhost(species)) return ModSounds.HORSE_GHOST_HURT.get();
        if (undead) return ModSounds.HORSE_UNDEAD_HURT.get();
        return switch (species) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_HURT.get();
            case ZEBRA, ZORSE -> ModSounds.ZEBRA_HURT.get();
            default -> ModSounds.HORSE_HURT.get();
        };
    }

    public static SoundEvent death(Species species, boolean undead) {
        if (undead) return ModSounds.HORSE_UNDEAD_DEATH.get();
        if (isGhost(species)) return ModSounds.HORSE_GHOST_DEATH.get();
        return switch (species) {
            case DONKEY, MULE, ZONKY -> ModSounds.DONKEY_DEATH.get();
            default -> ModSounds.HORSE_DEATH.get();
        };
    }

    /** Played when an untamed horse throws its rider off (vanilla makeMad()). */
    public static SoundEvent angry(Species species, boolean undead) {
        if (undead) return ModSounds.HORSE_MAD_UNDEAD.get();
        if (isGhost(species)) return ModSounds.HORSE_GHOST_MAD.get();
        return ModSounds.HORSE_MOB_AGGRESSIVE.get();
    }

    private static boolean isGhost(Species species) {
        return species == Species.GHOST || species == Species.GHOST_WINGED;
    }
}