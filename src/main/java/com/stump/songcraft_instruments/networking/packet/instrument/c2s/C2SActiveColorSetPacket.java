package com.stump.songcraft_instruments.networking.packet.instrument.c2s;

import com.stump.songcraft_instruments.capability.recording.RecordingCapabilityProvider;
import com.stump.songcraft_instruments.networking.IModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Tells the server the colors of the player's active particle color set,
 * so looper recordings can show performers' names in their colors.
 */
public class C2SActiveColorSetPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;
    private static final int MAX_COLORS = 64;

    private final int[] colors;

    public C2SActiveColorSetPacket(final int[] colors) {
        this.colors = colors;
    }
    public C2SActiveColorSetPacket(final FriendlyByteBuf buf) {
        colors = buf.readVarIntArray(MAX_COLORS);
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeVarIntArray(colors);
    }

    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();
        if (player != null)
            RecordingCapabilityProvider.setParticleColors(player, colors);
    }
}
