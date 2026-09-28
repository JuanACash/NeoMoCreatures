package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.butterfly.ButterflyVariant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.ItemTags;
/**
 * Port of {@code MoCEntityButterfly}. Silent and drop-less. Ten variants rolled uniformly, the last
 * three being moths (bigger, and attracted to light like a fly).
 */
public class MoCButterflyEntity extends MoCInsectEntity {

    private static final String TAG_VARIANT = "ButterflyVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCButterflyEntity.class, EntityDataSerializers.INT);

    public MoCButterflyEntity(EntityType<? extends MoCButterflyEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.12D)
                .add(Attributes.FLYING_SPEED, 0.6D);
    }


    /** Wiki: sometimes follows a player holding a flower (moths included, as it's the same entity). */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(-1, new MoCInsectTemptGoal(this, 1.0D, stack -> stack.is(ItemTags.FLOWERS)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ButterflyVariant.PIERIS_RAPAE.getId());
    }

    public ButterflyVariant getVariant() {
        return ButterflyVariant.byId(this.entityData.get(DATA_VARIANT));
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

    /** Original: selectType() - uniform 1-10. */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_VARIANT, this.random.nextInt(10) + 1);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public float getSizeFactor() {
        return this.getVariant().isMoth() ? 1.0F : 0.7F;
    }

    @Override
    public boolean isAttractedToLight() {
        return this.getVariant().isMoth();
    }

    @Override
    protected float getSoundVolume() {
        return this.isFlying() ? 0.13F : 0.1F;
    }

    
    /** Original: the butterfly is completely silent, hurt or dying. */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }
}
