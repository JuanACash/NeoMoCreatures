package com.example.neomocreatures.client;

import com.example.neomocreatures.NeoMoCreatures;
import com.example.neomocreatures.network.SetPetNamePayload;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Reusable naming prompt for ANY tamed creature — not horse-specific.
 * Opened client-side by ModNetworking in response to OpenNamingScreenPayload;
 * sends SetPetNamePayload back to the server when the player confirms.
 */
public class MoCNamingScreen extends Screen {

    private int previousBlur;
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NeoMoCreatures.MODID, "textures/gui/mocname.png");
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 181;

    private final int entityId;
    private EditBox nameBox;

    public MoCNamingScreen(int entityId) {
        super(Component.literal("Name your pet"));
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        var options = Minecraft.getInstance().options;
        this.previousBlur = options.menuBackgroundBlurriness().get();
        options.menuBackgroundBlurriness().set(0);
        int left = (this.width - TEX_WIDTH) / 2;
        int top = (this.height - TEX_HEIGHT) / 2;

        this.nameBox = new CenteredEditBox(this.font, this.width / 2, top + 85, 200, 20, Component.literal("Name"));
        this.nameBox.setMaxLength(32);
        this.nameBox.setBordered(false);
        this.addRenderableWidget(this.nameBox);
        this.setInitialFocus(this.nameBox);
        Minecraft.getInstance().getTextureManager().getTexture(TEXTURE).setFilter(false, false);

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.confirmName())
                .bounds(left + 78, top + 155, 100, 20)
                .build());
    }

    private void confirmName() {
        String name = this.nameBox.getValue();
        if (!name.isBlank()) {
            PacketDistributor.sendToServer(new SetPetNamePayload(this.entityId, name));
        }
        this.onClose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().options.menuBackgroundBlurriness().set(this.previousBlur);
        super.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int left = (this.width - TEX_WIDTH) / 2;
        int top = (this.height - TEX_HEIGHT) / 2;
        RenderSystem.setShaderColor(1.15F, 1.15F, 1.08F, 1.0F);
        graphics.blit(TEXTURE, left, top, 0, 0, TEX_WIDTH, TEX_HEIGHT, 256, 256);
        graphics.drawCenteredString(this.font, "Choose your Pet's name:", this.width / 2, top + 30, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter / numpad Enter
            this.confirmName();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}