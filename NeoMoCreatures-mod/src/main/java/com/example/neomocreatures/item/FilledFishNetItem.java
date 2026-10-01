package com.example.neomocreatures.item;

import com.example.neomocreatures.item.storage.StoredPetRegistry;
import com.example.neomocreatures.item.storage.StoredPetType;
import com.example.neomocreatures.util.NamingHelper;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * A fish net holding a caught tamed aquatic pet. Which pets it supports is defined in
 * {@link StoredPetRegistry#FISH_NET}; each pet restores its own data.
 */
public class FilledFishNetItem extends Item {

    private final Item emptyNet;

    public FilledFishNetItem(Properties properties, Item emptyNet) {
        super(properties);
        this.emptyNet = emptyNet;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        StoredPetRegistry.find(StoredPetRegistry.FISH_NET, tag).ifPresent(petType ->
                tooltip.add(petType.displayName().apply(tag).copy().withStyle(ChatFormatting.GRAY)));
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
        Optional<StoredPetType> found = StoredPetRegistry.find(StoredPetRegistry.FISH_NET, tag);
        if (found.isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }
        StoredPetType petType = found.get();

        // Same fluid-only ray trace vanilla buckets use, so releasing works while looking at water
        // from the shore or the surface, not only when standing inside the water block itself.
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos pos = hit.getType() == HitResult.Type.BLOCK
                ? hit.getBlockPos()
                : player.blockPosition().relative(player.getDirection());
        if (petType.needsWater() && !level.getFluidState(pos).is(FluidTags.WATER)) {
            return InteractionResultHolder.fail(stack);
        }

        Entity spawned = StoredPetRegistry.spawn(level, petType, tag, pos);
        if (spawned == null) {
            return InteractionResultHolder.fail(stack);
        }
        if (petType.promptsNaming()) {
            promptNamingIfUnnamed(spawned, tag, player);
        }

        ItemStack empty = new ItemStack(this.emptyNet);
        player.setItemInHand(hand, empty);
        return InteractionResultHolder.consume(empty);
    }

    /** A freshly released pet without a name asks its owner to name it (original: tameWithName on release). */
    private void promptNamingIfUnnamed(Entity pet, CompoundTag tag, Player releaser) {
        boolean unnamed = !tag.contains("Name") || tag.getString("Name").isEmpty();
        boolean releaserIsOwner = !tag.hasUUID("OwnerUUID") || tag.getUUID("OwnerUUID").equals(releaser.getUUID());
        if (unnamed && releaserIsOwner) {
            NamingHelper.promptRename(pet, releaser.getUUID());
        }
    }
}
