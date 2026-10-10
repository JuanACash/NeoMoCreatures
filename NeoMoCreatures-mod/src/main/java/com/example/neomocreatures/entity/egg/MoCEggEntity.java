package com.example.neomocreatures.entity.egg;

import javax.annotation.Nullable;

import com.example.neomocreatures.Config;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

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
 *
 * Wiki behaviour: never moves under its own power, but can be pushed by
 * mobs/players/pistons (not affected by knockback specifically); can be
 * killed like a mob (puffs white smoke on death); and can be picked back up
 * like a dropped item by simply walking into it, as long as it hasn't
 * already hatched.
 */
public class MoCEggEntity extends Mob {

    /** Ticks (in ~1-in-20-chance increments) before the egg hatches. */
    private static final int HATCH_THRESHOLD = 30;
    /** At this many increments, nearby players get a "keep watch!" message. */
    private static final int NOTIFY_AT = 5;
    /** If left unwatched this many increments, the egg gives up and despawns. */
    private static final int DESPAWN_UNWATCHED_THRESHOLD = 500;
    private static final double WATCH_RADIUS = 24.0D;
    /** Wiki: stray more than ~9 blocks away and the hatched baby comes out wild. */
    private static final double TAME_RADIUS = 9.0D;
    /** Block-light level of a torch (14) and above counts as "near a torch". */
    private static final int MIN_LIGHT_TO_HATCH = 14;
    /** Wiki-inspired: can't be picked back up in the first few seconds after being placed. */
    private static final int PICKUP_DELAY_TICKS = 60;

    private ResourceLocation hatchEntityId;
    @Nullable
    private String hatchVariant;
    /** Which item to hand back if this egg gets picked back up (see playerTouch()). */
    @Nullable
    private ResourceLocation sourceItemId;
    private boolean requiresLight = true;
    private boolean requiresWater = false;
    private boolean requirePickupToTame = false;
    private boolean wasPickedUp = false;

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

    public void setSourceItemId(@Nullable ResourceLocation sourceItemId) {
        this.sourceItemId = sourceItemId;
    }

    public void setRequiresLight(boolean requiresLight) {
        this.requiresLight = requiresLight;
    }

    public void setRequiresWater(boolean requiresWater) {
        this.requiresWater = requiresWater;
    }

    public void setRequirePickupToTame(boolean requirePickupToTame) {
        this.requirePickupToTame = requirePickupToTame;
    }

    public void setWasPickedUp(boolean wasPickedUp) {
        this.wasPickedUp = wasPickedUp;
    }

    /** Wiki: not affected by knockback (but still pushable by mobs/players/pistons). */
    @Override
    public void knockback(double strength, double x, double z) {
    }

    /**
     * Mob#travel() (where gravity is applied) only runs when the entity is
     * "controlled by local instance", which Mob computes as !isNoAi() — so a
     * noAi mob like this egg would otherwise never fall. Forcing this back to
     * true restores normal gravity while keeping goal-selector AI disabled.
     */
    @Override
    public boolean isControlledByLocalInstance() {
        return true;
    }

    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        // No egg (of any creature) should ever drown — the shark egg in
        // particular needs to sit submerged in water to hatch at all.
        return false;
    }

    @Override
    public void tick() {
        super.tick();

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
            boolean lightOk = !this.requiresLight
                    || this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) >= MIN_LIGHT_TO_HATCH;
            // Wiki: shark eggs "can only hatch if they are in the water" — no torch needed.
            boolean waterOk = !this.requiresWater || this.isInWater();
            if (lightOk && waterOk) {
                this.hatchTicks++;
                if (this.hatchTicks == NOTIFY_AT) {
                    notifyNearbyPlayer();
                }
                if (this.hatchTicks >= HATCH_THRESHOLD) {
                    hatch();
                }
            }
        }
    }

    private void notifyNearbyPlayer() {
        if (!Config.CREATURES.eggWarningMessages.get()) {
            return;
        }
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

            // Wiki (species-specific for the ostrich): only tames if a player actually
            // picked this exact egg up and placed it back down themselves — mere
            // proximity at hatch time doesn't count. Other species keep the original
            // proximity-based rule.
            Player tamer = (!this.requirePickupToTame || this.wasPickedUp)
                    ? this.level().getNearestPlayer(this, TAME_RADIUS)
                    : null;
            if (spawned instanceof EggHatchable hatchable) {
                hatchable.onHatchedFromEgg(tamer, this.hatchVariant);
            }
        }

        this.playSound(SoundEvents.CHICKEN_EGG, 0.4F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        this.discard();
    }

    /** Wiki: "You can pick up an egg like a dropped item by simply walking over it." */
    @Override
    public void playerTouch(Player player) {
        if (this.level().isClientSide || this.tickCount < PICKUP_DELAY_TICKS) {
            return;
        }

        ResourceLocation itemId = this.sourceItemId != null
                ? this.sourceItemId
                : BuiltInRegistries.ITEM.getKey(ModItems.MOC_EGG.get());
        Item item = BuiltInRegistries.ITEM.get(itemId);
        ItemStack stack = new ItemStack(item);
        if (this.hatchVariant != null || this.requirePickupToTame) {
            CompoundTag data = new CompoundTag();
            if (this.hatchVariant != null) {
                data.putString("HatchVariant", this.hatchVariant);
            }
            data.putBoolean("WasPickedUp", true);
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(data));
        }

        if (player.getInventory().add(stack)) {
            this.playSound(SoundEvents.ITEM_PICKUP, 0.2F,
                    ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.take(this, 1);

            if (this.hatchEntityId.equals(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.MOC_OSTRICH.get()))) {
                com.example.neomocreatures.entity.MoCOstrichEntity.alertNearbyOstriches(
                        this.level(), this.position(), player, 15.0D);
            }
        } else {
            return;
        }
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("HatchEntityType", this.hatchEntityId.toString());
        if (this.hatchVariant != null) {
            tag.putString("HatchVariant", this.hatchVariant);
        }
        if (this.sourceItemId != null) {
            tag.putString("SourceItem", this.sourceItemId.toString());
        }
        tag.putBoolean("RequiresLight", this.requiresLight);
        tag.putBoolean("RequiresWater", this.requiresWater);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HatchEntityType", 8)) {
            this.hatchEntityId = ResourceLocation.parse(tag.getString("HatchEntityType"));
        }
        if (tag.contains("RequiresLight")) {
            this.requiresLight = tag.getBoolean("RequiresLight");
        }
        if (tag.contains("RequiresWater")) {
            this.requiresWater = tag.getBoolean("RequiresWater");
        }
        this.hatchVariant = tag.contains("HatchVariant", 8) ? tag.getString("HatchVariant") : null;
        this.sourceItemId = tag.contains("SourceItem", 8) ? ResourceLocation.parse(tag.getString("SourceItem")) : null;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /** Wiki: "emit a puff of white smoke after death." */
    @Override
    protected void tickDeath() {
        if (this.deathTime == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.3D, this.getZ(),
                    8, 0.2D, 0.2D, 0.2D, 0.02D);
        }
        super.tickDeath();
    }
}