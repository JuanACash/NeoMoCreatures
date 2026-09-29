package com.example.neomocreatures.entity.golem;

import java.util.Optional;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Port of {@code MoCTools.destroyRandomBlockWithIBlockState}'s search, shared by every golem. Picks a
 * random block around the golem that can be safely lifted: nothing with an inventory or block entity,
 * nothing unbreakable, no fluids, no two-part blocks (doors, beds, tall plants, extended pistons),
 * nothing without an item form, nothing in the {@code neomocreatures:golem_cannot_lift} tag, and never
 * the block it stands on.
 */
public final class GolemBlockPicker {

    private GolemBlockPicker() {
    }

    /** Tries {@code attempts} random spots in a cube of {@code radius} around the golem; empty when none fits. */
    public static Optional<BlockPos> findLiftableBlock(Mob golem, int radius, int attempts) {
        Level level = golem.level();
        RandomSource random = golem.getRandom();
        BlockPos origin = golem.blockPosition();
        BlockPos underFeet = origin.below();
        int span = radius * 2 + 1;

        for (int i = 0; i < attempts; i++) {
            BlockPos pos = origin.offset(random.nextInt(span) - radius, random.nextInt(span) - radius, random.nextInt(span) - radius);
            if (!pos.equals(underFeet) && isLiftable(level, pos)) {
                return Optional.of(pos);
            }
        }
        return Optional.empty();
    }

    public static boolean isLiftable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) {
            return false;
        }
        if (state.getDestroySpeed(level, pos) < 0.0F || state.is(ModTags.GOLEM_CANNOT_LIFT)) {
            return false;
        }
        if (isMultiPartBlock(state) || state.getBlock().asItem() == Items.AIR) {
            return false;
        }
        // Original: only blocks with open air above them, so it never tears out something holding up another block.
        return level.getBlockState(pos.above()).isAir();
    }

    private static boolean isMultiPartBlock(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                || state.hasProperty(BlockStateProperties.BED_PART)
                || (state.hasProperty(BlockStateProperties.EXTENDED) && state.getValue(BlockStateProperties.EXTENDED));
    }
}