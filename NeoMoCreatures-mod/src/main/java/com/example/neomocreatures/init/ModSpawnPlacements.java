package com.example.neomocreatures.init;

import static com.example.neomocreatures.init.ModEntities.MOC_ANT;
import static com.example.neomocreatures.init.ModEntities.MOC_BASS;
import static com.example.neomocreatures.init.ModEntities.MOC_BEAR;
import static com.example.neomocreatures.init.ModEntities.MOC_BEE;
import static com.example.neomocreatures.init.ModEntities.MOC_BIG_CAT;
import static com.example.neomocreatures.init.ModEntities.MOC_BIG_GOLEM;
import static com.example.neomocreatures.init.ModEntities.MOC_BIRD;
import static com.example.neomocreatures.init.ModEntities.MOC_BOAR;
import static com.example.neomocreatures.init.ModEntities.MOC_BUNNY;
import static com.example.neomocreatures.init.ModEntities.MOC_BUTTERFLY;
import static com.example.neomocreatures.init.ModEntities.MOC_CAVE_OGRE;
import static com.example.neomocreatures.init.ModEntities.MOC_COD;
import static com.example.neomocreatures.init.ModEntities.MOC_CRAB;
import static com.example.neomocreatures.init.ModEntities.MOC_CRICKET;
import static com.example.neomocreatures.init.ModEntities.MOC_CROCODILE;
import static com.example.neomocreatures.init.ModEntities.MOC_DEER;
import static com.example.neomocreatures.init.ModEntities.MOC_DOLPHIN;
import static com.example.neomocreatures.init.ModEntities.MOC_DRAGONFLY;
import static com.example.neomocreatures.init.ModEntities.MOC_DUCK;
import static com.example.neomocreatures.init.ModEntities.MOC_ELEPHANT;
import static com.example.neomocreatures.init.ModEntities.MOC_ENT;
import static com.example.neomocreatures.init.ModEntities.MOC_FILCH_LIZARD;
import static com.example.neomocreatures.init.ModEntities.MOC_FIREFLY;
import static com.example.neomocreatures.init.ModEntities.MOC_FIRE_OGRE;
import static com.example.neomocreatures.init.ModEntities.MOC_FISHY;
import static com.example.neomocreatures.init.ModEntities.MOC_FLAME_WRAITH;
import static com.example.neomocreatures.init.ModEntities.MOC_FLY;
import static com.example.neomocreatures.init.ModEntities.MOC_FOX;
import static com.example.neomocreatures.init.ModEntities.MOC_GOAT;
import static com.example.neomocreatures.init.ModEntities.MOC_GRASSHOPPER;
import static com.example.neomocreatures.init.ModEntities.MOC_GREEN_OGRE;
import static com.example.neomocreatures.init.ModEntities.MOC_HELL_RAT;
import static com.example.neomocreatures.init.ModEntities.MOC_HORSE;
import static com.example.neomocreatures.init.ModEntities.MOC_HORSE_MOB;
import static com.example.neomocreatures.init.ModEntities.MOC_JELLYFISH;
import static com.example.neomocreatures.init.ModEntities.MOC_KOMODO_DRAGON;
import static com.example.neomocreatures.init.ModEntities.MOC_MAGGOT;
import static com.example.neomocreatures.init.ModEntities.MOC_MANTA_RAY;
import static com.example.neomocreatures.init.ModEntities.MOC_MANTICORE;
import static com.example.neomocreatures.init.ModEntities.MOC_MINI_GOLEM;
import static com.example.neomocreatures.init.ModEntities.MOC_MOLE;
import static com.example.neomocreatures.init.ModEntities.MOC_MOUSE;
import static com.example.neomocreatures.init.ModEntities.MOC_OSTRICH;
import static com.example.neomocreatures.init.ModEntities.MOC_RACCOON;
import static com.example.neomocreatures.init.ModEntities.MOC_RAT;
import static com.example.neomocreatures.init.ModEntities.MOC_ROACH;
import static com.example.neomocreatures.init.ModEntities.MOC_SALMON;
import static com.example.neomocreatures.init.ModEntities.MOC_SCORPION;
import static com.example.neomocreatures.init.ModEntities.MOC_SHARK;
import static com.example.neomocreatures.init.ModEntities.MOC_SILVER_SKELETON;
import static com.example.neomocreatures.init.ModEntities.MOC_SMALL_FISH;
import static com.example.neomocreatures.init.ModEntities.MOC_SNAIL;
import static com.example.neomocreatures.init.ModEntities.MOC_SNAKE;
import static com.example.neomocreatures.init.ModEntities.MOC_STINGRAY;
import static com.example.neomocreatures.init.ModEntities.MOC_TURKEY;
import static com.example.neomocreatures.init.ModEntities.MOC_TURTLE;
import static com.example.neomocreatures.init.ModEntities.MOC_WEREWOLF;
import static com.example.neomocreatures.init.ModEntities.MOC_WILD_WOLF;
import static com.example.neomocreatures.init.ModEntities.MOC_WRAITH;
import static com.example.neomocreatures.init.ModEntities.WYVERN;
import static com.example.neomocreatures.init.ModEntities.WYVERN_MOTHER;
import static com.example.neomocreatures.init.ModEntities.WYVERN_TIER2;

import java.util.List;

import com.example.neomocreatures.entity.MoCWyvernEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements.SpawnPredicate;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Spawn placement rules for every naturally spawning mod creature.
 * Shared rules are factory methods, so each call gets a lambda typed for its own entity
 * (SpawnPredicate is invariant, a single shared instance would not compile).
 */
public final class ModSpawnPlacements {

    private ModSpawnPlacements() {
        // Static registration only, no instances
    }

    /** Mod event bus listener. */
    public static void register(RegisterSpawnPlacementsEvent event) {
        // Farm animals and pets
        place(event, MOC_HORSE.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_BIG_CAT.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_KOMODO_DRAGON.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_FOX.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_RACCOON.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_TURKEY.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_GOAT.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_BUNNY.get(), SpawnPlacementTypes.ON_GROUND, animalRules());
        place(event, MOC_BIRD.get(), SpawnPlacementTypes.ON_GROUND, animalRules());

        place(event, MOC_HORSE_MOB.get(), SpawnPlacementTypes.ON_GROUND, horseMobRule());
        registerWyverns(event);
        place(event, MOC_MANTICORE.get(), SpawnPlacementTypes.ON_GROUND, manticoreRule());
        place(event, MOC_ELEPHANT.get(), SpawnPlacementTypes.ON_GROUND, elephantRule());
        place(event, MOC_SCORPION.get(), SpawnPlacementTypes.ON_GROUND, scorpionRule());
        place(event, MOC_OSTRICH.get(), SpawnPlacementTypes.ON_GROUND, solidGroundWithLightAtLeast9());
        // NO_RESTRICTIONS, not ON_GROUND: vanilla ice only accepts the vanilla polar bear as a
        // spawn, so ON_GROUND always refused ours on frozen oceans. The rule below does every check.
        place(event, MOC_BEAR.get(), SpawnPlacementTypes.NO_RESTRICTIONS, bearRule());
        place(event, MOC_SNAKE.get(), SpawnPlacementTypes.ON_GROUND, snakeRule());
        place(event, MOC_TURTLE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, turtleRule());

        // Sea creatures
        place(event, MOC_SHARK.get(), SpawnPlacementTypes.IN_WATER, inWater());
        place(event, MOC_STINGRAY.get(), SpawnPlacementTypes.IN_WATER, stingrayRule());
        place(event, MOC_DOLPHIN.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_MANTA_RAY.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_FISHY.get(), SpawnPlacementTypes.IN_WATER, fishyRule());
        place(event, MOC_COD.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_SALMON.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_BASS.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_SMALL_FISH.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, nearSeaLevelWater());
        place(event, MOC_CRAB.get(), SpawnPlacementTypes.NO_RESTRICTIONS, inWaterOrOnLand());
        place(event, MOC_CROCODILE.get(), SpawnPlacementTypes.NO_RESTRICTIONS, inWaterOrOnLand());

        // Monsters
        // Wiki: surface at night like a zombie, but also in caves; the darkness check covers both.
        place(event, MOC_GREEN_OGRE.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInDarkness());
        place(event, MOC_FIRE_OGRE.get(), SpawnPlacementTypes.ON_GROUND, fireOgreRule());
        // Same rule as the Cave Scorpion: genuinely underground, no light restriction.
        place(event, MOC_CAVE_OGRE.get(), SpawnPlacementTypes.ON_GROUND, deepUnderground());
        place(event, MOC_WEREWOLF.get(), SpawnPlacementTypes.ON_GROUND, solidGroundDarkOrNether());
        place(event, MOC_WILD_WOLF.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInDarkness());

        // Small land animals
        place(event, MOC_BOAR.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_DEER.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_RAT.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInDarkness());
        place(event, MOC_HELL_RAT.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_MOUSE.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_MOLE.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_DUCK.get(), SpawnPlacementTypes.ON_GROUND, duckRule());

        registerInsects(event);

        // Undead, spirits and golems
        place(event, MOC_SILVER_SKELETON.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInDarkness());
        place(event, MOC_WRAITH.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInDarkness());
        place(event, MOC_FLAME_WRAITH.get(), SpawnPlacementTypes.ON_GROUND, solidGround());
        place(event, MOC_ENT.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInLight());
        place(event, MOC_MINI_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Monster::checkMonsterSpawnRules);
        place(event, MOC_BIG_GOLEM.get(), SpawnPlacementTypes.ON_GROUND, Monster::checkMonsterSpawnRules);
        // Original MoCEntityAnimal rule: solid ground (sand included, not just grass) and light above 8.
        place(event, MOC_FILCH_LIZARD.get(), SpawnPlacementTypes.ON_GROUND, solidGroundInLight());
    }

    // ---------------------------------------------------------------------
    // Registration helpers
    // ---------------------------------------------------------------------

    /** Every mod placement uses the same heightmap and replaces any default rule. */
    private static <T extends Mob> void place(RegisterSpawnPlacementsEvent event, EntityType<T> type,
                                              SpawnPlacementType placement, SpawnPredicate<T> rule) {
        event.register(type, placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, rule,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static void registerWyverns(RegisterSpawnPlacementsEvent event) {
        for (DeferredHolder<EntityType<?>, EntityType<MoCWyvernEntity>> wyvernType
                : List.of(WYVERN, WYVERN_TIER2, WYVERN_MOTHER)) {
            place(event, wyvernType.get(), SpawnPlacementTypes.NO_RESTRICTIONS,
                    (type, level, reason, pos, random) ->
                            level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                                    // Outside the Wyvern Lair (rare mountain spawns with biome mods)
                                    // they only appear in the open, never inside caves.
                                    && (level.getLevel().dimension() == ModDimensions.WYVERN_LAIR
                                            || level.canSeeSky(pos)));
        }
    }

    /** Any solid block qualifies: sand, red sand, every terracotta, coarse dirt, gravel, podzol,
     *  mycelium, moss... Wiki: light 9+ and 2 free blocks above (the maggot only needs 1). */
    private static void registerInsects(RegisterSpawnPlacementsEvent event) {
        place(event, MOC_FLY.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_BUTTERFLY.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_DRAGONFLY.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_FIREFLY.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_BEE.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_SNAIL.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_ANT.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_MAGGOT.get(), SpawnPlacementTypes.ON_GROUND, insectRule(false));
        place(event, MOC_CRICKET.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_ROACH.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
        place(event, MOC_GRASSHOPPER.get(), SpawnPlacementTypes.ON_GROUND, insectRule(true));
    }

    // ---------------------------------------------------------------------
    // Shared rules
    // ---------------------------------------------------------------------

    /** Vanilla animal rule: grass-type block below and enough light. */
    private static <T extends Animal> SpawnPredicate<T> animalRules() {
        return Animal::checkAnimalSpawnRules;
    }

    private static <T extends Mob> SpawnPredicate<T> solidGround() {
        return (type, level, reason, pos, random) -> level.getBlockState(pos.below()).canOcclude();
    }

    /** Same darkness rule zombies/skeletons use (time of day, moon phase, sky vs block light). */
    private static <T extends Mob> SpawnPredicate<T> solidGroundInDarkness() {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).canOcclude()
                        && Monster.isDarkEnoughToSpawn(level, pos, random);
    }

    private static <T extends Mob> SpawnPredicate<T> solidGroundInLight() {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).canOcclude()
                        && level.getRawBrightness(pos, 0) > 8;
    }

    /** Needs solid ground underneath (works on sand too, not just grass). */
    private static <T extends Mob> SpawnPredicate<T> solidGroundWithLightAtLeast9() {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).canOcclude()
                        && level.getRawBrightness(pos, 0) >= 9;
    }

    /** Nether spawns ignore light level entirely, like the Fire Ogre. */
    private static <T extends Mob> SpawnPredicate<T> solidGroundDarkOrNether() {
        return (type, level, reason, pos, random) -> {
            if (!level.getBlockState(pos.below()).canOcclude()) {
                return false;
            }
            if (level.getLevel().dimension() == Level.NETHER) {
                return true;
            }
            return Monster.isDarkEnoughToSpawn(level, pos, random);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> deepUnderground() {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).canOcclude()
                        && pos.getY() <= 40 && !level.canSeeSky(pos);
    }

    private static <T extends Mob> SpawnPredicate<T> inWater() {
        return (type, level, reason, pos, random) -> level.getFluidState(pos).is(FluidTags.WATER);
    }

    /** Original: any water position within 12 blocks under sea level. */
    private static <T extends Mob> SpawnPredicate<T> nearSeaLevelWater() {
        return (type, level, reason, pos, random) ->
                pos.getY() >= level.getSeaLevel() - 12
                        && level.getFluidState(pos).is(FluidTags.WATER);
    }

    /** Semi-aquatic: either in water or standing on solid walkable ground. */
    private static <T extends Mob> SpawnPredicate<T> inWaterOrOnLand() {
        return (type, level, reason, pos, random) -> {
            boolean inWater = level.getFluidState(pos).is(FluidTags.WATER);
            boolean onGround = level.getBlockState(pos.below()).isSolid()
                    && level.getBlockState(pos).isPathfindable(PathComputationType.LAND);
            return inWater || onGround;
        };
    }

    private static <T extends Mob> SpawnPredicate<T> insectRule(boolean needsTwoBlocksOfSpace) {
        return (type, level, reason, pos, random) ->
                level.getBlockState(pos.below()).canOcclude()
                        && level.getRawBrightness(pos, 0) > 8
                        && (!needsTwoBlocksOfSpace || level.isEmptyBlock(pos.above()));
    }

    // ---------------------------------------------------------------------
    // Creature specific rules
    // ---------------------------------------------------------------------

    private static <T extends Mob> SpawnPredicate<T> horseMobRule() {
        return (type, level, reason, pos, random) -> {
            // Same "does this space actually block a mob" check vanilla uses: this is what lets
            // zombies/skeletons stand in tall grass, flowers, snow layers, crimson/warped roots,
            // etc., instead of requiring literal air.
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
            if (!spaceOk) {
                return false;
            }
            if (level.getBlockState(pos.below()).isAir()) {
                return false; // still needs solid ground underneath
            }
            if (level.getLevel().dimension() == Level.NETHER) {
                return true; // "almost anywhere" in the Nether, no light restriction
            }
            // The exact same darkness rule zombies/skeletons/spiders use.
            return Monster.isDarkEnoughToSpawn(level, pos, random);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> manticoreRule() {
        return (type, level, reason, pos, random) -> {
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
            if (!spaceOk) {
                return false;
            }
            if (!level.getBlockState(pos.below()).canOcclude()) {
                return false;
            }
            if (level.getLevel().dimension() == Level.NETHER) {
                return true; // red manticores, no light restriction in the Nether
            }
            // Wiki: light level 7 or less, Easy difficulty or higher (not Peaceful).
            if (level.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return Monster.isDarkEnoughToSpawn(level, pos, random);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> scorpionRule() {
        return (type, level, reason, pos, random) -> {
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
            if (!spaceOk || !level.getBlockState(pos.below()).canOcclude()) {
                return false;
            }
            if (level.getLevel().dimension() == Level.NETHER) {
                return true;
            }
            if (level.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            if (pos.getY() <= 40 && !level.canSeeSky(pos)) {
                return true; // cave scorpion, genuinely underground, no light restriction
            }
            return Monster.isDarkEnoughToSpawn(level, pos, random);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> elephantRule() {
        return (type, level, reason, pos, random) -> {
            if (!level.getBlockState(pos.below()).canOcclude()) {
                return false; // needs an actual opaque block underneath
            }
            // Large space above: tall grass, flowers, snow layers, etc. don't count against
            // this since they have no real collision shape.
            for (int y = 0; y < 3; y++) {
                if (!level.getBlockState(pos.above(y)).getCollisionShape(level, pos.above(y)).isEmpty()) {
                    return false;
                }
            }
            return level.getRawBrightness(pos, 0) >= 9;
        };
    }

    private static <T extends Mob> SpawnPredicate<T> bearRule() {
        return (type, level, reason, pos, random) -> {
            // What ON_GROUND used to check: open, dry space to stand in.
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getFluidState(pos).isEmpty();
            if (!spaceOk) {
                return false;
            }
            // #minecraft:animals_spawnable_on is just grass; ModTags.BEAR_SPAWNABLE_ON adds
            // every kind of ice and snow block, so polar bears spawn on ice spikes and icebergs.
            BlockState ground = level.getBlockState(pos.below());
            boolean groundOk = ground.is(BlockTags.ANIMALS_SPAWNABLE_ON) || ground.is(ModTags.BEAR_SPAWNABLE_ON);
            return groundOk && level.getRawBrightness(pos, 0) >= 9;
        };
    }

    private static <T extends Mob> SpawnPredicate<T> snakeRule() {
        return (type, level, reason, pos, random) -> {
            // Wiki: snakes can spawn on sand/clay/dirt too, not just the vanilla grass-only
            // "animals_spawnable_on" tag, and pythons can spawn right on the water surface,
            // but only in mangrove swamps.
            if (level.getRawBrightness(pos, 0) <= 8) {
                return false;
            }
            BlockState below = level.getBlockState(pos.below());
            if (below.is(BlockTags.ANIMALS_SPAWNABLE_ON)
                    || below.is(BlockTags.TERRACOTTA)
                    || below.is(Blocks.SAND)
                    || below.is(Blocks.RED_SAND)
                    || below.is(Blocks.DIRT)
                    || below.is(Blocks.COARSE_DIRT)
                    || below.is(Blocks.MUD)
                    || below.is(Blocks.MANGROVE_ROOTS)) {
                return true;
            }
            return level.getFluidState(pos.below()).is(FluidTags.WATER)
                    && level.getBiome(pos).is(Biomes.MANGROVE_SWAMP);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> turtleRule() {
        return (type, level, reason, pos, random) -> {
            // Amphibious: it may appear in shallow water or on mud, sand and dirt.
            // Skylight (raw brightness) rules out caves and underground water pockets.
            if (level.getRawBrightness(pos, 0) <= 8) {
                return false;
            }
            if (level.getFluidState(pos).is(FluidTags.WATER)) {
                return true;
            }
            BlockState ground = level.getBlockState(pos.below());
            return ground.is(BlockTags.DIRT) || ground.is(BlockTags.SAND);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> stingrayRule() {
        return (type, level, reason, pos, random) -> {
            // Same rule as vanilla surface water animals: within 13 blocks under sea level,
            // with water below and above. The stingray then settles at its own depth.
            int seaLevel = level.getSeaLevel();
            return pos.getY() >= seaLevel - 13 && pos.getY() <= seaLevel
                    && level.getFluidState(pos.below()).is(FluidTags.WATER)
                    && level.getBlockState(pos.above()).is(Blocks.WATER);
        };
    }

    /** Wiki: layer 46 up to sea level, in any water. */
    private static <T extends Mob> SpawnPredicate<T> fishyRule() {
        return (type, level, reason, pos, random) ->
                pos.getY() >= 46 && pos.getY() <= level.getSeaLevel()
                        && level.getFluidState(pos).is(FluidTags.WATER);
    }

    private static <T extends Mob> SpawnPredicate<T> fireOgreRule() {
        return (type, level, reason, pos, random) -> {
            if (!level.getBlockState(pos.below()).canOcclude()) {
                return false;
            }
            if (level.getLevel().dimension() == Level.NETHER) {
                return true; // Wiki: "can be found at any light level" in the Nether
            }
            // Wiki: only a 25% chance to spawn in the Overworld at all.
            return random.nextFloat() < 0.25F && Monster.isDarkEnoughToSpawn(level, pos, random);
        };
    }

    private static <T extends Mob> SpawnPredicate<T> duckRule() {
        return (type, level, reason, pos, random) -> {
            if (!level.getBlockState(pos.below()).canOcclude()) {
                return false;
            }
            // "Near water": checks a small radius around the spawn point.
            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-5, -2, -5), pos.offset(5, 2, 5))) {
                if (level.getFluidState(checkPos).is(FluidTags.WATER)) {
                    return true;
                }
            }
            return false;
        };
    }
}
