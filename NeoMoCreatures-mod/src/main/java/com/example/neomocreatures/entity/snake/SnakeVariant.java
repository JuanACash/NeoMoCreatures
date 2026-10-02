package com.example.neomocreatures.entity.snake;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
/**
 * The 8 snake colour variants, ported from the texture switch in
 * {@code drzhark.mocreatures.entity.hunter.MoCEntitySnake#getTexture()}
 * (original numeric types 1-8; the unused "sea snake" type 9 has no art
 * asset and is not ported).
 * <p>
 * Selection is uniformly random for now — the original's biome-weighted
 * {@code checkSpawningBiome()} table is ported in the natural-spawn step,
 * not here. {@code bold}/{@code venomous} match the original's
 * {@code isNotScared()} (type &gt; 2) and {@code isVenomous()} (types
 * 3-7) checks. {@code sizeFactor} is the per-type multiplier from the
 * original's {@code getSizeF()} — that method actually returns
 * {@code MoCAge * 0.01 * typeFactor}, but the age/growth system lives in
 * the mod's shared tameable-animal base class, not in the snake itself, so
 * it isn't ported; this uses just the type factor, i.e. a fully-grown snake.
 */
public enum SnakeVariant {

    GREEN_DARK(0, "snake_green_dark", false, false, 0.8F),
    WOLF(1, "snake_wolf", false, false, 0.8F),
    ORANGE(2, "snake_orange", true, true, 1.0F),
    GREEN_BRIGHT(3, "snake_green_bright", true, true, 1.0F),
    CORAL(4, "snake_coral", true, true, 0.6F),
    COBRA(5, "snake_cobra", true, true, 1.1F),
    RATTLE(6, "snake_rattle", true, true, 0.9F),
    PYTHON(7, "snake_python", true, false, 1.5F);

    private final int id;
    private final String textureName;
    private final boolean bold;
    private final boolean venomous;
    private final float sizeFactor;

    SnakeVariant(int id, String textureName, boolean bold, boolean venomous, float sizeFactor) {
        this.id = id;
        this.textureName = textureName;
        this.bold = bold;
        this.venomous = venomous;
        this.sizeFactor = sizeFactor;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    /** Original's {@code isNotScared()}: confronts and can be provoked into attacking, instead of just fleeing. */
    public boolean isBold() {
        return bold;
    }

    /** Original's {@code isVenomous()}: poisons on a successful bite. */
    public boolean isVenomous() {
        return venomous;
    }

    /** Original's per-type factor from {@code getSizeF()} (length and width scale together). */
    public float getSizeFactor() {
        return sizeFactor;
    }

    public static SnakeVariant byId(int id) {
        for (SnakeVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return GREEN_DARK;
    }

    public static SnakeVariant random(RandomSource random) {
        SnakeVariant[] variants = values();
        return variants[random.nextInt(variants.length)];
    }


    /** Which variant a whole spawn group will be, decided once per group by biome. */
    public static SnakeVariant forBiome(Holder<Biome> biome, RandomSource random) {
        if (biome.is(ModTags.SNAKE_RATTLE_OR_WOLF_BIOMES)) {
            return random.nextBoolean() ? RATTLE : WOLF;
        }

        // Wiki: "cobras also spawn in savannas" — savanna is cobra-only,
        // unlike the jungle biomes it shares with python/green.
        if (biome.is(ModTags.SNAKE_COBRA_BIOMES)) {
            return COBRA;
        }

        // Wiki: pythons can also spawn in mangrove swamps (and other swamps/marshes),
        // including right on the water surface (see the custom spawn placement predicate).
        if (biome.is(ModTags.SNAKE_PYTHON_BIOMES)) {
            return PYTHON;
        }

        if (biome.is(ModTags.SNAKE_JUNGLE_BIOMES)) {
            SnakeVariant[] options = {COBRA, PYTHON, GREEN_BRIGHT};
            return options[random.nextInt(options.length)];
        }

        if (biome.is(ModTags.SNAKE_CORAL_BIOMES)) {
            return CORAL;
        }

        if (biome.is(ModTags.SNAKE_ORANGE_BIOMES)) {
            return ORANGE;
        }

        return GREEN_DARK;
    }

}