package com.example.neomocreatures.entity.scorpion;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = NeoMoCreatures.MODID)
public class CaveScorpionSpawner {

    private static final int CHECK_INTERVAL_TICKS = 20 * 3;
    private static final int MAX_Y = 40;
    private static final int SPAWN_CHANCE_DENOMINATOR = 1;
    private static final int SEARCH_RADIUS = 24;
    private static final int MAX_TRIES = 10;
    private static final int VERTICAL_SEARCH_RANGE = 8;
    private static final double MIN_PLAYER_DISTANCE_SQR = SEARCH_RADIUS * SEARCH_RADIUS;
    private static final int MAX_LIGHT_LEVEL = 9;
    private static final int BABIES_CHANCE_DENOMINATOR = 4;

    private static int tickCounter;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (++tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.blockPosition().getY() > MAX_Y) {
                continue;
            }
            if (level.random.nextInt(SPAWN_CHANCE_DENOMINATOR) != 0) {
                continue;
            }
            tryCaveSpawnNear(level, player);
        }
    }

    private static void tryCaveSpawnNear(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < MAX_TRIES; i++) {
            int dx = level.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int dz = level.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int dy = level.random.nextInt(VERTICAL_SEARCH_RANGE * 2) - VERTICAL_SEARCH_RANGE;
            BlockPos pos = center.offset(dx, dy, dz);

            if (pos.getY() > MAX_Y
                    || player.distanceToSqr(Vec3.atCenterOf(pos)) < MIN_PLAYER_DISTANCE_SQR
                    || !level.getWorldBorder().isWithinBounds(pos)
                    // Never force-load a chunk just to test a candidate position.
                    || !level.hasChunkAt(pos)) {
                continue;
            }
            BlockPos above = pos.above();
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getBlockState(above).getCollisionShape(level, above).isEmpty();
            if (!spaceOk || level.getBlockState(pos.below()).isAir()) {
                continue;
            }
            if (level.getMaxLocalRawBrightness(pos) > MAX_LIGHT_LEVEL) {
                continue;
            }

            MoCScorpionEntity scorpion = ModEntities.MOC_SCORPION.get().create(level);
            if (scorpion == null) {
                NeoMoCreatures.LOGGER.warn("Could not create a cave scorpion entity");
                return;
            }
            scorpion.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    level.random.nextFloat() * 360F, 0F);
            scorpion.setVariant(ScorpionVariant.CAVE);
            if (level.random.nextInt(BABIES_CHANCE_DENOMINATOR) == 0) {
                scorpion.setHasBabiesPublic(true);
            }
            if (scorpion.checkSpawnObstruction(level)) {
                level.addFreshEntity(scorpion);
            }
            return;
        }
    }
}