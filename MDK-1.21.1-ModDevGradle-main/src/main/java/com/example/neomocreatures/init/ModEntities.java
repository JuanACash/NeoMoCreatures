package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;
import com.example.neomocreatures.entity.MoCBigCatEntity;
import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.MoCBearEntity;
import com.example.neomocreatures.entity.MoCKomodoDragonEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
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

    public static final DeferredHolder<EntityType<?>, EntityType<com.example.neomocreatures.entity.MoCBearEntity>> MOC_BEAR =
        ENTITY_TYPES.register("moc_bear", () -> EntityType.Builder
                .of(MoCBearEntity::new, MobCategory.CREATURE)
                .sized(1.1F, 1.5F)
                .clientTrackingRange(10)
                .build("moc_bear"));

    public static final DeferredHolder<EntityType<?>, EntityType<MoCKomodoDragonEntity>> MOC_KOMODO_DRAGON =
        ENTITY_TYPES.register("moc_komodo_dragon", () -> EntityType.Builder
                .of(MoCKomodoDragonEntity::new, MobCategory.CREATURE)
                .sized(1.3F, 0.9F)
                .clientTrackingRange(10)
                .build("moc_komodo_dragon"));

    // Generic egg — sits still, hatches into whatever HatchEntityType it was
    // set to (see MoCEggEntity). Same tiny size no matter what's inside.
    public static final DeferredHolder<EntityType<?>, EntityType<MoCEggEntity>> MOC_EGG =
            ENTITY_TYPES.register("moc_egg", () -> EntityType.Builder
                    .of(MoCEggEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(6)
                    .build("moc_egg"));

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
                event.put(MOC_BIG_CAT.get(), MoCBigCatEntity.createAttributes().build());
                event.put(MOC_MANTICORE.get(), MoCManticoreEntity.createAttributes().build());
                event.put(MOC_SCORPION.get(), MoCScorpionEntity.createAttributes().build());
                event.put(MOC_OSTRICH.get(), MoCOstrichEntity.createAttributes().build());
                event.put(MOC_BEAR.get(), MoCBearEntity.createAttributes().build());
                event.put(MOC_KOMODO_DRAGON.get(), MoCKomodoDragonEntity.createAttributes().build());
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
                // Wyverns fly, so no ground/light restrictions like the horse mob check —
                // the only requirement is that the space itself isn't solid (tall grass,
                // mushrooms, and other no-collision decoration are fine to spawn through).
                // Which biomes actually roll a wyvern is controlled entirely by each
                // Wyvern Lair biome's own spawners.creature list, not by anything here.
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
    
}