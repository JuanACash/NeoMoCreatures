package com.example.neomocreatures.client;

import net.minecraft.util.Mth;

/**
 * Shared math for entity model animations.
 * Every helper returns exactly the same value as the inline formula it replaces.
 */
public final class ModelAnimations {

    /** Degrees in one radian: an angle in degrees divided by this is the angle in radians. */
    public static final float DEGREES_PER_RADIAN = 57.29578F;

    /** Vanilla walk-cycle frequency (QuadrupedModel / HumanoidModel). */
    public static final float WALK_FREQUENCY = 0.6662F;

    private ModelAnimations() {
        // Utility class, no instances
    }

    /** Leg/tail swing for the walk cycle, in phase with the limb swing. */
    public static float walkSwing(float limbSwing, float limbSwingAmount, float amplitude) {
        return Mth.cos(limbSwing * WALK_FREQUENCY) * amplitude * limbSwingAmount;
    }

    /** Same swing half a cycle later, for the opposite leg of a pair. */
    public static float walkSwingOpposite(float limbSwing, float limbSwingAmount, float amplitude) {
        return Mth.cos(limbSwing * WALK_FREQUENCY + Mth.PI) * amplitude * limbSwingAmount;
    }
}
