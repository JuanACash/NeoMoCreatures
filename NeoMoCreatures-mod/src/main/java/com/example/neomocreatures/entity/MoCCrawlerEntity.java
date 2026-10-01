package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Shared base of the ground-dwelling insects (snail, ant, maggot, cricket), ported from the
 * parts of {@code MoCEntityAmbient} they use: never takes fall damage, and a helper that finds the
 * nearest creature big enough to matter to something this small.
 */
public abstract class MoCCrawlerEntity extends PathfinderMob {

    private static final float MIN_THREAT_SIZE = 0.5F;
    private static final double THREAT_SEARCH_HEIGHT = 4.0D;

    protected MoCCrawlerEntity(EntityType<? extends MoCCrawlerEntity> type, Level level) {
        super(type, level);
    }

    /** Original: isMovementCeased() - a species that is frozen in place (a hiding snail). */
    protected boolean isMovementCeased() {
        return false;
    }

    /** Original: getBoogey() - nearest living creature of another kind at least half a block wide or
     *  tall, within {@code radius} horizontally and 4 blocks vertically. */
    @Nullable
    protected LivingEntity findNearbyCreature(double radius) {
        return this.findNearbyCreature(radius, MIN_THREAT_SIZE);
    }

    /** Same search with an explicit size floor; 0 means any creature at all, however small. */
    @Nullable
    protected LivingEntity findNearbyCreature(double radius, float minSize) {
        AABB area = this.getBoundingBox().inflate(radius, THREAT_SEARCH_HEIGHT, radius);
        for (LivingEntity candidate : this.level().getEntitiesOfClass(LivingEntity.class, area,
                other -> other != this && other.getClass() != this.getClass()
                        && (other.getBbWidth() >= minSize || other.getBbHeight() >= minSize))) {
            return candidate;
        }
        return null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isMovementCeased()) {
            this.getNavigation().stop();
        }
        if (this.isInWater()) {
            // Original: damps vertical motion in water so it doesn't sink like a stone.
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    /** Wiki: every insect can scale solid blocks. */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision;
    }

    /** Wiki: every insect gives 1-3 experience when killed by a player or a tamed wolf. */
    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.random.nextInt(3);
    }

    /** Species that drop slimeballs (snail, maggot) override this to true. */
    protected boolean dropsSlimeballs() {
        return false;
    }

    /** Wiki: 0-2 slimeballs, increased by Looting. */
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!this.dropsSlimeballs()) {
            return;
        }
        int lootingLevel = 0;
        if (damageSource.getEntity() instanceof LivingEntity attacker) {
            lootingLevel = EnchantmentHelper.getEnchantmentLevel(
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                    attacker);
        }
        int count = this.random.nextInt(3) + this.random.nextInt(lootingLevel + 1);
        if (count > 0) {
            this.spawnAtLocation(new ItemStack(Items.SLIME_BALL, count));
        }
    }
}
