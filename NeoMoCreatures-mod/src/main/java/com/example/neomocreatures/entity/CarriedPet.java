package com.example.neomocreatures.entity;

import net.minecraft.world.entity.player.Player;

/** Marks an entity that can be picked up and carried on the player's head/shoulders
 *  (Kitty now, Bunny later) — used to stop a player from carrying more than one at once. */
public interface CarriedPet {
    boolean isHeld();

    Player getHolder();
}