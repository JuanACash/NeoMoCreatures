package com.example.neomocreatures.item;

import com.example.neomocreatures.init.ModParticles;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.item.storage.StoredPetRegistry;
import com.example.neomocreatures.item.storage.StoredPetType;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * A Pet Amulet holding a tamed pet. Which pets it supports is defined in
 * {@link StoredPetRegistry#AMULET}; each pet restores its own data.
 */
public class FilledAmuletItem extends Item {

    /** Legacy key for pets stored by entity id instead of a dedicated key. */
    private static final String GENERIC_TYPE_KEY = "EntityType";

    private final boolean fairyVariant;
    private final Item emptyVariant;

    public FilledAmuletItem(Properties properties, boolean fairyVariant, Item emptyVariant) {
        super(properties);
        this.fairyVariant = fairyVariant;
        this.emptyVariant = emptyVariant;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        Optional<StoredPetType> petType = StoredPetRegistry.find(StoredPetRegistry.AMULET, tag);
        if (petType.isPresent()) {
            tooltip.add(petType.get().displayName().apply(tag).copy().withStyle(ChatFormatting.GRAY));
        } else if (tag.contains(GENERIC_TYPE_KEY)) {
            tooltip.add(genericTypeOf(tag).getDescription().copy().withStyle(ChatFormatting.GRAY));
        }

        String name = tag.getString("Name");
        if (!name.isEmpty()) {
            tooltip.add(Component.literal(name).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
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
        Optional<StoredPetType> petType = StoredPetRegistry.find(StoredPetRegistry.AMULET, tag);
        Entity spawned = petType.isPresent()
                ? StoredPetRegistry.spawn(level, petType.get(), tag, pos)
                : spawnGeneric(level, tag, pos);
        if (spawned == null) {
            return InteractionResultHolder.fail(stack);
        }

        ((ServerLevel) level).sendParticles(ModParticles.VANISH_FX.get(),
                spawned.getX(), spawned.getY() + 0.5D, spawned.getZ(), 111, 0.4, 0.4, 0.4, 0.02);
        level.playSound(null, spawned.blockPosition(),
                this.fairyVariant ? ModSounds.AMULET_APPEAR_MAGIC.get() : ModSounds.AMULET_APPEAR.get(),
                SoundSource.NEUTRAL, 1.0F, 1.0F);

        ItemStack empty = new ItemStack(this.emptyVariant);
        player.setItemInHand(hand, empty);
        return InteractionResultHolder.consume(empty);
    }

    /** Fallback for any other entity stored only with its type id, health, name and owner. */
    @Nullable
    private Entity spawnGeneric(Level level, CompoundTag tag, BlockPos pos) {
        Entity entity = genericTypeOf(tag).create(level);
        if (entity == null) return null;
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        if (entity instanceof LivingEntity living) {
            living.setHealth(tag.getFloat("Health"));
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            entity.setCustomName(Component.literal(tag.getString("Name")));
        }
        if (entity instanceof TamableAnimal tamable && tag.hasUUID("OwnerUUID")) {
            tamable.setOwnerUUID(tag.getUUID("OwnerUUID"));
            tamable.setTame(true, false);
        }
        level.addFreshEntity(entity);
        return entity;
    }

    private static EntityType<?> genericTypeOf(CompoundTag tag) {
        return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(tag.getString(GENERIC_TYPE_KEY)));
    }
}
