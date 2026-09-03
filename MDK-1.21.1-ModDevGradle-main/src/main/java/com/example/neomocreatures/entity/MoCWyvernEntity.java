package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.wyvern.WyvernTier;
import com.example.neomocreatures.entity.wyvern.WyvernVariant;
import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MoCWyvernEntity extends TamableAnimal {

    /** How close (in blocks) a player has to be before a wild wyvern goes hostile. Wiki: 12-16. */
    private static final double AGGRO_RADIUS = 14.0D;
    /** Wiki: 10 seconds of Poison on a successful hit. */
    private static final int POISON_DURATION_TICKS = 200;
    /** Wiki: instantly removed if it drifts below Y=10 inside the Wyvern Lair. */
    private static final int LAIR_DESPAWN_Y = 10;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TIER =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    // Mirrors the original mod's FLYING dataManager flag: whether the wyvern is
    // currently airborne (gliding/hovering) instead of walking.
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);

    // Local (non-synced) counter that drives the wing-flap sound, same idea as
    // wingFlapCounter in the original MoCEntityWyvern.
    private int wingFlapCounter;

    public MoCWyvernEntity(EntityType<? extends MoCWyvernEntity> type, Level level) {
        super(type, level);
        WyvernTier tier;
        if (type == ModEntities.WYVERN_MOTHER.get()) {
            tier = WyvernTier.MOTHER;
        } else if (type == ModEntities.WYVERN_TIER2.get()) {
            tier = WyvernTier.TIER_2;
        } else {
            tier = WyvernTier.TIER_1;
        }
        setTier(tier);
        setVariant(tier == WyvernTier.MOTHER ? WyvernVariant.randomMother(this.random) : WyvernVariant.randomWild(this.random));
    }

    /**
     * Flying path navigation (like vanilla bees) so the wyvern paths through
     * open air instead of being treated as a ground walker. The manual
     * velocity handling in travel()/aiStep() below is unchanged — this only
     * affects how it finds its way to a target.
     */
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    // Wiki stats: 40 HP / 3 attack (tier 1) up to 80 HP / 17 attack (tier 2 and mother).
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static AttributeSupplier.Builder createTier2Attributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FLYING_SPEED, 0.14D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static AttributeSupplier.Builder createMotherAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FLYING_SPEED, 0.13D)
                .add(Attributes.ATTACK_DAMAGE, 17.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public WyvernVariant getVariant() {
        return WyvernVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(WyvernVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    /** Size/stat class: TIER_1 (wild), TIER_2 (bigger, same 8 textures) or MOTHER. */
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, WyvernVariant.SUN.getId());
        builder.define(DATA_TIER, WyvernTier.TIER_1.getId());
        builder.define(DATA_FLYING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("WyvernVariant", getVariant().name());
        tag.putString("WyvernTier", getTier().name());
        tag.putBoolean("WyvernFlying", getIsFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("WyvernVariant", 8)) {
            try {
                setVariant(WyvernVariant.valueOf(tag.getString("WyvernVariant")));
            } catch (IllegalArgumentException ignored) {
                // Unknown/legacy value in the save file — keep the default.
            }
        }
        if (tag.contains("WyvernTier", 8)) {
            try {
                setTier(WyvernTier.valueOf(tag.getString("WyvernTier")));
            } catch (IllegalArgumentException ignored) {
                // Unknown/legacy value in the save file — keep the default.
            }
        }
        if (tag.contains("WyvernFlying")) {
            setIsFlying(tag.getBoolean("WyvernFlying"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Only hunts players while it's still wild — same rule as
        // shouldAttackPlayers()/canAttackTarget() in the original entity.
        // Wiki aggro radius is 12-16 blocks; this still targets on Peaceful —
        // doHurtTarget() below is what actually withholds the hit damage there.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, (int) AGGRO_RADIUS,
                true, false, target -> !this.isTame()));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Naturally-spawned wyverns can't be tamed by feeding — in the original mod
        // the only way to get a tame wyvern is hatching a player-placed egg.
        // Revisit this once the egg item/entity exists; a hatched wyvern can just
        // call this.tame(player) directly instead of going through isFood/mobInteract.
        return false;
    }

    /**
     * Wild wyverns aren't tameable (see isFood() above). Once one is tamed
     * (hatched from an egg), sneak-right-click toggles sitting.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isTame() && this.isOwnedBy(player) && !this.level().isClientSide && player.isSecondaryUseActive()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.setTarget(null);
            this.getNavigation().stop();
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    /**
     * Wiki: bites deal damage plus a 10s Poison. On Peaceful the wyvern still
     * closes in and lands the venom (see the target selector above — it
     * isn't gated by difficulty), it just never deals the hit damage itself.
     */
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
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
        }
    }

    /** Wiki: wyverns never take fall damage — the wings always catch them. */
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (getIsFlying() && !this.isPassenger()) {
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
        } else {
            super.travel(travelVector);
        }
    }

    private float flyerFriction() {
        return 0.94F;
    }

    @Override
    public void aiStep() {
        if (this.wingFlapCounter > 0 && ++this.wingFlapCounter > 20) {
            this.wingFlapCounter = 0;
        }
        if (this.wingFlapCounter == 5 && !this.level().isClientSide) {
            // TODO: play the wyvern's wing-flap sound event here once it's registered.
        }

        if (!this.level().isClientSide) {
            // Wiki: instantly removed if it drifts below Y=10 in the Wyvern Lair.
            if (this.level().dimension() == ModDimensions.WYVERN_LAIR && this.getY() < LAIR_DESPAWN_Y) {
                this.discard();
                return;
            }

            // Wiki: breaks off and flies away once it drops below half health.
            if (this.getTarget() != null && this.getHealth() < this.getMaxHealth() / 2.0F) {
                this.setTarget(null);
                setIsFlying(true);
            }

            // Wild wyverns randomly take off / land, same as the original's
            // livingTick() random-chance toggle.
            if (!this.isOrderedToSit() && !this.isTame() && this.random.nextInt(300) == 0) {
                setIsFlying(!getIsFlying());
                if (getIsFlying() && this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                }
            }

            if (this.isOrderedToSit() && getIsFlying()) {
                setIsFlying(false);
            }

            if (this.getTarget() != null && !this.isOrderedToSit() && this.random.nextInt(20) == 0) {
                setIsFlying(true);
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                }
            }

            if (getIsFlying()) {
                // Gentle, capped descent so it doesn't float forever.
                Vec3 motion = this.getDeltaMovement();
                double newY = Math.max(motion.y - 0.03D, -0.5D);
                this.setDeltaMovement(motion.x, newY, motion.z);

                if (this.horizontalCollision) {
                    this.setDeltaMovement(this.getDeltaMovement().add(
                            this.random.nextGaussian() * 0.05D, 0.0D, this.random.nextGaussian() * 0.05D));
                }

                // Idle hover: lift back up occasionally instead of dropping like a rock
                // while there's nothing to path towards.
                if (this.getNavigation().isDone() && this.getTarget() == null) {
                    if (this.random.nextInt(40) == 0) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D,
                                0.3D + (this.random.nextDouble() * 0.3D), 0.0D));
                    }
                }

                if (this.random.nextInt(20) == 0) {
                    wingFlap();
                }
            }
        }

        super.aiStep();
    }

    public void wingFlap() {
        if (this.wingFlapCounter == 0) {
            this.wingFlapCounter = 1;
        }
    }

    @Override
    public void jumpFromGround() {
        wingFlap();
        super.jumpFromGround();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}