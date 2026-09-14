package com.example.neomocreatures.entity;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Porta isZebraRunning() del original: una zebra salvaje (no tamed) huye del
 * jugador mas cercano dentro de 8 bloques, salvo que ese jugador vaya montado
 * en Horse Tier4, Zebra o Zorse (ver MoCHorseEntity.isExemptZebraRider).
 */
public class ZebraFleeGoal extends Goal {

    private static final double DETECT_RADIUS = 8.0D;
    private static final double LOSE_INTEREST_RADIUS = 12.0D;
    private static final double FLEE_SPEED = 1.4D;
    private static final double FLEE_DISTANCE = 16.0D;

    private final MoCHorseEntity zebra;
    private Player fleeingFrom;

    public ZebraFleeGoal(MoCHorseEntity zebra) {
        this.zebra = zebra;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (zebra.getSpecies() != Species.ZEBRA || zebra.isTamed()) {
            return false;
        }
        Player nearest = zebra.level().getNearestPlayer(zebra, DETECT_RADIUS);
        if (nearest == null || MoCHorseEntity.isExemptZebraRider(nearest)) {
            return false;
        }
        this.fleeingFrom = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !zebra.isTamed()
                && fleeingFrom != null && fleeingFrom.isAlive()
                && zebra.distanceToSqr(fleeingFrom) < LOSE_INTEREST_RADIUS * LOSE_INTEREST_RADIUS
                && !MoCHorseEntity.isExemptZebraRider(fleeingFrom);
    }

    @Override
    public void start() {
        zebra.getNavigation().stop();
        zebra.setFleeing(true);
    }

    @Override
    public void tick() {
        Vec3 away = zebra.position().subtract(fleeingFrom.position());
        if (away.lengthSqr() < 1.0E-4) {
            away = new Vec3(zebra.getRandom().nextDouble() - 0.5, 0.0D, zebra.getRandom().nextDouble() - 0.5);
        }
        Vec3 target = zebra.position().add(away.normalize().scale(FLEE_DISTANCE));
        zebra.getNavigation().moveTo(target.x, target.y, target.z, FLEE_SPEED);
    }

    @Override
    public void stop() {
        zebra.setFleeing(false);
        this.fleeingFrom = null;
        zebra.getNavigation().stop();
    }
}