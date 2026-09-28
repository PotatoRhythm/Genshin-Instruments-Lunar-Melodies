package com.stump.songcraft_instruments.networking.packet.instrument.s2c;

import com.stump.songcraft_instruments.networking.IModPacket;
import com.stump.songcraft_instruments.sound.NoteSoundInstances;
import com.stump.songcraft_instruments.sound.held.HeldNoteSounds;
import com.stump.songcraft_instruments.sound.held.InitiatorID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class S2CLooperDampenPacket implements IModPacket {

    public static final NetworkDirection NETWORK_DIRECTION =
            NetworkDirection.PLAY_TO_CLIENT;

    private final InitiatorID initiatorID;

    public S2CLooperDampenPacket(InitiatorID initiatorID) {
        this.initiatorID = initiatorID;
    }

    public S2CLooperDampenPacket(FriendlyByteBuf buf) {
        this.initiatorID = InitiatorID.readFromNetwork(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        initiatorID.writeToNetwork(buf);
    }

    @Override
    public void handle(Context context) {
        NoteSoundInstances.dampenAll(initiatorID);
        HeldNoteSounds.dampenAll(initiatorID);
    }
}