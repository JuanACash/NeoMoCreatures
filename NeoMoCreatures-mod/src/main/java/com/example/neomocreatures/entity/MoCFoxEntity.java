package com.example.neomocreatures.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModSounds;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * 1:1 behavioural port of drzhark.mocreatures.entity.hunter.MoCEntityFox,
 * following the wiki: neutral (attacks only if provoked, or hunts mobs
 * smaller than itself), squeaks when hurt/killed, cubs are passive and
 * follow adults until they mature. Adults defending a nearby cub and cubs
 * fleeing when hurt are a deliberate addition beyond the wiki's own
 * "parents do not protect the cub(s)" note.
 * <p>
 * Extends {@link TamableAnimal} purely so a later taming pass does not
 * require re-registering the entity type or migrating saved data — taming
 * itself is intentionally NOT implemented yet.
 */
public class MoCFoxEntity extends TamableAnimal implements GrowthScaled {

    /** Wiki: "It takes at least one full Minecraft 'day' or more for fox cubs to mature." */
    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;

    private static final EntityDataAccessor<Boolean> DATA_SNOW =
            SynchedEntityData.defineId(MoCFoxEntity.class, EntityDataSerializers.BOOLEAN);

    public MoCFoxEntity(EntityType<? extends MoCFoxEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SNOW, false);
    }

    public boolean isSnow() {
        return this.entityData.get(DATA_SNOW);
    }

    public void setSnow(boolean snow) {
        this.entityData.set(DATA_SNOW, snow);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FoxCubPanicGoal(this, 1.5D));
        // Wiki: "Fox cubs are passive, and will flee from players" — adults are
        // merely neutral and don't flee on sight, only cubs do.
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D,
                (Predicate<LivingEntity>) livingEntity -> this.isBaby() && !this.isTame()));
        // Wiki: "will flee from players and follow adult foxes" — vanilla's
        // generic "follow nearest adult of my own class" goal fits as-is.
        this.goalSelector.addGoal(3, new FoxFollowParentGoal(this));
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Deliberate addition beyond the wiki: wild foxes back each other up
        // in a fight, wolf-pack style (any nearby fox, not just cubs).
        this.targetSelector.addGoal(1, new FoxDefendFoxGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        // Wiki: "give chase and hunt down insects, kitties, birds or any other
        // mob smaller than them" — the original had this exact goal commented
        // out; the wiki describes it as real behaviour, so it's enabled here.
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true, this::canHuntTarget));
    }

    private boolean canHuntTarget(@Nullable LivingEntity target) {
        // Excludes both this mod's own foxes AND vanilla's net.minecraft...Fox —
        // "hunt anything smaller than them" was never meant to include other foxes.
        // Wiki: "Tamed foxes stop attacking other mobs too."
        if (this.isBaby() || this.isTame() || target instanceof MoCFoxEntity
                || target instanceof net.minecraft.world.entity.animal.Fox) {
            return false;
        }
        return target.getBbWidth() <= 0.7F && target.getBbHeight() <= 0.7F;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        // Wiki: fox cubs never fight back — they flee instead (PanicGoal above).
        if (this.isBaby()) {
            return;
        }
        // Wiki: "Once tamed, foxes will not attack the player." Retaliating
        // against a non-player attacker (e.g. a zombie) is still allowed —
        // this only exempts players specifically. This single choke point
        // covers every path that could set a target (HurtByTargetGoal,
        // FoxDefendFoxGoal, the small-animal hunting goal), since they all
        // call setTarget() rather than touching the target field directly.
        if (this.isTame() && target instanceof Player) {
            return;
        }
        super.setTarget(target);
    }

    private static class FoxCubPanicGoal extends PanicGoal {
        private final MoCFoxEntity fox;

        FoxCubPanicGoal(MoCFoxEntity fox, double speedModifier) {
            super(fox, speedModifier);
            this.fox = fox;
        }

        @Override
        public boolean canUse() {
            return this.fox.isBaby() && super.canUse();
        }
    }

    /**
     * Scans for a nearby fox (any age — this subsumes the old "defend cubs
     * only" behaviour, since cubs are just a subset of "nearby fox") that
     * was just attacked, and joins the fight against whoever did it — the
     * same instinct vanilla wolves have for their pack. Wild foxes only:
     * a tamed fox would presumably defend its owner instead of joining
     * random fox fights, once taming exists.
     */
    private static class FoxDefendFoxGoal extends Goal {
        private final MoCFoxEntity self;

        FoxDefendFoxGoal(MoCFoxEntity self) {
            this.self = self;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (this.self.isBaby() || this.self.isTame() || this.self.getTarget() != null) {
                return false;
            }
            for (MoCFoxEntity other : findNearbyFoxes()) {
                LivingEntity threat = other.getLastHurtByMob();
                if (threat != null && threat.isAlive() && threat != this.self
                        && other.tickCount - other.getLastHurtByMobTimestamp() < 100) {
                    this.self.setTarget(threat);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.self.getTarget();
            return target != null && target.isAlive();
        }

        private List<MoCFoxEntity> findNearbyFoxes() {
            return this.self.level().getEntitiesOfClass(MoCFoxEntity.class,
                    this.self.getBoundingBox().inflate(10.0D), fox -> fox != this.self);
        }
    }

    private static class FoxFollowParentGoal extends Goal {
        private static final double SPEED_MODIFIER = 1.1D;
        private static final double FOLLOW_RANGE = 8.0D;
        private static final double STOP_DISTANCE_SQR = 9.0D;

        private final MoCFoxEntity cub;
        @Nullable
        private MoCFoxEntity parent;

        FoxFollowParentGoal(MoCFoxEntity cub) {
            this.cub = cub;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.cub.isBaby()) {
                return false;
            }
            List<MoCFoxEntity> candidates = this.cub.level().getEntitiesOfClass(MoCFoxEntity.class,
                    this.cub.getBoundingBox().inflate(FOLLOW_RANGE),
                    fox -> !fox.isBaby() && (!this.cub.isTame() || fox.isTame()));
            if (candidates.isEmpty()) {
                return false;
            }
            this.parent = candidates.get(this.cub.getRandom().nextInt(candidates.size()));
            return this.cub.distanceToSqr(this.parent) >= STOP_DISTANCE_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cub.isBaby() && this.parent != null && this.parent.isAlive()
                    && this.cub.distanceToSqr(this.parent) >= STOP_DISTANCE_SQR
                    && (!this.cub.isTame() || this.parent.isTame());
        }

        @Override
        public void start() {
            this.cub.getNavigation().moveTo(this.parent, SPEED_MODIFIER);
        }

        @Override
        public void stop() {
            this.parent = null;
        }

        @Override
        public void tick() {
            if (this.parent != null) {
                this.cub.getNavigation().moveTo(this.parent, SPEED_MODIFIER);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FOX_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FOX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FOX_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.3F;
    }

/*  @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.86F;
    }*/

    private float lastAppliedScale = -1F;

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 1.0F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 1.0F);
    }

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

    @Override
    public void updateGrowthScale() {
        this.tickGrowth();
    }

    /** Carries the whole group's species to every member, same fix MoCBearEntity uses for its cub groups. */
    private static final class FoxGroupData implements SpawnGroupData {
        final boolean snow;
        FoxGroupData(boolean snow) {
            this.snow = snow;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            boolean snow = spawnGroupData instanceof FoxGroupData shared
                    ? shared.snow
                    : pickSnowForBiome(level, this.blockPosition());
            setSnow(snow);

            // Wiki: "Cubs may also spawn with adults."
            if (this.random.nextInt(4) == 0) {
                this.setAge(-GROWTH_TICKS);
            }

            // Never forward our own custom SpawnGroupData into AgeableMob's
            // finalizeSpawn — same fix as MoCBearEntity: it converts the data
            // without checking the type and crashes with anything else.
            super.finalizeSpawn(level, difficulty, spawnType, null);
            return new FoxGroupData(snow);
        }

        if (spawnType == MobSpawnType.SPAWN_EGG) {
            // Also covers "use the spawn egg on an adult fox" — vanilla spawns a
            // fresh baby independently of the adult it was used on, same as any
            // other animal, so it gets its own roll here too.
            setSnow(this.random.nextBoolean());
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private boolean pickSnowForBiome(ServerLevelAccessor level, net.minecraft.core.BlockPos pos) {
        var biome = level.getBiome(pos);

        if (biome.is(net.minecraft.world.level.biome.Biomes.GROVE)
                || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_TAIGA)) {
            return true;
        }
        if (biome.is(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_PINE_TAIGA)
                || biome.is(net.minecraft.world.level.biome.Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                || biome.is(net.minecraft.world.level.biome.Biomes.TAIGA)) {
            return false;
        }

        // Safety net for a biome not explicitly listed (e.g. a datapack biome
        // reusing one of these spawners) — decide by climate instead of
        // defaulting silently.
        return biome.value().getBaseTemperature() <= 0.15F;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Snow", isSnow());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Snow")) {
            setSnow(tag.getBoolean("Snow"));
        }
    }


    private boolean isTamingFood(ItemStack stack) {
        return stack.is(com.example.neomocreatures.init.ModItems.TURKEY_RAW.get());
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(com.example.neomocreatures.init.ModItems.TURKEY_RAW.get())
                || stack.is(com.example.neomocreatures.init.ModItems.RAT_RAW.get());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.isTame() && stack.is(net.minecraft.world.item.Items.SWEET_BERRIES);
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Same rename-with-a-book convention used by every other tameable
        // mob in the mod (e.g. MoCKomodoDragonEntity#mobInteract).
        if (this.isTame() && this.isOwnedBy(player) && stack.is(net.minecraft.world.item.Items.BOOK)) {
            if (!this.level().isClientSide) {
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (!this.isTame() && isTamingFood(stack)) {
            if (!this.level().isClientSide) {
                this.tame(player);
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                this.setHealth(this.getMaxHealth());
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.setHealth(this.getMaxHealth());
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MoCFoxEntity cub = com.example.neomocreatures.init.ModEntities.MOC_FOX.get().create(level);
        if (cub == null) {
            return null;
        }

        // Wiki: normal x normal -> normal cub, snow x snow -> snow cub,
        // normal x snow -> 50/50.
        boolean otherSnow = otherParent instanceof MoCFoxEntity otherFox && otherFox.isSnow();
        boolean cubSnow = (this.isSnow() == otherSnow) ? this.isSnow() : this.random.nextBoolean();
        cub.setSnow(cubSnow);

        // Wiki: "will be born tamed" — same naming-prompt convention used
        // elsewhere in the mod (e.g. MoCKomodoDragonEntity#onHatchedFromEgg).
        // Breeding already requires both parents to be tamed for BreedGoal
        // to have fired at all, so this always finds an owner in practice.
        LivingEntity owner = this.getOwner();
        if (owner == null && otherParent instanceof TamableAnimal otherTame) {
            owner = otherTame.getOwner();
        }
        if (owner instanceof Player player) {
            cub.tame(player);
            com.example.neomocreatures.util.NamingHelper.promptRename(cub, player.getUUID());
        }

        return cub;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING),
                    attacker);
        }

        int fur = Math.min(this.random.nextInt(3) + lootingLevel, 5); // 0-2 base, +1 per Looting level
        if (fur > 0) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.FUR.get(), fur));
        }
    }

        /** Builds the NBT payload stored inside a filled Pet Amulet for this fox. */
    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Fox", true);
        tag.putBoolean("Snow", isSnow());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Captures this tamed fox into a Pet Amulet and removes it from the world. */
    private void capturePetInstant(Player player, net.minecraft.world.InteractionHand hand) {
        net.minecraft.nbt.CompoundTag tag = buildAmuletTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.PET_AMULET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3); // 1-3
    }
}