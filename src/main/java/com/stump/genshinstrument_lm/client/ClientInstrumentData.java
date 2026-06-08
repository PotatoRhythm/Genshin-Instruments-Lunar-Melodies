package com.stump.genshinstrument_lm.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientInstrumentData {

    private static final Map<UUID, Integer> PARTICLE_SETS = new HashMap<>();

    public static void setParticleSet(UUID id, int set) {
        PARTICLE_SETS.put(id, set);
    }

    public static int getParticleSet(UUID id) {
        return PARTICLE_SETS.getOrDefault(id, 0);
    }
}