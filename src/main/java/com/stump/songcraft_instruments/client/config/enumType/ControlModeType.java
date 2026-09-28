package com.stump.songcraft_instruments.client.config.enumType;

import java.util.Locale;

public enum ControlModeType {
    GENSHIN,
    HEARTOPIA,
    OCTAVE_SWAP;

    public String getKey() {
        return "button.songcraft_instruments.control_mode." + toString().toLowerCase(Locale.ENGLISH);
    }
}
