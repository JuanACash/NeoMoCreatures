package com.example.neomocreatures.entity.manticore;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * The 5 manticore colors — Plain (green), Dark (black), Frost (blue/snow),
 * Fire (red), and Toxic. Per the wiki: every color stings with Poison for 3
 * seconds EXCEPT Snow/Frost, which applies Slowness instead. Fire is a special
 * case on top of that — its sting applies Wither instead of Poison.
 */
public enum ManticoreVariant {

    PLAIN(0, "manticore_plain", 40.0D, 7.0D, 5, false) {
        @Override
        public void applyStingEffect(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 0));
        }
    },
    DARK(1, "manticore_dark", 35.0D, 6.5D, 5, false) {
        @Override
        public void applyStingEffect(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 0));
        }
    },
    FROST(2, "manticore_frost", 50.0D, 6.5D, 5, false) {
        @Override
        public void applyStingEffect(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3 * 20, 0));
        }
    },
    FIRE(3, "manticore_fire", 50.0D, 7.5D, 10, true) {
        @Override
        public void applyStingEffect(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 0));
        }
    },
    TOXIC(4, "manticore_toxic", 45.0D, 6.5D, 5, false) {
        @Override
        public void applyStingEffect(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 0));
        }
    };

    private final int id;
    private final String textureName;
    private final double maxHealth;
    private final double attackDamage;
    private final int xpValue;
    private final boolean fireImmune;

    ManticoreVariant(int id, String textureName, double maxHealth, double attackDamage, int xpValue, boolean fireImmune) {
        this.id = id;
        this.textureName = textureName;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.xpValue = xpValue;
        this.fireImmune = fireImmune;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public int getXpValue() {
        return xpValue;
    }

    public boolean isFireImmune() {
        return fireImmune;
    }

    /** Whatever status effect this color's sting applies — only called on the ~20% chance roll. */
    public abstract void applyStingEffect(LivingEntity target);

    public static ManticoreVariant byId(int id) {
        for (ManticoreVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return PLAIN;
    }
}