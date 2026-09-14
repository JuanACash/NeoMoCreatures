package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCFoxEntity;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/**
 * Single egg for both fox colorations. Placing it in the world (inherited
 * default behaviour, not overridden here) already spawns a random-variant
 * adult, since MoCFoxEntity#finalizeSpawn rolls isSnow() for
 * MobSpawnType.SPAWN_EGG. The only thing added here is: right-clicking an
 * adult fox with this egg spawns a cub matching THAT adult's own variant —
 * unlike BearSpawnEggItem, there's no per-egg variant to match against.
 */
public class FoxSpawnEggItem extends DeferredSpawnEggItem {

    public FoxSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_FOX, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof MoCFoxEntity adult) || adult.isBaby()) {
            return InteractionResult.PASS;
        }
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        MoCFoxEntity cub = ModEntities.MOC_FOX.get().create(serverLevel);
        if (cub != null) {
            cub.moveTo(target.getX(), target.getY(), target.getZ(), 0F, 0F);
            cub.setSnow(adult.isSnow());
            cub.setBaby(true);
            serverLevel.addFreshEntity(cub);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}