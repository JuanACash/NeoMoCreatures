package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Port of {@code MoCEntityFly}: attracted to light, buzzes near players while flying, no drops. */
public class MoCFlyEntity extends MoCInsectEntity {

    private static final int BUZZ_INTERVAL = 55;
    private static final double BUZZ_PLAYER_RANGE = 5.0D;

    private int soundCount;

    public MoCFlyEntity(EntityType<? extends MoCFlyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.25D);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(-2, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 0.8D, 1.3D,
                other -> !(other instanceof MoCInsectEntity)
                        && (other.getEyeHeight() > 0.3F || other.getBbWidth() > 0.3F)
                        && !(other instanceof Player player
                                && (isRottenFlesh(player.getMainHandItem()) || isRottenFlesh(player.getOffhandItem())))));
        this.goalSelector.addGoal(-1, new MoCInsectTemptGoal(this, 1.0D, MoCFlyEntity::isRottenFlesh));
    }

    private static boolean isRottenFlesh(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH);
    }

    @Override
    public boolean isAttractedToLight() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Original only reset its counter when a player happened to be near on the exact tick it hit
        // -1, so it could go silent forever; this resets it every time instead.
        if (!this.level().isClientSide && this.isFlying() && --this.soundCount <= 0) {
            if (this.level().getNearestPlayer(this, BUZZ_PLAYER_RANGE) != null) {
                this.playSound(ModSounds.FLY_BUZZ.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            this.soundCount = BUZZ_INTERVAL;
        }
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.2F : 0.12F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FLY_HURT.get();
    }
}
