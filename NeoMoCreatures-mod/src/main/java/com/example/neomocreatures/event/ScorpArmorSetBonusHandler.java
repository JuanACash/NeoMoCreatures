package com.example.neomocreatures.event;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Set bonus: a player wearing all 4 pieces of scorpion armour from the SAME
 * biome gets a passive effect for as long as they wear it. It is re-applied
 * every 60 ticks with a 400-tick duration so the effect icon never flickers,
 * similar to how vanilla handles the Turtle Shell / food regeneration.
 */
public class ScorpArmorSetBonusHandler {

    private static final int REFRESH_INTERVAL = 60;
    private static final int EFFECT_DURATION = 400;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % REFRESH_INTERVAL != 0) {
            return;
        }

        // Skip the whole set bonus check when the option is turned off in the config
        if (!Config.GENERAL.armorSetEffects.get()) {
            return;
        }

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (isFullSet(helmet, chest, legs, boots,
                ModItems.SCORP_HELMET_CAVE, ModItems.SCORP_PLATE_CAVE, ModItems.SCORP_LEGS_CAVE, ModItems.SCORP_BOOTS_CAVE)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_DURATION, 0, true, false));
        }

        if (isFullSet(helmet, chest, legs, boots,
                ModItems.SCORP_HELMET_DIRT, ModItems.SCORP_PLATE_DIRT, ModItems.SCORP_LEGS_DIRT, ModItems.SCORP_BOOTS_DIRT)) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 0, true, false));
        }

        if (isFullSet(helmet, chest, legs, boots,
                ModItems.SCORP_HELMET_NETHER, ModItems.SCORP_PLATE_NETHER, ModItems.SCORP_LEGS_NETHER, ModItems.SCORP_BOOTS_NETHER)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, EFFECT_DURATION, 0, true, false));
        }

        if (isFullSet(helmet, chest, legs, boots,
                ModItems.SCORP_HELMET_FROST, ModItems.SCORP_PLATE_FROST, ModItems.SCORP_LEGS_FROST, ModItems.SCORP_BOOTS_FROST)) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, EFFECT_DURATION, 0, true, false));
        }

        if (isFullSet(helmet, chest, legs, boots,
                ModItems.SCORP_HELMET_UNDEAD, ModItems.SCORP_PLATE_UNDEAD, ModItems.SCORP_LEGS_UNDEAD, ModItems.SCORP_BOOTS_UNDEAD)) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, EFFECT_DURATION, 0, true, false));
        }
    }

    private static boolean isFullSet(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots,
            net.neoforged.neoforge.registries.DeferredItem<?> helmetItem, net.neoforged.neoforge.registries.DeferredItem<?> chestItem,
            net.neoforged.neoforge.registries.DeferredItem<?> legsItem, net.neoforged.neoforge.registries.DeferredItem<?> bootsItem) {
        return helmet.is(helmetItem.get()) && chest.is(chestItem.get()) && legs.is(legsItem.get()) && boots.is(bootsItem.get());
    }
}