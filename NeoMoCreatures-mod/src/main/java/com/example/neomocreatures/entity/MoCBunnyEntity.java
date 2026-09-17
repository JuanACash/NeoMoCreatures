package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bunny.BunnyVariant;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetCarryUtil;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityBunny}: a small,
 * skittish (while wild) creature with 5 colour variants ({@link BunnyVariant}),
 * immune to fall damage, that grows from baby to adult over time (wiki:
 * "baby rabbits take 3-5 days to mature"), tamed by picking it up (rides the
 * player's head), healed/bred with (golden) carrots, and boosts any mount
 * the holding player is riding while carried.
 */
public class MoCBunnyEntity extends TamableAnimal implements CarriedPet {

    /** Wiki: "Baby rabbits take 3-5 days to mature" — using 4 in-game days as a fixed middle value. */
    private static final int GROWTH_TICKS = 96000;
    /** Fraction of adult size a freshly-spawned baby starts at. */
    private static final float BABY_SCALE = 0.5F;
    /** Below this, a scale change is float noise from the per-tick age increment, not worth a refreshDimensions() call. */
    private static final float SCALE_CHANGE_THRESHOLD = 0.01F;
    /** How high above the player's eyes the bunny sits — wiki: "placed on top of your head". */
    private static final double HEAD_HEIGHT_OFFSET = 0.35D;
    /** Wiki: "any mob the player is riding... receives a massive speed boost, almost impossible to control" — total speed x5. */
    private static final double MOUNT_SPEED_BOOST = 4.0D;
    private static final net.minecraft.resources.ResourceLocation MOUNT_SPEED_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "bunny_mount_speed_boost");

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBunnyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<java.util.UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCBunnyEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private float lastAppliedScale = -1F;
    /** Ticks until the next hop while moving — original's jumpTimer. */
    private int jumpTimer;
    /** How long after being released before it can be picked up again — avoids an instant re-grab flicker. */
    private int pickupCooldown;
    /** The rider's mount currently boosted, if any — tracked so we remove the modifier from the right entity. */
    @Nullable
    private LivingEntity boostedVehicle;
    /** Detects the moment vanilla just finished breeding us (isInLove() flips off) so we can add extra babies. */
    private boolean wasInLove;

    public MoCBunnyEntity(EntityType<? extends MoCBunnyEntity> type, Level level) {
        super(type, level);
        // Wiki: "usually stay out of water" — strongly discourages pathing through it.
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
        // Original's isNotScared() = isTamed() — wild bunnies flee, tamed ones don't.
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D,
                livingEntity -> !this.isTame()));
        // Wiki: "bred by feeding them golden carrots" — same vanilla love-mode
        // approach-and-breed behaviour as pigs, cows, etc.
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BunnyVariant.GOLDEN.getId());
        builder.define(DATA_HELD_BY, Optional.empty());
    }

    public BunnyVariant getVariant() {
        return BunnyVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(BunnyVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("BunnyVariant", getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BunnyVariant", 8)) {
            try {
                setVariant(BunnyVariant.valueOf(tag.getString("BunnyVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Bunny", true);
        tag.putString("BunnyVariant", getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
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

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        // Wiki / original: bunnies are immune to fall damage.
        return false;
    }

    @Override
    public void setBaby(boolean baby) {
        // Vanilla's default hardcodes -24000 here, which doesn't match our
        // own GROWTH_TICKS window (96000) — without this override, any
        // baby created through vanilla's own breeding starts already 75%
        // grown in our scale formula, since it inherits vanilla's shorter
        // age countdown instead of ours.
        this.setAge(baby ? -GROWTH_TICKS : 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (this.pickupCooldown > 0) {
            this.pickupCooldown--;
        }
        tickHeld();
        if (!this.level().isClientSide) {
            tickHop();
            tickLitterBoost();
        }
    }

    /**
     * Vanilla's own breeding (triggered by BreedGoal once both bunnies are
     * in love and close together) always spawns exactly one baby. We detect
     * the moment it just finished — isInLove() flipping from true to false —
     * and top up the litter to the wiki's "3 to 5 bunnies" ourselves,
     * instead of guessing the exact internal method vanilla used to do it.
     */
    private void tickLitterBoost() {
        boolean nowInLove = this.isInLove();
        if (this.wasInLove && !nowInLove && this.level() instanceof ServerLevel serverLevel) {
            int extraBabies = 2 + this.random.nextInt(3); // 2-4 more, on top of vanilla's own 1 = 3-5 total
            for (int i = 0; i < extraBabies; i++) {
                MoCBunnyEntity baby = com.example.neomocreatures.init.ModEntities.MOC_BUNNY.get().create(serverLevel);
                if (baby == null) {
                    continue;
                }
                baby.setVariant(BunnyVariant.random(this.random));
                baby.setBaby(true);
                baby.moveTo(this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D, this.getY(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D, 0.0F, 0.0F);
                if (this.getOwner() instanceof Player owner) {
                    baby.tame(owner);
                }
                serverLevel.addFreshEntity(baby);
            }
        }
        this.wasInLove = nowInLove;
    }

    /** Wiki: "They hop around aimlessly instead of walking" — original's jumpTimer logic. */
    private void tickHop() {
        if (isHeld()) {
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        boolean moving = motion.x > 0.05D || motion.z > 0.05D || motion.x < -0.05D || motion.z < -0.05D;
        if (--this.jumpTimer <= 0 && this.onGround() && moving) {
            this.setDeltaMovement(motion.x, 0.3D, motion.z);
            this.jumpTimer = 15;
        }
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = getGrowthFraction();
            if (Math.abs((float) scaleAttr.getBaseValue() - target) > SCALE_CHANGE_THRESHOLD) {
                scaleAttr.setBaseValue(target);
            }
        }
        float current = (float) scaleAttr.getValue();
        if (Math.abs(this.lastAppliedScale - current) > SCALE_CHANGE_THRESHOLD) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Wiki: "bred by feeding them golden carrots" — this is what makes
        // vanilla's BreedGoal/mobInteract-adjacent logic recognise it; we
        // still handle the actual interaction ourselves in mobInteract().
        return stack.is(Items.GOLDEN_CARROT);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MoCBunnyEntity baby = com.example.neomocreatures.init.ModEntities.MOC_BUNNY.get().create(level);
        if (baby != null && this.getOwner() instanceof Player owner) {
            baby.tame(owner);
        }
        return baby;
    }

    /** Carries the chosen variant to the rest of a spawn group — same pattern as MoCSnakeEntity's SnakeGroupData. */
    private static final class BunnyGroupData implements net.minecraft.world.entity.SpawnGroupData {
        final BunnyVariant variant;
        BunnyGroupData(BunnyVariant variant) {
            this.variant = variant;
        }
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnType,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        net.minecraft.world.entity.SpawnGroupData resultGroupData = spawnGroupData;

        if (spawnType == net.minecraft.world.entity.MobSpawnType.NATURAL
                || spawnType == net.minecraft.world.entity.MobSpawnType.CHUNK_GENERATION) {
            BunnyVariant variant = spawnGroupData instanceof BunnyGroupData shared
                    ? shared.variant
                    : pickVariantForBiome(level, this.blockPosition());
            resultGroupData = new BunnyGroupData(variant);
            setVariant(variant);
        } else {
            setVariant(BunnyVariant.random(this.random));
        }

        // Never forward our own custom SpawnGroupData into AgeableMob's
        // finalizeSpawn() — it converts the data without checking the type
        // and crashes with anything that isn't its own. We track the
        // group's shared variant ourselves instead (same fix already used
        // by MoCSnakeEntity/MoCBearEntity).
        super.finalizeSpawn(level, difficulty, spawnType, null);
        return resultGroupData;
    }

    /** Wiki: "Bunnies always spawn white in taiga, cold taiga, ice mountains or ice plains biomes." */
    private BunnyVariant pickVariantForBiome(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.core.BlockPos pos) {
        var biome = level.getBiome(pos);
        if (biome.is(net.minecraft.world.level.biome.Biomes.TAIGA)
                || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA)
                || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS)
                || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES)
                || biome.is(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS)) {
            return BunnyVariant.WHITE;
        }
        return BunnyVariant.random(this.random);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BUNNY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BUNNY_DEATH.get();
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
        this.playSound(ModSounds.BUNNY_LIFT.get(), 1.0F, 1.0F);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.noPhysics = false;
        this.pickupCooldown = 10;
        this.playSound(ModSounds.BUNNY_LAND.get(), 1.0F, 1.0F);
        clearMountSpeedBoost();
    }

    /** Same approach as MoCSnakeEntity/MoCKittyEntity: repositions the real entity next to the player's head every tick. */
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

        if (!this.level().isClientSide) {
            tickMountSpeedBoost(holder);
        }
    }

    /** Wiki: "any mob the player is riding on... will receive a massive speed boost". */
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

        // Wiki: "healed by feeding them carrots".
        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.CARROT) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "bred by feeding them golden carrots. Bunnies have to be at
        // full health for them to be able to breed" — a golden carrot heals
        // AND marks it ready to breed in the same feed (not either/or), so
        // it always ends up at full health right away instead of needing a
        // second feed before love mode can trigger.
        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.GOLDEN_CARROT)) {
            if (!this.level().isClientSide) {
                this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(this.getMaxHealth());
                }
                if (!this.isBaby() && !this.isInLove()) {
                    this.setInLove(player);
                }
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

        // Wiki: "A bunny can be tamed by simply right-clicking on one. This
        // will result in the bunny being placed on top of your head, and the
        // naming window to pop up." Same for an already-tamed bunny picked
        // up again — just skips the tame/rename part.
        if (this.pickupCooldown <= 0 && !isHeld() && stack.isEmpty()
                && !PetCarryUtil.isAlreadyCarryingAPet(player)) {
            if (!this.level().isClientSide) {
                if (!this.isTame()) {
                    this.tame(player);
                    NamingHelper.promptRename(this, player.getUUID());
                }
                if (this.isOwnedBy(player)) {
                    startHolding(player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}