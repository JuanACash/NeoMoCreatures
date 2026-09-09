package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.manticore.ManticoreVariant;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class MoCManticoreEntity extends TamableAnimal implements com.example.neomocreatures.entity.egg.EggHatchable,
        net.minecraft.world.entity.HasCustomInventoryScreen {

    private static final int STING_CHANCE = 5;
    private static final int STING_ANIM_TICKS = 50;
    private static final int GROWTH_TICKS = 72000;
    private static final float BABY_SCALE = 0.5F;
    private static final float ADULT_SCALE = 1.35F;

    private static final float RIDER_HEIGHT = 0.6F;
    private static final float RIDER_FORWARD = 0.1F;
    private static final float RIDDEN_FLYER_FRICTION = 0.93F;
    private static final double RIDDEN_ASCEND_THRUST = 0.15D;
    private static final double RIDDEN_DESCEND_THRUST = 0.3D;
    private static final double RIDDEN_FLYER_FALL_SPEED = 0.6D;
    private static final double RIDDEN_FLYER_GRAVITY_PULL = 0.02D;

    private boolean huntingFlying;
    private int rideWingFlapCounter;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAIL_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STING_TICKS =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SITTING_SYNCED =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCManticoreEntity.class, EntityDataSerializers.BOOLEAN);

    private final net.minecraft.world.SimpleContainer chestInventory = new net.minecraft.world.SimpleContainer(9);
    @Nullable
    private net.minecraft.resources.ResourceLocation saddleItemId;

    public MoCManticoreEntity(EntityType<? extends MoCManticoreEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ManticoreDarknessTargetGoal<>(this, Player.class));
    }

    private static float getBrightness(MoCManticoreEntity manticore) {
        return manticore.level().getMaxLocalRawBrightness(manticore.blockPosition()) / 15.0F;
    }

    private static class ManticoreDarknessTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCManticoreEntity manticore;

        ManticoreDarknessTargetGoal(MoCManticoreEntity manticore, Class<T> targetType) {
            super(manticore, targetType, true);
            this.manticore = manticore;
        }

        @Override
        public boolean canUse() {
            if (this.manticore.isTame() || this.manticore.isOrderedToSit()) {
                return false;
            }
            boolean inNether = this.manticore.level().dimension() == net.minecraft.world.level.Level.NETHER;
            return (inNether || getBrightness(this.manticore) <= 0.5F) && super.canUse();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FLYING_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason,
                                         @Nullable SpawnGroupData spawnGroupData) {
        if (spawnReason == MobSpawnType.NATURAL || spawnReason == MobSpawnType.CHUNK_GENERATION) {
            if (level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER) {
                setVariant(ManticoreVariant.FIRE);
                if (this.random.nextFloat() < 0.15F) {
                    EntityType<?> riderType = this.random.nextBoolean() ? EntityType.ZOMBIFIED_PIGLIN : EntityType.PIGLIN;
                    spawnRider(level, riderType);
                }
            } else {
                var biome = level.getBiome(this.blockPosition());
                boolean snowy = biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_PLAINS)
                        || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA)
                        || biome.is(net.minecraft.world.level.biome.Biomes.GROVE)
                        || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_SLOPES)
                        || biome.is(net.minecraft.world.level.biome.Biomes.FROZEN_PEAKS)
                        || biome.is(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS)
                        || biome.is(net.minecraft.world.level.biome.Biomes.ICE_SPIKES)
                        || biome.is(net.minecraft.world.level.biome.Biomes.FROZEN_OCEAN)
                        || biome.is(net.minecraft.world.level.biome.Biomes.FROZEN_RIVER)
                        || biome.is(net.minecraft.world.level.biome.Biomes.DEEP_FROZEN_OCEAN);
                if (snowy) {
                    setVariant(ManticoreVariant.FROST);
                } else {
                    int roll = this.random.nextInt(3);
                    setVariant(roll == 0 ? ManticoreVariant.PLAIN : roll == 1 ? ManticoreVariant.TOXIC : ManticoreVariant.DARK);
                }
                if (this.random.nextFloat() < 0.15F) {
                    EntityType<?> riderType = this.random.nextBoolean() ? EntityType.ZOMBIE : EntityType.SKELETON;
                    spawnRider(level, riderType);
                }
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    private void spawnRider(ServerLevelAccessor level, EntityType<?> riderType) {
        Entity rider = riderType.create(level.getLevel());
        if (rider instanceof Mob mob) {
            mob.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0F);
            level.getLevel().addFreshEntity(mob);
            mob.startRiding(this);
        }
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(ManticoreVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.setBaby(true);
        this.setAge(-GROWTH_TICKS);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            com.example.neomocreatures.util.NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.PORKCHOP)
                || stack.is(net.minecraft.world.item.Items.COD)
                || stack.is(net.minecraft.world.item.Items.SALMON);
    }

    public ManticoreVariant getVariant() {
        return ManticoreVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(ManticoreVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public boolean fireImmune() {
        return getVariant().isFireImmune() || super.fireImmune();
    }

    public boolean isSittingSynced() {
        return this.entityData.get(DATA_SITTING_SYNCED);
    }

    private void setSitting(boolean sitting) {
        this.setOrderedToSit(sitting);
        this.entityData.set(DATA_SITTING_SYNCED, sitting);
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ManticoreVariant.PLAIN.getId());
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_TAIL_TICKS, 0);
        builder.define(DATA_STING_TICKS, 0);
        builder.define(DATA_SITTING_SYNCED, false);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_HAS_CHEST, false);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ManticoreVariant", getVariant().name());
        tag.putBoolean("ManticoreSittingSynced", isSittingSynced());
        tag.putBoolean("ManticoreSaddled", isSaddled());
        if (this.saddleItemId != null) {
            tag.putString("ManticoreSaddleItem", this.saddleItemId.toString());
        }
        tag.putBoolean("ManticoreHasChest", hasChest());
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
            tag.put("ManticoreChestItems", chestItems);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ManticoreVariant", 8)) {
            try {
                setVariant(ManticoreVariant.valueOf(tag.getString("ManticoreVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("ManticoreSittingSynced")) {
            setSitting(tag.getBoolean("ManticoreSittingSynced"));
        }
        if (tag.contains("ManticoreSaddled")) {
            setSaddled(tag.getBoolean("ManticoreSaddled"));
        }
        if (tag.contains("ManticoreSaddleItem", 8)) {
            this.saddleItemId = net.minecraft.resources.ResourceLocation.parse(tag.getString("ManticoreSaddleItem"));
        }
        if (tag.contains("ManticoreHasChest")) {
            setHasChest(tag.getBoolean("ManticoreHasChest"));
        }
        if (tag.contains("ManticoreChestItems", 9)) {
            net.minecraft.nbt.ListTag chestItems = tag.getList("ManticoreChestItems", 10);
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

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void openMouth() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getTailTicks() {
        return this.entityData.get(DATA_TAIL_TICKS);
    }

    public int getStingTicks() {
        return this.entityData.get(DATA_STING_TICKS);
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
        return this.isBaby() ? com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH_BABY.get()
                : com.example.neomocreatures.init.ModSounds.BIG_CAT_DEATH.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(net.minecraft.world.item.Items.BOOK)) {
            if (!this.level().isClientSide) {
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
            if (!this.level().isClientSide) {
                setSitting(!this.isSittingSynced());
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

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                this.heal(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
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

        if (this.isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
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

    private void openChestMenu(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : net.minecraft.network.chat.Component.literal("Manticore Storage");
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                            net.minecraft.world.inventory.MenuType.GENERIC_9x1, id, inv, this.chestInventory, 1),
                    title));
        }
    }

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
        double y = this.getY() + RIDER_HEIGHT * this.getScale();
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        double vertical = isAscendHeld() ? 1.0D : (isDescendHeld() ? -1.0D : 0.0D);
        return new Vec3(player.xxa, vertical, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return this.onGround()
                ? (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)
                : (float) this.getAttributeValue(Attributes.FLYING_SPEED);
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

        if (isAscendHeld()) {
            this.setNoGravity(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, RIDDEN_ASCEND_THRUST, 0.0D));
            if (!this.level().isClientSide) {
                if (++rideWingFlapCounter >= 20) {
                    rideWingFlapCounter = 0;
                    this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_WING_FLAP.get(), 0.4F, 1.0F);
                }
            }
        } else if (isDescendHeld()) {
            this.setNoGravity(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -RIDDEN_DESCEND_THRUST, 0.0D));
        } else if (this.onGround()) {
            this.setNoGravity(false);
        } else {
            this.setNoGravity(true);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player && !this.onGround()) {
            this.moveRelative(RIDDEN_FLYER_FRICTION / 10F, travelVector);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            Vec3 delta = this.getDeltaMovement()
                    .multiply(RIDDEN_FLYER_FRICTION, RIDDEN_FLYER_FALL_SPEED, RIDDEN_FLYER_FRICTION)
                    .subtract(0.0D, RIDDEN_FLYER_GRAVITY_PULL, 0.0D);
            if (this.isInWater() && delta.y < 0.0D) {
                delta = delta.multiply(1.0D, 0.0D, 1.0D);
            }
            this.setDeltaMovement(delta);
            this.fallDistance = 0.0F;
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (!hurt) {
            return false;
        }
        boolean stinging = this.entityData.get(DATA_STING_TICKS) == 0 && this.random.nextInt(STING_CHANCE) == 0;
        if (stinging && target instanceof LivingEntity living) {
            this.entityData.set(DATA_STING_TICKS, 1);
            this.playSound(com.example.neomocreatures.init.ModSounds.SCORPION_STING.get(), 1.0F, 1.0F);
            getVariant().applySting(living, this.level().dimension() == net.minecraft.world.level.Level.NETHER);
        } else {
            openMouth();
        }
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        dropAllEquipment();
        dropCombatLoot(level, recentlyHitByPlayer);
    }

    public void dropAllEquipment() {
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

    private CompoundTag buildAmuletTag(java.util.UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("ManticoreVariant", getVariant().name());
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
        dropAllEquipment();
        CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    private void dropCombatLoot(ServerLevel level, boolean recentlyHitByPlayer) {
        LivingEntity killer = this.getLastHurtByMob();
        boolean killedByPlayerOrWolf = recentlyHitByPlayer
                || (killer instanceof net.minecraft.world.entity.animal.Wolf wolf && wolf.isTame());
        if (!killedByPlayerOrWolf) {
            return;
        }

        level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                level, this.getX(), this.getY(), this.getZ(), 5));

        int lootingLevel = 0;
        if (killer != null) {
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> looting =
                    killer.level().registryAccess()
                            .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING);
            lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(looting, killer);
        }

        int clawCount = Math.min(this.random.nextInt(3) + (lootingLevel > 0 ? this.random.nextInt(lootingLevel + 1) : 0), 2 + lootingLevel);
        if (clawCount > 0) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.BIG_CAT_CLAW.get(), clawCount));
        }

        int chitinCount = Math.min(this.random.nextInt(3) + (lootingLevel > 0 ? this.random.nextInt(lootingLevel + 1) : 0), 2 + lootingLevel);
        if (chitinCount > 0) {
            this.spawnAtLocation(new ItemStack(chitinItemFor(getVariant()), chitinCount));
        }

        int stingCount = Math.min(this.random.nextInt(3) + (lootingLevel > 0 ? this.random.nextInt(lootingLevel + 1) : 0), 2 + lootingLevel);
        if (stingCount > 0) {
            this.spawnAtLocation(new ItemStack(stingItemFor(getVariant()), stingCount));
        }

        float eggChance = 0.25F + lootingLevel * 0.05F;
        if (this.random.nextFloat() < eggChance) {
            this.spawnAtLocation(new ItemStack(eggItemFor(getVariant())));
        }
    }

    private static net.minecraft.world.item.Item chitinItemFor(ManticoreVariant variant) {
        return switch (variant) {
            case PLAIN -> com.example.neomocreatures.init.ModItems.CHITIN.get();
            case DARK -> com.example.neomocreatures.init.ModItems.CHITIN_BLACK.get();
            case FROST -> com.example.neomocreatures.init.ModItems.CHITIN_FROST.get();
            case FIRE -> com.example.neomocreatures.init.ModItems.CHITIN_NETHER.get();
            case TOXIC -> com.example.neomocreatures.init.ModItems.CHITIN_UNDEAD.get();
        };
    }

    private static net.minecraft.world.item.Item stingItemFor(ManticoreVariant variant) {
        return switch (variant) {
            case PLAIN -> com.example.neomocreatures.init.ModItems.SCORP_STING_DIRT.get();
            case DARK -> com.example.neomocreatures.init.ModItems.SCORP_STING_CAVE.get();
            case FROST -> com.example.neomocreatures.init.ModItems.SCORP_STING_FROST.get();
            case FIRE -> com.example.neomocreatures.init.ModItems.SCORP_STING_NETHER.get();
            case TOXIC -> com.example.neomocreatures.init.ModItems.SCORP_STING_UNDEAD.get();
        };
    }

    private static net.minecraft.world.item.Item eggItemFor(ManticoreVariant variant) {
        return switch (variant) {
            case PLAIN -> com.example.neomocreatures.init.ModItems.PLAIN_MANTICORE_EGG.get();
            case DARK -> com.example.neomocreatures.init.ModItems.DARK_MANTICORE_EGG.get();
            case FROST -> com.example.neomocreatures.init.ModItems.FROST_MANTICORE_EGG.get();
            case FIRE -> com.example.neomocreatures.init.ModItems.FIRE_MANTICORE_EGG.get();
            case TOXIC -> com.example.neomocreatures.init.ModItems.TOXIC_MANTICORE_EGG.get();
        };
    }

    public boolean isSoaring() {
        BlockPos pos = BlockPos.containing(this.getX(), this.getY() - 0.2D, this.getZ());
        return this.level().getBlockState(pos).isAir();
    }

    private void updateFlight() {
        if (this.getControllingPassenger() instanceof Player || this.isOrderedToSit()) {
            huntingFlying = false;
            return;
        }

        LivingEntity target = this.getTarget();
        boolean wantsToHunt = target != null && target.getY() > this.getY() + 1.5D;

        if (!huntingFlying && wantsToHunt) {
            huntingFlying = true;
        } else if (huntingFlying && (target == null || target.getY() <= this.getY() + 0.5D)) {
            huntingFlying = false;
        }

        this.setNoGravity(huntingFlying);

        if (huntingFlying && target != null) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            float yaw = (float) (net.minecraft.util.Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
            this.setYRot(yaw);
            this.yBodyRot = yaw;
            this.yHeadRot = yaw;

            Vec3 toTarget = new Vec3(dx,
                    (target.getY() + target.getBbHeight() * 0.5D) - this.getY(),
                    dz);
            double dist = toTarget.length();
            if (dist > 0.5D) {
                Vec3 dir = toTarget.normalize().scale(0.06D);
                Vec3 newMotion = this.getDeltaMovement().scale(0.9D).add(dir);
                double maxSpeed = 0.35D;
                double speedSqr = newMotion.horizontalDistanceSqr();
                if (speedSqr > maxSpeed * maxSpeed) {
                    double scale = maxSpeed / Math.sqrt(speedSqr);
                    newMotion = new Vec3(newMotion.x * scale, newMotion.y, newMotion.z * scale);
                }
                this.setDeltaMovement(newMotion);
            }
        } else if (!this.onGround()) {
            if (this.getDeltaMovement().y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1D, 0.6D, 1D));
            }
        }
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = net.minecraft.util.Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return net.minecraft.util.Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private float lastAppliedScale = -1F;

    private void tickGrowth() {
        net.minecraft.world.entity.ai.attributes.AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction() * ADULT_SCALE;
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
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            if (!this.isTame() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }

            tickIdleCounters();
            updateFlight();
        }
    }

    private void tickIdleCounters() {
        int tail = this.entityData.get(DATA_TAIL_TICKS);
        if (tail > 0 && ++tail > 10) {
            tail = 0;
        }
        if (tail == 0 && this.random.nextInt(200) == 0) {
            tail = 1;
        }
        this.entityData.set(DATA_TAIL_TICKS, tail);

        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > 30) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int sting = this.entityData.get(DATA_STING_TICKS);
        if (sting > 0 && ++sting > STING_ANIM_TICKS) {
            sting = 0;
        }
        this.entityData.set(DATA_STING_TICKS, sting);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}