package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bird.BirdVariant;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetCarryUtil;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityBird}: a small
 * flying creature with 6 colour variants ({@link BirdVariant}) that flies
 * around trees, eats seeds off the ground (becoming pre-tamed), is tamed by
 * feeding it again once pre-tamed, and can be carried on the player's head
 * ({@link CarriedPet}) — granting Slow Falling (long horizontal jumps + no
 * fall damage) and a massive speed boost to any mount the holder is riding,
 * same idea as {@code MoCBunnyEntity}.
 */
public class MoCBirdEntity extends TamableAnimal implements CarriedPet {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBirdEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<java.util.UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCBirdEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final double SEED_SEARCH_RADIUS = 8.0D;
    private static final double EAT_DISTANCE_SQR = 1.5D;
    /** Wiki: "as if they had the Slow Falling effect" — refreshed every tick while held. */
    private static final int SLOW_FALLING_REFRESH_TICKS = 5;
    private static final double HEAD_HEIGHT_OFFSET = 0.25D;
    private static final double MOUNT_SPEED_BOOST = 4.0D;
    private static final net.minecraft.resources.ResourceLocation MOUNT_SPEED_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "bird_mount_speed_boost");

    /** Wiki: "healed by any type of seeds" / eaten off the ground to become pre-tamed — same set as vanilla's Parrot. */
    private static boolean isSeed(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS) || stack.is(Items.MELON_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS) || stack.is(Items.BEETROOT_SEEDS);
    }

    /** True once it's eaten seeds off the ground — lets the next empty-hand right-click tame it. Not persisted, matching the original's transient flag. */
    private boolean hasEatenSeeds;
    private int pickupCooldown;
    @Nullable
    private LivingEntity boostedVehicle;
    /** Whether the holding player is currently falling — has hysteresis so the flap doesn't flicker on/off. */
    private boolean holderFalling;

    public MoCBirdEntity(EntityType<? extends MoCBirdEntity> type, Level level) {
        super(type, level);
        // Same movement style as vanilla's Parrot: smooth flight control
        // instead of the ground-mob default, so it doesn't jerk around
        // while airborne.
        this.moveControl = new FlyingMoveControl(this, 10, false);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void travel(Vec3 travelVector) {
        super.travel(travelVector);
        // Always glides down slowly instead of falling at normal gravity —
        // a small flyer shouldn't ever plummet.
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            Vec3 motion = this.getDeltaMovement();
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
        this.goalSelector.addGoal(2, new SeekSeedGoal(this));
        this.goalSelector.addGoal(3, new TreePerchGoal(this));
        // Same wander style as vanilla's Parrot: alternates between flying
        // to a random nearby spot and hopping/walking on the ground.
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
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
        builder.define(DATA_HELD_BY, Optional.empty());
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
        // Taming happens via the ground-seeds-then-empty-hand flow in
        // mobInteract(), not vanilla food-taming.
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    /** Carries the chosen variant to the rest of a spawn group — same pattern as MoCSnakeEntity/MoCBunnyEntity. */
    private static final class BirdGroupData implements net.minecraft.world.entity.SpawnGroupData {
        final BirdVariant variant;
        BirdGroupData(BirdVariant variant) {
            this.variant = variant;
        }
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnType,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        BirdVariant variant = spawnGroupData instanceof BirdGroupData shared
                ? shared.variant
                : BirdVariant.random(this.random);
        setVariant(variant);

        // Never forward our own custom SpawnGroupData into AgeableMob's
        // finalizeSpawn() — it crashes trying to cast it to its own type
        // (same fix already applied to MoCBunnyEntity/MoCSnakeEntity).
        super.finalizeSpawn(level, difficulty, spawnType, null);
        return new BirdGroupData(variant);
    }

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
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    // XP (1-3) is awarded via getBaseExperienceReward() below — vanilla
    // already only grants it when killed by a player or a tamed wolf,
    // same as confirmed working in MoCBearEntity.
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // Wiki: "drop 0-2 feathers... increased with Looting."
        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING),
                    attacker);
        }

        int featherCount = Math.min(this.random.nextInt(3) + lootingLevel, 5); // 0-2 base, +1 per Looting level
        if (featherCount > 0) {
            this.spawnAtLocation(new ItemStack(Items.FEATHER, featherCount));
        }
    }

    // ---- Taming, feeding ----

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "Tamed birds can be healed with seeds... by right-clicking it."
        if (this.isTame() && this.isOwnedBy(player) && isSeed(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.pickupCooldown <= 0 && !isHeld() && stack.isEmpty()
                && !PetCarryUtil.isAlreadyCarryingAPet(player)) {
            // Wiki: "drop seeds... Right-Click on the bird after they have
            // eaten the seeds" — taming and picking it up are two separate clicks.
            if (!this.isTame() && this.hasEatenSeeds) {
                if (!this.level().isClientSide) {
                    this.tame(player);
                    this.hasEatenSeeds = false;
                    NamingHelper.promptRename(this, player.getUUID());
                }
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && this.isOwnedBy(player)) {
                if (!this.level().isClientSide) {
                    startHolding(player);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Bird", true);
        tag.putString("BirdVariant", getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    private void capturePetInstant(Player player, InteractionHand hand) {
        net.minecraft.nbt.CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    // ---- CarriedPet (pickup onto the player's head) ----

    @Override
    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    @Override
    public Player getHolder() {
        return this.entityData.get(DATA_HELD_BY).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    private void startHolding(Player player) {
        this.entityData.set(DATA_HELD_BY, Optional.of(player.getUUID()));
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.pickupCooldown = 10;
        clearMountSpeedBoost();
    }

    private void tickHeld() {
        if (!isHeld()) {
            return;
        }
        Player holder = getHolder();
        if (holder == null || holder.isRemoved()) {
            if (!this.level().isClientSide) {
                stopHolding();
            }
            return;
        }
        if (!this.level().isClientSide && holder.isShiftKeyDown()) {
            stopHolding();
            return;
        }

        Vec3 targetPos = holder.getEyePosition().add(0.0D, HEAD_HEIGHT_OFFSET, 0.0D);
        this.moveTo(targetPos.x, targetPos.y, targetPos.z, holder.getYRot(), 0.0F);
        this.xo = targetPos.x;
        this.yo = targetPos.y;
        this.zo = targetPos.z;
        this.yRotO = holder.getYRot();
        this.setYHeadRot(holder.getYRot());
        this.yHeadRotO = holder.getYRot();
        this.setDeltaMovement(Vec3.ZERO);

        double holderYMotion = holder.getDeltaMovement().y;
        if (holderYMotion < -0.08D) {
            this.holderFalling = true;
        } else if (holderYMotion > -0.02D) {
            this.holderFalling = false;
        }
        // Anything between those two thresholds keeps whatever state it was
        // already in — that dead zone is what stops the rapid on/off flicker.

        if (!this.level().isClientSide) {
            // Wiki: "long-distance horizontal jumps (as if they had the Slow
            // Falling effect) and also become immune to fall damage" —
            // vanilla Slow Falling already grants both on its own.
            holder.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, SLOW_FALLING_REFRESH_TICKS, 0, true, false));
            tickMountSpeedBoost(holder);
        }
    }

    public boolean isHolderFalling() {
        return this.holderFalling;
    }

    /** Wiki: "if the player is riding a horse, wyvern or other mountable mob... massive speed boost". */
    private void tickMountSpeedBoost(Player holder) {
        Entity vehicle = holder.getVehicle();
        LivingEntity newVehicle = vehicle instanceof LivingEntity living ? living : null;
        if (this.boostedVehicle != newVehicle) {
            clearMountSpeedBoost();
            this.boostedVehicle = newVehicle;
            if (this.boostedVehicle != null) {
                AttributeInstance speed = this.boostedVehicle.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null && speed.getModifier(MOUNT_SPEED_MODIFIER_ID) == null) {
                    speed.addTransientModifier(new AttributeModifier(
                            MOUNT_SPEED_MODIFIER_ID, MOUNT_SPEED_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }
        }
    }

    private void clearMountSpeedBoost() {
        if (this.boostedVehicle != null) {
            AttributeInstance speed = this.boostedVehicle.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.removeModifier(MOUNT_SPEED_MODIFIER_ID);
            }
            this.boostedVehicle = null;
        }
    }

    @Override
    public boolean isPushable() {
        return !isHeld() && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isHeld() && super.canBeCollidedWith();
    }

    @Override
    public void pushEntities() {
        if (isHeld()) {
            return;
        }
        super.pushEntities();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.pickupCooldown > 0) {
            this.pickupCooldown--;
        }
        tickHeld();
    }

    /** Wiki: "search for any nearby leaves... any logs... eat seeds off the ground, then get pre-tamed." */
    private static class SeekSeedGoal extends Goal {
        private final MoCBirdEntity bird;
        @Nullable
        private ItemEntity targetSeed;

        SeekSeedGoal(MoCBirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bird.isTame() || bird.isHeld() || bird.hasEatenSeeds) {
                return false;
            }
            List<ItemEntity> items = bird.level().getEntitiesOfClass(ItemEntity.class,
                    bird.getBoundingBox().inflate(SEED_SEARCH_RADIUS),
                    item -> item.isAlive() && isSeed(item.getItem()));
            if (items.isEmpty()) {
                return false;
            }
            this.targetSeed = items.get(bird.random.nextInt(items.size()));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetSeed != null && this.targetSeed.isAlive() && !bird.hasEatenSeeds;
        }

        @Override
        public void stop() {
            this.targetSeed = null;
        }

        @Override
        public void tick() {
            if (this.targetSeed == null) {
                return;
            }
            bird.getLookControl().setLookAt(this.targetSeed, 30F, 30F);
            bird.getNavigation().moveTo(this.targetSeed.getX(), this.targetSeed.getY(), this.targetSeed.getZ(), 1.0D);
            if (bird.distanceToSqr(this.targetSeed) < EAT_DISTANCE_SQR) {
                ItemStack stack = this.targetSeed.getItem();
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.targetSeed.discard();
                }
                bird.hasEatenSeeds = true;
                bird.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
                this.targetSeed = null;
            }
        }
    }

    /** Wiki: "attracted to and fly around trees... search for leaves, then logs, then fly towards the top." */
    private static class TreePerchGoal extends Goal {
        private static final int SEARCH_RADIUS = 6;
        private final MoCBirdEntity bird;
        @Nullable
        private net.minecraft.core.BlockPos target;
        private int cooldown;

        TreePerchGoal(MoCBirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (bird.isHeld() || this.cooldown-- > 0) {
                return false;
            }
            this.cooldown = 100 + bird.random.nextInt(200);
            if (bird.random.nextInt(3) != 0) {
                return false;
            }
            net.minecraft.core.BlockPos origin = bird.blockPosition();
            for (net.minecraft.core.BlockPos pos : net.minecraft.core.BlockPos.betweenClosed(
                    origin.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                    origin.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {
                if (bird.level().getBlockState(pos).getBlock() instanceof LeavesBlock) {
                    net.minecraft.core.BlockPos.MutableBlockPos logSearch = pos.mutable();
                    for (int i = 0; i < 6; i++) {
                        logSearch.move(net.minecraft.core.Direction.DOWN);
                        if (bird.level().getBlockState(logSearch).is(net.minecraft.tags.BlockTags.LOGS)) {
                            this.target = findTreeTop(logSearch.immutable());
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private net.minecraft.core.BlockPos findTreeTop(net.minecraft.core.BlockPos logPos) {
            net.minecraft.core.BlockPos.MutableBlockPos top = logPos.mutable();
            while (bird.level().getBlockState(top).is(net.minecraft.tags.BlockTags.LOGS)
                    || bird.level().getBlockState(top).getBlock() instanceof LeavesBlock) {
                top.move(net.minecraft.core.Direction.UP);
            }
            return top.immutable();
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null && bird.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(this.target)) > 1.0D;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                bird.getNavigation().moveTo(this.target.getX() + 0.5D, this.target.getY() + 1.0D,
                        this.target.getZ() + 0.5D, 1.0D);
            }
        }

        @Override
        public void stop() {
            this.target = null;
        }
    }
}