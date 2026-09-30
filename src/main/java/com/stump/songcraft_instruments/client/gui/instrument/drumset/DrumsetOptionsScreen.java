package com.stump.songcraft_instruments.client.gui.instrument.drumset;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.songcraft_instruments.client.gui.options.partial.InstrumentOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DrumsetOptionsScreen extends InstrumentOptionsScreen {

    public DrumsetOptionsScreen(@Nullable InstrumentScreen screen) {
        super(screen);
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
