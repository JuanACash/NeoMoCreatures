package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Port of {@code drzhark.mocreatures.entity.neutral.MoCEntityBoar}. Neutral: never starts a fight
 * (no target-selector goal at all), but an adult that gets hit fights back; a baby only ever flees,
 * never retaliates. Spawns already as an adult (75% of the time) or a small baby (25%) — there is no
 * breeding goal in the original, so this doesn't breed either.
 */
public class MoCBoarEntity extends Animal {

    private static final float MAX_HEALTH = 10.0F;
    private static final float ATTACK_DAMAGE = 2.5F;
    private static final double MOVEMENT_SPEED = 0.3D;
    private static final double ATTACK_SPEED = 1.0D;
    private static final double FLEE_SPEED = 1.0D;
    private static final float FLEE_DISTANCE = 4.0F;
    private static final double FOLLOW_ADULT_SPEED = 1.0D;
    private static final double WANDER_SPEED = 1.0D;

     /** Hit-and-run only when the boar started the fight itself: hits once, backs off for a bit,
     *  then closes in again — like a baby Hoglin. If the player hit it first, it just fights
     *  normally instead (see isRetaliating()). */
    private static final double HIT_AND_RUN_RANGE = 2.2D;
    private static final int HIT_AND_RUN_FLEE_TICKS = 40;
    private static final double HIT_AND_RUN_FLEE_DISTANCE = 5.0D;

    /** Original: setMoCAge(60) at spawn — a baby grows from here up to full size over time. */
    private static final int BABY_START_AGE = 60;
    private static final int GROWN_AGE = 100;

    /** Wiki: attacks if the player gets too close, even unprovoked. */
    private static final double PLAYER_PROXIMITY = 6.0D;
    /** Wiki: "occasionally" attacks a few specific small mobs — a long recheck interval is what
     *  makes this occasional rather than constant. */
    private static final int SMALL_MOB_RECHECK_TICKS = 200;

    /** Original: a baby grows one step with a 1-in-300 chance per tick (about 10 minutes from 60 to 100). */
    private static final int GROWTH_CHANCE = 300;
    private static final String TAG_MOC_AGE = "MocAge";
    /** Synced so clients render babies at their real size (it used to be a server-only field). */
    private static final EntityDataAccessor<Integer> DATA_MOC_AGE =
            SynchedEntityData.defineId(MoCBoarEntity.class, EntityDataSerializers.INT);
    /** True once a player's hit sets its target — as long as this is true, it fights normally
     *  instead of the hit-and-run pattern; cleared once it no longer has a target at all. */
    private boolean retaliating;

    public MoCBoarEntity(EntityType<? extends MoCBoarEntity> type, Level level) {
        super(type, level);
        // Original: a straight 75%/25% roll, independent of any breeding — it's simply how it spawns.
        boolean adult = this.random.nextInt(4) != 0;
        if (!adult && !level.isClientSide) {
            this.setMocAge(BABY_START_AGE);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                // Wiki: attacks if you simply get too close — a short detection range is what
                // makes that "too close" rather than "sees you from anywhere".
                .add(Attributes.FOLLOW_RANGE, 8.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Original: EntityAIFleeFromPlayer, gated by isNotScared() — only a non-adult ever flees.
        this.goalSelector.addGoal(2, new BoarFleeGoal(this));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, FOLLOW_ADULT_SPEED));
        this.goalSelector.addGoal(4, new RetaliateAttackGoal(this, ATTACK_SPEED));
        this.goalSelector.addGoal(4, new BoarHitAndRunGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Wiki (piglets never attack): only an adult chases the player when provoked or approached,
        // and occasionally goes after a few specific small mobs — not in the decompiled source,
        // which had no target-selector goal at all.
        this.targetSelector.addGoal(2, new AdultOnlyTargetGoal<>(this, Player.class, 10, false));
        this.targetSelector.addGoal(3, new AdultOnlyTargetGoal<>(this, MoCFoxEntity.class, SMALL_MOB_RECHECK_TICKS, true));
        this.targetSelector.addGoal(3, new AdultOnlyTargetGoal<>(this, MoCRaccoonEntity.class, SMALL_MOB_RECHECK_TICKS, true));
        this.targetSelector.addGoal(3, new AdultOnlyTargetGoal<>(this, MoCTurkeyEntity.class, SMALL_MOB_RECHECK_TICKS, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MOC_AGE, GROWN_AGE);
    }

    public int getMocAge() {
        return this.entityData.get(DATA_MOC_AGE);
    }

    private void setMocAge(int age) {
        this.entityData.set(DATA_MOC_AGE, Math.min(age, GROWN_AGE));
    }

    public boolean isGrownAdult() {
        return this.getMocAge() >= GROWN_AGE;
    }

    @Override
    public boolean isBaby() {
        return !this.isGrownAdult();
    }

    @Override
    public void setBaby(boolean baby) {
        this.setMocAge(baby ? BABY_START_AGE : GROWN_AGE);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && !this.isGrownAdult() && this.random.nextInt(GROWTH_CHANCE) == 0) {
            this.setMocAge(this.getMocAge() + 1);
        }
    }

    @Override
    public float getAgeScale() {
        return this.isGrownAdult() ? 1.0F : this.getMocAge() * 0.01F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_MOC_AGE, this.getMocAge());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Boars saved before this fix have no age stored; they keep whatever they rolled when created.
        if (tag.contains(TAG_MOC_AGE, Tag.TAG_INT)) {
            this.setMocAge(tag.getInt(TAG_MOC_AGE));
        }
    }

    /** Original: attackEntityFrom() — an adult fights back against whoever hit it (unless it's
     *  something riding it); a baby never does, it can only flee. */
    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!super.hurt(damageSource, amount)) {
            return false;
        }
        Entity attacker = damageSource.getEntity();
        if (attacker != null && this.hasPassenger(attacker)) {
            return true;
        }
        if (attacker instanceof LivingEntity livingAttacker && attacker != this && this.isGrownAdult()) {
            this.setTarget(livingAttacker);
            this.retaliating = true;
        }
        return true;
    }

    /** Only true once a player's hit set this target — clears itself once the target is gone. */
    public boolean isRetaliating() {
        if (this.getTarget() == null) {
            this.retaliating = false;
        }
        return this.retaliating;
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // no breeding goal in the original
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Sounds — reuses vanilla's own Pig sounds, no custom Boar sound files.
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Wiki: 0-2 porkchop (raw, but cooked automatically if it died on fire — same as vanilla's own
     *  pig) + 0-2 hide, both scaling with Looting independently — piglets drop the same as adults. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, MoCLootUtil.rawOrCooked(this, Items.PORKCHOP, Items.COOKED_PORKCHOP),
                MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
        MoCLootUtil.dropItems(this, ModItems.HIDE.get(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }


    
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    /** Original: EntityAIFleeFromPlayer gated by isNotScared() (true only when grown up) — so this
     *  only ever fires for a baby. */
    private static final class BoarFleeGoal extends AvoidEntityGoal<Player> {
        private final MoCBoarEntity boar;

        BoarFleeGoal(MoCBoarEntity boar) {
            super(boar, Player.class, FLEE_DISTANCE, FLEE_SPEED, FLEE_SPEED);
            this.boar = boar;
        }

        @Override
        public boolean canUse() {
            return !this.boar.isGrownAdult() && super.canUse();
        }
    }

    /** Fights normally, no fleeing — only while isRetaliating() is true (the player hit it first). */
    private static final class RetaliateAttackGoal extends MeleeAttackGoal {
        private final MoCBoarEntity boar;

        RetaliateAttackGoal(MoCBoarEntity boar, double speedModifier) {
            super(boar, speedModifier, false);
            this.boar = boar;
        }

        @Override
        public boolean canUse() {
            return this.boar.isRetaliating() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.boar.isRetaliating() && super.canContinueToUse();
        }
    }

    /** The boar's own unprovoked aggression (proximity, or going after a small mob): closes in,
     *  lands one hit, then backs off for HIT_AND_RUN_FLEE_TICKS before closing in again. */
    private static final class BoarHitAndRunGoal extends Goal {
        private final MoCBoarEntity boar;
        private int fleeTicksRemaining;

        BoarHitAndRunGoal(MoCBoarEntity boar) {
            this.boar = boar;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.boar.getTarget();
            return target != null && target.isAlive() && !this.boar.isRetaliating();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            this.fleeTicksRemaining = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.boar.getTarget();
            this.boar.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.fleeTicksRemaining > 0) {
                this.fleeTicksRemaining--;
                Vec3 away = this.boar.position().subtract(target.position());
                if (away.lengthSqr() > 1.0E-4) {
                    away = away.normalize().scale(HIT_AND_RUN_FLEE_DISTANCE);
                    Vec3 fleeTo = this.boar.position().add(away);
                    this.boar.getNavigation().moveTo(fleeTo.x, fleeTo.y, fleeTo.z, ATTACK_SPEED);
                }
                return;
            }

            double distSqr = this.boar.distanceToSqr(target);
            if (distSqr <= HIT_AND_RUN_RANGE * HIT_AND_RUN_RANGE) {
                this.boar.doHurtTarget(target);
                this.fleeTicksRemaining = HIT_AND_RUN_FLEE_TICKS;
            } else {
                this.boar.getNavigation().moveTo(target, ATTACK_SPEED);
            }
        }
    }

    /** Wiki: "Piglets do not attack the player" — gates any target-selector goal to adults only. */
    private static final class AdultOnlyTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCBoarEntity boar;

        AdultOnlyTargetGoal(MoCBoarEntity boar, Class<T> targetClass, int recheckTicks, boolean mustSee) {
            super(boar, targetClass, recheckTicks, mustSee, false, null);
            this.boar = boar;
        }

        @Override
        public boolean canUse() {
            return this.boar.isGrownAdult() && super.canUse();
        }
    }   

}