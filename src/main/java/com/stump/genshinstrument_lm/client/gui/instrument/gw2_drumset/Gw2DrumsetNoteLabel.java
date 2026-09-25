package com.stump.genshinstrument_lm.client.gui.instrument.gw2_drumset;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.NoteLabelSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Gw2DrumsetNoteLabel implements INoteLabel {
    KEYBOARD_LAYOUT((note) ->
            INoteLabel.upperComponent(getDrumsetNoteButton(note).getKey().getDisplayName())
    ),
    QWERTY((note) ->
            INoteLabel.getQwerty(getDrumsetNoteButton(note).getKey())
    ),

    NONE(NoteLabelSupplier.EMPTY);


    private final NoteLabelSupplier labelSupplier;
    Gw2DrumsetNoteLabel(final NoteLabelSupplier supplier) {
        labelSupplier = supplier;
    }

    public static INoteLabel[] availableVals() {
        return INoteLabel.filterQwerty(values(), ModClientConfigs.GW2_DRUMSET_LABEL_TYPE.get(), QWERTY);
    }


    @Override
    public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
    }


    private static Gw2DrumsetNoteButton getDrumsetNoteButton(final NoteButton btn) {
        return (Gw2DrumsetNoteButton)btn;
    }
}