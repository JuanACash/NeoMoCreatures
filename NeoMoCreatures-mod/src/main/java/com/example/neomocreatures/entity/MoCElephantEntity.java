package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.ai.ConditionalMeleeAttackGoal;
import com.example.neomocreatures.entity.ai.ConditionalStrollGoal;
import com.example.neomocreatures.entity.elephant.ElephantVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.init.ModTags;
import com.example.neomocreatures.network.OpenPlayerInventoryPayload;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCInventoryUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Step 1 port of drzhark.mocreatures.entity.neutral.MoCEntityElephant: walks,
 * swims, retaliates when attacked, and grows from baby to adult. No taming,
 * tusks, harness/storage/garment/howdah/platform, or sitting yet — those
 * come in later steps the same way the wyvern's did.
 * <p>
 * Growth uses vanilla's standard baby-age system (unlike the original's
 * bespoke 0-100 age/temper scale) for consistency with the rest of this
 * codebase — see MoCWyvernEntity's tickGrowth() for the same pattern.
 */
public class MoCElephantEntity extends TamableAnimal implements GrowthScaled, PlayerRideableJumping,
        HasCustomInventoryScreen, StorablePet {

    private static final float BABY_SCALE = 0.5F;
    private static final int GROWTH_TICKS = 24000;
    private static final int TAME_GOAL = 10;
    private static final int SIT_HOLD_TICKS = 100; // ~5 seconds after the owner stops sneaking nearby
    private static final double SIT_TRIGGER_RANGE_SQR = 16.0D; // 4 blocks
    private static final float RIDER_HEIGHT = 1.85F;
    private static final float RIDER_FORWARD = 0F;
    private static final float SONGHUA_RIDER_HEIGHT_BONUS = 0.25F;
    private double lastTuskCheckX;
    private double lastTuskCheckZ;
    private static final float PLATFORM_BACK_SEAT_FORWARD = -3.0F;
    private static final int WHIP_CHARGE_DURATION_TICKS = 60; // 3 seconds, same as the unicorn's charge
    private static final int WHIP_SPEED_AMPLIFIER = 1;
    private static final int SAFE_FALL_BLOCKS = 3;
    // First 2 chests give 18 slots each, the 3rd and 4th (mammoth only) give 9 each —
    // 18+18+9+9 = 54, exactly vanilla's ChestMenu row limit, so all 4 stay fully usable.
    private final SimpleContainer chestInventory = new SimpleContainer(54);

    private static int slotsForChestIndex(int index) {
        return index < 2 ? 18 : 9;
    }

    /** Total slots occupied by the first `count` chests combined. */
    private static int totalChestSlots(int count) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += slotsForChestIndex(i);
        }
        return total;
    }

    private int sitTicksRemaining;
    /** Independent of BABY_SCALE (which only affects the visual model) — this is purely the collision box size. */
    private static final float BABY_HITBOX_SCALE = 0.75F;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    /** Idle animation counters, ticked server-side and synced — same pattern as MoCWyvernEntity's wing/mouth ticks. */
    private static final EntityDataAccessor<Integer> DATA_TAIL_TICKS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_EAR_TICKS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TRUNK_TICKS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TAME_PROGRESS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    /** Mouth-open animation ticks for eating — same pattern as MoCWyvernEntity's bite ticks. */
    private static final EntityDataAccessor<Integer> DATA_EAT_TICKS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HARNESSED =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING_SYNCED =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TUSK_TIER =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GARMENT =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HOWDAH =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PLATFORM =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WHIP_CHARGE_TICKS =
            SynchedEntityData.defineId(MoCElephantEntity.class, EntityDataSerializers.INT);

    /** Full ItemStack (not just a tier enum) so durability survives and anvil repair keeps working normally. */
    private ItemStack tuskStack = ItemStack.EMPTY;

    private static final Set<Block> BULLDOZER_BLACKLIST = Set.of(
            Blocks.OBSIDIAN,
            Blocks.CRYING_OBSIDIAN,
            Blocks.BEDROCK,
            Blocks.REINFORCED_DEEPSLATE);
    private static final double BULLDOZER_MIN_SPEED_SQR = 0.0025D;

    public MoCElephantEntity(EntityType<? extends MoCElephantEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ConditionalMeleeAttackGoal(this, 1.0D, false, () -> !this.isVehicle()));
        // Calves stick close to the nearest adult instead of wandering off on their own.
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D) {
            @Override
            public boolean canUse() {
                return !MoCElephantEntity.this.isTame() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new ConditionalStrollGoal(this, 1.0D, () -> !this.isVehicle()));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    /** Sneaking near a tamed, harnessed elephant sits it for a short while so it can be mounted. */
    private void tickHarnessSit() {
        if (!this.isTame() || !hasHarness()) {
            return;
        }
        boolean ownerSneakingNearby = this.getOwner() instanceof Player owner
                && owner.isShiftKeyDown()
                && this.distanceToSqr(owner) <= SIT_TRIGGER_RANGE_SQR;

        if (ownerSneakingNearby || this.isVehicle()) {
            this.sitTicksRemaining = SIT_HOLD_TICKS;
            if (!this.isOrderedToSit()) {
                setSitting(true);
            }
        } else if (this.isOrderedToSit()) {
            if (this.sitTicksRemaining > 0) {
                this.sitTicksRemaining--;
            } else {
                setSitting(false);
            }
        }
    }

    /**
     * Checks the block directly ahead in its current walking direction and stops
     * dead before stepping into water or off a drop tall enough to hurt it —
     * only while nobody's riding it (a rider's own choices aren't overridden).
     */
    private void avoidHazardsAhead() {
        if (this.isVehicle() || !this.onGround()) {
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        if (motion.x * motion.x + motion.z * motion.z < 0.0004D) {
            return;
        }
        Vec3 dir = new Vec3(motion.x, 0.0D, motion.z).normalize();
        BlockPos ahead = this.blockPosition()
                .offset((int) Math.round(dir.x), 0, (int) Math.round(dir.z));

        if (this.level().getFluidState(ahead).is(FluidTags.WATER)) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, motion.y, 0.0D);
            return;
        }

        BlockPos.MutableBlockPos check = ahead.below().mutable();
        int drop = 0;
        while (drop <= SAFE_FALL_BLOCKS && this.level().getBlockState(check).getCollisionShape(this.level(), check).isEmpty()) {
            check.move(0, -1, 0);
            drop++;
        }
        if (drop > SAFE_FALL_BLOCKS) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, motion.y, 0.0D);
        }
    }

    private void applyLavaBuoyancy() {
        if (this.isInLava()) {
            double submergedFraction = this.getFluidHeight(FluidTags.LAVA);
            if (this.getDeltaMovement().y < 0 && !this.onGround() && submergedFraction >= 0.5) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1, 0.0, 1));
            }
        }
    }

    /**
     * Breaks enough blocks directly ahead — as many as its own height needs —
     * to clear a path, and damages whatever it rams into, while moving with
     * tusks equipped ("bulldozer" effect). Uses real tick-to-tick position
     * change instead of getDeltaMovement(): while a player is riding and
     * steering, the CLIENT is authoritative for the actual motion (like a
     * boat), so the server's own deltaMovement can read as zero even though
     * it's clearly moving — comparing positions directly is reliable either way.
     */
    private void tickTuskBulldozer() {
        if (tuskStack.isEmpty() || this.isBaby() || !this.isVehicle() || !(this.level() instanceof ServerLevel level)
                || !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            lastTuskCheckX = this.getX();
            lastTuskCheckZ = this.getZ();
            return;
        }

        double dx = this.getX() - lastTuskCheckX;
        double dz = this.getZ() - lastTuskCheckZ;
        lastTuskCheckX = this.getX();
        lastTuskCheckZ = this.getZ();

        if (dx * dx + dz * dz < BULLDOZER_MIN_SPEED_SQR) {
            return;
        }

        damageCollidedEntities();

        Vec3 dir = new Vec3(dx, 0.0D, dz).normalize();

        // Its actual body footprint, pushed forward and padded a bit sideways so it always
        // covers at least 2 block columns wide — the exact hitbox alone can land centered
        // on a single column depending on position, which isn't enough to guarantee a path.
        double blocksLong = Math.max(1.0D, this.getBbWidth());
        AABB path = this.getBoundingBox()
                .inflate(0.65D, 0.0D, 0.65D)
                .move(dir.x * blocksLong, 0.0D, dir.z * blocksLong);

        BlockPos min = BlockPos.containing(path.minX, this.getY(), path.minZ);
        BlockPos max = BlockPos.containing(path.maxX, this.getY(), path.maxZ);

        float hardnessCap = bulldozerHardnessCap();
        int blocksHigh = Mth.ceil(this.getBbHeight()); // clears its own full height, not a fixed count
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int yOffset = 0; yOffset < blocksHigh; yOffset++) {
                    BlockPos pos = new BlockPos(x, this.blockPosition().getY() + yOffset, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir() || BULLDOZER_BLACKLIST.contains(state.getBlock())) {
                        continue;
                    }
                    float hardness = state.getDestroySpeed(level, pos);
                    if (hardness < 0F || hardness > hardnessCap) {
                        continue;
                    }
                    level.destroyBlock(pos, true, this);
                    damageTusks();
                }
            }
        }
    }

    public int getWhipChargeTicks() {
        return this.entityData.get(DATA_WHIP_CHARGE_TICKS);
    }

    /** Speed boost + a short ramming window — called by WhipItem while ridden. */
    public void startWhipCharge() {
        this.entityData.set(DATA_WHIP_CHARGE_TICKS, WHIP_CHARGE_DURATION_TICKS);
        this.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED, WHIP_CHARGE_DURATION_TICKS, WHIP_SPEED_AMPLIFIER, false, true));
    }

    /** Pushes (and lightly hurts) anything it bumps into while charging — same knockback-style ram as the unicorn. */
    private void tickWhipCharge() {
        int ticks = getWhipChargeTicks();
        if (ticks <= 0) {
            return;
        }
        AABB aabb = this.getBoundingBox().inflate(0.6D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e != this && e != this.getControllingPassenger() && !this.hasPassenger(e) && e.isAlive())) {
            if (target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                Vec3 knockDir = target.position().subtract(this.position()).normalize();
                target.setDeltaMovement(target.getDeltaMovement().add(knockDir.x * 1.2D, 0.4D, knockDir.z * 1.2D));
                target.hurtMarked = true;
            }
        }
        this.entityData.set(DATA_WHIP_CHARGE_TICKS, ticks - 1);
    }

    /** Rams whatever it's overlapping while bulldozing — vanilla's own hit-invulnerability
     *  window keeps this from re-hitting the same entity every single tick. */
    private void damageCollidedEntities() {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (Entity hit : this.level().getEntities(this, this.getBoundingBox().inflate(0.2D))) {
            if (hit == this.getOwner() || this.hasPassenger(hit) || !(hit instanceof LivingEntity living)) {
                continue;
            }
            living.hurt(this.damageSources().mobAttack(this), damage);
            living.knockback(0.4F, this.getX() - living.getX(), this.getZ() - living.getZ());
        }
    }

    private void damageTusks() {
        if (tuskStack.isEmpty()) {
            return;
        }
        tuskStack.setDamageValue(tuskStack.getDamageValue() + 1);
        if (tuskStack.getDamageValue() >= tuskStack.getMaxDamage()) {
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
            tuskStack = ItemStack.EMPTY;
            this.entityData.set(DATA_TUSK_TIER, 0);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    public ElephantVariant getVariant() {
        return ElephantVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    public boolean hasHarness() {
        return this.entityData.get(DATA_HARNESSED);
    }

    public boolean hasGarment() {
        return this.entityData.get(DATA_GARMENT);
    }

    private void setGarment(boolean garment) {
        this.entityData.set(DATA_GARMENT, garment);
    }

    public boolean hasHowdah() {
        return this.entityData.get(DATA_HOWDAH);
    }

    private void setHowdah(boolean howdah) {
        this.entityData.set(DATA_HOWDAH, howdah);
    }

    public boolean hasPlatform() {
        return this.entityData.get(DATA_PLATFORM);
    }

    private void setPlatform(boolean platform) {
        this.entityData.set(DATA_PLATFORM, platform);
    }

    public int getChestCount() {
        return this.entityData.get(DATA_CHEST_COUNT);
    }

    private void setChestCount(int count) {
        this.entityData.set(DATA_CHEST_COUNT, count);
    }

    public int getMaxChestCount() {
        return getVariant().isMammoth() ? 4 : 2;
    }

    /** 0 = none, 1 = wood, 2 = iron, 3 = diamond — for the model to pick the right look. */
    public int getTuskTier() {
        return this.entityData.get(DATA_TUSK_TIER);
    }

    private boolean isTuskItem(ItemStack stack) {
        return stack.is(ModItems.TUSKS_WOOD.get())
                || stack.is(ModItems.TUSKS_IRON.get())
                || stack.is(ModItems.TUSKS_DIAMOND.get());
    }

    private int tuskTierFor(ItemStack stack) {
        if (stack.is(ModItems.TUSKS_DIAMOND.get())) {
            return 3;
        }
        return stack.is(ModItems.TUSKS_IRON.get()) ? 2 : 1;
    }

    /** Elephant/mammoth hardness ceiling per tier — obsidian/bedrock are always excluded regardless. */
    private float bulldozerHardnessCap() {
        float base = switch (getTuskTier()) {
            case 1 -> 2.0F;
            case 2 -> 6.0F;
            case 3 -> 30.0F;
            default -> 0.0F;
        };
        return getVariant().isMammoth() ? base * 1.5F : base;
    }

    public boolean isSittingSynced() {
        return this.entityData.get(DATA_SITTING_SYNCED);
    }

    /** Always use this instead of calling setOrderedToSit() directly — mirrors it into
     *  a plain synced boolean the model can read reliably (same fix the wyvern needed). */
    private void setSitting(boolean sitting) {
        this.setOrderedToSit(sitting);
        this.entityData.set(DATA_SITTING_SYNCED, sitting);
    }

    private void setHarnessed(boolean harnessed) {
        this.entityData.set(DATA_HARNESSED, harnessed);
    }

    /** Also re-applies the species' max health/speed and heals to full — only meant to be called once, at spawn. */
    public void setVariant(ElephantVariant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
        AttributeInstance maxHealthAttr = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(variant.getMaxHealth());
        }
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(variant.getMovementSpeed());
        }
        this.setHealth(this.getMaxHealth());
    }

    public int getTailTicks() {
        return this.entityData.get(DATA_TAIL_TICKS);
    }

    public int getEarTicks() {
        return this.entityData.get(DATA_EAR_TICKS);
    }

    public int getTrunkTicks() {
        return this.entityData.get(DATA_TRUNK_TICKS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, ElephantVariant.AFRICAN.getId());
        builder.define(DATA_TAIL_TICKS, 0);
        builder.define(DATA_EAR_TICKS, 0);
        builder.define(DATA_TRUNK_TICKS, 0);
        builder.define(DATA_TAME_PROGRESS, 0);
        builder.define(DATA_EAT_TICKS, 0);
        builder.define(DATA_HARNESSED, false);
        builder.define(DATA_SITTING_SYNCED, false);
        builder.define(DATA_CHEST_COUNT, 0);
        builder.define(DATA_TUSK_TIER, 0);
        builder.define(DATA_GARMENT, false);
        builder.define(DATA_HOWDAH, false);
        builder.define(DATA_PLATFORM, false);
        builder.define(DATA_WHIP_CHARGE_TICKS, 0);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Taming (cake / sugar lump) comes in a later step.
        return false;
    }

    private boolean isTameFood(ItemStack stack) {
        return stack.is(ModItems.SUGAR_LUMP.get())
                || stack.is(Items.CAKE);
    }

    /** Cake counts double so either 10 sugar lumps or 5 cakes reach TAME_GOAL exactly. */
    private int tameFoodValue(ItemStack stack) {
        return stack.is(Items.CAKE) ? 2 : 1;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(ModItems.SUGAR_LUMP.get())
                || stack.is(Items.BREAD)
                || stack.is(Items.WHEAT)
                || stack.is(Items.BAKED_POTATO);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnReason,
            @Nullable SpawnGroupData spawnGroupData) {
        ElephantVariant variant = (spawnReason == MobSpawnType.NATURAL
                || spawnReason == MobSpawnType.CHUNK_GENERATION)
                ? variantForBiome(level, this.blockPosition())
                : ElephantVariant.randomSpawnable(this.random);
        setVariant(variant);
        // Both adults and babies spawn naturally, per the wiki.
        if (this.random.nextInt(2) == 0) {
            this.setAge(-GROWTH_TICKS);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
    }

    /**
     * Asian in sparse jungle, African in savanna plateau, either mammoth in the cold
     * biomes listed on the wiki. A herd's 2nd/3rd member can land a few blocks into a
     * neighboring biome that isn't one of those exact ones — the fallback below picks
     * by the actual biome temperature instead of pure random, so it never picks
     * something thematically wrong (e.g. an African in the snow) just because of that drift.
     */
    private ElephantVariant variantForBiome(ServerLevelAccessor level, BlockPos pos) {
        var biome = level.getBiome(pos);
        if (biome.is(ModTags.ELEPHANT_ASIAN_BIOMES)) {
            return ElephantVariant.ASIAN;
        }
        if (biome.is(ModTags.ELEPHANT_AFRICAN_BIOMES)) {
            return ElephantVariant.AFRICAN;
        }
        if (biome.is(ModTags.ELEPHANT_MAMMOTH_BIOMES)) {
            return this.random.nextBoolean() ? ElephantVariant.MAMMOTH_WOOLLY : ElephantVariant.MAMMOTH_SONGHUA;
        }

        float temperature = biome.value().getBaseTemperature();
        if (temperature <= 0.15F) {
            return this.random.nextBoolean() ? ElephantVariant.MAMMOTH_WOOLLY : ElephantVariant.MAMMOTH_SONGHUA;
        }
        if (temperature >= 1.0F) {
            return ElephantVariant.AFRICAN;
        }
        return ElephantVariant.ASIAN;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        // Only calves can be tamed — once grown, they refuse the food entirely.
        if (!this.isTame() && this.isBaby() && isTameFood(stack)) {
            if (!this.level().isClientSide) {
                playEatEffects();
                int progress = this.entityData.get(DATA_TAME_PROGRESS) + tameFoodValue(stack);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (progress >= TAME_GOAL) {
                    this.tame(player);
                    setSitting(false);
                    this.entityData.set(DATA_TAME_PROGRESS, 0);
                    NamingHelper.promptRename(this, player.getUUID());
                } else {
                    this.entityData.set(DATA_TAME_PROGRESS, progress);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                playEatEffects();
                this.heal(4.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Calves can't be equipped with anything.
        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && !hasHarness()
                && stack.is(ModItems.ELEPHANT_HARNESS.get())) {
            if (!this.level().isClientSide) {
                setHarnessed(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && hasHarness() && this.isOrderedToSit()
                && canAddPassenger(player) && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        // Calves can't be equipped with anything.
        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && hasHarness() && !hasGarment()
                && getChestCount() < getMaxChestCount()
                && stack.is(ModItems.ELEPHANT_CHEST.get())) {
            if (!this.level().isClientSide) {
                setChestCount(getChestCount() + 1);
                this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Only a fully grown, tamed Asian with a harness — never with chests already on, and never the other 3 species.
        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && hasHarness() && !hasGarment()
                && getVariant() == ElephantVariant.ASIAN && getChestCount() == 0
                && stack.is(ModItems.ELEPHANT_GARMENT.get())) {
            if (!this.level().isClientSide) {
                setGarment(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // The howdah needs the garment on first.
        if (this.isTame() && this.isOwnedBy(player) && hasGarment() && !hasHowdah()
                && stack.is(ModItems.ELEPHANT_HOWDAH.get())) {
            if (!this.level().isClientSide) {
                setHowdah(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && hasHarness() && !hasPlatform()
                && getVariant() == ElephantVariant.MAMMOTH_SONGHUA
                && stack.is(ModItems.MAMMOTH_PLATFORM.get())) {
            if (!this.level().isClientSide) {
                setPlatform(true);
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && getChestCount() > 0 && player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                openChestMenu(player);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && !this.isBaby() && tuskStack.isEmpty() && isTuskItem(stack)) {
            if (!this.level().isClientSide) {
                tuskStack = stack.copyWithCount(1);
                this.entityData.set(DATA_TUSK_TIER, tuskTierFor(tuskStack));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && !tuskStack.isEmpty() && stack.is(ItemTags.PICKAXES)) {
            if (!this.level().isClientSide) {
                this.spawnAtLocation(tuskStack.copy());
                tuskStack = ItemStack.EMPTY;
                this.entityData.set(DATA_TUSK_TIER, 0);
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(Items.SHEARS) && hasHowdah()) {
            if (!this.level().isClientSide) {
                setHowdah(false);
                this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_HOWDAH.get()));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(Items.SHEARS) && hasGarment() && !hasHowdah()) {
            if (!this.level().isClientSide) {
                setGarment(false);
                this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_GARMENT.get()));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        // Chests come off before the harness — shears always remove the most recently added one first.
        if (this.isTame() && stack.is(Items.SHEARS) && getChestCount() > 0) {
            if (!this.level().isClientSide) {
                int removedIndex = getChestCount() - 1;
                int rangeStart = totalChestSlots(removedIndex);
                int rangeEnd = totalChestSlots(removedIndex + 1);
                for (int slot = rangeStart; slot < rangeEnd; slot++) {
                    this.spawnAtLocation(chestInventory.getItem(slot));
                    chestInventory.setItem(slot, ItemStack.EMPTY);
                }
                setChestCount(removedIndex);
                this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_CHEST.get()));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && stack.is(Items.SHEARS) && hasPlatform()) {
            if (!this.level().isClientSide) {
                setPlatform(false);
                if (this.getPassengers().size() > 1) {
                    this.getPassengers().get(1).stopRiding();
                }
                this.spawnAtLocation(new ItemStack(ModItems.MAMMOTH_PLATFORM.get()));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        // Shears removal order matters once more equipment exists (howdah -> garment ->
        // platform/chests -> tusks -> harness last) — for now harness is the only thing to remove.
        // The harness only comes off once everything that depends on it is already gone.
        if (this.isTame() && stack.is(Items.SHEARS) && hasHarness()
                && getChestCount() == 0 && !hasGarment() && !hasPlatform() && tuskStack.isEmpty()) {
            if (!this.level().isClientSide) {
                setHarnessed(false);
                this.ejectPassengers();
                this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_HARNESS.get()));
                this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    /** Same eating sound as horses/wyverns, plus the mouth-open animation (see MoCElephantModel). */
    private void playEatEffects() {
        if (this.entityData.get(DATA_EAT_TICKS) == 0) {
            this.entityData.set(DATA_EAT_TICKS, 1);
        }
        this.playSound(ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
    }

    private static MenuType<ChestMenu> menuTypeForRows(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            default -> MenuType.GENERIC_9x6;
        };
    }

    private void openChestMenu(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            int rows = totalChestSlots(getChestCount()) / 9;
            var menuType = menuTypeForRows(rows);
            Component title = this.hasCustomName()
                    ? this.getDisplayName().copy().append(" Storage")
                    : Component.literal("Elephant Storage");
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new ChestMenu(menuType, id, inv, this.chestInventory, rows),
                    title));
        }
    }

    /**
     * Vanilla calls this automatically when the rider presses E while mounted on
     * ANY entity implementing HasCustomInventoryScreen — same hook the wyvern uses.
     * With chests equipped, E opens them instead of the player's own inventory.
     */
    @Override
    public void openCustomInventoryScreen(Player player) {
        if (this.level().isClientSide || !this.isTame()) {
            return;
        }
        if (getChestCount() > 0) {
            openChestMenu(player);
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new OpenPlayerInventoryPayload());
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            dropAllEquipment();
        }
        super.die(source);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHitByPlayer) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHitByPlayer);

        LivingEntity killer = this.getLastHurtByMob();
        if (!MoCLootUtil.isKilledByPlayerOrTamedWolf(recentlyHitByPlayer, killer)) {
            return;
        }

        MoCExperienceUtil.dropExperienceOrb(level, this, MoCExperienceUtil.rollStandardXp(this.random));

        int lootingLevel = MoCLootUtil.getLootingLevel(killer);

        MoCLootUtil.dropItems(this, ModItems.HIDE.get(), MoCLootUtil.rollWithFlatLooting(this.random, 3, lootingLevel, 5));
    }

    private void dropChestsAndContents() {
        for (int slot = 0; slot < totalChestSlots(getChestCount()); slot++) {
            this.spawnAtLocation(chestInventory.getItem(slot));
        }
        for (int i = 0; i < getChestCount(); i++) {
            this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_CHEST.get()));
        }
        setChestCount(0);
    }

    public int getEatTicks() {
        return this.entityData.get(DATA_EAT_TICKS);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? ModSounds.ELEPHANT_AMBIENT_BABY.get() : ModSounds.ELEPHANT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ELEPHANT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ELEPHANT_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
        if (!this.level().isClientSide) {
            tickIdleCounters();
            tickHarnessSit();
            tickTuskBulldozer();
            avoidHazardsAhead();
            applyLavaBuoyancy();
            if (this.isVehicle()) {
                tickWhipCharge();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TameProgress", this.entityData.get(DATA_TAME_PROGRESS));
        tag.putString("ElephantVariant", getVariant().name());
        tag.putBoolean("ElephantHarnessed", hasHarness());
        tag.putBoolean("ElephantSittingSynced", isSittingSynced());
        tag.putInt("ElephantChestCount", getChestCount());
        if (getChestCount() > 0) {
            tag.put("ElephantChestItems", MoCInventoryUtil.saveSlots(chestInventory, this.registryAccess()));
        }
        if (!tuskStack.isEmpty()) {
            tag.put("ElephantTusks", tuskStack.save(this.registryAccess(), new CompoundTag()));
        }
        tag.putBoolean("ElephantGarment", hasGarment());
        tag.putBoolean("ElephantHowdah", hasHowdah());
        tag.putBoolean("ElephantPlatform", hasPlatform());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("TameProgress")) {
            this.entityData.set(DATA_TAME_PROGRESS, tag.getInt("TameProgress"));
        }
        if (tag.contains("ElephantVariant", 8)) {
            try {
                setVariant(ElephantVariant.valueOf(tag.getString("ElephantVariant")));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (tag.contains("ElephantHarnessed")) {
            setHarnessed(tag.getBoolean("ElephantHarnessed"));
        }
        if (tag.contains("ElephantSittingSynced")) {
            this.setSitting(tag.getBoolean("ElephantSittingSynced"));
        }
        if (tag.contains("ElephantChestCount")) {
            setChestCount(tag.getInt("ElephantChestCount"));
        }
        if (tag.contains("ElephantChestItems", 9)) {
            MoCInventoryUtil.loadSlots(chestInventory, tag.getList("ElephantChestItems", 10), this.registryAccess());
        }
        if (tag.contains("ElephantTusks", 10)) {
            tuskStack = ItemStack.parse(this.registryAccess(), tag.getCompound("ElephantTusks")).orElse(ItemStack.EMPTY);
            this.entityData.set(DATA_TUSK_TIER, tuskStack.isEmpty() ? 0 : tuskTierFor(tuskStack));
        }
        if (tag.contains("ElephantGarment")) {
            setGarment(tag.getBoolean("ElephantGarment"));
        }
        if (tag.contains("ElephantHowdah")) {
            setHowdah(tag.getBoolean("ElephantHowdah"));
        }
        if (tag.contains("ElephantPlatform")) {
            setPlatform(tag.getBoolean("ElephantPlatform"));
        }
    }

    /** Random idle counters for the tail swish / ear flap / trunk sway — read client-side by MoCElephantModel. */
    private void tickIdleCounters() {
        int tail = this.entityData.get(DATA_TAIL_TICKS);
        if (tail > 0 && ++tail > 8) {
            tail = 0;
        }
        if (tail == 0 && this.random.nextInt(200) == 0) {
            tail = 1;
        }
        this.entityData.set(DATA_TAIL_TICKS, tail);

        int trunk = this.entityData.get(DATA_TRUNK_TICKS);
        if (trunk > 0 && ++trunk > 38) {
            trunk = 0;
        }
        if (trunk == 0 && this.random.nextInt(200) == 0) {
            trunk = this.random.nextInt(10) + 1;
        }
        this.entityData.set(DATA_TRUNK_TICKS, trunk);

        int ear = this.entityData.get(DATA_EAR_TICKS);
        if (ear > 0 && ++ear > 30) {
            ear = 0;
        }
        if (ear == 0 && this.random.nextInt(200) == 0) {
            ear = this.random.nextInt(20) + 1;
        }
        this.entityData.set(DATA_EAR_TICKS, ear);
        int eat = this.entityData.get(DATA_EAT_TICKS);
        if (eat > 0 && ++eat > 20) {
            eat = 0;
        }
        this.entityData.set(DATA_EAT_TICKS, eat);
    }

    /** Everything wearable this elephant currently has on — used by die() and by the Scroll of Freedom. */
    public void dropAllEquipment() {
        if (hasHowdah()) {
            this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_HOWDAH.get()));
            setHowdah(false);
        }
        if (hasGarment()) {
            this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_GARMENT.get()));
            setGarment(false);
        }
        if (hasPlatform()) {
            this.spawnAtLocation(new ItemStack(ModItems.MAMMOTH_PLATFORM.get()));
            setPlatform(false);
        }
        dropChestsAndContents();
        if (hasHarness()) {
            this.spawnAtLocation(new ItemStack(ModItems.ELEPHANT_HARNESS.get()));
            setHarnessed(false);
        }
        if (!tuskStack.isEmpty()) {
            this.spawnAtLocation(tuskStack.copy());
            tuskStack = ItemStack.EMPTY;
            this.entityData.set(DATA_TUSK_TIER, 0);
        }
    }

    /** Snapshot used to restore this elephant later from a filled Pet Amulet. */
    private CompoundTag buildAmuletTag(UUID owner) {
        CompoundTag tag = new CompoundTag();
        tag.putString("ElephantVariant", getVariant().name());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Pet Amulet capture: instant, no vanish animation. All equipment drops on the ground, not saved. */
    private void capturePetInstant(Player player, InteractionHand hand) {
        dropAllEquipment();
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }
    
    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

    private float lastAppliedScale = -1F;

    private void tickGrowth() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float newScale = getGrowthFraction() * (float) getVariant().getRenderScale();
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

    @Override
    protected AABB makeBoundingBox() {
        if (this.isBaby()) {
            EntityDimensions babyDimensions =
                    this.getType().getDimensions().scale(BABY_HITBOX_SCALE);
            return babyDimensions.makeBoundingBox(this.position());
        }
        return super.makeBoundingBox();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isBaby() && source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return !this.isBaby() && super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        // Only the FIRST rider steers — the platform's second seat is along for the ride only.
        if (hasHarness() && this.getFirstPassenger() instanceof Player player && this.hasPassenger(player)) {
            return player;
        }
        return null;
    }

    /** Only the platform allows a 2nd passenger; without it, a normal harness seats just one. */
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        int maxPassengers = hasPlatform() ? 2 : 1;
        return this.getPassengers().size() < maxPassengers;
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        int seatIndex = this.getPassengers().indexOf(passenger);
        float seatForward = RIDER_FORWARD + (seatIndex == 1 ? PLATFORM_BACK_SEAT_FORWARD : 0F);
        float yaw = this.getYRot() * ((float) Math.PI / 180F);
        double x = this.getX() - Math.sin(yaw) * seatForward;
        double z = this.getZ() + Math.cos(yaw) * seatForward;
        double y = this.getY() + RIDER_HEIGHT * this.getScale()
                + (getVariant() == ElephantVariant.MAMMOTH_SONGHUA ? SONGHUA_RIDER_HEIGHT_BONUS : 0F);
        moveFunction.accept(passenger, x, y, z);
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.5D, 0.0D, player.zza);
    }

    @Override
    public boolean canJump() {
        return hasHarness() && this.isVehicle();
    }

    /**
     * Applies the jump the instant the rider presses space — no charge bar like
     * horses. Gated on onGround() so the repeated calls vanilla sends every tick
     * the key is held don't re-launch it mid-air.
     */
    @Override
    public void onPlayerJump(int jumpPower) {
        if (jumpPower > 0 && (this.onGround() || this.isInWater() || this.isInLava())) {
            double jumpVelocity = getVariant().getJumpVelocity();
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, jumpVelocity, motion.z);
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

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
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
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
        this.setVariant(ElephantVariant.valueOf(tag.getString("ElephantVariant")));
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
