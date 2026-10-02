package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

/**
 * Caches the texture locations of one entity texture folder.
 * getTextureLocation runs every frame for every visible entity, so building a new
 * ResourceLocation each time (string concat + path validation) is wasted work;
 * each texture is now created once and reused.
 */
public final class EntityTextureCache {

    private final String folder;
    // Only touched from the render thread, so a plain HashMap is enough
    private final Map<String, ResourceLocation> locations = new HashMap<>();

    /** @param folder sub-folder of textures/entity, e.g. "moc_bear" */
    public EntityTextureCache(String folder) {
        this.folder = folder;
    }

    /** Location of textures/entity/&lt;folder&gt;/&lt;fileName&gt;.png, created on first use. */
    public ResourceLocation get(String fileName) {
        ResourceLocation location = this.locations.get(fileName);
        if (location == null) {
            location = ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID,
                    "textures/entity/" + this.folder + "/" + fileName + ".png");
            this.locations.put(fileName, location);
        }
        return location;
    }
}
