package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.ostrich.OstrichVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/**
 * One egg, three possible outcomes: an adult male, an adult female, or a
 * chick (1/3 chance each). A chick that hatches carries a "destined" adult
 * skin picked right away — 25% White (albino), the rest split evenly
 * between Male/Female — so its eventual grown-up color is decided at birth,
 * not re-rolled later.
 */
public class OstrichSpawnEggItem extends DeferredSpawnEggItem {

    private static final int GROWTH_TICKS = 48000; // 2 Minecraft days, matches MoCOstrichEntity

    public OstrichSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_OSTRICH, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCOstrichEntity ostrich = ModEntities.MOC_OSTRICH.get().create(serverLevel);
        if (ostrich == null) {
            return InteractionResult.FAIL;
        }

        ostrich.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);

        int roll = serverLevel.random.nextInt(4);
        if (roll == 0) {
            ostrich.setVariant(OstrichVariant.MALE);
        } else if (roll == 1) {
            ostrich.setVariant(OstrichVariant.FEMALE);
        } else if (roll == 2) {
            ostrich.setVariant(OstrichVariant.WHITE);
        } else {
            // Chick — 25% destined White, otherwise split evenly Male/Female.
            OstrichVariant destined = serverLevel.random.nextFloat() < 0.25F
                    ? OstrichVariant.WHITE
                    : (serverLevel.random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE);
    ostrich.setVariant(destined);
    ostrich.setBaby(true);
    ostrich.setAge(-GROWTH_TICKS);
}

        serverLevel.addFreshEntity(ostrich);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}