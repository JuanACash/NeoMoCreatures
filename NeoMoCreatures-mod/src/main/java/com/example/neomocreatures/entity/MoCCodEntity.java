package com.example.neomocreatures.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityCod}. */
public class MoCCodEntity extends MoCMediumFishEntity {

    public MoCCodEntity(EntityType<? extends MoCCodEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getTextureName() {
        return "mediumfish_cod";
    }

    @Override
    public net.minecraft.world.item.Item getRawFishItem() {
        return net.minecraft.world.item.Items.COD;
    }

    @Override
    public net.minecraft.world.item.Item getEggItem() {
        return com.example.neomocreatures.init.ModItems.COD_EGG.get();
    }
}