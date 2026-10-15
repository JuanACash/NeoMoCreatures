package com.example.neomocreatures.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Drives the SCALE attribute of an age-based mob (baby to adult).
 * One instance per entity: it remembers the last scale it applied.
 */
public final class AgeGrowthScaler {

    private final int growthTicks;
    private final float babyScale;
    private final float adultScale;
    private final float syncThreshold;
    private final ScaleHitboxSync hitboxSync;

    /**
     * @param growthTicks ticks a baby needs to become an adult
     * @param babyScale   scale fraction at birth (relative to the adult)
     * @param adultScale  final scale of the adult
     */
    public AgeGrowthScaler(int growthTicks, float babyScale, float adultScale) {
        this(growthTicks, babyScale, adultScale, 0.0F);
    }

    /**
     * @param syncThreshold smallest scale change worth applying. A baby's scale drifts by a tiny
     *                      float amount every tick, and rebuilding the hitbox that often disrupts
     *                      pathfinding. Use 0 to apply every change.
     */
    public AgeGrowthScaler(int growthTicks, float babyScale, float adultScale, float syncThreshold) {
        this.growthTicks = growthTicks;
        this.babyScale = babyScale;
        this.adultScale = adultScale;
        this.syncThreshold = syncThreshold;
        this.hitboxSync = new ScaleHitboxSync(syncThreshold);
    }

    /** For mobs whose growth parameters depend on the variant: use {@link #tick(AgeableMob, int, float, float)}. */
    public AgeGrowthScaler() {
        this(1, 1.0F, 1.0F);
    }

    /** Scale for the fixed parameters given to the constructor. */
    public float targetScale(AgeableMob mob) {
        return targetScale(mob, this.growthTicks, this.babyScale, this.adultScale);
    }

    /** Scale for parameters that depend on the entity (variant, individual size...). */
    public float targetScale(AgeableMob mob, int ticks, float baby, float adult) {
        if (!mob.isBaby()) {
            return adult;
        }
        float progress = Mth.clamp((mob.getAge() + ticks) / (float) ticks, 0.0F, 1.0F);
        return Mth.lerp(progress, baby, 1.0F) * adult;
    }

    public void tick(AgeableMob mob) {
        tick(mob, this.growthTicks, this.babyScale, this.adultScale);
    }

    /** Applies the target scale (server) and refreshes the hitbox when the scale changed (both sides). */
    public void tick(AgeableMob mob, int ticks, float baby, float adult) {
        AttributeInstance scaleAttr = mob.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        if (!mob.level().isClientSide) {
            float target = targetScale(mob, ticks, baby, adult);
            if (Math.abs((float) scaleAttr.getBaseValue() - target) > this.syncThreshold) {
                scaleAttr.setBaseValue(target);
            }
        }
        this.hitboxSync.sync(mob);
    }
}