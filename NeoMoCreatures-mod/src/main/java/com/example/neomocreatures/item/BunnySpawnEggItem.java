package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCBunnyEntity;
import com.example.neomocreatures.entity.bunny.BunnyVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** A single spawn egg — picks a random colour among the 5, same pattern as SnakeSpawnEggItem. */
public class BunnySpawnEggItem extends DeferredSpawnEggItem {

    public BunnySpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_BUNNY, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCBunnyEntity bunny = ModEntities.MOC_BUNNY.get().create(serverLevel);
        if (bunny == null) {
            return InteractionResult.FAIL;
        }

        bunny.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        bunny.setVariant(BunnyVariant.random(serverLevel.random));
        serverLevel.addFreshEntity(bunny);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}