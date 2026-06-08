package com.stump.genshinstrument_lm.client.gui.options;

import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleEditorScreen extends Screen {
    private final Screen lastScreen;
    private static final Component TITLE =
            Component.translatable("button.genshinstrument_lm.particle_color_editor");

    public ParticleEditorScreen(Screen lastScreen, InstrumentScreen instrumentScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        clearWidgets();
        initLeftPanel();
        initRightPanel();
        initBottomPanel();
    }

    @Override
    public void resize(net.minecraft.client.Minecraft minecraft, int w, int h) {
        super.resize(minecraft, w, h);
        this.init();
    }

    private void initLeftPanel() {}


    private void initRightPanel() {}

    private void initBottomPanel() {}


    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (lastScreen != null)
            minecraft.pushGuiLayer(lastScreen);
    }
}