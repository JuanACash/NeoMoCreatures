package com.example.neomocreatures.entity.kitty;

/**
 * Ids of the kitty's care states, as stored in its synced data and save file
 * (same numbers as the original mod, so existing worlds keep their state).
 */
public final class KittyCareState {

    public static final int STATE_SEEKING_BED = 3;
    public static final int STATE_IN_BED = 4;
    public static final int STATE_SEEKING_LITTER = 5;
    public static final int STATE_IN_LITTER = 6;
    public static final int STATE_IDLE = 7;
    public static final int STATE_PLAYING = 8;
    public static final int STATE_LOOKING_FOR_MATE = 9;
    public static final int STATE_CURIOUS = 11;
    public static final int STATE_SLEEPING = 12;
    public static final int STATE_AGGRESSIVE = 13;
    public static final int STATE_HELD_LEAD = 14;
    public static final int STATE_HELD_PLAYER = 15;
    public static final int STATE_WANTS_TREE = 16;
    public static final int STATE_STUCK_IN_TREE = 17;
    public static final int STATE_MATING = 18;
    public static final int STATE_SEEKING_BIRTH_BED = 19;
    public static final int STATE_GIVING_BIRTH = 20;
    public static final int STATE_DEFENDING_KITTENS = 21;

    private KittyCareState() {
        // Constants only, no instances
    }
}