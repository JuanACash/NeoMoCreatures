package com.example.neomocreatures.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.entity.filchlizard.FilchLizardVariant;
import com.example.neomocreatures.init.ModDimensions;
import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityFilchLizard}. A small, silent lizard that
 * steals shiny things (the neomocreatures:filch_lizard_steals item tag) — off the ground or straight
 * out of a nearby player's inventory — and runs off with them in its mouth (its main hand). Hitting it
 * makes it drop the whole stack; it never despawns or dies without giving its loot back.
 */
public class MoCFilchLizardEntity extends Animal {

    private static final double MAX_HEALTH = 8.0D;
    private static final double ARMOR = 2.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final int EXPERIENCE = 3;

    private static final double PANIC_SPEED_MODIFIER = 1.25D;
    private static final double WANDER_SPEED_MODIFIER = 1.0D;
    private static final float LOOK_DISTANCE = 6.0F;
    /** Original: stands up on its hind legs while carrying something, so its hitbox gets 2.5x taller. */
    private static final float CARRYING_HEIGHT_SCALE = 2.5F;
    /** Original: EntityAIGrabItemFromFloor — speed 1.2, looks 6 blocks around (4 up/down), grabs within 1. */
    private static final double GRAB_SPEED_MODIFIER = 1.2D;
    private static final double GROUND_SEARCH_HORIZONTAL = 6.0D;
    private static final double GROUND_SEARCH_VERTICAL = 4.0D;
    private static final double GRAB_REACH_SQR = 1.0D;
    /** Original: EntityAIStealFromPlayer — speed 0.8, targets the nearest player within 10, steals within 1.8. */
    private static final double STEAL_SPEED_MODIFIER = 0.8D;
    private static final double VICTIM_SEARCH_RANGE = 10.0D;
    private static final double STEAL_REACH_SQR = 3.25D;
    /** Original: AIAvoidWhenNasty — flees players within 16 while carrying something. */
    private static final float FLEE_DISTANCE = 16.0F;
    private static final double FLEE_WALK_SPEED_MODIFIER = 1.0D;
    private static final double FLEE_SPRINT_SPEED_MODIFIER = 1.33D;
    /** Original: after being hit it won't steal again for a while (it used 50 and 100 ticks; one 5 s cooldown here). */
    private static final int SCARED_TICKS = 100;
    /** Original: FilchLizardSpawnItemChance config, 25% by default. */
    private static final int SPAWN_WITH_LOOT_PERCENT = 25;
    private static final int PATH_RECALCULATE_TICKS = 10;
    private static final ResourceKey<LootTable> SPAWN_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "entities/filch_lizard_spawn_item"));

    private static final String TAG_VARIANT = "FilchVariant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCFilchLizardEntity.class, EntityDataSerializers.INT);

    /** Server-side: while above 0 it is too scared to steal. */
    private int scaredTicks;

    public MoCFilchLizardEntity(EntityType<? extends MoCFilchLizardEntity> type, Level level) {
        super(type, level);
        // Loot is handled by hand (the whole stack is always given back), never by vanilla drop chances.
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, PANIC_SPEED_MODIFIER));
        this.goalSelector.addGoal(2, new GrabLootFromGroundGoal(this));
        this.goalSelector.addGoal(3, new StealFromPlayerGoal(this));
        this.goalSelector.addGoal(4, new FleeWithLootGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED_MODIFIER));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // ---------------------------------------------------------------------
    // Variant
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, FilchLizardVariant.NORMAL.getId());
    }

    public FilchLizardVariant getVariant() {
        return FilchLizardVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    private void setVariant(FilchLizardVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(this.pickVariantFor(level));
        this.maybeSpawnWithLoot(level.getLevel());
        SpawnGroupData groupData = spawnGroupData != null ? spawnGroupData : new AgeableMob.AgeableMobGroupData(false);
        return super.finalizeSpawn(level, difficulty, spawnType, groupData);
    }

    /**
     * Original: checkSpawningBiome(). Badlands are checked before sandy biomes — in the original both
     * counted as sandy, so the red variant could never actually appear.
     */
    private FilchLizardVariant pickVariantFor(ServerLevelAccessor level) {
        if (level.getLevel().dimension() == ModDimensions.WYVERN_LAIR) {
            return FilchLizardVariant.SILVER;
        }
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        if (biome.is(BiomeTags.IS_BADLANDS)) {
            return FilchLizardVariant.RED_SAND;
        }
        if (biome.is(Tags.Biomes.IS_SANDY)) {
            return FilchLizardVariant.SAND;
        }
        return FilchLizardVariant.NORMAL;
    }

    // ---------------------------------------------------------------------
    // Carried loot
    // ---------------------------------------------------------------------

    /** The stolen item it carries in its mouth (its main hand), or an empty stack. */
    public ItemStack getCarriedLoot() {
        return this.getMainHandItem();
    }

    public boolean isCarryingLoot() {
        return !this.getCarriedLoot().isEmpty();
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDefaultDimensions(pose);
        return this.isCarryingLoot() ? dimensions.scale(1.0F, CARRYING_HEIGHT_SCALE) : dimensions;
    }

    /** Stands up (taller hitbox) the moment it picks something up, and crouches back down once it lets go. */
    @Override
    public void onEquipItem(EquipmentSlot slot, ItemStack oldItem, ItemStack newItem) {
        super.onEquipItem(slot, oldItem, newItem);
        if (slot == EquipmentSlot.MAINHAND && oldItem.isEmpty() != newItem.isEmpty()) {
            this.refreshDimensions();
        }
    }

    // ---------------------------------------------------------------------
    // Stealing
    // ---------------------------------------------------------------------

    public static boolean isStealable(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModTags.FILCH_LIZARD_STEALS);
    }

    /** Empty-mouthed and not scared. */
    private boolean canStartStealing() {
        return !this.isCarryingLoot() && this.scaredTicks <= 0;
    }

    /** Takes the whole stack off the ground, with the vanilla pick-up animation. */
    private void grabFromGround(ItemEntity item) {
        ItemStack loot = item.getItem().copy();
        this.take(item, loot.getCount());
        item.discard();
        this.setItemSlot(EquipmentSlot.MAINHAND, loot);
    }

    /** First inventory slot (hotbar, main, armour or offhand) holding something it wants, or -1. */
    private static int findStealableSlot(Player player) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (isStealable(inventory.getItem(slot))) {
                return slot;
            }
        }
        return -1;
    }

    /** Original: steals exactly one item from the player's inventory. */
    private void stealFrom(Player player) {
        int slot = findStealableSlot(player);
        if (slot < 0) {
            return;
        }
        Inventory inventory = player.getInventory();
        ItemStack loot = inventory.getItem(slot).split(1);
        inventory.setChanged();
        this.setItemSlot(EquipmentSlot.MAINHAND, loot);
        this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
    }

    /** Gives its whole loot back to the world. */
    private void dropCarriedLoot() {
        ItemStack loot = this.getCarriedLoot();
        if (!loot.isEmpty()) {
            this.spawnAtLocation(loot.copy(), 1.0F);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    /** Original: onInitialSpawn() — 25% chance to already be carrying something from its spawn loot table. */
    private void maybeSpawnWithLoot(ServerLevel level) {
        if (this.random.nextInt(100) >= SPAWN_WITH_LOOT_PERCENT) {
            return;
        }
        LootTable table = level.getServer().reloadableRegistries().getLootTable(SPAWN_LOOT_TABLE);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.SELECTOR);
        table.getRandomItems(params).stream().findFirst()
                .ifPresent(stack -> this.setItemSlot(EquipmentSlot.MAINHAND, stack));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.scaredTicks > 0) {
            this.scaredTicks--;
        }
    }

    /** Original: any hit makes it drop what it carries — here the whole stack, not just one — and it gets scared. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            this.dropCarriedLoot();
            this.scaredTicks = SCARED_TICKS;
        }
        return hurt;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide && !this.dead) {
            this.dropCarriedLoot();
        }
        super.die(damageSource);
    }

    /** Never despawns while carrying loot, so nothing it stole can vanish. */
    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isCarryingLoot();
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks
    // ---------------------------------------------------------------------

    /** Original: "Sneaky..." — it makes no footstep sounds. */
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected int getBaseExperienceReward() {
        return EXPERIENCE;
    }

    // ---------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.setVariant(FilchLizardVariant.byId(tag.getInt(TAG_VARIANT)));
        }
    }


    // ---------------------------------------------------------------------
    // Goals
    // ---------------------------------------------------------------------

    /** Original: EntityAIGrabItemFromFloor — walks to a stealable item lying nearby and takes it. */
    private static final class GrabLootFromGroundGoal extends Goal {
        private final MoCFilchLizardEntity lizard;
        @Nullable
        private ItemEntity loot;
        private int recalculateTicks;

        GrabLootFromGroundGoal(MoCFilchLizardEntity lizard) {
            this.lizard = lizard;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.lizard.canStartStealing()) {
                return false;
            }
            List<ItemEntity> items = this.lizard.level().getEntitiesOfClass(ItemEntity.class,
                    this.lizard.getBoundingBox().inflate(GROUND_SEARCH_HORIZONTAL, GROUND_SEARCH_VERTICAL, GROUND_SEARCH_HORIZONTAL),
                    item -> item.isAlive() && !item.hasPickUpDelay() && isStealable(item.getItem()));
            this.loot = items.stream().min(Comparator.comparingDouble(this.lizard::distanceToSqr)).orElse(null);
            return this.loot != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.loot != null && this.loot.isAlive() && this.lizard.canStartStealing();
        }

        @Override
        public void start() {
            this.recalculateTicks = 0;
        }

        @Override
        public void stop() {
            this.loot = null;
            this.lizard.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.loot == null) {
                return;
            }
            this.lizard.getLookControl().setLookAt(this.loot, 30.0F, 30.0F);
            if (this.lizard.distanceToSqr(this.loot) < GRAB_REACH_SQR) {
                this.lizard.grabFromGround(this.loot);
            } else if (--this.recalculateTicks <= 0) {
                this.recalculateTicks = PATH_RECALCULATE_TICKS;
                this.lizard.getNavigation().moveTo(this.loot, GRAB_SPEED_MODIFIER);
            }
        }
    }

    /** Original: EntityAIStealFromPlayer — sneaks up on a nearby survival player carrying something it wants. */
    private static final class StealFromPlayerGoal extends Goal {
        private final MoCFilchLizardEntity lizard;
        @Nullable
        private Player victim;
        private int recalculateTicks;

        StealFromPlayerGoal(MoCFilchLizardEntity lizard) {
            this.lizard = lizard;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.lizard.canStartStealing()) {
                return false;
            }
            Player player = this.lizard.level().getNearestPlayer(this.lizard.getX(), this.lizard.getY(), this.lizard.getZ(),
                    VICTIM_SEARCH_RANGE, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            if (player == null || findStealableSlot(player) < 0) {
                return false;
            }
            this.victim = player;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.victim != null && this.victim.isAlive() && this.lizard.canStartStealing()
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(this.victim)
                    && this.lizard.distanceToSqr(this.victim) <= VICTIM_SEARCH_RANGE * VICTIM_SEARCH_RANGE
                    && findStealableSlot(this.victim) >= 0;
        }

        @Override
        public void start() {
            this.recalculateTicks = 0;
        }

        @Override
        public void stop() {
            this.victim = null;
            this.lizard.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.victim == null) {
                return;
            }
            this.lizard.getLookControl().setLookAt(this.victim, 30.0F, 30.0F);
            if (this.lizard.distanceToSqr(this.victim) < STEAL_REACH_SQR) {
                this.lizard.getNavigation().stop();
                this.lizard.stealFrom(this.victim);
            } else if (--this.recalculateTicks <= 0) {
                this.recalculateTicks = PATH_RECALCULATE_TICKS;
                this.lizard.getNavigation().moveTo(this.victim, STEAL_SPEED_MODIFIER);
            }
        }
    }

    /** Original: AIAvoidWhenNasty — only runs from players while it has something in its mouth. */
    private static final class FleeWithLootGoal extends AvoidEntityGoal<Player> {
        private final MoCFilchLizardEntity lizard;

        FleeWithLootGoal(MoCFilchLizardEntity lizard) {
            super(lizard, Player.class, FLEE_DISTANCE, FLEE_WALK_SPEED_MODIFIER, FLEE_SPRINT_SPEED_MODIFIER);
            this.lizard = lizard;
        }

        @Override
        public boolean canUse() {
            return this.lizard.isCarryingLoot() && super.canUse();
        }
    }

}