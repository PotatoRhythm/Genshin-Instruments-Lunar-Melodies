package com.stump.songcraft_instruments.client.gui.instrument.trombone;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.TromboneSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TromboneOptionsScreen extends SoundTypeOptionsScreen<TromboneSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.trombone.soundType",
        OPTIONS_LABEL_KEY = "label.songcraft_instruments.trombone_options";

    public TromboneOptionsScreen(final GridInstrumentScreen screen) {
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
    protected TromboneSoundType getInitSoundType() {
        return ModClientConfigs.TROMBONE_SOUND_TYPE.get();
    }

    @Override
    protected TromboneSoundType[] values() {
        return TromboneSoundType.values();
    }


    @Override
    protected void saveSoundType(TromboneSoundType soundType) {
        ModClientConfigs.TROMBONE_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof TromboneScreen;
    }
}
