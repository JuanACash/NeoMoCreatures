package com.example.neomocreatures.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageTags;
import com.example.neomocreatures.util.PetStorageUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityShark}. Unlike
 * the older land-based tameables, the original extends its own
 * {@code MoCEntityTameableAquatic} base — a completely separate,
 * pre-{@code TamableAnimal} ownership/breeding framework (its own network
 * packets, its own pet-data tracking). Porting that architecture wholesale
 * would duplicate everything {@link TamableAnimal} + {@link CarriedPet} +
 * our own pet-amulet system already do consistently across every other
 * entity in this mod, so Shark is built on our usual base instead — only
 * the shark-specific behaviour (this file) is a faithful port.
 * <p>
 * Step 1 only: skeleton, swimming, and hunting behaviour. Intentionally NOT
 * implemented yet (step 2): taming and riding it directly (no saddle needed,
 * same as the original), healing. Dolphin-hunting is also not ported yet —
 * there's no MoCDolphinEntity in this project to target.
 */
public class MoCSharkEntity extends TamableAnimal implements EggHatchable, GrowthScaled, StorablePet {

    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    NeoMoCreatures.MODID, "shark_attack_speed_boost");
    private static final double ATTACK_SPEED_BOOST = 1.0D;
    /** Wiki: "suffocate and die when out of water" — ticks up while not in water, resets while in it. */
    private int outOfWaterTicks;
    /** Not in the original — the user's own addition, since baby sharks otherwise render the same size as adults. */
    private static final float BABY_SCALE = 0.5F;

    private static final double BASE_MAX_HEALTH = 30.0D;
    private static final double BASE_MOVEMENT_SPEED = 1.0D;
    private static final double BASE_ATTACK_DAMAGE = 5.0D;
    private static final double BASE_FOLLOW_RANGE = 32.0D;

    private static final int RANDOM_SWIM_INTERVAL = 10;
    /** Ticks between searches for a target (vanilla "random interval"). */
    private static final int TARGET_CHECK_INTERVAL = 5;
    /** A wild shark also hunts players on land within this distance. */
    private static final float LAND_HUNT_DISTANCE = 16.0F;

    /** Out of water: the shark starts taking damage after this many ticks... */
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    /** ...and then every this many ticks. */
    private static final int SUFFOCATION_INTERVAL_TICKS = 20;
    private static final float SUFFOCATION_DAMAGE = 2.0F;

    private static final double EGG_BASE_CHANCE = 0.10D;
    private static final double EGG_LOOTING_BONUS_PER_LEVEL = 0.02D;

    // SharkMoveControl tuning.
    private static final double BUOYANT_NUDGE = 0.005D;
    private static final float SPEED_SMOOTHING = 0.125F;
    private static final double VERTICAL_STEERING = 0.1D;
    private static final float MAX_TURN_DEGREES = 90.0F;
    private final ScaleHitboxSync hitboxSync = new ScaleHitboxSync();

    public MoCSharkEntity(EntityType<? extends MoCSharkEntity> type, Level level) {
        super(type, level);
        // Original's own SharkMoveControl: smooths turning/speed and adds a
        // small buoyant nudge upward while in water.
        this.moveControl = new SharkMoveControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

        
    /**
     * Mob.checkSpawnObstruction() rejects any position with liquid inside the hitbox.
     * The original extended WaterAnimal, which overrides it to allow water; as a
     * TamableAnimal, natural spawning in the ocean would always fail without this.
     */
    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    /**
     * Like vanilla water animals: no preference for light. Animal's version only accepts bright spots,
     * so it barely spawned at night or in deep, dark water.
     */
    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    /** Animal.removeWhenFarAway() returns false: without this, wild sharks would never despawn. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, RANDOM_SWIM_INTERVAL));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Original: hunts players either swimming or within 16 blocks, and
        // squid, but only while wild and not on peaceful.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, TARGET_CHECK_INTERVAL, true, false,
                player -> player != null && !this.isTame() && isReadyToHunt()
                        && (player.isInWater() || this.distanceTo(player) < LAND_HUNT_DISTANCE)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Squid.class, TARGET_CHECK_INTERVAL, false, false,
                squid -> squid != null && !this.isTame() && isReadyToHunt()));
    }

    private boolean isReadyToHunt() {
        return this.level().getDifficulty() != Difficulty.PEACEFUL;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, BASE_FOLLOW_RANGE)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // NeoForge's replacement for the now-final canBreatheUnderwater().
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // Original's isNotScared() always returning true is achieved here
    // simply by never registering a flee/avoid-entity goal at all.

    @Override
    public void aiStep() {
        super.aiStep();
        tickBabyScale();
        if (!this.level().isClientSide) {
            tickOutOfWaterSuffocation();
            tickAttackSpeedBoost();
        }
    }

    /** Not in the original — just makes a baby (freshly hatched) shark render smaller, as requested. */
    private void tickBabyScale() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = this.isBaby() ? BABY_SCALE : 1.0F;
            if (scaleAttr.getBaseValue() != target) {
                scaleAttr.setBaseValue(target);
            }
        }
        this.hitboxSync.sync(this);
    }

    @Override
    public void updateGrowthScale() {
        this.tickBabyScale();
    }

    /** Wiki: "like other mobs that live in water, sharks will suffocate and die when out of water." */
    private void tickOutOfWaterSuffocation() {
        if (this.isInWaterOrBubble()) {
            this.outOfWaterTicks = 0;
            return;
        }
        this.outOfWaterTicks++;
        if (this.outOfWaterTicks > SUFFOCATION_GRACE_TICKS && this.outOfWaterTicks % SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), SUFFOCATION_DAMAGE);
        }
    }

    /** The user's own addition, not in the original: swims faster while actively chasing/attacking a target. */
    private void tickAttackSpeedBoost() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        boolean attacking = this.getTarget() != null && this.getTarget().isAlive();
        boolean boosted = speed.getModifier(ATTACK_SPEED_MODIFIER_ID) != null;
        if (attacking && !boosted) {
            speed.addTransientModifier(new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID, ATTACK_SPEED_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (!attacking && boosted) {
            speed.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        // Original: never attacks a player riding in a boat.
        if (target instanceof Player player && player.getVehicle() instanceof Boat) {
            return false;
        }
        return super.doHurtTarget(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        // Original: doesn't retaliate against its own rider hitting it by accident.
        if (this.isVehicle() && attacker != null && this.getPassengers().contains(attacker)) {
            return false;
        }
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            if (attacker != this && attacker instanceof LivingEntity livingAttacker) {
                this.setTarget(livingAttacker);
            }
        }
        return wasHurt;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Wiki: "sharks cannot be healed by feeding them, as there is no
        // food item that they will accept" — taming is via egg, not food.
        return false;
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        // Wiki: "will hatch into a friendly baby shark" — always tamed on hatch.
        this.setBaby(true);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        // Wiki: "a tamed shark can be moved with the use of a fish net."
        if (this.isTame() && this.isOwnedBy(player)
                && stack.is(ModItems.FISH_NET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private CompoundTag buildNetTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Shark", true);
        PetStorageTags.writeIdentity(tag, this, owner);
        tag.putBoolean("Adult", !this.isBaby());
        return tag;
    }

    private void capturePetInstant(Player player, InteractionHand hand) {
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.FISH_NET_FULL.get(), buildNetTag(player.getUUID()));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Original reuses vanilla's guardian hurt/death sounds — the
        // closest existing large-fish-like vanilla creature.
        return SoundEvents.GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GUARDIAN_DEATH;
    }

    // XP (1-3) is awarded via getBaseExperienceReward() below — vanilla
    // already only grants it when killed by a player or a tamed wolf.
    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        // Wiki: "0-3 shark teeth (60% chance)... Looting does affect the chance... making it more common."
        double toothChance = Math.min(1.0D, 0.6D + lootingLevel * 0.1D);
        if (this.random.nextDouble() < toothChance) {
            MoCLootUtil.dropItems(this, ModItems.SHARK_TEETH.get(),
                    MoCLootUtil.rollWithLootingRange(this.random, 4, lootingLevel));
        }

        // Wiki: "10% chance to drop an egg... only if difficulty is Easy or higher."
        double eggChance = Math.min(1.0D, EGG_BASE_CHANCE + lootingLevel * EGG_LOOTING_BONUS_PER_LEVEL);
        if (this.level().getDifficulty() != Difficulty.PEACEFUL
                && this.random.nextDouble() < eggChance) {
            this.spawnAtLocation(new ItemStack(ModItems.SHARK_EGG.get()));
        }
    }

    /** Original's SharkMoveControl: adds a small buoyant nudge in water on top of the usual smoothed movement toward the target. */
    private static class SharkMoveControl extends MoveControl {
        private final MoCSharkEntity shark;

        SharkMoveControl(MoCSharkEntity shark) {
            super(shark);
            this.shark = shark;
        }

        @Override
        public void tick() {
            if (this.shark.isInWater()) {
                this.shark.setDeltaMovement(this.shark.getDeltaMovement().add(0.0D, BUOYANT_NUDGE, 0.0D));
            }
            if (this.operation == Operation.MOVE_TO && !this.shark.getNavigation().isDone()) {
                float speed = (float) (this.speedModifier * this.shark.getAttributeValue(Attributes.MOVEMENT_SPEED));
                // While it has a live target, snap straight to the target
                // speed instead of gradually lerping toward it — otherwise
                // the attack speed boost takes many ticks to actually show.
                boolean hasTarget = this.shark.getTarget() != null && this.shark.getTarget().isAlive();
                this.shark.setSpeed(hasTarget ? speed : Mth.lerp(SPEED_SMOOTHING, this.shark.getSpeed(), speed));
                double dx = this.wantedX - this.shark.getX();
                double dy = this.wantedY - this.shark.getY();
                double dz = this.wantedZ - this.shark.getZ();
                if (dy != 0.0D) {
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    this.shark.setDeltaMovement(this.shark.getDeltaMovement()
                            .add(0.0D, this.shark.getSpeed() * (dy / dist) * VERTICAL_STEERING, 0.0D));
                }
                if (dx != 0.0D || dz != 0.0D) {
                    float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.shark.setYRot(this.rotlerp(this.shark.getYRot(), yaw, MAX_TURN_DEGREES));
                    this.shark.yBodyRot = this.shark.getYRot();
                }
            } else {
                this.shark.setSpeed(0.0F);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildNetTag} when a Fish Net releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        PetStorageTags.restoreIdentity(tag, this);
        this.setBaby(!tag.getBoolean("Adult"));
    }
}