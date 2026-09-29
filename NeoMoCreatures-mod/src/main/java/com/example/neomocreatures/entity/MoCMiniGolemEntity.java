package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.golem.GolemBlockPicker;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.common.CommonHooks;
/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityMiniGolem}. A small stone golem that
 * only goes looking for players and iron golems while standing in the dark; out in bright light it
 * may lose interest mid-fight. While fighting it tears a nearby block out of the ground, holds it
 * over its head and throws it (see GolemBlockPicker for which blocks are safe to lift).
 */
public class MoCMiniGolemEntity extends Monster implements RockThrower {

    private static final double MAX_HEALTH = 20.0D;
    private static final double ARMOR = 6.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double ATTACK_DAMAGE = 4.0D;
    private static final int EXPERIENCE = 5;

    private static final double ATTACK_SPEED_MODIFIER = 1.0D;
    private static final float LOOK_DISTANCE = 8.0F;
    /** Original: getBrightness() < 0.5F to hunt, >= 0.5F may give up. */
    private static final float DARKNESS_THRESHOLD = 0.5F;
    /** Original: 1-in-100 chance per tick to give up the chase while in bright light. */
    private static final int GIVE_UP_IN_LIGHT_CHANCE = 100;
    /** Original: getAttackReachSqr() = 4 + target width. */
    private static final double ATTACK_REACH_SQR_BASE = 4.0D;
    /** Original: 1-in-30 chance per tick to grab a rock while it has a target and empty hands. */
    private static final int GRAB_ROCK_CHANCE = 30;
    /** Original: searches a 3x3x3 cube around itself with 27 random tries. */
    private static final int GRAB_RADIUS = 1;
    private static final int GRAB_ATTEMPTS = 27;
    /** Original: holds the rock over its head for 50 ticks before throwing. */
    private static final int HOLD_TICKS_BEFORE_THROW = 50;
    /** Original: only throws at targets closer than 48 blocks, otherwise just drops the rock. */
    private static final double MAX_THROW_DISTANCE = 48.0D;
    /** Original: MoCTools.throwStone(..., speedMod 10, height 0.25). */
    private static final double THROW_SPEED_DIVISOR = 10.0D;
    private static final double THROW_ARC = 0.25D;
    /** Wiki: burns in direct sunlight; original MoCEntityMob uses setFire(8). */
    private static final float SUN_BURN_SECONDS = 8.0F;
    /** Height above its feet of the held rock's bottom face — shared with the render layer. */
    public static final double HELD_ROCK_HEIGHT = 1.25D;

    private static final String TAG_HELD_BLOCK = "HeldBlock";
    private static final String TAG_HELD_DROPS_LOOT = "HeldBlockDropsLoot";
    private static final String TAG_HOLD_TICKS = "HoldTicks";

    private static final EntityDataAccessor<Optional<BlockState>> DATA_HELD_BLOCK =
            SynchedEntityData.defineId(MoCMiniGolemEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);

    /** Server-only: true when the held block was really torn out of the world, so it must be given back. */
    private boolean heldBlockDropsLoot;
    private int holdTicks;

    public MoCMiniGolemEntity(EntityType<? extends MoCMiniGolemEntity> type, Level level) {
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
        this.goalSelector.addGoal(2, new GolemMeleeAttackGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DarknessTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new DarknessTargetGoal<>(this, IronGolem.class));
    }

    // ---------------------------------------------------------------------
    // Synced state
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HELD_BLOCK, Optional.empty());
    }

    /** True while a rock is lifted above its head — read by the model to raise both arms. */
    public boolean isHoldingRock() {
        return this.getHeldBlock().isPresent();
    }

    /** The block lifted above its head, if any — read by the held-rock render layer. */
    public Optional<BlockState> getHeldBlock() {
        return this.entityData.get(DATA_HELD_BLOCK);
    }

    private void setHeldBlock(@Nullable BlockState state) {
        this.entityData.set(DATA_HELD_BLOCK, Optional.ofNullable(state));
    }

    private boolean isInDarkness() {
        return this.getLightLevelDependentMagicValue() < DARKNESS_THRESHOLD;
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return this.distanceToSqr(target) <= ATTACK_REACH_SQR_BASE + target.getBbWidth();
    }

    // ---------------------------------------------------------------------
    // Rock throwing
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.isAlive() && this.isSunBurnTick()) {
            this.igniteForSeconds(SUN_BURN_SECONDS);
        }
        LivingEntity target = this.getTarget();
        if (this.isHoldingRock()) {
            this.tickHeldRock();
        } else if (target != null && this.canThrowRocksAt(target) && this.random.nextInt(GRAB_ROCK_CHANCE) == 0) {
            this.tryGrabRock();
        }
    }

    /** Original: acquireTRock() — lifts a nearby block over its head. */
    private void tryGrabRock() {
        GolemBlockPicker.findLiftableBlock(this, GRAB_RADIUS, GRAB_ATTEMPTS).ifPresent(pos -> {
            BlockState state = this.level().getBlockState(pos);
            boolean tearsOut = this.canTearOut(pos);
            if (tearsOut) {
                this.level().destroyBlock(pos, false, this);
            }
            this.setHeldBlock(state);
            this.heldBlockDropsLoot = tearsOut;
            this.holdTicks = 0;
        });
    }

    /** When it may not grief (gamerule or a protection mod), it lifts a copy instead: the world keeps the block, the copy never drops. */
    private boolean canTearOut(BlockPos pos) {
        return this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                && CommonHooks.canEntityDestroy(this.level(), pos, this);
    }

    /** Original: attackWithTRock() — stands still holding it, then throws it at the target or drops it if out of range. */
    private void tickHeldRock() {
        if (++this.holdTicks < HOLD_TICKS_BEFORE_THROW) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.canThrowRocksAt(target)) {
            this.throwHeldRock(target);
        } else {
            this.dropHeldRock();
        }
        this.clearHeldRock();
    }

    /** Wiki: no rock throwing while the golem or its target is in water; original: nothing past 48 blocks. */
    private boolean canThrowRocksAt(LivingEntity target) {
        return !this.isInWater() && !target.isInWater() && this.distanceTo(target) < MAX_THROW_DISTANCE;
    }

    private void throwHeldRock(LivingEntity target) {
        this.getHeldBlock().ifPresent(state -> {
            MoCThrowableRockEntity rock = new MoCThrowableRockEntity(this.level(), this, state, this.heldBlockDropsLoot);
            double startY = this.getY() + HELD_ROCK_HEIGHT;
            rock.setPos(this.getX(), startY, this.getZ());
            rock.setDeltaMovement(
                    (target.getX() - this.getX()) / THROW_SPEED_DIVISOR,
                    (target.getY() - startY) / THROW_SPEED_DIVISOR + THROW_ARC,
                    (target.getZ() - this.getZ()) / THROW_SPEED_DIVISOR);
            this.level().addFreshEntity(rock);
        });
    }

    /** Gives the torn-out block back as an item (copies lifted with griefing off give nothing). */
    private void dropHeldRock() {
        if (this.heldBlockDropsLoot) {
            this.getHeldBlock().ifPresent(state -> this.spawnAtLocation(new ItemStack(state.getBlock()), (float) HELD_ROCK_HEIGHT));
        }
    }

    private void clearHeldRock() {
        this.setHeldBlock(null);
        this.heldBlockDropsLoot = false;
        this.holdTicks = 0;
    }

    /** Original: onDeath() turned the held rock back into an item. */
    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide && this.isHoldingRock()) {
            this.dropHeldRock();
            this.clearHeldRock();
        }
        super.die(damageSource);
    }

    /** Never despawns while holding a real block, so it can't vanish with it. */
    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.heldBlockDropsLoot;
    }

    // ---------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.getHeldBlock().ifPresent(state -> {
            tag.put(TAG_HELD_BLOCK, NbtUtils.writeBlockState(state));
            tag.putBoolean(TAG_HELD_DROPS_LOOT, this.heldBlockDropsLoot);
            tag.putInt(TAG_HOLD_TICKS, this.holdTicks);
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_HELD_BLOCK, Tag.TAG_COMPOUND)) {
            BlockState state = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound(TAG_HELD_BLOCK));
            if (!state.isAir()) {
                this.setHeldBlock(state);
                this.heldBlockDropsLoot = tag.getBoolean(TAG_HELD_DROPS_LOOT);
                this.holdTicks = tag.getInt(TAG_HOLD_TICKS);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GOLEM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GOLEM_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.GOLEM_STEP.get(), 1.0F, 1.0F);
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-1 ancient silver scrap and 0-1 redstone, each +0-1 per Looting level. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        this.dropUpToOne(ModItems.ANCIENT_SILVER_SCRAP.get(), lootingLevel);
        this.dropUpToOne(Items.REDSTONE, lootingLevel);
    }

    private void dropUpToOne(Item item, int lootingLevel) {
        int count = this.random.nextInt(2);
        for (int i = 0; i < lootingLevel; i++) {
            count += this.random.nextInt(2);
        }
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(item, count));
        }
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }

    @Override
    protected int getBaseExperienceReward() {
        return EXPERIENCE;
    }

    // ---------------------------------------------------------------------
    // Goals
    // ---------------------------------------------------------------------

    /** Original: AIGolemAttack — out in bright light it has a small chance each tick to drop the chase. */
    private static final class GolemMeleeAttackGoal extends MeleeAttackGoal {
        private final MoCMiniGolemEntity golem;

        GolemMeleeAttackGoal(MoCMiniGolemEntity golem) {
            super(golem, ATTACK_SPEED_MODIFIER, true);
            this.golem = golem;
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.golem.isInDarkness() && this.golem.getRandom().nextInt(GIVE_UP_IN_LIGHT_CHANCE) == 0) {
                this.golem.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }

        /** Original: attackWithTRock() clears its path every tick — it stands still, facing its target, while holding a rock. */
        @Override
        public void tick() {
            if (this.golem.isHoldingRock()) {
                this.golem.getNavigation().stop();
                LivingEntity target = this.golem.getTarget();
                if (target != null) {
                    this.golem.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                return;
            }
            super.tick();
        }
    }

    /** Original: AIGolemTarget — only picks new targets while standing in the dark. */
    private static final class DarknessTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCMiniGolemEntity golem;

        DarknessTargetGoal(MoCMiniGolemEntity golem, Class<T> targetClass) {
            super(golem, targetClass, true);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return this.golem.isInDarkness() && super.canUse();
        }
    }
}