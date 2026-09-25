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
    private final int rgb;
    private final double size;

    public S2CLooperParticlePacket(BlockPos pos, int rgb, double size) {
        this.pos = pos;
        this.rgb = rgb;
        this.size = size;
    }

    public S2CLooperParticlePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.rgb = buf.readInt();
        this.size = buf.readDouble();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(rgb);
        buf.writeDouble(size);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        LooperParticlePacketUtil.spawnLooperParticle(pos, rgb, size);
        context.setPacketHandled(true);
    }
}