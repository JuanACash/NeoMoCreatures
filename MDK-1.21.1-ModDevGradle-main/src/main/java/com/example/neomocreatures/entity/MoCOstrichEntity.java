package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.ostrich.OstrichVariant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MoCOstrichEntity extends TamableAnimal {

    private static final int HIDE_TICKS = 60;
    private static final int MOUTH_TICKS_MAX = 20;
    private static final int WING_TICKS_MAX = 80;
    private static final int GROWTH_TICKS = 48000;
    private static final float BABY_SCALE = 0.4F;
    private static final float BABY_HITBOX_SCALE = 0.5F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WING_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);

    private int hidingCounter;
    private float lastAppliedScale = -1F;

    public MoCOstrichEntity(EntityType<? extends MoCOstrichEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !MoCOstrichEntity.this.isHiding() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !MoCOstrichEntity.this.isHiding() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return !isBaby() && (getVariant() == OstrichVariant.MALE || getVariant() == OstrichVariant.WHITE) && super.canUse();
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public OstrichVariant getVariant() {
        return OstrichVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(OstrichVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    private void setHiding(boolean hiding) {
        this.entityData.set(DATA_HIDING, hiding);
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void startTalking() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getWingTicks() {
        return this.entityData.get(DATA_WING_TICKS);
    }

    private void flapWings() {
        if (this.entityData.get(DATA_WING_TICKS) == 0) {
            this.entityData.set(DATA_WING_TICKS, 1);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, OstrichVariant.DARK.getId());
        builder.define(DATA_HIDING, false);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_WING_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("OstrichVariant", getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("OstrichVariant", 8)) {
            try {
                setVariant(OstrichVariant.valueOf(tag.getString("OstrichVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        startTalking();
        return com.example.neomocreatures.init.ModSounds.OSTRICH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.OSTRICH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.OSTRICH_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (!hurt || this.level().isClientSide) {
            return hurt;
        }
        if (this.isTame()) {
            return true;
        }
        if (!this.isBaby() && (getVariant() == OstrichVariant.MALE || getVariant() == OstrichVariant.WHITE)) {
            startTalking();
            flapWings();
            return true;
        }
        setHiding(true);
        hidingCounter = HIDE_TICKS;
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double dx = Math.cos(angle) * 6.0D;
        double dz = Math.sin(angle) * 6.0D;
        this.getNavigation().moveTo(this.getX() + dx, this.getY(), this.getZ() + dz, 1.4D);
        return true;
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = net.minecraft.util.Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return net.minecraft.util.Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction();
            if (scaleAttr.getBaseValue() != newScale) {
                scaleAttr.setBaseValue(newScale);
            }
        }
        float currentScale = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != currentScale) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    @Override
    protected net.minecraft.world.phys.AABB makeBoundingBox() {
        if (this.isBaby()) {
            net.minecraft.world.entity.EntityDimensions babyHitbox =
                    this.getType().getDimensions().scale(BABY_HITBOX_SCALE);
            return babyHitbox.makeBoundingBox(this.position());
        }
        return super.makeBoundingBox();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickIdleCounters();
            if (hidingCounter > 0 && --hidingCounter == 0) {
                setHiding(false);
            }
        }
    }

    private void tickIdleCounters() {
        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > MOUTH_TICKS_MAX) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int wing = this.entityData.get(DATA_WING_TICKS);
        if (wing > 0 && ++wing > WING_TICKS_MAX) {
            wing = 0;
        }
        this.entityData.set(DATA_WING_TICKS, wing);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}