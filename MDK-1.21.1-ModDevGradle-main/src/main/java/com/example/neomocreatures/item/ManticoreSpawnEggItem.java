package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCManticoreEntity;
import com.example.neomocreatures.entity.manticore.ManticoreVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** A single spawn egg, per the wiki — picks a random color among the 5 each time it's used. */
public class ManticoreSpawnEggItem extends DeferredSpawnEggItem {

    public ManticoreSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_MANTICORE, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCManticoreEntity manticore = ModEntities.MOC_MANTICORE.get().create(serverLevel);
        if (manticore == null) {
            return InteractionResult.FAIL;
        }

        manticore.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        ManticoreVariant[] variants = ManticoreVariant.values();
        manticore.setVariant(variants[serverLevel.random.nextInt(variants.length)]);
        serverLevel.addFreshEntity(manticore);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}