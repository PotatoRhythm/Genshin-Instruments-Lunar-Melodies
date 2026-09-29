package com.stump.songcraft_instruments.block.util;

/**
 * The recording state of a looper, as shown to its connected players.
 */
public enum LooperSessionState {
    IDLE,
    /**
     * A group recording was started, and the countdown beeps are playing
     */
    COUNTDOWN,
    /**
     * The countdown finished; the first note played by any participant starts the recording
     */
    ARMED,
    RECORDING
}
