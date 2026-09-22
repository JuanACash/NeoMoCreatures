package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.ai.AquaticMoveControl;
import com.example.neomocreatures.entity.jellyfish.JellyfishVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.util.NamingHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
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
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish}, built on
 * {@link TamableAnimal} like the other aquatic mobs instead of the original's own aquatic
 * ownership framework.
 * <p>
 * A slow, translucent drifter with 12 colours (only 5 ever roll naturally). Untamed, it poisons any
 * player it touches while they're in the water — never on Peaceful, and never once tamed. It glows
 * faintly at night; that part is a purely visual overlay (see the renderer), since a mod-added
 * entity cannot light up the world around it the way a block-based light source does.
 */
public class MoCJellyfishEntity extends TamableAnimal {

    /** Original: age 100 with a size factor of age * 0.01 gives a model scale of 1.0 — no shrinking needed. */
    private static final double SCALE = 1.0D;
    private static final double MAX_HEALTH = 6.0D;
    /** Original: getAIMoveSpeed() = 0.02 — far slower than any of the fish. */
    private static final double SWIM_SPEED = 0.02D;

    // ---- Swimming ----
    // Unlike the fish, the wiki describes the jellyfish as pulsating in place rather than
    // constantly swimming, so this keeps the original's own long pause between wanders.
    private static final double WANDER_SPEED = 0.5D;
    private static final int WANDER_INTERVAL = 120;

    // ---- Out of water ----
    /** Wiki: about 6 seconds before it starts taking damage on land. */
    private static final int SUFFOCATION_GRACE_TICKS = 120;
    private static final int SUFFOCATION_INTERVAL_TICKS = 40;
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    // ---- Poison touch (untamed only) ----
    /** Original code: a 250-tick cooldown before it can poison again, then a 1 in 30 chance per tick. */
    private static final int POISON_COOLDOWN_TICKS = 250;
    private static final int POISON_CHANCE = 30;
    private static final double POISON_RANGE = 2.0D;
    /** Wiki: Poison I for 5 seconds (100 ticks), on Easy or higher — never on Peaceful, matched below. */
    private static final int POISON_DURATION_TICKS = 100;
    private static final int POISON_AMPLIFIER = 0;

    // ---- Glow ----
    /** Original: a 1 in 200 chance per tick to re-roll whether it currently glows, tied to daytime. */
    private static final int GLOW_CHECK_CHANCE = 200;

    private static final String VARIANT_TAG = "JellyfishVariant";
    /** NBT flag that marks a filled fish net as holding a jellyfish. */
    public static final String NET_KEY = "Jellyfish";

    
    /** Where each colour is allowed to spawn naturally. */
    private static final Map<JellyfishVariant, Set<ResourceKey<Biome>>> SPAWN_BIOMES = Map.ofEntries(
            Map.entry(JellyfishVariant.ORANGE_DARK, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(JellyfishVariant.PURPLE_GRAY, Set.of(Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN)),
            Map.entry(JellyfishVariant.BLUE_DARK, Set.of(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Map.entry(JellyfishVariant.GREEN, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(JellyfishVariant.ORANGE_RED, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(JellyfishVariant.ORANGE_YELLOW, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(JellyfishVariant.BLUE_SPECKLED, Set.of(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Map.entry(JellyfishVariant.WHITE, Set.of(Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN)),
            Map.entry(JellyfishVariant.PURPLE, Set.of(Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN)),
            Map.entry(JellyfishVariant.ORANGE_LIGHT, Set.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN)),
            Map.entry(JellyfishVariant.RED, Set.of(Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Map.entry(JellyfishVariant.BLUE_LIGHT, Set.of(Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN)));

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCJellyfishEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GLOWING =
            SynchedEntityData.defineId(MoCJellyfishEntity.class, EntityDataSerializers.BOOLEAN);

    private int outOfWaterTicks;
    private int poisonCooldown;

    public MoCJellyfishEntity(EntityType<? extends MoCJellyfishEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AquaticMoveControl(this, 30.0F, 0.1D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, SWIM_SPEED)
                .add(Attributes.SCALE, SCALE);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, WANDER_SPEED, WANDER_INTERVAL));
    }

    // ---------------------------------------------------------------------
    // Variant and glow
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, JellyfishVariant.ORANGE_DARK.getId());
        builder.define(DATA_GLOWING, false);
    }

    public JellyfishVariant getVariant() {
        return JellyfishVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(JellyfishVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    public boolean isGlowingAtNight() {
        return this.entityData.get(DATA_GLOWING);
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
            this.setVariant(JellyfishVariant.byName(tag.getString(VARIANT_TAG)));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        // The whole group must be one colour — pick once for the first jellyfish and reuse it for
        // the rest of the group, instead of rolling separately for each individual.
        JellyfishVariant variant;
        if (spawnGroupData instanceof SchoolGroupData schoolData) {
            variant = schoolData.variant;
        } else {
            variant = pickVariantForBiome(level);
            spawnGroupData = new SchoolGroupData(variant);
        }
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /** Picks uniformly among the colours allowed in the biome this individual is spawning in. */
    private JellyfishVariant pickVariantForBiome(ServerLevelAccessor level) {
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        List<JellyfishVariant> eligible = SPAWN_BIOMES.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(biome::is))
                .map(Map.Entry::getKey)
                .toList();
        if (eligible.isEmpty()) {
            return JellyfishVariant.randomWild(this.random);
        }
        return eligible.get(this.random.nextInt(eligible.size()));
    }

    /** Carries the colour picked for the first jellyfish of a spawn group to the rest of that group.
     *  Must extend AgeableMobGroupData, not just implement SpawnGroupData: AgeableMob.finalizeSpawn()
     *  casts whatever is passed in back to that exact type. */
    private static final class SchoolGroupData extends AgeableMob.AgeableMobGroupData {
        private final JellyfishVariant variant;

        SchoolGroupData(JellyfishVariant variant) {
            super(false);
            this.variant = variant;
        }
    }

    // ---------------------------------------------------------------------
    // Ticking
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickOutOfWater();
            this.tickGlow();
            this.tickPoisonTouch();
        }
    }

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

    private void tickGlow() {
        if (this.random.nextInt(GLOW_CHECK_CHANCE) == 0) {
            this.entityData.set(DATA_GLOWING, !this.level().isDay());
        }
    }

    /** Source-confirmed: an untamed jellyfish poisons a player within 2 blocks who is also in the
     *  water, never on Peaceful, at most every {@link #POISON_COOLDOWN_TICKS}. */
    private void tickPoisonTouch() {
        if (this.isTame() || this.level().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (this.poisonCooldown++ <= POISON_COOLDOWN_TICKS || this.random.nextInt(POISON_CHANCE) != 0) {
            return;
        }
        Player player = this.level().getNearestPlayer(this, POISON_RANGE);
        if (player == null || !player.isInWater() || this.distanceTo(player) >= POISON_RANGE
                || player.getAbilities().invulnerable || player.getVehicle() instanceof net.minecraft.world.entity.vehicle.Boat) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, POISON_AMPLIFIER));
        this.poisonCooldown = 0;
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
        this.moveRelative(0.1F, travelVector);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
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
        return null; // jellyfish do not breed
    }

    /** Wiki: 0-2 slimeballs, scaling with Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int count = this.random.nextInt(3) + this.random.nextInt(this.getLootingLevel(level, damageSource) + 1);
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(Items.SLIME_BALL, count));
        }
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
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SLIME_ATTACK;
    }
    
    /** Wiki: only a splash potion of Healing works — Regeneration must not heal it. */
    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return !effectInstance.is(MobEffects.REGENERATION) && super.canBeAffected(effectInstance);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SLIME_ATTACK;
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

    public void restoreFromNet(CompoundTag tag) {
        this.setTame(true, false);
        if (tag.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setVariant(JellyfishVariant.byName(tag.getString(VARIANT_TAG)));
        this.setHealth(tag.getFloat("Health"));
        if (tag.contains("Name") && !tag.getString("Name").isEmpty()) {
            this.setCustomName(Component.literal(tag.getString("Name")));
            this.setCustomNameVisible(true);
        }
    }
}