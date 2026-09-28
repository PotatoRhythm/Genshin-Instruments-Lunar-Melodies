package com.stump.songcraft_instruments.client.midi;

import com.stump.songcraft_instruments.client.gui.instrument.partial.note.NoteButton;
import com.stump.songcraft_instruments.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record PressedMIDINote(
    int notePitch,
    NoteButton pressedNote,
    NoteSound sound
)
{}


