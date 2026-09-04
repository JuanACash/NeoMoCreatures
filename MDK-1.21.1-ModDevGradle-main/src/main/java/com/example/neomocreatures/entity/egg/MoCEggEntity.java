package com.example.neomocreatures.entity.egg;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModEntities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 1:1-in-spirit port of drzhark.mocreatures.entity.item.MoCEntityEgg, minus
 * the aquatic-hatching branch (no aquatic tameable exists yet — add it back
 * the same way the original branches on isInWater() if/when one does).
 *
 * Generic on purpose: it only knows a target EntityType and an optional
 * variant string. Whatever hatches out, if it implements EggHatchable, gets
 * handed the nearest player to imprint on — the egg itself never needs to
 * know wyvern, or whatever other creature uses this next, exists.
 *
 * Per design: every egg is the same size regardless of what's inside (no
 * per-species getSize() scaling like the original had for ostrich eggs).
 */
public class MoCEggEntity extends Mob {

    /** Ticks (in ~1-in-20-chance increments) before the egg hatches. */
    private static final int HATCH_THRESHOLD = 30;
    /** At this many increments, nearby players get a "something's hatching" message. */
    private static final int NOTIFY_AT = 5;
    /** If left unwatched this many increments, the egg gives up and despawns. */
    private static final int DESPAWN_UNWATCHED_THRESHOLD = 500;
    private static final double WATCH_RADIUS = 24.0D;

    private ResourceLocation hatchEntityId;
    @Nullable
    private String hatchVariant;

    private int hatchTicks;
    private int unwatchedTicks;

    public MoCEggEntity(EntityType<? extends MoCEggEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setSilent(true);
        this.hatchEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.WYVERN.get());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected void registerGoals() {
        // Deliberately no goals — it just sits there until it hatches.
    }

    public void setHatchEntityId(ResourceLocation hatchEntityId) {
        this.hatchEntityId = hatchEntityId;
    }

    public void setHatchVariant(@Nullable String hatchVariant) {
        this.hatchVariant = hatchVariant;
    }

    @Override
    public void tick() {
        super.tick();
        // Keeps it from drifting off with currents/collisions — it only ever
        // moves vertically (e.g. water bobbing, if an aquatic branch is added).
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);

        if (this.level().isClientSide) {
            return;
        }

        if (this.random.nextInt(20) == 0) {
            this.unwatchedTicks++;
        }
        if (this.unwatchedTicks > DESPAWN_UNWATCHED_THRESHOLD
                && this.level().getNearestPlayer(this, WATCH_RADIUS) == null) {
            this.discard();
            return;
        }

        if (this.random.nextInt(20) == 0) {
            this.hatchTicks++;
            if (this.hatchTicks == NOTIFY_AT) {
                notifyNearbyPlayer();
            }
            if (this.hatchTicks >= HATCH_THRESHOLD) {
                hatch();
            }
        }
    }

    private void notifyNearbyPlayer() {
        Player player = this.level().getNearestPlayer(this, WATCH_RADIUS);
        if (player != null) {
            player.sendSystemMessage(Component.translatable("msg.neomocreatures.egg_hatching",
                    this.getBlockX(), this.getBlockY(), this.getBlockZ()));
        }
    }

    private void hatch() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        EntityType<?> hatchType = BuiltInRegistries.ENTITY_TYPE.get(this.hatchEntityId);
        Entity spawned = hatchType.create(serverLevel);
        if (spawned != null) {
            spawned.moveTo(this.getX(), this.getY(), this.getZ(), this.random.nextFloat() * 360F, 0F);
            if (spawned instanceof AgeableMob ageable) {
                ageable.setAge(-24000);
            }
            serverLevel.addFreshEntity(spawned);

            Player tamer = this.level().getNearestPlayer(this, WATCH_RADIUS);
            if (spawned instanceof EggHatchable hatchable) {
                hatchable.onHatchedFromEgg(tamer, this.hatchVariant);
            }
        }

        this.playSound(SoundEvents.CHICKEN_EGG, 0.4F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("HatchEntityType", this.hatchEntityId.toString());
        if (this.hatchVariant != null) {
            tag.putString("HatchVariant", this.hatchVariant);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HatchEntityType", 8)) {
            this.hatchEntityId = ResourceLocation.parse(tag.getString("HatchEntityType"));
        }
        this.hatchVariant = tag.contains("HatchVariant", 8) ? tag.getString("HatchVariant") : null;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }
}