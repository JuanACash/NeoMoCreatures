package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.kitty.MoCLitterBoxEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.util.MoCInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LitterBoxItem extends Item {

    public LitterBoxItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCLitterBoxEntity box = ModEntities.MOC_LITTER_BOX.get().create(serverLevel);
        if (box == null) {
            return InteractionResult.FAIL;
        }

        box.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        serverLevel.addFreshEntity(box);

        var player = context.getPlayer();
        if (player != null) {
            MoCInventoryUtil.shrinkUnlessCreative(player, context.getItemInHand());
        }

        return InteractionResult.SUCCESS;
    }
}