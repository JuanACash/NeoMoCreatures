package com.example.neomocreatures.worldgen;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Persists the fixed arrival floor of the Ogre Lair so every teleport lands on the same platform. */
public final class OgreLairArrivalData extends SavedData {

    private static final String DATA_NAME = "neomocreatures_ogre_lair_arrival";
    private static final String TAG_FLOOR_POS = "FloorPos";

    private BlockPos floorPos;

    private OgreLairArrivalData() {
    }

    public static OgreLairArrivalData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(OgreLairArrivalData::new, OgreLairArrivalData::load, null), DATA_NAME);
    }

    private static OgreLairArrivalData load(CompoundTag tag, HolderLookup.Provider registries) {
        OgreLairArrivalData data = new OgreLairArrivalData();
        if (tag.contains(TAG_FLOOR_POS)) {
            data.floorPos = BlockPos.of(tag.getLong(TAG_FLOOR_POS));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        if (floorPos != null) {
            tag.putLong(TAG_FLOOR_POS, floorPos.asLong());
        }
        return tag;
    }

    public Optional<BlockPos> getFloorPos() {
        return Optional.ofNullable(floorPos);
    }

    public void setFloorPos(BlockPos pos) {
        this.floorPos = pos.immutable();
        setDirty();
    }
}