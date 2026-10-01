package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;

/**
 * Port of {@code MoCEntityBee}. Neutral: being hit (unless the difficulty is Peaceful) makes it chase
 * and sting its attacker, and it calms down again if it never lands the sting. Follows the wiki and
 * vanilla rather than the original code, which only marked the target and never had an attack goal.
 * Like vanilla and real bees it stings once, loses its stinger and dies shortly after.
 */
public class MoCBeeEntity extends MoCInsectEntity {

    private static final int BUZZ_INTERVAL = 20;
    private static final double BUZZ_PLAYER_RANGE = 5.0D;

    private int soundCount;


    private static final double ATTACK_SPEED = 1.0D;
    /** Wiki: "very little damage". */
    private static final double ATTACK_DAMAGE = 1.0D;
    /** Wiki: it becomes neutral again "after a period of time" if it never lands the sting. */
    private static final int ANGER_DURATION_TICKS = 400;
    /** Vanilla: a stung bee is guaranteed dead within 1200 ticks, at a random moment before that. */
    private static final int STING_DEATH_WINDOW_TICKS = 1200;

    private static final String TAG_STUNG = "HasStung";
    private static final String TAG_TIME_SINCE_STING = "TimeSinceSting";

    private boolean hasStung;
    private int timeSinceSting;
    private int angerTicks;

    public MoCBeeEntity(EntityType<? extends MoCBeeEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Priority -1: it has to beat the base class's wander goal (priority 0) or it would never chase.
        this.goalSelector.addGoal(-1, new MeleeAttackGoal(this, ATTACK_SPEED, true));
        // Wiki: sometimes follows a player holding a flower.
        this.goalSelector.addGoal(-1, new MoCInsectTemptGoal(this, 1.0D, stack -> stack.is(ItemTags.FLOWERS)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.isFlying() && --this.soundCount <= 0
                && this.level().getNearestPlayer(this, BUZZ_PLAYER_RANGE) != null) {
            SoundEvent buzz = this.getTarget() != null ? ModSounds.BEE_UPSET.get() : ModSounds.BEE_BUZZ.get();
            this.playSound(buzz, this.getSoundVolume(), this.getVoicePitch());
            this.soundCount = BUZZ_INTERVAL;
        }
        this.tickAnger();
        this.tickStingDeath();
    }

    /** Wiki: after being provoked it becomes neutral again once enough time has passed. */
    private void tickAnger() {
        if (this.getTarget() == null) {
            this.angerTicks = 0;
        } else if (++this.angerTicks > ANGER_DURATION_TICKS) {
            this.setTarget(null);
            this.angerTicks = 0;
        }
    }

    /** Vanilla's own rule: every 5 ticks after stinging, a chance that grows to a certainty by 1200. */
    private void tickStingDeath() {
        if (!this.hasStung) {
            return;
        }
        this.timeSinceSting++;
        if (this.timeSinceSting % 5 == 0
                && this.random.nextInt(Mth.clamp(STING_DEATH_WINDOW_TICKS - this.timeSinceSting, 1, STING_DEATH_WINDOW_TICKS)) == 0) {
            this.hurt(this.damageSources().generic(), this.getHealth());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            Entity attacker = source.getEntity();
            // Wiki: only on Easy or higher; a bee that has already stung has nothing left to attack with.
            if (!this.hasStung && attacker instanceof LivingEntity living && attacker != this
                    && this.level().getDifficulty() != Difficulty.PEACEFUL) {
                this.setTarget(living);
            }
            return true;
        }
        return false;
    }

    /** One sting, then it loses its stinger, stops attacking and dies shortly after. */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);
        if (success) {
            this.hasStung = true;
            this.timeSinceSting = 0;
            this.setTarget(null);
        }
        return success;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TAG_STUNG, this.hasStung);
        tag.putInt(TAG_TIME_SINCE_STING, this.timeSinceSting);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hasStung = tag.getBoolean(TAG_STUNG);
        this.timeSinceSting = tag.getInt(TAG_TIME_SINCE_STING);
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.15F : 0.12F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BEE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BEE_HURT.get();
    }
}
