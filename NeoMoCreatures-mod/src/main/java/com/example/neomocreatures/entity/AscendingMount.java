package com.example.neomocreatures.entity;

/**
 * A mount whose rider can make it rise (jump key held) — flying, swimming or jumping mounts.
 * The client sends the key state through AscendInputPayload.
 */
public interface AscendingMount {

    void setAscendHeld(boolean held);
}
