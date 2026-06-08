package com.stump.genshinstrument_lm.client.config.enumType;

public enum ParticleColorType {
    DEFAULT(0),
    PASTEL(1),
    MONOCHROME(2);

    private final int index;
    ParticleColorType(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
}
