package com.example.neomocreatures.entity.deer;

import com.example.neomocreatures.init.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Tracks how long a deer stays calm, and who it trusts while calm or while being offered hay. */
final class DeerCalm {

    private static final String TAG_CALM_TICKS = "CalmTicks";

    private int ticksLeft;

    boolean isActive() {
        return this.ticksLeft > 0;
    }

    void start(int ticks) {
        this.ticksLeft = Math.max(0, ticks);
    }

    void clear() {
        this.ticksLeft = 0;
    }

    /** Counts one tick down. Returns true on the tick the calm runs out. */
    boolean tick() {
        if (this.ticksLeft <= 0) {
            return false;
        }
        this.ticksLeft--;
        return this.ticksLeft == 0;
    }

    void save(CompoundTag tag) {
        tag.putInt(TAG_CALM_TICKS, this.ticksLeft);
    }

    void load(CompoundTag tag) {
        this.ticksLeft = tag.getInt(TAG_CALM_TICKS);
    }

    /** A sneaking player holding hay is no threat to a deer, calm or not. */
    static boolean isOfferingHay(Player player) {
        return player.isShiftKeyDown() && player.isHolding(ModItems.HAYSTACK.get());
    }

    /** True when the deer should not run from this entity. */
    boolean trusts(LivingEntity other) {
        return other instanceof Player player && (this.isActive() || isOfferingHay(player));
    }
}