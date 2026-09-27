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
import com.stump.genshinstrument_lm.util.SpeakerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * immediately rebroadcasts the notes of its paired instruments from this block's position
 */
public class SpeakerBlockEntity extends BlockEntity {
    private static final double PARTICLE_SIZE = 0.2;
    private static final String SPEAKER_ID_TAG = "speaker_id", INSTRUMENTS_TAG = "instruments",
        INSTRUMENT_ID_TAG = "id", INSTRUMENT_POS_TAG = "pos";
    /**
     * how often (in ticks) paired block instruments are checked for still being paired
     */
    private static final int VALIDATE_INTERVAL = 40;
    /**
     * how often (in ticks) held notes re-emit their particle while sustained, same as the looper
     */
    private static final int HELD_PARTICLE_INTERVAL = 10;
    private int heldParticleTimer = 0;

    private final InitiatorID speakerInitiatorID;

    /**
     * Held notes currently being sustained through this speaker,
     * kept so they can be released if the speaker is removed mid-sustain.
     */
    private record HeldNoteKey(HeldNoteSound sound, NoteSoundMetadata meta) {}
    private final HashSet<HeldNoteKey> sustainedNotes = new HashSet<>();

    /**
     * identifies this specific speaker, so an instrument can tell it apart from
     * a new speaker later placed at the same position.
     */
    private UUID speakerId = UUID.randomUUID();

    /**
     * the instruments paired to this speaker, by instrument ID. block instruments map to their position
     * so they can be re-checked; held instruments map to null.
     * the speaker shows as connected exactly while this is non-empty.
     */
    private final HashMap<UUID, BlockPos> pairedInstruments = new HashMap<>();

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

    //#region Pairing

    public UUID getSpeakerId() {
        return speakerId;
    }

    public boolean hasInstrument(final UUID instrumentId) {
        return pairedInstruments.containsKey(instrumentId);
    }
    /**
     * @param instrumentPos The block instrument's position, or null for a held instrument
     */
    public void addInstrument(final UUID instrumentId, @Nullable final BlockPos instrumentPos) {
        pairedInstruments.put(instrumentId, instrumentPos);
        onPairingsChanged();
    }
    public void removeInstrument(final UUID instrumentId) {
        // containsKey, since held instruments map to a null position
        if (!pairedInstruments.containsKey(instrumentId))
            return;

        pairedInstruments.remove(instrumentId);
        onPairingsChanged();
    }

    private void onPairingsChanged() {
        setChanged();
        updateConnectedState();
    }
    /**
     * swaps da front texture between speaker_front and speaker_front_connected,
     * according to whether any instrument is paired
     */
    private void updateConnectedState() {
        if (level == null || level.isClientSide)
            return;

        final BlockState state = getBlockState();
        final boolean connected = !pairedInstruments.isEmpty();
        if (state.getValue(SpeakerBlock.CONNECTED) != connected)
            level.setBlockAndUpdate(getBlockPos(), state.setValue(SpeakerBlock.CONNECTED, connected));
    }

    public void tick(final Level level) {
        emitHeldParticles();

        if (level.getGameTime() % VALIDATE_INTERVAL == 0)
            validateBlockInstruments(level);
    }
    /**
     * drops block instruments that were broken or no longer list this speaker.
     * also corrects the connected state, e.g. for speakers saved by an older version.
     */
    private void validateBlockInstruments(final Level level) {
        final boolean changed = pairedInstruments.entrySet().removeIf((entry) -> {
            final BlockPos instrumentPos = entry.getValue();
            return (instrumentPos != null)
                && level.isLoaded(instrumentPos)
                && !SpeakerUtil.isBlockInstrumentPaired(level, instrumentPos, entry.getKey(), this);
        });

        if (changed)
            setChanged();
        updateConnectedState();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putUUID(SPEAKER_ID_TAG, speakerId);

        final ListTag instruments = new ListTag();
        pairedInstruments.forEach((instrumentId, instrumentPos) -> {
            final CompoundTag instrumentTag = new CompoundTag();
            instrumentTag.putUUID(INSTRUMENT_ID_TAG, instrumentId);
            if (instrumentPos != null)
                instrumentTag.put(INSTRUMENT_POS_TAG, NbtUtils.writeBlockPos(instrumentPos));
            instruments.add(instrumentTag);
        });
        pTag.put(INSTRUMENTS_TAG, instruments);
    }
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.hasUUID(SPEAKER_ID_TAG))
            speakerId = pTag.getUUID(SPEAKER_ID_TAG);

        pairedInstruments.clear();
        final ListTag instruments = pTag.getList(INSTRUMENTS_TAG, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < instruments.size(); i++) {
            final CompoundTag instrumentTag = instruments.getCompound(i);
            if (!instrumentTag.hasUUID(INSTRUMENT_ID_TAG))
                continue;

            pairedInstruments.put(instrumentTag.getUUID(INSTRUMENT_ID_TAG),
                instrumentTag.contains(INSTRUMENT_POS_TAG, CompoundTag.TAG_COMPOUND)
                    ? NbtUtils.readBlockPos(instrumentTag.getCompound(INSTRUMENT_POS_TAG))
                    : null
            );
        }
    }

    //#endregion

    private void emitNoteParticle(final int rgb) {
        GIPacketHandler.sendToTracking(
            new S2CLooperParticlePacket(getBlockPos(), rgb, PARTICLE_SIZE),
            (ServerLevel) getLevel(),
            getBlockPos()
        );
    }

    /**
     * keeps emitting particles while held notes (e.g. the flute's) are sustained,
     * as the looper does in LooperBlockEntity
     */
    private void emitHeldParticles() {
        if (sustainedNotes.isEmpty()) {
            heldParticleTimer = 0;
            return;
        }
        if (++heldParticleTimer < HELD_PARTICLE_INTERVAL)
            return;

        heldParticleTimer = 0;
        sustainedNotes.forEach((key) -> emitNoteParticle(key.meta().particleColor()));
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
