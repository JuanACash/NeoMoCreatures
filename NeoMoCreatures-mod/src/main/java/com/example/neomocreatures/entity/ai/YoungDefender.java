package com.example.neomocreatures.entity.ai;

import net.minecraft.world.entity.LivingEntity;

/** Implemented by animals whose adults react when a baby of their species is hurt nearby. */
public interface YoungDefender {

    /** Called for each adult near a hurt baby of the same species. */
    void defendYoung(LivingEntity attacker);
}