package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.ant.MoCAntEntity;
import com.example.neomocreatures.entity.bear.MoCBearEntity;
import com.example.neomocreatures.entity.bee.MoCBeeEntity;
import com.example.neomocreatures.entity.bigcat.MoCBigCatEntity;
import com.example.neomocreatures.entity.bird.MoCBirdEntity;
import com.example.neomocreatures.entity.boar.MoCBoarEntity;
import com.example.neomocreatures.entity.bunny.MoCBunnyEntity;
import com.example.neomocreatures.entity.butterfly.MoCButterflyEntity;
import com.example.neomocreatures.entity.crab.MoCCrabEntity;
import com.example.neomocreatures.entity.cricket.MoCCricketEntity;
import com.example.neomocreatures.entity.crocodile.MoCCrocodileEntity;
import com.example.neomocreatures.entity.deer.MoCDeerEntity;
import com.example.neomocreatures.entity.dolphin.MoCDolphinEntity;
import com.example.neomocreatures.entity.dragonfly.MoCDragonflyEntity;
import com.example.neomocreatures.entity.duck.MoCDuckEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.entity.elephant.MoCElephantEntity;
import com.example.neomocreatures.entity.ent.MoCEntEntity;
import com.example.neomocreatures.entity.firefly.MoCFireflyEntity;
import com.example.neomocreatures.entity.fishy.MoCFishyEntity;
import com.example.neomocreatures.entity.fly.MoCFlyEntity;
import com.example.neomocreatures.entity.fox.MoCFoxEntity;
import com.example.neomocreatures.entity.goat.MoCGoatEntity;
import com.example.neomocreatures.entity.golem.MoCBigGolemEntity;
import com.example.neomocreatures.entity.golem.MoCMiniGolemEntity;
import com.example.neomocreatures.entity.grasshopper.MoCGrasshopperEntity;
import com.example.neomocreatures.entity.horse.MoCHorseEntity;
import com.example.neomocreatures.entity.jellyfish.MoCJellyfishEntity;
import com.example.neomocreatures.entity.kitty.MoCKittyBedEntity;
import com.example.neomocreatures.entity.kitty.MoCKittyEntity;
import com.example.neomocreatures.entity.kitty.MoCLitterBoxEntity;
import com.example.neomocreatures.entity.komodo.MoCKomodoDragonEntity;
import com.example.neomocreatures.entity.maggot.MoCMaggotEntity;
import com.example.neomocreatures.entity.mantaray.MoCMantaRayEntity;
import com.example.neomocreatures.entity.manticore.MoCManticoreEntity;
import com.example.neomocreatures.entity.mediumfish.MoCBassEntity;
import com.example.neomocreatures.entity.mediumfish.MoCCodEntity;
import com.example.neomocreatures.entity.mediumfish.MoCSalmonEntity;
import com.example.neomocreatures.entity.mole.MoCMoleEntity;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;
import com.example.neomocreatures.entity.mouse.MoCMouseEntity;
import com.example.neomocreatures.entity.ogre.MoCCaveOgreEntity;
import com.example.neomocreatures.entity.ogre.MoCFireOgreEntity;
import com.example.neomocreatures.entity.ogre.MoCGreenOgreEntity;
import com.example.neomocreatures.entity.ostrich.MoCOstrichEntity;
import com.example.neomocreatures.entity.raccoon.MoCRaccoonEntity;
import com.example.neomocreatures.entity.rat.MoCHellRatEntity;
import com.example.neomocreatures.entity.rat.MoCRatEntity;
import com.example.neomocreatures.entity.roach.MoCRoachEntity;
import com.example.neomocreatures.entity.rock.MoCSummonedRockEntity;
import com.example.neomocreatures.entity.rock.MoCThrowableRockEntity;
import com.example.neomocreatures.entity.scorpion.MoCScorpionEntity;
import com.example.neomocreatures.entity.shark.MoCSharkEntity;
import com.example.neomocreatures.entity.silverskeleton.MoCSilverSkeletonEntity;
import com.example.neomocreatures.entity.smallfish.MoCSmallFishEntity;
import com.example.neomocreatures.entity.snail.MoCSnailEntity;
import com.example.neomocreatures.entity.snake.MoCSnakeEntity;
import com.example.neomocreatures.entity.stingray.MoCStingrayEntity;
import com.example.neomocreatures.entity.turkey.MoCTurkeyEntity;
import com.example.neomocreatures.entity.turtle.MoCTurtleEntity;
import com.example.neomocreatures.entity.werewolf.MoCWerewolfEntity;
import com.example.neomocreatures.entity.wildwolf.MoCWildWolfEntity;
import com.example.neomocreatures.entity.wraith.MoCFlameWraithEntity;
import com.example.neomocreatures.entity.wraith.MoCWraithEntity;
import com.example.neomocreatures.entity.wyvern.MoCWyvernEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NeoMoCreatures.MODID);

    // NOTE on MobCategory: this is the exact bug found in BigDan's NeoForge
    // port of Mo' Creatures (the Wyvern was registered as MobCategory.AMBIENT,
    // the same bucket as bats/flies, which starves it of natural-spawn
    // budget). Anything meant to be a common, visible creature should be
    // MobCategory.CREATURE, not AMBIENT.
    public static final DeferredHolder<EntityType<?>, EntityType<MoCHorseEntity>> MOC_HORSE =
            ENTITY_TYPES.register("moc_horse", () -> EntityType.Builder
                    .of(MoCHorseEntity::new, MobCategory.CREATURE)
                    .sized(1.3964844F, 1.6F)
                    .clientTrackingRange(10)
                    // Relative to the default PASSENGER attachment (top of
                    // the hitbox, y=1.6).Ti Our model's back sits lower than
                    // a vanilla horse's, so this pulls the rider down.
                    .attach(EntityAttachment.PASSENGER, 0.0F, 1.4F, - 0.3F)
                    .build("moc_horse"));

        
    public static final DeferredHolder<EntityType<?>, EntityType<MoCHorseMobEntity>> MOC_HORSE_MOB =
        ENTITY_TYPES.register("moc_horse_mob", () -> EntityType.Builder
                .of(MoCHorseMobEntity::new, MobCategory.MONSTER)
                .sized(1.3964844F, 1.6F)
                .clientTrackingRange(10)
                .build("moc_horse_mob"));

    // Three distinct hitbox sizes for the three WyvernTier size classes —
    // these are the actual final adult sizes (hitbox does NOT get multiplied
    // again by Attributes.SCALE at adulthood, see MoCWyvernEntity#tickGrowth()
    // — SCALE only ever goes UP TO 1.0, shrinking the baby, never past it).
    public static final DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> WYVERN =
            ENTITY_TYPES.register("wyvern", () -> EntityType.Builder
                    .of(MoCWyvernEntity::new, MobCategory.CREATURE)
                    .sized(1.45F, 1.55F)
                    .clientTrackingRange(10)
                    .build("wyvern"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> WYVERN_TIER2 =
            ENTITY_TYPES.register("wyvern_tier2", () -> EntityType.Builder
                    .of(MoCWyvernEntity::new, MobCategory.CREATURE)
                    .sized(1.8F, 2.0F)
                    .clientTrackingRange(10)
                    .build("wyvern_tier2"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> WYVERN_MOTHER =
            ENTITY_TYPES.register("wyvern_mother", () -> EntityType.Builder
                    .of(MoCWyvernEntity::new, MobCategory.CREATURE)
                    .sized(2.2F, 2.35F)
                    .clientTrackingRange(10)
                    .build("wyvern_mother"));

    // Only ever reached by hatching a mother wyvern egg — bigger than the
    // wild mother (2.2x2.35) but smaller than before (4.2x5.0). This value
    // MUST match WyvernTier.MOTHER_TAMED's hitboxWidth/hitboxHeight, or the
    // math in getVisualScale()/tickGrowth() (which assumes this size is the
    // real final hitbox) stops matching what's actually registered here.
    public static final DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> WYVERN_MOTHER_TAMED =
            ENTITY_TYPES.register("wyvern_mother_tamed", () -> EntityType.Builder
                    .of(MoCWyvernEntity::new, MobCategory.CREATURE)
                    .sized(2.9F, 3.1F)
                    .clientTrackingRange(10)
                    .build("wyvern_mother_tamed"));


    public static final DeferredHolder<EntityType<?>, EntityType<MoCElephantEntity>> MOC_ELEPHANT =
        ENTITY_TYPES.register("moc_elephant", () -> EntityType.Builder
                .of(MoCElephantEntity::new, MobCategory.CREATURE)
                .sized(1.1F, 3.0F)
                .clientTrackingRange(10)
                .build("moc_elephant"));

   public static final DeferredHolder<EntityType<?>, EntityType<MoCBigCatEntity>> MOC_BIG_CAT =
        ENTITY_TYPES.register("moc_big_cat", () -> EntityType.Builder
                .of(MoCBigCatEntity::new, MobCategory.CREATURE)
                .sized(1.2F, 1.3F)
                .clientTrackingRange(10)
                .build("moc_big_cat"));

   public static final DeferredHolder<EntityType<?>, EntityType<MoCManticoreEntity>> MOC_MANTICORE =
        ENTITY_TYPES.register("moc_manticore", () -> EntityType.Builder
                .of(MoCManticoreEntity::new, MobCategory.MONSTER)
                .sized(1.35F, 1.45F)
                .clientTrackingRange(10)
                .build("moc_manticore"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCScorpionEntity>> MOC_SCORPION =
        ENTITY_TYPES.register("moc_scorpion", () -> EntityType.Builder
                .of(MoCScorpionEntity::new, MobCategory.MONSTER)
                .sized(1.4F, 0.9F)
                .clientTrackingRange(10)
                .build("moc_scorpion"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCOstrichEntity>> MOC_OSTRICH =
        ENTITY_TYPES.register("moc_ostrich", () -> EntityType.Builder
                .of(MoCOstrichEntity::new, MobCategory.CREATURE)
                .sized(1.0F, 1.6F)
                .clientTrackingRange(10)
                .build("moc_ostrich"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBearEntity>> MOC_BEAR =
        ENTITY_TYPES.register("moc_bear", () -> EntityType.Builder
                .of(MoCBearEntity::new, MobCategory.CREATURE)
                .sized(1.1F, 1.5F)
                .clientTrackingRange(10)
                .build("moc_bear"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFoxEntity>> MOC_FOX =
        ENTITY_TYPES.register("moc_fox", () -> EntityType.Builder
                .of(MoCFoxEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 0.7F)
                .clientTrackingRange(8)
                .build("moc_fox"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCKomodoDragonEntity>> MOC_KOMODO_DRAGON =
        ENTITY_TYPES.register("moc_komodo_dragon", () -> EntityType.Builder
                .of(MoCKomodoDragonEntity::new, MobCategory.CREATURE)
                .sized(1.3F, 0.9F)
                .clientTrackingRange(10)
                .build("moc_komodo_dragon"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCRaccoonEntity>> MOC_RACCOON =
        ENTITY_TYPES.register("moc_raccoon", () -> EntityType.Builder
                .of(MoCRaccoonEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 0.525F)
                .clientTrackingRange(8)
                .build("moc_raccoon"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCTurkeyEntity>> MOC_TURKEY =
        ENTITY_TYPES.register("moc_turkey", () -> EntityType.Builder
                .of(MoCTurkeyEntity::new, MobCategory.CREATURE)
                .sized(0.7F, 0.9F)
                .clientTrackingRange(8)
                .build("moc_turkey"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCGoatEntity>> MOC_GOAT =
        ENTITY_TYPES.register("moc_goat", () -> EntityType.Builder
                .of(MoCGoatEntity::new, MobCategory.CREATURE)
                .sized(0.8F, 0.9F)
                .clientTrackingRange(10)
                .build("moc_goat"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCKittyEntity>> MOC_KITTY =
        ENTITY_TYPES.register("moc_kitty", () -> EntityType.Builder
                .of(MoCKittyEntity::new, MobCategory.CREATURE)
                .sized(0.5F, 0.6F)
                .clientTrackingRange(8)
                .build("moc_kitty"));

   public static final DeferredHolder<EntityType<?>, EntityType<MoCSnakeEntity>> MOC_SNAKE =
        ENTITY_TYPES.register("moc_snake", () -> EntityType.Builder
                .of(MoCSnakeEntity::new, MobCategory.CREATURE)
                .sized(1.4F, 0.5F)
                .clientTrackingRange(8)
                .build("moc_snake"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBunnyEntity>> MOC_BUNNY =
        ENTITY_TYPES.register("moc_bunny", () -> EntityType.Builder
                .of(MoCBunnyEntity::new, MobCategory.CREATURE)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(8)
                .build("moc_bunny"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBirdEntity>> MOC_BIRD =
        ENTITY_TYPES.register("moc_bird", () -> EntityType.Builder
                .of(MoCBirdEntity::new, MobCategory.CREATURE)
                .sized(0.4F, 0.5F)
                .clientTrackingRange(8)
                .build("moc_bird"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSharkEntity>> MOC_SHARK =
        ENTITY_TYPES.register("moc_shark", () -> EntityType.Builder
                .of(MoCSharkEntity::new, MobCategory.WATER_CREATURE)
                .sized(1.6F, 1.0F)
                .clientTrackingRange(10)
                .build("moc_shark"));

    
    public static final DeferredHolder<EntityType<?>, EntityType<MoCTurtleEntity>> MOC_TURTLE =
        ENTITY_TYPES.register("moc_turtle", () -> EntityType.Builder
                .of(MoCTurtleEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 0.425F)
                .clientTrackingRange(8)
                .build("moc_turtle"));

    
    public static final DeferredHolder<EntityType<?>, EntityType<MoCStingrayEntity>> MOC_STINGRAY =
        ENTITY_TYPES.register("moc_stingray", () -> EntityType.Builder
                .of(MoCStingrayEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.7F, 0.3F)
                .clientTrackingRange(8)
                .build("moc_stingray"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCDolphinEntity>> MOC_DOLPHIN =
        ENTITY_TYPES.register("moc_dolphin", () -> EntityType.Builder
                .of(MoCDolphinEntity::new, MobCategory.WATER_CREATURE)
                .sized(1.3F, 0.605F)
                // Original: the eyes sit at 31.5% of the hitbox height.
                .eyeHeight(0.19F)
                .clientTrackingRange(10)
                .build("moc_dolphin"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCMantaRayEntity>> MOC_MANTA_RAY =
        ENTITY_TYPES.register("moc_manta_ray", () -> EntityType.Builder
                .of(MoCMantaRayEntity::new, MobCategory.WATER_CREATURE)
                .sized(1.4F, 0.4F)
                // Original: the eyes sit at 58.75% of the hitbox height.
                .eyeHeight(0.235F)
                .clientTrackingRange(10)
                .build("moc_manta_ray"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFishyEntity>> MOC_FISHY =
        ENTITY_TYPES.register("moc_fishy", () -> EntityType.Builder
                .of(MoCFishyEntity::new, MobCategory.WATER_CREATURE)
                // Original hitbox 0.5 x 0.3, divided by the 0.6 scale attribute the entity applies.
                .sized(0.83F, 0.5F)
                // Original: the eyes sit at 65% of the hitbox height.
                .eyeHeight(0.325F)
                .clientTrackingRange(8)
                .build("moc_fishy"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCCodEntity>> MOC_COD =
        ENTITY_TYPES.register("moc_cod", () -> EntityType.Builder
                .of(MoCCodEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.7F, 0.45F)
                // Original: the eyes sit at 77.5% of the hitbox height.
                .eyeHeight(0.349F)
                .clientTrackingRange(8)
                .build("moc_cod"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSalmonEntity>> MOC_SALMON =
        ENTITY_TYPES.register("moc_salmon", () -> EntityType.Builder
                .of(MoCSalmonEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.7F, 0.45F)
                .eyeHeight(0.349F)
                .clientTrackingRange(8)
                .build("moc_salmon"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBassEntity>> MOC_BASS =
        ENTITY_TYPES.register("moc_bass", () -> EntityType.Builder
                .of(MoCBassEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.7F, 0.45F)
                .eyeHeight(0.349F)
                .clientTrackingRange(8)
                .build("moc_bass"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSmallFishEntity>> MOC_SMALL_FISH =
        ENTITY_TYPES.register("moc_small_fish", () -> EntityType.Builder
                .of(MoCSmallFishEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.5F, 0.3F)
                // Original: the eyes sit at 45% of the hitbox height.
                .eyeHeight(0.135F)
                .clientTrackingRange(6)
                .build("moc_small_fish"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCJellyfishEntity>> MOC_JELLYFISH =
        ENTITY_TYPES.register("moc_jellyfish", () -> EntityType.Builder
                .of(MoCJellyfishEntity::new, MobCategory.WATER_CREATURE)
                .sized(0.45F, 0.575F)
                // Original: the eyes sit at 85% of the hitbox height.
                .eyeHeight(0.489F)
                .clientTrackingRange(8)
                .build("moc_jellyfish"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCCrabEntity>> MOC_CRAB =
        ENTITY_TYPES.register("moc_crab", () -> EntityType.Builder
                .of(MoCCrabEntity::new, MobCategory.AMBIENT)
                .sized(0.45F, 0.3F)
                .clientTrackingRange(8)
                .build("moc_crab"));
                
    public static final DeferredHolder<EntityType<?>, EntityType<MoCCrocodileEntity>> MOC_CROCODILE =
        ENTITY_TYPES.register("moc_crocodile", () -> EntityType.Builder
                .of(MoCCrocodileEntity::new, MobCategory.AMBIENT)
                .sized(0.9F, 0.5F)
                .eyeHeight(0.35F)
                .clientTrackingRange(10)
                .build("moc_crocodile"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCGreenOgreEntity>> MOC_GREEN_OGRE =
        ENTITY_TYPES.register("moc_green_ogre", () -> EntityType.Builder
                .of(MoCGreenOgreEntity::new, MobCategory.MONSTER)
                .sized(1.8F, 3.05F)
                .eyeHeight(2.7755F)
                .clientTrackingRange(12)
                .build("moc_green_ogre"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFireOgreEntity>> MOC_FIRE_OGRE =
        ENTITY_TYPES.register("moc_fire_ogre", () -> EntityType.Builder
                .of(MoCFireOgreEntity::new, MobCategory.MONSTER)
                .sized(1.8F, 3.05F)
                .eyeHeight(2.7755F)
                .clientTrackingRange(12)
                .build("moc_fire_ogre"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCCaveOgreEntity>> MOC_CAVE_OGRE =
        ENTITY_TYPES.register("moc_cave_ogre", () -> EntityType.Builder
                .of(MoCCaveOgreEntity::new, MobCategory.MONSTER)
                .sized(1.8F, 3.05F)
                .eyeHeight(2.7755F)
                .clientTrackingRange(12)
                .build("moc_cave_ogre"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCWerewolfEntity>> MOC_WEREWOLF =
        ENTITY_TYPES.register("moc_werewolf", () -> EntityType.Builder
                .of(MoCWerewolfEntity::new, MobCategory.MONSTER)
                .sized(1.2F, 2.4F)
                .clientTrackingRange(10)
                .build("moc_werewolf"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCWildWolfEntity>> MOC_WILD_WOLF =
        ENTITY_TYPES.register("moc_wild_wolf", () -> EntityType.Builder
                .of(MoCWildWolfEntity::new, MobCategory.MONSTER)
                .sized(0.8F, 0.8F)
                .clientTrackingRange(10)
                .build("moc_wild_wolf"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBoarEntity>> MOC_BOAR =
        ENTITY_TYPES.register("moc_boar", () -> EntityType.Builder
                .of(MoCBoarEntity::new, MobCategory.CREATURE)
                .sized(0.9F, 0.9F)
                .clientTrackingRange(10)
                .build("moc_boar"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCDeerEntity>> MOC_DEER =
        ENTITY_TYPES.register("moc_deer", () -> EntityType.Builder
                .of(MoCDeerEntity::new, MobCategory.CREATURE)
                .sized(0.9F, 1.425F)
                .clientTrackingRange(10)
                .build("moc_deer"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCRatEntity>> MOC_RAT =
        ENTITY_TYPES.register("moc_rat", () -> EntityType.Builder
                .of(MoCRatEntity::new, MobCategory.MONSTER)
                .sized(0.58F, 0.455F)
                .clientTrackingRange(8)
                .build("moc_rat"));  

    public static final DeferredHolder<EntityType<?>, EntityType<MoCHellRatEntity>> MOC_HELL_RAT =
        ENTITY_TYPES.register("moc_hell_rat", () -> EntityType.Builder
                .of(MoCHellRatEntity::new, MobCategory.MONSTER)
                .sized(0.7F, 0.65F)
                .clientTrackingRange(8)
                .build("moc_hell_rat"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCMouseEntity>> MOC_MOUSE =
        ENTITY_TYPES.register("moc_mouse", () -> EntityType.Builder
                .of(MoCMouseEntity::new, MobCategory.CREATURE)
                .sized(0.45F, 0.3F)
                .clientTrackingRange(6)
                .build("moc_mouse"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCMoleEntity>> MOC_MOLE =
        ENTITY_TYPES.register("moc_mole", () -> EntityType.Builder
                .of(MoCMoleEntity::new, MobCategory.CREATURE)
                .sized(1.0F, 0.5F)
                .clientTrackingRange(6)
                .build("moc_mole"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCDuckEntity>> MOC_DUCK =
        ENTITY_TYPES.register("moc_duck", () -> EntityType.Builder
                .of(MoCDuckEntity::new, MobCategory.CREATURE)
                .sized(0.4F, 0.7F)
                .clientTrackingRange(6)
                .build("moc_duck"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFlyEntity>> MOC_FLY =
        ENTITY_TYPES.register("moc_fly", () -> EntityType.Builder
                .of(MoCFlyEntity::new, MobCategory.AMBIENT)
                .sized(0.2F, 0.2F)
                .eyeHeight(0.2F)
                .clientTrackingRange(6)
                .build("moc_fly"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCButterflyEntity>> MOC_BUTTERFLY =
        ENTITY_TYPES.register("moc_butterfly", () -> EntityType.Builder
                .of(MoCButterflyEntity::new, MobCategory.AMBIENT)
                .sized(0.5F, 0.3F)
                .eyeHeight(0.1F)
                .clientTrackingRange(6)
                .build("moc_butterfly"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCDragonflyEntity>> MOC_DRAGONFLY =
        ENTITY_TYPES.register("moc_dragonfly", () -> EntityType.Builder
                .of(MoCDragonflyEntity::new, MobCategory.AMBIENT)
                .sized(0.5F, 0.3F)
                .eyeHeight(0.2F)
                .clientTrackingRange(6)
                .build("moc_dragonfly"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFireflyEntity>> MOC_FIREFLY =
        ENTITY_TYPES.register("moc_firefly", () -> EntityType.Builder
                .of(MoCFireflyEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.3F)
                .eyeHeight(0.15F)
                .clientTrackingRange(6)
                .build("moc_firefly"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBeeEntity>> MOC_BEE =
        ENTITY_TYPES.register("moc_bee", () -> EntityType.Builder
                .of(MoCBeeEntity::new, MobCategory.AMBIENT)
                .sized(0.4F, 0.3F)
                .eyeHeight(0.2F)
                .clientTrackingRange(6)
                .build("moc_bee"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSnailEntity>> MOC_SNAIL =
        ENTITY_TYPES.register("moc_snail", () -> EntityType.Builder
                .of(MoCSnailEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.3F)
                .clientTrackingRange(6)
                .build("moc_snail"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCRoachEntity>> MOC_ROACH =
        ENTITY_TYPES.register("moc_roach", () -> EntityType.Builder
                .of(MoCRoachEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.3F)
                .eyeHeight(0.1F)
                .clientTrackingRange(6)
                .build("moc_roach"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCAntEntity>> MOC_ANT =
        ENTITY_TYPES.register("moc_ant", () -> EntityType.Builder
                .of(MoCAntEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.2F)
                .eyeHeight(0.1F)
                .clientTrackingRange(6)
                .build("moc_ant"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCMaggotEntity>> MOC_MAGGOT =
        ENTITY_TYPES.register("moc_maggot", () -> EntityType.Builder
                .of(MoCMaggotEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.3F)
                .eyeHeight(0.135F)
                .clientTrackingRange(6)
                .build("moc_maggot"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCCricketEntity>> MOC_CRICKET =
        ENTITY_TYPES.register("moc_cricket", () -> EntityType.Builder
                .of(MoCCricketEntity::new, MobCategory.AMBIENT)
                .sized(0.3F, 0.3F)
                .eyeHeight(0.15F)
                .clientTrackingRange(6)
                .build("moc_cricket"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCGrasshopperEntity>> MOC_GRASSHOPPER =
        ENTITY_TYPES.register("moc_grasshopper", () -> EntityType.Builder
                .of(MoCGrasshopperEntity::new, MobCategory.AMBIENT)
                .sized(0.4F, 0.3F)
                .eyeHeight(0.15F)
                .clientTrackingRange(6)
                .build("moc_grasshopper"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSilverSkeletonEntity>> MOC_SILVER_SKELETON =
        ENTITY_TYPES.register("moc_silver_skeleton", () -> EntityType.Builder
                .of(MoCSilverSkeletonEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(8)
                .build("moc_silver_skeleton"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCWraithEntity>> MOC_WRAITH =
        ENTITY_TYPES.register("moc_wraith", () -> EntityType.Builder
                .of(MoCWraithEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.3F)
                .clientTrackingRange(8)
                .build("moc_wraith"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCFlameWraithEntity>> MOC_FLAME_WRAITH =
        ENTITY_TYPES.register("moc_flame_wraith", () -> EntityType.Builder
                .of(MoCFlameWraithEntity::new, MobCategory.MONSTER)
                .sized(0.6F, 1.3F)
                .clientTrackingRange(8)
                .build("moc_flame_wraith"));
                
    public static final DeferredHolder<EntityType<?>, EntityType<MoCEntEntity>> MOC_ENT =
        ENTITY_TYPES.register("moc_ent", () -> EntityType.Builder
                .of(MoCEntEntity::new, MobCategory.CREATURE)
                .sized(1.4F, 7.0F)
                .eyeHeight(5.11F)
                .clientTrackingRange(10)
                .build("moc_ent"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCMiniGolemEntity>> MOC_MINI_GOLEM =
        ENTITY_TYPES.register("moc_mini_golem", () -> EntityType.Builder
                .of(MoCMiniGolemEntity::new, MobCategory.MONSTER)
                .sized(0.9F, 1.2F)
                .eyeHeight(1.1F)
                .clientTrackingRange(8)
                .build("moc_mini_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCBigGolemEntity>> MOC_BIG_GOLEM =
        ENTITY_TYPES.register("moc_big_golem", () -> EntityType.Builder
                .of(MoCBigGolemEntity::new, MobCategory.MONSTER)
                .sized(1.8F, 4.3F)
                .eyeHeight(4.02F)
                .clientTrackingRange(10)
                .build("moc_big_golem"));

    // Generic egg — sits still, hatches into whatever HatchEntityType it was
    // set to (see MoCEggEntity). Same tiny size no matter what's inside.
    public static final DeferredHolder<EntityType<?>, EntityType<MoCEggEntity>> MOC_EGG =
            ENTITY_TYPES.register("moc_egg", () -> EntityType.Builder
                    .of(MoCEggEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(6)
                    .build("moc_egg"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCKittyBedEntity>> MOC_KITTY_BED =
            ENTITY_TYPES.register("moc_kitty_bed", () -> EntityType.Builder
                        .of(MoCKittyBedEntity::new, MobCategory.MISC)
                        .sized(1.0F, 0.4F)
                        .clientTrackingRange(6)
                        .build("moc_kitty_bed"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCLitterBoxEntity>> MOC_LITTER_BOX =
        ENTITY_TYPES.register("moc_litter_box", () -> EntityType.Builder
                .of(MoCLitterBoxEntity::new, MobCategory.MISC)
                .sized(1.0F, 0.4F)
                .clientTrackingRange(6)
                .build("moc_litter_box"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCThrowableRockEntity>> MOC_THROWABLE_ROCK =
        ENTITY_TYPES.register("moc_throwable_rock", () -> EntityType.Builder
                .<MoCThrowableRockEntity>of(MoCThrowableRockEntity::new, MobCategory.MISC)
                .sized(1.0F, 1.0F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("moc_throwable_rock"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCSummonedRockEntity>> MOC_SUMMONED_ROCK =
        ENTITY_TYPES.register("moc_summoned_rock", () -> EntityType.Builder
                .<MoCSummonedRockEntity>of(MoCSummonedRockEntity::new, MobCategory.MISC)
                .sized(1.0F, 1.0F)
                .clientTrackingRange(8)
                .updateInterval(2)
                .build("moc_summoned_rock"));
}
