package com.stump.songcraft_instruments.client.gui.instrument.vintagelyre;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.gui.options.partial.SingleButtonOptionsScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VintageLyreOptionsScreen extends SingleButtonOptionsScreen {

    public VintageLyreOptionsScreen(final VintageLyreScreen screen) {
        super(screen);
    }

    @Override
    protected String optionsLabelKey() {
        return "label.songcraft_instruments.vintage_lyre_options";
    }


    @Override
    protected AbstractButton constructButton() {
        return CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.NORMALIZE_VINTAGE_LYRE.get())
            .withTooltip((value) -> Tooltip.create(Component.translatable("button.songcraft_instruments.normalize_vintage_lyre.tooltip")))
            .create(0, 0,
                getBigButtonWidth(), getButtonHeight(),
                Component.translatable("button.songcraft_instruments.normalize_vintage_lyre"), this::onNormalizeLyreChanged
            );
    }

    private void onNormalizeLyreChanged(CycleButton<Boolean> button, Boolean value) {
        ModClientConfigs.NORMALIZE_VINTAGE_LYRE.set(value);
    }
}