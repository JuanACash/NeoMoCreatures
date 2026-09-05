package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.entity.wyvern.WyvernTier;
import com.example.neomocreatures.entity.wyvern.WyvernVariant;
import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.init.ModEntities;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class MoCWyvernEntity extends TamableAnimal implements EggHatchable {

    /** How close (in blocks) a player has to be before a wild wyvern goes hostile. Wiki: 12-16. */
    private static final double AGGRO_RADIUS = 14.0D;
    /** Wiki: 10 seconds of Poison on a successful hit. */
    private static final int POISON_DURATION_TICKS = 200;
    /** Wiki: instantly removed if it drifts below Y=10 inside the Wyvern Lair. */
    private static final int LAIR_DESPAWN_Y = 10;
    /** Original's wingFlapCounter: runs 1→20 then resets to 0 while a flap burst is active. */
    private static final int WING_FLAP_BURST_TICKS = 20;
    /** Original's mouthCounter: runs 1→30 then resets to 0 while the bite/mouth animation plays. */
    private static final int MOUTH_BURST_TICKS = 30;
    /** Every hatched baby starts at this same absolute size, whatever tier it'll grow into. */
    private static final float BABY_SCALE = 0.4F;
    /** Wiki: tier 2 and mother take longer to grow than a common wyvern. */
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
    private static final EntityDataAccessor<Boolean> DATA_DIVING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    // Synced so BOTH the server AND the riding player's own client know
    // whether ascend/descend are held — the client needs this because it's
    // the rider's own client that simulates the mount's movement locally
    // (same as vanilla horses), not just the server.
    private static final EntityDataAccessor<Boolean> DATA_ASCEND_HELD =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DESCEND_HELD =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    // TamableAnimal#isOrderedToSit() was not reliably reaching the client in
    // testing (server confirmed true, client-side model/renderer checks
    // never saw it) — our own synced flag, same proven pattern as
    // DATA_FLYING, so rendering can trust it regardless of whatever's going
    // on with the vanilla one.
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);

    public MoCWyvernEntity(EntityType<? extends MoCWyvernEntity> type, Level level) {
        super(type, level);
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
        setVariant(isMotherTier ? WyvernVariant.MOTHER : WyvernVariant.randomWild(this.random));
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

    // Wiki stats: 40 HP / 3 attack (tier 1) up to 80 HP / 17 attack (tier 2 and mother).
    // Attributes.SCALE is ALWAYS 1.0 for a grown adult of any tier — the tier
    // size difference already lives entirely in each EntityType's own
    // .sized() hitbox. SCALE only ever drops below 1.0 temporarily, while a
    // hatched baby is still growing — see tickGrowth().
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public static AttributeSupplier.Builder createTier2Attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FLYING_SPEED, 0.14D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public static AttributeSupplier.Builder createMotherAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FLYING_SPEED, 0.13D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public static AttributeSupplier.Builder createMotherTamedAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FLYING_SPEED, 0.13D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public WyvernVariant getVariant() {
        return WyvernVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(WyvernVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
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

    /** Client-safe: use this (not isOrderedToSit()) for anything rendering-related. */
    public boolean isSittingSynced() {
        return this.entityData.get(DATA_SITTING);
    }

    /** Sets BOTH the vanilla AI flag (server-side goal behavior) and our own synced one (rendering). */
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
        builder.define(DATA_DIVING, false);
        builder.define(DATA_ASCEND_HELD, false);
        builder.define(DATA_DESCEND_HELD, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("WyvernVariant", getVariant().name());
        tag.putString("WyvernTier", getTier().name());
        tag.putBoolean("WyvernFlying", getIsFlying());
        tag.putBoolean("WyvernSittingSynced", isSittingSynced());
        tag.putBoolean("WyvernSaddled", isSaddled());
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
        if (tag.contains("WyvernSaddled")) {
            setSaddled(tag.getBoolean("WyvernSaddled"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new WyvernMeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WyvernFlyGoal(this, 1.3D));
        this.goalSelector.addGoal(6, new WyvernGroundWanderGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, (int) AGGRO_RADIUS,
                true, false, target -> !this.isTame() && !this.isVehicle()));
    }

    private static class WyvernFlyGoal extends WaterAvoidingRandomFlyingGoal {
        private final MoCWyvernEntity wyvern;

        WyvernFlyGoal(MoCWyvernEntity wyvern, double speedModifier) {
            super(wyvern, speedModifier);
            this.wyvern = wyvern;
        }

        @Override
        public boolean canUse() {
            return !this.wyvern.isVehicle() && this.wyvern.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.wyvern.isVehicle() && this.wyvern.getIsFlying() && super.canContinueToUse();
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

    // Same as the two goals above: a ridden wyvern must not have vanilla AI
    // fighting the rider's own input for control of movement/targeting.
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
            if (isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    this.heal(4.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.isBaby() && !this.isSaddled()
                    && (stack.is(net.minecraft.world.item.Items.SADDLE)
                        || stack.is(com.example.neomocreatures.init.ModItems.HORSE_SADDLE.get()))) {
                if (!this.level().isClientSide) {
                    this.setSaddled(true);
                    this.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
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
            // Dismounted mid-air — glide down via the damped-fall logic in
            // aiStep() instead of holding altitude like wild-wyvern flight AI.
            setIsFlying(false);
        }
    }

    // Synced so the value is visible on whichever client needs it (the
    // rider's own client simulates this mount's movement locally, just like
    // a vanilla horse) — see DATA_ASCEND_HELD / DATA_DESCEND_HELD above.
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

    /** Roughly on its back, just behind the shoulders/neck — tune these two if the seat looks off. */
    private static final float RIDER_FORWARD = 0.2F;
    private static final float RIDER_HEIGHT = 1.1F;
    /** The linear getVisualScale() multiplier alone sits the player too low specifically on mother-tamed's much bigger back — tune this extra bump if still off. */
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

    /**
     * Called automatically by LivingEntity#travel() every tick there is a
     * controlling Player passenger — this is what lets us steer the wyvern's
     * facing from the rider's own look direction, same pattern as Camel /
     * AbstractHorse in vanilla.
     */
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
            // Nothing held while airborne — stay in "flying" mode so travel()'s
            // damped descent applies, instead of switching to vanilla gravity
            // mid-air (which is what was causing the fast drop).
            setIsFlying(true);
        }
        // Only "diving" while it's actually a dive — held AND airborne —
        // not a lingering animation state that outlives either condition.
        setDiving(isDescendHeld() && isOnAir());
    }

    /**
     * Called automatically by LivingEntity#travel() to build the input
     * vector used for ground movement — x/z come straight from the rider's
     * own raw input (player.xxa/zza), same as AbstractHorse reads it.
     */
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

    /**
     * Wiki-parity with horses: floats instead of sinking while standing/
     * swimming in water (not while actively flying, which has its own
     * water-slowdown branch already).
     */
    private void applyWaterBuoyancy() {
        if (this.isInWater() && !getIsFlying()) {
            double submergedFraction = this.getFluidHeight(net.minecraft.tags.FluidTags.WATER);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
        }
    }

    // Same flight feel as the horse's fairy_horse/pegasus flight — only the
    // in-air speed, not ground walk speed (that stays the wyvern's own
    // MOVEMENT_SPEED attribute, applied normally by super.travel() below).
    private static final float RIDDEN_FLYER_FRICTION = 0.93F;
    private static final double RIDDEN_ASCEND_THRUST = 0.15D;
    private static final double RIDDEN_DESCEND_THRUST = 0.3D;
    private static final double RIDDEN_FLYER_FALL_SPEED = 0.6D;
    private static final double RIDDEN_FLYER_GRAVITY_PULL = 0.02D;

    private float flyerFriction() {
        return 0.94F;
    }

    @Override
    public void travel(Vec3 travelVector) {
        // Ridden + flying needs its own no-gravity physics — the vanilla
        // ridden-travel pipeline (fed by getRiddenInput/getRiddenSpeed
        // above) only knows how to do normal grounded movement, so we take
        // over here instead, but still run on whichever side the engine
        // calls travel() on (client for the rider, server for homologation)
        // — no manual isClientSide branching needed, same as vanilla horses.
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player && getIsFlying()) {
            this.setNoGravity(true);
            this.moveRelative(RIDDEN_FLYER_FRICTION / 10F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement()
                    .multiply(RIDDEN_FLYER_FRICTION, RIDDEN_FLYER_FALL_SPEED, RIDDEN_FLYER_FRICTION)
                    .subtract(0.0D, RIDDEN_FLYER_GRAVITY_PULL, 0.0D));
            this.fallDistance = 0.0F;
            return;
        }

        if (getIsFlying() && !this.isPassenger() && !this.isVehicle()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(flyerFriction()));
            }
            this.fallDistance = 0.0F;
        } else if (!this.isVehicle()) {
            applyWaterBuoyancy();
            super.travel(travelVector);
        } else {
            // Ridden + grounded: let the vanilla ridden-travel pipeline
            // (getRiddenInput/getRiddenSpeed/tickRidden above) drive this,
            // same as AbstractHorse — just make sure buoyancy still applies.
            applyWaterBuoyancy();
            super.travel(travelVector);
        }
    }

    @Override
    public void aiStep() {
        tickWingFlap();
        tickGrowth();

        if (!this.level().isClientSide) {
            // Only glide-dampen a natural fall — while sitting, let gravity
            // apply normally so it actually settles onto the ground instead
            // of hovering in a near-permanent slow-motion glide.
            if (!getIsFlying() && !this.isSittingSynced() && isOnAir() && this.getDeltaMovement().y < 0.0D) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
            }

            if (!this.isTame() && this.level().dimension() == ModDimensions.WYVERN_LAIR
                    && this.getY() < LAIR_DESPAWN_Y) {
                this.discard();
                return;
            }

            if (this.getTarget() != null && !this.isVehicle() && this.getHealth() < this.getMaxHealth() / 2.0F) {
                this.setTarget(null);
                setIsFlying(true);
            }

            if (!this.isOrderedToSit() && !this.isTame()) {
                if (!getIsFlying() && this.random.nextInt(100) == 0) {
                    setIsFlying(true);
                    if (this.onGround()) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                    }
                } else if (getIsFlying() && this.random.nextInt(150) == 0) {
                    setIsFlying(false);
                }
            }

            if (this.isOrderedToSit() && getIsFlying()) {
                setIsFlying(false);
            }

            if (this.getTarget() != null && !this.isOrderedToSit() && !this.isVehicle() && this.random.nextInt(20) == 0) {
                setIsFlying(true);
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                }
            }

            if (getIsFlying() && !this.isVehicle()) {
                Vec3 motion = this.getDeltaMovement();
                double newY;
                LivingEntity attackTarget = this.getTarget();

                if (attackTarget != null) {
                    double heightDiff = attackTarget.getY() - this.getY();
                    if (heightDiff < -1.0D) {
                        newY = Math.max(motion.y - 0.08D, -0.6D);
                    } else if (heightDiff > 1.0D) {
                        newY = Math.min(motion.y + 0.06D, 0.5D);
                    } else {
                        newY = motion.y * 0.8D;
                    }
                } else {
                    int groundY = this.level().getHeight(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            this.getBlockX(), this.getBlockZ());
                    double heightAboveGround = this.getY() - groundY;

                    if (heightAboveGround < 10.0D) {
                        newY = Math.min(motion.y + 0.06D, 0.5D);
                    } else if (heightAboveGround > 48.0D) {
                        newY = Math.max(motion.y - 0.05D, -0.5D);
                    } else {
                        newY = motion.y * 0.8D;
                        if (Math.abs(newY) < 0.01D) {
                            newY = 0.0D;
                        }
                    }
                }
                this.setDeltaMovement(motion.x, newY, motion.z);

                if (this.horizontalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().add(
                            this.random.nextGaussian() * 0.05D, 0.0D, this.random.nextGaussian() * 0.05D));
                }

                if (isAirborneFlapping()) {
                    wingFlap();
                }

                if (this.getNavigation().isDone() && this.getTarget() == null) {
                    if (this.random.nextInt(40) == 0) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D,
                                0.3D + (this.random.nextDouble() * 0.3D), 0.0D));
                    }
                }
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