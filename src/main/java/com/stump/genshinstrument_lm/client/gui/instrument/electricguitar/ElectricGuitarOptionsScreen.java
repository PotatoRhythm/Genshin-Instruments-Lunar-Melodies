package com.stump.genshinstrument_lm.client.gui.instrument.electricguitar;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.config.enumType.ElectricGuitarSoundType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricGuitarOptionsScreen extends SoundTypeOptionsScreen<ElectricGuitarSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument_lm.electric_guitar.soundType",
        OPTIONS_LABEL_KEY = "label.genshinstrument_lm.electric_guitar_options";

    public ElectricGuitarOptionsScreen(final GridInstrumentScreen screen) {
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
    protected ElectricGuitarSoundType getInitSoundType() {
        return ModClientConfigs.ELECTRIC_GUITAR_SOUND_TYPE.get();
    }

    @Override
    protected ElectricGuitarSoundType[] values() {
        return ElectricGuitarSoundType.values();
    }


    @Override
    protected void saveSoundType(ElectricGuitarSoundType soundType) {
        ModClientConfigs.ELECTRIC_GUITAR_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof ElectricGuitarScreen;
    }
}
