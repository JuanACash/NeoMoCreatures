package com.example.neomocreatures.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.entity.wyvern.WyvernTier;
import com.example.neomocreatures.entity.wyvern.WyvernVariant;
import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MoCWyvernEntity extends TamableAnimal implements EggHatchable, net.minecraft.world.entity.HasCustomInventoryScreen, GrowthScaled {

    private static final double AGGRO_RADIUS = 14.0D;
    private static final int POISON_DURATION_TICKS = 200;
    private static final int LAIR_DESPAWN_Y = 10;
    private static final int WING_FLAP_BURST_TICKS = 20;
    private static final int MOUTH_BURST_TICKS = 30;
    private static final float BABY_SCALE = 0.4F;
    private static final int TIER_1_GROWTH_TICKS = 24000;
    private static final int SLOW_GROWTH_TICKS = 48000;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TIER =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WING_FLAP_TICKS =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BITE_TICKS =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    /** 0 = none, 1 = iron, 2 = gold, 3 = diamond — same three tiers as horse armor. */
    private static final EntityDataAccessor<Integer> DATA_ARMOR_TIER =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    /** Works on every tier, unlike armor/saddle restrictions elsewhere. */
    private static final EntityDataAccessor<Boolean> DATA_HAS_CHEST =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DIVING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TARGET =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TICKS =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GHOST =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
   
   
    private static final int TRANSFORM_DURATION_TICKS = 100;
    private static final int TRANSFORM_SOUND_TICKS = 60;

    public MoCWyvernEntity(EntityType<? extends MoCWyvernEntity> type, Level level) {
        super(type, level);
        this.moveControl = new WyvernMoveControl(this);
        WyvernTier tier;
        if (type == ModEntities.WYVERN_MOTHER_TAMED.get()) {
            tier = WyvernTier.MOTHER_TAMED;
        } else if (type == ModEntities.WYVERN_MOTHER.get()) {
            tier = WyvernTier.MOTHER;
        } else if (type == ModEntities.WYVERN_TIER2.get()) {
            tier = WyvernTier.TIER_2;
        } else {
            tier = WyvernTier.TIER_1;
        }
        setTier(tier);
        boolean isMotherTier = tier == WyvernTier.MOTHER || tier == WyvernTier.MOTHER_TAMED;
        // Only the server rolls the wild variant: a client-side roll could stick whenever the server's
        // pick equals the synced default (SUN), since default values aren't sent to clients.
        if (isMotherTier) {
            setVariant(WyvernVariant.MOTHER);
        } else if (!level.isClientSide) {
            setVariant(WyvernVariant.randomWild(this.random));
        }

        // 50/50 ground or air at spawn — only actually lands if there's solid
        // ground right below it; otherwise it just starts flying regardless.
        if (!level.isClientSide) {
            boolean wantsGround = this.random.nextBoolean();
            boolean solidGroundBelow = level.getBlockState(this.blockPosition().below()).canOcclude();
            setIsFlying(!(wantsGround && solidGroundBelow));
        }
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(WyvernVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
            }
        }
        int growthTicks = getTier() == WyvernTier.TIER_1 ? TIER_1_GROWTH_TICKS : SLOW_GROWTH_TICKS;
        this.setAge(-growthTicks);
        tickGrowth();

        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            this.setSitting(false);
            com.example.neomocreatures.util.NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    public static AttributeSupplier.Builder createTier2Attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FLYING_SPEED, 0.14D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    public static AttributeSupplier.Builder createMotherAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FLYING_SPEED, 0.13D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    public static AttributeSupplier.Builder createMotherTamedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FLYING_SPEED, 0.13D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    public WyvernVariant getVariant() {
        return WyvernVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(WyvernVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public boolean isGhost() {
        return this.entityData.get(DATA_GHOST);
    }

    private void setGhost(boolean ghost) {
        this.entityData.set(DATA_GHOST, ghost);
    }

    public boolean isTransforming() {
        return this.entityData.get(DATA_TRANSFORM_TICKS) > 0;
    }

    public int getTransformTicks() {
        return this.entityData.get(DATA_TRANSFORM_TICKS);
    }

    public WyvernVariant getTransformTarget() {
        return WyvernVariant.byId(this.entityData.get(DATA_TRANSFORM_TARGET));
    }

    private void startTransform(WyvernVariant target) {
        this.entityData.set(DATA_TRANSFORM_TARGET, target.getId());
        this.entityData.set(DATA_TRANSFORM_TICKS, TRANSFORM_DURATION_TICKS);
    }

    public WyvernTier getTier() {
        return WyvernTier.byId(this.entityData.get(DATA_TIER));
    }

    public void setTier(WyvernTier tier) {
        this.entityData.set(DATA_TIER, tier.getId());
    }

    public boolean getIsFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public void setIsFlying(boolean flying) {
        if (flying && !this.getIsFlying()) {
            this.flightTicks = MIN_FLIGHT_TICKS + this.random.nextInt(FLIGHT_TICKS_RANGE);
        }
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

    public int getWingFlapTicks() {
        return this.entityData.get(DATA_WING_FLAP_TICKS);
    }

    public int getBiteTicks() {
        return this.entityData.get(DATA_BITE_TICKS);
    }

    public boolean isSittingSynced() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.setOrderedToSit(sitting);
        this.entityData.set(DATA_SITTING, sitting);
    }

    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    /** 0 = none, 1 = iron, 2 = gold, 3 = diamond. */
    public int getArmorTier() {
        return this.entityData.get(DATA_ARMOR_TIER);
    }

    // Same armor point values as vanilla's own horse armor (Iron 5 / Gold 7 /
    // Diamond 11) — wyvern armor is a literal horse armor item, so it should
    // give literally the same defense.
    private static final double[] ARMOR_TIER_POINTS = {0.0D, 5.0D, 7.0D, 11.0D};
    private static final net.minecraft.resources.ResourceLocation ARMOR_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "wyvern_armor");

    public void setArmorTier(int tier) {
        this.entityData.set(DATA_ARMOR_TIER, tier);
        net.minecraft.world.entity.ai.attributes.AttributeInstance armorAttr = this.getAttribute(Attributes.ARMOR);
        if (armorAttr == null) {
            return;
        }
        armorAttr.removeModifier(ARMOR_MODIFIER_ID);
        if (tier > 0) {
            armorAttr.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    ARMOR_MODIFIER_ID, ARMOR_TIER_POINTS[tier],
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
        }
    }

    // 18 slots (2 rows), every tier can carry one, shears can never remove it.
    private final net.minecraft.world.SimpleContainer chestInventory = new net.minecraft.world.SimpleContainer(18);
    /** Which exact item to give back when the saddle is removed with shears — vanilla Saddle vs our HORSE_SADDLE. */
    @Nullable
    private net.minecraft.resources.ResourceLocation saddleItemId;

    public boolean hasChest() {
        return this.entityData.get(DATA_HAS_CHEST);
    }

    private void setHasChest(boolean hasChest) {
        this.entityData.set(DATA_HAS_CHEST, hasChest);
    }

    private void openChestMenu(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            net.minecraft.network.chat.Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : net.minecraft.network.chat.Component.literal("Wyvern Storage");
            serverPlayer.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (id, inv, p) -> new net.minecraft.world.inventory.ChestMenu(
                            net.minecraft.world.inventory.MenuType.GENERIC_9x2, id, inv, this.chestInventory, 2),
                    title));
        }
    }

    /**
     * Generic Entity hook vanilla already calls for the E-while-riding key on
     * ANY mount (not just AbstractHorse) — no special wiring needed on our
     * side beyond this override, same as tickRidden()/getRiddenInput() etc.
     */
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

    public void dropChestAndContents() {
        if (!hasChest()) {
            return;
        }
        this.spawnAtLocation(net.minecraft.world.item.Items.CHEST);
        for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
            this.spawnAtLocation(chestInventory.getItem(slot));
        }
        setHasChest(false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSittingSynced()) {
            return null;
        }
        startMouthAnimation();
        return ModSounds.WYVERN_GRUNT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        startMouthAnimation();
        return ModSounds.WYVERN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WYVERN_DEATH.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, WyvernVariant.SUN.getId());
        builder.define(DATA_TIER, WyvernTier.TIER_1.getId());
        builder.define(DATA_FLYING, false);
        builder.define(DATA_WING_FLAP_TICKS, 0);
        builder.define(DATA_BITE_TICKS, 0);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_ARMOR_TIER, 0);
        builder.define(DATA_HAS_CHEST, false);
        builder.define(DATA_DIVING, false);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
        builder.define(DATA_TRANSFORM_TARGET, WyvernVariant.MOTHER.getId());
        builder.define(DATA_TRANSFORM_TICKS, 0);
        builder.define(DATA_GHOST, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("WyvernVariant", getVariant().name());
        tag.putString("WyvernTier", getTier().name());
        tag.putBoolean("WyvernFlying", getIsFlying());
        tag.putBoolean("WyvernSittingSynced", isSittingSynced());
        tag.putBoolean("WyvernGhost", isGhost());
        tag.putBoolean("WyvernSaddled", isSaddled());
        if (this.saddleItemId != null) {
            tag.putString("WyvernSaddleItem", this.saddleItemId.toString());
        }
        tag.putInt("WyvernArmorTier", getArmorTier());
        tag.putBoolean("WyvernHasChest", hasChest());
        if (hasChest()) {
            net.minecraft.nbt.ListTag chestItems = new net.minecraft.nbt.ListTag();
            for (int slot = 0; slot < chestInventory.getContainerSize(); slot++) {
                ItemStack stack = chestInventory.getItem(slot);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", slot);
                    itemTag.put("Item", stack.save(this.registryAccess(), new CompoundTag()));
                    chestItems.add(itemTag);
                }
            }
            tag.put("WyvernChestItems", chestItems);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("WyvernVariant", 8)) {
            try {
                setVariant(WyvernVariant.valueOf(tag.getString("WyvernVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("WyvernTier", 8)) {
            try {
                setTier(WyvernTier.valueOf(tag.getString("WyvernTier")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("WyvernFlying")) {
            setIsFlying(tag.getBoolean("WyvernFlying"));
        }
        if (tag.contains("WyvernSittingSynced")) {
            this.setSitting(tag.getBoolean("WyvernSittingSynced"));
        }
        if (tag.contains("WyvernGhost")) {
            setGhost(tag.getBoolean("WyvernGhost"));
        }
        if (tag.contains("WyvernSaddled")) {
            setSaddled(tag.getBoolean("WyvernSaddled"));
        }
        if (tag.contains("WyvernSaddleItem", 8)) {
            this.saddleItemId = net.minecraft.resources.ResourceLocation.parse(tag.getString("WyvernSaddleItem"));
        }
        if (tag.contains("WyvernArmorTier")) {
            setArmorTier(tag.getInt("WyvernArmorTier"));
        }
        if (tag.getBoolean("WyvernHasChest")) {
            setHasChest(true);
        }
        if (tag.contains("WyvernChestItems", 9)) {
            net.minecraft.nbt.ListTag chestItems = tag.getList("WyvernChestItems", 10);
            for (int i = 0; i < chestItems.size(); i++) {
                CompoundTag itemTag = chestItems.getCompound(i);
                int slot = itemTag.getInt("Slot");
                ItemStack stack = ItemStack.parse(this.registryAccess(), itemTag.getCompound("Item"))
                        .orElse(ItemStack.EMPTY);
                if (slot >= 0 && slot < chestInventory.getContainerSize()) {
                    chestInventory.setItem(slot, stack);
                }
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new WyvernMeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new WyvernLandGoal(this));
        this.goalSelector.addGoal(5, new WyvernSoarGoal(this));
        this.goalSelector.addGoal(6, new WyvernGroundWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, (int) AGGRO_RADIUS,
                false, false, target -> !this.isTame() && !this.isVehicle()));
    }

        /** Ghast: RandomFloatAroundGoal — drifts to random points around it, a few blocks above the ground. */
    private static final class WyvernSoarGoal extends Goal {
        private final MoCWyvernEntity wyvern;

        WyvernSoarGoal(MoCWyvernEntity wyvern) {
            this.wyvern = wyvern;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.wyvern.canFlyFreely() || this.wyvern.wantsToLand()) {
                return false;
            }
            MoveControl control = this.wyvern.getMoveControl();
            if (!control.hasWanted()) {
                return true;
            }
            double distanceSqr = this.wyvern.distanceToSqr(control.getWantedX(), control.getWantedY(), control.getWantedZ());
            return distanceSqr < ARRIVED_DISTANCE_SQR || distanceSqr > LOST_DISTANCE_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.wyvern.getRandom();
            double x = this.wyvern.getX() + (random.nextDouble() * 2.0D - 1.0D) * SOAR_RANGE;
            double z = this.wyvern.getZ() + (random.nextDouble() * 2.0D - 1.0D) * SOAR_RANGE;
            int ground = this.wyvern.level().getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z));
            double y = ground + MIN_SOAR_ALTITUDE + random.nextInt(SOAR_ALTITUDE_RANGE);
            this.wyvern.getMoveControl().setWantedPosition(x, y, z, 1.0D);
        }
    }

    /** Parrot-like landing: once a flight is over it glides down to firm ground nearby and walks again. */
    private static final class WyvernLandGoal extends Goal {
        private final MoCWyvernEntity wyvern;
        @Nullable
        private BlockPos landingSpot;
        private int retryTicks;

        WyvernLandGoal(MoCWyvernEntity wyvern) {
            this.wyvern = wyvern;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.wyvern.canFlyFreely() && this.wyvern.wantsToLand();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void start() {
            this.findLandingSpot();
        }

        @Override
        public void stop() {
            this.landingSpot = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.wyvern.onGround() || this.isAboutToTouchDown()) {
                this.wyvern.setIsFlying(false);
                return;
            }
            if (this.landingSpot == null || --this.retryTicks <= 0) {
                this.findLandingSpot();
                return;
            }
            this.wyvern.getMoveControl().setWantedPosition(
                    this.landingSpot.getX() + 0.5D, this.landingSpot.getY() + 1.0D, this.landingSpot.getZ() + 0.5D, 1.0D);
        }

        /** A dry, sturdy spot within 8 blocks; over water or lava it just keeps flying a little longer. */
        private void findLandingSpot() {
            this.retryTicks = LANDING_RETRY_TICKS;
            RandomSource random = this.wyvern.getRandom();
            Level level = this.wyvern.level();
            int x = this.wyvern.getBlockX() + random.nextInt(LANDING_SEARCH_RANGE * 2 + 1) - LANDING_SEARCH_RANGE;
            int z = this.wyvern.getBlockZ() + random.nextInt(LANDING_SEARCH_RANGE * 2 + 1) - LANDING_SEARCH_RANGE;
            BlockPos spot = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
            BlockPos ground = spot.below();
            if (level.getFluidState(ground).isEmpty() && level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)) {
                this.landingSpot = spot;
            } else {
                this.landingSpot = null;
                this.wyvern.extendFlight();
            }
        }

        /** Something solid right under its feet — close enough to put its legs down. */
        private boolean isAboutToTouchDown() {
            AABB below = this.wyvern.getBoundingBox().move(0.0D, -TOUCHDOWN_HEIGHT, 0.0D);
            return !this.wyvern.level().noCollision(this.wyvern, below);
        }
    }

    /**
     * Ghast-style free flight: every few ticks a small push towards its destination, with air drag doing
     * the rest, so it glides in smooth curves and turns to face where it is flying. Checks the way is
     * clear first and gives up on blocked destinations. On the ground (or ridden) it moves like any mob.
     */
    private static final class WyvernMoveControl extends MoveControl {
        private final MoCWyvernEntity wyvern;
        private int impulseCooldown;

        WyvernMoveControl(MoCWyvernEntity wyvern) {
            super(wyvern);
            this.wyvern = wyvern;
        }

        @Override
        public void tick() {
            if (!this.wyvern.getIsFlying() || this.wyvern.isVehicle()) {
                super.tick();
                return;
            }
            if (this.operation == Operation.MOVE_TO && --this.impulseCooldown <= 0) {
                this.impulseCooldown = MIN_IMPULSE_INTERVAL + this.wyvern.getRandom().nextInt(IMPULSE_INTERVAL_RANGE);
                Vec3 toTarget = new Vec3(this.wantedX - this.wyvern.getX(), this.wantedY - this.wyvern.getY(),
                        this.wantedZ - this.wyvern.getZ());
                double distance = toTarget.length();
                Vec3 direction = toTarget.normalize();
                if (distance * distance < ARRIVED_DISTANCE_SQR || !this.canReach(direction, Mth.ceil(distance))) {
                    this.operation = Operation.WAIT;
                } else {
                    this.wyvern.setDeltaMovement(this.wyvern.getDeltaMovement().add(direction.scale(FLIGHT_IMPULSE * this.speedModifier)));
                }
            }
            this.faceFlightDirection();
        }

        private boolean canReach(Vec3 direction, int steps) {
            AABB box = this.wyvern.getBoundingBox();
            for (int i = 1; i < steps; i++) {
                box = box.move(direction);
                if (!this.wyvern.level().noCollision(this.wyvern, box)) {
                    return false;
                }
            }
            return true;
        }

        private void faceFlightDirection() {
            Vec3 motion = this.wyvern.getDeltaMovement();
            if (motion.horizontalDistanceSqr() > 1.0E-4D) {
                float yaw = (float) (Mth.atan2(motion.z, motion.x) * Mth.RAD_TO_DEG) - 90.0F;
                this.wyvern.setYRot(this.rotlerp(this.wyvern.getYRot(), yaw, TURN_SPEED));
                this.wyvern.yBodyRot = this.wyvern.getYRot();
            }
        }
    }

    private static class WyvernGroundWanderGoal extends WaterAvoidingRandomStrollGoal {
        private final MoCWyvernEntity wyvern;

        WyvernGroundWanderGoal(MoCWyvernEntity wyvern, double speedModifier) {
            super(wyvern, speedModifier);
            this.wyvern = wyvern;
        }

        @Override
        public boolean canUse() {
            return !this.wyvern.isVehicle() && !this.wyvern.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.wyvern.isVehicle() && !this.wyvern.getIsFlying() && super.canContinueToUse();
        }
    }

    private static class WyvernMeleeAttackGoal extends MeleeAttackGoal {
        private final MoCWyvernEntity wyvern;

        WyvernMeleeAttackGoal(MoCWyvernEntity wyvern, double speedModifier, boolean followEvenIfNotSeen) {
            super(wyvern, speedModifier, followEvenIfNotSeen);
            this.wyvern = wyvern;
        }

        @Override
        public boolean canUse() {
            return !this.wyvern.isVehicle() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.wyvern.isVehicle() && super.canContinueToUse();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);
        if (!wasHurt || this.level().isClientSide) {
            return wasHurt;
        }

        Entity attacker = source.getEntity();
        if (this.isTame() && attacker != null && attacker.equals(this.getOwner())) {
            this.setLastHurtByMob(null);
            this.setTarget(null);
            return wasHurt;
        }

        if (attacker instanceof Player player) {
            this.setTarget(player);
            setIsFlying(true);
        } else if (this.isTame()) {
            this.setTarget(null);
            setIsFlying(true);
        }
        return wasHurt;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(com.example.neomocreatures.init.ModItems.RAT_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.TURKEY_RAW.get());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isTame() && this.isOwnedBy(player)) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(net.minecraft.world.item.Items.BOOK)) {
                if (!this.level().isClientSide) {
                    com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(com.example.neomocreatures.init.ModItems.WHIP.get())) {
                if (!this.level().isClientSide) {
                    this.setSitting(!this.isSittingSynced());
                    this.setTarget(null);
                    this.getNavigation().stop();
                    this.level().playSound(null, this.blockPosition(), ModSounds.WHIP.get(),
                            net.minecraft.sounds.SoundSource.NEUTRAL, 0.5F,
                            0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
                    if (!player.getAbilities().instabuild) {
                        stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
                if (!this.level().isClientSide) {
                    capturePetInstant(player, hand);
                }
                return InteractionResult.SUCCESS;
            }
            if (isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
                startMouthAnimation();
                if (!this.level().isClientSide) {
                    this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                    this.heal(4.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            
            if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_DARKNESS.get())
                    && getVariant() == WyvernVariant.MOTHER && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(WyvernVariant.MOTHER_DARK);
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_UNDEAD.get())
                    && getVariant() == WyvernVariant.MOTHER && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(WyvernVariant.MOTHER_UNDEAD);
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_LIGHT.get())
                    && getVariant() == WyvernVariant.MOTHER && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startTransform(WyvernVariant.MOTHER_LIGHT);
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_LIGHT.get())
                    && !getVariant().isMother()) {
                if (!this.level().isClientSide) {
                    this.spawnAtLocation(new ItemStack(eggItemFor(getVariant())));
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_FIRE.get())
                    && getVariant().isMother()) {
                if (!this.level().isClientSide) {
                    this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.MOTHER_WYVERN_EGG.get()));
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }

            if (!this.isBaby() && !this.isSaddled()
                    && (stack.is(net.minecraft.world.item.Items.SADDLE)
                        || stack.is(com.example.neomocreatures.init.ModItems.HORSE_SADDLE.get()))) {
                if (!this.level().isClientSide) {
                    this.setSaddled(true);
                    this.saddleItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                    this.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.isBaby() && this.getArmorTier() == 0
                    && (stack.is(net.minecraft.world.item.Items.IRON_HORSE_ARMOR)
                        || stack.is(net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR)
                        || stack.is(net.minecraft.world.item.Items.DIAMOND_HORSE_ARMOR))) {
                int newArmorTier = stack.is(net.minecraft.world.item.Items.IRON_HORSE_ARMOR) ? 1
                        : stack.is(net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR) ? 2 : 3;
                if (!this.level().isClientSide) {
                    this.setArmorTier(newArmorTier);
                    this.playSound(ModSounds.HORSE_ARMOR_PUT.get(), 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.isBaby() && !hasChest() && stack.is(net.minecraft.world.item.Items.CHEST)) {
                if (!this.level().isClientSide) {
                    setHasChest(true);
                    this.playSound(net.minecraft.sounds.SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            // Shears: armor first, then saddle — same order as the horse.
            // The chest is NOT removable by shears — no branch for it here.
            if (this.isTame() && stack.is(net.minecraft.world.item.Items.SHEARS) && this.getArmorTier() > 0) {
                if (!this.level().isClientSide) {
                    net.minecraft.world.item.Item armorItem = switch (this.getArmorTier()) {
                        case 1 -> net.minecraft.world.item.Items.IRON_HORSE_ARMOR;
                        case 2 -> net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR;
                        default -> net.minecraft.world.item.Items.DIAMOND_HORSE_ARMOR;
                    };
                    this.setArmorTier(0);
                    this.spawnAtLocation(new ItemStack(armorItem));
                    this.playSound(ModSounds.HORSE_ARMOR_OFF.get(), 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            }
            if (this.isTame() && stack.is(net.minecraft.world.item.Items.SHEARS) && this.isSaddled()) {
                if (!this.level().isClientSide) {
                    this.setSaddled(false);
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
                    this.setSitting(false);
                    player.startRiding(this);
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide && hasChest() && player.isSecondaryUseActive()) {
                openChestMenu(player);
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide && player.isSecondaryUseActive()) {
                this.setSitting(!this.isSittingSynced());
                this.setTarget(null);
                this.getNavigation().stop();
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            poisonTarget(target);
            return true;
        }
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            poisonTarget(target);
        }
        return hurt;
    }

    private void poisonTarget(Entity target) {
        startMouthAnimation();
        if (!this.level().isClientSide) {
            this.playSound(ModSounds.WYVERN_POISON.get(), 1.0F, 1.0F);
        }
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
        }
    }

    private void startMouthAnimation() {
        if (this.entityData.get(DATA_BITE_TICKS) == 0) {
            this.entityData.set(DATA_BITE_TICKS, 1);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            dropChestAndContents();
            trySpawnGhost();
        }
        super.die(source);
    }

    private void trySpawnGhost() {
        if (!this.isTame() || this.random.nextInt(4) != 0) {
            return;
        }
        MoCWyvernEntity ghost = (MoCWyvernEntity) this.getType().create(this.level());
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
        ghost.playSound(ModSounds.WYVERN_GRUNT.get(), 1.0F, 1.0F);
        com.example.neomocreatures.util.NamingHelper.promptRename(ghost, this.getOwnerUUID());
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);
        dropSaddleAndArmor();
        dropCombatLoot(level, recentlyHitByPlayer);

        // Requested: an undead wyvern spawns maggots on death, same as the tamed undead horse.
        if (this.getVariant() == com.example.neomocreatures.entity.wyvern.WyvernVariant.MOTHER_UNDEAD) {
            spawnMaggotsOnDeath(level);
        }
    }

    /** Spawns 1-3 maggots at the death location. */
    private void spawnMaggotsOnDeath(ServerLevel level) {
        int count = 1 + this.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            com.example.neomocreatures.entity.MoCMaggotEntity maggot =
                    com.example.neomocreatures.init.ModEntities.MOC_MAGGOT.get().create(level);
            if (maggot != null) {
                maggot.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                level.addFreshEntity(maggot);
            }
        }
    }

    /** Saddle and armor always drop if equipped, regardless of what killed the wyvern. Never affected by Looting. */
    public void dropSaddleAndArmor() {
        if (this.isSaddled()) {
            net.minecraft.world.item.Item saddleItem = this.saddleItemId != null
                    ? net.minecraft.core.registries.BuiltInRegistries.ITEM.get(this.saddleItemId)
                    : net.minecraft.world.item.Items.SADDLE;
            this.spawnAtLocation(new ItemStack(saddleItem));
        }
        if (this.getArmorTier() > 0) {
            net.minecraft.world.item.Item armorItem = switch (this.getArmorTier()) {
                case 1 -> net.minecraft.world.item.Items.IRON_HORSE_ARMOR;
                case 2 -> net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR;
                default -> net.minecraft.world.item.Items.DIAMOND_HORSE_ARMOR;
            };
            this.spawnAtLocation(new ItemStack(armorItem));
        }
    }

    /** Mouth-open animation + horse's drinking sound, shared by all four essences. */
    private void useEssence(Player player, ItemStack stack) {
        startMouthAnimation();
        if (!this.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.playSound(ModSounds.HORSE_DRINKING.get(), 1.0F, 1.0F);
            if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
            }
        }
    }

    /** Snapshot used to restore this wyvern later from a filled Pet Amulet. */
    private CompoundTag buildAmuletTag(java.util.UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("WyvernVariant", getVariant().name());
        tag.putString("WyvernTier", getTier().name());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Pet Amulet capture: instant, no vanish animation. Saddle/armor/chest drop on the ground, not saved. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropSaddleAndArmor();
        dropChestAndContents();
        CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    /**
     * Experience and an egg of its own species, only if it was killed by a
     * player or a tamed wolf. Looting only raises the egg chance.
     */
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

        float eggChance = 0.10F + lootingLevel * 0.03F;
        if (this.random.nextFloat() < eggChance) {
            this.spawnAtLocation(new ItemStack(eggItemFor(getVariant())));
        }
    }

    /** Each variant only drops its own egg; every mother form shares the mother egg. */
    private static net.minecraft.world.item.Item eggItemFor(WyvernVariant variant) {
        return switch (variant) {
            case JUNGLE -> com.example.neomocreatures.init.ModItems.JUNGLE_WYVERN_EGG.get();
            case SWAMP -> com.example.neomocreatures.init.ModItems.SWAMP_WYVERN_EGG.get();
            case SAND -> com.example.neomocreatures.init.ModItems.SAND_WYVERN_EGG.get();
            case SUN -> com.example.neomocreatures.init.ModItems.SUN_WYVERN_EGG.get();
            case ARCTIC -> com.example.neomocreatures.init.ModItems.ARCTIC_WYVERN_EGG.get();
            case CAVE -> com.example.neomocreatures.init.ModItems.CAVE_WYVERN_EGG.get();
            case MOUNTAIN -> com.example.neomocreatures.init.ModItems.MOUNTAIN_WYVERN_EGG.get();
            case SEA -> com.example.neomocreatures.init.ModItems.SEA_WYVERN_EGG.get();
            case MOTHER, MOTHER_UNDEAD, MOTHER_LIGHT, MOTHER_DARK, MOTHER_CORRUPT ->
                    com.example.neomocreatures.init.ModItems.MOTHER_WYVERN_EGG.get();
        };
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled() && this.getFirstPassenger() instanceof Player player && this.hasPassenger(player)) {
            return player;
        }
        return null;
    }

    public boolean canBeControlledByRider() {
        return this.isSaddled() && this.getControllingPassenger() instanceof Player;
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!this.level().isClientSide && passenger instanceof Player && getIsFlying() && !this.onGround()) {
            setIsFlying(false);
        }
    }

    public void setAscendHeld(boolean held) {
        this.entityData.set(DATA_ASCEND_HELD, held);
    }

    public void setDescendHeld(boolean held) {
        this.entityData.set(DATA_DESCEND_HELD, held);
    }

    private boolean isAscendHeld() {
        return this.entityData.get(DATA_ASCEND_HELD);
    }

    private boolean isDescendHeld() {
        return this.entityData.get(DATA_DESCEND_HELD);
    }

    private static final float RIDER_FORWARD = 0.2F;
    private static final float RIDER_HEIGHT = 1.1F;
    private static final float MOTHER_TAMED_RIDER_HEIGHT_BONUS = 0.6F;
    private static final float MOTHER_TAMED_RIDER_FORWARD_BONUS = 0.3F;

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float forward = RIDER_FORWARD
                + (getTier() == WyvernTier.MOTHER_TAMED ? MOTHER_TAMED_RIDER_FORWARD_BONUS : 0.0F);
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() - Math.sin(yaw) * forward;
        double z = this.getZ() + Math.cos(yaw) * forward;
        double y = this.getY() + RIDER_HEIGHT * getVisualScale()
                + (getTier() == WyvernTier.MOTHER_TAMED ? MOTHER_TAMED_RIDER_HEIGHT_BONUS : 0.0F);
        moveFunction.accept(passenger, x, y, z);
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
            setIsFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, RIDDEN_ASCEND_THRUST, 0.0D));
        } else if (isDescendHeld()) {
            setIsFlying(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -RIDDEN_DESCEND_THRUST, 0.0D));
        } else if (this.onGround()) {
            setIsFlying(false);
        } else {
            setIsFlying(true);
        }
        setDiving(isDescendHeld() && isOnAir());

        if (!this.level().isClientSide && isAscendHeld() && isAirborneFlapping()) {
            wingFlap();
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        double vertical = isAscendHeld() ? 1.0D : (isDescendHeld() ? -1.0D : 0.0D);
        return new Vec3(player.xxa, vertical, player.zza);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return getIsFlying()
                ? (float) this.getAttributeValue(Attributes.FLYING_SPEED)
                : (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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

    private static final float RIDDEN_FLYER_FRICTION = 0.93F;
    private static final double RIDDEN_ASCEND_THRUST = 0.15D;
    private static final double RIDDEN_DESCEND_THRUST = 0.3D;
    private static final double RIDDEN_FLYER_FALL_SPEED = 0.6D;
    private static final double RIDDEN_FLYER_GRAVITY_PULL = 0.02D;

    // ---------------------------------------------------------------------
    // Free flight (not ridden) — ghast-style gliding, parrot-style takeoffs and landings
    // ---------------------------------------------------------------------

    /** Ghast: a push of 0.1 towards its destination every 2-6 ticks, with 0.91 air drag — smooth, floaty flight. */
    private static final double FLIGHT_IMPULSE = 0.1D;
    private static final double AIR_DRAG = 0.91D;
    private static final int MIN_IMPULSE_INTERVAL = 2;
    private static final int IMPULSE_INTERVAL_RANGE = 5;
    /** How close (squared) it has to get to a destination before picking a new one. */
    private static final double ARRIVED_DISTANCE_SQR = 1.0D;
    /** Ghast: gives up on a destination more than 60 blocks away (squared). */
    private static final double LOST_DISTANCE_SQR = 3600.0D;
    /** Ghast: new destinations are picked up to 16 blocks away horizontally. */
    private static final double SOAR_RANGE = 16.0D;
    /** Cruising height above the ground while soaring: 4 to 13 blocks. */
    private static final int MIN_SOAR_ALTITUDE = 4;
    private static final int SOAR_ALTITUDE_RANGE = 10;
    /** Parrot-like: on the ground it takes off now and then (about every 30 s on average). */
    private static final int TAKE_OFF_CHANCE = 600;
    private static final double TAKE_OFF_BOOST = 0.4D;
    /** Each flight lasts 20-50 s before it looks for a place to land. */
    private static final int MIN_FLIGHT_TICKS = 400;
    private static final int FLIGHT_TICKS_RANGE = 600;
    /** Landing spots are searched within 8 blocks, retried every 5 s; it touches down within 1.5 blocks of the ground. */
    private static final int LANDING_SEARCH_RANGE = 8;
    private static final int LANDING_RETRY_TICKS = 100;
    private static final double TOUCHDOWN_HEIGHT = 1.5D;
    private static final float TURN_SPEED = 10.0F;

    /** Server-side: ticks left in the current free flight before it looks for somewhere to land. */
    private int flightTicks;

    /** Flying on its own: not ridden, not ordered to sit, and not chasing anything. */
    private boolean canFlyFreely() {
        return this.getIsFlying() && !this.isVehicle() && !this.isOrderedToSit() && this.getTarget() == null;
    }

    private boolean wantsToLand() {
        return this.flightTicks <= 0;
    }

    /** Nowhere to land (water, lava): keep soaring a little longer before trying again. */
    private void extendFlight() {
        this.flightTicks = LANDING_RETRY_TICKS;
    }

    /** Parrot-like takeoff: a hop into the air, then it glides like a ghast. */
    private void takeOff() {
        this.setIsFlying(true);
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, TAKE_OFF_BOOST, 0.0D));
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player && getIsFlying()) {
            this.setNoGravity(true);
            this.moveRelative(RIDDEN_FLYER_FRICTION / 10F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            Vec3 delta = this.getDeltaMovement()
                    .multiply(RIDDEN_FLYER_FRICTION, RIDDEN_FLYER_FALL_SPEED, RIDDEN_FLYER_FRICTION)
                    .subtract(0.0D, RIDDEN_FLYER_GRAVITY_PULL, 0.0D);
            // Even with descend/Z held, never push it below the water
            // surface once it's touching water — floats instead of sinking.
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
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));
            } else {
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(AIR_DRAG));
            }
            this.fallDistance = 0.0F;
        } else {
            applyWaterBuoyancy();
            applyLavaBuoyancy();
            super.travel(travelVector);
        }
    }

    @Override
    public void aiStep() {
        tickWingFlap();
        tickGrowth();
        tickEssenceTransform();

        if (!this.level().isClientSide) {
            if (!getIsFlying() && !this.isSittingSynced() && isOnAir() && this.getDeltaMovement().y < 0.0D) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
            }

            if (!this.isTame() && this.level().dimension() == ModDimensions.WYVERN_LAIR
                    && this.getY() < LAIR_DESPAWN_Y) {
                this.discard();
                return;
            }

            // Parrot-like: mostly on the ground, taking off now and then; landing is handled by WyvernLandGoal.
            if (!this.isOrderedToSit() && !this.isTame() && !getIsFlying() && this.getTarget() == null
                    && this.onGround() && this.random.nextInt(TAKE_OFF_CHANCE) == 0) {
                this.takeOff();
            }
            if (this.canFlyFreely() && this.flightTicks > 0) {
                this.flightTicks--;
            }

            if (this.isOrderedToSit() && getIsFlying()) {
                setIsFlying(false);
            }

            boolean solidGroundBelow = this.level().getBlockState(this.blockPosition().below()).canOcclude();
            if (this.getTarget() != null && !this.isOrderedToSit() && !this.isVehicle()) {
                if (!solidGroundBelow && this.random.nextInt(20) == 0) {
                    // No solid ground to fight from here — take off instead.
                    setIsFlying(true);
                    if (this.onGround()) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                    }
                } else if (getIsFlying() && solidGroundBelow && this.random.nextInt(20) == 0) {
                    // Ground is available — land to keep fighting from there instead of flying.
                    setIsFlying(false);
                }
            }

            if (getIsFlying() && !this.isVehicle() && isAirborneFlapping()) {
                wingFlap();
            }
        }

        super.aiStep();
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        int growthTicks = getTier() == WyvernTier.TIER_1 ? TIER_1_GROWTH_TICKS : SLOW_GROWTH_TICKS;
        float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
        float babyFraction = BABY_SCALE / getTier().getRenderScale();
        return Mth.lerp(progress, babyFraction, 1.0F);
    }

    private float lastAppliedScale = -1F;

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

    public float getVisualScale() {
        return getGrowthFraction() * getTier().getRenderScale();
    }

    private void tickWingFlap() {
        if (this.level().isClientSide) {
            return;
        }

        int flapCounter = this.entityData.get(DATA_WING_FLAP_TICKS);
        if (flapCounter > 0 && ++flapCounter > WING_FLAP_BURST_TICKS) {
            flapCounter = 0;
        }
        this.entityData.set(DATA_WING_FLAP_TICKS, flapCounter);
        if (flapCounter == 5) {
            this.playSound(ModSounds.WYVERN_WING_FLAP.get(), 0.4F, 1.0F);
        }

        int mouthCounter = this.entityData.get(DATA_BITE_TICKS);
        if (mouthCounter > 0 && ++mouthCounter > MOUTH_BURST_TICKS) {
            mouthCounter = 0;
        }
        this.entityData.set(DATA_BITE_TICKS, mouthCounter);
    }

    /** Countdown for essence transformations: sound partway through, variant swap at the end. */
    private void tickEssenceTransform() {
        if (this.level().isClientSide || !isTransforming()) {
            return;
        }
        int ticks = getTransformTicks() - 1;
        this.entityData.set(DATA_TRANSFORM_TICKS, ticks);
        if (ticks == TRANSFORM_SOUND_TICKS) {
            this.playSound(ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
        }
        if (ticks <= 0) {
            setVariant(getTransformTarget());
        }
    }

    public void wingFlap() {
        if (this.entityData.get(DATA_WING_FLAP_TICKS) == 0) {
            this.entityData.set(DATA_WING_FLAP_TICKS, 1);
        }
    }

    public boolean isDiving() {
        return this.entityData.get(DATA_DIVING);
    }

    private void setDiving(boolean diving) {
        if (this.entityData.get(DATA_DIVING) != diving) {
            this.entityData.set(DATA_DIVING, diving);
        }
    }

    @Override
    public void jumpFromGround() {
        if (getIsFlying()) {
            wingFlap();
        }
        super.jumpFromGround();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}