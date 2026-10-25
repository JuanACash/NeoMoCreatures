package com.example.neomocreatures.entity.horse;

import java.util.List;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Coat;
import com.example.neomocreatures.breeding.MoCHorseGenetics.FairyColor;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.util.MoCTickUtil;
import com.example.neomocreatures.util.NamingHelper;

/**
 * Mo' Creatures style breeding: two tamed adult horses in love that stay close together
 * for a while (gestation) produce a foal whose species and coat follow {@link MoCHorseGenetics}.
 * Runs on the server only, once per tick, from the horse's tick().
 */
public final class HorseBreedingHandler {

    /** Gestation time with easyHorseBreeding off: 5 minutes. */
    private static final int SLOW_GESTATION_TICKS = 6000;
    /** Gestation time with easyHorseBreeding on: 15 seconds. */
    private static final int EASY_GESTATION_TICKS = 300;

    private static final double MATE_SEARCH_XZ = 4.0D;
    private static final double MATE_SEARCH_Y = 2.0D;
    /** No other horse may be this close when the foal spawns. */
    private static final double CROWD_SEARCH_XZ = 8.0D;
    private static final double CROWD_SEARCH_Y = 4.0D;

    /** Adult foals start this many ticks from adulthood (vanilla baby age, 20 minutes). */
    private static final int FOAL_AGE = -24000;

    private final MoCHorseEntity horse;
    /** Not saved: unloading the horse simply restarts gestation, as before. */
    private int gestationProgress = 0;
    /** Cached result of the mate scan, refreshed every few ticks. Not saved. */
    private boolean mateNearby = false;

    public HorseBreedingHandler(MoCHorseEntity horse) {
        this.horse = horse;
    }

    public void tick() {
        // Love lasts about 30 seconds, but a gestation that already started keeps running without it.
        if (!this.horse.isInLove() && !this.isGestating()) {
            this.mateNearby = false;
            return;
        }

        if (MoCTickUtil.isScanTick(this.horse, MoCTickUtil.THREAT_SCAN_INTERVAL)) {
            this.mateNearby = !findMates().isEmpty();
        }
        if (!this.mateNearby) {
            this.gestationProgress = 0;
            return;
        }

        this.gestationProgress++;
        if (this.gestationProgress < gestationTicks()) {
            return;
        }

        // Fresh lookup right before birth: the cached flag may be a few ticks old.
        List<MoCHorseEntity> mates = findMates();
        if (mates.isEmpty()) {
            this.mateNearby = false;
            this.gestationProgress = 0;
            return;
        }
        MoCHorseEntity mate = mates.get(0);
        // Only one of the two parents spawns the foal: the one with the lower UUID
        if (this.horse.getUUID().compareTo(mate.getUUID()) > 0) {
            return;
        }

        // No third horse within 8 blocks horizontally — checked last, right
        // before actually spawning, so gestation progress isn't lost while
        // waiting for the area to clear; it just keeps retrying each tick.
        if (!MoCTickUtil.isScanTick(this.horse, MoCTickUtil.THREAT_SCAN_INTERVAL)) {
            return; // retry on the next scan tick
        }
        boolean crowded = !this.horse.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.horse.getBoundingBox().inflate(CROWD_SEARCH_XZ, CROWD_SEARCH_Y, CROWD_SEARCH_XZ),
                other -> other != this.horse && other != mate).isEmpty();
        if (crowded) {
            return;
        }

        this.gestationProgress = 0;
        this.spawnFoal(mate);
    }

    /** True while a gestation is running, even if the love timer already ran out. */
    public boolean isGestating() {
        return this.gestationProgress > 0;
    }

    private List<MoCHorseEntity> findMates() {
        return this.horse.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.horse.getBoundingBox().inflate(MATE_SEARCH_XZ, MATE_SEARCH_Y, MATE_SEARCH_XZ),
                other -> other != this.horse && other.isTamed() && !other.isBaby()
                        && !other.isSterileHybrid() && (other.isInLove() || other.isGestating())
                        && MoCHorseGenetics.canBreed(this.horse.getSpecies(), this.horse.getCoat(),
                                other.getSpecies(), other.getCoat()));
    }

    private static int gestationTicks() {
        return Config.CREATURES.easyHorseBreeding.get() ? EASY_GESTATION_TICKS : SLOW_GESTATION_TICKS;
    }

    private void spawnFoal(MoCHorseEntity mate) {
        Species species = this.horse.getSpecies();
        Species mateSpecies = mate.getSpecies();

        boolean isFairyBreeding = (species == Species.UNICORN && mateSpecies == Species.PEGASUS)
                || (species == Species.PEGASUS && mateSpecies == Species.UNICORN)
                || (species == Species.FAIRY_HORSE && mateSpecies == Species.FAIRY_HORSE);

        Species foalSpecies = MoCHorseGenetics.resolveOffspringSpecies(species, mateSpecies);
        Coat foalCoat = (foalSpecies == Species.HORSE && species == Species.HORSE && mateSpecies == Species.HORSE)
                ? MoCHorseGenetics.resolveOffspringCoat(this.horse.getCoat(), mate.getCoat(), this.horse.getRandom())
                : Coat.WHITE;

        FairyColor foalFairyColor = FairyColor.WHITE;
        if (species == Species.FAIRY_HORSE && mateSpecies == Species.FAIRY_HORSE) {
            if (this.horse.getFairyColor() == mate.getFairyColor()) {
                foalFairyColor = this.horse.getFairyColor();
            } else {
                foalSpecies = Species.HORSE_BUG; // different colors -> easter egg
            }
        }

        MoCHorseEntity foal = ModEntities.MOC_HORSE.get().create(this.horse.level());
        if (foal == null) return;

        foal.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 0.0F, 0.0F);
        foal.setSpecies(foalSpecies);
        foal.setCoat(foalCoat);
        if (foalSpecies == Species.FAIRY_HORSE) {
            foal.setFairyColor(foalFairyColor);
        }
        foal.setHealth((float) foal.getMaxHealth());
        foal.setAge(FOAL_AGE);
        if (this.horse.getOwnerUUID() != null) {
            foal.setOwnerUUID(this.horse.getOwnerUUID());
            foal.setTamed(true);
            NamingHelper.promptRename(foal, this.horse.getOwnerUUID());
        }
        this.horse.level().addFreshEntity(foal);

        if (isFairyBreeding) {
            this.horse.startVanish();
            mate.startVanish();
        } else {
            this.horse.resetLove();
            mate.resetLove();
        }
    }
}