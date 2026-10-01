package com.example.neomocreatures.item;

import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.worldgen.WyvernPortalPlatform;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Staff that teleports the holder to the fixed arrival point of the Wyvern
 * Lair dimension, remembering the origin dimension and position so a second
 * use returns the player home. Ported from the original Mo' Creatures
 * {@code ItemStaffPortal}.
 * <p>
 * Differences from the 1.16.5 original, due to modern Minecraft APIs:
 * <ul>
 *   <li>Return position is stored in the {@code minecraft:custom_data} data
 *       component instead of raw item NBT (NBT-on-stack was replaced by data
 *       components in 1.20.5+).</li>
 *   <li>Activates on any right-click ({@link #use}) rather than only when
 *       aiming at a block, for more consistent behaviour.</li>
 * </ul>
 */
public class StaffPortalItem extends Item {

    private static final int ARRIVAL_X = 0;
    private static final int ARRIVAL_Z = 0;

    private static final String TAG_RETURN_DIMENSION = "ReturnDimension";
    private static final String TAG_RETURN_X = "ReturnX";
    private static final String TAG_RETURN_Y = "ReturnY";
    private static final String TAG_RETURN_Z = "ReturnZ";

    public StaffPortalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (player.isPassenger() || player.isVehicle()) {
            return InteractionResultHolder.fail(stack);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel serverLevel = (ServerLevel) level;

        if (hasIllegalEnchantments(serverLevel, stack)) {
            removeIllegalStaff(serverPlayer, stack, hand);
            return InteractionResultHolder.success(stack);
        }

        serverPlayer.resetFallDistance();

        if (serverLevel.dimension() == ModDimensions.WYVERN_LAIR) {
            teleportBackHome(serverPlayer, stack);
        } else {
            teleportToWyvernLair(serverPlayer, stack, serverLevel);
        }

        return InteractionResultHolder.success(stack);
    }

    /**
     * Mirrors the original's anti-cheat check: Mending or Unbreaking on this
     * staff would let it bypass its 3-use durability limit, so both are banned.
     */
    private boolean hasIllegalEnchantments(ServerLevel level, ItemStack stack) {
        Registry<Enchantment> enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        int mendingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                enchantments.getHolderOrThrow(Enchantments.MENDING), stack);
        int unbreakingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                enchantments.getHolderOrThrow(Enchantments.UNBREAKING), stack);
        return mendingLevel > 0 || unbreakingLevel > 0;
    }

    private void removeIllegalStaff(ServerPlayer player, ItemStack stack, InteractionHand hand) {
        player.displayClientMessage(
                Component.translatable("item.neomocreatures.staff_portal.illegal_enchant")
                        .withStyle(ChatFormatting.RED),
                false);
        player.setItemInHand(hand, ItemStack.EMPTY);
    }

    private void teleportToWyvernLair(ServerPlayer player, ItemStack stack, ServerLevel currentLevel) {
        MinecraftServer server = player.server;
        ServerLevel destination = server.getLevel(ModDimensions.WYVERN_LAIR);
        if (destination == null) {
            return;
        }

        storeReturnPoint(stack, currentLevel.dimension(), player.blockPosition());
        BlockPos arrivalPos = resolveArrivalPos(destination);
        WyvernPortalPlatform.generateIfMissing(destination, arrivalPos);

        Vec3 targetPos = Vec3.atBottomCenterOf(arrivalPos.above());
        player.changeDimension(new DimensionTransition(destination, targetPos, Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLACE_PORTAL_TICKET));

        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private static BlockPos cachedArrivalPos = null;

    private BlockPos resolveArrivalPos(ServerLevel destination) {
        if (cachedArrivalPos == null) {
            cachedArrivalPos = WyvernPortalPlatform.findSolidGround(destination, ARRIVAL_X, ARRIVAL_Z);
        }
        return cachedArrivalPos;
    }

    private void teleportBackHome(ServerPlayer player, ItemStack stack) {
        ReturnPoint returnPoint = readReturnPoint(stack);
        if (returnPoint == null) {
            return;
        }

        MinecraftServer server = player.server;
        ServerLevel destination = server.getLevel(returnPoint.dimension());
        if (destination == null) {
            destination = server.getLevel(Level.OVERWORLD);
        }
        if (destination == null) {
            return;
        }

        Vec3 targetPos = Vec3.atBottomCenterOf(returnPoint.pos().above());
        player.changeDimension(new DimensionTransition(destination, targetPos, Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLACE_PORTAL_TICKET));

        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    private void storeReturnPoint(ItemStack stack, ResourceKey<Level> dimension, BlockPos pos) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_RETURN_DIMENSION, dimension.location().toString());
            tag.putInt(TAG_RETURN_X, pos.getX());
            tag.putInt(TAG_RETURN_Y, pos.getY());
            tag.putInt(TAG_RETURN_Z, pos.getZ());
        });
    }

    private ReturnPoint readReturnPoint(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_RETURN_DIMENSION)) {
            return null;
        }
        ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString(TAG_RETURN_DIMENSION));
        if (dimensionId == null) {
            return null;
        }
        BlockPos pos = new BlockPos(tag.getInt(TAG_RETURN_X), tag.getInt(TAG_RETURN_Y), tag.getInt(TAG_RETURN_Z));
        return new ReturnPoint(ResourceKey.create(Registries.DIMENSION, dimensionId), pos);
    }

    private record ReturnPoint(ResourceKey<Level> dimension, BlockPos pos) {
    }
}