package com.example.neomocreatures.entity;

import net.minecraft.world.entity.EntityType;
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
    public net.minecraft.world.item.Item getRawFishItem() {
        return net.minecraft.world.item.Items.SALMON;
    }

    @Override
    public net.minecraft.world.item.Item getEggItem() {
        return com.example.neomocreatures.init.ModItems.SALMON_EGG.get();
    }
}