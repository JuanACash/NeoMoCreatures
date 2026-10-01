package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCExperienceUtil;
import com.example.neomocreatures.util.MoCLootUtil;
import com.example.neomocreatures.util.NamingHelper;
import com.example.neomocreatures.util.PetStorageUtil;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 1:1 behavioural port of drzhark.mocreatures.entity.hunter.MoCEntityRaccoon,
 * following the wiki: neutral (attacks only if provoked, or attacked first),
 * wanders and chirps, avoids water/cliffs (default ground-navigation
 * behaviour already does this). The wiki explicitly notes hunting smaller
 * mobs is "Not added yet" — matching the original, where that target goal
 * is commented out — so it's deliberately left out here too, unlike Fox.
 * <p>
 * Cub growth/follow-parent behaviour ported the same way as MoCFoxEntity,
 * even though this specific wiki excerpt doesn't describe cubs — the
 * original code clearly has that infrastructure (age, EntityAIFollowAdult).
 * <p>
 * Extends {@link TamableAnimal} purely so a later taming pass does not
 * require re-registering the entity type or migrating saved data — taming
 * itself is intentionally NOT implemented yet.
 */
public class MoCRaccoonEntity extends TamableAnimal implements GrowthScaled, StorablePet {

    private static final int GROWTH_TICKS = 24000;
    private static final float BABY_SCALE = 0.5F;

    public MoCRaccoonEntity(EntityType<? extends MoCRaccoonEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.SCALE, 0.7D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RaccoonCubPanicGoal(this, 1.0D));
        // Cubs flee from players; adults are merely neutral and don't flee on sight.
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.0D, 1.2D,
                (Predicate<LivingEntity>) livingEntity -> this.isBaby()));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Wiki: "Raccoons will also attack smaller mobs such as insects and
        // kitties." Same pattern as MoCFoxEntity's own hunting goal.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, true, this::canHuntTarget));
    }

    private boolean canHuntTarget(@Nullable LivingEntity target) {
        if (this.isBaby() || this.isTame() || target instanceof MoCRaccoonEntity) {
            return false;
        }
        return target.getBbWidth() <= 0.7F && target.getBbHeight() <= 0.7F;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.isBaby() || this.isTame()) {
            return;
        }
        super.setTarget(target);
    }

    private static class RaccoonCubPanicGoal extends PanicGoal {
        private final MoCRaccoonEntity raccoon;

        RaccoonCubPanicGoal(MoCRaccoonEntity raccoon, double speedModifier) {
            super(raccoon, speedModifier);
            this.raccoon = raccoon;
        }

        @Override
        public boolean canUse() {
            return this.raccoon.isBaby() && super.canUse();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.RACCOON_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.RACCOON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.RACCOON_DEATH.get();
    }

    private float lastAppliedScale = -1F;

    @Override
    public void aiStep() {
        super.aiStep();
        tickGrowth();
    }

    private float getGrowthFraction() {
        if (!this.isBaby()) {
            return 0.7F;
        }
        float progress = Mth.clamp((this.getAge() + GROWTH_TICKS) / (float) GROWTH_TICKS, 0.0F, 1.0F);
        return Mth.lerp(progress, BABY_SCALE, 0.7F);
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

    /** Wiki: "tamed by giving them any edible item (including... vanilla and
     *  mod edible items)... including rotten flesh... and golden apples." A
     *  plain FOOD-component check covers any of those generically, modded
     *  included, without hardcoding a list. */
    private boolean isTamingItem(ItemStack stack) {
        return stack.has(DataComponents.FOOD);
    }

    @Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType spawnType,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        if (spawnType == net.minecraft.world.entity.MobSpawnType.NATURAL
                || spawnType == net.minecraft.world.entity.MobSpawnType.CHUNK_GENERATION) {
            // Wiki: "Cubs may also spawn with adults." No per-species variant to
            // share across the group (unlike Fox/Bear), so no custom
            // SpawnGroupData is needed — just an independent roll per individual.
            if (this.random.nextInt(4) == 0) {
                this.setAge(-GROWTH_TICKS);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // taming isn't food-based breeding/healing — see mobInteract
    }

    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(Items.BOOK)) {
            return NamingHelper.renameWithBook(this, player);
        }

        if (this.isTame() && this.isOwnedBy(player) && stack.is(com.example.neomocreatures.init.ModItems.PET_AMULET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (!this.isTame() && isTamingItem(stack)) {
            if (!this.level().isClientSide) {
                this.tame(player);
                com.example.neomocreatures.util.NamingHelper.promptRename(this, player.getUUID());
                this.playSound(com.example.neomocreatures.init.ModSounds.HORSE_EATING.get(), 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.SUCCESS;
        }


        if (this.isTame() && this.isOwnedBy(player) && isTamingItem(stack) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(this.getMaxHealth());
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
        return null; // no breeding — nothing in the wiki excerpt describes any
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        // Wiki: "0-2 fur... +1 per level of looting... 1 to 5 fur with Looting III."
        MoCLootUtil.dropItems(this, com.example.neomocreatures.init.ModItems.FUR.get(), MoCLootUtil.rollWithFlatLooting(this.random, 3, lootingLevel, 5));
    }

    /** Builds the NBT payload stored inside a filled Pet Amulet for this raccoon. */
    private net.minecraft.nbt.CompoundTag buildAmuletTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Raccoon", true);
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putInt("Age", this.getAge());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    /** Captures this tamed raccoon into a Pet Amulet and removes it from the world. */
    private void capturePetInstant(Player player, net.minecraft.world.InteractionHand hand) {
        PetStorageUtil.storeReplacingHeldItem(player, hand, this, ModItems.PET_AMULET_FULL.get(), buildAmuletTag(player.getUUID()));
    }

    @Override
    protected int getBaseExperienceReward() {
        return MoCExperienceUtil.rollStandardXp(this.random);
    }

    // ---------------------------------------------------------------------
    // Pet Amulet / Fish Net storage
    // ---------------------------------------------------------------------

    /** Restores the data saved by {@link #buildAmuletTag} when a Pet Amulet releases this pet. */
    @Override
    public void restoreFromStorage(CompoundTag tag) {
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
            this.setCustomName(net.minecraft.network.chat.Component.literal(tag.getString("Name")));
        }
    }
}
