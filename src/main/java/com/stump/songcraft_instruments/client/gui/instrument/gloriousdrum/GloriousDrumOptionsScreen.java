package com.stump.songcraft_instruments.client.gui.instrument.gloriousdrum;

import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.songcraft_instruments.client.gui.options.partial.InstrumentOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GloriousDrumOptionsScreen extends InstrumentOptionsScreen {

    public GloriousDrumOptionsScreen(AratakisGreatAndGloriousDrumScreen screen) {
        super(screen);
    }
    

    @Override
    protected void saveLabel(INoteLabel newLabel) {
        if (newLabel instanceof GloriousDrumNoteLabel label) {
            ModClientConfigs.GLORIOUS_DRUM_LABEL_TYPE.set(label);
        }
    }

    @Override
    public INoteLabel[] getLabels() {
        return GloriousDrumNoteLabel.availableVals();
    }
    @Override
    public GloriousDrumNoteLabel getCurrentLabel() {
        return ModClientConfigs.GLORIOUS_DRUM_LABEL_TYPE.get();
    }

    @Override
    protected GloriousDrumMidiOptionsScreen midiOptionsScreen() {
        return new GloriousDrumMidiOptionsScreen(MIDI_OPTIONS, this, instrumentScreen);
    }
    
}
