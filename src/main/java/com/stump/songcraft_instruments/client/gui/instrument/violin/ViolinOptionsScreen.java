package com.stump.songcraft_instruments.client.gui.instrument.violin;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.ViolinSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViolinOptionsScreen extends SoundTypeOptionsScreen<ViolinSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.violin.soundType",
        OPTIONS_LABEL_KEY = "label.songcraft_instruments.violin_options";

    public ViolinOptionsScreen(final GridInstrumentScreen screen) {
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
    protected ViolinSoundType getInitSoundType() {
        return ModClientConfigs.VIOLIN_SOUND_TYPE.get();
    }

    @Override
    protected ViolinSoundType[] values() {
        return ViolinSoundType.values();
    }


    @Override
    protected void saveSoundType(ViolinSoundType soundType) {
        ModClientConfigs.VIOLIN_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof ViolinScreen;
    }
}
