package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.init.ModSounds;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class MoCTurkeyEntity extends TamableAnimal implements GrowthScaled {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;

    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.MELON_SEEDS);
    private static final Ingredient BREEDING_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);

    private static final EntityDataAccessor<Boolean> DATA_MALE =
            SynchedEntityData.defineId(MoCTurkeyEntity.class, EntityDataSerializers.BOOLEAN);

    public MoCTurkeyEntity(EntityType<? extends MoCTurkeyEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MALE, false);
    }

    public boolean isMale() {
        return this.entityData.get(DATA_MALE);
    }

    public void setMale(boolean male) {
        this.entityData.set(DATA_MALE, male);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));

    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
        }
        tickGrowth();
    }

    private float lastAppliedScale = -1F;

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

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TURKEY_AMBIENT.get();
    }

    @Override
    public void playAmbientSound() {
        // Quieter than the shared getSoundVolume() hook (which also covers
        // hurt/death) — this only turns down the ambient gobble.
        SoundEvent sound = this.getAmbientSound();
        if (sound != null) {
            this.playSound(sound, 0.4F, this.getVoicePitch());
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TURKEY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TURKEY_HURT.get();
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType spawnType,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        setMale(this.random.nextBoolean());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Male", isMale());
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Male")) {
            setMale(tag.getBoolean("Male"));
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.isTame() && BREEDING_ITEMS.test(stack);
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this || !(otherAnimal instanceof MoCTurkeyEntity other)) {
            return false;
        }
        if (!this.isTame() || !other.isTame()) {
            return false;
        }
        if (this.isMale() == other.isMale()) {
            return false; // needs one male and one female
        }
        return super.canMate(otherAnimal);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MoCTurkeyEntity baby = com.example.neomocreatures.init.ModEntities.MOC_TURKEY.get().create(level);
        if (baby != null) {
            baby.setMale(this.random.nextBoolean());
        }
        return baby;
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        MoCTurkeyEntity baby = (MoCTurkeyEntity) this.getBreedOffspring(level, partner);
        if (baby == null) {
            return;
        }
        baby.setBaby(true);
        baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
        level.addFreshEntity(baby);

        this.setAge(6000);
        partner.setAge(6000);
        this.resetLove();
        partner.resetLove();
        level.broadcastEntityEvent(this, (byte) 18);
        if (level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
            level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                    level, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }

        java.util.UUID ownerUUID = this.getOwnerUUID();
        if (ownerUUID != null) {
            baby.setOwnerUUID(ownerUUID);
            baby.setTame(true, true);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
            lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING),
                    attacker);
        }

        net.minecraft.world.item.Item turkeyItem = this.isOnFire()
                ? com.example.neomocreatures.init.ModItems.TURKEY_COOKED.get()
                : com.example.neomocreatures.init.ModItems.TURKEY_RAW.get();
        int rawTurkey = Math.min(this.random.nextInt(3) + lootingLevel, 5);
        if (rawTurkey > 0) {
            this.spawnAtLocation(new ItemStack(turkeyItem, rawTurkey));
        }

        int feathers = Math.min(this.random.nextInt(3) + lootingLevel, 5);
        if (feathers > 0) {
            this.spawnAtLocation(new ItemStack(Items.FEATHER, feathers));
        }
    }

    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Turkey", true);
        tag.putBoolean("Male", isMale());
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

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
        return 1 + this.random.nextInt(3); 
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
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

        if (!this.isTame() && stack.is(Items.MELON_SEEDS)) {
            if (!this.level().isClientSide) {
                this.tame(player);
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.PUMPKIN_SEEDS) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(this.getMaxHealth());
                this.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }
}