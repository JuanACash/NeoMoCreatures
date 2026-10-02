package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.wildwolf.WildWolfVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityWWolf}. Neutral: only actively hunts
 * a player/iron golem while it's dark (brightness below 0.5, like a zombie); always fights back if
 * hit regardless of light. 5 colours, forced to TIMBER in cold/snowy/frozen/ice biomes. Occasionally
 * lets a nearby skeleton or zombie mount it, like vanilla's spider jockey.
 */
public class MoCWildWolfEntity extends Monster {

    private static final double MAX_HEALTH = 15.0D;
    private static final double MOVEMENT_SPEED = 0.3D;
    private static final double ATTACK_DAMAGE = 3.5D;
    private static final double ATTACK_SPEED = 1.0D;

    /** Original: shouldAttackPlayers()-equivalent brightness ceiling for both goals. */
    private static final float MAX_ATTACK_BRIGHTNESS = 0.5F;

    /** Original: a 1 in 200 chance per tick to wag its tail; the mouth-open pose lasts 15 ticks
     *  after a hurt/ambient sound, the tail wag lasts 8. */
    private static final int TAIL_WAG_CHANCE = 200;
    private static final int MOUTH_OPEN_DURATION = 15;
    private static final int TAIL_WAG_DURATION = 8;

    /** Original: a 1 in 100 chance per tick to let a nearby skeleton/zombie mount it, jockey-style. */
    private static final int JOCKEY_CHANCE = 100;
    private static final double JOCKEY_SEARCH_RADIUS_XZ = 4.0D;
    private static final double JOCKEY_SEARCH_RADIUS_Y = 2.0D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCWildWolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_COUNTER =
            SynchedEntityData.defineId(MoCWildWolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAIL_COUNTER =
            SynchedEntityData.defineId(MoCWildWolfEntity.class, EntityDataSerializers.INT);

    public MoCWildWolfEntity(EntityType<? extends MoCWildWolfEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, WildWolfVariant.CLASSIC.getId());
        builder.define(DATA_MOUTH_COUNTER, 0);
        builder.define(DATA_TAIL_COUNTER, 0);
    }

    /** Client-visual only, matching the original: how long the open-mouth/tail-wag pose lasts. */
    public int getMouthCounter() {
        return this.entityData.get(DATA_MOUTH_COUNTER);
    }

    public int getTailCounter() {
        return this.entityData.get(DATA_TAIL_COUNTER);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WolfAttackGoal(this, ATTACK_SPEED));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new WolfTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new WolfTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new WolfTargetGoal<>(this, Pig.class, false));
        this.targetSelector.addGoal(4, new WolfTargetGoal<>(this, Cow.class, false));
        this.targetSelector.addGoal(4, new WolfTargetGoal<>(this, Sheep.class, false));
        this.targetSelector.addGoal(4, new WolfTargetGoal<>(this, Chicken.class, false));
    }

    public WildWolfVariant getVariant() {
        return WildWolfVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(WildWolfVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        // Original: checkSpawningBiome() forces TIMBER in a cold/snowy/frozen/ice biome, checked
        // before the normal 1-5 roll, which only fires if a variant hasn't already been assigned.
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        ResourceKey<Biome> biomeKey = biome.unwrapKey().orElse(null);
        boolean cold = biomeKey != null && isColdBiomePath(biomeKey.location().getPath());
        this.setVariant(cold ? WildWolfVariant.TIMBER : WildWolfVariant.random(this.random));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private static boolean isColdBiomePath(String path) {
        return path.contains("snow") || path.contains("frozen") || path.contains("ice") || path.contains("cold");
    }

    // ---------------------------------------------------------------------
    // Ticking: mouth/tail pose timers, and the skeleton/zombie jockey
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.random.nextInt(TAIL_WAG_CHANCE) == 0) {
            this.entityData.set(DATA_TAIL_COUNTER, 1);
        }
        int mouth = this.getMouthCounter();
        if (mouth > 0) {
            this.entityData.set(DATA_MOUTH_COUNTER, mouth + 1 > MOUTH_OPEN_DURATION ? 0 : mouth + 1);
        }
        int tail = this.getTailCounter();
        if (tail > 0) {
            this.entityData.set(DATA_TAIL_COUNTER, tail + 1 > TAIL_WAG_DURATION ? 0 : tail + 1);
        }
        this.tickJockey();
    }

    /** Original: livingUpdate() — lets a nearby skeleton/zombie mount it, like a spider jockey. */
    private void tickJockey() {
        if (this.isVehicle() || this.random.nextInt(JOCKEY_CHANCE) != 0) {
            return;
        }
        AABB area = this.getBoundingBox().inflate(JOCKEY_SEARCH_RADIUS_XZ, JOCKEY_SEARCH_RADIUS_Y, JOCKEY_SEARCH_RADIUS_XZ);
        for (Monster monster : this.level().getEntitiesOfClass(Monster.class, area,
                m -> m.getVehicle() == null && (m instanceof Skeleton || m instanceof Zombie))) {
            monster.startRiding(this);
            break;
        }
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            double dist = 0.1D;
            double x = this.getX() + dist * Math.sin(this.getYRot() / 57.29578F);
            double z = this.getZ() - dist * Math.cos(this.getYRot() / 57.29578F);
            double y = this.getY() + this.getBbHeight() * 0.75D - 0.1D;
            moveFunction.accept(passenger, x, y, z);
            passenger.setYRot(this.getYRot());
        }
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
        this.entityData.set(DATA_MOUTH_COUNTER, 1);
        return ModSounds.WILD_WOLF_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        this.entityData.set(DATA_MOUTH_COUNTER, 1);
        return ModSounds.WILD_WOLF_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WILD_WOLF_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-2 fur, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        MoCLootUtil.dropItems(this, ModItems.FUR.get(), MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }


    // ---------------------------------------------------------------------
    // Darkness-gated goals (attacks on sight, but only while it's dark)
    // ---------------------------------------------------------------------

    /** Wiki: once it starts fighting, it keeps going even if the light rises afterward — no
     *  give-up-on-light behaviour here, unlike the Ogre. */
    private static final class WolfAttackGoal extends MeleeAttackGoal {
        WolfAttackGoal(MoCWildWolfEntity wolf, double speedModifier) {
            super(wolf, speedModifier, false);
        }
    }

    private static final class WolfTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCWildWolfEntity wolf;

        WolfTargetGoal(MoCWildWolfEntity wolf, Class<T> targetClass, boolean mustSee) {
            super(wolf, targetClass, mustSee);
            this.wolf = wolf;
        }

        @Override
        public boolean canUse() {
            return this.wolf.getBrightness() < MAX_ATTACK_BRIGHTNESS && super.canUse();
        }
    }

    /** No getBrightness() in 1.21.1 — same helper already used on the Ogre. */
    private float getBrightness() {
        return this.level().getMaxLocalRawBrightness(this.blockPosition()) / 15.0F;
    }
}