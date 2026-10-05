package com.example.neomocreatures.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.golem.GolemBlockPicker;
import com.example.neomocreatures.entity.golem.GolemBody;
import com.example.neomocreatures.entity.golem.GolemCore;
import com.example.neomocreatures.entity.golem.GolemState;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.CommonHooks;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityGolem} (the Big Golem). A giant built
 * out of 23 block "cubes" (see GolemBody) around a precious core. This first step covers its body,
 * looks and basic darkness-bound melee AI; assembling, armour, rock throwing and its death sequence
 * are added in later steps.
 */
public class MoCBigGolemEntity extends Monster implements RockThrower {

    private static final double MAX_HEALTH = 50.0D;
    /** Original: getAIMoveSpeed() tops out at 0.15 with all six leg cubes. */
    private static final double MOVEMENT_SPEED = 0.15D;
    private static final double ATTACK_DAMAGE = 7.0D;
    /** Wiki: 5 experience (the 1.16 code gave 20). */
    private static final int EXPERIENCE = 5;

    private static final double ATTACK_SPEED_MODIFIER = 1.0D;
    private static final float LOOK_DISTANCE = 8.0F;
    /** Original: getBrightness() < 0.5F to hunt, >= 0.5F may give up. */
    private static final float DARKNESS_THRESHOLD = 0.5F;
    private static final int GIVE_UP_IN_LIGHT_CHANCE = 100;
    /** Original: getAttackReachSqr() = 4 + target width. */
    private static final double ATTACK_REACH_SQR_BASE = 4.0D;
    /** Original: wakes up when a player comes within 8 blocks. */
    private static final double WAKE_UP_DISTANCE = 8.0D;
    /** Wiki: once fully formed it searches much farther — follow range goes from 16 to 32. */
    private static final double FORMED_RANGE_BONUS = 16.0D;
    private static final ResourceLocation FORMED_RANGE_ID =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "big_golem_formed_range");
    /** Original: pulls blocks off the surface up to 12 blocks away. */
    private static final int SUMMON_RADIUS = 12;
    /** Original: 1-in-20 per tick while assembling, otherwise 1-in-(42 - state * difficulty). */
    private static final int SUMMON_CHANCE_ASSEMBLING = 20;
    private static final int SUMMON_CHANCE_BASE = 42;
    /** Original: blocks harder than 50 are never pulled out for real. */
    private static final float MAX_SUMMON_HARDNESS = 50.0F;
    /** Original: the summoned chest opens while a rock is within 2 blocks of it. */
    private static final double CHEST_OPEN_RANGE = 2.0D;
    private static final float GOLEM_SOUND_VOLUME = 3.0F;
    private static final float REJECT_SOUND_VOLUME = 2.0F;
    /** Wiki: at 20 health or less it turns orange and gets more dangerous (the 1.16 code used 30). */
    private static final float ENRAGE_HEALTH = 20.0F;
    /** Original: 1-in-20 per tick it re-checks its health while it has an enemy. */
    private static final int PHASE_CHECK_CHANCE = 20;
    /** Original: "so you can't hit a Golem too hard". */
    private static final float MAX_DAMAGE_PER_HIT = 5.0F;
    /** Original: only throws arm cubes at targets farther than 6 blocks. */
    private static final double MIN_THROW_DISTANCE = 6.0D;
    /** Original tCounter: arms raised from tick 25, cube released at 70, animation over at 90. */
    private static final int THROW_POSE_TICK = 25;
    private static final int THROW_RELEASE_TICK = 70;
    private static final int THROW_END_TICK = 90;
    /** Original: MoCTools.throwStone(..., speedMod 10, height 0.4). */
    private static final double THROW_SPEED_DIVISOR = 10.0D;
    private static final double THROW_ARC = 0.4D;
    /** Roughly its shoulder height, where the arm cube leaves from. */
    private static final double ARM_THROW_HEIGHT = 3.0D;
    /** Entity event telling clients to play the throwing animation. */
    private static final byte EVENT_START_THROW = 4;
    private static final int LEG_CUBE_COUNT = 6;
    /** Original: rollRotationOffset() — leans 10 degrees per leg cube of difference between both legs. */
    private static final float LEAN_DEGREES_PER_CUBE = 10.0F;
    /** Original: below 10 health it starts dying. */
    private static final float DYING_HEALTH = 10.0F;
    /** Original dCounter: pulls rocks (1-in-3 per tick) for 80 ticks, smokes at 120, bursts after 140. */
    private static final int DYING_PULL_TICKS = 80;
    private static final int DYING_PULL_CHANCE = 3;
    private static final int DYING_SMOKE_TICK = 120;
    private static final int DYING_BURST_TICK = 140;
    private static final int SMOKE_PARTICLES_PER_TICK = 10;
    /** Original VacuumFX: 2 particles per tick, 1.5 blocks in front of it, 0.8 below the top of its body. */
    private static final int VACUUM_PARTICLES_PER_TICK = 2;
    private static final double VACUUM_FRONT_OFFSET = 1.5D;
    private static final double VACUUM_TOP_OFFSET = 0.8D;
    /** Keeps the kill credited to the last player who hurt it, so the 7 s death sequence still gives XP. */
    private static final int PLAYER_CREDIT_TICKS = 100;
    /** Entity event telling clients to start the dying smoke. */
    private static final byte EVENT_START_SMOKE = 5;
    /** Original loot table: 2-4 ancient silver scrap and 3-6 redstone, each +0-1 per Looting level. */
    private static final int SCRAP_MIN = 2;
    private static final int SCRAP_RANGE = 3;
    private static final int REDSTONE_MIN = 3;
    private static final int REDSTONE_RANGE = 4;
    private static final String TAG_DYING_TICKS = "DyingTicks";
    /** Original: returnRandomCheapBlock() — conjured when it may not tear blocks out; never dropped. */
    private static final BlockState[] CHEAP_BLOCKS = {
            Blocks.DIRT.defaultBlockState(), Blocks.COBBLESTONE.defaultBlockState(),
            Blocks.OAK_PLANKS.defaultBlockState(), Blocks.ICE.defaultBlockState()};

    private static final String TAG_GOLEM_STATE = "GolemState";

    private static final EntityDataAccessor<Integer> DATA_GOLEM_STATE =
            SynchedEntityData.defineId(MoCBigGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<CompoundTag> DATA_CUBE_TEXTURES =
            SynchedEntityData.defineId(MoCBigGolemEntity.class, EntityDataSerializers.COMPOUND_TAG);

    /** Server-side source of truth; clients only see the texture indices in DATA_CUBE_TEXTURES. */
    private final GolemBody body = new GolemBody();


    /** Original tCounter: 0 when idle, counts up while throwing an arm cube (on both sides, for the animation). */
    private int throwTicks;

    /** Server-side: ticks since it started dying (original dCounter). */
    private int dyingTicks;
    /** Client-side: true once the dying smoke has started. */
    private boolean smoking;

    public MoCBigGolemEntity(EntityType<? extends MoCBigGolemEntity> type, Level level) {
        super(type, level);
        this.body.set(GolemBody.CORE_SLOT, GolemCore.random(this.random).toCube());
        this.syncBody();
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
        builder.define(DATA_GOLEM_STATE, GolemState.DORMANT.getId());
        builder.define(DATA_CUBE_TEXTURES, new CompoundTag());
    }

    public GolemState getGolemState() {
        return GolemState.byId(this.entityData.get(DATA_GOLEM_STATE));
    }

    protected void setGolemState(GolemState state) {
        this.entityData.set(DATA_GOLEM_STATE, state.getId());
    }

    /** Texture index of every body slot (GolemBody.EMPTY_TEXTURE when missing) — read by the model. */
    public byte[] getCubeTextures() {
        return GolemBody.texturesFromSyncTag(this.entityData.get(DATA_CUBE_TEXTURES));
    }

    /** Pushes the current body to clients; call after every change to {@link #body}. */
    private void syncBody() {
        this.entityData.set(DATA_CUBE_TEXTURES, this.body.toSyncTag());
    }

    private boolean isInDarkness() {
        return this.getLightLevelDependentMagicValue() < DARKNESS_THRESHOLD;
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return this.distanceToSqr(target) <= ATTACK_REACH_SQR_BASE + target.getBbWidth();
    }

    // ---------------------------------------------------------------------
    // Assembling
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.throwTicks > 0) {
            this.throwTicks++;
        }
        if (this.level().isClientSide) {
            if (this.throwTicks > THROW_END_TICK) {
                this.throwTicks = 0;
            }
            if (this.smoking) {
                this.spawnSmoke();
            }
            if (this.isChestOpen()) {
                this.spawnVacuumParticles();
            }
            return;
        }
        if (!this.isAlive()) {
            return;
        }
        if (this.getGolemState() == GolemState.DYING) {
            this.tickDying();
            return;
        }
        this.updateCombatPhase();
        this.updateAssemblyState();
        if (this.canSummonRocks() && this.random.nextInt(this.summonChance()) == 0) {
            this.summonRock(false);
        }
        this.tickArmThrow();
    }

    /**
     * Original: wakes up when a player gets close, and stops assembling once all 23 cubes are in place.
     * Only survival/adventure players wake it, and it takes them as its target right away; if it loses
     * its target halfway through, it goes back to rest (blue) until one comes close again.
     */
    private void updateAssemblyState() {
        if (this.getTarget() != null && !this.hasValidTarget()) {
            this.setTarget(null);
        }
        GolemState state = this.getGolemState();
        if ((state == GolemState.DORMANT || state == GolemState.SUMMONING) && this.getTarget() == null) {
            Player player = this.findAttackablePlayer(WAKE_UP_DISTANCE);
            if (player != null) {
                this.setTarget(player);
                this.setGolemState(GolemState.SUMMONING);
            } else if (state == GolemState.SUMMONING) {
                this.setGolemState(GolemState.DORMANT);
                this.setFormedRange(false);
            }
        }
        if (this.getGolemState() == GolemState.SUMMONING && this.body.isComplete()) {
            this.setGolemState(GolemState.ACTIVE);
            this.setFormedRange(true);
        }
    }

    /** Only builds itself up while it is actually hostile to someone it may attack and can still reach. */
    private boolean canSummonRocks() {
        GolemState state = this.getGolemState();
        return state != GolemState.DORMANT && state != GolemState.DYING && !this.body.isComplete() && this.hasValidTarget();
    }

    /** Alive, attackable (never creative or spectator players) and within its follow range. */
    private boolean hasValidTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || !this.canAttack(target)) {
            return false;
        }
        double followRange = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        return this.distanceToSqr(target) <= followRange * followRange;
    }

    /** Nearest survival/adventure player within range — creative and spectator players are ignored. */
    @Nullable
    private Player findAttackablePlayer(double range) {
        return this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(),
                range, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
    }

    /** Wiki: once formed, it spots any player it can actually see within its (larger) range. */
    @Nullable
    private Player findVisiblePlayer(double range) {
        Player player = this.findAttackablePlayer(range);
        return player != null && this.hasLineOfSight(player) ? player : null;
    }

    /** Adds or removes the formed-golem follow range bonus; the modifier is saved with the entity. */
    private void setFormedRange(boolean formed) {
        AttributeInstance range = this.getAttribute(Attributes.FOLLOW_RANGE);
        if (range == null) {
            return;
        }
        if (formed && !range.hasModifier(FORMED_RANGE_ID)) {
            range.addPermanentModifier(new AttributeModifier(FORMED_RANGE_ID, FORMED_RANGE_BONUS, AttributeModifier.Operation.ADD_VALUE));
        } else if (!formed) {
            range.removeModifier(FORMED_RANGE_ID);
        }
    }

    /** Never fights while dormant or still assembling, nor once it starts dying. */
    private boolean canFight() {
        GolemState state = this.getGolemState();
        return state == GolemState.ACTIVE || state == GolemState.ENRAGED;
    }

    private int summonChance() {
        if (this.getGolemState() == GolemState.SUMMONING) {
            return SUMMON_CHANCE_ASSEMBLING;
        }
        return Math.max(1, SUMMON_CHANCE_BASE - this.getGolemState().getId() * this.level().getDifficulty().getId());
    }

    /**
     * Original: acquireRock() — tears a surface block out nearby (or conjures a cheap one) and calls it over.
     *
     * @param hovering true while dying: the rock floats around the golem instead of joining its body
     */
    private void summonRock(boolean hovering) {
        BlockPos pos = this.randomSurfacePos();
        BlockState state = this.level().getBlockState(pos);
        GolemBody.Cube cube;
        if (this.canSummonForReal(pos, state)) {
            this.level().destroyBlock(pos, false, this);
            cube = new GolemBody.Cube(state, true);
        } else {
            cube = new GolemBody.Cube(CHEAP_BLOCKS[this.random.nextInt(CHEAP_BLOCKS.length)], false);
        }
        MoCSummonedRockEntity rock = new MoCSummonedRockEntity(this.level(), this, cube,
                pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
        this.level().addFreshEntity(hovering ? rock.hovering() : rock);
    }

    private BlockPos randomSurfacePos() {
        int span = SUMMON_RADIUS * 2 + 1;
        int x = this.getBlockX() + this.random.nextInt(span) - SUMMON_RADIUS;
        int z = this.getBlockZ() + this.random.nextInt(span) - SUMMON_RADIUS;
        int y = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
        return new BlockPos(x, y, z);
    }

    private boolean canSummonForReal(BlockPos pos, BlockState state) {
        return Config.MONSTERS.golemDestroyBlocks.get()
                && GolemBlockPicker.isLiftable(this.level(), pos, true)
                && state.getDestroySpeed(this.level(), pos) <= MAX_SUMMON_HARDNESS
                && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                && CommonHooks.canEntityDestroy(this.level(), pos, this);
    }

    /** Original: receiveRock() — called by a summoned rock that reached the golem. */
    public void receiveRock(GolemBody.Cube cube) {
        int slot = this.getGolemState() == GolemState.DYING ? -1 : this.claimSlotForNewCube();
        if (slot < 0) {
            // No room: bounce it off, but never lose a block that came from the world.
            this.playSound(ModSounds.TURTLE_HURT.get(), REJECT_SOUND_VOLUME, 1.0F);
            if (cube.returnable()) {
                this.spawnAtLocation(new ItemStack(cube.state().getBlock()));
            }
            return;
        }
        this.body.set(slot, cube);
        this.syncBody();
        this.playSound(ModSounds.GOLEM_ATTACH.get(), GOLEM_SOUND_VOLUME, 1.0F);
        // Original: every attached cube heals it by the difficulty level.
        this.heal(this.level().getDifficulty().getId());
    }

    /**
     * Original: getRandomCubeAdj() — chest plates come first; limbs always grow from the body outwards,
     * so a new cube takes the limb's root slot and pushes the cubes already there towards the tip.
     */
    private int claimSlotForNewCube() {
        int slot = this.pickEmptySlot();
        if (slot < 0) {
            return -1;
        }
        if (isLimbMiddle(slot)) {
            this.body.set(slot, this.body.get(slot - 1));
            return slot - 1;
        }
        if (isLimbTip(slot)) {
            if (this.body.isEmpty(slot - 2) && this.body.isEmpty(slot - 1)) {
                return slot - 2;
            }
            this.body.set(slot, this.body.get(slot - 1));
            this.body.set(slot - 1, this.body.get(slot - 2));
            return slot - 2;
        }
        return slot;
    }

    /** Original: getRandomMissingCube() — any missing chest plate first, then any other missing slot. */
    private int pickEmptySlot() {
        java.util.List<Integer> chest = new java.util.ArrayList<>();
        for (int slot = GolemBody.CHEST_FIRST; slot <= GolemBody.CHEST_LAST; slot++) {
            if (this.body.isEmpty(slot)) {
                chest.add(slot);
            }
        }
        java.util.List<Integer> candidates = chest.isEmpty() ? this.body.emptySlots() : chest;
        return candidates.isEmpty() ? -1 : candidates.get(this.random.nextInt(candidates.size()));
    }

    private static boolean isLimbMiddle(int slot) {
        return slot == GolemBody.LEFT_ARM || slot == GolemBody.RIGHT_ARM
                || slot == GolemBody.LEFT_KNEE || slot == GolemBody.RIGHT_KNEE;
    }

    private static boolean isLimbTip(int slot) {
        return slot == GolemBody.LEFT_HAND || slot == GolemBody.RIGHT_HAND
                || slot == GolemBody.LEFT_FOOT || slot == GolemBody.RIGHT_FOOT;
    }

    /** True while any body slot is empty — works on both sides (clients read the synced textures). */
    public boolean isMissingCubes() {
        for (byte texture : this.getCubeTextures()) {
            if (texture == GolemBody.EMPTY_TEXTURE) {
                return true;
            }
        }
        return false;
    }

    /** Original: openChest() — the chest opens to take in a summoned rock flying close by. */
    public boolean isChestOpen() {
        return this.isMissingCubes() && !this.level().getEntitiesOfClass(MoCSummonedRockEntity.class,
                this.getBoundingBox().inflate(CHEST_OPEN_RANGE)).isEmpty();
    }

    /**
     * Original: attackEntityFrom(). While assembling it takes no damage at all. Once built, its cubes are
     * armour: hits only knock cubes loose until the four chest plates are gone (or the chest is open to
     * take in a rock), and even then no single hit deals more than 5.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.isCreativePlayer()) {
            return super.hurt(source, amount);
        }
        GolemState state = this.getGolemState();
        if (state == GolemState.DYING) {
            return false;
        }
        if (state == GolemState.SUMMONING) {
            this.targetAttacker(source);
            return false;
        }
        if (this.isArmored()) {
            int difficulty = Math.max(1, this.level().getDifficulty().getId());
            if (this.random.nextInt(difficulty) == 0) {
                this.knockOffRandomCube();
            } else {
                this.playSound(ModSounds.TURTLE_HURT.get(), REJECT_SOUND_VOLUME, 1.0F);
            }
            this.targetAttacker(source);
            return false;
        }
        // Never dies straight from a hit: below 10 health it plays its death sequence instead.
        float damage = Math.min(Math.min(amount, MAX_DAMAGE_PER_HIT), Math.max(0.0F, this.getHealth() - 1.0F));
        boolean hurt = super.hurt(source, damage);
        if (hurt && this.getHealth() < DYING_HEALTH) {
            this.startDying();
        }
        return hurt;
    }

    private boolean isArmored() {
        return !this.isChestOpen() && !this.isChestUncovered();
    }

    private boolean isChestUncovered() {
        for (int slot = GolemBody.CHEST_FIRST; slot <= GolemBody.CHEST_LAST; slot++) {
            if (!this.body.isEmpty(slot)) {
                return false;
            }
        }
        return true;
    }

    private void targetAttacker(DamageSource source) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL
                && source.getEntity() instanceof LivingEntity attacker && attacker != this) {
            this.setTarget(attacker);
        }
    }

    // ---------------------------------------------------------------------
    // Combat
    // ---------------------------------------------------------------------

    /**
     * Original: with an enemy it re-checks its health now and then — yellow at 30 or more, orange below
     * (the dying phase comes with its death sequence). An orange golem that loses its enemy calms down.
     */
    private void updateCombatPhase() {
        GolemState state = this.getGolemState();
        if ((state == GolemState.ACTIVE || state == GolemState.ENRAGED) && this.getTarget() == null) {
            Player enemy = this.findVisiblePlayer(this.getAttributeValue(Attributes.FOLLOW_RANGE));
            if (enemy != null) {
                this.setTarget(enemy);
            } else if (state == GolemState.ENRAGED) {
                this.setGolemState(GolemState.SUMMONING);
                return;
            }
        }
        if ((state == GolemState.ACTIVE || state == GolemState.ENRAGED)
                && this.getTarget() != null && this.random.nextInt(PHASE_CHECK_CHANCE) == 0) {
            if (this.getHealth() < DYING_HEALTH) {
                this.startDying();
            } else {
                this.setGolemState(this.getHealth() >= ENRAGE_HEALTH ? GolemState.ACTIVE : GolemState.ENRAGED);
            }
        }
    }

    /** Original: destroyRandomGolemCube() — knocks a cube loose (never the core); limbs lose their tip first. */
    private void knockOffRandomCube() {
        List<Integer> used = this.body.usedSlots();
        if (used.isEmpty()) {
            return;
        }
        int slot = used.get(this.random.nextInt(used.size()));
        if (slot == GolemBody.CORE_SLOT) {
            return;
        }
        GolemBody.Cube cube = this.takeCube(this.outermostCubeOfLimb(slot));
        this.playSound(ModSounds.GOLEM_HURT.get(), GOLEM_SOUND_VOLUME, 1.0F);
        if (cube.returnable()) {
            this.spawnAtLocation(new ItemStack(cube.state().getBlock()));
        }
    }

    /** Original: starts the throw when its target is over 6 blocks away, and lets the cube go at tick 70. */
    private void tickArmThrow() {
        LivingEntity target = this.getTarget();
        if (this.throwTicks == 0) {
            if (target != null && this.canShootArmCubes() && this.distanceTo(target) > MIN_THROW_DISTANCE) {
                this.throwTicks = 1;
                this.level().broadcastEntityEvent(this, EVENT_START_THROW);
            }
            return;
        }
        if (this.throwTicks == THROW_RELEASE_TICK) {
            if (target != null && target.isAlive() && this.canShootArmCubes() && this.hasLineOfSight(target)) {
                this.shootArmCube(target);
                this.throwTicks = 0;
            }
        } else if (this.throwTicks > THROW_END_TICK) {
            this.throwTicks = 0;
        }
    }

    private boolean canShootArmCubes() {
        GolemState state = this.getGolemState();
        return (state == GolemState.ACTIVE || state == GolemState.ENRAGED) && !this.armCubeSlots().isEmpty();
    }

    /** Original: shootBlock() — throws one of its own arm cubes (tips first) at the target. */
    private void shootArmCube(LivingEntity target) {
        List<Integer> arms = this.armCubeSlots();
        GolemBody.Cube cube = this.takeCube(this.outermostCubeOfLimb(arms.get(this.random.nextInt(arms.size()))));
        this.playSound(ModSounds.GOLEM_SHOOT.get(), GOLEM_SOUND_VOLUME, 1.0F);

        MoCThrowableRockEntity rock = new MoCThrowableRockEntity(this.level(), this, cube.state(), cube.returnable());
        double startY = this.getY() + ARM_THROW_HEIGHT;
        rock.setPos(this.getX(), startY, this.getZ());
        rock.setDeltaMovement(
                (target.getX() - this.getX()) / THROW_SPEED_DIVISOR,
                (target.getY() - startY) / THROW_SPEED_DIVISOR + THROW_ARC,
                (target.getZ() - this.getZ()) / THROW_SPEED_DIVISOR);
        this.level().addFreshEntity(rock);
    }

    private List<Integer> armCubeSlots() {
        List<Integer> arms = new java.util.ArrayList<>();
        for (int slot = GolemBody.LEFT_SHOULDER; slot <= GolemBody.RIGHT_HAND; slot++) {
            if (!this.body.isEmpty(slot)) {
                arms.add(slot);
            }
        }
        return arms;
    }

    /** Removes a cube from the body, syncs it and returns what was there. */
    private GolemBody.Cube takeCube(int slot) {
        GolemBody.Cube cube = this.body.get(slot);
        this.body.set(slot, null);
        this.syncBody();
        return cube;
    }

    /** Limbs come apart from the tip inwards: a shoulder/thigh hit takes the hand/foot (or elbow/knee) first. */
    private int outermostCubeOfLimb(int slot) {
        if (isLimbRoot(slot)) {
            if (!this.body.isEmpty(slot + 2)) {
                return slot + 2;
            }
            if (!this.body.isEmpty(slot + 1)) {
                return slot + 1;
            }
        } else if (isLimbMiddle(slot) && !this.body.isEmpty(slot + 1)) {
            return slot + 1;
        }
        return slot;
    }

    private static boolean isLimbRoot(int slot) {
        return slot == GolemBody.LEFT_SHOULDER || slot == GolemBody.RIGHT_SHOULDER
                || slot == GolemBody.LEFT_THIGH || slot == GolemBody.RIGHT_THIGH;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_START_THROW) {
            this.throwTicks = 1;
        } else if (id == EVENT_START_SMOKE) {
            this.smoking = true;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** True while both arms are raised for a throw — read by the model. */
    public boolean isThrowing() {
        return this.throwTicks > THROW_POSE_TICK;
    }

    // ---------------------------------------------------------------------
    // Legs
    // ---------------------------------------------------------------------

    /**
     * Original: getAIMoveSpeed() — slower with every leg cube it loses. Wiki: it can only walk once at
     * least one knee is formed, so knocking the knees off pins it in place until it rebuilds them.
     */
    @Override
    public float getSpeed() {
        boolean hasKnee = this.countCubes(GolemBody.LEFT_KNEE, GolemBody.LEFT_KNEE)
                + this.countCubes(GolemBody.RIGHT_KNEE, GolemBody.RIGHT_KNEE) > 0;
        if (!hasKnee) {
            return 0.0F;
        }
        return super.getSpeed() * this.countCubes(GolemBody.LEFT_THIGH, GolemBody.RIGHT_FOOT) / LEG_CUBE_COUNT;
    }

    /** Original: rollRotationOffset() — leans towards the side with the shorter leg. Read by the renderer. */
    public float getLeanDegrees() {
        int left = this.countCubes(GolemBody.LEFT_THIGH, GolemBody.LEFT_FOOT);
        int right = this.countCubes(GolemBody.RIGHT_THIGH, GolemBody.RIGHT_FOOT);
        return (left - right) * LEAN_DEGREES_PER_CUBE;
    }

    /** Counts filled slots in [first, last] — works on both sides through the synced textures. */
    private int countCubes(int first, int last) {
        byte[] textures = this.getCubeTextures();
        int count = 0;
        for (int slot = first; slot <= last; slot++) {
            if (textures[slot] != GolemBody.EMPTY_TEXTURE) {
                count++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------------
    // Death
    // ---------------------------------------------------------------------

    private void startDying() {
        this.setGolemState(GolemState.DYING);
        this.setTarget(null);
        this.getNavigation().stop();
        this.throwTicks = 0;
        this.dyingTicks = 0;
    }

    /** Original: state 4 — stands still, pulls a last cloud of rocks around itself, smokes, then bursts. */
    private void tickDying() {
        this.getNavigation().stop();
        if (this.lastHurtByPlayer != null) {
            this.lastHurtByPlayerTime = PLAYER_CREDIT_TICKS;
        }
        this.dyingTicks++;
        if (this.dyingTicks < DYING_PULL_TICKS && this.random.nextInt(DYING_PULL_CHANCE) == 0) {
            this.summonRock(true);
        }
        if (this.dyingTicks == DYING_SMOKE_TICK) {
            this.playSound(ModSounds.GOLEM_DEATH.get(), GOLEM_SOUND_VOLUME, 1.0F);
            this.level().broadcastEntityEvent(this, EVENT_START_SMOKE);
        }
        if (this.dyingTicks > DYING_BURST_TICK) {
            this.burst();
        }
    }

    /** Original: destroyGolem() — but it really dies, so its loot and XP drop (the original just vanished). */
    private void burst() {
        this.playSound(ModSounds.GOLEM_EXPLODE.get(), GOLEM_SOUND_VOLUME, 1.0F);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        DamageSource cause = this.lastHurtByPlayer != null
                ? this.damageSources().playerAttack(this.lastHurtByPlayer)
                : this.damageSources().generic();
        this.setHealth(0.0F);
        this.die(cause);
    }

    private void spawnSmoke() {
        for (int i = 0; i < SMOKE_PARTICLES_PER_TICK; i++) {
            this.level().addParticle(ParticleTypes.POOF,
                    this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(),
                    this.random.nextGaussian() * 0.2D, this.random.nextGaussian() * 0.2D, this.random.nextGaussian() * 0.2D);
        }
    }

    /**
     * Original: VacuumFX — particles sucked into the open chest. Vanilla portal particles travel back to
     * the point they spawn at, so spawning them at the chest with outward speed gives the same effect
     * without a custom particle texture.
     */
    private void spawnVacuumParticles() {
        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        double x = this.getX() - Mth.sin(yaw) * VACUUM_FRONT_OFFSET;
        double z = this.getZ() + Mth.cos(yaw) * VACUUM_FRONT_OFFSET;
        double y = this.getY() + this.getBbHeight() - VACUUM_TOP_OFFSET;
        for (int i = 0; i < VACUUM_PARTICLES_PER_TICK; i++) {
            this.level().addParticle(ParticleTypes.PORTAL, x, y, z,
                    (this.random.nextDouble() - 0.5D) * 4.0D,
                    -this.random.nextDouble(),
                    (this.random.nextDouble() - 0.5D) * 4.0D);
        }
    }

    /** Can't move at all while dying. */
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getGolemState() == GolemState.DYING;
    }

    /** Never despawns while carrying real blocks torn from the world (besides its core), so they can't vanish with it. */
    @Override
    public boolean requiresCustomPersistence() {
        if (super.requiresCustomPersistence()) {
            return true;
        }
        for (int slot : this.body.usedSlots()) {
            GolemBody.Cube cube = this.body.get(slot);
            if (slot != GolemBody.CORE_SLOT && cube != null && cube.returnable()) {
                return true;
            }
        }
        return false;
    }

    /** However it dies, it gives back every real block of its body — its precious core always. */
    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide && !this.dead) {
            this.dropBodyCubes();
        }
        super.die(damageSource);
    }

    private void dropBodyCubes() {
        for (int slot : this.body.usedSlots()) {
            GolemBody.Cube cube = this.body.get(slot);
            if (cube != null && cube.returnable()) {
                this.spawnAtLocation(new ItemStack(cube.state().getBlock()));
            }
            this.body.set(slot, null);
        }
        this.syncBody();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        this.dropWithLooting(ModItems.ANCIENT_SILVER_SCRAP.get(), SCRAP_MIN + this.random.nextInt(SCRAP_RANGE), lootingLevel);
        this.dropWithLooting(Items.REDSTONE, REDSTONE_MIN + this.random.nextInt(REDSTONE_RANGE), lootingLevel);
    }

    private void dropWithLooting(Item item, int baseCount, int lootingLevel) {
        int count = baseCount;
        for (int i = 0; i < lootingLevel; i++) {
            count += this.random.nextInt(2);
        }
        this.spawnAtLocation(new ItemStack(item, count));
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    /** Original: getHurtSound() returns the golem grunt; it has no idle or death sound of its own. */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GOLEM_AMBIENT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.GOLEM_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected int getBaseExperienceReward() {
        return EXPERIENCE;
    }

    // ---------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_GOLEM_STATE, this.getGolemState().getId());
        this.body.save(tag);
        tag.putInt(TAG_DYING_TICKS, this.dyingTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setGolemState(GolemState.byId(tag.getInt(TAG_GOLEM_STATE)));
        this.body.load(tag, this.level().holderLookup(Registries.BLOCK));
        this.syncBody();
        this.dyingTicks = tag.getInt(TAG_DYING_TICKS);
    }

    // ---------------------------------------------------------------------
    // Goals
    // ---------------------------------------------------------------------

    /** Original: AIGolemAttack — out in bright light it has a small chance each tick to drop the chase. */
    private static final class GolemMeleeAttackGoal extends MeleeAttackGoal {
        private final MoCBigGolemEntity golem;

        GolemMeleeAttackGoal(MoCBigGolemEntity golem) {
            super(golem, ATTACK_SPEED_MODIFIER, true);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return this.golem.canFight() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.golem.canFight()) {
                return false;
            }
            if (!this.golem.isInDarkness() && this.golem.getRandom().nextInt(GIVE_UP_IN_LIGHT_CHANCE) == 0) {
                this.golem.setTarget(null);
                return false;
            }
            return super.canContinueToUse();
        }
    }

    /** Original: AIGolemTarget — only picks new targets while standing in the dark. */
    private static final class DarknessTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCBigGolemEntity golem;

        DarknessTargetGoal(MoCBigGolemEntity golem, Class<T> targetClass) {
            super(golem, targetClass, true);
            this.golem = golem;
        }

        @Override
        public boolean canUse() {
            return this.golem.isInDarkness() && super.canUse();
        }
    }
}