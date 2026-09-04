package com.example.neomocreatures.entity.egg;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;

/**
 * Implemented by any entity that can hatch out of a MoCEggEntity. Keeps the
 * egg itself generic (it doesn't need to know about wyverns, or whatever
 * other tameable creature uses eggs next) — the egg just spawns the entity
 * and, if it implements this, hands it off to finish its own setup.
 */
public interface EggHatchable {

    /**
     * Called right after this entity is spawned by a hatching egg.
     *
     * @param tamer     the nearest player at hatch time, to imprint/tame on — or
     *                  null if nobody was around.
     * @param variantId optional variant/species identifier carried by the egg
     *                  (e.g. a WyvernVariant name), or null to just use
     *                  whatever default the entity's own constructor picked.
     */
    void onHatchedFromEgg(@Nullable Player tamer, @Nullable String variantId);
}