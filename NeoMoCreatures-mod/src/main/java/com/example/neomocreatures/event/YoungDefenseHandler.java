package com.example.neomocreatures.event;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.ai.YoungDefense;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/** When a baby animal is hurt, the adults of its species nearby are told who did it. */
@EventBusSubscriber(modid = NeoMoCreatures.MODID)
public final class YoungDefenseHandler {

    private YoungDefenseHandler() {
    }

    @SubscribeEvent
    public static void onBabyHurt(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof AgeableMob baby) || baby.level().isClientSide || !baby.isBaby()) {
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            YoungDefense.alertAdults(baby, attacker);
        }
    }
}