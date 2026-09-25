package com.stump.genshinstrument_lm.sound;

import com.stump.genshinstrument_lm.sound.held.InitiatorID;

import java.util.ArrayList;
import java.util.List;

public class NoteSoundInstances {

    private static final List<NoteSoundInstance> INSTANCES = new ArrayList<>();

    public static void add(NoteSoundInstance instance) {
        INSTANCES.add(instance);
    }

    public static void remove(NoteSoundInstance instance) {
        INSTANCES.remove(instance);
    }

    public static void dampenAll(final int initiatorId) {
        for (NoteSoundInstance instance : new ArrayList<>(INSTANCES)) {
            if (instance.getInitiatorId().orElse(-1) == initiatorId) {
                instance.dampen();
            }
        }
    }

    public static void dampenAll(final InitiatorID initiatorId) {
        for (NoteSoundInstance instance : new ArrayList<>(INSTANCES)) {
            if (instance.getOInitiatorId().map(initiatorId::equals).orElse(false)) {
                instance.dampen();
            }
        }
    }

    public static void clear() {
        for (NoteSoundInstance instance : new ArrayList<>(INSTANCES)) {
            instance.stopNote();
        }

        INSTANCES.clear();
    }
}