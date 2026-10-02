package com.example.neomocreatures.util;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Shared save/load logic for mount chests and saddles.
 * The NBT layout is exactly the one the mounts already used, so existing worlds load unchanged.
 */
public final class MoCInventoryUtil {

    private MoCInventoryUtil() {
        // Utility class, no instances
    }

    /** Saves every non-empty slot as {Slot: int, Item: compound}. */
    public static ListTag saveSlots(Container container, HolderLookup.Provider registries) {
        ListTag items = new ListTag();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", slot);
                itemTag.put("Item", stack.save(registries, new CompoundTag()));
                items.add(itemTag);
            }
        }
        return items;
    }

    /** Restores slots written by {@link #saveSlots}; out-of-range slots are ignored. */
    public static void loadSlots(Container container, ListTag items, HolderLookup.Provider registries) {
        for (int i = 0; i < items.size(); i++) {
            CompoundTag itemTag = items.getCompound(i);
            int slot = itemTag.getInt("Slot");
            ItemStack stack = ItemStack.parse(registries, itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
            if (slot >= 0 && slot < container.getContainerSize()) {
                container.setItem(slot, stack);
            }
        }
    }

    /** The exact saddle item a mount wears, or the vanilla saddle for mounts saved before it was tracked. */
    public static Item saddleItemOrDefault(@Nullable ResourceLocation saddleItemId) {
        return saddleItemId != null ? BuiltInRegistries.ITEM.get(saddleItemId) : Items.SADDLE;
    }
}
