package com.stump.genshinstrument_lm.client.gui.instrument.guitar;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.config.enumType.GuitarSoundType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuitarOptionsScreen extends SoundTypeOptionsScreen<GuitarSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument_lm.guitar.soundType",
        OPTIONS_LABEL_KEY = "label.genshinstrument_lm.guitar_options";

    public GuitarOptionsScreen(final GridInstrumentScreen screen) {
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
    protected GuitarSoundType getInitSoundType() {
        return ModClientConfigs.GUITAR_SOUND_TYPE.get();
    }

    @Override
    protected GuitarSoundType[] values() {
        return GuitarSoundType.values();
    }


    @Override
    protected void saveSoundType(GuitarSoundType soundType) {
        ModClientConfigs.GUITAR_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof GuitarScreen;
    }
}
