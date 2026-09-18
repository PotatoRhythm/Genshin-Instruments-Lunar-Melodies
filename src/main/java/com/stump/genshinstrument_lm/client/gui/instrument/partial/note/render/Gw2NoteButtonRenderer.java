package com.stump.genshinstrument_lm.client.gui.instrument.partial.note.render;

import com.stump.genshinstrument_lm.client.gui.instrument.partial.InstrumentThemeLoader;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.util.CommonUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class Gw2NoteButtonRenderer extends NoteButtonRenderer {

    protected static final ResourceLocation GW2_NOTE_ROOT = new ResourceLocation(
            "genshinstrument_lm",
            "textures/gui/genshinstrument_lm/instrument/gw2"
    );

    private static final String[] NOTE_LETTERS = {
            "c", "d", "e", "f", "g", "a", "b"
    };

    public Gw2NoteButtonRenderer(NoteButton noteButton) {
        super(noteButton, null);

        int index = noteButton.soundIndex();
        String noteLetter = NOTE_LETTERS[index % 7];

        accidentalsLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "accidentals_" + noteLetter + ".png");
        noteReleasedLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "released_" + noteLetter + ".png");
        notePressedLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "pressed_" + noteLetter + ".png");
        noteHoverLocation = CommonUtil.getResourceFrom(GW2_NOTE_ROOT,  "hovered_" + noteLetter + ".png");
    }

    @Override
    protected void renderNoteSymbol(final GuiGraphics gui, final InstrumentThemeLoader themeLoader) {
        // Don't render note symbol
    }
}