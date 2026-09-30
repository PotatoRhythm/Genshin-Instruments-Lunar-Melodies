package com.stump.songcraft_instruments.client.gui.instrument.partial;

import com.stump.songcraft_instruments.SCInstrumentMod;
import com.stump.songcraft_instruments.client.config.enumType.SoundType;
import com.stump.songcraft_instruments.client.util.TogglablePedalSound;
import com.stump.songcraft_instruments.event.MidiEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;

/**
 * The sound types an instrument can switch between, via a button next to its volume slider.
 * @see InstrumentScreen#soundTypeOption
 *
 * @param values All sound types of the instrument
 * @param config The config value storing the chosen sound type
 * @param buttonKey The translation key of the button cycling through the sound types
 * @param midiPedal The sound types to switch between upon MIDI pedal events. Null for none.
 */
@OnlyIn(Dist.CLIENT)
public record SoundTypeOption<T extends Enum<T> & SoundType>(
    T[] values,
    EnumValue<T> config,
    String buttonKey,
    @Nullable TogglablePedalSound<T> midiPedal
) {
    public SoundTypeOption(final T[] values, final EnumValue<T> config, final String buttonKey) {
        this(values, config, buttonKey, null);
    }


    @EventBusSubscriber(bus = Bus.FORGE, modid = SCInstrumentMod.MODID, value = Dist.CLIENT)
    private static class MidiPedalListener {
        @SubscribeEvent
        public static void onMidiReceivedEvent(final MidiEvent event) {
            final InstrumentScreen instrumentScreen = InstrumentScreen.getCurrentScreen(Minecraft.getInstance()).orElse(null);
            if (instrumentScreen == null)
                return;

            final SoundTypeOption<?> option = instrumentScreen.soundTypeOption();
            if ((option == null) || (option.midiPedal() == null))
                return;


            final byte[] message = event.message.getMessage();

            // Only listen for pedal events
            // Check 80 too bc FreePiano
            if (((message[0] != -80) && (message[0] != -176)) || (message[1] != 64))
                return;


            //NOTE: I did not test this on an actual pedal, this value might need to be flipped
            instrumentScreen.setPreferredSoundType(
                (message[2] >= 64) ? option.midiPedal().enabled : option.midiPedal().disabled
            );
        }
    }
}
