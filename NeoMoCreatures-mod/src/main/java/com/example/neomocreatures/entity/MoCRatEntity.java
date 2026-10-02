package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.rat.RatVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityRat}. Only actively hunts a
 * player/iron golem while it's dark (brightness below 0.5, giving up with a small chance once it's
 * bright); if hit, calls every rat within a 16x4x16 box to pile onto the attacker too. Never burns
 * in sunlight. 3 colours, forced to brown in desert/mesa and white in snow/frozen biomes.
 * <p>
 * Deviates from the original in two ways, per the wiki: does not climb walls (the original's own
 * Spider-style climbing was dropped), and moves slightly slower than the player instead of the
 * original code's own faster-than-player 0.3.
 */
public class MoCRatEntity extends Monster {

    private static final double MAX_HEALTH = 16.0D;
    /** Wiki: "slightly slower than the player's walking pace" — the original code's own 0.3 was
     *  actually faster than the player, not slower; the player's own base value is 0.1. */
    private static final double MOVEMENT_SPEED = 0.15D;
    private static final double ATTACK_DAMAGE = 3.0D;
    private static final double ATTACK_SPEED = 1.0D;
    private static final double WANDER_SPEED = 1.0D;

    private static final float MAX_ATTACK_BRIGHTNESS = 0.5F;
    private static final int GIVE_UP_IN_LIGHT_CHANCE = 100;
    /** Original: calls in every other rat within this box when one of them is hit. */
    private static final double ALERT_RADIUS_XZ = 16.0D;
    private static final double ALERT_RADIUS_Y = 4.0D;

    private static final String TAG_VARIANT = "RatVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCRatEntity.class, EntityDataSerializers.INT);

    public MoCRatEntity(EntityType<? extends MoCRatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RatAttackGoal(this, ATTACK_SPEED));
        // Wiki: "wander around aimlessly" and "usually stay out of water".
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new RatTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new RatTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, RatVariant.BROWN.getId());
    }

    public RatVariant getVariant() {
        return RatVariant.byId(this.entityData.get(DATA_VARIANT));
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

    /** Original: selectType() — 65% brown, 33% black, 2% white; checkSpawningBiome() overrides that
     *  with a forced colour in desert/mesa (brown) or snow/frozen (white) biomes. */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_VARIANT, this.rollVariant(level).getId());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private RatVariant rollVariant(ServerLevelAccessor level) {
        var biomeKey = level.getBiome(this.blockPosition()).unwrapKey().orElse(null);
        if (biomeKey != null) {
            String path = biomeKey.location().getPath();
            if (path.contains("desert") || path.contains("mesa") || path.contains("badlands")) {
                return RatVariant.BROWN;
            }
            if (path.contains("snow") || path.contains("frozen")) {
                return RatVariant.WHITE;
            }
        }
        // Deviates from the original's 65/33/2 split (brown/black/white only): red is folded in as
        // an even 4th option alongside the other three.
        RatVariant[] variants = RatVariant.values();
        return variants[this.random.nextInt(variants.length)];
    }

    /** Original: never takes fall damage. */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    /** Wiki: never burns in sunlight. */
    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    /** Original: attackEntityFrom() — being hit also calls in every other nearby rat without a
     *  target of its own to pile onto the same attacker. */
    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        Entity attacker = damageSource.getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            this.setTarget(livingAttacker);
            if (!this.level().isClientSide) {
                AABB area = this.getBoundingBox().inflate(ALERT_RADIUS_XZ, ALERT_RADIUS_Y, ALERT_RADIUS_XZ);
                for (MoCRatEntity other : this.level().getEntitiesOfClass(MoCRatEntity.class, area,
                        rat -> rat != this && rat.getTarget() == null)) {
                    other.setTarget(livingAttacker);
                }
            }
        }
        return super.hurt(damageSource, amount);
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.RAT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.RAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.RAT_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-1 raw rat (auto-cooked if it died on fire), scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, MoCLootUtil.rawOrCooked(this, ModItems.RAT_RAW.get(), ModItems.RAT_COOKED.get()),
                MoCLootUtil.rollWithLootingBonus(this.random, 2, lootingLevel));
    }


    // ---------------------------------------------------------------------
    // Darkness-gated goals
    // ---------------------------------------------------------------------

    private static final class RatAttackGoal extends MeleeAttackGoal {
        private final MoCRatEntity rat;

        RatAttackGoal(MoCRatEntity rat, double speedModifier) {
            super(rat, speedModifier, true);
            this.rat = rat;
        }

        @Override
        public boolean canContinueToUse() {
            float brightness = this.rat.getBrightness();
            if (brightness >= MAX_ATTACK_BRIGHTNESS && this.rat.getRandom().nextInt(GIVE_UP_IN_LIGHT_CHANCE) == 0) {
                this.rat.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }
    }

    private static final class RatTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCRatEntity rat;

        RatTargetGoal(MoCRatEntity rat, Class<T> targetClass, boolean mustSee) {
            super(rat, targetClass, mustSee);
            this.rat = rat;
        }

        @Override
        public boolean canUse() {
            return this.rat.getBrightness() < MAX_ATTACK_BRIGHTNESS && super.canUse();
        }
    }

    /** No getBrightness() in 1.21.1 — same helper already used elsewhere. */
    private float getBrightness() {
        return this.level().getMaxLocalRawBrightness(this.blockPosition()) / 15.0F;
    }
}