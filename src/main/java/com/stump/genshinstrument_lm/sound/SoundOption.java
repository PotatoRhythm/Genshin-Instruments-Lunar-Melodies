package com.stump.genshinstrument_lm.sound;

import com.stump.genshinstrument_lm.sound.held.HeldNoteSound;

public class SoundOption {
    private final NoteSound[] noteSounds;
    private final HeldNoteSound[] heldSounds;
    private final boolean singleNote;

    // Constructor for standard notes
    public SoundOption(NoteSound[] noteSounds) {
        this(noteSounds, false);
    }

    // Constructor for standard notes with single-note behavior
    public SoundOption(NoteSound[] noteSounds, boolean singleNote) {
        this.noteSounds = noteSounds;
        this.heldSounds = null;
        this.singleNote = singleNote;
    }

    // Constructor for held notes
    public SoundOption(HeldNoteSound[] heldSounds) {
        this(heldSounds, false);
    }

    // Constructor for held notes with single-note behavior
    public SoundOption(HeldNoteSound[] heldSounds, boolean singleNote) {
        this.heldSounds = heldSounds;
        this.noteSounds = null;
        this.singleNote = singleNote;
    }

    public boolean isHeld() {
        return heldSounds != null;
    }

    public boolean isSingleNote() {
        return singleNote;
    }

    public NoteSound[] getNoteSounds() {
        return noteSounds;
    }

    public HeldNoteSound[] getHeldSounds() {
        return heldSounds;
    }

    public NoteSound[] getNoteSoundsForGrid() {
        if (isHeld()) {
            return HeldNoteSound.getSounds(heldSounds, HeldNoteSound.Phase.ATTACK);
        } else {
            return noteSounds;
        }
    }
}