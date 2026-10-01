package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import com.example.neomocreatures.entity.egg.EggHatchable;
import com.example.neomocreatures.util.NamingHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Port of {@code drzhark.mocreatures.entity.aquatic.MoCEntityShark}. Unlike
 * the older land-based tameables, the original extends its own
 * {@code MoCEntityTameableAquatic} base — a completely separate,
 * pre-{@code TamableAnimal} ownership/breeding framework (its own network
 * packets, its own pet-data tracking). Porting that architecture wholesale
 * would duplicate everything {@link TamableAnimal} + {@link CarriedPet} +
 * our own pet-amulet system already do consistently across every other
 * entity in this mod, so Shark is built on our usual base instead — only
 * the shark-specific behaviour (this file) is a faithful port.
 * <p>
 * Step 1 only: skeleton, swimming, and hunting behaviour. Intentionally NOT
 * implemented yet (step 2): taming and riding it directly (no saddle needed,
 * same as the original), healing. Dolphin-hunting is also not ported yet —
 * there's no MoCDolphinEntity in this project to target.
 */
public class MoCSharkEntity extends TamableAnimal implements EggHatchable, GrowthScaled {

    private static final net.minecraft.resources.ResourceLocation ATTACK_SPEED_MODIFIER_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    com.example.neomocreatures.NeoMoCreatures.MODID, "shark_attack_speed_boost");
    private static final double ATTACK_SPEED_BOOST = 1.0D;
    /** Wiki: "suffocate and die when out of water" — ticks up while not in water, resets while in it. */
    private int outOfWaterTicks;
    /** Not in the original — the user's own addition, since baby sharks otherwise render the same size as adults. */
    private static final float BABY_SCALE = 0.5F;
    private float lastAppliedScale = -1F;

    public MoCSharkEntity(EntityType<? extends MoCSharkEntity> type, Level level) {
        super(type, level);
        // Original's own SharkMoveControl: smooths turning/speed and adds a
        // small buoyant nudge upward while in water.
        this.moveControl = new SharkMoveControl(this);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

        
    /**
     * Mob.checkSpawnObstruction() rejects any position with liquid inside the hitbox.
     * The original extended WaterAnimal, which overrides it to allow water; as a
     * TamableAnimal, natural spawning in the ocean would always fail without this.
     */
    @Override
    public boolean checkSpawnObstruction(net.minecraft.world.level.LevelReader level) {
        return level.isUnobstructed(this);
    }

    /**
     * Like vanilla water animals: no preference for light. Animal's version only accepts bright spots,
     * so it barely spawned at night or in deep, dark water.
     */
    @Override
    public float getWalkTargetValue(net.minecraft.core.BlockPos pos, net.minecraft.world.level.LevelReader level) {
        return 0.0F;
    }

    /** Animal.removeWhenFarAway() returns false: without this, wild sharks would never despawn. */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame() && !this.isPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Original: hunts players either swimming or within 16 blocks, and
        // squid, but only while wild and not on peaceful.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false,
                player -> player != null && !this.isTame() && isReadyToHunt()
                        && (player.isInWater() || this.distanceTo(player) < 16.0F)));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Squid.class, 5, false, false,
                squid -> squid != null && !this.isTame() && isReadyToHunt()));
    }

    private boolean isReadyToHunt() {
        return this.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SCALE, 1.0D);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type) {
        // NeoForge's replacement for the now-final canBreatheUnderwater().
        if (type == net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value()) {
            return false;
        }
        return super.canDrownInFluidType(type);
    }

    // Original's isNotScared() always returning true is achieved here
    // simply by never registering a flee/avoid-entity goal at all.

    @Override
    public void aiStep() {
        super.aiStep();
        tickBabyScale();
        if (!this.level().isClientSide) {
            tickOutOfWaterSuffocation();
            tickAttackSpeedBoost();
        }
    }

    /** Not in the original — just makes a baby (freshly hatched) shark render smaller, as requested. */
    private void tickBabyScale() {
        AttributeInstance scaleAttr = this.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!this.level().isClientSide) {
            float target = this.isBaby() ? BABY_SCALE : 1.0F;
            if (scaleAttr.getBaseValue() != target) {
                scaleAttr.setBaseValue(target);
            }
        }
        float current = (float) scaleAttr.getValue();
        if (this.lastAppliedScale != current) {
            this.lastAppliedScale = current;
            this.refreshDimensions();
        }
    }

    @Override
    public void updateGrowthScale() {
        this.tickBabyScale();
    }

    /** Wiki: "like other mobs that live in water, sharks will suffocate and die when out of water." */
    private void tickOutOfWaterSuffocation() {
        if (this.isInWaterOrBubble()) {
            this.outOfWaterTicks = 0;
            return;
        }
        this.outOfWaterTicks++;
        if (this.outOfWaterTicks > 300 && this.outOfWaterTicks % 20 == 0) {
            this.hurt(this.damageSources().drown(), 2.0F);
        }
    }

    /** The user's own addition, not in the original: swims faster while actively chasing/attacking a target. */
    private void tickAttackSpeedBoost() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        boolean attacking = this.getTarget() != null && this.getTarget().isAlive();
        boolean boosted = speed.getModifier(ATTACK_SPEED_MODIFIER_ID) != null;
        if (attacking && !boosted) {
            speed.addTransientModifier(new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID, ATTACK_SPEED_BOOST, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        } else if (!attacking && boosted) {
            speed.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        // Original: never attacks a player riding in a boat.
        if (target instanceof Player player && player.getVehicle() instanceof Boat) {
            return false;
        }
        return super.doHurtTarget(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        // Original: doesn't retaliate against its own rider hitting it by accident.
        if (this.isVehicle() && attacker != null && this.getPassengers().contains(attacker)) {
            return false;
        }
        boolean wasHurt = super.hurt(source, amount);
        if (wasHurt && this.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {
            if (attacker != this && attacker instanceof LivingEntity livingAttacker) {
                this.setTarget(livingAttacker);
            }
        }
        return wasHurt;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Wiki: "sharks cannot be healed by feeding them, as there is no
        // food item that they will accept" — taming is via egg, not food.
        return false;
    }

    @Override
    public void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId) {
        // Wiki: "will hatch into a friendly baby shark" — always tamed on hatch.
        this.setBaby(true);
        this.setHealth(this.getMaxHealth());
        if (tamer != null) {
            this.tame(tamer);
            NamingHelper.promptRename(this, tamer.getUUID());
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame() && this.isOwnedBy(player) && stack.is(net.minecraft.world.item.Items.BOOK)) {
            if (!this.level().isClientSide) {
                NamingHelper.promptRename(this, player.getUUID());
            }
            return InteractionResult.SUCCESS;
        }

        // Wiki: "a tamed shark can be moved with the use of a fish net."
        if (this.isTame() && this.isOwnedBy(player)
                && stack.is(com.example.neomocreatures.init.ModItems.FISH_NET.get())) {
            if (!this.level().isClientSide) {
                capturePetInstant(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private net.minecraft.nbt.CompoundTag buildNetTag(java.util.UUID owner) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putBoolean("Shark", true);
        tag.putFloat("Health", this.getHealth());
        tag.putBoolean("Adult", !this.isBaby());
        tag.putString("Name", this.getCustomName() != null ? this.getCustomName().getString() : "");
        if (owner != null) {
            tag.putUUID("OwnerUUID", owner);
        }
        return tag;
    }

    private void capturePetInstant(Player player, InteractionHand hand) {
        net.minecraft.nbt.CompoundTag tag = buildNetTag(player.getUUID());
        ItemStack filled = new ItemStack(com.example.neomocreatures.init.ModItems.FISH_NET_FULL.get());
        filled.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(hand, filled);
        this.discard();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Original reuses vanilla's guardian hurt/death sounds — the
        // closest existing large-fish-like vanilla creature.
        return SoundEvents.GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GUARDIAN_DEATH;
    }

    // XP (1-3) is awarded via getBaseExperienceReward() below — vanilla
    // already only grants it when killed by a player or a tamed wolf.
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
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

        // Wiki: "0-3 shark teeth (60% chance)... Looting does affect the chance... making it more common."
        double toothChance = Math.min(1.0D, 0.6D + lootingLevel * 0.1D);
        if (this.random.nextDouble() < toothChance) {
            int teeth = this.random.nextInt(4 + lootingLevel);
            if (teeth > 0) {
                this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.SHARK_TEETH.get(), teeth));
            }
        }

        // Wiki: "10% chance to drop an egg... only if difficulty is Easy or higher."
        double eggChance = Math.min(1.0D, 0.10D + lootingLevel * 0.02D);
        if (this.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL
                && this.random.nextDouble() < eggChance) {
            this.spawnAtLocation(new ItemStack(com.example.neomocreatures.init.ModItems.SHARK_EGG.get()));
        }
    }

    /** Original's SharkMoveControl: adds a small buoyant nudge in water on top of the usual smoothed movement toward the target. */
    private static class SharkMoveControl extends MoveControl {
        private final MoCSharkEntity shark;

        SharkMoveControl(MoCSharkEntity shark) {
            super(shark);
            this.shark = shark;
        }

        @Override
        public void tick() {
            if (this.shark.isInWater()) {
                this.shark.setDeltaMovement(this.shark.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            }
            if (this.operation == Operation.MOVE_TO && !this.shark.getNavigation().isDone()) {
                float speed = (float) (this.speedModifier * this.shark.getAttributeValue(Attributes.MOVEMENT_SPEED));
                // While it has a live target, snap straight to the target
                // speed instead of gradually lerping toward it — otherwise
                // the attack speed boost takes many ticks to actually show.
                boolean hasTarget = this.shark.getTarget() != null && this.shark.getTarget().isAlive();
                this.shark.setSpeed(hasTarget ? speed : Mth.lerp(0.125F, this.shark.getSpeed(), speed));
                double dx = this.wantedX - this.shark.getX();
                double dy = this.wantedY - this.shark.getY();
                double dz = this.wantedZ - this.shark.getZ();
                if (dy != 0.0D) {
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    this.shark.setDeltaMovement(this.shark.getDeltaMovement()
                            .add(0.0D, this.shark.getSpeed() * (dy / dist) * 0.1D, 0.0D));
                }
                if (dx != 0.0D || dz != 0.0D) {
                    float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                    this.shark.setYRot(this.rotlerp(this.shark.getYRot(), yaw, 90.0F));
                    this.shark.yBodyRot = this.shark.getYRot();
                }
            } else {
                this.shark.setSpeed(0.0F);
            }
        }
    }
}