package com.example.neomocreatures.util;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.TamableAnimal;

/** Shared rule for bred offspring: born tamed to the parents' owner, who is then asked to name it. */
public final class TamedOffspring {

    private TamedOffspring() {
    }

    /**
     * Tames the newborn to the owner of whichever parent has one and opens the naming screen
     * for that owner. Does nothing when neither parent is tamed.
     */
    public static void inheritOwner(TamableAnimal newborn, TamableAnimal parent, @Nullable AgeableMob otherParent) {
        UUID ownerId = parent.getOwnerUUID();
        if (ownerId == null && otherParent instanceof TamableAnimal other) {
            ownerId = other.getOwnerUUID();
        }
        if (ownerId == null) {
            return;
        }
        newborn.setTame(true, false);
        newborn.setOwnerUUID(ownerId);
        NamingHelper.promptRename(newborn, ownerId);
    }
}