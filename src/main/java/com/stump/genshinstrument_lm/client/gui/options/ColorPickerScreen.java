package com.stump.genshinstrument_lm.client.gui.options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.function.IntConsumer;

public class ColorPickerScreen extends Screen {
    private final IntConsumer previewCallback;
    private final IntConsumer confirmCallback;

    private final int originalColor;
    private float hue, saturation, value;

    private EditBox hueField, satField, valField, hexField;

    private int panelX, panelY;
    private final int PANEL_WIDTH = 169;
    private final int PANEL_HEIGHT = 106;

    private boolean confirmed = false;

    private static final int SLIDER_X = 5;
    private static final int SLIDER_WIDTH = 125;
    private static final int SLIDER_HEIGHT = 10;
    private static final int PREVIEW_WIDTH = 40;

    private boolean draggingHue, draggingSat, draggingVal;

    public ColorPickerScreen(int initialColor, IntConsumer previewCallback, IntConsumer confirmCallback) {
        super(Component.literal("Color Picker"));

        this.previewCallback = previewCallback;
        this.confirmCallback = confirmCallback;
        this.originalColor = initialColor;

        int r = (initialColor >> 16) & 0xFF;
        int g = (initialColor >> 8) & 0xFF;
        int b = initialColor & 0xFF;

        float[] hsv = Color.RGBtoHSB(r, g, b, null);

        hue = hsv[0] * 360f;
        saturation = hsv[1] * 100f;
        value = hsv[2] * 100f;
    }

    @Override
    protected void init() {
        panelX = (width - PANEL_WIDTH) / 2 - 15;
        panelY = (height - PANEL_HEIGHT) / 2 - 9;

        hueField = new EditBox(font, panelX + SLIDER_X + SLIDER_WIDTH + 8, panelY + 15,
                26, 10, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if (!focused) {
                    setHighlightPos(0);
                    setCursorPosition(0);
                }
            }
        };

        satField = new EditBox(font, panelX + SLIDER_X + SLIDER_WIDTH + 8, panelY + 40,
                26, 10, Component.empty()){
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if (!focused) {
                    setHighlightPos(0);
                    setCursorPosition(0);
                }
            }
        };
        valField = new EditBox(font, panelX + SLIDER_X + SLIDER_WIDTH + 8, panelY + 65,
                26, 10, Component.empty()){
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if (!focused) {
                    setHighlightPos(0);
                    setCursorPosition(0);
                }
            }
        };
        hexField = new EditBox(font, panelX + PREVIEW_WIDTH + 10, panelY + 88,
                55, 12, Component.empty()){
            @Override
            public void setFocused(boolean focused) {
                super.setFocused(focused);

                if (!focused) {
                    setHighlightPos(0);
                    setCursorPosition(0);
                }
            }
        };

        hueField.setMaxLength(3);
        satField.setMaxLength(3);
        valField.setMaxLength(3);
        hexField.setMaxLength(10);

        hueField.setValue(String.valueOf((int) hue));
        satField.setValue(String.valueOf((int) saturation));
        valField.setValue(String.valueOf((int) value));
        hexField.setValue(colorToHex(getCurrentColor()));

        hueField.setResponder(text -> {
            try {
                hue = clamp(Integer.parseInt(text), 0, 360);
                sendPreview();
            } catch (Exception ignored) {}
        });

        satField.setResponder(text -> {
            try {
                saturation = clamp(Integer.parseInt(text), 0, 100);
                sendPreview();
            } catch (Exception ignored) {}
        });

        valField.setResponder(text -> {
            try {
                value = clamp(Integer.parseInt(text), 0, 100);
                sendPreview();
            } catch (Exception ignored) {}
        });

        hexField.setResponder(text -> {
            if (hexField.isFocused()) {
                Integer rgb = parseHexColor(text);

                if (rgb == null) return;

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                float[] hsv = Color.RGBtoHSB(r, g, b, null);

                hue = hsv[0] * 360f;
                saturation = hsv[1] * 100f;
                value = hsv[2] * 100f;

                sendPreview();
            }
        });

        addRenderableWidget(hueField);
        addRenderableWidget(satField);
        addRenderableWidget(valField);
        addRenderableWidget(hexField);

        addRenderableWidget(
                Button.builder(
                        Component.literal("Save"),
                        btn -> {
                            confirmed = true;
                            confirmCallback.accept(getCurrentColor());
                            onClose();
                        }
                ).bounds(
                        panelX + 128,
                        panelY + 87,
                        36,
                        14
                ).build()
        );

        sendPreview();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(panelX - 4, panelY - 4,
                panelX + PANEL_WIDTH + 4, panelY + PANEL_HEIGHT + 4, 0xFFB0AFB0);
        g.fill(panelX - 3, panelY - 3,
                panelX + PANEL_WIDTH + 3, panelY + PANEL_HEIGHT + 3, 0xFF9A999A);
        g.fill(panelX - 1, panelY - 1,
                panelX + PANEL_WIDTH + 1, panelY + PANEL_HEIGHT + 1, 0xFF262626);
        g.fill(panelX, panelY,
                panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xFF2D2D2D);

        g.drawString(font, "Hue", panelX + SLIDER_X, panelY + 5, 0xFFFFFF);
        g.drawString(font, "Saturation", panelX + SLIDER_X, panelY + 30, 0xFFFFFF);
        g.drawString(font, "Value", panelX + SLIDER_X, panelY + 55, 0xFFFFFF);

        g.fill(panelX + SLIDER_X - 1, panelY + 15 - 1,
                panelX + SLIDER_X + SLIDER_WIDTH + 1, panelY + 15 + SLIDER_HEIGHT + 1, 0xFF000000);
        g.fill(panelX + SLIDER_X - 1, panelY + 40 - 1,
                panelX + SLIDER_X + SLIDER_WIDTH + 1, panelY + 40 + SLIDER_HEIGHT + 1, 0xFF000000);
        g.fill(panelX + SLIDER_X - 1, panelY + 65 - 1,
                panelX + SLIDER_X + SLIDER_WIDTH + 1, panelY + 65 + SLIDER_HEIGHT + 1, 0xFF000000);
        drawSlider(g, panelX + SLIDER_X, panelY + 15, SLIDER_WIDTH, hue / 360f, SliderType.HUE);
        drawSlider(g, panelX + SLIDER_X, panelY + 40, SLIDER_WIDTH, saturation / 100f, SliderType.SATURATION);
        drawSlider(g, panelX + SLIDER_X, panelY + 65, SLIDER_WIDTH, value / 100f, SliderType.VALUE);

        int color = getCurrentColor();
        g.fill(panelX + SLIDER_X - 1, panelY + 88 - 1,
                panelX + PREVIEW_WIDTH + 1, panelY + 100 + 1, 0xFFA0A0A0);
        g.fill(panelX + SLIDER_X, panelY + 88,
                panelX + PREVIEW_WIDTH, panelY + 100, color);

        super.render(g, mouseX, mouseY, partialTick);
    }

    private void drawSlider(GuiGraphics g, int x, int y, int width, float position, SliderType type) {
        for (int i = 0; i < width; i++) {
            float t = i / (float) (width - 1);
            int color;

            switch (type) {
                case HUE ->
                        color = Color.HSBtoRGB(t, saturation / 100f, value / 100f);
                case SATURATION ->
                        color = Color.HSBtoRGB(hue / 360f, t, value / 100f);
                default ->
                        color = Color.HSBtoRGB(hue / 360f, saturation / 100f, t);
            }

            g.fill(x + i, y, x + i + 1, y + SLIDER_HEIGHT, 0xFF000000 | (color & 0xFFFFFF));
        }

        int knobX = x + (int) (position * width);
        g.fill(knobX - 1, y - 2, knobX + 1, y + SLIDER_HEIGHT + 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        draggingHue = insideSlider(mouseX, mouseY, panelY + 15);
        draggingSat = insideSlider(mouseX, mouseY, panelY + 40);
        draggingVal = insideSlider(mouseX, mouseY, panelY + 65);
        updateDragging(mouseX);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        updateDragging(mouseX);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingHue = false;
        draggingSat = false;
        draggingVal = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean insideSlider(double mouseX, double mouseY, int sliderY) {
        return mouseX >= panelX + SLIDER_X
                && mouseX <= panelX + SLIDER_X + SLIDER_WIDTH
                && mouseY >= sliderY
                && mouseY <= sliderY + SLIDER_HEIGHT;
    }

    private void updateDragging(double mouseX) {
        float t = (float) (
                (mouseX - (panelX + SLIDER_X))
                        / SLIDER_WIDTH
        );

        t = Math.max(0f, Math.min(1f, t));
        boolean changed = false;

        if (draggingHue) {
            hue = t * 360f;
            changed = true;
        }
        if (draggingSat) {
            saturation = t * 100f;
            changed = true;
        }
        if (draggingVal) {
            value = t * 100f;
            changed = true;
        }

        if (changed) {
            if (!hueField.isFocused()) {
                hueField.setValue(String.valueOf((int) hue));
            }
            if (!satField.isFocused()) {
                satField.setValue(String.valueOf((int) saturation));
            }
            if (!valField.isFocused()) {
                valField.setValue(String.valueOf((int) value));
            }
            if (!hexField.isFocused()) {
                hexField.setValue(colorToHex(getCurrentColor()));
                hexField.setCursorPosition(0);
                hexField.setHighlightPos(0);
            }

            sendPreview();
        }
    }

    private void sendPreview() {
        previewCallback.accept(getCurrentColor());
    }

    private int getCurrentColor() {
        int rgb = Color.HSBtoRGB(
                hue / 360f,
                saturation / 100f,
                value / 100f
        );

        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void onClose() {
        if (!confirmed) {
            previewCallback.accept(originalColor);
        }

        minecraft.popGuiLayer();
    }

    private enum SliderType {
        HUE,
        SATURATION,
        VALUE
    }

    private String colorToHex(int color) {
        return String.format("#%06X", color & 0xFFFFFF);
    }

    private Integer parseHexColor(String text) {
        if (text == null) {
            return null;
        }

        text = text.trim();

        if (text.startsWith("#")) {
            text = text.substring(1);
        } else if (text.startsWith("0x") || text.startsWith("0X")) {
            text = text.substring(2);
        }

        if (!text.matches("[0-9A-Fa-f]{6}")) {
            return null;
        }
        try {
            return Integer.parseInt(text, 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (hexField != null && !hexField.isFocused()) {
            Integer rgb = parseHexColor(hexField.getValue());
            if (rgb == null) {
                hexField.setValue(colorToHex(getCurrentColor()));
                hexField.setCursorPosition(0);
                hexField.setHighlightPos(0);
            } else {
                String normalized =
                        colorToHex(0xFF000000 | rgb);
                if (!hexField.getValue().equals(normalized)) {
                    hexField.setValue(normalized);
                    hexField.setCursorPosition(0);
                    hexField.setHighlightPos(0);
                }
            }
        }
    }
}