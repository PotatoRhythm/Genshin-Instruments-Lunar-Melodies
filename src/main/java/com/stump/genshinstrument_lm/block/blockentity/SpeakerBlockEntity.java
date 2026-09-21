package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.networking.GIPacketHandler;
import com.stump.genshinstrument_lm.networking.packet.instrument.NoteSoundMetadata;
import com.stump.genshinstrument_lm.networking.packet.instrument.s2c.S2CLooperParticlePacket;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.HeldNoteSoundPacketUtil;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.HeldSoundPhase;
import com.stump.genshinstrument_lm.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.stump.genshinstrument_lm.sound.NoteSound;
import com.stump.genshinstrument_lm.sound.held.HeldNoteSound;
import com.stump.genshinstrument_lm.sound.held.InitiatorID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

/**
 * immediately rebroadcasts the notes of a paired instrument from this block's position,
 * rather than recording them for later playback like LooperBlockEntity does.
 */
public class SpeakerBlockEntity extends BlockEntity {
    private static final double MIN_NOTE = -12, MAX_NOTE = 30;

    private final InitiatorID speakerInitiatorID;

    /**
     * Held notes currently being sustained through this speaker,
     * kept so they can be released if the speaker is removed mid-sustain.
     */
    private record HeldNoteKey(HeldNoteSound sound, NoteSoundMetadata meta) {}
    private final HashSet<HeldNoteKey> sustainedNotes = new HashSet<>();

    public SpeakerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SPEAKER.get(), pPos, pBlockState);
        this.speakerInitiatorID = new InitiatorID("block",
            String.format("x%sy%sz%s", pPos.getX(), pPos.getY(), pPos.getZ())
        );
    }

    /**
     * @return The same metadata, but originating from this speaker's position.
     */
    private NoteSoundMetadata relocate(final NoteSoundMetadata meta) {
        return new NoteSoundMetadata(getBlockPos(), meta.pitch(), meta.volume(), meta.instrumentId(), meta.noteIdentifier());
    }

    public void playNote(final NoteSound sound, final NoteSoundMetadata meta) {
        final NoteSoundMetadata relocated = relocate(meta);

        NoteSoundPacketUtil.sendPlayNotePackets(level, sound, relocated);
        emitNoteParticle(sound.index + relocated.pitch());
    }

    public void playHeldNote(final HeldNoteSound sound, final NoteSoundMetadata meta, final HeldSoundPhase phase) {
        final NoteSoundMetadata relocated = relocate(meta);

        HeldNoteSoundPacketUtil.sendPlayNotePackets(level, sound, relocated, phase, speakerInitiatorID);

        final HeldNoteKey key = new HeldNoteKey(sound, relocated);
        if (phase == HeldSoundPhase.ATTACK) {
            sustainedNotes.add(key);
            emitNoteParticle(sound.index() + relocated.pitch());
        } else if (phase == HeldSoundPhase.RELEASE) {
            sustainedNotes.remove(key);
        }
    }

    private void emitNoteParticle(final int noteIndex) {
        double particleColor = (noteIndex - MIN_NOTE) / (MAX_NOTE - MIN_NOTE);
        particleColor = Mth.clamp(particleColor, 0.0, 1.0);

        GIPacketHandler.sendToTracking(
            new S2CLooperParticlePacket(getBlockPos(), particleColor, 0),
            (ServerLevel) getLevel(),
            getBlockPos()
        );
    }

    private void releaseSustainedNotes() {
        sustainedNotes.forEach((key) ->
            HeldNoteSoundPacketUtil.sendPlayNotePackets(
                level, key.sound(), key.meta(), HeldSoundPhase.RELEASE, speakerInitiatorID
            )
        );
        sustainedNotes.clear();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        releaseSustainedNotes();
    }
}
