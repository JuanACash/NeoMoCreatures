package com.example.neomocreatures.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Resolves the arrival point of the Ogre Lair: finds an open floor the first time,
 * builds the platform there, remembers it, and returns the same spot afterwards.
 */
public final class OgreLairArrivalLocator {

    private static final int SEARCH_CENTER_X = 0;
    private static final int SEARCH_CENTER_Z = 0;
    private static final int SEARCH_STEP = 8;
    private static final int MAX_SEARCH_RADIUS = 16;
    private static final int MIN_SEARCH_Y = 8;
    private static final int MAX_SEARCH_Y = 110;
    private static final int FALLBACK_FLOOR_Y = 64;

    private OgreLairArrivalLocator() {
    }

    /** @return the block the player's feet should be placed on (one above the platform floor) */
    public static BlockPos resolve(ServerLevel level) {
        OgreLairArrivalData data = OgreLairArrivalData.get(level);
        if (data.getFloorPos().isEmpty()) {
            BlockPos floorPos = findOpenFloor(level);
            OgreLairPortalPlatform.build(level, floorPos);
            data.setFloorPos(floorPos);
        }
        return data.getFloorPos().orElseThrow().above();
    }

    private static BlockPos findOpenFloor(ServerLevel level) {
        for (int radius = 0; radius <= MAX_SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    BlockPos found = scanColumn(level, SEARCH_CENTER_X + dx * SEARCH_STEP,
                            SEARCH_CENTER_Z + dz * SEARCH_STEP);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return new BlockPos(SEARCH_CENTER_X, FALLBACK_FLOOR_Y, SEARCH_CENTER_Z);
    }

    private static BlockPos scanColumn(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, MAX_SEARCH_Y, z);
        for (int y = MAX_SEARCH_Y; y >= MIN_SEARCH_Y; y--) {
            cursor.setY(y);
            if (OgreLairPortalPlatform.isSuitableSite(level, cursor)) {
                return cursor.immutable();
            }
        }
        return null;
    }
}