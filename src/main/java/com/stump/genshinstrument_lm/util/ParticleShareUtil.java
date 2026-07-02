package com.stump.genshinstrument_lm.util;

import com.stump.genshinstrument_lm.client.colorSet.ColorSet;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class ParticleShareUtil {

    public static final String PREFIX = "SC-COLOR";
    public static final String VERSION = "V1";

    private ParticleShareUtil() {}

    public static String encode(ColorSet set) {
        StringBuilder builder = new StringBuilder();

        builder.append(PREFIX)
                .append(':')
                .append(VERSION)
                .append(':')
                .append(URLEncoder.encode(set.getName(), StandardCharsets.UTF_8))
                .append(':');

        int[] colors = set.getColors();

        for (int i = 0; i < colors.length; i++) {
            if (i != 0) builder.append('-');
            builder.append(String.format("%06X", colors[i] & 0xFFFFFF));
        }

        return builder.toString();
    }

    public static ColorSet decode(String text) {
        if (text == null) { return null; }

        String[] parts = text.split(":");

        if (parts.length != 4) { return null; }
        if (!PREFIX.equals(parts[0])) { return null; }
        if (!VERSION.equals(parts[1])) { return null; }

        String name = URLDecoder.decode(parts[2], StandardCharsets.UTF_8);

        String[] colorStrings = parts[3].split("-");
        if (colorStrings.length != 6) { return null; }

        int[] colors = new int[6];

        for (int i = 0; i < 6; i++) {
            try {
                int rgb = Integer.parseInt(colorStrings[i], 16);
                colors[i] = 0xFF000000 | rgb;
            } catch (Exception e) {
                return null;
            }
        }

        return new ColorSet(name, colors);
    }
}