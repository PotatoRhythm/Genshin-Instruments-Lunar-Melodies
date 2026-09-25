package com.stump.genshinstrument_lm.client.gui.instrument.drumset;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.config.enumType.DrumsetSoundType;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.genshinstrument_lm.client.gui.options.partial.SoundTypeOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DrumsetOptionsScreen extends SoundTypeOptionsScreen<DrumsetSoundType> {
    private static final String SOUND_TYPE_KEY = "button.genshinstrument_lm.drumset.soundType",
            OPTIONS_LABEL_KEY = "label.genshinstrument_lm.drumset_options";

    public DrumsetOptionsScreen(@Nullable InstrumentScreen screen) {
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
    protected DrumsetSoundType getInitSoundType() {
        return ModClientConfigs.DRUMSET_SOUND_TYPE.get();
    }

    @Override
    protected DrumsetSoundType[] values() {
        return DrumsetSoundType.values();
    }


    @Override
    protected void saveSoundType(DrumsetSoundType soundType) {
        ModClientConfigs.DRUMSET_SOUND_TYPE.set(soundType);
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen screen) {
        return screen instanceof DrumsetScreen;
    }

    @Override
    public INoteLabel[] getLabels() {
        return DrumsetNoteLabel.availableVals();
    }

    @Override
    public INoteLabel getCurrentLabel() {
        return ModClientConfigs.DRUMSET_LABEL_TYPE.get();
    }

    @Override
    protected void saveLabel(INoteLabel newLabel) {
        if (newLabel instanceof DrumsetNoteLabel label) {
            ModClientConfigs.DRUMSET_LABEL_TYPE.set(label);
        }
    }
}
