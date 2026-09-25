package com.stump.genshinstrument_lm.networking.packet.instrument.s2c;

import com.stump.genshinstrument_lm.networking.IModPacket;
import com.stump.genshinstrument_lm.sound.NoteSoundInstances;
import com.stump.genshinstrument_lm.sound.held.HeldNoteSounds;
import com.stump.genshinstrument_lm.sound.held.InitiatorID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class S2CDampenNotesPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final int initiatorId;

    public S2CDampenNotesPacket(int initiatorId) {
        this.initiatorId = initiatorId;
    }

    public S2CDampenNotesPacket(FriendlyByteBuf buf) {
        this.initiatorId = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(initiatorId);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(Context context) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null)
            return;

        final var entity = minecraft.level.getEntity(initiatorId);

        if (entity == null)
            return;

        NoteSoundInstances.dampenAll(initiatorId);
        HeldNoteSounds.dampenAll(InitiatorID.fromEntity(entity));
    }
}