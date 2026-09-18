package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCSharkEntity;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

/** A fish net holding a caught tamed aquatic pet — same "use anywhere in front of you" pattern as FilledAmuletItem. Extend the if-chain in use() for future sea creatures beyond Shark. */
public class FilledFishNetItem extends Item {

    private final Item emptyNet;

    public FilledFishNetItem(Properties properties, Item emptyNet) {
        super(properties);
        this.emptyNet = emptyNet;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("Shark")) {
            tooltip.add(ModEntities.MOC_SHARK.get().getDescription().copy().withStyle(ChatFormatting.GRAY));
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            tooltip.add(Component.literal(tag.getString("Name"))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }

        BlockPos pos = player.blockPosition().relative(player.getDirection());
        Entity spawned = null;
        if (tag.contains("Shark")) {
            // Wiki: shark eggs only hatch in water — the same restriction
            // makes sense for releasing an adult one back out.
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnShark((ServerLevel) level, tag, pos);
            }
        }
        if (spawned == null) {
            return InteractionResultHolder.fail(stack);
        }

        ItemStack empty = new ItemStack(this.emptyNet);
        player.setItemInHand(hand, empty);
        return InteractionResultHolder.consume(empty);
    }

    private Entity spawnShark(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCSharkEntity shark = ModEntities.MOC_SHARK.get().create(level);
        if (shark == null) return null;
        shark.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        shark.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            shark.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        shark.setHealth((float) tag.getFloat("Health"));
        shark.setBaby(!tag.getBoolean("Adult"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            shark.setCustomName(Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(shark);
        return shark;
    }
}