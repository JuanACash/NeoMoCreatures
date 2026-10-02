package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.ai.ConditionalPanicGoal;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 1:1 behavioural port of drzhark.mocreatures.entity.neutral.MoCEntityGoat,
 * following the wiki. Only ONE model exists in the original (not two
 * separate male/female geometries) — sex/age just toggle which parts are
 * visible (udder vs. bigger horns + beard). The original's per-tick manual
 * counters for ear/tail/mouth swinging are replaced with steady
 * ageInTicks-driven idle animations in the model, same approach used for
 * MoCKomodoDragonEntity's tail/tongue flicks.
 * <p>
 * Extends {@link TamableAnimal} purely so a later taming pass does not
 * require re-registering the entity type — taming/naming is intentionally
 * NOT implemented yet (isFood() stays false). Milking, provoking, item-
 * eating, and male dueling are all core WILD behaviour per the wiki, so
 * they're implemented now rather than deferred.
 */
public class MoCGoatEntity extends TamableAnimal implements GrowthScaled, StorablePet {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;
    private static final int ANGER_DURATION_TICKS = 100;
    private static final double ITEM_EAT_RANGE = 8.0D;
    private static final double FOOD_FOLLOW_RANGE = 10.0D;
    private static final int ATTACK_TICKS_MAX = 30; // matches the original's fixed "attacking = 30" on every hit
    private static final int LEG_TICKS_MAX = 100;    // matches the original's movecount > 100 reset
    private static final int LEG_TICKS_DIG_SOUND_AT = 30; // matches the original's movecount == 30 -> GOAT_DIG
    private static final int BLEAT_TICKS_MAX = 15;

    private static final EntityDataAccessor<Boolean> DATA_MALE =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ANGRY =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICKS =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LEG_TICKS =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_BLEAT_TICKS =
            SynchedEntityData.defineId(MoCGoatEntity.class, EntityDataSerializers.INT);

    private int angerTicks;

    public MoCGoatEntity(EntityType<? extends MoCGoatEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MALE, false);
        builder.define(DATA_COLOR, 0);
        builder.define(DATA_ANGRY, false);
        builder.define(DATA_ATTACK_TICKS, 0);
        builder.define(DATA_LEG_TICKS, 0);
        builder.define(DATA_BLEAT_TICKS, 0);
    }

    public boolean isMale() {
        return this.entityData.get(DATA_MALE);
    }

    public void setMale(boolean male) {
        this.entityData.set(DATA_MALE, male);
    }

    /** 0-5: index into the shared 6-color palette, independent of sex. */
    public int getColorIndex() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setColorIndex(int color) {
        this.entityData.set(DATA_COLOR, color);
    }

    public boolean isAngry() {
        return this.entityData.get(DATA_ANGRY);
    }

    /** Ticks remaining in the lowered-head ram-lunge pose; 0 when idle. */
    public int getAttackTicks() {
        return this.entityData.get(DATA_ATTACK_TICKS);
    }

    /** Ticks into the pawing-the-ground warning stomp; 0 when not doing it. */
    public int getLegTicks() {
        return this.entityData.get(DATA_LEG_TICKS);
    }

    public boolean isBleating() {
        return this.entityData.get(DATA_BLEAT_TICKS) > 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ARMOR, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.5D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1D) // wiki: "a minuscule knockback effect"
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Wiki: female goats and kids are passive — they flee instead of
        // fighting (see setTarget()'s block on retaliation for them).
        this.goalSelector.addGoal(1, new ConditionalPanicGoal(this, 1.2D, () -> this.isBaby() || !this.isMale()));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        // Wiki: "Male goats will fight between themselves."
        this.goalSelector.addGoal(3, new GoatDuelGoal(this));
        this.goalSelector.addGoal(4, new GoatEatItemGoal(this));
        // Wiki: "A goat will follow the player if they have any edible item in hand."
        this.goalSelector.addGoal(5, new GoatFollowFoodGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && (this.isBaby() || !this.isMale())) {
            return; // wiki: female goats and kids are passive
        }
        if (target != null && this.isTame() && target instanceof Player) {
            // Wiki: "Tamed goats will not attack the player if provoked."
            // This blocks HurtByTargetGoal-style retaliation against a
            // player; the milking-ram exception deliberately calls
            // setTargetInternal() directly instead of this method, so it
            // isn't caught by this guard.
            return;
        }
        setTargetInternal(target);
    }

    private void setTargetInternal(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            this.angerTicks = ANGER_DURATION_TICKS;
        }
        this.entityData.set(DATA_ANGRY, target != null && this.isMale() && !this.isBaby());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isMale() && !this.isBaby() && this.getTarget() != null) {
            this.angerTicks--;
            if (this.angerTicks <= 0 || !this.getTarget().isAlive()) {
                // Wiki: "will usually attack a few times, stop attacking and
                // then turn neutral again" — unlike most neutral mobs.
                this.setTarget(null);
            }
        }
        tickAttackAnimation();
        tickLegStomp();
        tickBleat();
        tickGrowth();
    }

    private void tickAttackAnimation() {
        int ticks = this.entityData.get(DATA_ATTACK_TICKS);
        if (ticks > 0) {
            this.entityData.set(DATA_ATTACK_TICKS, ticks - 1);
        }
    }

    private void tickLegStomp() {
        int ticks = this.entityData.get(DATA_LEG_TICKS);
        if (ticks > 0) {
            ticks++;
            if (ticks == LEG_TICKS_DIG_SOUND_AT) {
                this.playSound(ModSounds.GOAT_DIG.get(), 1.0F, 1.0F);
            }
            if (ticks > LEG_TICKS_MAX) {
                ticks = 0;
            }
            this.entityData.set(DATA_LEG_TICKS, ticks);
        } else if (this.isMale() && !this.isBaby() && this.isAngry()
                && this.entityData.get(DATA_ATTACK_TICKS) == 0
                && this.random.nextInt(35) == 0) {
            this.entityData.set(DATA_LEG_TICKS, 1);
        }
    }

    private void tickBleat() {
        int ticks = this.entityData.get(DATA_BLEAT_TICKS);
        if (ticks > 0) {
            this.entityData.set(DATA_BLEAT_TICKS, ticks - 1);
        }
    }

    private float lastAppliedScale = -1F;

    /** Also used by the model to gate horn-growth stages by maturity. */
    public float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        return Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
    }

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = Mth.lerp(getGrowthFraction(), BABY_SCALE, 1.0F);
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

    /** Wiki: "usually stay out of water" — no swim animation needed since it never enters deep water on its own. */
    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false; // wiki/original: goats never take fall damage
    }

    @Override
    protected float getJumpPower() {
        // Deltas match the original's fixed jump velocities (0.41 baby /
        // 0.45 female / 0.5 male) relative to vanilla's own ~0.42 baseline —
        // applied as an offset on top of super's result so jump-boost potion
        // bonuses (already folded into super.getJumpPower()) keep working.
        float base = super.getJumpPower();
        if (this.isBaby()) {
            return base - 0.01F;
        }
        if (!this.isMale()) {
            return base + 0.03F;
        }
        return base + 0.08F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Same rename-with-a-book convention used by every other tameable
        // mob in the mod.
        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.BUCKET)) {
            if (this.isBaby()) {
                return InteractionResult.FAIL; // wiki doesn't describe milking kids
            }
            if (this.isMale()) {
                // Wiki: "males will [ram] if you attempt to milk them" — this
                // explicitly still applies once tamed, so it goes through
                // setTargetInternal() to bypass the "tamed goats won't
                // attack the player" rule below, which only setTarget()
                // itself enforces.
                if (!this.level().isClientSide) {
                    this.setTargetInternal(player);
                }
                return InteractionResult.FAIL;
            }
            if (!this.level().isClientSide) {
                this.playSound(SoundEvents.GOAT_MILK, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.getInventory().add(new ItemStack(Items.MILK_BUCKET));
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "To heal a hurt goat, feed it with any edible item" — tamed only.
        if (this.isTame() && this.isOwnedBy(player) && isFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.setHealth(this.getMaxHealth());
                this.playSound(ModSounds.GOAT_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "A goat can be tamed by feeding it any edible food item."
        if (!this.isTame() && isFood(stack)) {
            if (!this.level().isClientSide) {
                this.tame(player);
                NamingHelper.promptRename(this, player.getUUID());
                this.playSound(ModSounds.GOAT_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
    
    @Override
    public boolean doHurtTarget(Entity target) {
        // Wiki/original: every hit sets a fixed lunge-pose counter, read by
        // the model to lower the head and neck momentarily — this is the
        // "ram" look.
        this.entityData.set(DATA_ATTACK_TICKS, ATTACK_TICKS_MAX);

        if (target instanceof MoCGoatEntity otherGoat) {
            // Wiki: male duels are a shoving match, not real combat — the
            // original never calls the vanilla damage path here at all,
            // only a knockback ("bigSmack") plus the smack sound, with a
            // 1-in-3 chance for both sides to calm down and end the duel.
            applyRamKnockback(target, 0.4F);
            this.playSound(ModSounds.GOAT_SMACK.get(), 1.0F, 1.0F);
            if (this.random.nextInt(3) == 0) {
                this.setTarget(null);
                otherGoat.setTarget(null);
            }
            return false;
        }

        // Attacking a non-goat (e.g. a player who provoked it): stronger
        // knockback alongside real damage, same 1-in-3 chance to calm down
        // mid-fight the original applies to every attack, goat or not.
        applyRamKnockback(target, 0.8F);
        boolean hurt = super.doHurtTarget(target);
        if (this.random.nextInt(3) == 0) {
            this.setTarget(null);
        }
        return hurt;
    }

    /** 1:1 port of the original's MoCTools.bigSmack — a stronger, more directional shove than vanilla's own knockback. */
    private void applyRamKnockback(Entity target, float force) {
        double dx = this.getX() - target.getX();
        double dz = this.getZ() - target.getZ();
        if (dx * dx + dz * dz < 0.0001D) {
            dx = (this.random.nextDouble() - this.random.nextDouble()) * 0.01D;
            dz = (this.random.nextDouble() - this.random.nextDouble()) * 0.01D;
        }
        double dist = Math.sqrt(dx * dx + dz * dz);
        Vec3 current = target.getDeltaMovement();
        Vec3 pushed = current.scale(0.5D).subtract((dx / dist) * force, -force, (dz / dist) * force);
        if (pushed.y > force) {
            pushed = new Vec3(pushed.x, force, pushed.z);
        }
        target.setDeltaMovement(pushed);
        target.hurtMarked = true; // makes sure the pushed velocity actually syncs to the client
    }

    /** Wiki: "will eat any item, including blocks, diamonds, weapons, and tools... cannot eat experience orbs." */
    private static class GoatEatItemGoal extends Goal {
        private final MoCGoatEntity goat;
        @Nullable
        private ItemEntity targetItem;

        GoatEatItemGoal(MoCGoatEntity goat) {
            this.goat = goat;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.goat.getTarget() != null) {
                return false;
            }
            List<ItemEntity> items = this.goat.level().getEntitiesOfClass(ItemEntity.class,
                    this.goat.getBoundingBox().inflate(ITEM_EAT_RANGE), ItemEntity::isAlive);
            if (items.isEmpty()) {
                return false;
            }
            this.targetItem = items.get(this.goat.getRandom().nextInt(items.size()));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetItem != null && this.targetItem.isAlive();
        }

        @Override
        public void start() {
            if (this.targetItem != null) {
                this.goat.getNavigation().moveTo(this.targetItem, 1.0D);
            }
        }

        @Override
        public void tick() {
            if (this.targetItem == null) {
                return;
            }
            this.goat.getLookControl().setLookAt(this.targetItem);
            if (this.goat.distanceToSqr(this.targetItem) < 2.5D) {
                if (!this.goat.level().isClientSide) {
                    this.goat.playSound(ModSounds.GOAT_EATING.get(), 1.0F, 1.0F);
                    this.targetItem.discard();
                }
                this.targetItem = null;
            } else {
                this.goat.getNavigation().moveTo(this.targetItem, 1.0D);
            }
        }

        @Override
        public void stop() {
            this.targetItem = null;
        }
    }

    /** Wiki: "A goat will follow the player if they have any edible item in hand." */
    private static class GoatFollowFoodGoal extends Goal {
        private final MoCGoatEntity goat;
        @Nullable
        private Player target;

        GoatFollowFoodGoal(MoCGoatEntity goat) {
            this.goat = goat;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private boolean isHoldingFood(Player player) {
            return player.getMainHandItem().has(DataComponents.FOOD) || player.getOffhandItem().has(DataComponents.FOOD);
        }

        @Override
        public boolean canUse() {
            if (this.goat.getTarget() != null) {
                return false;
            }
            Player nearest = this.goat.level().getNearestPlayer(this.goat, FOOD_FOLLOW_RANGE);
            if (nearest == null || !isHoldingFood(nearest)) {
                return false;
            }
            this.target = nearest;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null && this.target.isAlive() && isHoldingFood(this.target)
                    && this.goat.distanceToSqr(this.target) < FOOD_FOLLOW_RANGE * FOOD_FOLLOW_RANGE;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                this.goat.getLookControl().setLookAt(this.target);
                this.goat.getNavigation().moveTo(this.target, 1.0D);
            }
        }

        @Override
        public void stop() {
            this.target = null;
        }
    }

    /** Wiki: males lower their heads, stamp, raise tails, then charge and knock each other back. */
    private static class GoatDuelGoal extends Goal {
        private final MoCGoatEntity self;

        GoatDuelGoal(MoCGoatEntity self) {
            this.self = self;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!this.self.isMale() || this.self.isBaby() || this.self.getTarget() != null
                    || this.self.getRandom().nextInt(200) != 0) {
                return false;
            }
            List<MoCGoatEntity> rivals = this.self.level().getEntitiesOfClass(MoCGoatEntity.class,
                    this.self.getBoundingBox().inflate(14.0D),
                    other -> other != this.self && other.isMale() && !other.isBaby());
            if (rivals.isEmpty()) {
                return false;
            }
            MoCGoatEntity rival = rivals.get(this.self.getRandom().nextInt(rivals.size()));
            this.self.setTarget(rival);
            rival.setTarget(this.self);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return false; // one-shot trigger — the anger-timer/MeleeAttackGoal take it from here
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        this.entityData.set(DATA_BLEAT_TICKS, BLEAT_TICKS_MAX);
        if (this.isBaby()) {
            return ModSounds.GOAT_AMBIENT_BABY.get();
        }
        return this.isMale() ? ModSounds.GOAT_AMBIENT_MALE.get() : ModSounds.GOAT_AMBIENT_FEMALE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GOAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GOAT_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
            DifficultyInstance difficulty, MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnGroupData) {
        if ((spawnType == MobSpawnType.NATURAL
                || spawnType == MobSpawnType.CHUNK_GENERATION
                || spawnType == MobSpawnType.SPAWN_EGG)
                && this.random.nextInt(100) < 15) {
            this.setBaby(true);
        }
        setMale(this.random.nextBoolean());
        setColorIndex(this.random.nextInt(6));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Male", isMale());
        tag.putInt("ColorIndex", getColorIndex());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Male")) {
            setMale(tag.getBoolean("Male"));
        }
        if (tag.contains("ColorIndex")) {
            setColorIndex(tag.getInt("ColorIndex"));
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Wiki: "any edible food item (mod and vanilla)" — same check
        // GoatFollowFoodGoal already uses to decide if a player is "holding
        // something edible", just applied here to the item in hand.
        return stack.has(DataComponents.FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        MoCLootUtil.dropItems(this, Items.LEATHER, MoCLootUtil.rollWithFlatLooting(this.random, 3, lootingLevel, 5));
    }

    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    /** Builds the NBT payload stored inside a filled Pet Amulet for this goat. */
    private CompoundTag buildAmuletTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Goat", true);
        tag.putBoolean("Male", isMale());
        tag.putInt("Color", getColorIndex());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Captures this tamed goat into a Pet Amulet and removes it from the world. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setMale(tag.getBoolean("Male"));
        this.setColorIndex(tag.getInt("Color"));
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
            this.setCustomName(Component.literal(tag.getString("Name")));
        }
    }
}
