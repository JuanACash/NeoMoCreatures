package com.example.neomocreatures.entity;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;

/**
 * A mount that can wear a saddle. Lets shared code equip and remove the saddle
 * without knowing the concrete entity class.
 */
public interface SaddledMount {

    boolean isSaddled();

    void setSaddled(boolean saddled);

    /** Registry id of the exact saddle item being worn, or null for mounts saved before it was tracked. */
    @Nullable
    ResourceLocation getSaddleItemId();

    void setSaddleItemId(@Nullable ResourceLocation saddleItemId);
}