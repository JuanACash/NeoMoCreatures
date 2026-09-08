package com.example.neomocreatures.entity.scorpion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The 5 scorpion colors — Dirt, Cave, Nether, Frost (all 4 spawn naturally),
 * plus Undead (only obtainable via Essence of Darkness on a tamed scorpion,
 * per the wiki — never spawns naturally). Stats are uniform across all of
 * them; only the sting's status effect differs by color, exactly like the
 * manticore. Nether is fire immune (matches the original's
 * checkSpawningBiome() setting the fire-immune flag for Nether spawns).
 */
public enum ScorpionVariant {

    DIRT(0, "scorpion_dirt", false) {
        @Override
        public void applySting(LivingEntity target, boolean stingerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 70, 0));
        }
    },
    CAVE(1, "scorpion_cave", false) {
        @Override
        public void applySting(LivingEntity target, boolean stingerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 70, 0));
        }
    },
    NETHER(2, "scorpion_fire", true) {
        @Override
        public void applySting(LivingEntity target, boolean stingerInNether) {
            if (target instanceof Player && !stingerInNether) {
                target.igniteForSeconds(15);
            }
        }
    },
    FROST(3, "scorpion_frost", false) {
        @Override
        public void applySting(LivingEntity target, boolean stingerInNether) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 0));
        }
    },
    UNDEAD(4, "scorpion_undead", false) {
        @Override
        public void applySting(LivingEntity target, boolean stingerInNether) {
            // Undead scorpions are passive per the wiki — never sting unprompted.
        }
    };

    private final int id;
    private final String textureName;
    private final boolean fireImmune;

    ScorpionVariant(int id, String textureName, boolean fireImmune) {
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

    public abstract void applySting(LivingEntity target, boolean stingerInNether);

    public static ScorpionVariant byId(int id) {
        for (ScorpionVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return DIRT;
    }
}