package com.example.neomocreatures.entity.elephant;

import net.minecraft.util.RandomSource;

/**
 * The 5 elephant/mammoth species from the original mod. ASIAN_DECORATED is
 * never picked at spawn — it's only reached by equipping a garment on a
 * tamed Asian elephant (a later step), same as the wiki describes it.
 */
public enum ElephantVariant {

    AFRICAN(0, "elephant_african", 50.0D, 0.23D, true, false, 1.1D, 0.65D),
    ASIAN(1, "elephant_asian", 30.0D, 0.24D, false, false, 0.9D, 0.84D),
    ASIAN_DECORATED(2, "elephant_asian_decorated", 40.0D, 0.22D, false, false, 0.9D, 0.84D),
    MAMMOTH_WOOLLY(3, "mammoth_woolly", 40.0D, 0.22D, false, true, 1.0D, 0.75D),
    MAMMOTH_SONGHUA(4, "mammoth_songhua", 60.0D, 0.23D, false, true, 1.3D, 0.65D);

    /** Species that can be rolled on natural spawn / spawn egg use. */
    public static final ElephantVariant[] SPAWNABLE = {AFRICAN, ASIAN, MAMMOTH_WOOLLY, MAMMOTH_SONGHUA};

    private final int id;
    private final String textureName;
    private final double maxHealth;
    private final double movementSpeed;
    private final boolean bigEars;
    private final boolean mammoth;
    private final double renderScale;
    private final double jumpVelocity;

    ElephantVariant(int id, String textureName, double maxHealth, double movementSpeed,
                    boolean bigEars, boolean mammoth, double renderScale, double jumpVelocity) {
        this.id = id;
        this.textureName = textureName;
        this.maxHealth = maxHealth;
        this.movementSpeed = movementSpeed;
        this.bigEars = bigEars;
        this.mammoth = mammoth;
        this.renderScale = renderScale;
        this.jumpVelocity = jumpVelocity;
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

    public double getMovementSpeed() {
        return movementSpeed;
    }

    /** Vertical impulse for a single default (uncharged) jump while ridden. */
    public double getJumpVelocity() {
        return jumpVelocity;
    }

    /** African elephants get the large fan ears; everyone else gets the small rounded ear. */
    public boolean hasBigEars() {
        return bigEars;
    }

    /** Woolly and Songhua mammoths get the head bump + wool skirt geometry. */
    public boolean isMammoth() {
        return mammoth;
    }

    /** African is the 1.0 baseline; Songhua mammoths biggest, Asian elephants smallest. */
    public double getRenderScale() {
        return renderScale;
    }

    public static ElephantVariant byId(int id) {
        for (ElephantVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return AFRICAN;
    }

    public static ElephantVariant randomSpawnable(RandomSource random) {
        return SPAWNABLE[random.nextInt(SPAWNABLE.length)];
    }
}