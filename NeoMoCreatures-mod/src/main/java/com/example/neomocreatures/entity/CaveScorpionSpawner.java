package com.example.neomocreatures.entity;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class CaveScorpionSpawner {

    private static final int CHECK_INTERVAL_TICKS = 20 * 3;
    private static final int MAX_Y = 40;
    private static final int SPAWN_CHANCE_DENOMINATOR = 1;
    private static final int SEARCH_RADIUS = 24;
    private static final int MAX_TRIES = 10;

    private int tickCounter;

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide) {
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
            NeoMoCreatures.LOGGER.info("[CaveScorpion] Checking near player at Y={}", player.blockPosition().getY());
            tryCaveSpawnNear(level, player);
        }
    }

    private void tryCaveSpawnNear(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int i = 0; i < MAX_TRIES; i++) {
            int dx = level.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int dz = level.random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int dy = level.random.nextInt(16) - 8;
            BlockPos pos = center.offset(dx, dy, dz);

            if (pos.getY() > MAX_Y) {
                continue;
            }
            if (player.distanceToSqr(Vec3.atCenterOf(pos)) < 24.0D * 24.0D) {
                continue;
            }
            if (!level.getWorldBorder().isWithinBounds(pos)) {
                continue;
            }
            boolean spaceOk = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
            if (!spaceOk) {
                NeoMoCreatures.LOGGER.info("[CaveScorpion] try {} rejected: space not ok at {}", i, pos);
                continue;
            }
            if (level.getBlockState(pos.below()).isAir()) {
                NeoMoCreatures.LOGGER.info("[CaveScorpion] try {} rejected: no floor at {}", i, pos);
                continue;
            }
            int brightness = level.getMaxLocalRawBrightness(pos);
            if (brightness > 9) {
                NeoMoCreatures.LOGGER.info("[CaveScorpion] try {} rejected: too bright ({}) at {}", i, brightness, pos);
                continue;
            }

            MoCScorpionEntity scorpion = ModEntities.MOC_SCORPION.get().create(level);
            if (scorpion == null) {
                NeoMoCreatures.LOGGER.info("[CaveScorpion] entity.create() returned null!");
                return;
            }
            scorpion.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    level.random.nextFloat() * 360F, 0F);
            scorpion.setVariant(ScorpionVariant.CAVE);
            if (level.random.nextInt(4) == 0) {
                scorpion.setHasBabiesPublic(true);
            }
            if (scorpion.checkSpawnObstruction(level)) {
                level.addFreshEntity(scorpion);
            }
            return;
        }
        NeoMoCreatures.LOGGER.info("[CaveScorpion] all {} tries exhausted, none valid", MAX_TRIES);
    }
}