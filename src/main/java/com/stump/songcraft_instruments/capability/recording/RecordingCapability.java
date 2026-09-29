package com.stump.songcraft_instruments.capability.recording;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.UUID;

@AutoRegisterCapability
public class RecordingCapability {
    public static final String
        RECORDING_TAG = "Recording",
        REC_POS_TAG = "LooperPos",
        CONNECTED_POS_TAG = "ConnectedLooperPos",
        CONNECTION_ID_TAG = "ConnectionId"
    ;

    private boolean isRecording = false;
    private BlockPos looperPos = null;

    // The player's single instrument connection to a looper
    private BlockPos connectedLooperPos = null;
    private UUID connectionId = null;

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

    public void setConnection(final BlockPos connectedLooperPos, final UUID connectionId) {
        this.connectedLooperPos = connectedLooperPos;
        this.connectionId = connectionId;
    }
    public BlockPos getConnectedLooperPos() {
        return connectedLooperPos;
    }
    public UUID getConnectionId() {
        return connectionId;
    }

    public void copyFrom(final RecordingCapability other) {
        isRecording = other.isRecording;
        looperPos = other.looperPos;
        connectedLooperPos = other.connectedLooperPos;
        connectionId = other.connectionId;
    }

    public void saveNBTData(final CompoundTag nbt) {
        nbt.putBoolean(RECORDING_TAG, isRecording);

        if (looperPos != null)
            nbt.put(REC_POS_TAG, NbtUtils.writeBlockPos(looperPos));

        if (connectedLooperPos != null && connectionId != null) {
            nbt.put(CONNECTED_POS_TAG, NbtUtils.writeBlockPos(connectedLooperPos));
            nbt.putUUID(CONNECTION_ID_TAG, connectionId);
        }
    }

    public void loadNBTData(final CompoundTag nbt) {
        isRecording = nbt.getBoolean(RECORDING_TAG);

        if (nbt.contains(REC_POS_TAG))
            looperPos = NbtUtils.readBlockPos(nbt.getCompound(REC_POS_TAG));

        if (nbt.contains(CONNECTED_POS_TAG) && nbt.hasUUID(CONNECTION_ID_TAG)) {
            connectedLooperPos = NbtUtils.readBlockPos(nbt.getCompound(CONNECTED_POS_TAG));
            connectionId = nbt.getUUID(CONNECTION_ID_TAG);
        }
    }
}
