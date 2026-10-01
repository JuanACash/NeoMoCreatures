package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityWraith}. Flies like the insects (no
 * gravity, never falls, no fall damage); attacks the player and iron golems on sight, always. Undead
 * (immune to poison, heals from harm/hurt by healing); burns in direct sunlight like any undead.
 * A 5% chance to spawn as "Scratch", a named easter-egg variant with its own texture.
 */
public class MoCWraithEntity extends Monster {

    private static final double ATTACK_DAMAGE = 3.0D;
    private static final double MAX_HEALTH = 20.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double ATTACK_SPEED = 1.0D;
    private static final double WANDER_SPEED = 1.0D;
    private static final int ATTACK_ANIMATION_TICKS = 10;
    private static final float SUNBURN_DAMAGE = 2.0F;
    /** Original: a 5% chance, with an easter-egg config toggle this port always leaves on. */

    /** 0 = idle; counts up while the arm-swing animation plays. Read by the model/renderer. */
    public int attackCounter;

    public MoCWraithEntity(EntityType<? extends MoCWraithEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.FLYING_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, ATTACK_SPEED, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    /** Original: selectType() — a 5% chance for the "Scratch" easter egg. */
    public void rollScratchEasterEgg() {
        if (this.random.nextInt(100) < 5) {
            this.setCustomName(Component.literal("Scratch"));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.attackCounter > 0) {
            this.attackCounter += 2;
            if (this.attackCounter > ATTACK_ANIMATION_TICKS) {
                this.attackCounter = 0;
            }
        }
        if (!this.level().isClientSide && this.checkSunDamage()) {
            // Wiki: "they die in sunlight... but they don't get set on fire" — direct fire-type
            // damage instead of igniteForSeconds(): fire-type so a fire-immune Flame Wraith is
            // correctly exempt from it, but no actual fire texture/particles like real burning.
            this.hurt(this.damageSources().onFire(), SUNBURN_DAMAGE);
            if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        this.getX(), this.getY() + this.getBbHeight() * 0.5D, this.getZ(),
                        4, 0.2D, 0.3D, 0.2D, 0.02D);
            }
        }
    }

    protected boolean checkSunDamage() {
        if (!this.level().isDay()) {
            return false;
        }
        float brightness = this.getLightLevelDependentMagicValue();
        BlockPos pos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        return brightness > 0.5F
                && this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && this.level().canSeeSky(pos);
    }

    /** Original: startArmSwingAttack() — plays on every hit. */
    @Override
    public boolean doHurtTarget(Entity target) {
        this.attackCounter = 1;
        return super.doHurtTarget(target);
    }

    /** Undead-style resistances, same as the Silver Skeleton. */
    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.MobEffectInstance effect) {
        return effect.getEffect() != net.minecraft.world.effect.MobEffects.POISON.value() && super.canBeAffected(effect);
    }

    /** Original: never takes fall damage — matches its flying ability. */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
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
        return com.example.neomocreatures.init.ModSounds.WRAITH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.WRAITH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.WRAITH_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-2 gunpowder, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        int count = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(Items.GUNPOWDER, count));
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 5;
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
                                        net.minecraft.world.DifficultyInstance difficulty,
                                        net.minecraft.world.entity.MobSpawnType spawnType,
                                        @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        this.rollScratchEasterEgg();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }
}