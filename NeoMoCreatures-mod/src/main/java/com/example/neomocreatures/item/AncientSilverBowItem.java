package com.example.neomocreatures.item;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

/**
 * Bow that shoots faster, harder and more accurately than the vanilla one.
 * Only the stat multipliers are customised; the rest of the shooting flow is vanilla.
 */
public class AncientSilverBowItem extends BowItem {

    public static final int DURABILITY = 720;
    public static final float DAMAGE_MULTIPLIER = 1.30F;
    public static final float VELOCITY_MULTIPLIER = 1.2F;
    public static final float INACCURACY_MULTIPLIER = 0.8F;
    /** Values below 1.0 make the bow reach full draw sooner. */
    public static final float DRAW_TIME_MULTIPLIER = 0.8F;

    private final Supplier<Ingredient> repairIngredient;

    public AncientSilverBowItem(Properties properties, Supplier<Ingredient> repairIngredient) {
        super(properties);
        this.repairIngredient = repairIngredient;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeLeft) {
        // Scale the charged time so vanilla power calculation sees a faster draw
        int useDuration = getUseDuration(stack, shooter);
        int scaledCharge = Math.round((useDuration - timeLeft) / DRAW_TIME_MULTIPLIER);
        super.releaseUsing(stack, level, shooter, useDuration - scaledCharge);
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
                                   float inaccuracy, float angle, @Nullable LivingEntity target) {
        if (projectile instanceof AbstractArrow arrow) {
            arrow.setBaseDamage(arrow.getBaseDamage() * DAMAGE_MULTIPLIER);
        }
        super.shootProjectile(shooter, projectile, index, velocity * VELOCITY_MULTIPLIER,
                inaccuracy * INACCURACY_MULTIPLIER, angle, target);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return this.repairIngredient.get().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }
}