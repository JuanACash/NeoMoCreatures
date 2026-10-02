package com.example.neomocreatures.entity.ostrich;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Maps the ostrich's equipment between items and the ids it stores in its synced data:
 * helmets (vanilla and mod helmets) and the wool used to dye its flag.
 */
public final class OstrichEquipment {

    private OstrichEquipment() {
        // Utility class, no instances
    }

    /** Helmet id for a helmet item, or HELMET_NONE if the ostrich cannot wear it. */
    public static int helmetIdFor(ItemStack stack) {
        if (stack.is(Items.LEATHER_HELMET)) return MoCOstrichEntity.HELMET_LEATHER;
        if (stack.is(Items.IRON_HELMET)) return MoCOstrichEntity.HELMET_IRON;
        if (stack.is(Items.GOLDEN_HELMET)) return MoCOstrichEntity.HELMET_GOLD;
        if (stack.is(Items.DIAMOND_HELMET)) return MoCOstrichEntity.HELMET_DIAMOND;
        if (stack.is(ModItems.HIDE_HELMET.get())) return MoCOstrichEntity.HELMET_HIDE;
        if (stack.is(ModItems.FUR_HELMET.get())) return MoCOstrichEntity.HELMET_FUR;
        if (stack.is(ModItems.REPTILE_HELMET.get())) return MoCOstrichEntity.HELMET_REPTILE;
        if (stack.is(ModItems.SCORP_HELMET_DIRT.get())) return MoCOstrichEntity.HELMET_SCORP_DIRT;
        if (stack.is(ModItems.SCORP_HELMET_CAVE.get())) return MoCOstrichEntity.HELMET_SCORP_CAVE;
        if (stack.is(ModItems.SCORP_HELMET_FROST.get())) return MoCOstrichEntity.HELMET_SCORP_FROST;
        if (stack.is(ModItems.SCORP_HELMET_NETHER.get())) return MoCOstrichEntity.HELMET_SCORP_NETHER;
        if (stack.is(ModItems.SCORP_HELMET_UNDEAD.get())) return MoCOstrichEntity.HELMET_SCORP_UNDEAD;
        return MoCOstrichEntity.HELMET_NONE;
    }

    /** Item to drop back for a stored helmet id. */
    public static Item itemForHelmet(int id) {
        return switch (id) {
            case MoCOstrichEntity.HELMET_LEATHER -> Items.LEATHER_HELMET;
            case MoCOstrichEntity.HELMET_IRON -> Items.IRON_HELMET;
            case MoCOstrichEntity.HELMET_GOLD -> Items.GOLDEN_HELMET;
            case MoCOstrichEntity.HELMET_DIAMOND -> Items.DIAMOND_HELMET;
            case MoCOstrichEntity.HELMET_HIDE -> ModItems.HIDE_HELMET.get();
            case MoCOstrichEntity.HELMET_FUR -> ModItems.FUR_HELMET.get();
            case MoCOstrichEntity.HELMET_REPTILE -> ModItems.REPTILE_HELMET.get();
            case MoCOstrichEntity.HELMET_SCORP_DIRT -> ModItems.SCORP_HELMET_DIRT.get();
            case MoCOstrichEntity.HELMET_SCORP_CAVE -> ModItems.SCORP_HELMET_CAVE.get();
            case MoCOstrichEntity.HELMET_SCORP_FROST -> ModItems.SCORP_HELMET_FROST.get();
            case MoCOstrichEntity.HELMET_SCORP_NETHER -> ModItems.SCORP_HELMET_NETHER.get();
            case MoCOstrichEntity.HELMET_SCORP_UNDEAD -> ModItems.SCORP_HELMET_UNDEAD.get();
            default -> Items.LEATHER_HELMET;
        };
    }

    /** Dye color of a wool item, or null if the item is not wool. */
    @Nullable
    public static DyeColor dyeColorForWool(Item item) {
        for (DyeColor color : DyeColor.values()) {
            if (item == woolItemFor(color)) {
                return color;
            }
        }
        return null;
    }

    /** Wool item of a dye color (to drop the flag back). */
    public static Item woolItemFor(DyeColor color) {
        Block block = switch (color) {
            case WHITE -> Blocks.WHITE_WOOL;
            case ORANGE -> Blocks.ORANGE_WOOL;
            case MAGENTA -> Blocks.MAGENTA_WOOL;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL;
            case YELLOW -> Blocks.YELLOW_WOOL;
            case LIME -> Blocks.LIME_WOOL;
            case PINK -> Blocks.PINK_WOOL;
            case GRAY -> Blocks.GRAY_WOOL;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL;
            case CYAN -> Blocks.CYAN_WOOL;
            case PURPLE -> Blocks.PURPLE_WOOL;
            case BLUE -> Blocks.BLUE_WOOL;
            case BROWN -> Blocks.BROWN_WOOL;
            case GREEN -> Blocks.GREEN_WOOL;
            case RED -> Blocks.RED_WOOL;
            case BLACK -> Blocks.BLACK_WOOL;
        };
        return block.asItem();
    }
}