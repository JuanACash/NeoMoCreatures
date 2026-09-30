package com.example.neomocreatures.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

/**
 * Step for LitterBox: placeable furniture entity + cleaning + attracting
 * hostiles while dirty. Kitty using it (getting it dirty, sitting on it) is
 * a later step — for now setUsedLitter(true) is only reachable from outside
 * (future Kitty code), never triggered internally here.
 */
public class MoCLitterBoxEntity extends Mob {

    private static final EntityDataAccessor<Boolean> DATA_PICKED_UP =
            SynchedEntityData.defineId(MoCLitterBoxEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_USED_LITTER =
            SynchedEntityData.defineId(MoCLitterBoxEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int AUTO_RESET_TICKS = 5000;
    private static final double ATTRACT_RADIUS = 12.0D;

    private int litterTime;

    public MoCLitterBoxEntity(EntityType<? extends MoCLitterBoxEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PICKED_UP, false);
        builder.define(DATA_USED_LITTER, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D);
    }

    /** Placed furniture, not a wild mob: it must never despawn when players wander off. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void registerGoals() {
        // No AI — it never moves on its own.
    }

    public boolean isPickedUp() {
        return this.entityData.get(DATA_PICKED_UP);
    }

    public void setPickedUp(boolean value) {
        this.entityData.set(DATA_PICKED_UP, value);
    }

    public boolean isUsedLitter() {
        return this.entityData.get(DATA_USED_LITTER);
    }

    public void setUsedLitter(boolean value) {
        this.entityData.set(DATA_USED_LITTER, value);
        this.litterTime = 0;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return !this.isRemoved();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false; // invulnerable — matches the original's always-false attackEntityFrom
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.isEmpty() && stack.is(Items.SAND)) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(net.minecraft.sounds.SoundEvents.SAND_PLACE, 1.0F, 1.0F);
                setUsedLitter(false);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.getVehicle() == null) {
            if (player.isShiftKeyDown()) {
                if (!this.level().isClientSide) {
                    player.getInventory().add(new ItemStack(com.example.neomocreatures.init.ModItems.KITTY_LITTER.get()));
                    this.playSound(net.minecraft.sounds.SoundEvents.ITEM_PICKUP, 0.2F,
                            ((this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F));
                    this.discard();
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide) {
                this.setYRot(Math.round(this.getYRot() / 90.0F) * 90.0F + 90.0F);
                this.setYHeadRot(this.getYRot());
                this.playSound(net.minecraft.sounds.SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround()) {
            setPickedUp(false);
        }

        if (isUsedLitter()) {
            if (!this.level().isClientSide) {
                this.litterTime++;
                for (Monster monster : this.level().getEntitiesOfClass(Monster.class,
                        this.getBoundingBox().inflate(ATTRACT_RADIUS, 4.0D, ATTRACT_RADIUS))) {
                    monster.setTarget(this);
                }
                if (this.litterTime > AUTO_RESET_TICKS) {
                    setUsedLitter(false);
                }
            } else {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("UsedLitter", isUsedLitter());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setUsedLitter(tag.getBoolean("UsedLitter"));
    }

    @Override
    protected void positionRider(net.minecraft.world.entity.Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        moveFunction.accept(passenger, this.getX(), this.getY() + 0.2D, this.getZ());
    }
}