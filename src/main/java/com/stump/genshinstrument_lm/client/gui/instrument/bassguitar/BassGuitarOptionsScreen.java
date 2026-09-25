package com.stump.genshinstrument_lm.client.gui.instrument.bassguitar;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.config.enumType.BassGuitarSoundType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BassGuitarOptionsScreen extends SoundTypeOptionsScreen<BassGuitarSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument_lm.bass_guitar.soundType",
        OPTIONS_LABEL_KEY = "label.genshinstrument_lm.bass_guitar_options";

    public BassGuitarOptionsScreen(final GridInstrumentScreen screen) {
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
    protected BassGuitarSoundType getInitSoundType() {
        return ModClientConfigs.BASS_GUITAR_SOUND_TYPE.get();
    }

    @Override
    protected BassGuitarSoundType[] values() {
        return BassGuitarSoundType.values();
    }


    @Override
    protected void saveSoundType(BassGuitarSoundType soundType) {
        ModClientConfigs.BASS_GUITAR_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof BassGuitarScreen;
    }
}
