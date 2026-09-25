package com.stump.genshinstrument_lm.networking.packet.instrument.s2c;

import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.ColorSetPacketUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class S2CColorSetAddPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final String encoded;

    public S2CColorSetAddPacket(String encoded) {
        this.encoded = encoded;
    }

    public S2CColorSetAddPacket(FriendlyByteBuf buf) {
        this.encoded = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(encoded);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> ColorSetPacketUtil.addColorSet(encoded));
        context.setPacketHandled(true);
    }
}