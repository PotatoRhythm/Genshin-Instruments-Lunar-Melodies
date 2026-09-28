package com.stump.songcraft_instruments.networking.packet.instrument.util;

import com.stump.songcraft_instruments.sound.held.HeldNoteSound;

/**
 * Different from {@link HeldNoteSound.Phase}!
 * Represents the packet state of a held note sound.
 */
public enum HeldSoundPhase {
    ATTACK, RELEASE
}
