package com.stump.genshinstrument_lm.render.util;

import net.minecraft.world.item.DyeColor;

public class InstrumentDyeColors {

    public static int getColor(DyeColor dye) {
        return switch (dye) {
            case WHITE -> 0xFFFFFF;
            case ORANGE -> 0xB86F3C;
            case MAGENTA -> 0x8E4A68;
            case LIGHT_BLUE -> 0x5F8FA8;
            case YELLOW -> 0xC2A85A;
            case LIME -> 0x7E9B55;
            case PINK -> 0xB77B83;
            case GRAY -> 0x5A5A57;
            case LIGHT_GRAY -> 0x9B9A91;
            case CYAN -> 0x4F8C88;
            case PURPLE -> 0x765A8A;
            case BLUE -> 0x425D8A;
            case BROWN -> 0x6B4A32;
            case GREEN -> 0x557A52;
            case RED -> 0x9B4B45;
            case BLACK -> 0x1E1D1A;
        };
    }
}