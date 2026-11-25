package com.example.neomocreatures.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Leaves that bear apples. Age 0 is bare, 1 has blossoms, 2 has green apples and 3 has ripe apples.
 * Ripe apples are picked with a right click, which sends the leaves back to age 0.
 */
public class AppleFruitLeavesBlock extends LeavesBlock implements BonemealableBlock {

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    private static final int MIN_LIGHT_TO_GROW = 9;
    /** One in this many random ticks moves the fruit to its next stage. */
    private static final int GROWTH_ROLL = 2;
    private static final float PICK_SOUND_VOLUME = 1.0F;
    private static final float PICK_SOUND_BASE_PITCH = 0.8F;
    private static final float PICK_SOUND_PITCH_RANGE = 0.4F;

    public AppleFruitLeavesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE || super.isRandomlyTicking(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        // The leaves may have just decayed, so read the current state again
        BlockState current = level.getBlockState(pos);
        if (current.is(this) && canGrow(level, pos, current) && random.nextInt(GROWTH_ROLL) == 0) {
            setAge(level, pos, current, current.getValue(AGE) + 1, null);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (state.getValue(AGE) < MAX_AGE) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(Items.APPLE));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    PICK_SOUND_VOLUME, PICK_SOUND_BASE_PITCH + level.random.nextFloat() * PICK_SOUND_PITCH_RANGE);
            setAge(level, pos, state, 0, player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        setAge(level, pos, state, Math.min(MAX_AGE, state.getValue(AGE) + 1), null);
    }

    private static boolean canGrow(ServerLevel level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE && level.getRawBrightness(pos.above(), 0) >= MIN_LIGHT_TO_GROW;
    }

    private static void setAge(Level level, BlockPos pos, BlockState state, int age, @Nullable Entity actor) {
        BlockState updated = state.setValue(AGE, age);
        level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(actor, updated));
    }
}