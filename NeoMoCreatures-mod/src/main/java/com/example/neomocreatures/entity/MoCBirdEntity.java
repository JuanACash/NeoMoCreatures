package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bird.BirdVariant;

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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityBird}: a small
 * flying creature with 6 colour variants ({@link BirdVariant}).
 * <p>
 * Step 1 only: skeleton, variants, and basic passive flying/wander/flee
 * behaviour. Intentionally NOT implemented yet (later steps): seeking out
 * and eating wheat/melon seeds on the ground, taming (feeding it again once
 * pre-tamed), the {@code CarriedPet} pickup, and perching in trees.
 * <p>
 * Extends {@link TamableAnimal} for the same reason as the other recent
 * ports: taming later doesn't require re-registering the entity type.
 */
public class MoCBirdEntity extends TamableAnimal {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBirdEntity.class, EntityDataSerializers.INT);

    public MoCBirdEntity(EntityType<? extends MoCBirdEntity> type, Level level) {
        super(type, level);
        // Same movement style as vanilla's Parrot: smooth flight control
        // instead of the ground-mob default, so it doesn't jerk around
        // while airborne.
        this.moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, 10, false);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        super.travel(travelVector);
        // Always glides down slowly instead of falling at normal gravity —
        // a small flyer shouldn't ever plummet.
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, Math.max(motion.y, -0.15D), motion.z);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Original's EntityAIFleeFromEntityMoC: flee from anything bigger
        // than a small animal, except other birds.
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.0D, 1.3D,
                livingEntity -> !(livingEntity instanceof MoCBirdEntity)
                        && (livingEntity.getBbWidth() > 0.4F || livingEntity.getBbHeight() > 0.4F)));
        // Same wander style as vanilla's Parrot: alternates between flying
        // to a random nearby spot and hopping/walking on the ground,
        // instead of only ever flying like MoCWyvernEntity does.
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.15D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BirdVariant.BLUE.getId());
    }

    public BirdVariant getVariant() {
        return BirdVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(BirdVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("BirdVariant", getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BirdVariant", 8)) {
            try {
                setVariant(BirdVariant.valueOf(tag.getString("BirdVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Taming happens by feeding it seeds twice (a later step), not vanilla food-taming.
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    /** Original's getTexture()'s per-colour ambient sound. */
    @Override
    protected SoundEvent getAmbientSound() {
        return switch (getVariant()) {
            case WHITE -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_WHITE.get();
            case BLACK -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_BLACK.get();
            case GREEN -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_GREEN.get();
            case BLUE -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_BLUE.get();
            case YELLOW -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_YELLOW.get();
            case RED -> com.example.neomocreatures.init.ModSounds.BIRD_AMBIENT_RED.get();
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Original reuses vanilla's parrot hurt/death sounds directly.
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }
}