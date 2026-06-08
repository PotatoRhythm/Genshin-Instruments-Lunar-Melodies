package com.stump.genshinstrument_lm.networking.packet.instrument.c2s;

import com.stump.genshinstrument_lm.capability.recording.RecordingCapabilityProvider;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.S2CParticleColorChangedPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Sent from client to server when player changes particle color type
 */
public class C2SParticleColorChangedPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;
    private final int particleSet;

    public C2SParticleColorChangedPacket(int particleSet) {
        this.particleSet = particleSet;
    }

    public C2SParticleColorChangedPacket(FriendlyByteBuf buf) {
        this.particleSet = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(particleSet);
    }

    @Override
    public void handle(Context context) {
        ServerPlayer player = context.getSender();
        if (player == null)
            return;
        RecordingCapabilityProvider.setParticleSet(player, particleSet);
        S2CParticleColorChangedPacket packet = new S2CParticleColorChangedPacket(player.getUUID(), particleSet);
        GIPacketHandler.sendToTracking(packet, (ServerLevel) player.level(), player.blockPosition());
        GIPacketHandler.sendToClient(packet, player);
    }
}