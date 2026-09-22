package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.egg.MoCEggEntity;

import net.minecraft.world.item.Item;

/** Wiki: a medium fish egg hatches in water — same restriction as the shark egg, no torch needed. */
public class MediumFishEggItem extends MoCEggItem {

    public MediumFishEggItem(Item.Properties properties, HatchSpec spec) {
        super(properties, spec);
    }

    @Override
    protected void configureEgg(MoCEggEntity egg) {
        egg.setRequiresLight(false);
        egg.setRequiresWater(true);
    }
}