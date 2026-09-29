package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.ent.EntVariant;
import com.example.neomocreatures.init.ModSounds;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import net.minecraft.world.damagesource.DamageTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

/**
 * Port of {@code drzhark.mocreatures.entity.neutral.MoCEntityEnt}. Neutral: never starts a fight,
 * but fights back against whoever manages to hurt it. Its only weaknesses are a melee hit from an
 * axe and fire (plus damage that bypasses invulnerability, e.g. /kill or the void) — everything
 * else, lava included, is ignored.
 */
public class MoCEntEntity extends Animal {

    private static final double MAX_HEALTH = 60.0D;
    private static final double ARMOR = 7.0D;
    private static final double ATTACK_DAMAGE = 7.5D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double KNOCKBACK_RESISTANCE = 1.0D;
    /** Original: stepHeight = 2F — walks straight up two-block ledges. */
    private static final double STEP_HEIGHT = 2.0D;

    private static final double ATTACK_SPEED_MODIFIER = 1.0D;
    private static final double WANDER_SPEED_MODIFIER = 1.0D;
    private static final float LOOK_DISTANCE = 8.0F;
    /** Original: MoCTools.bigSmack(this, target, 1F) on every landed hit. */
    private static final float SMACK_FORCE = 1.0F;
    /** Original: 1-in-500 chance per tick to plant (only while not fighting), 1-in-100 to call critters. */
    private static final int PLANT_ATTEMPT_CHANCE = 500;
    private static final int ATTRACT_ATTEMPT_CHANCE = 100;
    /** Original: each of the 9 spots around it gets the plant with 1-in-3 odds (1-in-10 for saplings). */
    private static final int PLANT_SPOT_CHANCE = 3;
    private static final int SAPLING_SPOT_CHANCE = 10;

    private static final double CRITTER_SEARCH_HORIZONTAL = 8.0D;
    private static final double CRITTER_SEARCH_VERTICAL = 3.0D;
    private static final float CRITTER_MAX_SIZE = 0.6F;
    private static final double CRITTER_WALK_SPEED = 1.0D;

    /** Original: 4-15 of a single item (log, sticks or sapling), no looting bonus. */
    private static final int DROP_MIN = 4;
    private static final int DROP_RANGE = 12;
    /** Wiki: 1-3 experience, only when killed by a player (vanilla already enforces the player part). */
    private static final int EXPERIENCE_MIN = 1;
    private static final int EXPERIENCE_RANGE = 3;

    private static final Block[] SMALL_GRASSES = {Blocks.SHORT_GRASS, Blocks.FERN};
    private static final Block[] TALL_PLANTS = {
            Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY, Blocks.TALL_GRASS, Blocks.LARGE_FERN};
    private static final Block[] SMALL_FLOWERS = {
            Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP,
            Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY};

    private static final String TAG_VARIANT = "EntVariant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCEntEntity.class, EntityDataSerializers.INT);

    public MoCEntEntity(EntityType<? extends MoCEntEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.STEP_HEIGHT, STEP_HEIGHT);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, ATTACK_SPEED_MODIFIER, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED_MODIFIER));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));

        // Neutral: only ever targets whoever actually hurt it (which can only be an axe wielder).
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    // ---------------------------------------------------------------------
    // Nature behaviour — planting and calling small critters
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.getTarget() == null && this.random.nextInt(PLANT_ATTEMPT_CHANCE) == 0) {
            this.tryPlantAround();
        }
        if (this.random.nextInt(ATTRACT_ATTEMPT_CHANCE) == 0) {
            this.attractCritters();
        }
    }

    /** Original: plantOnFertileGround() — turns dirt underfoot into grass, or plants around itself on grass. */
    private void tryPlantAround() {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        BlockPos feet = this.blockPosition();
        BlockPos below = feet.below();
        BlockState groundState = this.level().getBlockState(below);

        if (groundState.is(Blocks.DIRT)) {
            BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
            this.level().setBlock(below, grass, Block.UPDATE_ALL);
            this.level().gameEvent(GameEvent.BLOCK_CHANGE, below, GameEvent.Context.of(this, grass));
            return;
        }
        if (!groundState.is(Blocks.GRASS_BLOCK) || !this.level().getBlockState(feet).isAir()) {
            return;
        }

        BlockState plant = this.pickPlantToGrow();
        int spotChance = plant.getBlock() instanceof SaplingBlock ? SAPLING_SPOT_CHANCE : PLANT_SPOT_CHANCE;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (this.random.nextInt(spotChance) == 0) {
                    this.placePlant(feet.offset(dx, 0, dz), plant);
                }
            }
        }
    }

    /** Original odds out of 20: 8 small grass, 3 tall plant, 3 dandelion, 3 small flower, 1 each mushroom, 1 own sapling. */
    private BlockState pickPlantToGrow() {
        int roll = this.random.nextInt(20);
        Block block;
        if (roll < 8) {
            block = this.pickRandom(SMALL_GRASSES);
        } else if (roll < 11) {
            block = this.pickRandom(TALL_PLANTS);
        } else if (roll < 14) {
            block = Blocks.DANDELION;
        } else if (roll < 17) {
            block = this.pickRandom(SMALL_FLOWERS);
        } else if (roll == 17) {
            block = Blocks.BROWN_MUSHROOM;
        } else if (roll == 18) {
            block = Blocks.RED_MUSHROOM;
        } else {
            block = this.getVariant().getSapling();
        }
        return block.defaultBlockState();
    }

    private Block pickRandom(Block[] blocks) {
        return blocks[this.random.nextInt(blocks.length)];
    }

    /** Only places where the plant can actually survive; two-block plants get both halves. */
    private void placePlant(BlockPos pos, BlockState plant) {
        if (!this.level().getBlockState(pos).isAir() || !plant.canSurvive(this.level(), pos)) {
            return;
        }
        if (plant.getBlock() instanceof DoublePlantBlock) {
            if (!this.level().getBlockState(pos.above()).isAir()) {
                return;
            }
            DoublePlantBlock.placeAt(this.level(), plant, pos, Block.UPDATE_ALL);
        } else {
            this.level().setBlock(pos, plant, Block.UPDATE_ALL);
        }
        this.level().gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(this, plant));
    }

    /** Original: atractCritter() — a few nearby small, untamed, calm animals walk over to the Ent. */
    private void attractCritters() {
        List<Animal> critters = this.level().getEntitiesOfClass(Animal.class,
                this.getBoundingBox().inflate(CRITTER_SEARCH_HORIZONTAL, CRITTER_SEARCH_VERTICAL, CRITTER_SEARCH_HORIZONTAL),
                this::isAttractableCritter);
        // Original stops once its counter passes a 1-3 roll, so 2-4 critters answer the call.
        int maxCritters = this.random.nextInt(3) + 2;
        for (int i = 0; i < critters.size() && i < maxCritters; i++) {
            critters.get(i).getNavigation().moveTo(this, CRITTER_WALK_SPEED);
        }
    }

    private boolean isAttractableCritter(Animal animal) {
        boolean small = animal.getBbWidth() < CRITTER_MAX_SIZE && animal.getBbHeight() < CRITTER_MAX_SIZE;
        boolean tamed = animal instanceof TamableAnimal tamable && tamable.isTame();
        return small && !tamed && animal.getTarget() == null;
    }

    // ---------------------------------------------------------------------
    // Variant
    // ---------------------------------------------------------------------

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, EntVariant.OAK.getId());
    }

    public EntVariant getVariant() {
        return EntVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    private void setVariant(EntVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.setVariant(EntVariant.byId(tag.getInt(TAG_VARIANT)));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(this.pickVariantFor(level));
        // Ents never spawn as babies — AgeableMob.finalizeSpawn() needs this exact group data type.
        SpawnGroupData groupData = spawnGroupData != null ? spawnGroupData : new AgeableMob.AgeableMobGroupData(false);
        return super.finalizeSpawn(level, difficulty, spawnType, groupData);
    }

    /** Flower forests only grow oak ents and birch forests only birch ones; anywhere else it's the original 50/50. */
    private EntVariant pickVariantFor(ServerLevelAccessor level) {
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        if (biome.is(Biomes.FLOWER_FOREST)) {
            return EntVariant.OAK;
        }
        if (biome.is(Biomes.BIRCH_FOREST) || biome.is(Biomes.OLD_GROWTH_BIRCH_FOREST)) {
            return EntVariant.BIRCH;
        }
        return EntVariant.random(this.random);
    }

    // ---------------------------------------------------------------------
    // Damage rules — axe only
    // ---------------------------------------------------------------------

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        }
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !isAxeMeleeHit(source) && !isFireDamage(source);
    }

    /** Wiki: fire is its other weakness, but lava is not — so only flames and burning count, not the IS_FIRE tag as a whole. */
    private static boolean isFireDamage(DamageSource source) {
        return source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.CAMPFIRE);
    }

    /** True only for a direct melee hit (no projectiles) from something holding an axe. */
    private static boolean isAxeMeleeHit(DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker) || source.getDirectEntity() != attacker) {
            return false;
        }
        ItemStack weapon = attacker.getMainHandItem();
        return weapon.is(ItemTags.AXES);
    }

    // ---------------------------------------------------------------------
    // Combat
    // ---------------------------------------------------------------------

    /** Original: applyEnchantments() — every landed hit plays the goat smack and sends the target flying. */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            this.playSound(ModSounds.GOAT_SMACK.get(), 1.0F, 1.0F);
            this.applyBigSmack(target, SMACK_FORCE);
        }
        return hurt;
    }

    /** 1:1 port of the original's MoCTools.bigSmack. */
    private void applyBigSmack(Entity target, float force) {
        double dx = this.getX() - target.getX();
        double dz = this.getZ() - target.getZ();
        while (dx * dx + dz * dz < 1.0E-4D) {
            dx = (this.random.nextDouble() - this.random.nextDouble()) * 0.01D;
            dz = (this.random.nextDouble() - this.random.nextDouble()) * 0.01D;
        }
        double dist = Math.sqrt(dx * dx + dz * dz);
        Vec3 pushed = target.getDeltaMovement().scale(0.5D).subtract((dx / dist) * force, -force, (dz / dist) * force);
        if (pushed.y > force) {
            pushed = new Vec3(pushed.x, force, pushed.z);
        }
        target.setDeltaMovement(pushed);
        target.hurtMarked = true; // syncs the new velocity to the client (needed for players)
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks
    // ---------------------------------------------------------------------

    /** Original: canBePushed() returns false. */
    @Override
    public boolean isPushable() {
        return false;
    }

    /** Original: canTriggerWalking() returns false — silent steps, no vibrations. */
    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // no breeding in the original
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original: dropSpecialItems() — one roll picks a log, sticks or a sapling of its own wood, 4-15 of it. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int count = DROP_MIN + this.random.nextInt(DROP_RANGE);
        ItemLike drop = switch (this.random.nextInt(3)) {
            case 0 -> this.getVariant().getLog();
            case 1 -> Items.STICK;
            default -> this.getVariant().getSapling();
        };
        this.spawnAtLocation(new ItemStack(drop, count));
    }

    /** Wiki: 1-3 experience. */
    @Override
    protected int getBaseExperienceReward() {
        return EXPERIENCE_MIN + this.random.nextInt(EXPERIENCE_RANGE);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR;
    }
}