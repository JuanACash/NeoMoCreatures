package com.example.neomocreatures.entity;

/**
 * A mount whose rider can make it go down (mod descend key held) — flying or diving mounts.
 * The client sends the key state through DescendInputPayload.
 */
public interface DescendingMount {

    void setDescendHeld(boolean held);
}
