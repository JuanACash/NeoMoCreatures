package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bunny.BunnyVariant;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityBunny}: a small,
 * skittish (while wild) creature with 5 colour variants ({@link BunnyVariant}),
 * immune to fall damage, that grows from baby to adult over time (wiki:
 * "baby rabbits take 3-5 days to mature").
 * <p>
 * Step 1 only: skeleton, variants, growth, and basic passive wander/flee
 * behaviour. Intentionally NOT implemented yet (step 2): the instant-tame
 * pickup interaction (rides the player's head), the golden-carrot eat/heal/
 * breed-readiness mechanic, the automatic proximity-based reproduction, the
 * jump-hop movement quirk, and the carry lift/land sounds.
 * <p>
 * Extends {@link TamableAnimal} for the same reason as {@code MoCSnakeEntity}:
 * step 2's taming doesn't require re-registering the entity type. {@link #isFood}
 * always returns false — taming happens by picking the bunny up, not feeding it.
 */
public class MoCBunnyEntity extends TamableAnimal {

    /** Wiki: "Baby rabbits take 3-5 days to mature" — using 4 in-game days as a fixed middle value. */
    private static final int GROWTH_TICKS = 96000;
    /** Fraction of adult size a freshly-spawned baby starts at. */
    private static final float BABY_SCALE = 0.5F;
    /** Below this, a scale change is float noise from the per-tick age increment, not worth a refreshDimensions() call. */
    private static final float SCALE_CHANGE_THRESHOLD = 0.01F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBunnyEntity.class, EntityDataSerializers.INT);

    private float lastAppliedScale = -1F;
    /** Ticks until the next hop while moving — original's jumpTimer. */
    private int jumpTimer;

    public MoCBunnyEntity(EntityType<? extends MoCBunnyEntity> type, Level level) {
        super(type, level);
        // Wiki: "usually stay out of water" — strongly discourages pathing through it.
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
        // Original's isNotScared() = isTamed() — wild bunnies flee, tamed ones don't.
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D,
                livingEntity -> !this.isTame()));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BunnyVariant.GOLDEN.getId());
    }

    public BunnyVariant getVariant() {
        return BunnyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(BunnyVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        // Wiki / original: bunnies are immune to fall damage.
        return false;
    }


    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickHop();
        }
    }

    /** Wiki: "They hop around aimlessly instead of walking" — original's jumpTimer logic. */
    private void tickHop() {
        net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
        boolean moving = motion.x > 0.05D || motion.z > 0.05D || motion.x < -0.05D || motion.z < -0.05D;
        if (--this.jumpTimer <= 0 && this.onGround() && moving) {
            this.setDeltaMovement(motion.x, 0.3D, motion.z);
            this.jumpTimer = 15;
        }
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = getGrowthFraction();
            if (Math.abs((float) scaleAttr.getBaseValue() - target) > SCALE_CHANGE_THRESHOLD) {
                scaleAttr.setBaseValue(target);
            }
        }
        float current = (float) scaleAttr.getValue();
        if (Math.abs(this.lastAppliedScale - current) > SCALE_CHANGE_THRESHOLD) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Taming happens by picking the bunny up (step 2), not by feeding it.
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // Reproduction is the original's custom proximity/timer system
        // (step 2), not vanilla love-mode breeding.
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BUNNY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BUNNY_DEATH.get();
    }
}