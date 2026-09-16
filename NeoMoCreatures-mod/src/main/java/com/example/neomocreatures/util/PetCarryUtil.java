package com.example.neomocreatures.util;

import com.example.neomocreatures.entity.CarriedPet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PetCarryUtil {

    private PetCarryUtil() {
    }

    /** True if this player is already carrying any CarriedPet (Kitty, Bunny, etc). */
    public static boolean isAlreadyCarryingAPet(Player player) {
        for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(5.0D))) {
            if (nearby instanceof CarriedPet pet && pet.isHeld() && pet.getHolder() == player) {
                return true;
            }
        }
        return false;
    }
}