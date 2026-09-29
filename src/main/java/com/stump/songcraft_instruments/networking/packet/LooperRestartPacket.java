package com.stump.songcraft_instruments.networking.packet;

import com.stump.songcraft_instruments.networking.IModPacket;
import com.stump.songcraft_instruments.util.LooperRecordStateUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * A C2S packet requesting the recording the player is part of to start over.
 */
public class LooperRestartPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    public LooperRestartPacket() {}
    public LooperRestartPacket(final FriendlyByteBuf buf) {}

    @Override
    public void handle(final Context context) {
        LooperRecordStateUtil.handleRestart(context.getSender());
    }
}
