package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.kitty.KittyVariant;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;

/**
 * Step 1+2 port of drzhark.mocreatures.entity.neutral.MoCEntityKitty: walks,
 * grows from kitten to adult, makes sound, 11 coat colors. No litter box,
 * kitty bed, taming, or the original's ~20-state AI yet — those come in
 * later steps.
 */
public class MoCKittyEntity extends TamableAnimal {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCKittyEntity.class, EntityDataSerializers.INT);

    private float lastAppliedScale = -1F;

    public MoCKittyEntity(EntityType<? extends MoCKittyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, KittyVariant.CREAM.getId());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.SCALE, 1.0D);
    }

    public KittyVariant getVariant() {
        return KittyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(KittyVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    // ---------------------------------------------------------------
    // Model hooks — always neutral for now. Sitting/swinging/mood state
    // get wired to real behavior once the AI state machine is ported.
    // ---------------------------------------------------------------
    public boolean isKittySitting() {
        return false;
    }

    public boolean isKittySwinging() {
        return false;
    }

    public int getKittyState() {
        return 0;
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // taming/breeding come in a later step
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, net.minecraft.world.entity.AgeableMob otherParent) {
        return null; // taming/breeding come in a later step
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ProtectKittenGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true, this::canHuntSmallMob));
    }

    private boolean canHuntSmallMob(@Nullable LivingEntity target) {
        if (target == null || target instanceof MoCKittyEntity || target instanceof Player) {
            return false;
        }
        return target.getBbWidth() < this.getBbWidth() && target.getBbHeight() < this.getBbHeight();
    }

    // Kittens never fight back or hunt, no matter which goal tries to set a target.
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.isBaby()) {
            return;
        }
        super.setTarget(target);
    }

    /**
     * A tamed kitty attacks the player if they hurt one of its kittens nearby —
     * checked reactively rather than as a real ongoing goal.
     */
    private static class ProtectKittenGoal extends Goal {
        private final MoCKittyEntity kitty;

        ProtectKittenGoal(MoCKittyEntity kitty) {
            this.kitty = kitty;
            this.setFlags(java.util.EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.kitty.isTame() || this.kitty.isBaby() || this.kitty.getTarget() != null) {
                return false;
            }
            for (MoCKittyEntity kitten : this.kitty.level().getEntitiesOfClass(MoCKittyEntity.class,
                    this.kitty.getBoundingBox().inflate(10.0D, 6.0D, 10.0D), MoCKittyEntity::isBaby)) {
                LivingEntity threat = kitten.getLastHurtByMob();
                if (threat instanceof Player && threat.isAlive() && this.kitty.distanceToSqr(threat) < 400.0D) {
                    this.kitty.setTarget(threat);
                    break;
                }
            }
            return false;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // Matches the original's selectType(): only rolls a color if one hasn't
        // already been set (e.g. by a future spawn-egg override).
        if (getVariant() == KittyVariant.CREAM) {
            setVariant(KittyVariant.rollNatural(this.random));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            tickGrowth();
        }
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        float newScale = getGrowthFraction();
        if (scaleAttr.getBaseValue() != newScale) {
            scaleAttr.setBaseValue(newScale);
        }
        float currentScale = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != currentScale) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    // ---------------------------------------------------------------
    // Sounds — babies use their own separate set, matching the original.
    // ---------------------------------------------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_AMBIENT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_HURT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isBaby()
                ? com.example.neomocreatures.init.ModSounds.KITTY_DEATH_BABY.get()
                : com.example.neomocreatures.init.ModSounds.KITTY_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("KittyVariant", getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("KittyVariant")) {
            setVariant(KittyVariant.byId(tag.getInt("KittyVariant")));
        }
    }
}