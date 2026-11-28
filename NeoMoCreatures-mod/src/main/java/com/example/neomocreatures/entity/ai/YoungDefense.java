package com.example.neomocreatures.entity.ai;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Shared rules for animals that defend their young. */
public final class YoungDefense {

    private static final double DEFENDER_RADIUS = 16.0D;

    private YoungDefense() {
    }

    /** An adult may fight anyone except itself, its own species, its owner and creative or spectator players. */
    public static boolean isValidAttacker(Mob defender, LivingEntity attacker) {
        if (attacker == defender || attacker.getType() == defender.getType()) {
            return false;
        }
        if (defender instanceof TamableAnimal tamable && tamable.isOwnedBy(attacker)) {
            return false;
        }
        return !(attacker instanceof Player player && (player.isCreative() || player.isSpectator()));
    }

    /** Calls defendYoung() on every living adult of the baby's species near it. */
    public static void alertAdults(AgeableMob baby, LivingEntity attacker) {
        AABB area = baby.getBoundingBox().inflate(DEFENDER_RADIUS);
        for (AgeableMob adult : baby.level().getEntitiesOfClass(AgeableMob.class, area,
                other -> other != baby && other.getType() == baby.getType() && !other.isBaby() && other.isAlive())) {
            if (adult instanceof YoungDefender defender) {
                defender.defendYoung(attacker);
            }
        }
    }
}