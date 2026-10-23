package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCKittyBedEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.util.MoCInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class KittyBedItem extends Item {

    private final DyeColor color;

    public KittyBedItem(DyeColor color, Item.Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCKittyBedEntity bed = ModEntities.MOC_KITTY_BED.get().create(serverLevel);
        if (bed == null) {
            return InteractionResult.FAIL;
        }

        bed.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        bed.setSheetColor(this.color.getId());
        serverLevel.addFreshEntity(bed);

        var player = context.getPlayer();
        if (player != null) {
            MoCInventoryUtil.shrinkUnlessCreative(player, context.getItemInHand());
        }

        return InteractionResult.SUCCESS;
    }
}