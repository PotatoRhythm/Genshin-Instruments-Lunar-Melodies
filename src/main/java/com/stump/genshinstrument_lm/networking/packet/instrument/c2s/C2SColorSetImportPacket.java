package com.stump.genshinstrument_lm.networking.packet.instrument.c2s;

import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.S2CColorSetApplyPacket;
import com.stump.genshinstrument_lm.util.ParticleShareUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class C2SColorSetImportPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final String encoded;

    public C2SColorSetImportPacket(String encoded) {
        this.encoded = encoded;
    }

    public C2SColorSetImportPacket(FriendlyByteBuf buf) {
        this.encoded = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(encoded);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        ServerPlayer sender = ctx.getSender();
        if (sender == null) { return; }
        if (!encoded.startsWith(ParticleShareUtil.PREFIX + ":")) { return; }
        GIPacketHandler.sendToClient(new S2CColorSetApplyPacket(encoded), sender);
        ctx.setPacketHandled(true);
    }
}