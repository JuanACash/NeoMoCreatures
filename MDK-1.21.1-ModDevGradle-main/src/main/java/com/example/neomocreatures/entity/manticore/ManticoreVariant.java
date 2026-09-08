package com.example.neomocreatures.entity.manticore;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The 4 manticore colors from the real released mod (Plain/green, Dark, Frost,
 * Fire), plus Toxic as our own addition. Stats are uniform across all of them
 * (40 HP, 6 damage). Green and Dark sting with Poison, Frost with Slowness
 * (both 70 ticks, amplifier 0, any LivingEntity target); Fire ignites for 15
 * seconds but ONLY a Player target and ONLY outside the Nether — an exact
 * quirk of the original source. Toxic isn't in the original mod at all, so it
 * shares Plain/Dark's Poison sting.
 */
public enum ManticoreVariant {

    PLAIN(0, "manticore_plain", false) {
        @Override
        public void applySting(LivingEntity target, boolean managerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 70, 0));
        }
    },
    DARK(1, "manticore_dark", false) {
        @Override
        public void applySting(LivingEntity target, boolean managerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 70, 0));
        }
    },
    FROST(2, "manticore_frost", false) {
        @Override
        public void applySting(LivingEntity target, boolean managerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 0));
        }
    },
    FIRE(3, "manticore_fire", true) {
        @Override
        public void applySting(LivingEntity target, boolean managerInNether) {
            if (target instanceof Player && !managerInNether) {
                target.igniteForSeconds(15);
            }
        }
    },
    TOXIC(4, "manticore_toxic", false) {
        @Override
        public void applySting(LivingEntity target, boolean managerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 70, 0));
        }
    };

    private final int id;
    private final String textureName;
    private final boolean fireImmune;

    ManticoreVariant(int id, String textureName, boolean fireImmune) {
        this.id = id;
        this.textureName = textureName;
        this.fireImmune = fireImmune;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public boolean isFireImmune() {
        return fireImmune;
    }

    /** Only called on the ~20% sting-chance roll. */
    public abstract void applySting(LivingEntity target, boolean managerInNether);

    public static ManticoreVariant byId(int id) {
        for (ManticoreVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return PLAIN;
    }
}