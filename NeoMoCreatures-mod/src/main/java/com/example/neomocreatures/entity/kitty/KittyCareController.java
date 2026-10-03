package com.example.neomocreatures.entity.kitty;

import java.util.Comparator;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.example.neomocreatures.entity.MoCKittyEntity;
import com.example.neomocreatures.entity.MoCLitterBoxEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.NamingHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * State machine of a tamed kitty's needs (the original's "kitty states"): eating in its bed,
 * using the litter box, getting curious about and playing with a wool ball, looking for a mate,
 * giving birth and defending its kittens, sleeping and climbing trees.
 * Runs on the server, once per tick, from the kitty's aiStep().
 */
public final class KittyCareController {

    /** Beds, litter boxes and players are searched within this many blocks. */
    private static final double SEARCH_RADIUS = 18.0D;

    private final MoCKittyEntity kitty;
    /** Ticks spent in the current state; reset every time the state changes. */
    private int careTimer;
    @Nullable
    private MoCKittyEntity matePartner;
    @Nullable
    private ItemEntity playTarget;
    @Nullable
    private BlockPos treeTarget;
    private boolean onTree;

    public KittyCareController(MoCKittyEntity kitty) {
        this.kitty = kitty;
    }

    public void resetTimer() {
        this.careTimer = 0;
    }

    /** The wool ball the kitty chases while playing. */
    public void setPlayTarget(@Nullable ItemEntity ball) {
        this.playTarget = ball;
    }

    public void setMatePartner(@Nullable MoCKittyEntity partner) {
        this.matePartner = partner;
    }

    /** Steps 3/4/13 of the original's state machine: seek a filled bed when idle
     *  or aggressive, eat in it, calm down. Litter box (5/6) comes in a later step. */
    public void tick() {
        if (this.kitty.getRandom().nextInt(200) == 0) {
            this.kitty.toggleEmoteIcon();
        }
        if (!this.kitty.isTame() || this.kitty.isBaby()) {
            return;
        }
        switch (this.kitty.getKittyState()) {
            case KittyCareState.STATE_SEEKING_BED -> tickSeekingBed();
            case KittyCareState.STATE_IN_BED -> tickInBed();
            case KittyCareState.STATE_SEEKING_LITTER -> tickSeekingLitter();
            case KittyCareState.STATE_IN_LITTER -> tickInLitter();
            case KittyCareState.STATE_AGGRESSIVE -> tickAggressive();
            case KittyCareState.STATE_CURIOUS -> tickCurious();
            case KittyCareState.STATE_PLAYING -> tickPlaying();
            case KittyCareState.STATE_LOOKING_FOR_MATE -> tickLookingForMate();
            case KittyCareState.STATE_MATING -> tickMating();
            case KittyCareState.STATE_SEEKING_BIRTH_BED -> tickSeekingBirthBed();
            case KittyCareState.STATE_GIVING_BIRTH -> tickGivingBirth();
            case KittyCareState.STATE_DEFENDING_KITTENS -> tickDefendingKittens();
            case KittyCareState.STATE_HELD_LEAD, KittyCareState.STATE_HELD_PLAYER -> this.kitty.tickHeld();
            case KittyCareState.STATE_SLEEPING -> tickSleeping();
            case KittyCareState.STATE_WANTS_TREE -> tickWantsTree();
            case KittyCareState.STATE_STUCK_IN_TREE -> tickStuckInTree();
            default -> tickIdleCare();
        }
    }

    private void tickIdleCare() {
        if (!this.kitty.level().isDay() && this.kitty.getRandom().nextInt(500) == 0) {
            MoCKittyBedEntity bed = findAnyBed(18.0D);
            if (bed == null) {
                this.kitty.setKittyCareState(KittyCareState.STATE_SLEEPING);
            } else {
                double dist = bed.distanceTo(this.kitty);
                if (dist > 2.0F) {
                    this.kitty.getNavigation().moveTo(bed, 1.0D);
                } else if (this.kitty.startRiding(bed)) {
                    this.kitty.setKittyCareState(KittyCareState.STATE_SLEEPING);
                }
            }
            return;
        }
        if (this.kitty.getRandom().nextInt(20) == 0) {
            Player nearby = this.kitty.level().getNearestPlayer(this.kitty, 12D);
            if (nearby != null && nearby.getMainHandItem().is(ModItems.WOOL_BALL.get())) {
                this.kitty.setKittyCareState(KittyCareState.STATE_CURIOUS);
                return;
            }
        }
        if (this.kitty.getHealth() < this.kitty.getMaxHealth() || this.kitty.getRandom().nextInt(3000) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_SEEKING_BED);
            return;
        }
        if (this.kitty.level().canSeeSky(this.kitty.blockPosition()) && this.kitty.getRandom().nextInt(4000) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_WANTS_TREE);
        }
    }

    private void tickSeekingBed() {
        this.careTimer++;
        if (this.careTimer > 500) {
            if (this.kitty.getRandom().nextInt(200) == 0) {
                this.kitty.setKittyCareState(KittyCareState.STATE_AGGRESSIVE);
                return;
            }
            if (this.kitty.getRandom().nextInt(500) == 0) {
                this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
                return;
            }
        }
        if (this.kitty.getRandom().nextInt(20) != 0) {
            return;
        }
        approachAndUseBed();
    }

    private void tickInBed() {
        if (!(this.kitty.getVehicle() instanceof MoCKittyBedEntity bed)) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            return;
        }
        lockRotationToVehicle(bed);
        if (!bed.hasFood() && !bed.hasMilk()) {
            this.kitty.heal(this.kitty.getMaxHealth());
            this.kitty.stopRiding();
            this.kitty.setKittyCareState(KittyCareState.STATE_SEEKING_LITTER);
            return;
        }
        if (this.kitty.getRandom().nextInt(2500) == 0) {
            this.kitty.heal(this.kitty.getMaxHealth());
            this.kitty.stopRiding();
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
        }
    }

    private void tickAggressive() {
        MoCKittyBedEntity bed = findFilledBed(SEARCH_RADIUS);
        if (bed != null) {
            this.kitty.setTarget(null);
            double dist = bed.distanceTo(this.kitty);
            if (dist > 2.0F) {
                this.kitty.getNavigation().moveTo(bed, 1.0D);
            } else if (this.kitty.startRiding(bed)) {
                this.kitty.setKittyCareState(KittyCareState.STATE_IN_BED);
            }
            return;
        }
        Player nearest = this.kitty.level().getNearestPlayer(this.kitty, SEARCH_RADIUS);
        this.kitty.setTarget(nearest);
        if (nearest == null || this.kitty.getRandom().nextInt(500) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
        }
    }

    private void tickLookingForMate() {
        this.careTimer++;
        if (this.kitty.getRandom().nextInt(20) == 0) {
            MoCKittyEntity candidate = this.kitty.level().getEntitiesOfClass(MoCKittyEntity.class,
                            this.kitty.getBoundingBox().inflate(16.0D, 6.0D, 16.0D),
                            k -> k != this.kitty && k.getKittyState() == KittyCareState.STATE_LOOKING_FOR_MATE)
                    .stream()
                    .min(Comparator.comparingDouble(this.kitty::distanceToSqr))
                    .orElse(null);
            if (candidate != null) {
                if (this.kitty.distanceToSqr(candidate) < 4.0D) {
                    this.matePartner = candidate;
                    candidate.setMatePartner(this.kitty);
                    this.kitty.setKittyCareState(KittyCareState.STATE_MATING);
                    candidate.setKittyCareState(KittyCareState.STATE_MATING);
                } else {
                    this.kitty.getNavigation().moveTo(candidate, 1.0D);
                }
            }
        }
        if (this.careTimer > 2000) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
        }
    }

    private void tickMating() {
        if (this.matePartner == null || !this.matePartner.isAlive() || this.matePartner.getKittyState() != KittyCareState.STATE_MATING) {
            this.kitty.setKittyCareState(KittyCareState.STATE_LOOKING_FOR_MATE);
            return;
        }
        if (this.kitty.getRandom().nextInt(50) == 0) {
            this.kitty.startSwing();
        }
        double dist = this.matePartner.distanceTo(this.kitty);
        if (dist < 5.0D) {
            this.careTimer++;
        }
        if (this.careTimer > 500 && this.kitty.getRandom().nextInt(50) == 0) {
            this.matePartner.setKittyCareState(KittyCareState.STATE_IDLE);
            this.kitty.setKittyCareState(KittyCareState.STATE_SEEKING_BIRTH_BED);
        }
    }

    private void tickSeekingBirthBed() {
        if (this.kitty.getRandom().nextInt(20) != 0) {
            return;
        }
        MoCKittyBedEntity bed = findAnyBed(SEARCH_RADIUS);
        if (bed == null) {
            return;
        }
        double dist = bed.distanceTo(this.kitty);
        if (dist > 2.0F) {
            this.kitty.getNavigation().moveTo(bed, 1.0D);
            return;
        }
        if (this.kitty.startRiding(bed)) {
            this.kitty.setKittyCareState(KittyCareState.STATE_GIVING_BIRTH);
        }
    }

    private void tickGivingBirth() {
        if (this.kitty.getVehicle() == null) {
            this.kitty.setKittyCareState(KittyCareState.STATE_SEEKING_BIRTH_BED);
            return;
        }
        this.kitty.setYRot(180F);
        this.careTimer++;
        if (this.careTimer <= 1000) {
            return;
        }
        int litterSize = this.kitty.getRandom().nextInt(3) + 1;
        for (int i = 0; i < litterSize; i++) {
            MoCKittyEntity kitten = ModEntities.MOC_KITTY.get().create((ServerLevel) this.kitty.level());
            if (kitten == null) {
                continue;
            }
            KittyVariant kittenVariant = this.kitty.getRandom().nextBoolean() ? this.kitty.getVariant() : KittyVariant.rollNatural(this.kitty.getRandom());
            kitten.setVariant(kittenVariant);
            kitten.moveTo(this.kitty.getX(), this.kitty.getY(), this.kitty.getZ(), 0F, 0F);
            kitten.setBaby(true);
            this.kitty.level().addFreshEntity(kitten);
            this.kitty.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
            if (this.kitty.getOwnerUUID() != null) {
                kitten.setOwnerUUID(this.kitty.getOwnerUUID());
                kitten.setTame(true, true);
                NamingHelper.promptRename(kitten, this.kitty.getOwnerUUID());
            }
        }
        this.kitty.stopRiding();
        this.kitty.setKittyCareState(KittyCareState.STATE_DEFENDING_KITTENS);
    }

    private void tickDefendingKittens() {
        this.careTimer++;
        if (this.careTimer > 2000) {
            boolean anyKittensNearby = !this.kitty.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitty.getBoundingBox().inflate(24.0D, 8.0D, 24.0D), MoCKittyEntity::isBaby).isEmpty();
            if (!anyKittensNearby) {
                this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
                return;
            }
            this.careTimer = 1000;
        }
    }

    private void tickSleeping() {
        this.kitty.setSitting(true);
        // Like eating: keep its rotation locked to the bed, or its look-around AI turns it and the
        // renderer drags the whole sleeping body along (a passenger's body follows a head turned past 50°).
        if (this.kitty.getVehicle() != null) {
            lockRotationToVehicle(this.kitty.getVehicle());
        }
        if (this.kitty.getRandom().nextInt(100) == 0) {
            this.kitty.playSound(ModSounds.KITTY_PURR.get(), 0.7F, 1.0F);
        }
        this.careTimer++;
        if (this.kitty.level().isDay() || (this.careTimer > 500 && this.kitty.getRandom().nextInt(500) == 0)) {
            this.kitty.setSitting(false);
            if (this.kitty.isVehicle() || this.kitty.getVehicle() != null) {
                this.kitty.stopRiding();
            }
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
        }
    }

    /**
     * Simplified from the original: it walks toward a nearby tree and "arrives"
     * there instead of literally climbing through leaves block by block (the
     * original disables collision to crawl up through leaves, which needs APIs
     * that don't map cleanly to the modern pathfinder without real risk of
     * getting a kitty stuck inside a tree).
     */
    private void tickWantsTree() {
        this.careTimer++;
        if (this.careTimer > 500) {
            this.kitty.setKittyCareState(this.onTree ? KittyCareState.STATE_STUCK_IN_TREE : KittyCareState.STATE_IDLE);
            return;
        }
        if (this.treeTarget == null && this.kitty.getRandom().nextInt(50) == 0) {
            this.treeTarget = findNearbyTreeTop(18);
        }
        if (this.treeTarget == null) {
            return;
        }
        this.kitty.getNavigation().moveTo(this.treeTarget.getX() + 0.5D, this.treeTarget.getY(), this.treeTarget.getZ() + 0.5D, 1.0D);
        if (this.kitty.blockPosition().closerThan(this.treeTarget, 2.0D)) {
            this.onTree = true;
            this.treeTarget = null;
        }
    }

    private void tickStuckInTree() {
        if (this.kitty.getRandom().nextInt(100) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            this.onTree = false;
            return;
        }
        Player nearby = this.kitty.level().getNearestPlayer(this.kitty, 2.0D);
        if (nearby != null) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            this.onTree = false;
        }
    }

    @Nullable
    private BlockPos findNearbyTreeTop(int radius) {
        BlockPos base = this.kitty.blockPosition();
        for (int i = 0; i < 10; i++) {
            int dx = this.kitty.getRandom().nextInt(radius * 2 + 1) - radius;
            int dz = this.kitty.getRandom().nextInt(radius * 2 + 1) - radius;
            BlockPos.MutableBlockPos pos = base.offset(dx, 10, dz).mutable();
            for (int y = base.getY() + 10; y > base.getY() - 5; y--) {
                pos.setY(y);
                if (this.kitty.level().getBlockState(pos).is(BlockTags.LEAVES)) {
                    return pos.immutable();
                }
            }
        }
        return null;
    }

    @Nullable
    private MoCKittyBedEntity findAnyBed(double radius) {
        MoCKittyBedEntity best = null;
        double bestDistSqr = radius * radius;
        for (MoCKittyBedEntity bed : this.kitty.level().getEntitiesOfClass(
                MoCKittyBedEntity.class, this.kitty.getBoundingBox().inflate(radius))) {
            if (bed.isVehicle()) {
                continue;
            }
            double d = bed.distanceToSqr(this.kitty);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = bed;
            }
        }
        return best;
    }

    private void approachAndUseBed() {
        MoCKittyBedEntity bed = findFilledBed(SEARCH_RADIUS);
        if (bed == null) {
            return;
        }
        double dist = bed.distanceTo(this.kitty);
        if (dist > 2.0F) {
            this.kitty.getNavigation().moveTo(bed, 1.0D);
            return;
        }
        if (this.kitty.startRiding(bed)) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IN_BED);
        }
    }

    private void tickSeekingLitter() {
        this.careTimer++;
        if (this.careTimer > 2000 && this.kitty.getRandom().nextInt(1000) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_AGGRESSIVE);
            return;
        }
        if (this.kitty.getRandom().nextInt(20) != 0) {
            return;
        }
        MoCLitterBoxEntity box = findCleanLitterBox(SEARCH_RADIUS);
        if (box == null) {
            return;
        }
        double dist = box.distanceTo(this.kitty);
        if (dist > 2.0F) {
            this.kitty.getNavigation().moveTo(box, 1.0D);
            return;
        }
        if (this.kitty.startRiding(box)) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IN_LITTER);
        }
    }

    private void tickCurious() {
    Player nearby = this.kitty.level().getNearestPlayer(this.kitty, 18D);
    if (nearby == null || this.kitty.getRandom().nextInt(10) != 0) {
        return;
    }
    if (!nearby.getMainHandItem().is(ModItems.WOOL_BALL.get())) {
        this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
        return;
    }
    double dist = nearby.distanceTo(this.kitty);
    if (dist > 5.0F) {
        this.kitty.getNavigation().moveTo(nearby, 1.0D);
    }
}

    private void tickPlaying() {
        int boredomChance = 200; // TODO: use getTemper()-based 300 once temperament exists
        if (this.kitty.getRandom().nextInt(boredomChance) == 0) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            return;
        }
        if (this.playTarget == null || !this.playTarget.isAlive()) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            return;
        }
        double dist = this.playTarget.distanceTo(this.kitty);
        if (dist < 1.5D) {
            this.kitty.startSwing();
            if (this.kitty.getRandom().nextInt(10) == 0) {
                Vec3 push = this.playTarget.position().subtract(this.kitty.position()).normalize().scale(0.3D);
                this.playTarget.setDeltaMovement(push.x, 0.15D, push.z);
            }
        } else {
            this.kitty.getNavigation().moveTo(this.playTarget, 1.0D);
        }
    }

    private void tickInLitter() {
        if (!(this.kitty.getVehicle() instanceof MoCLitterBoxEntity box)) {
            this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
            return;
        }
        lockRotationToVehicle(box);
        this.careTimer++;
        if (this.careTimer <= 300) {
            if (this.kitty.getRandom().nextInt(40) == 0) {
                this.kitty.playSound(SoundEvents.SAND_BREAK, 1.0F, 1.0F);
            }
            return;
        }
        this.kitty.playSound(SoundEvents.SLIME_BLOCK_PLACE, 1.0F, 1.0F);
        box.setUsedLitter(true);
        this.kitty.stopRiding();
        this.kitty.setKittyCareState(KittyCareState.STATE_IDLE);
    }

    private void lockRotationToVehicle(Entity vehicle) {
        this.kitty.setYRot(vehicle.getYRot());
        this.kitty.yBodyRot = this.kitty.getYRot();
        this.kitty.yHeadRot = this.kitty.getYRot();
        this.kitty.setXRot(0F);
    }

    @Nullable
    private MoCLitterBoxEntity findCleanLitterBox(double radius) {
        MoCLitterBoxEntity best = null;
        double bestDistSqr = radius * radius;
        for (MoCLitterBoxEntity box : this.kitty.level().getEntitiesOfClass(
                MoCLitterBoxEntity.class, this.kitty.getBoundingBox().inflate(radius))) {
            if (box.isVehicle() || box.isUsedLitter()) {
                continue;
            }
            double d = box.distanceToSqr(this.kitty);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = box;
            }
        }
        return best;
    }

    @Nullable
    private MoCKittyBedEntity findFilledBed(double radius) {
        MoCKittyBedEntity best = null;
        double bestDistSqr = radius * radius;
        for (MoCKittyBedEntity bed : this.kitty.level().getEntitiesOfClass(
                MoCKittyBedEntity.class, this.kitty.getBoundingBox().inflate(radius))) {
            if (bed.isVehicle() || (!bed.hasFood() && !bed.hasMilk())) {
                continue;
            }
            double d = bed.distanceToSqr(this.kitty);
            if (d < bestDistSqr) {
                bestDistSqr = d;
                best = bed;
            }
        }
        return best;
    }
}