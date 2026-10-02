package com.example.neomocreatures.entity.monster;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Aggressive, untameable horse variant — ported from MoCEntityHorseMob.
 * First pass: entity + basic hostile behavior + which texture to show.
 * Not yet ported: sounds, natural spawning rules, drops, wings/horn
 * geometry for the bathorse, the nightmare's animated/decaying textures
 * (using one static frame each for now).
 */
public class MoCHorseMobEntity extends Monster {

    public enum Variant {
        UNDEAD, SKELETON, BATHORSE, NIGHTMARE
    }

    private static final EntityDataAccessor<Integer> DATA_DECAY_STAGE =
        SynchedEntityData.defineId(MoCHorseMobEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCHorseMobEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCHorseMobEntity.class, EntityDataSerializers.INT);
    private static final int MOUTH_OPEN_TICKS = 30;



    public MoCHorseMobEntity(EntityType<? extends MoCHorseMobEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.UNDEAD.ordinal());
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_DECAY_STAGE, 0);
    }

    private boolean isUndeadOrSkeleton() {
        return getVariant() == Variant.UNDEAD || getVariant() == Variant.SKELETON;
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void openMouth() {
        this.entityData.set(DATA_MOUTH_TICKS, MOUTH_OPEN_TICKS);
    }

    public int getDecayStage() {
        return this.entityData.get(DATA_DECAY_STAGE);
    }

    @Override
    protected void positionRider(net.minecraft.world.entity.Entity passenger, MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() - Math.sin(yaw) * -0.15F;
        double z = this.getZ() + Math.cos(yaw) * -0.15F;
        double y = this.getY() + 0.7F;
        moveFunction.accept(passenger, x, y, z);

        // Lock the rider's facing to match the mount exactly, every tick —
        // without this, the rider's own AI (e.g. LookAtPlayerGoal) can still
        // turn its head/body independently of which way the horse is facing.
        passenger.setYRot(this.getYRot());
        passenger.setXRot(this.getXRot());
        if (passenger instanceof net.minecraft.world.entity.LivingEntity livingPassenger) {
            livingPassenger.yBodyRot = this.getYRot();
            livingPassenger.yHeadRot = this.getYRot();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        openMouth();
        return this.random.nextBoolean() ? ModSounds.HORSE_MOB_GRUNT1.get() : ModSounds.HORSE_MOB_GRUNT2.get();
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        openMouth();
        return ModSounds.HORSE_MOB_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HORSE_MOB_DEATH.get();
    }

    /** Undead/skeleton take damage from healing and are healed by harming,
     * matching vanilla zombies/skeletons. */
    @Override
    public boolean isInvertedHealAndHarm() {
        return isUndeadOrSkeleton();
    }

    /** Immune to poison, same as vanilla undead. */
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (isUndeadOrSkeleton() && effect.getEffect().value() == MobEffects.POISON.value()) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && !this.level().isClientSide) {
            this.playSound(ModSounds.HORSE_MOB_AGGRESSIVE.get(), 1.0F, 1.0F);
            openMouth();
        }
        return result;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        if (!this.level().isClientSide && getVariant() == Variant.UNDEAD) {
            Slime slime = net.minecraft.world.entity.EntityType.SLIME.create(this.level());
            if (slime != null) {
                slime.setSize(1, true);
                slime.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
                this.level().addFreshEntity(slime);
            }
        }
        super.die(damageSource);
    }

    public Variant getVariant() {
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("MoCVariant", getVariant().name());
        tag.putInt("MoCDecayStage", getDecayStage());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("MoCVariant")) {
            setVariant(Variant.valueOf(tag.getString("MoCVariant")));
        }
        if (tag.contains("MoCDecayStage")) {
            this.entityData.set(DATA_DECAY_STAGE, tag.getInt("MoCDecayStage"));
        }
    }

    /** Nightmares are fire-immune (from the nether); everything else burns
     * in daylight like the original mod's isHarmedByDaylight(). */
    @Override
    public boolean fireImmune() {
        return this.getVariant() == Variant.NIGHTMARE || super.fireImmune();
    }

    /** Zombified piglins are neutral — a nightmare they're riding shouldn't
     * chase the player proactively, only react if the player gets close. */
    private boolean canTargetPlayer(net.minecraft.world.entity.LivingEntity target) {
        if (this.getFirstPassenger() instanceof net.minecraft.world.entity.monster.ZombifiedPiglin) {
            return this.distanceTo(target) <= 4.0F;
        }
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level().isDay() && !this.fireImmune() && !this.isInWaterOrRain()) {
            float brightness = this.getLightLevelDependentMagicValue();
            BlockPos pos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
            if (brightness > 0.5F && this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                    && this.level().canSeeSky(pos)) {
                this.igniteForSeconds(8);
            }
        }

        if (!this.level().isClientSide && this.getMouthTicks() > 0) {
            this.entityData.set(DATA_MOUTH_TICKS, this.getMouthTicks() - 1);
        }

        if (!this.level().isClientSide && getVariant() == Variant.BATHORSE) {
            updateFlight();
        }
    }
    @Override
    protected int getBaseExperienceReward() {
        return 5;
    }

    @Override
    public boolean shouldDropExperience() {
        return super.shouldDropExperience() || this.getLastHurtByMob() instanceof net.minecraft.world.entity.animal.Wolf;
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);

        net.minecraft.world.entity.LivingEntity killer = this.getLastHurtByMob();
        // Any wolf counts here (tamed or not), unlike the rest of the mod.
        if (!recentlyHitByPlayer && !(killer instanceof net.minecraft.world.entity.animal.Wolf)) {
            return;
        }

        int lootingLevel = MoCLootUtil.getLootingLevel(killer);

        // Base 0-2, Looting raises the max (same as vanilla: +1 to the cap per level).
        int commonDropCount = MoCLootUtil.rollWithLootingRange(this.random, 3, lootingLevel);
        // Base 25% (1 in 4), Looting reduces the denominator to make it more likely, floored at 1 (100%).
        int rareChanceDenominator = Math.max(1, 4 - lootingLevel);
        boolean heartDrops = this.random.nextInt(rareChanceDenominator) == 0;
        int heartCount = heartDrops ? 1 + this.random.nextInt(2) : 0;

        switch (getVariant()) {
            case BATHORSE -> {
                MoCLootUtil.dropItems(this, net.minecraft.world.item.Items.LEATHER, commonDropCount);
                MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.HEART_OF_DARKNESS.get(), heartCount);
            }
            case NIGHTMARE -> {
                MoCLootUtil.dropItems(this, net.minecraft.world.item.Items.LEATHER, commonDropCount);
                MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.HEART_OF_FIRE.get(), heartCount);
            }
            case SKELETON -> {
                MoCLootUtil.dropItems(this, net.minecraft.world.item.Items.BONE, commonDropCount);
            }
            case UNDEAD -> {
                MoCLootUtil.dropItems(this, net.minecraft.world.item.Items.ROTTEN_FLESH, commonDropCount);
                MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.HEART_OF_UNDEAD.get(), heartCount);
            }
        }
    }

    /** Keeps the bathorse gliding roughly 1-10 blocks above the ground, like
     * the original's min/maxFlyingHeight — not a full flight-pathing system
     * (no FlyingPathNavigation yet), just vertical drift while it walks/chases
     * normally on the X/Z plane. */


    public boolean isSoaring() {
        BlockPos pos = BlockPos.containing(
                this.getX(),
                this.getY() - 0.2D,
                this.getZ()
        );

        return this.level().getBlockState(pos).isAir();
    }

    private void updateFlight() {
        net.minecraft.world.entity.LivingEntity target = this.getTarget();
        boolean hunting = target != null && target.getY() > this.getY() + 1.5;

        this.setNoGravity(hunting);

        if (hunting) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.setYRot(yaw);
            this.yBodyRot = yaw;
            this.yHeadRot = yaw;
            
            net.minecraft.world.phys.Vec3 toTarget = new net.minecraft.world.phys.Vec3(
                    target.getX() - this.getX(),
                    (target.getY() + target.getBbHeight() * 0.5) - this.getY(),
                    target.getZ() - this.getZ());
            double dist = toTarget.length();
            if (dist > 0.5) {
                net.minecraft.world.phys.Vec3 dir = toTarget.normalize().scale(0.06);
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9).add(dir));
            }
        } else if (!this.onGround()) {
            if (this.getDeltaMovement().y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.6, 1));
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canTargetPlayer));    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason,
                                        @Nullable SpawnGroupData spawnGroupData) {
        if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
            setVariant(Variant.NIGHTMARE);
            if (this.random.nextFloat() < 0.3F) {
                spawnRider(level, pickNightmareRiderType(level));
            }
        } else {
            RandomSource random = this.random;
            int roll = random.nextInt(100);
            if (roll <= 40) {
                setVariant(Variant.UNDEAD);
                this.entityData.set(DATA_DECAY_STAGE, this.random.nextInt(4)); // 0-3
            } else if (roll <= 80) {
                setVariant(Variant.SKELETON);
            } else {
                setVariant(Variant.BATHORSE);
            }
            if (this.random.nextFloat() < 0.15F) {
                EntityType<?> riderType = this.random.nextBoolean() ? EntityType.ZOMBIE : EntityType.SKELETON;
                spawnRider(level, riderType);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    /** Wither skeletons only if this nightmare is inside a nether fortress;
     * otherwise zombified piglin, skeleton, or a normal piglin. */
    private EntityType<?> pickNightmareRiderType(ServerLevelAccessor level) {
        net.minecraft.world.level.levelgen.structure.Structure fortress = level.getLevel().registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE)
                .get(net.minecraft.world.level.levelgen.structure.BuiltinStructures.FORTRESS);

        boolean nearFortress = fortress != null && level.getLevel().structureManager()
                .getStructureWithPieceAt(this.blockPosition(), fortress)
                .isValid();

        int options = nearFortress ? 4 : 3;
        int pick = this.random.nextInt(options);
        return switch (pick) {
            case 0 -> EntityType.ZOMBIFIED_PIGLIN;
            case 1 -> EntityType.SKELETON;
            case 2 -> EntityType.PIGLIN;
            default -> EntityType.WITHER_SKELETON;
        };
    }

    private void spawnRider(ServerLevelAccessor level, EntityType<?> riderType) {
        net.minecraft.world.entity.Entity rider = riderType.create(level.getLevel());
        if (rider instanceof net.minecraft.world.entity.Mob mob) {
            mob.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
            level.getLevel().addFreshEntity(mob);
            mob.startRiding(this);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        if (getVariant() == Variant.BATHORSE) {
            return false;
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return true;
    }
}