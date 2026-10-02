package com.example.neomocreatures.entity.smallfish;

import java.util.List;
import java.util.Map;

import com.example.neomocreatures.init.ModTags;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
/**
 * The 8 small fish species, ported from {@code MoCEntitySmallFish.fishNames} plus the piranha,
 * which the original splits into its own aggressive subclass. Here they are all one entity with a
 * variant, so the aggressive behaviour is switched on per-individual by {@link #isAggressive()}.
 */
public enum SmallFishVariant {

    ANCHOVY(0, "smallfish_anchovy", false),
    ANGELFISH(1, "smallfish_angelfish", false),
    ANGLER(2, "smallfish_anglerfish", false),
    CLOWNFISH(3, "smallfish_clownfish", false),
    GOLDFISH(4, "smallfish_goldfish", false),
    HIPPOTANG(5, "smallfish_hippotang", false),
    MANDARIN(6, "smallfish_mandarinfish", false),
    PIRANHA(7, "smallfish_piranha", true);

    private final int id;
    private final String textureName;
    private final boolean aggressive;

    SmallFishVariant(int id, String textureName, boolean aggressive) {
        this.id = id;
        this.textureName = textureName;
        this.aggressive = aggressive;
    }

    public int getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    /** Wiki: every small fish is passive except the piranha, which attacks on sight. */
    public boolean isAggressive() {
        return aggressive;
    }

    public static SmallFishVariant byId(int id) {
        for (SmallFishVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return ANCHOVY;
    }

    public static SmallFishVariant byName(String name) {
        for (SmallFishVariant variant : values()) {
            if (variant.name().equals(name)) {
                return variant;
            }
        }
        return ANCHOVY;
    }

    /** Original: selectType() rolls uniformly among the 7 passive kinds — the piranha is its own spawn. */
    public static SmallFishVariant randomPassive(RandomSource random) {
        SmallFishVariant[] passive = { ANCHOVY, ANGELFISH, ANGLER, CLOWNFISH, GOLDFISH, HIPPOTANG, MANDARIN };
        return passive[random.nextInt(passive.length)];
    }


    /**
     * Biomes each species can spawn in, as tags (vanilla plus optional modded biomes);
     * the piranha's own biomes never overlap the rest.
     */
    private static final Map<SmallFishVariant, TagKey<Biome>> SPAWN_BIOMES = Map.ofEntries(
            Map.entry(ANCHOVY, ModTags.SMALL_FISH_ANCHOVY_BIOMES),
            Map.entry(ANGELFISH, ModTags.SMALL_FISH_ANGELFISH_BIOMES),
            Map.entry(ANGLER, ModTags.SMALL_FISH_ANGLER_BIOMES),
            Map.entry(CLOWNFISH, ModTags.SMALL_FISH_CLOWNFISH_BIOMES),
            Map.entry(GOLDFISH, ModTags.SMALL_FISH_GOLDFISH_BIOMES),
            Map.entry(HIPPOTANG, ModTags.SMALL_FISH_HIPPOTANG_BIOMES),
            Map.entry(MANDARIN, ModTags.SMALL_FISH_MANDARIN_BIOMES),
            Map.entry(PIRANHA, ModTags.SMALL_FISH_PIRANHA_BIOMES));

    /** Picks uniformly among the species allowed in the biome this individual is spawning in. */
    public static SmallFishVariant forBiome(Holder<Biome> biome, RandomSource random) {
        List<SmallFishVariant> eligible = SPAWN_BIOMES.entrySet().stream()
                .filter(entry -> biome.is(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        if (eligible.isEmpty()) {
            return SmallFishVariant.randomPassive(random);
        }
        return eligible.get(random.nextInt(eligible.size()));
    }

}