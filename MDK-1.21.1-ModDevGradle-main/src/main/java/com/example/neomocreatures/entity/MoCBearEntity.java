package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bear.BearVariant;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class MoCBearEntity extends TamableAnimal {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BEAR_STATE =
            SynchedEntityData.defineId(MoCBearEntity.class, EntityDataSerializers.INT);

    public static final int FOURS_STATE = 0;
    public static final int STANDING_STATE = 1;
    public static final int SITTING_STATE = 2;

    private static final float BABY_SCALE = 0.5F;

    private int standingTicks;
    private float lastAppliedScale = -1F;

    public MoCBearEntity(EntityType<? extends MoCBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BearVariant.BLACK.getId());
        builder.define(DATA_BEAR_STATE, FOURS_STATE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public BearVariant getVariant() {
        return BearVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(BearVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public int getBearState() {
        return this.entityData.get(DATA_BEAR_STATE);
    }

    private void setBearState(int state) {
        this.entityData.set(DATA_BEAR_STATE, state);
    }

    // ---------------------------------------------------------------
    // Temperament: this is the single choke point for "does this bear
    // ever fight back". Cubs and pandas simply can never have a target,
    // no matter which goal tries to set one.
    // ---------------------------------------------------------------
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && (this.isBaby() || getVariant().getTemperament() == BearVariant.Temperament.PASSIVE)) {
            return;
        }
        super.setTarget(target);
    }

    private boolean shouldTargetPlayers(@Nullable LivingEntity target) {
        return getVariant().getTemperament() == BearVariant.Temperament.HOSTILE;
    }

    private boolean canHuntAnimal(@Nullable LivingEntity target) {
        return !(target instanceof MoCBearEntity) && !(target instanceof MoCBigCatEntity);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new FollowSameVariantAdultGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ProtectCubGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::shouldTargetPlayers));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, true, this::canHuntAnimal));
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false; // no taming/feeding yet — that's a later step
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob otherParent) {
        return null; // breeding comes later, alongside taming
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // Variant/biome-based natural spawning is its own later step; for now
        // spawn eggs are what set the variant (see BearSpawnEggItem).
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            tickGrowth();
            tickBearState();
        }
    }

    // ---------------------------------------------------------------
    // Growth — identical pattern to MoCBigCatEntity: smooth scale
    // interpolation over getVariant().getGrowthTicks(), not a sudden
    // pop at adulthood.
    // ---------------------------------------------------------------
    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        int growthTicks = getVariant().getGrowthTicks();
        float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        float newScale = getGrowthFraction() * (float) getVariant().getRenderScale();
        if (scaleAttr.getBaseValue() != newScale) {
            scaleAttr.setBaseValue(newScale);
        }
        float currentScale = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != currentScale) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    // ---------------------------------------------------------------
    // Standing on hind legs (black/grizzly/polar) / sitting (panda),
    // occasionally, especially near a player.
    // ---------------------------------------------------------------
    private void tickBearState() {
        if (this.standingTicks > 0 && ++this.standingTicks > 100) {
            this.standingTicks = 0;
            setBearState(FOURS_STATE);
        }
        if (!this.isBaby() && getBearState() == FOURS_STATE && this.standingTicks == 0 && this.random.nextInt(200) == 0) {
            Player nearby = this.level().getNearestPlayer(this, 6D);
            if (nearby != null && this.hasLineOfSight(nearby)) {
                this.standingTicks = 1;
                setBearState(getVariant() == BearVariant.PANDA ? SITTING_STATE : STANDING_STATE);
            }
        }
    }

    // ---------------------------------------------------------------
    // Sounds — all 4 species share the same set, matching the original.
    // ---------------------------------------------------------------
    @Override
    protected SoundEvent getAmbientSound() {
        return com.example.neomocreatures.init.ModSounds.BEAR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.BEAR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.BEAR_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BearVariant", getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BearVariant")) {
            setVariant(BearVariant.byId(tag.getInt("BearVariant")));
        }
    }

    // ---------------------------------------------------------------
    // A cub only follows an ADULT of its own species — never a
    // different species, tamed or not. It loses this once it itself
    // is tamed (no-op for now since taming doesn't exist yet).
    // ---------------------------------------------------------------
    private static class FollowSameVariantAdultGoal extends Goal {
        private final MoCBearEntity cub;
        private final double speedModifier;
        private MoCBearEntity adult;
        private int timeToRecalcPath;

        FollowSameVariantAdultGoal(MoCBearEntity cub, double speedModifier) {
            this.cub = cub;
            this.speedModifier = speedModifier;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.cub.isBaby() || this.cub.isTame()) {
                return false;
            }
            java.util.List<MoCBearEntity> nearby = this.cub.level().getEntitiesOfClass(MoCBearEntity.class,
                    this.cub.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                    bear -> !bear.isBaby() && bear.getVariant() == this.cub.getVariant());
            if (nearby.isEmpty()) {
                return false;
            }
            this.adult = nearby.get(0);
            return this.cub.distanceToSqr(this.adult) > 9.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cub.isBaby() && !this.cub.isTame() && this.adult != null && this.adult.isAlive()
                    && this.cub.distanceToSqr(this.adult) > 9.0D && this.cub.distanceToSqr(this.adult) < 256.0D;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.adult = null;
        }

        @Override
        public void tick() {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.cub.getNavigation().moveTo(this.adult, this.speedModifier);
            }
        }
    }

    /**
     * Reactive check, not a real ongoing goal: every so often, if a nearby
     * cub of the same species currently has an attacker, the adult (if
     * neutral) picks up that same target. Polar bears don't need this
     * (already always hostile) and pandas can never get a target anyway
     * (blocked in setTarget()).
     */
    private static class ProtectCubGoal extends Goal {
        private final MoCBearEntity bear;

        ProtectCubGoal(MoCBearEntity bear) {
            this.bear = bear;
            this.setFlags(java.util.EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (this.bear.isBaby() || this.bear.getVariant().getTemperament() != BearVariant.Temperament.NEUTRAL
                    || this.bear.getTarget() != null) {
                return false;
            }
            for (MoCBearEntity cub : this.bear.level().getEntitiesOfClass(MoCBearEntity.class,
                    this.bear.getBoundingBox().inflate(10.0D, 6.0D, 10.0D),
                    b -> b.isBaby() && b.getVariant() == this.bear.getVariant())) {
                LivingEntity threat = cub.getLastHurtByMob();
                if (threat != null && threat.isAlive() && this.bear.distanceToSqr(threat) < 400.0D) {
                    this.bear.setTarget(threat);
                    break;
                }
            }
            return false;
        }
    }
}