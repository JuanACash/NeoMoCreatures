package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.monster.MoCHorseMobEntity;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntitySilverSkeleton}. Only actively hunts a
 * player/iron golem while it's dark (brightness below 0.5, giving up with a small chance once it's
 * bright); always fights back if hit. Swings its left or right arm at random on each hit, each with
 * its own short animation timer.
 */
public class MoCSilverSkeletonEntity extends Monster {

    private static final double MAX_HEALTH = 25.0D;
    private static final double ARMOR = 11.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double ATTACK_DAMAGE = 7.0D;
    private static final double ATTACK_SPEED = 1.0D;

    private static final int ATTACK_ANIMATION_TICKS = 10;

    /** Wiki: "significantly increase" its speed once it notices the player — matches the sprint
     *  animation the model already reads via isSprinting(). */
    private static final net.minecraft.resources.ResourceLocation AGGRO_SPEED_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "silver_skeleton_aggro_speed");
    private static final AttributeModifier AGGRO_SPEED_MODIFIER = new AttributeModifier(
            AGGRO_SPEED_MODIFIER_ID, 0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    /** Wiki: mounts any nearby Manticore, Wild Wolf, Scorpion or Horse Mob, like a zombie/skeleton
     *  jockey. */
    private static final int JOCKEY_CHANCE = 100;
    private static final double JOCKEY_SEARCH_RADIUS_XZ = 4.0D;
    private static final double JOCKEY_SEARCH_RADIUS_Y = 2.0D;

    /** 0 = idle; counts up while that arm's swing animation plays. Read by the model/renderer. */
    public int attackCounterLeft;
    public int attackCounterRight;

    public MoCSilverSkeletonEntity(EntityType<? extends MoCSilverSkeletonEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SkeletonAttackGoal(this, ATTACK_SPEED));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new SkeletonTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new SkeletonTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackCounterLeft > 0 && ++this.attackCounterLeft > ATTACK_ANIMATION_TICKS) {
            this.attackCounterLeft = 0;
        }
        if (this.attackCounterRight > 0 && ++this.attackCounterRight > ATTACK_ANIMATION_TICKS) {
            this.attackCounterRight = 0;
        }
        if (!this.level().isClientSide) {
            this.updateAggroSpeed();
            this.tickJockey();
            if (this.isSunBurnTick()) {
                this.igniteForSeconds(8);
            }
        }
    }

    private void updateAggroSpeed() {
        boolean aggro = this.getTarget() != null;
        this.setSprinting(aggro);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        boolean has = speed.hasModifier(AGGRO_SPEED_MODIFIER_ID);
        if (aggro && !has) {
            speed.addTransientModifier(AGGRO_SPEED_MODIFIER);
        } else if (!aggro && has) {
            speed.removeModifier(AGGRO_SPEED_MODIFIER_ID);
        }
    }

    /** Wiki: mounts a nearby Manticore, Wild Wolf, Scorpion or Horse Mob, jockey-style. */
    private void tickJockey() {
        if (this.isVehicle() || this.getVehicle() != null || this.random.nextInt(JOCKEY_CHANCE) != 0) {
            return;
        }
        AABB area = this.getBoundingBox().inflate(JOCKEY_SEARCH_RADIUS_XZ, JOCKEY_SEARCH_RADIUS_Y, JOCKEY_SEARCH_RADIUS_XZ);
        for (LivingEntity mount : this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getVehicle() == null && (e instanceof MoCManticoreEntity || e instanceof MoCWildWolfEntity
                        || e instanceof MoCScorpionEntity || e instanceof MoCHorseMobEntity))) {
            this.startRiding(mount);
            break;
        }
    }

    /** Wiki: burns at dawn once the sun is high enough, unless in shade or water — same rule vanilla
     *  applies to its own Skeleton/Zombie. */
    @Override
    public boolean isSunBurnTick() {
        if (!this.level().isDay()) {
            return false;
        }
        float brightness = this.getLightLevelDependentMagicValue();
        BlockPos pos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        boolean wet = this.isInWaterRainOrBubble();
        return !wet && brightness > 0.5F
                && this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && this.level().canSeeSky(pos);
    }

    /** Original: startAttackAnimation() — a random arm on every hit. */
    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.random.nextInt(2) == 0) {
            this.attackCounterLeft = 1;
        } else {
            this.attackCounterRight = 1;
        }
        return super.doHurtTarget(target);
    }

    /** Replaces the old MobType.UNDEAD (removed in 1.21.1): heals from harm, is hurt by healing,
     *  same as vanilla's own Skeleton. */
    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.POISON.value() && super.canBeAffected(effect);
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    // ---------------------------------------------------------------------
    // Sounds — reuses vanilla's own Skeleton sounds.
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-2 ancient silver scrap + 0-2 bone (independent rolls, both scaling with
     *  Looting); plus a rare (1.5%+1%*Looting, player-kill only) silver sword. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        int scrap = this.random.nextInt(3) - 1 + this.random.nextInt(lootingLevel + 1);
        if (scrap > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.ANCIENT_SILVER_SCRAP.get(), scrap));
        }
        int bone = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (bone > 0) {
            this.spawnAtLocation(new ItemStack(Items.BONE, bone));
        }
        if (damageSource.getEntity() instanceof Player
                && this.random.nextFloat() < 0.015F + 0.01F * lootingLevel) {
            this.spawnAtLocation(new ItemStack(ModItems.SILVER_SWORD.get()));
        }
    }

    // ---------------------------------------------------------------------
    // Darkness-gated goals
    // ---------------------------------------------------------------------

    private static final class SkeletonAttackGoal extends MeleeAttackGoal {
        SkeletonAttackGoal(MoCSilverSkeletonEntity skeleton, double speedModifier) {
            super(skeleton, speedModifier, true);
        }
    }

    private static final class SkeletonTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        SkeletonTargetGoal(MoCSilverSkeletonEntity skeleton, Class<T> targetClass, boolean mustSee) {
            super(skeleton, targetClass, mustSee);
        }
    }
}