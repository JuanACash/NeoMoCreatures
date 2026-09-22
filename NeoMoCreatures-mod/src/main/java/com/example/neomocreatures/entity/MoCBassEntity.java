package com.example.neomocreatures.entity;

import net.minecraft.world.entity.EntityType;
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
    public net.minecraft.world.item.Item getRawFishItem() {
        // Bass has no vanilla raw-fish item; using a tropical fish, same call made for the Fishy.
        return net.minecraft.world.item.Items.TROPICAL_FISH;
    }

    @Override
    public net.minecraft.world.item.Item getEggItem() {
        return com.example.neomocreatures.init.ModItems.BASS_EGG.get();
    }
}