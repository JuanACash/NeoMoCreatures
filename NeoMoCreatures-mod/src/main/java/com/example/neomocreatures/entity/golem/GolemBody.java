package com.example.neomocreatures.entity.golem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The 23 block "cubes" a Big Golem is made of (original: the golemCubes byte array). Server-side it
 * keeps the exact block of every cube plus whether it may be given back to the world; clients only
 * receive the texture index of each cube, through {@link #toSyncTag()}.
 */
public final class GolemBody {

    public static final int SLOT_COUNT = 23;
    /** Original: slot 4 is the precious back cube (see GolemCore). */
    public static final int CORE_SLOT = 4;
    public static final int EMPTY_TEXTURE = -1;

    // Slot layout, as in the original model.
    public static final int CHEST_FIRST = 0;
    public static final int CHEST_LAST = 3;
    public static final int LEFT_SHOULDER = 9;
    public static final int LEFT_ARM = 10;
    public static final int LEFT_HAND = 11;
    public static final int RIGHT_SHOULDER = 12;
    public static final int RIGHT_ARM = 13;
    public static final int RIGHT_HAND = 14;
    public static final int LEFT_THIGH = 15;
    public static final int LEFT_KNEE = 16;
    public static final int LEFT_FOOT = 17;
    public static final int RIGHT_THIGH = 18;
    public static final int RIGHT_KNEE = 19;
    public static final int RIGHT_FOOT = 20;

    private static final String TAG_CUBES = "Cubes";
    private static final String TAG_SLOT = "Slot";
    private static final String TAG_BLOCK = "Block";
    private static final String TAG_RETURNABLE = "Returnable";
    private static final String TAG_SYNC_TEXTURES = "Textures";

    /**
     * One cube of the body.
     *
     * @param returnable true when the block was really taken out of the world (or is the core), so it
     *                   must be given back as an item; false for cheap blocks conjured with mobGriefing off
     */
    public record Cube(BlockState state, boolean returnable) {
    }

    private final Cube[] cubes = new Cube[SLOT_COUNT];

    @Nullable
    public Cube get(int slot) {
        return this.cubes[slot];
    }

    public void set(int slot, @Nullable Cube cube) {
        this.cubes[slot] = cube;
    }

    public boolean isEmpty(int slot) {
        return this.cubes[slot] == null;
    }

    public boolean isComplete() {
        for (Cube cube : this.cubes) {
            if (cube == null) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> emptySlots() {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (this.cubes[slot] == null) {
                slots.add(slot);
            }
        }
        return slots;
    }

    public List<Integer> usedSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (this.cubes[slot] != null) {
                slots.add(slot);
            }
        }
        return slots;
    }

    // ---------------------------------------------------------------------
    // Client sync — texture indices only
    // ---------------------------------------------------------------------

    public CompoundTag toSyncTag() {
        byte[] textures = new byte[SLOT_COUNT];
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            Cube cube = this.cubes[slot];
            textures[slot] = (byte) (cube == null ? EMPTY_TEXTURE : GolemCubeTextures.textureOf(cube.state()));
        }
        CompoundTag tag = new CompoundTag();
        tag.putByteArray(TAG_SYNC_TEXTURES, textures);
        return tag;
    }

    /** Reads the per-slot texture indices out of a sync tag; always returns SLOT_COUNT entries. */
    public static byte[] texturesFromSyncTag(CompoundTag syncTag) {
        byte[] textures = syncTag.getByteArray(TAG_SYNC_TEXTURES);
        if (textures.length == SLOT_COUNT) {
            return textures;
        }
        byte[] empty = new byte[SLOT_COUNT];
        Arrays.fill(empty, (byte) EMPTY_TEXTURE);
        return empty;
    }

    // ---------------------------------------------------------------------
    // Persistence — the exact blocks
    // ---------------------------------------------------------------------

    public void save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            Cube cube = this.cubes[slot];
            if (cube != null) {
                CompoundTag cubeTag = new CompoundTag();
                cubeTag.putByte(TAG_SLOT, (byte) slot);
                cubeTag.put(TAG_BLOCK, NbtUtils.writeBlockState(cube.state()));
                cubeTag.putBoolean(TAG_RETURNABLE, cube.returnable());
                list.add(cubeTag);
            }
        }
        tag.put(TAG_CUBES, list);
    }

    public void load(CompoundTag tag, HolderGetter<Block> blocks) {
        Arrays.fill(this.cubes, null);
        ListTag list = tag.getList(TAG_CUBES, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag cubeTag = list.getCompound(i);
            int slot = cubeTag.getByte(TAG_SLOT);
            BlockState state = NbtUtils.readBlockState(blocks, cubeTag.getCompound(TAG_BLOCK));
            if (slot >= 0 && slot < SLOT_COUNT && !state.isAir()) {
                this.cubes[slot] = new Cube(state, cubeTag.getBoolean(TAG_RETURNABLE));
            }
        }
    }
}