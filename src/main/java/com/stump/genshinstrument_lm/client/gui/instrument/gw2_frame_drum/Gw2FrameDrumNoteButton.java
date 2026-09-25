package com.stump.genshinstrument_lm.client.gui.instrument.gw2_frame_drum;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentScreen;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.render.NoteButtonRenderer;
import com.stump.genshinstrument_lm.client.keyMaps.InstrumentKeyMappings;
import com.stump.genshinstrument_lm.sound.GISounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Gw2FrameDrumNoteButton extends NoteButton {
    public final int row, column;
    /**
     * The index of the gw2 drum.
     * <p>
     * Starts form the top left, ends at the bottom right.
     */
    public final int index;

    public Gw2FrameDrumNoteButton(InstrumentScreen instrumentScreen, int row, int column) {
        super(
            GISounds.GW2_FRAME_DRUM[getIndex(row, column)],
            ModClientConfigs.GW2_FRAME_DRUM_LABEL_TYPE.get().getLabelSupplier(),
            instrumentScreen
        );

        this.row = row;
        this.column = column;
        index = getIndex(row, column);
    }

    private static int getIndex(int row, int column) {
        return column + row * 5;
    }

    @Override
    public int getNoteOffset() {
        return index;
    }


    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, () ->
            instrumentScreen.getResourceFromRoot("note/label/" + column + ".png", false)
        );
    }


    public Key getKey() {
        return InstrumentKeyMappings.GENSHIN_INSTRUMENT_MAPPINGS[1 - row][column];
    }
}
