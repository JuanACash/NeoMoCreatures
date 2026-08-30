package com.example.examplemod.item;

import com.example.examplemod.entity.MoCHorseEntity;
import com.example.examplemod.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class PetAmuletItem extends Item {

    public PetAmuletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // Solo funciona en algo tameado por ESTE jugador (caballos del mod o TamableAnimal, presente o futuro)
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

        // Quita todo lo que traiga puesto, sin importar el tipo de entidad
        if (target instanceof MoCHorseEntity horse) {
            horse.dropSaddleAndArmor();
            horse.dropChestAndContents();
        }
        // (cuando lleguen entidades nuevas como el wyvern, si tienen su propio equipo,
        //  se les puede añadir aquí su propio "dropAllEquipment()")

        CompoundTag tag = new CompoundTag();
        tag.putString("EntityType", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
        tag.putFloat("Health", target.getHealth());
        tag.putBoolean("Adult", !target.isBaby());
        if (target.hasCustomName()) {
            tag.putString("Name", target.getCustomName().getString());
        }
        tag.putUUID("OwnerUUID", player.getUUID());
        if (target instanceof MoCHorseEntity horse) {
            tag.putString("Species", horse.getSpecies().name());
            if (horse.getSpecies() == com.example.examplemod.breeding.MoCHorseGenetics.Species.FAIRY_HORSE) {
                tag.putString("FairyColor", horse.getFairyColor().name());
            }
        }

        ItemStack filled = new ItemStack(ModItems.PET_AMULET_FULL.get());
        filled.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        stack.shrink(1);
        if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
        target.discard();
        return InteractionResult.SUCCESS;
    }
}