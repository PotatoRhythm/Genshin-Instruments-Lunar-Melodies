package com.stump.songcraft_instruments.client.gui.instrument.ukelele;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Ukulele3rdOctaveType {
    CHORDS("button.songcraft_instruments.ukulele_3rd_octave.chords"),
    TREBLE("button.songcraft_instruments.ukulele_3rd_octave.treble");

    public final String key;
    Ukulele3rdOctaveType(final String key) {
        this.key = key;
    }
}
