package com.stump.songcraft_instruments.client.gui.instrument.djemdjemdrum;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.gui.instrument.partial.InstrumentScreen;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.songcraft_instruments.client.gui.options.partial.InstrumentOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DjemDjemDrumOptionsScreen extends InstrumentOptionsScreen {
    public DjemDjemDrumOptionsScreen(@Nullable InstrumentScreen screen) {
        super(screen);
    }

    @Override
    public INoteLabel[] getLabels() {
        return DjemDjemDrumNoteLabel.availableVals();
    }

    @Override
    public INoteLabel getCurrentLabel() {
        return ModClientConfigs.DJEM_DJEM_DRUM_LABEL_TYPE.get();
    }

    @Override
    protected void saveLabel(INoteLabel newLabel) {
        if (newLabel instanceof DjemDjemDrumNoteLabel label) {
            ModClientConfigs.DJEM_DJEM_DRUM_LABEL_TYPE.set(label);
        }
    }
}
