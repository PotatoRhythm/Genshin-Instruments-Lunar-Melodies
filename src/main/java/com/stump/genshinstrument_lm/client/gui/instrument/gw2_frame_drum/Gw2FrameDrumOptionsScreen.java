package com.stump.genshinstrument_lm.client.gui.instrument.gw2_frame_drum;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.genshinstrument_lm.client.gui.options.partial.InstrumentOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class Gw2FrameDrumOptionsScreen extends InstrumentOptionsScreen {
    public Gw2FrameDrumOptionsScreen(@Nullable InstrumentScreen screen) {
        super(screen);
    }

    @Override
    public INoteLabel[] getLabels() {
        return Gw2FrameDrumNoteLabel.availableVals();
    }

    @Override
    public INoteLabel getCurrentLabel() {
        return ModClientConfigs.GW2_FRAME_DRUM_LABEL_TYPE.get();
    }

    @Override
    protected void saveLabel(INoteLabel newLabel) {
        if (newLabel instanceof Gw2FrameDrumNoteLabel label) {
            ModClientConfigs.GW2_FRAME_DRUM_LABEL_TYPE.set(label);
        }
    }
}
