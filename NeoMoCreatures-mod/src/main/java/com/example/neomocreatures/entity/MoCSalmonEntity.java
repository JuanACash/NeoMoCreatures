package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntitySalmon}. */
public class MoCSalmonEntity extends MoCMediumFishEntity {

    public MoCSalmonEntity(EntityType<? extends MoCSalmonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getTextureName() {
        return "mediumfish_salmon";
    }

    @Override
    public Item getRawFishItem() {
        return Items.SALMON;
    }

    @Override
    public Item getEggItem() {
        return ModItems.SALMON_EGG.get();
    }
}