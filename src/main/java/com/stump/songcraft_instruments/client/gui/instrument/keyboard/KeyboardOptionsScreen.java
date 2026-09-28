package com.stump.songcraft_instruments.client.gui.instrument.keyboard;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.config.enumType.KeyboardSoundType;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.grid.GridInstrumentScreen;
import com.stump.songcraft_instruments.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardOptionsScreen extends SoundTypeOptionsScreen<KeyboardSoundType> {
    private static final String SOUND_TYPE_KEY = "button.songcraft_instruments.keyboard.soundType",
        OPTIONS_LABEL_KEY = "label.songcraft_instruments.keyboard_options";
    
    public KeyboardOptionsScreen(final GridInstrumentScreen screen) {
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
    protected KeyboardSoundType getInitSoundType() {
        return ModClientConfigs.KEYBOARD_SOUND_TYPE.get();
    }

    @Override
    protected KeyboardSoundType[] values() {
        return KeyboardSoundType.values();
    }


    @Override
    protected void saveSoundType(KeyboardSoundType soundType) {
        ModClientConfigs.KEYBOARD_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof KeyboardScreen;
    }
}
