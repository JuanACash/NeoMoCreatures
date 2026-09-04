package com.example.neomocreatures.entity.wyvern;

/**
 * Size/stat class of a wyvern, independent of its texture (WyvernVariant).
 * Stats sourced from the community wiki's documented wyvern behaviour:
 * 40 HP / 3 attack for normal wyverns, 80 HP / 17 attack for tier 2 and
 * mother wyverns. Tier 2 reuses the same 8 biome textures as tier 1, just
 * bigger; only the mother has her own dedicated texture set.
 *
 * MOTHER is the wild size. MOTHER_TAMED is the bigger form — same stats,
 * only reachable by hatching a mother wyvern egg (see MoCEggEntity /
 * ModEntities.WYVERN_MOTHER_TAMED); a wild mother never uses these numbers.
 */
public enum WyvernTier {

    TIER_1(1.0F, 1.45F, 1.55F, 40.0D, 3.0D),
    TIER_2(1.3F, 1.8F, 2.0F, 60.0D, 10.0D),
    MOTHER(1.5F, 2.2F, 2.35F, 80.0D, 17.0D),
    MOTHER_TAMED(3.0F, 4.2F, 5.0F, 80.0D, 17.0D);

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

    /** Multiplier applied on top of the base model in MoCWyvernRenderer#scale. */
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