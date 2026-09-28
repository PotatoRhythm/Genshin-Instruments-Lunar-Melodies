package com.stump.songcraft_instruments.client.midi;

import com.stump.songcraft_instruments.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record MidiOverflowResult(
    NoteSound newNoteSound,
    int pitchOffset,
    int fixedOctaveNote,
    OverflowType type
) {
    public static enum OverflowType {TOP, BOTTOM}
}