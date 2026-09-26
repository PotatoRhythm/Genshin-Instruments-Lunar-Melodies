package com.stump.genshinstrument_lm.client.gui.instrument.partial.grid;

import com.mojang.logging.LogUtils;
import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.grid.NoteGridButton;
import com.stump.genshinstrument_lm.client.midi.InstrumentMidiReceiver;
import com.stump.genshinstrument_lm.client.midi.MidiOverflowResult;
import com.stump.genshinstrument_lm.client.midi.PressedMIDINote;
import com.stump.genshinstrument_lm.sound.NoteSound;
import com.stump.genshinstrument_lm.util.LabelUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class GridInstrumentMidiReceiver extends InstrumentMidiReceiver {

    public GridInstrumentMidiReceiver(GridInstrumentScreen instrument) {
        super(instrument);
    }
    protected GridInstrumentScreen gridInstrument() {
        return (GridInstrumentScreen) instrument;
    }

    @Override
    public boolean allowMidiOverflow() {
        return true;
    }

    protected int maxMidiNote() {
        return LabelUtil.NOTES_PER_SCALE * gridInstrument().columns();
    }

    @Override
    protected NoteButton getHighestNote() {
        return gridInstrument().getNoteButton(gridInstrument().rows() - 1, gridInstrument().columns() - 1);
    }
    @Override
    protected NoteButton getLowestNote() {
        return gridInstrument().getNoteButton(0, 0);
    }


    @Override
    protected @Nullable NoteButton handleMidiPress(int note, int key) {
        final GridInstrumentScreen instrumentScreen = (GridInstrumentScreen) instrument;

        final int layoutNote = note % 12;
        final boolean higherThan3 = layoutNote > key + 4;

        // Handle transposition
        final boolean shouldSharpen = shouldSharpen(layoutNote, key);
        final boolean shouldFlatten = shouldFlatten(shouldSharpen);

        transposeMidi(shouldSharpen, shouldFlatten);

        // A sharpened/flattened note is still the same note - just pitched up/down.
        // Thus, go backwards/forwards to stay on the same note.

        int playedNote = note + (shouldFlatten ? 1 : shouldSharpen ? -1 : 0);

        playedNote = ((playedNote + (higherThan3 ? 1 : 0)) / 2)
            // 12th note should go to the next column
            + playedNote / (12 + key);

        return instrumentScreen.getNoteButtonByMIDINote(playedNote);
    }

    @Override
    public PressedMIDINote playNote(NoteButton noteBtn, @Nullable MidiOverflowResult midiOverflow, int basePitch) {
        if (midiOverflow == null) {
            noteBtn.play();

            if (noteBtn instanceof NoteGridButton gridButton) {
                return new PressedMIDINote(
                        gridButton.getLastPlayedPitch(),
                        noteBtn,
                        gridButton.getLastPlayedSound()
                );
            }

            return new PressedMIDINote(
                    noteBtn.getPitch(),
                    noteBtn,
                    noteBtn.getSound()
            );
        }

        final GridInstrumentScreen screen = gridInstrument();
        final NoteSound[] sounds = screen.getInitSounds();

        if (sounds == null || sounds.length == 0) {
            int newPitch = NoteSound.clampPitch(
                    basePitch
                            + midiOverflow.pitchOffset()
                            + ModClientConfigs.TRANSPOSE.get()
            );

            noteBtn.play(midiOverflow.newNoteSound(), newPitch);

            return new PressedMIDINote(
                    newPitch,
                    noteBtn,
                    midiOverflow.newNoteSound()
            );
        }

        final int overflowSoundPitch =
                midiOverflow.type() == MidiOverflowResult.OverflowType.BOTTOM
                        ? getSampleChromaticPitch(0)
                        : getSampleChromaticPitch(sounds.length - 1);

        final int targetPitch =
                overflowSoundPitch
                        + midiOverflow.pitchOffset()
                        + basePitch
                        + ModClientConfigs.TRANSPOSE.get();

        // Find the available sample closest to the desired pitch.
        NoteSound closestSound = sounds[0];
        int closestPitch = getSampleChromaticPitch(0);
        int closestDistance = Math.abs(targetPitch - closestPitch);

        for (int i = 1; i < sounds.length; i++) {
            final int samplePitch = getSampleChromaticPitch(i);
            final int distance = Math.abs(targetPitch - samplePitch);

            if (distance < closestDistance) {
                closestSound = sounds[i];
                closestPitch = samplePitch;
                closestDistance = distance;
            }
        }

        int newPitch = targetPitch - closestPitch;
        newPitch = NoteSound.clampPitch(newPitch);

        noteBtn.play(closestSound, newPitch);

        return new PressedMIDINote(newPitch, noteBtn, closestSound);
    }

    private int getSampleChromaticPitch(int index) {
        final int[] naturalNotePitches = {
                0, 2, 4, 5, 7, 9, 11
        };

        final int row = index % gridInstrument().rows();
        final int column = index / gridInstrument().rows();

        return column * 12 + naturalNotePitches[row];
    }
}
