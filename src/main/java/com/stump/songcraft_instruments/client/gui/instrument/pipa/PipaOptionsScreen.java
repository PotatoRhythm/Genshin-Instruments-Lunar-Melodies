package com.stump.songcraft_instruments.client.gui.instrument.pipa;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.PipaSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipaOptionsScreen extends SoundTypeOptionsScreen<PipaSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.pipa.soundType",
            OPTIONS_LABEL_KEY = "label.songcraft_instruments.pipa_options";

    public PipaOptionsScreen(final GridInstrumentScreen screen) {
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
    protected PipaSoundType getInitSoundType() {
        return ModClientConfigs.PIPA_SOUND_TYPE.get();
    }

    @Override
    protected PipaSoundType[] values() {
        return PipaSoundType.values();
    }


    @Override
    protected void saveSoundType(PipaSoundType soundType) {
        ModClientConfigs.PIPA_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof PipaScreen;
    }
}
