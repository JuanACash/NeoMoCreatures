package com.example.examplemod.item;

import com.example.examplemod.entity.MoCHorseEntity;
import com.example.examplemod.init.ModSounds;

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
 * Adaptado de MoCItemWhip (original): clic derecho sobre un bloque del suelo
 * (con aire arriba). En el original afecta en área a varias mascotas (gatos,
 * wyverns, elefantes, escorpiones, avestruces) con distintos efectos
 * (sentarse, atacar, sprint). Como todavia solo tenemos MoCHorseEntity,
 * portamos unicamente la rama de "caballo montado" (sprintCounter en el
 * original -> boost de Speed temporal aqui). Las ramas de sitting/nightmare
 * no se portan porque esos estados no existen todavia en nuestra entidad.
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
                    if (horse.getSpecies() == com.example.examplemod.breeding.MoCHorseGenetics.Species.NIGHTMARE) {
                        horse.setNightmareTicks(200);
                    } else if (horse.getSpecies() == com.example.examplemod.breeding.MoCHorseGenetics.Species.UNICORN
                            || horse.getSpecies() == com.example.examplemod.breeding.MoCHorseGenetics.Species.FAIRY_HORSE) {
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