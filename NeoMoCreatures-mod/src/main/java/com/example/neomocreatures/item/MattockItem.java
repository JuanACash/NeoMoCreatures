package com.example.neomocreatures.item;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * Pickaxe + shovel + hoe in one tool. Mining speed comes from the mattock block tag,
 * right click flattens like a shovel and sneaking + right click tills like a hoe.
 * Optionally applies the same on-hit effect as the scorpion weapons.
 */
public class MattockItem extends DiggerItem {

    @Nullable
    private final Supplier<MobEffectInstance> effect;
    private final int fireSeconds;

    public MattockItem(Tier tier, Properties properties) {
        this(tier, properties, null, 0);
    }

    public MattockItem(Tier tier, Properties properties, Supplier<MobEffectInstance> effect) {
        this(tier, properties, effect, 0);
    }

    public MattockItem(Tier tier, Properties properties, int fireSeconds) {
        this(tier, properties, null, fireSeconds);
    }

    private MattockItem(Tier tier, Properties properties, @Nullable Supplier<MobEffectInstance> effect, int fireSeconds) {
        super(tier, ModTags.MINEABLE_WITH_MATTOCK, properties);
        this.effect = effect;
        this.fireSeconds = fireSeconds;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Reuse the vanilla behaviour instead of duplicating it
        boolean tilling = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        return (tilling ? Items.IRON_HOE : Items.IRON_SHOVEL).useOn(context);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        ScorpionHitEffects.apply(target, this.effect, this.fireSeconds);
        return result;
    }
}