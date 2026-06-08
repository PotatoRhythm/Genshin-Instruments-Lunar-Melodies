package com.stump.genshinstrument_lm.capability.playerCustomization;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class PlayerCustomization {
    public static final String PARTICLE_COLOR_TAG = "InstrumentParticleColor";
    private int particleColorSet = 0;

    public int getParticleColorSet() {
        return particleColorSet;
    }

    public void setParticleColorSet(int set) {
        this.particleColorSet = set;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt(PARTICLE_COLOR_TAG, particleColorSet);
    }

    public void loadNBTData(CompoundTag nbt) {
        particleColorSet = nbt.getInt(PARTICLE_COLOR_TAG);
    }
}
