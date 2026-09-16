package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCSnakeEntity;
import com.example.neomocreatures.entity.snake.SnakeVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** A single spawn egg — picks a random colour among the 8, same pattern as ScorpionSpawnEggItem. */
public class SnakeSpawnEggItem extends DeferredSpawnEggItem {

    public SnakeSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_SNAKE, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCSnakeEntity snake = ModEntities.MOC_SNAKE.get().create(serverLevel);
        if (snake == null) {
            return InteractionResult.FAIL;
        }

        snake.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        snake.setVariant(SnakeVariant.random(serverLevel.random));
        serverLevel.addFreshEntity(snake);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}