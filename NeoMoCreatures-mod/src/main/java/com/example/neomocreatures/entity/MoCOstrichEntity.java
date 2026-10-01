package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.ostrich.OstrichVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MoCOstrichEntity extends TamableAnimal implements GrowthScaled, com.example.neomocreatures.entity.egg.EggHatchable,
        net.minecraft.world.entity.HasCustomInventoryScreen, net.minecraft.world.entity.PlayerRideableJumping, StorablePet {

    private static final int HIDE_TICKS = 60;
    private static final int MOUTH_TICKS_MAX = 20;
    private static final int WING_TICKS_MAX = 80;
    private static final int GROWTH_TICKS = 48000;
    private static final float BABY_SCALE = 0.4F;
    private static final float BABY_HITBOX_SCALE = 0.5F;
    private static final int EGG_APPEAR_TICKS = 2400;
    private static final double PAIR_RADIUS = 8.0D;
    private static final float JUMP_VELOCITY = 0.63F;
    private static final double RIDER_BACK_OFFSET = 0.15D;
    private static final int TRANSFORM_DURATION_TICKS = 100;
    private static final int TRANSFORM_SOUND_TICKS = 60;
    private static final int JUMP_DEBOUNCE_TICKS = 10;
    private static final double CHARGE_RADIUS = 2.0D;

    public static final int ESSENCE_NONE = 0;
    public static final int ESSENCE_WYVERN = 1;
    public static final int ESSENCE_FIRE = 2;
    public static final int ESSENCE_UNDEAD = 3;
    public static final int ESSENCE_UNIHORNED = 4;

    public static final int HELMET_NONE = 0;
    public static final int HELMET_LEATHER = 1;
    public static final int HELMET_IRON = 2;
    public static final int HELMET_GOLD = 3;
    public static final int HELMET_DIAMOND = 4;
    public static final int HELMET_HIDE = 5;
    public static final int HELMET_FUR = 6;
    public static final int HELMET_REPTILE = 7;
    public static final int HELMET_SCORP_DIRT = 8;
    public static final int HELMET_SCORP_CAVE = 9;
    public static final int HELMET_SCORP_FROST = 10;
    public static final int HELMET_SCORP_NETHER = 11;
    public static final int HELMET_SCORP_UNDEAD = 12;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WING_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_HELMET =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FLAG_COLOR =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEAD_BURIED =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ESSENCE =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PENDING_ESSENCE =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_UNIHORNED_CHARGE_TICKS =
            SynchedEntityData.defineId(MoCOstrichEntity.class, EntityDataSerializers.INT);

    private final net.minecraft.world.SimpleContainer chestInventory = new net.minecraft.world.SimpleContainer(18);
    @Nullable
    private net.minecraft.resources.ResourceLocation saddleItemId;

    private int hidingCounter;
    private float lastAppliedScale = -1F;
    private int eggAppearCounter;
    private int pairingType;
    private int jumpDebounceCounter;
    private boolean wasAscendHeldLastTick;
    private boolean jumpPending;

    @Nullable
    private java.util.UUID feederUUID;
    @Nullable
    private java.util.UUID partnerUUID;
    @Nullable
    private java.util.UUID grudgeTargetUUID;

    public MoCOstrichEntity(EntityType<? extends MoCOstrichEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !MoCOstrichEntity.this.isHiding() && !MoCOstrichEntity.this.isHeadBuried() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !MoCOstrichEntity.this.isHiding() && !MoCOstrichEntity.this.isHeadBuried() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return !isBaby() && (getVariant() == OstrichVariant.MALE || getVariant() == OstrichVariant.WHITE)
                        && super.canUse();
            }
        });
        this.targetSelector.addGoal(2, new AttackEggHolderGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.WHEAT)
                || stack.is(net.minecraft.world.item.Items.WHEAT_SEEDS)
                || stack.is(net.minecraft.world.item.Items.APPLE)
                || stack.is(net.minecraft.world.item.Items.GOLDEN_APPLE);
    }

    public OstrichVariant getVariant() {
        return OstrichVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(OstrichVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    private void setHiding(boolean hiding) {
        this.entityData.set(DATA_HIDING, hiding);
    }

    public boolean isHeadBuried() {
        return this.entityData.get(DATA_HEAD_BURIED);
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void startTalking() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getWingTicks() {
        return this.entityData.get(DATA_WING_TICKS);
    }

    public int getAscendCooldownTicks() {
        return this.jumpDebounceCounter;
    }

    private void flapWings() {
        if (this.entityData.get(DATA_WING_TICKS) == 0) {
            this.entityData.set(DATA_WING_TICKS, 1);
        }
    }

    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    private void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    public boolean hasChest() {
        return this.entityData.get(DATA_HAS_CHEST);
    }

    private void setHasChest(boolean hasChest) {
        this.entityData.set(DATA_HAS_CHEST, hasChest);
    }

    public int getHelmet() {
        return this.entityData.get(DATA_HELMET);
    }

    private void setHelmet(int helmet) {
        this.entityData.set(DATA_HELMET, helmet);
    }

    public int getFlagColor() {
        return this.entityData.get(DATA_FLAG_COLOR);
    }

    private void setFlagColor(int colorId) {
        this.entityData.set(DATA_FLAG_COLOR, colorId);
    }

    public int getEssence() {
        return this.entityData.get(DATA_ESSENCE);
    }

    public void setEssence(int essence) {
        this.entityData.set(DATA_ESSENCE, essence);
    }

    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    private void setFlying(boolean flying) {
        if (isFlying() != flying) {
            this.entityData.set(DATA_FLYING, flying);
        }
    }

    public boolean isTransforming() {
        return this.entityData.get(DATA_TRANSFORM_TICKS) > 0;
    }

    public int getTransformTicks() {
        return this.entityData.get(DATA_TRANSFORM_TICKS);
    }

    public int getPendingEssence() {
        return this.entityData.get(DATA_PENDING_ESSENCE);
    }

    private void setPendingEssence(int essence) {
        this.entityData.set(DATA_PENDING_ESSENCE, essence);
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_UNIHORNED_CHARGE_TICKS) > 0;
    }

    private boolean canFlyEssence() {
        return this.isVehicle() && (getEssence() == ESSENCE_WYVERN || getEssence() == ESSENCE_FIRE);
    }

    public void setAscendHeld(boolean held) {
        this.entityData.set(DATA_ASCEND_HELD, held);
    }

    private boolean isAscendHeld() {
        return this.entityData.get(DATA_ASCEND_HELD);
    }

    @Override
    public boolean fireImmune() {
        return getEssence() == ESSENCE_FIRE || super.fireImmune();
    }

    private static int essenceIdFor(net.minecraft.world.item.Item item) {
        if (item == com.example.neomocreatures.init.ModItems.ESSENCE_OF_DARKNESS.get()) return ESSENCE_WYVERN;
        if (item == com.example.neomocreatures.init.ModItems.ESSENCE_OF_FIRE.get()) return ESSENCE_FIRE;
        if (item == com.example.neomocreatures.init.ModItems.ESSENCE_OF_UNDEAD.get()) return ESSENCE_UNDEAD;
        if (item == com.example.neomocreatures.init.ModItems.ESSENCE_OF_LIGHT.get()) return ESSENCE_UNIHORNED;
        return ESSENCE_NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, OstrichVariant.DARK.getId());
        builder.define(DATA_HIDING, false);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_WING_TICKS, 0);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_HAS_CHEST, false);
        builder.define(DATA_HELMET, 0);
        builder.define(DATA_FLAG_COLOR, -1);
        builder.define(DATA_HEAD_BURIED, false);
        builder.define(DATA_ESSENCE, ESSENCE_NONE);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_TRANSFORM_TICKS, 0);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_PENDING_ESSENCE, ESSENCE_NONE);
        builder.define(DATA_UNIHORNED_CHARGE_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("OstrichVariant", getVariant().name());
        tag.putInt("OstrichEssence", getEssence());
        tag.putBoolean("OstrichSaddled", isSaddled());
        tag.putBoolean("OstrichHasChest", hasChest());
        tag.putInt("OstrichHelmet", getHelmet());
        tag.putInt("OstrichFlagColor", getFlagColor());
        tag.putBoolean("OstrichHeadBuried", isHeadBuried());
        if (this.saddleItemId != null) {
            tag.putString("OstrichSaddleItem", this.saddleItemId.toString());
        }
        if (grudgeTargetUUID != null) {
            tag.putUUID("OstrichGrudge", grudgeTargetUUID);
        }
        if (hasChest()) {
            net.minecraft.nbt.ListTag chestItems = new net.minecraft.nbt.ListTag();
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack chestStack = chestInventory.getItem(slot);
                if (!chestStack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", slot);
                    itemTag.put("Item", chestStack.save(this.registryAccess(), new CompoundTag()));
                    chestItems.add(itemTag);
                }
            }
            tag.put("OstrichChestItems", chestItems);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("OstrichVariant", 8)) {
            try {
                setVariant(OstrichVariant.valueOf(tag.getString("OstrichVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("OstrichEssence")) {
            setEssence(tag.getInt("OstrichEssence"));
        }
        if (tag.contains("OstrichSaddled")) {
            setSaddled(tag.getBoolean("OstrichSaddled"));
        }
        if (tag.contains("OstrichHasChest")) {
            setHasChest(tag.getBoolean("OstrichHasChest"));
        }
        if (tag.contains("OstrichHelmet")) {
            setHelmet(tag.getInt("OstrichHelmet"));
        }
        if (tag.contains("OstrichFlagColor")) {
            setFlagColor(tag.getInt("OstrichFlagColor"));
        }
        if (tag.contains("OstrichHeadBuried")) {
            this.entityData.set(DATA_HEAD_BURIED, tag.getBoolean("OstrichHeadBuried"));
        }
        if (tag.contains("OstrichSaddleItem", 8)) {
            this.saddleItemId = net.minecraft.resources.ResourceLocation.parse(tag.getString("OstrichSaddleItem"));
        }
        if (tag.hasUUID("OstrichGrudge")) {
            grudgeTargetUUID = tag.getUUID("OstrichGrudge");
        }
        if (tag.contains("OstrichChestItems", 9)) {
            net.minecraft.nbt.ListTag chestItems = tag.getList("OstrichChestItems", 10);
            for (int i = 0; i < chestItems.size(); i++) {
                CompoundTag itemTag = chestItems.getCompound(i);
                int slot = itemTag.getInt("Slot");
                ItemStack chestStack = ItemStack.parse(this.registryAccess(), itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
                if (slot >= 0 && slot < chestInventory.getContainerSize()) {
                    chestInventory.setItem(slot, chestStack);
                }
            }
        }
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(OstrichVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
            }
        } else {
            setVariant(this.random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE);
        }
        this.setBaby(true);
        this.setAge(-GROWTH_TICKS);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            com.example.neomocreatures.util.NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnType,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        net.minecraft.world.entity.SpawnGroupData data =
                super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        int roll = this.random.nextInt(100);
        if (roll < 20) {
            // 20% chance to spawn straight as a chick
            this.setBaby(true);
            this.setAge(-GROWTH_TICKS);
            setVariant(this.random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE);
        } else {
            setVariant(OstrichVariant.rollNatural(this.random));
        }

        return data;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (isHeadBuried()) {
            return null;
        }
        startTalking();
        return com.example.neomocreatures.init.ModSounds.OSTRICH_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.OSTRICH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.OSTRICH_DEATH.get();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (isSaddled() && this.getFirstPassenger() instanceof Player player && this.hasPassenger(player)) {
            return player;
        }
        return null;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() + Math.sin(yaw) * RIDER_BACK_OFFSET;
        double z = this.getZ() - Math.cos(yaw) * RIDER_BACK_OFFSET;
        double y = this.getY() + 0.85D * this.getScale();
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa, 0.0D, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float base = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        if (getEssence() == ESSENCE_FIRE) {
            base *= 1.4F;
        }
        return base;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        if (this.isInWater() || this.isInLava()) {
            Vec3 motion = this.getDeltaMovement();
            if (motion.y < 0.2D) {
                double newY = Math.min(motion.y + 0.04D, 0.2D);
                this.setDeltaMovement(motion.x, newY, motion.z);
            }
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_HEAD_BURIED, false);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            boolean isOnAirNow = isOnAirClear();
            boolean flyingMount = canFlyEssence() && !this.onGround() && isOnAirNow;
            setFlying(flyingMount);

            // Real makeEntityJump(): fires repeatedly while space is held, not
            // just on a single press — its own short debounce (not a one-shot).
            // Real cooldown: counts down every tick regardless of ascend key state,
            // so releasing and spamming the key does not bypass the wait.
            if (jumpDebounceCounter > 0) {
                jumpDebounceCounter--;
            }

            if (canFlyEssence() && isAscendHeld() && jumpDebounceCounter == 0) {
                jumpPending = true;
                jumpDebounceCounter = JUMP_DEBOUNCE_TICKS;
            }

            if (jumpPending && canFlyEssence()) {
                Vec3 motion = this.getDeltaMovement();
                double newY = motion.y + 0.8D;
                this.setDeltaMovement(motion.x, newY, motion.z);
                jumpPending = false;
                if (getEssence() == ESSENCE_FIRE && isOnAirNow) { // selfPropelledFlyer() — Nether only
                    float yaw = this.getYRot() * ((float) Math.PI / 180F);
                    Vec3 boosted = this.getDeltaMovement();
                    this.setDeltaMovement(
                            boosted.x - 0.5D * Math.sin(yaw),
                            boosted.y,
                            boosted.z + 0.5D * Math.cos(yaw));
                }
            }

            if (flyingMount) {
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
                this.moveRelative(0.096F, travelVector); // flyerFriction()/10
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x * 0.96D, motion.y * 0.89D - 0.055D, motion.z * 0.96D);
                if (this.onGround()) {
                    jumpPending = false;
                }
                return;
            }
            if (this.onGround()) {
                jumpPending = false;
            }
        }
        super.travel(travelVector);
    }

    private boolean isOnAirClear() {
        net.minecraft.core.BlockPos below = net.minecraft.core.BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
        net.minecraft.core.BlockPos below2 = net.minecraft.core.BlockPos.containing(this.getX(), this.getY() - 1.2D, this.getZ());
        return this.level().getBlockState(below).isAir() && this.level().getBlockState(below2).isAir();
    }

    @Override
    public boolean canJump() {
        return isSaddled() && this.isVehicle() && !canFlyEssence();
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 0 && (this.onGround() || this.isInWater() || this.isInLava())) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, JUMP_VELOCITY, motion.z);
            this.hasImpulse = true;
        }
    }

    @Override
    public void handleStartJump(int jumpPower) {
    }

    @Override
    public void handleStopJump() {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (canFlyEssence()) {
            return false;
        }
        return super.causeFallDamage(Math.max(0F, fallDistance - 4F), multiplier, source);
    }

    public void applyWhipBoost() {
        this.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 100, 1, false, true));
        if (getEssence() == ESSENCE_UNIHORNED) {
            this.entityData.set(DATA_UNIHORNED_CHARGE_TICKS, 100);
        }
    }

    public void dropAllEquipment() {
        if (isSaddled()) {
            this.ejectPassengers();
            net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                    ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                    : net.minecraft.world.item.Items.SADDLE;
            this.spawnAtLocation(new ItemStack(saddleItem));
            this.saddleItemId = null;
            setSaddled(false);
        }
        if (getHelmet() != HELMET_NONE) {
            this.spawnAtLocation(new ItemStack(itemForHelmet(getHelmet())));
            setHelmet(HELMET_NONE);
        }
        if (hasChest()) {
            this.spawnAtLocation(new ItemStack(net.minecraft.world.item.Items.CHEST));
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack stack = chestInventory.getItem(slot);
                if (!stack.isEmpty()) {
                    this.spawnAtLocation(stack);
                }
            }
            chestInventory.clearContent();
            if (getFlagColor() != -1) {
                this.spawnAtLocation(new ItemStack(woolItemFor(net.minecraft.world.item.DyeColor.byId(getFlagColor()))));
                setFlagColor(-1);
            }
            setHasChest(false);
        }
    }

    /** Snapshot used to restore this ostrich later from a filled Pet Amulet. */
    private CompoundTag buildAmuletTag(java.util.UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("OstrichVariant", getVariant().name());
        tag.putInt("OstrichEssence", getEssence());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Pet Amulet capture: instant, no vanish animation. Saddle/helmet/chest drop on the ground, not saved. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropAllEquipment();
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    private void tickUnihornedCharge() {
        if (getEssence() != ESSENCE_UNIHORNED || !this.isVehicle() || this.random.nextInt(15) != 0) {
            return;
        }
        for (LivingEntity nearby : this.level().getEntitiesOfClass(
                LivingEntity.class, this.getBoundingBox().inflate(CHARGE_RADIUS),
                e -> e != this && e != this.getControllingPassenger())) {
            Vec3 push = nearby.position().subtract(this.position()).normalize().scale(0.8D);
            nearby.push(push.x, 0.3D, push.z);
            nearby.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                if (this.isVehicle()) {
                    applyWhipBoost();
                } else {
                    boolean buried = !isHeadBuried();
                    this.entityData.set(DATA_HEAD_BURIED, buried);
                    if (buried) {
                        this.getNavigation().stop();
                        this.setTarget(null);
                    }
                }
                this.level().playSound(null, this.blockPosition(), com.example.neomocreatures.init.ModSounds.WHIP.get(),
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.5F,
                        0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            startTalking();
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(4.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTame() && !this.isBaby() && eggAppearCounter <= 0
                && getVariant() != OstrichVariant.MALE
                && stack.is(net.minecraft.world.item.Items.MELON_SEEDS)) {
            startTalking();
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                tryStartBreeding(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby()) {

            int essenceRoll = essenceIdFor(stack.getItem());
            if (essenceRoll != ESSENCE_NONE && !isTransforming() && getEssence() == ESSENCE_NONE) {
                if (!this.level().isClientSide) {
                    setPendingEssence(essenceRoll);
                    this.entityData.set(DATA_TRANSFORM_TICKS, TRANSFORM_DURATION_TICKS);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_DRINKING.get(), 1.0F, 1.0F);
                    if (!player.getInventory().add(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE))) {
                        player.drop(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE), false);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (!isSaddled() && (stack.is(net.minecraft.world.item.Items.SADDLE)
                    || stack.is(com.example.neomocreatures.init.ModItems.HORSE_SADDLE.get()))) {
                if (!this.level().isClientSide) {
                    setSaddled(true);
                    this.saddleItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                    this.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            int helmetFromItem = helmetIdFor(stack);
            if (helmetFromItem != HELMET_NONE) {
                if (getHelmet() != HELMET_NONE) {
                    // SUCCESS (not FAIL) fully consumes the interaction, so vanilla's own
                    // item-based armor-equip fallback never gets a chance to sneak the new
                    // helmet into the entity's real (unused) head equipment slot.
                    return InteractionResult.SUCCESS;
                }
                if (!this.level().isClientSide) {
                    setHelmet(helmetFromItem);
                    this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_ARMOR_PUT.get(), 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (!hasChest() && stack.is(net.minecraft.world.item.Items.CHEST)) {
                if (!this.level().isClientSide) {
                    setHasChest(true);
                    this.playSound(net.minecraft.sounds.SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            net.minecraft.world.item.DyeColor woolColor = dyeColorForWool(stack.getItem());
            if (hasChest() && woolColor != null && woolColor.getId() != getFlagColor()) {
                if (!this.level().isClientSide) {
                    if (getFlagColor() != -1) {
                        this.spawnAtLocation(new ItemStack(woolItemFor(net.minecraft.world.item.DyeColor.byId(getFlagColor()))));
                    }
                    setFlagColor(woolColor.getId());
                    this.playSound(net.minecraft.sounds.SoundEvents.WOOL_PLACE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (stack.is(net.minecraft.world.item.Items.SHEARS) && (isSaddled() || getHelmet() != HELMET_NONE)) {
                if (!this.level().isClientSide) {
                    if (getHelmet() != HELMET_NONE) {
                        this.spawnAtLocation(new ItemStack(itemForHelmet(getHelmet())));
                        setHelmet(HELMET_NONE);
                        this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_ARMOR_OFF.get(), 1.0F, 1.0F);
                    } else {
                        setSaddled(false);
                        this.ejectPassengers();
                        net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                                ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                                : net.minecraft.world.item.Items.SADDLE;
                        this.saddleItemId = null;
                        this.spawnAtLocation(new ItemStack(saddleItem));
                        this.playSound(net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                    }
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        if (this.isTame() && this.isOwnedBy(player) && player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                openInventoryFor(player);
            }
            return InteractionResult.SUCCESS;
        }

        if (isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            // Before mounting, give priority to whatever the held item wants to do
            // (scrolls, pet amulet, future items with their own interactLivingEntity).
            // Same pattern MoCHorseEntity uses for the same reason.
            if (!stack.isEmpty()) {
                InteractionResult itemResult = stack.interactLivingEntity(player, this, hand);
                if (itemResult.consumesAction()) {
                    return itemResult;
                }
            }
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private static int helmetIdFor(ItemStack stack) {
        if (stack.is(net.minecraft.world.item.Items.LEATHER_HELMET)) return HELMET_LEATHER;
        if (stack.is(net.minecraft.world.item.Items.IRON_HELMET)) return HELMET_IRON;
        if (stack.is(net.minecraft.world.item.Items.GOLDEN_HELMET)) return HELMET_GOLD;
        if (stack.is(net.minecraft.world.item.Items.DIAMOND_HELMET)) return HELMET_DIAMOND;
        if (stack.is(com.example.neomocreatures.init.ModItems.HIDE_HELMET.get())) return HELMET_HIDE;
        if (stack.is(com.example.neomocreatures.init.ModItems.FUR_HELMET.get())) return HELMET_FUR;
        if (stack.is(com.example.neomocreatures.init.ModItems.REPTILE_HELMET.get())) return HELMET_REPTILE;
        if (stack.is(com.example.neomocreatures.init.ModItems.SCORP_HELMET_DIRT.get())) return HELMET_SCORP_DIRT;
        if (stack.is(com.example.neomocreatures.init.ModItems.SCORP_HELMET_CAVE.get())) return HELMET_SCORP_CAVE;
        if (stack.is(com.example.neomocreatures.init.ModItems.SCORP_HELMET_FROST.get())) return HELMET_SCORP_FROST;
        if (stack.is(com.example.neomocreatures.init.ModItems.SCORP_HELMET_NETHER.get())) return HELMET_SCORP_NETHER;
        if (stack.is(com.example.neomocreatures.init.ModItems.SCORP_HELMET_UNDEAD.get())) return HELMET_SCORP_UNDEAD;
        return HELMET_NONE;
    }

    private static net.minecraft.world.item.Item itemForHelmet(int id) {
        return switch (id) {
            case HELMET_LEATHER -> net.minecraft.world.item.Items.LEATHER_HELMET;
            case HELMET_IRON -> net.minecraft.world.item.Items.IRON_HELMET;
            case HELMET_GOLD -> net.minecraft.world.item.Items.GOLDEN_HELMET;
            case HELMET_DIAMOND -> net.minecraft.world.item.Items.DIAMOND_HELMET;
            case HELMET_HIDE -> com.example.neomocreatures.init.ModItems.HIDE_HELMET.get();
            case HELMET_FUR -> com.example.neomocreatures.init.ModItems.FUR_HELMET.get();
            case HELMET_REPTILE -> com.example.neomocreatures.init.ModItems.REPTILE_HELMET.get();
            case HELMET_SCORP_DIRT -> com.example.neomocreatures.init.ModItems.SCORP_HELMET_DIRT.get();
            case HELMET_SCORP_CAVE -> com.example.neomocreatures.init.ModItems.SCORP_HELMET_CAVE.get();
            case HELMET_SCORP_FROST -> com.example.neomocreatures.init.ModItems.SCORP_HELMET_FROST.get();
            case HELMET_SCORP_NETHER -> com.example.neomocreatures.init.ModItems.SCORP_HELMET_NETHER.get();
            case HELMET_SCORP_UNDEAD -> com.example.neomocreatures.init.ModItems.SCORP_HELMET_UNDEAD.get();
            default -> net.minecraft.world.item.Items.LEATHER_HELMET;
        };
    }

    @Nullable
    private static net.minecraft.world.item.DyeColor dyeColorForWool(net.minecraft.world.item.Item item) {
        for (net.minecraft.world.item.DyeColor color : net.minecraft.world.item.DyeColor.values()) {
            if (item == woolItemFor(color)) {
                return color;
            }
        }
        return null;
    }

    private static net.minecraft.world.item.Item woolItemFor(net.minecraft.world.item.DyeColor color) {
        net.minecraft.world.level.block.Block block = switch (color) {
            case WHITE -> net.minecraft.world.level.block.Blocks.WHITE_WOOL;
            case ORANGE -> net.minecraft.world.level.block.Blocks.ORANGE_WOOL;
            case MAGENTA -> net.minecraft.world.level.block.Blocks.MAGENTA_WOOL;
            case LIGHT_BLUE -> net.minecraft.world.level.block.Blocks.LIGHT_BLUE_WOOL;
            case YELLOW -> net.minecraft.world.level.block.Blocks.YELLOW_WOOL;
            case LIME -> net.minecraft.world.level.block.Blocks.LIME_WOOL;
            case PINK -> net.minecraft.world.level.block.Blocks.PINK_WOOL;
            case GRAY -> net.minecraft.world.level.block.Blocks.GRAY_WOOL;
            case LIGHT_GRAY -> net.minecraft.world.level.block.Blocks.LIGHT_GRAY_WOOL;
            case CYAN -> net.minecraft.world.level.block.Blocks.CYAN_WOOL;
            case PURPLE -> net.minecraft.world.level.block.Blocks.PURPLE_WOOL;
            case BLUE -> net.minecraft.world.level.block.Blocks.BLUE_WOOL;
            case BROWN -> net.minecraft.world.level.block.Blocks.BROWN_WOOL;
            case GREEN -> net.minecraft.world.level.block.Blocks.GREEN_WOOL;
            case RED -> net.minecraft.world.level.block.Blocks.RED_WOOL;
            case BLACK -> net.minecraft.world.level.block.Blocks.BLACK_WOOL;
        };
        return block.asItem();
    }

    private void tryStartBreeding(Player player, ItemStack stack) {
        MoCOstrichEntity partner = findValidPartner();
        if (partner == null) {
            return;
        }

        stack.shrink(1);
        this.feederUUID = player.getUUID();
        this.partnerUUID = partner.getUUID();
        this.eggAppearCounter = EGG_APPEAR_TICKS;

        OstrichVariant mine = getVariant();
        OstrichVariant theirs = partner.getVariant();
        if (mine == OstrichVariant.WHITE && theirs == OstrichVariant.WHITE) {
            this.pairingType = 2;
        } else if (mine == OstrichVariant.WHITE || theirs == OstrichVariant.WHITE) {
            this.pairingType = 1;
        } else {
            this.pairingType = 0;
        }
    }

    @Nullable
    private MoCOstrichEntity findValidPartner() {
        OstrichVariant mine = getVariant();
        java.util.List<MoCOstrichEntity> nearby = this.level().getEntitiesOfClass(MoCOstrichEntity.class,
                this.getBoundingBox().inflate(PAIR_RADIUS),
                other -> other != this && !other.isTame() && !other.isBaby() && other.eggAppearCounter <= 0);

        for (MoCOstrichEntity other : nearby) {
            OstrichVariant theirs = other.getVariant();
            boolean iAmValidLayer;
            if (mine == OstrichVariant.FEMALE && theirs == OstrichVariant.MALE) {
                iAmValidLayer = true;
            } else if (mine == OstrichVariant.WHITE && theirs == OstrichVariant.MALE) {
                iAmValidLayer = true;
            } else if (mine == OstrichVariant.FEMALE && theirs == OstrichVariant.WHITE) {
                iAmValidLayer = true;
            } else if (mine == OstrichVariant.WHITE && theirs == OstrichVariant.WHITE) {
                iAmValidLayer = true;
            } else {
                iAmValidLayer = false;
            }
            if (iAmValidLayer) {
                return other;
            }
        }
        return null;
    }

    private void tickBreeding() {
        if (eggAppearCounter <= 0) {
            return;
        }
        if (--eggAppearCounter == 0) {
            com.example.neomocreatures.entity.egg.MoCEggEntity egg =
                    com.example.neomocreatures.init.ModEntities.MOC_EGG.get().create((ServerLevel) this.level());
            if (egg != null) {
                egg.moveTo(this.getX(), this.getY(), this.getZ(), 0F, 0F);
                egg.setHatchEntityId(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(
                        com.example.neomocreatures.init.ModEntities.MOC_OSTRICH.get()));
                egg.setHatchVariant(rollDestinedVariant().name());
                egg.setSourceItemId(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(
                        com.example.neomocreatures.init.ModItems.OSTRICH_EGG.get()));
                egg.setRequiresLight(false);
                egg.setRequirePickupToTame(true);
                this.level().addFreshEntity(egg);
            }
            feederUUID = null;
            partnerUUID = null;
        }
    }

    private OstrichVariant rollDestinedVariant() {
        if (pairingType == 2) {
            return OstrichVariant.WHITE;
        } else if (pairingType == 1) {
            return this.random.nextFloat() < 0.25F ? OstrichVariant.WHITE
                    : (this.random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE);
        } else {
            return this.random.nextBoolean() ? OstrichVariant.MALE : OstrichVariant.FEMALE;
        }
    }

    public static void alertNearbyOstriches(Level level, Vec3 pos, Player thief, double radius) {
        java.util.List<MoCOstrichEntity> nearby = level.getEntitiesOfClass(MoCOstrichEntity.class,
                new net.minecraft.world.phys.AABB(pos, pos).inflate(radius),
                o -> !o.isTame() && !o.isBaby());
        for (MoCOstrichEntity ostrich : nearby) {
            ostrich.grudgeTargetUUID = thief.getUUID();
            ostrich.setTarget(thief);
        }
    }

    private static class AttackEggHolderGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final MoCOstrichEntity ostrich;

        AttackEggHolderGoal(MoCOstrichEntity ostrich) {
            this.ostrich = ostrich;
            this.setFlags(java.util.EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return findGrudgeTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return findGrudgeTarget() != null;
        }

        @Override
        public void start() {
            this.ostrich.setTarget(findGrudgeTarget());
        }

        @Nullable
        private Player findGrudgeTarget() {
            if (this.ostrich.isTame() || this.ostrich.grudgeTargetUUID == null) {
                return null;
            }
            Player player = this.ostrich.level().getPlayerByUUID(this.ostrich.grudgeTargetUUID);
            if (player == null || !player.isAlive()) {
                this.ostrich.grudgeTargetUUID = null;
                return null;
            }
            return player;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (!hurt || this.level().isClientSide) {
            return hurt;
        }
        if (this.isTame()) {
            return true;
        }
        if (!this.isBaby() && (getVariant() == OstrichVariant.MALE || getVariant() == OstrichVariant.WHITE)) {
            startTalking();
            flapWings();
            return true;
        }
        setHiding(true);
        hidingCounter = HIDE_TICKS;
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double dx = Math.cos(angle) * 6.0D;
        double dz = Math.sin(angle) * 6.0D;
        this.getNavigation().moveTo(this.getX() + dx, this.getY(), this.getZ() + dz, 1.4D);
        return true;
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = net.minecraft.util.Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return net.minecraft.util.Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction();
            if (scaleAttr.getBaseValue() != newScale) {
                scaleAttr.setBaseValue(newScale);
            }
        }
        float currentScale = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != currentScale) {
            this.lastAppliedScale = currentScale;
            this.refreshDimensions();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.tickGrowth();
    }

    @Override
    protected net.minecraft.world.phys.AABB makeBoundingBox() {
        if (this.isBaby()) {
            net.minecraft.world.entity.EntityDimensions babyHitbox =
                    this.getType().getDimensions().scale(BABY_HITBOX_SCALE);
            return babyHitbox.makeBoundingBox(this.position());
        }
        return super.makeBoundingBox();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickIdleCounters();
            tickBreeding();
            tickAscendFlapSound();
            if (hidingCounter > 0 && --hidingCounter == 0) {
                setHiding(false);
            }
            tickEssenceTransform();
            int chargeTicksRemaining = this.entityData.get(DATA_UNIHORNED_CHARGE_TICKS);
            if (chargeTicksRemaining > 0) {
                this.entityData.set(DATA_UNIHORNED_CHARGE_TICKS, chargeTicksRemaining - 1);
                tickUnihornedCharge();
            }
        }
    }

    private void tickEssenceTransform() {
        int ticks = this.entityData.get(DATA_TRANSFORM_TICKS);
        if (ticks <= 0) {
            return;
        }
        ticks--;
        this.entityData.set(DATA_TRANSFORM_TICKS, ticks);
        if (ticks == TRANSFORM_SOUND_TICKS) {
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
        }
        if (ticks <= 0) {
            setEssence(getPendingEssence());
        }
    }

    private void tickIdleCounters() {
        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > MOUTH_TICKS_MAX) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int wing = this.entityData.get(DATA_WING_TICKS);
        if (wing > 0 && ++wing > WING_TICKS_MAX) {
            wing = 0;
        }
        this.entityData.set(DATA_WING_TICKS, wing);
    }

    // Plays the wing-flap sound exactly once per key press (not once per thrust pulse),
    // entirely server-side so it never depends on client/server timing.
    private void tickAscendFlapSound() {
        boolean ascendHeldNow = isAscendHeld();
        if (ascendHeldNow && !wasAscendHeldLastTick && canFlyEssence()) {
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_WING_FLAP.get(), 0.4F, 1.0F);
        }
        wasAscendHeldLastTick = ascendHeldNow;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            int lootingLevel = MoCLootUtil.getLootingLevel(source.getEntity());

            // Raw ostrich meat: 0-2 base, affected by Looting. Always dropped raw,
            // even if it died on fire (there's no "cooked on fire" logic here).
            MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.OSTRICH_RAW.get(),
                    MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));

            // Essence hearts / unicorn horn depending on the ostrich's essence, 25% base + Looting.
            net.minecraft.world.item.Item essenceHeartItem = switch (getEssence()) {
                case ESSENCE_WYVERN -> com.example.neomocreatures.init.ModItems.HEART_OF_DARKNESS.get();
                case ESSENCE_FIRE -> com.example.neomocreatures.init.ModItems.HEART_OF_FIRE.get();
                case ESSENCE_UNDEAD -> com.example.neomocreatures.init.ModItems.HEART_OF_UNDEAD.get();
                case ESSENCE_UNIHORNED -> com.example.neomocreatures.init.ModItems.UNICORN_HORN.get();
                default -> null;
            };
            if (essenceHeartItem != null) {
                if (MoCLootUtil.rollChance(this.random, 0.25F, 0.1F, lootingLevel)) {
                    MoCLootUtil.dropItems(this, essenceHeartItem, 1 + MoCLootUtil.rollWithLootingBonus(this.random, 2, lootingLevel));
                }
            }

            // Equipped saddle (the real one or the crafted one, depending on saddleItemId).
            if (isSaddled()) {
                net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                        ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                        : net.minecraft.world.item.Items.SADDLE;
                this.spawnAtLocation(new ItemStack(saddleItem));
            }

            // Equipped helmet.
            if (getHelmet() != HELMET_NONE) {
                this.spawnAtLocation(new ItemStack(itemForHelmet(getHelmet())));
            }

            // Chest + contents + the flag's wool (same as before).
            if (hasChest()) {
                this.spawnAtLocation(new ItemStack(net.minecraft.world.item.Items.CHEST));
                for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                    ItemStack chestStack = chestInventory.getItem(slot);
                    if (!chestStack.isEmpty()) {
                        this.spawnAtLocation(chestStack);
                    }
                }
                if (getFlagColor() != -1) {
                    this.spawnAtLocation(new ItemStack(woolItemFor(net.minecraft.world.item.DyeColor.byId(getFlagColor()))));
                }
            }
        }
        super.die(source);
    }

    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide && this.isTame()) {
            openInventoryFor(player);
        }
    }

    private void openChestMenu(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : net.minecraft.network.chat.Component.literal("Ostrich Storage");
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                            net.minecraft.world.inventory.MenuType.GENERIC_9x2, id, inv, this.chestInventory, 2),
                    title));
        }
    }

    private void openInventoryFor(Player player) {
        if (hasChest()) {
            openChestMenu(player);
        } else if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer,
                    new com.example.neomocreatures.network.OpenPlayerInventoryPayload());
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setVariant(com.example.neomocreatures.entity.ostrich.OstrichVariant.valueOf(tag.getString("OstrichVariant")));
        if (tag.contains("OstrichEssence")) {
            this.setEssence(tag.getInt("OstrichEssence"));
        }
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setHealth((float) tag.getFloat("Health"));
        if (tag.contains("Age")) {
            this.setAge(tag.getInt("Age"));
        } else {
            this.setAge(tag.getBoolean("Adult") ? 0 : -24000);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
    }
}
