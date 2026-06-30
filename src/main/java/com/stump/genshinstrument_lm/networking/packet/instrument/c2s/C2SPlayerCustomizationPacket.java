package com.stump.genshinstrument_lm.networking.packet.instrument.c2s;

import com.stump.genshinstrument_lm.capability.playerCustomization.PlayerCustomizationProvider;
import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.particle.ColorSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class C2SPlayerCustomizationPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION =
            NetworkDirection.PLAY_TO_SERVER;

    private final int activeSet;
    private final List<ColorSet> colorSets;

    public C2SPlayerCustomizationPacket(int activeSet, List<ColorSet> colorSets) {
        this.activeSet = activeSet;
        this.colorSets = colorSets;
    }

    public C2SPlayerCustomizationPacket(FriendlyByteBuf buf) {
        this.activeSet = buf.readInt();
        int setCount = buf.readInt();
        this.colorSets = new ArrayList<>();

        for (int i = 0; i < setCount; i++) {
            String name = buf.readUtf();
            int colorCount = buf.readInt();
            int[] colors = new int[colorCount];
            for (int c = 0; c < colorCount; c++) {
                colors[c] = buf.readInt();
            }
            colorSets.add(new ColorSet(name, colors));
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(activeSet);
        buf.writeInt(colorSets.size());

        for (ColorSet set : colorSets) {
            buf.writeUtf(set.getName());
            int[] colors = set.getColors();
            buf.writeInt(colors.length);
            for (int color : colors) {
                buf.writeInt(color);
            }
        }
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) { return; }
        player.getCapability(PlayerCustomizationProvider.CAPABILITY)
                .ifPresent(data -> {
                    data.setActiveColorSet(activeSet);
                    data.setColorSets(
                            new ArrayList<>(colorSets)
                    );
                });

        context.setPacketHandled(true);
    }
}