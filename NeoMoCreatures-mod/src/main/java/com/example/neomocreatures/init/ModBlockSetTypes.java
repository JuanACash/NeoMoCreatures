package com.example.neomocreatures.init;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/** Block set type and wood type shared by every wyvwood door, trapdoor, button and fence gate. */
public final class ModBlockSetTypes {

    public static final BlockSetType WYVWOOD = BlockSetType.register(new BlockSetType("neomocreatures:wyvwood"));
    public static final WoodType WYVWOOD_WOOD = WoodType.register(new WoodType("neomocreatures:wyvwood", WYVWOOD));

    private ModBlockSetTypes() {
    }
}