package com.example.neomocreatures.event;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * While a player is mounted on a Nightmare, they shouldn't catch fire
 * or take fire/lava damage (the Nightmare protects its rider).
 */
public class NightmareRiderFireImmunityHandler {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().is(DamageTypeTags.IS_FIRE) && isRidingNightmare(victim)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide || !(entity instanceof LivingEntity living) || !living.isOnFire()) {
            return;
        }
        if (isRidingNightmare(living)) {
            living.clearFire();
        }
    }

    private static boolean isRidingNightmare(Entity entity) {
        return entity.getVehicle() instanceof MoCHorseEntity horse
                && (horse.getSpecies() == Species.NIGHTMARE || horse.getSpecies() == Species.DARK_PEGASUS);
    }
}