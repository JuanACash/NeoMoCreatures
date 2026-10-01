package com.example.neomocreatures.entity;

import com.example.neomocreatures.init.ModItems;
import com.example.neomocreatures.init.ModSounds;
import com.example.neomocreatures.util.MoCLootUtil;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Port of {@code drzhark.mocreatures.entity.passive.MoCEntityDuck}. Fully passive; never fights
 * back, no goal ever targets anything. Hand-reimplements the same wing-flap tracking vanilla's own
 * Chicken uses internally (this doesn't extend Chicken, so the source copies the fields/formulas by
 * hand) — kept here for parity even though the model itself only actually reads the ageInTicks-based
 * flap while airborne, not these fields directly.
 * <p>
 * The original's own tailored attributes (MAX_HEALTH 4, MOVEMENT_SPEED 0.25, defined in a
 * {@code registerAttributes()} method) were never actually wired into its entity registration — the
 * same real oversight already found on the Mouse and the Mole. This port uses the tailored values.
 * <p>
 * Wiki: lays an egg every 5 minutes, same as a vanilla Chicken (using vanilla's own egg item, since
 * the mod has no duck-specific one).
 */
public class MoCDuckEntity extends TamableAnimal {

    private static final double MAX_HEALTH = 4.0D;
    private static final double MOVEMENT_SPEED = 0.25D;
    private static final double PANIC_SPEED = 1.4D;
    private static final double WANDER_SPEED = 1.0D;

    /** Wiki: "lay eggs every 5 minutes" — a flat 6000-tick timer, matching that exact interval,
     *  instead of vanilla Chicken's own randomized 6000-12000 range. */
    private static final int EGG_LAY_INTERVAL = 6000;
    private int eggTimer = EGG_LAY_INTERVAL;

    /** Original: hand-copied from vanilla's own Chicken flap tracking. */
    public boolean isFlapping = false;
    public float flap;
    public float flapSpeed = 1.0F;
    public float oFlap;
    public float flapPosition;
    public float oFlapPosition;

    public MoCDuckEntity(EntityType<? extends MoCDuckEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, PANIC_SPEED));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, WANDER_SPEED));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    /** Original: livingTick() — same formulas as vanilla's own Chicken wing-flap tracking; also
     *  handles the egg-laying timer (wiki: "every 5 minutes"). */
    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlapPosition = this.flapPosition;
        this.oFlap = this.flap;
        this.flapPosition = this.flapPosition + (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapPosition = Math.min(Math.max(this.flapPosition, 0.0F), 1.0F);
        if (!this.onGround() && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }
        this.flapSpeed *= 0.9F;
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapSpeed * 2.0F;

        if (!this.level().isClientSide && --this.eggTimer <= 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(new ItemStack(Items.EGG));
            this.eggTimer = EGG_LAY_INTERVAL;
        }
    }

    /** Original: never takes fall damage — same float-fall behaviour as vanilla's own Chicken. */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    // ---------------------------------------------------------------------
    // Vanilla animal hooks that do not apply
    // ---------------------------------------------------------------------

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
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

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DUCK_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DUCK_HURT.get();
    }

    /** The original's own "duckdying" sound reference has no matching sound file or sounds.json
     *  entry anywhere in the mod — a genuinely broken/missing asset. Reuses the hurt sound instead
     *  of leaving death silent. */
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DUCK_HURT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** Original loot table: 1 raw duck (auto-cooked if it died on fire, 0-1 extra with Looting) +
     *  1-2 feathers (also scaling with Looting), both independent. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        MoCLootUtil.dropItems(this, MoCLootUtil.rawOrCooked(this, ModItems.DUCK_RAW.get(), ModItems.DUCK_COOKED.get()),
                1 + this.random.nextInt(lootingLevel + 1));
        MoCLootUtil.dropItems(this, Items.FEATHER, 1 + MoCLootUtil.rollWithLootingBonus(this.random, 2, lootingLevel));
    }

}