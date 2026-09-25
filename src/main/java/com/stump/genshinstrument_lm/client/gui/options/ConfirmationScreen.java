package com.stump.genshinstrument_lm.client.gui.options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfirmationScreen extends Screen {

    private final Component message;
    private final Runnable confirmCallback;

    private boolean confirmed = false;

    private int panelX;
    private int panelY;

    private static final int PANEL_WIDTH = 140;
    private static final int PANEL_HEIGHT = 60;

    public ConfirmationScreen(Component message, Runnable confirmCallback) {
        super(Component.empty());

        this.message = message;
        this.confirmCallback = confirmCallback;
    }

    @Override
    protected void init() {
        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;

        int buttonWidth = 45;
        int buttonHeight = 15;

        int yesX = panelX + buttonWidth / 3;
        int noX = panelX + PANEL_WIDTH - buttonWidth / 3 - buttonWidth;
        int buttonY = panelY + PANEL_HEIGHT - buttonHeight - 8;

        addRenderableWidget(
                Button.builder(
                        Component.literal("Yes"),
                        btn -> {
                            confirmed = true;
                            onClose();
                        }
                ).bounds(
                        yesX,
                        buttonY,
                        buttonWidth,
                        buttonHeight
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("No"),
                        btn -> onClose()
                ).bounds(
                        noX,
                        buttonY,
                        buttonWidth,
                        buttonHeight
                ).build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x40000000);

        g.fill(panelX - 4, panelY - 4,
                panelX + PANEL_WIDTH + 4, panelY + PANEL_HEIGHT + 4, 0xFFB0AFB0);
        g.fill(panelX - 3, panelY - 3,
                panelX + PANEL_WIDTH + 3, panelY + PANEL_HEIGHT + 3, 0xFF9A999A);
        g.fill(panelX - 1, panelY - 1,
                panelX + PANEL_WIDTH + 1, panelY + PANEL_HEIGHT + 1, 0xFF262626);
        g.fill(panelX, panelY,
                panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF2D2D2D);

        int textWidth = font.width(message);

        g.drawString(
                font,
                message,
                panelX + (PANEL_WIDTH - textWidth) / 2,
                panelY + 14,
                0xFFFFFF
        );

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (confirmed) {
            confirmCallback.run();
        }

        minecraft.popGuiLayer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
                || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
            confirmed = true;
            onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}