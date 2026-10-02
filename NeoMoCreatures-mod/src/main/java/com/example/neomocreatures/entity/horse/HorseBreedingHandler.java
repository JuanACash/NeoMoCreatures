package com.example.neomocreatures.entity.horse;

import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Coat;
import com.example.neomocreatures.breeding.MoCHorseGenetics.FairyColor;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.entity.horse.HorseBreedingHandler;

import java.util.List;

/**
 * Mo' Creatures style breeding: two tamed adult horses in love that stay close together
 * for a while (gestation) produce a foal whose species and coat follow {@link MoCHorseGenetics}.
 * Runs on the server only, once per tick, from the horse's tick().
 */
public final class HorseBreedingHandler {

    /** Ticks two horses in love must stay together before the foal appears (15 seconds). */
    private static final int GESTATION_TICKS = 300;

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

    public HorseBreedingHandler(MoCHorseEntity horse) {
        this.horse = horse;
    }

    public void tick() {
        if (!this.horse.isInLove()) {
            this.gestationProgress = 0;
            return;
        }

        List<MoCHorseEntity> mates = this.horse.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.horse.getBoundingBox().inflate(MATE_SEARCH_XZ, MATE_SEARCH_Y, MATE_SEARCH_XZ),
                other -> other != this.horse && other.isTamed() && !other.isBaby()
                        && !other.isSterileHybrid() && other.isInLove()
                        && MoCHorseGenetics.canBreed(this.horse.getSpecies(), this.horse.getCoat(),
                                other.getSpecies(), other.getCoat()));

        if (mates.isEmpty()) {
            this.gestationProgress = 0;
            return;
        }

        this.gestationProgress++;
        if (this.gestationProgress < GESTATION_TICKS) {
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
        boolean crowded = !this.horse.level().getEntitiesOfClass(
                MoCHorseEntity.class, this.horse.getBoundingBox().inflate(CROWD_SEARCH_XZ, CROWD_SEARCH_Y, CROWD_SEARCH_XZ),
                other -> other != this.horse && other != mate).isEmpty();
        if (crowded) {
            return;
        }

        this.gestationProgress = 0;
        this.spawnFoal(mate);
    }

    private void spawnFoal(MoCHorseEntity mate) {
        Species species = this.horse.getSpecies();
        Species mateSpecies = mate.getSpecies();

        boolean isFairyBreeding = (species == Species.UNICORN && mateSpecies == Species.PEGASUS)
                || (species == Species.PEGASUS && mateSpecies == Species.UNICORN)
                || (species == Species.FAIRY_HORSE && mateSpecies == Species.FAIRY_HORSE);

        Species foalSpecies = MoCHorseGenetics.resolveOffspringSpecies(species, mateSpecies);
        Coat foalCoat = (foalSpecies == Species.HORSE && species == Species.HORSE && mateSpecies == Species.HORSE)
                ? MoCHorseGenetics.resolveOffspringCoat(this.horse.getCoat(), mate.getCoat())
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