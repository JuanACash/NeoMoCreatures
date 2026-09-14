package com.example.neomocreatures.item;

import java.util.function.Supplier;

import com.example.neomocreatures.breeding.MoCHorseGenetics.Coat;
import com.example.neomocreatures.breeding.MoCHorseGenetics.Species;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

/**
 * Right-click on the ground: spawns ONE random pick from the "found in the
 * wild" list (5 tier-1 horse coats, zebra, donkey) as an ADULT.
 * Right-click on an existing MoC horse: spawns a BABY matching that horse's
 * species/coat exactly — same behavior as vanilla's horse spawn egg used
 * on an existing horse.
 */
public class WildHorseSpawnEggItem extends DeferredSpawnEggItem {

    private record WildOption(Species species, Coat coat) {}

    private static final WildOption[] WILD_OPTIONS = {
            new WildOption(Species.HORSE, Coat.WHITE),
            new WildOption(Species.HORSE, Coat.CREAMY),
            new WildOption(Species.HORSE, Coat.BROWN),
            new WildOption(Species.HORSE, Coat.DARKBROWN),
            new WildOption(Species.HORSE, Coat.BLACK),
            new WildOption(Species.ZEBRA, Coat.WHITE),
            new WildOption(Species.DONKEY, Coat.WHITE),
    };

    public WildHorseSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, Item.Properties properties) {
        super(type, 0xD3A265, 0xEDE1C6, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        WildOption pick = WILD_OPTIONS[serverLevel.random.nextInt(WILD_OPTIONS.length)];
        spawnHorse(serverLevel, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, pick.species(), pick.coat(), false);

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target.level() instanceof ServerLevel serverLevel) || !(target instanceof MoCHorseEntity parent)) {
            return InteractionResult.PASS;
        }

        spawnHorse(serverLevel, target.getX(), target.getY(), target.getZ(), parent.getSpecies(), parent.getCoat(), true);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private void spawnHorse(ServerLevel level, double x, double y, double z, Species species, Coat coat, boolean baby) {
        MoCHorseEntity horse = ModEntities.MOC_HORSE.get().create(level);
        if (horse == null) return;

        double offsetX = (level.random.nextDouble() - 0.5D) * 2.0D;
        double offsetZ = (level.random.nextDouble() - 0.5D) * 2.0D;
        horse.moveTo(x + offsetX, y, z + offsetZ, level.random.nextFloat() * 360F, 0F);
        horse.setSpecies(species);
        horse.setCoat(coat);
        if (baby) {
            horse.setAge(-24000);
        }
        level.addFreshEntity(horse);
    }
}