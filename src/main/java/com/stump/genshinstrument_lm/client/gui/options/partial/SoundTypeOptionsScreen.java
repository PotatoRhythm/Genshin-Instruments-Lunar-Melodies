package com.stump.genshinstrument_lm.client.gui.options.partial;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.client.config.enumType.SoundType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.util.TogglablePedalSound;
import com.stump.genshinstrument_lm.event.MidiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * An options screen that implements a button to cycle through the instrument's sounds.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = GInstrumentMod.MODID, value = Dist.CLIENT)
public abstract class SoundTypeOptionsScreen<T extends SoundType> extends SingleButtonOptionsScreen {

    public SoundTypeOptionsScreen(final GridInstrumentScreen screen) {
        super(screen);
    }
    public SoundTypeOptionsScreen(final Screen lastScreen) {
        super(lastScreen);
    }


    private T preferredSoundType = getInitSoundType();

    public T getPreferredSoundType() {
        return preferredSoundType;
    }
    public void setPreferredSoundType(T preferredSoundType) {
        this.preferredSoundType = preferredSoundType;

        if (instrumentScreen.map(this::isValidForSet).orElse(false))
            instrumentScreen.get().setSoundOption(preferredSoundType.getSoundArr().get());
    }

    protected abstract T getInitSoundType();
    protected abstract T[] values();

    protected abstract String soundTypeButtonKey();


    public AbstractButton createSoundTypeButton(final int width) {
        return constructSoundTypeButton(width);
    }

    protected CycleButton<T> constructSoundTypeButton(final int width) {
        return CycleButton.<T>builder((soundType) ->
                Component.translatable(soundTypeButtonKey() + "." + soundType.getName()))
                .withValues(values()).withInitialValue(getPreferredSoundType())
                .create(0, 0, width, getButtonHeight(),
                        Component.translatable(soundTypeButtonKey()),
                        this::onSoundTypeChange
                );
    }

    @Override
    protected AbstractButton constructButton() {
        return constructSoundTypeButton(getBigButtonWidth());
    }

    protected void onSoundTypeChange(final CycleButton<T> btn, final T soundType) {
        setPreferredSoundType(soundType);

        instrumentScreen.ifPresent((screen) ->
            queueToSave(screen.getInstrumentId().getPath() + "_sound_type", () -> saveSoundType(soundType))
        );
    }
    protected abstract void saveSoundType(final T soundType);

    protected abstract boolean isValidForSet(final InstrumentScreen screen);



    /* ----------- MIDI Pedal Behaviour ----------- */

    /**
     * @return The sounds that should be used upon MIDI pedal events by this instrument. Null for none.
     */
    public TogglablePedalSound<T> midiPedalListener() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onMidiReceivedEvent(final MidiEvent event) {
        final InstrumentScreen instrumentScreen = InstrumentScreen.getCurrentScreen(Minecraft.getInstance()).orElse(null);

        if ((instrumentScreen == null)
            || !(instrumentScreen.optionsScreen instanceof SoundTypeOptionsScreen optionsScreen)
        ) return;

        final var pedalSounds = optionsScreen.midiPedalListener();
        if (optionsScreen.midiPedalListener() == null)
            return;


        final byte[] message = event.message.getMessage();

        // Only listen for pedal events
        // Check 80 too bc FreePiano
        if (((message[0] != -80) && (message[0] != -176)) || (message[1] != 64))
            return;


        //NOTE: I did not test this on an actual pedal, this value might need to be flipped
        optionsScreen.setPreferredSoundType((message[2] >= 64) ? pedalSounds.enabled : pedalSounds.disabled);
    }

}
