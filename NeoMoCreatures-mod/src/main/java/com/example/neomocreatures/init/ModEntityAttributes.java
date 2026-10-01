package com.example.neomocreatures.init;

import static com.example.neomocreatures.init.ModEntities.*;

import com.example.neomocreatures.entity.MoCAntEntity;
import com.example.neomocreatures.entity.MoCBearEntity;
import com.example.neomocreatures.entity.MoCBeeEntity;
import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.example.neomocreatures.entity.MoCBigGolemEntity;
import com.example.neomocreatures.entity.MoCBirdEntity;
import com.example.neomocreatures.entity.MoCBoarEntity;
import com.example.neomocreatures.entity.MoCBunnyEntity;
import com.example.neomocreatures.entity.MoCButterflyEntity;
import com.example.neomocreatures.entity.MoCCaveOgreEntity;
import com.example.neomocreatures.entity.MoCCrabEntity;
import com.example.neomocreatures.entity.MoCCricketEntity;
import com.example.neomocreatures.entity.MoCCrocodileEntity;
import com.example.neomocreatures.entity.MoCDeerEntity;
import com.example.neomocreatures.entity.MoCDolphinEntity;
import com.example.neomocreatures.entity.MoCDragonflyEntity;
import com.example.neomocreatures.entity.MoCDuckEntity;
import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.MoCEntEntity;
import com.example.neomocreatures.entity.MoCFilchLizardEntity;
import com.example.neomocreatures.entity.MoCFireOgreEntity;
import com.example.neomocreatures.entity.MoCFireflyEntity;
import com.example.neomocreatures.entity.MoCFishyEntity;
import com.example.neomocreatures.entity.MoCFlameWraithEntity;
import com.example.neomocreatures.entity.MoCFlyEntity;
import com.example.neomocreatures.entity.MoCFoxEntity;
import com.example.neomocreatures.entity.MoCGoatEntity;
import com.example.neomocreatures.entity.MoCGrasshopperEntity;
import com.example.neomocreatures.entity.MoCGreenOgreEntity;
import com.example.neomocreatures.entity.MoCHellRatEntity;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCJellyfishEntity;
import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.example.neomocreatures.entity.MoCKittyEntity;
import com.example.neomocreatures.entity.MoCKomodoDragonEntity;
import com.example.neomocreatures.entity.MoCLitterBoxEntity;
import com.example.neomocreatures.entity.MoCMaggotEntity;
import com.example.neomocreatures.entity.MoCMantaRayEntity;
import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.MoCMediumFishEntity;
import com.example.neomocreatures.entity.MoCMiniGolemEntity;
import com.example.neomocreatures.entity.MoCMoleEntity;
import com.example.neomocreatures.entity.MoCMouseEntity;
import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.MoCRaccoonEntity;
import com.example.neomocreatures.entity.MoCRatEntity;
import com.example.neomocreatures.entity.MoCRoachEntity;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.MoCSharkEntity;
import com.example.neomocreatures.entity.MoCSilverSkeletonEntity;
import com.example.neomocreatures.entity.MoCSmallFishEntity;
import com.example.neomocreatures.entity.MoCSnailEntity;
import com.example.neomocreatures.entity.MoCSnakeEntity;
import com.example.neomocreatures.entity.MoCStingrayEntity;
import com.example.neomocreatures.entity.MoCTurkeyEntity;
import com.example.neomocreatures.entity.MoCTurtleEntity;
import com.example.neomocreatures.entity.MoCWerewolfEntity;
import com.example.neomocreatures.entity.MoCWildWolfEntity;
import com.example.neomocreatures.entity.MoCWraithEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * Default attributes (health, speed, damage...) for every living mod entity.
 * Each value comes from the entity's own createAttributes() method.
 */
public final class ModEntityAttributes {

    private ModEntityAttributes() {
        // Static registration only, no instances
    }

    /** Mod event bus listener. */
    public static void register(EntityAttributeCreationEvent event) {
        event.put(MOC_HORSE.get(), MoCHorseEntity.createAttributes().build());
        event.put(MOC_HORSE_MOB.get(), MoCHorseMobEntity.createAttributes().build());
        event.put(WYVERN.get(), MoCWyvernEntity.createAttributes().build());
        event.put(WYVERN_TIER2.get(), MoCWyvernEntity.createTier2Attributes().build());
        event.put(WYVERN_MOTHER.get(), MoCWyvernEntity.createMotherAttributes().build());
        event.put(WYVERN_MOTHER_TAMED.get(), MoCWyvernEntity.createMotherTamedAttributes().build());
        event.put(MOC_EGG.get(), MoCEggEntity.createAttributes().build());
        event.put(MOC_ELEPHANT.get(), MoCElephantEntity.createAttributes().build());
        event.put(MOC_KITTY_BED.get(), MoCKittyBedEntity.createAttributes().build());
        event.put(MOC_LITTER_BOX.get(), MoCLitterBoxEntity.createAttributes().build());
        event.put(MOC_BIG_CAT.get(), MoCBigCatEntity.createAttributes().build());
        event.put(MOC_MANTICORE.get(), MoCManticoreEntity.createAttributes().build());
        event.put(MOC_SCORPION.get(), MoCScorpionEntity.createAttributes().build());
        event.put(MOC_OSTRICH.get(), MoCOstrichEntity.createAttributes().build());
        event.put(MOC_BEAR.get(), MoCBearEntity.createAttributes().build());
        event.put(MOC_FOX.get(), MoCFoxEntity.createAttributes().build());
        event.put(MOC_RACCOON.get(), MoCRaccoonEntity.createAttributes().build());
        event.put(MOC_TURKEY.get(), MoCTurkeyEntity.createAttributes().build());
        event.put(MOC_GOAT.get(), MoCGoatEntity.createAttributes().build());
        event.put(MOC_KOMODO_DRAGON.get(), MoCKomodoDragonEntity.createAttributes().build());
        event.put(MOC_KITTY.get(), MoCKittyEntity.createAttributes().build());
        event.put(MOC_SNAKE.get(), MoCSnakeEntity.createAttributes().build());
        event.put(MOC_BUNNY.get(), MoCBunnyEntity.createAttributes().build());
        event.put(MOC_BIRD.get(), MoCBirdEntity.createAttributes().build());
        event.put(MOC_SHARK.get(), MoCSharkEntity.createAttributes().build());
        event.put(MOC_TURTLE.get(), MoCTurtleEntity.createAttributes().build());
        event.put(MOC_STINGRAY.get(), MoCStingrayEntity.createAttributes().build());
        event.put(MOC_DOLPHIN.get(), MoCDolphinEntity.createAttributes().build());
        event.put(MOC_MANTA_RAY.get(), MoCMantaRayEntity.createAttributes().build());
        event.put(MOC_FISHY.get(), MoCFishyEntity.createAttributes().build());
        event.put(MOC_COD.get(), MoCMediumFishEntity.createAttributes().build());
        event.put(MOC_SALMON.get(), MoCMediumFishEntity.createAttributes().build());
        event.put(MOC_BASS.get(), MoCMediumFishEntity.createAttributes().build());
        event.put(MOC_SMALL_FISH.get(), MoCSmallFishEntity.createAttributes().build());
        event.put(MOC_JELLYFISH.get(), MoCJellyfishEntity.createAttributes().build());
        event.put(MOC_CRAB.get(), MoCCrabEntity.createAttributes().build());
        event.put(MOC_CROCODILE.get(), MoCCrocodileEntity.createAttributes().build());
        event.put(MOC_GREEN_OGRE.get(), MoCGreenOgreEntity.createAttributes().build());
        event.put(MOC_FIRE_OGRE.get(), MoCFireOgreEntity.createAttributes().build());
        event.put(MOC_CAVE_OGRE.get(), MoCCaveOgreEntity.createAttributes().build());
        event.put(MOC_WEREWOLF.get(), MoCWerewolfEntity.createAttributes().build());
        event.put(MOC_WILD_WOLF.get(), MoCWildWolfEntity.createAttributes().build());
        event.put(MOC_BOAR.get(), MoCBoarEntity.createAttributes().build());
        event.put(MOC_DEER.get(), MoCDeerEntity.createAttributes().build());
        event.put(MOC_RAT.get(), MoCRatEntity.createAttributes().build());
        event.put(MOC_HELL_RAT.get(), MoCHellRatEntity.createAttributes().build());
        event.put(MOC_MOUSE.get(), MoCMouseEntity.createAttributes().build());
        event.put(MOC_MOLE.get(), MoCMoleEntity.createAttributes().build());
        event.put(MOC_DUCK.get(), MoCDuckEntity.createAttributes().build());
        event.put(MOC_FLY.get(), MoCFlyEntity.createAttributes().build());
        event.put(MOC_BUTTERFLY.get(), MoCButterflyEntity.createAttributes().build());
        event.put(MOC_DRAGONFLY.get(), MoCDragonflyEntity.createAttributes().build());
        event.put(MOC_FIREFLY.get(), MoCFireflyEntity.createAttributes().build());
        event.put(MOC_BEE.get(), MoCBeeEntity.createAttributes().build());
        event.put(MOC_SNAIL.get(), MoCSnailEntity.createAttributes().build());
        event.put(MOC_ROACH.get(), MoCRoachEntity.createAttributes().build());
        event.put(MOC_ANT.get(), MoCAntEntity.createAttributes().build());
        event.put(MOC_MAGGOT.get(), MoCMaggotEntity.createAttributes().build());
        event.put(MOC_CRICKET.get(), MoCCricketEntity.createAttributes().build());
        event.put(MOC_GRASSHOPPER.get(), MoCGrasshopperEntity.createAttributes().build());
        event.put(MOC_SILVER_SKELETON.get(), MoCSilverSkeletonEntity.createAttributes().build());
        event.put(MOC_WRAITH.get(), MoCWraithEntity.createAttributes().build());
        event.put(MOC_FLAME_WRAITH.get(), MoCFlameWraithEntity.createAttributes().build());
        event.put(MOC_ENT.get(), MoCEntEntity.createAttributes().build());
        event.put(MOC_MINI_GOLEM.get(), MoCMiniGolemEntity.createAttributes().build());
        event.put(MOC_BIG_GOLEM.get(), MoCBigGolemEntity.createAttributes().build());
        event.put(MOC_FILCH_LIZARD.get(), MoCFilchLizardEntity.createAttributes().build());
    }
}
