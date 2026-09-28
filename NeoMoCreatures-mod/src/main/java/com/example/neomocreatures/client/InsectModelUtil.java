package com.example.neomocreatures.client;

/** Small helpers shared by the flying-insect models. */
final class InsectModelUtil {

    private InsectModelUtil() {
    }

    /** Same RGB, alpha replaced — used to draw wings semi-transparent. */
    static int withAlpha(int color, float alpha) {
        return ((int) (alpha * 255.0F) << 24) | (color & 0x00FFFFFF);
    }
}
