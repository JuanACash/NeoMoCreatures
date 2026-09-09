package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.entity.scorpion.ScorpionVariant;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class MoCScorpionEntity extends TamableAnimal implements EggHatchable, net.minecraft.world.entity.PlayerRideableJumping {

    private static final int STING_CHANCE = 5; // 1 in 5, matches rand.nextInt(5)==0
    private static final int STING_ANIM_TICKS = 50;
    private static final int CLAW_SWING_TICKS = 24;
    private static final int MOUTH_TALK_TICKS = 50;
    private static final int MAX_BABIES_DROPPED = 5; // rand.nextInt(5) -> 0-4 babies, matches the original
    private static final float HEAL_AMOUNT = 4.0F;
    private static final int TRANSFORM_DURATION_TICKS = 100; // 5s, matches the wyvern/big cat essence pacing
    private static final int TRANSFORM_SOUND_TICKS = 60;
    private static final float BABY_SCALE = 0.3F;
    private static final float BABY_HITBOX_SCALE = 0.4F;
    private static final float RIDER_HEIGHT = 0F;
    private static final float RIDER_FORWARD = -0.1F;
    // v^2-proportional to jump height, same relation as the big cat's 0.62F -> 2.7 blocks; ~1.6 blocks target per the wiki.
    private static final float JUMP_VELOCITY = 0.48F;
    private static final float SIDEWAYS_RIDDEN_FACTOR = 0.3F; // wiki: "very slow when moving sideways"
    private float lastAppliedScale = -1F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_BABIES =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_MOUTH_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CLAW_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STING_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TRANSFORM_TICKS =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<java.util.Optional<java.util.UUID>> DATA_HELD_BY =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(MoCScorpionEntity.class, EntityDataSerializers.BOOLEAN);

    public MoCScorpionEntity(EntityType<? extends MoCScorpionEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(6, new LeapAtTargetGoal(this, 0.4F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ScorpionDarknessTargetGoal<>(this, Player.class));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    private static int getRawLight(MoCScorpionEntity scorpion) {
        return scorpion.level().getMaxLocalRawBrightness(scorpion.blockPosition());
    }

    private static class ScorpionDarknessTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final MoCScorpionEntity scorpion;

        ScorpionDarknessTargetGoal(MoCScorpionEntity scorpion, Class<T> targetType) {
            super(scorpion, targetType, true);
            this.scorpion = scorpion;
        }

        @Override
        public boolean canUse() {
            return !this.scorpion.isTame() && getRawLight(this.scorpion) <= 9 && super.canUse();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 18.0D) // wild HP — bumped to 40 once tamed (taming step)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming is the pick-up-baby mechanic, not feeding (later step)
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(com.example.neomocreatures.init.ModItems.RAT_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.RAT_COOKED.get());
    }

    public boolean isTransforming() {
        return this.entityData.get(DATA_TRANSFORM_TICKS) > 0;
    }

    public int getTransformTicks() {
        return this.entityData.get(DATA_TRANSFORM_TICKS);
    }

    private void startUndeadTransform() {
        this.entityData.set(DATA_TRANSFORM_TICKS, TRANSFORM_DURATION_TICKS);
    }

    /** Counts down an in-progress Undead transform; plays the sound partway through, then flips the variant. */
    private void tickEssenceTransform() {
        if (this.level().isClientSide || !isTransforming()) {
            return;
        }
        int ticks = this.entityData.get(DATA_TRANSFORM_TICKS) - 1;
        this.entityData.set(DATA_TRANSFORM_TICKS, ticks);
        if (ticks == TRANSFORM_SOUND_TICKS) {
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_TRANSFORM.get(), 1.0F, 1.0F);
        }
        if (ticks <= 0) {
            setVariant(ScorpionVariant.UNDEAD);
        }
    }

    /** Drinking sound + item consumption shared by every essence use, same pattern as the wyvern/big cat. */
    private void useEssence(Player player, ItemStack stack) {
        if (!this.level().isClientSide) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_DRINKING.get(), 1.0F, 1.0F);
            if (!player.getInventory().add(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE))) {
                player.drop(new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE), false);
            }
        }
    }

    /** Each color only ever lays its own egg. */
    private static Item eggItemFor(ScorpionVariant variant) {
        return switch (variant) {
            case DIRT -> com.example.neomocreatures.init.ModItems.DIRT_SCORPION_EGG.get();
            case CAVE -> com.example.neomocreatures.init.ModItems.CAVE_SCORPION_EGG.get();
            case FROST -> com.example.neomocreatures.init.ModItems.FROST_SCORPION_EGG.get();
            case NETHER -> com.example.neomocreatures.init.ModItems.FIRE_SCORPION_EGG.get();
            case UNDEAD -> com.example.neomocreatures.init.ModItems.UNDEAD_SCORPION_EGG.get();
        };
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = this.isBaby() ? BABY_SCALE : 1.0F;
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

    @Nullable
    private Player heldBy;

    public boolean isHeld() {
        return this.entityData.get(DATA_HELD_BY).isPresent();
    }

    /** Client-side use: lets the renderer track the holder's live, interpolated position instead of the network-lagged one. */
    @Nullable
    public Player getHolder() {
        return this.entityData.get(DATA_HELD_BY).map(uuid -> this.level().getPlayerByUUID(uuid)).orElse(null);
    }

    private void startHolding(Player player) {
        this.heldBy = player;
        this.entityData.set(DATA_HELD_BY, java.util.Optional.of(player.getUUID()));
        this.setNoAi(true);
        this.setNoGravity(true);
        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
    }

    private void stopHolding() {
        this.entityData.set(DATA_HELD_BY, java.util.Optional.empty());
        this.setNoAi(false);
        this.setNoGravity(false);
        this.heldBy = null;
    }

    private void tickHeld() {
        if (!isHeld()) {
            return;
        }
        if (this.heldBy == null || this.heldBy.isRemoved() || this.heldBy.level() != this.level()) {
            stopHolding();
            return;
        }
        net.minecraft.world.phys.Vec3 look = this.heldBy.getLookAngle();
        net.minecraft.world.phys.Vec3 handPos = this.heldBy.getEyePosition()
                .add(look.scale(0.6D))
                .add(0.0D, -0.35D, 0.0D);
        this.moveTo(handPos.x, handPos.y, handPos.z, this.getYRot(), 0.0F);
        this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
    }

    public ScorpionVariant getVariant() {
        return ScorpionVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(ScorpionVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public boolean fireImmune() {
        return getVariant().isFireImmune() || super.fireImmune();
    }

    /** Same trick vanilla's own Spider uses: touching a wall horizontally counts as "on a climbable". */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
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

    /** Wiki: same speed forward/backward, but very slow moving sideways. */
    @Override
    protected net.minecraft.world.phys.Vec3 getRiddenInput(Player player, net.minecraft.world.phys.Vec3 travelVector) {
        return new net.minecraft.world.phys.Vec3(player.xxa * SIDEWAYS_RIDDEN_FACTOR, 0.0D, player.zza);
    }

    /** Wiki: riding doesn't change its speed — no multiplier here, unlike the big cat. */
    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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
    }

    @Override
    public boolean canJump() {
        return isSaddled() && this.isVehicle();
    }

    /** Fixed jump, no charge bar — same one-shot approach as the elephant's. Works from water/lava too. */
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

    public boolean hasBabies() {
        return this.entityData.get(DATA_HAS_BABIES);
    }

    private void setHasBabies(boolean flag) {
        this.entityData.set(DATA_HAS_BABIES, flag);
    }

    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    private void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    /** Public entry point for spawn eggs / other external code — internal logic stays on setHasBabies(). */
    public void setHasBabiesPublic(boolean flag) {
        setHasBabies(flag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Whip toggles sitting — only while tamed and not currently being
        // ridden, exactly like the original MoCEntityPetScorpion.
        if (this.isTame() && stack.is(com.example.neomocreatures.init.ModItems.WHIP.get()) && !this.isVehicle()) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
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

        if (this.isTame() && this.isOwnedBy(player)) {
            if (stack.is(net.minecraft.world.item.Items.BOOK)) {
                if (!this.level().isClientSide) {
                    com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_UNDEAD.get())
                    && getVariant() != ScorpionVariant.UNDEAD && !isTransforming()) {
                if (!this.level().isClientSide) {
                    startUndeadTransform();
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_UNDEAD.get())
                    && getVariant() == ScorpionVariant.UNDEAD) {
                if (!this.level().isClientSide) {
                    this.spawnAtLocation(new ItemStack(eggItemFor(ScorpionVariant.UNDEAD)));
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (stack.is(com.example.neomocreatures.init.ModItems.ESSENCE_OF_DARKNESS.get())
                    && getVariant() != ScorpionVariant.UNDEAD) {
                if (!this.level().isClientSide) {
                    this.spawnAtLocation(new ItemStack(eggItemFor(getVariant())));
                }
                useEssence(player, stack);
                return InteractionResult.SUCCESS;
            }
            if (isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
                startTalking();
                if (!this.level().isClientSide) {
                    this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                    this.heal(HEAL_AMOUNT);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.isBaby() && !isSaddled()
                    && (stack.is(net.minecraft.world.item.Items.SADDLE)
                        || stack.is(com.example.neomocreatures.init.ModItems.HORSE_SADDLE.get()))) {
                if (!this.level().isClientSide) {
                    setSaddled(true);
                    this.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (stack.is(net.minecraft.world.item.Items.SHEARS) && isSaddled()) {
                if (!this.level().isClientSide) {
                    setSaddled(false);
                    this.ejectPassengers();
                    this.spawnAtLocation(new ItemStack(net.minecraft.world.item.Items.SADDLE));
                    this.playSound(net.minecraft.sounds.SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Anyone can mount a saddled tame scorpion, same as a horse — not just the owner.
        if (isSaddled() && !this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isBaby()) {
            if (!this.level().isClientSide) {
                if (isHeld()) {
                    stopHolding();
                } else {
                    boolean wasTame = this.isTame();
                    startHolding(player);
                    if (!wasTame) {
                        this.tame(player);
                        com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ScorpionVariant.DIRT.getId());
        builder.define(DATA_HAS_BABIES, false);
        builder.define(DATA_MOUTH_TICKS, 0);
        builder.define(DATA_CLAW_TICKS, 0);
        builder.define(DATA_STING_TICKS, 0);
        builder.define(DATA_TRANSFORM_TICKS, 0);
        builder.define(DATA_HELD_BY, java.util.Optional.empty());
        builder.define(DATA_SADDLED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("ScorpionVariant", getVariant().name());
        tag.putBoolean("ScorpionBabies", hasBabies());
        tag.putBoolean("ScorpionSaddled", isSaddled());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ScorpionVariant", 8)) {
            try {
                setVariant(ScorpionVariant.valueOf(tag.getString("ScorpionVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("ScorpionBabies")) {
            setHasBabies(tag.getBoolean("ScorpionBabies"));
        }
        if (tag.contains("ScorpionSaddled")) {
            setSaddled(tag.getBoolean("ScorpionSaddled"));
        }
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new net.minecraft.world.entity.ai.navigation.WallClimberNavigation(this, level);
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

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType spawnReason,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        if (!this.isTame() && this.random.nextInt(4) == 0) {
            setHasBabies(true);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            trySpawnBabies();
        }
        super.die(source);
    }

    /** Wiki: killing an adult carrying babies knocks 0-4 of them loose, wild and pick-up-able. */
    private void trySpawnBabies() {
        if (this.isBaby() || !hasBabies()) {
            return;
        }
        int count = this.random.nextInt(MAX_BABIES_DROPPED);
        for (int i = 0; i < count; i++) {
            MoCScorpionEntity baby = (MoCScorpionEntity) this.getType().create(this.level());
            if (baby == null) {
                continue;
            }
            baby.moveTo(this.getX(), this.getY(), this.getZ(), this.random.nextFloat() * 360F, 0F);
            baby.setVariant(getVariant());
            baby.setBaby(true);
            this.level().addFreshEntity(baby);
            baby.playSound(net.minecraft.sounds.SoundEvents.SLIME_SQUISH, 1.0F, 1.0F);
        }
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(ScorpionVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.setBaby(true);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            com.example.neomocreatures.util.NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    public int getMouthTicks() {
        return this.entityData.get(DATA_MOUTH_TICKS);
    }

    private void startTalking() {
        if (this.entityData.get(DATA_MOUTH_TICKS) == 0) {
            this.entityData.set(DATA_MOUTH_TICKS, 1);
        }
    }

    public int getClawTicks() {
        return this.entityData.get(DATA_CLAW_TICKS);
    }

    private void swingClaw() {
        if (this.entityData.get(DATA_CLAW_TICKS) == 0) {
            this.entityData.set(DATA_CLAW_TICKS, 1);
        }
    }

    public int getStingTicks() {
        return this.entityData.get(DATA_STING_TICKS);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        startTalking();
        return com.example.neomocreatures.init.ModSounds.SCORPION_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.SCORPION_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.SCORPION_DEATH.get();
    }

    /**
     * ~20% chance per hit to sting instead of a plain claw swing — matches
     * rand.nextInt(5)==0. Nether's ignite is Player-only and only outside the
     * Nether; every other color's potion effect applies to any LivingEntity.
     */
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
            swingClaw();
        }
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickHeld();
            if (this.isNoAi() && !isHeld()) {
                this.setNoAi(false);
            }
            // Like any other hostile mob: never sticks around once the difficulty
            // drops to Peaceful (wild only — a tamed one stays with its owner).
            if (!this.isTame() && this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }

            tickIdleCounters();
            tickEssenceTransform();
        }
    }

    private void tickIdleCounters() {
        int mouth = this.entityData.get(DATA_MOUTH_TICKS);
        if (mouth > 0 && ++mouth > MOUTH_TALK_TICKS) {
            mouth = 0;
        }
        this.entityData.set(DATA_MOUTH_TICKS, mouth);

        int claw = this.entityData.get(DATA_CLAW_TICKS);
        if (claw > 0) {
            if (claw == 10 || claw == 20) {
                this.playSound(com.example.neomocreatures.init.ModSounds.SCORPION_CLAW.get(), 1.0F, 1.0F);
            }
            if (++claw > CLAW_SWING_TICKS) {
                claw = 0;
            }
        }
        this.entityData.set(DATA_CLAW_TICKS, claw);

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