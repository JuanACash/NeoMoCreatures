package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.bigcat.BigCatVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModTags;
import com.example.neomocreatures.util.MoCExperienceUtil;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Step 2 port of drzhark.mocreatures.entity.hunter.MoCEntity{Lion,Tiger,
 * Leopard,Panther}: the full 8-species roster (with real per-species
 * health/damage from the original source), the lion's mane, and the wiki's
 * hunting behavior — wild adults actively hunt players AND small animals,
 * then go neutral for a while after a kill until hungry again. Taming,
 * saddle, and chest still come in later steps.
 */
public class MoCBigCatEntity extends TamableAnimal implements GrowthScaled, net.minecraft.world.entity.PlayerRideableJumping,
        net.minecraft.world.entity.HasCustomInventoryScreen, StorablePet {

    private static final float BABY_SCALE = 0.5F;
    private static final double SPRINT_SPEED_BONUS = 0.15D;
    /** How long it stays "fed" and ignores prey after a kill — 5 minutes. */
    private static final int HUNGER_COOLDOWN_TICKS = 6000;
    /** Matches the original's canAttackTarget(): won't bother with anything bigger than roughly a player/deer. */
    private static final float MAX_PREY_SIZE = 2.0F;
    /** Independent of BABY_SCALE (which only affects the visual model) — this is purely the collision box size. */
    private static final float BABY_HITBOX_SCALE = 0.75F;
    private static final double EAT_NEARBY_ITEM_RANGE = 12.0D;
    private static final float RIDER_HEIGHT = 0.4F;
    private static final float RIDER_FORWARD = 0F;
    /** Vertical impulse for the fixed 2.7-block jump — winged cats (later step) won't get this at all. */
    private static final float JUMP_VELOCITY = 0.62F;
    private static final int WING_TRANSFORM_DURATION_TICKS = 100;
    private static final int WING_TRANSFORM_SOUND_TICKS = 60;
    private static final float RIDDEN_FLYER_FRICTION = 0.93F;
    private static final double RIDDEN_ASCEND_THRUST = 0.15D;
    private static final double RIDDEN_DESCEND_THRUST = 0.3D;
    private static final double RIDDEN_FLYER_FALL_SPEED = 0.6D;
    private static final double RIDDEN_FLYER_GRAVITY_PULL = 0.02D;
    private static final int WING_FLAP_BURST_TICKS = 20;
    private static final int SAFE_FALL_BLOCKS = 3;
    private static final float LIGER_RIDER_HEIGHT_BONUS = 0.2F;


    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.INT);
    /** Mouth-open animation ticks for roaring/hurting — same pattern as the wyvern's bite ticks. */
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.INT);
    /** Idle tail-swish ticks, server-driven and synced. */
    private static final EntityDataAccessor<Integer> DATA_TAIL_TICKS =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EATEN =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_MEDALLION =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING_SYNCED =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_GHOST =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_WINGS =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WING_FLAP_TICKS =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TICKS =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCBigCatEntity.class, EntityDataSerializers.BOOLEAN);


    /** Ticks left before it's hungry again and resumes hunting — 0 means "hungry, will hunt". */
    private int hungerCooldown;

    private final net.minecraft.world.SimpleContainer chestInventory = new net.minecraft.world.SimpleContainer(9);
    @Nullable
    private net.minecraft.resources.ResourceLocation saddleItemId;

    public MoCBigCatEntity(EntityType<? extends MoCBigCatEntity> type, Level level) {
        super(type, level);
    }

    @Override
protected void registerGoals() {
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Wild adult big cats actively hunt players AND smaller animals (per the
        // wiki: "from mobs as small as ants to as large as deer, including the
        // player") until they've made a kill, then go neutral until hungry again.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, this::canHunt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true, this::canHuntAnimal));
    }

    private boolean canHunt(@Nullable LivingEntity target) {
        return !this.isTame() && !this.isBaby() && this.hungerCooldown <= 0;
    }

    private boolean canHuntAnimal(@Nullable LivingEntity target) {
        if (!canHunt(target) || target instanceof MoCBigCatEntity || target instanceof MoCBearEntity
                || target instanceof net.minecraft.world.entity.animal.PolarBear
                || target instanceof net.minecraft.world.entity.animal.Panda
                || target instanceof MoCElephantEntity) {
            return false;
        }
        return target != null && target.getBbHeight() < MAX_PREY_SIZE && target.getBbWidth() < MAX_PREY_SIZE;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.isBaby() && source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
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

        // Whip toggles sitting when used directly on a tamed cat — also blocks
        // mounting while holding it, simply because this check runs first.
        if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                setSitting(!isSittingSynced());
                this.setTarget(null);
                this.getNavigation().stop();
                this.level().playSound(null, this.blockPosition(), com.example.neomocreatures.init.ModSounds.WHIP.get(),
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.5F,
                        0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.isTame() && this.isBaby() && hasEaten() && stack.is(com.example.neomocreatures.init.ModItems.MEDALLION.get())) {
            if (!this.level().isClientSide) {
                this.tame(player);
                this.entityData.set(DATA_HAS_MEDALLION, true);
                setSitting(false);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isCarnivoreFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !isSterile()
                && !this.isInLove() && isBreedingFood(stack)) {
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.setInLove(player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !hasWings() && !isTransforming()
                && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_DARKNESS.get())
                && getVariant().canGetDarknessWings()) {
            if (!this.level().isClientSide) {
                startWingTransform();
            }
            useEssence(player, stack);
            return InteractionResult.SUCCESS;
        }
        if (this.isTame() && this.isOwnedBy(player) && !hasWings() && !isTransforming()
                && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_LIGHT.get())
                && getVariant().canGetLightWings()) {
            if (!this.level().isClientSide) {
                startWingTransform();
            }
            useEssence(player, stack);
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !isSaddled()
                && (stack.is(net.minecraft.world.item.Items.SADDLE)
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

        // Saddle-only — the chest can't be removed by shears, only by death or a pet amulet (later step).
        if (this.isTame() && stack.is(net.minecraft.world.item.Items.SHEARS) && isSaddled()) {
            if (!this.level().isClientSide) {
                setSaddled(false);
                this.ejectPassengers();
                net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                        ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                        : net.minecraft.world.item.Items.SADDLE;
                this.saddleItemId = null;
                this.spawnAtLocation(new ItemStack(saddleItem));
                this.playSound(net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !hasChest()
                && stack.is(net.minecraft.world.item.Items.CHEST)) {
            if (!this.level().isClientSide) {
                setHasChest(true);
                this.playSound(net.minecraft.sounds.SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                setSitting(false);
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        if (!this.level().isClientSide && hasChest() && player.isSecondaryUseActive()) {
            openChestMenu(player);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    public void setSitting(boolean sitting) {
        this.setOrderedToSit(sitting);
        this.entityData.set(DATA_SITTING_SYNCED, sitting);
    }

    private void openChestMenu(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : net.minecraft.network.chat.Component.literal("Big Cat Storage");
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                            net.minecraft.world.inventory.MenuType.GENERIC_9x1, id, inv, this.chestInventory, 1),
                    title));
        }
    }

    /** E while riding opens the chest instead of the player's own inventory — same hook the wyvern/elephant use. */
    @Override
    public void openCustomInventoryScreen(Player player) {
        if (this.level().isClientSide || !this.isTame()) {
            return;
        }
        if (hasChest()) {
            openChestMenu(player);
            return;
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer,
                    new com.example.neomocreatures.network.OpenPlayerInventoryPayload());
        }
    }

    /** Wild cubs auto-eat dropped porkchop/raw fish left nearby — the first step of taming. */
    private void tickEatNearbyFood() {
        if (this.isTame() || !this.isBaby() || hasEaten()) {
            return;
        }
        net.minecraft.world.entity.item.ItemEntity nearestFood = null;
        double nearestDistSqr = EAT_NEARBY_ITEM_RANGE * EAT_NEARBY_ITEM_RANGE;
        for (net.minecraft.world.entity.item.ItemEntity itemEntity : this.level().getEntitiesOfClass(
                net.minecraft.world.entity.item.ItemEntity.class, this.getBoundingBox().inflate(EAT_NEARBY_ITEM_RANGE))) {
            if (!isCarnivoreFood(itemEntity.getItem())) {
                continue;
            }
            double distSqr = itemEntity.distanceToSqr(this);
            if (distSqr < nearestDistSqr) {
                nearestDistSqr = distSqr;
                nearestFood = itemEntity;
            }
        }
        if (nearestFood == null) {
            return;
        }
        if (nearestDistSqr > 4.0D) {
            this.getNavigation().moveTo(nearestFood, 1.0D);
            return;
        }
        nearestFood.getItem().shrink(1);
        if (nearestFood.getItem().isEmpty()) {
            nearestFood.discard();
        }
        this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
        setHasEaten(true);
    }

    /** Countdown for the wing essence: drinking sound already played on use, transform sound partway, wings appear at the end. */
    private void tickWingTransform() {
        if (!isTransforming()) {
            return;
        }
        int ticks = getTransformTicks() - 1;
        this.entityData.set(DATA_TRANSFORM_TICKS, ticks);
        if (ticks == WING_TRANSFORM_SOUND_TICKS) {
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
        }
        if (ticks <= 0) {
            this.entityData.set(DATA_HAS_WINGS, true);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public static int getGrowthTicks(BigCatVariant variant) {
        return variant.getGrowthTicks();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // Love mode is handled by hand in mobInteract below, not vanilla's automatic feeding.
    }

    private static boolean isBreedingFood(ItemStack stack) {
        return isCarnivoreFood(stack)
                || stack.is(net.minecraft.world.item.Items.BEEF)
                || stack.is(net.minecraft.world.item.Items.RABBIT);
    }

    /** Winged, ghost, and every hybrid are sterile — can never enter love mode at all. */
    private boolean isSterile() {
        return isGhost() || hasWings() || getVariant().isHybrid();
    }

    public static boolean isCarnivoreFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.PORKCHOP)
                || stack.is(net.minecraft.world.item.Items.COD)
                || stack.is(net.minecraft.world.item.Items.SALMON)
                || stack.is(net.minecraft.world.item.Items.TROPICAL_FISH);
    }

    public BigCatVariant getVariant() {
        return BigCatVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public boolean hasEaten() {
        return this.entityData.get(DATA_HAS_EATEN);
    }

    private void setHasEaten(boolean flag) {
        this.entityData.set(DATA_HAS_EATEN, flag);
    }

    public boolean hasMedallion() {
        return this.entityData.get(DATA_HAS_MEDALLION);
    }

    public void setMedallion(boolean value) {
        this.entityData.set(DATA_HAS_MEDALLION, value);
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

    public boolean hasWings() {
        return this.entityData.get(DATA_HAS_WINGS);
    }

    public void setWings(boolean wings) {
        this.entityData.set(DATA_HAS_WINGS, wings);
    }

    public boolean getIsFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public void setIsFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
        this.setNoGravity(flying);
    }

    public boolean isOnAir() {
        return !this.onGround() && !this.isInWater() && !this.isInLava();
    }

    public boolean isAirborne() {
        return !this.onGround() && (isOnAir() || getIsFlying());
    }

    public boolean isGliding() {
        return isAirborne() && this.getDeltaMovement().y < -0.03D;
    }

    public boolean isAirborneFlapping() {
        return isAirborne() && !isGliding();
    }

    public void setAscendHeld(boolean held) {
        this.entityData.set(DATA_ASCEND_HELD, held);
    }

    public void setDescendHeld(boolean held) {
        this.entityData.set(DATA_DESCEND_HELD, held);
    }

    public boolean isAscendHeld() {
        return this.entityData.get(DATA_ASCEND_HELD);
    }

    private boolean isDescendHeld() {
        return this.entityData.get(DATA_DESCEND_HELD);
    }

    public boolean isTransforming() {
        return this.entityData.get(DATA_TRANSFORM_TICKS) > 0;
    }

    public int getTransformTicks() {
        return this.entityData.get(DATA_TRANSFORM_TICKS);
    }

    private void startWingTransform() {
        this.entityData.set(DATA_TRANSFORM_TICKS, WING_TRANSFORM_DURATION_TICKS);
    }

    public boolean isGhost() {
        return this.entityData.get(DATA_GHOST);
    }

    private void setGhost(boolean ghost) {
        this.entityData.set(DATA_GHOST, ghost);
    }

    public boolean isSittingSynced() {
        return this.entityData.get(DATA_SITTING_SYNCED);
    }

    /** Also re-applies the species' max health/attack and heals to full — only meant to be called once, at spawn. */
    public void setVariant(BigCatVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
        AttributeInstance maxHealthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(variant.getMaxHealth());
        }
        AttributeInstance damageAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.setBaseValue(variant.getAttackDamage());
        }
        this.setHealth(this.getMaxHealth());
    }

    private enum WildFamily { SNOW_LEOPARD, LEOPARD, PANTHER, TIGER, LION }

    /** Carries the family chosen for the first spawned member to the rest of its herd — the actual
     *  fix for tigers/leopards/panthers showing up mixed together in the same group. */
    private static final class BigCatGroupData implements net.minecraft.world.entity.SpawnGroupData {
        final WildFamily family;
        BigCatGroupData(WildFamily family) {
            this.family = family;
        }
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnReason,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        BigCatGroupData resultGroupData = null;

        if (spawnReason == net.minecraft.world.entity.MobSpawnType.NATURAL
                || spawnReason == net.minecraft.world.entity.MobSpawnType.CHUNK_GENERATION) {
            WildFamily family;
            if (spawnGroupData instanceof BigCatGroupData shared) {
                family = shared.family;
            } else {
                family = pickFamilyForBiome(level, this.blockPosition());
            }
            resultGroupData = new BigCatGroupData(family);
            setVariant(rollVariantForFamily(family));
        } else {
            setVariant(BigCatVariant.randomLion(this.random)); // /summon, mob spawner, etc.
        }

        if (this.random.nextInt(4) == 0) {
            this.setAge(-getVariant().getGrowthTicks());
        }

        // Never forward our own custom SpawnGroupData into AgeableMob's finalizeSpawn —
        // it casts it internally without checking the type and crashes on anything else.
        // Let it run its own "baby chance" logic on fresh data (null), and return OURS
        // separately so the rest of the pack keeps getting the right family.
        super.finalizeSpawn(level, difficulty, spawnReason, null);
        return resultGroupData;
    }

    /** Which family a whole herd will be, decided once per herd by biome — never mixed within a group. */
    private WildFamily pickFamilyForBiome(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.core.BlockPos pos) {
        var biome = level.getBiome(pos);

        if (biome.is(ModTags.BIGCAT_SNOW_LEOPARD_BIOMES)) {
            return WildFamily.SNOW_LEOPARD;
        }
        if (biome.is(ModTags.BIGCAT_JUNGLE_BIOMES)) {
            int roll = this.random.nextInt(38); // 14 + 10 + 14
            if (roll < 14) return WildFamily.LEOPARD;
            if (roll < 24) return WildFamily.PANTHER;
            return WildFamily.TIGER;
        }
        if (biome.is(ModTags.BIGCAT_FOREST_BIOMES)) {
            return this.random.nextInt(24) < 14 ? WildFamily.LEOPARD : WildFamily.PANTHER; // 14 vs 10
        }
        if (biome.is(ModTags.BIGCAT_LION_BIOMES)) {
            return WildFamily.LION;
        }

        // Safety net for any biome not explicitly listed — decide by climate, never pure random.
        float temperature = biome.value().getBaseTemperature();
        if (temperature <= 0.15F) {
            return WildFamily.SNOW_LEOPARD;
        }
        if (temperature >= 1.5F) {
            return WildFamily.LION;
        }
        return this.random.nextBoolean() ? WildFamily.LEOPARD : WildFamily.PANTHER;
    }

    private BigCatVariant rollVariantForFamily(WildFamily family) {
        return switch (family) {
            case SNOW_LEOPARD -> BigCatVariant.SNOW_LEOPARD;
            case LEOPARD -> BigCatVariant.LEOPARD;
            case PANTHER -> BigCatVariant.PANTHER;
            case TIGER -> BigCatVariant.randomWildTiger(this.random);
            case LION -> BigCatVariant.randomWildLion(this.random);
        };
    }

    public static boolean isSnowyBiome(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos) {
        return level.getBiome(pos).value().getBaseTemperature() <= 0.15F;
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void openMouth() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    /** Mouth-open animation + horse's drinking sound, shared by both essences. */
    private void useEssence(Player player, ItemStack stack) {
        openMouth();
        if (!this.level().isClientSide) {
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_DRINKING.get(), 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!player.getInventory().add(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE))) {
                player.drop(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE), false);
            }
        }
    }

    public int getTailTicks() {
        return this.entityData.get(DATA_TAIL_TICKS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BigCatVariant.LION_FEMALE.getId());
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_TAIL_TICKS, 0);
        builder.define(DATA_HAS_EATEN, false);
        builder.define(DATA_HAS_MEDALLION, false);
        builder.define(DATA_SITTING_SYNCED, false);
        builder.define(DATA_GHOST, false);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_HAS_CHEST, false);
        builder.define(DATA_HAS_WINGS, false);
        builder.define(DATA_TRANSFORM_TICKS, 0);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
        builder.define(DATA_WING_FLAP_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("BigCatVariant", getVariant().name());
        tag.putInt("HungerCooldown", this.hungerCooldown);
        tag.putBoolean("BigCatHasEaten", hasEaten());
        tag.putBoolean("BigCatHasMedallion", hasMedallion());
        tag.putBoolean("BigCatSittingSynced", isSittingSynced());
        tag.putBoolean("BigCatGhost", isGhost());
        tag.putBoolean("BigCatSaddled", isSaddled());
        if (this.saddleItemId != null) {
            tag.putString("BigCatSaddleItem", this.saddleItemId.toString());
        }
        tag.putBoolean("BigCatHasChest", hasChest());
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
            tag.put("BigCatChestItems", chestItems);
        }
        tag.putBoolean("BigCatWings", hasWings());
        tag.putBoolean("BigCatFlying", getIsFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BigCatVariant", 8)) {
            try {
                setVariant(BigCatVariant.valueOf(tag.getString("BigCatVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("HungerCooldown")) {
            this.hungerCooldown = tag.getInt("HungerCooldown");
        }
        if (tag.contains("BigCatHasEaten")) {
            setHasEaten(tag.getBoolean("BigCatHasEaten"));
        }
        if (tag.contains("BigCatHasMedallion")) {
            this.entityData.set(DATA_HAS_MEDALLION, tag.getBoolean("BigCatHasMedallion"));
        }
        if (tag.contains("BigCatSittingSynced")) {
            this.setSitting(tag.getBoolean("BigCatSittingSynced"));
        }
        if (tag.contains("BigCatGhost")) {
            setGhost(tag.getBoolean("BigCatGhost"));
        }
        if (tag.contains("BigCatSaddled")) {
            setSaddled(tag.getBoolean("BigCatSaddled"));
        }
        if (tag.contains("BigCatSaddleItem", 8)) {
            this.saddleItemId = net.minecraft.resources.ResourceLocation.parse(tag.getString("BigCatSaddleItem"));
        }
        if (tag.contains("BigCatHasChest")) {
            setHasChest(tag.getBoolean("BigCatHasChest"));
        }
        if (tag.contains("BigCatChestItems", 9)) {
            net.minecraft.nbt.ListTag chestItems = tag.getList("BigCatChestItems", 10);
            for (int i = 0; i < chestItems.size(); i++) {
                CompoundTag itemTag = chestItems.getCompound(i);
                int slot = itemTag.getInt("Slot");
                ItemStack chestStack = ItemStack.parse(this.registryAccess(), itemTag.getCompound("Item")).orElse(ItemStack.EMPTY);
                if (slot >= 0 && slot < chestInventory.getContainerSize()) {
                    chestInventory.setItem(slot, chestStack);
                }
            }
        }
        if (tag.contains("BigCatWings")) {
            this.entityData.set(DATA_HAS_WINGS, tag.getBoolean("BigCatWings"));
        }
        if (tag.contains("BigCatFlying")) {
            setIsFlying(tag.getBoolean("BigCatFlying"));
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && target instanceof LivingEntity living && !living.isAlive()) {
            // Made a kill — go neutral for a while instead of immediately hunting again.
            this.hungerCooldown = HUNGER_COOLDOWN_TICKS;
            this.setTarget(null);
        }
        return hurt;
    }

    @Override
    protected net.minecraft.world.phys.AABB makeBoundingBox() {
        if (this.isBaby()) {
            net.minecraft.world.entity.EntityDimensions babyDimensions =
                    this.getType().getDimensions().scale(BABY_HITBOX_SCALE);
            return babyDimensions.makeBoundingBox(this.position());
        }
        return super.makeBoundingBox();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        openMouth();
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_AMBIENT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        openMouth();
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_HURT_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        openMouth();
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH.get();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickIdleCounters();
            tickSprintSpeed();
            tickEatNearbyFood();
            tickWingTransform();
            tickWingedFlight();
            avoidHazardsAhead();
            if (this.hungerCooldown > 0) {
                this.hungerCooldown--;
            }
        }
    }

    /** Wing-flap sound + no-target glide-down, only relevant once winged — same behavior the wyvern has. */
    private void tickWingedFlight() {
        if (!hasWings()) {
            return;
        }
        if (!getIsFlying() && !this.isSittingSynced() && isOnAir() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        if (this.isOrderedToSit() && getIsFlying()) {
            setIsFlying(false);
        }

        if (getIsFlying() && this.isVehicle() && isAscendHeld()) {
            int flapCounter = this.entityData.get(DATA_WING_FLAP_TICKS);
            if (++flapCounter > WING_FLAP_BURST_TICKS) {
                flapCounter = 0;
            }
            this.entityData.set(DATA_WING_FLAP_TICKS, flapCounter);
            if (flapCounter == 5) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_WING_FLAP.get(), 0.4F, 1.0F);
            }
        } else {
            this.entityData.set(DATA_WING_FLAP_TICKS, 0);
        }

        if (getIsFlying() && !this.isVehicle()) {
            net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
            double newY = Math.max(motion.y - 0.03D, -0.25D);
            this.setDeltaMovement(motion.x, newY, motion.z);
            if (this.onGround()) {
                setIsFlying(false);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            trySpawnGhost();
        }
        super.die(source);
    }

    @Override
    public void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        dropAllEquipment();
        dropCombatLoot(level, recentlyHitByPlayer);
    }

    /** Medallion/saddle/chest always drop if present, regardless of who killed it. Never affected by Looting. */
    public void dropAllEquipment() {
        dropAllEquipment(true);
    }

    private void dropAllEquipment(boolean includeMedallion) {
        if (includeMedallion && hasMedallion()) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.MEDALLION.get()));
            this.entityData.set(DATA_HAS_MEDALLION, false);
        }
        if (isSaddled()) {
            net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                    ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                    : net.minecraft.world.item.Items.SADDLE;
            this.spawnAtLocation(new ItemStack(saddleItem));
            this.saddleItemId = null;
            setSaddled(false);
        }
        if (hasChest()) {
            this.spawnAtLocation(new ItemStack(net.minecraft.world.item.Items.CHEST));
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                this.spawnAtLocation(chestInventory.getItem(slot));
            }
            setHasChest(false);
        }
    }

    /** Snapshot used to restore this big cat later from a filled Pet Amulet. */
    private CompoundTag buildAmuletTag(java.util.UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("BigCatVariant", getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putBoolean("Wings", hasWings());
        tag.putBoolean("Medallion", hasMedallion());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Pet Amulet capture: instant, no vanish animation. Medallion/saddle/chest drop on the ground, not saved. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropAllEquipment(false); // keep the medallion — it travels with the cat inside the amulet
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    private void dropCombatLoot(ServerLevel level, boolean recentlyHitByPlayer) {
        LivingEntity killer = this.getLastHurtByMob();
        if (!MoCLootUtil.isKilledByPlayerOrTamedWolf(recentlyHitByPlayer, killer)) {
            return;
        }

        MoCExperienceUtil.dropExperienceOrb(level, this, MoCExperienceUtil.rollStandardXp(this.random));

        int lootingLevel = MoCLootUtil.getLootingLevel(killer);

        MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.BIG_CAT_CLAW.get(), MoCLootUtil.rollWithFlatLooting(this.random, 3, lootingLevel, 5));
    }

    /** 25% chance that a tamed big cat leaves a translucent ghost of its own variant when it dies. */
    private void trySpawnGhost() {
        if (!this.isTame() || isGhost() || hasWings() || this.random.nextInt(4) != 0) {
            return;
        }
        MoCBigCatEntity ghost = (MoCBigCatEntity) this.getType().create(this.level());
        if (ghost == null) {
            return;
        }
        ghost.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
        ghost.setVariant(getVariant());
        ghost.setGhost(true);
        ghost.setTame(true, false);
        ghost.setOwnerUUID(this.getOwnerUUID());
        ghost.setAge(0);
        this.level().addFreshEntity(ghost);
        ghost.playSound(com.example.neomocreatures.init.ModSounds.BIG_CAT_AMBIENT.get(), 1.0F, 1.0F);
        com.example.neomocreatures.util.NamingHelper.promptRename(ghost, this.getOwnerUUID());
    }

    private static final net.minecraft.resources.ResourceLocation SPRINT_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "big_cat_sprint");

    /** Wild hunting or a tamed cat chasing its target moves noticeably faster — matches the original's isSprinting() bonus. */
    private void tickSprintSpeed() {
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) {
            return;
        }
        boolean sprinting = this.getTarget() != null;
        boolean hasBonus = speedAttr.getModifier(SPRINT_MODIFIER_ID) != null;
        if (sprinting && !hasBonus) {
            speedAttr.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    SPRINT_MODIFIER_ID, SPRINT_SPEED_BONUS, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        } else if (!sprinting && hasBonus) {
            speedAttr.removeModifier(SPRINT_MODIFIER_ID);
        }
    }

    /**
     * Checks the block directly ahead in its current walking direction and stops
     * dead before stepping into water or off a drop tall enough to hurt it —
     * only while nobody's riding it (a rider's own choices aren't overridden).
     */
    private void avoidHazardsAhead() {
        if (this.isVehicle() || !this.onGround()) {
            return;
        }
        net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z < 0.0004D) {
            return;
        }
        net.minecraft.world.phys.Vec3 dir = new net.minecraft.world.phys.Vec3(motion.x, 0.0D, motion.z).normalize();
        net.minecraft.core.BlockPos ahead = this.blockPosition()
                .offset((int) Math.round(dir.x), 0, (int) Math.round(dir.z));

        if (this.level().getFluidState(ahead).is(net.minecraft.tags.FluidTags.WATER)) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, motion.y, 0.0D);
            return;
        }

        net.minecraft.core.BlockPos.MutableBlockPos check = ahead.below().mutable();
        int drop = 0;
        while (drop <= SAFE_FALL_BLOCKS && this.level().getBlockState(check).getCollisionShape(this.level(), check).isEmpty()) {
            check.move(0, -1, 0);
            drop++;
        }
        if (drop > SAFE_FALL_BLOCKS) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, motion.y, 0.0D);
        }
    }

    /** Random idle tail swish + the mouth-open animation timer. */
    private void tickIdleCounters() {
        int tail = this.entityData.get(DATA_TAIL_TICKS);
        if (tail > 0 && ++tail > 10) {
            tail = 0;
        }
        if (tail == 0 && this.random.nextInt(250) == 0) {
            tail = 1;
        }
        this.entityData.set(DATA_TAIL_TICKS, tail);

        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > 30) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        int growthTicks = getVariant().getGrowthTicks();
        float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private float lastAppliedScale = -1F;

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction() * (float) getVariant().getRenderScale();
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
    public boolean canMate(net.minecraft.world.entity.animal.Animal otherAnimal) {
        if (!(otherAnimal instanceof MoCBigCatEntity other) || other == this) {
            return false;
        }
        if (isSterile() || other.isSterile()) {
            return false;
        }
        if (!super.canMate(otherAnimal)) {
            return false;
        }
        return BigCatVariant.canBreedTogether(getVariant(), other.getVariant());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        if (!(otherParent instanceof MoCBigCatEntity other)) {
            return null;
        }
        MoCBigCatEntity cub = com.example.neomocreatures.init.ModEntities.MOC_BIG_CAT.get().create(level);
        if (cub == null) {
            return null;
        }
        cub.setVariant(BigCatVariant.rollOffspring(getVariant(), other.getVariant(), this.random));

        // Both parents are tamed (that's the only way they could breed at all) —
        // the cub is tamed to the same owner the instant it's born, per the wiki.
        java.util.UUID ownerId = this.getOwnerUUID() != null ? this.getOwnerUUID() : other.getOwnerUUID();
        if (ownerId != null) {
            cub.setTame(true, false);
            cub.setOwnerUUID(ownerId);
            com.example.neomocreatures.util.NamingHelper.promptRename(cub, ownerId);
        }
        return cub;
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
        double x = this.getX() - Math.sin(yaw) * RIDER_FORWARD;
        double z = this.getZ() + Math.cos(yaw) * RIDER_FORWARD;
        float extraHeight = getVariant() == BigCatVariant.LIGER ? LIGER_RIDER_HEIGHT_BONUS : 0F;
        double y = this.getY() + (RIDER_HEIGHT + extraHeight) * this.getScale();
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(Player player, net.minecraft.world.phys.Vec3 travelVector) {
        double vertical = hasWings() ? (isAscendHeld() ? 1.0D : (isDescendHeld() ? -1.0D : 0.0D)) : 0.0D;
        return new net.minecraft.world.phys.Vec3(player.xxa, vertical, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        // One of the fastest mountable mobs — noticeably quicker than its own wandering/hunting pace.
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.2F;
    }

    private float flyerFriction() {
        return 0.94F;
    }

    private void applyWaterBuoyancy() {
        if (this.isInWater() && !getIsFlying()) {
            double submergedFraction = this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
        }
    }

    private void applyLavaBuoyancy() {
        if (this.isInLava() && !getIsFlying()) {
            double submergedFraction = this.getFluidHeight(net.minecraft.tags.FluidTags.LAVA);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
        }
    }

    @Override
    protected void tickRidden(Player player, net.minecraft.world.phys.Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        if (!hasWings()) {
            return;
        }

        if (isAscendHeld()) {
            setIsFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, RIDDEN_ASCEND_THRUST, 0.0D));
        } else if (isDescendHeld()) {
            setIsFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -RIDDEN_DESCEND_THRUST, 0.0D));
        } else if (this.onGround()) {
            setIsFlying(false);
        } else {
            // No input at all while airborne and mounted — glide down, same as the wyvern with an idle rider.
            setIsFlying(true);
        }
    }

    @Override
    public void travel(net.minecraft.world.phys.Vec3 travelVector) {
        if (!hasWings()) {
            applyWaterBuoyancy();
            applyLavaBuoyancy();
            super.travel(travelVector);
            return;
        }

        if (this.isVehicle() && this.getControllingPassenger() instanceof Player && getIsFlying()) {
            this.setNoGravity(true);
            this.moveRelative(RIDDEN_FLYER_FRICTION / 10F, travelVector);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            net.minecraft.world.phys.Vec3 delta = this.getDeltaMovement()
                    .multiply(RIDDEN_FLYER_FRICTION, RIDDEN_FLYER_FALL_SPEED, RIDDEN_FLYER_FRICTION)
                    .subtract(0.0D, RIDDEN_FLYER_GRAVITY_PULL, 0.0D);
            if (this.isInWater() && delta.y < 0.0D) {
                delta = delta.multiply(1.0D, 0.0D, 1.0D);
            }
            this.setDeltaMovement(delta);
            this.fallDistance = 0.0F;
            return;
        }

        if (getIsFlying() && !this.isPassenger() && !this.isVehicle()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(flyerFriction()));
            }
            this.fallDistance = 0.0F;
        } else {
            applyWaterBuoyancy();
            applyLavaBuoyancy();
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canJump() {
        return isSaddled() && this.isVehicle() && !hasWings();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return !hasWings() && !this.isBaby() && super.causeFallDamage(fallDistance, multiplier, source);
    }

    /** Fixed 2.7-block jump, no charge bar — same one-shot approach as the elephant's. */
    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 0 && (this.onGround() || this.isInWater() || this.isInLava())) {
            net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, JUMP_VELOCITY, motion.z);
            this.hasImpulse = true;
        }
    }

    @Override
    public void handleStartJump(int jumpPower) {
        // No charge to release — the jump already happened in onPlayerJump().
    }

    @Override
    public void handleStopJump() {
        // Nothing to reset — no charge state is kept.
    }
    

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setVariant(com.example.neomocreatures.entity.bigcat.BigCatVariant.valueOf(tag.getString("BigCatVariant")));
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
        if (tag.getBoolean("Wings")) {
            this.setWings(true);
        }
        if (tag.getBoolean("Medallion")) {
            this.setMedallion(true);
        }
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
    }
}
