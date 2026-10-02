package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityBass}. */
public class MoCBassEntity extends MoCMediumFishEntity {

    public MoCBassEntity(EntityType<? extends MoCBassEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getTextureName() {
        return "mediumfish_bass";
    }

    @Override
    public Item getRawFishItem() {
        // Bass has no vanilla raw-fish item; using a tropical fish, same call made for the Fishy.
        return Items.TROPICAL_FISH;
    }

    @Override
    public Item getEggItem() {
        return ModItems.BASS_EGG.get();
    }
}