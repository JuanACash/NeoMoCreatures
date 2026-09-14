package com.example.neomocreatures.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Food that, when eaten, returns an empty bowl — same behavior as vanilla's Mushroom Stew. */
public class BowlFoodItem extends Item {

    public BowlFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
            ItemStack bowl = new ItemStack(Items.BOWL);
            if (result.isEmpty()) {
                return bowl;
            }
            if (!player.getInventory().add(bowl)) {
                player.drop(bowl, false);
            }
        }
        return result;
    }
}