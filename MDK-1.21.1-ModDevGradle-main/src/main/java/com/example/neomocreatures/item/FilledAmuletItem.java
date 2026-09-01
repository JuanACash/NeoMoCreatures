package com.example.neomocreatures.item;

import com.example.neomocreatures.breeding.MoCHorseGenetics.FairyColor;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModParticles;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class FilledAmuletItem extends Item {

    private final boolean fairyVariant;
    private final Item emptyVariant;

    public FilledAmuletItem(Properties properties, boolean fairyVariant, Item emptyVariant) {
        super(properties);
        this.fairyVariant = fairyVariant;
        this.emptyVariant = emptyVariant;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
            java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();

        if (tag.contains("Species")) {
            String display = com.example.neomocreatures.breeding.MoCHorseGenetics.displayName(
                    com.example.neomocreatures.breeding.MoCHorseGenetics.Species.valueOf(tag.getString("Species")));
            tooltip.add(net.minecraft.network.chat.Component.literal(display).withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("EntityType")) {
            ResourceLocation typeId = ResourceLocation.parse(tag.getString("EntityType"));
            net.minecraft.world.entity.EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
            tooltip.add(type.getDescription().copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        }

        String name = tag.getString("Name");
        if (!name.isEmpty()) {
            tooltip.add(net.minecraft.network.chat.Component.literal(name)
                    .withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
        }
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return net.minecraft.world.InteractionResultHolder.success(stack);
        }

        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (tag.isEmpty()) {
            return net.minecraft.world.InteractionResultHolder.fail(stack);
        }
        BlockPos pos = player.blockPosition().relative(player.getDirection());
        Entity spawned = tag.contains("Species") ? spawnHorse(level, tag, pos) : spawnGeneric(level, tag, pos);
        if (spawned == null) {
            return net.minecraft.world.InteractionResultHolder.fail(stack);
        }
        ((ServerLevel) level).sendParticles(ModParticles.VANISH_FX.get(),
                spawned.getX(), spawned.getY() + 0.5D, spawned.getZ(), 111, 0.4, 0.4, 0.4, 0.02);
        level.playSound(null, spawned.blockPosition(),
                fairyVariant ? ModSounds.AMULET_APPEAR_MAGIC.get() : ModSounds.AMULET_APPEAR.get(),
                SoundSource.NEUTRAL, 1.0F, 1.0F);

        ItemStack empty = new ItemStack(this.emptyVariant);
        player.setItemInHand(hand, empty);
        return net.minecraft.world.InteractionResultHolder.consume(empty);
    }

    private Entity spawnHorse(Level level, CompoundTag tag, BlockPos pos) {
        MoCHorseEntity horse = ModEntities.MOC_HORSE.get().create(level);
        if (horse == null) return null;
        horse.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        horse.setSpecies(Species.valueOf(tag.getString("Species")));
        horse.setTamed(true);
        if (tag.hasUUID("OwnerUUID")) {
            horse.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        horse.setHealth((float) tag.getFloat("Health"));
                if (tag.contains("Age")) {
            horse.setAge(tag.getInt("Age"));
        } else {
            horse.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Coat")) {
            horse.setCoat(com.example.neomocreatures.breeding.MoCHorseGenetics.Coat.valueOf(tag.getString("Coat")));
        }
        if (horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) != null && tag.contains("MaxHealth")) {
            horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(tag.getDouble("MaxHealth"));
        }
        if (horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null && tag.contains("MovementSpeed")) {
            horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(tag.getDouble("MovementSpeed"));
        }
        if (horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH) != null && tag.contains("JumpStrength")) {
            horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH).setBaseValue(tag.getDouble("JumpStrength"));
        }
        if (tag.contains("UndeadStage")) {
            horse.setUndeadStagePublic(tag.getInt("UndeadStage"));
            horse.setUndeadLockedPublic(tag.getBoolean("UndeadLocked"));
            horse.setUndeadDecayTicksPublic(tag.getInt("UndeadDecayTicks"));
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            horse.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        if (tag.contains("FairyColor")) {
            horse.setFairyColor(FairyColor.valueOf(tag.getString("FairyColor")));
            horse.setFairyColorLocked(true);
        }
        if (tag.contains("SaddleItem")) {
            Item saddleItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("SaddleItem")));
            horse.setSaddle(new ItemStack(saddleItem));
        }
        if (tag.contains("ArmorItem")) {
            Item armorItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("ArmorItem")));
            horse.setItemSlot(net.minecraft.world.entity.EquipmentSlot.BODY, new ItemStack(armorItem));
        }
        if (tag.getBoolean("HasChest")) {
            horse.setHasChestPublic(true);
            if (tag.contains("ChestItems")) {
                net.minecraft.nbt.ListTag items = tag.getList("ChestItems", net.minecraft.nbt.Tag.TAG_COMPOUND);
                for (int i = 0; i < items.size(); i++) {
                    CompoundTag itemTag = items.getCompound(i);
                    int slot = itemTag.getInt("Slot");
                    ItemStack.parse(level.registryAccess(), itemTag).ifPresent(is -> horse.setChestSlotPublic(slot, is));
                }
            }
        }
        level.addFreshEntity(horse);
        return horse;
    }

    private Entity spawnGeneric(Level level, CompoundTag tag, BlockPos pos) {
        ResourceLocation typeId = ResourceLocation.parse(tag.getString("EntityType"));
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
        Entity entity = type.create(level);
        if (entity == null) return null;
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
            living.setHealth((float) tag.getFloat("Health"));
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            entity.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        if (entity instanceof net.minecraft.world.entity.TamableAnimal tamable && tag.hasUUID("OwnerUUID")) {
            tamable.setOwnerUUID(tag.getUUID("OwnerUUID"));
            tamable.setTame(true, false);
        }
        level.addFreshEntity(entity);
        return entity;
    }
}