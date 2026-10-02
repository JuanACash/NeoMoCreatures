package com.example.neomocreatures.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.entity.snake.SnakeVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetCarryUtil;
import com.example.neomocreatures.util.PetStorageUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.hunter.MoCEntitySnake}: a
 * semi-aquatic snake with the 8 colour variants ({@link SnakeVariant}),
 * uniformly random on spawn; passive wandering plus hiss/confront/pissed/
 * bite/venom/rattle behaviour for bold, wild variants; taming via egg
 * hatching (wild snakes can't be tamed directly), growth to the variant's
 * adult size over time, healing with raw rat, and the "carried on the
 * player's shoulders" pickup interaction — same {@link CarriedPet} system
 * already used by {@code MoCKittyEntity} (Minecraft's Player has no
 * "passenger" attachment point defined, so real entity-riding just leaves
 * the rider at the player's feet; Kitty's system sidesteps that entirely by
 * just repositioning the real entity next to the player's head every tick).
 * Intentionally NOT implemented yet: biome-weighted variant selection and
 * natural egg drops on death.
 * <p>
 * Extends {@link TamableAnimal}, matching {@code MoCKomodoDragonEntity} and
 * {@code MoCScorpionEntity}. {@link #isFood} always returns false — taming
 * happens via {@link #onHatchedFromEgg}, not feeding.
 */
public class MoCSnakeEntity extends TamableAnimal
        implements EggHatchable, CarriedPet, GrowthScaled, StorablePet {

    private static final double NEAR_PLAYER_RANGE = 5.0D;
    private static final double SEARCH_RADIUS = 12.0D;
    // Original's exact tick constants: hiss sound every 25 ticks, mouth
    // closes every 35, pissed past 100 hiss-ticks at a 1-in-50 roll, and the
    // whole counter resets past 500 as a safety net.
    private static final int HISS_SOUND_INTERVAL = 25;
    private static final int MOUTH_CLOSE_INTERVAL = 35;
    private static final int PISSED_THRESHOLD = 100;
    private static final int PISSED_CHANCE = 50;
    private static final int HISS_COUNTER_RESET = 500;
    private static final int RATTLE_CHANCE_NEAR = 30;
    private static final int RATTLE_CHANCE_FAR = 100;
    private static final int VENOM_DURATION_TICKS = 150;
    private static final int VENOM_AMPLIFIER = 2;

    /** Ticks from hatch to full (variant) adult size — same pattern as MoCKomodoDragonEntity. */
    private static final int GROWTH_TICKS = 48000;
    /** Fraction of the variant's adult size a freshly-hatched baby starts at. */
    private static final float BABY_SCALE = 0.35F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCSnakeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_BITING =
            SynchedEntityData.defineId(MoCSnakeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCSnakeEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    /** How long after being released before it can be picked up again — avoids an instant re-grab flicker. */
    private int pickupCooldown;

    // Purely cosmetic, ticked client-side exactly like the original's own
    // instance fields (see tickCosmeticAnimation()) — the Model reads these
    // straight off the entity every frame, exactly like the original's
    // setLivingAnimations() reads them off MoCEntitySnake.
    private float fTongue;
    private float fMouth;
    private float fRattle;
    /** Original's public {@code bodyswing}: counts down from 2 during a bite, resets to 2.5. */
    public float bodyswing = 2.0F;
    private int movInt;
    /** Only used to avoid a redundant refreshDimensions() call every tick — see tickGrowth(). */
    private float lastAppliedScale = -1F;

    // Computed independently on both sides every tick (see tickNearPlayer())
    // — not synced, because both sides derive it the same deterministic way,
    // exactly like the original's isNearPlayer field.
    private boolean isNearPlayer;
    private boolean isPissed;
    private int hissCounter;

    public MoCSnakeEntity(EntityType<? extends MoCSnakeEntity> type, Level level) {
        super(type, level);
        this.movInt = this.random.nextInt(10);
        // Original overrides isAmphibian()/canBreatheUnderwater() to let the
        // snake wander freely between land and water without hesitating at
        // the shoreline; zeroing the water malus is what actually achieves
        // that (same fix already used by MoCKomodoDragonEntity).
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    /** How high above the player's feet the snake sits while carried. */
    private static final double SHOULDER_HEIGHT_OFFSET = -0.15D;

    @Override
    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    @Override
    public Player getHolder() {
        return this.entityData.get(DATA_HELD_BY).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    private void startHolding(Player player) {
        this.entityData.set(DATA_HELD_BY, Optional.of(player.getUUID()));
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.pickupCooldown = 10;
    }

    /** Same approach as MoCKittyEntity: repositions the real entity next to the player's head every tick instead of using real riding (Player has no passenger attachment point). */
    private void tickHeld() {
        if (!isHeld()) {
            return;
        }
        Player holder = getHolder();
        if (holder == null || holder.isRemoved()) {
            if (!this.level().isClientSide) {
                stopHolding();
            }
            return;
        }
        if (!this.level().isClientSide && holder.isShiftKeyDown()) {
            stopHolding();
            return;
        }

        Vec3 targetPos = holder.getEyePosition().add(0.0D, SHOULDER_HEIGHT_OFFSET, 0.0D);
        this.moveTo(targetPos.x, targetPos.y, targetPos.z, holder.getYRot(), 0.0F);
        this.xo = targetPos.x;
        this.yo = targetPos.y;
        this.zo = targetPos.z;
        this.yRotO = holder.getYRot();
        // Always faces the same way the player is facing, instead of
        // independently looking around.
        this.setYHeadRot(holder.getYRot());
        this.yHeadRotO = holder.getYRot();
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean isPushable() {
        return !isHeld() && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isHeld() && super.canBeCollidedWith();
    }

    @Override
    public void pushEntities() {
        if (isHeld()) {
            return;
        }
        super.pushEntities();
    }

    /** How far below the water's top surface the snake's feet sit, so it looks partially submerged instead of floating on top of it. */
    private static final double WATER_SUBMERSION_DEPTH = 0.15D;

    @Override
    public void travel(Vec3 travelVector) {
        super.travel(travelVector);
        // Hard-locks Y to the water surface instead of spring-correcting
        // toward it — a spring always leaves a small residual settle/bounce;
        // snapping the position directly eliminates it entirely.
        if (!this.level().isClientSide && this.isInWater()) {
            double surfaceY = findWaterSurfaceY();
            if (surfaceY != Double.NEGATIVE_INFINITY) {
                this.setPos(this.getX(), surfaceY - WATER_SUBMERSION_DEPTH, this.getZ());
                Vec3 motion = this.getDeltaMovement();
                boolean moving = motion.x * motion.x + motion.z * motion.z > 1.0E-4D;
                this.setDeltaMovement(motion.x, 0.0D, motion.z);
                // playStepSound() relies on vanilla's on-ground footstep
                // bookkeeping, which never triggers once we're hard-locking
                // Y here instead of letting normal gravity/ground contact
                // happen — so the swim sound has to be driven from here.
                if (moving && this.random.nextInt(20) == 0) {
                    this.playSound(ModSounds.SNAKE_SWIM.get(), 1.0F, 1.0F);
                }
            }
        }
    }

    /** Y of the top of the nearest water column near the entity's feet, or {@code Double.NEGATIVE_INFINITY} if none is close by. */
    private double findWaterSurfaceY() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(
                Mth.floor(this.getX()), Mth.floor(this.getY()) - 1, Mth.floor(this.getZ()));
        int topWaterY = Integer.MIN_VALUE;
        for (int dy = 0; dy <= 3; dy++) {
            if (this.level().getFluidState(pos).is(FluidTags.WATER)) {
                topWaterY = pos.getY();
            }
            pos.move(0, 1, 0);
        }
        return topWaterY == Integer.MIN_VALUE ? Double.NEGATIVE_INFINITY : topWaterY + 1.0D;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Original always ran a hunt-goal against players but blocked the
        // actual bite in attackEntityAsMob() unless the snake was bold,
        // wild and pissed. Gating the goal itself on the same 3 conditions
        // is equivalent and avoids a target-then-do-nothing dead end.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                target -> this.isPissed() && this.getVariant().isBold() && !this.isTame() && !this.isBaby()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, SnakeVariant.GREEN_DARK.getId());
        builder.define(DATA_BITING, false);
        builder.define(DATA_HELD_BY, Optional.empty());
    }

    public SnakeVariant getVariant() {
        return SnakeVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(SnakeVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    /** Below this, a scale change is float noise from the per-tick age increment, not worth a refreshDimensions() call. */
    private static final float SCALE_CHANGE_THRESHOLD = 0.01F;

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction() * getVariant().getSizeFactor();
            if (Math.abs((float) scaleAttr.getBaseValue() - newScale) > SCALE_CHANGE_THRESHOLD) {
                scaleAttr.setBaseValue(newScale);
            }
        }
        float currentScale = (float) scaleAttr.getValue();
        // refreshDimensions() rebuilds the hitbox and briefly disrupts
        // pathfinding — calling it every single tick (which the old version
        // above did, since currentScale drifts by a tiny float amount every
        // tick during growth) made babies effectively unable to navigate.
        if (Math.abs(this.lastAppliedScale - currentScale) > SCALE_CHANGE_THRESHOLD) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.tickGrowth();
    }

    public float getTongueOff() {
        return this.fTongue;
    }

    public float getMouthOff() {
        return this.fMouth;
    }

    public float getRattleOff() {
        return this.fRattle;
    }

    public int getMovInt() {
        return this.movInt;
    }

    /** Original's {@code isClimbing()}: colliding horizontally while still moving upward. */
    public boolean isClimbing() {
        return this.horizontalCollision && this.getDeltaMovement().y > 0.01D;
    }

    /** Original's {@code isResting()}: not near/biting, grounded, and not moving horizontally. */
    public boolean isResting() {
        return !getNearPlayer() && this.onGround()
                && Math.abs(this.getDeltaMovement().x) < 0.01D
                && Math.abs(this.getDeltaMovement().z) < 0.01D;
    }

    /** Original's {@code getNearPlayer()}: a bold snake within 5 blocks of a player, or mid-bite. */
    public boolean getNearPlayer() {
        return this.isNearPlayer || isBiting();
    }

    /** Original's "picked" pose (head raised, tail curled down) — now driven by our CarriedPet state. */
    public boolean isPickedUp() {
        return isHeld();
    }

    public boolean isBiting() {
        return this.entityData.get(DATA_BITING);
    }

    private void setBiting(boolean biting) {
        this.entityData.set(DATA_BITING, biting);
    }

    public boolean isPissed() {
        return this.isPissed;
    }

    private void setPissed(boolean pissed) {
        this.isPissed = pissed;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (this.pickupCooldown > 0) {
            this.pickupCooldown--;
        }
        tickHeld();
        // Original: nearPlayer (and the resting-yaw freeze) is computed on
        // both sides identically, every tick, with no network sync at all.
        tickNearPlayer();
        if (this.level().isClientSide) {
            tickCosmeticAnimation();
        }
        // Original: this block also runs unconditionally on both sides, but
        // only the server's isPissed actually affects targeting — the
        // client's own copy is inert.
        tickHissAndPissed();
    }

    private void tickNearPlayer() {
        boolean bold = getVariant().isBold() && !this.isBaby();
        Player nearest = this.level().getNearestPlayer(this, SEARCH_RADIUS);
        this.isNearPlayer = bold && nearest != null
                && this.distanceToSqr(nearest) < NEAR_PLAYER_RANGE * NEAR_PLAYER_RANGE;
        if (isResting()) {
            this.setYBodyRot(this.getYRot());
            this.setYHeadRot(this.getYRot());
        }
    }

    /**
     * Entity.playSound() is a no-op when called from client code: ClientLevel
     * only actually plays it if the "excluding player" argument is exactly
     * the local player, and Entity.playSound() always passes null. This is
     * the local-sound path instead, needed for fRattle/bodyswing since those
     * are ticked client-only in tickCosmeticAnimation(). Level.playLocalSound()
     * is common code (a no-op on the server), so no client class is touched.
     */
    private void playLocalSound(SoundEvent sound, float volume, float pitch) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume, pitch, false);
    }

    /** 1:1 port of the client-only half of the original's tick(): fTongue/fMouth/fRattle ramps, movInt reroll, and the bite/bodyswing countdown. */
    private void tickCosmeticAnimation() {
        if (this.fTongue != 0F) {
            this.fTongue += 0.2F;
            if (this.fTongue > 8F) {
                this.fTongue = 0F;
            }
        }
        if (this.fMouth != 0F && this.hissCounter == 0) {
            this.fMouth += 0.1F;
            if (this.fMouth > 0.5F) {
                this.fMouth = 0F;
            }
        }
        if (getVariant() == SnakeVariant.RATTLE && this.fRattle != 0F) {
            // Original checks `fRattle == 1.0f` after a +0.2F-per-tick ramp
            // starting at 0.1F — that sequence (0.1, 0.3, 0.5, 0.7, 0.9, 1.1,
            // 1.3...) never lands on exactly 1.0F, so the sound could never
            // actually fire. Detecting the crossing instead of an exact hit
            // is the fix.
            float previousRattle = this.fRattle;
            this.fRattle += 0.2F;
            if (previousRattle < 1.0F && this.fRattle >= 1.0F) {
                playLocalSound(ModSounds.SNAKE_RATTLE.get(), 1.0F, 1.0F);
            }
            if (this.fRattle > 8F) {
                this.fRattle = 0F;
            }
        }
        if (this.random.nextInt(50) == 0 && this.fTongue == 0F) {
            this.fTongue = 0.1F;
        }
        if (this.random.nextInt(100) == 0 && this.fMouth == 0F) {
            this.fMouth = 0.1F;
        }
        if (getVariant() == SnakeVariant.RATTLE) {
            int chance = getNearPlayer() ? RATTLE_CHANCE_NEAR : RATTLE_CHANCE_FAR;
            if (this.random.nextInt(chance) == 0) {
                this.fRattle = 0.1F;
            }
        }
        if (!isResting() && this.random.nextInt(50) == 0) {
            this.movInt = this.random.nextInt(10);
        }
        if (isBiting()) {
            this.bodyswing -= 0.5F;
            this.fMouth = 0.3F;
            if (this.bodyswing < 0F) {
                playLocalSound(ModSounds.SNAKE_SNAP.get(), 1.0F, 1.0F);
                this.bodyswing = 2.5F;
                this.fMouth = 0F;
                setBiting(false);
            }
        }
    }

    /** 1:1 port of the original's hiss/pissed tick block. */
    private void tickHissAndPissed() {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL
                && getNearPlayer() && !this.isTame() && getVariant().isBold()) {
            this.hissCounter++;
            if (this.hissCounter % HISS_SOUND_INTERVAL == 0) {
                this.fMouth = 0.3F;
                this.playSound(ModSounds.SNAKE_ANGRY.get(), 1.0F, 1.0F);
            }
            if (this.hissCounter % MOUTH_CLOSE_INTERVAL == 0) {
                this.fMouth = 0F;
            }
            if (this.hissCounter > PISSED_THRESHOLD && this.random.nextInt(PISSED_CHANCE) == 0) {
                setPissed(true);
                this.hissCounter = 0;
            }
        }
        if (this.hissCounter > HISS_COUNTER_RESET) {
            this.hissCounter = 0;
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        // Original's attackEntityAsMob(): only types > 2 (bold) show the
        // bite pose at all; the snap sound plays later, once the
        // client-side bodyswing countdown finishes (tickCosmeticAnimation()).
        if (hurt && getVariant().isBold()) {
            setBiting(true);
            if (getVariant().isVenomous() && target instanceof LivingEntity livingTarget) {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, VENOM_DURATION_TICKS, VENOM_AMPLIFIER));
            }
        }
        return hurt;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Original's attackEntityFrom(): types 1-2 (shy) never get the
        // pissed/retarget treatment at all.
        if (!getVariant().isBold()) {
            return super.hurt(source, amount);
        }
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && !this.level().isClientSide && !this.isTame()
                && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            Entity attacker = source.getEntity();
            if (attacker != this && attacker instanceof LivingEntity livingAttacker) {
                setPissed(true);
                this.setTarget(livingAttacker);
            }
        }
        return wasHurt;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData resultGroupData = spawnGroupData;

        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            VariantGroupData<SnakeVariant> group = VariantGroupData.of(spawnGroupData, SnakeVariant.class,
                    () -> SnakeVariant.forBiome(level.getBiome(this.blockPosition()), this.random));
            resultGroupData = group;
            setVariant(group.variant());
        } else {
            // SPAWN_EGG / mob spawner / command spawns: uniformly random,
            // same as before (see SnakeSpawnEggItem, which sets its own
            // variant directly and doesn't go through this path at all).
            setVariant(SnakeVariant.random(this.random));
        }

        // Never forward our own custom SpawnGroupData into TamableAnimal's
        // finalizeSpawn — same fix as MoCBearEntity: it converts the data
        // without checking the type and crashes with anything that isn't
        // its own. We track the group's shared variant ourselves instead.
        super.finalizeSpawn(level, difficulty, spawnType, null);
        return resultGroupData;
    }

    @Override
    public boolean shouldDropExperience() {
        // Handled manually in dropCustomDeathLoot(), gated on the killer.
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // Wiki: "when killed by a player or tamed wolf" — anything else
        // (falling, lava, another wild mob) drops nothing.
        Entity killer = damageSource.getEntity();
        boolean validKiller = killer instanceof Player || MoCLootUtil.isTamedWolf(killer);
        if (!validKiller) {
            return;
        }

        // Wiki: 0-2 eggs of its own variant, not affected by Fortune/Looting.
        MoCLootUtil.dropItems(this, getEggItem(), this.random.nextInt(3));

        // Wiki: 1-3 experience.
        MoCExperienceUtil.dropExperienceOrb(level, this, MoCExperienceUtil.rollStandardXp(this.random));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("SnakeVariant", getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SnakeVariant", 8)) {
            try {
                setVariant(SnakeVariant.valueOf(tag.getString("SnakeVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // NeoForge's replacement for the now-final canBreatheUnderwater() — same
        // intent as the original mod's MoCEntitySnake.canBreatheUnderwater().
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    @Override
    public boolean isPushedByFluid() {
        // Doesn't get dragged by currents, but still floats normally (see
        // the removed isAffectedByFluids override below — without FloatGoal
        // spamming jumps in water, plain vanilla buoyancy is enough to keep
        // it at the surface instead of sinking).
        return false;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Original: legless, so no footstep sound on land at all — only a swim sound in water.
        if (this.isInWater()) {
            this.playSound(ModSounds.SNAKE_SWIM.get(), 1.0F, 1.0F);
        }
    }

    @Override
    public void jumpFromGround() {
        // Wiki/original: snakes can't jump on land, only surface while swimming.
        if (this.isInWater()) {
            super.jumpFromGround();
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Wiki: "Wild snakes cannot be tamed. Instead, the player has to
        // obtain a snake egg." — see onHatchedFromEgg().
        return false;
    }

    /** Wiki: "Tamed snakes can be healed with raw rat." */
    private boolean isHealingFood(ItemStack stack) {
        return stack.is(ModItems.RAT_RAW.get());
    }

    private CompoundTag buildAmuletTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Snake", true);
        tag.putInt("SnakeVariant", getVariant().getId());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    private void capturePetInstant(Player player, InteractionHand hand) {
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    /** The taming egg matching this snake's own variant — used for the death drop. */
    private Item getEggItem() {
        return switch (getVariant()) {
            case GREEN_DARK -> ModItems.SNAKE_EGG_GREEN_DARK.get();
            case WOLF -> ModItems.SNAKE_EGG_WOLF.get();
            case ORANGE -> ModItems.SNAKE_EGG_ORANGE.get();
            case GREEN_BRIGHT -> ModItems.SNAKE_EGG_GREEN_BRIGHT.get();
            case CORAL -> ModItems.SNAKE_EGG_CORAL.get();
            case COBRA -> ModItems.SNAKE_EGG_COBRA.get();
            case RATTLE -> ModItems.SNAKE_EGG_RATTLE.get();
            case PYTHON -> ModItems.SNAKE_EGG_PYTHON.get();
        };
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "To pick up a snake, right-click on it and it will go onto
        // your shoulders." Same system as MoCKittyEntity — needs an empty
        // hand, and only one CarriedPet at a time per player.
        if (this.isTame() && this.isOwnedBy(player) && this.pickupCooldown <= 0 && !isHeld() && stack.isEmpty()
                && !PetCarryUtil.isAlreadyCarryingAPet(player)) {
            if (!this.level().isClientSide) {
                startHolding(player);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(SnakeVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.setBaby(true);
        this.setAge(-GROWTH_TICKS);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SNAKE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SNAKE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SNAKE_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setVariant(SnakeVariant.byId(tag.getInt("SnakeVariant")));
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            this.setAge(tag.getInt("Age"));
        } else {
            this.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
        }
    }
}
