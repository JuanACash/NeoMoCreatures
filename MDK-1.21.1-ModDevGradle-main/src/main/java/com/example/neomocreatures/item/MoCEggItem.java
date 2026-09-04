package com.example.neomocreatures.item;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.MoCEggEntity;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Places a MoCEggEntity in the world. A specific egg item (jungle wyvern egg,
 * mother wyvern egg, etc.) bakes in a fixed HatchSpec — which creature it
 * hatches, its variant/species, and an optional chance of an "upgraded" tier
 * (e.g. tier 2 instead of tier 1). The plain "Mystery Egg" has no spec: with
 * nothing baked in and no drop-tagged NBT (for a future creature that tags
 * its own egg drops), it deliberately does nothing when used.
 */
public class MoCEggItem extends Item {

    /**
     * @param tier1Type   the creature this normally hatches
     * @param tier2Type   an optional "upgraded" alternative (null = never rolls one)
     * @param tier2Chance chance (0.0-1.0) of rolling tier2Type instead of tier1Type
     * @param variantId   variant/species tag handed to EggHatchable, e.g. "JUNGLE"
     */
    public record HatchSpec(Supplier<? extends EntityType<?>> tier1Type,
            @Nullable Supplier<? extends EntityType<?>> tier2Type,
            double tier2Chance, String variantId) {
    }

    @Nullable
    private final HatchSpec spec;

    /** Plain, unconfigured egg (the "Mystery Egg") — does nothing when used. */
    public MoCEggItem(Properties properties) {
        this(properties, null);
    }

    public MoCEggItem(Properties properties, @Nullable HatchSpec spec) {
        super(properties);
        this.spec = spec;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        ResourceLocation hatchEntityId;
        String variantId;
        if (this.spec != null) {
            boolean rollTier2 = this.spec.tier2Type() != null
                    && serverLevel.random.nextDouble() < this.spec.tier2Chance();
            EntityType<?> chosen;
            if (rollTier2) {
                chosen = this.spec.tier2Type().get();
            } else {
                chosen = this.spec.tier1Type().get();
            }
            hatchEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(chosen);
            variantId = this.spec.variantId();
        } else {
            CompoundTag tag = context.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (!tag.contains("HatchEntityType", 8)) {
                // No spec baked in and no drop-tagged NBT — nothing to hatch.
                return InteractionResult.PASS;
            }
            hatchEntityId = ResourceLocation.parse(tag.getString("HatchEntityType"));
            variantId = tag.contains("HatchVariant", 8) ? tag.getString("HatchVariant") : null;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        MoCEggEntity egg = ModEntities.MOC_EGG.get().create(serverLevel);
        if (egg == null) {
            return InteractionResult.FAIL;
        }
        egg.setHatchEntityId(hatchEntityId);
        egg.setHatchVariant(variantId);
        egg.setSourceItemId(BuiltInRegistries.ITEM.getKey(this));
        egg.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0F, 0F);
        serverLevel.addFreshEntity(egg);

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}