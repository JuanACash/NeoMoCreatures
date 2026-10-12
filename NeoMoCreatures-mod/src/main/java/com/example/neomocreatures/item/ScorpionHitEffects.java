package com.example.neomocreatures.item;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.example.neomocreatures.Config;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/** Shared on-hit behaviour of the scorpion weapons, so every weapon honours the same config option. */
public final class ScorpionHitEffects {

    private ScorpionHitEffects() {
    }

    /** Applies the weapon's status effect and fire to the target, unless weapon effects are disabled. */
    public static void apply(LivingEntity target, @Nullable Supplier<MobEffectInstance> effect, int fireSeconds) {
        if (target.level().isClientSide || !Config.GENERAL.weaponEffects.get()) {
            return;
        }
        if (effect != null) {
            target.addEffect(effect.get());
        }
        if (fireSeconds > 0) {
            target.igniteForSeconds(fireSeconds);
        }
    }
}