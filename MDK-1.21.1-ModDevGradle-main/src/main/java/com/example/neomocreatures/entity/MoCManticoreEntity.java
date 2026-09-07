package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.manticore.ManticoreVariant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Port of drzhark.mocreatures.entity.hostile.MoCEntityManticore + its 5 color
 * subclasses: hostile while wild (only in the dark), wanders/flies aimlessly
 * (roars occasionally via the normal ambient sound), aggros players AND big
 * cats within ~12-16 blocks, scorpion-style sting with each color's own
 * status effect on a ~20% per-hit chance. Reuses the Big Cat's shared body
 * model/sounds per the wiki. Despawns on Peaceful like any other hostile mob.
 * No taming, colors' biome spawn, saddle, or chest yet - those come in later steps.
 */
public class MoCManticoreEntity extends TamableAnimal {

    private static final float BRIGHTNESS_FLEE_THRESHOLD = 0.5F;
    private static final double AGGRO_RADIUS = 14.0D; // wiki says "12 or 16" depending on source — split the difference
    private static final int STING_CHANCE = 5; // 1 in 5, matches rand.nextInt(5)==0
    private static final int STING_ANIM_TICKS = 50;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAIL_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STING_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);

    public MoCManticoreEntity(EntityType<? extends MoCManticoreEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new ManticoreAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new ManticoreFlyGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new ManticoreGroundWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ManticoreTargetGoal<>(this, Player.class, false));
        // Wiki: "Wild manticores will attack other mobs, including big cats."
        this.targetSelector.addGoal(3, new ManticoreTargetGoal<>(this, MoCBigCatEntity.class, false));
    }

    private static class ManticoreFlyGoal extends WaterAvoidingRandomFlyingGoal {
        private final MoCManticoreEntity manticore;

        ManticoreFlyGoal(MoCManticoreEntity manticore, double speedModifier) {
            super(manticore, speedModifier);
            this.manticore = manticore;
        }

        @Override
        public boolean canUse() {
            return this.manticore.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.manticore.getIsFlying() && super.canContinueToUse();
        }
    }

    private static class ManticoreGroundWanderGoal extends WaterAvoidingRandomStrollGoal {
        private final MoCManticoreEntity manticore;

        ManticoreGroundWanderGoal(MoCManticoreEntity manticore, double speedModifier) {
            super(manticore, speedModifier);
            this.manticore = manticore;
        }

        @Override
        public boolean canUse() {
            return !this.manticore.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.manticore.getIsFlying() && super.canContinueToUse();
        }
    }

    /** Gives up mid-fight if it steps into bright light — matches the original's darkness-lover behavior. */
    private static class ManticoreAttackGoal extends MeleeAttackGoal {
        private final MoCManticoreEntity manticore;

        ManticoreAttackGoal(MoCManticoreEntity manticore, double speedModifier, boolean followEvenIfNotSeen) {
            super(manticore, speedModifier, followEvenIfNotSeen);
            this.manticore = manticore;
        }

        @Override
        public boolean canContinueToUse() {
            float brightness = this.manticore.level().getBrightness(
                    net.minecraft.world.level.LightLayer.SKY, this.manticore.blockPosition());
            if (brightness >= BRIGHTNESS_FLEE_THRESHOLD && this.manticore.getRandom().nextInt(100) == 0) {
                this.manticore.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }
    }

    /** Only ever picks a target while it's dark enough — matches the original's spawn/combat light gate. */
    private static class ManticoreTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCManticoreEntity manticore;

        ManticoreTargetGoal(MoCManticoreEntity manticore, Class<T> targetType, boolean mustSee) {
            super(manticore, targetType, (int) AGGRO_RADIUS, mustSee, false, null);
            this.manticore = manticore;
        }

        @Override
        public boolean canUse() {
            float brightness = this.manticore.level().getBrightness(
                    net.minecraft.world.level.LightLayer.SKY, this.manticore.blockPosition());
            return !this.manticore.isTame() && brightness < BRIGHTNESS_FLEE_THRESHOLD && super.canUse();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Taming (egg hatching) comes in a later step.
        return false;
    }

    public ManticoreVariant getVariant() {
        return ManticoreVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    /** Also re-applies the color's max health/attack/fire immunity and heals to full — only meant to be called once, at spawn. */
    public void setVariant(ManticoreVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
        AttributeInstance maxHealthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(variant.getMaxHealth());
        }
        AttributeInstance damageAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.setBaseValue(variant.getAttackDamage());
        }
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public boolean fireImmune() {
        return getVariant().isFireImmune() || super.fireImmune();
    }

    public boolean getIsFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public void setIsFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
        this.setNoGravity(flying);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ManticoreVariant.PLAIN.getId());
        builder.define(DATA_FLYING, false);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_TAIL_TICKS, 0);
        builder.define(DATA_STING_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ManticoreVariant", getVariant().name());
        tag.putBoolean("ManticoreFlying", getIsFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ManticoreVariant", 8)) {
            try {
                setVariant(ManticoreVariant.valueOf(tag.getString("ManticoreVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("ManticoreFlying")) {
            setIsFlying(tag.getBoolean("ManticoreFlying"));
        }
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void openMouth() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getTailTicks() {
        return this.entityData.get(DATA_TAIL_TICKS);
    }

    public int getStingTicks() {
        return this.entityData.get(DATA_STING_TICKS);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        openMouth();
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_AMBIENT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        openMouth();
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_HURT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH.get();
    }

    /**
     * ~20% chance per hit to trigger the sting (status effect + tail-strike pose +
     * its sound) instead of a plain bite - matches the original's rand.nextInt(5)==0.
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (!hurt) {
            return false;
        }
        if (this.entityData.get(DATA_STING_TICKS) == 0 && this.random.nextInt(STING_CHANCE) == 0 && target instanceof LivingEntity living) {
            this.entityData.set(DATA_STING_TICKS, 1);
            this.playSound(com.example.neomocreatures.init.ModSounds.WYVERN_POISON.get(), 1.0F, 1.0F);
            getVariant().applyStingEffect(living);
        } else {
            openMouth();
        }
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (!this.isTame() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }

            tickIdleCounters();
            tickWanderFlight();
        }
    }

    private void tickWanderFlight() {
        if (this.getTarget() == null) {
            if (!getIsFlying() && this.random.nextInt(100) == 0) {
                setIsFlying(true);
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                }
            } else if (getIsFlying() && this.onGround() && this.random.nextInt(150) == 0) {
                setIsFlying(false);
            }
        } else if (!getIsFlying()) {
            setIsFlying(true);
            if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
            }
        }
    }

    private void tickIdleCounters() {
        int tail = this.entityData.get(DATA_TAIL_TICKS);
        if (tail > 0 && ++tail > 10) {
            tail = 0;
        }
        if (tail == 0 && this.random.nextInt(250) == 0) {
            tail = 1;
        }
        this.entityData.set(DATA_TAIL_TICKS, tail);

        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > 30) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int sting = this.entityData.get(DATA_STING_TICKS);
        if (sting > 0 && ++sting > STING_ANIM_TICKS) {
            sting = 0;
        }
        this.entityData.set(DATA_STING_TICKS, sting);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}