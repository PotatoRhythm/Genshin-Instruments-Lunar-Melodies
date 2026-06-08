package com.stump.genshinstrument_lm.networking.packet.instrument.s2c;

import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.LooperParticlePacketUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class S2CLooperParticlePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private final BlockPos pos;
    private final double color;
    private final int colorSet;

    public S2CLooperParticlePacket(BlockPos pos, double color, int colorSet) {
        this.pos = pos;
        this.color = color;
        this.colorSet = colorSet;
    }

    public S2CLooperParticlePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.color = buf.readDouble();
        this.colorSet = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeDouble(color);
        buf.writeInt(colorSet);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        LooperParticlePacketUtil.spawnLooperParticle(pos, color, colorSet);
        ctx.setPacketHandled(true);
    }
}
