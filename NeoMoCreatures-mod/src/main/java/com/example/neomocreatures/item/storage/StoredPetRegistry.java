package com.example.neomocreatures.item.storage;

import com.example.neomocreatures.breeding.MoCHorseGenetics;
import com.example.neomocreatures.entity.MoCCrabEntity;
import com.example.neomocreatures.entity.MoCDolphinEntity;
import com.example.neomocreatures.entity.MoCFishyEntity;
import com.example.neomocreatures.entity.MoCJellyfishEntity;
import com.example.neomocreatures.entity.MoCMantaRayEntity;
import com.example.neomocreatures.entity.MoCMediumFishEntity;
import com.example.neomocreatures.entity.MoCSmallFishEntity;
import com.example.neomocreatures.entity.MoCStingrayEntity;
import com.example.neomocreatures.entity.MoCTurtleEntity;
import com.example.neomocreatures.entity.StorablePet;
import com.example.neomocreatures.entity.wyvern.WyvernTier;
import com.example.neomocreatures.init.ModEntities;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Every pet that the Pet Amulet and the Fish Net can hold.
 * Adding a new storable pet only needs a new entry here plus {@link StorablePet}
 * on the entity; the items themselves never change.
 * Order matters: the first entry whose key is in the item data wins.
 */
public final class StoredPetRegistry {

    /** Pets stored in a Pet Amulet (and its fairy variant). */
    public static final List<StoredPetType> AMULET = List.of(
            StoredPetType.of("Species", ModEntities.MOC_HORSE)
                    .withDisplayName(tag -> Component.literal(MoCHorseGenetics.displayName(
                            MoCHorseGenetics.Species.valueOf(tag.getString("Species"))))),
            StoredPetType.of("WyvernVariant", StoredPetRegistry::wyvernTypeFor)
                    .withDisplayName(tag -> ModEntities.WYVERN.get().getDescription()),
            StoredPetType.of("ElephantVariant", ModEntities.MOC_ELEPHANT),
            StoredPetType.of("BigCatVariant", ModEntities.MOC_BIG_CAT),
            StoredPetType.of("ManticoreVariant", ModEntities.MOC_MANTICORE),
            StoredPetType.of("ScorpionVariant", ModEntities.MOC_SCORPION),
            StoredPetType.of("OstrichVariant", ModEntities.MOC_OSTRICH),
            StoredPetType.of("BearVariant", ModEntities.MOC_BEAR),
            StoredPetType.of("KomodoDragon", ModEntities.MOC_KOMODO_DRAGON),
            StoredPetType.of("Fox", ModEntities.MOC_FOX),
            StoredPetType.of("Raccoon", ModEntities.MOC_RACCOON),
            StoredPetType.of("Turkey", ModEntities.MOC_TURKEY),
            StoredPetType.of("Goat", ModEntities.MOC_GOAT),
            StoredPetType.of("Kitty", ModEntities.MOC_KITTY),
            StoredPetType.of("Snake", ModEntities.MOC_SNAKE),
            StoredPetType.of("Bunny", ModEntities.MOC_BUNNY),
            StoredPetType.of("Bird", ModEntities.MOC_BIRD),
            StoredPetType.of(MoCTurtleEntity.AMULET_KEY, ModEntities.MOC_TURTLE)
    );

    /** Aquatic (and semi-aquatic) pets stored in a Fish Net. */
    public static final List<StoredPetType> FISH_NET = List.of(
            StoredPetType.of("Shark", ModEntities.MOC_SHARK)
                    .requiringWater(),
            StoredPetType.of(MoCStingrayEntity.NET_KEY, ModEntities.MOC_STINGRAY)
                    .requiringWater().promptingNaming(),
            StoredPetType.of(MoCDolphinEntity.NET_KEY, ModEntities.MOC_DOLPHIN)
                    .requiringWater().promptingNaming(),
            StoredPetType.of(MoCMantaRayEntity.NET_KEY, ModEntities.MOC_MANTA_RAY)
                    .requiringWater().promptingNaming(),
            StoredPetType.of(MoCFishyEntity.NET_KEY, ModEntities.MOC_FISHY)
                    .requiringWater().promptingNaming(),
            // One entry for cod, salmon and bass: the data itself says which type to create
            StoredPetType.of(MoCMediumFishEntity.NET_KEY, StoredPetRegistry::entityTypeFromId)
                    .withDisplayName(tag -> Component.translatable("entity.neomocreatures.moc_cod"))
                    .requiringWater().promptingNaming(),
            StoredPetType.of(MoCSmallFishEntity.NET_KEY, ModEntities.MOC_SMALL_FISH)
                    .withDisplayName(tag -> Component.translatable("entity.neomocreatures.moc_small_fish"))
                    .requiringWater().promptingNaming(),
            StoredPetType.of(MoCJellyfishEntity.NET_KEY, ModEntities.MOC_JELLYFISH)
                    .withDisplayName(tag -> Component.translatable("entity.neomocreatures.moc_jellyfish"))
                    .requiringWater().promptingNaming(),
            // Crabs walk on land too, so they can be released anywhere
            StoredPetType.of(MoCCrabEntity.NET_KEY, ModEntities.MOC_CRAB)
                    .withDisplayName(tag -> Component.translatable("entity.neomocreatures.moc_crab"))
                    .promptingNaming()
    );

    private StoredPetRegistry() {
        // Static registry, no instances
    }

    /** First pet type whose key is present in the stored data. */
    public static Optional<StoredPetType> find(List<StoredPetType> types, CompoundTag tag) {
        return types.stream().filter(type -> type.matches(tag)).findFirst();
    }

    /**
     * Creates the pet, centers it on the block, restores its data and adds it to the world.
     * Returns null if the entity could not be created or cannot be restored from storage.
     */
    @Nullable
    public static Entity spawn(Level level, StoredPetType petType, CompoundTag tag, BlockPos pos) {
        Entity entity = petType.typeResolver().apply(tag).create(level);
        if (!(entity instanceof StorablePet pet)) {
            return null;
        }
        entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        pet.restoreFromStorage(tag);
        level.addFreshEntity(entity);
        return entity;
    }

    private static EntityType<?> wyvernTypeFor(CompoundTag tag) {
        return switch (WyvernTier.valueOf(tag.getString("WyvernTier"))) {
            case MOTHER_TAMED -> ModEntities.WYVERN_MOTHER_TAMED.get();
            case TIER_2 -> ModEntities.WYVERN_TIER2.get();
            default -> ModEntities.WYVERN.get();
        };
    }

    private static EntityType<?> entityTypeFromId(CompoundTag tag) {
        return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(tag.getString("EntityId")));
    }
}
