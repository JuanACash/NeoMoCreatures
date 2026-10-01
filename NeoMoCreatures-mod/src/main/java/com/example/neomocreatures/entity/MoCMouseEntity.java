package com.example.neomocreatures.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.mouse.MouseVariant;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityMouse}. Fully passive: flees the
 * player, no goal ever targets anything. Climbs walls like a Spider. 3 colours, forced to brown in
 * mesa and white in snowy biomes.
 * <p>
 * The original's own tailored attributes (MAX_HEALTH 8, MOVEMENT_SPEED 0.35, defined in a
 * {@code registerAttributes()} method) were never actually wired into its entity registration,
 * which used the generic base creature attributes instead — a real oversight in the source. This
 * port uses the tailored values, since that's clearly what was intended.
 * <p>
 * Deviates from the original's own approach to being carried (riding the player via vanilla's real
 * passenger system, {@code startRiding}) — that turned out not to reliably let go again. This uses
 * the same technique already proven working for the Kitty's own carry system instead: who is
 * holding it lives in a synced UUID, changed only server-side, and both sides follow that holder's
 * position each tick.
 */
public class MoCMouseEntity extends TamableAnimal {

    private static final double MAX_HEALTH = 8.0D;
    private static final double MOVEMENT_SPEED = 0.35D;

    private static final double PANIC_SPEED = 1.4D;
    private static final double FLEE_SPEED = 1.2D;
    private static final float FLEE_DISTANCE = 4.0F;
    private static final double WANDER_SPEED = 1.0D;

    private static final String TAG_VARIANT = "MouseVariant";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCMouseEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING =
            SynchedEntityData.defineId(MoCMouseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCMouseEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public MoCMouseEntity(EntityType<? extends MoCMouseEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, FLEE_DISTANCE, FLEE_SPEED, FLEE_SPEED));
        this.goalSelector.addGoal(2, new PanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, MouseVariant.BEIGE.getId());
        builder.define(DATA_CLIMBING, false);
        builder.define(DATA_HELD_BY, Optional.empty());
    }

    public MouseVariant getVariant() {
        return MouseVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.entityData.set(DATA_VARIANT, tag.getInt(TAG_VARIANT));
        }
    }

    /** Original: climbs like a Spider — any sideways collision counts as a wall to climb. */
    @Override
    public boolean onClimbable() {
        return this.entityData.get(DATA_CLIMBING);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_CLIMBING, this.horizontalCollision);
        }
        this.tickHeld();
    }

    /** Original: selectType() — uniform 1-3; checkSpawningBiome() overrides that with a forced
     *  colour in mesa (brown) or snowy (white) biomes. */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                        net.minecraft.world.entity.MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.entityData.set(DATA_VARIANT, this.rollVariant(level).getId());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private MouseVariant rollVariant(ServerLevelAccessor level) {
        var biomeKey = level.getBiome(this.blockPosition()).unwrapKey().orElse(null);
        if (biomeKey != null) {
            String path = biomeKey.location().getPath();
            if (path.contains("mesa") || path.contains("badlands")) {
                return MouseVariant.BROWN;
            }
            if (path.contains("snow")) {
                return MouseVariant.WHITE;
            }
        }
        MouseVariant[] variants = MouseVariant.values();
        return variants[this.random.nextInt(variants.length)];
    }

    /** Original: never takes fall damage — matches its wall-climbing ability. */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    @Nullable
    private Player getHolder() {
        Optional<UUID> uuid = this.entityData.get(DATA_HELD_BY);
        if (uuid.isEmpty()) {
            return null;
        }
        return this.level().getPlayerByUUID(uuid.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            if (this.isHeld()) {
                this.entityData.set(DATA_HELD_BY, Optional.empty());
                this.setNoGravity(false);
            } else {
                this.entityData.set(DATA_HELD_BY, Optional.of(player.getUUID()));
                this.setNoGravity(true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void tickHeld() {
        if (!this.isHeld()) {
            return;
        }
        Player holder = this.getHolder();
        if (holder == null) {
            if (!this.level().isClientSide) {
                this.entityData.set(DATA_HELD_BY, Optional.empty());
                this.setNoGravity(false);
            }
            return;
        }
        if (!this.level().isClientSide && holder.isShiftKeyDown()) {
            this.entityData.set(DATA_HELD_BY, Optional.empty());
            this.setNoGravity(false);
            return;
        }
        this.setPos(holder.getX(), holder.getEyeY() + 0.2D, holder.getZ());
        this.setYRot(holder.getYRot());
        this.setXRot(0.0F);
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MOUSE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.MOUSE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MOUSE_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 0-2 wheat seeds, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        int count = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(Items.WHEAT_SEEDS, count));
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

}