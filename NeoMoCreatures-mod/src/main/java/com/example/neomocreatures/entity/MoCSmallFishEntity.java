package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.ai.AquaticMoveControl;
import com.example.neomocreatures.entity.ai.DepthBandSwimGoal;
import com.example.neomocreatures.entity.ai.HerdFollowGoal;
import com.example.neomocreatures.entity.smallfish.SmallFishVariant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.NamingHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntitySmallFish} and its aggressive subclass
 * {@code MoCEntityPiranha}, unified into one entity with a {@link SmallFishVariant} instead of 8
 * separate classes — only the piranha's behaviour actually differs, and it is switched on per
 * individual by {@link SmallFishVariant#isAggressive()}.
 * <p>
 * Step 1 (this file): 8 colour variants, a tiny fish that swims 0-2 blocks under the surface,
 * suffocates out of water and, only for the piranha, attacks players on sight and sticks to its
 * group. Not implemented yet: taming with a fish net or a hatched egg, natural spawning and drops.
 */
public class MoCSmallFishEntity extends TamableAnimal implements EggHatchable {

    /** Original: age 100 with a size factor of age * 0.01 gives a model scale of 1.0 — no shrinking needed. */
    private static final double SCALE = 1.0D;
    private static final double MAX_HEALTH = 4.0D;
    /** Original: only the piranha registers this; every other variant keeps the vanilla default (0). */
    private static final double PIRANHA_MAX_HEALTH = 5.0D;
    private static final double PIRANHA_ATTACK_DAMAGE = 3.5D;
    /** Original: getAIMoveSpeed() = 0.10. */
    private static final double SWIM_SPEED = 0.10D;

    // ---- Goals (passive variants) ----
    private static final double PANIC_SPEED = 1.3D;
    private static final int FLEE_PRIORITY = 2;
    /** Original: flees from anything bigger than 0.3 blocks in either dimension. */
    private static final float FLEE_SIZE_THRESHOLD = 0.3F;
    private static final float FLEE_DISTANCE = 2.0F;
    private static final double FLEE_FAR_SPEED = 0.6D;
    private static final double FLEE_NEAR_SPEED = 1.5D;
    private static final int WANDER_PRIORITY = 5;
    private static final double WANDER_SPEED = 1.0D;
    // Vanilla's own fish (AbstractFish) use 10 here; our earlier 80 meant an average 4-second pause
    // between swims, which read as standing still.
    private static final int WANDER_INTERVAL = 10;

    // ---- Goals (piranha only) ----
    private static final double ATTACK_SPEED = 1.0D;
    private static final double HERD_SPEED = 0.6D;
    private static final double HERD_MIN_RANGE = 4.0D;
    private static final double HERD_MAX_RANGE = 20.0D;
    private static final int HERD_EXECUTION_CHANCE = 120;

    /** NBT flag that marks a filled fish net as holding a small fish (any of the 8 variants). */
    public static final String NET_KEY = "SmallFish";
    // ---- Fishing hook attraction (untamed only) ----
    /** Original: a 1 in 30 chance per tick to look for a hook to swim toward. */
    private static final int HOOK_SEEK_CHANCE = 30;
    /** Original: only looks at the closest player within this range. */
    private static final double HOOK_SEEK_RANGE = 18.0D;
    private static final double HOOK_APPROACH_SPEED = 1.0D;

    // ---- Swimming ----
    private static final double MIN_CRUISE_DEPTH = 0.0D;
    private static final double MAX_CRUISE_DEPTH = 2.0D;
    private static final float SWIM_MAX_TURN_DEGREES = 30.0F;
    private static final float WATER_THRUST = 0.1F;
    private static final double WATER_DRAG = 0.9D;
    private static final double CLIMB_LEDGE_IMPULSE = 0.05D;
    private static final double VERTICAL_STEERING = 0.1D;

    // ---- Out of water ----
    private static final int SUFFOCATION_GRACE_TICKS = 300;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    private static final String VARIANT_TAG = "SmallFishVariant";

    /** Where each species is allowed to spawn naturally; the piranha's own biomes never overlap the rest. */
    private static final Map<SmallFishVariant, Set<ResourceKey<Biome>>> SPAWN_BIOMES = Map.ofEntries(
            Map.entry(SmallFishVariant.ANCHOVY, Set.of(Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN)),
            Map.entry(SmallFishVariant.ANGELFISH, Set.of(Biomes.WARM_OCEAN)),
            Map.entry(SmallFishVariant.ANGLER, Set.of(Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN)),
            Map.entry(SmallFishVariant.CLOWNFISH, Set.of(Biomes.WARM_OCEAN)),
            Map.entry(SmallFishVariant.GOLDFISH, Set.of(Biomes.LUKEWARM_OCEAN)),
            Map.entry(SmallFishVariant.HIPPOTANG, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(SmallFishVariant.MANDARIN, Set.of(Biomes.WARM_OCEAN)),
            Map.entry(SmallFishVariant.PIRANHA, Set.of(Biomes.RIVER, Biomes.SWAMP, Biomes.MANGROVE_SWAMP)));

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCSmallFishEntity.class, EntityDataSerializers.INT);

    private int outOfWaterTicks;

    public MoCSmallFishEntity(EntityType<? extends MoCSmallFishEntity> type, Level level) {
        super(type, level);
        // WaterAnimal does this; without it vanilla's random destination picker rejects almost
        // every water position for this mob, and the fish never finds anywhere to swim to.
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AquaticMoveControl(this, SWIM_MAX_TURN_DEGREES, VERTICAL_STEERING);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, SWIM_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 0.0D)
                .add(Attributes.SCALE, SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PassivePanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(FLEE_PRIORITY, new PassiveFleeGoal(this));
        this.goalSelector.addGoal(WANDER_PRIORITY, new DepthBandSwimGoal(this, WANDER_SPEED, WANDER_INTERVAL,
                MIN_CRUISE_DEPTH, MAX_CRUISE_DEPTH));

        this.goalSelector.addGoal(3, new PiranhaAttackGoal(this, ATTACK_SPEED));
        this.goalSelector.addGoal(4, new PiranhaHerdGoal(this, HERD_SPEED, HERD_MIN_RANGE, HERD_MAX_RANGE, HERD_EXECUTION_CHANCE));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new PiranhaTargetPlayerGoal(this));
    }

    private static boolean isBigEnoughToFleeFrom(LivingEntity entity) {
        return entity.getBbHeight() > FLEE_SIZE_THRESHOLD || entity.getBbWidth() > FLEE_SIZE_THRESHOLD;
    }

    // ---------------------------------------------------------------------
    // Variant
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, SmallFishVariant.ANCHOVY.getId());
    }

    public SmallFishVariant getVariant() {
        return SmallFishVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    /** Also rebalances health and attack damage: only the piranha variant fights back. */
    public void setVariant(SmallFishVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
        this.applyVariantAttributes(variant);
    }

    private void applyVariantAttributes(SmallFishVariant variant) {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (health != null) {
            health.setBaseValue(variant.isAggressive() ? PIRANHA_MAX_HEALTH : MAX_HEALTH);
        }
        if (attackDamage != null) {
            attackDamage.setBaseValue(variant.isAggressive() ? PIRANHA_ATTACK_DAMAGE : 0.0D);
        }
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(VARIANT_TAG, this.getVariant().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(VARIANT_TAG, Tag.TAG_STRING)) {
            SmallFishVariant variant = SmallFishVariant.byName(tag.getString(VARIANT_TAG));
            this.entityData.set(DATA_VARIANT, variant.getId());
            this.applyVariantAttributes(variant);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(pickVariantForBiome(level));
        return super.finalizeSpawn(level, difficulty, spawnType, new AgeableMob.AgeableMobGroupData(false));
    }

    /** Picks uniformly among the species allowed in the biome this individual is spawning in. */
    private SmallFishVariant pickVariantForBiome(ServerLevelAccessor level) {
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        List<SmallFishVariant> eligible = SPAWN_BIOMES.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(biome::is))
                .map(Map.Entry::getKey)
                .toList();
        if (eligible.isEmpty()) {
            return SmallFishVariant.randomPassive(this.random);
        }
        return eligible.get(this.random.nextInt(eligible.size()));
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickOutOfWater();
            this.tickHookSeeking();
        }
    }

    /** Stranded fish cannot move (no water to path through) and eventually suffocate. */
    private void tickOutOfWater() {
        if (this.isInWaterOrBubble()) {
            this.outOfWaterTicks = 0;
            return;
        }
        this.outOfWaterTicks++;
        if (this.outOfWaterTicks > SUFFOCATION_GRACE_TICKS
                && this.outOfWaterTicks % SUFFOCATION_INTERVAL_TICKS == 0) {
            this.hurt(this.damageSources().drown(), SUFFOCATION_DAMAGE);
        }
    }

    /**
     * Wiki: swims toward a nearby player's cast fishing hook. Reaching it does not by itself get the
     * fish caught — modern Minecraft only hooks a living entity through the bobber's own in-flight
     * collision, the same way it would with or without this mod, and exposes no way for another mod
     * to force that afterward.
     */
    private void tickHookSeeking() {
        if (this.isTame() || this.isInWater() == false || this.random.nextInt(HOOK_SEEK_CHANCE) != 0) {
            return;
        }
        Player player = this.level().getNearestPlayer(this, HOOK_SEEK_RANGE);
        if (player == null || !(player.fishing instanceof FishingHook hook) || hook.isRemoved()) {
            return;
        }
        this.getNavigation().moveTo(hook, HOOK_APPROACH_SPEED);
    }

    // ---------------------------------------------------------------------
    // Water
    // ---------------------------------------------------------------------

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isEffectiveAi() || !this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        this.moveRelative(WATER_THRUST, travelVector);
        if (this.horizontalCollision && !this.getNavigation().isDone()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, Math.max(motion.y, CLIMB_LEDGE_IMPULSE), motion.z);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(WATER_DRAG));
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // small fish do not breed
    }

    /** Wiki: 0-2 raw fish (a tropical fish, same call made for the fishy) and 0-2 eggs of its own
     *  variant, both scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = this.getLootingLevel(level, damageSource);
        int fishCount = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (fishCount > 0) {
            this.spawnAtLocation(new ItemStack(Items.TROPICAL_FISH, fishCount));
        }
        int eggCount = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (eggCount > 0) {
            this.spawnAtLocation(new ItemStack(this.getEggItemForVariant(), eggCount));
        }
    }

    private net.minecraft.world.item.Item getEggItemForVariant() {
        return switch (this.getVariant()) {
            case ANCHOVY -> ModItems.ANCHOVY_EGG.get();
            case ANGELFISH -> ModItems.ANGELFISH_EGG.get();
            case ANGLER -> ModItems.ANGLERFISH_EGG.get();
            case CLOWNFISH -> ModItems.CLOWNFISH_EGG.get();
            case GOLDFISH -> ModItems.GOLDFISH_EGG.get();
            case HIPPOTANG -> ModItems.HIPPOTANG_EGG.get();
            case MANDARIN -> ModItems.MANDARINFISH_EGG.get();
            case PIRANHA -> ModItems.PIRANHA_EGG.get();
        };
    }

    private int getLootingLevel(ServerLevel level, DamageSource damageSource) {
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            return EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        return 0;
    }

    /** Wiki: 1-3 experience, awarded only when a player or a tamed wolf made the kill (vanilla's rule). */
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

        // ---------------------------------------------------------------------
    // Hatching from an egg
    // ---------------------------------------------------------------------

    /** Each egg bakes in a fixed species; a hatched small fish is tamed to whoever was nearby. */
    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        this.setVariant(variantId != null ? SmallFishVariant.byName(variantId) : SmallFishVariant.randomPassive(this.random));
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    // ---------------------------------------------------------------------
    // Taming and interaction
    // ---------------------------------------------------------------------

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.FISH_NET.get()) && (!this.isTame() || this.isOwnedBy(player))) {
            return this.captureInFishNet(player, hand, stack);
        }
        if (this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /** A tamed small fish never despawns and must not count against the spawn cap of wild ones. */
    @Override
    public void setTame(boolean tame, boolean applyTamingSideEffects) {
        super.setTame(tame, applyTamingSideEffects);
        if (tame) {
            this.setPersistenceRequired();
        }
    }

    // ---------------------------------------------------------------------
    // Fish net
    // ---------------------------------------------------------------------

    /** Turns one empty net into a filled one holding this fish, and removes it from the world. */
    private InteractionResult captureInFishNet(Player player, InteractionHand hand, ItemStack emptyNet) {
        if (!this.level().isClientSide) {
            ItemStack filled = new ItemStack(ModItems.FISH_NET_FULL.get());
            filled.set(DataComponents.CUSTOM_DATA, CustomData.of(this.createNetTag(player)));
            if (!player.getAbilities().instabuild) {
                emptyNet.shrink(1);
            }
            if (emptyNet.isEmpty()) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            this.discard();
        }
        return InteractionResult.SUCCESS;
    }

    private CompoundTag createNetTag(Player owner) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NET_KEY, true);
        tag.putString(VARIANT_TAG, this.getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        tag.putUUID("OwnerUUID", owner.getUUID());
        return tag;
    }

    /** Applies the data stored by {@link #createNetTag} to a freshly created fish, and tames it: the
     *  wiki tames on release from a net regardless of whether it was tame when caught. */
    public void restoreFromNet(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setVariant(SmallFishVariant.byName(tag.getString(VARIANT_TAG)));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }

    // ---------------------------------------------------------------------
    // Variant-gated goals
    // ---------------------------------------------------------------------

    /** Only the 7 passive variants panic when hurt; the piranha stands its ground. */
    private static final class PassivePanicGoal extends PanicGoal {
        private final MoCSmallFishEntity fish;

        PassivePanicGoal(MoCSmallFishEntity fish, double speedModifier) {
            super(fish, speedModifier);
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            // Wiki: stranded on land, a small fish does not move at all until it suffocates.
            return this.fish.isInWater() && !this.fish.getVariant().isAggressive() && super.canUse();
        }
    }

    /** Only the 7 passive variants flee from bigger things; the piranha attacks them instead. */
    private static final class PassiveFleeGoal extends AvoidEntityGoal<LivingEntity> {
        private final MoCSmallFishEntity fish;

        PassiveFleeGoal(MoCSmallFishEntity fish) {
            super(fish, LivingEntity.class, FLEE_DISTANCE, FLEE_FAR_SPEED, FLEE_NEAR_SPEED,
                    MoCSmallFishEntity::isBigEnoughToFleeFrom);
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            return this.fish.isInWater() && !this.fish.getVariant().isAggressive() && super.canUse();
        }
    }

    /** Only the piranha variant fights. */
    private static final class PiranhaAttackGoal extends MeleeAttackGoal {
        private final MoCSmallFishEntity fish;

        PiranhaAttackGoal(MoCSmallFishEntity fish, double speedModifier) {
            super(fish, speedModifier, false);
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            return this.fish.isInWater() && this.fish.getVariant().isAggressive() && super.canUse();
        }
    }

    /** Only the piranha variant sticks to its group. */
    private static final class PiranhaHerdGoal extends HerdFollowGoal {
        private final MoCSmallFishEntity fish;

        PiranhaHerdGoal(MoCSmallFishEntity fish, double speedModifier, double minRange, double maxRange, int executionChance) {
            super(fish, speedModifier, minRange, maxRange, executionChance,
                    other -> other instanceof MoCSmallFishEntity otherFish && otherFish.getVariant().isAggressive());
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            return this.fish.getVariant().isAggressive() && super.canUse();
        }
    }

    /** Only the piranha variant targets players, and never its own owner (wiki: tamed piranhas
     *  don't attack their owner — everyone else in range is still fair game). */
    private static final class PiranhaTargetPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final MoCSmallFishEntity fish;

        PiranhaTargetPlayerGoal(MoCSmallFishEntity fish) {
            super(fish, Player.class, true);
            this.fish = fish;
        }

        @Override
        public boolean canUse() {
            if (!this.fish.getVariant().isAggressive() || !super.canUse()) {
                return false;
            }
            if (this.target != null && this.fish.isOwnedBy(this.target)) {
                this.target = null;
                return false;
            }
            return true;
        }
    }
}