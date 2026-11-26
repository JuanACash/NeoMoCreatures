package com.example.neomocreatures.item;

import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.worldgen.OgreLairArrivalLocator;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Portal staff that travels to the Ogre Lair; everything else is inherited from {@link StaffPortalItem}. */
public class StaffOgreLairItem extends StaffPortalItem {

    public StaffOgreLairItem(Properties properties) {
        super(properties);
    }

    @Override
    protected ResourceKey<Level> getDestinationDimension() {
        return ModDimensions.OGRE_LAIR;
    }

    @Override
    protected BlockPos prepareArrival(ServerLevel destination) {
        return OgreLairArrivalLocator.resolve(destination);
    }
}