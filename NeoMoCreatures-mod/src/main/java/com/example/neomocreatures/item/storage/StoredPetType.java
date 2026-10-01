package com.example.neomocreatures.item.storage;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

/**
 * Describes one kind of pet a storage item can hold.
 *
 * @param tagKey        key whose presence in the item data identifies this pet
 * @param typeResolver  entity type to create, read from the stored data when it varies
 * @param displayName   tooltip line shown on the filled item
 * @param needsWater    the pet can only be released into water
 * @param promptsNaming an unnamed pet asks its owner for a name when released
 */
public record StoredPetType(String tagKey,
                            Function<CompoundTag, EntityType<?>> typeResolver,
                            Function<CompoundTag, Component> displayName,
                            boolean needsWater,
                            boolean promptsNaming) {

    /** A pet with a fixed entity type, named after that type. */
    public static StoredPetType of(String tagKey, Supplier<? extends EntityType<?>> type) {
        return of(tagKey, tag -> type.get());
    }

    /** A pet whose entity type depends on the stored data, named after that type. */
    public static StoredPetType of(String tagKey, Function<CompoundTag, EntityType<?>> typeResolver) {
        return new StoredPetType(tagKey, typeResolver, tag -> typeResolver.apply(tag).getDescription(), false, false);
    }

    public StoredPetType withDisplayName(Function<CompoundTag, Component> name) {
        return new StoredPetType(this.tagKey, this.typeResolver, name, this.needsWater, this.promptsNaming);
    }

    public StoredPetType requiringWater() {
        return new StoredPetType(this.tagKey, this.typeResolver, this.displayName, true, this.promptsNaming);
    }

    public StoredPetType promptingNaming() {
        return new StoredPetType(this.tagKey, this.typeResolver, this.displayName, this.needsWater, true);
    }

    public boolean matches(CompoundTag tag) {
        return tag.contains(this.tagKey);
    }
}
