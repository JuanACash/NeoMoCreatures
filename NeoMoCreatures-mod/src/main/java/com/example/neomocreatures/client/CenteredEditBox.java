package com.example.neomocreatures.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * An EditBox whose text is drawn centered on a fixed X, growing outward
 * from the middle as you type. Reuses EditBox's own input handling
 * (typing, backspace, focus) — only the rendering is replaced.
 * <p>
 * Simplification: click-to-place-cursor still targets vanilla's original
 * left-anchored layout, so clicking mid-text may not land exactly where it
 * visually looks. Typing and Backspace both work correctly regardless.
 */
public class CenteredEditBox extends EditBox {

    private final int centerX;

    public CenteredEditBox(Font font, int centerX, int y, int width, int height, Component message) {
        super(font, centerX - width / 2, y, width, height, message);
        this.centerX = centerX;
        this.setBordered(false);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        String text = this.getValue();
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        int textX = this.centerX - textWidth / 2;
        int textY = this.getY() + (this.height - 8) / 2;

        graphics.drawString(font, text, textX, textY, 0xFFFFFF, false);

        if (this.isFocused()) {
            graphics.fill(textX + textWidth + 1, textY - 1, textX + textWidth + 2, textY + 9, 0xFFFFFFFF);
        }
    }
}