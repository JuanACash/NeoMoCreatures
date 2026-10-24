package com.example.neomocreatures.util;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.SaddledMount;
import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Helpers shared by every tamed mount. Saddle interactions: only the owner can put a saddle on
 * or take it off; mounting is deliberately not handled here (anyone can mount, like a horse).
 * The saddle methods return null when the interaction does not apply, so the caller tries the next step.
 * Riding: {@link #alignWithRider} keeps a ridden mount facing where its rider looks.
 */
public final class MountInteractionUtil {

    /** Share of the rider's pitch copied to the mount: land and air mounts tilt their head gently. */
    public static final float PITCH_HALF = 0.5F;

    /** Keeps the mount level regardless of where the rider looks (swimming mounts, komodo dragon). */
    public static final float PITCH_LEVEL = 0.0F;

    private static final float FULL_TURN_DEGREES = 360.0F;

    private MountInteractionUtil() {
        // Utility class, no instances
    }

    /** The owner puts a saddle from the saddle tag on an adult, unsaddled mount. */
    @Nullable
    public static <T extends TamableAnimal & SaddledMount> InteractionResult tryEquipSaddle(
            T mount, Player player, ItemStack stack) {
        if (!(mount.isTame() && mount.isOwnedBy(player) && !mount.isBaby() && !mount.isSaddled()
                && stack.is(ModTags.MOUNT_SADDLES))) {
            return null;
        }

        if (!mount.level().isClientSide) {
            mount.setSaddled(true);
            mount.setSaddleItemId(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            mount.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
            MoCInventoryUtil.shrinkUnlessCreative(player, stack);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * The owner takes the saddle off with shears; the exact saddle item is dropped.
     *
     * @param damageShears whether the shears lose durability (some mounts do, some don't)
     */
    @Nullable
    public static <T extends TamableAnimal & SaddledMount> InteractionResult tryRemoveSaddle(
            T mount, Player player, ItemStack stack, boolean damageShears) {
        if (!(mount.isTame() && mount.isOwnedBy(player) && stack.is(Items.SHEARS) && mount.isSaddled())) {
            return null;
        }

        if (!mount.level().isClientSide) {
            mount.setSaddled(false);
            mount.ejectPassengers();
            Item saddleItem = MoCInventoryUtil.saddleItemOrDefault(mount.getSaddleItemId());
            mount.setSaddleItemId(null);
            mount.spawnAtLocation(new ItemStack(saddleItem));
            mount.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            if (damageShears && !player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Turns a ridden mount to face where its rider looks. Call it from {@code tickRidden}
     * right after {@code super.tickRidden}.
     *
     * @param pitchFactor share of the rider's pitch copied to the mount, see {@link #PITCH_HALF}
     *                    and {@link #PITCH_LEVEL}
     */
    public static void alignWithRider(LivingEntity mount, Player rider, float pitchFactor) {
        mount.setYRot(rider.getYRot());
        mount.yRotO = mount.getYRot();
        mount.setXRot(rider.getXRot() * pitchFactor);
        // Same wrap-around that Entity#setRot applies; that method is protected, so it is repeated here.
        mount.setYRot(mount.getYRot() % FULL_TURN_DEGREES);
        mount.setXRot(mount.getXRot() % FULL_TURN_DEGREES);
        mount.yBodyRot = mount.getYRot();
        mount.yHeadRot = mount.getYRot();
    }
}