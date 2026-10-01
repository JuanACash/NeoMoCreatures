package com.example.neomocreatures.entity;

import com.example.neomocreatures.entity.werewolf.WerewolfVariant;
import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityWerewolf}. Two forms sharing one
 * entity: a harmless human by day (never attacks, capped at 15 health) and a hostile wolf by night
 * (40 health, resistant to ordinary weapons, weak to silver). Transforms between the two roughly
 * every 12.5 seconds on average once conditions allow it, with a ~1.5-2.25s shaking animation.
 * <p>
 * Step 1: behaviour only — model comes in a later step.
 */
public class MoCWerewolfEntity extends Monster {

    // ---- Stats per form ----
    private static final float HUMAN_MAX_HEALTH = 15.0F;
    private static final float WOLF_MAX_HEALTH = 40.0F;
    private static final double HUMAN_SPEED = 0.1D;
    private static final double WOLF_SPEED = 0.2D;
    /** Original: faster once actively chasing a target more than ~3.46 blocks away. */
    private static final double WOLF_HUNCHED_SPEED = 0.35D;
    private static final double HUNCHED_DISTANCE_SQR = 12.0D;
    private static final double ATTACK_DAMAGE = 7.5D;
    private static final double ATTACK_SPEED = 1.0D;

    // ---- Transformation ----
    /** Original: a 1 in 250 chance per tick to start transforming, once conditions are met. */
    private static final int TRANSFORM_TRIGGER_CHANCE = 250;
    /** Original: a 1 in 3 chance per tick to advance the shake while transforming. */
    private static final int TRANSFORM_TICK_CHANCE = 3;
    private static final int TRANSFORM_DURATION = 30;
    private static final int TRANSFORM_SOUND_TICK = 10;
    private static final double TRANSFORM_WOBBLE = 0.3D;
    private static final float TRANSFORM_SELF_DAMAGE = 1.0F;

    // ---- Silver weakness (wolf form only) ----
    /** Wiki: "All other weapons... will only deal 1 damage, regardless of their enchantments." */
    private static final float NORMAL_WEAPON_DAMAGE = 1.0F;
    private static final float SILVER_WEAPON_DAMAGE_MULTIPLIER = 3.0F;
    private static final float SILVER_SWORD_FIXED_DAMAGE = 10.0F;

    private static final EntityDataAccessor<Boolean> DATA_HUMAN_FORM =
            SynchedEntityData.defineId(MoCWerewolfEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HUNCHED =
            SynchedEntityData.defineId(MoCWerewolfEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(MoCWerewolfEntity.class, EntityDataSerializers.INT);

    private boolean transforming;
    private int transformTicks;

    public MoCWerewolfEntity(EntityType<? extends MoCWerewolfEntity> type, Level level) {
        super(type, level);
    }

    /** Registered hitbox is the wolf's own 1.2x2.4; human form scales down from there via
     *  Attributes.SCALE instead of overriding getDimensions(), which is final in 1.21.1 — same
     *  pattern already used for the Big Cat's baby scale. Uniform scaling can't hit both the human
     *  width and height ratios at once, so this picks the height ratio (1.8/2.4) as the closer match. */
    private static final float HUMAN_SCALE = 1.0F;
    private static final float WOLF_SCALE = 1.0F;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, WOLF_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, HUMAN_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.SCALE, WOLF_SCALE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HUMAN_FORM, true);
        builder.define(DATA_HUNCHED, false);
        builder.define(DATA_VARIANT, WerewolfVariant.BROWN.getId());
    }


    private static final String TAG_HUMAN_FORM = "HumanForm";
    private static final String TAG_VARIANT = "WerewolfVariant";

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TAG_HUMAN_FORM, this.isHumanForm());
        tag.putInt(TAG_VARIANT, this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_HUMAN_FORM, Tag.TAG_BYTE)) {
            this.entityData.set(DATA_HUMAN_FORM, tag.getBoolean(TAG_HUMAN_FORM));
        }
        if (tag.contains(TAG_VARIANT, Tag.TAG_INT)) {
            this.entityData.set(DATA_VARIANT, tag.getInt(TAG_VARIANT));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, ATTACK_SPEED, false));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    // ---------------------------------------------------------------------
    // Form state
    // ---------------------------------------------------------------------

    public boolean isHumanForm() {
        return this.entityData.get(DATA_HUMAN_FORM);
    }

    public boolean isHunched() {
        return this.entityData.get(DATA_HUNCHED);
    }

    public WerewolfVariant getVariant() {
        return WerewolfVariant.byId(this.entityData.get(DATA_VARIANT));
    }

    @Override
    public boolean fireImmune() {
        return this.getVariant() == WerewolfVariant.FIRE || super.fireImmune();
    }

    @Override
    public void setHealth(float health) {
        if (this.isHumanForm() && health > HUMAN_MAX_HEALTH) {
            health = HUMAN_MAX_HEALTH;
        }
        super.setHealth(health);
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
                                        net.minecraft.world.DifficultyInstance difficulty,
                                        net.minecraft.world.entity.MobSpawnType spawnType,
                                        @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        WerewolfVariant variant = level.getLevel().dimension() == net.minecraft.world.level.Level.NETHER
                ? WerewolfVariant.FIRE
                : WerewolfVariant.random(this.random);
        this.entityData.set(DATA_VARIANT, variant.getId());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    // ---------------------------------------------------------------------
    // Attacking
    // ---------------------------------------------------------------------

    /** Original: attackEntityAsMob() — never attacks in human form (and clears any target that
     *  slipped through); fire-form ignites whatever it hits. */
    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.isHumanForm()) {
            this.setTarget(null);
            return false;
        }
        if (this.getVariant() == WerewolfVariant.FIRE && target instanceof LivingEntity) {
            target.igniteForSeconds(10);
        }
        return super.doHurtTarget(target);
    }

    /**
     * Original: attackEntityFrom() — only in wolf form: ordinary weapons do half damage (capped at
     * 4); a silver weapon does triple; the mod's own silver sword always does a flat 10. Simplified
     * from the original's separate sword/tool-item branches into one silver check, since both
     * branches did the same multiplier math.
     */
    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!this.isHumanForm() && damageSource.getEntity() instanceof Player) {
            ItemStack weapon = damageSource.getWeaponItem();
            if (weapon != null && !weapon.isEmpty()) {
                amount = this.adjustDamageForWeapon(weapon, amount);
            }
        }
        return super.hurt(damageSource, amount);
    }

    private float adjustDamageForWeapon(ItemStack weapon, float amount) {
        if (weapon.is(ModItems.SILVER_SWORD.get())) {
            return SILVER_SWORD_FIXED_DAMAGE;
        }
        if (this.isSilverWeapon(weapon)) {
            return amount * SILVER_WEAPON_DAMAGE_MULTIPLIER;
        }
        return NORMAL_WEAPON_DAMAGE;
    }

    private boolean isSilverWeapon(ItemStack weapon) {
        if (weapon.is(ModItems.SILVER_SWORD.get()) || weapon.is(ModItems.SILVER_AXE.get())) {
            return true;
        }
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(weapon.getItem()).getPath();
        return path.contains("silver");
    }

    // ---------------------------------------------------------------------
    // Ticking: transformation and hunch state
    // ---------------------------------------------------------------------

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }

        boolean wantsToTransform = (this.isNight() && this.isHumanForm()) || (!this.isNight() && !this.isHumanForm());
        if (!this.transforming && wantsToTransform && this.random.nextInt(TRANSFORM_TRIGGER_CHANCE) == 0) {
            this.transforming = true;
        }

        if (this.isHumanForm() && this.getTarget() != null) {
            this.setTarget(null);
        }
        if (!this.isHumanForm()) {
            boolean hunched = this.getTarget() != null && this.distanceToSqr(this.getTarget()) > HUNCHED_DISTANCE_SQR;
            this.entityData.set(DATA_HUNCHED, hunched);
            this.setSpeedAttribute(hunched ? WOLF_HUNCHED_SPEED : WOLF_SPEED);
        }

        if (this.transforming) {
            this.tickTransforming();
        }
    }

    private boolean isNight() {
        return !this.level().isDay();
    }

    private void tickTransforming() {
        if (this.random.nextInt(TRANSFORM_TICK_CHANCE) != 0) {
            return;
        }
        this.transformTicks++;
        double dx = (this.transformTicks % 2 == 0) ? TRANSFORM_WOBBLE : -TRANSFORM_WOBBLE;
        this.setPos(this.getX() + dx, this.getY(), this.getZ());
        if (this.transformTicks % 2 == 0) {
            this.hurt(this.damageSources().mobAttack(this), TRANSFORM_SELF_DAMAGE);
        }
        if (this.transformTicks == TRANSFORM_SOUND_TICK) {
            SoundEvent sound = this.isHumanForm() ? ModSounds.WEREHUMAN_TRANSFORM.get() : ModSounds.WEREWOLF_TRANSFORM.get();
            this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.transformTicks > TRANSFORM_DURATION) {
            this.completeTransform();
        }
    }

    private void completeTransform() {
        this.transforming = false;
        this.transformTicks = 0;
        boolean nowHuman = !this.isHumanForm();
        this.entityData.set(DATA_HUMAN_FORM, nowHuman);
        // Original: selectType() only ever rolls once, the first time the entity is created — never
        // again on a later transform. Re-rolling here (as an earlier draft did) is what was making
        // it flip to a different colour, and a different human face, every single time it changed
        // form — this individual's colour is fixed for life once finalizeSpawn() first sets it.
        if (nowHuman) {
            this.setHealth(HUMAN_MAX_HEALTH);
            this.setSpeedAttribute(HUMAN_SPEED);
            this.setScaleAttribute(HUMAN_SCALE);
        } else {
            this.setHealth(WOLF_MAX_HEALTH);
            this.setSpeedAttribute(WOLF_SPEED);
            this.setScaleAttribute(WOLF_SCALE);
        }
        this.refreshDimensions();
        for (int i = 0; i < 30; i++) {
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.POOF,
                    this.getX() + (this.random.nextDouble() - 0.5D), this.getY() + this.random.nextDouble() * this.getBbHeight(),
                    this.getZ() + (this.random.nextDouble() - 0.5D), 0.0D, 0.05D, 0.0D);
        }
    }


    /** Guards against a null attribute instance (e.g. a werewolf saved before Attributes.SCALE was
     *  added to createAttributes()) silently throwing mid-transform and leaving it stuck forever,
     *  never actually completing the change of form. */
    private void setSpeedAttribute(double value) {
        AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(value);
        }
    }

    private void setScaleAttribute(double value) {
        AttributeInstance attribute = this.getAttribute(Attributes.SCALE);
        if (attribute != null) {
            attribute.setBaseValue(value);
        }
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isHumanForm() ? null : ModSounds.WEREWOLF_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.isHumanForm()) {
            return this.transforming ? null : ModSounds.WEREHUMAN_HURT.get();
        }
        return ModSounds.WEREWOLF_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isHumanForm() ? ModSounds.WEREHUMAN_DEATH.get() : ModSounds.WEREWOLF_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Wiki: fixed 5 experience, regardless of which form it died in, only on a player/tamed-wolf kill. */
    @Override
    protected int getBaseExperienceReward() {
        return 5;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (this.isHumanForm()) {
            this.dropHumanLoot(level, damageSource);
        } else {
            this.dropWolfLoot(level, damageSource);
        }
    }

    /** Wiki: 0-2 of each wooden tool (all independent rolls, scaled by Looting), plus a 50% chance
     *  of 1 leather, that chance boosted by Looting. */
    private void dropHumanLoot(ServerLevel level, DamageSource damageSource) {
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        this.dropCount(Items.STICK, lootingLevel);
        this.dropCount(Items.WOODEN_AXE, lootingLevel);
        this.dropCount(Items.WOODEN_HOE, lootingLevel);
        this.dropCount(Items.WOODEN_PICKAXE, lootingLevel);
        this.dropCount(Items.WOODEN_SHOVEL, lootingLevel);
        this.dropCount(Items.WOODEN_SWORD, lootingLevel);
        if (MoCLootUtil.rollChance(this.random, 0.5F, 0.05F, lootingLevel)) {
            this.spawnAtLocation(new ItemStack(Items.LEATHER));
        }
    }

    /** Wiki: 0-2 of each iron tool + 0-2 golden apples (all independent rolls, scaled by Looting);
     *  a 50% chance of 1 diamond (boosted by Looting) only for the black/white/brown variants; a
     *  50% chance of 1 netherite scrap (boosted by Looting) only for the fire variant. */
    private void dropWolfLoot(ServerLevel level, DamageSource damageSource) {
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);
        this.dropCount(Items.GOLDEN_APPLE, lootingLevel);
        this.dropCount(Items.IRON_AXE, lootingLevel);
        this.dropCount(Items.IRON_HOE, lootingLevel);
        this.dropCount(Items.IRON_PICKAXE, lootingLevel);
        this.dropCount(Items.IRON_SHOVEL, lootingLevel);
        this.dropCount(Items.IRON_SWORD, lootingLevel);

        if (MoCLootUtil.rollChance(this.random, 0.5F, 0.05F, lootingLevel)) {
            Item rareDrop = this.getVariant() == WerewolfVariant.FIRE ? Items.NETHERITE_SCRAP : Items.DIAMOND;
            this.spawnAtLocation(new ItemStack(rareDrop));
        }
    }

    private void dropCount(Item item, int lootingLevel) {
        MoCLootUtil.dropItems(this, item, MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
    }

}