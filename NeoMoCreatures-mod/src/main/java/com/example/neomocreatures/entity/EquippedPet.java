package com.example.neomocreatures.entity;

/**
 * A pet that can wear equipment (saddle, armor, chest, medallion...). Releasing it with a
 * Scroll of Freedom drops everything first, so nothing is lost with the now-wild animal.
 */
public interface EquippedPet {

    /** Drops every piece of equipment and any chest contents at the pet's position. */
    void dropAllEquipment();
}
