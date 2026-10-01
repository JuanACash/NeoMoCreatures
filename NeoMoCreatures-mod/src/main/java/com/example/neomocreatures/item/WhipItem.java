package com.example.neomocreatures.item;

import com.example.neomocreatures.entity.MoCElephantEntity;
import com.example.neomocreatures.entity.MoCHorseEntity;
import com.example.neomocreatures.entity.MoCOstrichEntity;
import com.example.neomocreatures.entity.MoCWyvernEntity;
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
 * Right-click on a ground block (with air above). In the original it affects
 * several pets in an area (cats, wyverns, elephants, scorpions, ostriches)
 * with different effects (sitting, attacking, sprinting). So far we've ported
 * the "mounted horse" branch (sprintCounter in the original -> temporary
 * Speed boost here) and the "tamed wyvern" branch (sit + stay put).
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

            // Wiki: a whip makes tamed wyverns sit and stay put (prevents
            // flying off/getting lost) — doesn't require being ridden.
            for (MoCWyvernEntity wyvern : level.getEntitiesOfClass(MoCWyvernEntity.class, player.getBoundingBox().inflate(RADIUS))) {
                if (wyvern.isTame()) {
                    wyvern.setSitting(true);
                    wyvern.setTarget(null);
                }
            }

            // Wiki: whipping a mounted, harnessed elephant/mammoth gives it a speed boost AND
            // has it ram (push + hurt) anything in its way — both effects together, unlike the
            // horse where unicorns get only the charge and everything else gets only speed.
            for (MoCElephantEntity elephant : level.getEntitiesOfClass(MoCElephantEntity.class, player.getBoundingBox().inflate(RADIUS))) {
                if (elephant.isTame() && elephant.hasHarness() && elephant.isVehicle()) {
                    elephant.startWhipCharge();
                }
            }

            // Wiki: whip gives a ridden ostrich a short speed boost.
            for (MoCOstrichEntity ostrich : level.getEntitiesOfClass(MoCOstrichEntity.class, player.getBoundingBox().inflate(RADIUS))) {
                if (ostrich.isTame() && ostrich.isVehicle()) {
                    ostrich.applyWhipBoost();
                }
            }

            context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        return InteractionResult.SUCCESS;
    }
}