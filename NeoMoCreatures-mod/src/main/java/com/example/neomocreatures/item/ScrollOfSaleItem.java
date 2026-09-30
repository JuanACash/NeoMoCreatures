package com.example.neomocreatures.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ScrollOfSaleItem extends Item {

    public ScrollOfSaleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
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

        // Ends up ownerless: the next player who renames it (with a book)
        // becomes the new owner — see the "adopt" hook in ModNetworking
        // and MoCHorseEntity#mobInteract. Doesn't have much use in
        // singleplayer.
        if (target instanceof TamableAnimal tamable) {
            tamable.setOwnerUUID(null);
        } else if (target instanceof AbstractHorse horse) {
            horse.setOwnerUUID(null);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}