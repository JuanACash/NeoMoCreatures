package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityOgre}. Shared behaviour of the 3
 * species (Green/Fire/Cave), each its own {@link EntityType} with its own stats, texture, spawn
 * biomes and drops, exactly like the original's own class hierarchy.
 * <p>
 * Step 1 (this file, plus the 3 subclasses): stats, darkness-gated targeting, sounds. Not yet
 * ported: the area block-destroying blast attack (pending a decision on respecting mobGriefing),
 * natural spawn placement, and the original's second "two-headed, hammer-wielding" model variant
 * (the model shipped here only covers its single-headed "type 1" form).
 */
public abstract class MoCOgreEntity extends Monster {

    // ---- Attack animation, read by the model ----
    /** 0 = idle, counts up while an arm-swing attack is playing. */
    public int attackCounter;
    /** 0 = none, 1 = left arm, 2 = right arm, 3 = both — which arm the model should swing. */
    public int armToAnimate;
    /** 0 = idle, counts up during the ground-smash wind-up (both arms raised) before it lands. */
    private int smashCounter;
    /** Client-side only, matching the original: which of the two heads (2 or 3) is currently
     *  tracking the player, re-rolled every so often. Only meaningful for two-headed ogres. */
    private int movingHead;

    private static final EntityDataAccessor<Integer> DATA_OGRE_TYPE =
            SynchedEntityData.defineId(MoCOgreEntity.class, EntityDataSerializers.INT);

    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double KNOCKBACK_RESISTANCE = 1.0D;

    private static final double ATTACK_SPEED = 1.25D;

    // ---- Attack animation timing ----
    /** Original: attackCounter advances by 2 per tick (1 if swinging both arms) and resets past 10. */
    private static final int ATTACK_ANIMATION_END = 10;

    // ---- Block-destroying smash attack ----
    /** Wiki: "when they notice the player from a 12 block radius" — same range as the target goals'
     *  own FOLLOW_RANGE default, so no separate constant needed for that part. */
    private static final int SMASH_TRIGGER_CHANCE = 40;
    private static final int SMASH_WINDUP_TICKS = 10;
    private static final float MAX_DESTRUCTIBLE_RESISTANCE = 30.0F;

    protected MoCOgreEntity(EntityType<? extends MoCOgreEntity> type, Level level) {
        super(type, level);
    }

    /** The texture file name under {@code textures/entity/moc_ogre/}, without the extension. */
    public abstract String getTextureName();

        /** Wiki: 2.5 for green, 2.0 for fire, 3.0 for cave. */
    public abstract double getDestroyRadius();

    /** Wiki: only the fire ogre ignites the ground it breaks. */
    public boolean isFireStarter() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OGRE_TYPE, 0);
    }

    /** Original: selectType() — 1 (single head, no weapon) or 2 (two heads, wields a hammer),
     *  50/50, rolled once per individual and read by the model. Not tied to species at all. */
    public int getOgreType() {
        return this.entityData.get(DATA_OGRE_TYPE);
    }

    private void selectType() {
        if (this.getOgreType() == 0) {
            this.entityData.set(DATA_OGRE_TYPE, this.random.nextInt(2) + 1);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.selectType();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /** Original: getMovingHead() — for a two-headed ogre, which head (2 or 3) currently tracks the
     *  player, switching every ~60 ticks on average; purely a client-visual detail (called from the
     *  renderer), so it isn't synced. */
    public int getMovingHead() {
        if (this.getOgreType() == 1) {
            return 1;
        }
        if (this.random.nextInt(60) == 0) {
            this.movingHead = this.random.nextInt(2) + 2;
        }
        return this.movingHead;
    }

    /** No getBrightness() in 1.21.1 — the raw light level at its feet, normalized to 0-1. */
    protected float getBrightness() {
        return this.level().getMaxLocalRawBrightness(this.blockPosition()) / 15.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, ATTACK_SPEED, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Wiki: neutral toward players — only fights back once hit, never attacks on sight.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    /** Original: fights back only above Peaceful, and never against whatever is riding it. */
    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!super.hurt(damageSource, amount)) {
            return false;
        }
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            return true;
        }
        if (damageSource.getEntity() instanceof LivingEntity attacker && attacker != this && !this.hasPassenger(attacker)) {
            this.setTarget(attacker);
        }
        return true;
    }

    // ---------------------------------------------------------------------
    // Attack animation
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackCounter > 0) {
            this.attackCounter += this.armToAnimate == 3 ? 1 : 2;
            if (this.attackCounter > ATTACK_ANIMATION_END) {
                this.attackCounter = 0;
                this.armToAnimate = 0;
            }
        }
        if (!this.level().isClientSide) {
            this.tickSmashAttack();
        }
    }

    /** Wiki: raises its club/hammer and smashes the ground on noticing the player, destroying
     *  blocks around it (never under it) and hurting nearby living things — gated by mobGriefing,
     *  same as big/mini golems. */
    private void tickSmashAttack() {
        if (this.smashCounter > 0) {
            if (++this.smashCounter > SMASH_WINDUP_TICKS) {
                this.smashCounter = 0;
                this.performGroundSmash();
            }
            return;
        }
        if (this.getTarget() != null && this.attackCounter == 0 && this.random.nextInt(SMASH_TRIGGER_CHANCE) == 0) {
            this.smashCounter = 1;
            this.armToAnimate = 3;
        }
    }

    private void performGroundSmash() {
        if (this.isDeadOrDying()) {
            return;
        }
        double radius = this.getDestroyRadius();
        AABB area = this.getBoundingBox().inflate(radius, radius, radius);
        for (LivingEntity nearby : this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this)) {
            if (this.distanceToSqr(nearby) <= radius * radius) {
                nearby.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        BlockPos center = this.blockPosition();
        int intRadius = (int) Math.ceil(radius);
        // Only in front of it, roughly at standing height — not a sphere that also digs down or
        // reaches behind it.
        Vec3 forward = Vec3.directionFromRotation(0F, this.getYRot());
        // Original centers its blast at posY + 1 (roughly chest height), never at the ogre's own
        // feet — this range starts at feet level and goes up, so it can never dig into the floor.
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-intRadius, 0, -intRadius), center.offset(intRadius, 2, intRadius))) {
            if (pos.equals(center) || pos.distSqr(center) > radius * radius) {
                continue;
            }
            double dot = (pos.getX() + 0.5D - this.getX()) * forward.x + (pos.getZ() + 0.5D - this.getZ()) * forward.z;
            if (dot <= 0.0D) {
                continue;
            }
            BlockState state = this.level().getBlockState(pos);
            if (state.isAir() || state.getBlock().getExplosionResistance() > MAX_DESTRUCTIBLE_RESISTANCE) {
                continue;
            }
            this.level().destroyBlock(pos, true, this);
            if (this.isFireStarter() && this.random.nextInt(3) == 0) {
                BlockPos above = pos.above();
                if (this.level().getBlockState(above).isAir()) {
                    this.level().setBlockAndUpdate(above, Blocks.FIRE.defaultBlockState());
                }
            }
        }
    }

    /** Original: startArmSwingAttack() — single-headed ogres always swing their right arm (they
     *  have no weapon to hold in the other); two-headed ones pick either arm at random. */
    @Override
    public boolean doHurtTarget(Entity target) {
        this.attackCounter = 1;
        this.armToAnimate = (this.getOgreType() == 2 && this.random.nextInt(2) == 0) ? 1 : 2;
        return super.doHurtTarget(target);
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.OGRE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.OGRE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.OGRE_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Wiki/original loot tables give 1-3 experience for every ogre kill regardless of species. */
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    // ---------------------------------------------------------------------
    // Darkness-gated goals
    // ---------------------------------------------------------------------

}