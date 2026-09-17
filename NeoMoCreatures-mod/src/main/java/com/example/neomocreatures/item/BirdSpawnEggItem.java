package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCBirdEntity;
import com.example.neomocreatures.entity.bird.BirdVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** A single spawn egg — picks a random colour among the 6, same pattern as BunnySpawnEggItem. */
public class BirdSpawnEggItem extends DeferredSpawnEggItem {

    public BirdSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_BIRD, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCBirdEntity bird = ModEntities.MOC_BIRD.get().create(serverLevel);
        if (bird == null) {
            return InteractionResult.FAIL;
        }

        bird.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        bird.setVariant(BirdVariant.random(serverLevel.random));
        serverLevel.addFreshEntity(bird);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}