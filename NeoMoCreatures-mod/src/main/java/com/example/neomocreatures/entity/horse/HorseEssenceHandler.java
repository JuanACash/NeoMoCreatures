package com.example.neomocreatures.entity.horse;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Right-click logic of the four essences on a tamed adult horse: species transformations,
 * undead conversion/locking, and healing or love mode on already transformed horses.
 */
public final class HorseEssenceHandler {

    /** Pegasus transformations only work this high up (sky-only species). */
    private static final double MIN_SKY_TRANSFORM_Y = 150.0D;

    /** Species Essence of Undead can turn into (or refresh as) their undead version. */
    private static final Set<Species> UNDEAD_CAPABLE = EnumSet.of(
            Species.HORSE, Species.ZORSE, Species.UNICORN,
            Species.BATHORSE, Species.PEGASUS, Species.DARK_PEGASUS);

    private final MoCHorseEntity horse;

    public HorseEssenceHandler(MoCHorseEntity horse) {
        this.horse = horse;
    }

    /** Result of using the held essence, or null when the item is not an essence that applies here. */
    @Nullable
    public InteractionResult tryInteract(Player player, ItemStack stack) {
        if (!this.horse.isTamed() || this.horse.isBaby()) {
            return null;
        }
        if (stack.is(ModItems.ESSENCE_OF_DARKNESS.get())) {
            return this.useDarkness(player, stack);
        }
        if (stack.is(ModItems.ESSENCE_OF_FIRE.get())) {
            return this.useFire(player, stack);
        }
        if (stack.is(ModItems.ESSENCE_OF_UNDEAD.get())) {
            return this.useUndead(player, stack);
        }
        if (stack.is(ModItems.ESSENCE_OF_LIGHT.get())) {
            return this.useLight(player, stack);
        }
        return null;
    }

    /** Zorse -> Bathorse, Pegasus -> Dark Pegasus (high in the sky); heals Bathorse/Dark Pegasus. */
    @Nullable
    private InteractionResult useDarkness(Player player, ItemStack stack) {
        Species species = this.horse.getSpecies();
        if (species == Species.ZORSE && !this.horse.isTransforming()) {
            return this.transform(player, stack, Species.BATHORSE);
        }
        if (species == Species.BATHORSE || species == Species.DARK_PEGASUS) {
            return this.healOrFallInLove(player, stack);
        }
        if (species == Species.PEGASUS && !this.horse.isTransforming()) {
            return this.transformInSky(player, stack, Species.DARK_PEGASUS);
        }
        return null;
    }

    /** Zorse -> Nightmare; heals a Nightmare. */
    @Nullable
    private InteractionResult useFire(Player player, ItemStack stack) {
        Species species = this.horse.getSpecies();
        if (species == Species.ZORSE && !this.horse.isTransforming()) {
            return this.transform(player, stack, Species.NIGHTMARE);
        }
        if (species == Species.NIGHTMARE) {
            return this.healOrFallInLove(player, stack);
        }
        return null;
    }

    /**
     * Starts the undead conversion, or, on an already undead horse, resets it to the first
     * undead stage and heals it. Blocked once Essence of Light has locked the stage.
     */
    @Nullable
    private InteractionResult useUndead(Player player, ItemStack stack) {
        if (!UNDEAD_CAPABLE.contains(this.horse.getSpecies()) || this.horse.isUndeadLocked()) {
            return null;
        }
        if (!this.horse.level().isClientSide) {
            if (!this.horse.isUndead() && !this.horse.isUndeadTransforming()) {
                this.horse.startUndeadTransform();
                this.horse.consumeEssence(player, stack);
            } else if (!this.horse.isUndeadTransforming()) {
                if (this.horse.getHealth() < this.horse.getMaxHealth()) {
                    this.horse.heal(this.horse.getMaxHealth());
                }
                this.horse.setUndeadStagePublic(MoCHorseEntity.UNDEAD_STAGE_0);
                this.horse.setUndeadDecayTicksPublic(0);
                this.horse.consumeEssence(player, stack);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Locks the current undead stage permanently; Nightmare -> Unicorn, Bathorse -> Pegasus
     * (high in the sky); heals Unicorn/Pegasus/Fairy horse.
     */
    @Nullable
    private InteractionResult useLight(Player player, ItemStack stack) {
        if (this.horse.isUndead() && !this.horse.isUndeadTransforming()) {
            if (!this.horse.level().isClientSide) {
                this.horse.heal(this.horse.getMaxHealth());
                this.horse.setUndeadLockedPublic(true);
                this.horse.consumeEssence(player, stack);
            }
            return InteractionResult.SUCCESS;
        }
        Species species = this.horse.getSpecies();
        if (species == Species.NIGHTMARE && !this.horse.isTransforming()) {
            return this.transform(player, stack, Species.UNICORN);
        }
        if (species == Species.UNICORN || species == Species.PEGASUS || species == Species.FAIRY_HORSE) {
            return this.healOrFallInLove(player, stack);
        }
        if (species == Species.BATHORSE && !this.horse.isTransforming()) {
            return this.transformInSky(player, stack, Species.PEGASUS);
        }
        return null;
    }

    private InteractionResult transform(Player player, ItemStack stack, Species target) {
        if (!this.horse.level().isClientSide) {
            this.horse.startTransform(target);
            this.horse.consumeEssence(player, stack);
        }
        return InteractionResult.SUCCESS;
    }

    /** Pegasus transformations only start high in the sky and without a rider. */
    private InteractionResult transformInSky(Player player, ItemStack stack, Species target) {
        if (this.horse.getY() < MIN_SKY_TRANSFORM_Y || this.horse.isVehicle()) {
            return InteractionResult.PASS;
        }
        return this.transform(player, stack, target);
    }

    /** Heals a hurt horse, otherwise puts it in love mode; does nothing when neither applies. */
    private InteractionResult healOrFallInLove(Player player, ItemStack stack) {
        if (!this.horse.level().isClientSide) {
            if (this.horse.getHealth() < this.horse.getMaxHealth()) {
                this.horse.heal(this.horse.getMaxHealth());
            } else if (!this.horse.isInLove() && this.horse.canFallInLove()) {
                this.horse.setInLove(player);
            } else {
                return InteractionResult.PASS;
            }
            this.horse.consumeEssence(player, stack);
        }
        return InteractionResult.SUCCESS;
    }
}