package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.egg.MoCEggEntity;

import net.minecraft.world.item.Item;

/** Wiki: a single generic egg, not one per species — hatching it rolls a random passive colour
 *  (never a piranha, same as the wild spawn roll). Requires water, like the other aquatic eggs. */
public class SmallFishEggItem extends MoCEggItem {

    public SmallFishEggItem(Item.Properties properties, HatchSpec spec) {
        super(properties, spec);
    }

    @Override
    protected void configureEgg(MoCEggEntity egg) {
        egg.setRequiresLight(false);
        egg.setRequiresWater(true);
    }
}