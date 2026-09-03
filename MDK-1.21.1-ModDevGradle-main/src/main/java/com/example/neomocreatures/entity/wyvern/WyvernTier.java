package com.example.neomocreatures.entity.wyvern;

public enum WyvernTier {

    TIER_1(1.0F, 1.45F, 1.55F, 40.0D, 10.0D),
    TIER_2(2.6F, 3.8F, 4.5F, 60.0D, 11.0D),
    MOTHER(3.0F, 4.2F, 5.0F, 80.0D, 12.0D);

    private static final WyvernTier[] VALUES = values();

    private final float renderScale;
    private final float hitboxWidth;
    private final float hitboxHeight;
    private final double maxHealth;
    private final double attackDamage;

    WyvernTier(float renderScale, float hitboxWidth, float hitboxHeight, double maxHealth, double attackDamage) {
        this.renderScale = renderScale;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
    }

    public float getRenderScale() {
        return renderScale;
    }

    public float getHitboxWidth() {
        return hitboxWidth;
    }

    public float getHitboxHeight() {
        return hitboxHeight;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public int getId() {
        return ordinal();
    }

    public static WyvernTier byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : TIER_1;
    }
}