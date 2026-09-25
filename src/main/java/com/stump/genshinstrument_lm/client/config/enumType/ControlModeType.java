package com.stump.genshinstrument_lm.client.config.enumType;

import java.util.Locale;

public enum ControlModeType {
    GENSHIN,
    HEARTOPIA,
    OCTAVE_SWAP;

    public String getKey() {
        return "button.genshinstrument_lm.control_mode." + toString().toLowerCase(Locale.ENGLISH);
    }
}
