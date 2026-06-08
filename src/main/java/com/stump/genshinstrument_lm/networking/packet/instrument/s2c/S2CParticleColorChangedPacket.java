package com.stump.genshinstrument_lm.networking.packet.instrument.s2c;

import com.stump.genshinstrument_lm.client.ClientInstrumentData;
import com.stump.genshinstrument_lm.networking.IModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class S2CParticleColorChangedPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final UUID playerId;
    private final int particleSet;

    public S2CParticleColorChangedPacket(UUID playerId, int particleSet) {
        this.playerId = playerId;
        this.particleSet = particleSet;
    }

    public S2CParticleColorChangedPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readUUID();
        this.particleSet = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
        buf.writeInt(particleSet);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;
        Player player = mc.level.getPlayerByUUID(playerId);
        if (player == null)
            return;

        ClientInstrumentData.setParticleSet(playerId, particleSet);
        ctx.setPacketHandled(true);
    }
}