package com.example.neomocreatures.util;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

/**
 * Ownership rules for every tameable creature. Vanilla splits them between
 * {@link TamableAnimal} and {@link AbstractHorse}; this class hides that split so callers
 * never branch on the concrete type.
 */
public final class PetOwnershipUtil {

    private PetOwnershipUtil() {
        // Utility class, no instances
    }

    /** True if the entity is a tameable creature owned by the given player. */
    public static boolean isOwnedBy(Entity entity, UUID playerId) {
        return isTameable(entity)
                && entity instanceof OwnableEntity ownable
                && playerId.equals(ownable.getOwnerUUID());
    }

    /** True for a tamed creature that lost its owner (Scroll of Sale / Reset Owner). */
    public static boolean isTamedWithoutOwner(Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            return tamable.isTame() && tamable.getOwnerUUID() == null;
        }
        if (entity instanceof AbstractHorse horse) {
            return horse.isTamed() && horse.getOwnerUUID() == null;
        }
        return false;
    }

    /** Sets or clears the owner. Does nothing for entities that are not tameable. */
    public static void setOwner(Entity entity, @Nullable UUID ownerId) {
        if (entity instanceof TamableAnimal tamable) {
            tamable.setOwnerUUID(ownerId);
        } else if (entity instanceof AbstractHorse horse) {
            horse.setOwnerUUID(ownerId);
        }
    }

    private static boolean isTameable(Entity entity) {
        return entity instanceof TamableAnimal || entity instanceof AbstractHorse;
    }
}