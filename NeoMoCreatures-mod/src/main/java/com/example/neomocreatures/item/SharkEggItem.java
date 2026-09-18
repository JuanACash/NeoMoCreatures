package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.egg.MoCEggEntity;

import net.minecraft.world.item.Item;

/** Wiki: a shark egg "can only hatch if it is in the water" — no torch needed, unlike every other egg. */
public class SharkEggItem extends MoCEggItem {

    public SharkEggItem(Item.Properties properties, HatchSpec spec) {
        super(properties, spec);
    }

    @Override
    protected void configureEgg(MoCEggEntity egg) {
        egg.setRequiresLight(false);
        egg.setRequiresWater(true);
    }
}