package com.example.examplemod.item;

import java.util.function.Supplier;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class ScorpionAxeItem extends AxeItem {

    private final Supplier<MobEffectInstance> effect;
    private final int fireSeconds;

    public ScorpionAxeItem(Tier tier, Properties properties, Supplier<MobEffectInstance> effect) {
        this(tier, properties, effect, 0);
    }

    public ScorpionAxeItem(Tier tier, Properties properties, int fireSeconds) {
        this(tier, properties, null, fireSeconds);
    }

    private ScorpionAxeItem(Tier tier, Properties properties, Supplier<MobEffectInstance> effect, int fireSeconds) {
        super(tier, properties);
        this.effect = effect;
        this.fireSeconds = fireSeconds;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide) {
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