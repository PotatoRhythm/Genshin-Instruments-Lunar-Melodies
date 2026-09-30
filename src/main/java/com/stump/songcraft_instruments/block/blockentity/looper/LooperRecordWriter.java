package com.stump.songcraft_instruments.block.blockentity.looper;

import com.stump.songcraft_instruments.block.blockentity.LooperBlockEntity;
import com.stump.songcraft_instruments.block.util.WritableNoteType;
import com.stump.songcraft_instruments.capability.recording.RecordingCapabilityProvider;
import com.stump.songcraft_instruments.gamerule.ModGameRules;
import com.stump.songcraft_instruments.networking.packet.instrument.NoteSoundMetadata;
import com.stump.songcraft_instruments.networking.packet.instrument.util.HeldSoundPhase;
import com.stump.songcraft_instruments.sound.NoteSound;
import com.stump.songcraft_instruments.sound.held.HeldNoteSound;
import com.stump.songcraft_instruments.util.CommonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

import static com.stump.songcraft_instruments.item.emirecord.BurnedRecordItem.*;

/**
 * Writes recorded notes into the looper's inserted record.
 */
public class LooperRecordWriter {
    public static final String PARTICLE_COLOR_TAG = "ParticleColor",
        PERFORMER_TAG = "Performer", PERFORMERS_TAG = "Performers",
        // The names and particle colors of the performers, in the same order as PERFORMERS_TAG
        PERFORMER_NAMES_TAG = "PerformerNames", PERFORMER_COLORS_TAG = "PerformerColors";

    private final LooperBlockEntity looper;

    public LooperRecordWriter(final LooperBlockEntity looper) {
        this.looper = looper;
    }


    /**
     * Writes a new note to the writable record.
     */
    public void writeNote(NoteSound sound, NoteSoundMetadata soundMeta, int timestamp, int particleRgb, UUID performer) {
        if (!looper.isWritable())
            return;

        final CompoundTag noteTag = serializeNoteMeta(soundMeta, timestamp, particleRgb, performer);
        noteTag.putString(NOTE_TYPE, WritableNoteType.REGULAR.name());

        noteTag.putInt(SOUND_INDEX_TAG, sound.index);
        noteTag.putString(SOUND_TYPE_TAG, sound.baseSoundLocation.toString());

        CommonUtil.getOrCreateListTag(looper.getChannel(), NOTES_TAG).add(noteTag);
        looper.setChanged();
    }
    /**
     * Writes a new note to the writable record.
     */
    public void writeHeldNote(HeldNoteSound sound, HeldSoundPhase phase,
                              NoteSoundMetadata soundMeta, int timestamp,
                              int particleRgb, UUID performer) {
        if (!looper.isWritable())
            return;

        final CompoundTag noteTag = serializeNoteMeta(soundMeta, timestamp, particleRgb, performer);
        noteTag.putString(NOTE_TYPE, WritableNoteType.HELD.name());

        noteTag.putInt(SOUND_INDEX_TAG, sound.index());
        noteTag.putString(SOUND_TYPE_TAG, sound.baseSoundLocation().toString());
        noteTag.putString(HELD_PHASE, phase.name());

        CommonUtil.getOrCreateListTag(looper.getChannel(), NOTES_TAG).add(noteTag);
        looper.setChanged();
    }

    public void writeDampen(int timestamp, UUID performer) {
        if (!looper.isWritable())
            return;

        final CompoundTag noteTag = new CompoundTag();

        noteTag.putString(NOTE_TYPE, WritableNoteType.DAMPEN.name());
        noteTag.putInt(TIMESTAMP_TAG, timestamp);
        noteTag.putInt(PERFORMER_TAG, getPerformerIndex(performer));

        CommonUtil.getOrCreateListTag(looper.getChannel(), NOTES_TAG).add(noteTag);

        looper.setChanged();
    }

    private CompoundTag serializeNoteMeta(NoteSoundMetadata soundMeta, int timestamp, int particleRgb, UUID performer) {
        final CompoundTag noteTag = new CompoundTag();

        noteTag.putInt(PITCH_TAG, soundMeta.pitch());
        noteTag.putFloat(VOLUME_TAG, soundMeta.volume() / 100f);
        noteTag.putInt(PARTICLE_COLOR_TAG, particleRgb);
        noteTag.putInt(TIMESTAMP_TAG, timestamp);
        // Stored per note so records can hold multiple instruments (group recordings)
        noteTag.putString(INSTRUMENT_ID_TAG, soundMeta.instrumentId().toString());
        noteTag.putInt(PERFORMER_TAG, getPerformerIndex(performer));

        return noteTag;
    }

    /**
     * Each player recording on a record is a performer, identified in its notes by their index in the
     * record's performer list. Performers are played back as separate initiators, so that dampening
     * and held notes of one do not affect the others.
     * @return The index of the performer, added to the record if not yet present
     */
    private int getPerformerIndex(final UUID performer) {
        final CompoundTag channel = looper.getChannel();
        if (!channel.contains(PERFORMERS_TAG, Tag.TAG_LIST))
            channel.put(PERFORMERS_TAG, new ListTag());

        final ListTag performers = channel.getList(PERFORMERS_TAG, Tag.TAG_INT_ARRAY);
        for (int i = 0; i < performers.size(); i++) {
            if (NbtUtils.loadUUID(performers.get(i)).equals(performer))
                return i;
        }

        performers.add(NbtUtils.createUUID(performer));
        CommonUtil.getOrCreateListTag(channel, PERFORMER_NAMES_TAG).add(StringTag.valueOf(getPlayerName(performer)));
        CommonUtil.getOrCreateListTag(channel, PERFORMER_COLORS_TAG)
            .add(new IntArrayTag(getPlayerParticleColors(performer)));
        return performers.size() - 1;
    }

    /**
     * @return The particle colors the performer's client sent when the recording started. Empty if unknown.
     */
    private int[] getPlayerParticleColors(final UUID playerId) {
        final Player player = looper.getLevel().getPlayerByUUID(playerId);
        return (player != null) ? RecordingCapabilityProvider.getParticleColors(player) : new int[0];
    }

    /**
     * @return The name of the (online) performer, as shown on the record's tooltip
     */
    private String getPlayerName(final UUID playerId) {
        final Player player = looper.getLevel().getPlayerByUUID(playerId);
        return (player != null) ? player.getGameProfile().getName() : playerId.toString();
    }

    /**
     * Discards everything recorded so far
     */
    public void clearRecordedNotes() {
        looper.getChannel().remove(NOTES_TAG);
        looper.getChannel().remove(PERFORMERS_TAG);
        looper.getChannel().remove(PERFORMER_NAMES_TAG);
        looper.getChannel().remove(PERFORMER_COLORS_TAG);
        looper.setTicks(0);
        looper.setChanged();
    }

    /**
     * A capped looper is a looper that cannot have any more notes in it, as defined in {@link ModGameRules#RULE_LOOPER_MAX_NOTES}.
     * Any negative will make the looper uncappable.
     * @return Whether this looper is capped
     */
    public boolean isCapped(final Level level) {
        final int cap = level.getGameRules().getInt(ModGameRules.RULE_LOOPER_MAX_NOTES);
        return (cap >= 0) && (looper.getChannel().getList(NOTES_TAG, Tag.TAG_COMPOUND).size() >= cap);
    }
}
