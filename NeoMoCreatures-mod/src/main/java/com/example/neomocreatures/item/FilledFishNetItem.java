package com.example.neomocreatures.item;

import java.util.List;

import com.example.neomocreatures.entity.MoCCrabEntity;
import com.example.neomocreatures.entity.MoCDolphinEntity;
import com.example.neomocreatures.entity.MoCFishyEntity;
import com.example.neomocreatures.entity.MoCJellyfishEntity;
import com.example.neomocreatures.entity.MoCMantaRayEntity;
import com.example.neomocreatures.entity.MoCMediumFishEntity;
import com.example.neomocreatures.entity.MoCSharkEntity;
import com.example.neomocreatures.entity.MoCSmallFishEntity;
import com.example.neomocreatures.entity.MoCStingrayEntity;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.util.NamingHelper;

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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

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
        } if (tag.contains(MoCStingrayEntity.NET_KEY)) {
            tooltip.add(ModEntities.MOC_STINGRAY.get().getDescription().copy().withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCDolphinEntity.NET_KEY)) {
            tooltip.add(ModEntities.MOC_DOLPHIN.get().getDescription().copy().withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCMantaRayEntity.NET_KEY)) {
            tooltip.add(ModEntities.MOC_MANTA_RAY.get().getDescription().copy().withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCFishyEntity.NET_KEY)) {
            tooltip.add(ModEntities.MOC_FISHY.get().getDescription().copy().withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCMediumFishEntity.NET_KEY)) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("entity.neomocreatures.moc_cod")
                    .withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCSmallFishEntity.NET_KEY)) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("entity.neomocreatures.moc_small_fish")
                    .withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCJellyfishEntity.NET_KEY)) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("entity.neomocreatures.moc_jellyfish")
                    .withStyle(ChatFormatting.GRAY));
        } if (tag.contains(MoCCrabEntity.NET_KEY)) {
            tooltip.add(net.minecraft.network.chat.Component.translatable("entity.neomocreatures.moc_crab")
                    .withStyle(ChatFormatting.GRAY));
        } if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
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

        // Same fluid-only ray trace vanilla buckets use, so releasing works while looking at water
        // from the shore or the surface, not only when standing inside the water block itself.
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos pos = hit.getType() == HitResult.Type.BLOCK
                ? hit.getBlockPos()
                : player.blockPosition().relative(player.getDirection());
        Entity spawned = null;
        if (tag.contains("Shark")) {
            // Wiki: shark eggs only hatch in water — the same restriction
            // makes sense for releasing an adult one back out.
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnShark((ServerLevel) level, tag, pos);
            }
        } if (tag.contains(MoCStingrayEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnStingray((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCDolphinEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnDolphin((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCMantaRayEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnMantaRay((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCFishyEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnFishy((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCMediumFishEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnMediumFish((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCSmallFishEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnSmallFish((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCJellyfishEntity.NET_KEY)) {
            if (level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                spawned = spawnJellyfish((ServerLevel) level, tag, pos);
                promptNamingIfUnnamed(spawned, tag, player);
            }
        } if (tag.contains(MoCCrabEntity.NET_KEY)) {
            spawned = spawnCrab((ServerLevel) level, tag, pos);
            promptNamingIfUnnamed(spawned, tag, player);
        } if (spawned == null) {
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

    private Entity spawnStingray(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCStingrayEntity ray = ModEntities.MOC_STINGRAY.get().create(level);
        if (ray == null) return null;
        ray.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        ray.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            ray.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        ray.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            ray.setCustomName(Component.literal(tag.getString("Name")));
            ray.setCustomNameVisible(true);
        }
        level.addFreshEntity(ray);
        return ray;
    }

    private Entity spawnDolphin(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCDolphinEntity dolphin = ModEntities.MOC_DOLPHIN.get().create(level);
        if (dolphin == null) return null;
        dolphin.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        dolphin.restoreFromNet(tag);
        level.addFreshEntity(dolphin);
        return dolphin;
    }

    private Entity spawnMantaRay(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCMantaRayEntity ray = ModEntities.MOC_MANTA_RAY.get().create(level);
        if (ray == null) return null;
        ray.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        ray.restoreFromNet(tag);
        level.addFreshEntity(ray);
        return ray;
    }

    private Entity spawnFishy(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCFishyEntity fishy = ModEntities.MOC_FISHY.get().create(level);
        if (fishy == null) return null;
        fishy.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        fishy.restoreFromNet(tag);
        level.addFreshEntity(fishy);
        return fishy;
    }

    /** One release path for all 3 species: the tag itself says which EntityType to spawn. */
    private Entity spawnMediumFish(ServerLevel level, CompoundTag tag, BlockPos pos) {
        net.minecraft.resources.ResourceLocation id = net.minecraft.resources.ResourceLocation.parse(tag.getString("EntityId"));
        net.minecraft.world.entity.EntityType<?> type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(id);
        net.minecraft.world.entity.Entity entity = type.create(level);
        if (!(entity instanceof MoCMediumFishEntity fish)) return null;
        fish.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        fish.restoreFromNet(tag);
        level.addFreshEntity(fish);
        return fish;
    }

    private Entity spawnSmallFish(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCSmallFishEntity fish = ModEntities.MOC_SMALL_FISH.get().create(level);
        if (fish == null) return null;
        fish.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        fish.restoreFromNet(tag);
        level.addFreshEntity(fish);
        return fish;
    }

    private Entity spawnJellyfish(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCJellyfishEntity jellyfish = ModEntities.MOC_JELLYFISH.get().create(level);
        if (jellyfish == null) return null;
        jellyfish.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        jellyfish.restoreFromNet(tag);
        level.addFreshEntity(jellyfish);
        return jellyfish;
    }

    private Entity spawnCrab(ServerLevel level, CompoundTag tag, BlockPos pos) {
        MoCCrabEntity crab = ModEntities.MOC_CRAB.get().create(level);
        if (crab == null) return null;
        crab.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        crab.restoreFromNet(tag);
        level.addFreshEntity(crab);
        return crab;
    }

    /** A freshly released pet without a name asks its owner to name it (original: tameWithName on release). */
    private void promptNamingIfUnnamed(Entity pet, CompoundTag tag, Player releaser) {
        boolean unnamed = !tag.contains("Name") || tag.getString("Name").isEmpty();
        boolean releaserIsOwner = !tag.hasUUID("OwnerUUID") || tag.getUUID("OwnerUUID").equals(releaser.getUUID());
        if (pet != null && unnamed && releaserIsOwner) {
            NamingHelper.promptRename(pet, releaser.getUUID());
        }
    }
}