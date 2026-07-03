package com.stump.genshinstrument_lm.client.gui.options;

import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.packet.instrument.c2s.C2SColorSetAcceptPacket;
import com.stump.genshinstrument_lm.client.colorSet.ColorSet;
import com.stump.genshinstrument_lm.util.ParticleColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ColorImportConfirmationScreen extends Screen {

    private static final ResourceLocation NOTE_TEXTURE =
            new ResourceLocation("genshinstrument_lm", "textures/particle/custom_note.png");

    private final String encoded;
    private final ColorSet previewSet;

    private int panelX;
    private int panelY;

    private static final int PANEL_WIDTH = 114;
    private static final int PANEL_HEIGHT = 140;

    public ColorImportConfirmationScreen(String encoded, ColorSet previewSet) {
        super(Component.empty());
        this.encoded = encoded;
        this.previewSet = previewSet;
    }

    @Override
    protected void init() {
        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;

        int buttonWidth = 40;
        int buttonHeight = 15;
        int buttonY = panelY + PANEL_HEIGHT - 24;

        addRenderableWidget(
                Button.builder(Component.literal("Yes"),
                                b -> {
                                    GIPacketHandler.sendToServer(new C2SColorSetAcceptPacket(encoded));
                                    onClose();
                                })
                        .bounds(panelX + 10, buttonY, buttonWidth, buttonHeight)
                        .build()
        );

        addRenderableWidget(
                Button.builder(Component.literal("No"),
                                b -> onClose())
                        .bounds(panelX + PANEL_WIDTH - 10 - buttonWidth, buttonY, buttonWidth, buttonHeight)
                        .build()
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
                panelX + PANEL_WIDTH + 1, panelY + PANEL_HEIGHT + 1, 0xFF474746);
        g.fill(panelX, panelY,
                panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF161617);

        int currentY = panelY + 12;

        Component title = Component.literal("Import color set?").withStyle(style -> style.withUnderlined(true));
        g.drawString(font, title, panelX + (PANEL_WIDTH - font.width(title)) / 2, currentY, 0xFFFFFF);

        currentY += 25;

        g.drawString(font, "Name: " + previewSet.getName(), panelX + 10, currentY, 0xFFFFFF);

        currentY += 28;

        g.drawString(font, "Preview", panelX + 10, currentY, 0xFFFFFF);

        currentY += 11;

        int previewX = panelX + 10;
        int previewWidth = PANEL_WIDTH - 20;

        int noteBaseY = currentY;

        for (int i = 0; i < 6; i++) {
            int noteX = previewX - 3 + (previewWidth - 16) * i / 5;
            int noteY = noteBaseY;

            int color = ParticleColorUtil.rgbToARGB(previewSet.getColors()[i]);

            g.setColor(
                    ((color >> 16) & 0xFF) / 255.0F,
                    ((color >> 8) & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F,
                    1.0F
            );

            g.blit(NOTE_TEXTURE, noteX + 5, noteY, 0, 0, 16, 8, 8, 8);
            g.blit(NOTE_TEXTURE, noteX, noteY + 8, 0, 0, 16, 8, 8, 8);

            g.setColor(1F, 1F, 1F, 1F);
        }

        currentY += 20;

        int previewY = currentY;
        int previewHeight = 10;

        g.fill(previewX - 1, previewY - 1,
                previewX + previewWidth + 1, previewY + previewHeight + 1, 0xFF909090);

        for (int x = 0; x < previewWidth; x++) {
            float t = (float) x / (previewWidth - 1);
            int color = ParticleColorUtil.getGradientColor(t, previewSet.getColors());
            color = ParticleColorUtil.rgbToARGB(color);

            g.fill(previewX + x, previewY,
                    previewX + x + 1, previewY + previewHeight, color);
        }

        for (int i = 1; i <= 4; i++) {
            int markX = previewX + (previewWidth * i) / 5;

            g.fill(markX, previewY, markX + 1, previewY + 3, 0xFF909090);
            g.fill(markX, previewY + previewHeight - 3,
                    markX + 1, previewY + previewHeight, 0xFF909090);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        minecraft.popGuiLayer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
            GIPacketHandler.sendToServer(new C2SColorSetAcceptPacket(encoded));
            onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}