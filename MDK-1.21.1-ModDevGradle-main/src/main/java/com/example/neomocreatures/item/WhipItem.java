package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rright-click on a ground block
 * (with air above). In the original it affects several pets in an area
 * (cats, wyverns, elephants, scorpions, ostriches) with different
 * effects (sitting, attacking, sprinting). Since we only have
 * MoCHorseEntity so far, we only ported the "mounted horse" branch
 * (sprintCounter in the original -> temporary Speed boost here).
 */
public class WhipItem extends Item {

    private static final int SPEED_DURATION_TICKS = 100;
    private static final int SPEED_AMPLIFIER = 1;
    private static final double RADIUS = 12.0D;

    public WhipItem(Properties properties) {
        super(properties.durability(24));
    }

    @Override
    public int getEnchantmentValue() {
            return 14;
        }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clicked = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        if (context.getClickedFace() == Direction.DOWN || clicked.isAir() || !above.isAir()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            level.playSound(null, pos, ModSounds.WHIP.get(), SoundSource.PLAYERS, 0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            for (MoCHorseEntity horse : level.getEntitiesOfClass(MoCHorseEntity.class, player.getBoundingBox().inflate(RADIUS))) {
                if (horse.isTamed() && horse.isVehicle()) {
                    if (horse.getSpecies() == com.example.neomocreatures.breeding.MoCHorseGenetics.Species.NIGHTMARE) {
                        horse.setNightmareTicks(200);
                    } else if (horse.getSpecies() == com.example.neomocreatures.breeding.MoCHorseGenetics.Species.UNICORN
                            || horse.getSpecies() == com.example.neomocreatures.breeding.MoCHorseGenetics.Species.FAIRY_HORSE) {
                        horse.startUnicornCharge();
                    } else {
                        horse.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SPEED_DURATION_TICKS, SPEED_AMPLIFIER, false, true));
                    }
                }
            }

            context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return InteractionResult.SUCCESS;
    }
}