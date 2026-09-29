package com.example.neomocreatures.entity.ent;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Original: selectType() rolls 1 (oak) or 2 (birch) with equal odds. */
public enum EntVariant {

    OAK(1, "ent_oak", Blocks.OAK_LOG, Blocks.OAK_SAPLING),
    BIRCH(2, "ent_birch", Blocks.BIRCH_LOG, Blocks.BIRCH_SAPLING);

    private final int id;
    private final String textureName;
    private final Block log;
    private final Block sapling;

    EntVariant(int id, String textureName, Block log, Block sapling) {
        this.id = id;
        this.textureName = textureName;
        this.log = log;
        this.sapling = sapling;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public Block getLog() {
        return log;
    }

    public Block getSapling() {
        return sapling;
    }

    public static EntVariant byId(int id) {
        for (EntVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return OAK;
    }

    public static EntVariant random(RandomSource random) {
        EntVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }
}