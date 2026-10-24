package com.example.neomocreatures.util;

import java.util.UUID;
import java.util.function.Function;

import com.example.neomocreatures.entity.EquippedPet;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Owner interactions shared by every tamed pet: renaming with a book and
 * capturing into a Pet Amulet. Entities call this at the top of mobInteract
 * instead of repeating the same blocks.
 */
public final class PetInteractionUtil {

    private static final float WHIP_VOLUME = 0.5F;
    private static final float WHIP_PITCH_BASE = 0.4F;
    private static final float WHIP_PITCH_RANGE = 0.4F;
    private static final float WHIP_PITCH_MIN_DIVISOR = 0.8F;

    private PetInteractionUtil() {
        // Utility class, no instances
    }

    /**
     * Handles the owner's Book (rename) and empty Pet Amulet (capture) interactions.
     *
     * @param amuletTagBuilder builds the pet-specific data saved inside the amulet
     * @return the result if the interaction was handled, or {@link InteractionResult#PASS}
     *         when the caller should keep processing it
     */
    public static InteractionResult tryOwnerBookOrAmulet(TamableAnimal pet, Player player, InteractionHand hand,
                                                         Function<UUID, CompoundTag> amuletTagBuilder) {
        // Only the owner of a tamed pet can rename or capture it.
        if (!pet.isTame() || !pet.isOwnedBy(player)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(pet, player);
        }

        if (stack.is(ModItems.PET_AMULET.get())) {
            // World changes happen on the server only.
            if (!pet.level().isClientSide) {
                capture(pet, player, hand, amuletTagBuilder);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /** Instant capture: the pet decides which equipment drops on the ground (see EquippedPet). */
    private static void capture(TamableAnimal pet, Player player, InteractionHand hand,
                                Function<UUID, CompoundTag> amuletTagBuilder) {
        if (pet instanceof EquippedPet equipped) {
            equipped.dropEquipmentForCapture();
        }
        PetStorageUtil.storeReplacingHeldItem(player, hand, pet, ModItems.PET_AMULET_FULL.get(),
                amuletTagBuilder.apply(player.getUUID()));
    }

    /** Plays the whip crack at the pet's position. Call on the server side only. */
    public static void playWhipSound(Entity pet) {
        pet.level().playSound(null, pet.blockPosition(), ModSounds.WHIP.get(), SoundSource.NEUTRAL,
                WHIP_VOLUME,
                WHIP_PITCH_BASE / (pet.getRandom().nextFloat() * WHIP_PITCH_RANGE + WHIP_PITCH_MIN_DIVISOR));
    }

    /** Plays the whip crack and wears the whip by one use (not in creative). Call on the server side only. */
    public static void useWhip(Entity pet, Player player, ItemStack whip) {
        playWhipSound(pet);
        if (!player.getAbilities().instabuild) {
            whip.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }
}