package com.stump.genshinstrument_lm.client.colorSet;

import java.util.ArrayList;
import java.util.List;

public class ColorSetData {

    private int activeSet;
    private List<ColorSet> sets;

    public ColorSetData() {
        this.sets = new ArrayList<>();
    }

    public int getActiveSet() {
        return activeSet;
    }

    public void setActiveSet(int activeSet) {
        this.activeSet = activeSet;
    }

    public List<ColorSet> getSets() {
        if (sets == null) {
            sets = new ArrayList<>();
        }
        return sets;
    }

    public void setSets(List<ColorSet> sets) {
        this.sets = (sets != null) ? sets : new ArrayList<>();
    }
}