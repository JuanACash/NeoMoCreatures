package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCBearEntity;
import com.example.neomocreatures.entity.bear.BearVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** One egg per species — unlike BigCat, bears have no families/hybrids, so each egg always spawns the same fixed variant. */
public class BearSpawnEggItem extends DeferredSpawnEggItem {

    private final BearVariant variant;

    public BearSpawnEggItem(BearVariant variant, int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_BEAR, backgroundColor, highlightColor, properties);
        this.variant = variant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCBearEntity bear = ModEntities.MOC_BEAR.get().create(serverLevel);
        if (bear == null) {
            return InteractionResult.FAIL;
        }

        bear.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        bear.setVariant(variant);
        serverLevel.addFreshEntity(bear);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand) {
        if (!(target instanceof MoCBearEntity adult) || adult.isBaby() || adult.getVariant() != this.variant) {
            return InteractionResult.PASS;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        MoCBearEntity cub = ModEntities.MOC_BEAR.get().create(serverLevel);
        if (cub != null) {
            cub.moveTo(target.getX(), target.getY(), target.getZ(), 0F, 0F);
            cub.setVariant(this.variant);
            cub.setBaby(true);
            serverLevel.addFreshEntity(cub);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}