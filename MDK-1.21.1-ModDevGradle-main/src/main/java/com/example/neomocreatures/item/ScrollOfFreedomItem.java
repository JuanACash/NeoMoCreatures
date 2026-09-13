package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.MoCBearEntity;
import com.example.neomocreatures.entity.MoCKomodoDragonEntity;

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

        // Drops saddle/armor/chest before freeing it
        if (target instanceof MoCHorseEntity horse) {
            horse.dropSaddleAndArmor();
            horse.dropChestAndContents();
        } else if (target instanceof MoCWyvernEntity wyvern) {
            wyvern.dropSaddleAndArmor();
            wyvern.dropChestAndContents();
        } else if (target instanceof MoCElephantEntity elephant) {
            elephant.dropAllEquipment();
        } else if (target instanceof MoCBigCatEntity bigCat) {
            bigCat.dropAllEquipment();
        } else if (target instanceof MoCManticoreEntity manticore) {
            manticore.dropAllEquipment();
        } else if (target instanceof MoCScorpionEntity scorpion) {
            scorpion.dropAllEquipment();
        } else if (target instanceof MoCOstrichEntity ostrich) {
            ostrich.dropAllEquipment();
        } else if (target instanceof MoCBearEntity bear) {
            bear.dropAllEquipment();
        } else if (target instanceof MoCKomodoDragonEntity komodo) {
            komodo.dropAllEquipment();
        }
        // (future entities with their own equipment: add their drop here)

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