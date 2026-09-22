package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCSmallFishEntity;
import com.example.neomocreatures.entity.smallfish.SmallFishVariant;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/** Unlike BirdSpawnEggItem/SnakeSpawnEggItem, this always spawns one fixed variant — the wiki and
 *  the original both give each small fish species its own dedicated spawn egg. */
public class SmallFishSpawnEggItem extends DeferredSpawnEggItem {

    private final SmallFishVariant variant;

    public SmallFishSpawnEggItem(SmallFishVariant variant, int backgroundColor, int highlightColor, Item.Properties properties) {
        super(ModEntities.MOC_SMALL_FISH, backgroundColor, highlightColor, properties);
        this.variant = variant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCSmallFishEntity fish = ModEntities.MOC_SMALL_FISH.get().create(serverLevel);
        if (fish == null) {
            return InteractionResult.FAIL;
        }

        fish.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                serverLevel.random.nextFloat() * 360F, 0F);
        fish.setVariant(this.variant);
        serverLevel.addFreshEntity(fish);

        var player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}