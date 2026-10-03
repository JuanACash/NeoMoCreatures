package com.example.neomocreatures.entity.elephant;

import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.item.ItemStack;

/**
 * Tusk tiers an elephant can wear and how hard a block each tier can bulldoze through.
 * Tier ids are synced to the client (the model picks its tusk look from them).
 */
public final class ElephantTusks {

    public static final int NONE = 0;
    public static final int WOOD = 1;
    public static final int IRON = 2;
    public static final int DIAMOND = 3;

    /** Mammoths are stronger: every tier breaks 50% harder blocks. */
    private static final float MAMMOTH_HARDNESS_MULTIPLIER = 1.5F;

    private ElephantTusks() {
        // Utility class, no instances
    }

    public static boolean isTuskItem(ItemStack stack) {
        return stack.is(ModItems.TUSKS_WOOD.get())
                || stack.is(ModItems.TUSKS_IRON.get())
                || stack.is(ModItems.TUSKS_DIAMOND.get());
    }

    /** Tier of a tusk item; only meaningful when {@link #isTuskItem} is true. */
    public static int tierFor(ItemStack stack) {
        if (stack.is(ModItems.TUSKS_DIAMOND.get())) {
            return DIAMOND;
        }
        return stack.is(ModItems.TUSKS_IRON.get()) ? IRON : WOOD;
    }

    /** Hardness ceiling per tier — obsidian/bedrock are always excluded regardless. */
    public static float hardnessCap(int tier, boolean mammoth) {
        float base = switch (tier) {
            case WOOD -> 2.0F;
            case IRON -> 6.0F;
            case DIAMOND -> 30.0F;
            default -> 0.0F;
        };
        return mammoth ? base * MAMMOTH_HARDNESS_MULTIPLIER : base;
    }
}