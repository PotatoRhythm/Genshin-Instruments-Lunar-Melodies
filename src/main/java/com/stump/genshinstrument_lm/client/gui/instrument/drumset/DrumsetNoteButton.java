package com.stump.genshinstrument_lm.client.gui.instrument.drumset;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.render.NoteButtonRenderer;
import com.stump.genshinstrument_lm.client.keyMaps.InstrumentKeyMappings;
import com.stump.genshinstrument_lm.networking.buttonidentifier.DrumsetNoteIdentifier;
import com.stump.genshinstrument_lm.networking.buttonidentifier.NoteButtonIdentifier;
import com.stump.genshinstrument_lm.sound.GISounds;

public class DrumsetNoteButton extends NoteButton {

    public final int index;

    public DrumsetNoteButton(DrumsetScreen screen, int index) {
        super(
                GISounds.DRUMSET_GW2[index],
                ModClientConfigs.DRUMSET_LABEL_TYPE.get().getLabelSupplier(),
                screen
        );

        this.index = index;
    }

    public Key getKey() {
        return InstrumentKeyMappings.DRUMSET_MAPPINGS[index].getKey();
    }

    @Override
    public NoteButtonIdentifier getIdentifier() {
        return new DrumsetNoteIdentifier(this);
    }

    @Override
    public int getNoteOffset() {
        return index;
    }

    @Override
    protected NoteButtonRenderer initNoteRenderer() {
        return new NoteButtonRenderer(this, () ->
                instrumentScreen.getResourceFromRoot(
                        "note/label/" + (index % 4) + ".png",
                        false
                )
        );
    }
}