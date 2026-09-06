package com.example.neomocreatures.init;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;

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
    
}