package com.example.neomocreatures.entity;

import com.example.neomocreatures.util.MoCLootUtil;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Port of {@code drzhark.mocreatures.entity.hostile.MoCEntityHellRat}. The original itself extends
 * the Rat directly and only changes stats/texture/fire behaviour — same here: this extends our own
 * {@link MoCRatEntity} and inherits its whole AI (darkness-gated attack, pack alert on hit, wander,
 * etc.) unchanged, on top of {@link com.example.neomocreatures.init.ModItems#HEART_OF_FIRE}.
 * <p>
 * The original's own animated texture (2 frames, {@code hell_rat1.png}/{@code hell_rat2.png}) is
 * recomputed here on a tick-based cadence instead of its original's odd render-call-based random
 * increment — same simplification already used for the Nightmare horse and Fire Ogre.
 */
public class MoCHellRatEntity extends MoCRatEntity {

    private static final double MAX_HEALTH = 40.0D;
    private static final double MOVEMENT_SPEED = 0.325D;
    private static final double ATTACK_DAMAGE = 4.5D;
    private static final double ARMOR = 7.0D;
    private static final int IGNITE_SECONDS = 5;
    private static final int TEXTURE_FRAME_INTERVAL = 10;

    public MoCHellRatEntity(EntityType<? extends MoCHellRatEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MoCRatEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.ARMOR, ARMOR);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    /** 2 frames, alternating every {@link #TEXTURE_FRAME_INTERVAL} ticks — read by the renderer. */
    public String getTextureFrameName() {
        int frame = 1 + (this.tickCount / TEXTURE_FRAME_INTERVAL) % 2;
        return "hell_rat" + frame;
    }

    /** Original: doHurtTarget() — every hit also sets the target on fire for 5 seconds. */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean success = super.doHurtTarget(target);
        if (success && target instanceof LivingEntity) {
            target.igniteForSeconds(IGNITE_SECONDS);
        }
        return success;
    }

    /** Original: 2 flame particles per tick around it, client-side only, purely cosmetic. */
    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);
            }
        }
        super.aiStep();
    }

    // ---------------------------------------------------------------------
    // Sounds
    // ---------------------------------------------------------------------

    @Override
    protected SoundEvent getAmbientSound() {
        return com.example.neomocreatures.init.ModSounds.HELL_RAT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return com.example.neomocreatures.init.ModSounds.HELL_RAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return com.example.neomocreatures.init.ModSounds.HELL_RAT_DEATH.get();
    }

    // ---------------------------------------------------------------------
    // Drops
    // ---------------------------------------------------------------------

    /** 0-2 redstone, scaling with Looting, plus a chance of 1 coal, that chance boosted by Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        int lootingLevel = MoCLootUtil.getLootingLevel(level, damageSource);

        MoCLootUtil.dropItems(this, Items.REDSTONE, MoCLootUtil.rollWithLootingBonus(this.random, 3, lootingLevel));
        if (MoCLootUtil.rollChance(this.random, 0.5F, 0.05F, lootingLevel)) {
            this.spawnAtLocation(new ItemStack(Items.COAL));
        }
    }

    /** Fixed 5 experience, regardless of who made the kill. */
    @Override
    protected int getBaseExperienceReward() {
        return 5;
    }
}