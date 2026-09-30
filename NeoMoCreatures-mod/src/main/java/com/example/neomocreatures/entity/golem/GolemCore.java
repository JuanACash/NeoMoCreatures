package com.example.neomocreatures.entity.golem;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Original: initGolemCubes() — every Big Golem is born with one precious block in its back (slot 4),
 * picked at random. It is never knocked off in combat and is given back when the golem dies.
 */
public enum GolemCore {

    GOLD(Blocks.GOLD_BLOCK),
    IRON(Blocks.IRON_BLOCK),
    DIAMOND(Blocks.DIAMOND_BLOCK),
    EMERALD(Blocks.EMERALD_BLOCK);

    private final Block block;

    GolemCore(Block block) {
        this.block = block;
    }

    public GolemBody.Cube toCube() {
        return new GolemBody.Cube(this.block.defaultBlockState(), true);
    }

    public static GolemCore random(RandomSource random) {
        GolemCore[] cores = values();
        return cores[random.nextInt(cores.length)];
    }
}