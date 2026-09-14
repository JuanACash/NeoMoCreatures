package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** A single spawn egg — picks a random color among the 5 (including Undead) each time it's used. */
public class ScorpionSpawnEggItem extends DeferredSpawnEggItem {

    public ScorpionSpawnEggItem(int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_SCORPION, backgroundColor, highlightColor, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCScorpionEntity scorpion = ModEntities.MOC_SCORPION.get().create(serverLevel);
        if (scorpion == null) {
            return InteractionResult.FAIL;
        }

        scorpion.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        ScorpionVariant[] variants = ScorpionVariant.values();
        ScorpionVariant variant = variants[serverLevel.random.nextInt(variants.length)];
        scorpion.setVariant(variant);
        if (variant != ScorpionVariant.UNDEAD && serverLevel.random.nextInt(4) == 0) {
            scorpion.setHasBabiesPublic(true);
        }
        serverLevel.addFreshEntity(scorpion);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}