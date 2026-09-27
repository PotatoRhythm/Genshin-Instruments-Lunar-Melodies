package com.stump.genshinstrument_lm.block.blockentity;

import com.stump.genshinstrument_lm.block.SpeakerBlock;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

/**
 * immediately rebroadcasts the notes of a paired instrument from this block's position
 */
public class SpeakerBlockEntity extends BlockEntity {
    private static final double PARTICLE_SIZE = 0.2;
    private static final String PAIR_COUNT_TAG = "pair_count";

    private final InitiatorID speakerInitiatorID;

    /**
     * Held notes currently being sustained through this speaker,
     * kept so they can be released if the speaker is removed mid-sustain.
     */
    private record HeldNoteKey(HeldNoteSound sound, NoteSoundMetadata meta) {}
    private final HashSet<HeldNoteKey> sustainedNotes = new HashSet<>();

    /**
     * counts how many instruments are paired to this speaker. pairings live on the instruments,
     * so this is a best-effort count: it goes up on pair and down on an explicit unpair,
     * but an instrument destroyed while paired is not subtracted.
     */
    private int pairCount = 0;

    public SpeakerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SPEAKER.get(), pPos, pBlockState);
        this.speakerInitiatorID = new InitiatorID("block",
            String.format("x%sy%sz%s", pPos.getX(), pPos.getY(), pPos.getZ())
        );
    }

    /**
     * return The same metadata, but originating from this speaker's position.
     */
    private NoteSoundMetadata relocate(final NoteSoundMetadata meta) {
        return new NoteSoundMetadata(getBlockPos(), meta.pitch(), meta.volume(), meta.particleColor(), meta.instrumentId(), meta.noteIdentifier());
    }

    public void playNote(final NoteSound sound, final NoteSoundMetadata meta) {
        final NoteSoundMetadata relocated = relocate(meta);

        NoteSoundPacketUtil.sendPlayNotePackets(level, sound, relocated);
        emitNoteParticle(relocated.particleColor());
    }

    public void playHeldNote(final HeldNoteSound sound, final NoteSoundMetadata meta, final HeldSoundPhase phase) {
        final NoteSoundMetadata relocated = relocate(meta);

        HeldNoteSoundPacketUtil.sendPlayNotePackets(level, sound, relocated, phase, speakerInitiatorID);

        final HeldNoteKey key = new HeldNoteKey(sound, relocated);
        if (phase == HeldSoundPhase.ATTACK) {
            sustainedNotes.add(key);
            emitNoteParticle(relocated.particleColor());
        } else if (phase == HeldSoundPhase.RELEASE) {
            sustainedNotes.remove(key);
        }
    }

    public void onPaired() {
        setPairCount(pairCount + 1);
    }
    public void onUnpaired() {
        setPairCount(Math.max(0, pairCount - 1));
    }
    private void setPairCount(final int count) {
        pairCount = count;
        setChanged();

        // swap da front texture between speaker_front and speaker_front_connected
        final BlockState state = getBlockState();
        final boolean connected = pairCount > 0;
        if (state.getValue(SpeakerBlock.CONNECTED) != connected)
            level.setBlockAndUpdate(getBlockPos(), state.setValue(SpeakerBlock.CONNECTED, connected));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt(PAIR_COUNT_TAG, pairCount);
    }
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        pairCount = pTag.getInt(PAIR_COUNT_TAG);
    }

    private void emitNoteParticle(final int rgb) {
        GIPacketHandler.sendToTracking(
            new S2CLooperParticlePacket(getBlockPos(), rgb, PARTICLE_SIZE),
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
