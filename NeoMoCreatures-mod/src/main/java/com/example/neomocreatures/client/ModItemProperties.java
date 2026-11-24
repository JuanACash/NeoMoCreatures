package com.example.neomocreatures.client;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.item.AncientSilverBowItem;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** Client-side item model predicates. */
public final class ModItemProperties {

    private static final ResourceLocation PULL = ResourceLocation.withDefaultNamespace("pull");
    private static final ResourceLocation PULLING = ResourceLocation.withDefaultNamespace("pulling");
    private static final float VANILLA_FULL_DRAW_TICKS = 20.0F;

    private ModItemProperties() {
    }

    public static void register() {
        registerBow(ModItems.ANCIENT_SILVER_BOW.get(), AncientSilverBowItem.DRAW_TIME_MULTIPLIER);
    }

    private static void registerBow(Item bow, float drawTimeMultiplier) {
        float fullDrawTicks = VANILLA_FULL_DRAW_TICKS * drawTimeMultiplier;
        ItemProperties.register(bow, PULL, (stack, level, entity, seed) -> {
            if (entity == null || entity.getUseItem() != stack) {
                return 0.0F;
            }
            return (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / fullDrawTicks;
        });
        ItemProperties.register(bow, PULLING, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}