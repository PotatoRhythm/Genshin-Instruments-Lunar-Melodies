package com.stump.genshinstrument_lm.client.gui.instrument.djemdjemdrum;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.INoteLabel;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.label.NoteLabelSupplier;
import com.stump.genshinstrument_lm.util.LabelUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DjemDjemDrumNoteLabel implements INoteLabel {
	KEYBOARD_LAYOUT((note) ->
		INoteLabel.upperComponent(ddd(note).getKey().getDisplayName())
	),
	QWERTY((note) ->
		INoteLabel.getQwerty(ddd(note).getKey())
	),

    NONE(NoteLabelSupplier.EMPTY);


    private final NoteLabelSupplier labelSupplier;
    DjemDjemDrumNoteLabel(final NoteLabelSupplier supplier) {
        labelSupplier = supplier;
    }

	public static INoteLabel[] availableVals() {
        return INoteLabel.filterQwerty(values(), ModClientConfigs.DJEM_DJEM_DRUM_LABEL_TYPE.get(), QWERTY);
    }


	@Override
	public NoteLabelSupplier getLabelSupplier() {
        return labelSupplier;
	}


	private static DjemDjemDrumNoteButton ddd(final NoteButton btn) {
        return (DjemDjemDrumNoteButton)btn;
    }
}