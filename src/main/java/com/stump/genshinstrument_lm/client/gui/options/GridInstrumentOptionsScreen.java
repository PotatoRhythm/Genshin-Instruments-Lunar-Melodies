package com.stump.genshinstrument_lm.client.gui.options;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.config.enumType.ControlModeType;
import com.stump.genshinstrument_lm.client.config.enumType.NoteGridLabel;
import com.stump.genshinstrument_lm.client.config.enumType.ParticleColorType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.genshinstrument_lm.client.gui.options.partial.InstrumentOptionsScreen;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.packet.instrument.c2s.C2SParticleColorChangedPacket;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.GridLayout.RowHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public class GridInstrumentOptionsScreen extends InstrumentOptionsScreen {

    public GridInstrumentOptionsScreen(final GridInstrumentScreen screen) {
        super(screen);
    }
    public GridInstrumentOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }

    @Override
    public INoteLabel[] getLabels() {
        return NoteGridLabel.availableVals();
    }
    @Override
    public NoteGridLabel getCurrentLabel() {
        return ModClientConfigs.GRID_LABEL_TYPE.get();
    }

    @Override
    protected void saveLabel(final INoteLabel newLabel) {
        if (newLabel instanceof NoteGridLabel)
            ModClientConfigs.GRID_LABEL_TYPE.set((NoteGridLabel)newLabel);
    }

    @Override
    protected void initVisualsSection(GridLayout grid, RowHelper rowHelper) {
        final CycleButton<Boolean> renderBackground = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(ModClientConfigs.RENDER_BACKGROUND.get())
            .create(0, 0,
                getSmallButtonWidth(), getButtonHeight(),
                Component.translatable("button.genshinstrument_lm.render_background"), this::onRenderBackgroundChanged
            );
        rowHelper.addChild(renderBackground);

        final CycleButton<ParticleColorType> particleColorButton = CycleButton.<ParticleColorType>builder(color ->
                        Component.translatable("button.genshinstrument_lm.particle_color_type." + color.name().toLowerCase())
                )
                .withValues(ParticleColorType.values())
                .withInitialValue(ModClientConfigs.PARTICLE_COLOR_TYPE.get())
                .withTooltip(color -> Tooltip.create(Component.translatable("button.genshinstrument_lm.particle_color_type.tooltip")))
                .create(0, 0,
                        getSmallButtonWidth(), getButtonHeight(),
                        Component.translatable("button.genshinstrument_lm.particle_color_type"),
                        this::onParticleColorTypeChanged
                );
        rowHelper.addChild(particleColorButton);

        super.initVisualsSection(grid, rowHelper);
    }

    @Override
    protected void initControlSection(GridLayout grid, RowHelper rowHelper) {
        CycleButton<ControlModeType> controlModeButton = CycleButton.<ControlModeType>builder(mode ->
                        Component.translatable("button.genshinstrument_lm.control_mode." + mode.name().toLowerCase())
                )
                .withValues(ControlModeType.values())
                .withInitialValue(ModClientConfigs.CONTROL_MODE.get()) // optional persistent config
                .withTooltip((value) -> Tooltip.create(Component.translatable(value.getKey()+".description")))
                .create(0, 0,
                        getSmallButtonWidth(), getButtonHeight(), Component.translatable("button.genshinstrument_lm.control_mode"), this::onControlModeChanged
                );
        rowHelper.addChild(controlModeButton);

        final CycleButton<Boolean> extendRange = CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
                .withInitialValue(ModClientConfigs.EXTEND_RANGE.get())
                .withTooltip((value) -> Tooltip.create(Component.translatable("button.genshinstrument_lm.extend_range.tooltip")))
                .create(0, 0,
                        getSmallButtonWidth(), getButtonHeight(),
                        Component.translatable("button.genshinstrument_lm.extend_range"), this::onExtendRangeChanged
                );
        rowHelper.addChild(extendRange);

        super.initControlSection(grid, rowHelper);
    }

    protected void onRenderBackgroundChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.RENDER_BACKGROUND.set(value);
    }

    protected void onExtendRangeChanged(final CycleButton<Boolean> button, final boolean value) {
        ModClientConfigs.EXTEND_RANGE.set(value);
        instrumentScreen.ifPresent(screen -> {
            if (screen instanceof GridInstrumentScreen gridScreen) {
                gridScreen.updateOctaveRange(value);
            }
        });
    }
    protected void onControlModeChanged(final CycleButton<ControlModeType> button, final ControlModeType value) {
        ModClientConfigs.CONTROL_MODE.set(value);
    }

    protected void onParticleColorTypeChanged(final CycleButton<ParticleColorType> button, final ParticleColorType value) {
        ModClientConfigs.PARTICLE_COLOR_TYPE.set(value);
        GIPacketHandler.sendToServer(new C2SParticleColorChangedPacket(value.ordinal()));
    }

    // Register this options type as the main configs
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class,
            () -> new ConfigScreenFactory((minecraft, screen) -> new GridInstrumentOptionsScreen(screen))
        );
    }
    
}
