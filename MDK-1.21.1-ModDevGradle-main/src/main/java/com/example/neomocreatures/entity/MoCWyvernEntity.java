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
    // Mirrors the original mod's FLYING dataManager flag: whether the wyvern is
    // currently airborne (gliding/hovering) instead of walking.
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.BOOLEAN);
    // Synced so the client-side model can actually see these and animate —
    // the old non-synced wingFlapCounter never reached the renderer.
    private static final EntityDataAccessor<Integer> DATA_WING_FLAP_TICKS =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BITE_TICKS =
            SynchedEntityData.defineId(MoCWyvernEntity.class, EntityDataSerializers.INT);

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
        // Only the plain "wyvern_mother" texture can come from natural spawn
        // or the spawn egg — the undead/light/dark/corrupt mother textures
        // are reserved for a special, non-natural way of getting them later
        // (see WyvernVariant.randomMother(), currently unused for that reason).
        boolean isMotherTier = tier == WyvernTier.MOTHER || tier == WyvernTier.MOTHER_TAMED;
        setVariant(isMotherTier ? WyvernVariant.MOTHER : WyvernVariant.randomWild(this.random));
    }

    /**
     * Egg-hatched wyverns are always babies, tamed to whoever was standing
     * nearby when it hatched (and prompted to name it, same as horses).
     * Tier is already correct — the egg spawned this as the right EntityType
     * for whichever tier it rolled — so this only needs to fix up the
     * variant/texture and finish taming. variantId lets the egg preserve a
     * specific look (e.g. "JUNGLE" or "MOTHER"); null/unrecognized just
     * keeps whatever the constructor already picked.
     */
    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        if (variantId != null) {
            try {
                setVariant(WyvernVariant.valueOf(variantId));
            } catch (IllegalArgumentException ignored) {
                // Unrecognized variant name — keep the one already picked.
            }
        }
        // Wiki: mother/tier2 take longer (~2 MC days) to grow up than a
        // common wyvern (~1 MC day) — overrides the generic -24000 the egg
        // already set for every AgeableMob.
        int growthTicks = getTier() == WyvernTier.TIER_1 ? TIER_1_GROWTH_TICKS : SLOW_GROWTH_TICKS;
        this.setAge(-growthTicks);
        tickGrowth();

        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            this.setOrderedToSit(false);
            com.example.neomocreatures.util.NamingHelper.promptRename(this, tamer.getUUID());
        }
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
    // Attributes.SCALE is ALWAYS 1.0 for a grown adult of any tier — the tier
    // size difference already lives entirely in each EntityType's own
    // .sized() hitbox (1.45→1.8→2.2→4.2). Multiplying by a tier-specific
    // SCALE value on top of that double-applies the size difference (that's
    // what made mother/tier2 render/hitbox oversized). SCALE only ever drops
    // below 1.0 temporarily, while a hatched baby is still growing — see
    // tickGrowth().
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

    /** Only ever used by WYVERN_MOTHER_TAMED — same stats as the wild mother, just bigger. */
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

    /** Original's isOnAir(): physically airborne, regardless of the AI "flying" flag. */
    public boolean isOnAir() {
        return !this.onGround() && !this.isInWater() && !this.isInLava();
    }

    /**
     * Whether flight/glide animation and behaviour should apply at all right
     * now: physically not touching ground, and either genuinely airborne or
     * still carrying the "flying" AI flag from just before landing. Without
     * the onGround() check, isFlying could stay true for a moment after
     * touching down and the wings would keep moving while stood on the ground.
     */
    public boolean isAirborne() {
        return !this.onGround() && (isOnAir() || getIsFlying());
    }

    /**
     * Wings-out, no-flap glide pose: only while actually falling (meaningful
     * downward vertical speed). Any other time it's airborne — rising,
     * hovering, cruising — it should be actively flapping, not just holding
     * a fixed stretched pose.
     */
    public boolean isGliding() {
        return isAirborne() && this.getDeltaMovement().y < -0.03D;
    }

    public boolean isAirborneFlapping() {
        return isAirborne() && !isGliding();
    }

    /** Ticks left in the current wing-flap burst (1..20, 0 = idle glide pose). */
    public int getWingFlapTicks() {
        return this.entityData.get(DATA_WING_FLAP_TICKS);
    }

    /** Ticks left in the current mouth/bite animation (1..30, 0 = closed). */
    public int getBiteTicks() {
        return this.entityData.get(DATA_BITE_TICKS);
    }

    /** Original calls openMouth() from here too — not just on a successful bite. */
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isOrderedToSit()) {
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
        // Vanilla's WaterAvoidingRandomStrollGoal picks ground-level points
        // regardless of navigation type — that's what kept this stuck near
        // the ground despite FlyingPathNavigation. This picks 3D points at
        // varying height (like a parrot) and only runs while actually flying.
        this.goalSelector.addGoal(5, new WyvernFlyGoal(this, 1.3D));
        // Reinstated ground wander, gated to the opposite condition, so it
        // actually walks around (and animates its legs) while not flying —
        // without this it just stood still whenever grounded.
        this.goalSelector.addGoal(6, new WyvernGroundWanderGoal(this, 1.0D));
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

    /** WaterAvoidingRandomFlyingGoal gated to only wander while getIsFlying() is true. */
    private static class WyvernFlyGoal extends WaterAvoidingRandomFlyingGoal {
        private final MoCWyvernEntity wyvern;

        WyvernFlyGoal(MoCWyvernEntity wyvern, double speedModifier) {
            super(wyvern, speedModifier);
            this.wyvern = wyvern;
        }

        @Override
        public boolean canUse() {
            return this.wyvern.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.wyvern.getIsFlying() && super.canContinueToUse();
        }
    }

    /** WaterAvoidingRandomStrollGoal gated to only wander while NOT flying. */
    private static class WyvernGroundWanderGoal extends WaterAvoidingRandomStrollGoal {
        private final MoCWyvernEntity wyvern;

        WyvernGroundWanderGoal(MoCWyvernEntity wyvern, double speedModifier) {
            super(wyvern, speedModifier);
            this.wyvern = wyvern;
        }

        @Override
        public boolean canUse() {
            return !this.wyvern.getIsFlying() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.wyvern.getIsFlying() && super.canContinueToUse();
        }
    }

    /**
     * HurtByTargetGoal already retaliates against melee attackers, but a bow
     * shot from far away needs this to be immediate/reliable: as soon as it
     * takes damage from a player (arrow or otherwise), target them and take
     * off right away instead of waiting on the random per-tick flying rolls.
     * Wiki: a tamed wyvern never attacks its owner, and it may just fly off
     * (rather than fight back) if hurt by something that isn't a player —
     * e.g. skeleton arrows.
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);
        if (!wasHurt || this.level().isClientSide) {
            return wasHurt;
        }

        Entity attacker = source.getEntity();
        if (this.isTame() && attacker != null && attacker.equals(this.getOwner())) {
            // Friendly fire from the owner never turns into retaliation.
            this.setLastHurtByMob(null);
            this.setTarget(null);
            return wasHurt;
        }

        if (attacker instanceof Player player) {
            this.setTarget(player);
            setIsFlying(true);
        } else if (this.isTame()) {
            // Not a player (arrow from a skeleton, etc.) — a tame wyvern
            // just flees instead of fighting back.
            this.setTarget(null);
            setIsFlying(true);
        }
        return wasHurt;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Naturally-spawned wyverns can't be tamed by feeding — in the original mod
        // the only way to get a tame wyvern is hatching a player-placed egg.
        // (Healing an already-tamed wyvern with raw rat/turkey is handled
        // separately in mobInteract() below, so this staying false doesn't
        // block that — it only blocks the wild-taming-by-food path.)
        return false;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(com.example.neomocreatures.init.ModItems.RAT_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.TURKEY_RAW.get());
    }

    /**
     * Wild wyverns aren't tameable (see isFood() above). Once tamed: sneak +
     * right-click toggles sitting, and raw rat/raw turkey heals it (wiki).
     */
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
                    this.setOrderedToSit(!this.isOrderedToSit());
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
            if (!this.level().isClientSide && player.isSecondaryUseActive()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.setTarget(null);
                this.getNavigation().stop();
                return InteractionResult.SUCCESS;
            }
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
        startMouthAnimation();
        if (!this.level().isClientSide) {
            this.playSound(ModSounds.WYVERN_POISON.get(), 1.0F, 1.0F);
        }
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
        }
    }

    /** Original's mouthCounter gate: only (re)start if it's currently idle. */
    private void startMouthAnimation() {
        if (this.entityData.get(DATA_BITE_TICKS) == 0) {
            this.entityData.set(DATA_BITE_TICKS, 1);
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
        tickWingFlap();
        tickGrowth();

        if (!this.level().isClientSide) {
            // Original's onLivingUpdate(): dampens any fall to a slow glide
            // whenever it's physically airborne, regardless of the isFlying
            // AI flag — this is what makes it glide right after spawning in
            // midair or whenever it drifts off the flying AI state entirely.
            if (!getIsFlying() && isOnAir() && this.getDeltaMovement().y < 0.0D) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
            }

            // Wiki: instantly removed if it drifts below Y=10 in the Wyvern Lair.
            // Only applies to wild (non-tamed) wyverns.
            if (!this.isTame() && this.level().dimension() == ModDimensions.WYVERN_LAIR
                    && this.getY() < LAIR_DESPAWN_Y) {
                this.discard();
                return;
            }

            // Wiki: breaks off and flies away once it drops below half health.
            if (this.getTarget() != null && this.getHealth() < this.getMaxHealth() / 2.0F) {
                this.setTarget(null);
                setIsFlying(true);
            }

            // Wild wyverns take off a lot more readily than they land — aiming
            // for roughly 60% of their time airborne vs. 40% grounded.
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

            if (this.getTarget() != null && !this.isOrderedToSit() && this.random.nextInt(20) == 0) {
                setIsFlying(true);
                if (this.onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4D, 0));
                }
            }

            if (getIsFlying()) {
                Vec3 motion = this.getDeltaMovement();
                double newY;
                LivingEntity attackTarget = this.getTarget();

                if (attackTarget != null) {
                    // Chasing something to attack: head for its altitude
                    // instead of the cruising band below — this is what was
                    // keeping it stuck way above the player instead of
                    // swooping down into melee range.
                    double heightDiff = attackTarget.getY() - this.getY();
                    if (heightDiff < -1.0D) {
                        newY = Math.max(motion.y - 0.08D, -0.6D);
                    } else if (heightDiff > 1.0D) {
                        newY = Math.min(motion.y + 0.06D, 0.5D);
                    } else {
                        newY = motion.y * 0.8D;
                    }
                } else {
                    // Actively climb toward a cruising altitude band above the
                    // ground instead of just constantly decaying downward — that
                    // constant decay is why it used to barely lift off at all.
                    int groundY = this.level().getHeight(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            this.getBlockX(), this.getBlockZ());
                    double heightAboveGround = this.getY() - groundY;

                    if (heightAboveGround < 10.0D) {
                        newY = Math.min(motion.y + 0.06D, 0.5D);
                    } else if (heightAboveGround > 48.0D) {
                        newY = Math.max(motion.y - 0.05D, -0.5D);
                    } else {
                        // Cruising band: damp toward level flight instead of a
                        // constant downward decay. The old constant -0.03/tick
                        // sink meant it was almost always reading as "falling"
                        // (gliding) during ordinary cruising, instead of mostly
                        // flapping like actual flight — this keeps it level most
                        // of the time so isGliding() only fires on a genuine dip.
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

                // Idle hover: lift back up occasionally instead of dropping like a rock
                // while there's nothing to path towards.
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

    /**
     * Continuous baby-to-adult growth (like horses), instead of vanilla's
     * default instant baby/adult size switch. Every hatchling starts at the
     * same absolute BABY_SCALE regardless of tier, and grows toward its own
     * tier's adult scale over its (tier-dependent) growth duration.
     */
    /**
     * Continuous baby-to-adult growth (like horses), instead of vanilla's
     * default instant baby/adult size switch. The adult target is ALWAYS
     * 1.0 (see createXAttributes() above for why) — WyvernTier's renderScale
     * is only used here, as a ratio, to figure out what fraction of THIS
     * tier's full size counts as "the same absolute hatchling size" every
     * tier starts at (a tier with 3x the adult size needs a proportionally
     * smaller starting fraction to look the same size at birth).
     */
    /**
     * Hitbox-only growth curve: Attributes.SCALE never exceeds 1.0, so it
     * only ever SHRINKS a baby relative to its EntityType's own (already
     * tier-correct) declared hitbox — it never multiplies that hitbox
     * upward. Visual rendering uses getVisualScale() below instead, which is
     * a separate, independent calculation (see MoCWyvernRenderer) — mixing
     * the two into one shared multiplier is what caused the hitbox to get
     * multiplied twice (tier's own big .sized() AND a tier-sized attribute
     * on top of it) and rendered mother/tier2 comically oversized.
     */
        private void tickGrowth() {
        if (this.level().isClientSide) {
            return;
        }
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }

        float newScale;
        if (!this.isBaby()) {
            newScale = 1.0F;
        } else {
            int growthTicks = getTier() == WyvernTier.TIER_1 ? TIER_1_GROWTH_TICKS : SLOW_GROWTH_TICKS;
            float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
            float babyFraction = BABY_SCALE / getTier().getRenderScale();
            newScale = Mth.lerp(progress, babyFraction, 1.0F);
        }

        if (scaleAttr.getBaseValue() != newScale) {
            scaleAttr.setBaseValue(newScale);
            this.refreshDimensions();
        }
    }

    /**
     * Visual size multiplier for MoCWyvernRenderer — same growth curve, but
     * computed independently of Attributes.SCALE (kept ≤1.0 above): this one
     * DOES reach tier.getRenderScale() at full growth (1.3/1.5/3.0), since
     * the visual model doesn't already have a bigger size baked in the way
     * the hitbox does.
     */
    public float getVisualScale() {
        float adultScale = getTier().getRenderScale();
        if (!this.isBaby()) {
            return adultScale;
        }
        int growthTicks = getTier() == WyvernTier.TIER_1 ? TIER_1_GROWTH_TICKS : SLOW_GROWTH_TICKS;
        float progress = Mth.clamp((this.getAge() + growthTicks) / (float) growthTicks, 0.0F, 1.0F);
        float babyScale = BABY_SCALE / adultScale;
        return Mth.lerp(progress, babyScale, adultScale);
    }

    /**
     * Same shape as the original's onLivingUpdate(): wingFlapCounter counts
     * 1→20 then resets to 0 (playing the flap sound at 5), and mouthCounter
     * counts 1→30 then resets. Both are read directly by MoCWyvernModel.
     */
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

    /** Original's wingFlap(): (re)starts the burst only if it's currently idle. */
    public void wingFlap() {
        if (this.entityData.get(DATA_WING_FLAP_TICKS) == 0) {
            this.entityData.set(DATA_WING_FLAP_TICKS, 1);
        }
    }

    @Override
    public void jumpFromGround() {
        // Only flap for a real airborne launch — a mundane ground hop while
        // pathfinding around an obstacle (which also calls this) shouldn't
        // make it visibly flap its wings.
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