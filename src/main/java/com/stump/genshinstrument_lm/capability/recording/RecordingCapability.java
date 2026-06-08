package com.stump.genshinstrument_lm.capability.recording;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class RecordingCapability {
    public static final String
        RECORDING_TAG = "Recording",
        REC_POS_TAG = "LooperPos"
    ;

    private boolean isRecording = false;
    private BlockPos looperPos = null;

    public void setRecording(final BlockPos looperPos) {
        isRecording = true;
        this.looperPos = looperPos;
    }
    public void setNotRecording() {
        isRecording = false;
        looperPos = null;
    }

    public boolean isRecording() {
        return isRecording;
    }
    public BlockPos getLooperPos() {
        return looperPos;
    }

    public void saveNBTData(final CompoundTag nbt) {
        nbt.putBoolean(RECORDING_TAG, isRecording);

        if (looperPos != null)
            nbt.put(REC_POS_TAG, NbtUtils.writeBlockPos(looperPos));

        nbt.putInt(PARTICLE_SET_TAG, particleSet);
    }

    public void loadNBTData(final CompoundTag nbt) {
        isRecording = nbt.getBoolean(RECORDING_TAG);

        if (nbt.contains(REC_POS_TAG))
            looperPos = NbtUtils.readBlockPos(nbt.getCompound(REC_POS_TAG));

        particleSet = nbt.getInt(PARTICLE_SET_TAG);
    }

    public static final String PARTICLE_SET_TAG = "ParticleSet";
    private int particleSet = 0;
    public int getParticleSet() {
        return particleSet;
    }
    public void setParticleSet(int particleSet) {
        this.particleSet = particleSet;
    }
}
