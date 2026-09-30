package com.example.neomocreatures.entity;

/**
 * A mob whose size lives in Attributes.SCALE (growth from baby to adult, variant size...). Lets the
 * scale be applied the moment it joins the world, before it is sent to any player.
 */
public interface GrowthScaled {

    /** Applies the current growth/variant scale right away. Server side only. */
    void updateGrowthScale();
}