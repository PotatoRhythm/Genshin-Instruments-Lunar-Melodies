package com.stump.songcraft_instruments.block.blockentity.looper;

import com.mojang.logging.LogUtils;
import com.stump.songcraft_instruments.block.blockentity.LooperBlockEntity;
import com.stump.songcraft_instruments.block.blockentity.SpeakerBlockEntity;
import com.stump.songcraft_instruments.block.util.WritableNoteType;
import com.stump.songcraft_instruments.networking.SCPacketHandler;
import com.stump.songcraft_instruments.networking.packet.instrument.NoteSoundMetadata;
import com.stump.songcraft_instruments.networking.packet.instrument.s2c.S2CLooperDampenPacket;
import com.stump.songcraft_instruments.networking.packet.instrument.s2c.S2CLooperParticlePacket;
import com.stump.songcraft_instruments.networking.packet.instrument.util.HeldNoteSoundPacketUtil;
import com.stump.songcraft_instruments.networking.packet.instrument.util.HeldSoundPhase;
import com.stump.songcraft_instruments.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.stump.songcraft_instruments.sound.NoteSound;
import com.stump.songcraft_instruments.sound.held.HeldNoteSound;
import com.stump.songcraft_instruments.sound.held.InitiatorID;
import com.stump.songcraft_instruments.sound.registrar.HeldNoteSoundRegistrar;
import com.stump.songcraft_instruments.sound.registrar.NoteSoundRegistrar;
import com.stump.songcraft_instruments.util.SpeakerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.stump.songcraft_instruments.block.blockentity.looper.LooperRecordWriter.PARTICLE_COLOR_TAG;
import static com.stump.songcraft_instruments.block.blockentity.looper.LooperRecordWriter.PERFORMER_TAG;
import static com.stump.songcraft_instruments.item.emirecord.BurnedRecordItem.*;

/**
 * Plays back the notes of the looper's inserted record, to nearby players and paired speakers.
 */
public class LooperPlayback {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final LooperBlockEntity looper;
    private final InitiatorID looperInitiatorID;
    private int heldParticleTimer = 0;

    /**
     * A set of cached notes as to use them
     * for pausing and resuming the looper.
     */
    private final HashSet<CachedHeldNote> cachedHeldNotes = new HashSet<>();
    private record CachedHeldNote(HeldNoteSound sound, NoteSoundMetadata meta, int particleRgb, InitiatorID initiator) {}

    public LooperPlayback(final LooperBlockEntity looper, final BlockPos pos) {
        this.looper = looper;
        this.looperInitiatorID = new InitiatorID("block",
            String.format("x%sy%sz%s", pos.getX(), pos.getY(), pos.getZ())
        );
    }


    public void stopAndClearHeldSounds() {
        notifyHeldNotesPhase(HeldSoundPhase.RELEASE);
        cachedHeldNotes.clear();
    }

    public void notifyHeldNotesPhase(final HeldSoundPhase phase) {
        final List<SpeakerBlockEntity> speakers = getPairedSpeakers();

        cachedHeldNotes.forEach((note) -> {
            HeldNoteSoundPacketUtil.sendPlayNotePackets(
                looper.getLevel(),
                note.sound(), note.meta(),
                phase,
                note.initiator()
            );
            speakers.forEach((speaker) -> speaker.playHeldNote(note.sound(), note.meta(), phase, note.initiator()));
        });
    }

    /**
     * return the speakers paired to this looper, which relay everything it plays
     */
    private List<SpeakerBlockEntity> getPairedSpeakers() {
        final Level level = looper.getLevel();
        if (level == null || level.isClientSide)
            return List.of();
        return SpeakerUtil.getFromBlock(level, looper);
    }

    /**
     * @return The initiator a performer's notes are played back with.
     * The first performer (and solo recordings) use the looper's own initiator.
     */
    private InitiatorID getPerformerInitiatorID(final int performerIndex) {
        return (performerIndex == 0)
            ? looperInitiatorID
            : new InitiatorID(looperInitiatorID.type(), looperInitiatorID.identifier() + "p" + performerIndex);
    }


    public void playNote(final CompoundTag note) {
        try {
            // Acquire note type
            final WritableNoteType noteType;
            final String rawNoteType = note.getString(NOTE_TYPE);

            // Support for older versions
            if (rawNoteType.isEmpty()) {
                noteType = WritableNoteType.REGULAR;
            } else {
                noteType = WritableNoteType.valueOf(rawNoteType);
            }

            switch (noteType) {
                case REGULAR:
                    playNoteSound(note);
                    break;

                case HELD:
                    playHeldSound(note);
                    break;

                case DAMPEN:
                    dampenSounds(getPerformerInitiatorID(note.getInt(PERFORMER_TAG)));
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Attempted to play a looper note at {}, but met with an exception", looper.getBlockPos(), e);
        }
    }

    private void playNoteSound(final CompoundTag noteTag) {
        final NoteSoundMetadata meta = metaFromNoteTag(noteTag);
        final ResourceLocation soundLocation = new ResourceLocation(noteTag.getString(SOUND_TYPE_TAG));
        final int soundIndex = noteTag.getInt(SOUND_INDEX_TAG);

        final NoteSound sound = NoteSoundRegistrar.getSounds(soundLocation)[soundIndex];
        final InitiatorID initiator = getPerformerInitiatorID(noteTag.getInt(PERFORMER_TAG));

        NoteSoundPacketUtil.sendPlayNotePackets(
                looper.getLevel(),
                sound,
                meta,
                initiator
        );
        getPairedSpeakers().forEach((speaker) -> speaker.playNote(sound, meta, initiator));

        int rgb = noteTag.getInt(PARTICLE_COLOR_TAG);

        triggerEmitNoteParticle(rgb);
    }

    private void playHeldSound(final CompoundTag noteTag) {
        final NoteSoundMetadata meta = metaFromNoteTag(noteTag);

        final ResourceLocation soundLocation = new ResourceLocation(noteTag.getString(SOUND_TYPE_TAG));
        final int soundIndex = noteTag.getInt(SOUND_INDEX_TAG);
        final HeldNoteSound sound = HeldNoteSoundRegistrar.getSounds(soundLocation)[soundIndex];

        final HeldSoundPhase phase = HeldSoundPhase.valueOf(noteTag.getString(HELD_PHASE));
        final InitiatorID initiator = getPerformerInitiatorID(noteTag.getInt(PERFORMER_TAG));

        HeldNoteSoundPacketUtil.sendPlayNotePackets(
            looper.getLevel(), sound,
            meta, phase, initiator
        );
        getPairedSpeakers().forEach((speaker) -> speaker.playHeldNote(sound, meta, phase, initiator));

        if (phase == HeldSoundPhase.ATTACK) {
            int rgb = noteTag.getInt(PARTICLE_COLOR_TAG);

            cachedHeldNotes.add(new CachedHeldNote(sound, meta, rgb, initiator));
            triggerEmitNoteParticle(rgb);

        } else if (phase == HeldSoundPhase.RELEASE) {
            cachedHeldNotes.removeIf((note) ->
                    note.sound().equals(sound) &&
                            note.meta().equals(meta) &&
                            note.initiator().equals(initiator)
            );
        }
    }

    /**
     * Dampens the sounds of a single performer
     */
    private void dampenSounds(final InitiatorID initiator) {
        // Speakers don't get the dampen packet, so release their copies of the held notes
        getPairedSpeakers().forEach((speaker) -> speaker.releaseHeldNotesFrom(initiator));
        cachedHeldNotes.removeIf((note) -> note.initiator().equals(initiator));

        SCPacketHandler.sendToTracking(
                new S2CLooperDampenPacket(initiator), (ServerLevel) looper.getLevel(), looper.getBlockPos()
        );
    }

    private NoteSoundMetadata metaFromNoteTag(final CompoundTag noteTag) {
        return new NoteSoundMetadata(
            looper.getBlockPos(),
            noteTag.getInt(PITCH_TAG),
            (int)(noteTag.getFloat(VOLUME_TAG) * 100),
            noteTag.getInt(PARTICLE_COLOR_TAG),
            new ResourceLocation(noteTag.getString(INSTRUMENT_ID_TAG)), Optional.empty()
        );
    }

    public void triggerEmitNoteParticle(int rgb) {

        double size = 0.2;

        SCPacketHandler.sendToTracking(
                new S2CLooperParticlePacket(
                        looper.getBlockPos(),
                        rgb,
                        size
                ),
                (ServerLevel) looper.getLevel(),
                looper.getBlockPos()
        );
    }

    public void emitHeldParticles() {
        if (cachedHeldNotes.isEmpty())
            return;
        if (++heldParticleTimer < 10)
            return;

        heldParticleTimer = 0;

        for (CachedHeldNote heldNote : cachedHeldNotes)
        {
            triggerEmitNoteParticle(
                    heldNote.particleRgb()
            );
        }
    }
}
