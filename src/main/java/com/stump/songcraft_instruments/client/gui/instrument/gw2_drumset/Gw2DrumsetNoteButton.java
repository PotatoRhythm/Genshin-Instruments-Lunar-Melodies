package com.stump.songcraft_instruments.client.gui.instrument.gw2_drumset;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.stump.songcraft_instruments.client.config.ModClientConfigs;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.render.Gw2DrumsetNoteButtonRenderer;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.NoteButton;
import com.stump.songcraft_instruments.client.gui.instrument.partial.note.render.NoteButtonRenderer;
import com.stump.songcraft_instruments.client.keyMaps.InstrumentKeyMappings;
import com.stump.songcraft_instruments.networking.buttonidentifier.DrumsetNoteIdentifier;
import com.stump.songcraft_instruments.networking.buttonidentifier.NoteButtonIdentifier;
import com.stump.songcraft_instruments.sound.SCSounds;

public class Gw2DrumsetNoteButton extends NoteButton {

    public final int index;

    public Gw2DrumsetNoteButton(Gw2DrumsetScreen screen, int index) {
        super(
                SCSounds.DRUMSET_GW2[index],
                ModClientConfigs.GW2_DRUMSET_LABEL_TYPE.get().getLabelSupplier(),
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
        return new Gw2DrumsetNoteButtonRenderer(this);
    }
}