package com.example.examplemod.item;

import com.example.examplemod.entity.MoCHorseEntity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ScrollOfFreedomItem extends Item {

    public ScrollOfFreedomItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // Genérico contra TamableAnimal/AbstractHorse, igual que PetAmuletItem,
        // para que funcione con cualquier mob tameable futuro sin tocar este archivo.
        boolean isMoCTameable = target instanceof AbstractHorse || target instanceof TamableAnimal;
        if (!isMoCTameable || !(target instanceof OwnableEntity ownable)) {
            return InteractionResult.PASS;
        }
        if (ownable.getOwnerUUID() == null || !ownable.getOwnerUUID().equals(player.getUUID())) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Suelta silla/armadura/cofre antes de liberarlo
        if (target instanceof MoCHorseEntity horse) {
            horse.dropSaddleAndArmor();
            horse.dropChestAndContents();
        }
        // (futuras entidades con su propio equipo: añadir su drop aquí)

        if (target instanceof TamableAnimal tamable) {
            tamable.setOwnerUUID(null);
            tamable.setTame(false, true);
        } else if (target instanceof AbstractHorse horse) {
            horse.setOwnerUUID(null);
            horse.setTamed(false);
        }
        target.setCustomName(null);
        target.setCustomNameVisible(false);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}