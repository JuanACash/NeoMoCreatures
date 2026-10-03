package com.example.neomocreatures.entity.ostrich;

import java.util.List;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

/**
 * Wild ostrich breeding: feeding melon seeds to a wild hen with a valid partner nearby starts a
 * countdown, after which she lays an egg. White parents raise the chance of a white chick.
 * Runs on the server only.
 */
public final class OstrichBreedingHandler {

    /** Two minutes between being fed and laying the egg. */
    private static final int EGG_APPEAR_TICKS = 2400;
    /** A partner must be within this many blocks when the hen is fed. */
    private static final double PAIR_RADIUS = 8.0D;
    /** Chance of a white chick when exactly one parent is white. */
    private static final float MIXED_WHITE_CHANCE = 0.25F;

    /** Pairing kinds, decided when breeding starts. */
    private static final int PAIRING_NORMAL = 0;
    private static final int PAIRING_ONE_WHITE = 1;
    private static final int PAIRING_BOTH_WHITE = 2;

    private final MoCOstrichEntity ostrich;
    /** Ticks left until the egg is laid; 0 when not breeding. Not saved, as before. */
    private int eggAppearCounter;
    private int pairingType;

    public OstrichBreedingHandler(MoCOstrichEntity ostrich) {
        this.ostrich = ostrich;
    }

    /** True while an egg is on its way; such an ostrich can't start breeding again. */
    public boolean isLayingEgg() {
        return this.eggAppearCounter > 0;
    }

    /**
     * Starts the countdown if a valid partner is nearby, using up one seed.
     * Does nothing (and keeps the seed) when no partner is found.
     */
    public void tryStart(ItemStack seeds) {
        MoCOstrichEntity partner = this.findValidPartner();
        if (partner == null) {
            return;
        }

        seeds.shrink(1);
        this.eggAppearCounter = EGG_APPEAR_TICKS;

        OstrichVariant mine = this.ostrich.getVariant();
        OstrichVariant theirs = partner.getVariant();
        if (mine == OstrichVariant.WHITE && theirs == OstrichVariant.WHITE) {
            this.pairingType = PAIRING_BOTH_WHITE;
        } else if (mine == OstrichVariant.WHITE || theirs == OstrichVariant.WHITE) {
            this.pairingType = PAIRING_ONE_WHITE;
        } else {
            this.pairingType = PAIRING_NORMAL;
        }
    }

    public void tick() {
        if (this.eggAppearCounter <= 0) {
            return;
        }
        if (--this.eggAppearCounter == 0) {
            this.layEgg();
        }
    }

    @Nullable
    private MoCOstrichEntity findValidPartner() {
        OstrichVariant mine = this.ostrich.getVariant();
        List<MoCOstrichEntity> nearby = this.ostrich.level().getEntitiesOfClass(MoCOstrichEntity.class,
                this.ostrich.getBoundingBox().inflate(PAIR_RADIUS),
                other -> other != this.ostrich && !other.isTame() && !other.isBaby() && !other.isLayingEgg());

        for (MoCOstrichEntity other : nearby) {
            if (canLayWith(mine, other.getVariant())) {
                return other;
            }
        }
        return null;
    }

    /** This ostrich lays the egg: a hen (or white) paired with a male (or white). */
    private static boolean canLayWith(OstrichVariant mine, OstrichVariant theirs) {
        boolean iCanLay = mine == OstrichVariant.FEMALE || mine == OstrichVariant.WHITE;
        boolean theyCanFertilize = theirs == OstrichVariant.MALE || theirs == OstrichVariant.WHITE;
        return iCanLay && theyCanFertilize;
    }

    private void layEgg() {
        MoCEggEntity egg = ModEntities.MOC_EGG.get().create((ServerLevel) this.ostrich.level());
        if (egg != null) {
            egg.moveTo(this.ostrich.getX(), this.ostrich.getY(), this.ostrich.getZ(), 0F, 0F);
            egg.setHatchEntityId(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.MOC_OSTRICH.get()));
            egg.setHatchVariant(this.rollChickVariant().name());
            egg.setSourceItemId(BuiltInRegistries.ITEM.getKey(ModItems.OSTRICH_EGG.get()));
            egg.setRequiresLight(false);
            egg.setRequirePickupToTame(true);
            this.ostrich.level().addFreshEntity(egg);
        }
    }

    private OstrichVariant rollChickVariant() {
        RandomSource random = this.ostrich.getRandom();
        if (this.pairingType == PAIRING_BOTH_WHITE) {
            return OstrichVariant.WHITE;
        } else if (this.pairingType == PAIRING_ONE_WHITE) {
            return random.nextFloat() < MIXED_WHITE_CHANCE ? OstrichVariant.WHITE
                    : (random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE);
        } else {
            return random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE;
        }
    }
}