package com.stump.genshinstrument_lm.client.gui.instrument.partial.grid;

import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.held.IHoldableNoteButton;
import com.stump.genshinstrument_lm.client.keyMaps.InstrumentKeyMappings;
import com.stump.genshinstrument_lm.client.midi.MidiOverflowResult;
import com.stump.genshinstrument_lm.client.midi.PressedMIDINote;
import com.stump.genshinstrument_lm.sound.NoteSound;

import java.util.HashMap;
import java.util.Map;

public class GridHeartopiaController {

    private final GridInstrumentScreen screen;
    private final GridInstrumentMidiReceiver midiReceiver;

    private final Map<Integer, PressedMIDINote> pressedNotes = new HashMap<>();

    public GridHeartopiaController(GridInstrumentScreen screen) {
        this.screen = screen;
        this.midiReceiver = new GridInstrumentMidiReceiver(screen);
    }

    // ---------------------------------------------------
    // KEY PRESS
    // ---------------------------------------------------

    public boolean handleKeyPress(int keyCode, int scanCode) {

        if (pressedNotes.containsKey(keyCode))
            return true;

        final Integer pitchOffset = InstrumentKeyMappings.HEARTOPIA_KEY_TO_PITCH.get(keyCode);

        if (pitchOffset == null)
            return false;

        // Limit max note if extended range is off
        if (!ModClientConfigs.EXTEND_RANGE.get()
                && pitchOffset >= screen.columns() * 12 + 1) {
            return true;
        }

        int targetNote = pitchOffset;

        screen.resetTransposition();

        final MidiOverflowResult overflowRes =
            midiReceiver.handleMidiOverflow(targetNote);

        if (overflowRes != null) {
            targetNote = overflowRes.fixedOctaveNote();
            int newInsPitch = overflowRes.pitchOffset() + screen.getPitch();
            if ((newInsPitch < NoteSound.MIN_PITCH) || (newInsPitch > NoteSound.MAX_PITCH)) {
                screen.setPitch(0);
            }
        }

        final int basePitch = screen.getPitch();

        final NoteButton pressedNote = midiReceiver.handleMidiPress(targetNote, 0);

        if (pressedNote == null)
            return true;

        pressedNote.unlockInput();

        final PressedMIDINote pressedNoteObj = midiReceiver.playNote(pressedNote, overflowRes, basePitch);

        if (pressedNoteObj != null) {
            pressedNotes.put(keyCode, pressedNoteObj);
        }

        return true;
    }

    // ---------------------------------------------------
    // KEY RELEASE
    // ---------------------------------------------------

    public boolean handleKeyRelease(int keyCode) {

        final PressedMIDINote prevNote =
            pressedNotes.remove(keyCode);

        if (prevNote == null)
            return InstrumentKeyMappings.HEARTOPIA_KEY_TO_PITCH.containsKey(keyCode);

        final NoteButton prevButton = prevNote.pressedNote();

        if (!(prevButton instanceof IHoldableNoteButton)) {
            prevButton.release();
            return true;
        }

        final IHoldableNoteButton heldButton =
            (IHoldableNoteButton) prevButton;

        heldButton.releaseHeld(prevNote.notePitch(), true, heldButton.toHeldSound(prevNote.sound()));

        return true;
    }
}