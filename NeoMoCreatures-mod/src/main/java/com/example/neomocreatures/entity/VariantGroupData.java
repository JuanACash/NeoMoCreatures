package com.example.neomocreatures.entity;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.SpawnGroupData;

/**
 * Carries the variant chosen for the first member of a natural spawn group to the rest of the
 * group, so a herd/school/pack never ends up mixed. Extends AgeableMobGroupData (never a baby by
 * itself) because AgeableMob.finalizeSpawn() casts whatever group data it receives to that type.
 *
 * @param <V> the variant type (enum, family, or a simple flag)
 */
public final class VariantGroupData<V> extends AgeableMob.AgeableMobGroupData {

    private final V variant;

    public VariantGroupData(V variant) {
        super(false);
        this.variant = variant;
    }

    public V variant() {
        return this.variant;
    }

    /**
     * The group data already shared by this spawn group, or a new one for its first member.
     * The picker only runs for the first member, so it may use randomness freely.
     */
    @SuppressWarnings("unchecked")
    public static <V> VariantGroupData<V> of(@Nullable SpawnGroupData existing, Class<V> variantType, Supplier<V> picker) {
        if (existing instanceof VariantGroupData<?> group && variantType.isInstance(group.variant)) {
            return (VariantGroupData<V>) group;
        }
        return new VariantGroupData<>(picker.get());
    }
}
