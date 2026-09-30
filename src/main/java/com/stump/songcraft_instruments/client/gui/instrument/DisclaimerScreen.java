package com.stump.songcraft_instruments.client.gui.instrument;

import java.awt.Color;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

/**
 * A disclaimer shown the first time an instrument derived from another game is opened,
 * until the player acknowledges it.
 * @implNote
 * This screen was heavily inspired by <a href=https://ko-fi.com/s/665c3cc518>Kistu-Lyre+</a>'s Disclaimer screen.
 * Please consider supporting them on Ko-fi!
 */
@OnlyIn(Dist.CLIENT)
public class DisclaimerScreen extends WarningScreen {

    private static final Component TITLE = Component.translatable(
        "songcraft_instruments.genshin_disclaimer.title"
    ).withStyle(ChatFormatting.BOLD);

    private final Screen previousScreen;
    private final BooleanValue acceptedConfig;

    /**
     * @param contentKey The translation key of the disclaimer's text
     * @param acceptedConfig The config value remembering that this disclaimer was acknowledged
     */
    private DisclaimerScreen(final Screen previousScreen, final String contentKey, final BooleanValue acceptedConfig) {
        this(previousScreen, Component.translatable(contentKey), acceptedConfig);
    }
    private DisclaimerScreen(final Screen previousScreen, final Component content, final BooleanValue acceptedConfig) {
        super(TITLE, content, null, TITLE.copy().append("\n").append(content));
        this.previousScreen = previousScreen;
        this.acceptedConfig = acceptedConfig;
    }

    public static DisclaimerScreen genshin(final Screen previousScreen) {
        return new DisclaimerScreen(previousScreen,
            "songcraft_instruments.genshin_disclaimer.content", ModClientConfigs.ACCEPTED_GENSHIN_CONSENT
        );
    }
    public static DisclaimerScreen gw2(final Screen previousScreen) {
        return new DisclaimerScreen(previousScreen,
            "songcraft_instruments.gw2_disclaimer.content", ModClientConfigs.ACCEPTED_GW2_CONSENT
        );
    }


    @Override
    protected int getLineHeight() {
        return 10;
    }

    @Override
    protected void init() {
        final Button acknowledgeButton = Button.builder(CommonComponents.GUI_ACKNOWLEDGE, (button) -> {
            acceptedConfig.set(true);
            minecraft.setScreen(previousScreen);
        }).build();


        // Make space for if the button is overlayed atop of the greeting
        final int preferredButtonY = 100 + 140, annoyingButtonY = height - acknowledgeButton.getHeight() - 10;

        acknowledgeButton.setPosition(
            (width - acknowledgeButton.getWidth()) / 2,
            Math.min(annoyingButtonY, preferredButtonY)
        );

        this.addRenderableWidget(acknowledgeButton);

        super.init();
    }

    @Override
    protected void renderTitle(GuiGraphics gui) {
        gui.drawCenteredString(font, title, width/2, 30, Color.WHITE.getRGB());
    }


    @Override
    public void onClose() {
        super.onClose();
        previousScreen.onClose();
    }


    @Override
    protected void initButtons(int idc) {}
}
