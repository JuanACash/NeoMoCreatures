package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
    public Item getRawFishItem() {
        return Items.COD;
    }

    @Override
    public Item getEggItem() {
        return ModItems.COD_EGG.get();
    }
}