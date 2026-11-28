package com.example.neomocreatures.entity.ai;

import com.example.neomocreatures.Config;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;

/**
 * Lets a tamed pet fight for its owner like a vanilla wolf: it attacks whoever hurts the owner
 * and whoever the owner hurts. A sitting pet stays seated.
 */
public final class OwnerDefense {

    private static final int PROTECT_OWNER_PRIORITY = 1;
    private static final int ASSIST_OWNER_PRIORITY = 2;

    private OwnerDefense() {
    }

    public static boolean isEnabled() {
        return Config.CREATURES.petsDefendOwner.get();
    }

    /** Adds the two owner defense goals to a pet's target selector. */
    public static void addGoals(GoalSelector targetSelector, TamableAnimal pet) {
        targetSelector.addGoal(PROTECT_OWNER_PRIORITY, new ProtectOwnerGoal(pet));
        targetSelector.addGoal(ASSIST_OWNER_PRIORITY, new AssistOwnerGoal(pet));
    }

    /** Same exclusions as a vanilla wolf. */
    public static boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) {
            return false;
        }
        if (target instanceof AbstractHorse horse && horse.isTamed()) {
            return false;
        }
        if (target instanceof TamableAnimal other && other.isTame()) {
            return false;
        }
        return !(target instanceof Player player && owner instanceof Player ownerPlayer
                && !ownerPlayer.canHarmPlayer(player));
    }

    private static boolean canAct(TamableAnimal pet) {
        return isEnabled() && !pet.isOrderedToSit() && !pet.isInSittingPose();
    }

    /**
     * Remembers the owner's fight that was already going on when the pet appeared (for example when it
     * comes out of an amulet or its chunk loads), so the pet does not join it. A pet that cannot act
     * keeps ignoring every fight until it can.
     */
    private static final class IgnoredFight {
        private int timestamp;
        private boolean captured;

        /** True when the owner's latest fight started after the pet appeared and while it could act. */
        boolean isNew(int ownerTimestamp, boolean canAct) {
            if (!canAct || !this.captured) {
                this.timestamp = ownerTimestamp;
                this.captured = true;
            }
            return canAct && ownerTimestamp != this.timestamp;
        }
    }

    /** Attacks whoever hurt the owner. */
    private static final class ProtectOwnerGoal extends OwnerHurtByTargetGoal {
        private final TamableAnimal pet;
        private final IgnoredFight ignoredFight = new IgnoredFight();

        ProtectOwnerGoal(TamableAnimal pet) {
            super(pet);
            this.pet = pet;
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.pet.getOwner();
            if (owner == null) {
                return false;
            }
            boolean newFight = this.ignoredFight.isNew(owner.getLastHurtByMobTimestamp(), canAct(this.pet));
            return newFight && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return canAct(this.pet) && super.canContinueToUse();
        }
    }

    /** Attacks whoever the owner hurt. */
    private static final class AssistOwnerGoal extends OwnerHurtTargetGoal {
        private final TamableAnimal pet;
        private final IgnoredFight ignoredFight = new IgnoredFight();

        AssistOwnerGoal(TamableAnimal pet) {
            super(pet);
            this.pet = pet;
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.pet.getOwner();
            if (owner == null) {
                return false;
            }
            boolean newFight = this.ignoredFight.isNew(owner.getLastHurtMobTimestamp(), canAct(this.pet));
            return newFight && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return canAct(this.pet) && super.canContinueToUse();
        }
    }
}