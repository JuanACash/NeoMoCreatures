package com.example.neomocreatures.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.init.ModEntities;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/** One volume multiplier per creature, built from the entity types registered in {@link ModEntities}. */
public final class SoundConfig {

    private static final String OGRE_GROUP = "moc_ogre";
    private static final String WYVERN_GROUP = "wyvern";
    private static final Set<String> SILENT_ENTITIES =
            Set.of("moc_egg", "moc_kitty_bed", "moc_litter_box", "moc_throwable_rock", "moc_summoned_rock");
    /** Sort names for groups whose label is not simply their id ("Golem (Big)", "Wraith (Flame)"). */
    private static final Map<String, String> SORT_OVERRIDES = Map.of(
            "moc_big_golem", "golem big",
            "moc_mini_golem", "golem mini",
            "moc_flame_wraith", "wraith flame");
    private static final int DEFAULT_PERCENT = 100;
    private static final int MAX_PERCENT = 200;

    private final Map<String, ModConfigSpec.IntValue> volumes = new LinkedHashMap<>();

    public SoundConfig(ModConfigSpec.Builder builder) {
        builder.translation(ConfigTranslations.of("sounds")).push("sounds");

        Map<String, String> groups = new TreeMap<>();
        for (var holder : ModEntities.ENTITY_TYPES.getEntries()) {
            String group = groupOf(holder.getId());
            if (group != null) {
                groups.put(sortKey(group), group);
            }
        }

        for (String group : groups.values()) {
            volumes.put(group, builder
                    .comment("Volume percentage for every sound this creature makes. 0 mutes it, 100 is the default, 200 is double.")
                    .translation(ConfigTranslations.of("sound." + group))
                    .defineInRange(group, DEFAULT_PERCENT, 0, MAX_PERCENT));
        }

        builder.pop();
    }

    /** Returns the configured multiplier for an entity type id, or 1 if the entity is not configurable. */
    public double getVolume(ResourceLocation entityTypeId) {
        String group = groupOf(entityTypeId);
        ModConfigSpec.IntValue value = group == null ? null : volumes.get(group);
        int percent = value == null ? DEFAULT_PERCENT : value.get();
        return percent / 100.0D;
    }

    /** Maps an entity type id to its config entry; null for entities of other mods or that make no sound. */
    @Nullable
    private static String groupOf(ResourceLocation id) {
        if (!NeoMoCreatures.MODID.equals(id.getNamespace()) || SILENT_ENTITIES.contains(id.getPath())) {
            return null;
        }
        String path = id.getPath();
        if (path.endsWith("_ogre")) {
            return OGRE_GROUP;
        }
        return path.startsWith("wyvern") ? WYVERN_GROUP : path;
    }

    /** Name used to order the config entries alphabetically, matching the label shown in the menu. */
    private static String sortKey(String group) {
        String override = SORT_OVERRIDES.get(group);
        if (override != null) {
            return override;
        }
        return (group.startsWith("moc_") ? group.substring(4) : group).replace('_', ' ');
    }

}