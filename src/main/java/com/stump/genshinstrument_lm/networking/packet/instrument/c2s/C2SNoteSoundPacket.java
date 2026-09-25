package com.stump.genshinstrument_lm.networking.packet.instrument.c2s;

import com.stump.genshinstrument_lm.client.gui.instrument.partial.note.NoteButton;
import com.stump.genshinstrument_lm.networking.packet.instrument.NoteSoundMetadata;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.stump.genshinstrument_lm.sound.NoteSound;
import com.stump.genshinstrument_lm.util.ParticleColorUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * A C2S packet notifying the server that a
 * specific {@link NoteSound} should be played in the level
 */
public class C2SNoteSoundPacket extends C2SNotePacket<NoteSound> {

    public C2SNoteSoundPacket(NoteSound sound, NoteSoundMetadata meta) {
        super(sound, meta);
    }
    @OnlyIn(Dist.CLIENT)
    public C2SNoteSoundPacket(NoteButton noteButton, NoteSound sound, int pitch) {
        super(
                sound,
                new NoteSoundMetadata(
                        noteButton.getSoundSourcePos(),
                        pitch,
                        noteButton.instrumentScreen.volume,
                        ParticleColorUtil.getNoteRGB(sound.index, pitch),
                        noteButton.instrumentScreen.getInstrumentId(),
                        Optional.ofNullable(noteButton.getIdentifier())
                )
        );
    }
    public C2SNoteSoundPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    protected void writeSound(FriendlyByteBuf buf) {
        sound.writeToNetwork(buf);
    }
    @Override
    protected NoteSound readSound(FriendlyByteBuf buf) {
        return NoteSound.readFromNetwork(buf);
    }

    protected void sendPlayNotePackets(final ServerPlayer player) {
        NoteSoundPacketUtil.sendPlayerPlayNotePackets(player, sound, meta);
    }
}