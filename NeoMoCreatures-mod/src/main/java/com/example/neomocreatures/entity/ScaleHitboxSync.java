package com.example.neomocreatures.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Rebuilds a mob's hitbox when its SCALE attribute changed enough since the last rebuild.
 * Rebuilding the hitbox briefly disrupts pathfinding, so a growing mob must not do it every
 * tick just because its scale drifts by a tiny float amount. One instance per entity.
 */
public final class ScaleHitboxSync {

    private final float threshold;
    private float lastAppliedScale = -1F;

    /** Rebuilds on any change. */
    public ScaleHitboxSync() {
        this(0.0F);
    }

    /** @param threshold smallest scale change worth a hitbox rebuild */
    public ScaleHitboxSync(float threshold) {
        this.threshold = threshold;
    }

    /** Call on both sides, after the scale attribute was updated. */
    public void sync(LivingEntity entity) {
        AttributeInstance scaleAttr = entity.getAttribute(Attributes.SCALE);
        if (scaleAttr == null) {
            return;
        }
        float current = (float) scaleAttr.getValue();
        if (Math.abs(this.lastAppliedScale - current) > this.threshold) {
            this.lastAppliedScale = current;
            entity.refreshDimensions();
        }
    }
}