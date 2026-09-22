package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.crab.CrabVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.NamingHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.fluids.FluidType;
import java.util.function.Predicate;

/**
 * Port of {@code drzhark.mocreatures.entity.ambient.MoCEntityCrab}, built on {@link TamableAnimal}
 * instead of the original's own aquatic ownership framework.
 * <p>
 * 5 colours. Fully passive per the wiki (unlike the original's own code, which had it deal 1.5
 * contact damage and fight back when hurt — dropped in favour of the wiki, which explicitly says
 * "will never attack"). Flees the player on approach, and again — faster — when hurt, which is also
 * what raises its claws in the model. Walks on land and underwater without drowning, and climbs
 * vertical surfaces the same trick vanilla's Spider uses (any sideways collision counts as a ladder).
 * That same trick is also the original's known quirk: a crab that climbs into a gap under a ceiling
 * can get stuck and suffocate there — normal block-suffocation damage, nothing special to add.
 */
public class MoCCrabEntity extends TamableAnimal {

    /** Original: age is a per-individual size roll (50-99) rather than a growth stage; ported as a
     *  fixed random SCALE rolled once, not something that changes over the crab's life. */
    private static final float MIN_SCALE = 0.5F;
    private static final float MAX_SCALE = 0.7F;
    private static final double MAX_HEALTH = 6.0D;
    private static final double ARMOR = 2.0D;
    private static final double MOVEMENT_SPEED = 0.3D;
    private static final double FOLLOW_RANGE = 12.0D;

    // ---- Goals ----
    private static final double FOLLOW_SPEED = 0.8D;
    private static final double WANDER_SPEED = 1.0D;
    private static final int PANIC_PRIORITY = 2;
    /** Faster than the ordinary flee-from-player speed: this is also what raises the claws. */
    private static final double PANIC_SPEED = 1.4D;
    private static final float FLEE_DISTANCE = 6.0F;
    private static final double FLEE_SPEED = 1.0D;

    private static final String VARIANT_TAG = "CrabVariant";
    /** NBT flag that marks a filled fish net as holding a crab. */
    public static final String NET_KEY = "Crab";

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCCrabEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(MoCCrabEntity.class, EntityDataSerializers.FLOAT);

    public MoCCrabEntity(EntityType<? extends MoCCrabEntity> type, Level level) {
        super(type, level);
        // Lets it cross water instead of pathing around it, matching the wiki's "dwell in water".
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, FOLLOW_SPEED, 8.0F, 2.0F));
        this.goalSelector.addGoal(PANIC_PRIORITY, new PanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, FLEE_DISTANCE, FLEE_SPEED, FLEE_SPEED,
                (Predicate<net.minecraft.world.entity.LivingEntity>) player -> !this.isOwnedBy((Player) player)));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    /** Modern equivalent of the original's {@code isOnLadder() = collidedHorizontally}: any sideways
     *  bump is treated as a wall to climb, exactly like vanilla's Spider. */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    // ---------------------------------------------------------------------
    // Variant and size
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, CrabVariant.RED.getId());
        builder.define(DATA_SCALE, MIN_SCALE);
    }

    public CrabVariant getVariant() {
        return CrabVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(CrabVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public float getCrabScale() {
        return this.entityData.get(DATA_SCALE);
    }

    private void rollScale() {
        this.entityData.set(DATA_SCALE, MIN_SCALE + this.random.nextFloat() * (MAX_SCALE - MIN_SCALE));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(VARIANT_TAG, this.getVariant().name());
        tag.putFloat("CrabScale", this.getCrabScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(VARIANT_TAG, Tag.TAG_STRING)) {
            this.setVariant(CrabVariant.byName(tag.getString(VARIANT_TAG)));
        }
        if (tag.contains("CrabScale")) {
            this.entityData.set(DATA_SCALE, tag.getFloat("CrabScale"));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(CrabVariant.random(this.random));
        this.rollScale();
        return super.finalizeSpawn(level, difficulty, spawnType, new AgeableMob.AgeableMobGroupData(false));
    }

    // ---------------------------------------------------------------------
    // Water
    // ---------------------------------------------------------------------

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if (type == net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // crabs do not breed
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    /** Original: the crab is silent — no hurt or death sound. */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Wiki: 0-2 raw crab meat, scaling with Looting, dropped even when killed on fire. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        int count = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.CRAB_RAW.get(), count));
        }
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }

    /** Wiki: 1-3 experience, awarded only when a player or a tamed wolf made the kill (vanilla's rule). */
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    // ---------------------------------------------------------------------
    // Taming and interaction
    // ---------------------------------------------------------------------

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.FISH_NET.get()) && (!this.isTame() || this.isOwnedBy(player))) {
            return this.captureInFishNet(player, hand, stack);
        }
        if (this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    private InteractionResult captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        if (!this.level().isClientSide) {
            ItemStack filled = new ItemStack(ModItems.FISH_NET_FULL.get());
            filled.set(DataComponents.CUSTOM_DATA, CustomData.of(this.createNetTag(player)));
            if (!player.getAbilities().instabuild) {
                emptyNet.shrink(1);
            }
            if (emptyNet.isEmpty()) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            this.discard();
        }
        return InteractionResult.SUCCESS;
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putString(VARIANT_TAG, this.getVariant().name());
        tag.putFloat("CrabScale", this.getCrabScale());
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    public void restoreFromNet(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setVariant(CrabVariant.byName(tag.getString(VARIANT_TAG)));
        this.entityData.set(DATA_SCALE, tag.getFloat("CrabScale"));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }
}