package com.example.neomocreatures.util;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

/**
 * Shared NBT read/write for the data every storable pet keeps in an amulet.
 * Each pet still writes and reads its own variant-specific keys.
 */
public final class PetStorageTags {

    private static final int DEFAULT_BABY_AGE = -24000;

    private PetStorageTags() {
        // Utility class, no instances
    }

    /** Writes health, name and owner. For pets that do not store their age. */
    public static void writeIdentity(CompoundTag tag, LivingEntity pet, UUID owner) {
        tag.putFloat("Health", pet.getHealth());
        tag.putString("Name", pet.getCustomName() != null ? pet.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
    }

    /** Writes health, age, name and owner into the amulet tag. */
    public static void writeCommon(CompoundTag tag, AgeableMob pet, UUID owner) {
        writeIdentity(tag, pet, owner);
        tag.putBoolean("Adult", !pet.isBaby());
        tag.putInt("Age", pet.getAge());
    }

    /** Tames the pet and restores owner, health and name. For pets that do not store their age. */
    public static void restoreIdentity(CompoundTag tag, TamableAnimal pet) {
        restoreIdentity(tag, pet, false);
    }

    /** Same as above, optionally keeping the restored name always visible (fish-net pets). */
    public static void restoreIdentity(CompoundTag tag, TamableAnimal pet, boolean showName) {
        pet.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            pet.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        pet.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            pet.setCustomName(Component.literal(tag.getString("Name")));
            if (showName) {
                pet.setCustomNameVisible(true);
            }
        }
    }

    /** Tames the pet and restores owner, health, name and age from the amulet tag. */
    public static void restoreCommon(CompoundTag tag, TamableAnimal pet) {
        restoreIdentity(tag, pet);
        if (tag.contains("Age")) {
            pet.setAge(tag.getInt("Age"));
        } else {
            pet.setAge(tag.getBoolean("Adult") ? 0 : DEFAULT_BABY_AGE);
        }
    }
}