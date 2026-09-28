package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.cricket.CricketVariant;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Port of {@code MoCEntityCricket}. Hops instead of walking: whenever it is moving along the ground
 * it launches itself forward and up. Chirps by day and rasps at night, each only rarely. No drops.
 */
public class MoCCricketEntity extends MoCCrawlerEntity {

    private static final double HOP_MIN_SPEED = 0.05D;
    private static final double HOP_HORIZONTAL_BOOST = 5.0D;
    private static final double HOP_VERTICAL_SPEED = 0.45D;
    private static final int HOP_COOLDOWN_TICKS = 30;
    private static final double SOUND_CHANCE = 0.1D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCCricketEntity.class, EntityDataSerializers.INT);

    private int jumpCounter;

    public MoCCricketEntity(EntityType<? extends MoCCricketEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ARMOR, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.2D));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, CricketVariant.BROWN.getId());
    }

    public CricketVariant getVariant() {
        return CricketVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    private static final String TAG_VARIANT = "CricketVariant";

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.entityData.set(DATA_VARIANT, tag.getInt(TAG_VARIANT));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_VARIANT, this.random.nextInt(100) <= 50
                ? CricketVariant.LIGHT_BROWN.getId() : CricketVariant.BROWN.getId());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.jumpCounter > 0 && ++this.jumpCounter > HOP_COOLDOWN_TICKS) {
            this.jumpCounter = 0;
        }
    }

    /** Original: while moving along the ground, launch forward (5x its current speed) and upward. */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || !this.onGround() || this.jumpCounter != 0) {
            return;
        }
        var motion = this.getDeltaMovement();
        if (Math.abs(motion.x) > HOP_MIN_SPEED || Math.abs(motion.z) > HOP_MIN_SPEED) {
            this.setDeltaMovement(motion.x * HOP_HORIZONTAL_BOOST, HOP_VERTICAL_SPEED, motion.z * HOP_HORIZONTAL_BOOST);
            this.jumpCounter = 1;
        }
    }

    /** Original: at night the rasping call, by day the chirp - each only 10% of the time it's asked. */
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextDouble() > SOUND_CHANCE) {
            return null;
        }
        return this.level().isDay() ? ModSounds.CRICKET_CHIRP.get() : ModSounds.CRICKET_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CRICKET_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CRICKET_HURT.get();
    }
}
