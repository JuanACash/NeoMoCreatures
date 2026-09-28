package com.example.neomocreatures.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;

/** Port of {@code MoCEntityMaggot}: a slow crawler that climbs walls, never jumps, no drops. */
public class MoCMaggotEntity extends MoCCrawlerEntity {

    public MoCMaggotEntity(EntityType<? extends MoCMaggotEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 0.8D));
    }

    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    @Override
    public void jumpFromGround() {
    }

    /** Same vanilla-sound stand-in as the snail: the original's exact constant can't be identified. */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }


    @Override
    protected boolean dropsSlimeballs() {
        return true;
    }
}
