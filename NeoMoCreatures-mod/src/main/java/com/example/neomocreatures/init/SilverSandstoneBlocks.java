package com.example.neomocreatures.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.MapColor;

/** Slab, stairs and wall made from silver sandstone. */
public final class SilverSandstoneBlocks {

    public static final StoneFamily SILVER_SANDSTONE = StoneFamilyRegistrar.register(
            "silver_sandstone", ModBlocks.SILVER_SANDSTONE, MapColor.SAND, 1.2F, 1.2F);

    private SilverSandstoneBlocks() {
    }

    /** Forces this class to load so its registry entries exist before the registry events fire. */
    public static void init() {
    }

    public static void addToCreativeTab(CreativeModeTab.Output output) {
        SILVER_SANDSTONE.addToCreativeTab(output);
    }
}