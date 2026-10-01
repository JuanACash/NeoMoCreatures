package com.example.neomocreatures.entity;

import net.minecraft.nbt.CompoundTag;

/**
 * A tamed pet that can be stored inside an item (Pet Amulet or Fish Net)
 * and recreated from the data that item holds.
 */
public interface StorablePet {

    /**
     * Applies the stored data to a freshly created instance.
     * Called after the entity is positioned and before it is added to the world.
     */
    void restoreFromStorage(CompoundTag tag);
}
