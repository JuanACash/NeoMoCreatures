package com.example.neomocreatures.entity;

import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityMole}. Fully passive, but with a real
 * defensive state machine: spotting a nearby creature bigger than half a block makes it dive
 * underground (invisible, invulnerable, unpushable, silent, and untargetable while buried), then it
 * peeks back out before fully resurfacing.
 * <p>
 * The original's own tailored attributes (FOLLOW_RANGE 12, MAX_HEALTH 6, MOVEMENT_SPEED 0.2, defined
 * in a {@code registerAttributes()} method) were never actually wired into its entity registration
 * — the same real oversight already found on the Mouse. This port uses the tailored values.
 */
public class MoCMoleEntity extends TamableAnimal {

    /** Deviates from the original's own random chance for these two transitions: holds the tilted
     *  pose for a fixed 2 seconds before actually vanishing/reappearing, instead of a coin flip that
     *  could resolve almost instantly. */
    private static final int TRANSITION_TICKS = 40;
    private int transitionTimer;
    private static final double FOLLOW_RANGE = 12.0D;
    private static final double MAX_HEALTH = 6.0D;
    private static final double MOVEMENT_SPEED = 0.2D;
    private static final double WANDER_SPEED = 1.0D;

    /** 0 = normal (visible, walking around); 1 = diving down (visible briefly, tilted nose-down);
     *  2 = fully buried (invisible, invulnerable, silent, no collision, untargetable); 3 = peeking
     *  back out (visible, tilted nose-up) before returning to 0 or ducking back to 2. */
    private static final int STATE_NORMAL = 0;
    private static final int STATE_DIVING = 1;
    private static final int STATE_HIDDEN = 2;
    private static final int STATE_PEEKING = 3;

    private static final double BOOGEY_SEARCH_RADIUS = 4.0D;
    private static final float BOOGEY_MIN_SIZE = 0.5F;

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(MoCMoleEntity.class, EntityDataSerializers.INT);

    public MoCMoleEntity(EntityType<? extends MoCMoleEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_NORMAL);
    }

    public int getMoleState() {
        return this.entityData.get(DATA_STATE);
    }


    private void setMoleState(int state) {
        this.entityData.set(DATA_STATE, state);
        // Only fully hidden (buried) makes it invisible — diving and peeking must stay visible so
        // their tilt animation is actually seen.
        this.setInvisible(state == STATE_HIDDEN);
    }

    /** Original: pitchRotationOffset() — the whole body tilts while diving/peeking. Degrees. */
    public float getTiltDegrees() {
        return switch (this.getMoleState()) {
            case STATE_DIVING -> 60.0F;
            case STATE_PEEKING -> -45.0F;
            default -> 0.0F;
        };
    }
    /** Original: getAdjustedYOffset() — sinks into the ground while diving/hidden. */
    public float getSinkOffset() {
        return switch (this.getMoleState()) {
            case STATE_DIVING -> 0.3F;
            case STATE_HIDDEN -> 1.0F;
            case STATE_PEEKING -> 0.1F;
            default -> 0.0F;
        };
    }

    public boolean isOnDirt() {
        BlockPos pos = BlockPos.containing(this.getX(), this.getBoundingBox().minY - 0.5D, this.getZ());
        Block block = this.level().getBlockState(pos).getBlock();
        return isDiggableBlock(block);
    }

    /** Wiki: coarse dirt, dirt, grass block, gravel, mycelium, podzol, red sand, sand — explicitly
     *  NOT farmland, soul sand, or grass paths, since those are 15/16 of a block tall or less. Moss
     *  block added on request, for the Lush Caves spawn. */
    private static boolean isDiggableBlock(Block block) {
        return block == Blocks.COARSE_DIRT || block == Blocks.DIRT || block == Blocks.GRASS_BLOCK
                || block == Blocks.GRAVEL || block == Blocks.MYCELIUM || block == Blocks.PODZOL
                || block == Blocks.RED_SAND || block == Blocks.SAND || block == Blocks.MOSS_BLOCK;
    }


    /** Wiki: "particles of that respective block can be seen" while digging. */
    private void spawnDiggingParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = BlockPos.containing(this.getX(), this.getBoundingBox().minY - 0.5D, this.getZ());
        BlockState state = this.level().getBlockState(pos);
        serverLevel.sendParticles(new net.minecraft.core.particles.BlockParticleOption(
                        net.minecraft.core.particles.ParticleTypes.BLOCK, state),
                this.getX(), this.getY() + 0.2D, this.getZ(), 8, 0.2D, 0.1D, 0.2D, 0.0D);
    }

    /** Original: getBoogey() — nearest living, non-Mole creature at least half a block wide/tall. */
    @Nullable
    private LivingEntity findNearbyThreat() {
        AABB area = this.getBoundingBox().inflate(BOOGEY_SEARCH_RADIUS, 4.0D, BOOGEY_SEARCH_RADIUS);
        for (LivingEntity candidate : this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e.getClass() != MoCMoleEntity.class && (e.getBbWidth() >= BOOGEY_MIN_SIZE || e.getBbHeight() >= BOOGEY_MIN_SIZE))) {
            return candidate;
        }
        return null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        // Not from the original source (checked both real jars, neither has this) — added on
        // request: a steady trickle of dirt particles marking where it's buried.
        if (this.getMoleState() == STATE_HIDDEN && this.tickCount % 10 == 0) {
            this.spawnDiggingParticles();
        }
        int state = this.getMoleState();

        // DIVING and PEEKING are timed poses: hold for a fixed 2 seconds so the tilt is clearly
        // visible, then complete the transition — not a random chance that could resolve instantly.
        if (state == STATE_DIVING || state == STATE_PEEKING) {
            this.getNavigation().stop();
            if (++this.transitionTimer >= TRANSITION_TICKS) {
                this.transitionTimer = 0;
                this.setMoleState(state == STATE_DIVING ? STATE_HIDDEN : STATE_NORMAL);
            }
        }
        if (state == STATE_NORMAL && this.isOnDirt()) {
            LivingEntity threat = this.findNearbyThreat();
            if (threat != null && this.hasLineOfSight(threat)) {
                this.transitionTimer = 0;
                this.setMoleState(STATE_DIVING);
            }
        }
        if (state == STATE_HIDDEN && this.random.nextInt(20) == 0 && this.findNearbyThreat() == null) {
            this.transitionTimer = 0;
            this.setMoleState(STATE_PEEKING);
        }
        if (this.getMoleState() != STATE_NORMAL && !this.isOnDirt()) {
            this.transitionTimer = 0;
            this.setMoleState(STATE_NORMAL);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.getMoleState() == STATE_HIDDEN) {
            return false;
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean isPushable() {
        return this.getMoleState() != STATE_HIDDEN && super.isPushable();
    }

    @Override
    public boolean isPickable() {
        return this.getMoleState() != STATE_HIDDEN && super.isPickable();
    }

    @Override
    public boolean isSilent() {
        return this.getMoleState() == STATE_HIDDEN || super.isSilent();
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Sounds — reuses vanilla's own Rabbit sounds; no ambient sound at all.
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-1 fur, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.FUR.get(), MoCLootUtil.rollWithLootingBonus(this.random, 2, lootingLevel));
    }


    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }
}