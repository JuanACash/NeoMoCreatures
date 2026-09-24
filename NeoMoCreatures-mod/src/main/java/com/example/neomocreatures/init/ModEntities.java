package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.entity.MoCLitterBoxEntity;
import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;
import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.MoCMediumFishEntity;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.MoCBearEntity;
import com.example.neomocreatures.entity.MoCKomodoDragonEntity;
import com.example.neomocreatures.entity.MoCFoxEntity;
import com.example.neomocreatures.entity.MoCRaccoonEntity;
import com.example.neomocreatures.entity.MoCTurkeyEntity;
import com.example.neomocreatures.entity.MoCGoatEntity;
import com.example.neomocreatures.entity.MoCKittyEntity;
import com.example.neomocreatures.entity.MoCSnakeEntity;
import com.example.neomocreatures.entity.MoCBunnyEntity;
import com.example.neomocreatures.entity.MoCBirdEntity;
import com.example.neomocreatures.entity.MoCSharkEntity;
import com.example.neomocreatures.entity.MoCTurtleEntity;
import com.example.neomocreatures.entity.MoCStingrayEntity;
import com.example.neomocreatures.entity.MoCDolphinEntity;
import com.example.neomocreatures.entity.MoCMantaRayEntity;
import com.example.neomocreatures.entity.MoCFishyEntity;
import com.example.neomocreatures.entity.MoCCodEntity;
import com.example.neomocreatures.entity.MoCSalmonEntity;
import com.example.neomocreatures.entity.MoCBassEntity;
import com.example.neomocreatures.entity.MoCSmallFishEntity;
import com.example.neomocreatures.entity.MoCJellyfishEntity;
import com.example.neomocreatures.entity.MoCCrabEntity;
import com.example.neomocreatures.entity.MoCCrocodileEntity;
import com.example.neomocreatures.entity.MoCGreenOgreEntity;
import com.example.neomocreatures.entity.MoCFireOgreEntity;
import com.example.neomocreatures.entity.MoCCaveOgreEntity;
import com.example.neomocreatures.entity.MoCWerewolfEntity;
import com.example.neomocreatures.entity.MoCWildWolfEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
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
                .of(com.example.neomocreatures.entity.MoCKittyEntity::new, MobCategory.CREATURE)
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

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.neomocreatures.entity.MoCLitterBoxEntity>> MOC_LITTER_BOX =
        ENTITY_TYPES.register("moc_litter_box", () -> EntityType.Builder
                .of(com.example.neomocreatures.entity.MoCLitterBoxEntity::new, MobCategory.MISC)
                .sized(1.0F, 0.4F)
                .clientTrackingRange(6)
                .build("moc_litter_box"));

    public static void registerAttributes(IEventBus modEventBus) {
        modEventBus.addListener((EntityAttributeCreationEvent event) -> {
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
        });
        }
        
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                MOC_HORSE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) ->
                        Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        }
    public static void registerHorseMobSpawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        event.register(MOC_HORSE_MOB.get(),
                net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                        // Same "does this space actually block a mob" check vanilla
                        // uses — this is what lets zombies/skeletons stand in tall
                        // grass, flowers, snow layers, crimson/warped roots, etc.,
                        // instead of requiring literal air.
                        boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
                        if (!spaceOk) {
                        return false;
                        }
                        if (level.getBlockState(pos.below()).isAir()) {
                        return false; // still needs solid ground underneath
                        }

                        if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                        return true; // "almost anywhere" in the Nether — no light restriction
                        }

                        // The exact same darkness rule zombies/skeletons/spiders use
                        // (accounts for time of day, moon phase, sky vs block light).
                        return net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
                },
                net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerManticoreSpawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        event.register(MOC_MANTICORE.get(),
                net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                        boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
                        if (!spaceOk) {
                        return false;
                        }
                        if (!level.getBlockState(pos.below()).canOcclude()) {
                        return false;
                        }

                        if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                        return true; // red manticores — no light restriction in the Nether
                        }

                        // Wiki: light level 7 or less, Easy difficulty or higher (not Peaceful).
                        if (level.getLevel().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
                        return false;
                        }
                        // Same darkness rule zombies/skeletons use — accounts for time of day
                        // and moon phase, unlike a raw sky-light comparison.
                        return net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
                },
                net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerScorpionSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(MOC_SCORPION.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                        boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
                        if (!spaceOk || !level.getBlockState(pos.below()).canOcclude()) {
                        return false;
                        }
                        if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                        return true;
                        }
                        if (level.getLevel().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
                        return false;
                        }
                        if (pos.getY() <= 40 && !level.canSeeSky(pos)) {
                        return true; // cave scorpion — genuinely underground, no light restriction
                        }
                        // Same darkness rule zombies/skeletons use — accounts for time of day
                        // and moon phase, unlike a raw sky-light comparison.
                        return net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerWyvernSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                for (DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> wyvernType
                        : java.util.List.of(WYVERN, WYVERN_TIER2, WYVERN_MOTHER)) {
                        event.register(
                                wyvernType.get(),
                                SpawnPlacementTypes.NO_RESTRICTIONS,
                                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                (type, level, reason, pos, random) ->
                                        level.getBlockState(pos).getCollisionShape(level, pos).isEmpty(),
                                RegisterSpawnPlacementsEvent.Operation.REPLACE
                        );
                }
        }

        public static void registerElephantSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_ELEPHANT.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                if (!level.getBlockState(pos.below()).canOcclude()) {
                                return false; // needs an actual opaque block underneath
                                }
                                // Large space above — tall grass, flowers, snow layers, etc. don't
                                // count against this since they have no real collision shape.
                                for (int y = 0; y < 3; y++) {
                                if (!level.getBlockState(pos.above(y)).getCollisionShape(level, pos.above(y)).isEmpty()) {
                                        return false;
                                }
                                }
                                return level.getRawBrightness(pos, 0) >= 9;
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
        public static void registerBigCatSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(
                        MOC_BIG_CAT.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE
                );
        }

        public static void registerOstrichSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_OSTRICH.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                if (!level.getBlockState(pos.below()).canOcclude()) {
                                return false; // needs solid ground underneath (works on sand too, not just grass)
                                }
                                return level.getRawBrightness(pos, 0) >= 9;
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerBearSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_BEAR.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                var biome = level.getBiome(pos);
                                boolean onFrozenOceanIce = (biome.is(net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN)
                                        || biome.is(net.minecraft.world.level.biome.Biomes.DEEP_FROZEN_OCEAN))
                                        && level.getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.ICE);

                                // Covers grass_block, snow layers, snow_block and packed_ice for
                                // black/grizzly/panda and most polar bear terrain; the frozen-ocean
                                // check above adds plain ICE, which isn't in that tag.
                                boolean groundOk = onFrozenOceanIce
                                        || level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.ANIMALS_SPAWNABLE_ON);

                                return groundOk && level.getRawBrightness(pos, 0) >= 9;
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerKomodoSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_KOMODO_DRAGON.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerFoxSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_FOX.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerRaccoonSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_RACCOON.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
    
        public static void registerTurkeySpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_TURKEY.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerGoatSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_GOAT.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        net.minecraft.world.entity.animal.Animal::checkAnimalSpawnRules,
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerSnakeSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_SNAKE.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                // Wiki: snakes can spawn on sand/clay/dirt too — not just
                                // the vanilla "animals_spawnable_on" (grass-only) tag
                                // Animal.checkAnimalSpawnRules() requires — and pythons
                                // can spawn right on the water surface, but only in
                                // mangrove swamps.
                                if (level.getRawBrightness(pos, 0) <= 8) {
                                        return false;
                                }
                                net.minecraft.world.level.block.state.BlockState below =
                                        level.getBlockState(pos.below());
                                if (below.is(net.minecraft.tags.BlockTags.ANIMALS_SPAWNABLE_ON)
                                        || below.is(net.minecraft.tags.BlockTags.TERRACOTTA)
                                        || below.is(net.minecraft.world.level.block.Blocks.SAND)
                                        || below.is(net.minecraft.world.level.block.Blocks.RED_SAND)
                                        || below.is(net.minecraft.world.level.block.Blocks.DIRT)
                                        || below.is(net.minecraft.world.level.block.Blocks.COARSE_DIRT)
                                        || below.is(net.minecraft.world.level.block.Blocks.MUD)
                                        || below.is(net.minecraft.world.level.block.Blocks.MANGROVE_ROOTS)) {
                                        return true;
                                }
                                return level.getFluidState(pos.below()).is(net.minecraft.tags.FluidTags.WATER)
                                        && level.getBiome(pos).is(net.minecraft.world.level.biome.Biomes.MANGROVE_SWAMP);
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerBunnySpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_BUNNY.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerBirdSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_BIRD.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                Animal.checkAnimalSpawnRules(type, level, reason, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerSharkSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_SHARK.get(),
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerTurtleSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_TURTLE.get(),
                        SpawnPlacementTypes.NO_RESTRICTIONS,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                // Amphibious: it may appear in shallow water or on mud, sand and dirt.
                                // Skylight (raw brightness) rules out caves and underground water pockets.
                                if (level.getRawBrightness(pos, 0) <= 8) {
                                        return false;
                                }
                                if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                                        return true;
                                }
                                net.minecraft.world.level.block.state.BlockState ground = level.getBlockState(pos.below());
                                return ground.is(net.minecraft.tags.BlockTags.DIRT) || ground.is(net.minecraft.tags.BlockTags.SAND);
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerStingraySpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_STINGRAY.get(),
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                // Same rule as vanilla surface water animals: within 13 blocks under sea level,
                                // with water below and above. The stingray then settles at its own depth.
                                int seaLevel = level.getSeaLevel();
                                return pos.getY() >= seaLevel - 13 && pos.getY() <= seaLevel
                                        && level.getFluidState(pos.below()).is(net.minecraft.tags.FluidTags.WATER)
                                        && level.getBlockState(pos.above()).is(net.minecraft.world.level.block.Blocks.WATER);
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerDolphinSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_DOLPHIN.get(),
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                // Original: any water position within 12 blocks under sea level.
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerMantaRaySpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_MANTA_RAY.get(),
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                // Original: any water position within 12 blocks under sea level.
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
        
        public static void registerFishySpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_FISHY.get(),
                        SpawnPlacementTypes.IN_WATER,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                // Wiki: layer 46 up to sea level, in any water.
                                pos.getY() >= 46 && pos.getY() <= level.getSeaLevel()
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerMediumFishSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                // A separate lambda per call, not a shared variable: SpawnPredicate<T> is invariant,
                // so a predicate typed for the abstract base cannot be reused for each concrete
                // EntityType<MoCCodEntity>/<MoCSalmonEntity>/<MoCBassEntity> — a fresh lambda literal
                // adapts to whichever T each register() call needs.
                event.register(MOC_COD.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                // Original: any water position within 12 blocks under sea level.
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
                event.register(MOC_SALMON.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
                event.register(MOC_BASS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerSmallFishSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_SMALL_FISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                // Original: any water position within 12 blocks under sea level.
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerJellyfishSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                pos.getY() >= level.getSeaLevel() - 12
                                        && level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerCrabSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_CRAB.get(), net.minecraft.world.entity.SpawnPlacementTypes.NO_RESTRICTIONS,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                boolean inWater = level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER);
                                boolean onGround = level.getBlockState(pos.below()).isSolid()
                                        && level.getBlockState(pos).isPathfindable(net.minecraft.world.level.pathfinder.PathComputationType.LAND);
                                return inWater || onGround;
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerCrocodileSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_CROCODILE.get(), net.minecraft.world.entity.SpawnPlacementTypes.NO_RESTRICTIONS,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                boolean inWater = level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER);
                                boolean onGround = level.getBlockState(pos.below()).isSolid()
                                        && level.getBlockState(pos).isPathfindable(net.minecraft.world.level.pathfinder.PathComputationType.LAND);
                                return inWater || onGround;
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerGreenOgreSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                // Wiki: surface at night like a zombie, but also in caves — the same darkness check
                // already covers both, since it doesn't care whether the darkness comes from being
                // underground or from nighttime on the surface.
                event.register(MOC_GREEN_OGRE.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                level.getBlockState(pos.below()).canOcclude()
                                        && net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerFireOgreSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_FIRE_OGRE.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                if (!level.getBlockState(pos.below()).canOcclude()) {
                                return false;
                                }
                                if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                                // Wiki: "can be found at any light level" in the Nether.
                                return true;
                                }
                                // Wiki: only a 25% chance to spawn in the Overworld at all.
                                return random.nextFloat() < 0.25F
                                        && net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerCaveOgreSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                // Same rule as the Cave Scorpion: genuinely underground, no light restriction.
                event.register(MOC_CAVE_OGRE.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                level.getBlockState(pos.below()).canOcclude()
                                        && pos.getY() <= 40 && !level.canSeeSky(pos),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerWerewolfSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_WEREWOLF.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> {
                                if (!level.getBlockState(pos.below()).canOcclude()) {
                                        return false;
                                }
                                // Nether spawns ignore light level entirely, like the Fire Ogre.
                                if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                                        return true;
                                }
                                return net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random);
                        },
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        public static void registerWerewolfNetherSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_WEREWOLF.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) -> level.getBlockState(pos.below()).canOcclude(),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
        
        public static void registerWildWolfSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                event.register(MOC_WILD_WOLF.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (type, level, reason, pos, random) ->
                                level.getBlockState(pos.below()).canOcclude()
                                        && net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn(level, pos, random),
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }
}