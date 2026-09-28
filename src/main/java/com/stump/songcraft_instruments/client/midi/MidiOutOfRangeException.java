package com.stump.songcraft_instruments.client.midi;

public class MidiOutOfRangeException extends Exception {
    
    public MidiOutOfRangeException() {
        super("MIDI note is out of allowed range");
    }

}
