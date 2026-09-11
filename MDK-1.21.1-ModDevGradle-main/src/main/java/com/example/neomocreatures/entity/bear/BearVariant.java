package com.example.neomocreatures.entity.bear;

/**
 * The 4 bear species. Stats/render scale taken directly from
 * MoCEntityBlackBear/GrizzlyBear/PolarBear/PandaBear (registerAttributes()
 * and getBearSize()) in the original mod.
 */
public enum BearVariant {

    BLACK(0, "bear_black", 30.0D, 5.5D, 0.9D, Temperament.NEUTRAL, 0.65D),
    GRIZZLY(1, "bear_grizzly", 40.0D, 7.0D, 1.2D, Temperament.NEUTRAL, 0.75D),
    POLAR(2, "bear_polar", 45.0D, 7.5D, 1.4D, Temperament.HOSTILE, 0.85D),
    PANDA(3, "bear_panda", 20.0D, 4.0D, 0.8D, Temperament.PASSIVE, 0.5D);

    /**
     * NEUTRAL: leaves the player alone unless provoked, or unless a cub of
     * its own species nearby is under attack.
     * HOSTILE: attacks the player on sight (polar).
     * PASSIVE: never fights back or hunts, even if hit (panda).
     */
    public enum Temperament { NEUTRAL, HOSTILE, PASSIVE }

    private static final double SMALLEST_RENDER_SCALE = 0.8D; // Panda, the smallest of the 4

    private final int id;
    private final String textureName;
    private final double maxHealth;
    private final double attackDamage;
    private final double renderScale;
    private final Temperament temperament;

    private final double riderHeight;

    BearVariant(int id, String textureName, double maxHealth, double attackDamage,
                double renderScale, Temperament temperament, double riderHeight) {
        this.id = id;
        this.textureName = textureName;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.renderScale = renderScale;
        this.temperament = temperament;
        this.riderHeight = riderHeight;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public double getRenderScale() {
        return renderScale;
    }

    public double getRiderHeight() {
        return riderHeight;
    }

    public Temperament getTemperament() {
        return temperament;
    }

    /** At least 20 minutes (24000 ticks) — bigger species take proportionally longer, same curve as BigCat. */
    public int getGrowthTicks() {
        return (int) Math.round(24000D * (renderScale / SMALLEST_RENDER_SCALE));
    }

    public static BearVariant byId(int id) {
        for (BearVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return BLACK;
    }
}