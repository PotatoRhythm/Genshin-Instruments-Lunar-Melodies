package com.stump.genshinstrument_lm.particle;

public class ColorSet {
    private String name;
    private int[] colors; // up to 6

    public ColorSet(String name, int[] colors) {
        this.name = name;
        this.colors = colors;
    }

    public String getName() { return name; }
    public int[] getColors() { return colors; }

    public void setName(String name) { this.name = name; }
    public void setColors(int[] colors) { this.colors = colors; }
}