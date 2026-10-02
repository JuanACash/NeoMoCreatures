package com.example.neomocreatures.entity;

/**
 * A mount that can shield its rider from fire: no burning, no fire/lava damage and no fire
 * overlay on screen while riding it (nightmare, fire manticore, nether scorpion...).
 */
public interface FireproofMount {

    /** True while this mount, in its current form, protects whoever rides it from fire. */
    boolean protectsRiderFromFire();
}