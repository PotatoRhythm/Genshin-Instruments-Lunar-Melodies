package com.stump.genshinstrument_lm.client.gui.options;

import com.stump.genshinstrument_lm.client.colorSet.ColorSet;
import com.stump.genshinstrument_lm.util.ParticleColorUtil;
import com.stump.genshinstrument_lm.util.ParticleShareUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import com.stump.genshinstrument_lm.client.colorSet.ColorSetManager;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ParticleEditorScreen extends Screen {
    private final Screen lastScreen;

    private static final Component TITLE =
            Component.translatable("button.genshinstrument_lm.particle_color_editor");
    private static final ResourceLocation NOTE_TEXTURE =
            new ResourceLocation("genshinstrument_lm", "textures/particle/custom_note.png");
    private static final ResourceLocation TRASH_TEXTURE =
            new ResourceLocation("genshinstrument_lm", "textures/gui/sprites/trash.png");

    private int backgroundPanelX;
    private int backgroundPanelY;
    private int backgroundPanelWidth;
    private int backgroundPanelHeight;

    // Left Panel
    private int leftPanelX;
    private int leftPanelY;
    private int leftPanelWidth;
    private int leftPanelHeight;

    private int leftPanelScroll = 0;
    private boolean draggingLeftScrollbar = false;
    private int scrollbarDragOffset = 0;

    // Right Panel
    private int rightPanelX;
    private int rightPanelY;
    private int rightPanelWidth;
    private int rightPanelHeight;

    private EditBox nameField;
    private final InvisibleButton[] colorButtons = new InvisibleButton[6];
    private final EditBox[] colorFields = new EditBox[6];
    private final int[] colorValues = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};

    // Bottom Panel
    private Button newSetButton;
    private Button deleteSetButton;

    private Button shareButton;
    private int clipboardMessageTicks = 0;
    private Button saveButton;
    private Button doneButton;

    public ParticleEditorScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        if (minecraft == null || minecraft.player == null) return;

        clearWidgets();

        initBackgroundPanel();
        initLeftPanel();
        initRightPanel();
        initBottomPanel();

        List<ColorSet> sets = ColorSetManager.getSets();
        if (sets.isEmpty()) {
            loadEmptyState();
            return;
        }

        int active = ColorSetManager.getActiveSet();

        if (active < 0 || active >= sets.size()) {
            active = 0;
            ColorSetManager.setActiveSet(active);
        }

        loadColorSet(active);
    }

    @Override
    public void resize(net.minecraft.client.@NotNull Minecraft minecraft, int w, int h) {
        super.resize(minecraft, w, h);
        this.init();
    }

    private void initBackgroundPanel() {
        backgroundPanelWidth = 250;
        backgroundPanelHeight = 226;
        backgroundPanelX = (width - backgroundPanelWidth) / 2;
        backgroundPanelY = (height - backgroundPanelHeight) / 2;
    }

    private void initLeftPanel() {
        leftPanelWidth = 88;
        leftPanelHeight = 202;
        leftPanelX = backgroundPanelX + 5;
        leftPanelY = backgroundPanelY + 5;
    }

    private void initRightPanel() {
        rightPanelWidth = backgroundPanelWidth - leftPanelWidth - 15;
        rightPanelHeight = 202;
        rightPanelX = leftPanelX + leftPanelWidth + 5;
        rightPanelY = backgroundPanelY + 5;

        int padding = 5;
        int panelPaddingX = 15;
        int currentY = rightPanelY + 4;

        // Name Field
        currentY += 14; // label
        int nameFieldHeight = 14;
        nameField = createEditBox(nameField, rightPanelX + panelPaddingX, currentY,
                rightPanelWidth - panelPaddingX * 2, nameFieldHeight, Component.literal("Name"));
        addRenderableWidget(nameField);
        currentY += nameFieldHeight + padding;

        // Color Field and Buttons
        currentY += 19; // label
        int rowHeight = 12;
        int rowWidth = 55;

        for (int i = 0; i < 6; i++) {
            // Color Entry Field
            int rowY = currentY + i * (rowHeight + padding);
            int fieldX = rightPanelX + panelPaddingX + 27;
            if (colorFields[i] == null) {
                final int index = i;
                colorFields[i] = createEditBox(null, fieldX, rowY,
                        rowWidth, rowHeight, Component.literal("Color " + (i + 1)));

                colorFields[i].setValue(ParticleColorUtil.colorToHex(colorValues[index]));
                colorFields[i].setResponder(text -> {
                    Integer rgb = ParticleColorUtil.parseHexColor(text);
                    if (rgb != null) {
                        colorValues[index] = 0xFF000000 | rgb;
                    }
                });

            } else {
                EditBox colorField = colorFields[i];
                colorField.setX(fieldX);
                colorField.setY(rowY);
                colorField.setWidth(rowWidth);
                colorField.setHeight(rowHeight);

                if (!colorField.isFocused()) {
                    String expected = ParticleColorUtil.colorToHex(colorValues[i]);
                    if (!colorField.getValue().equals(expected)) {
                        colorField.setValue(expected);
                        colorField.setCursorPosition(0);
                        colorField.setHighlightPos(0);
                    }
                }
            }

            // Color Button
            int rectX = rightPanelX + panelPaddingX + 29 + rowWidth + padding;
            int rectY = rowY - 2;
            if (colorButtons[i] == null) {
                final int index = i;
                colorButtons[i] = new InvisibleButton(
                        rectX,
                        rectY - 1,
                        18,
                        rowHeight + 4,
                        btn -> openColorPicker(index)
                );
            } else {
                colorButtons[i].setX(rectX);
                colorButtons[i].setY(rectY - 1);
                colorButtons[i].setWidth(18);
                colorButtons[i].setHeight(rowHeight + 4);
            }

            addRenderableWidget(colorFields[i]);
            addRenderableWidget(colorButtons[i]);
        }
    }

    private void initBottomPanel() {
        int bottomPanelWidth = backgroundPanelWidth - 10;
        int bottomPanelHeight = (int) (backgroundPanelHeight * 0.05f) + 1;
        int bottomPanelX = backgroundPanelX + 5;
        int bottomPanelY = backgroundPanelY + backgroundPanelHeight - bottomPanelHeight - 6;

        int buttonHeight = 15;
        int newSetWidth = 55;
        int deleteWidth = 15;
        int doneWidth = 42;
        int saveWidth = 42;
        int shareWidth = 45;
        int padding = 5;

        int buttonY = bottomPanelY + (bottomPanelHeight - buttonHeight) / 2 + 2;
        int newSetX = bottomPanelX + 6;
        int deleteX = newSetX + newSetWidth + 5;
        int doneX = bottomPanelX + bottomPanelWidth - doneWidth - 4;
        int saveX = doneX - saveWidth - padding;
        int shareX = saveX - shareWidth - padding;

        newSetButton = createButton(newSetButton, Component.literal("New Set"), newSetX, buttonY,
                newSetWidth, buttonHeight, btn -> onNewSet());
        deleteSetButton = createButton(deleteSetButton, Component.literal(""), deleteX, buttonY,
                deleteWidth, buttonHeight, btn -> onDelete());
        shareButton = createButton(shareButton,Component.literal("Share"),shareX,buttonY,
                shareWidth, buttonHeight, btn -> onShare());
        saveButton = createButton(saveButton, Component.literal("Save"), saveX, buttonY,
                saveWidth, buttonHeight, btn -> onSave());
        doneButton = createButton(doneButton, Component.literal("Done"), doneX, buttonY,
                doneWidth, buttonHeight, btn -> onDone());

        addRenderableWidget(newSetButton);
        addRenderableWidget(deleteSetButton);
        addRenderableWidget(shareButton);
        addRenderableWidget(saveButton);
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float pt) {
        renderBackground(g);
        renderBackgroundPanel(g);
        renderLeftPanel(g);
        renderRightPanel(g);

        super.render(g, mouseX, mouseY, pt);

        renderTrash(g);
        renderSharePopup(g);
    }

    private void renderBackgroundPanel(GuiGraphics g) {
        int borderColor = 0xFFB0AFB0;
        int backgroundColor = 0xFF9A999A;

        g.fill(backgroundPanelX, backgroundPanelY,
                backgroundPanelX + backgroundPanelWidth,
                backgroundPanelY + backgroundPanelHeight,
                borderColor);
        g.fill(backgroundPanelX + 1, backgroundPanelY + 1,
                backgroundPanelX + backgroundPanelWidth - 1,
                backgroundPanelY + backgroundPanelHeight - 1,
                backgroundColor);
    }

    private void renderLeftPanel(GuiGraphics g) {
        int borderColor = 0xFF474746;
        int backgroundColor = 0xFF161617;

        g.fill(leftPanelX, leftPanelY, leftPanelX + leftPanelWidth,
                leftPanelY + leftPanelHeight,
                borderColor);
        g.fill(leftPanelX + 1, leftPanelY + 1,
                leftPanelX + leftPanelWidth - 1, leftPanelY + leftPanelHeight - 1,
                backgroundColor);

        int titleY = leftPanelY + 6;

        g.drawString(minecraft.font, "Saved Sets", leftPanelX + 14, titleY, 0xFFFFFF);
        g.fill(leftPanelX + 7, titleY + 10,
                leftPanelX + leftPanelWidth - 12, titleY + 11,
                0xFF808080);

        int entryY = titleY + 15 - leftPanelScroll;
        int entryHeight = 20;
        int entrySpacing = 3;

        g.enableScissor(leftPanelX + 1, leftPanelY + 19,
                leftPanelX + leftPanelWidth - 7, leftPanelY + leftPanelHeight - 1);
        for (int i = 0; i < ColorSetManager.getSets().size(); i++) {
            if (entryY + entryHeight < leftPanelY + 12) {
                entryY += entryHeight + entrySpacing;
                continue;
            }

            if (entryY > leftPanelY + leftPanelHeight) {
                break;
            }

            ColorSet set = ColorSetManager.getSets().get(i);

            boolean selected = i == ColorSetManager.getActiveSet();
            int border = selected ? 0xFFF5A300 : 0xFF606060;
            int background = selected ? 0xFF242424 : 0xFF1D1D1F;

            int entryX = leftPanelX + 4;
            int entryWidth = leftPanelWidth - 13;

            g.fill(entryX, entryY, entryX + entryWidth, entryY + entryHeight, border);
            g.fill(entryX + 1, entryY + 1, entryX + entryWidth - 1, entryY + entryHeight - 1, background);

            String displayName = getTruncatedSetName(set.getName());
            g.drawString(minecraft.font, displayName, entryX + 3, entryY + 3, 0xFFFFFF);

            // gradient preview
            int previewX = entryX + 3;
            int previewY = entryY + 13;
            int previewWidth = entryWidth - 6;
            int previewHeight = 3;

            int[] colors = set.getColors();

            for (int x = 0; x < previewWidth; x++) {
                float t = (float)x / (previewWidth - 1);
                int color = ParticleColorUtil.getGradientColor(t, colors);
                color = ParticleColorUtil.rgbToARGB(color);
                g.fill(previewX + x, previewY, previewX + x + 1, previewY + previewHeight, color);
            }
            entryY += entryHeight + entrySpacing;
        }
        g.disableScissor();

        int contentHeight = getLeftContentHeight();
        int maxScroll = getMaxLeftScroll();

        int trackX = leftPanelX + leftPanelWidth - 5;
        int trackY = leftPanelY + 24;
        int trackHeight = leftPanelHeight - 36;

        g.fill(trackX, trackY - 2, trackX + 1, trackY + trackHeight + 2, 0xFFC3C3C3);

        int thumbHeight;

        if (maxScroll <= 0) {
            thumbHeight = trackHeight;
        } else {
            thumbHeight = Math.max(12, (trackHeight * trackHeight) / contentHeight);
        }

        int thumbY;

        if (maxScroll <= 0) {
            thumbY = trackY;
        } else {
            thumbY = trackY + ((trackHeight - thumbHeight) * leftPanelScroll / maxScroll);
        }

        g.fill(trackX - 1, thumbY, trackX - 1 + 3, thumbY + thumbHeight, 0xFF616161);
        g.fill(trackX, thumbY + 1, trackX + 1, thumbY + thumbHeight - 1, 0xFF6F6F6F);

        int upY = trackY - 7;
        int downY = trackY + trackHeight + 5;

        g.fill(trackX, upY, trackX + 1, upY + 1, 0xFFC3C3C3);
        g.fill(trackX - 1, upY + 1, trackX + 2, upY + 2, 0xFFC3C3C3);
        g.fill(trackX - 2, upY + 2, trackX + 3, upY + 3, 0xFFC3C3C3);

        g.fill(trackX - 2, downY, trackX + 3, downY + 1, 0xFFC3C3C3);
        g.fill(trackX - 1, downY + 1, trackX + 2, downY + 2, 0xFFC3C3C3);
        g.fill(trackX, downY + 2, trackX + 1, downY + 3, 0xFFC3C3C3);
    }

    private void renderRightPanel(GuiGraphics g) {
        int borderColor = 0xFF474746;
        int backgroundColor = 0xFF161617;

        g.fill(rightPanelX, rightPanelY, rightPanelX + rightPanelWidth, rightPanelY + rightPanelHeight, borderColor);
        g.fill(rightPanelX + 1, rightPanelY + 1, rightPanelX + rightPanelWidth - 1, rightPanelY + rightPanelHeight - 1, backgroundColor);

        int padding = 5;
        int panelPaddingX = 15;
        g.drawString(minecraft.font, "Color Set Name:", rightPanelX + panelPaddingX + 4, rightPanelY + 6, 0xFFFFFF);

        int currentY = rightPanelY + padding + 20 + 11 + padding;
        g.drawString(minecraft.font, "Enter Octave Colors", rightPanelX + panelPaddingX + 4, currentY, 0xFFFFFF);

        currentY += 15;

        int rowHeight = 12;
        int rowSpacing = 5;

        int[] octaveLabels = {-2, -1, 0, 1, 2, 3};

        for (int i = 0; i < 6; i++) {
            int rowY = currentY + i * (rowHeight + rowSpacing);

            String numberText = String.valueOf(octaveLabels[i]);
            int rightEdge = rightPanelX + panelPaddingX + 20;
            int textX = rightEdge - minecraft.font.width(numberText);

            g.drawString(minecraft.font, numberText, textX, rowY + 4, 0xFFFFFF);

            int rectX = rightPanelX + panelPaddingX + 5 + 15 + 63 + 4;
            int rectY = rowY - 3;

            g.setColor(((colorValues[i] >> 16) & 0xFF) / 255.0F,
                    ((colorValues[i] >> 8) & 0xFF) / 255.0F,
                    (colorValues[i] & 0xFF) / 255.0F,
                    1.0F
            );

            g.blit(NOTE_TEXTURE, rectX + 5, rectY, 0, 0, 16, 8, 8, 8);
            g.blit(NOTE_TEXTURE, rectX, rectY + 8, 0, 0, 16, 8, 8, 8);

            // reset color
            g.setColor(1F, 1F, 1F, 1F);
        }

        int previewY = currentY + 6 * (rowHeight + rowSpacing) + 5;
        g.drawString(minecraft.font, "Preview", rightPanelX + panelPaddingX + 4, previewY, 0xFFFFFF);
        int previewX = rightPanelX + panelPaddingX;
        int previewWidth = rightPanelWidth - (panelPaddingX * 2);
        int previewHeight = 15;

        g.fill(previewX - 1, previewY + 11, previewX + previewWidth + 1, previewY + 13 + previewHeight, 0xFF909090);

        for (int x = 0; x < previewWidth; x++) {
            float t = (float)x / (previewWidth - 1);
            int color = ParticleColorUtil.getGradientColor(t, colorValues);
            color = ParticleColorUtil.rgbToARGB(color);
            g.fill(previewX + x, previewY + 12, previewX + x + 1, previewY + 12 + previewHeight, color);
        }

        int gradientY = previewY + 12;

        for (int i = 1; i <= 4; i++) {
            int markX = previewX + (previewWidth * i) / 5;
            g.fill(markX, gradientY, markX + 1, gradientY + 3, 0xFF909090);
            g.fill(markX, gradientY + previewHeight - 3, markX + 1, gradientY + previewHeight, 0xFF909090);
        }
    }

    private void renderTrash(GuiGraphics g) {
        int trashX = deleteSetButton.getX() + (deleteSetButton.getWidth() - 11) / 2;
        int trashY = deleteSetButton.getY() + (deleteSetButton.getHeight() - 10) / 2;
        g.blit(TRASH_TEXTURE, trashX, trashY, 0, 0,
                11, 10, 11, 10);
    }

    private void renderSharePopup(GuiGraphics g) {
        if (clipboardMessageTicks > 0) {
            String text = "Color set copied to clipboard!";

            int w = font.width(text) + 10;
            int h = 14;
            int x = rightPanelX + 15;
            int y = rightPanelY + rightPanelHeight - h;

            g.fill(x, y, x + w, y + h, 0xCC000000);
            g.drawString(font, text, x + 5, y + 3, 0xFFFFFF);
        }
    }

    private void onNewSet() {
        List<ColorSet> sets = ColorSetManager.getSets();

        int[] colors = {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF};
        ColorSet newSet = new ColorSet("Custom Set", colors);

        sets.add(newSet);

        int newIndex = sets.size() - 1;

        ColorSetManager.setActiveSet(newIndex);
        ColorSetManager.save();

        loadColorSet(newIndex);
    }

    private void onDelete() {
        int selectedSetIndex = ColorSetManager.getActiveSet();

        if (selectedSetIndex < 0 || selectedSetIndex >= ColorSetManager.getSets().size()) {
            return;
        }

        String rawName = ColorSetManager.getSets().get(selectedSetIndex).getName();
        String truncatedName = getTruncatedSetName(rawName);

        minecraft.pushGuiLayer(
                new ConfirmationScreen(
                        Component.literal("Delete " + truncatedName + "?"),
                        this::deleteSelectedSet
                )
        );
    }

    private void deleteSelectedSet() {
        List<ColorSet> sets = ColorSetManager.getSets();

        int index = ColorSetManager.getActiveSet();
        sets.remove(index);

        if (sets.isEmpty()) {
            ColorSetManager.setActiveSet(-1);
            loadEmptyState();
            ColorSetManager.save();
            return;
        }

        int newIndex = Math.min(index, sets.size() - 1);
        ColorSetManager.setActiveSet(newIndex);
        loadColorSet(newIndex);
        ColorSetManager.save();

        nameField.setValue("Custom Set");

        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i] = 0xFFFFFFFF;
            if (colorFields[i] != null) {
                colorFields[i].setValue("#FFFFFF");
            }
        }
    }

    private void onShare() {
        int active = ColorSetManager.getActiveSet();

        if (active < 0) {
            return;
        }

        ColorSet set = ColorSetManager.getSets().get(active);
        String share = ParticleShareUtil.encode(set);

        minecraft.keyboardHandler.setClipboard(share);

        clipboardMessageTicks = 60;
    }

    private void onSave() {
        List<ColorSet> sets = ColorSetManager.getSets();
        int selectedSetIndex = ColorSetManager.getActiveSet();

        int[] colors = colorValues.clone();
        ColorSet newSet = new ColorSet(nameField.getValue().trim(), colors);

        if (selectedSetIndex >= 0 && selectedSetIndex < sets.size()) {
            sets.set(selectedSetIndex, newSet);
        } else {
            sets.add(newSet);
            selectedSetIndex = sets.size() - 1;
        }

        ColorSetManager.setActiveSet(selectedSetIndex);
        ColorSetManager.save();
    }

    private void onDone() {
        onSave();
        onClose();
    }

    @Override
    public void onClose() {
        super.onClose();
        if (lastScreen != null)
            minecraft.pushGuiLayer(lastScreen);
    }

    @Override
    public void tick() {
        super.tick();

        for (int i = 0; i < colorFields.length; i++) {
            if (colorFields[i] != null && !colorFields[i].isFocused()) {
                Integer rgb = ParticleColorUtil.parseHexColor(colorFields[i].getValue());

                if (rgb == null) {
                    colorFields[i].setValue(ParticleColorUtil.colorToHex(colorValues[i]));
                    colorFields[i].setCursorPosition(0);
                    colorFields[i].setHighlightPos(0);
                } else {
                    colorValues[i] = 0xFF000000 | rgb;

                    String normalized = ParticleColorUtil.colorToHex(colorValues[i]);
                    if (!colorFields[i].getValue().equals(normalized)) {
                        colorFields[i].setValue(normalized);
                        colorFields[i].setCursorPosition(0);
                        colorFields[i].setHighlightPos(0);
                    }
                }
            }
        }

        if (clipboardMessageTicks > 0) {
            clipboardMessageTicks--;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = leftPanelY + 21 - leftPanelScroll;
        int visibleTop = leftPanelY + 18;
        int visibleBottom = leftPanelY + leftPanelHeight - 1;

        for (int i = 0; i < ColorSetManager.getSets().size(); i++) {
            // above visible area
            if (y + 20 < visibleTop) {
                y += 23;
                continue;
            }
            // below visible area
            if (y > visibleBottom) {
                break;
            }

            int clickTop = Math.max(y, visibleTop);
            int clickBottom = Math.min(y + 20, visibleBottom);
            if (mouseX >= leftPanelX + 4
                    && mouseX <= leftPanelX + leftPanelWidth - 9
                    && mouseY >= clickTop
                    && mouseY <= clickBottom) {

                loadColorSet(i);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            y += 23;
        }

        int trackX = getScrollbarTrackX();
        int trackY = getScrollbarTrackY();
        int trackHeight = getScrollbarTrackHeight();

        int thumbY = getScrollbarThumbY();
        int thumbHeight = getScrollbarThumbHeight();

        // Up/Down Arrows
        int upY = trackY - 7;
        int downY = trackY + trackHeight + 5;
        if (mouseX >= trackX - 3
                && mouseX <= trackX + 4
                && mouseY >= upY - 1
                && mouseY <= upY + 4) {
            leftPanelScroll = Mth.clamp(leftPanelScroll - 23, 0, getMaxLeftScroll());
            return true;
        }
        if (mouseX >= trackX - 3
                && mouseX <= trackX + 4
                && mouseY >= downY - 1
                && mouseY <= downY + 4) {
            leftPanelScroll = Mth.clamp(leftPanelScroll + 23, 0, getMaxLeftScroll());
            return true;
        }
        if (mouseX >= trackX - 2
                && mouseX <= trackX + 3
                && mouseY >= trackY
                && mouseY <= trackY + trackHeight) {

            int maxScroll = getMaxLeftScroll();
            if (maxScroll > 0) {
                if (mouseY < thumbY || mouseY > thumbY + thumbHeight) {
                    int newThumbY = Mth.clamp((int)mouseY - thumbHeight / 2, trackY, trackY + trackHeight - thumbHeight);
                    float percent = (float)(newThumbY - trackY) / (trackHeight - thumbHeight);
                    leftPanelScroll = (int)(percent * maxScroll);
                    thumbY = getScrollbarThumbY();
                }
                draggingLeftScrollbar = true;
                scrollbarDragOffset = (int)mouseY - thumbY;
            }
            return true;
        }

        boolean result =super.mouseClicked(mouseX, mouseY, button);
        //remove button highlight
        if (getFocused() instanceof net.minecraft.client.gui.components.Button) {
            setFocused(null);
        }

        return result;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingLeftScrollbar) {
            int trackY = getScrollbarTrackY();
            int trackHeight = getScrollbarTrackHeight();
            int thumbHeight = getScrollbarThumbHeight();
            int thumbY = Mth.clamp((int)mouseY - scrollbarDragOffset, trackY, trackY + trackHeight - thumbHeight);
            int maxScroll = getMaxLeftScroll();

            if (maxScroll > 0) {
                float percent = (float)(thumbY - trackY) / (trackHeight - thumbHeight);
                leftPanelScroll = (int)(percent * maxScroll);
            }
            else {
                leftPanelScroll = 0;
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingLeftScrollbar = false;
        return super.mouseReleased(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mouseX >= leftPanelX
                && mouseX <= leftPanelX + leftPanelWidth
                && mouseY >= leftPanelY
                && mouseY <= leftPanelY + leftPanelHeight) {

            leftPanelScroll -= (int)(delta * 12);
            leftPanelScroll = Mth.clamp(leftPanelScroll, 0, getMaxLeftScroll());

            initLeftPanel();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private int getScrollbarTrackX() {
        return leftPanelX + leftPanelWidth - 5;
    }
    private int getScrollbarTrackY() {
        return leftPanelY + 24;
    }
    private int getScrollbarTrackHeight() {
        return leftPanelHeight - 36;
    }
    private int getScrollbarThumbHeight() {
        int maxScroll = getMaxLeftScroll();
        if (maxScroll <= 0) {
            return getScrollbarTrackHeight();
        }
        return Math.max(12, (getScrollbarTrackHeight() * getScrollbarTrackHeight()) / getLeftContentHeight());
    }
    private int getScrollbarThumbY() {
        int maxScroll = getMaxLeftScroll();

        if (maxScroll <= 0) {
            return getScrollbarTrackY();
        }

        return getScrollbarTrackY() + ((getScrollbarTrackHeight()
                 - getScrollbarThumbHeight()) * leftPanelScroll / maxScroll);
    }
    private int getLeftContentHeight() {
        int count = ColorSetManager.getSets().size();
        return count * 23 + 3;
    }
    private int getMaxLeftScroll() {
        int visibleHeight =leftPanelHeight - 18;
        return Math.max(0, getLeftContentHeight() - visibleHeight);
    }

    private EditBox createEditBox(EditBox box, int x, int y, int width, int height, Component message) {
        if (box == null) {
            box = new EditBox(minecraft.font, x, y, width, height, message) {
                @Override
                public void setFocused(boolean focused) {
                    super.setFocused(focused);
                    if (!focused)
                        setHighlightPos(getCursorPosition());
                }
            };
        } else {
            box.setX(x);
            box.setY(y);
            box.setWidth(width);
            box.setHeight(height);
        }
        return box;
    }

    private Button createButton(Button button, Component label, int x, int y, int w, int h, Button.OnPress action) {
        if (button == null) {
            button = Button.builder(label, action)
                    .bounds(x, y, w, h)
                    .build();
        } else {
            button.setPosition(x, y);
            button.setWidth(w);
            button.setHeight(h);
        }
        return button;
    }

    private static class InvisibleButton extends net.minecraft.client.gui.components.Button {
        public InvisibleButton(int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        }
        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // render nothing
        }
    }

    private void loadColorSet(int index) {
        List<ColorSet> sets = ColorSetManager.getSets();

        if (index < 0 || index >= sets.size()) return;

        ColorSet set = sets.get(index);

        // single source of truth
        ColorSetManager.setActiveSet(index);
        nameField.setValue(set.getName());

        int[] colors = set.getColors();
        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i] = i < colors.length ? colors[i] : 0xFFFFFFFF;

            if (colorFields[i] != null) {
                colorFields[i].setValue(ParticleColorUtil.colorToHex(colorValues[i]));
            }
        }
    }

    private void loadEmptyState() {
        ColorSetManager.setActiveSet(-1);

        nameField.setValue("Custom Set");

        for (int i = 0; i < colorValues.length; i++) {
            colorValues[i] = 0xFFFFFFFF;
            if (colorFields[i] != null) {
                colorFields[i].setValue("#FFFFFF");
            }
        }
    }

    private void openColorPicker(int colorIndex) {
        minecraft.pushGuiLayer(
                new ColorPickerScreen(
                        colorValues[colorIndex],
                        previewColor -> {
                            colorValues[colorIndex] = previewColor;
                            if (colorFields[colorIndex] != null) {
                                colorFields[colorIndex].setValue(ParticleColorUtil.colorToHex(previewColor));
                            }
                        },
                        confirmedColor -> {
                            colorValues[colorIndex] = confirmedColor;
                            if (colorFields[colorIndex] != null) {
                                colorFields[colorIndex].setValue(ParticleColorUtil.colorToHex(confirmedColor));
                            }
                        }
                )
        );
    }

    private String getTruncatedSetName(String name) {
        int entryWidth = leftPanelWidth - 13;
        int textWidth = entryWidth - 6;

        String displayName = minecraft.font.plainSubstrByWidth(name, textWidth);

        if (!displayName.equals(name)) {
            displayName += "..";
        }

        return displayName;
    }
}