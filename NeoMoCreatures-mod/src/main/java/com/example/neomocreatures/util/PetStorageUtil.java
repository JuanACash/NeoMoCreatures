package com.example.neomocreatures.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;

/**
 * Shared logic for storing a tamed pet inside an item (Pet Amulet, Fish Net)
 * and removing it from the world. Each pet still builds its own data tag.
 */
public final class PetStorageUtil {

    private PetStorageUtil() {
        // Utility class, no instances
    }

    /** Creates a filled item holding the pet's data. */
    public static ItemStack createFilledItem(ItemLike filledItem, CompoundTag petData) {
        ItemStack filled = new ItemStack(filledItem);
        filled.set(DataComponents.CUSTOM_DATA, CustomData.of(petData));
        return filled;
    }

    /**
     * Replaces the whole stack in the player's hand with the filled item
     * and removes the pet (Pet Amulet style).
     */
    public static void storeReplacingHeldItem(Player player, InteractionHand hand, Entity pet,
                                              ItemLike filledItem, CompoundTag petData) {
        player.setItemInHand(hand, createFilledItem(filledItem, petData));
        pet.discard();
    }

    /**
     * Consumes one empty net (unless in creative), gives the filled net back
     * and removes the pet. The filled net goes to the hand if it is now empty,
     * otherwise to the inventory, or is dropped when the inventory is full.
     */
    public static void storeConsumingOne(Player player, InteractionHand hand, ItemStack emptyItem, Entity pet,
                                         ItemLike filledItem, CompoundTag petData) {
        ItemStack filled = createFilledItem(filledItem, petData);
        if (!player.getAbilities().instabuild) {
            emptyItem.shrink(1);
        }
        if (emptyItem.isEmpty()) {
            player.setItemInHand(hand, filled);
        } else if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
        pet.discard();
    }
}
