package com.stump.genshinstrument_lm.util;

import com.stump.genshinstrument_lm.client.colorSet.ColorSetManager;
import com.stump.genshinstrument_lm.client.colorSet.ColorSet;
import net.minecraft.util.Mth;

public final class ParticleColorUtil {

    private ParticleColorUtil() {}

    public static int getGradientColor(float t, int[] colors) {
        if (colors == null || colors.length == 0) {
            return 0xFFFFFF;
        }

        if (colors.length == 1) {
            return colors[0] & 0xFFFFFF;
        }

        t = Mth.clamp(t, 0.0F, 1.0F);

        float scaled = t * (colors.length - 1);

        int idx = (int) scaled;
        idx = Mth.clamp(idx, 0, colors.length - 2);

        float localT = scaled - idx;

        int c1 = colors[idx];
        int c2 = colors[idx + 1];

        float r1 = ((c1 >> 16) & 0xFF) / 255f;
        float g1 = ((c1 >> 8) & 0xFF) / 255f;
        float b1 = (c1 & 0xFF) / 255f;

        float r2 = ((c2 >> 16) & 0xFF) / 255f;
        float g2 = ((c2 >> 8) & 0xFF) / 255f;
        float b2 = (c2 & 0xFF) / 255f;

        int r = (int) ((r1 + (r2 - r1) * localT) * 255.0F);
        int g = (int) ((g1 + (g2 - g1) * localT) * 255.0F);
        int b = (int) ((b1 + (b2 - b1) * localT) * 255.0F);

        return (r << 16) | (g << 8) | b;
    }

    public static int getNoteRGB(int buttonIndex, int transpose) {
        ColorSet set = ColorSetManager.getActiveColorSet();

        if (set == null || set.getColors() == null) {
            return 0xFFFFFF;
        }

        final double MIN_NOTE = -12;
        final double MAX_NOTE = 48;

        int semitoneIndex = getSemitoneFromButton(buttonIndex) + transpose;
        double t = (semitoneIndex - MIN_NOTE) / (MAX_NOTE - MIN_NOTE);
        t = Mth.clamp(t, 0.0, 1.0);

        return getGradientColor((float) t, set.getColors());
    }

    public static int rgbToARGB(int rgb) {
        return 0xFF000000 | (rgb & 0xFFFFFF);
    }

    public static Integer parseHexColor(String text) {
        if (text == null) {
            return null;
        }

        text = text.trim();

        if (text.startsWith("#")) {
            text = text.substring(1);
        } else if (text.startsWith("0x") || text.startsWith("0X")) {
            text = text.substring(2);
        }

        if (!text.matches("[0-9A-Fa-f]{6}")) {
            return null;
        }

        return Integer.parseInt(text, 16);
    }

    public static String colorToHex(int color) {
        return String.format("#%06X", color & 0xFFFFFF);
    }

    private static final int[] SCALE_PATTERN = {0, 2, 4, 5, 7, 9, 11};
    private static int getSemitoneFromButton(int index) {
        int octave = index / 7;
        int step = index % 7;
        return SCALE_PATTERN[step] + (octave * 12);
    }
}