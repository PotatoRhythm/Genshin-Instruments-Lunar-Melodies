package com.stump.genshinstrument_lm.client.gui.instrument.partial.note.render;

import com.stump.genshinstrument_lm.client.gui.instrument.gw2_drumset.Gw2DrumsetNoteButton;
import com.stump.genshinstrument_lm.util.CommonUtil;

public class Gw2DrumsetNoteButtonRenderer extends Gw2NoteButtonRenderer {

    private static final String[] NOTE_LETTERS = {
            "c", "d", "e", "f", "g", "b", "c", "d",
            "f", "a", "c_sharp", "d_sharp", "f_sharp", "g_sharp", "a_sharp"
    };

    public Gw2DrumsetNoteButtonRenderer(Gw2DrumsetNoteButton noteButton) {
        super(noteButton);

        int index = noteButton.index;
        String noteLetter = NOTE_LETTERS[index];

        accidentalsLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "accidentals_" + noteLetter + ".png");
        noteReleasedLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "released_" + noteLetter + ".png");
        notePressedLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "pressed_" + noteLetter + ".png");
        noteHoverLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "hovered_" + noteLetter + ".png");
    }
}