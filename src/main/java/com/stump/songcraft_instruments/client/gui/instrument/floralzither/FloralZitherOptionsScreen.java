package com.stump.songcraft_instruments.client.gui.instrument.floralzither;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.ZitherSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import com.stump.songcraft_instruments.client.util.TogglablePedalSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloralZitherOptionsScreen extends SoundTypeOptionsScreen<ZitherSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.zither.soundType",
        OPTIONS_LABEL_KEY = "label.songcraft_instruments.zither_options";
    
    public FloralZitherOptionsScreen(final GridInstrumentScreen screen) {
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
    protected ZitherSoundType getInitSoundType() {
        return ModClientConfigs.ZITHER_SOUND_TYPE.get();
    }

    @Override
    protected ZitherSoundType[] values() {
        return ZitherSoundType.values();
    }

    @Override
    public TogglablePedalSound<ZitherSoundType> midiPedalListener() {
        return new TogglablePedalSound<>(ZitherSoundType.NEW, ZitherSoundType.OLD);
    }



    @Override
    protected void saveSoundType(ZitherSoundType soundType) {
        ModClientConfigs.ZITHER_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof FloralZitherScreen;
    }
}
