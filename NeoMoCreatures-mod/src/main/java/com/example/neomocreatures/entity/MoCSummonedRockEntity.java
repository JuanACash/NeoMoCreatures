package com.example.neomocreatures.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.golem.GolemBody;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Port of the "summoned" behaviour (2) of {@code MoCEntityThrowableRock}: a block the Big Golem tore
 * out of the ground, flying back to it to become part of its body. It passes through terrain so it
 * can never get stuck, hurts whatever it brushes past, and if its golem is gone it breaks, giving
 * the block back when it was really taken out of the world.
 */
public class MoCSummonedRockEntity extends Entity implements CarriedBlockEntity {

    /** Original: summoned rocks hit for 4 too. */
    private static final float IMPACT_DAMAGE = 4.0F;
    /** Original: speed divisor starts at 100 and drops by 1 per tick down to 10 — it accelerates as it flies. */
    private static final int START_SLOWNESS = 100;
    private static final int MIN_SLOWNESS = 10;
    /** Original: lift of 0.15 per tick plus 1/20 of the height difference. */
    private static final double LIFT = 0.15D;
    private static final double VERTICAL_DIVISOR = 20.0D;
    /** Original: joins the golem once within 1.5 (squared, horizontal) of it. */
    private static final double ATTACH_DISTANCE_SQR = 1.5D;
    /** Safety net: breaks after 30 s if it somehow never reaches its golem. */
    private static final int MAX_LIFE_TICKS = 600;
    /** Original behaviour 4: rocks pulled by a dying golem stop and float once within 2.5 (squared, horizontal). */
    private static final double HOVER_DISTANCE_SQR = 2.5D;
    private static final String TAG_HOVERING = "Hovering";

    private static final String TAG_BLOCK_STATE = "BlockState";
    private static final String TAG_RETURNABLE = "Returnable";
    private static final String TAG_GOLEM = "Golem";
    private static final String TAG_LIFE = "Life";

    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE =
            SynchedEntityData.defineId(MoCSummonedRockEntity.class, EntityDataSerializers.BLOCK_STATE);

    /** Server-only: true when the block was really torn out of the world. */
    private boolean returnable;
    @Nullable
    private UUID golemId;
    private int slowness = START_SLOWNESS;
    private int life;

    /** True for rocks pulled by a dying golem: they gather around it instead of attaching. */
    private boolean hovering;

    public MoCSummonedRockEntity(EntityType<? extends MoCSummonedRockEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MoCSummonedRockEntity(Level level, MoCBigGolemEntity golem, GolemBody.Cube cube, double x, double y, double z) {
        this(ModEntities.MOC_SUMMONED_ROCK.get(), level);
        this.setPos(x, y, z);
        this.entityData.set(DATA_BLOCK_STATE, cube.state());
        this.returnable = cube.returnable();
        this.golemId = golem.getUUID();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLOCK_STATE, Blocks.STONE.defaultBlockState());
    }

    @Override
    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE);
    }

    /** Marks this rock as one that floats around a dying golem instead of joining its body. */
    public MoCSummonedRockEntity hovering() {
        this.hovering = true;
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        MoCBigGolemEntity golem = this.findGolem();
        if (golem == null || !golem.isAlive() || ++this.life >= MAX_LIFE_TICKS) {
            this.shatter();
            return;
        }

        this.hurtTouchedEntities(golem);

        double dx = golem.getX() - this.getX();
        double dz = golem.getZ() - this.getZ();
        double distanceSqr = dx * dx + dz * dz;
        if (this.hovering) {
            // Original behaviour 4: gathers around the dying golem and floats there until it bursts.
            this.slowness = MIN_SLOWNESS;
            if (distanceSqr < HOVER_DISTANCE_SQR) {
                this.setDeltaMovement(Vec3.ZERO);
                return;
            }
        } else if (distanceSqr < ATTACH_DISTANCE_SQR) {
            golem.receiveRock(new GolemBody.Cube(this.getBlockState(), this.returnable));
            this.discard();
            return;
        } else {
            this.slowness = Math.max(MIN_SLOWNESS, this.slowness - 1);
        }

        this.setDeltaMovement(dx / this.slowness, (golem.getY() - this.getY()) / VERTICAL_DIVISOR + LIFT, dz / this.slowness);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /** Original: rocks hurt any living thing they fly into, except golems. */
    private void hurtTouchedEntities(MoCBigGolemEntity golem) {
        List<LivingEntity> touched = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(),
                entity -> entity.isAlive() && !(entity instanceof RockThrower));
        for (LivingEntity entity : touched) {
            entity.hurt(this.damageSources().mobAttack(golem), IMPACT_DAMAGE);
        }
    }

    @Nullable
    private MoCBigGolemEntity findGolem() {
        if (this.golemId != null && this.level() instanceof ServerLevel serverLevel
                && serverLevel.getEntity(this.golemId) instanceof MoCBigGolemEntity golem) {
            return golem;
        }
        return null;
    }

    /** Breaks apart where it is, giving the block back as an item when it was really taken from the world. */
    private void shatter() {
        BlockState state = this.getBlockState();
        this.level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, this.blockPosition(), Block.getId(state));
        if (this.returnable) {
            ItemStack blockItem = new ItemStack(state.getBlock());
            if (!blockItem.isEmpty()) {
                this.spawnAtLocation(blockItem);
            }
        }
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    // ---------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put(TAG_BLOCK_STATE, NbtUtils.writeBlockState(this.getBlockState()));
        tag.putBoolean(TAG_RETURNABLE, this.returnable);
        tag.putInt(TAG_LIFE, this.life);
        tag.putBoolean(TAG_HOVERING, this.hovering);
        if (this.golemId != null) {
            tag.putUUID(TAG_GOLEM, this.golemId);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains(TAG_BLOCK_STATE, Tag.TAG_COMPOUND)) {
            this.entityData.set(DATA_BLOCK_STATE,
                    NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound(TAG_BLOCK_STATE)));
        }
        this.returnable = tag.getBoolean(TAG_RETURNABLE);
        this.life = tag.getInt(TAG_LIFE);
        this.hovering = tag.getBoolean(TAG_HOVERING);
        this.golemId = tag.hasUUID(TAG_GOLEM) ? tag.getUUID(TAG_GOLEM) : null;
    }
}