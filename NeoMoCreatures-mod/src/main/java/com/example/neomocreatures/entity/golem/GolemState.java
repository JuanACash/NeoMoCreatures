package com.example.neomocreatures.entity.golem;

import javax.annotation.Nullable;

/** Original GOLEM_STATE values: 0 spawned, 1 summoning rocks, 2 has enemy, 3 below 30 health, 4 dying. */
public enum GolemState {

    DORMANT(0, "golem_effect_blue"),
    SUMMONING(1, "golem_effect_red"),
    ACTIVE(2, "golem_effect_yellow"),
    ENRAGED(3, "golem_effect_orange"),
    DYING(4, "golem_effect_red");

    private final int id;
    @Nullable
    private final String effectTextureName;

    GolemState(int id, @Nullable String effectTextureName) {
        this.id = id;
        this.effectTextureName = effectTextureName;
    }

    public int getId() {
        return id;
    }

    /** Name of the glowing overlay texture for this state, or null when it doesn't glow. */
    @Nullable
    public String getEffectTextureName() {
        return effectTextureName;
    }

    /** Original: the head and chest turn red once it has an enemy (state above 1). */
    public boolean isAngry() {
        return this.id > SUMMONING.id;
    }

    public static GolemState byId(int id) {
        for (GolemState state : values()) {
            if (state.id == id) {
                return state;
            }
        }
        return DORMANT;
    }
}