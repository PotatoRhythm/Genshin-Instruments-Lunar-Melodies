package com.stump.genshinstrument_lm.util;

import com.stump.genshinstrument_lm.GInstrumentMod;
import com.stump.genshinstrument_lm.block.blockentity.LooperBlockEntity;
import com.stump.genshinstrument_lm.block.blockentity.SpeakerBlockEntity;
import com.stump.genshinstrument_lm.block.partial.IDoubleBlock;
import com.stump.genshinstrument_lm.event.InstrumentPlayedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * handles pairing an instrument to up to MAX_SPEAKERS SpeakerBlockEntities and resolving
 * those pairings when the instrument is played.
 *
 * a pairing is recorded on both sides: the instrument stores each speaker's position and ID,
 * and the speaker stores the instrument's ID. a pairing only counts while both sides agree,
 * so a broken speaker (or a new speaker placed where an old one was) is dropped from
 * the instrument's list the next time that list is read.
 *
 * loopers pair the same way as block instruments (the "instrument" data lives on the looper),
 * except that a speaker accepts at most one looper. the looper relays its own playback to its speakers.
 *
 * instrument tag layout: speaker: { instrument_id: UUID, speakers: [ { pos: {X,Y,Z}, id: UUID }, ... ] }
 */
public class SpeakerUtil {
    public static final String SPEAKER_TAG = "speaker", SPEAKERS_TAG = "speakers",
        INSTRUMENT_ID_TAG = "instrument_id", POS_TAG = "pos", ID_TAG = "id";
    public static final int MAX_SPEAKERS = 8;

    public enum PairResult { PAIRED, ALREADY_PAIRED, FULL, LOOPER_TAKEN }

    private record SpeakerEntry(BlockPos pos, UUID speakerId) {}


    //#region Pairing

    public static PairResult pair(final Level level, final ItemStack instrument, final SpeakerBlockEntity sbe) {
        return pair(level, GInstrumentMod.modTag(instrument), sbe, null, false);
    }
    /**
     * pairs a block source: a block instrument (including the other half of a double block, like the Keyboard)
     * or a looper. a speaker accepts at most one looper.
     */
    public static PairResult pair(final Level level, final BlockEntity source, final SpeakerBlockEntity sbe) {
        final boolean isLooper = source instanceof LooperBlockEntity;
        final PairResult result = pair(level, GInstrumentMod.modTag(source), sbe, source.getBlockPos(), isLooper);
        onBlockInstrumentChanged(level, source);
        return result;
    }
    private static PairResult pair(final Level level, final CompoundTag modTag, final SpeakerBlockEntity sbe,
                                   @Nullable final BlockPos instrumentPos, final boolean isLooper) {
        final List<SpeakerEntry> entries = getValidEntries(level, modTag);
        final UUID instrumentId = getOrCreateInstrumentId(modTag);

        final SpeakerEntry newEntry = new SpeakerEntry(sbe.getBlockPos(), sbe.getSpeakerId());
        if (entries.contains(newEntry)) {
            writeEntries(modTag, entries);
            return PairResult.ALREADY_PAIRED;
        }
        if (entries.size() >= MAX_SPEAKERS) {
            writeEntries(modTag, entries);
            return PairResult.FULL;
        }
        if (isLooper && sbe.hasOtherLooper(level, instrumentId)) {
            writeEntries(modTag, entries);
            return PairResult.LOOPER_TAKEN;
        }

        entries.add(newEntry);
        writeEntries(modTag, entries);
        sbe.addInstrument(instrumentId, instrumentPos, isLooper);
        return PairResult.PAIRED;
    }

    /**
     * return whether the speaker was paired to the instrument
     */
    public static boolean unpair(final Level level, final ItemStack instrument, final SpeakerBlockEntity sbe) {
        return unpair(level, GInstrumentMod.modTag(instrument), sbe);
    }
    public static boolean unpair(final Level level, final BlockEntity instrument, final SpeakerBlockEntity sbe) {
        final boolean removed = unpair(level, GInstrumentMod.modTag(instrument), sbe);
        onBlockInstrumentChanged(level, instrument);
        return removed;
    }
    private static boolean unpair(final Level level, final CompoundTag modTag, final SpeakerBlockEntity sbe) {
        final UUID instrumentId = getInstrumentId(modTag);
        final List<SpeakerEntry> entries = getValidEntries(level, modTag);

        final boolean removed = entries.remove(new SpeakerEntry(sbe.getBlockPos(), sbe.getSpeakerId()));
        writeEntries(modTag, entries);

        if (instrumentId != null)
            sbe.removeInstrument(instrumentId);
        return removed;
    }

    /**
     * unpairs every speaker from a block instrument (and the other half of a double block) or a looper.
     * speakers in unloaded chunks drop the instrument on their own once loaded,
     * since the instrument no longer lists them.
     * return how many speakers were unpaired
     */
    public static int unpairAll(final Level level, final BlockEntity instrument) {
        final CompoundTag modTag = GInstrumentMod.modTag(instrument);
        final UUID instrumentId = getInstrumentId(modTag);
        final List<SpeakerEntry> entries = getValidEntries(level, modTag);

        if (instrumentId != null) {
            for (final SpeakerEntry entry : entries) {
                if (level.isLoaded(entry.pos()) && (level.getBlockEntity(entry.pos()) instanceof SpeakerBlockEntity sbe))
                    sbe.removeInstrument(instrumentId);
            }
        }

        modTag.remove(SPEAKER_TAG);
        onBlockInstrumentChanged(level, instrument);
        return entries.size();
    }

    /**
     * return how many speakers are currently paired to the instrument. drops stale pairings.
     */
    public static int speakerCount(final Level level, final ItemStack instrument) {
        return getSpeakers(level, GInstrumentMod.modTag(instrument)).size();
    }
    public static int speakerCount(final Level level, final BlockEntity instrument) {
        final int count = getSpeakers(level, GInstrumentMod.modTag(instrument)).size();
        onBlockInstrumentChanged(level, instrument);
        return count;
    }

    /**
     * return whether the block instrument or looper at the given position is still paired to the given speaker.
     * used by speakers to verify their own list of block sources.
     */
    public static boolean isBlockInstrumentPaired(final Level level, final BlockPos instrumentPos,
                                                  final UUID instrumentId, final SpeakerBlockEntity sbe) {
        final BlockEntity be = level.getBlockEntity(instrumentPos);
        if (be == null)
            return false;

        final CompoundTag modTag = GInstrumentMod.modTag(be);
        return instrumentId.equals(getInstrumentId(modTag))
            && readEntries(modTag).contains(new SpeakerEntry(sbe.getBlockPos(), sbe.getSpeakerId()));
    }

    //#endregion


    //#region Resolving on play

    /**
     * return every speaker paired to the instrument that produced the event.
     * stale pairings are dropped along the way.
     */
    public static List<SpeakerBlockEntity> getFromEvent(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer())
            return List.of();

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;
        final Level level = event.level();

        if (entityInfo.isItemInstrument())
            return getSpeakers(level, GInstrumentMod.modTag(player.getItemInHand(entityInfo.hand.get())));

        if (entityInfo.isBlockInstrument())
            return getFromBlock(level, level.getBlockEntity(event.soundMeta().pos()));

        return List.of();
    }

    /**
     * return every speaker paired to a block source: a block instrument or a looper.
     * stale pairings are dropped along the way.
     */
    public static List<SpeakerBlockEntity> getFromBlock(final Level level, final BlockEntity source) {
        final CompoundTag modTag = GInstrumentMod.modTag(source);
        if (pruneStaleEntries(level, modTag))
            onBlockInstrumentChanged(level, source);
        return resolveSpeakers(level, modTag);
    }

    /**
     * drops stale pairings from the instrument's data,
     * return the loaded speakers that are still paired.
     */
    private static List<SpeakerBlockEntity> getSpeakers(final Level level, final CompoundTag modTag) {
        pruneStaleEntries(level, modTag);
        return resolveSpeakers(level, modTag);
    }
    /**
     * only rewrites the data when something was actually dropped,
     * so playing a note doesn't touch the instrument's NBT every time
     * return whether any pairing was dropped
     */
    private static boolean pruneStaleEntries(final Level level, final CompoundTag modTag) {
        final List<SpeakerEntry> valid = getValidEntries(level, modTag);
        if (valid.size() == readEntries(modTag).size())
            return false;

        writeEntries(modTag, valid);
        return true;
    }
    private static List<SpeakerBlockEntity> resolveSpeakers(final Level level, final CompoundTag modTag) {
        final List<SpeakerBlockEntity> speakers = new ArrayList<>();
        for (final SpeakerEntry entry : readEntries(modTag)) {
            if (level.isLoaded(entry.pos()) && (level.getBlockEntity(entry.pos()) instanceof SpeakerBlockEntity sbe))
                speakers.add(sbe);
        }
        return speakers;
    }

    //#endregion


    //#region Instrument data

    /**
     * return the instrument's entries that are still valid pairings.
     * an entry is valid when its position holds the same speaker (by ID), and that speaker
     * still lists this instrument. entries in unloaded chunks can't be checked, so they are kept.
     */
    private static List<SpeakerEntry> getValidEntries(final Level level, final CompoundTag modTag) {
        final UUID instrumentId = getInstrumentId(modTag);
        final List<SpeakerEntry> valid = new ArrayList<>();
        if (instrumentId == null)
            return valid;

        for (final SpeakerEntry entry : readEntries(modTag)) {
            if (!level.isLoaded(entry.pos())) {
                valid.add(entry);
                continue;
            }

            if ((level.getBlockEntity(entry.pos()) instanceof SpeakerBlockEntity sbe)
                && sbe.getSpeakerId().equals(entry.speakerId())
                && sbe.hasInstrument(instrumentId))
                valid.add(entry);
        }

        return valid;
    }

    private static List<SpeakerEntry> readEntries(final CompoundTag modTag) {
        final List<SpeakerEntry> entries = new ArrayList<>();
        final CompoundTag speakerTag = modTag.getCompound(SPEAKER_TAG);

        final ListTag list = speakerTag.getList(SPEAKERS_TAG, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            final CompoundTag entryTag = list.getCompound(i);
            if (entryTag.hasUUID(ID_TAG))
                entries.add(new SpeakerEntry(NbtUtils.readBlockPos(entryTag.getCompound(POS_TAG)), entryTag.getUUID(ID_TAG)));
        }

        return entries;
    }
    private static void writeEntries(final CompoundTag modTag, final List<SpeakerEntry> entries) {
        if (entries.isEmpty()) {
            modTag.remove(SPEAKER_TAG);
            return;
        }

        final ListTag list = new ListTag();
        for (final SpeakerEntry entry : entries) {
            final CompoundTag entryTag = new CompoundTag();
            entryTag.put(POS_TAG, NbtUtils.writeBlockPos(entry.pos()));
            entryTag.putUUID(ID_TAG, entry.speakerId());
            list.add(entryTag);
        }

        CommonUtil.getOrCreateElementTag(modTag, SPEAKER_TAG).put(SPEAKERS_TAG, list);
    }

    @Nullable
    private static UUID getInstrumentId(final CompoundTag modTag) {
        final CompoundTag speakerTag = modTag.getCompound(SPEAKER_TAG);
        return speakerTag.hasUUID(INSTRUMENT_ID_TAG) ? speakerTag.getUUID(INSTRUMENT_ID_TAG) : null;
    }
    private static UUID getOrCreateInstrumentId(final CompoundTag modTag) {
        final UUID existing = getInstrumentId(modTag);
        if (existing != null)
            return existing;

        final UUID id = UUID.randomUUID();
        CommonUtil.getOrCreateElementTag(modTag, SPEAKER_TAG).putUUID(INSTRUMENT_ID_TAG, id);
        return id;
    }

    /**
     * marks a block instrument as changed, and mirrors its speaker data
     * onto the other half of a double block (like the Keyboard).
     */
    private static void onBlockInstrumentChanged(final Level level, final BlockEntity instrument) {
        instrument.setChanged();

        final BlockPos pos = instrument.getBlockPos();
        final BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IDoubleBlock doubleBlock))
            return;

        final BlockEntity other = level.getBlockEntity(doubleBlock.getOtherBlock(state, pos, level));
        if (other == null)
            return;

        final CompoundTag modTag = GInstrumentMod.modTag(instrument), otherModTag = GInstrumentMod.modTag(other);
        if (modTag.contains(SPEAKER_TAG, CompoundTag.TAG_COMPOUND))
            otherModTag.put(SPEAKER_TAG, modTag.getCompound(SPEAKER_TAG).copy());
        else
            otherModTag.remove(SPEAKER_TAG);
        other.setChanged();
    }

    //#endregion


    /**
     * shows the player the outcome of a pairing attempt, e.g. "1 of 8 speakers connected!"
     */
    public static void sendPairMessage(final Player player, final PairResult result, final int count) {
        final Component message = switch (result) {
            case PAIRED -> Component.translatable("genshinstrument_lm.speaker.paired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.GREEN);
            case ALREADY_PAIRED -> Component.translatable("genshinstrument_lm.speaker.already_paired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.YELLOW);
            case FULL -> Component.translatable("genshinstrument_lm.speaker.full", MAX_SPEAKERS)
                .withStyle(ChatFormatting.RED);
            case LOOPER_TAKEN -> Component.translatable("genshinstrument_lm.speaker.looper_taken")
                .withStyle(ChatFormatting.RED);
        };
        player.displayClientMessage(message, true);
    }
    public static void sendUnpairMessage(final Player player, final boolean removed, final int count) {
        player.displayClientMessage(removed
            ? Component.translatable("genshinstrument_lm.speaker.unpaired", count, MAX_SPEAKERS)
                .withStyle(ChatFormatting.GREEN)
            : Component.translatable("genshinstrument_lm.speaker.not_paired")
                .withStyle(ChatFormatting.YELLOW)
        , true);
    }
    public static void sendUnpairAllMessage(final Player player, final int removedCount) {
        player.displayClientMessage((removedCount > 0)
            ? Component.translatable("genshinstrument_lm.speaker.unpaired_all", removedCount)
                .withStyle(ChatFormatting.GREEN)
            : Component.translatable("genshinstrument_lm.speaker.none_paired")
                .withStyle(ChatFormatting.YELLOW)
        , true);
    }
}
