package com.example.neomocreatures.entity.deer;
import net.minecraft.util.RandomSource;

/** Original: selectType() rolls 20% stag, 50% doe, 30% fawn (which also forces non-adult). */
public enum DeerVariant {

    STAG(1, "deer_stag", 1.6F),
    DOE(2, "deer_doe", 1.3F),
    FAWN(3, "deer_fawn", 0.0F); // fawn's scale grows with age instead of a fixed value

    private final int id;
    private final String textureName;
    private final float fixedScale;

    DeerVariant(int id, String textureName, float fixedScale) {
        this.id = id;
        this.textureName = textureName;
        this.fixedScale = fixedScale;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public float getFixedScale() {
        return fixedScale;
    }

    public static DeerVariant byId(int id) {
        for (DeerVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return DOE;
    }

    /** Same stag to doe ratio the original uses when rolling a spawn (20% stag, 50% doe). */
    private static final int STAG_WEIGHT = 20;
    private static final int DOE_WEIGHT = 50;

    public static DeerVariant randomAdult(RandomSource random) {
        return random.nextInt(STAG_WEIGHT + DOE_WEIGHT) < STAG_WEIGHT ? STAG : DOE;
    }

}