package com.example.neomocreatures.event;

import com.example.neomocreatures.entity.FireproofMount;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * While riding a fireproof mount (see {@link FireproofMount}), the rider doesn't catch fire
 * or take fire/lava damage.
 */
public class NightmareRiderFireImmunityHandler {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().is(DamageTypeTags.IS_FIRE) && isOnFireproofMount(victim)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide || !(entity instanceof LivingEntity living) || !living.isOnFire()) {
            return;
        }
        if (isOnFireproofMount(living)) {
            living.clearFire();
        }
    }

    /** Shared with the client overlay handler so both sides always agree. */
    public static boolean isOnFireproofMount(Entity entity) {
        return entity.getVehicle() instanceof FireproofMount mount && mount.protectsRiderFromFire();
    }
}