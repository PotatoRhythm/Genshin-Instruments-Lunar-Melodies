package com.stump.songcraft_instruments.client.gui.instrument.microphone;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.MicrophoneSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MicrophoneOptionsScreen extends SoundTypeOptionsScreen<MicrophoneSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.microphone.soundType",
        OPTIONS_LABEL_KEY = "label.songcraft_instruments.microphone_options";

    public MicrophoneOptionsScreen(final GridInstrumentScreen screen) {
        super(screen);
    }
    

    @Override
    protected String soundTypeButtonKey() {
        return SOUND_TYPE_KEY;
    }
    @Override
    protected String optionsLabelKey() {
        return OPTIONS_LABEL_KEY;
    }


    @Override
    protected MicrophoneSoundType getInitSoundType() {
        return ModClientConfigs.MICROPHONE_SOUND_TYPE.get();
    }

    @Override
    protected MicrophoneSoundType[] values() {
        return MicrophoneSoundType.values();
    }


    @Override
    protected void saveSoundType(MicrophoneSoundType soundType) {
        ModClientConfigs.MICROPHONE_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof MicrophoneScreen || screen instanceof MicrophoneStandScreen;
    }
}
