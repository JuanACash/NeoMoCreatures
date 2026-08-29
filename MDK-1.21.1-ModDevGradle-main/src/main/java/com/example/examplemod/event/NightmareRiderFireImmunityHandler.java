package com.example.examplemod.event;

import com.example.examplemod.breeding.MoCHorseGenetics.Species;
import com.example.examplemod.entity.MoCHorseEntity;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Mientras un jugador va montado en un Nightmare, no debe quemarse
 * ni tomar daño por fuego/lava (el Nightmare protege a su jinete).
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