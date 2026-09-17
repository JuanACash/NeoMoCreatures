package com.example.neomocreatures.item;

import com.example.neomocreatures.breeding.MoCHorseGenetics.FairyColor;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCScorpionEntity;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;
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
        } else if (tag.contains("WyvernVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.WYVERN.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("ElephantVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_ELEPHANT.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("BigCatVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_BIG_CAT.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("ManticoreVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_MANTICORE.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("ScorpionVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_SCORPION.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("OstrichVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_OSTRICH.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("BearVariant")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_BEAR.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("KomodoDragon")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_KOMODO_DRAGON.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Fox")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_FOX.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Raccoon")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_RACCOON.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Turkey")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_TURKEY.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Goat")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_GOAT.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Kitty")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_KITTY.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        } else if (tag.contains("Snake")) {
            tooltip.add(com.example.neomocreatures.init.ModEntities.MOC_SNAKE.get().getDescription()
                    .copy().withStyle(net.minecraft.ChatFormatting.GRAY));
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
        Entity spawned;
        if (tag.contains("Species")) {
            spawned = spawnHorse(level, tag, pos);
        } else if (tag.contains("WyvernVariant")) {
            spawned = spawnWyvern(level, tag, pos);
        } else if (tag.contains("ElephantVariant")) {
            spawned = spawnElephant(level, tag, pos);
        } else if (tag.contains("BigCatVariant")) {
            spawned = spawnBigCat(level, tag, pos);
        } else if (tag.contains("ManticoreVariant")) {
            spawned = spawnManticore(level, tag, pos);
        } else if (tag.contains("ScorpionVariant")) {
            spawned = spawnScorpion(level, tag, pos);
        } else if (tag.contains("OstrichVariant")) {
            spawned = spawnOstrich(level, tag, pos);
        } else if (tag.contains("BearVariant")) {
            spawned = spawnBear(level, tag, pos);
        } else if (tag.contains("KomodoDragon")) {
            spawned = spawnKomodo(level, tag, pos);
        } else if (tag.contains("Fox")) {
            spawned = spawnFox(level, tag, pos);
        } else if (tag.contains("Raccoon")) {
            spawned = spawnRaccoon(level, tag, pos);
        } else if (tag.contains("Turkey")) {
            spawned = spawnTurkey(level, tag, pos);
        } else if (tag.contains("Goat")) {
            spawned = spawnGoat(level, tag, pos);
        } else if (tag.contains("Kitty")) {
            spawned = spawnKitty(level, tag, pos);
        } else if (tag.contains("Snake")) {
            spawned = spawnSnake(level, tag, pos);
        } else {
            spawned = spawnGeneric(level, tag, pos);
        }
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

    private Entity spawnWyvern(Level level, CompoundTag tag, BlockPos pos) {
        var tier = com.example.neomocreatures.entity.wyvern.WyvernTier.valueOf(tag.getString("WyvernTier"));
        EntityType<com.example.neomocreatures.entity.MoCWyvernEntity> type = switch (tier) {
            case MOTHER_TAMED -> ModEntities.WYVERN_MOTHER_TAMED.get();
            case TIER_2 -> ModEntities.WYVERN_TIER2.get();
            default -> ModEntities.WYVERN.get();
        };
        com.example.neomocreatures.entity.MoCWyvernEntity wyvern = type.create(level);
        if (wyvern == null) return null;
        wyvern.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        wyvern.setVariant(com.example.neomocreatures.entity.wyvern.WyvernVariant.valueOf(tag.getString("WyvernVariant")));
        wyvern.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            wyvern.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        wyvern.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            wyvern.setAge(tag.getInt("Age"));
        } else {
            wyvern.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            wyvern.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(wyvern);
        return wyvern;
    }

    private Entity spawnElephant(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCElephantEntity elephant =
                ModEntities.MOC_ELEPHANT.get().create(level);
        if (elephant == null) return null;
        elephant.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        elephant.setVariant(com.example.neomocreatures.entity.elephant.ElephantVariant.valueOf(tag.getString("ElephantVariant")));
        elephant.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            elephant.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        elephant.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            elephant.setAge(tag.getInt("Age"));
        } else {
            elephant.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            elephant.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(elephant);
        return elephant;
    }

    private Entity spawnBigCat(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCBigCatEntity bigCat = ModEntities.MOC_BIG_CAT.get().create(level);
        if (bigCat == null) return null;
        bigCat.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        bigCat.setVariant(com.example.neomocreatures.entity.bigcat.BigCatVariant.valueOf(tag.getString("BigCatVariant")));
        bigCat.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            bigCat.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        bigCat.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            bigCat.setAge(tag.getInt("Age"));
        } else {
            bigCat.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.getBoolean("Wings")) {
            bigCat.setWings(true);
        }
        if (tag.getBoolean("Medallion")) {
            bigCat.setMedallion(true);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            bigCat.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(bigCat);
        return bigCat;
    }

    private Entity spawnManticore(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCManticoreEntity manticore = ModEntities.MOC_MANTICORE.get().create(level);
        if (manticore == null) return null;
        manticore.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        manticore.setVariant(com.example.neomocreatures.entity.manticore.ManticoreVariant.valueOf(tag.getString("ManticoreVariant")));
        manticore.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            manticore.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        manticore.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            manticore.setAge(tag.getInt("Age"));
        } else {
            manticore.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            manticore.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(manticore);
        return manticore;
    }

    private Entity spawnScorpion(Level level, CompoundTag tag, BlockPos pos) {
        MoCScorpionEntity scorpion = ModEntities.MOC_SCORPION.get().create(level);
        if (scorpion == null) return null;
        scorpion.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        scorpion.setVariant(ScorpionVariant.valueOf(tag.getString("ScorpionVariant")));
        scorpion.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            scorpion.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        scorpion.setBaby(!tag.getBoolean("Adult"));
        scorpion.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            scorpion.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(scorpion);
        return scorpion;
    }

    private Entity spawnOstrich(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCOstrichEntity ostrich = ModEntities.MOC_OSTRICH.get().create(level);
        if (ostrich == null) return null;
        ostrich.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        ostrich.setVariant(com.example.neomocreatures.entity.ostrich.OstrichVariant.valueOf(tag.getString("OstrichVariant")));
        if (tag.contains("OstrichEssence")) {
            ostrich.setEssence(tag.getInt("OstrichEssence"));
        }
        ostrich.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            ostrich.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        ostrich.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            ostrich.setAge(tag.getInt("Age"));
        } else {
            ostrich.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            ostrich.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(ostrich);
        return ostrich;
    }

    private Entity spawnBear(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCBearEntity bear =
                ModEntities.MOC_BEAR.get().create(level);
        if (bear == null) return null;
        bear.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        bear.setVariant(com.example.neomocreatures.entity.bear.BearVariant.byId(tag.getInt("BearVariant")));
        bear.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            bear.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        bear.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            bear.setAge(tag.getInt("Age"));
        } else {
            bear.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            bear.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(bear);
        return bear;
    }

    private Entity spawnKomodo(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCKomodoDragonEntity komodo =
                ModEntities.MOC_KOMODO_DRAGON.get().create(level);
        if (komodo == null) return null;
        komodo.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        if (tag.contains("IndividualAdultScale")) {
            komodo.setIndividualAdultScale(tag.getFloat("IndividualAdultScale"));
        }
        komodo.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            komodo.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        komodo.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            komodo.setAge(tag.getInt("Age"));
        } else {
            komodo.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            komodo.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(komodo);
        return komodo;
    }

    private Entity spawnFox(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCFoxEntity fox =
                ModEntities.MOC_FOX.get().create(level);
        if (fox == null) return null;
        fox.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        fox.setSnow(tag.getBoolean("Snow"));
        fox.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            fox.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        fox.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            fox.setAge(tag.getInt("Age"));
        } else {
            fox.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            fox.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(fox);
        return fox;
    }

    private Entity spawnRaccoon(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCRaccoonEntity raccoon =
                ModEntities.MOC_RACCOON.get().create(level);
        if (raccoon == null) return null;
        raccoon.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        raccoon.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            raccoon.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        raccoon.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            raccoon.setAge(tag.getInt("Age"));
        } else {
            raccoon.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            raccoon.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(raccoon);
        return raccoon;
    }

    private Entity spawnTurkey(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCTurkeyEntity turkey =
                ModEntities.MOC_TURKEY.get().create(level);
        if (turkey == null) return null;
        turkey.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        turkey.setMale(tag.getBoolean("Male"));
        turkey.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            turkey.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        turkey.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            turkey.setAge(tag.getInt("Age"));
        } else {
            turkey.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            turkey.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(turkey);
        return turkey;
    }

    private Entity spawnGoat(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCGoatEntity goat =
                ModEntities.MOC_GOAT.get().create(level);
        if (goat == null) return null;
        goat.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        goat.setMale(tag.getBoolean("Male"));
        goat.setColorIndex(tag.getInt("Color"));
        goat.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            goat.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        goat.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            goat.setAge(tag.getInt("Age"));
        } else {
            goat.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            goat.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(goat);
        return goat;
    }

    private Entity spawnKitty(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCKittyEntity kitty =
                com.example.neomocreatures.init.ModEntities.MOC_KITTY.get().create(level);
        if (kitty == null) return null;
        kitty.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        kitty.setVariant(com.example.neomocreatures.entity.kitty.KittyVariant.byId(tag.getInt("KittyVariant")));
        kitty.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            kitty.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        kitty.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            kitty.setAge(tag.getInt("Age"));
        } else {
            kitty.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            kitty.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(kitty);
        return kitty;
    }

    private Entity spawnSnake(Level level, CompoundTag tag, BlockPos pos) {
        com.example.neomocreatures.entity.MoCSnakeEntity snake =
                ModEntities.MOC_SNAKE.get().create(level);
        if (snake == null) return null;
        snake.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        snake.setVariant(com.example.neomocreatures.entity.snake.SnakeVariant.byId(tag.getInt("SnakeVariant")));
        snake.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            snake.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        snake.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            snake.setAge(tag.getInt("Age"));
        } else {
            snake.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            snake.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
        level.addFreshEntity(snake);
        return snake;
    }
}