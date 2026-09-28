package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.dragonfly.DragonflyVariant;
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

/** Port of {@code MoCEntityDragonfly}: four colours, buzzes near players while flying, no drops. */
public class MoCDragonflyEntity extends MoCInsectEntity {

    private static final int BUZZ_INTERVAL = 20;
    private static final double BUZZ_PLAYER_RANGE = 5.0D;
    private static final String TAG_VARIANT = "DragonflyVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCDragonflyEntity.class, EntityDataSerializers.INT);

    private int soundCount;

    public MoCDragonflyEntity(EntityType<? extends MoCDragonflyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.ARMOR, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, DragonflyVariant.BLUE.getId());
    }

    public DragonflyVariant getVariant() {
        return DragonflyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

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

    /** Original: selectType() - uniform 1-4. */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_VARIANT, this.random.nextInt(4) + 1);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level().getNearestPlayer(this, BUZZ_PLAYER_RANGE) != null
                && this.isFlying() && --this.soundCount <= 0) {
            this.playSound(ModSounds.DRAGONFLY_BUZZ.get(), this.getSoundVolume(), this.getVoicePitch());
            this.soundCount = BUZZ_INTERVAL;
        }
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.25F : 0.12F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DRAGONFLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRAGONFLY_HURT.get();
    }
}
