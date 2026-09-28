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
import net.minecraft.world.level.Level;

/**
 * Port of {@code MoCEntityRoach}: a flying insect that scrambles away from anything bigger than
 * 0.3 blocks (except crabs) — but only while it's on the ground; once airborne it's no longer
 * scared. Borrows the grasshopper's hurt sound. No drops.
 */
public class MoCRoachEntity extends MoCInsectEntity {

    private static final float FLEE_DISTANCE = 6.0F;
    private static final double FLEE_FAR_SPEED = 0.8D;
    private static final double FLEE_NEAR_SPEED = 1.3D;
    private static final float FLEE_MIN_SIZE = 0.3F;

    public MoCRoachEntity(EntityType<? extends MoCRoachEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 1.0D)
                .add(Attributes.FLYING_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, LivingEntity.class, FLEE_DISTANCE, FLEE_FAR_SPEED,
                FLEE_NEAR_SPEED, other -> !(other instanceof MoCCrabEntity)
                        && (other.getEyeHeight() > FLEE_MIN_SIZE || other.getBbWidth() > FLEE_MIN_SIZE)) {
            @Override
            public boolean canUse() {
                return !MoCRoachEntity.this.isFlying() && super.canUse();
            }
        });
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.1F : 0.25F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GRASSHOPPER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GRASSHOPPER_HURT.get();
    }
}
