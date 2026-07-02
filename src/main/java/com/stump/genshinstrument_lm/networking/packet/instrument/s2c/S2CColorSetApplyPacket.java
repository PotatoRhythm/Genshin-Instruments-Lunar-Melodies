package com.stump.genshinstrument_lm.networking.packet.instrument.s2c;

import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.client.colorSet.ColorSet;
import com.stump.genshinstrument_lm.client.colorSet.ColorSetManager;
import com.stump.genshinstrument_lm.util.ParticleShareUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class S2CColorSetApplyPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    private final String encoded;

    public S2CColorSetApplyPacket(String encoded) {
        this.encoded = encoded;
    }

    public S2CColorSetApplyPacket(FriendlyByteBuf buf) {
        this.encoded = buf.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(encoded);
    }

    @Override
    public void handle(NetworkEvent.Context ctx) {
        if (Minecraft.getInstance().player == null) { return; }

        ColorSet set = ParticleShareUtil.decode(encoded);

        if (set == null) { return; }

        var sets = ColorSetManager.getSets();
        sets.add(set);

        ColorSetManager.setActiveSet(sets.size() - 1);
        ColorSetManager.save();

        Minecraft.getInstance().player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "Imported color set: " + set.getName()
                )
        );

        ctx.setPacketHandled(true);
    }
}