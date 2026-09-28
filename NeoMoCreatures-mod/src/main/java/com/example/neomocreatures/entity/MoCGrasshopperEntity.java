package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.grasshopper.GrasshopperVariant;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Port of {@code MoCEntityGrasshopper}: a flying insect that also hops like a cricket. Two greens.
 * Buzzes its wings when it's off the ground with a player nearby; chirps rarely. No drops.
 */
public class MoCGrasshopperEntity extends MoCInsectEntity {

    private static final double HOP_MIN_SPEED = 0.05D;
    private static final double HOP_HORIZONTAL_BOOST = 5.0D;
    private static final double HOP_VERTICAL_SPEED = 0.45D;
    private static final int HOP_COOLDOWN_TICKS = 30;
    private static final int BUZZ_INTERVAL = 10;
    private static final double BUZZ_PLAYER_RANGE = 5.0D;
    private static final double SOUND_CHANCE = 0.1D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCGrasshopperEntity.class, EntityDataSerializers.INT);

    private int jumpCounter;
    private int soundCounter;

    public MoCGrasshopperEntity(EntityType<? extends MoCGrasshopperEntity> type, Level level) {
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, GrasshopperVariant.OLIVE_GREEN.getId());
    }

    public GrasshopperVariant getVariant() {
        return GrasshopperVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    private static final String TAG_VARIANT = "GrasshopperVariant";

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
                ? GrasshopperVariant.BRIGHT_GREEN.getId() : GrasshopperVariant.OLIVE_GREEN.getId());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if ((this.isFlying() || !this.onGround()) && --this.soundCounter <= 0) {
            if (this.level().getNearestPlayer(this, BUZZ_PLAYER_RANGE) != null) {
                this.playSound(ModSounds.GRASSHOPPER_FLY.get(), this.getSoundVolume(), this.getVoicePitch());
            }
            this.soundCounter = BUZZ_INTERVAL;
        }
        if (this.jumpCounter > 0 && ++this.jumpCounter > HOP_COOLDOWN_TICKS) {
            this.jumpCounter = 0;
        }
    }

    /** Original: hops like the cricket does whenever it's moving along the ground. */
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

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.12F : 0.15F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextDouble() <= SOUND_CHANCE ? ModSounds.GRASSHOPPER_CHIRP.get() : null;
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
