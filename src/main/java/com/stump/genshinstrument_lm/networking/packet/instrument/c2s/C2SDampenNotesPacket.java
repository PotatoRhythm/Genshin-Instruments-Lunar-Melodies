package com.stump.genshinstrument_lm.networking.packet.instrument.c2s;

import com.stump.genshinstrument_lm.block.blockentity.LooperBlockEntity;
import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.S2CDampenNotesPacket;
import com.stump.genshinstrument_lm.util.LooperUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class C2SDampenNotesPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final int initiatorId;

    public C2SDampenNotesPacket(int initiatorId) {
        this.initiatorId = initiatorId;
    }

    public C2SDampenNotesPacket(FriendlyByteBuf buf) {
        this.initiatorId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(initiatorId);
    }

    @Override
    public void handle(Context context) {
        final ServerPlayer player = context.getSender();

        if (player == null)
            return;

        if (LooperUtil.isRecording(player)) {
            final BlockPos looperPos = LooperUtil.getRecordingLooperPos(player);

            if (looperPos != null) {
                final LooperBlockEntity looper =
                        LooperUtil.getFromPos(player.level(), looperPos);

                if (looper != null) {
                    looper.writeDampen(looper.getTicks());
                }
            }
        }

        GIPacketHandler.sendToTrackingEntity(
                new S2CDampenNotesPacket(player.getId()),
                player
        );
    }
}