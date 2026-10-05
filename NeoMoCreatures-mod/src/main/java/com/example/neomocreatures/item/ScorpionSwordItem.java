package com.example.neomocreatures.item;

import java.util.function.Supplier;

import com.example.neomocreatures.Config;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/** Diamond-tier base sword that applies a status effect, or sets fire, on hit. */
public class ScorpionSwordItem extends SwordItem {

    private final Supplier<MobEffectInstance> effect;
    private final int fireSeconds;

    public ScorpionSwordItem(Tier tier, Properties properties, Supplier<MobEffectInstance> effect) {
        this(tier, properties, effect, 0);
    }

    public ScorpionSwordItem(Tier tier, Properties properties, int fireSeconds) {
        this(tier, properties, null, fireSeconds);
    }

    private ScorpionSwordItem(Tier tier, Properties properties, Supplier<MobEffectInstance> effect, int fireSeconds) {
        super(tier, properties);
        this.effect = effect;
        this.fireSeconds = fireSeconds;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide && Config.GENERAL.weaponEffects.get()) {
            if (this.effect != null) {
                target.addEffect(this.effect.get());
            }
            if (this.fireSeconds > 0) {
                target.igniteForSeconds(this.fireSeconds);
            }
        }
        return result;
    }
}