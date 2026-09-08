package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.scorpion.ScorpionVariant;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.Difficulty;

/**
 * Step 1 port of drzhark.mocreatures.entity.monster.MoCEntityScorpion:
 * spider-like (only aggros on its own when light &lt;= 9, always fights back
 * if hit, climbs walls/fences via horizontal-collision-as-climbable), leaps
 * at its target, avoids direct sunlight, and stings for a color-specific
 * status effect on a ~20% per-hit chance. 25% of wild spawns carry babies on
 * their back (purely visual for now — the pick-up-to-tame mechanic and the
 * wild-vs-tamed 18/40 HP split come in the taming step).
 */
public class MoCScorpionEntity extends TamableAnimal {

    private static final int STING_CHANCE = 5; // 1 in 5, matches rand.nextInt(5)==0
    private static final int STING_ANIM_TICKS = 50;
    private static final int CLAW_SWING_TICKS = 24;
    private static final int MOUTH_TALK_TICKS = 50;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_BABIES =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CLAW_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STING_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);

    public MoCScorpionEntity(EntityType<? extends MoCScorpionEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(6, new LeapAtTargetGoal(this, 0.4F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ScorpionDarknessTargetGoal<>(this, Player.class));
    }

    private static int getRawLight(MoCScorpionEntity scorpion) {
        return scorpion.level().getMaxLocalRawBrightness(scorpion.blockPosition());
    }

    private static class ScorpionDarknessTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCScorpionEntity scorpion;

        ScorpionDarknessTargetGoal(MoCScorpionEntity scorpion, Class<T> targetType) {
            super(scorpion, targetType, true);
            this.scorpion = scorpion;
        }

        @Override
        public boolean canUse() {
            return !this.scorpion.isTame() && getRawLight(this.scorpion) <= 9 && super.canUse();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 18.0D) // wild HP — bumped to 40 once tamed (taming step)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming is the pick-up-baby mechanic, not feeding (later step)
    }

    public ScorpionVariant getVariant() {
        return ScorpionVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(ScorpionVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public boolean fireImmune() {
        return getVariant().isFireImmune() || super.fireImmune();
    }

    /** Same trick vanilla's own Spider uses: touching a wall horizontally counts as "on a climbable". */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    public boolean hasBabies() {
        return this.entityData.get(DATA_HAS_BABIES);
    }

    private void setHasBabies(boolean flag) {
        this.entityData.set(DATA_HAS_BABIES, flag);
    }

    /** Public entry point for spawn eggs / other external code — internal logic stays on setHasBabies(). */
    public void setHasBabiesPublic(boolean flag) {
        setHasBabies(flag);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ScorpionVariant.DIRT.getId());
        builder.define(DATA_HAS_BABIES, false);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_CLAW_TICKS, 0);
        builder.define(DATA_STING_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ScorpionVariant", getVariant().name());
        tag.putBoolean("ScorpionBabies", hasBabies());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ScorpionVariant", 8)) {
            try {
                setVariant(ScorpionVariant.valueOf(tag.getString("ScorpionVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("ScorpionBabies")) {
            setHasBabies(tag.getBoolean("ScorpionBabies"));
        }
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new net.minecraft.world.entity.ai.navigation.WallClimberNavigation(this, level);
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnReason,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        if (!this.isTame() && this.random.nextInt(4) == 0) {
            setHasBabies(true);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void startTalking() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getClawTicks() {
        return this.entityData.get(DATA_CLAW_TICKS);
    }

    private void swingClaw() {
        if (this.entityData.get(DATA_CLAW_TICKS) == 0) {
            this.entityData.set(DATA_CLAW_TICKS, 1);
        }
    }

    public int getStingTicks() {
        return this.entityData.get(DATA_STING_TICKS);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        startTalking();
        return com.example.neomocreatures.init.ModSounds.SCORPION_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.SCORPION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.SCORPION_DEATH.get();
    }

    /**
     * ~20% chance per hit to sting instead of a plain claw swing — matches
     * rand.nextInt(5)==0. Nether's ignite is Player-only and only outside the
     * Nether; every other color's potion effect applies to any LivingEntity.
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (!hurt) {
            return false;
        }
        boolean stinging = this.entityData.get(DATA_STING_TICKS) == 0 && this.random.nextInt(STING_CHANCE) == 0;
        if (stinging && target instanceof LivingEntity living) {
            this.entityData.set(DATA_STING_TICKS, 1);
            this.playSound(com.example.neomocreatures.init.ModSounds.SCORPION_STING.get(), 1.0F, 1.0F);
            getVariant().applySting(living, this.level().dimension() == net.minecraft.world.level.Level.NETHER);
        } else {
            swingClaw();
        }
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            // Like any other hostile mob: never sticks around once the difficulty
            // drops to Peaceful (wild only — a tamed one stays with its owner).
            if (!this.isTame() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }

            tickIdleCounters();
        }
    }

    private void tickIdleCounters() {
        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > MOUTH_TALK_TICKS) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int claw = this.entityData.get(DATA_CLAW_TICKS);
        if (claw > 0) {
            if (claw == 10 || claw == 20) {
                this.playSound(com.example.neomocreatures.init.ModSounds.SCORPION_CLAW.get(), 1.0F, 1.0F);
            }
            if (++claw > CLAW_SWING_TICKS) {
                claw = 0;
            }
        }
        this.entityData.set(DATA_CLAW_TICKS, claw);

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