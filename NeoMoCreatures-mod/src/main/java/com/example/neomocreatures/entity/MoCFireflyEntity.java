package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;

/** Port of {@code MoCEntityFirefly}: borrows the Grasshopper's own wing-buzz and hurt sounds, no drops. */
public class MoCFireflyEntity extends MoCInsectEntity {

    private static final int BUZZ_INTERVAL = 20;
    private static final double BUZZ_PLAYER_RANGE = 5.0D;

    private int soundCount;

    public MoCFireflyEntity(EntityType<? extends MoCFireflyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.ARMOR, 1.0D);
    }


    /** Wiki: flies away from the player when approached. */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(-2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 0.8D, 1.3D));
    }

    /** Wiki: "makes clicking sounds occasionally" — its one dedicated sound file. */
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FIREFLY_AMBIENT.get();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level().getNearestPlayer(this, BUZZ_PLAYER_RANGE) != null
                && this.isFlying() && --this.soundCount <= 0) {
            this.playSound(ModSounds.GRASSHOPPER_FLY.get(), this.getSoundVolume(), this.getVoicePitch());
            this.soundCount = BUZZ_INTERVAL;
        }
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.12F : 0.1F;
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
